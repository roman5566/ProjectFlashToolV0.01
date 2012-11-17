/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ 
/*    */ public class CalendarSerializerFactory extends BaseSerializerFactory
/*    */ {
/*    */   public CalendarSerializerFactory(Class javaType, QName xmlType)
/*    */   {
/* 27 */     super(CalendarSerializer.class, xmlType, javaType);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.CalendarSerializerFactory
 * JD-Core Version:    0.6.0
 */