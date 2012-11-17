/*    */ package org.apache.axis;
/*    */ 
/*    */ public class AxisServiceConfigImpl
/*    */   implements AxisServiceConfig
/*    */ {
/*    */   private String methods;
/*    */ 
/*    */   public void setAllowedMethods(String methods)
/*    */   {
/* 36 */     this.methods = methods;
/*    */   }
/*    */ 
/*    */   public String getAllowedMethods()
/*    */   {
/* 45 */     return this.methods;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.AxisServiceConfigImpl
 * JD-Core Version:    0.6.0
 */