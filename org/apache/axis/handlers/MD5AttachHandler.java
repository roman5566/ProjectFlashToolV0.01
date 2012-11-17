/*     */ package org.apache.axis.handlers;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.security.MessageDigest;
/*     */ import javax.activation.DataHandler;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Message;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.Part;
/*     */ import org.apache.axis.attachments.AttachmentUtils;
/*     */ import org.apache.axis.attachments.Attachments;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.encoding.Base64;
/*     */ import org.apache.axis.message.SOAPBodyElement;
/*     */ import org.apache.axis.message.SOAPEnvelope;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.Text;
/*     */ 
/*     */ public class MD5AttachHandler extends BasicHandler
/*     */ {
/*  35 */   protected static Log log = LogFactory.getLog(MD5AttachHandler.class.getName());
/*     */ 
/*     */   public void invoke(MessageContext msgContext) throws AxisFault
/*     */   {
/*  39 */     log.debug("Enter: MD5AttachHandler::invoke");
/*     */     try
/*     */     {
/*  42 */       Message msg = msgContext.getRequestMessage();
/*  43 */       SOAPConstants soapConstants = msgContext.getSOAPConstants();
/*  44 */       SOAPEnvelope env = msg.getSOAPEnvelope();
/*  45 */       SOAPBodyElement sbe = env.getFirstBody();
/*  46 */       Element sbElement = sbe.getAsDOM();
/*     */ 
/*  48 */       Node n = sbElement.getFirstChild();
/*     */ 
/*  50 */       while ((n != null) && (!(n instanceof Element))) n = n.getNextSibling();
/*  51 */       Element paramElement = (Element)n;
/*     */ 
/*  53 */       String href = paramElement.getAttribute(soapConstants.getAttrHref());
/*  54 */       Part ap = msg.getAttachmentsImpl().getAttachmentByReference(href);
/*  55 */       DataHandler dh = AttachmentUtils.getActivationDataHandler(ap);
/*  56 */       Node timeNode = paramElement.getFirstChild();
/*  57 */       long startTime = -1L;
/*     */ 
/*  59 */       if ((timeNode != null) && ((timeNode instanceof Text))) {
/*  60 */         String startTimeStr = ((Text)timeNode).getData();
/*     */ 
/*  62 */         startTime = Long.parseLong(startTimeStr);
/*     */       }
/*     */ 
/*  66 */       long receivedTime = System.currentTimeMillis();
/*  67 */       long elapsedTime = -1L;
/*     */ 
/*  71 */       if (startTime > 0L) elapsedTime = receivedTime - startTime;
/*  72 */       String elapsedTimeStr = elapsedTime + "";
/*     */ 
/*  75 */       MessageDigest md = MessageDigest.getInstance("MD5");
/*  76 */       InputStream attachmentStream = dh.getInputStream();
/*  77 */       int bread = 0;
/*  78 */       byte[] buf = new byte[65536];
/*     */       do
/*     */       {
/*  81 */         bread = attachmentStream.read(buf);
/*  82 */         if (bread > 0) {
/*  83 */           md.update(buf, 0, bread);
/*     */         }
/*     */       }
/*  86 */       while (bread > -1);
/*  87 */       attachmentStream.close();
/*  88 */       buf = null;
/*     */ 
/*  90 */       String contentType = dh.getContentType();
/*     */ 
/*  92 */       if ((contentType != null) && (contentType.length() != 0)) {
/*  93 */         md.update(contentType.getBytes("US-ASCII"));
/*     */       }
/*     */ 
/*  96 */       sbe = env.getFirstBody();
/*  97 */       sbElement = sbe.getAsDOM();
/*     */ 
/*  99 */       n = sbElement.getFirstChild();
/* 100 */       while ((n != null) && (!(n instanceof Element))) n = n.getNextSibling();
/* 101 */       paramElement = (Element)n;
/*     */ 
/* 103 */       String MD5String = Base64.encode(md.digest());
/* 104 */       String senddata = " elapsedTime=" + elapsedTimeStr + " MD5=" + MD5String;
/*     */ 
/* 107 */       paramElement.appendChild(paramElement.getOwnerDocument().createTextNode(senddata));
/*     */ 
/* 109 */       sbe = new SOAPBodyElement(sbElement);
/* 110 */       env.clearBody();
/* 111 */       env.addBodyElement(sbe);
/* 112 */       msg = new Message(env);
/*     */ 
/* 114 */       msgContext.setResponseMessage(msg);
/*     */     }
/*     */     catch (Exception e) {
/* 117 */       log.error(Messages.getMessage("exception00"), e);
/* 118 */       throw AxisFault.makeFault(e);
/*     */     }
/*     */ 
/* 121 */     log.debug("Exit: MD5AttachHandler::invoke");
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.handlers.MD5AttachHandler
 * JD-Core Version:    0.6.0
 */