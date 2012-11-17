/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.encoding.Base64;
/*    */ import org.apache.axis.encoding.SerializationContext;
/*    */ import org.apache.axis.encoding.SimpleValueSerializer;
/*    */ import org.apache.axis.wsdl.fromJava.Types;
/*    */ import org.w3c.dom.Element;
/*    */ import org.xml.sax.Attributes;
/*    */ 
/*    */ public class Base64Serializer
/*    */   implements SimpleValueSerializer
/*    */ {
/*    */   public QName xmlType;
/*    */   public Class javaType;
/*    */ 
/*    */   public Base64Serializer(Class javaType, QName xmlType)
/*    */   {
/* 42 */     this.xmlType = xmlType;
/* 43 */     this.javaType = javaType;
/*    */   }
/*    */ 
/*    */   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context)
/*    */     throws IOException
/*    */   {
/* 53 */     context.startElement(name, attributes);
/* 54 */     context.writeString(getValueAsString(value, context));
/* 55 */     context.endElement();
/*    */   }
/*    */ 
/*    */   public String getValueAsString(Object value, SerializationContext context) {
/* 59 */     byte[] data = null;
/* 60 */     if (this.javaType == new byte[0].getClass()) {
/* 61 */       data = (byte[])value;
/*    */     } else {
/* 63 */       data = new byte[((Byte[])value).length];
/* 64 */       for (int i = 0; i < data.length; i++) {
/* 65 */         Byte b = ((Byte[])value)[i];
/* 66 */         if (b != null) {
/* 67 */           data[i] = b.byteValue();
/*    */         }
/*    */       }
/*    */     }
/* 71 */     return Base64.encode(data, 0, data.length);
/*    */   }
/*    */   public String getMechanismType() {
/* 74 */     return "Axis SAX Mechanism";
/*    */   }
/*    */ 
/*    */   public Element writeSchema(Class javaType, Types types)
/*    */     throws Exception
/*    */   {
/* 88 */     return null;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.Base64Serializer
 * JD-Core Version:    0.6.0
 */