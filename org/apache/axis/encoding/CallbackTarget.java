/*    */ package org.apache.axis.encoding;
/*    */ 
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public class CallbackTarget
/*    */   implements Target
/*    */ {
/*    */   public Callback target;
/*    */   public Object hint;
/*    */ 
/*    */   public CallbackTarget(Callback target, Object hint)
/*    */   {
/* 31 */     this.target = target;
/* 32 */     this.hint = hint;
/*    */   }
/*    */ 
/*    */   public void set(Object value) throws SAXException {
/* 36 */     this.target.setValue(value, this.hint);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.CallbackTarget
 * JD-Core Version:    0.6.0
 */