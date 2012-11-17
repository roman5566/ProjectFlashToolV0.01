/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.encoding.DeserializationContext;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public class QNameDeserializer extends SimpleDeserializer
/*    */ {
/* 34 */   private DeserializationContext context = null;
/*    */ 
/*    */   public QNameDeserializer(Class javaType, QName xmlType)
/*    */   {
/* 41 */     super(javaType, xmlType);
/*    */   }
/*    */ 
/*    */   public Object makeValue(String source)
/*    */   {
/* 49 */     source = source.trim();
/* 50 */     int colon = source.lastIndexOf(":");
/* 51 */     String namespace = colon < 0 ? "" : this.context.getNamespaceURI(source.substring(0, colon));
/*    */ 
/* 53 */     String localPart = colon < 0 ? source : source.substring(colon + 1);
/* 54 */     return new QName(namespace, localPart);
/*    */   }
/*    */ 
/*    */   public void onStartElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*    */     throws SAXException
/*    */   {
/* 62 */     this.context = context;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.QNameDeserializer
 * JD-Core Version:    0.6.0
 */