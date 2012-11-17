/*     */ package org.apache.axis.encoding.ser;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import javax.activation.DataHandler;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.Message;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.Part;
/*     */ import org.apache.axis.attachments.Attachments;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.encoding.Serializer;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.fromJava.Types;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Element;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.helpers.AttributesImpl;
/*     */ 
/*     */ public class JAFDataHandlerSerializer
/*     */   implements Serializer
/*     */ {
/*  45 */   protected static Log log = LogFactory.getLog(JAFDataHandlerSerializer.class.getName());
/*     */ 
/*     */   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context)
/*     */     throws IOException
/*     */   {
/*  55 */     DataHandler dh = (DataHandler)value;
/*     */ 
/*  57 */     Attachments attachments = context.getCurrentMessage().getAttachmentsImpl();
/*     */ 
/*  59 */     if (attachments == null)
/*     */     {
/*  63 */       throw new IOException(Messages.getMessage("noAttachments"));
/*     */     }
/*  65 */     SOAPConstants soapConstants = context.getMessageContext().getSOAPConstants();
/*  66 */     Part attachmentPart = attachments.createAttachmentPart(dh);
/*     */ 
/*  68 */     AttributesImpl attrs = new AttributesImpl();
/*  69 */     if ((attributes != null) && (0 < attributes.getLength())) {
/*  70 */       attrs.setAttributes(attributes);
/*     */     }
/*  72 */     int typeIndex = -1;
/*  73 */     if ((typeIndex = attrs.getIndex("http://www.w3.org/2001/XMLSchema-instance", "type")) != -1)
/*     */     {
/*  77 */       attrs.removeAttribute(typeIndex);
/*     */     }
/*     */ 
/*  80 */     if (attachments.getSendType() == 4) {
/*  81 */       context.setWriteXMLType(null);
/*  82 */       context.startElement(name, attrs);
/*  83 */       AttributesImpl attrs2 = new AttributesImpl();
/*  84 */       attrs2.addAttribute("", soapConstants.getAttrHref(), soapConstants.getAttrHref(), "CDATA", attachmentPart.getContentIdRef());
/*     */ 
/*  86 */       context.startElement(new QName("http://www.w3.org/2004/08/xop/include", "Include"), attrs2);
/*  87 */       context.endElement();
/*  88 */       context.endElement();
/*     */     } else {
/*  90 */       boolean doTheDIME = false;
/*  91 */       if (attachments.getSendType() == 3) {
/*  92 */         doTheDIME = true;
/*     */       }
/*  94 */       attrs.addAttribute("", soapConstants.getAttrHref(), soapConstants.getAttrHref(), "CDATA", doTheDIME ? attachmentPart.getContentId() : attachmentPart.getContentIdRef());
/*     */ 
/*  97 */       context.startElement(name, attrs);
/*  98 */       context.endElement();
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getMechanismType() {
/* 102 */     return "Axis SAX Mechanism";
/*     */   }
/*     */ 
/*     */   public Element writeSchema(Class javaType, Types types)
/*     */     throws Exception
/*     */   {
/* 116 */     return null;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.JAFDataHandlerSerializer
 * JD-Core Version:    0.6.0
 */