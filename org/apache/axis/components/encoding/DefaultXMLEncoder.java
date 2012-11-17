/*    */ package org.apache.axis.components.encoding;
/*    */ 
/*    */ public class DefaultXMLEncoder extends UTF8Encoder
/*    */ {
/* 20 */   private String encoding = null;
/*    */ 
/*    */   public DefaultXMLEncoder(String encoding) {
/* 23 */     this.encoding = encoding;
/*    */   }
/*    */ 
/*    */   public String getEncoding()
/*    */   {
/* 32 */     return this.encoding;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.encoding.DefaultXMLEncoder
 * JD-Core Version:    0.6.0
 */