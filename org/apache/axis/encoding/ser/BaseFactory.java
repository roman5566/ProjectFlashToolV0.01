/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.utils.cache.MethodCache;
/*    */ 
/*    */ public abstract class BaseFactory
/*    */ {
/* 32 */   private static final Class[] STRING_CLASS_QNAME_CLASS = { String.class, Class.class, QName.class };
/*    */ 
/*    */   protected Method getMethod(Class clazz, String methodName)
/*    */   {
/* 40 */     Method method = null;
/*    */     try {
/* 42 */       method = MethodCache.getInstance().getMethod(clazz, methodName, STRING_CLASS_QNAME_CLASS);
/*    */     } catch (NoSuchMethodException e) {
/*    */     }
/* 45 */     return method;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.BaseFactory
 * JD-Core Version:    0.6.0
 */