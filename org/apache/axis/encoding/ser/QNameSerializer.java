/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.encoding.SerializationContext;
/*    */ import org.apache.axis.encoding.SimpleValueSerializer;
/*    */ import org.apache.axis.wsdl.fromJava.Types;
/*    */ import org.w3c.dom.Element;
/*    */ import org.xml.sax.Attributes;
/*    */ 
/*    */ public class QNameSerializer
/*    */   implements SimpleValueSerializer
/*    */ {
/*    */   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context)
/*    */     throws IOException
/*    */   {
/* 43 */     String qnameString = getValueAsString(value, context);
/* 44 */     context.startElement(name, attributes);
/* 45 */     context.writeString(qnameString);
/* 46 */     context.endElement();
/*    */   }
/*    */ 
/*    */   public static String qName2String(QName qname, SerializationContext context)
/*    */   {
/* 51 */     String str = context.qName2String(qname);
/*    */ 
/* 53 */     if (str == qname.getLocalPart()) {
/* 54 */       String namespace = qname.getNamespaceURI();
/* 55 */       if ((namespace != null) && (namespace.length() > 0)) {
/* 56 */         String prefix = context.getPrefixForURI(qname.getNamespaceURI(), null, true);
/*    */ 
/* 59 */         return prefix + ":" + str;
/*    */       }
/*    */     }
/* 62 */     return str;
/*    */   }
/*    */ 
/*    */   public String getValueAsString(Object value, SerializationContext context) {
/* 66 */     return qName2String((QName)value, context);
/*    */   }
/*    */   public String getMechanismType() {
/* 69 */     return "Axis SAX Mechanism";
/*    */   }
/*    */ 
/*    */   public Element writeSchema(Class javaType, Types types)
/*    */     throws Exception
/*    */   {
/* 83 */     return null;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.QNameSerializer
 * JD-Core Version:    0.6.0
 */