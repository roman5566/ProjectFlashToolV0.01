/*     */ package org.apache.axis.encoding;
/*     */ 
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.rpc.JAXRPCException;
/*     */ import javax.xml.rpc.encoding.DeserializerFactory;
/*     */ import javax.xml.rpc.encoding.SerializerFactory;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class TypeMappingDelegate
/*     */   implements TypeMapping
/*     */ {
/*  33 */   static final TypeMappingImpl placeholder = new TypeMappingImpl();
/*     */   TypeMappingImpl delegate;
/*     */   TypeMappingDelegate next;
/*     */ 
/*     */   TypeMappingDelegate(TypeMappingImpl delegate)
/*     */   {
/*  42 */     if (delegate == null) {
/*  43 */       throw new RuntimeException(Messages.getMessage("NullDelegate"));
/*     */     }
/*  45 */     this.delegate = delegate;
/*     */   }
/*     */ 
/*     */   public String[] getSupportedEncodings()
/*     */   {
/*  54 */     return this.delegate.getSupportedEncodings();
/*     */   }
/*     */ 
/*     */   public void setSupportedEncodings(String[] namespaceURIs) {
/*  58 */     this.delegate.setSupportedEncodings(namespaceURIs);
/*     */   }
/*     */ 
/*     */   public void register(Class javaType, QName xmlType, SerializerFactory sf, DeserializerFactory dsf)
/*     */     throws JAXRPCException
/*     */   {
/*  73 */     this.delegate.register(javaType, xmlType, sf, dsf);
/*     */   }
/*     */ 
/*     */   public SerializerFactory getSerializer(Class javaType, QName xmlType)
/*     */     throws JAXRPCException
/*     */   {
/*  80 */     SerializerFactory sf = this.delegate.getSerializer(javaType, xmlType);
/*     */ 
/*  82 */     if ((sf == null) && (this.next != null)) {
/*  83 */       sf = this.next.getSerializer(javaType, xmlType);
/*     */     }
/*     */ 
/*  86 */     if (sf == null) {
/*  87 */       sf = this.delegate.finalGetSerializer(javaType);
/*     */     }
/*     */ 
/*  90 */     return sf;
/*     */   }
/*     */ 
/*     */   public SerializerFactory getSerializer(Class javaType)
/*     */     throws JAXRPCException
/*     */   {
/*  96 */     return getSerializer(javaType, null);
/*     */   }
/*     */ 
/*     */   public DeserializerFactory getDeserializer(Class javaType, QName xmlType)
/*     */     throws JAXRPCException
/*     */   {
/* 102 */     return getDeserializer(javaType, xmlType, this);
/*     */   }
/*     */ 
/*     */   public DeserializerFactory getDeserializer(Class javaType, QName xmlType, TypeMappingDelegate start)
/*     */     throws JAXRPCException
/*     */   {
/* 108 */     DeserializerFactory df = this.delegate.getDeserializer(javaType, xmlType, start);
/*     */ 
/* 110 */     if ((df == null) && (this.next != null)) {
/* 111 */       df = this.next.getDeserializer(javaType, xmlType, start);
/*     */     }
/* 113 */     if (df == null) {
/* 114 */       df = this.delegate.finalGetDeserializer(javaType, xmlType, start);
/*     */     }
/* 116 */     return df;
/*     */   }
/*     */ 
/*     */   public DeserializerFactory getDeserializer(QName xmlType)
/*     */     throws JAXRPCException
/*     */   {
/* 122 */     return getDeserializer(null, xmlType);
/*     */   }
/*     */ 
/*     */   public void removeSerializer(Class javaType, QName xmlType) throws JAXRPCException
/*     */   {
/* 127 */     this.delegate.removeSerializer(javaType, xmlType);
/*     */   }
/*     */ 
/*     */   public void removeDeserializer(Class javaType, QName xmlType) throws JAXRPCException
/*     */   {
/* 132 */     this.delegate.removeDeserializer(javaType, xmlType);
/*     */   }
/*     */ 
/*     */   public boolean isRegistered(Class javaType, QName xmlType) {
/* 136 */     boolean result = this.delegate.isRegistered(javaType, xmlType);
/* 137 */     if ((!result) && (this.next != null)) {
/* 138 */       return this.next.isRegistered(javaType, xmlType);
/*     */     }
/* 140 */     return result;
/*     */   }
/*     */ 
/*     */   public QName getTypeQName(Class javaType)
/*     */   {
/* 151 */     return this.delegate.getTypeQName(javaType, this.next);
/*     */   }
/*     */ 
/*     */   public Class getClassForQName(QName xmlType)
/*     */   {
/* 160 */     return getClassForQName(xmlType, null);
/*     */   }
/*     */ 
/*     */   public Class getClassForQName(QName xmlType, Class javaType)
/*     */   {
/* 170 */     return this.delegate.getClassForQName(xmlType, javaType, this.next);
/*     */   }
/*     */ 
/*     */   public QName getTypeQNameExact(Class javaType)
/*     */   {
/* 182 */     QName result = this.delegate.getTypeQNameExact(javaType, this.next);
/*     */ 
/* 184 */     return result;
/*     */   }
/*     */ 
/*     */   public void setNext(TypeMappingDelegate next)
/*     */   {
/* 191 */     if (next == this) {
/* 192 */       return;
/*     */     }
/* 194 */     this.next = next;
/*     */   }
/*     */ 
/*     */   public TypeMappingDelegate getNext()
/*     */   {
/* 201 */     return this.next;
/*     */   }
/*     */ 
/*     */   public Class[] getAllClasses()
/*     */   {
/* 208 */     return this.delegate.getAllClasses(this.next);
/*     */   }
/*     */ 
/*     */   public QName getXMLType(Class javaType, QName xmlType, boolean encoded)
/*     */     throws JAXRPCException
/*     */   {
/* 230 */     QName result = this.delegate.getXMLType(javaType, xmlType, encoded);
/* 231 */     if ((result == null) && (this.next != null)) {
/* 232 */       return this.next.getXMLType(javaType, xmlType, encoded);
/*     */     }
/* 234 */     return result;
/*     */   }
/*     */ 
/*     */   public void setDoAutoTypes(boolean doAutoTypes) {
/* 238 */     this.delegate.setDoAutoTypes(doAutoTypes);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.TypeMappingDelegate
 * JD-Core Version:    0.6.0
 */