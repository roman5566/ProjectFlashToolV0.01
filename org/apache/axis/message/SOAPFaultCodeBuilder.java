/*    */ package org.apache.axis.message;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.Constants;
/*    */ import org.apache.axis.encoding.Callback;
/*    */ import org.apache.axis.encoding.CallbackTarget;
/*    */ import org.apache.axis.encoding.DeserializationContext;
/*    */ import org.apache.axis.encoding.Deserializer;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public class SOAPFaultCodeBuilder extends SOAPHandler
/*    */   implements Callback
/*    */ {
/* 38 */   protected QName faultCode = null;
/* 39 */   protected SOAPFaultCodeBuilder next = null;
/*    */ 
/*    */   public QName getFaultCode()
/*    */   {
/* 45 */     return this.faultCode;
/*    */   }
/*    */ 
/*    */   public SOAPFaultCodeBuilder getNext() {
/* 49 */     return this.next;
/*    */   }
/*    */ 
/*    */   public SOAPHandler onStartChild(String namespace, String name, String prefix, Attributes attributes, DeserializationContext context)
/*    */     throws SAXException
/*    */   {
/* 60 */     QName thisQName = new QName(namespace, name);
/* 61 */     if (thisQName.equals(Constants.QNAME_FAULTVALUE_SOAP12)) {
/* 62 */       Deserializer currentDeser = null;
/* 63 */       currentDeser = context.getDeserializerForType(Constants.XSD_QNAME);
/* 64 */       if (currentDeser != null) {
/* 65 */         currentDeser.registerValueTarget(new CallbackTarget(this, thisQName));
/*    */       }
/* 67 */       return (SOAPHandler)currentDeser;
/* 68 */     }if (thisQName.equals(Constants.QNAME_FAULTSUBCODE_SOAP12)) {
/* 69 */       return this.next = new SOAPFaultCodeBuilder();
/*    */     }
/* 71 */     return null;
/*    */   }
/*    */ 
/*    */   public void setValue(Object value, Object hint)
/*    */   {
/* 81 */     QName thisQName = (QName)hint;
/* 82 */     if (thisQName.equals(Constants.QNAME_FAULTVALUE_SOAP12))
/* 83 */       this.faultCode = ((QName)value);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.SOAPFaultCodeBuilder
 * JD-Core Version:    0.6.0
 */