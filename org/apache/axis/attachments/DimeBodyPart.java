/*     */ package org.apache.axis.attachments;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.StringTokenizer;
/*     */ import javax.activation.DataHandler;
/*     */ import javax.activation.DataSource;
/*     */ import javax.activation.FileDataSource;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class DimeBodyPart
/*     */ {
/*  71 */   protected static Log log = LogFactory.getLog(DimeBodyPart.class.getName());
/*     */ 
/*  74 */   protected Object data = null;
/*  75 */   protected DimeTypeNameFormat dtnf = null;
/*  76 */   protected byte[] type = null;
/*  77 */   protected byte[] id = null;
/*     */   static final byte POSITION_FIRST = 4;
/*     */   static final byte POSITION_LAST = 2;
/*     */   private static final byte CHUNK = 1;
/*     */   private static final byte CHUNK_NEXT = 2;
/*     */   private static final byte ONLY_CHUNK = -1;
/*     */   private static final byte LAST_CHUNK = 0;
/*  84 */   private static int MAX_TYPE_LENGTH = 65535;
/*  85 */   private static int MAX_ID_LENGTH = 65535;
/*     */   static final long MAX_DWORD = 4294967295L;
/* 249 */   private static final byte[] pad = new byte[4];
/*     */   static final byte CURRENT_OPT_T = 0;
/*     */ 
/*     */   protected DimeBodyPart()
/*     */   {
/*     */   }
/*     */ 
/*     */   public DimeBodyPart(byte[] data, DimeTypeNameFormat format, String type, String id)
/*     */   {
/* 103 */     System.arraycopy(data, 0, this.data = new byte[data.length], 0, data.length);
/* 104 */     this.dtnf = format;
/* 105 */     this.type = type.getBytes();
/* 106 */     if (this.type.length > MAX_TYPE_LENGTH) {
/* 107 */       throw new IllegalArgumentException(Messages.getMessage("attach.dimetypeexceedsmax", "" + this.type.length, "" + MAX_TYPE_LENGTH));
/*     */     }
/*     */ 
/* 110 */     this.id = id.getBytes();
/* 111 */     if (this.id.length > MAX_ID_LENGTH)
/* 112 */       throw new IllegalArgumentException(Messages.getMessage("attach.dimelengthexceedsmax", "" + this.id.length, "" + MAX_ID_LENGTH));
/*     */   }
/*     */ 
/*     */   public DimeBodyPart(DataHandler dh, DimeTypeNameFormat format, String type, String id)
/*     */   {
/* 127 */     this.data = dh;
/* 128 */     this.dtnf = format;
/* 129 */     if ((type == null) || (type.length() == 0))
/* 130 */       type = "application/octet-stream";
/* 131 */     this.type = type.getBytes();
/* 132 */     if (this.type.length > MAX_TYPE_LENGTH) {
/* 133 */       throw new IllegalArgumentException(Messages.getMessage("attach.dimetypeexceedsmax", "" + this.type.length, "" + MAX_TYPE_LENGTH));
/*     */     }
/*     */ 
/* 136 */     this.id = id.getBytes();
/* 137 */     if (this.id.length > MAX_ID_LENGTH)
/* 138 */       throw new IllegalArgumentException(Messages.getMessage("attach.dimelengthexceedsmax", "" + this.id.length, "" + MAX_ID_LENGTH));
/*     */   }
/*     */ 
/*     */   public DimeBodyPart(DataHandler dh, String id)
/*     */   {
/* 151 */     this(dh, DimeTypeNameFormat.MIME, dh.getContentType(), id);
/*     */ 
/* 153 */     String ct = dh.getContentType();
/*     */ 
/* 155 */     if (ct != null) {
/* 156 */       ct = ct.trim();
/* 157 */       if (ct.toLowerCase().startsWith("application/uri")) {
/* 158 */         StringTokenizer st = new StringTokenizer(ct, " \t;");
/* 159 */         String t = st.nextToken(" \t;");
/*     */ 
/* 161 */         if (t.equalsIgnoreCase("application/uri"))
/* 162 */           while (st.hasMoreTokens()) {
/* 163 */             t = st.nextToken(" \t;");
/* 164 */             if (t.equalsIgnoreCase("uri")) {
/* 165 */               t = st.nextToken("=");
/* 166 */               if (t != null) {
/* 167 */                 t = t.trim();
/* 168 */                 if (t.startsWith("\"")) t = t.substring(1);
/*     */ 
/* 171 */                 if (t.endsWith("\"")) t = t.substring(0, t.length() - 1);
/*     */ 
/* 173 */                 this.type = t.getBytes();
/* 174 */                 this.dtnf = DimeTypeNameFormat.URI;
/*     */               }
/* 176 */               return;
/* 177 */             }if (t.equalsIgnoreCase("uri=")) {
/* 178 */               t = st.nextToken(" \t;");
/* 179 */               if ((null != t) && (t.length() != 0)) {
/* 180 */                 t = t.trim();
/* 181 */                 if (t.startsWith("\"")) t = t.substring(1);
/*     */ 
/* 183 */                 if (t.endsWith("\"")) t = t.substring(0, t.length() - 1);
/*     */ 
/* 185 */                 this.type = t.getBytes();
/* 186 */                 this.dtnf = DimeTypeNameFormat.URI;
/* 187 */                 return;
/*     */               }
/*     */             }
/* 189 */             if ((!t.toLowerCase().startsWith("uri=")) || 
/* 190 */               (-1 == t.indexOf('='))) continue;
/* 191 */             t = t.substring(t.indexOf('=')).trim();
/* 192 */             if (t.length() != 0) {
/* 193 */               t = t.trim();
/* 194 */               if (t.startsWith("\"")) t = t.substring(1);
/*     */ 
/* 197 */               if (t.endsWith("\""))
/* 198 */                 t = t.substring(0, t.length() - 1);
/* 199 */               this.type = t.getBytes();
/* 200 */               this.dtnf = DimeTypeNameFormat.URI;
/* 201 */               return;
/*     */             }
/*     */           }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void write(OutputStream os, byte position, long maxchunk)
/*     */     throws IOException
/*     */   {
/* 222 */     if (maxchunk < 1L) throw new IllegalArgumentException(Messages.getMessage("attach.dimeMaxChunkSize0", "" + maxchunk));
/*     */ 
/* 224 */     if (maxchunk > 4294967295L) throw new IllegalArgumentException(Messages.getMessage("attach.dimeMaxChunkSize1", "" + maxchunk));
/*     */ 
/* 226 */     if ((this.data instanceof byte[])) {
/* 227 */       send(os, position, (byte[])this.data, maxchunk);
/* 228 */     } else if ((this.data instanceof DynamicContentDataHandler)) {
/* 229 */       send(os, position, (DynamicContentDataHandler)this.data, maxchunk);
/* 230 */     } else if ((this.data instanceof DataHandler)) {
/* 231 */       DataSource source = ((DataHandler)this.data).getDataSource();
/* 232 */       DynamicContentDataHandler dh2 = new DynamicContentDataHandler(source);
/* 233 */       send(os, position, dh2, maxchunk);
/*     */     }
/*     */   }
/*     */ 
/*     */   void write(OutputStream os, byte position)
/*     */     throws IOException
/*     */   {
/* 246 */     write(os, position, 4294967295L);
/*     */   }
/*     */ 
/*     */   void send(OutputStream os, byte position, byte[] data, long maxchunk)
/*     */     throws IOException
/*     */   {
/* 253 */     send(os, position, data, 0, data.length, maxchunk);
/*     */   }
/*     */ 
/*     */   void send(OutputStream os, byte position, byte[] data, int offset, int length, long maxchunk)
/*     */     throws IOException
/*     */   {
/* 260 */     byte chunknext = 0;
/*     */     do
/*     */     {
/* 263 */       int sendlength = (int)Math.min(maxchunk, length - offset);
/*     */ 
/* 265 */       sendChunk(os, position, data, offset, sendlength, (byte)((sendlength < length - offset ? 1 : 0) | chunknext));
/*     */ 
/* 268 */       offset += sendlength;
/* 269 */       chunknext = 2;
/*     */     }
/* 271 */     while (offset < length);
/*     */   }
/*     */ 
/*     */   void send(OutputStream os, byte position, DataHandler dh, long maxchunk) throws IOException
/*     */   {
/* 276 */     InputStream in = null;
/*     */     try { long dataSize = getDataSize();
/* 279 */       in = dh.getInputStream();
/* 280 */       byte[] readbuf = new byte[65536];
/*     */ 
/* 283 */       sendHeader(os, position, dataSize, 0);
/* 284 */       long totalsent = 0L;
/*     */       int bytesread;
/*     */       do { bytesread = in.read(readbuf);
/* 288 */         if (bytesread > 0) {
/* 289 */           os.write(readbuf, 0, bytesread);
/* 290 */           totalsent += bytesread;
/*     */         }
/*     */       }
/* 293 */       while (bytesread > -1);
/* 294 */       os.write(pad, 0, dimePadding(totalsent));
/*     */     } finally
/*     */     {
/* 297 */       if (in != null)
/*     */         try {
/* 299 */           in.close();
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   void send(OutputStream os, byte position, DynamicContentDataHandler dh, long maxchunk)
/*     */     throws IOException
/*     */   {
/* 323 */     BufferedInputStream in = new BufferedInputStream(dh.getInputStream());
/*     */ 
/* 325 */     int myChunkSize = dh.getChunkSize();
/*     */ 
/* 327 */     byte[] buffer1 = new byte[myChunkSize];
/* 328 */     byte[] buffer2 = new byte[myChunkSize];
/*     */ 
/* 330 */     int bytesRead1 = 0; int bytesRead2 = 0;
/*     */ 
/* 332 */     bytesRead1 = in.read(buffer1);
/*     */ 
/* 334 */     if (bytesRead1 < 0) {
/* 335 */       sendHeader(os, position, 0L, -1);
/* 336 */       os.write(pad, 0, dimePadding(0L));
/* 337 */       return;
/*     */     }
/* 339 */     byte chunkbyte = 1;
/*     */     do {
/* 341 */       bytesRead2 = in.read(buffer2);
/*     */ 
/* 343 */       if (bytesRead2 < 0)
/*     */       {
/* 349 */         if (chunkbyte == 1)
/* 350 */           chunkbyte = -1;
/*     */         else {
/* 352 */           chunkbyte = 0;
/*     */         }
/* 354 */         sendChunk(os, position, buffer1, 0, bytesRead1, chunkbyte);
/* 355 */         break;
/*     */       }
/*     */ 
/* 358 */       sendChunk(os, position, buffer1, 0, bytesRead1, chunkbyte);
/*     */ 
/* 361 */       chunkbyte = 2;
/*     */ 
/* 363 */       System.arraycopy(buffer2, 0, buffer1, 0, myChunkSize);
/* 364 */       bytesRead1 = bytesRead2;
/*     */     }
/* 366 */     while (bytesRead2 > 0);
/*     */   }
/*     */ 
/*     */   protected void sendChunk(OutputStream os, byte position, byte[] data, byte chunk)
/*     */     throws IOException
/*     */   {
/* 373 */     sendChunk(os, position, data, 0, data.length, chunk);
/*     */   }
/*     */ 
/*     */   protected void sendChunk(OutputStream os, byte position, byte[] data, int offset, int length, byte chunk)
/*     */     throws IOException
/*     */   {
/* 380 */     sendHeader(os, position, length, chunk);
/* 381 */     os.write(data, offset, length);
/* 382 */     os.write(pad, 0, dimePadding(length));
/*     */   }
/*     */ 
/*     */   protected void sendHeader(OutputStream os, byte position, long length, byte chunk)
/*     */     throws IOException
/*     */   {
/* 390 */     byte[] fixedHeader = new byte[12];
/*     */ 
/* 393 */     boolean isFirstChunk = (chunk == 1) || (chunk == -1);
/*     */ 
/* 398 */     if (chunk == 2)
/* 399 */       chunk = 1;
/* 400 */     else if (chunk == -1) {
/* 401 */       chunk = 0;
/*     */     }
/*     */ 
/* 405 */     fixedHeader[0] = 8;
/*     */     int tmp55_54 = 0;
/*     */     byte[] tmp55_52 = fixedHeader; tmp55_52[tmp55_54] = (byte)(tmp55_52[tmp55_54] | (byte)(position & 0x6 & ((chunk & 0x1) != 0 ? -3 : -1) & ((chunk & 0x2) != 0 ? -5 : -1)));
/*     */     int tmp96_95 = 0;
/*     */     byte[] tmp96_93 = fixedHeader; tmp96_93[tmp96_95] = (byte)(tmp96_93[tmp96_95] | chunk & 0x1);
/*     */ 
/* 413 */     boolean MB = 0 != (0x4 & fixedHeader[0]);
/*     */ 
/* 415 */     if ((MB) || (isFirstChunk))
/* 416 */       fixedHeader[1] = (byte)(this.dtnf.toByte() << 4 & 0xF0);
/*     */     else
/* 418 */       fixedHeader[1] = 0;
/*     */     int tmp161_160 = 1;
/*     */     byte[] tmp161_158 = fixedHeader; tmp161_158[tmp161_160] = (byte)(tmp161_158[tmp161_160] | 0x0);
/*     */ 
/* 425 */     fixedHeader[2] = 0;
/* 426 */     fixedHeader[3] = 0;
/*     */ 
/* 429 */     if (((MB) || (isFirstChunk)) && (this.id != null) && (this.id.length > 0)) {
/* 430 */       fixedHeader[4] = (byte)(this.id.length >>> 8 & 0xFF);
/* 431 */       fixedHeader[5] = (byte)(this.id.length & 0xFF);
/*     */     } else {
/* 433 */       fixedHeader[4] = 0;
/* 434 */       fixedHeader[5] = 0;
/*     */     }
/*     */ 
/* 438 */     if ((MB) || (isFirstChunk)) {
/* 439 */       fixedHeader[6] = (byte)(this.type.length >>> 8 & 0xFF);
/* 440 */       fixedHeader[7] = (byte)(this.type.length & 0xFF);
/*     */     } else {
/* 442 */       fixedHeader[6] = 0;
/* 443 */       fixedHeader[7] = 0;
/*     */     }
/*     */ 
/* 447 */     fixedHeader[8] = (byte)(int)(length >>> 24 & 0xFF);
/* 448 */     fixedHeader[9] = (byte)(int)(length >>> 16 & 0xFF);
/* 449 */     fixedHeader[10] = (byte)(int)(length >>> 8 & 0xFF);
/* 450 */     fixedHeader[11] = (byte)(int)(length & 0xFF);
/*     */ 
/* 452 */     os.write(fixedHeader);
/*     */ 
/* 458 */     if (((MB) || (isFirstChunk)) && (this.id != null) && (this.id.length > 0)) {
/* 459 */       os.write(this.id);
/* 460 */       os.write(pad, 0, dimePadding(this.id.length));
/*     */     }
/*     */ 
/* 464 */     if ((MB) || (isFirstChunk)) {
/* 465 */       os.write(this.type);
/* 466 */       os.write(pad, 0, dimePadding(this.type.length));
/*     */     }
/*     */   }
/*     */ 
/*     */   static final int dimePadding(long l) {
/* 471 */     return (int)(4L - (l & 0x3) & 0x3);
/*     */   }
/*     */ 
/*     */   long getTransmissionSize(long chunkSize) {
/* 475 */     long size = 0L;
/* 476 */     size += this.id.length;
/* 477 */     size += dimePadding(this.id.length);
/* 478 */     size += this.type.length;
/* 479 */     size += dimePadding(this.type.length);
/*     */ 
/* 481 */     long dataSize = getDataSize();
/*     */ 
/* 483 */     if (0L == dataSize) {
/* 484 */       size += 12L;
/*     */     }
/*     */     else {
/* 487 */       long fullChunks = dataSize / chunkSize;
/* 488 */       long lastChunkSize = dataSize % chunkSize;
/*     */ 
/* 490 */       if (0L != lastChunkSize) size += 12L;
/* 491 */       size += 12L * fullChunks;
/* 492 */       size += fullChunks * dimePadding(chunkSize);
/* 493 */       size += dimePadding(lastChunkSize);
/* 494 */       size += dataSize;
/*     */     }
/* 496 */     return size;
/*     */   }
/*     */ 
/*     */   long getTransmissionSize() {
/* 500 */     return getTransmissionSize(4294967295L);
/*     */   }
/*     */ 
/*     */   protected long getDataSize() {
/* 504 */     if ((this.data instanceof byte[])) return ((byte[])this.data).length;
/* 505 */     if ((this.data instanceof DataHandler))
/* 506 */       return getDataSize((DataHandler)this.data);
/* 507 */     return -1L;
/*     */   }
/*     */ 
/*     */   protected long getDataSize(DataHandler dh) {
/* 511 */     long dataSize = -1L;
/*     */     try
/*     */     {
/* 514 */       DataSource ds = dh.getDataSource();
/*     */ 
/* 518 */       if ((ds instanceof FileDataSource)) {
/* 519 */         FileDataSource fdh = (FileDataSource)ds;
/*     */ 
/* 521 */         File df = fdh.getFile();
/*     */ 
/* 523 */         if (!df.exists()) {
/* 524 */           throw new RuntimeException(Messages.getMessage("noFile", df.getAbsolutePath()));
/*     */         }
/*     */ 
/* 528 */         dataSize = df.length(); } else { dataSize = 0L;
/* 531 */         InputStream in = ds.getInputStream();
/* 532 */         byte[] readbuf = new byte[65536];
/*     */         int bytesread;
/*     */         do { bytesread = in.read(readbuf);
/* 537 */           if (bytesread <= 0) continue; dataSize += bytesread;
/*     */         }
/* 539 */         while (bytesread > -1);
/*     */ 
/* 541 */         if (in.markSupported())
/*     */         {
/* 544 */           in.reset();
/*     */         }
/*     */         else
/*     */         {
/* 548 */           in.close();
/*     */         } }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 553 */       log.error(Messages.getMessage("exception00"), e);
/*     */     }
/* 555 */     return dataSize;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.attachments.DimeBodyPart
 * JD-Core Version:    0.6.0
 */