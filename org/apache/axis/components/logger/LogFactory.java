/*    */ package org.apache.axis.components.logger;
/*    */ 
/*    */ import java.security.AccessController;
/*    */ import java.security.PrivilegedAction;
/*    */ import org.apache.commons.discovery.tools.DiscoverSingleton;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class LogFactory
/*    */ {
/* 33 */   private static final org.apache.commons.logging.LogFactory logFactory = getLogFactory();
/*    */ 
/*    */   public static Log getLog(String name)
/*    */   {
/* 37 */     return org.apache.commons.logging.LogFactory.getLog(name);
/*    */   }
/*    */ 
/*    */   private static final org.apache.commons.logging.LogFactory getLogFactory() {
/* 41 */     return (org.apache.commons.logging.LogFactory)AccessController.doPrivileged(new PrivilegedAction()
/*    */     {
/*    */       public Object run()
/*    */       {
/* 45 */         return DiscoverSingleton.find(org.apache.commons.logging.LogFactory.class, "commons-logging.properties", "org.apache.commons.logging.impl.LogFactoryImpl");
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.logger.LogFactory
 * JD-Core Version:    0.6.0
 */