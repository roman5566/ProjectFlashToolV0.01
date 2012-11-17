/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class ByteArrayOutputStream extends OutputStream
/*     */ {
/*  49 */   private List buffers = new ArrayList();
/*     */   private int currentBufferIndex;
/*     */   private int filledBufferSum;
/*     */   private byte[] currentBuffer;
/*     */   private int count;
/*     */ 
/*     */   public ByteArrayOutputStream()
/*     */   {
/*  60 */     this(1024);
/*     */   }
/*     */ 
/*     */   public ByteArrayOutputStream(int size)
/*     */   {
/*  71 */     if (size < 0) {
/*  72 */       throw new IllegalArgumentException(Messages.getMessage("illegalArgumentException01", Integer.toString(size)));
/*     */     }
/*     */ 
/*  76 */     needNewBuffer(size);
/*     */   }
/*     */ 
/*     */   private byte[] getBuffer(int index) {
/*  80 */     return (byte[])this.buffers.get(index);
/*     */   }
/*     */ 
/*     */   private void needNewBuffer(int newcount) {
/*  84 */     if (this.currentBufferIndex < this.buffers.size() - 1)
/*     */     {
/*  86 */       this.filledBufferSum += this.currentBuffer.length;
/*  87 */       this.currentBufferIndex += 1;
/*  88 */       this.currentBuffer = getBuffer(this.currentBufferIndex);
/*     */     }
/*     */     else
/*     */     {
/*     */       int newBufferSize;
/*  92 */       if (this.currentBuffer == null) {
/*  93 */         int newBufferSize = newcount;
/*  94 */         this.filledBufferSum = 0;
/*     */       } else {
/*  96 */         newBufferSize = Math.max(this.currentBuffer.length << 1, newcount - this.filledBufferSum);
/*     */ 
/*  98 */         this.filledBufferSum += this.currentBuffer.length;
/*     */       }
/* 100 */       this.currentBufferIndex += 1;
/* 101 */       this.currentBuffer = new byte[newBufferSize];
/* 102 */       this.buffers.add(this.currentBuffer);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void write(byte[] b, int off, int len)
/*     */   {
/* 110 */     if ((off < 0) || (off > b.length) || (len < 0) || (off + len > b.length) || (off + len < 0))
/*     */     {
/* 115 */       throw new IndexOutOfBoundsException(Messages.getMessage("indexOutOfBoundsException00"));
/*     */     }
/* 117 */     if (len == 0) {
/* 118 */       return;
/*     */     }
/* 120 */     int newcount = this.count + len;
/* 121 */     int remaining = len;
/* 122 */     int inBufferPos = this.count - this.filledBufferSum;
/* 123 */     while (remaining > 0) {
/* 124 */       int part = Math.min(remaining, this.currentBuffer.length - inBufferPos);
/* 125 */       System.arraycopy(b, off + len - remaining, this.currentBuffer, inBufferPos, part);
/*     */ 
/* 127 */       remaining -= part;
/* 128 */       if (remaining > 0) {
/* 129 */         needNewBuffer(newcount);
/* 130 */         inBufferPos = 0;
/*     */       }
/*     */     }
/* 133 */     this.count = newcount;
/*     */   }
/*     */ 
/*     */   public synchronized void write(int b)
/*     */   {
/* 142 */     write(new byte[] { (byte)b }, 0, 1);
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 149 */     return this.count;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*     */   }
/*     */ 
/*     */   public synchronized void reset()
/*     */   {
/* 167 */     this.count = 0;
/* 168 */     this.filledBufferSum = 0;
/* 169 */     this.currentBufferIndex = 0;
/* 170 */     this.currentBuffer = getBuffer(this.currentBufferIndex);
/*     */   }
/*     */ 
/*     */   public synchronized void writeTo(OutputStream out)
/*     */     throws IOException
/*     */   {
/* 177 */     int remaining = this.count;
/* 178 */     for (int i = 0; i < this.buffers.size(); i++) {
/* 179 */       byte[] buf = getBuffer(i);
/* 180 */       int c = Math.min(buf.length, remaining);
/* 181 */       out.write(buf, 0, c);
/* 182 */       remaining -= c;
/* 183 */       if (remaining == 0)
/*     */         break;
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized byte[] toByteArray()
/*     */   {
/* 193 */     int remaining = this.count;
/* 194 */     int pos = 0;
/* 195 */     byte[] newbuf = new byte[this.count];
/* 196 */     for (int i = 0; i < this.buffers.size(); i++) {
/* 197 */       byte[] buf = getBuffer(i);
/* 198 */       int c = Math.min(buf.length, remaining);
/* 199 */       System.arraycopy(buf, 0, newbuf, pos, c);
/* 200 */       pos += c;
/* 201 */       remaining -= c;
/* 202 */       if (remaining == 0) {
/*     */         break;
/*     */       }
/*     */     }
/* 206 */     return newbuf;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 213 */     return new String(toByteArray());
/*     */   }
/*     */ 
/*     */   public String toString(String enc)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 220 */     return new String(toByteArray(), enc);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.ByteArrayOutputStream
 * JD-Core Version:    0.6.0
 */