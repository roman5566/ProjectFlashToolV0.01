/*     */ package org.apache.axis.attachments;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.Collection;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.Properties;
/*     */ import javax.activation.DataHandler;
/*     */ import javax.activation.DataSource;
/*     */ import javax.activation.FileDataSource;
/*     */ import javax.mail.Header;
/*     */ import javax.mail.MessagingException;
/*     */ import javax.mail.Multipart;
/*     */ import javax.mail.Session;
/*     */ import javax.mail.internet.ContentType;
/*     */ import javax.mail.internet.InternetHeaders;
/*     */ import javax.mail.internet.MimeBodyPart;
/*     */ import javax.mail.internet.MimeMessage;
/*     */ import javax.mail.internet.MimeMultipart;
/*     */ import javax.xml.soap.MimeHeader;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.AxisProperties;
/*     */ import org.apache.axis.Part;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.SessionUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class MimeUtils
/*     */ {
/*  39 */   protected static Log log = LogFactory.getLog(MimeUtils.class.getName());
/*     */ 
/* 172 */   public static String[] filter = { "Message-ID", "Mime-Version", "Content-Type" };
/*     */ 
/*     */   public static long getContentLength(Multipart mp)
/*     */     throws MessagingException, IOException
/*     */   {
/*  53 */     int totalParts = mp.getCount();
/*  54 */     long totalContentLength = 0L;
/*     */ 
/*  56 */     for (int i = 0; i < totalParts; i++) {
/*  57 */       MimeBodyPart bp = (MimeBodyPart)mp.getBodyPart(i);
/*     */ 
/*  60 */       totalContentLength += getContentLength(bp);
/*     */     }
/*     */ 
/*  63 */     String ctype = mp.getContentType();
/*  64 */     ContentType ct = new ContentType(ctype);
/*     */ 
/*  66 */     String boundaryStr = ct.getParameter("boundary");
/*     */ 
/*  68 */     int boundaryStrLen = boundaryStr.length() + 4;
/*     */ 
/*  75 */     return totalContentLength + boundaryStrLen * (totalParts + 1) + 2 * totalParts + 4L;
/*     */   }
/*     */ 
/*     */   protected static long getContentLength(MimeBodyPart bp)
/*     */   {
/*  87 */     long headerLength = -1L;
/*  88 */     long dataSize = -1L;
/*     */     try
/*     */     {
/*  91 */       headerLength = getHeaderLength(bp);
/*     */ 
/*  93 */       DataHandler dh = bp.getDataHandler();
/*  94 */       DataSource ds = dh.getDataSource();
/*     */ 
/*  98 */       if ((ds instanceof FileDataSource)) {
/*  99 */         FileDataSource fdh = (FileDataSource)ds;
/*     */ 
/* 101 */         File df = fdh.getFile();
/*     */ 
/* 103 */         if (!df.exists()) {
/* 104 */           throw new RuntimeException(Messages.getMessage("noFile", df.getAbsolutePath()));
/*     */         }
/*     */ 
/* 108 */         dataSize = df.length();
/*     */       } else {
/* 110 */         dataSize = bp.getSize();
/*     */ 
/* 112 */         if (-1L == dataSize) { dataSize = 0L;
/*     */ 
/* 115 */           InputStream in = ds.getInputStream();
/* 116 */           byte[] readbuf = new byte[65536];
/*     */           int bytesread;
/*     */           do { bytesread = in.read(readbuf);
/*     */ 
/* 122 */             if (bytesread > 0)
/* 123 */               dataSize += bytesread;
/*     */           }
/* 125 */           while (bytesread > -1);
/*     */ 
/* 127 */           in.close(); }
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 131 */       log.error(Messages.getMessage("exception00"), e);
/*     */     }
/*     */ 
/* 134 */     return dataSize + headerLength;
/*     */   }
/*     */ 
/*     */   private static long getHeaderLength(MimeBodyPart bp)
/*     */     throws MessagingException, IOException
/*     */   {
/* 148 */     MimeBodyPart headersOnly = new MimeBodyPart(new InternetHeaders(), new byte[0]);
/*     */ 
/* 152 */     Enumeration en = bp.getAllHeaders();
/* 153 */     while (en.hasMoreElements()) {
/* 154 */       Header header = (Header)en.nextElement();
/*     */ 
/* 156 */       headersOnly.addHeader(header.getName(), header.getValue());
/*     */     }
/*     */ 
/* 159 */     ByteArrayOutputStream bas = new ByteArrayOutputStream(16384);
/*     */ 
/* 162 */     headersOnly.writeTo(bas);
/* 163 */     bas.close();
/*     */ 
/* 165 */     return bas.size();
/*     */   }
/*     */ 
/*     */   public static void writeToMultiPartStream(OutputStream os, MimeMultipart mp)
/*     */   {
/*     */     try
/*     */     {
/* 189 */       Properties props = AxisProperties.getProperties();
/*     */ 
/* 191 */       props.setProperty("mail.smtp.host", "localhost");
/*     */ 
/* 195 */       Session session = Session.getInstance(props, null);
/*     */ 
/* 197 */       MimeMessage message = new MimeMessage(session);
/*     */ 
/* 200 */       message.setContent(mp);
/* 201 */       message.saveChanges();
/* 202 */       message.writeTo(os, filter);
/*     */     } catch (MessagingException e) {
/* 204 */       log.error(Messages.getMessage("javaxMailMessagingException00"), e);
/*     */     } catch (IOException e) {
/* 206 */       log.error(Messages.getMessage("javaIOException00"), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getContentType(MimeMultipart mp)
/*     */   {
/* 217 */     StringBuffer contentType = new StringBuffer(mp.getContentType());
/*     */ 
/* 220 */     for (int i = 0; i < contentType.length(); ) {
/* 221 */       char ch = contentType.charAt(i);
/* 222 */       if ((ch == '\r') || (ch == '\n'))
/* 223 */         contentType.deleteCharAt(i);
/*     */       else
/* 225 */         i++;
/*     */     }
/* 227 */     return contentType.toString();
/*     */   }
/*     */ 
/*     */   public static MimeMultipart createMP(String env, Collection parts, int sendType)
/*     */     throws AxisFault
/*     */   {
/* 245 */     MimeMultipart multipart = null;
/*     */     try
/*     */     {
/* 248 */       String rootCID = SessionUtils.generateSessionId();
/*     */ 
/* 250 */       if (sendType == 4) {
/* 251 */         multipart = new MimeMultipart("related;type=\"application/xop+xml\"; start=\"<" + rootCID + ">\"; start-info=\"text/xml; charset=utf-8\"");
/*     */       }
/*     */       else {
/* 254 */         multipart = new MimeMultipart("related; type=\"text/xml\"; start=\"<" + rootCID + ">\"");
/*     */       }
/*     */ 
/* 258 */       messageBodyPart = new MimeBodyPart();
/*     */ 
/* 261 */       messageBodyPart.setText(env, "UTF-8");
/* 262 */       if (sendType == 4) {
/* 263 */         messageBodyPart.setHeader("Content-Type", "application/xop+xml; charset=utf-8; type=\"text/xml; charset=utf-8\"");
/*     */       }
/*     */       else {
/* 266 */         messageBodyPart.setHeader("Content-Type", "text/xml; charset=UTF-8");
/*     */       }
/*     */ 
/* 269 */       messageBodyPart.setHeader("Content-Id", "<" + rootCID + ">");
/* 270 */       messageBodyPart.setHeader("Content-Transfer-Encoding", "binary");
/*     */ 
/* 272 */       multipart.addBodyPart(messageBodyPart);
/*     */ 
/* 274 */       for (it = parts.iterator(); it.hasNext(); ) {
/* 275 */         Part part = (Part)it.next();
/*     */ 
/* 277 */         DataHandler dh = AttachmentUtils.getActivationDataHandler(part);
/*     */ 
/* 280 */         String contentID = part.getContentId();
/*     */ 
/* 282 */         messageBodyPart = new MimeBodyPart();
/*     */ 
/* 284 */         messageBodyPart.setDataHandler(dh);
/*     */ 
/* 286 */         String contentType = part.getContentType();
/* 287 */         if ((contentType == null) || (contentType.trim().length() == 0))
/*     */         {
/* 289 */           contentType = dh.getContentType();
/*     */         }
/* 291 */         if ((contentType == null) || (contentType.trim().length() == 0))
/*     */         {
/* 293 */           contentType = "application/octet-stream";
/*     */         }
/*     */ 
/* 296 */         messageBodyPart.setHeader("Content-Type", contentType);
/*     */ 
/* 298 */         messageBodyPart.setHeader("Content-Id", "<" + contentID + ">");
/*     */ 
/* 300 */         messageBodyPart.setHeader("Content-Transfer-Encoding", "binary");
/*     */ 
/* 304 */         Iterator i = part.getNonMatchingMimeHeaders(new String[] { "Content-Type", "Content-Id", "Content-Transfer-Encoding" });
/*     */ 
/* 308 */         while (i.hasNext()) {
/* 309 */           MimeHeader header = (MimeHeader)i.next();
/*     */ 
/* 311 */           messageBodyPart.setHeader(header.getName(), header.getValue());
/*     */         }
/*     */ 
/* 314 */         multipart.addBodyPart(messageBodyPart);
/*     */       }
/*     */     }
/*     */     catch (MessagingException e)
/*     */     {
/*     */       MimeBodyPart messageBodyPart;
/*     */       Iterator it;
/* 317 */       log.error(Messages.getMessage("javaxMailMessagingException00"), e);
/*     */     }
/*     */ 
/* 320 */     return multipart;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.attachments.MimeUtils
 * JD-Core Version:    0.6.0
 */