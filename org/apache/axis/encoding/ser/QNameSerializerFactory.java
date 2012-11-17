/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ 
/*    */ public class QNameSerializerFactory extends BaseSerializerFactory
/*    */ {
/*    */   public QNameSerializerFactory(Class javaType, QName xmlType)
/*    */   {
/* 26 */     super(QNameSerializer.class, xmlType, javaType);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.QNameSerializerFactory
 * JD-Core Version:    0.6.0
 */