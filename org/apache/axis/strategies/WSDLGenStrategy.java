/*    */ package org.apache.axis.strategies;
/*    */ 
/*    */ import org.apache.axis.AxisFault;
/*    */ import org.apache.axis.Handler;
/*    */ import org.apache.axis.HandlerIterationStrategy;
/*    */ import org.apache.axis.MessageContext;
/*    */ 
/*    */ public class WSDLGenStrategy
/*    */   implements HandlerIterationStrategy
/*    */ {
/*    */   public void visit(Handler handler, MessageContext msgContext)
/*    */     throws AxisFault
/*    */   {
/* 33 */     handler.generateWSDL(msgContext);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.strategies.WSDLGenStrategy
 * JD-Core Version:    0.6.0
 */