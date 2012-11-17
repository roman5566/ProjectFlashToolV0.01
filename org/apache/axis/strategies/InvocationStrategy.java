/*    */ package org.apache.axis.strategies;
/*    */ 
/*    */ import org.apache.axis.AxisFault;
/*    */ import org.apache.axis.Handler;
/*    */ import org.apache.axis.HandlerIterationStrategy;
/*    */ import org.apache.axis.MessageContext;
/*    */ 
/*    */ public class InvocationStrategy
/*    */   implements HandlerIterationStrategy
/*    */ {
/*    */   public void visit(Handler handler, MessageContext msgContext)
/*    */     throws AxisFault
/*    */   {
/* 32 */     handler.invoke(msgContext);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.strategies.InvocationStrategy
 * JD-Core Version:    0.6.0
 */