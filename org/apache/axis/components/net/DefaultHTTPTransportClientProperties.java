/*    */ package org.apache.axis.components.net;
/*    */ 
/*    */ import org.apache.axis.AxisProperties;
/*    */ 
/*    */ public class DefaultHTTPTransportClientProperties
/*    */   implements TransportClientProperties
/*    */ {
/*    */   private static final String emptyString = "";
/* 29 */   protected String proxyHost = null;
/* 30 */   protected String nonProxyHosts = null;
/* 31 */   protected String proxyPort = null;
/* 32 */   protected String proxyUser = null;
/* 33 */   protected String proxyPassword = null;
/*    */ 
/*    */   public String getProxyHost()
/*    */   {
/* 40 */     if (this.proxyHost == null) {
/* 41 */       this.proxyHost = AxisProperties.getProperty("http.proxyHost");
/* 42 */       if (this.proxyHost == null)
/* 43 */         this.proxyHost = "";
/*    */     }
/* 45 */     return this.proxyHost;
/*    */   }
/*    */ 
/*    */   public String getNonProxyHosts()
/*    */   {
/* 52 */     if (this.nonProxyHosts == null) {
/* 53 */       this.nonProxyHosts = AxisProperties.getProperty("http.nonProxyHosts");
/* 54 */       if (this.nonProxyHosts == null)
/* 55 */         this.nonProxyHosts = "";
/*    */     }
/* 57 */     return this.nonProxyHosts;
/*    */   }
/*    */ 
/*    */   public String getProxyPort()
/*    */   {
/* 64 */     if (this.proxyPort == null) {
/* 65 */       this.proxyPort = AxisProperties.getProperty("http.proxyPort");
/* 66 */       if (this.proxyPort == null)
/* 67 */         this.proxyPort = "";
/*    */     }
/* 69 */     return this.proxyPort;
/*    */   }
/*    */ 
/*    */   public String getProxyUser()
/*    */   {
/* 76 */     if (this.proxyUser == null) {
/* 77 */       this.proxyUser = AxisProperties.getProperty("http.proxyUser");
/* 78 */       if (this.proxyUser == null)
/* 79 */         this.proxyUser = "";
/*    */     }
/* 81 */     return this.proxyUser;
/*    */   }
/*    */ 
/*    */   public String getProxyPassword()
/*    */   {
/* 88 */     if (this.proxyPassword == null) {
/* 89 */       this.proxyPassword = AxisProperties.getProperty("http.proxyPassword");
/* 90 */       if (this.proxyPassword == null)
/* 91 */         this.proxyPassword = "";
/*    */     }
/* 93 */     return this.proxyPassword;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.net.DefaultHTTPTransportClientProperties
 * JD-Core Version:    0.6.0
 */