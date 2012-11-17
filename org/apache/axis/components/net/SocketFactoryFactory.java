/*    */ package org.apache.axis.components.net;
/*    */ 
/*    */ import java.util.Hashtable;
/*    */ import org.apache.axis.AxisProperties;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class SocketFactoryFactory
/*    */ {
/* 33 */   protected static Log log = LogFactory.getLog(SocketFactoryFactory.class.getName());
/*    */ 
/* 37 */   private static Hashtable factories = new Hashtable();
/*    */ 
/* 39 */   private static final Class[] classes = { Hashtable.class };
/*    */ 
/*    */   public static synchronized SocketFactory getFactory(String protocol, Hashtable attributes)
/*    */   {
/* 66 */     SocketFactory theFactory = (SocketFactory)factories.get(protocol);
/*    */ 
/* 68 */     if (theFactory == null) {
/* 69 */       Object[] objects = { attributes };
/*    */ 
/* 71 */       if (protocol.equalsIgnoreCase("http")) {
/* 72 */         theFactory = (SocketFactory)AxisProperties.newInstance(SocketFactory.class, classes, objects);
/*    */       }
/* 74 */       else if (protocol.equalsIgnoreCase("https")) {
/* 75 */         theFactory = (SecureSocketFactory)AxisProperties.newInstance(SecureSocketFactory.class, classes, objects);
/*    */       }
/*    */ 
/* 79 */       if (theFactory != null) {
/* 80 */         factories.put(protocol, theFactory);
/*    */       }
/*    */     }
/* 83 */     return theFactory;
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 43 */     AxisProperties.setClassOverrideProperty(SocketFactory.class, "axis.socketFactory");
/*    */ 
/* 46 */     AxisProperties.setClassDefault(SocketFactory.class, "org.apache.axis.components.net.DefaultSocketFactory");
/*    */ 
/* 49 */     AxisProperties.setClassOverrideProperty(SecureSocketFactory.class, "axis.socketSecureFactory");
/*    */ 
/* 52 */     AxisProperties.setClassDefault(SecureSocketFactory.class, "org.apache.axis.components.net.JSSESocketFactory");
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.net.SocketFactoryFactory
 * JD-Core Version:    0.6.0
 */