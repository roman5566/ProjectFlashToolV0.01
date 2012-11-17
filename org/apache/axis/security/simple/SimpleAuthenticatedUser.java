/*    */ package org.apache.axis.security.simple;
/*    */ 
/*    */ import org.apache.axis.security.AuthenticatedUser;
/*    */ 
/*    */ public class SimpleAuthenticatedUser
/*    */   implements AuthenticatedUser
/*    */ {
/*    */   private String name;
/*    */ 
/*    */   public SimpleAuthenticatedUser(String name)
/*    */   {
/* 33 */     this.name = name;
/*    */   }
/*    */ 
/*    */   public String getName()
/*    */   {
/* 41 */     return this.name;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.security.simple.SimpleAuthenticatedUser
 * JD-Core Version:    0.6.0
 */