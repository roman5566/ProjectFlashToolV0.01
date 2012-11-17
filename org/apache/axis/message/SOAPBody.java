/*     */ package org.apache.axis.message;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Vector;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.soap.Name;
/*     */ import javax.xml.soap.SOAPElement;
/*     */ import javax.xml.soap.SOAPException;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.encoding.DeserializationContext;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.xml.sax.Attributes;
/*     */ 
/*     */ public class SOAPBody extends MessageElement
/*     */   implements javax.xml.soap.SOAPBody
/*     */ {
/*  49 */   private static Log log = LogFactory.getLog(SOAPBody.class.getName());
/*     */   private SOAPConstants soapConstants;
/*  53 */   private boolean disableFormatting = false;
/*  54 */   private boolean doSAAJEncodingCompliance = false;
/*  55 */   private static ArrayList knownEncodingStyles = new ArrayList();
/*     */ 
/*     */   SOAPBody(SOAPEnvelope env, SOAPConstants soapConsts)
/*     */   {
/*  65 */     super(soapConsts.getEnvelopeURI(), "Body");
/*  66 */     this.soapConstants = soapConsts;
/*     */     try {
/*  68 */       setParentElement(env);
/*     */     }
/*     */     catch (SOAPException ex) {
/*  71 */       log.fatal(Messages.getMessage("exception00"), ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public SOAPBody(String namespace, String localPart, String prefix, Attributes attributes, DeserializationContext context, SOAPConstants soapConsts)
/*     */     throws AxisFault
/*     */   {
/*  78 */     super(namespace, localPart, prefix, attributes, context);
/*  79 */     this.soapConstants = soapConsts;
/*     */   }
/*     */ 
/*     */   public void setParentElement(SOAPElement parent) throws SOAPException {
/*  83 */     if (parent == null)
/*  84 */       throw new IllegalArgumentException(Messages.getMessage("nullParent00"));
/*     */     try
/*     */     {
/*  87 */       SOAPEnvelope env = (SOAPEnvelope)parent;
/*  88 */       super.setParentElement(env);
/*  89 */       setEnvelope(env);
/*     */     } catch (Throwable t) {
/*  91 */       throw new SOAPException(t);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void disableFormatting() {
/*  96 */     this.disableFormatting = true;
/*     */   }
/*     */ 
/*     */   public void setEncodingStyle(String encodingStyle) throws SOAPException {
/* 100 */     if (encodingStyle == null) {
/* 101 */       encodingStyle = "";
/*     */     }
/*     */ 
/* 104 */     if (this.doSAAJEncodingCompliance)
/*     */     {
/* 106 */       if (!knownEncodingStyles.contains(encodingStyle)) {
/* 107 */         throw new IllegalArgumentException(Messages.getMessage("badEncodingStyle1", encodingStyle));
/*     */       }
/*     */     }
/* 110 */     super.setEncodingStyle(encodingStyle);
/*     */   }
/*     */ 
/*     */   protected void outputImpl(SerializationContext context) throws Exception {
/* 114 */     boolean oldPretty = context.getPretty();
/* 115 */     if (!this.disableFormatting)
/* 116 */       context.setPretty(true);
/*     */     else {
/* 118 */       context.setPretty(false);
/*     */     }
/*     */ 
/* 121 */     List bodyElements = getChildren();
/*     */ 
/* 123 */     if ((bodyElements != null) && (bodyElements.isEmpty()));
/* 131 */     context.startElement(new QName(this.soapConstants.getEnvelopeURI(), "Body"), getAttributesEx());
/*     */ 
/* 135 */     if (bodyElements != null) {
/* 136 */       Iterator e = bodyElements.iterator();
/* 137 */       while (e.hasNext()) {
/* 138 */         MessageElement body = (MessageElement)e.next();
/* 139 */         body.output(context);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 145 */     context.outputMultiRefs();
/*     */ 
/* 148 */     context.endElement();
/*     */ 
/* 150 */     context.setPretty(oldPretty);
/*     */   }
/*     */ 
/*     */   Vector getBodyElements() throws AxisFault {
/* 154 */     initializeChildren();
/* 155 */     return new Vector(getChildren());
/*     */   }
/*     */ 
/*     */   SOAPBodyElement getFirstBody() throws AxisFault
/*     */   {
/* 160 */     if (!hasChildNodes())
/* 161 */       return null;
/* 162 */     return (SOAPBodyElement)getChildren().get(0);
/*     */   }
/*     */ 
/*     */   void addBodyElement(SOAPBodyElement element)
/*     */   {
/* 167 */     if (log.isDebugEnabled())
/* 168 */       log.debug(Messages.getMessage("addBody00"));
/*     */     try {
/* 170 */       addChildElement(element);
/*     */     }
/*     */     catch (SOAPException ex) {
/* 173 */       log.fatal(Messages.getMessage("exception00"), ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   void removeBodyElement(SOAPBodyElement element)
/*     */   {
/* 179 */     if (log.isDebugEnabled())
/* 180 */       log.debug(Messages.getMessage("removeBody00"));
/* 181 */     removeChild(element);
/*     */   }
/*     */ 
/*     */   void clearBody()
/*     */   {
/* 186 */     removeContents();
/*     */   }
/*     */ 
/*     */   SOAPBodyElement getBodyByName(String namespace, String localPart)
/*     */     throws AxisFault
/*     */   {
/* 192 */     QName name = new QName(namespace, localPart);
/* 193 */     return (SOAPBodyElement)getChildElement(name);
/*     */   }
/*     */ 
/*     */   public javax.xml.soap.SOAPBodyElement addBodyElement(Name name)
/*     */     throws SOAPException
/*     */   {
/* 200 */     SOAPBodyElement bodyElement = new SOAPBodyElement(name);
/* 201 */     addChildElement(bodyElement);
/* 202 */     return bodyElement;
/*     */   }
/*     */ 
/*     */   public javax.xml.soap.SOAPFault addFault(Name name, String s, Locale locale) throws SOAPException {
/* 206 */     AxisFault af = new AxisFault(new QName(name.getURI(), name.getLocalName()), s, "", new Element[0]);
/* 207 */     SOAPFault fault = new SOAPFault(af);
/* 208 */     addChildElement(fault);
/* 209 */     return fault;
/*     */   }
/*     */ 
/*     */   public javax.xml.soap.SOAPFault addFault(Name name, String s) throws SOAPException {
/* 213 */     AxisFault af = new AxisFault(new QName(name.getURI(), name.getLocalName()), s, "", new Element[0]);
/* 214 */     SOAPFault fault = new SOAPFault(af);
/* 215 */     addChildElement(fault);
/* 216 */     return fault;
/*     */   }
/*     */ 
/*     */   public javax.xml.soap.SOAPBodyElement addDocument(Document document) throws SOAPException {
/* 220 */     SOAPBodyElement bodyElement = new SOAPBodyElement(document.getDocumentElement());
/* 221 */     addChildElement(bodyElement);
/* 222 */     return bodyElement;
/*     */   }
/*     */ 
/*     */   public javax.xml.soap.SOAPFault addFault() throws SOAPException
/*     */   {
/* 227 */     AxisFault af = new AxisFault(new QName("http://xml.apache.org/axis/", "Server.generalException"), "", "", new Element[0]);
/* 228 */     SOAPFault fault = new SOAPFault(af);
/* 229 */     addChildElement(fault);
/* 230 */     return fault;
/*     */   }
/*     */ 
/*     */   public javax.xml.soap.SOAPFault getFault() {
/* 234 */     List bodyElements = getChildren();
/* 235 */     if (bodyElements != null) {
/* 236 */       Iterator e = bodyElements.iterator();
/* 237 */       while (e.hasNext()) {
/* 238 */         Object element = e.next();
/* 239 */         if ((element instanceof javax.xml.soap.SOAPFault)) {
/* 240 */           return (javax.xml.soap.SOAPFault)element;
/*     */         }
/*     */       }
/*     */     }
/* 244 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean hasFault() {
/* 248 */     return getFault() != null;
/*     */   }
/*     */ 
/*     */   public void addChild(MessageElement element)
/*     */     throws SOAPException
/*     */   {
/* 257 */     element.setEnvelope(getEnvelope());
/* 258 */     super.addChild(element);
/*     */   }
/*     */ 
/*     */   public SOAPElement addChildElement(SOAPElement element)
/*     */     throws SOAPException
/*     */   {
/* 268 */     SOAPElement child = super.addChildElement(element);
/* 269 */     setDirty();
/* 270 */     return child;
/*     */   }
/*     */ 
/*     */   public SOAPElement addChildElement(Name name) throws SOAPException {
/* 274 */     SOAPBodyElement child = new SOAPBodyElement(name);
/* 275 */     addChildElement(child);
/* 276 */     return child;
/*     */   }
/*     */ 
/*     */   public SOAPElement addChildElement(String localName) throws SOAPException
/*     */   {
/* 281 */     SOAPBodyElement child = new SOAPBodyElement(getNamespaceURI(), localName);
/*     */ 
/* 283 */     addChildElement(child);
/* 284 */     return child;
/*     */   }
/*     */ 
/*     */   public SOAPElement addChildElement(String localName, String prefix) throws SOAPException
/*     */   {
/* 289 */     SOAPBodyElement child = new SOAPBodyElement(getNamespaceURI(prefix), localName);
/*     */ 
/* 291 */     child.setPrefix(prefix);
/* 292 */     addChildElement(child);
/* 293 */     return child;
/*     */   }
/*     */ 
/*     */   public SOAPElement addChildElement(String localName, String prefix, String uri)
/*     */     throws SOAPException
/*     */   {
/* 299 */     SOAPBodyElement child = new SOAPBodyElement(uri, localName);
/* 300 */     child.setPrefix(prefix);
/* 301 */     child.addNamespaceDeclaration(prefix, uri);
/* 302 */     addChildElement(child);
/* 303 */     return child;
/*     */   }
/*     */ 
/*     */   public void setSAAJEncodingCompliance(boolean comply) {
/* 307 */     this.doSAAJEncodingCompliance = true;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  58 */     knownEncodingStyles.add("http://schemas.xmlsoap.org/soap/encoding/");
/*  59 */     knownEncodingStyles.add("http://www.w3.org/2003/05/soap-encoding");
/*  60 */     knownEncodingStyles.add("");
/*  61 */     knownEncodingStyles.add("http://www.w3.org/2003/05/soap-envelope/encoding/none");
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.SOAPBody
 * JD-Core Version:    0.6.0
 */