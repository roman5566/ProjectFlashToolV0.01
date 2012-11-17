/*     */ package org.apache.axis.transport.mail;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.Reader;
/*     */ import java.io.Writer;
/*     */ import java.rmi.server.UID;
/*     */ import java.util.Properties;
/*     */ import javax.mail.Session;
/*     */ import javax.mail.internet.InternetAddress;
/*     */ import javax.mail.internet.MimeMessage;
/*     */ import javax.mail.internet.MimeMessage.RecipientType;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Message;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.components.uuid.UUIDGen;
/*     */ import org.apache.axis.components.uuid.UUIDGenFactory;
/*     */ import org.apache.axis.handlers.BasicHandler;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.net.pop3.POP3Client;
/*     */ import org.apache.commons.net.pop3.POP3MessageInfo;
/*     */ import org.apache.commons.net.smtp.SMTPClient;
/*     */ import org.apache.commons.net.smtp.SMTPReply;
/*     */ 
/*     */ public class MailSender extends BasicHandler
/*     */ {
/*  51 */   protected static Log log = LogFactory.getLog(MailSender.class.getName());
/*  52 */   private UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();
/*     */ 
/*  54 */   Properties prop = new Properties();
/*  55 */   Session session = Session.getDefaultInstance(this.prop, null);
/*     */ 
/*     */   public void invoke(MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/*  67 */     if (log.isDebugEnabled()) {
/*  68 */       log.debug(Messages.getMessage("enter00", "MailSender::invoke"));
/*     */     }
/*     */     try
/*     */     {
/*  72 */       String id = writeUsingSMTP(msgContext);
/*     */ 
/*  75 */       readUsingPOP3(id, msgContext);
/*     */     } catch (Exception e) {
/*  77 */       log.debug(e);
/*  78 */       throw AxisFault.makeFault(e);
/*     */     }
/*  80 */     if (log.isDebugEnabled())
/*  81 */       log.debug(Messages.getMessage("exit00", "HTTPDispatchHandler::invoke"));
/*     */   }
/*     */ 
/*     */   private String writeUsingSMTP(MessageContext msgContext)
/*     */     throws Exception
/*     */   {
/*  97 */     String id = new UID().toString();
/*  98 */     String smtpHost = msgContext.getStrProp("transport.mail.smtp.host");
/*     */ 
/* 100 */     SMTPClient client = new SMTPClient();
/* 101 */     client.connect(smtpHost);
/*     */ 
/* 105 */     System.out.print(client.getReplyString());
/* 106 */     int reply = client.getReplyCode();
/* 107 */     if (!SMTPReply.isPositiveCompletion(reply)) {
/* 108 */       client.disconnect();
/* 109 */       AxisFault fault = new AxisFault("SMTP", "( SMTP server refused connection )", null, null);
/* 110 */       throw fault;
/*     */     }
/*     */ 
/* 113 */     client.login(smtpHost);
/* 114 */     System.out.print(client.getReplyString());
/* 115 */     reply = client.getReplyCode();
/* 116 */     if (!SMTPReply.isPositiveCompletion(reply)) {
/* 117 */       client.disconnect();
/* 118 */       AxisFault fault = new AxisFault("SMTP", "( SMTP server refused connection )", null, null);
/* 119 */       throw fault;
/*     */     }
/*     */ 
/* 122 */     String fromAddress = msgContext.getStrProp("transport.mail.from");
/* 123 */     String toAddress = msgContext.getStrProp("transport.mail.to");
/*     */ 
/* 125 */     MimeMessage msg = new MimeMessage(this.session);
/* 126 */     msg.setFrom(new InternetAddress(fromAddress));
/* 127 */     msg.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(toAddress));
/*     */ 
/* 130 */     String action = msgContext.useSOAPAction() ? msgContext.getSOAPActionURI() : "";
/*     */ 
/* 134 */     if (action == null) {
/* 135 */       action = "";
/*     */     }
/*     */ 
/* 138 */     Message reqMessage = msgContext.getRequestMessage();
/*     */ 
/* 140 */     msg.addHeader("User-Agent", Messages.getMessage("axisUserAgent"));
/* 141 */     msg.addHeader("SOAPAction", action);
/* 142 */     msg.setDisposition("inline");
/* 143 */     msg.setSubject(id);
/*     */ 
/* 145 */     ByteArrayOutputStream out = new ByteArrayOutputStream(8192);
/* 146 */     reqMessage.writeTo(out);
/* 147 */     msg.setContent(out.toString(), reqMessage.getContentType(msgContext.getSOAPConstants()));
/*     */ 
/* 149 */     ByteArrayOutputStream out2 = new ByteArrayOutputStream(8192);
/* 150 */     msg.writeTo(out2);
/*     */ 
/* 152 */     client.setSender(fromAddress);
/* 153 */     System.out.print(client.getReplyString());
/* 154 */     client.addRecipient(toAddress);
/* 155 */     System.out.print(client.getReplyString());
/*     */ 
/* 157 */     Writer writer = client.sendMessageData();
/* 158 */     System.out.print(client.getReplyString());
/* 159 */     writer.write(out2.toString());
/* 160 */     writer.flush();
/* 161 */     writer.close();
/*     */ 
/* 163 */     System.out.print(client.getReplyString());
/* 164 */     if (!client.completePendingCommand()) {
/* 165 */       System.out.print(client.getReplyString());
/* 166 */       AxisFault fault = new AxisFault("SMTP", "( Failed to send email )", null, null);
/* 167 */       throw fault;
/*     */     }
/* 169 */     System.out.print(client.getReplyString());
/* 170 */     client.logout();
/* 171 */     client.disconnect();
/* 172 */     return id;
/*     */   }
/*     */ 
/*     */   private void readUsingPOP3(String id, MessageContext msgContext)
/*     */     throws Exception
/*     */   {
/* 182 */     String pop3Host = msgContext.getStrProp("transport.mail.pop3.host");
/* 183 */     String pop3User = msgContext.getStrProp("transport.mail.pop3.userid");
/* 184 */     String pop3passwd = msgContext.getStrProp("transport.mail.pop3.password");
/*     */ 
/* 187 */     POP3MessageInfo[] messages = null;
/*     */ 
/* 189 */     MimeMessage mimeMsg = null;
/* 190 */     POP3Client pop3 = new POP3Client();
/*     */ 
/* 192 */     pop3.setDefaultTimeout(60000);
/*     */ 
/* 194 */     for (int i = 0; i < 12; i++) {
/* 195 */       pop3.connect(pop3Host);
/*     */ 
/* 197 */       if (!pop3.login(pop3User, pop3passwd)) {
/* 198 */         pop3.disconnect();
/* 199 */         AxisFault fault = new AxisFault("POP3", "( Could not login to server.  Check password. )", null, null);
/* 200 */         throw fault;
/*     */       }
/*     */ 
/* 203 */       messages = pop3.listMessages();
/* 204 */       if ((messages != null) && (messages.length > 0)) {
/* 205 */         StringBuffer buffer = null;
/* 206 */         for (int j = 0; j < messages.length; j++) {
/* 207 */           Reader reader = pop3.retrieveMessage(messages[j].number);
/* 208 */           if (reader == null) {
/* 209 */             AxisFault fault = new AxisFault("POP3", "( Could not retrieve message header. )", null, null);
/* 210 */             throw fault;
/*     */           }
/*     */ 
/* 213 */           buffer = new StringBuffer();
/* 214 */           BufferedReader bufferedReader = new BufferedReader(reader);
/*     */           int ch;
/* 216 */           while ((ch = bufferedReader.read()) != -1) {
/* 217 */             buffer.append((char)ch);
/*     */           }
/* 219 */           bufferedReader.close();
/* 220 */           if (buffer.toString().indexOf(id) != -1) {
/* 221 */             ByteArrayInputStream bais = new ByteArrayInputStream(buffer.toString().getBytes());
/* 222 */             Properties prop = new Properties();
/* 223 */             Session session = Session.getDefaultInstance(prop, null);
/*     */ 
/* 225 */             mimeMsg = new MimeMessage(session, bais);
/* 226 */             pop3.deleteMessage(messages[j].number);
/* 227 */             break;
/*     */           }
/* 229 */           buffer = null;
/*     */         }
/*     */       }
/* 232 */       pop3.logout();
/* 233 */       pop3.disconnect();
/* 234 */       if (mimeMsg != null) break;
/* 235 */       Thread.sleep(5000L);
/*     */     }
/*     */ 
/* 241 */     if (mimeMsg == null) {
/* 242 */       pop3.logout();
/* 243 */       pop3.disconnect();
/* 244 */       AxisFault fault = new AxisFault("POP3", "( Could not retrieve message list. )", null, null);
/* 245 */       throw fault;
/*     */     }
/*     */ 
/* 248 */     String contentType = mimeMsg.getContentType();
/* 249 */     String contentLocation = mimeMsg.getContentID();
/* 250 */     Message outMsg = new Message(mimeMsg.getInputStream(), false, contentType, contentLocation);
/*     */ 
/* 253 */     outMsg.setMessageType("response");
/* 254 */     msgContext.setResponseMessage(outMsg);
/* 255 */     if (log.isDebugEnabled()) {
/* 256 */       log.debug("\n" + Messages.getMessage("xmlRecd00"));
/* 257 */       log.debug("-----------------------------------------------");
/* 258 */       log.debug(outMsg.getSOAPPartAsString());
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.mail.MailSender
 * JD-Core Version:    0.6.0
 */