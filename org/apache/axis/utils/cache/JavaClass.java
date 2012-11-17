/*    */ package org.apache.axis.utils.cache;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.Hashtable;
/*    */ 
/*    */ public class JavaClass
/*    */   implements Serializable
/*    */ {
/* 30 */   private static Hashtable classes = new Hashtable();
/* 31 */   private Hashtable methods = new Hashtable();
/*    */   private Class jc;
/*    */ 
/*    */   public static synchronized JavaClass find(Class jc)
/*    */   {
/* 40 */     JavaClass result = (JavaClass)classes.get(jc);
/*    */ 
/* 42 */     if (result == null) {
/* 43 */       result = new JavaClass(jc);
/* 44 */       classes.put(jc, result);
/*    */     }
/*    */ 
/* 47 */     return result;
/*    */   }
/*    */ 
/*    */   public JavaClass(Class jc)
/*    */   {
/* 54 */     this.jc = jc;
/* 55 */     classes.put(jc, this);
/*    */   }
/*    */ 
/*    */   public Class getJavaClass()
/*    */   {
/* 62 */     return this.jc;
/*    */   }
/*    */ 
/*    */   public Method[] getMethod(String name)
/*    */   {
/* 71 */     JavaMethod jm = (JavaMethod)this.methods.get(name);
/*    */ 
/* 73 */     if (jm == null) {
/* 74 */       this.methods.put(name, jm = new JavaMethod(this.jc, name));
/*    */     }
/*    */ 
/* 77 */     return jm.getMethod();
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.cache.JavaClass
 * JD-Core Version:    0.6.0
 */