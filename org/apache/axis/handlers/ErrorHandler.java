/*    */ package org.apache.axis.handlers;
/*    */ 
/*    */ import org.apache.axis.AxisFault;
/*    */ import org.apache.axis.MessageContext;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class ErrorHandler extends BasicHandler
/*    */ {
/* 31 */   protected static Log log = LogFactory.getLog(ErrorHandler.class.getName());
/*    */ 
/*    */   public void invoke(MessageContext msgContext) throws AxisFault
/*    */   {
/* 35 */     log.debug("Enter: ErrorHandler::invoke");
/* 36 */     throw new AxisFault("Server.Whatever", "ERROR", null, null);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.handlers.ErrorHandler
 * JD-Core Version:    0.6.0
 */