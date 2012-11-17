/*     */ package org.apache.axis.configuration;
/*     */ 
/*     */ import org.apache.axis.AxisProperties;
/*     */ import org.apache.axis.EngineConfiguration;
/*     */ import org.apache.axis.EngineConfigurationFactory;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class EngineConfigurationFactoryDefault
/*     */   implements EngineConfigurationFactory
/*     */ {
/*  43 */   protected static Log log = LogFactory.getLog(EngineConfigurationFactoryDefault.class.getName());
/*     */   public static final String OPTION_CLIENT_CONFIG_FILE = "axis.ClientConfigFile";
/*     */   public static final String OPTION_SERVER_CONFIG_FILE = "axis.ServerConfigFile";
/*     */   protected static final String CLIENT_CONFIG_FILE = "client-config.wsdd";
/*     */   protected static final String SERVER_CONFIG_FILE = "server-config.wsdd";
/*     */   protected String clientConfigFile;
/*     */   protected String serverConfigFile;
/*     */ 
/*     */   public static EngineConfigurationFactory newFactory(Object param)
/*     */   {
/*  66 */     if (param != null) {
/*  67 */       return null;
/*     */     }
/*     */ 
/*  81 */     return new EngineConfigurationFactoryDefault();
/*     */   }
/*     */ 
/*     */   protected EngineConfigurationFactoryDefault()
/*     */   {
/*  89 */     this.clientConfigFile = AxisProperties.getProperty("axis.ClientConfigFile", "client-config.wsdd");
/*     */ 
/*  93 */     this.serverConfigFile = AxisProperties.getProperty("axis.ServerConfigFile", "server-config.wsdd");
/*     */   }
/*     */ 
/*     */   public EngineConfiguration getClientEngineConfig()
/*     */   {
/* 104 */     return new FileProvider(this.clientConfigFile);
/*     */   }
/*     */ 
/*     */   public EngineConfiguration getServerEngineConfig()
/*     */   {
/* 113 */     return new FileProvider(this.serverConfigFile);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.configuration.EngineConfigurationFactoryDefault
 * JD-Core Version:    0.6.0
 */