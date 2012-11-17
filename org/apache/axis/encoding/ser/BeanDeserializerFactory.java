/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.description.TypeDesc;
/*    */ import org.apache.axis.encoding.Deserializer;
/*    */ import org.apache.axis.utils.BeanPropertyDescriptor;
/*    */ import org.apache.axis.utils.BeanUtils;
/*    */ import org.apache.axis.utils.JavaUtils;
/*    */ 
/*    */ public class BeanDeserializerFactory extends BaseDeserializerFactory
/*    */ {
/* 39 */   protected transient TypeDesc typeDesc = null;
/* 40 */   protected transient Map propertyMap = null;
/*    */ 
/*    */   public BeanDeserializerFactory(Class javaType, QName xmlType) {
/* 43 */     super(BeanDeserializer.class, xmlType, javaType);
/*    */ 
/* 47 */     if (JavaUtils.isEnumClass(javaType)) {
/* 48 */       this.deserClass = EnumDeserializer.class;
/*    */     }
/*    */ 
/* 51 */     this.typeDesc = TypeDesc.getTypeDescForClass(javaType);
/* 52 */     this.propertyMap = getProperties(javaType, this.typeDesc);
/*    */   }
/*    */ 
/*    */   public static Map getProperties(Class javaType, TypeDesc typeDesc)
/*    */   {
/* 59 */     Map propertyMap = null;
/*    */ 
/* 61 */     if (typeDesc != null) {
/* 62 */       propertyMap = typeDesc.getPropertyDescriptorMap();
/*    */     } else {
/* 64 */       BeanPropertyDescriptor[] pd = BeanUtils.getPd(javaType, null);
/* 65 */       propertyMap = new HashMap();
/*    */ 
/* 67 */       for (int i = 0; i < pd.length; i++) {
/* 68 */         BeanPropertyDescriptor descriptor = pd[i];
/* 69 */         propertyMap.put(descriptor.getName(), descriptor);
/*    */       }
/*    */     }
/*    */ 
/* 73 */     return propertyMap;
/*    */   }
/*    */ 
/*    */   protected Deserializer getGeneralPurpose(String mechanismType)
/*    */   {
/* 81 */     if ((this.javaType == null) || (this.xmlType == null)) {
/* 82 */       return super.getGeneralPurpose(mechanismType);
/*    */     }
/*    */ 
/* 85 */     if (this.deserClass == EnumDeserializer.class) {
/* 86 */       return super.getGeneralPurpose(mechanismType);
/*    */     }
/*    */ 
/* 89 */     return new BeanDeserializer(this.javaType, this.xmlType, this.typeDesc, this.propertyMap);
/*    */   }
/*    */ 
/*    */   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
/*    */   {
/* 94 */     in.defaultReadObject();
/* 95 */     this.typeDesc = TypeDesc.getTypeDescForClass(this.javaType);
/* 96 */     this.propertyMap = getProperties(this.javaType, this.typeDesc);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.BeanDeserializerFactory
 * JD-Core Version:    0.6.0
 */