/*    */ package org.apache.axis.message;
/*    */ 
/*    */ import org.w3c.dom.CDATASection;
/*    */ 
/*    */ public class CDATAImpl extends Text
/*    */   implements CDATASection
/*    */ {
/*    */   static final String cdataUC = "<![CDATA[";
/*    */   static final String cdataLC = "<![cdata[";
/*    */ 
/*    */   public CDATAImpl(String text)
/*    */   {
/* 29 */     super(text);
/*    */   }
/*    */ 
/*    */   public boolean isComment() {
/* 33 */     return false;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.CDATAImpl
 * JD-Core Version:    0.6.0
 */