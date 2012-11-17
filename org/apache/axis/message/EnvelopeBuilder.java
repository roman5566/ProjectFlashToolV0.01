/*     */ package org.apache.axis.message;
/*     */ 
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.soap.SOAPException;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.encoding.DeserializationContext;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class EnvelopeBuilder extends SOAPHandler
/*     */ {
/*     */   private SOAPEnvelope envelope;
/*  41 */   private SOAPConstants soapConstants = SOAPConstants.SOAP11_CONSTANTS;
/*     */ 
/*  43 */   private boolean gotHeader = false;
/*  44 */   private boolean gotBody = false;
/*     */ 
/*     */   public EnvelopeBuilder(String messageType, SOAPConstants soapConstants)
/*     */   {
/*  48 */     this.envelope = new SOAPEnvelope(false, soapConstants);
/*  49 */     this.envelope.setMessageType(messageType);
/*  50 */     this.myElement = this.envelope;
/*     */   }
/*     */ 
/*     */   public EnvelopeBuilder(SOAPEnvelope env, String messageType)
/*     */   {
/*  55 */     this.envelope = env;
/*  56 */     this.envelope.setMessageType(messageType);
/*  57 */     this.myElement = this.envelope;
/*     */   }
/*     */ 
/*     */   public SOAPEnvelope getEnvelope()
/*     */   {
/*  62 */     return this.envelope;
/*     */   }
/*     */ 
/*     */   public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/*  70 */     if (!localName.equals("Envelope")) {
/*  71 */       throw new SAXException(Messages.getMessage("badTag00", localName));
/*     */     }
/*     */ 
/*  75 */     MessageContext msgContext = context.getMessageContext();
/*  76 */     SOAPConstants singleVersion = null;
/*  77 */     if (msgContext != null) {
/*  78 */       singleVersion = (SOAPConstants)msgContext.getProperty("SingleSOAPVersion");
/*     */     }
/*     */ 
/*  82 */     if (namespace.equals("http://schemas.xmlsoap.org/soap/envelope/"))
/*     */     {
/*  84 */       this.soapConstants = SOAPConstants.SOAP11_CONSTANTS;
/*  85 */     } else if (namespace.equals("http://www.w3.org/2003/05/soap-envelope"))
/*     */     {
/*  87 */       this.soapConstants = SOAPConstants.SOAP12_CONSTANTS;
/*     */     }
/*  89 */     else this.soapConstants = null;
/*     */ 
/*  92 */     if ((this.soapConstants == null) || ((singleVersion != null) && (this.soapConstants != singleVersion)))
/*     */     {
/*  99 */       this.soapConstants = SOAPConstants.SOAP11_CONSTANTS;
/* 100 */       if (singleVersion == null) singleVersion = this.soapConstants;
/*     */       try
/*     */       {
/* 103 */         AxisFault fault = new AxisFault(this.soapConstants.getVerMismatchFaultCodeQName(), null, Messages.getMessage("versionMissmatch00"), null, null, null);
/*     */ 
/* 106 */         SOAPHeaderElement newHeader = new SOAPHeaderElement(this.soapConstants.getEnvelopeURI(), "Upgrade");
/*     */ 
/* 112 */         MessageElement innerHeader = new MessageElement(this.soapConstants.getEnvelopeURI(), "SupportedEnvelope");
/*     */ 
/* 115 */         innerHeader.addAttribute(null, "qname", new QName(singleVersion.getEnvelopeURI(), "Envelope"));
/*     */ 
/* 118 */         newHeader.addChildElement(innerHeader);
/* 119 */         fault.addHeader(newHeader);
/*     */ 
/* 121 */         throw new SAXException(fault);
/*     */       }
/*     */       catch (SOAPException e) {
/* 124 */         throw new SAXException(e);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 130 */     if (context.getMessageContext() != null) {
/* 131 */       context.getMessageContext().setSOAPConstants(this.soapConstants);
/*     */     }
/* 133 */     if ((this.soapConstants == SOAPConstants.SOAP12_CONSTANTS) && (attributes.getValue("http://www.w3.org/2003/05/soap-envelope", "encodingStyle") != null))
/*     */     {
/* 136 */       AxisFault fault = new AxisFault(Constants.FAULT_SOAP12_SENDER, null, Messages.getMessage("noEncodingStyleAttrAppear", "Envelope"), null, null, null);
/*     */ 
/* 139 */       throw new SAXException(fault);
/*     */     }
/*     */ 
/* 142 */     this.envelope.setPrefix(prefix);
/* 143 */     this.envelope.setNamespaceURI(namespace);
/* 144 */     this.envelope.setNSMappings(context.getCurrentNSMappings());
/* 145 */     this.envelope.setSoapConstants(this.soapConstants);
/* 146 */     context.pushNewElement(this.envelope);
/*     */   }
/*     */ 
/*     */   public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 156 */     QName thisQName = new QName(namespace, localName);
/* 157 */     if (thisQName.equals(this.soapConstants.getHeaderQName())) {
/* 158 */       if (this.gotHeader) {
/* 159 */         throw new SAXException(Messages.getMessage("only1Header00"));
/*     */       }
/* 161 */       this.gotHeader = true;
/* 162 */       return new HeaderBuilder(this.envelope);
/*     */     }
/*     */ 
/* 165 */     if (thisQName.equals(this.soapConstants.getBodyQName())) {
/* 166 */       if (this.gotBody) {
/* 167 */         throw new SAXException(Messages.getMessage("only1Body00"));
/*     */       }
/* 169 */       this.gotBody = true;
/* 170 */       return new BodyBuilder(this.envelope);
/*     */     }
/*     */ 
/* 173 */     if (!this.gotBody) {
/* 174 */       throw new SAXException(Messages.getMessage("noCustomElems00"));
/*     */     }
/* 176 */     if (this.soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
/* 177 */       throw new SAXException(Messages.getMessage("noElemAfterBody12"));
/*     */     }
/*     */     try
/*     */     {
/* 181 */       MessageElement element = new MessageElement(namespace, localName, prefix, attributes, context);
/*     */ 
/* 184 */       if (element.getFixupDeserializer() != null)
/* 185 */         return (SOAPHandler)element.getFixupDeserializer();
/*     */     } catch (AxisFault axisFault) {
/* 187 */       throw new SAXException(axisFault);
/*     */     }
/*     */ 
/* 190 */     return null;
/*     */   }
/*     */ 
/*     */   public void onEndChild(String namespace, String localName, DeserializationContext context)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endElement(String namespace, String localName, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 203 */     this.envelope.setDirty(false);
/* 204 */     this.envelope.setRecorded(true);
/* 205 */     this.envelope.reset();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.EnvelopeBuilder
 * JD-Core Version:    0.6.0
 */