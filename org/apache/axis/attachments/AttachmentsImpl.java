/*     */ package org.apache.axis.attachments;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import javax.activation.DataHandler;
/*     */ import javax.activation.DataSource;
/*     */ import javax.mail.internet.MimeMultipart;
/*     */ import javax.xml.soap.MimeHeaders;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Part;
/*     */ import org.apache.axis.SOAPPart;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class AttachmentsImpl
/*     */   implements Attachments
/*     */ {
/*  39 */   protected static Log log = LogFactory.getLog(AttachmentsImpl.class.getName());
/*     */ 
/*  43 */   private HashMap attachments = new HashMap();
/*     */ 
/*  46 */   private LinkedList orderedAttachments = new LinkedList();
/*     */ 
/*  49 */   protected SOAPPart soapPart = null;
/*     */ 
/*  54 */   protected MultiPartInputStream mpartStream = null;
/*     */ 
/*  59 */   protected int sendtype = 1;
/*     */ 
/*  65 */   protected String contentLocation = null;
/*     */ 
/*  70 */   private HashMap stackDataHandler = new HashMap();
/*     */ 
/*  75 */   private IncomingAttachmentStreams _streams = null;
/*     */ 
/*  77 */   private boolean _askedForAttachments = false;
/*  78 */   private boolean _askedForStreams = false;
/*     */ 
/* 417 */   MimeMultipart multipart = null;
/* 418 */   DimeMultiPart dimemultipart = null;
/*     */ 
/*     */   public AttachmentsImpl(Object intialContents, String contentType, String contentLocation)
/*     */     throws AxisFault
/*     */   {
/*  96 */     if (contentLocation != null) {
/*  97 */       contentLocation = contentLocation.trim();
/*     */ 
/*  99 */       if (contentLocation.length() == 0) {
/* 100 */         contentLocation = null;
/*     */       }
/*     */     }
/*     */ 
/* 104 */     this.contentLocation = contentLocation;
/*     */ 
/* 106 */     if ((contentType != null) && 
/* 107 */       (!contentType.equals("  ")))
/*     */     {
/* 110 */       StringTokenizer st = new StringTokenizer(contentType, " \t;");
/*     */ 
/* 113 */       if (st.hasMoreTokens()) {
/* 114 */         String token = st.nextToken();
/*     */ 
/* 116 */         if (token.equalsIgnoreCase("multipart/related"))
/*     */         {
/* 118 */           this.sendtype = 2;
/* 119 */           this.mpartStream = new MultiPartRelatedInputStream(contentType, (InputStream)intialContents);
/*     */ 
/* 124 */           if (null == contentLocation)
/*     */           {
/* 128 */             contentLocation = this.mpartStream.getContentLocation();
/*     */ 
/* 130 */             if (contentLocation != null) {
/* 131 */               contentLocation = contentLocation.trim();
/*     */ 
/* 133 */               if (contentLocation.length() == 0) {
/* 134 */                 contentLocation = null;
/*     */               }
/*     */             }
/*     */           }
/*     */ 
/* 139 */           this.soapPart = new SOAPPart(null, this.mpartStream, false);
/*     */ 
/* 142 */           MultiPartRelatedInputStream specificType = (MultiPartRelatedInputStream)this.mpartStream;
/* 143 */           this._streams = new MultipartAttachmentStreams(specificType.boundaryDelimitedStream, specificType.orderedParts);
/* 144 */         } else if (token.equalsIgnoreCase("application/dime")) {
/*     */           try {
/* 146 */             this.mpartStream = new MultiPartDimeInputStream((InputStream)intialContents);
/*     */ 
/* 148 */             this.soapPart = new SOAPPart(null, this.mpartStream, false); } catch (Exception e) {
/* 149 */             throw AxisFault.makeFault(e);
/* 150 */           }this.sendtype = 3;
/* 151 */           MultiPartDimeInputStream specificType = (MultiPartDimeInputStream)this.mpartStream;
/* 152 */           this._streams = new DimeAttachmentStreams(specificType.dimeDelimitedStream);
/* 153 */         } else if (token.indexOf("application/xop+xml") != -1) {
/* 154 */           this.sendtype = 4;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void mergeinAttachments()
/*     */     throws AxisFault
/*     */   {
/* 170 */     if (this.mpartStream != null) {
/* 171 */       Collection atts = this.mpartStream.getAttachments();
/*     */ 
/* 173 */       if (this.contentLocation == null) {
/* 174 */         this.contentLocation = this.mpartStream.getContentLocation();
/*     */       }
/* 176 */       this.mpartStream = null;
/*     */ 
/* 178 */       setAttachmentParts(atts);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Part removeAttachmentPart(String reference)
/*     */     throws AxisFault
/*     */   {
/* 194 */     if (this._askedForStreams) {
/* 195 */       throw new IllegalStateException(Messages.getMessage("concurrentModificationOfStream"));
/*     */     }
/*     */ 
/* 198 */     this.multipart = null;
/*     */ 
/* 200 */     this.dimemultipart = null;
/*     */ 
/* 202 */     mergeinAttachments();
/*     */ 
/* 204 */     Part removedPart = getAttachmentByReference(reference);
/*     */ 
/* 206 */     if (removedPart != null) {
/* 207 */       this.attachments.remove(removedPart.getContentId());
/* 208 */       this.attachments.remove(removedPart.getContentLocation());
/* 209 */       this.orderedAttachments.remove(removedPart);
/*     */     }
/*     */ 
/* 212 */     return removedPart;
/*     */   }
/*     */ 
/*     */   public Part addAttachmentPart(Part newPart)
/*     */     throws AxisFault
/*     */   {
/* 225 */     if (this._askedForStreams) {
/* 226 */       throw new IllegalStateException(Messages.getMessage("concurrentModificationOfStream"));
/*     */     }
/*     */ 
/* 229 */     this.multipart = null;
/* 230 */     this.dimemultipart = null;
/*     */ 
/* 232 */     mergeinAttachments();
/*     */ 
/* 234 */     Part oldPart = (Part)this.attachments.put(newPart.getContentId(), newPart);
/*     */ 
/* 236 */     if (oldPart != null) {
/* 237 */       this.orderedAttachments.remove(oldPart);
/* 238 */       this.attachments.remove(oldPart.getContentLocation());
/*     */     }
/*     */ 
/* 241 */     this.orderedAttachments.add(newPart);
/*     */ 
/* 243 */     if (newPart.getContentLocation() != null) {
/* 244 */       this.attachments.put(newPart.getContentLocation(), newPart);
/*     */     }
/*     */ 
/* 247 */     return oldPart;
/*     */   }
/*     */ 
/*     */   public Part createAttachmentPart(Object datahandler)
/*     */     throws AxisFault
/*     */   {
/* 254 */     Integer key = new Integer(datahandler.hashCode());
/* 255 */     if (this.stackDataHandler.containsKey(key)) {
/* 256 */       return (Part)this.stackDataHandler.get(key);
/*     */     }
/*     */ 
/* 259 */     this.multipart = null;
/*     */ 
/* 261 */     this.dimemultipart = null;
/*     */ 
/* 263 */     mergeinAttachments();
/*     */ 
/* 265 */     if (!(datahandler instanceof DataHandler)) {
/* 266 */       throw new AxisFault(Messages.getMessage("unsupportedAttach", datahandler.getClass().getName(), DataHandler.class.getName()));
/*     */     }
/*     */ 
/* 272 */     Part ret = new AttachmentPart((DataHandler)datahandler);
/*     */ 
/* 275 */     addAttachmentPart(ret);
/*     */ 
/* 278 */     this.stackDataHandler.put(key, ret);
/*     */ 
/* 280 */     return ret;
/*     */   }
/*     */ 
/*     */   public void setAttachmentParts(Collection parts)
/*     */     throws AxisFault
/*     */   {
/* 292 */     if (this._askedForStreams) {
/* 293 */       throw new IllegalStateException(Messages.getMessage("concurrentModificationOfStream"));
/*     */     }
/*     */ 
/* 296 */     removeAllAttachments();
/*     */     Iterator i;
/* 298 */     if ((parts != null) && (!parts.isEmpty()))
/* 299 */       for (i = parts.iterator(); i.hasNext(); ) {
/* 300 */         Object part = i.next();
/*     */ 
/* 302 */         if (null != part)
/* 303 */           if ((part instanceof Part))
/* 304 */             addAttachmentPart((Part)part);
/*     */           else
/* 306 */             createAttachmentPart(part);
/*     */       }
/*     */   }
/*     */ 
/*     */   public Part getAttachmentByReference(String reference)
/*     */     throws AxisFault
/*     */   {
/* 327 */     if (this._askedForStreams) {
/* 328 */       throw new IllegalStateException(Messages.getMessage("concurrentModificationOfStream"));
/*     */     }
/*     */ 
/* 331 */     if (null == reference) {
/* 332 */       return null;
/*     */     }
/*     */ 
/* 335 */     reference = reference.trim();
/*     */ 
/* 337 */     if (0 == reference.length()) {
/* 338 */       return null;
/*     */     }
/*     */ 
/* 341 */     mergeinAttachments();
/*     */ 
/* 346 */     Part ret = (Part)this.attachments.get(reference);
/* 347 */     if (null != ret) return ret;
/*     */ 
/* 350 */     if ((!reference.startsWith("cid:")) && (null != this.contentLocation))
/*     */     {
/* 353 */       String fqreference = this.contentLocation;
/*     */ 
/* 355 */       if (!fqreference.endsWith("/")) {
/* 356 */         fqreference = fqreference + "/";
/*     */       }
/*     */ 
/* 359 */       if (reference.startsWith("/"))
/* 360 */         fqreference = fqreference + reference.substring(1);
/*     */       else {
/* 362 */         fqreference = fqreference + reference;
/*     */       }
/*     */ 
/* 366 */       ret = (AttachmentPart)this.attachments.get(fqreference);
/*     */     }
/*     */ 
/* 369 */     if ((null == ret) && (reference.startsWith("cid:")))
/*     */     {
/* 371 */       ret = (Part)this.attachments.get(reference.substring(4));
/*     */     }
/*     */ 
/* 374 */     return ret;
/*     */   }
/*     */ 
/*     */   public Collection getAttachments()
/*     */     throws AxisFault
/*     */   {
/* 386 */     if (this._askedForStreams) {
/* 387 */       throw new IllegalStateException(Messages.getMessage("concurrentModificationOfStream"));
/*     */     }
/*     */ 
/* 390 */     mergeinAttachments();
/*     */ 
/* 392 */     return new LinkedList(this.orderedAttachments);
/*     */   }
/*     */ 
/*     */   public Part getRootPart()
/*     */   {
/* 402 */     return this.soapPart;
/*     */   }
/*     */ 
/*     */   public void setRootPart(Part newRoot)
/*     */   {
/*     */     try {
/* 408 */       this.soapPart = ((SOAPPart)newRoot);
/* 409 */       this.multipart = null;
/* 410 */       this.dimemultipart = null;
/*     */     } catch (ClassCastException e) {
/* 412 */       throw new ClassCastException(Messages.getMessage("onlySOAPParts"));
/*     */     }
/*     */   }
/*     */ 
/*     */   public long getContentLength()
/*     */     throws AxisFault
/*     */   {
/* 428 */     if (this._askedForStreams) {
/* 429 */       throw new IllegalStateException(Messages.getMessage("concurrentModificationOfStream"));
/*     */     }
/*     */ 
/* 432 */     mergeinAttachments();
/*     */ 
/* 434 */     int sendtype = this.sendtype == 1 ? 2 : this.sendtype;
/*     */     try
/*     */     {
/* 437 */       if ((sendtype == 2) || (sendtype == 4)) {
/* 438 */         return MimeUtils.getContentLength(this.multipart = MimeUtils.createMP(this.soapPart.getAsString(), this.orderedAttachments, getSendType()));
/*     */       }
/* 440 */       if (sendtype == 3) return createDimeMessage().getTransmissionSize(); 
/*     */     }
/*     */     catch (Exception e) {
/* 442 */       throw AxisFault.makeFault(e);
/*     */     }
/* 444 */     return 0L;
/*     */   }
/*     */ 
/*     */   protected DimeMultiPart createDimeMessage()
/*     */     throws AxisFault
/*     */   {
/* 455 */     int sendtype = this.sendtype == 1 ? 2 : this.sendtype;
/* 456 */     if ((sendtype == 3) && 
/* 457 */       (this.dimemultipart == null))
/*     */     {
/* 459 */       this.dimemultipart = new DimeMultiPart();
/* 460 */       this.dimemultipart.addBodyPart(new DimeBodyPart(this.soapPart.getAsBytes(), DimeTypeNameFormat.URI, "http://schemas.xmlsoap.org/soap/envelope/", "uuid:714C6C40-4531-442E-A498-3AC614200295"));
/*     */ 
/* 465 */       Iterator i = this.orderedAttachments.iterator();
/* 466 */       while (i.hasNext()) {
/* 467 */         AttachmentPart part = (AttachmentPart)i.next();
/* 468 */         DataHandler dh = AttachmentUtils.getActivationDataHandler(part);
/*     */ 
/* 470 */         this.dimemultipart.addBodyPart(new DimeBodyPart(dh, part.getContentId()));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 475 */     return this.dimemultipart;
/*     */   }
/*     */ 
/*     */   public void writeContentToStream(OutputStream os)
/*     */     throws AxisFault
/*     */   {
/* 487 */     int sendtype = this.sendtype == 1 ? 2 : this.sendtype;
/*     */     try
/*     */     {
/* 491 */       mergeinAttachments();
/* 492 */       if ((sendtype == 2) || (sendtype == 4)) {
/* 493 */         MimeUtils.writeToMultiPartStream(os, this.multipart = MimeUtils.createMP(this.soapPart.getAsString(), this.orderedAttachments, getSendType()));
/*     */ 
/* 500 */         Iterator i = this.orderedAttachments.iterator();
/* 501 */         while (i.hasNext()) {
/* 502 */           AttachmentPart part = (AttachmentPart)i.next();
/* 503 */           DataHandler dh = AttachmentUtils.getActivationDataHandler(part);
/*     */ 
/* 505 */           DataSource ds = dh.getDataSource();
/*     */ 
/* 507 */           if ((ds != null) && ((ds instanceof ManagedMemoryDataSource)))
/* 508 */             ((ManagedMemoryDataSource)ds).delete();
/*     */         }
/*     */       }
/* 511 */       else if (sendtype == 3) { createDimeMessage().write(os); } 
/*     */     } catch (Exception e) {
/* 512 */       throw AxisFault.makeFault(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getContentType()
/*     */     throws AxisFault
/*     */   {
/* 524 */     mergeinAttachments();
/*     */ 
/* 526 */     int sendtype = this.sendtype == 1 ? 2 : this.sendtype;
/*     */ 
/* 528 */     if ((sendtype == 2) || (sendtype == 4)) {
/* 529 */       return MimeUtils.getContentType(this.multipart = MimeUtils.createMP(this.soapPart.getAsString(), this.orderedAttachments, getSendType()));
/*     */     }
/*     */ 
/* 536 */     return "application/dime";
/*     */   }
/*     */ 
/*     */   public int getAttachmentCount()
/*     */   {
/* 545 */     if (this._askedForStreams) {
/* 546 */       throw new IllegalStateException(Messages.getMessage("concurrentModificationOfStream"));
/*     */     }
/*     */     try
/*     */     {
/* 550 */       mergeinAttachments();
/*     */ 
/* 554 */       this.soapPart.saveChanges();
/*     */ 
/* 556 */       return this.orderedAttachments.size();
/*     */     } catch (AxisFault e) {
/* 558 */       log.warn(Messages.getMessage("exception00"), e);
/*     */     }
/*     */ 
/* 561 */     return 0;
/*     */   }
/*     */ 
/*     */   public boolean isAttachment(Object value)
/*     */   {
/* 573 */     return AttachmentUtils.isAttachment(value);
/*     */   }
/*     */ 
/*     */   public void removeAllAttachments()
/*     */   {
/* 583 */     if (this._askedForStreams)
/* 584 */       throw new IllegalStateException(Messages.getMessage("concurrentModificationOfStream"));
/*     */     try
/*     */     {
/* 587 */       this.multipart = null;
/* 588 */       this.dimemultipart = null;
/* 589 */       mergeinAttachments();
/* 590 */       this.attachments.clear();
/* 591 */       this.orderedAttachments.clear();
/* 592 */       this.stackDataHandler.clear();
/*     */     } catch (AxisFault af) {
/* 594 */       log.warn(Messages.getMessage("exception00"), af);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Iterator getAttachments(MimeHeaders headers)
/*     */   {
/* 611 */     if (this._askedForStreams) {
/* 612 */       throw new IllegalStateException(Messages.getMessage("concurrentModificationOfStream"));
/*     */     }
/* 614 */     Vector vecParts = new Vector();
/* 615 */     Iterator iterator = GetAttachmentsIterator();
/* 616 */     while (iterator.hasNext()) {
/* 617 */       Part part = (Part)iterator.next();
/* 618 */       if (((part instanceof AttachmentPart)) && 
/* 619 */         (((AttachmentPart)part).matches(headers))) {
/* 620 */         vecParts.add(part);
/*     */       }
/*     */     }
/*     */ 
/* 624 */     return vecParts.iterator();
/*     */   }
/*     */ 
/*     */   private Iterator GetAttachmentsIterator()
/*     */   {
/* 635 */     Iterator iterator = this.attachments.values().iterator();
/* 636 */     return iterator;
/*     */   }
/*     */ 
/*     */   public Part createAttachmentPart()
/*     */     throws AxisFault
/*     */   {
/* 648 */     return new AttachmentPart();
/*     */   }
/*     */ 
/*     */   public void setSendType(int sendtype) {
/* 652 */     if (sendtype < 1)
/* 653 */       throw new IllegalArgumentException("");
/* 654 */     if (sendtype > 5)
/* 655 */       throw new IllegalArgumentException("");
/* 656 */     this.sendtype = sendtype;
/*     */   }
/*     */ 
/*     */   public int getSendType() {
/* 660 */     return this.sendtype;
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */   {
/* 669 */     Iterator iterator = GetAttachmentsIterator();
/* 670 */     while (iterator.hasNext()) {
/* 671 */       Part part = (Part)iterator.next();
/* 672 */       if ((part instanceof AttachmentPart)) {
/* 673 */         AttachmentPart apart = (AttachmentPart)part;
/* 674 */         apart.dispose();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static int getSendType(String value)
/*     */   {
/* 690 */     if (value.equalsIgnoreCase("MTOM")) return 4;
/* 691 */     if (value.equalsIgnoreCase("MIME")) return 2;
/* 692 */     if (value.equalsIgnoreCase("DIME")) return 3;
/* 693 */     if (value.equalsIgnoreCase("NONE")) return 5;
/* 694 */     return 1;
/*     */   }
/*     */ 
/*     */   public static String getSendTypeString(int value)
/*     */   {
/* 704 */     if (value == 4) {
/* 705 */       return "MTOM";
/*     */     }
/* 707 */     if (value == 2) {
/* 708 */       return "MIME";
/*     */     }
/* 710 */     if (value == 3) {
/* 711 */       return "DIME";
/*     */     }
/* 713 */     if (value == 5) {
/* 714 */       return "NONE";
/*     */     }
/* 716 */     return null;
/*     */   }
/*     */ 
/*     */   public IncomingAttachmentStreams getIncomingAttachmentStreams()
/*     */   {
/* 727 */     if (this._askedForAttachments) {
/* 728 */       throw new IllegalStateException(Messages.getMessage("concurrentModificationOfStream"));
/*     */     }
/* 730 */     this._askedForStreams = true;
/* 731 */     this.mpartStream = null;
/* 732 */     return this._streams;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.attachments.AttachmentsImpl
 * JD-Core Version:    0.6.0
 */