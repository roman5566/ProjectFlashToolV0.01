/*    */ package org.apache.axis.utils.cache;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.Vector;
/*    */ 
/*    */ public class JavaMethod
/*    */ {
/* 30 */   private Method[] methods = null;
/*    */ 
/*    */   public JavaMethod(Class jc, String name)
/*    */   {
/* 38 */     Method[] methods = jc.getMethods();
/* 39 */     Vector workinglist = new Vector();
/*    */ 
/* 43 */     for (int i = 0; i < methods.length; i++) {
/* 44 */       if (methods[i].getName().equals(name)) {
/* 45 */         workinglist.addElement(methods[i]);
/*    */       }
/*    */ 
/*    */     }
/*    */ 
/* 50 */     if (workinglist.size() > 0) {
/* 51 */       this.methods = new Method[workinglist.size()];
/* 52 */       workinglist.copyInto(this.methods);
/*    */     }
/*    */   }
/*    */ 
/*    */   public Method[] getMethod()
/*    */   {
/* 62 */     return this.methods;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.cache.JavaMethod
 * JD-Core Version:    0.6.0
 */