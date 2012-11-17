/*    */ package org.apache.axis.handlers;
/*    */ 
/*    */ import org.apache.axis.AxisFault;
/*    */ import org.apache.axis.Message;
/*    */ import org.apache.axis.MessageContext;
/*    */ import org.apache.axis.SOAPPart;
/*    */ import org.apache.axis.monitor.SOAPMonitorService;
/*    */ 
/*    */ public class SOAPMonitorHandler extends BasicHandler
/*    */ {
/* 35 */   private static long next_message_id = 1L;
/*    */ 
/*    */   public void invoke(MessageContext messageContext)
/*    */     throws AxisFault
/*    */   {
/* 48 */     String target = messageContext.getTargetService();
/*    */ 
/* 50 */     if (target == null)
/* 51 */       target = "";
/*    */     Message message;
/*    */     Long id;
/*    */     Integer type;
/*    */     Message message;
/* 57 */     if (!messageContext.getPastPivot()) {
/* 58 */       Long id = assignMessageId(messageContext);
/* 59 */       Integer type = new Integer(0);
/* 60 */       message = messageContext.getRequestMessage();
/*    */     } else {
/* 62 */       id = getMessageId(messageContext);
/* 63 */       type = new Integer(1);
/* 64 */       message = messageContext.getResponseMessage();
/*    */     }
/*    */ 
/* 67 */     String soap = null;
/* 68 */     if (message != null) {
/* 69 */       soap = ((SOAPPart)message.getSOAPPart()).getAsString();
/*    */     }
/*    */ 
/* 73 */     if ((id != null) && (soap != null))
/* 74 */       SOAPMonitorService.publishMessage(id, type, target, soap);
/*    */   }
/*    */ 
/*    */   private Long assignMessageId(MessageContext messageContext)
/*    */   {
/* 82 */     Long id = null;
/* 83 */     synchronized ("SOAPMonitorId") {
/* 84 */       id = new Long(next_message_id);
/* 85 */       next_message_id += 1L;
/*    */     }
/* 87 */     messageContext.setProperty("SOAPMonitorId", id);
/* 88 */     return id;
/*    */   }
/*    */ 
/*    */   private Long getMessageId(MessageContext messageContext)
/*    */   {
/* 95 */     Long id = null;
/* 96 */     id = (Long)messageContext.getProperty("SOAPMonitorId");
/* 97 */     return id;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.handlers.SOAPMonitorHandler
 * JD-Core Version:    0.6.0
 */