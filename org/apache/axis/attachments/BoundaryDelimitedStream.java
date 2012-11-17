/*     */ package org.apache.axis.attachments;
/*     */ 
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Arrays;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class BoundaryDelimitedStream extends FilterInputStream
/*     */ {
/*  31 */   protected static Log log = LogFactory.getLog(BoundaryDelimitedStream.class.getName());
/*     */ 
/*  34 */   protected byte[] boundary = null;
/*     */ 
/*  37 */   int boundaryLen = 0;
/*     */ 
/*  40 */   int boundaryBufLen = 0;
/*     */ 
/*  43 */   InputStream is = null;
/*     */ 
/*  46 */   boolean closed = true;
/*     */ 
/*  49 */   boolean eos = false;
/*     */ 
/*  52 */   boolean theEnd = false;
/*     */ 
/*  55 */   int readbufsz = 0;
/*     */ 
/*  58 */   byte[] readbuf = null;
/*     */ 
/*  61 */   int readBufPos = 0;
/*     */ 
/*  64 */   int readBufEnd = 0;
/*     */   protected static final int BOUNDARY_NOT_FOUND = 2147483647;
/*  72 */   int boundaryPos = 2147483647;
/*     */ 
/*  75 */   static int streamCount = 0;
/*     */ 
/*  90 */   protected int streamNo = -1;
/*     */ 
/*  93 */   static boolean isDebugEnabled = false;
/*     */ 
/* 486 */   private int[] skip = null;
/*     */ 
/*     */   protected static synchronized int newStreamNo()
/*     */   {
/*  84 */     log.debug(Messages.getMessage("streamNo", "" + (streamCount + 1)));
/*     */ 
/*  86 */     return ++streamCount;
/*     */   }
/*     */ 
/*     */   public synchronized BoundaryDelimitedStream getNextStream()
/*     */     throws IOException
/*     */   {
/* 104 */     return getNextStream(this.readbufsz);
/*     */   }
/*     */ 
/*     */   protected synchronized BoundaryDelimitedStream getNextStream(int readbufsz)
/*     */     throws IOException
/*     */   {
/* 118 */     BoundaryDelimitedStream ret = null;
/*     */ 
/* 120 */     if (!this.theEnd)
/*     */     {
/* 123 */       ret = new BoundaryDelimitedStream(this, readbufsz);
/*     */     }
/*     */ 
/* 126 */     return ret;
/*     */   }
/*     */ 
/*     */   protected BoundaryDelimitedStream(BoundaryDelimitedStream prev, int readbufsz)
/*     */     throws IOException
/*     */   {
/* 141 */     super(null);
/*     */ 
/* 143 */     this.streamNo = newStreamNo();
/* 144 */     this.boundary = prev.boundary;
/* 145 */     this.boundaryLen = prev.boundaryLen;
/* 146 */     this.boundaryBufLen = prev.boundaryBufLen;
/* 147 */     this.skip = prev.skip;
/* 148 */     this.is = prev.is;
/* 149 */     this.closed = false;
/* 150 */     this.eos = false;
/* 151 */     readbufsz = prev.readbufsz;
/* 152 */     this.readbuf = prev.readbuf;
/*     */ 
/* 155 */     prev.readBufPos += this.boundaryBufLen;
/* 156 */     this.readBufEnd = prev.readBufEnd;
/*     */ 
/* 159 */     this.boundaryPos = boundaryPosition(this.readbuf, this.readBufPos, this.readBufEnd);
/* 160 */     prev.theEnd = this.theEnd;
/*     */   }
/*     */ 
/*     */   BoundaryDelimitedStream(InputStream is, byte[] boundary, int readbufsz)
/*     */     throws AxisFault
/*     */   {
/* 178 */     super(null);
/*     */ 
/* 180 */     isDebugEnabled = log.isDebugEnabled();
/* 181 */     this.streamNo = newStreamNo();
/* 182 */     this.closed = false;
/* 183 */     this.is = is;
/*     */ 
/* 186 */     this.boundary = new byte[boundary.length];
/*     */ 
/* 188 */     System.arraycopy(boundary, 0, this.boundary, 0, boundary.length);
/*     */ 
/* 190 */     this.boundaryLen = this.boundary.length;
/* 191 */     this.boundaryBufLen = (this.boundaryLen + 2);
/*     */ 
/* 195 */     this.readbufsz = Math.max(this.boundaryBufLen * 2, readbufsz);
/*     */   }
/*     */ 
/*     */   private final int readFromStream(byte[] b) throws IOException
/*     */   {
/* 200 */     return readFromStream(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   private final int readFromStream(byte[] b, int start, int length)
/*     */     throws IOException
/*     */   {
/* 207 */     int minRead = Math.max(this.boundaryBufLen * 2, length);
/*     */ 
/* 209 */     minRead = Math.min(minRead, length - start);
/*     */ 
/* 211 */     int br = 0;
/* 212 */     int brTotal = 0;
/*     */     do
/*     */     {
/* 215 */       br = this.is.read(b, brTotal + start, length - brTotal);
/*     */ 
/* 217 */       if (br > 0)
/* 218 */         brTotal += br;
/*     */     }
/* 220 */     while ((br > -1) && (brTotal < minRead));
/*     */ 
/* 222 */     return brTotal != 0 ? brTotal : br;
/*     */   }
/*     */ 
/*     */   public synchronized int read(byte[] b, int off, int len)
/*     */     throws IOException
/*     */   {
/* 239 */     if (this.closed) {
/* 240 */       throw new IOException(Messages.getMessage("streamClosed"));
/*     */     }
/*     */ 
/* 243 */     if (this.eos) {
/* 244 */       return -1;
/*     */     }
/*     */ 
/* 247 */     if (this.readbuf == null) {
/* 248 */       this.readbuf = new byte[Math.max(len, this.readbufsz)];
/* 249 */       this.readBufEnd = readFromStream(this.readbuf);
/*     */ 
/* 251 */       if (this.readBufEnd < 0) {
/* 252 */         this.readbuf = null;
/* 253 */         this.closed = true;
/* 254 */         finalClose();
/*     */ 
/* 256 */         throw new IOException(Messages.getMessage("eosBeforeMarker"));
/*     */       }
/*     */ 
/* 260 */       this.readBufPos = 0;
/*     */ 
/* 263 */       this.boundaryPos = boundaryPosition(this.readbuf, 0, this.readBufEnd);
/*     */     }
/*     */ 
/* 266 */     int bwritten = 0;
/*     */     do
/*     */     {
/* 270 */       int bcopy = Math.min(this.readBufEnd - this.readBufPos - this.boundaryBufLen, len - bwritten);
/*     */ 
/* 274 */       bcopy = Math.min(bcopy, this.boundaryPos - this.readBufPos);
/*     */ 
/* 276 */       if (bcopy > 0) {
/* 277 */         System.arraycopy(this.readbuf, this.readBufPos, b, off + bwritten, bcopy);
/*     */ 
/* 279 */         bwritten += bcopy;
/* 280 */         this.readBufPos += bcopy;
/*     */       }
/*     */ 
/* 283 */       if (this.readBufPos == this.boundaryPos) {
/* 284 */         this.eos = true;
/*     */ 
/* 286 */         log.debug(Messages.getMessage("atEOS", "" + this.streamNo));
/* 287 */       } else if (bwritten < len) {
/* 288 */         byte[] dstbuf = this.readbuf;
/*     */ 
/* 290 */         if (this.readbuf.length < len) {
/* 291 */           dstbuf = new byte[len];
/*     */         }
/*     */ 
/* 294 */         int movecnt = this.readBufEnd - this.readBufPos;
/*     */ 
/* 297 */         System.arraycopy(this.readbuf, this.readBufPos, dstbuf, 0, movecnt);
/*     */ 
/* 300 */         int readcnt = readFromStream(dstbuf, movecnt, dstbuf.length - movecnt);
/*     */ 
/* 303 */         if (readcnt < 0) {
/* 304 */           this.readbuf = null;
/* 305 */           this.closed = true;
/* 306 */           finalClose();
/*     */ 
/* 308 */           throw new IOException(Messages.getMessage("eosBeforeMarker"));
/*     */         }
/*     */ 
/* 312 */         this.readBufEnd = (readcnt + movecnt);
/* 313 */         this.readbuf = dstbuf;
/* 314 */         this.readBufPos = 0;
/*     */ 
/* 317 */         if (2147483647 != this.boundaryPos)
/* 318 */           this.boundaryPos -= movecnt;
/*     */         else {
/* 320 */           this.boundaryPos = boundaryPosition(this.readbuf, this.readBufPos, this.readBufEnd);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 328 */     while ((!this.eos) && (bwritten < len));
/*     */ 
/* 330 */     if ((log.isDebugEnabled()) && 
/* 331 */       (bwritten > 0)) {
/* 332 */       byte[] tb = new byte[bwritten];
/*     */ 
/* 334 */       System.arraycopy(b, off, tb, 0, bwritten);
/* 335 */       log.debug(Messages.getMessage("readBStream", new String[] { "" + bwritten, "" + this.streamNo, new String(tb) }));
/*     */     }
/*     */ 
/* 342 */     if ((this.eos) && (this.theEnd)) {
/* 343 */       this.readbuf = null;
/*     */     }
/*     */ 
/* 346 */     return bwritten;
/*     */   }
/*     */ 
/*     */   public int read(byte[] b)
/*     */     throws IOException
/*     */   {
/* 358 */     return read(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/* 369 */     byte[] b = new byte[1];
/* 370 */     int read = read(b);
/*     */ 
/* 372 */     if (read < 0) {
/* 373 */       return -1;
/*     */     }
/* 375 */     return b[0] & 0xFF;
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */     throws IOException
/*     */   {
/* 386 */     if (this.closed) {
/* 387 */       return;
/*     */     }
/*     */ 
/* 390 */     log.debug(Messages.getMessage("bStreamClosed", "" + this.streamNo));
/*     */ 
/* 392 */     this.closed = true;
/*     */ 
/* 394 */     if (!this.eos)
/*     */     {
/* 397 */       byte[] readrest = new byte[16384];
/* 398 */       int bread = 0;
/*     */       do
/*     */       {
/* 401 */         bread = read(readrest);
/* 402 */       }while (bread > -1);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mark(int readlimit)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */     throws IOException
/*     */   {
/* 424 */     throw new IOException(Messages.getMessage("attach.bounday.mns"));
/*     */   }
/*     */ 
/*     */   public boolean markSupported()
/*     */   {
/* 435 */     return false;
/*     */   }
/*     */ 
/*     */   public int available() throws IOException
/*     */   {
/* 440 */     int bcopy = this.readBufEnd - this.readBufPos - this.boundaryBufLen;
/*     */ 
/* 443 */     bcopy = Math.min(bcopy, this.boundaryPos - this.readBufPos);
/*     */ 
/* 445 */     return Math.max(0, bcopy);
/*     */   }
/*     */ 
/*     */   protected int boundaryPosition(byte[] searchbuf, int start, int end)
/*     */     throws IOException
/*     */   {
/* 460 */     int foundAt = boundarySearch(searchbuf, start, end);
/*     */ 
/* 463 */     if (2147483647 != foundAt) {
/* 464 */       if (foundAt + this.boundaryLen + 2 > end) {
/* 465 */         foundAt = 2147483647;
/*     */       }
/* 469 */       else if ((searchbuf[(foundAt + this.boundaryLen)] == 45) && (searchbuf[(foundAt + this.boundaryLen + 1)] == 45))
/*     */       {
/* 471 */         finalClose();
/* 472 */       } else if ((searchbuf[(foundAt + this.boundaryLen)] != 13) || (searchbuf[(foundAt + this.boundaryLen + 1)] != 10))
/*     */       {
/* 476 */         foundAt = 2147483647;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 481 */     return foundAt;
/*     */   }
/*     */ 
/*     */   private int boundarySearch(byte[] text, int start, int end)
/*     */   {
/* 492 */     int i = 0; int j = 0; int k = 0;
/*     */ 
/* 494 */     if (this.boundaryLen > end - start) {
/* 495 */       return 2147483647;
/*     */     }
/*     */ 
/* 498 */     if (null == this.skip) {
/* 499 */       this.skip = new int[256];
/*     */ 
/* 501 */       Arrays.fill(this.skip, this.boundaryLen);
/*     */ 
/* 503 */       for (k = 0; k < this.boundaryLen - 1; k++) {
/* 504 */         this.skip[this.boundary[k]] = (this.boundaryLen - k - 1);
/*     */       }
/*     */     }
/*     */ 
/* 508 */     for (k = start + this.boundaryLen - 1; k < end; )
/*     */     {
/*     */       try
/*     */       {
/* 514 */         j = this.boundaryLen - 1; i = k;
/* 515 */         for (; (j >= 0) && (text[i] == this.boundary[j]); j--)
/* 516 */           i--;
/*     */       }
/*     */       catch (ArrayIndexOutOfBoundsException e) {
/* 519 */         StringBuffer sb = new StringBuffer();
/* 520 */         sb.append(">>>" + e);
/*     */ 
/* 523 */         sb.append("start=" + start);
/* 524 */         sb.append("k=" + k);
/* 525 */         sb.append("text.length=" + text.length);
/* 526 */         sb.append("i=" + i);
/* 527 */         sb.append("boundary.length=" + this.boundary.length);
/* 528 */         sb.append("j=" + j);
/* 529 */         sb.append("end=" + end);
/* 530 */         log.warn(Messages.getMessage("exception01", sb.toString()));
/* 531 */         throw e;
/*     */       }
/*     */ 
/* 534 */       if (j == -1)
/* 535 */         return i + 1;
/* 509 */       k += this.skip[(text[k] & 0xFF)];
/*     */     }
/*     */ 
/* 540 */     return 2147483647;
/*     */   }
/*     */ 
/*     */   protected void finalClose()
/*     */     throws IOException
/*     */   {
/* 549 */     if (this.theEnd) return;
/* 550 */     this.theEnd = true;
/* 551 */     this.is.close();
/* 552 */     this.is = null;
/*     */   }
/*     */ 
/*     */   public static void printarry(byte[] b, int start, int end)
/*     */   {
/* 564 */     if (log.isDebugEnabled()) {
/* 565 */       byte[] tb = new byte[end - start];
/*     */ 
/* 567 */       System.arraycopy(b, start, tb, 0, end - start);
/* 568 */       log.debug("\"" + new String(tb) + "\"");
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.attachments.BoundaryDelimitedStream
 * JD-Core Version:    0.6.0
 */