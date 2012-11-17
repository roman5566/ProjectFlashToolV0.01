/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInputStream;
/*    */ import javax.xml.namespace.QName;
/*    */ import javax.xml.rpc.JAXRPCException;
/*    */ import org.apache.axis.description.TypeDesc;
/*    */ import org.apache.axis.utils.BeanPropertyDescriptor;
/*    */ import org.apache.axis.utils.BeanUtils;
/*    */ import org.apache.axis.utils.JavaUtils;
/*    */ 
/*    */ public class BeanSerializerFactory extends BaseSerializerFactory
/*    */ {
/* 37 */   protected transient TypeDesc typeDesc = null;
/* 38 */   protected transient BeanPropertyDescriptor[] propertyDescriptor = null;
/*    */ 
/*    */   public BeanSerializerFactory(Class javaType, QName xmlType) {
/* 41 */     super(BeanSerializer.class, xmlType, javaType);
/* 42 */     init(javaType);
/*    */   }
/*    */ 
/*    */   private void init(Class javaType)
/*    */   {
/* 49 */     if (JavaUtils.isEnumClass(javaType)) {
/* 50 */       this.serClass = EnumSerializer.class;
/*    */     }
/*    */ 
/* 53 */     this.typeDesc = TypeDesc.getTypeDescForClass(javaType);
/*    */ 
/* 55 */     if (this.typeDesc != null)
/* 56 */       this.propertyDescriptor = this.typeDesc.getPropertyDescriptors();
/*    */     else
/* 58 */       this.propertyDescriptor = BeanUtils.getPd(javaType, null);
/*    */   }
/*    */ 
/*    */   public javax.xml.rpc.encoding.Serializer getSerializerAs(String mechanismType)
/*    */     throws JAXRPCException
/*    */   {
/* 64 */     return (org.apache.axis.encoding.Serializer)super.getSerializerAs(mechanismType);
/*    */   }
/*    */ 
/*    */   protected org.apache.axis.encoding.Serializer getGeneralPurpose(String mechanismType)
/*    */   {
/* 72 */     if ((this.javaType == null) || (this.xmlType == null)) {
/* 73 */       return super.getGeneralPurpose(mechanismType);
/*    */     }
/*    */ 
/* 76 */     if (this.serClass == EnumSerializer.class) {
/* 77 */       return super.getGeneralPurpose(mechanismType);
/*    */     }
/*    */ 
/* 80 */     return new BeanSerializer(this.javaType, this.xmlType, this.typeDesc, this.propertyDescriptor);
/*    */   }
/*    */ 
/*    */   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
/*    */   {
/* 85 */     in.defaultReadObject();
/* 86 */     init(this.javaType);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.BeanSerializerFactory
 * JD-Core Version:    0.6.0
 */