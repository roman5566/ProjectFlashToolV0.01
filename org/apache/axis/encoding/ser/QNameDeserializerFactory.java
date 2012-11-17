/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ 
/*    */ public class QNameDeserializerFactory extends BaseDeserializerFactory
/*    */ {
/*    */   public QNameDeserializerFactory(Class javaType, QName xmlType)
/*    */   {
/* 27 */     super(QNameDeserializer.class, xmlType, javaType);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.QNameDeserializerFactory
 * JD-Core Version:    0.6.0
 */