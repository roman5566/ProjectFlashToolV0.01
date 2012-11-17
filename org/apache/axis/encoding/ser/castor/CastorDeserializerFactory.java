/*    */ package org.apache.axis.encoding.ser.castor;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.encoding.DeserializerFactory;
/*    */ import org.apache.axis.encoding.ser.BaseDeserializerFactory;
/*    */ 
/*    */ public class CastorDeserializerFactory extends BaseDeserializerFactory
/*    */ {
/*    */   public CastorDeserializerFactory(Class javaType, QName xmlType)
/*    */   {
/* 32 */     super(CastorDeserializer.class, xmlType, javaType);
/*    */   }
/*    */   public static DeserializerFactory create(Class javaType, QName xmlType) {
/* 35 */     return new CastorDeserializerFactory(javaType, xmlType);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.castor.CastorDeserializerFactory
 * JD-Core Version:    0.6.0
 */