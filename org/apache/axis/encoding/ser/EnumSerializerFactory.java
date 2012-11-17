/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ 
/*    */ public class EnumSerializerFactory extends BaseSerializerFactory
/*    */ {
/*    */   public EnumSerializerFactory(Class javaType, QName xmlType)
/*    */   {
/* 27 */     super(EnumSerializer.class, xmlType, javaType);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.EnumSerializerFactory
 * JD-Core Version:    0.6.0
 */