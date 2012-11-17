/*    */ package org.apache.axis.components.encoding;
/*    */ 
/*    */ import java.io.UnsupportedEncodingException;
/*    */ 
/*    */ class EncodedByteArray
/*    */ {
/* 32 */   private byte[] array = null;
/*    */   private int pointer;
/* 35 */   private final double PADDING = 1.5D;
/*    */ 
/*    */   public EncodedByteArray(byte[] bytes, int startPos, int length)
/*    */   {
/* 39 */     this.array = new byte[(int)(bytes.length * 1.5D)];
/* 40 */     System.arraycopy(bytes, startPos, this.array, 0, length);
/* 41 */     this.pointer = length;
/*    */   }
/*    */ 
/*    */   public EncodedByteArray(int size) {
/* 45 */     this.array = new byte[size];
/*    */   }
/*    */ 
/*    */   public void append(int aByte) {
/* 49 */     if (this.pointer + 1 >= this.array.length) {
/* 50 */       byte[] newArray = new byte[(int)(this.array.length * 1.5D)];
/* 51 */       System.arraycopy(this.array, 0, newArray, 0, this.pointer);
/* 52 */       this.array = newArray;
/*    */     }
/* 54 */     this.array[this.pointer] = (byte)aByte;
/* 55 */     this.pointer += 1;
/*    */   }
/*    */ 
/*    */   public void append(byte[] byteArray) {
/* 59 */     if (this.pointer + byteArray.length >= this.array.length) {
/* 60 */       byte[] newArray = new byte[(int)(this.array.length * 1.5D) + byteArray.length];
/* 61 */       System.arraycopy(this.array, 0, newArray, 0, this.pointer);
/* 62 */       this.array = newArray;
/*    */     }
/*    */ 
/* 65 */     System.arraycopy(byteArray, 0, this.array, this.pointer, byteArray.length);
/* 66 */     this.pointer += byteArray.length;
/*    */   }
/*    */ 
/*    */   public void append(byte[] byteArray, int pos, int length) {
/* 70 */     if (this.pointer + length >= this.array.length) {
/* 71 */       byte[] newArray = new byte[(int)(this.array.length * 1.5D) + byteArray.length];
/* 72 */       System.arraycopy(this.array, 0, newArray, 0, this.pointer);
/* 73 */       this.array = newArray;
/*    */     }
/* 75 */     System.arraycopy(byteArray, pos, this.array, this.pointer, length);
/* 76 */     this.pointer += length;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 84 */     return new String(this.array, 0, this.pointer);
/*    */   }
/*    */ 
/*    */   public String toString(String charsetName)
/*    */     throws UnsupportedEncodingException
/*    */   {
/* 94 */     return new String(this.array, 0, this.pointer, charsetName);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.encoding.EncodedByteArray
 * JD-Core Version:    0.6.0
 */