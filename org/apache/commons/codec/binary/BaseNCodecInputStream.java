/*     */ package org.apache.commons.codec.binary;
/*     */ 
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ public class BaseNCodecInputStream extends FilterInputStream
/*     */ {
/*     */   private final boolean doEncode;
/*     */   private final BaseNCodec baseNCodec;
/*  35 */   private final byte[] singleByte = new byte[1];
/*     */ 
/*     */   protected BaseNCodecInputStream(InputStream in, BaseNCodec baseNCodec, boolean doEncode) {
/*  38 */     super(in);
/*  39 */     this.doEncode = doEncode;
/*  40 */     this.baseNCodec = baseNCodec;
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*  52 */     int r = read(this.singleByte, 0, 1);
/*  53 */     while (r == 0) {
/*  54 */       r = read(this.singleByte, 0, 1);
/*     */     }
/*  56 */     if (r > 0) {
/*  57 */       return this.singleByte[0] < 0 ? 256 + this.singleByte[0] : this.singleByte[0];
/*     */     }
/*  59 */     return -1;
/*     */   }
/*     */ 
/*     */   public int read(byte[] b, int offset, int len)
/*     */     throws IOException
/*     */   {
/*  83 */     if (b == null)
/*  84 */       throw new NullPointerException();
/*  85 */     if ((offset < 0) || (len < 0))
/*  86 */       throw new IndexOutOfBoundsException();
/*  87 */     if ((offset > b.length) || (offset + len > b.length))
/*  88 */       throw new IndexOutOfBoundsException();
/*  89 */     if (len == 0) {
/*  90 */       return 0;
/*     */     }
/*  92 */     int readLen = 0;
/*     */ 
/* 109 */     while (readLen == 0) {
/* 110 */       if (!this.baseNCodec.hasData()) {
/* 111 */         byte[] buf = new byte[this.doEncode ? 4096 : 8192];
/* 112 */         int c = this.in.read(buf);
/* 113 */         if (this.doEncode)
/* 114 */           this.baseNCodec.encode(buf, 0, c);
/*     */         else {
/* 116 */           this.baseNCodec.decode(buf, 0, c);
/*     */         }
/*     */       }
/* 119 */       readLen = this.baseNCodec.readResults(b, offset, len);
/*     */     }
/* 121 */     return readLen;
/*     */   }
/*     */ 
/*     */   public boolean markSupported()
/*     */   {
/* 131 */     return false;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.binary.BaseNCodecInputStream
 * JD-Core Version:    0.6.0
 */