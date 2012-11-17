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
/*     */ import org.apache.axis.encoding.SerializerFactory;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public abstract class BaseSerializerFactory extends BaseFactory
/*     */   implements SerializerFactory
/*     */ {
/*  42 */   protected static Log log = LogFactory.getLog(BaseSerializerFactory.class.getName());
/*     */ 
/*  45 */   static transient Vector mechanisms = null;
/*     */ 
/*  47 */   protected Class serClass = null;
/*  48 */   protected QName xmlType = null;
/*  49 */   protected Class javaType = null;
/*     */ 
/*  51 */   protected transient org.apache.axis.encoding.Serializer ser = null;
/*  52 */   protected transient Constructor serClassConstructor = null;
/*  53 */   protected transient Method getSerializer = null;
/*     */ 
/* 144 */   private static final Class[] CLASS_QNAME_CLASS = { Class.class, QName.class };
/*     */ 
/*     */   public BaseSerializerFactory(Class serClass)
/*     */   {
/*  61 */     if (!org.apache.axis.encoding.Serializer.class.isAssignableFrom(serClass)) {
/*  62 */       throw new ClassCastException(Messages.getMessage("BadImplementation00", serClass.getName(), org.apache.axis.encoding.Serializer.class.getName()));
/*     */     }
/*     */ 
/*  67 */     this.serClass = serClass;
/*     */   }
/*     */ 
/*     */   public BaseSerializerFactory(Class serClass, QName xmlType, Class javaType)
/*     */   {
/*  72 */     this(serClass);
/*  73 */     this.xmlType = xmlType;
/*  74 */     this.javaType = javaType;
/*     */   }
/*     */ 
/*     */   public javax.xml.rpc.encoding.Serializer getSerializerAs(String mechanismType)
/*     */     throws JAXRPCException
/*     */   {
/*  80 */     synchronized (this) {
/*  81 */       if (this.ser == null) {
/*  82 */         this.ser = getSerializerAsInternal(mechanismType);
/*     */       }
/*  84 */       return this.ser;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected org.apache.axis.encoding.Serializer getSerializerAsInternal(String mechanismType)
/*     */     throws JAXRPCException
/*     */   {
/*  91 */     org.apache.axis.encoding.Serializer serializer = getSpecialized(mechanismType);
/*     */ 
/*  95 */     if (serializer == null) {
/*  96 */       serializer = getGeneralPurpose(mechanismType);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 101 */       if (serializer == null)
/* 102 */         serializer = (org.apache.axis.encoding.Serializer)this.serClass.newInstance();
/*     */     }
/*     */     catch (Exception e) {
/* 105 */       throw new JAXRPCException(Messages.getMessage("CantGetSerializer", this.serClass.getName()), e);
/*     */     }
/*     */ 
/* 110 */     return serializer;
/*     */   }
/*     */ 
/*     */   protected org.apache.axis.encoding.Serializer getGeneralPurpose(String mechanismType)
/*     */   {
/* 118 */     if ((this.javaType != null) && (this.xmlType != null)) {
/* 119 */       Constructor serClassConstructor = getSerClassConstructor();
/* 120 */       if (serClassConstructor != null) {
/*     */         try {
/* 122 */           return (org.apache.axis.encoding.Serializer)serClassConstructor.newInstance(new Object[] { this.javaType, this.xmlType });
/*     */         }
/*     */         catch (InstantiationException e)
/*     */         {
/* 126 */           if (log.isDebugEnabled())
/* 127 */             log.debug(Messages.getMessage("exception00"), e);
/*     */         }
/*     */         catch (IllegalAccessException e) {
/* 130 */           if (log.isDebugEnabled())
/* 131 */             log.debug(Messages.getMessage("exception00"), e);
/*     */         }
/*     */         catch (InvocationTargetException e) {
/* 134 */           if (log.isDebugEnabled()) {
/* 135 */             log.debug(Messages.getMessage("exception00"), e);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 140 */     return null;
/*     */   }
/*     */ 
/*     */   private Constructor getConstructor(Class clazz)
/*     */   {
/*     */     try
/*     */     {
/* 151 */       return clazz.getConstructor(CLASS_QNAME_CLASS); } catch (NoSuchMethodException e) {
/*     */     }
/* 153 */     return null;
/*     */   }
/*     */ 
/*     */   protected org.apache.axis.encoding.Serializer getSpecialized(String mechanismType)
/*     */   {
/* 161 */     if ((this.javaType != null) && (this.xmlType != null)) {
/* 162 */       Method getSerializer = getGetSerializer();
/* 163 */       if (getSerializer != null) {
/*     */         try {
/* 165 */           return (org.apache.axis.encoding.Serializer)getSerializer.invoke(null, new Object[] { mechanismType, this.javaType, this.xmlType });
/*     */         }
/*     */         catch (IllegalAccessException e)
/*     */         {
/* 172 */           if (log.isDebugEnabled())
/* 173 */             log.debug(Messages.getMessage("exception00"), e);
/*     */         }
/*     */         catch (InvocationTargetException e) {
/* 176 */           if (log.isDebugEnabled()) {
/* 177 */             log.debug(Messages.getMessage("exception00"), e);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 182 */     return null;
/*     */   }
/*     */ 
/*     */   public Iterator getSupportedMechanismTypes()
/*     */   {
/* 193 */     if (mechanisms == null) {
/* 194 */       mechanisms = new Vector(1);
/* 195 */       mechanisms.add("Axis SAX Mechanism");
/*     */     }
/* 197 */     return mechanisms.iterator();
/*     */   }
/*     */ 
/*     */   public QName getXMLType()
/*     */   {
/* 205 */     return this.xmlType;
/*     */   }
/*     */ 
/*     */   public Class getJavaType()
/*     */   {
/* 213 */     return this.javaType;
/*     */   }
/*     */ 
/*     */   public static SerializerFactory createFactory(Class factory, Class javaType, QName xmlType)
/*     */   {
/* 229 */     if (factory == null) {
/* 230 */       return null;
/*     */     }
/*     */     try
/*     */     {
/* 234 */       if (factory == BeanSerializerFactory.class)
/* 235 */         return new BeanSerializerFactory(javaType, xmlType);
/* 236 */       if (factory == SimpleSerializerFactory.class)
/* 237 */         return new SimpleSerializerFactory(javaType, xmlType);
/* 238 */       if (factory == EnumSerializerFactory.class)
/* 239 */         return new EnumSerializerFactory(javaType, xmlType);
/* 240 */       if (factory == ElementSerializerFactory.class)
/* 241 */         return new ElementSerializerFactory();
/* 242 */       if (factory == SimpleListSerializerFactory.class)
/* 243 */         return new SimpleListSerializerFactory(javaType, xmlType);
/*     */     }
/*     */     catch (Exception e) {
/* 246 */       if (log.isDebugEnabled()) {
/* 247 */         log.debug(Messages.getMessage("exception00"), e);
/*     */       }
/* 249 */       return null;
/*     */     }
/*     */ 
/* 252 */     SerializerFactory sf = null;
/*     */     try {
/* 254 */       Method method = factory.getMethod("create", CLASS_QNAME_CLASS);
/*     */ 
/* 256 */       sf = (SerializerFactory)method.invoke(null, new Object[] { javaType, xmlType });
/*     */     }
/*     */     catch (NoSuchMethodException e)
/*     */     {
/* 260 */       if (log.isDebugEnabled())
/* 261 */         log.debug(Messages.getMessage("exception00"), e);
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 264 */       if (log.isDebugEnabled())
/* 265 */         log.debug(Messages.getMessage("exception00"), e);
/*     */     }
/*     */     catch (InvocationTargetException e) {
/* 268 */       if (log.isDebugEnabled()) {
/* 269 */         log.debug(Messages.getMessage("exception00"), e);
/*     */       }
/*     */     }
/*     */ 
/* 273 */     if (sf == null) {
/*     */       try {
/* 275 */         Constructor constructor = factory.getConstructor(CLASS_QNAME_CLASS);
/*     */ 
/* 277 */         sf = (SerializerFactory)constructor.newInstance(new Object[] { javaType, xmlType });
/*     */       }
/*     */       catch (NoSuchMethodException e)
/*     */       {
/* 281 */         if (log.isDebugEnabled())
/* 282 */           log.debug(Messages.getMessage("exception00"), e);
/*     */       }
/*     */       catch (InstantiationException e) {
/* 285 */         if (log.isDebugEnabled())
/* 286 */           log.debug(Messages.getMessage("exception00"), e);
/*     */       }
/*     */       catch (IllegalAccessException e) {
/* 289 */         if (log.isDebugEnabled())
/* 290 */           log.debug(Messages.getMessage("exception00"), e);
/*     */       }
/*     */       catch (InvocationTargetException e) {
/* 293 */         if (log.isDebugEnabled()) {
/* 294 */           log.debug(Messages.getMessage("exception00"), e);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 299 */     if (sf == null)
/*     */       try {
/* 301 */         sf = (SerializerFactory)factory.newInstance();
/*     */       } catch (InstantiationException e) {
/*     */       } catch (IllegalAccessException e) {
/*     */       }
/* 305 */     return sf;
/*     */   }
/*     */ 
/*     */   protected Method getGetSerializer()
/*     */   {
/* 313 */     if (this.getSerializer == null) {
/* 314 */       this.getSerializer = getMethod(this.javaType, "getSerializer");
/*     */     }
/* 316 */     return this.getSerializer;
/*     */   }
/*     */ 
/*     */   protected Constructor getSerClassConstructor()
/*     */   {
/* 324 */     if (this.serClassConstructor == null) {
/* 325 */       this.serClassConstructor = getConstructor(this.serClass);
/*     */     }
/* 327 */     return this.serClassConstructor;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.BaseSerializerFactory
 * JD-Core Version:    0.6.0
 */