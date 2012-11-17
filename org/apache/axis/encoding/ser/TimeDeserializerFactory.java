/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ 
/*    */ public class TimeDeserializerFactory extends BaseDeserializerFactory
/*    */ {
/*    */   public TimeDeserializerFactory(Class javaType, QName xmlType)
/*    */   {
/* 28 */     super(TimeDeserializer.class, xmlType, javaType);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.TimeDeserializerFactory
 * JD-Core Version:    0.6.0
 */