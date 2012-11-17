/*    */ package org.apache.axis.deployment.wsdd.providers;
/*    */ 
/*    */ import org.apache.axis.EngineConfiguration;
/*    */ import org.apache.axis.Handler;
/*    */ import org.apache.axis.deployment.wsdd.WSDDProvider;
/*    */ import org.apache.axis.deployment.wsdd.WSDDService;
/*    */ import org.apache.axis.providers.BasicProvider;
/*    */ import org.apache.axis.utils.ClassUtils;
/*    */ 
/*    */ public class WSDDComProvider extends WSDDProvider
/*    */ {
/*    */   public static final String OPTION_PROGID = "ProgID";
/*    */   public static final String OPTION_THREADING_MODEL = "threadingModel";
/*    */ 
/*    */   public String getName()
/*    */   {
/* 37 */     return "COM";
/*    */   }
/*    */ 
/*    */   public Handler newProviderInstance(WSDDService service, EngineConfiguration registry)
/*    */     throws Exception
/*    */   {
/* 44 */     Class _class = ClassUtils.forName("org.apache.axis.providers.ComProvider");
/*    */ 
/* 46 */     BasicProvider provider = (BasicProvider)_class.newInstance();
/*    */ 
/* 48 */     String option = service.getParameter("ProgID");
/*    */ 
/* 50 */     if (!option.equals("")) {
/* 51 */       provider.setOption("ProgID", option);
/*    */     }
/*    */ 
/* 54 */     option = service.getParameter("threadingModel");
/*    */ 
/* 56 */     if ((option != null) && (!option.equals(""))) {
/* 57 */       provider.setOption("threadingModel", option);
/*    */     }
/*    */ 
/* 60 */     return provider;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.providers.WSDDComProvider
 * JD-Core Version:    0.6.0
 */