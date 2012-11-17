/*     */ package org.apache.axis.encoding.ser;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Iterator;
/*     */ import java.util.Vector;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.rpc.JAXRPCException;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.encoding.DeserializerFactory;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public abstract class BaseDeserializerFactory extends BaseFactory
/*     */   implements DeserializerFactory
/*     */ {
/*  42 */   protected static Log log = LogFactory.getLog(BaseDeserializerFactory.class.getName());
/*     */ 
/*  45 */   static transient Vector mechanisms = null;
/*     */ 
/*  47 */   protected Class deserClass = null;
/*  48 */   protected QName xmlType = null;
/*  49 */   protected Class javaType = null;
/*     */ 
/*  51 */   protected transient Constructor deserClassConstructor = null;
/*  52 */   protected transient Method getDeserializer = null;
/*     */ 
/* 133 */   private static final Class[] CLASS_QNAME_CLASS = { Class.class, QName.class };
/*     */ 
/*     */   public BaseDeserializerFactory(Class deserClass)
/*     */   {
/*  59 */     if (!org.apache.axis.encoding.Deserializer.class.isAssignableFrom(deserClass)) {
/*  60 */       throw new ClassCastException(org.apache.axis.i18n.Messages.getMessage("BadImplementation00", deserClass.getName(), org.apache.axis.encoding.Deserializer.class.getName()));
/*     */     }
/*     */ 
/*  65 */     this.deserClass = deserClass;
/*     */   }
/*     */ 
/*     */   public BaseDeserializerFactory(Class deserClass, QName xmlType, Class javaType)
/*     */   {
/*  71 */     this(deserClass);
/*  72 */     this.xmlType = xmlType;
/*  73 */     this.javaType = javaType;
/*     */   }
/*     */ 
/*     */   public javax.xml.rpc.encoding.Deserializer getDeserializerAs(String mechanismType)
/*     */     throws JAXRPCException
/*     */   {
/*  79 */     org.apache.axis.encoding.Deserializer deser = null;
/*     */ 
/*  84 */     deser = getSpecialized(mechanismType);
/*     */ 
/*  88 */     if (deser == null) {
/*  89 */       deser = getGeneralPurpose(mechanismType);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*  94 */       if (deser == null)
/*  95 */         deser = (org.apache.axis.encoding.Deserializer)this.deserClass.newInstance();
/*     */     }
/*     */     catch (Exception e) {
/*  98 */       throw new JAXRPCException(e);
/*     */     }
/* 100 */     return deser;
/*     */   }
/*     */ 
/*     */   protected org.apache.axis.encoding.Deserializer getGeneralPurpose(String mechanismType)
/*     */   {
/* 108 */     if ((this.javaType != null) && (this.xmlType != null)) {
/* 109 */       Constructor deserClassConstructor = getDeserClassConstructor();
/* 110 */       if (deserClassConstructor != null) {
/*     */         try {
/* 112 */           return (org.apache.axis.encoding.Deserializer)deserClassConstructor.newInstance(new Object[] { this.javaType, this.xmlType });
/*     */         }
/*     */         catch (InstantiationException e)
/*     */         {
/* 116 */           if (log.isDebugEnabled())
/* 117 */             log.debug(org.apache.axis.utils.Messages.getMessage("exception00"), e);
/*     */         }
/*     */         catch (IllegalAccessException e) {
/* 120 */           if (log.isDebugEnabled())
/* 121 */             log.debug(org.apache.axis.utils.Messages.getMessage("exception00"), e);
/*     */         }
/*     */         catch (InvocationTargetException e) {
/* 124 */           if (log.isDebugEnabled()) {
/* 125 */             log.debug(org.apache.axis.utils.Messages.getMessage("exception00"), e);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 130 */     return null;
/*     */   }
/*     */ 
/*     */   private Constructor getConstructor(Class clazz)
/*     */   {
/*     */     try
/*     */     {
/* 140 */       return clazz.getConstructor(CLASS_QNAME_CLASS); } catch (NoSuchMethodException e) {
/*     */     }
/* 142 */     return null;
/*     */   }
/*     */ 
/*     */   protected org.apache.axis.encoding.Deserializer getSpecialized(String mechanismType)
/*     */   {
/* 150 */     if ((this.javaType != null) && (this.xmlType != null)) {
/* 151 */       Method getDeserializer = getGetDeserializer();
/* 152 */       if (getDeserializer != null) {
/*     */         try {
/* 154 */           return (org.apache.axis.encoding.Deserializer)getDeserializer.invoke(null, new Object[] { mechanismType, this.javaType, this.xmlType });
/*     */         }
/*     */         catch (IllegalAccessException e)
/*     */         {
/* 161 */           if (log.isDebugEnabled())
/* 162 */             log.debug(org.apache.axis.utils.Messages.getMessage("exception00"), e);
/*     */         }
/*     */         catch (InvocationTargetException e) {
/* 165 */           if (log.isDebugEnabled()) {
/* 166 */             log.debug(org.apache.axis.utils.Messages.getMessage("exception00"), e);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 171 */     return null;
/*     */   }
/*     */ 
/*     */   public Iterator getSupportedMechanismTypes()
/*     */   {
/* 180 */     if (mechanisms == null) {
/* 181 */       mechanisms = new Vector(1);
/* 182 */       mechanisms.add("Axis SAX Mechanism");
/*     */     }
/* 184 */     return mechanisms.iterator();
/*     */   }
/*     */ 
/*     */   public static DeserializerFactory createFactory(Class factory, Class javaType, QName xmlType)
/*     */   {
/* 200 */     if (factory == null) {
/* 201 */       return null;
/*     */     }
/*     */     try
/*     */     {
/* 205 */       if (factory == BeanDeserializerFactory.class)
/* 206 */         return new BeanDeserializerFactory(javaType, xmlType);
/* 207 */       if (factory == SimpleDeserializerFactory.class)
/* 208 */         return new SimpleDeserializerFactory(javaType, xmlType);
/* 209 */       if (factory == EnumDeserializerFactory.class)
/* 210 */         return new EnumDeserializerFactory(javaType, xmlType);
/* 211 */       if (factory == ElementDeserializerFactory.class)
/* 212 */         return new ElementDeserializerFactory();
/* 213 */       if (factory == SimpleListDeserializerFactory.class)
/* 214 */         return new SimpleListDeserializerFactory(javaType, xmlType);
/*     */     }
/*     */     catch (Exception e) {
/* 217 */       if (log.isDebugEnabled()) {
/* 218 */         log.debug(org.apache.axis.utils.Messages.getMessage("exception00"), e);
/*     */       }
/* 220 */       return null;
/*     */     }
/*     */ 
/* 223 */     DeserializerFactory df = null;
/*     */     try {
/* 225 */       Method method = factory.getMethod("create", CLASS_QNAME_CLASS);
/*     */ 
/* 227 */       df = (DeserializerFactory)method.invoke(null, new Object[] { javaType, xmlType });
/*     */     }
/*     */     catch (NoSuchMethodException e)
/*     */     {
/* 231 */       if (log.isDebugEnabled())
/* 232 */         log.debug(org.apache.axis.utils.Messages.getMessage("exception00"), e);
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 235 */       if (log.isDebugEnabled())
/* 236 */         log.debug(org.apache.axis.utils.Messages.getMessage("exception00"), e);
/*     */     }
/*     */     catch (InvocationTargetException e) {
/* 239 */       if (log.isDebugEnabled()) {
/* 240 */         log.debug(org.apache.axis.utils.Messages.getMessage("exception00"), e);
/*     */       }
/*     */     }
/*     */ 
/* 244 */     if (df == null) {
/*     */       try {
/* 246 */         Constructor constructor = factory.getConstructor(CLASS_QNAME_CLASS);
/*     */ 
/* 248 */         df = (DeserializerFactory)constructor.newInstance(new Object[] { javaType, xmlType });
/*     */       }
/*     */       catch (NoSuchMethodException e)
/*     */       {
/* 252 */         if (log.isDebugEnabled())
/* 253 */           log.debug(org.apache.axis.utils.Messages.getMessage("exception00"), e);
/*     */       }
/*     */       catch (InstantiationException e) {
/* 256 */         if (log.isDebugEnabled())
/* 257 */           log.debug(org.apache.axis.utils.Messages.getMessage("exception00"), e);
/*     */       }
/*     */       catch (IllegalAccessException e) {
/* 260 */         if (log.isDebugEnabled())
/* 261 */           log.debug(org.apache.axis.utils.Messages.getMessage("exception00"), e);
/*     */       }
/*     */       catch (InvocationTargetException e) {
/* 264 */         if (log.isDebugEnabled()) {
/* 265 */           log.debug(org.apache.axis.utils.Messages.getMessage("exception00"), e);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 270 */     if (df == null)
/*     */       try {
/* 272 */         df = (DeserializerFactory)factory.newInstance();
/*     */       } catch (InstantiationException e) {
/*     */       } catch (IllegalAccessException e) {
/*     */       }
/* 276 */     return df;
/*     */   }
/*     */ 
/*     */   protected Constructor getDeserClassConstructor()
/*     */   {
/* 283 */     if (this.deserClassConstructor == null) {
/* 284 */       this.deserClassConstructor = getConstructor(this.deserClass);
/*     */     }
/* 286 */     return this.deserClassConstructor;
/*     */   }
/*     */ 
/*     */   protected Method getGetDeserializer()
/*     */   {
/* 294 */     if (this.getDeserializer == null) {
/* 295 */       this.getDeserializer = getMethod(this.javaType, "getDeserializer");
/*     */     }
/* 297 */     return this.getDeserializer;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.BaseDeserializerFactory
 * JD-Core Version:    0.6.0
 */