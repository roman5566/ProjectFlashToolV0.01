/*    */ package org.apache.axis.transport.local;
/*    */ 
/*    */ import org.apache.axis.AxisFault;
/*    */ import org.apache.axis.Message;
/*    */ import org.apache.axis.MessageContext;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.handlers.BasicHandler;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class LocalResponder extends BasicHandler
/*    */ {
/* 33 */   protected static Log log = LogFactory.getLog(LocalResponder.class.getName());
/*    */ 
/*    */   public void invoke(MessageContext msgContext) throws AxisFault
/*    */   {
/* 37 */     if (log.isDebugEnabled()) {
/* 38 */       log.debug("Enter: LocalResponder::invoke");
/*    */     }
/*    */ 
/* 41 */     String msgStr = msgContext.getResponseMessage().getSOAPPartAsString();
/*    */ 
/* 43 */     if (log.isDebugEnabled()) {
/* 44 */       log.debug(msgStr);
/*    */ 
/* 46 */       log.debug("Exit: LocalResponder::invoke");
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.local.LocalResponder
 * JD-Core Version:    0.6.0
 */