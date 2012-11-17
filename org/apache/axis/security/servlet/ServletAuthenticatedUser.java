/*    */ package org.apache.axis.security.servlet;
/*    */ 
/*    */ import java.security.Principal;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import org.apache.axis.security.AuthenticatedUser;
/*    */ 
/*    */ public class ServletAuthenticatedUser
/*    */   implements AuthenticatedUser
/*    */ {
/*    */   private String name;
/*    */   private HttpServletRequest req;
/*    */ 
/*    */   public ServletAuthenticatedUser(HttpServletRequest req)
/*    */   {
/* 38 */     this.req = req;
/* 39 */     Principal principal = req.getUserPrincipal();
/* 40 */     this.name = (principal == null ? null : principal.getName());
/*    */   }
/*    */ 
/*    */   public String getName()
/*    */   {
/* 48 */     return this.name;
/*    */   }
/*    */ 
/*    */   public HttpServletRequest getRequest()
/*    */   {
/* 53 */     return this.req;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.security.servlet.ServletAuthenticatedUser
 * JD-Core Version:    0.6.0
 */