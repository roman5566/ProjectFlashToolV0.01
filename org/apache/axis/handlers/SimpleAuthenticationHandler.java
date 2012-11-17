/*    */ package org.apache.axis.handlers;
/*    */ 
/*    */ import org.apache.axis.AxisFault;
/*    */ import org.apache.axis.MessageContext;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.security.AuthenticatedUser;
/*    */ import org.apache.axis.security.SecurityProvider;
/*    */ import org.apache.axis.security.simple.SimpleSecurityProvider;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class SimpleAuthenticationHandler extends BasicHandler
/*    */ {
/* 41 */   protected static Log log = LogFactory.getLog(SimpleAuthenticationHandler.class.getName());
/*    */ 
/*    */   public void invoke(MessageContext msgContext)
/*    */     throws AxisFault
/*    */   {
/* 48 */     if (log.isDebugEnabled()) {
/* 49 */       log.debug("Enter: SimpleAuthenticationHandler::invoke");
/*    */     }
/*    */ 
/* 52 */     SecurityProvider provider = (SecurityProvider)msgContext.getProperty("securityProvider");
/* 53 */     if (provider == null) {
/* 54 */       provider = new SimpleSecurityProvider();
/* 55 */       msgContext.setProperty("securityProvider", provider);
/*    */     }
/*    */ 
/* 58 */     if (provider != null) {
/* 59 */       String userID = msgContext.getUsername();
/* 60 */       if (log.isDebugEnabled()) {
/* 61 */         log.debug(Messages.getMessage("user00", userID));
/*    */       }
/*    */ 
/* 65 */       if ((userID == null) || (userID.equals(""))) {
/* 66 */         throw new AxisFault("Server.Unauthenticated", Messages.getMessage("cantAuth00", userID), null, null);
/*    */       }
/*    */ 
/* 70 */       String passwd = msgContext.getPassword();
/* 71 */       if (log.isDebugEnabled()) {
/* 72 */         log.debug(Messages.getMessage("password00", passwd));
/*    */       }
/*    */ 
/* 75 */       AuthenticatedUser authUser = provider.authenticate(msgContext);
/*    */ 
/* 78 */       if (authUser == null) {
/* 79 */         throw new AxisFault("Server.Unauthenticated", Messages.getMessage("cantAuth01", userID), null, null);
/*    */       }
/*    */ 
/* 83 */       if (log.isDebugEnabled()) {
/* 84 */         log.debug(Messages.getMessage("auth00", userID));
/*    */       }
/*    */ 
/* 87 */       msgContext.setProperty("authenticatedUser", authUser);
/*    */     }
/*    */ 
/* 90 */     if (log.isDebugEnabled())
/* 91 */       log.debug("Exit: SimpleAuthenticationHandler::invoke");
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.handlers.SimpleAuthenticationHandler
 * JD-Core Version:    0.6.0
 */