/*    */ package org.apache.axis.encoding.ser.castor;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.encoding.DeserializerFactory;
/*    */ import org.apache.axis.encoding.ser.BaseDeserializerFactory;
/*    */ 
/*    */ public class CastorEnumTypeDeserializerFactory extends BaseDeserializerFactory
/*    */ {
/*    */   public CastorEnumTypeDeserializerFactory(Class javaType, QName xmlType)
/*    */   {
/* 32 */     super(CastorEnumTypeDeserializer.class, xmlType, javaType);
/*    */   }
/*    */   public static DeserializerFactory create(Class javaType, QName xmlType) {
/* 35 */     return new CastorEnumTypeDeserializerFactory(javaType, xmlType);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.castor.CastorEnumTypeDeserializerFactory
 * JD-Core Version:    0.6.0
 */