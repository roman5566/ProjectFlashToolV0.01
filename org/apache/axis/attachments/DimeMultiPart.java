/*    */ package org.apache.axis.attachments;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import java.util.Vector;
/*    */ 
/*    */ public final class DimeMultiPart
/*    */ {
/*    */   static final long transSize = 2147483647L;
/*    */   static final byte CURRENT_VERSION = 1;
/* 34 */   protected Vector parts = new Vector();
/*    */ 
/*    */   public void addBodyPart(DimeBodyPart part) {
/* 37 */     this.parts.add(part);
/*    */   }
/*    */ 
/*    */   public void write(OutputStream os) throws IOException
/*    */   {
/* 42 */     int size = this.parts.size();
/* 43 */     int last = size - 1;
/*    */ 
/* 45 */     for (int i = 0; i < size; i++)
/* 46 */       ((DimeBodyPart)this.parts.elementAt(i)).write(os, (byte)((i == 0 ? 4 : 0) | (i == last ? 2 : 0)), 2147483647L);
/*    */   }
/*    */ 
/*    */   public long getTransmissionSize()
/*    */   {
/* 54 */     long size = 0L;
/*    */ 
/* 56 */     for (int i = this.parts.size() - 1; i > -1; i--) {
/* 57 */       size += ((DimeBodyPart)this.parts.elementAt(i)).getTransmissionSize(2147483647L);
/*    */     }
/*    */ 
/* 61 */     return size;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.attachments.DimeMultiPart
 * JD-Core Version:    0.6.0
 */