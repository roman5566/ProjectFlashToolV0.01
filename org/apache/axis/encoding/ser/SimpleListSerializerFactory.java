/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ 
/*    */ public class SimpleListSerializerFactory extends BaseSerializerFactory
/*    */ {
/*    */   public SimpleListSerializerFactory(Class javaType, QName xmlType)
/*    */   {
/* 35 */     super(SimpleListSerializer.class, xmlType, javaType);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.SimpleListSerializerFactory
 * JD-Core Version:    0.6.0
 */