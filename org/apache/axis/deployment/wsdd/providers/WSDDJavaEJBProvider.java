/*    */ package org.apache.axis.deployment.wsdd.providers;
/*    */ 
/*    */ import org.apache.axis.EngineConfiguration;
/*    */ import org.apache.axis.Handler;
/*    */ import org.apache.axis.deployment.wsdd.WSDDProvider;
/*    */ import org.apache.axis.deployment.wsdd.WSDDService;
/*    */ import org.apache.axis.providers.java.EJBProvider;
/*    */ 
/*    */ public class WSDDJavaEJBProvider extends WSDDProvider
/*    */ {
/*    */   public String getName()
/*    */   {
/* 34 */     return "EJB";
/*    */   }
/*    */ 
/*    */   public Handler newProviderInstance(WSDDService service, EngineConfiguration registry)
/*    */     throws Exception
/*    */   {
/* 43 */     return new EJBProvider();
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.providers.WSDDJavaEJBProvider
 * JD-Core Version:    0.6.0
 */