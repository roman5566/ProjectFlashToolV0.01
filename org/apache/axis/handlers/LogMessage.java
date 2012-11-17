/*    */ package org.apache.axis.handlers;
/*    */ 
/*    */ import org.apache.axis.MessageContext;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class LogMessage extends BasicHandler
/*    */ {
/* 30 */   protected static Log log = LogFactory.getLog(LogMessage.class.getName());
/*    */ 
/*    */   public void invoke(MessageContext context)
/*    */   {
/* 35 */     String msg = (String)getOption("message");
/* 36 */     if (msg != null)
/* 37 */       log.info(msg);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.handlers.LogMessage
 * JD-Core Version:    0.6.0
 */