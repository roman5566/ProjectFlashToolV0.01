/*    */ package org.apache.axis.deployment.wsdd.providers;
/*    */ 
/*    */ import org.apache.axis.EngineConfiguration;
/*    */ import org.apache.axis.Handler;
/*    */ import org.apache.axis.deployment.wsdd.WSDDProvider;
/*    */ import org.apache.axis.deployment.wsdd.WSDDService;
/*    */ import org.apache.axis.providers.java.RPCProvider;
/*    */ 
/*    */ public class WSDDJavaRPCProvider extends WSDDProvider
/*    */ {
/*    */   public String getName()
/*    */   {
/* 32 */     return "RPC";
/*    */   }
/*    */ 
/*    */   public Handler newProviderInstance(WSDDService service, EngineConfiguration registry)
/*    */     throws Exception
/*    */   {
/* 41 */     return new RPCProvider();
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.providers.WSDDJavaRPCProvider
 * JD-Core Version:    0.6.0
 */