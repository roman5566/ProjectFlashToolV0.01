/*     */ package org.apache.axis.transport.local;
/*     */ 
/*     */ import java.net.URL;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Message;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.attachments.Attachments;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.handlers.BasicHandler;
/*     */ import org.apache.axis.message.SOAPEnvelope;
/*     */ import org.apache.axis.message.SOAPFault;
/*     */ import org.apache.axis.server.AxisServer;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class LocalSender extends BasicHandler
/*     */ {
/*  40 */   protected static Log log = LogFactory.getLog(LocalSender.class.getName());
/*     */   private volatile AxisServer server;
/*     */ 
/*     */   public synchronized void init()
/*     */   {
/*  49 */     this.server = new AxisServer();
/*     */   }
/*     */ 
/*     */   public void invoke(MessageContext clientContext) throws AxisFault {
/*  53 */     if (log.isDebugEnabled()) {
/*  54 */       log.debug("Enter: LocalSender::invoke");
/*     */     }
/*     */ 
/*  57 */     AxisServer targetServer = (AxisServer)clientContext.getProperty("LocalTransport.AxisServer");
/*     */ 
/*  60 */     if (log.isDebugEnabled()) {
/*  61 */       log.debug(Messages.getMessage("usingServer00", "LocalSender", "" + targetServer));
/*     */     }
/*     */ 
/*  65 */     if (targetServer == null)
/*     */     {
/*  68 */       if (this.server == null) init();
/*  69 */       targetServer = this.server;
/*     */     }
/*     */ 
/*  73 */     MessageContext serverContext = new MessageContext(targetServer);
/*     */ 
/*  80 */     Message clientRequest = clientContext.getRequestMessage();
/*     */ 
/*  82 */     String msgStr = clientRequest.getSOAPPartAsString();
/*     */ 
/*  84 */     if (log.isDebugEnabled()) {
/*  85 */       log.debug(Messages.getMessage("sendingXML00", "LocalSender"));
/*  86 */       log.debug(msgStr);
/*     */     }
/*     */ 
/*  89 */     Message serverRequest = new Message(msgStr);
/*     */ 
/*  91 */     Attachments serverAttachments = serverRequest.getAttachmentsImpl();
/*  92 */     Attachments clientAttachments = clientRequest.getAttachmentsImpl();
/*     */ 
/*  94 */     if ((null != clientAttachments) && (null != serverAttachments)) {
/*  95 */       serverAttachments.setAttachmentParts(clientAttachments.getAttachments());
/*     */     }
/*     */ 
/*  98 */     serverContext.setRequestMessage(serverRequest);
/*     */ 
/* 102 */     serverContext.setTransportName("local");
/*     */ 
/* 105 */     String user = clientContext.getUsername();
/* 106 */     if (user != null) {
/* 107 */       serverContext.setUsername(user);
/* 108 */       String pass = clientContext.getPassword();
/* 109 */       if (pass != null) {
/* 110 */         serverContext.setPassword(pass);
/*     */       }
/*     */     }
/*     */ 
/* 114 */     String transURL = clientContext.getStrProp("transport.url");
/* 115 */     if (transURL != null) {
/*     */       try {
/* 117 */         URL url = new URL(transURL);
/* 118 */         String file = url.getFile();
/* 119 */         if ((file.length() > 0) && (file.charAt(0) == '/')) {
/* 120 */           file = file.substring(1);
/*     */         }
/* 122 */         serverContext.setProperty("realpath", file);
/* 123 */         serverContext.setProperty("transport.url", "local:///" + file);
/*     */ 
/* 126 */         serverContext.setTargetService(file);
/*     */       } catch (Exception e) {
/* 128 */         throw AxisFault.makeFault(e);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 135 */     String remoteService = clientContext.getStrProp("LocalTransport.RemoteService");
/* 136 */     if (remoteService != null) {
/* 137 */       serverContext.setTargetService(remoteService);
/*     */     }
/*     */     try
/*     */     {
/* 141 */       targetServer.invoke(serverContext);
/*     */     } catch (AxisFault fault) {
/* 143 */       Message respMsg = serverContext.getResponseMessage();
/* 144 */       if (respMsg == null) {
/* 145 */         respMsg = new Message(fault);
/* 146 */         serverContext.setResponseMessage(respMsg);
/*     */       } else {
/* 148 */         SOAPFault faultEl = new SOAPFault(fault);
/* 149 */         SOAPEnvelope env = respMsg.getSOAPEnvelope();
/* 150 */         env.clearBody();
/* 151 */         env.addBodyElement(faultEl);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 157 */     clientContext.setResponseMessage(serverContext.getResponseMessage());
/* 158 */     clientContext.getResponseMessage().getSOAPPartAsString();
/*     */ 
/* 160 */     if (log.isDebugEnabled())
/* 161 */       log.debug("Exit: LocalSender::invoke");
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.local.LocalSender
 * JD-Core Version:    0.6.0
 */