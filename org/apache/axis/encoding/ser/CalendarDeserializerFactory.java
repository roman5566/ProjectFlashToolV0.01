/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ 
/*    */ public class CalendarDeserializerFactory extends BaseDeserializerFactory
/*    */ {
/*    */   public CalendarDeserializerFactory(Class javaType, QName xmlType)
/*    */   {
/* 29 */     super(CalendarDeserializer.class, xmlType, javaType);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.CalendarDeserializerFactory
 * JD-Core Version:    0.6.0
 */