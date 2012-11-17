/*    */ package org.apache.axis.deployment.wsdd;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.encoding.SerializationContext;
/*    */ import org.apache.axis.utils.XMLUtils;
/*    */ import org.w3c.dom.Attr;
/*    */ import org.w3c.dom.Element;
/*    */ import org.xml.sax.helpers.AttributesImpl;
/*    */ 
/*    */ public class WSDDArrayMapping extends WSDDTypeMapping
/*    */ {
/* 40 */   private QName innerType = null;
/*    */ 
/*    */   public WSDDArrayMapping()
/*    */   {
/*    */   }
/*    */ 
/*    */   public WSDDArrayMapping(Element e)
/*    */     throws WSDDException
/*    */   {
/* 49 */     super(e);
/* 50 */     Attr innerTypeAttr = e.getAttributeNode("innerType");
/* 51 */     if (innerTypeAttr != null) {
/* 52 */       String qnameStr = innerTypeAttr.getValue();
/* 53 */       this.innerType = XMLUtils.getQNameFromString(qnameStr, e);
/*    */     }
/* 55 */     this.serializer = "org.apache.axis.encoding.ser.ArraySerializerFactory";
/* 56 */     this.deserializer = "org.apache.axis.encoding.ser.ArrayDeserializerFactory";
/*    */   }
/*    */ 
/*    */   protected QName getElementName() {
/* 60 */     return QNAME_ARRAYMAPPING;
/*    */   }
/*    */ 
/*    */   public QName getInnerType()
/*    */   {
/* 67 */     return this.innerType;
/*    */   }
/*    */ 
/*    */   public void writeToContext(SerializationContext context) throws IOException {
/* 71 */     AttributesImpl attrs = new AttributesImpl();
/*    */ 
/* 73 */     String typeStr = context.qName2String(this.typeQName);
/* 74 */     attrs.addAttribute("", "languageSpecificType", "languageSpecificType", "CDATA", typeStr);
/*    */ 
/* 76 */     String qnameStr = context.qName2String(this.qname);
/* 77 */     attrs.addAttribute("", "qname", "qname", "CDATA", qnameStr);
/*    */ 
/* 79 */     String innerTypeStr = context.qName2String(this.innerType);
/* 80 */     attrs.addAttribute("", "innerType", "innerType", "CDATA", innerTypeStr);
/*    */ 
/* 82 */     context.startElement(QNAME_ARRAYMAPPING, attrs);
/* 83 */     context.endElement();
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.WSDDArrayMapping
 * JD-Core Version:    0.6.0
 */