/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ 
/*    */ public class Base64DeserializerFactory extends BaseDeserializerFactory
/*    */ {
/*    */   public Base64DeserializerFactory(Class javaType, QName xmlType)
/*    */   {
/* 27 */     super(Base64Deserializer.class, xmlType, javaType);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.Base64DeserializerFactory
 * JD-Core Version:    0.6.0
 */