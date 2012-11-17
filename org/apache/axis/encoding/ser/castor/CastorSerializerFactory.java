/*    */ package org.apache.axis.encoding.ser.castor;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.encoding.SerializerFactory;
/*    */ import org.apache.axis.encoding.ser.BaseSerializerFactory;
/*    */ 
/*    */ public class CastorSerializerFactory extends BaseSerializerFactory
/*    */ {
/*    */   public CastorSerializerFactory(Class javaType, QName xmlType)
/*    */   {
/* 31 */     super(CastorSerializer.class, xmlType, javaType);
/*    */   }
/*    */   public static SerializerFactory create(Class javaType, QName xmlType) {
/* 34 */     return new CastorSerializerFactory(javaType, xmlType);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.castor.CastorSerializerFactory
 * JD-Core Version:    0.6.0
 */