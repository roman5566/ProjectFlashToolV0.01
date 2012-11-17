/*    */ package org.apache.axis.components.net;
/*    */ 
/*    */ import org.apache.axis.AxisProperties;
/*    */ 
/*    */ public class DefaultHTTPSTransportClientProperties extends DefaultHTTPTransportClientProperties
/*    */ {
/*    */   public String getProxyHost()
/*    */   {
/* 31 */     if (this.proxyHost == null) {
/* 32 */       this.proxyHost = AxisProperties.getProperty("https.proxyHost");
/* 33 */       super.getProxyHost();
/*    */     }
/* 35 */     return this.proxyHost;
/*    */   }
/*    */ 
/*    */   public String getNonProxyHosts()
/*    */   {
/* 42 */     if (this.nonProxyHosts == null) {
/* 43 */       this.nonProxyHosts = AxisProperties.getProperty("https.nonProxyHosts");
/* 44 */       super.getNonProxyHosts();
/*    */     }
/* 46 */     return this.nonProxyHosts;
/*    */   }
/*    */ 
/*    */   public String getProxyPort()
/*    */   {
/* 53 */     if (this.proxyPort == null) {
/* 54 */       this.proxyPort = AxisProperties.getProperty("https.proxyPort");
/* 55 */       super.getProxyPort();
/*    */     }
/* 57 */     return this.proxyPort;
/*    */   }
/*    */ 
/*    */   public String getProxyUser()
/*    */   {
/* 64 */     if (this.proxyUser == null) {
/* 65 */       this.proxyUser = AxisProperties.getProperty("https.proxyUser");
/* 66 */       super.getProxyUser();
/*    */     }
/* 68 */     return this.proxyUser;
/*    */   }
/*    */ 
/*    */   public String getProxyPassword()
/*    */   {
/* 75 */     if (this.proxyPassword == null) {
/* 76 */       this.proxyPassword = AxisProperties.getProperty("https.proxyPassword");
/* 77 */       super.getProxyPassword();
/*    */     }
/* 79 */     return this.proxyPassword;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.net.DefaultHTTPSTransportClientProperties
 * JD-Core Version:    0.6.0
 */