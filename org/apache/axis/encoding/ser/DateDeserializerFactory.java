/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ 
/*    */ public class DateDeserializerFactory extends BaseDeserializerFactory
/*    */ {
/*    */   public DateDeserializerFactory(Class javaType, QName xmlType)
/*    */   {
/* 29 */     super(DateDeserializer.class, xmlType, javaType);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.DateDeserializerFactory
 * JD-Core Version:    0.6.0
 */