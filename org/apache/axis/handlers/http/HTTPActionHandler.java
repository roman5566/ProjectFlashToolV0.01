/*    */ package org.apache.axis.handlers.http;
/*    */ 
/*    */ import org.apache.axis.AxisFault;
/*    */ import org.apache.axis.MessageContext;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.handlers.BasicHandler;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class HTTPActionHandler extends BasicHandler
/*    */ {
/* 40 */   protected static Log log = LogFactory.getLog(HTTPActionHandler.class.getName());
/*    */ 
/*    */   public void invoke(MessageContext msgContext)
/*    */     throws AxisFault
/*    */   {
/* 45 */     log.debug("Enter: HTTPActionHandler::invoke");
/*    */ 
/* 49 */     if (msgContext.getService() == null) {
/* 50 */       String action = msgContext.getSOAPActionURI();
/* 51 */       log.debug("  HTTP SOAPAction: " + action);
/*    */ 
/* 57 */       if (action == null) {
/* 58 */         throw new AxisFault("Server.NoHTTPSOAPAction", Messages.getMessage("noSOAPAction00"), null, null);
/*    */       }
/*    */ 
/* 63 */       action = action.trim();
/*    */ 
/* 66 */       if ((action.length() > 0) && (action.charAt(0) == '"'))
/*    */       {
/* 68 */         if (action.equals("\"\""))
/* 69 */           action = "";
/*    */         else {
/* 71 */           action = action.substring(1, action.length() - 1);
/*    */         }
/*    */ 
/*    */       }
/*    */ 
/* 76 */       if (action.length() > 0) {
/* 77 */         msgContext.setTargetService(action);
/*    */       }
/*    */     }
/*    */ 
/* 81 */     log.debug("Exit: HTTPActionHandler::invoke");
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.handlers.http.HTTPActionHandler
 * JD-Core Version:    0.6.0
 */