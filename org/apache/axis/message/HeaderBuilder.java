/*    */ package org.apache.axis.message;
/*    */ 
/*    */ import org.apache.axis.AxisFault;
/*    */ import org.apache.axis.Constants;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.encoding.DeserializationContext;
/*    */ import org.apache.axis.soap.SOAPConstants;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public class HeaderBuilder extends SOAPHandler
/*    */ {
/* 36 */   protected static Log log = LogFactory.getLog(HeaderBuilder.class.getName());
/*    */   private SOAPHeaderElement header;
/*    */   private SOAPEnvelope envelope;
/*    */ 
/*    */   HeaderBuilder(SOAPEnvelope envelope)
/*    */   {
/* 44 */     this.envelope = envelope;
/*    */   }
/*    */ 
/*    */   public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*    */     throws SAXException
/*    */   {
/* 52 */     SOAPConstants soapConstants = context.getSOAPConstants();
/*    */ 
/* 54 */     if ((soapConstants == SOAPConstants.SOAP12_CONSTANTS) && (attributes.getValue("http://www.w3.org/2003/05/soap-envelope", "encodingStyle") != null))
/*    */     {
/* 57 */       AxisFault fault = new AxisFault(Constants.FAULT_SOAP12_SENDER, null, Messages.getMessage("noEncodingStyleAttrAppear", "Header"), null, null, null);
/*    */ 
/* 60 */       throw new SAXException(fault);
/*    */     }
/*    */ 
/* 63 */     if (!context.isDoneParsing()) {
/* 64 */       if (this.myElement == null) {
/*    */         try {
/* 66 */           this.myElement = new SOAPHeader(namespace, localName, prefix, attributes, context, this.envelope.getSOAPConstants());
/*    */         }
/*    */         catch (AxisFault axisFault)
/*    */         {
/* 70 */           throw new SAXException(axisFault);
/*    */         }
/* 72 */         this.envelope.setHeader((SOAPHeader)this.myElement);
/*    */       }
/* 74 */       context.pushNewElement(this.myElement);
/*    */     }
/*    */   }
/*    */ 
/*    */   public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*    */     throws SAXException
/*    */   {
/*    */     try
/*    */     {
/* 86 */       this.header = new SOAPHeaderElement(namespace, localName, prefix, attributes, context);
/*    */     }
/*    */     catch (AxisFault axisFault) {
/* 89 */       throw new SAXException(axisFault);
/*    */     }
/*    */ 
/* 92 */     SOAPHandler handler = new SOAPHandler();
/* 93 */     handler.myElement = this.header;
/*    */ 
/* 95 */     return handler;
/*    */   }
/*    */ 
/*    */   public void onEndChild(String namespace, String localName, DeserializationContext context)
/*    */   {
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.HeaderBuilder
 * JD-Core Version:    0.6.0
 */