/*     */ package org.apache.axis.transport.http;
/*     */ 
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ public class ChunkedInputStream extends FilterInputStream
/*     */ {
/*  30 */   protected long chunkSize = 0L;
/*  31 */   protected volatile boolean closed = false;
/*     */ 
/*  33 */   private static final int maxCharLong = Long.toHexString(9223372036854775807L).toString().length();
/*     */ 
/*  36 */   private byte[] buf = new byte[maxCharLong + 2];
/*     */ 
/*     */   private ChunkedInputStream() {
/*  39 */     super(null);
/*     */   }
/*     */ 
/*     */   public ChunkedInputStream(InputStream is) {
/*  43 */     super(is);
/*     */   }
/*     */ 
/*     */   public synchronized int read() throws IOException
/*     */   {
/*  48 */     if (this.closed)
/*  49 */       return -1;
/*     */     try
/*     */     {
/*  52 */       if ((this.chunkSize < 1L) && 
/*  53 */         (0L == getChunked())) {
/*  54 */         return -1;
/*     */       }
/*     */ 
/*  57 */       int rc = this.in.read();
/*  58 */       if (rc > 0) {
/*  59 */         this.chunkSize -= 1L;
/*     */       }
/*  61 */       return rc;
/*     */     } catch (IOException e) {
/*  63 */       this.closed = true;
/*  64 */     }throw e;
/*     */   }
/*     */ 
/*     */   public int read(byte[] b)
/*     */     throws IOException
/*     */   {
/*  70 */     return read(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   public synchronized int read(byte[] b, int off, int len)
/*     */     throws IOException
/*     */   {
/*  77 */     if (this.closed) {
/*  78 */       return -1;
/*     */     }
/*     */ 
/*  81 */     int totalread = 0;
/*  82 */     int bytesread = 0;
/*     */     try
/*     */     {
/*     */       do {
/*  86 */         if ((this.chunkSize < 1L) && 
/*  87 */           (0L == getChunked())) {
/*  88 */           if (totalread == 0) return -1;
/*  89 */           return totalread;
/*     */         }
/*     */ 
/*  92 */         bytesread = this.in.read(b, off + totalread, Math.min(len - totalread, (int)Math.min(this.chunkSize, 2147483647L)));
/*     */ 
/*  94 */         if (bytesread > 0) {
/*  95 */           totalread += bytesread;
/*  96 */           this.chunkSize -= bytesread;
/*     */         }
/*     */ 
/*  99 */         if (len - totalread <= 0) break; 
/*  99 */       }while (bytesread > -1);
/*     */     } catch (IOException e) {
/* 101 */       this.closed = true;
/* 102 */       throw e;
/*     */     }
/* 104 */     return totalread;
/*     */   }
/*     */ 
/*     */   public long skip(long n) throws IOException
/*     */   {
/* 109 */     if (this.closed) {
/* 110 */       return 0L;
/*     */     }
/* 112 */     long skipped = 0L;
/* 113 */     byte[] b = new byte[1024];
/* 114 */     int bread = -1;
/*     */     do
/*     */     {
/* 117 */       bread = read(b, 0, b.length);
/* 118 */       if (bread <= 0) continue; skipped += bread;
/*     */     }
/* 120 */     while ((bread != -1) && (skipped < n));
/* 121 */     return skipped;
/*     */   }
/*     */ 
/*     */   public int available() throws IOException
/*     */   {
/* 126 */     if (this.closed) {
/* 127 */       return 0;
/*     */     }
/* 129 */     int rc = (int)Math.min(this.chunkSize, 2147483647L);
/*     */ 
/* 131 */     return Math.min(rc, this.in.available());
/*     */   }
/*     */ 
/*     */   protected long getChunked() throws IOException {
/* 135 */     int bufsz = 0;
/* 136 */     this.chunkSize = -1L;
/* 137 */     int c = -1;
/*     */     do
/*     */     {
/* 140 */       c = this.in.read();
/* 141 */       if ((c <= -1) || 
/* 142 */         (c == 13) || (c == 10) || (c == 32) || (c == 9)) continue;
/* 143 */       this.buf[(bufsz++)] = (byte)c;
/*     */     }
/*     */ 
/* 147 */     while ((c > -1) && ((c != 10) || (bufsz == 0)) && (bufsz < this.buf.length));
/* 148 */     if (c < 0) {
/* 149 */       this.closed = true;
/*     */     }
/* 151 */     String sbuf = new String(this.buf, 0, bufsz);
/*     */ 
/* 153 */     if (bufsz > maxCharLong) {
/* 154 */       this.closed = true;
/* 155 */       throw new IOException("Chunked input stream failed to receive valid chunk size:" + sbuf);
/*     */     }
/*     */     try {
/* 158 */       this.chunkSize = Long.parseLong(sbuf, 16);
/*     */     } catch (NumberFormatException ne) {
/* 160 */       this.closed = true;
/* 161 */       throw new IOException("'" + sbuf + "' " + ne.getMessage());
/*     */     }
/* 163 */     if (this.chunkSize < 0L)
/* 164 */       this.closed = true;
/* 165 */     if (this.chunkSize == 0L) {
/* 166 */       this.closed = true;
/*     */ 
/* 168 */       if (this.in.read() != -1) {
/* 169 */         this.in.read();
/*     */       }
/*     */     }
/* 172 */     if ((this.chunkSize != 0L) && (c < 0))
/*     */     {
/* 174 */       throw new IOException("HTTP Chunked stream closed in middle of chunk.");
/*     */     }
/* 176 */     if (this.chunkSize < 0L) {
/* 177 */       throw new IOException("HTTP Chunk size received " + this.chunkSize + " is less than zero.");
/*     */     }
/*     */ 
/* 180 */     return this.chunkSize;
/*     */   }
/*     */ 
/*     */   public void close() throws IOException
/*     */   {
/* 185 */     synchronized (this) {
/* 186 */       if (this.closed) {
/* 187 */         return;
/*     */       }
/* 189 */       this.closed = true;
/*     */     }
/*     */ 
/* 192 */     byte[] b = new byte[1024];
/* 193 */     int bread = -1;
/*     */     do
/*     */     {
/* 196 */       bread = read(b, 0, b.length);
/*     */     }
/* 198 */     while (bread != -1);
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */     throws IOException
/*     */   {
/* 210 */     throw new IOException("Don't support marked streams");
/*     */   }
/*     */ 
/*     */   public boolean markSupported() {
/* 214 */     return false;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.http.ChunkedInputStream
 * JD-Core Version:    0.6.0
 */