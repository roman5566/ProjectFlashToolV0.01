/*    */ package org.apache.axis.deployment.wsdd.providers;
/*    */ 
/*    */ import org.apache.axis.ConfigurationException;
/*    */ import org.apache.axis.EngineConfiguration;
/*    */ import org.apache.axis.Handler;
/*    */ import org.apache.axis.deployment.wsdd.WSDDProvider;
/*    */ import org.apache.axis.deployment.wsdd.WSDDService;
/*    */ import org.apache.axis.utils.ClassUtils;
/*    */ import org.apache.axis.utils.Messages;
/*    */ 
/*    */ public class WSDDHandlerProvider extends WSDDProvider
/*    */ {
/*    */   public String getName()
/*    */   {
/* 37 */     return "Handler";
/*    */   }
/*    */ 
/*    */   public Handler newProviderInstance(WSDDService service, EngineConfiguration registry)
/*    */     throws Exception
/*    */   {
/* 44 */     String providerClass = service.getParameter("handlerClass");
/* 45 */     if (providerClass == null) {
/* 46 */       throw new ConfigurationException(Messages.getMessage("noHandlerClass00"));
/*    */     }
/*    */ 
/* 49 */     Class _class = ClassUtils.forName(providerClass);
/*    */ 
/* 51 */     if (!Handler.class.isAssignableFrom(_class)) {
/* 52 */       throw new ConfigurationException(Messages.getMessage("badHandlerClass00", _class.getName()));
/*    */     }
/*    */ 
/* 56 */     return (Handler)_class.newInstance();
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.providers.WSDDHandlerProvider
 * JD-Core Version:    0.6.0
 */