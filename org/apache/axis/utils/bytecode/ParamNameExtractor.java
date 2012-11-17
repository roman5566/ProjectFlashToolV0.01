/*    */ package org.apache.axis.utils.bytecode;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.lang.reflect.Method;
/*    */ import java.lang.reflect.Proxy;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class ParamNameExtractor
/*    */ {
/* 34 */   protected static Log log = LogFactory.getLog(ParamNameExtractor.class.getName());
/*    */ 
/*    */   public static String[] getParameterNamesFromDebugInfo(Method method)
/*    */   {
/* 44 */     int numParams = method.getParameterTypes().length;
/* 45 */     if (numParams == 0) {
/* 46 */       return null;
/*    */     }
/*    */ 
/* 49 */     Class c = method.getDeclaringClass();
/*    */ 
/* 52 */     if (Proxy.isProxyClass(c)) {
/* 53 */       return null;
/*    */     }
/*    */ 
/*    */     try
/*    */     {
/* 58 */       ParamReader pr = new ParamReader(c);
/*    */ 
/* 60 */       String[] names = pr.getParameterNames(method);
/* 61 */       return names;
/*    */     }
/*    */     catch (IOException e) {
/* 64 */       log.info(Messages.getMessage("error00") + ":" + e);
/* 65 */     }return null;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.bytecode.ParamNameExtractor
 * JD-Core Version:    0.6.0
 */