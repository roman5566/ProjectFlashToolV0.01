/*     */ package org.apache.axis.transport.http;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ public class NonBlockingBufferedInputStream extends InputStream
/*     */ {
/*     */   private InputStream in;
/*  28 */   private int remainingContent = 2147483647;
/*     */ 
/*  31 */   private byte[] buffer = new byte[4096];
/*  32 */   private int offset = 0;
/*  33 */   private int numbytes = 0;
/*     */ 
/*     */   public void setInputStream(InputStream in)
/*     */   {
/*  40 */     this.in = in;
/*  41 */     this.numbytes = 0;
/*  42 */     this.offset = 0;
/*  43 */     this.remainingContent = (in == null ? 0 : 2147483647);
/*     */   }
/*     */ 
/*     */   public void setContentLength(int value)
/*     */   {
/*  52 */     if (this.in != null) this.remainingContent = (value - (this.numbytes - this.offset));
/*     */   }
/*     */ 
/*     */   private void refillBuffer()
/*     */     throws IOException
/*     */   {
/*  63 */     if ((this.remainingContent <= 0) || (this.in == null)) return;
/*     */ 
/*  66 */     this.numbytes = this.in.available();
/*  67 */     if (this.numbytes > this.remainingContent) this.numbytes = this.remainingContent;
/*  68 */     if (this.numbytes > this.buffer.length) this.numbytes = this.buffer.length;
/*  69 */     if (this.numbytes <= 0) this.numbytes = 1;
/*     */ 
/*  72 */     this.numbytes = this.in.read(this.buffer, 0, this.numbytes);
/*     */ 
/*  75 */     this.remainingContent -= this.numbytes;
/*  76 */     this.offset = 0;
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*  85 */     if (this.in == null) return -1;
/*  86 */     if (this.offset >= this.numbytes) refillBuffer();
/*  87 */     if (this.offset >= this.numbytes) return -1;
/*  88 */     return this.buffer[(this.offset++)] & 0xFF;
/*     */   }
/*     */ 
/*     */   public int read(byte[] dest)
/*     */     throws IOException
/*     */   {
/* 100 */     return read(dest, 0, dest.length);
/*     */   }
/*     */ 
/*     */   public int read(byte[] dest, int off, int len)
/*     */     throws IOException
/*     */   {
/* 115 */     int ready = this.numbytes - this.offset;
/*     */ 
/* 117 */     if (ready >= len) {
/* 118 */       System.arraycopy(this.buffer, this.offset, dest, off, len);
/* 119 */       this.offset += len;
/* 120 */       return len;
/* 121 */     }if (ready > 0) {
/* 122 */       System.arraycopy(this.buffer, this.offset, dest, off, ready);
/* 123 */       this.offset = this.numbytes;
/* 124 */       return ready;
/*     */     }
/* 126 */     if (this.in == null) return -1;
/* 127 */     refillBuffer();
/* 128 */     if (this.offset >= this.numbytes) return -1;
/* 129 */     return read(dest, off, len);
/*     */   }
/*     */ 
/*     */   public int skip(int len)
/*     */     throws IOException
/*     */   {
/* 140 */     int count = 0;
/* 141 */     while ((len-- > 0) && (read() >= 0)) count++;
/* 142 */     return count;
/*     */   }
/*     */ 
/*     */   public int available()
/*     */     throws IOException
/*     */   {
/* 150 */     if (this.in == null) return 0;
/*     */ 
/* 153 */     return this.numbytes - this.offset + this.in.available();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 160 */     setInputStream(null);
/*     */   }
/*     */ 
/*     */   public int peek()
/*     */     throws IOException
/*     */   {
/* 170 */     if (this.in == null) return -1;
/* 171 */     if (this.offset >= this.numbytes) refillBuffer();
/* 172 */     if (this.offset >= this.numbytes) return -1;
/* 173 */     return this.buffer[this.offset] & 0xFF;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.http.NonBlockingBufferedInputStream
 * JD-Core Version:    0.6.0
 */