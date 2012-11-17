/*     */ package org.apache.axis.attachments;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.StringTokenizer;
/*     */ import javax.activation.DataHandler;
/*     */ import javax.mail.Header;
/*     */ import javax.mail.MessagingException;
/*     */ import javax.mail.internet.InternetHeaders;
/*     */ import javax.mail.internet.MimeUtility;
/*     */ import javax.xml.soap.SOAPException;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public final class MultipartAttachmentStreams extends IncomingAttachmentStreams
/*     */ {
/*  38 */   private BoundaryDelimitedStream _delimitedStream = null;
/*     */ 
/*  40 */   private Iterator _attachmentParts = null;
/*     */ 
/*     */   public MultipartAttachmentStreams(BoundaryDelimitedStream delimitedStream) throws AxisFault
/*     */   {
/*  44 */     this(delimitedStream, null);
/*     */   }
/*     */ 
/*     */   public MultipartAttachmentStreams(BoundaryDelimitedStream delimitedStream, Collection priorParts) throws AxisFault
/*     */   {
/*  49 */     if (delimitedStream == null) {
/*  50 */       throw new AxisFault(Messages.getMessage("nullDelimitedStream"));
/*     */     }
/*  52 */     this._delimitedStream = delimitedStream;
/*  53 */     if (priorParts != null)
/*  54 */       setAttachmentsPriorToSoapPart(priorParts.iterator());
/*     */   }
/*     */ 
/*     */   public void setAttachmentsPriorToSoapPart(Iterator iterator)
/*     */   {
/*  59 */     this._attachmentParts = iterator;
/*     */   }
/*     */ 
/*     */   public IncomingAttachmentStreams.IncomingAttachmentInputStream getNextStream()
/*     */     throws AxisFault
/*     */   {
/*  68 */     if (!isReadyToGetNextStream())
/*  69 */       throw new IllegalStateException(Messages.getMessage("nextStreamNotReady"));
/*     */     IncomingAttachmentStreams.IncomingAttachmentInputStream stream;
/*  72 */     if ((this._attachmentParts != null) && (this._attachmentParts.hasNext())) {
/*  73 */       AttachmentPart part = (AttachmentPart)this._attachmentParts.next();
/*     */       try
/*     */       {
/*  76 */         stream = new IncomingAttachmentStreams.IncomingAttachmentInputStream(this, part.getDataHandler().getInputStream());
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/*     */         IncomingAttachmentStreams.IncomingAttachmentInputStream stream;
/*  79 */         throw new AxisFault(Messages.getMessage("failedToGetAttachmentPartStream"), e);
/*     */       }
/*     */       catch (SOAPException e) {
/*  82 */         throw new AxisFault(Messages.getMessage("failedToGetAttachmentPartStream"), e);
/*     */       }
/*     */       IncomingAttachmentStreams.IncomingAttachmentInputStream stream;
/*  85 */       stream.addHeader("Content-Id", part.getContentId());
/*     */ 
/*  87 */       stream.addHeader("Content-Location", part.getContentLocation());
/*     */ 
/*  89 */       stream.addHeader("Content-Type", part.getContentType());
/*     */     }
/*     */     else
/*     */     {
/*     */       try
/*     */       {
/*  95 */         this._delimitedStream = this._delimitedStream.getNextStream();
/*  96 */         if (this._delimitedStream == null) {
/*  97 */           return null;
/*     */         }
/*  99 */         InternetHeaders headers = new InternetHeaders(this._delimitedStream);
/* 100 */         String delimiter = null;
/* 101 */         String encoding = headers.getHeader("Content-Transfer-Encoding", delimiter);
/*     */ 
/* 104 */         if ((encoding != null) && (encoding.length() > 0)) {
/* 105 */           encoding = encoding.trim();
/* 106 */           IncomingAttachmentStreams.IncomingAttachmentInputStream stream = new IncomingAttachmentStreams.IncomingAttachmentInputStream(this, MimeUtility.decode(this._delimitedStream, encoding));
/*     */ 
/* 108 */           stream.addHeader("Content-Transfer-Encoding", encoding);
/*     */         }
/*     */         else
/*     */         {
/* 112 */           stream = new IncomingAttachmentStreams.IncomingAttachmentInputStream(this, this._delimitedStream);
/*     */         }
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/*     */         IncomingAttachmentStreams.IncomingAttachmentInputStream stream;
/* 115 */         throw new AxisFault(Messages.getMessage("failedToGetDelimitedAttachmentStream"), e);
/*     */       }
/*     */       catch (MessagingException e) {
/* 118 */         throw new AxisFault(Messages.getMessage("failedToGetDelimitedAttachmentStream"), e);
/*     */       }
/*     */       InternetHeaders headers;
/* 124 */       Enumeration e = headers.getAllHeaders();
/* 125 */       while ((e != null) && (e.hasMoreElements())) {
/* 126 */         Header header = (Header)e.nextElement();
/* 127 */         String name = header.getName();
/* 128 */         String value = header.getValue();
/* 129 */         if (("Content-Id".equals(name)) || ("Content-Type".equals(name)) || ("Content-Location".equals(name)))
/*     */         {
/* 132 */           value = value.trim();
/* 133 */           if ((("Content-Id".equals(name)) || ("Content-Location".equals(name))) && ((name.indexOf('>') > 0) || (name.indexOf('<') > 0)))
/*     */           {
/* 136 */             value = new StringTokenizer(value, "<>").nextToken();
/*     */           }
/*     */         }
/* 139 */         stream.addHeader(name, value);
/*     */       }
/*     */     }
/* 142 */     setReadyToGetNextStream(false);
/* 143 */     return stream;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.attachments.MultipartAttachmentStreams
 * JD-Core Version:    0.6.0
 */