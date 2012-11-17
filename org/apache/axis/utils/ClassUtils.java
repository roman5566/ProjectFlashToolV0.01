/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Hashtable;
/*     */ 
/*     */ public final class ClassUtils
/*     */ {
/*  34 */   private static ClassLoader defaultClassLoader = ClassUtils.class.getClassLoader();
/*     */ 
/*  38 */   private static Hashtable classloaders = new Hashtable();
/*     */ 
/*     */   public static void setDefaultClassLoader(ClassLoader loader)
/*     */   {
/*  47 */     if (loader != null)
/*  48 */       defaultClassLoader = loader;
/*     */   }
/*     */ 
/*     */   public static ClassLoader getDefaultClassLoader() {
/*  52 */     return defaultClassLoader;
/*     */   }
/*     */ 
/*     */   public static void setClassLoader(String className, ClassLoader loader)
/*     */   {
/*  63 */     if ((className != null) && (loader != null))
/*  64 */       classloaders.put(className, loader);
/*     */   }
/*     */ 
/*     */   public static ClassLoader getClassLoader(String className)
/*     */   {
/*  75 */     if (className == null) {
/*  76 */       return null;
/*     */     }
/*  78 */     return (ClassLoader)classloaders.get(className);
/*     */   }
/*     */ 
/*     */   public static void removeClassLoader(String className)
/*     */   {
/*  87 */     classloaders.remove(className);
/*     */   }
/*     */ 
/*     */   public static Class forName(String className)
/*     */     throws ClassNotFoundException
/*     */   {
/* 100 */     return loadClass(className);
/*     */   }
/*     */ 
/*     */   public static Class forName(String _className, boolean init, ClassLoader _loader)
/*     */     throws ClassNotFoundException
/*     */   {
/* 118 */     String className = _className;
/* 119 */     ClassLoader loader = _loader;
/*     */     try
/*     */     {
/* 122 */       Object ret = AccessController.doPrivileged(new PrivilegedAction(className, loader) { private final String val$className;
/*     */         private final ClassLoader val$loader;
/*     */ 
/*     */         public Object run() { try { return Class.forName(this.val$className, true, this.val$loader); } catch (Throwable e) {
/*     */           }
/* 129 */           return e;
/*     */         }
/*     */       });
/* 134 */       if ((ret instanceof Class))
/* 135 */         return (Class)ret;
/* 136 */       if ((ret instanceof ClassNotFoundException)) {
/* 137 */         throw ((ClassNotFoundException)ret);
/*     */       }
/* 139 */       throw new ClassNotFoundException(_className);
/*     */     } catch (ClassNotFoundException cnfe) {
/*     */     }
/* 142 */     return loadClass(className);
/*     */   }
/*     */ 
/*     */   private static Class loadClass(String _className)
/*     */     throws ClassNotFoundException
/*     */   {
/* 157 */     String className = _className;
/*     */ 
/* 160 */     Object ret = AccessController.doPrivileged(new PrivilegedAction(className)
/*     */     {
/*     */       private final String val$className;
/*     */ 
/*     */       public Object run() {
/*     */         try {
/* 167 */           ClassLoader classLoader = ClassUtils.getClassLoader(this.val$className);
/* 168 */           return Class.forName(this.val$className, true, classLoader);
/*     */         }
/*     */         catch (ClassNotFoundException cnfe) {
/*     */         }
/*     */         catch (SecurityException cnfe) {
/*     */         }
/*     */         try {
/* 175 */           ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
/*     */ 
/* 177 */           return Class.forName(this.val$className, true, classLoader);
/*     */         }
/*     */         catch (ClassNotFoundException cnfe2) {
/*     */           try {
/* 181 */             ClassLoader classLoader = ClassUtils.class.getClassLoader();
/*     */ 
/* 183 */             return Class.forName(this.val$className, true, classLoader);
/*     */           }
/*     */           catch (ClassNotFoundException cnfe3) {
/*     */             try {
/* 187 */               return ClassUtils.defaultClassLoader.loadClass(this.val$className); } catch (Throwable e) {
/*     */             }
/*     */           }
/*     */         }
/* 191 */         return e;
/*     */       }
/*     */     });
/* 199 */     if ((ret instanceof Class))
/* 200 */       return (Class)ret;
/* 201 */     if ((ret instanceof ClassNotFoundException)) {
/* 202 */       throw ((ClassNotFoundException)ret);
/*     */     }
/* 204 */     throw new ClassNotFoundException(_className);
/*     */   }
/*     */ 
/*     */   public static InputStream getResourceAsStream(Class clazz, String resource, boolean checkThreadContextFirst)
/*     */   {
/* 222 */     InputStream myInputStream = null;
/*     */ 
/* 224 */     if ((checkThreadContextFirst) && (Thread.currentThread().getContextClassLoader() != null))
/*     */     {
/* 227 */       myInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
/*     */     }
/*     */ 
/* 231 */     if (myInputStream == null)
/*     */     {
/* 233 */       myInputStream = getResourceAsStream(clazz, resource);
/*     */     }
/* 235 */     return myInputStream;
/*     */   }
/*     */ 
/*     */   public static InputStream getResourceAsStream(Class clazz, String resource)
/*     */   {
/* 251 */     InputStream myInputStream = null;
/*     */ 
/* 253 */     if (clazz.getClassLoader() != null)
/*     */     {
/* 255 */       myInputStream = clazz.getClassLoader().getResourceAsStream(resource);
/*     */     }
/*     */     else {
/* 258 */       myInputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(resource);
/*     */     }
/* 260 */     if ((myInputStream == null) && (Thread.currentThread().getContextClassLoader() != null))
/*     */     {
/* 262 */       myInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
/*     */     }
/* 264 */     if (myInputStream == null)
/*     */     {
/* 266 */       myInputStream = clazz.getResourceAsStream(resource);
/*     */     }
/* 268 */     return myInputStream;
/*     */   }
/*     */ 
/*     */   public static ClassLoader createClassLoader(String classpath, ClassLoader parent)
/*     */     throws SecurityException
/*     */   {
/* 289 */     String[] names = StringUtils.split(classpath, System.getProperty("path.separator").charAt(0));
/*     */ 
/* 291 */     URL[] urls = new URL[names.length];
/*     */     try {
/* 293 */       for (int i = 0; i < urls.length; i++) {
/* 294 */         urls[i] = new File(names[i]).toURL();
/*     */       }
/*     */     }
/*     */     catch (MalformedURLException e)
/*     */     {
/* 299 */       throw new IllegalArgumentException("Unable to parse classpath: " + classpath);
/*     */     }
/*     */ 
/* 303 */     return new URLClassLoader(urls, parent);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.ClassUtils
 * JD-Core Version:    0.6.0
 */