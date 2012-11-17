/*    */ package org.apache.axis.configuration;
/*    */ 
/*    */ import org.apache.axis.EngineConfiguration;
/*    */ import org.apache.axis.EngineConfigurationFactory;
/*    */ 
/*    */ /** @deprecated */
/*    */ public class DefaultEngineConfigurationFactory
/*    */   implements EngineConfigurationFactory
/*    */ {
/*    */   protected final EngineConfigurationFactory factory;
/*    */ 
/*    */   protected DefaultEngineConfigurationFactory(EngineConfigurationFactory factory)
/*    */   {
/* 38 */     this.factory = factory;
/*    */   }
/*    */ 
/*    */   public DefaultEngineConfigurationFactory()
/*    */   {
/* 46 */     this(EngineConfigurationFactoryFinder.newFactory());
/*    */   }
/*    */ 
/*    */   public EngineConfiguration getClientEngineConfig()
/*    */   {
/* 55 */     return this.factory == null ? null : this.factory.getClientEngineConfig();
/*    */   }
/*    */ 
/*    */   public EngineConfiguration getServerEngineConfig()
/*    */   {
/* 64 */     return this.factory == null ? null : this.factory.getServerEngineConfig();
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.configuration.DefaultEngineConfigurationFactory
 * JD-Core Version:    0.6.0
 */