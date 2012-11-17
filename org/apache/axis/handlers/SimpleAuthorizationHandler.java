/*     */ package org.apache.axis.handlers;
/*     */ 
/*     */ import java.util.StringTokenizer;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Handler;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.security.AuthenticatedUser;
/*     */ import org.apache.axis.security.SecurityProvider;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class SimpleAuthorizationHandler extends BasicHandler
/*     */ {
/*  50 */   protected static Log log = LogFactory.getLog(SimpleAuthorizationHandler.class.getName());
/*     */ 
/*     */   public void invoke(MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/*  57 */     if (log.isDebugEnabled()) {
/*  58 */       log.debug("Enter: SimpleAuthorizationHandler::invoke");
/*     */     }
/*     */ 
/*  61 */     boolean allowByDefault = JavaUtils.isTrueExplicitly(getOption("allowByDefault"));
/*     */ 
/*  64 */     AuthenticatedUser user = (AuthenticatedUser)msgContext.getProperty("authenticatedUser");
/*     */ 
/*  67 */     if (user == null) {
/*  68 */       throw new AxisFault("Server.NoUser", Messages.getMessage("needUser00"), null, null);
/*     */     }
/*     */ 
/*  71 */     String userID = user.getName();
/*  72 */     Handler serviceHandler = msgContext.getService();
/*     */ 
/*  74 */     if (serviceHandler == null) {
/*  75 */       throw new AxisFault(Messages.getMessage("needService00"));
/*     */     }
/*  77 */     String serviceName = serviceHandler.getName();
/*     */ 
/*  79 */     String allowedRoles = (String)serviceHandler.getOption("allowedRoles");
/*  80 */     if (allowedRoles == null) {
/*  81 */       if (allowByDefault) {
/*  82 */         if (log.isDebugEnabled())
/*  83 */           log.debug(Messages.getMessage("noRoles00"));
/*     */       }
/*     */       else
/*     */       {
/*  87 */         if (log.isDebugEnabled()) {
/*  88 */           log.debug(Messages.getMessage("noRoles01"));
/*     */         }
/*     */ 
/*  91 */         throw new AxisFault("Server.Unauthorized", Messages.getMessage("notAuth00", userID, serviceName), null, null);
/*     */       }
/*     */ 
/*  96 */       if (log.isDebugEnabled()) {
/*  97 */         log.debug("Exit: SimpleAuthorizationHandler::invoke");
/*     */       }
/*  99 */       return;
/*     */     }
/*     */ 
/* 102 */     SecurityProvider provider = (SecurityProvider)msgContext.getProperty("securityProvider");
/* 103 */     if (provider == null) {
/* 104 */       throw new AxisFault(Messages.getMessage("noSecurity00"));
/*     */     }
/* 106 */     StringTokenizer st = new StringTokenizer(allowedRoles, ",");
/* 107 */     while (st.hasMoreTokens()) {
/* 108 */       String thisRole = st.nextToken();
/* 109 */       if (provider.userMatches(user, thisRole))
/*     */       {
/* 111 */         if (log.isDebugEnabled()) {
/* 112 */           log.debug(Messages.getMessage("auth01", userID, serviceName));
/*     */         }
/*     */ 
/* 116 */         if (log.isDebugEnabled()) {
/* 117 */           log.debug("Exit: SimpleAuthorizationHandler::invoke");
/*     */         }
/* 119 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 123 */     throw new AxisFault("Server.Unauthorized", Messages.getMessage("cantAuth02", userID, serviceName), null, null);
/*     */   }
/*     */ 
/*     */   public void onFault(MessageContext msgContext)
/*     */   {
/* 132 */     if (log.isDebugEnabled()) {
/* 133 */       log.debug("Enter: SimpleAuthorizationHandler::onFault");
/* 134 */       log.debug("Exit: SimpleAuthorizationHandler::onFault");
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.handlers.SimpleAuthorizationHandler
 * JD-Core Version:    0.6.0
 */