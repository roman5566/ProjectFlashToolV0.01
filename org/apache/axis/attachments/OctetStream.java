/*    */ package org.apache.axis.attachments;
/*    */ 
/*    */ public class OctetStream
/*    */ {
/* 21 */   private byte[] bytes = null;
/*    */ 
/*    */   public OctetStream() {
/*    */   }
/*    */ 
/*    */   public OctetStream(byte[] bytes) {
/* 27 */     this.bytes = bytes;
/*    */   }
/*    */ 
/*    */   public byte[] getBytes() {
/* 31 */     return this.bytes;
/*    */   }
/*    */ 
/*    */   public void setBytes(byte[] bytes) {
/* 35 */     this.bytes = bytes;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.attachments.OctetStream
 * JD-Core Version:    0.6.0
 */