/*    */ package org.apache.axis.message;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.Constants;
/*    */ import org.apache.axis.encoding.Callback;
/*    */ import org.apache.axis.encoding.CallbackTarget;
/*    */ import org.apache.axis.encoding.DeserializationContext;
/*    */ import org.apache.axis.encoding.Deserializer;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public class SOAPFaultReasonBuilder extends SOAPHandler
/*    */   implements Callback
/*    */ {
/* 38 */   private ArrayList text = new ArrayList();
/*    */   private SOAPFaultBuilder faultBuilder;
/*    */ 
/*    */   public SOAPFaultReasonBuilder(SOAPFaultBuilder faultBuilder)
/*    */   {
/* 42 */     this.faultBuilder = faultBuilder;
/*    */   }
/*    */ 
/*    */   public SOAPHandler onStartChild(String namespace, String name, String prefix, Attributes attributes, DeserializationContext context)
/*    */     throws SAXException
/*    */   {
/* 52 */     QName thisQName = new QName(namespace, name);
/* 53 */     if (thisQName.equals(Constants.QNAME_TEXT_SOAP12)) {
/* 54 */       Deserializer currentDeser = null;
/* 55 */       currentDeser = context.getDeserializerForType(Constants.XSD_STRING);
/* 56 */       if (currentDeser != null) {
/* 57 */         currentDeser.registerValueTarget(new CallbackTarget(this.faultBuilder, thisQName));
/*    */       }
/*    */ 
/* 60 */       return (SOAPHandler)currentDeser;
/*    */     }
/* 62 */     return null;
/*    */   }
/*    */ 
/*    */   public void setValue(Object value, Object hint)
/*    */   {
/* 75 */     this.text.add(value);
/*    */   }
/*    */ 
/*    */   public ArrayList getText() {
/* 79 */     return this.text;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.SOAPFaultReasonBuilder
 * JD-Core Version:    0.6.0
 */