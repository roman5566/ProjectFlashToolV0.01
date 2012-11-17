/*     */ package org.apache.axis.server;
/*     */ 
/*     */ import java.util.Map;
/*     */ import javax.naming.InitialContext;
/*     */ import javax.naming.NamingException;
/*     */ import javax.servlet.ServletContext;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class JNDIAxisServerFactory extends DefaultAxisServerFactory
/*     */ {
/*     */   public AxisServer getServer(Map environment)
/*     */     throws AxisFault
/*     */   {
/*  56 */     log.debug("Enter: JNDIAxisServerFactory::getServer");
/*     */ 
/*  58 */     InitialContext context = null;
/*     */     try
/*     */     {
/*  63 */       context = new InitialContext();
/*     */     } catch (NamingException e) {
/*  65 */       log.warn(Messages.getMessage("jndiNotFound00"), e);
/*     */     }
/*     */ 
/*  68 */     ServletContext servletContext = null;
/*     */     try {
/*  70 */       servletContext = (ServletContext)environment.get("servletContext");
/*     */     }
/*     */     catch (ClassCastException e) {
/*  73 */       log.warn(Messages.getMessage("servletContextWrongClass00"), e);
/*     */     }
/*     */ 
/*  77 */     AxisServer server = null;
/*  78 */     if ((context != null) && (servletContext != null))
/*     */     {
/*  91 */       String name = servletContext.getRealPath("/WEB-INF/Server");
/*     */ 
/* 113 */       if (name != null) {
/*     */         try {
/* 115 */           server = (AxisServer)context.lookup(name);
/*     */         }
/*     */         catch (NamingException e) {
/* 118 */           server = super.getServer(environment);
/*     */           try {
/* 120 */             context.bind(name, server);
/*     */           }
/*     */           catch (NamingException e1)
/*     */           {
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 128 */     if (server == null) {
/* 129 */       server = super.getServer(environment);
/*     */     }
/*     */ 
/* 132 */     log.debug("Exit: JNDIAxisServerFactory::getServer");
/*     */ 
/* 134 */     return server;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.server.JNDIAxisServerFactory
 * JD-Core Version:    0.6.0
 */