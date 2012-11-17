/*    */ package org.apache.axis.encoding;
/*    */ 
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public class DeserializerTarget
/*    */   implements Target
/*    */ {
/*    */   public Deserializer target;
/*    */   public Object hint;
/*    */ 
/*    */   public DeserializerTarget(Deserializer target, Object hint)
/*    */   {
/* 31 */     this.target = target;
/* 32 */     this.hint = hint;
/*    */   }
/*    */ 
/*    */   public void set(Object value) throws SAXException {
/* 36 */     if (this.hint != null)
/* 37 */       this.target.setChildValue(value, this.hint);
/*    */     else
/* 39 */       this.target.setValue(value);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.DeserializerTarget
 * JD-Core Version:    0.6.0
 */