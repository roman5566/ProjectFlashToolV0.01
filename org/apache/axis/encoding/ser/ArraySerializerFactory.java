/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.Constants;
/*    */ import org.apache.axis.encoding.Serializer;
/*    */ 
/*    */ public class ArraySerializerFactory extends BaseSerializerFactory
/*    */ {
/* 38 */   private QName componentType = null;
/* 39 */   private QName componentQName = null;
/*    */ 
/*    */   public ArraySerializerFactory()
/*    */   {
/* 32 */     this(new Object[0].getClass(), Constants.SOAP_ARRAY);
/*    */   }
/*    */   public ArraySerializerFactory(Class javaType, QName xmlType) {
/* 35 */     super(ArraySerializer.class, xmlType, javaType);
/*    */   }
/*    */ 
/*    */   public ArraySerializerFactory(QName componentType)
/*    */   {
/* 42 */     super(ArraySerializer.class, Constants.SOAP_ARRAY, new Object[0].getClass());
/* 43 */     this.componentType = componentType;
/*    */   }
/*    */ 
/*    */   public ArraySerializerFactory(QName componentType, QName componentQName) {
/* 47 */     this(componentType);
/* 48 */     this.componentQName = componentQName;
/*    */   }
/*    */ 
/*    */   public void setComponentQName(QName componentQName)
/*    */   {
/* 55 */     this.componentQName = componentQName;
/*    */   }
/*    */ 
/*    */   public void setComponentType(QName componentType)
/*    */   {
/* 62 */     this.componentType = componentType;
/*    */   }
/*    */ 
/*    */   public QName getComponentQName()
/*    */   {
/* 69 */     return this.componentQName;
/*    */   }
/*    */ 
/*    */   public QName getComponentType()
/*    */   {
/* 75 */     return this.componentType;
/*    */   }
/*    */ 
/*    */   protected Serializer getGeneralPurpose(String mechanismType)
/*    */   {
/* 85 */     if (this.componentType == null) {
/* 86 */       return super.getGeneralPurpose(mechanismType);
/*    */     }
/* 88 */     return new ArraySerializer(this.javaType, this.xmlType, this.componentType, this.componentQName);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.ArraySerializerFactory
 * JD-Core Version:    0.6.0
 */