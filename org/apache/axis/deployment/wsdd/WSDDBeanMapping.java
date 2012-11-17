/*    */ package org.apache.axis.deployment.wsdd;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.encoding.SerializationContext;
/*    */ import org.w3c.dom.Element;
/*    */ import org.xml.sax.helpers.AttributesImpl;
/*    */ 
/*    */ public class WSDDBeanMapping extends WSDDTypeMapping
/*    */ {
/*    */   public WSDDBeanMapping()
/*    */   {
/*    */   }
/*    */ 
/*    */   public WSDDBeanMapping(Element e)
/*    */     throws WSDDException
/*    */   {
/* 51 */     super(e);
/*    */ 
/* 53 */     this.serializer = "org.apache.axis.encoding.ser.BeanSerializerFactory";
/* 54 */     this.deserializer = "org.apache.axis.encoding.ser.BeanDeserializerFactory";
/* 55 */     this.encodingStyle = null;
/*    */   }
/*    */ 
/*    */   protected QName getElementName() {
/* 59 */     return QNAME_BEANMAPPING;
/*    */   }
/*    */ 
/*    */   public void writeToContext(SerializationContext context) throws IOException {
/* 63 */     AttributesImpl attrs = new AttributesImpl();
/*    */ 
/* 65 */     String typeStr = context.qName2String(this.typeQName);
/* 66 */     attrs.addAttribute("", "languageSpecificType", "languageSpecificType", "CDATA", typeStr);
/*    */ 
/* 69 */     String qnameStr = context.qName2String(this.qname);
/* 70 */     attrs.addAttribute("", "qname", "qname", "CDATA", qnameStr);
/*    */ 
/* 72 */     context.startElement(WSDDConstants.QNAME_BEANMAPPING, attrs);
/* 73 */     context.endElement();
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.WSDDBeanMapping
 * JD-Core Version:    0.6.0
 */