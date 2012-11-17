/*    */ package org.apache.axis.transport.http;
/*    */ 
/*    */ import java.security.Principal;
/*    */ import javax.servlet.ServletContext;
/*    */ import javax.servlet.http.HttpServlet;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpSession;
/*    */ import javax.xml.rpc.server.ServletEndpointContext;
/*    */ 
/*    */ public class ServletEndpointContextImpl
/*    */   implements ServletEndpointContext
/*    */ {
/*    */   public HttpSession getHttpSession()
/*    */   {
/* 29 */     HttpServletRequest srvreq = (HttpServletRequest)getMessageContext().getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
/*    */ 
/* 31 */     return srvreq == null ? null : srvreq.getSession();
/*    */   }
/*    */ 
/*    */   public javax.xml.rpc.handler.MessageContext getMessageContext() {
/* 35 */     return org.apache.axis.MessageContext.getCurrentContext();
/*    */   }
/*    */ 
/*    */   public ServletContext getServletContext() {
/* 39 */     HttpServlet srv = (HttpServlet)getMessageContext().getProperty(HTTPConstants.MC_HTTP_SERVLET);
/*    */ 
/* 41 */     return srv == null ? null : srv.getServletContext();
/*    */   }
/*    */ 
/*    */   public boolean isUserInRole(String role) {
/* 45 */     HttpServletRequest srvreq = (HttpServletRequest)getMessageContext().getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
/*    */ 
/* 47 */     return srvreq == null ? false : srvreq.isUserInRole(role);
/*    */   }
/*    */ 
/*    */   public Principal getUserPrincipal() {
/* 51 */     HttpServletRequest srvreq = (HttpServletRequest)getMessageContext().getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
/*    */ 
/* 54 */     return srvreq == null ? null : srvreq.getUserPrincipal();
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.http.ServletEndpointContextImpl
 * JD-Core Version:    0.6.0
 */