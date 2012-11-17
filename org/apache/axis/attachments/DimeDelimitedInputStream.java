/*     */ package org.apache.axis.attachments;
/*     */ 
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class DimeDelimitedInputStream extends FilterInputStream
/*     */ {
/*  62 */   protected static Log log = LogFactory.getLog(DimeDelimitedInputStream.class.getName());
/*     */ 
/*  65 */   InputStream is = null;
/*  66 */   volatile boolean closed = true;
/*  67 */   boolean theEnd = false;
/*  68 */   boolean moreChunks = false;
/*  69 */   boolean MB = false;
/*  70 */   boolean ME = false;
/*  71 */   DimeTypeNameFormat tnf = null;
/*  72 */   String type = null;
/*  73 */   String id = null;
/*  74 */   long recordLength = 0L;
/*  75 */   long bytesRead = 0L;
/*  76 */   int dataPadLength = 0;
/*  77 */   private static byte[] trash = new byte[4];
/*  78 */   protected int streamNo = 0;
/*  79 */   protected IOException streamInError = null;
/*     */ 
/*  81 */   protected static int streamCount = 0;
/*     */ 
/*  88 */   static boolean isDebugEnabled = false;
/*     */ 
/*     */   protected static synchronized int newStreamNo()
/*     */   {
/*  84 */     log.debug(Messages.getMessage("streamNo", "" + (streamCount + 1)));
/*  85 */     return ++streamCount;
/*     */   }
/*     */ 
/*     */   synchronized DimeDelimitedInputStream getNextStream()
/*     */     throws IOException
/*     */   {
/* 100 */     if (null != this.streamInError) throw this.streamInError;
/* 101 */     if (this.theEnd) return null;
/* 102 */     if ((this.bytesRead < this.recordLength) || (this.moreChunks)) {
/* 103 */       throw new RuntimeException(Messages.getMessage("attach.dimeReadFullyError"));
/*     */     }
/* 105 */     this.dataPadLength -= readPad(this.dataPadLength);
/*     */ 
/* 108 */     return new DimeDelimitedInputStream(this.is);
/*     */   }
/*     */ 
/*     */   DimeDelimitedInputStream(InputStream is)
/*     */     throws IOException
/*     */   {
/* 118 */     super(null);
/* 119 */     isDebugEnabled = log.isDebugEnabled();
/* 120 */     this.streamNo = newStreamNo();
/* 121 */     this.closed = false;
/* 122 */     this.is = is;
/* 123 */     readHeader(false);
/*     */   }
/*     */ 
/*     */   private final int readPad(int size) throws IOException {
/* 127 */     if (0 == size) return 0;
/* 128 */     int read = readFromStream(trash, 0, size);
/*     */ 
/* 130 */     if (size != read) {
/* 131 */       this.streamInError = new IOException(Messages.getMessage("attach.dimeNotPaddedCorrectly"));
/*     */ 
/* 133 */       throw this.streamInError;
/*     */     }
/* 135 */     return read;
/*     */   }
/*     */ 
/*     */   private final int readFromStream(byte[] b) throws IOException {
/* 139 */     return readFromStream(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   private final int readFromStream(byte[] b, int start, int length)
/*     */     throws IOException
/*     */   {
/* 145 */     if (length == 0) return 0;
/*     */ 
/* 147 */     int br = 0;
/* 148 */     int brTotal = 0;
/*     */     do
/*     */     {
/*     */       try {
/* 152 */         br = this.is.read(b, brTotal + start, length - brTotal);
/*     */       } catch (IOException e) {
/* 154 */         this.streamInError = e;
/* 155 */         throw e;
/*     */       }
/* 157 */       if (br <= 0) continue; brTotal += br;
/*     */     }
/* 159 */     while ((br > -1) && (brTotal < length));
/*     */ 
/* 161 */     return br > -1 ? brTotal : br;
/*     */   }
/*     */ 
/*     */   public String getContentId()
/*     */   {
/* 169 */     return this.id;
/*     */   }
/*     */ 
/*     */   public DimeTypeNameFormat getDimeTypeNameFormat() {
/* 173 */     return this.tnf;
/*     */   }
/*     */ 
/*     */   public String getType()
/*     */   {
/* 183 */     return this.type;
/*     */   }
/*     */ 
/*     */   public synchronized int read(byte[] b, int off, int len)
/*     */     throws IOException
/*     */   {
/* 197 */     if (this.closed) {
/* 198 */       this.dataPadLength -= readPad(this.dataPadLength);
/* 199 */       throw new IOException(Messages.getMessage("streamClosed"));
/*     */     }
/* 201 */     return _read(b, off, len);
/*     */   }
/*     */ 
/*     */   protected int _read(byte[] b, int off, int len) throws IOException
/*     */   {
/* 206 */     if (len < 0) throw new IllegalArgumentException(Messages.getMessage("attach.readLengthError", "" + len));
/*     */ 
/* 210 */     if (off < 0) throw new IllegalArgumentException(Messages.getMessage("attach.readOffsetError", "" + off));
/*     */ 
/* 213 */     if (b == null) throw new IllegalArgumentException(Messages.getMessage("attach.readArrayNullError"));
/*     */ 
/* 215 */     if (b.length < off + len) throw new IllegalArgumentException(Messages.getMessage("attach.readArraySizeError", "" + b.length, "" + len, "" + off));
/*     */ 
/* 219 */     if (null != this.streamInError) throw this.streamInError;
/*     */ 
/* 221 */     if (0 == len) return 0;
/*     */ 
/* 223 */     if ((this.recordLength == 0L) && (this.bytesRead == 0L) && (!this.moreChunks)) {
/* 224 */       this.bytesRead += 1L;
/* 225 */       if (this.ME) {
/* 226 */         finalClose();
/*     */       }
/* 228 */       return 0;
/*     */     }
/* 230 */     if ((this.bytesRead >= this.recordLength) && (!this.moreChunks)) {
/* 231 */       this.dataPadLength -= readPad(this.dataPadLength);
/* 232 */       if (this.ME) {
/* 233 */         finalClose();
/*     */       }
/* 235 */       return -1;
/*     */     }
/*     */ 
/* 238 */     int totalbytesread = 0;
/* 239 */     int bytes2read = 0;
/*     */     do
/*     */     {
/* 242 */       if ((this.bytesRead >= this.recordLength) && (this.moreChunks)) {
/* 243 */         readHeader(true);
/*     */       }
/* 245 */       bytes2read = (int)Math.min(this.recordLength - this.bytesRead, len - totalbytesread);
/*     */ 
/* 247 */       bytes2read = (int)Math.min(this.recordLength - this.bytesRead, len - totalbytesread);
/*     */       try
/*     */       {
/* 250 */         bytes2read = this.is.read(b, off + totalbytesread, bytes2read);
/*     */       }
/*     */       catch (IOException e) {
/* 253 */         this.streamInError = e;
/* 254 */         throw e;
/*     */       }
/*     */ 
/* 257 */       if (0 < bytes2read) {
/* 258 */         totalbytesread += bytes2read;
/* 259 */         this.bytesRead += bytes2read;
/*     */       }
/*     */     }
/*     */ 
/* 263 */     while ((bytes2read > -1) && (totalbytesread < len) && ((this.bytesRead < this.recordLength) || (this.moreChunks)));
/*     */ 
/* 266 */     if (0 > bytes2read) {
/* 267 */       if (this.moreChunks) {
/* 268 */         this.streamInError = new IOException(Messages.getMessage("attach.DimeStreamError0"));
/*     */ 
/* 270 */         throw this.streamInError;
/*     */       }
/* 272 */       if (this.bytesRead < this.recordLength) {
/* 273 */         this.streamInError = new IOException(Messages.getMessage("attach.DimeStreamError1", "" + (this.recordLength - this.bytesRead)));
/*     */ 
/* 276 */         throw this.streamInError;
/*     */       }
/* 278 */       if (!this.ME) {
/* 279 */         this.streamInError = new IOException(Messages.getMessage("attach.DimeStreamError0"));
/*     */ 
/* 281 */         throw this.streamInError;
/*     */       }
/*     */ 
/* 284 */       this.dataPadLength = 0;
/*     */     }
/* 286 */     else if (this.bytesRead >= this.recordLength)
/*     */     {
/*     */       try {
/* 289 */         this.dataPadLength -= readPad(this.dataPadLength);
/*     */       }
/*     */       catch (IOException e) {
/* 292 */         if (!this.ME) throw e;
/*     */ 
/* 294 */         this.dataPadLength = 0;
/* 295 */         this.streamInError = null;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 300 */     if ((this.bytesRead >= this.recordLength) && (this.ME)) {
/* 301 */       finalClose();
/*     */     }
/*     */ 
/* 304 */     return totalbytesread >= 0 ? totalbytesread : -1;
/*     */   }
/*     */ 
/*     */   void readHeader(boolean isChunk) throws IOException
/*     */   {
/* 309 */     this.bytesRead = 0L;
/* 310 */     if (isChunk) {
/* 311 */       if (!this.moreChunks) throw new RuntimeException(Messages.getMessage("attach.DimeStreamError2"));
/*     */ 
/* 313 */       this.dataPadLength -= readPad(this.dataPadLength);
/*     */     }
/*     */ 
/* 316 */     byte[] header = new byte[12];
/*     */ 
/* 318 */     if (header.length != readFromStream(header)) {
/* 319 */       this.streamInError = new IOException(Messages.getMessage("attach.DimeStreamError3", "" + header.length));
/*     */ 
/* 322 */       throw this.streamInError;
/*     */     }
/*     */ 
/* 326 */     byte version = (byte)(header[0] >>> 3 & 0x1F);
/*     */ 
/* 328 */     if (version > 1) {
/* 329 */       this.streamInError = new IOException(Messages.getMessage("attach.DimeStreamError4", "" + version, "1"));
/*     */ 
/* 332 */       throw this.streamInError;
/*     */     }
/*     */ 
/* 336 */     this.MB = (0 != (0x4 & header[0]));
/* 337 */     this.ME = (0 != (0x2 & header[0]));
/* 338 */     this.moreChunks = (0 != (0x1 & header[0]));
/*     */ 
/* 341 */     if (!isChunk) {
/* 342 */       this.tnf = DimeTypeNameFormat.parseByte((byte)(header[1] >>> 4 & 0xF));
/*     */     }
/*     */ 
/* 345 */     int optionsLength = header[2] << 8 & 0xFF00 | header[3];
/*     */ 
/* 349 */     int idLength = header[4] << 8 & 0xFF00 | header[5];
/*     */ 
/* 353 */     int typeLength = header[6] << 8 & 0xFF00 | header[7];
/*     */ 
/* 357 */     this.recordLength = (header[8] << 24 & 0xFF000000 | header[9] << 16 & 0xFF0000 | header[10] << 8 & 0xFF00 | header[11] & 0xFF);
/*     */ 
/* 364 */     if (0 != optionsLength) {
/* 365 */       byte[] optBytes = new byte[optionsLength];
/*     */ 
/* 367 */       if (optionsLength != readFromStream(optBytes)) {
/* 368 */         this.streamInError = new IOException(Messages.getMessage("attach.DimeStreamError5", "" + optionsLength));
/*     */ 
/* 371 */         throw this.streamInError;
/*     */       }
/* 373 */       optBytes = null;
/*     */ 
/* 375 */       int pad = DimeBodyPart.dimePadding(optionsLength);
/*     */ 
/* 377 */       if (pad != readFromStream(header, 0, pad)) {
/* 378 */         this.streamInError = new IOException(Messages.getMessage("attach.DimeStreamError7"));
/*     */ 
/* 380 */         throw this.streamInError;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 385 */     if (0 < idLength) {
/* 386 */       byte[] idBytes = new byte[idLength];
/*     */ 
/* 388 */       if (idLength != readFromStream(idBytes)) {
/* 389 */         this.streamInError = new IOException(Messages.getMessage("attach.DimeStreamError8"));
/*     */ 
/* 391 */         throw this.streamInError;
/*     */       }
/* 393 */       if ((idLength != 0) && (!isChunk)) {
/* 394 */         this.id = new String(idBytes);
/*     */       }
/* 396 */       int pad = DimeBodyPart.dimePadding(idLength);
/*     */ 
/* 398 */       if (pad != readFromStream(header, 0, pad)) {
/* 399 */         this.streamInError = new IOException(Messages.getMessage("attach.DimeStreamError9"));
/*     */ 
/* 401 */         throw this.streamInError;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 406 */     if (0 < typeLength) {
/* 407 */       byte[] typeBytes = new byte[typeLength];
/*     */ 
/* 409 */       if (typeLength != readFromStream(typeBytes)) {
/* 410 */         this.streamInError = new IOException(Messages.getMessage("attach.DimeStreamError10"));
/*     */ 
/* 412 */         throw this.streamInError;
/*     */       }
/* 414 */       if ((typeLength != 0) && (!isChunk)) {
/* 415 */         this.type = new String(typeBytes);
/*     */       }
/* 417 */       int pad = DimeBodyPart.dimePadding(typeLength);
/*     */ 
/* 419 */       if (pad != readFromStream(header, 0, pad)) {
/* 420 */         this.streamInError = new IOException(Messages.getMessage("attach.DimeStreamError11"));
/*     */ 
/* 423 */         throw this.streamInError;
/*     */       }
/*     */     }
/* 426 */     log.debug("MB:" + this.MB + ", ME:" + this.ME + ", CF:" + this.moreChunks + "Option length:" + optionsLength + ", ID length:" + idLength + ", typeLength:" + typeLength + ", TYPE_T:" + this.tnf);
/*     */ 
/* 430 */     log.debug("id:\"" + this.id + "\"");
/* 431 */     log.debug("type:\"" + this.type + "\"");
/* 432 */     log.debug("recordlength:\"" + this.recordLength + "\"");
/*     */ 
/* 434 */     this.dataPadLength = DimeBodyPart.dimePadding(this.recordLength);
/*     */   }
/*     */ 
/*     */   public int read(byte[] b)
/*     */     throws IOException
/*     */   {
/* 445 */     return read(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/* 456 */     byte[] b = new byte[1];
/* 457 */     int read = read(b, 0, 1);
/*     */ 
/* 459 */     if (read < 0)
/* 460 */       return -1;
/* 461 */     return b[0] & 0xFF;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 475 */     synchronized (this) {
/* 476 */       if (this.closed) return;
/* 477 */       this.closed = true;
/*     */     }
/* 479 */     log.debug(Messages.getMessage("bStreamClosed", "" + this.streamNo));
/* 480 */     if ((this.bytesRead < this.recordLength) || (this.moreChunks))
/*     */     {
/* 483 */       byte[] readrest = new byte[16384];
/* 484 */       int bread = 0;
/*     */       do
/*     */       {
/* 487 */         bread = _read(readrest, 0, readrest.length);
/*     */       }
/* 489 */       while (bread > -1);
/*     */     }
/* 491 */     this.dataPadLength -= readPad(this.dataPadLength);
/*     */   }
/*     */ 
/*     */   public void mark(int readlimit)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */     throws IOException
/*     */   {
/* 503 */     this.streamInError = new IOException(Messages.getMessage("attach.bounday.mns"));
/*     */ 
/* 505 */     throw this.streamInError;
/*     */   }
/*     */ 
/*     */   public boolean markSupported() {
/* 509 */     return false;
/*     */   }
/*     */ 
/*     */   public synchronized int available() throws IOException {
/* 513 */     if (null != this.streamInError) throw this.streamInError;
/* 514 */     int chunkAvail = (int)Math.min(2147483647L, this.recordLength - this.bytesRead);
/*     */ 
/* 517 */     int streamAvail = 0;
/*     */     try
/*     */     {
/* 520 */       streamAvail = this.is.available();
/*     */     } catch (IOException e) {
/* 522 */       this.streamInError = e;
/* 523 */       throw e;
/*     */     }
/*     */ 
/* 526 */     if ((chunkAvail == 0) && (this.moreChunks) && (12 + this.dataPadLength <= streamAvail))
/*     */     {
/* 528 */       this.dataPadLength -= readPad(this.dataPadLength);
/* 529 */       readHeader(true);
/* 530 */       return available();
/*     */     }
/* 532 */     return Math.min(streamAvail, chunkAvail);
/*     */   }
/*     */ 
/*     */   protected void finalClose() throws IOException {
/*     */     try {
/* 537 */       this.theEnd = true;
/* 538 */       if (null != this.is) this.is.close(); 
/*     */     }
/*     */     finally {
/* 540 */       this.is = null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.attachments.DimeDelimitedInputStream
 * JD-Core Version:    0.6.0
 */