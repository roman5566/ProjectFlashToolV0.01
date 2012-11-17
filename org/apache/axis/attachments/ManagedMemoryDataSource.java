/*     */ package org.apache.axis.attachments;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.WeakHashMap;
/*     */ import javax.activation.DataHandler;
/*     */ import javax.activation.DataSource;
/*     */ import org.apache.axis.InternalException;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class ManagedMemoryDataSource
/*     */   implements DataSource
/*     */ {
/*  36 */   protected static Log log = LogFactory.getLog(ManagedMemoryDataSource.class.getName());
/*     */ 
/*  43 */   protected String contentType = "application/octet-stream";
/*     */ 
/*  46 */   InputStream ss = null;
/*     */   public static final int MIN_MEMORY_DISK_CACHED = -1;
/*     */   public static final int MAX_MEMORY_DISK_CACHED = 16384;
/*  55 */   protected int maxCached = 16384;
/*     */ 
/*  60 */   protected File diskCacheFile = null;
/*     */ 
/*  65 */   protected WeakHashMap readers = new WeakHashMap();
/*     */ 
/*  70 */   protected boolean deleted = false;
/*     */   public static final int READ_CHUNK_SZ = 32768;
/*  79 */   protected boolean debugEnabled = false;
/*     */ 
/* 223 */   protected LinkedList memorybuflist = new LinkedList();
/*     */ 
/* 227 */   protected byte[] currentMemoryBuf = null;
/*     */ 
/* 230 */   protected int currentMemoryBufSz = 0;
/*     */ 
/* 234 */   protected long totalsz = 0L;
/*     */ 
/* 237 */   protected BufferedOutputStream cachediskstream = null;
/*     */ 
/* 241 */   protected boolean closed = false;
/*     */ 
/* 480 */   protected static Log is_log = LogFactory.getLog(Instream.class.getName());
/*     */ 
/*     */   protected ManagedMemoryDataSource()
/*     */   {
/*     */   }
/*     */ 
/*     */   public ManagedMemoryDataSource(InputStream ss, int maxCached, String contentType)
/*     */     throws IOException
/*     */   {
/* 102 */     this(ss, maxCached, contentType, false);
/*     */   }
/*     */ 
/*     */   public ManagedMemoryDataSource(InputStream ss, int maxCached, String contentType, boolean readall)
/*     */     throws IOException
/*     */   {
/* 120 */     if ((ss instanceof BufferedInputStream))
/* 121 */       this.ss = ss;
/*     */     else {
/* 123 */       this.ss = new BufferedInputStream(ss);
/*     */     }
/* 125 */     this.maxCached = maxCached;
/*     */ 
/* 127 */     if ((null != contentType) && (contentType.length() != 0)) {
/* 128 */       this.contentType = contentType;
/*     */     }
/*     */ 
/* 131 */     if (maxCached < -1) {
/* 132 */       throw new IllegalArgumentException(Messages.getMessage("badMaxCached", "" + maxCached));
/*     */     }
/*     */ 
/* 136 */     if (log.isDebugEnabled()) {
/* 137 */       this.debugEnabled = true;
/*     */     }
/*     */ 
/* 141 */     if (readall) {
/* 142 */       byte[] readbuffer = new byte[32768];
/* 143 */       int read = 0;
/*     */       do
/*     */       {
/* 146 */         read = ss.read(readbuffer);
/*     */ 
/* 148 */         if (read > 0)
/* 149 */           write(readbuffer, read);
/*     */       }
/* 151 */       while (read > -1);
/*     */ 
/* 153 */       close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getContentType()
/*     */   {
/* 164 */     return this.contentType;
/*     */   }
/*     */ 
/*     */   public synchronized InputStream getInputStream()
/*     */     throws IOException
/*     */   {
/* 182 */     return new Instream();
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 193 */     String ret = null;
/*     */     try
/*     */     {
/* 196 */       flushToDisk();
/*     */ 
/* 198 */       if (this.diskCacheFile != null)
/* 199 */         ret = this.diskCacheFile.getAbsolutePath();
/*     */     }
/*     */     catch (Exception e) {
/* 202 */       this.diskCacheFile = null;
/*     */     }
/*     */ 
/* 205 */     return ret;
/*     */   }
/*     */ 
/*     */   public OutputStream getOutputStream()
/*     */     throws IOException
/*     */   {
/* 219 */     return null;
/*     */   }
/*     */ 
/*     */   protected void write(byte[] data)
/*     */     throws IOException
/*     */   {
/* 250 */     write(data, data.length);
/*     */   }
/*     */ 
/*     */   protected synchronized void write(byte[] data, int length)
/*     */     throws IOException
/*     */   {
/* 266 */     if (this.closed) {
/* 267 */       throw new IOException(Messages.getMessage("streamClosed"));
/*     */     }
/*     */ 
/* 270 */     int writesz = length;
/* 271 */     int byteswritten = 0;
/*     */ 
/* 273 */     if ((null != this.memorybuflist) && (this.totalsz + writesz > this.maxCached))
/*     */     {
/* 275 */       if (null == this.cachediskstream) {
/* 276 */         flushToDisk();
/*     */       }
/*     */     }
/*     */ 
/* 280 */     if (this.memorybuflist != null) {
/*     */       do {
/* 282 */         if (null == this.currentMemoryBuf) {
/* 283 */           this.currentMemoryBuf = new byte[32768];
/* 284 */           this.currentMemoryBufSz = 0;
/*     */ 
/* 286 */           this.memorybuflist.add(this.currentMemoryBuf);
/*     */         }
/*     */ 
/* 290 */         int bytes2write = Math.min(writesz - byteswritten, this.currentMemoryBuf.length - this.currentMemoryBufSz);
/*     */ 
/* 295 */         System.arraycopy(data, byteswritten, this.currentMemoryBuf, this.currentMemoryBufSz, bytes2write);
/*     */ 
/* 298 */         byteswritten += bytes2write;
/* 299 */         this.currentMemoryBufSz += bytes2write;
/*     */ 
/* 301 */         if (byteswritten >= writesz)
/*     */           continue;
/* 303 */         this.currentMemoryBuf = new byte[32768];
/* 304 */         this.currentMemoryBufSz = 0;
/*     */ 
/* 306 */         this.memorybuflist.add(this.currentMemoryBuf);
/*     */       }
/* 308 */       while (byteswritten < writesz);
/*     */     }
/*     */ 
/* 311 */     if (null != this.cachediskstream) {
/* 312 */       this.cachediskstream.write(data, 0, length);
/*     */     }
/*     */ 
/* 315 */     this.totalsz += writesz;
/*     */   }
/*     */ 
/*     */   protected synchronized void close()
/*     */     throws IOException
/*     */   {
/* 328 */     if (!this.closed) {
/* 329 */       this.closed = true;
/*     */ 
/* 331 */       if (null != this.cachediskstream) {
/* 332 */         this.cachediskstream.close();
/*     */ 
/* 334 */         this.cachediskstream = null;
/*     */       }
/*     */ 
/* 337 */       if (null != this.memorybuflist) {
/* 338 */         if (this.currentMemoryBufSz > 0) {
/* 339 */           byte[] tmp = new byte[this.currentMemoryBufSz];
/*     */ 
/* 342 */           System.arraycopy(this.currentMemoryBuf, 0, tmp, 0, this.currentMemoryBufSz);
/*     */ 
/* 344 */           this.memorybuflist.set(this.memorybuflist.size() - 1, tmp);
/*     */         }
/*     */ 
/* 349 */         this.currentMemoryBuf = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void finalize() throws Throwable
/*     */   {
/* 356 */     if (null != this.cachediskstream) {
/* 357 */       this.cachediskstream.close();
/*     */ 
/* 359 */       this.cachediskstream = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void flushToDisk()
/*     */     throws IOException, FileNotFoundException
/*     */   {
/* 372 */     LinkedList ml = this.memorybuflist;
/*     */ 
/* 374 */     log.debug(Messages.getMessage("maxCached", "" + this.maxCached, "" + this.totalsz));
/*     */ 
/* 377 */     if ((ml != null) && 
/* 378 */       (null == this.cachediskstream))
/*     */       try {
/* 380 */         MessageContext mc = MessageContext.getCurrentContext();
/* 381 */         String attdir = mc == null ? null : mc.getStrProp("attachments.directory");
/*     */ 
/* 386 */         this.diskCacheFile = File.createTempFile("Axis", ".att", attdir == null ? null : new File(attdir));
/*     */ 
/* 392 */         if (log.isDebugEnabled()) {
/* 393 */           log.debug(Messages.getMessage("diskCache", this.diskCacheFile.getAbsolutePath()));
/*     */         }
/*     */ 
/* 398 */         this.cachediskstream = new BufferedOutputStream(new FileOutputStream(this.diskCacheFile));
/*     */ 
/* 401 */         int listsz = ml.size();
/*     */ 
/* 404 */         Iterator it = ml.iterator();
/* 405 */         while (it.hasNext()) {
/* 406 */           byte[] rbuf = (byte[])it.next();
/* 407 */           int bwrite = listsz-- == 0 ? this.currentMemoryBufSz : rbuf.length;
/*     */ 
/* 411 */           this.cachediskstream.write(rbuf, 0, bwrite);
/*     */ 
/* 413 */           if (this.closed) {
/* 414 */             this.cachediskstream.close();
/*     */ 
/* 416 */             this.cachediskstream = null;
/*     */           }
/*     */         }
/*     */ 
/* 420 */         this.memorybuflist = null;
/*     */       } catch (SecurityException se) {
/* 422 */         this.diskCacheFile = null;
/* 423 */         this.cachediskstream = null;
/* 424 */         this.maxCached = 2147483647;
/*     */ 
/* 426 */         log.info(Messages.getMessage("nodisk00"), se);
/*     */       }
/*     */   }
/*     */ 
/*     */   public synchronized boolean delete()
/*     */   {
/* 434 */     boolean ret = false;
/*     */ 
/* 436 */     this.deleted = true;
/*     */ 
/* 438 */     this.memorybuflist = null;
/*     */ 
/* 440 */     if (this.diskCacheFile != null) {
/* 441 */       if (this.cachediskstream != null) {
/*     */         try {
/* 443 */           this.cachediskstream.close();
/*     */         }
/*     */         catch (Exception e) {
/*     */         }
/* 447 */         this.cachediskstream = null;
/*     */       }
/*     */ 
/* 450 */       Object[] array = this.readers.keySet().toArray();
/* 451 */       for (int i = 0; i < array.length; i++) {
/* 452 */         Instream stream = (Instream)array[i];
/* 453 */         if (null == stream) continue;
/*     */         try {
/* 455 */           stream.close();
/*     */         }
/*     */         catch (Exception e) {
/*     */         }
/*     */       }
/* 460 */       this.readers.clear();
/*     */       try
/*     */       {
/* 463 */         this.diskCacheFile.delete();
/*     */ 
/* 465 */         ret = true;
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 469 */         this.diskCacheFile.deleteOnExit();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 474 */     return ret;
/*     */   }
/*     */ 
/*     */   public static void main(String[] arg)
/*     */   {
/*     */     try
/*     */     {
/* 861 */       String readFile = arg[0];
/* 862 */       String writeFile = arg[1];
/* 863 */       FileInputStream ss = new FileInputStream(readFile);
/*     */ 
/* 865 */       ManagedMemoryDataSource ms = new ManagedMemoryDataSource(ss, 1048576, "foo/data", true);
/*     */ 
/* 867 */       DataHandler dh = new DataHandler(ms);
/*     */ 
/* 869 */       InputStream is = dh.getInputStream();
/* 870 */       FileOutputStream fo = new FileOutputStream(writeFile);
/*     */ 
/* 872 */       byte[] buf = new byte[512];
/* 873 */       int read = 0;
/*     */       do
/*     */       {
/* 876 */         read = is.read(buf);
/*     */ 
/* 878 */         if (read > 0)
/* 879 */           fo.write(buf, 0, read);
/*     */       }
/* 881 */       while (read > -1);
/*     */ 
/* 883 */       fo.close();
/* 884 */       is.close();
/*     */     } catch (Exception e) {
/* 886 */       log.error(Messages.getMessage("exception00"), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public File getDiskCacheFile()
/*     */   {
/* 895 */     return this.diskCacheFile;
/*     */   }
/*     */ 
/*     */   private class Instream extends InputStream
/*     */   {
/* 490 */     protected long bread = 0L;
/*     */ 
/* 493 */     FileInputStream fin = null;
/*     */ 
/* 496 */     int currentIndex = 0;
/*     */ 
/* 500 */     byte[] currentBuf = null;
/*     */ 
/* 503 */     int currentBufPos = 0;
/*     */ 
/* 506 */     boolean readClosed = false;
/*     */ 
/*     */     protected Instream()
/*     */       throws IOException
/*     */     {
/* 516 */       if (ManagedMemoryDataSource.this.deleted) {
/* 517 */         throw new IOException(Messages.getMessage("resourceDeleted"));
/*     */       }
/*     */ 
/* 521 */       ManagedMemoryDataSource.this.readers.put(this, null);
/*     */     }
/*     */ 
/*     */     public int available()
/*     */       throws IOException
/*     */     {
/* 534 */       if (ManagedMemoryDataSource.this.deleted) {
/* 535 */         throw new IOException(Messages.getMessage("resourceDeleted"));
/*     */       }
/*     */ 
/* 539 */       if (this.readClosed) {
/* 540 */         throw new IOException(Messages.getMessage("streamClosed"));
/*     */       }
/*     */ 
/* 544 */       int ret = new Long(Math.min(2147483647L, ManagedMemoryDataSource.this.totalsz - this.bread)).intValue();
/*     */ 
/* 546 */       if (ManagedMemoryDataSource.this.debugEnabled) {
/* 547 */         ManagedMemoryDataSource.is_log.debug("available() = " + ret + ".");
/*     */       }
/*     */ 
/* 550 */       return ret;
/*     */     }
/*     */ 
/*     */     public int read()
/*     */       throws IOException
/*     */     {
/* 562 */       synchronized (ManagedMemoryDataSource.this) {
/* 563 */         byte[] retb = new byte[1];
/* 564 */         int br = read(retb, 0, 1);
/*     */ 
/* 566 */         if (br == -1) {
/* 567 */           return -1;
/*     */         }
/* 569 */         return 0xFF & retb[0];
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean markSupported()
/*     */     {
/* 580 */       if (ManagedMemoryDataSource.this.debugEnabled) {
/* 581 */         ManagedMemoryDataSource.is_log.debug("markSupported() = false.");
/*     */       }
/*     */ 
/* 584 */       return false;
/*     */     }
/*     */ 
/*     */     public void mark(int readlimit)
/*     */     {
/* 594 */       if (ManagedMemoryDataSource.this.debugEnabled)
/* 595 */         ManagedMemoryDataSource.is_log.debug("mark()");
/*     */     }
/*     */ 
/*     */     public void reset()
/*     */       throws IOException
/*     */     {
/* 606 */       if (ManagedMemoryDataSource.this.debugEnabled) {
/* 607 */         ManagedMemoryDataSource.is_log.debug("reset()");
/*     */       }
/*     */ 
/* 610 */       throw new IOException(Messages.getMessage("noResetMark"));
/*     */     }
/*     */ 
/*     */     public long skip(long skipped) throws IOException
/*     */     {
/* 615 */       if (ManagedMemoryDataSource.this.debugEnabled) {
/* 616 */         ManagedMemoryDataSource.is_log.debug("skip(" + skipped + ").");
/*     */       }
/*     */ 
/* 619 */       if (ManagedMemoryDataSource.this.deleted) {
/* 620 */         throw new IOException(Messages.getMessage("resourceDeleted"));
/*     */       }
/*     */ 
/* 624 */       if (this.readClosed) {
/* 625 */         throw new IOException(Messages.getMessage("streamClosed"));
/*     */       }
/*     */ 
/* 629 */       if (skipped < 1L) {
/* 630 */         return 0L;
/*     */       }
/*     */ 
/* 633 */       synchronized (ManagedMemoryDataSource.this) {
/* 634 */         skipped = Math.min(skipped, ManagedMemoryDataSource.this.totalsz - this.bread);
/*     */ 
/* 638 */         if (skipped == 0L) {
/* 639 */           return 0L;
/*     */         }
/*     */ 
/* 642 */         List ml = ManagedMemoryDataSource.this.memorybuflist;
/* 643 */         int bwritten = 0;
/*     */ 
/* 645 */         if (ml != null) {
/* 646 */           if (null == this.currentBuf) {
/* 647 */             this.currentBuf = ((byte[])ml.get(this.currentIndex));
/* 648 */             this.currentBufPos = 0;
/*     */           }
/*     */           do
/*     */           {
/* 652 */             long bcopy = Math.min(this.currentBuf.length - this.currentBufPos, skipped - bwritten);
/*     */ 
/* 656 */             bwritten = (int)(bwritten + bcopy);
/* 657 */             this.currentBufPos = (int)(this.currentBufPos + bcopy);
/*     */ 
/* 659 */             if (bwritten < skipped) {
/* 660 */               this.currentBuf = ((byte[])ml.get(++this.currentIndex));
/* 661 */               this.currentBufPos = 0;
/*     */             }
/*     */           }
/* 663 */           while (bwritten < skipped);
/*     */         }
/*     */ 
/* 666 */         if (null != this.fin) {
/* 667 */           this.fin.skip(skipped);
/*     */         }
/*     */ 
/* 670 */         this.bread += skipped;
/*     */       }
/*     */ 
/* 673 */       if (ManagedMemoryDataSource.this.debugEnabled) {
/* 674 */         ManagedMemoryDataSource.is_log.debug("skipped " + skipped + ".");
/*     */       }
/*     */ 
/* 677 */       return skipped;
/*     */     }
/*     */ 
/*     */     public int read(byte[] b, int off, int len) throws IOException
/*     */     {
/* 682 */       if (ManagedMemoryDataSource.this.debugEnabled) {
/* 683 */         ManagedMemoryDataSource.is_log.debug(hashCode() + " read(" + off + ", " + len + ")");
/*     */       }
/*     */ 
/* 687 */       if (ManagedMemoryDataSource.this.deleted) {
/* 688 */         throw new IOException(Messages.getMessage("resourceDeleted"));
/*     */       }
/*     */ 
/* 692 */       if (this.readClosed) {
/* 693 */         throw new IOException(Messages.getMessage("streamClosed"));
/*     */       }
/*     */ 
/* 697 */       if (b == null) {
/* 698 */         throw new InternalException(Messages.getMessage("nullInput"));
/*     */       }
/*     */ 
/* 701 */       if (off < 0) {
/* 702 */         throw new IndexOutOfBoundsException(Messages.getMessage("negOffset", "" + off));
/*     */       }
/*     */ 
/* 706 */       if (len < 0) {
/* 707 */         throw new IndexOutOfBoundsException(Messages.getMessage("length", "" + len));
/*     */       }
/*     */ 
/* 711 */       if (len + off > b.length) {
/* 712 */         throw new IndexOutOfBoundsException(Messages.getMessage("writeBeyond"));
/*     */       }
/*     */ 
/* 716 */       if (len == 0) {
/* 717 */         return 0;
/*     */       }
/*     */ 
/* 720 */       int bwritten = 0;
/*     */ 
/* 722 */       synchronized (ManagedMemoryDataSource.this) {
/* 723 */         if (this.bread == ManagedMemoryDataSource.this.totalsz) {
/* 724 */           return -1;
/*     */         }
/*     */ 
/* 727 */         List ml = ManagedMemoryDataSource.this.memorybuflist;
/*     */ 
/* 729 */         long longlen = len;
/* 730 */         longlen = Math.min(longlen, ManagedMemoryDataSource.this.totalsz - this.bread);
/*     */ 
/* 734 */         len = new Long(longlen).intValue();
/*     */ 
/* 736 */         if (ManagedMemoryDataSource.this.debugEnabled) {
/* 737 */           ManagedMemoryDataSource.is_log.debug("len = " + len);
/*     */         }
/*     */ 
/* 740 */         if (ml != null) {
/* 741 */           if (null == this.currentBuf) {
/* 742 */             this.currentBuf = ((byte[])ml.get(this.currentIndex));
/* 743 */             this.currentBufPos = 0;
/*     */           }
/*     */ 
/*     */           do
/*     */           {
/* 749 */             int bcopy = Math.min(this.currentBuf.length - this.currentBufPos, len - bwritten);
/*     */ 
/* 753 */             System.arraycopy(this.currentBuf, this.currentBufPos, b, off + bwritten, bcopy);
/*     */ 
/* 756 */             bwritten += bcopy;
/* 757 */             this.currentBufPos += bcopy;
/*     */ 
/* 759 */             if (bwritten < len) {
/* 760 */               this.currentBuf = ((byte[])ml.get(++this.currentIndex));
/* 761 */               this.currentBufPos = 0;
/*     */             }
/*     */           }
/* 763 */           while (bwritten < len);
/*     */         }
/*     */ 
/* 766 */         if ((bwritten == 0) && (null != ManagedMemoryDataSource.this.diskCacheFile)) {
/* 767 */           if (ManagedMemoryDataSource.this.debugEnabled) {
/* 768 */             ManagedMemoryDataSource.is_log.debug(Messages.getMessage("reading", "" + len));
/*     */           }
/*     */ 
/* 771 */           if (null == this.fin) {
/* 772 */             if (ManagedMemoryDataSource.this.debugEnabled) {
/* 773 */               ManagedMemoryDataSource.is_log.debug(Messages.getMessage("openBread", ManagedMemoryDataSource.this.diskCacheFile.getCanonicalPath()));
/*     */             }
/*     */ 
/* 779 */             if (ManagedMemoryDataSource.this.debugEnabled) {
/* 780 */               ManagedMemoryDataSource.is_log.debug(Messages.getMessage("openBread", "" + this.bread));
/*     */             }
/*     */ 
/* 784 */             this.fin = new FileInputStream(ManagedMemoryDataSource.this.diskCacheFile);
/*     */ 
/* 786 */             if (this.bread > 0L) {
/* 787 */               this.fin.skip(this.bread);
/*     */             }
/*     */           }
/*     */ 
/* 791 */           if (ManagedMemoryDataSource.this.cachediskstream != null) {
/* 792 */             if (ManagedMemoryDataSource.this.debugEnabled) {
/* 793 */               ManagedMemoryDataSource.is_log.debug(Messages.getMessage("flushing"));
/*     */             }
/*     */ 
/* 796 */             ManagedMemoryDataSource.this.cachediskstream.flush();
/*     */           }
/*     */ 
/* 799 */           if (ManagedMemoryDataSource.this.debugEnabled) {
/* 800 */             ManagedMemoryDataSource.is_log.debug(Messages.getMessage("flushing"));
/* 801 */             ManagedMemoryDataSource.is_log.debug("len=" + len);
/* 802 */             ManagedMemoryDataSource.is_log.debug("off=" + off);
/* 803 */             ManagedMemoryDataSource.is_log.debug("b.length=" + b.length);
/*     */           }
/*     */ 
/* 806 */           bwritten = this.fin.read(b, off, len);
/*     */         }
/*     */ 
/* 809 */         if (bwritten > 0) {
/* 810 */           this.bread += bwritten;
/*     */         }
/*     */       }
/*     */ 
/* 814 */       if (ManagedMemoryDataSource.this.debugEnabled) {
/* 815 */         ManagedMemoryDataSource.is_log.debug(hashCode() + Messages.getMessage("read", new StringBuffer().append("").append(bwritten).toString()));
/*     */       }
/*     */ 
/* 819 */       return bwritten;
/*     */     }
/*     */ 
/*     */     public synchronized void close()
/*     */       throws IOException
/*     */     {
/* 829 */       if (ManagedMemoryDataSource.this.debugEnabled) {
/* 830 */         ManagedMemoryDataSource.is_log.debug("close()");
/*     */       }
/*     */ 
/* 833 */       if (!this.readClosed) {
/* 834 */         ManagedMemoryDataSource.this.readers.remove(this);
/*     */ 
/* 836 */         this.readClosed = true;
/*     */ 
/* 838 */         if (this.fin != null) {
/* 839 */           this.fin.close();
/*     */         }
/*     */ 
/* 842 */         this.fin = null;
/*     */       }
/*     */     }
/*     */ 
/*     */     protected void finalize() throws Throwable {
/* 847 */       close();
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.attachments.ManagedMemoryDataSource
 * JD-Core Version:    0.6.0
 */