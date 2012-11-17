/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.encoding.SerializationContext;
/*    */ import org.apache.axis.encoding.SimpleValueSerializer;
/*    */ import org.apache.axis.types.HexBinary;
/*    */ import org.apache.axis.utils.JavaUtils;
/*    */ import org.apache.axis.wsdl.fromJava.Types;
/*    */ import org.w3c.dom.Element;
/*    */ import org.xml.sax.Attributes;
/*    */ 
/*    */ public class HexSerializer
/*    */   implements SimpleValueSerializer
/*    */ {
/*    */   public QName xmlType;
/*    */   public Class javaType;
/*    */ 
/*    */   public HexSerializer(Class javaType, QName xmlType)
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
/* 59 */     value = JavaUtils.convert(value, this.javaType);
/* 60 */     if (this.javaType == HexBinary.class) {
/* 61 */       return value.toString();
/*    */     }
/* 63 */     return HexBinary.encode((byte[])value);
/*    */   }
/*    */ 
/*    */   public String getMechanismType() {
/* 67 */     return "Axis SAX Mechanism";
/*    */   }
/*    */ 
/*    */   public Element writeSchema(Class javaType, Types types)
/*    */     throws Exception
/*    */   {
/* 81 */     return null;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.HexSerializer
 * JD-Core Version:    0.6.0
 */