/*    */ package org.apache.axis.components.net;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import org.apache.axis.AxisProperties;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class TransportClientPropertiesFactory
/*    */ {
/* 29 */   protected static Log log = LogFactory.getLog(SocketFactoryFactory.class.getName());
/*    */ 
/* 32 */   private static HashMap cache = new HashMap();
/* 33 */   private static HashMap defaults = new HashMap();
/*    */ 
/*    */   public static TransportClientProperties create(String protocol)
/*    */   {
/* 42 */     TransportClientProperties tcp = (TransportClientProperties)cache.get(protocol);
/*    */ 
/* 45 */     if (tcp == null) {
/* 46 */       tcp = (TransportClientProperties)AxisProperties.newInstance(TransportClientProperties.class, (Class)defaults.get(protocol));
/*    */ 
/* 50 */       if (tcp != null) {
/* 51 */         cache.put(protocol, tcp);
/*    */       }
/*    */     }
/*    */ 
/* 55 */     return tcp;
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 36 */     defaults.put("http", DefaultHTTPTransportClientProperties.class);
/* 37 */     defaults.put("https", DefaultHTTPSTransportClientProperties.class);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.net.TransportClientPropertiesFactory
 * JD-Core Version:    0.6.0
 */