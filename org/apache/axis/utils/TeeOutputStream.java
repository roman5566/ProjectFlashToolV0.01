/*    */ package org.apache.axis.utils;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class TeeOutputStream extends OutputStream
/*    */ {
/*    */   private OutputStream left;
/*    */   private OutputStream right;
/*    */ 
/*    */   public TeeOutputStream(OutputStream left, OutputStream right)
/*    */   {
/* 27 */     this.left = left;
/* 28 */     this.right = right;
/*    */   }
/*    */ 
/*    */   public void close() throws IOException {
/* 32 */     this.left.close();
/* 33 */     this.right.close();
/*    */   }
/*    */ 
/*    */   public void flush() throws IOException {
/* 37 */     this.left.flush();
/* 38 */     this.right.flush();
/*    */   }
/*    */ 
/*    */   public void write(byte[] b) throws IOException {
/* 42 */     this.left.write(b);
/* 43 */     this.right.write(b);
/*    */   }
/*    */ 
/*    */   public void write(byte[] b, int off, int len) throws IOException {
/* 47 */     this.left.write(b, off, len);
/* 48 */     this.right.write(b, off, len);
/*    */   }
/*    */ 
/*    */   public void write(int b) throws IOException {
/* 52 */     this.left.write(b);
/* 53 */     this.right.write(b);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.TeeOutputStream
 * JD-Core Version:    0.6.0
 */