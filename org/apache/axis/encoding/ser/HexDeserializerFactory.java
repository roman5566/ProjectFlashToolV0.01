/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ 
/*    */ public class HexDeserializerFactory extends BaseDeserializerFactory
/*    */ {
/*    */   public HexDeserializerFactory(Class javaType, QName xmlType)
/*    */   {
/* 27 */     super(HexDeserializer.class, xmlType, javaType);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.HexDeserializerFactory
 * JD-Core Version:    0.6.0
 */