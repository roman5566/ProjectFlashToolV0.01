/*     */ package org.apache.axis.utils.cache;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.apache.axis.utils.ClassUtils;
/*     */ 
/*     */ public class MethodCache
/*     */ {
/*     */   private static transient MethodCache instance;
/*     */   private static transient ThreadLocal cache;
/* 115 */   private static final Object NULL_OBJECT = new Object();
/*     */ 
/*     */   private MethodCache()
/*     */   {
/*  51 */     cache = new ThreadLocal();
/*     */   }
/*     */ 
/*     */   public static MethodCache getInstance()
/*     */   {
/*  59 */     if (instance == null) {
/*  60 */       instance = new MethodCache();
/*     */     }
/*  62 */     return instance;
/*     */   }
/*     */ 
/*     */   private Map getMethodCache()
/*     */   {
/*  69 */     Map map = (Map)cache.get();
/*  70 */     if (map == null) {
/*  71 */       map = new HashMap();
/*  72 */       cache.set(map);
/*     */     }
/*  74 */     return map;
/*     */   }
/*     */ 
/*     */   public Method getMethod(Class clazz, String methodName, Class[] parameterTypes)
/*     */     throws NoSuchMethodException
/*     */   {
/* 131 */     String className = clazz.getName();
/* 132 */     Map cache = getMethodCache();
/* 133 */     Method method = null;
/* 134 */     Map methods = null;
/*     */ 
/* 160 */     MethodKey key = new MethodKey(methodName, parameterTypes);
/* 161 */     methods = (Map)cache.get(clazz);
/* 162 */     if (methods != null) {
/* 163 */       Object o = methods.get(key);
/* 164 */       if (o != null) {
/* 165 */         if ((o instanceof Method)) {
/* 166 */           return (Method)o;
/*     */         }
/*     */ 
/* 172 */         return null;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 182 */       method = clazz.getMethod(methodName, parameterTypes);
/*     */     } catch (NoSuchMethodException e1) {
/* 184 */       if ((!clazz.isPrimitive()) && (!className.startsWith("java.")) && (!className.startsWith("javax."))) {
/*     */         try {
/* 186 */           Class helper = ClassUtils.forName(className + "_Helper");
/* 187 */           method = helper.getMethod(methodName, parameterTypes);
/*     */         }
/*     */         catch (ClassNotFoundException e2)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/* 194 */     if (methods == null) {
/* 195 */       methods = new HashMap();
/* 196 */       cache.put(clazz, methods);
/*     */     }
/*     */ 
/* 203 */     if (null == method)
/* 204 */       methods.put(key, NULL_OBJECT);
/*     */     else {
/* 206 */       methods.put(key, method);
/*     */     }
/* 208 */     return method;
/*     */   }
/*     */ 
/*     */   static class MethodKey
/*     */   {
/*     */     private final String methodName;
/*     */     private final Class[] parameterTypes;
/*     */ 
/*     */     MethodKey(String methodName, Class[] parameterTypes)
/*     */     {
/*  94 */       this.methodName = methodName;
/*  95 */       this.parameterTypes = parameterTypes;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object other) {
/*  99 */       MethodKey that = (MethodKey)other;
/* 100 */       return (this.methodName.equals(that.methodName)) && (Arrays.equals(this.parameterTypes, that.parameterTypes));
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 110 */       return this.methodName.hashCode();
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.cache.MethodCache
 * JD-Core Version:    0.6.0
 */