/*     */ package org.apache.axis;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.xml.soap.AttachmentPart;
/*     */ import javax.xml.soap.SOAPBody;
/*     */ import javax.xml.soap.SOAPException;
/*     */ import javax.xml.soap.SOAPHeader;
/*     */ import javax.xml.soap.SOAPMessage;
/*     */ import org.apache.axis.attachments.Attachments;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.handlers.soap.SOAPService;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.apache.axis.utils.ClassUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class Message extends SOAPMessage
/*     */   implements Serializable
/*     */ {
/*  62 */   protected static Log log = LogFactory.getLog(Message.class.getName());
/*     */   public static final String REQUEST = "request";
/*     */   public static final String RESPONSE = "response";
/*     */   public static final String MIME_MULTIPART_RELATED = "multipart/related";
/*     */   public static final String MIME_APPLICATION_DIME = "application/dime";
/*     */   public static final String CONTENT_TYPE_MTOM = "application/xop+xml";
/*     */   public static final String DEFAULT_ATTACHMNET_IMPL = "org.apache.axis.attachments.AttachmentsImpl";
/*  84 */   private static String mAttachmentsImplClassName = "org.apache.axis.attachments.AttachmentsImpl";
/*     */   public static final String MIME_UNKNOWN = "  ";
/*     */   private String messageType;
/*     */   private SOAPPart mSOAPPart;
/* 107 */   private Attachments mAttachments = null;
/*     */   private org.apache.axis.message.MimeHeaders headers;
/* 111 */   private boolean saveRequired = true;
/*     */   private MessageContext msgContext;
/* 249 */   private static Class attachImpl = null;
/*     */ 
/* 252 */   private static boolean checkForAttachmentSupport = true;
/*     */ 
/* 254 */   private static boolean attachmentSupportEnabled = false;
/*     */ 
/* 552 */   private Hashtable mProps = new Hashtable();
/*     */ 
/*     */   public static String getAttachmentImplClassName()
/*     */   {
/* 119 */     return mAttachmentsImplClassName;
/*     */   }
/*     */ 
/*     */   public String getMessageType()
/*     */   {
/* 130 */     return this.messageType;
/*     */   }
/*     */ 
/*     */   public void setMessageType(String messageType)
/*     */   {
/* 139 */     this.messageType = messageType;
/*     */   }
/*     */ 
/*     */   public MessageContext getMessageContext()
/*     */   {
/* 148 */     return this.msgContext;
/*     */   }
/*     */ 
/*     */   public void setMessageContext(MessageContext msgContext)
/*     */   {
/* 157 */     this.msgContext = msgContext;
/*     */   }
/*     */ 
/*     */   public Message(Object initialContents, boolean bodyInStream)
/*     */   {
/* 175 */     setup(initialContents, bodyInStream, null, null, null);
/*     */   }
/*     */ 
/*     */   public Message(Object initialContents, boolean bodyInStream, javax.xml.soap.MimeHeaders headers)
/*     */   {
/* 194 */     setup(initialContents, bodyInStream, null, null, headers);
/*     */   }
/*     */ 
/*     */   public Message(Object initialContents, org.apache.axis.message.MimeHeaders headers)
/*     */   {
/* 211 */     setup(initialContents, true, null, null, headers);
/*     */   }
/*     */ 
/*     */   public Message(Object initialContents, boolean bodyInStream, String contentType, String contentLocation)
/*     */   {
/* 235 */     setup(initialContents, bodyInStream, contentType, contentLocation, null);
/*     */   }
/*     */ 
/*     */   public Message(Object initialContents)
/*     */   {
/* 246 */     setup(initialContents, false, null, null, null);
/*     */   }
/*     */ 
/*     */   private static synchronized boolean isAttachmentSupportEnabled(MessageContext mc)
/*     */   {
/* 257 */     if (checkForAttachmentSupport)
/*     */     {
/* 259 */       checkForAttachmentSupport = false;
/*     */       try
/*     */       {
/* 262 */         String attachImpName = AxisProperties.getProperty("attachments.implementation", "org.apache.axis.attachments.AttachmentsImpl");
/*     */ 
/* 265 */         if (null != mc) {
/* 266 */           AxisEngine ae = mc.getAxisEngine();
/* 267 */           if (null != ae) {
/* 268 */             attachImpName = (String)ae.getOption("attachments.implementation");
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 276 */         ClassUtils.forName("javax.activation.DataHandler");
/* 277 */         ClassUtils.forName("javax.mail.internet.MimeMultipart");
/*     */ 
/* 279 */         attachImpl = ClassUtils.forName(attachImpName);
/*     */ 
/* 281 */         attachmentSupportEnabled = true;
/*     */       }
/*     */       catch (ClassNotFoundException ex) {
/*     */       }
/*     */       catch (NoClassDefFoundError ex) {
/*     */       }
/*     */       catch (SecurityException ex) {
/*     */       }
/* 289 */       log.debug(Messages.getMessage("attachEnabled") + "  " + attachmentSupportEnabled);
/*     */     }
/*     */ 
/* 292 */     return attachmentSupportEnabled;
/*     */   }
/*     */ 
/*     */   private void setup(Object initialContents, boolean bodyInStream, String contentType, String contentLocation, javax.xml.soap.MimeHeaders mimeHeaders)
/*     */   {
/* 311 */     if ((contentType == null) && (mimeHeaders != null)) {
/* 312 */       String[] contentTypes = mimeHeaders.getHeader("Content-Type");
/* 313 */       contentType = contentTypes != null ? contentTypes[0] : null;
/*     */     }
/* 315 */     if ((contentLocation == null) && (mimeHeaders != null)) {
/* 316 */       String[] contentLocations = mimeHeaders.getHeader("Content-Location");
/* 317 */       contentLocation = contentLocations != null ? contentLocations[0] : null;
/*     */     }
/* 319 */     if (contentType != null) {
/* 320 */       int delimiterIndex = contentType.lastIndexOf("charset");
/* 321 */       if (delimiterIndex > 0) {
/* 322 */         String charsetPart = contentType.substring(delimiterIndex);
/* 323 */         int delimiterIndex2 = charsetPart.indexOf(';');
/* 324 */         if (delimiterIndex2 != -1) {
/* 325 */           charsetPart = charsetPart.substring(0, delimiterIndex2);
/*     */         }
/* 327 */         int charsetIndex = charsetPart.indexOf('=');
/* 328 */         String charset = charsetPart.substring(charsetIndex + 1).trim();
/* 329 */         if ((charset.startsWith("\"")) || (charset.startsWith("'"))) {
/* 330 */           charset = charset.substring(1, charset.length());
/*     */         }
/* 332 */         if ((charset.endsWith("\"")) || (charset.endsWith("'")))
/* 333 */           charset = charset.substring(0, charset.length() - 1);
/*     */         try
/*     */         {
/* 336 */           setProperty("javax.xml.soap.character-set-encoding", charset);
/*     */         }
/*     */         catch (SOAPException e)
/*     */         {
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 346 */     if (isAttachmentSupportEnabled(getMessageContext()))
/*     */     {
/* 350 */       Constructor attachImplConstr = attachImpl.getConstructors()[0];
/*     */       try {
/* 352 */         this.mAttachments = ((Attachments)attachImplConstr.newInstance(new Object[] { initialContents, contentType, contentLocation }));
/*     */ 
/* 357 */         this.mSOAPPart = ((SOAPPart)this.mAttachments.getRootPart());
/*     */       } catch (InvocationTargetException ex) {
/* 359 */         log.fatal(Messages.getMessage("invocationTargetException00"), ex);
/*     */ 
/* 361 */         throw new RuntimeException(ex.getMessage());
/*     */       } catch (InstantiationException ex) {
/* 363 */         log.fatal(Messages.getMessage("instantiationException00"), ex);
/*     */ 
/* 365 */         throw new RuntimeException(ex.getMessage());
/*     */       } catch (IllegalAccessException ex) {
/* 367 */         log.fatal(Messages.getMessage("illegalAccessException00"), ex);
/*     */ 
/* 369 */         throw new RuntimeException(ex.getMessage());
/*     */       }
/* 371 */     } else if ((contentType != null) && (contentType.startsWith("multipart"))) {
/* 372 */       throw new RuntimeException(Messages.getMessage("noAttachments"));
/*     */     }
/*     */ 
/* 376 */     if (null == this.mSOAPPart) {
/* 377 */       this.mSOAPPart = new SOAPPart(this, initialContents, bodyInStream);
/*     */     }
/*     */     else {
/* 380 */       this.mSOAPPart.setMessage(this);
/*     */     }
/*     */ 
/* 383 */     if (this.mAttachments != null) this.mAttachments.setRootPart(this.mSOAPPart);
/*     */ 
/* 385 */     this.headers = (mimeHeaders == null ? new org.apache.axis.message.MimeHeaders() : new org.apache.axis.message.MimeHeaders(mimeHeaders));
/*     */   }
/*     */ 
/*     */   public javax.xml.soap.SOAPPart getSOAPPart()
/*     */   {
/* 399 */     return this.mSOAPPart;
/*     */   }
/*     */ 
/*     */   public String getSOAPPartAsString()
/*     */     throws AxisFault
/*     */   {
/* 412 */     return this.mSOAPPart.getAsString();
/*     */   }
/*     */ 
/*     */   public byte[] getSOAPPartAsBytes()
/*     */     throws AxisFault
/*     */   {
/* 425 */     return this.mSOAPPart.getAsBytes();
/*     */   }
/*     */ 
/*     */   public org.apache.axis.message.SOAPEnvelope getSOAPEnvelope()
/*     */     throws AxisFault
/*     */   {
/* 435 */     return this.mSOAPPart.getAsSOAPEnvelope();
/*     */   }
/*     */ 
/*     */   public Attachments getAttachmentsImpl()
/*     */   {
/* 449 */     return this.mAttachments;
/*     */   }
/*     */ 
/*     */   public String getContentType(SOAPConstants sc)
/*     */     throws AxisFault
/*     */   {
/* 462 */     boolean soap12 = false;
/*     */ 
/* 464 */     if (sc != null) {
/* 465 */       if (sc == SOAPConstants.SOAP12_CONSTANTS)
/* 466 */         soap12 = true;
/*     */     }
/*     */     else
/*     */     {
/* 470 */       org.apache.axis.message.SOAPEnvelope envelope = getSOAPEnvelope();
/* 471 */       if ((envelope != null) && 
/* 472 */         (envelope.getSOAPConstants() == SOAPConstants.SOAP12_CONSTANTS)) {
/* 473 */         soap12 = true;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 478 */     String encoding = XMLUtils.getEncoding(this, this.msgContext);
/* 479 */     String ret = sc.getContentType() + "; charset=" + encoding.toLowerCase();
/*     */ 
/* 482 */     if (soap12) {
/* 483 */       ret = "application/soap+xml; charset=" + encoding;
/*     */     }
/*     */ 
/* 486 */     if ((getSendType() != 5) && (this.mAttachments != null) && (0 != this.mAttachments.getAttachmentCount()))
/*     */     {
/* 488 */       ret = this.mAttachments.getContentType();
/*     */     }
/* 490 */     return ret;
/*     */   }
/*     */ 
/*     */   private int getSendType() {
/* 494 */     int sendType = 1;
/* 495 */     if ((this.msgContext != null) && (this.msgContext.getService() != null)) {
/* 496 */       sendType = this.msgContext.getService().getSendType();
/*     */     }
/* 498 */     return sendType;
/*     */   }
/*     */ 
/*     */   public long getContentLength()
/*     */     throws AxisFault
/*     */   {
/* 510 */     long ret = this.mSOAPPart.getContentLength();
/* 511 */     if ((this.mAttachments != null) && (0 < this.mAttachments.getAttachmentCount())) {
/* 512 */       ret = this.mAttachments.getContentLength();
/*     */     }
/* 514 */     return ret;
/*     */   }
/*     */ 
/*     */   public void writeTo(OutputStream os)
/*     */     throws SOAPException, IOException
/*     */   {
/* 535 */     if ((getSendType() == 5) || (this.mAttachments == null) || (0 == this.mAttachments.getAttachmentCount()))
/*     */       try {
/* 537 */         String charEncoding = XMLUtils.getEncoding(this, this.msgContext);
/* 538 */         this.mSOAPPart.setEncoding(charEncoding);
/* 539 */         this.mSOAPPart.writeTo(os);
/*     */       } catch (IOException e) {
/* 541 */         log.error(Messages.getMessage("javaIOException00"), e);
/*     */       }
/*     */     else
/*     */       try {
/* 545 */         this.mAttachments.writeContentToStream(os);
/*     */       } catch (Exception e) {
/* 547 */         log.error(Messages.getMessage("exception00"), e);
/*     */       }
/*     */   }
/*     */ 
/*     */   public SOAPBody getSOAPBody()
/*     */     throws SOAPException
/*     */   {
/* 555 */     return this.mSOAPPart.getEnvelope().getBody();
/*     */   }
/*     */ 
/*     */   public SOAPHeader getSOAPHeader() throws SOAPException {
/* 559 */     return this.mSOAPPart.getEnvelope().getHeader();
/*     */   }
/*     */ 
/*     */   public void setProperty(String property, Object value) throws SOAPException {
/* 563 */     this.mProps.put(property, value);
/*     */   }
/*     */ 
/*     */   public Object getProperty(String property) throws SOAPException {
/* 567 */     return this.mProps.get(property);
/*     */   }
/*     */ 
/*     */   public String getContentDescription()
/*     */   {
/* 579 */     String[] values = this.headers.getHeader("Content-Description");
/* 580 */     if ((values != null) && (values.length > 0))
/* 581 */       return values[0];
/* 582 */     return null;
/*     */   }
/*     */ 
/*     */   public void setContentDescription(String description)
/*     */   {
/* 593 */     this.headers.setHeader("Content-Description", description);
/*     */   }
/*     */ 
/*     */   public void saveChanges()
/*     */     throws SOAPException
/*     */   {
/* 620 */     this.headers.removeHeader("Content-Length");
/* 621 */     if ((this.mAttachments != null) && (0 < this.mAttachments.getAttachmentCount())) {
/*     */       try {
/* 623 */         this.headers.setHeader("Content-Type", this.mAttachments.getContentType());
/*     */       } catch (AxisFault af) {
/* 625 */         log.error(Messages.getMessage("exception00"), af);
/*     */       }
/*     */     }
/* 628 */     this.saveRequired = false;
/*     */     try
/*     */     {
/* 631 */       this.mSOAPPart.saveChanges();
/*     */     } catch (AxisFault axisFault) {
/* 633 */       log.error(Messages.getMessage("exception00"), axisFault);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean saveRequired()
/*     */   {
/* 646 */     return this.saveRequired;
/*     */   }
/*     */ 
/*     */   public javax.xml.soap.MimeHeaders getMimeHeaders()
/*     */   {
/* 657 */     return this.headers;
/*     */   }
/*     */ 
/*     */   public void removeAllAttachments()
/*     */   {
/* 667 */     this.mAttachments.removeAllAttachments();
/*     */   }
/*     */ 
/*     */   public int countAttachments()
/*     */   {
/* 678 */     return this.mAttachments == null ? 0 : this.mAttachments.getAttachmentCount();
/*     */   }
/*     */ 
/*     */   public Iterator getAttachments()
/*     */   {
/*     */     try
/*     */     {
/* 689 */       if ((this.mAttachments != null) && (0 != this.mAttachments.getAttachmentCount()))
/* 690 */         return this.mAttachments.getAttachments().iterator();
/*     */     }
/*     */     catch (AxisFault af) {
/* 693 */       log.error(Messages.getMessage("exception00"), af);
/*     */     }
/* 695 */     return Collections.EMPTY_LIST.iterator();
/*     */   }
/*     */ 
/*     */   public Iterator getAttachments(javax.xml.soap.MimeHeaders headers)
/*     */   {
/* 710 */     return this.mAttachments.getAttachments(headers);
/*     */   }
/*     */ 
/*     */   public void addAttachmentPart(AttachmentPart attachmentpart)
/*     */   {
/*     */     try
/*     */     {
/* 725 */       this.mAttachments.addAttachmentPart((Part)attachmentpart);
/*     */     } catch (AxisFault af) {
/* 727 */       log.error(Messages.getMessage("exception00"), af);
/*     */     }
/*     */   }
/*     */ 
/*     */   public AttachmentPart createAttachmentPart()
/*     */   {
/* 742 */     if (!isAttachmentSupportEnabled(getMessageContext())) {
/* 743 */       throw new RuntimeException(Messages.getMessage("noAttachments"));
/*     */     }
/*     */     try
/*     */     {
/* 747 */       return (AttachmentPart)this.mAttachments.createAttachmentPart();
/*     */     } catch (AxisFault af) {
/* 749 */       log.error(Messages.getMessage("exception00"), af);
/*     */     }
/* 751 */     return null;
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */   {
/* 758 */     if (this.mAttachments != null)
/* 759 */       this.mAttachments.dispose();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.Message
 * JD-Core Version:    0.6.0
 */