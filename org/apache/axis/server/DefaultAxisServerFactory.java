/*     */ package org.apache.axis.server;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.Map;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.AxisProperties;
/*     */ import org.apache.axis.EngineConfiguration;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.utils.ClassUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class DefaultAxisServerFactory
/*     */   implements AxisServerFactory
/*     */ {
/*  38 */   protected static Log log = LogFactory.getLog(DefaultAxisServerFactory.class.getName());
/*     */ 
/*     */   public AxisServer getServer(Map environment)
/*     */     throws AxisFault
/*     */   {
/*  71 */     log.debug("Enter: DefaultAxisServerFactory::getServer");
/*     */ 
/*  73 */     AxisServer ret = createServer(environment);
/*     */ 
/*  75 */     if (ret != null) {
/*  76 */       if (environment != null) {
/*  77 */         ret.setOptionDefault("attachments.Directory", (String)environment.get("axis.attachments.Directory"));
/*     */ 
/*  80 */         ret.setOptionDefault("attachments.Directory", (String)environment.get("servlet.realpath"));
/*     */       }
/*     */ 
/*  84 */       String attachmentsdir = (String)ret.getOption("attachments.Directory");
/*     */ 
/*  86 */       if (attachmentsdir != null) {
/*  87 */         File attdirFile = new File(attachmentsdir);
/*  88 */         if (!attdirFile.isDirectory()) {
/*  89 */           attdirFile.mkdirs();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  94 */     log.debug("Exit: DefaultAxisServerFactory::getServer");
/*     */ 
/*  96 */     return ret;
/*     */   }
/*     */ 
/*     */   private static AxisServer createServer(Map environment)
/*     */   {
/* 106 */     EngineConfiguration config = getEngineConfiguration(environment);
/*     */ 
/* 109 */     return config == null ? new AxisServer() : new AxisServer(config);
/*     */   }
/*     */ 
/*     */   private static EngineConfiguration getEngineConfiguration(Map environment)
/*     */   {
/* 122 */     log.debug("Enter: DefaultAxisServerFactory::getEngineConfiguration");
/*     */ 
/* 124 */     EngineConfiguration config = null;
/*     */ 
/* 126 */     if (environment != null) {
/*     */       try {
/* 128 */         config = (EngineConfiguration)environment.get("engineConfig");
/*     */       } catch (ClassCastException e) {
/* 130 */         log.warn(Messages.getMessage("engineConfigWrongClass00"), e);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 135 */     if (config == null)
/*     */     {
/* 138 */       String configClass = AxisProperties.getProperty("axis.engineConfigClass");
/* 139 */       if (configClass != null)
/*     */       {
/*     */         try
/*     */         {
/* 144 */           Class cls = ClassUtils.forName(configClass);
/* 145 */           config = (EngineConfiguration)cls.newInstance();
/*     */         } catch (ClassNotFoundException e) {
/* 147 */           log.warn(Messages.getMessage("engineConfigNoClass00", configClass), e);
/*     */         }
/*     */         catch (InstantiationException e) {
/* 150 */           log.warn(Messages.getMessage("engineConfigNoInstance00", configClass), e);
/*     */         }
/*     */         catch (IllegalAccessException e) {
/* 153 */           log.warn(Messages.getMessage("engineConfigIllegalAccess00", configClass), e);
/*     */         }
/*     */         catch (ClassCastException e) {
/* 156 */           log.warn(Messages.getMessage("engineConfigWrongClass01", configClass), e);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 162 */     log.debug("Exit: DefaultAxisServerFactory::getEngineConfiguration");
/*     */ 
/* 164 */     return config;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.server.DefaultAxisServerFactory
 * JD-Core Version:    0.6.0
 */