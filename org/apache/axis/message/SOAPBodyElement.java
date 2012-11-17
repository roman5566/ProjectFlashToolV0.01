/*     */ package org.apache.axis.message;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.soap.Name;
/*     */ import javax.xml.soap.SOAPElement;
/*     */ import javax.xml.soap.SOAPException;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.InternalException;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.encoding.DeserializationContext;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.xml.sax.Attributes;
/*     */ 
/*     */ public class SOAPBodyElement extends MessageElement
/*     */   implements javax.xml.soap.SOAPBodyElement
/*     */ {
/*  40 */   private static Log log = LogFactory.getLog(SOAPBodyElement.class.getName());
/*     */ 
/*     */   public SOAPBodyElement(String namespace, String localPart, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws AxisFault
/*     */   {
/*  50 */     super(namespace, localPart, prefix, attributes, context);
/*     */   }
/*     */ 
/*     */   public SOAPBodyElement(Name name)
/*     */   {
/*  55 */     super(name);
/*     */   }
/*     */ 
/*     */   public SOAPBodyElement(QName qname)
/*     */   {
/*  60 */     super(qname);
/*     */   }
/*     */ 
/*     */   public SOAPBodyElement(QName qname, Object value)
/*     */   {
/*  65 */     super(qname, value);
/*     */   }
/*     */ 
/*     */   public SOAPBodyElement(Element elem)
/*     */   {
/*  70 */     super(elem);
/*     */   }
/*     */ 
/*     */   public SOAPBodyElement()
/*     */   {
/*     */   }
/*     */ 
/*     */   public SOAPBodyElement(InputStream input)
/*     */   {
/*  79 */     super(getDocumentElement(input));
/*     */   }
/*     */ 
/*     */   public SOAPBodyElement(String namespace, String localPart)
/*     */   {
/*  84 */     super(namespace, localPart);
/*     */   }
/*     */ 
/*     */   private static Element getDocumentElement(InputStream input)
/*     */   {
/*     */     try {
/*  90 */       return XMLUtils.newDocument(input).getDocumentElement(); } catch (Exception e) {
/*     */     }
/*  92 */     throw new InternalException(e);
/*     */   }
/*     */ 
/*     */   public void setParentElement(SOAPElement parent) throws SOAPException
/*     */   {
/*  97 */     if (parent == null) {
/*  98 */       throw new IllegalArgumentException(Messages.getMessage("nullParent00"));
/*     */     }
/*     */ 
/* 101 */     if ((parent instanceof SOAPEnvelope)) {
/* 102 */       log.warn(Messages.getMessage("bodyElementParent"));
/* 103 */       parent = ((SOAPEnvelope)parent).getBody();
/*     */     }
/* 105 */     if ((!(parent instanceof SOAPBody)) && (!(parent instanceof RPCElement))) {
/* 106 */       throw new IllegalArgumentException(Messages.getMessage("illegalArgumentException00"));
/*     */     }
/*     */ 
/* 109 */     super.setParentElement(parent);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.SOAPBodyElement
 * JD-Core Version:    0.6.0
 */