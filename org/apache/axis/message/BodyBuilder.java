/*     */ package org.apache.axis.message;
/*     */ 
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.Message;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.constants.Style;
/*     */ import org.apache.axis.description.OperationDesc;
/*     */ import org.apache.axis.encoding.DeserializationContext;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class BodyBuilder extends SOAPHandler
/*     */ {
/*  42 */   protected static Log log = LogFactory.getLog(BodyBuilder.class.getName());
/*     */ 
/*  45 */   boolean gotRPCElement = false;
/*     */   private SOAPEnvelope envelope;
/*     */ 
/*     */   BodyBuilder(SOAPEnvelope envelope)
/*     */   {
/*  51 */     this.envelope = envelope;
/*     */   }
/*     */ 
/*     */   public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/*  59 */     SOAPConstants soapConstants = context.getSOAPConstants();
/*     */ 
/*  61 */     if ((soapConstants == SOAPConstants.SOAP12_CONSTANTS) && (attributes.getValue("http://www.w3.org/2003/05/soap-envelope", "encodingStyle") != null))
/*     */     {
/*  64 */       AxisFault fault = new AxisFault(Constants.FAULT_SOAP12_SENDER, null, Messages.getMessage("noEncodingStyleAttrAppear", "Body"), null, null, null);
/*     */ 
/*  67 */       throw new SAXException(fault);
/*     */     }
/*     */ 
/*  71 */     if (!context.isDoneParsing()) {
/*  72 */       if (!context.isProcessingRef()) {
/*  73 */         if (this.myElement == null) {
/*     */           try {
/*  75 */             this.myElement = new SOAPBody(namespace, localName, prefix, attributes, context, this.envelope.getSOAPConstants());
/*     */           }
/*     */           catch (AxisFault axisFault) {
/*  78 */             throw new SAXException(axisFault);
/*     */           }
/*     */         }
/*  81 */         context.pushNewElement(this.myElement);
/*     */       }
/*  83 */       this.envelope.setBody((SOAPBody)this.myElement);
/*     */     }
/*     */   }
/*     */ 
/*     */   public MessageElement makeNewElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws AxisFault
/*     */   {
/*  92 */     return new SOAPBody(namespace, localName, prefix, attributes, context, context.getSOAPConstants());
/*     */   }
/*     */ 
/*     */   public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 107 */     SOAPBodyElement element = null;
/* 108 */     if (log.isDebugEnabled()) {
/* 109 */       log.debug("Enter: BodyBuilder::onStartChild()");
/*     */     }
/*     */ 
/* 112 */     QName qname = new QName(namespace, localName);
/* 113 */     SOAPHandler handler = null;
/*     */ 
/* 123 */     boolean isRoot = true;
/* 124 */     String root = attributes.getValue(Constants.URI_DEFAULT_SOAP_ENC, "root");
/*     */ 
/* 126 */     if ((root != null) && (root.equals("0"))) isRoot = false;
/*     */ 
/* 128 */     MessageContext msgContext = context.getMessageContext();
/* 129 */     OperationDesc[] operations = null;
/*     */     try {
/* 131 */       if (msgContext != null) {
/* 132 */         operations = msgContext.getPossibleOperationsByQName(qname);
/*     */       }
/*     */ 
/* 136 */       if ((operations != null) && (operations.length == 1))
/* 137 */         msgContext.setOperation(operations[0]);
/*     */     }
/*     */     catch (AxisFault e)
/*     */     {
/* 141 */       throw new SAXException(e);
/*     */     }
/*     */ 
/* 144 */     Style style = operations == null ? Style.RPC : operations[0].getStyle();
/* 145 */     SOAPConstants soapConstants = context.getSOAPConstants();
/*     */ 
/* 151 */     if ((localName.equals("Fault")) && (namespace.equals(soapConstants.getEnvelopeURI())))
/*     */     {
/*     */       try {
/* 154 */         element = new SOAPFault(namespace, localName, prefix, attributes, context);
/*     */       }
/*     */       catch (AxisFault axisFault) {
/* 157 */         throw new SAXException(axisFault);
/*     */       }
/* 159 */       element.setEnvelope(context.getEnvelope());
/* 160 */       handler = new SOAPFaultBuilder((SOAPFault)element, context);
/*     */     }
/* 162 */     else if ((!this.gotRPCElement) && 
/* 163 */       (isRoot) && (style != Style.MESSAGE)) {
/* 164 */       this.gotRPCElement = true;
/*     */       try
/*     */       {
/* 168 */         element = new RPCElement(namespace, localName, prefix, attributes, context, operations);
/*     */       }
/*     */       catch (AxisFault e)
/*     */       {
/* 175 */         throw new SAXException(e);
/*     */       }
/*     */ 
/* 182 */       if ((msgContext != null) && (!msgContext.isHighFidelity()) && ((operations == null) || (operations.length == 1)))
/*     */       {
/* 184 */         ((RPCElement)element).setNeedDeser(false);
/* 185 */         boolean isResponse = false;
/* 186 */         if ((msgContext.getCurrentMessage() != null) && ("response".equals(msgContext.getCurrentMessage().getMessageType())))
/*     */         {
/* 188 */           isResponse = true;
/* 189 */         }handler = new RPCHandler((RPCElement)element, isResponse);
/*     */ 
/* 191 */         if (operations != null) {
/* 192 */           ((RPCHandler)handler).setOperation(operations[0]);
/* 193 */           msgContext.setOperation(operations[0]);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 199 */     if (element == null) {
/* 200 */       if ((style == Style.RPC) && (soapConstants == SOAPConstants.SOAP12_CONSTANTS))
/*     */       {
/* 202 */         throw new SAXException(Messages.getMessage("onlyOneBodyFor12"));
/*     */       }
/*     */       try {
/* 205 */         element = new SOAPBodyElement(namespace, localName, prefix, attributes, context);
/*     */       }
/*     */       catch (AxisFault axisFault) {
/* 208 */         throw new SAXException(axisFault);
/*     */       }
/* 210 */       if (element.getFixupDeserializer() != null) {
/* 211 */         handler = (SOAPHandler)element.getFixupDeserializer();
/*     */       }
/*     */     }
/* 214 */     if (handler == null) {
/* 215 */       handler = new SOAPHandler();
/*     */     }
/* 217 */     handler.myElement = element;
/*     */ 
/* 221 */     if (log.isDebugEnabled()) {
/* 222 */       log.debug("Exit: BodyBuilder::onStartChild()");
/*     */     }
/* 224 */     return handler;
/*     */   }
/*     */ 
/*     */   public void onEndChild(String namespace, String localName, DeserializationContext context)
/*     */   {
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.BodyBuilder
 * JD-Core Version:    0.6.0
 */