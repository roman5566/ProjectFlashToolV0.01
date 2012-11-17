/*     */ package org.apache.axis.message;
/*     */ 
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.soap.Name;
/*     */ import javax.xml.soap.SOAPElement;
/*     */ import javax.xml.soap.SOAPException;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.encoding.DeserializationContext;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.soap.SOAP12Constants;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.xml.sax.Attributes;
/*     */ 
/*     */ public class SOAPHeaderElement extends MessageElement
/*     */   implements javax.xml.soap.SOAPHeaderElement
/*     */ {
/*  44 */   protected boolean processed = false;
/*     */ 
/*  46 */   protected String actor = "http://schemas.xmlsoap.org/soap/actor/next";
/*  47 */   protected boolean mustUnderstand = false;
/*  48 */   protected boolean relay = false;
/*     */ 
/* 220 */   boolean alreadySerialized = false;
/*     */ 
/*     */   public SOAPHeaderElement(String namespace, String localPart)
/*     */   {
/*  52 */     super(namespace, localPart);
/*     */   }
/*     */ 
/*     */   public SOAPHeaderElement(Name name)
/*     */   {
/*  57 */     super(name);
/*     */   }
/*     */ 
/*     */   public SOAPHeaderElement(QName qname)
/*     */   {
/*  62 */     super(qname);
/*     */   }
/*     */ 
/*     */   public SOAPHeaderElement(String namespace, String localPart, Object value)
/*     */   {
/*  68 */     super(namespace, localPart, value);
/*     */   }
/*     */ 
/*     */   public SOAPHeaderElement(QName qname, Object value)
/*     */   {
/*  73 */     super(qname, value);
/*     */   }
/*     */ 
/*     */   public SOAPHeaderElement(Element elem)
/*     */   {
/*  78 */     super(elem);
/*     */ 
/*  82 */     SOAPConstants soapConstants = getSOAPConstants();
/*     */ 
/*  84 */     String val = elem.getAttributeNS(soapConstants.getEnvelopeURI(), "mustUnderstand");
/*     */     try
/*     */     {
/*  88 */       setMustUnderstandFromString(val, soapConstants == SOAPConstants.SOAP12_CONSTANTS);
/*     */     }
/*     */     catch (AxisFault axisFault)
/*     */     {
/*  92 */       log.error(axisFault);
/*     */     }
/*     */ 
/*  95 */     QName roleQName = soapConstants.getRoleAttributeQName();
/*  96 */     this.actor = elem.getAttributeNS(roleQName.getNamespaceURI(), roleQName.getLocalPart());
/*     */ 
/* 102 */     if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
/* 103 */       String relayVal = elem.getAttributeNS(soapConstants.getEnvelopeURI(), "relay");
/*     */ 
/* 105 */       this.relay = ((relayVal != null) && ((relayVal.equals("true")) || (relayVal.equals("1"))));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setParentElement(SOAPElement parent) throws SOAPException
/*     */   {
/* 111 */     if (parent == null) {
/* 112 */       throw new IllegalArgumentException(Messages.getMessage("nullParent00"));
/*     */     }
/*     */ 
/* 115 */     if ((parent instanceof SOAPEnvelope)) {
/* 116 */       log.warn(Messages.getMessage("bodyHeaderParent"));
/* 117 */       parent = ((SOAPEnvelope)parent).getHeader();
/*     */     }
/* 119 */     if (!(parent instanceof SOAPHeader)) {
/* 120 */       throw new IllegalArgumentException(Messages.getMessage("illegalArgumentException00"));
/*     */     }
/*     */ 
/* 123 */     super.setParentElement(parent);
/*     */   }
/*     */ 
/*     */   public SOAPHeaderElement(String namespace, String localPart, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws AxisFault
/*     */   {
/* 133 */     super(namespace, localPart, prefix, attributes, context);
/*     */ 
/* 135 */     SOAPConstants soapConstants = getSOAPConstants();
/*     */ 
/* 138 */     String val = attributes.getValue(soapConstants.getEnvelopeURI(), "mustUnderstand");
/*     */ 
/* 141 */     setMustUnderstandFromString(val, soapConstants == SOAPConstants.SOAP12_CONSTANTS);
/*     */ 
/* 144 */     QName roleQName = soapConstants.getRoleAttributeQName();
/* 145 */     this.actor = attributes.getValue(roleQName.getNamespaceURI(), roleQName.getLocalPart());
/*     */ 
/* 151 */     if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
/* 152 */       String relayVal = attributes.getValue(soapConstants.getEnvelopeURI(), "relay");
/*     */ 
/* 154 */       this.relay = ((relayVal != null) && ((relayVal.equals("true")) || (relayVal.equals("1"))));
/*     */     }
/*     */ 
/* 157 */     this.processed = false;
/* 158 */     this.alreadySerialized = true;
/*     */   }
/*     */ 
/*     */   private void setMustUnderstandFromString(String val, boolean isSOAP12) throws AxisFault
/*     */   {
/* 163 */     if ((val != null) && (val.length() > 0))
/* 164 */       if ("0".equals(val))
/* 165 */         this.mustUnderstand = false;
/* 166 */       else if ("1".equals(val))
/* 167 */         this.mustUnderstand = true;
/* 168 */       else if (isSOAP12) {
/* 169 */         if ("true".equalsIgnoreCase(val))
/* 170 */           this.mustUnderstand = true;
/* 171 */         else if ("false".equalsIgnoreCase(val))
/* 172 */           this.mustUnderstand = false;
/*     */         else {
/* 174 */           throw new AxisFault(Messages.getMessage("badMUVal", val, new QName(this.namespaceURI, this.name).toString()));
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 181 */         throw new AxisFault(Messages.getMessage("badMUVal", val, new QName(this.namespaceURI, this.name).toString()));
/*     */       }
/*     */   }
/*     */ 
/*     */   public boolean getMustUnderstand()
/*     */   {
/* 190 */     return this.mustUnderstand;
/*     */   }
/* 192 */   public void setMustUnderstand(boolean b) { this.mustUnderstand = b; }
/*     */ 
/*     */   public String getActor() {
/* 195 */     return this.actor;
/*     */   }
/* 197 */   public void setActor(String a) { this.actor = a; }
/*     */ 
/*     */   public String getRole() {
/* 200 */     return this.actor;
/*     */   }
/* 202 */   public void setRole(String a) { this.actor = a; }
/*     */ 
/*     */   public boolean getRelay()
/*     */   {
/* 206 */     return this.relay;
/*     */   }
/*     */   public void setRelay(boolean relay) {
/* 209 */     this.relay = relay;
/*     */   }
/*     */ 
/*     */   public void setProcessed(boolean value) {
/* 213 */     this.processed = value;
/*     */   }
/*     */ 
/*     */   public boolean isProcessed() {
/* 217 */     return this.processed;
/*     */   }
/*     */ 
/*     */   protected void outputImpl(SerializationContext context)
/*     */     throws Exception
/*     */   {
/* 225 */     if (!this.alreadySerialized) {
/* 226 */       SOAPConstants soapVer = getSOAPConstants();
/* 227 */       QName roleQName = soapVer.getRoleAttributeQName();
/*     */ 
/* 229 */       if (this.actor != null)
/* 230 */         setAttribute(roleQName.getNamespaceURI(), roleQName.getLocalPart(), this.actor);
/*     */       String val;
/*     */       String val;
/* 235 */       if ((context.getMessageContext() != null) && (context.getMessageContext().getSOAPConstants() == SOAPConstants.SOAP12_CONSTANTS))
/* 236 */         val = this.mustUnderstand ? "true" : "false";
/*     */       else {
/* 238 */         val = this.mustUnderstand ? "1" : "0";
/*     */       }
/* 240 */       setAttribute(soapVer.getEnvelopeURI(), "mustUnderstand", val);
/*     */ 
/* 244 */       if ((soapVer == SOAPConstants.SOAP12_CONSTANTS) && (this.relay)) {
/* 245 */         setAttribute(soapVer.getEnvelopeURI(), "relay", "true");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 250 */     super.outputImpl(context);
/*     */   }
/*     */ 
/*     */   public NamedNodeMap getAttributes() {
/* 254 */     makeAttributesEditable();
/* 255 */     SOAPConstants soapConstants = getSOAPConstants();
/* 256 */     String mustUnderstand = this.attributes.getValue(soapConstants.getEnvelopeURI(), "mustUnderstand");
/*     */ 
/* 258 */     QName roleQName = soapConstants.getRoleAttributeQName();
/* 259 */     String actor = this.attributes.getValue(roleQName.getNamespaceURI(), roleQName.getLocalPart());
/*     */ 
/* 261 */     if (mustUnderstand == null) {
/* 262 */       if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
/* 263 */         setAttributeNS(soapConstants.getEnvelopeURI(), "mustUnderstand", "false");
/*     */       }
/*     */       else {
/* 266 */         setAttributeNS(soapConstants.getEnvelopeURI(), "mustUnderstand", "0");
/*     */       }
/*     */     }
/*     */ 
/* 270 */     if (actor == null) {
/* 271 */       setAttributeNS(roleQName.getNamespaceURI(), roleQName.getLocalPart(), this.actor);
/*     */     }
/*     */ 
/* 274 */     return super.getAttributes();
/*     */   }
/*     */ 
/*     */   private SOAPConstants getSOAPConstants() {
/* 278 */     SOAPConstants soapConstants = null;
/* 279 */     if (this.context != null) {
/* 280 */       return this.context.getSOAPConstants();
/*     */     }
/* 282 */     if ((getNamespaceURI() != null) && (getNamespaceURI().equals(SOAPConstants.SOAP12_CONSTANTS.getEnvelopeURI())))
/*     */     {
/* 284 */       soapConstants = SOAPConstants.SOAP12_CONSTANTS;
/*     */     }
/* 286 */     if ((soapConstants == null) && (getEnvelope() != null)) {
/* 287 */       soapConstants = getEnvelope().getSOAPConstants();
/*     */     }
/* 289 */     if (soapConstants == null) {
/* 290 */       soapConstants = SOAPConstants.SOAP11_CONSTANTS;
/*     */     }
/* 292 */     return soapConstants;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.SOAPHeaderElement
 * JD-Core Version:    0.6.0
 */