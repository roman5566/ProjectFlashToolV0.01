/*     */ package org.apache.axis.soap;
/*     */ 
/*     */ import java.net.MalformedURLException;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import javax.xml.soap.MimeHeaders;
/*     */ import javax.xml.soap.SOAPConnection;
/*     */ import javax.xml.soap.SOAPException;
/*     */ import javax.xml.soap.SOAPMessage;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Message;
/*     */ import org.apache.axis.attachments.Attachments;
/*     */ import org.apache.axis.client.Call;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class SOAPConnectionImpl extends SOAPConnection
/*     */ {
/*  34 */   private boolean closed = false;
/*  35 */   private Integer timeout = null;
/*     */ 
/*     */   public Integer getTimeout()
/*     */   {
/*  42 */     return this.timeout;
/*     */   }
/*     */ 
/*     */   public void setTimeout(Integer timeout)
/*     */   {
/*  50 */     this.timeout = timeout;
/*     */   }
/*     */ 
/*     */   public SOAPMessage call(SOAPMessage request, Object endpoint)
/*     */     throws SOAPException
/*     */   {
/*  67 */     if (this.closed)
/*  68 */       throw new SOAPException(Messages.getMessage("connectionClosed00"));
/*     */     try
/*     */     {
/*  71 */       Call call = new Call(endpoint.toString());
/*  72 */       ((Message)request).setMessageContext(call.getMessageContext());
/*  73 */       Attachments attachments = ((Message)request).getAttachmentsImpl();
/*     */ 
/*  75 */       if (attachments != null) {
/*  76 */         Iterator iterator = attachments.getAttachments().iterator();
/*  77 */         while (iterator.hasNext()) {
/*  78 */           Object attachment = iterator.next();
/*  79 */           call.addAttachmentPart(attachment);
/*     */         }
/*     */       }
/*     */ 
/*  83 */       String soapActionURI = checkForSOAPActionHeader(request);
/*  84 */       if (soapActionURI != null) {
/*  85 */         call.setSOAPActionURI(soapActionURI);
/*     */       }
/*  87 */       call.setTimeout(this.timeout);
/*  88 */       call.setReturnClass(SOAPMessage.class);
/*  89 */       call.setProperty("call.CheckMustUnderstand", Boolean.FALSE);
/*  90 */       call.invoke((Message)request);
/*  91 */       return call.getResponseMessage();
/*     */     } catch (MalformedURLException mue) {
/*  93 */       throw new SOAPException(mue); } catch (AxisFault af) {
/*     */     }
/*  95 */     return new Message(af);
/*     */   }
/*     */ 
/*     */   private String checkForSOAPActionHeader(SOAPMessage request)
/*     */   {
/* 107 */     MimeHeaders hdrs = request.getMimeHeaders();
/* 108 */     if (hdrs != null) {
/* 109 */       String[] saHdrs = hdrs.getHeader("SOAPAction");
/* 110 */       if ((saHdrs != null) && (saHdrs.length > 0))
/* 111 */         return saHdrs[0];
/*     */     }
/* 113 */     return null;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws SOAPException
/*     */   {
/* 121 */     if (this.closed) {
/* 122 */       throw new SOAPException(Messages.getMessage("connectionClosed00"));
/*     */     }
/* 124 */     this.closed = true;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.soap.SOAPConnectionImpl
 * JD-Core Version:    0.6.0
 */