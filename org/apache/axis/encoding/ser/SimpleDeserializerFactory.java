/*     */ package org.apache.axis.encoding.ser;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.rpc.JAXRPCException;
/*     */ import javax.xml.rpc.encoding.Deserializer;
/*     */ import org.apache.axis.utils.BeanPropertyDescriptor;
/*     */ import org.apache.axis.utils.BeanUtils;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ 
/*     */ public class SimpleDeserializerFactory extends BaseDeserializerFactory
/*     */ {
/*  39 */   private static final Class[] STRING_STRING_CLASS = { String.class, String.class };
/*     */ 
/*  42 */   private static final Class[] STRING_CLASS = { String.class };
/*     */ 
/*  45 */   private transient Constructor constructor = null;
/*  46 */   private boolean isBasicType = false;
/*     */ 
/*     */   public SimpleDeserializerFactory(Class javaType, QName xmlType)
/*     */   {
/*  52 */     super(SimpleDeserializer.class, xmlType, javaType);
/*  53 */     this.isBasicType = JavaUtils.isBasic(javaType);
/*  54 */     initConstructor(javaType);
/*     */   }
/*     */ 
/*     */   private void initConstructor(Class javaType) {
/*  58 */     if (!this.isBasicType)
/*     */       try
/*     */       {
/*  61 */         if (QName.class.isAssignableFrom(javaType)) {
/*  62 */           this.constructor = javaType.getDeclaredConstructor(STRING_STRING_CLASS);
/*     */         }
/*     */         else
/*  65 */           this.constructor = javaType.getDeclaredConstructor(STRING_CLASS);
/*     */       }
/*     */       catch (NoSuchMethodException e)
/*     */       {
/*     */         try {
/*  70 */           this.constructor = javaType.getDeclaredConstructor(new Class[0]);
/*     */ 
/*  72 */           BeanPropertyDescriptor[] pds = BeanUtils.getPd(javaType);
/*  73 */           if ((pds != null) && 
/*  74 */             (BeanUtils.getSpecificPD(pds, "_value") != null)) {
/*  75 */             return;
/*     */           }
/*     */ 
/*  78 */           throw new IllegalArgumentException(e.toString());
/*     */         } catch (NoSuchMethodException ex) {
/*  80 */           throw new IllegalArgumentException(ex.toString());
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   public Deserializer getDeserializerAs(String mechanismType)
/*     */     throws JAXRPCException
/*     */   {
/*  92 */     if (this.javaType == Object.class) {
/*  93 */       return null;
/*     */     }
/*  95 */     if (this.isBasicType) {
/*  96 */       return new SimpleDeserializer(this.javaType, this.xmlType);
/*     */     }
/*     */ 
/* 100 */     SimpleDeserializer deser = (SimpleDeserializer)super.getDeserializerAs(mechanismType);
/*     */ 
/* 102 */     if (deser != null) {
/* 103 */       deser.setConstructor(this.constructor);
/*     */     }
/* 105 */     return deser;
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
/*     */   {
/* 110 */     in.defaultReadObject();
/* 111 */     initConstructor(this.javaType);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.SimpleDeserializerFactory
 * JD-Core Version:    0.6.0
 */