/*    */ package org.apache.axis.utils;
/*    */ 
/*    */ import java.net.InetAddress;
/*    */ import java.net.UnknownHostException;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class NetworkUtils
/*    */ {
/*    */   public static final String LOCALHOST = "127.0.0.1";
/*    */   public static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
/* 49 */   protected static Log log = LogFactory.getLog(NetworkUtils.class.getName());
/*    */ 
/*    */   public static String getLocalHostname()
/*    */   {
/*    */     String hostname;
/*    */     try
/*    */     {
/* 65 */       InetAddress address = InetAddress.getLocalHost();
/*    */ 
/* 67 */       String hostname = address.getHostName();
/* 68 */       if ((hostname == null) || (hostname.length() == 0)) {
/* 69 */         hostname = address.toString();
/*    */       }
/*    */ 
/*    */     }
/*    */     catch (UnknownHostException noIpAddrException)
/*    */     {
/* 75 */       if (log.isDebugEnabled()) {
/* 76 */         log.debug("Failed to lookup local IP address", noIpAddrException);
/*    */       }
/* 78 */       hostname = "127.0.0.1";
/*    */     }
/* 80 */     return hostname;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.NetworkUtils
 * JD-Core Version:    0.6.0
 */