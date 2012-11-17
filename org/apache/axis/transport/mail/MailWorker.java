/*     */ package org.apache.axis.transport.mail;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.Writer;
/*     */ import java.util.Properties;
/*     */ import javax.mail.Session;
/*     */ import javax.mail.internet.InternetAddress;
/*     */ import javax.mail.internet.MimeMessage;
/*     */ import javax.mail.internet.MimeMessage.RecipientType;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Message;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.message.SOAPEnvelope;
/*     */ import org.apache.axis.message.SOAPFault;
/*     */ import org.apache.axis.server.AxisServer;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.net.smtp.SMTPClient;
/*     */ import org.apache.commons.net.smtp.SMTPReply;
/*     */ 
/*     */ public class MailWorker
/*     */   implements Runnable
/*     */ {
/*  43 */   protected static Log log = LogFactory.getLog(MailWorker.class.getName());
/*     */   private MailServer server;
/*     */   private MimeMessage mimeMessage;
/*  53 */   private static String transportName = "Mail";
/*     */ 
/*  55 */   private Properties prop = new Properties();
/*  56 */   private Session session = Session.getDefaultInstance(this.prop, null);
/*     */ 
/*     */   public MailWorker(MailServer server, MimeMessage mimeMessage)
/*     */   {
/*  64 */     this.server = server;
/*  65 */     this.mimeMessage = mimeMessage;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  73 */     AxisServer engine = MailServer.getAxisServer();
/*     */ 
/*  76 */     MessageContext msgContext = new MessageContext(engine);
/*     */ 
/*  80 */     StringBuffer soapAction = new StringBuffer();
/*  81 */     StringBuffer fileName = new StringBuffer();
/*  82 */     StringBuffer contentType = new StringBuffer();
/*  83 */     StringBuffer contentLocation = new StringBuffer();
/*     */ 
/*  85 */     Message responseMsg = null;
/*     */     try
/*     */     {
/*  90 */       msgContext.setTargetService(null);
/*     */     } catch (AxisFault fault) {
/*     */     }
/*  93 */     msgContext.setResponseMessage(null);
/*  94 */     msgContext.reset();
/*  95 */     msgContext.setTransportName(transportName);
/*     */ 
/*  97 */     responseMsg = null;
/*     */     try
/*     */     {
/*     */       try
/*     */       {
/* 102 */         parseHeaders(this.mimeMessage, contentType, contentLocation, soapAction);
/*     */ 
/* 107 */         msgContext.setProperty("realpath", fileName.toString());
/*     */ 
/* 109 */         msgContext.setProperty("path", fileName.toString());
/*     */ 
/* 111 */         msgContext.setProperty("jws.classDir", "jwsClasses");
/*     */ 
/* 116 */         String soapActionString = soapAction.toString();
/* 117 */         if (soapActionString != null) {
/* 118 */           msgContext.setUseSOAPAction(true);
/* 119 */           msgContext.setSOAPActionURI(soapActionString);
/*     */         }
/* 121 */         Message requestMsg = new Message(this.mimeMessage.getInputStream(), false, contentType.toString(), contentLocation.toString());
/*     */ 
/* 123 */         msgContext.setRequestMessage(requestMsg);
/*     */ 
/* 126 */         engine.invoke(msgContext);
/*     */ 
/* 129 */         responseMsg = msgContext.getResponseMessage();
/* 130 */         if (responseMsg == null)
/* 131 */           throw new AxisFault(Messages.getMessage("nullResponse00"));
/*     */       }
/*     */       catch (Exception e) {
/* 134 */         e.printStackTrace();
/*     */         AxisFault af;
/* 136 */         if ((e instanceof AxisFault)) {
/* 137 */           AxisFault af = (AxisFault)e;
/* 138 */           log.debug(Messages.getMessage("serverFault00"), af);
/*     */         } else {
/* 140 */           af = AxisFault.makeFault(e);
/*     */         }
/*     */ 
/* 146 */         responseMsg = msgContext.getResponseMessage();
/* 147 */         if (responseMsg == null)
/* 148 */           responseMsg = new Message(af);
/*     */         else {
/*     */           try {
/* 151 */             SOAPEnvelope env = responseMsg.getSOAPEnvelope();
/* 152 */             env.clearBody();
/* 153 */             env.addBodyElement(new SOAPFault((AxisFault)e));
/*     */           }
/*     */           catch (AxisFault fault)
/*     */           {
/*     */           }
/*     */         }
/*     */       }
/* 160 */       String replyTo = ((InternetAddress)this.mimeMessage.getReplyTo()[0]).getAddress();
/* 161 */       String sendFrom = ((InternetAddress)this.mimeMessage.getAllRecipients()[0]).getAddress();
/* 162 */       String subject = "Re: " + this.mimeMessage.getSubject();
/* 163 */       writeUsingSMTP(msgContext, this.server.getHost(), sendFrom, replyTo, subject, responseMsg);
/*     */     } catch (Exception e) {
/* 165 */       e.printStackTrace();
/* 166 */       log.debug(Messages.getMessage("exception00"), e);
/*     */     }
/* 168 */     if (msgContext.getProperty("quit.requested") != null)
/*     */       try
/*     */       {
/* 171 */         this.server.stop();
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/*     */   }
/*     */ 
/*     */   private void writeUsingSMTP(MessageContext msgContext, String smtpHost, String sendFrom, String replyTo, String subject, Message output)
/*     */     throws Exception
/*     */   {
/* 195 */     SMTPClient client = new SMTPClient();
/* 196 */     client.connect(smtpHost);
/*     */ 
/* 200 */     System.out.print(client.getReplyString());
/* 201 */     int reply = client.getReplyCode();
/* 202 */     if (!SMTPReply.isPositiveCompletion(reply)) {
/* 203 */       client.disconnect();
/* 204 */       AxisFault fault = new AxisFault("SMTP", "( SMTP server refused connection )", null, null);
/* 205 */       throw fault;
/*     */     }
/*     */ 
/* 208 */     client.login(smtpHost);
/* 209 */     System.out.print(client.getReplyString());
/* 210 */     reply = client.getReplyCode();
/* 211 */     if (!SMTPReply.isPositiveCompletion(reply)) {
/* 212 */       client.disconnect();
/* 213 */       AxisFault fault = new AxisFault("SMTP", "( SMTP server refused connection )", null, null);
/* 214 */       throw fault;
/*     */     }
/*     */ 
/* 217 */     MimeMessage msg = new MimeMessage(this.session);
/* 218 */     msg.setFrom(new InternetAddress(sendFrom));
/* 219 */     msg.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(replyTo));
/* 220 */     msg.setDisposition("inline");
/* 221 */     msg.setSubject(subject);
/*     */ 
/* 223 */     ByteArrayOutputStream out = new ByteArrayOutputStream(8192);
/* 224 */     output.writeTo(out);
/* 225 */     msg.setContent(out.toString(), output.getContentType(msgContext.getSOAPConstants()));
/*     */ 
/* 227 */     ByteArrayOutputStream out2 = new ByteArrayOutputStream(8192);
/* 228 */     msg.writeTo(out2);
/*     */ 
/* 230 */     client.setSender(sendFrom);
/* 231 */     System.out.print(client.getReplyString());
/* 232 */     client.addRecipient(replyTo);
/* 233 */     System.out.print(client.getReplyString());
/*     */ 
/* 235 */     Writer writer = client.sendMessageData();
/* 236 */     System.out.print(client.getReplyString());
/* 237 */     writer.write(out2.toString());
/* 238 */     writer.flush();
/* 239 */     writer.close();
/*     */ 
/* 241 */     System.out.print(client.getReplyString());
/* 242 */     if (!client.completePendingCommand()) {
/* 243 */       System.out.print(client.getReplyString());
/* 244 */       AxisFault fault = new AxisFault("SMTP", "( Failed to send email )", null, null);
/* 245 */       throw fault;
/*     */     }
/* 247 */     System.out.print(client.getReplyString());
/* 248 */     client.logout();
/* 249 */     client.disconnect();
/*     */   }
/*     */ 
/*     */   private void parseHeaders(MimeMessage mimeMessage, StringBuffer contentType, StringBuffer contentLocation, StringBuffer soapAction)
/*     */     throws Exception
/*     */   {
/* 265 */     contentType.append(mimeMessage.getContentType());
/* 266 */     contentLocation.append(mimeMessage.getContentID());
/* 267 */     String[] values = mimeMessage.getHeader("SOAPAction");
/* 268 */     if (values != null)
/* 269 */       soapAction.append(values[0]);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.mail.MailWorker
 * JD-Core Version:    0.6.0
 */