/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ 
/*    */ public class TimeSerializerFactory extends BaseSerializerFactory
/*    */ {
/*    */   public TimeSerializerFactory(Class javaType, QName xmlType)
/*    */   {
/* 27 */     super(TimeSerializer.class, xmlType, javaType);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.TimeSerializerFactory
 * JD-Core Version:    0.6.0
 */