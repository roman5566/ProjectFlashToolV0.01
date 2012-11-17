/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ 
/*    */ public class Base64SerializerFactory extends BaseSerializerFactory
/*    */ {
/*    */   public Base64SerializerFactory(Class javaType, QName xmlType)
/*    */   {
/* 27 */     super(Base64Serializer.class, xmlType, javaType);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.Base64SerializerFactory
 * JD-Core Version:    0.6.0
 */