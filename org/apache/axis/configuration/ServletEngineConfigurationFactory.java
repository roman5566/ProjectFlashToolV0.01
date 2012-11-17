/*    */ package org.apache.axis.configuration;
/*    */ 
/*    */ import javax.servlet.ServletContext;
/*    */ 
/*    */ /** @deprecated */
/*    */ public class ServletEngineConfigurationFactory extends DefaultEngineConfigurationFactory
/*    */ {
/*    */   public ServletEngineConfigurationFactory(ServletContext ctx)
/*    */   {
/* 34 */     super(EngineConfigurationFactoryFinder.newFactory(ctx));
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.configuration.ServletEngineConfigurationFactory
 * JD-Core Version:    0.6.0
 */