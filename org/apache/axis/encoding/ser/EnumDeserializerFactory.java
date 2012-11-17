/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ 
/*    */ public class EnumDeserializerFactory extends BaseDeserializerFactory
/*    */ {
/*    */   public EnumDeserializerFactory(Class javaType, QName xmlType)
/*    */   {
/* 27 */     super(EnumDeserializer.class, xmlType, javaType);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.EnumDeserializerFactory
 * JD-Core Version:    0.6.0
 */