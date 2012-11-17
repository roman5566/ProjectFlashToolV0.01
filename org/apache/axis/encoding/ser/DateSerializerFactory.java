/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ 
/*    */ public class DateSerializerFactory extends BaseSerializerFactory
/*    */ {
/*    */   public DateSerializerFactory(Class javaType, QName xmlType)
/*    */   {
/* 27 */     super(DateSerializer.class, xmlType, javaType);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.DateSerializerFactory
 * JD-Core Version:    0.6.0
 */