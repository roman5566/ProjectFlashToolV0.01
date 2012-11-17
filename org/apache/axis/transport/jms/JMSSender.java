/*     */ package org.apache.axis.transport.jms;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.jms.Destination;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Message;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.attachments.Attachments;
/*     */ import org.apache.axis.handlers.BasicHandler;
/*     */ 
/*     */ public class JMSSender extends BasicHandler
/*     */ {
/*     */   public void invoke(MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/*  54 */     JMSConnector connector = null;
/*     */     try
/*     */     {
/*  57 */       Object destination = msgContext.getProperty("transport.jms.Destination");
/*  58 */       if (destination == null) {
/*  59 */         throw new AxisFault("noDestination");
/*     */       }
/*  61 */       connector = (JMSConnector)msgContext.getProperty("transport.jms.Connector");
/*     */ 
/*  63 */       JMSEndpoint endpoint = null;
/*  64 */       if ((destination instanceof String))
/*  65 */         endpoint = connector.createEndpoint((String)destination);
/*     */       else {
/*  67 */         endpoint = connector.createEndpoint((Destination)destination);
/*     */       }
/*  69 */       ByteArrayOutputStream out = new ByteArrayOutputStream();
/*  70 */       msgContext.getRequestMessage().writeTo(out);
/*     */ 
/*  72 */       HashMap props = createSendProperties(msgContext);
/*     */ 
/*  76 */       String ret = null;
/*  77 */       Message message = msgContext.getRequestMessage();
/*  78 */       Attachments mAttachments = message.getAttachmentsImpl();
/*  79 */       if ((mAttachments != null) && (0 != mAttachments.getAttachmentCount()))
/*     */       {
/*  81 */         String contentType = mAttachments.getContentType();
/*  82 */         if ((contentType != null) && (!contentType.trim().equals("")))
/*     */         {
/*  84 */           props.put("contentType", contentType);
/*     */         }
/*     */       }
/*     */ 
/*  88 */       boolean waitForResponse = true;
/*  89 */       if (msgContext.containsProperty("transport.jms.waitForResponse")) {
/*  90 */         waitForResponse = ((Boolean)msgContext.getProperty("transport.jms.waitForResponse")).booleanValue();
/*     */       }
/*     */ 
/*  93 */       if (waitForResponse)
/*     */       {
/*  95 */         long timeout = msgContext.getTimeout();
/*  96 */         byte[] response = endpoint.call(out.toByteArray(), timeout, props);
/*  97 */         Message msg = new Message(response);
/*  98 */         msgContext.setResponseMessage(msg);
/*     */       }
/*     */       else
/*     */       {
/* 102 */         endpoint.send(out.toByteArray(), props);
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 107 */       throw new AxisFault("failedSend", e);
/*     */     }
/*     */     finally
/*     */     {
/* 111 */       if (connector != null)
/* 112 */         JMSConnectorManager.getInstance().release(connector);
/*     */     }
/*     */   }
/*     */ 
/*     */   private HashMap createSendProperties(MessageContext context)
/*     */   {
/* 122 */     HashMap props = createApplicationProperties(context);
/*     */ 
/* 124 */     if (context.containsProperty("transport.jms.priority")) {
/* 125 */       props.put("transport.jms.priority", context.getProperty("transport.jms.priority"));
/*     */     }
/* 127 */     if (context.containsProperty("transport.jms.deliveryMode")) {
/* 128 */       props.put("transport.jms.deliveryMode", context.getProperty("transport.jms.deliveryMode"));
/*     */     }
/* 130 */     if (context.containsProperty("transport.jms.ttl")) {
/* 131 */       props.put("transport.jms.ttl", context.getProperty("transport.jms.ttl"));
/*     */     }
/* 133 */     if (context.containsProperty("transport.jms.jmsCorrelationID")) {
/* 134 */       props.put("transport.jms.jmsCorrelationID", context.getProperty("transport.jms.jmsCorrelationID"));
/*     */     }
/* 136 */     return props;
/*     */   }
/*     */ 
/*     */   protected HashMap createApplicationProperties(MessageContext context)
/*     */   {
/* 143 */     HashMap props = null;
/* 144 */     if (context.containsProperty("transport.jms.msgProps"))
/*     */     {
/* 146 */       props = new HashMap();
/* 147 */       props.putAll((Map)context.getProperty("transport.jms.msgProps"));
/*     */     }
/*     */ 
/* 150 */     return props;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.jms.JMSSender
 * JD-Core Version:    0.6.0
 */