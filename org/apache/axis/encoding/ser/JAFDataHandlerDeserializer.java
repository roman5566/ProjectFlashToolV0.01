/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.AxisFault;
/*    */ import org.apache.axis.Part;
/*    */ import org.apache.axis.attachments.AttachmentUtils;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.encoding.DeserializationContext;
/*    */ import org.apache.axis.encoding.DeserializerImpl;
/*    */ import org.apache.axis.message.SOAPHandler;
/*    */ import org.apache.axis.soap.SOAPConstants;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public class JAFDataHandlerDeserializer extends DeserializerImpl
/*    */ {
/* 41 */   protected static Log log = LogFactory.getLog(JAFDataHandlerDeserializer.class.getName());
/*    */ 
/*    */   public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*    */     throws SAXException
/*    */   {
/* 49 */     if ((!context.isDoneParsing()) && 
/* 50 */       (this.myElement == null)) {
/*    */       try {
/* 52 */         this.myElement = makeNewElement(namespace, localName, prefix, attributes, context);
/*    */       } catch (AxisFault axisFault) {
/* 54 */         throw new SAXException(axisFault);
/*    */       }
/* 56 */       context.pushNewElement(this.myElement);
/*    */     }
/*    */ 
/* 59 */     populateDataHandler(context, namespace, localName, attributes);
/*    */   }
/*    */ 
/*    */   private void populateDataHandler(DeserializationContext context, String namespace, String localName, Attributes attributes) {
/* 63 */     SOAPConstants soapConstants = context.getSOAPConstants();
/*    */ 
/* 65 */     QName type = context.getTypeFromAttributes(namespace, localName, attributes);
/*    */ 
/* 68 */     if (log.isDebugEnabled()) {
/* 69 */       log.debug(Messages.getMessage("gotType00", "Deser", "" + type));
/*    */     }
/*    */ 
/* 72 */     String href = attributes.getValue(soapConstants.getAttrHref());
/* 73 */     if (href != null) {
/* 74 */       Object ref = context.getObjectByRef(href);
/*    */       try {
/* 76 */         ref = AttachmentUtils.getActivationDataHandler((Part)ref);
/*    */       } catch (AxisFault e) {
/*    */       }
/* 79 */       setValue(ref);
/*    */     }
/*    */   }
/*    */ 
/*    */   public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*    */     throws SAXException
/*    */   {
/* 93 */     if ((namespace.equals("http://www.w3.org/2004/08/xop/include")) && (localName.equals("Include")))
/*    */     {
/* 95 */       populateDataHandler(context, namespace, localName, attributes);
/* 96 */       return null;
/*    */     }
/* 98 */     throw new SAXException(Messages.getMessage("noSubElements", namespace + ":" + localName));
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.JAFDataHandlerDeserializer
 * JD-Core Version:    0.6.0
 */