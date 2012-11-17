/*    */ package org.apache.axis.handlers;
/*    */ 
/*    */ import org.apache.axis.AxisFault;
/*    */ import org.apache.axis.Constants;
/*    */ import org.apache.axis.Message;
/*    */ import org.apache.axis.MessageContext;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.message.SOAPEnvelope;
/*    */ import org.apache.axis.message.SOAPHeaderElement;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class DebugHandler extends BasicHandler
/*    */ {
/* 35 */   protected static Log log = LogFactory.getLog(DebugHandler.class.getName());
/*    */   public static final String NS_URI_DEBUG = "http://xml.apache.org/axis/debug";
/*    */ 
/*    */   public void invoke(MessageContext msgContext)
/*    */     throws AxisFault
/*    */   {
/* 41 */     log.debug("Enter: DebugHandler::invoke");
/*    */     try {
/* 43 */       Message msg = msgContext.getRequestMessage();
/*    */ 
/* 45 */       SOAPEnvelope message = msg.getSOAPEnvelope();
/* 46 */       SOAPHeaderElement header = message.getHeaderByName("http://xml.apache.org/axis/debug", "Debug");
/*    */ 
/* 49 */       if (header != null) {
/* 50 */         Integer i = (Integer)header.getValueAsType(Constants.XSD_INT);
/*    */ 
/* 52 */         if (i == null) {
/* 53 */           throw new AxisFault(Messages.getMessage("cantConvert03"));
/*    */         }
/* 55 */         int debugVal = i.intValue();
/* 56 */         log.debug(Messages.getMessage("debugLevel00", "" + debugVal));
/*    */ 
/* 58 */         header.setProcessed(true);
/*    */       }
/*    */     }
/*    */     catch (Exception e) {
/* 62 */       log.error(Messages.getMessage("exception00"), e);
/* 63 */       throw AxisFault.makeFault(e);
/*    */     }
/* 65 */     log.debug("Exit: DebugHandler::invoke");
/*    */   }
/*    */ 
/*    */   public void onFault(MessageContext msgContext) {
/* 69 */     log.debug("Enter: DebugHandler::onFault");
/* 70 */     log.debug("Exit: DebugHandler::onFault");
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.handlers.DebugHandler
 * JD-Core Version:    0.6.0
 */