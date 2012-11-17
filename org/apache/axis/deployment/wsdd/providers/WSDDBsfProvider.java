/*    */ package org.apache.axis.deployment.wsdd.providers;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.axis.EngineConfiguration;
/*    */ import org.apache.axis.Handler;
/*    */ import org.apache.axis.deployment.wsdd.WSDDProvider;
/*    */ import org.apache.axis.deployment.wsdd.WSDDService;
/*    */ import org.apache.axis.encoding.SerializationContext;
/*    */ import org.apache.axis.providers.BSFProvider;
/*    */ 
/*    */ public class WSDDBsfProvider extends WSDDProvider
/*    */ {
/*    */   public String getName()
/*    */   {
/* 36 */     return "BSF";
/*    */   }
/*    */ 
/*    */   public Handler newProviderInstance(WSDDService service, EngineConfiguration registry)
/*    */     throws Exception
/*    */   {
/* 43 */     Handler provider = new BSFProvider();
/*    */ 
/* 45 */     String option = service.getParameter("language");
/*    */ 
/* 47 */     if (!option.equals("")) {
/* 48 */       provider.setOption("language", option);
/*    */     }
/*    */ 
/* 51 */     option = service.getParameter("src");
/*    */ 
/* 53 */     if (!option.equals("")) {
/* 54 */       provider.setOption("src", option);
/*    */     }
/*    */ 
/* 60 */     if (!option.equals("")) {
/* 61 */       provider.setOption("script", option);
/*    */     }
/*    */ 
/* 64 */     return provider;
/*    */   }
/*    */ 
/*    */   public void writeToContext(SerializationContext context)
/*    */     throws IOException
/*    */   {
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.providers.WSDDBsfProvider
 * JD-Core Version:    0.6.0
 */