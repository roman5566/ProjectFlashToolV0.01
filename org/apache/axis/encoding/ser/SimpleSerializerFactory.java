/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import javax.xml.rpc.JAXRPCException;
/*    */ import javax.xml.rpc.encoding.Serializer;
/*    */ import org.apache.axis.utils.JavaUtils;
/*    */ 
/*    */ public class SimpleSerializerFactory extends BaseSerializerFactory
/*    */ {
/* 29 */   private boolean isBasicType = false;
/*    */ 
/*    */   public SimpleSerializerFactory(Class javaType, QName xmlType)
/*    */   {
/* 36 */     super(SimpleSerializer.class, xmlType, javaType);
/* 37 */     this.isBasicType = JavaUtils.isBasic(javaType);
/*    */   }
/*    */ 
/*    */   public Serializer getSerializerAs(String mechanismType) throws JAXRPCException {
/* 41 */     if (this.isBasicType) {
/* 42 */       return new SimpleSerializer(this.javaType, this.xmlType);
/*    */     }
/* 44 */     return super.getSerializerAs(mechanismType);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.SimpleSerializerFactory
 * JD-Core Version:    0.6.0
 */