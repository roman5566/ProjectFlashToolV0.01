/*    */ package org.apache.axis.utils;
/*    */ 
/*    */ import java.net.Authenticator;
/*    */ import java.net.PasswordAuthentication;
/*    */ import org.apache.axis.components.net.TransportClientProperties;
/*    */ import org.apache.axis.components.net.TransportClientPropertiesFactory;
/*    */ 
/*    */ public class DefaultAuthenticator extends Authenticator
/*    */ {
/* 28 */   private TransportClientProperties tcp = null;
/*    */   private String user;
/*    */   private String password;
/*    */ 
/*    */   public DefaultAuthenticator(String user, String pass)
/*    */   {
/* 34 */     this.user = user;
/* 35 */     this.password = pass;
/*    */   }
/*    */ 
/*    */   protected PasswordAuthentication getPasswordAuthentication()
/*    */   {
/* 40 */     if (this.user == null) {
/* 41 */       this.user = getTransportClientProperties().getProxyUser();
/*    */     }
/* 43 */     if (this.password == null) {
/* 44 */       this.password = getTransportClientProperties().getProxyPassword();
/*    */     }
/* 46 */     return new PasswordAuthentication(this.user, this.password.toCharArray());
/*    */   }
/*    */ 
/*    */   private TransportClientProperties getTransportClientProperties() {
/* 50 */     if (this.tcp == null) {
/* 51 */       this.tcp = TransportClientPropertiesFactory.create("http");
/*    */     }
/* 53 */     return this.tcp;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.DefaultAuthenticator
 * JD-Core Version:    0.6.0
 */