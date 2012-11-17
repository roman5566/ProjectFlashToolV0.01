/*    */ package org.apache.axis.utils.cache;
/*    */ 
/*    */ import java.util.Hashtable;
/*    */ import org.apache.axis.utils.ClassUtils;
/*    */ 
/*    */ public class ClassCache
/*    */ {
/* 31 */   Hashtable classCache = new Hashtable();
/*    */ 
/*    */   public synchronized void registerClass(String name, Class cls)
/*    */   {
/* 45 */     if (name == null) return;
/* 46 */     JavaClass oldClass = (JavaClass)this.classCache.get(name);
/* 47 */     if ((oldClass != null) && (oldClass.getJavaClass() == cls)) return;
/* 48 */     this.classCache.put(name, new JavaClass(cls));
/*    */   }
/*    */ 
/*    */   public synchronized void deregisterClass(String name)
/*    */   {
/* 57 */     this.classCache.remove(name);
/*    */   }
/*    */ 
/*    */   public boolean isClassRegistered(String name)
/*    */   {
/* 67 */     return (this.classCache != null) && (this.classCache.get(name) != null);
/*    */   }
/*    */ 
/*    */   public JavaClass lookup(String className, ClassLoader cl)
/*    */     throws ClassNotFoundException
/*    */   {
/* 79 */     if (className == null) {
/* 80 */       return null;
/*    */     }
/* 82 */     JavaClass jc = (JavaClass)this.classCache.get(className);
/* 83 */     if ((jc == null) && (cl != null))
/*    */     {
/* 85 */       Class cls = ClassUtils.forName(className, true, cl);
/* 86 */       jc = new JavaClass(cls);
/*    */     }
/* 88 */     return jc;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.cache.ClassCache
 * JD-Core Version:    0.6.0
 */