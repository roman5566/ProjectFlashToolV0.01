/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ 
/*    */ public class VectorSerializerFactory extends BaseSerializerFactory
/*    */ {
/*    */   public VectorSerializerFactory(Class javaType, QName xmlType)
/*    */   {
/* 29 */     super(VectorSerializer.class, xmlType, javaType);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.VectorSerializerFactory
 * JD-Core Version:    0.6.0
 */