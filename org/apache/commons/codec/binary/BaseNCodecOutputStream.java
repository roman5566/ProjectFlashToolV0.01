/*     */ package org.apache.commons.codec.binary;
/*     */ 
/*     */ import java.io.FilterOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ public class BaseNCodecOutputStream extends FilterOutputStream
/*     */ {
/*     */   private final boolean doEncode;
/*     */   private final BaseNCodec baseNCodec;
/*  35 */   private final byte[] singleByte = new byte[1];
/*     */ 
/*     */   public BaseNCodecOutputStream(OutputStream out, BaseNCodec basedCodec, boolean doEncode) {
/*  38 */     super(out);
/*  39 */     this.baseNCodec = basedCodec;
/*  40 */     this.doEncode = doEncode;
/*     */   }
/*     */ 
/*     */   public void write(int i)
/*     */     throws IOException
/*     */   {
/*  53 */     this.singleByte[0] = (byte)i;
/*  54 */     write(this.singleByte, 0, 1);
/*     */   }
/*     */ 
/*     */   public void write(byte[] b, int offset, int len)
/*     */     throws IOException
/*     */   {
/*  77 */     if (b == null)
/*  78 */       throw new NullPointerException();
/*  79 */     if ((offset < 0) || (len < 0))
/*  80 */       throw new IndexOutOfBoundsException();
/*  81 */     if ((offset > b.length) || (offset + len > b.length))
/*  82 */       throw new IndexOutOfBoundsException();
/*  83 */     if (len > 0) {
/*  84 */       if (this.doEncode)
/*  85 */         this.baseNCodec.encode(b, offset, len);
/*     */       else {
/*  87 */         this.baseNCodec.decode(b, offset, len);
/*     */       }
/*  89 */       flush(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void flush(boolean propogate)
/*     */     throws IOException
/*     */   {
/* 103 */     int avail = this.baseNCodec.available();
/* 104 */     if (avail > 0) {
/* 105 */       byte[] buf = new byte[avail];
/* 106 */       int c = this.baseNCodec.readResults(buf, 0, avail);
/* 107 */       if (c > 0) {
/* 108 */         this.out.write(buf, 0, c);
/*     */       }
/*     */     }
/* 111 */     if (propogate)
/* 112 */       this.out.flush();
/*     */   }
/*     */ 
/*     */   public void flush()
/*     */     throws IOException
/*     */   {
/* 124 */     flush(true);
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 136 */     if (this.doEncode)
/* 137 */       this.baseNCodec.encode(this.singleByte, 0, -1);
/*     */     else {
/* 139 */       this.baseNCodec.decode(this.singleByte, 0, -1);
/*     */     }
/* 141 */     flush();
/* 142 */     this.out.close();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.binary.BaseNCodecOutputStream
 * JD-Core Version:    0.6.0
 */