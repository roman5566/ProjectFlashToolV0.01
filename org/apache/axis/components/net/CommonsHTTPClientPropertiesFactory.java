/*    */ package org.apache.axis.components.net;
/*    */ 
/*    */ import org.apache.axis.AxisProperties;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class CommonsHTTPClientPropertiesFactory
/*    */ {
/* 26 */   protected static Log log = LogFactory.getLog(CommonsHTTPClientPropertiesFactory.class.getName());
/*    */   private static CommonsHTTPClientProperties properties;
/*    */ 
/*    */   public static synchronized CommonsHTTPClientProperties create()
/*    */   {
/* 32 */     if (properties == null) {
/* 33 */       properties = (CommonsHTTPClientProperties)AxisProperties.newInstance(CommonsHTTPClientProperties.class, DefaultCommonsHTTPClientProperties.class);
/*    */     }
/*    */ 
/* 37 */     return properties;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.net.CommonsHTTPClientPropertiesFactory
 * JD-Core Version:    0.6.0
 */