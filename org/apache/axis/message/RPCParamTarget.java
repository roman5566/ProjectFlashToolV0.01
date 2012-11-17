/*    */ package org.apache.axis.message;
/*    */ 
/*    */ import org.apache.axis.encoding.Target;
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public class RPCParamTarget
/*    */   implements Target
/*    */ {
/*    */   private RPCParam param;
/*    */ 
/*    */   public RPCParamTarget(RPCParam param)
/*    */   {
/* 28 */     this.param = param;
/*    */   }
/*    */ 
/*    */   public void set(Object value) throws SAXException {
/* 32 */     this.param.set(value);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.RPCParamTarget
 * JD-Core Version:    0.6.0
 */