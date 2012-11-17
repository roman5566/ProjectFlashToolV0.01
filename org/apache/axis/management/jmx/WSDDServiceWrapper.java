/*    */ package org.apache.axis.management.jmx;
/*    */ 
/*    */ import org.apache.axis.deployment.wsdd.WSDDService;
/*    */ 
/*    */ public class WSDDServiceWrapper
/*    */ {
/*    */   private WSDDService _wsddService;
/*    */ 
/*    */   public WSDDService getWSDDService()
/*    */   {
/* 24 */     return this._wsddService;
/*    */   }
/*    */ 
/*    */   public void setWSDDService(WSDDService wsddService) {
/* 28 */     this._wsddService = wsddService;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.management.jmx.WSDDServiceWrapper
 * JD-Core Version:    0.6.0
 */