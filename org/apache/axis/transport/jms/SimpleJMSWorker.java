/*     */ package org.apache.axis.transport.jms;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.InputStream;
/*     */ import javax.jms.BytesMessage;
/*     */ import javax.jms.Destination;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Message;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.server.AxisServer;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class SimpleJMSWorker
/*     */   implements Runnable
/*     */ {
/*  44 */   protected static Log log = LogFactory.getLog(SimpleJMSWorker.class.getName());
/*     */   SimpleJMSListener listener;
/*     */   BytesMessage message;
/*     */ 
/*     */   public SimpleJMSWorker(SimpleJMSListener listener, BytesMessage message)
/*     */   {
/*  52 */     this.listener = listener;
/*  53 */     this.message = message;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  61 */     InputStream in = null;
/*     */     try
/*     */     {
/*  65 */       byte[] buffer = new byte[8192];
/*  66 */       ByteArrayOutputStream out = new ByteArrayOutputStream();
/*  67 */       int bytesRead = this.message.readBytes(buffer);
/*  68 */       for (; bytesRead != -1; bytesRead = this.message.readBytes(buffer))
/*     */       {
/*  70 */         out.write(buffer, 0, bytesRead);
/*     */       }
/*  72 */       in = new ByteArrayInputStream(out.toByteArray());
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  76 */       log.error(Messages.getMessage("exception00"), e);
/*  77 */       e.printStackTrace();
/*  78 */       return;
/*     */     }
/*     */ 
/*  82 */     AxisServer server = SimpleJMSListener.getAxisServer();
/*     */ 
/*  86 */     String contentType = null;
/*     */     try
/*     */     {
/*  89 */       contentType = this.message.getStringProperty("contentType");
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  93 */       e.printStackTrace();
/*     */     }
/*     */ 
/*  96 */     Message msg = null;
/*  97 */     if ((contentType != null) && (!contentType.trim().equals("")))
/*     */     {
/*  99 */       msg = new Message(in, true, contentType, null);
/*     */     }
/*     */     else
/*     */     {
/* 103 */       msg = new Message(in);
/*     */     }
/*     */ 
/* 106 */     MessageContext msgContext = new MessageContext(server);
/* 107 */     msgContext.setRequestMessage(msg);
/*     */     try
/*     */     {
/* 110 */       server.invoke(msgContext);
/* 111 */       msg = msgContext.getResponseMessage();
/*     */     }
/*     */     catch (AxisFault af)
/*     */     {
/* 115 */       msg = new Message(af);
/* 116 */       msg.setMessageContext(msgContext);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 120 */       msg = new Message(new AxisFault(e.toString()));
/* 121 */       msg.setMessageContext(msgContext);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 127 */       Destination destination = this.message.getJMSReplyTo();
/* 128 */       if (destination == null)
/* 129 */         return;
/* 130 */       JMSEndpoint replyTo = this.listener.getConnector().createEndpoint(destination);
/* 131 */       ByteArrayOutputStream out = new ByteArrayOutputStream();
/* 132 */       msg.writeTo(out);
/* 133 */       replyTo.send(out.toByteArray());
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 137 */       e.printStackTrace();
/*     */     }
/*     */ 
/* 140 */     if (msgContext.getProperty("quit.requested") != null)
/*     */       try {
/* 142 */         this.listener.shutdown();
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.jms.SimpleJMSWorker
 * JD-Core Version:    0.6.0
 */