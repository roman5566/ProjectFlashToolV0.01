/*    */ package org.apache.axis.security.servlet;
/*    */ 
/*    */ import java.security.Principal;
/*    */ import java.util.HashMap;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import org.apache.axis.MessageContext;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.security.AuthenticatedUser;
/*    */ import org.apache.axis.security.SecurityProvider;
/*    */ import org.apache.axis.transport.http.HTTPConstants;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class ServletSecurityProvider
/*    */   implements SecurityProvider
/*    */ {
/* 44 */   protected static Log log = LogFactory.getLog(ServletSecurityProvider.class.getName());
/*    */ 
/* 47 */   static HashMap users = null;
/*    */ 
/*    */   public AuthenticatedUser authenticate(MessageContext msgContext)
/*    */   {
/* 56 */     HttpServletRequest req = (HttpServletRequest)msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
/*    */ 
/* 59 */     if (req == null) {
/* 60 */       return null;
/*    */     }
/* 62 */     log.debug(Messages.getMessage("got00", "HttpServletRequest"));
/*    */ 
/* 64 */     Principal principal = req.getUserPrincipal();
/* 65 */     if (principal == null) {
/* 66 */       log.debug(Messages.getMessage("noPrincipal00"));
/* 67 */       return null;
/*    */     }
/*    */ 
/* 70 */     log.debug(Messages.getMessage("gotPrincipal00", principal.getName()));
/*    */ 
/* 72 */     return new ServletAuthenticatedUser(req);
/*    */   }
/*    */ 
/*    */   public boolean userMatches(AuthenticatedUser user, String principal)
/*    */   {
/* 81 */     if (user == null) return principal == null;
/*    */ 
/* 83 */     if ((user instanceof ServletAuthenticatedUser)) {
/* 84 */       ServletAuthenticatedUser servletUser = (ServletAuthenticatedUser)user;
/* 85 */       return servletUser.getRequest().isUserInRole(principal);
/*    */     }
/*    */ 
/* 88 */     return false;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.security.servlet.ServletSecurityProvider
 * JD-Core Version:    0.6.0
 */