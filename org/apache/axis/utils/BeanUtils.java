/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import java.beans.BeanInfo;
/*     */ import java.beans.IntrospectionException;
/*     */ import java.beans.Introspector;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Vector;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.InternalException;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.description.FieldDesc;
/*     */ import org.apache.axis.description.TypeDesc;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class BeanUtils
/*     */ {
/*  38 */   public static final Object[] noArgs = new Object[0];
/*  39 */   protected static Log log = LogFactory.getLog(BeanUtils.class.getName());
/*     */ 
/*     */   public static BeanPropertyDescriptor[] getPd(Class javaType)
/*     */   {
/*  48 */     return getPd(javaType, null);
/*     */   }
/*     */ 
/*     */   public static BeanPropertyDescriptor[] getPd(Class javaType, TypeDesc typeDesc)
/*     */   {
/*     */     try
/*     */     {
/*  60 */       Class secJavaType = javaType;
/*     */ 
/*  63 */       PropertyDescriptor[] rawPd = getPropertyDescriptors(secJavaType);
/*  64 */       pd = processPropertyDescriptors(rawPd, javaType, typeDesc);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */       BeanPropertyDescriptor[] pd;
/*  67 */       throw new InternalException(e);
/*     */     }
/*     */     BeanPropertyDescriptor[] pd;
/*  69 */     return pd;
/*     */   }
/*     */ 
/*     */   private static PropertyDescriptor[] getPropertyDescriptors(Class secJavaType) {
/*  73 */     return (PropertyDescriptor[])AccessController.doPrivileged(new PrivilegedAction(secJavaType) { private final Class val$secJavaType;
/*     */ 
/*  76 */       public Object run() { PropertyDescriptor[] result = null;
/*     */         try
/*     */         {
/*  80 */           if (AxisFault.class.isAssignableFrom(this.val$secJavaType))
/*     */           {
/*  82 */             result = Introspector.getBeanInfo(this.val$secJavaType, AxisFault.class).getPropertyDescriptors();
/*     */           }
/*  85 */           else if ((Throwable.class != this.val$secJavaType) && (Throwable.class.isAssignableFrom(this.val$secJavaType)))
/*     */           {
/*  87 */             result = Introspector.getBeanInfo(this.val$secJavaType, Throwable.class).getPropertyDescriptors();
/*     */           }
/*     */           else
/*     */           {
/*  92 */             result = Introspector.getBeanInfo(this.val$secJavaType).getPropertyDescriptors();
/*     */           }
/*     */         }
/*     */         catch (IntrospectionException Iie)
/*     */         {
/*     */         }
/*     */ 
/*  99 */         return result;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public static Vector getBeanAttributes(Class javaType, TypeDesc typeDesc)
/*     */   {
/* 108 */     Vector ret = new Vector();
/*     */ 
/* 110 */     if (typeDesc == null)
/*     */     {
/*     */       try
/*     */       {
/* 116 */         Method getAttributeElements = javaType.getMethod("getAttributeElements", new Class[0]);
/*     */ 
/* 120 */         String[] array = (String[])getAttributeElements.invoke(null, noArgs);
/*     */ 
/* 123 */         ret = new Vector(array.length);
/* 124 */         for (int i = 0; i < array.length; i++)
/* 125 */           ret.add(array[i]);
/*     */       }
/*     */       catch (Exception e) {
/* 128 */         ret.clear();
/*     */       }
/*     */     } else {
/* 131 */       FieldDesc[] fields = typeDesc.getFields();
/* 132 */       if (fields != null) {
/* 133 */         for (int i = 0; i < fields.length; i++) {
/* 134 */           FieldDesc field = fields[i];
/* 135 */           if (!field.isElement()) {
/* 136 */             ret.add(field.getFieldName());
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 142 */     return ret;
/*     */   }
/*     */ 
/*     */   public static BeanPropertyDescriptor[] processPropertyDescriptors(PropertyDescriptor[] rawPd, Class cls)
/*     */   {
/* 156 */     return processPropertyDescriptors(rawPd, cls, null);
/*     */   }
/*     */ 
/*     */   public static BeanPropertyDescriptor[] processPropertyDescriptors(PropertyDescriptor[] rawPd, Class cls, TypeDesc typeDesc)
/*     */   {
/* 163 */     BeanPropertyDescriptor[] myPd = new BeanPropertyDescriptor[rawPd.length];
/*     */ 
/* 165 */     ArrayList pd = new ArrayList();
/*     */     try
/*     */     {
/* 168 */       for (int i = 0; i < rawPd.length; i++)
/*     */       {
/* 170 */         if (rawPd[i].getName().equals("_any"))
/*     */           continue;
/* 172 */         pd.add(new BeanPropertyDescriptor(rawPd[i]));
/*     */       }
/*     */ 
/* 176 */       Field[] fields = cls.getFields();
/* 177 */       if ((fields != null) && (fields.length > 0))
/*     */       {
/* 180 */         for (int i = 0; i < fields.length; i++) {
/* 181 */           Field f = fields[i];
/*     */ 
/* 185 */           String clsName = f.getDeclaringClass().getName();
/* 186 */           if ((clsName.startsWith("java.")) || (clsName.startsWith("javax.")))
/*     */           {
/*     */             continue;
/*     */           }
/*     */ 
/* 191 */           if ((Modifier.isStatic(f.getModifiers())) || (Modifier.isFinal(f.getModifiers())) || (Modifier.isTransient(f.getModifiers()))) {
/*     */             continue;
/*     */           }
/* 194 */           String fName = f.getName();
/* 195 */           boolean found = false;
/* 196 */           for (int j = 0; (j < rawPd.length) && (!found); j++) {
/* 197 */             String pName = ((BeanPropertyDescriptor)pd.get(j)).getName();
/*     */ 
/* 199 */             if ((pName.length() != fName.length()) || (!pName.substring(0, 1).equalsIgnoreCase(fName.substring(0, 1))))
/*     */             {
/*     */               continue;
/*     */             }
/* 203 */             found = (pName.length() == 1) || (pName.substring(1).equals(fName.substring(1)));
/*     */           }
/*     */ 
/* 208 */           if (!found) {
/* 209 */             pd.add(new FieldPropertyDescriptor(f.getName(), f));
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 216 */       if ((typeDesc != null) && (typeDesc.getFields(true) != null))
/*     */       {
/* 218 */         ArrayList ordered = new ArrayList();
/*     */ 
/* 220 */         FieldDesc[] fds = typeDesc.getFields(true);
/* 221 */         for (int i = 0; i < fds.length; i++) {
/* 222 */           FieldDesc field = fds[i];
/* 223 */           if (field.isElement()) {
/* 224 */             boolean found = false;
/* 225 */             int j = 0;
/* 226 */             while ((j < pd.size()) && (!found))
/*     */             {
/* 228 */               if (field.getFieldName().equals(((BeanPropertyDescriptor)pd.get(j)).getName()))
/*     */               {
/* 230 */                 ordered.add(pd.remove(j));
/* 231 */                 found = true;
/*     */               }
/* 227 */               j++;
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 237 */         while (pd.size() > 0) {
/* 238 */           ordered.add(pd.remove(0));
/*     */         }
/*     */ 
/* 241 */         pd = ordered;
/*     */       }
/*     */ 
/* 244 */       myPd = new BeanPropertyDescriptor[pd.size()];
/* 245 */       for (int i = 0; i < pd.size(); i++)
/* 246 */         myPd[i] = ((BeanPropertyDescriptor)pd.get(i));
/*     */     }
/*     */     catch (Exception e) {
/* 249 */       log.error(Messages.getMessage("badPropertyDesc00", cls.getName()), e);
/*     */ 
/* 251 */       throw new InternalException(e);
/*     */     }
/*     */ 
/* 254 */     return myPd;
/*     */   }
/*     */ 
/*     */   public static BeanPropertyDescriptor getAnyContentPD(Class javaType) {
/* 258 */     PropertyDescriptor[] pds = getPropertyDescriptors(javaType);
/* 259 */     return getSpecificPD(pds, "_any");
/*     */   }
/*     */ 
/*     */   public static BeanPropertyDescriptor getSpecificPD(PropertyDescriptor[] pds, String name)
/*     */   {
/* 264 */     for (int i = 0; i < pds.length; i++) {
/* 265 */       PropertyDescriptor pd = pds[i];
/* 266 */       if (pd.getName().equals(name))
/* 267 */         return new BeanPropertyDescriptor(pd);
/*     */     }
/* 269 */     return null;
/*     */   }
/*     */ 
/*     */   public static BeanPropertyDescriptor getSpecificPD(BeanPropertyDescriptor[] pds, String name)
/*     */   {
/* 274 */     for (int i = 0; i < pds.length; i++) {
/* 275 */       BeanPropertyDescriptor pd = pds[i];
/* 276 */       if (pd.getName().equals(name))
/* 277 */         return pd;
/*     */     }
/* 279 */     return null;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.BeanUtils
 * JD-Core Version:    0.6.0
 */