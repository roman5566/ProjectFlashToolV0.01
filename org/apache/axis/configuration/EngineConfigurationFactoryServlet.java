/*     */ package org.apache.axis.configuration;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import javax.servlet.ServletConfig;
/*     */ import javax.servlet.ServletContext;
/*     */ import org.apache.axis.AxisProperties;
/*     */ import org.apache.axis.ConfigurationException;
/*     */ import org.apache.axis.EngineConfiguration;
/*     */ import org.apache.axis.EngineConfigurationFactory;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.server.AxisServer;
/*     */ import org.apache.axis.utils.ClassUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class EngineConfigurationFactoryServlet extends EngineConfigurationFactoryDefault
/*     */ {
/*  51 */   protected static Log log = LogFactory.getLog(EngineConfigurationFactoryServlet.class.getName());
/*     */   private ServletConfig cfg;
/*     */ 
/*     */   public static EngineConfigurationFactory newFactory(Object param)
/*     */   {
/*  79 */     return (param instanceof ServletConfig) ? new EngineConfigurationFactoryServlet((ServletConfig)param) : null;
/*     */   }
/*     */ 
/*     */   protected EngineConfigurationFactoryServlet(ServletConfig conf)
/*     */   {
/*  90 */     this.cfg = conf;
/*     */   }
/*     */ 
/*     */   public EngineConfiguration getServerEngineConfig()
/*     */   {
/*  99 */     return getServerEngineConfig(this.cfg);
/*     */   }
/*     */ 
/*     */   private static EngineConfiguration getServerEngineConfig(ServletConfig cfg)
/*     */   {
/* 111 */     ServletContext ctx = cfg.getServletContext();
/*     */ 
/* 114 */     String configFile = cfg.getInitParameter("axis.ServerConfigFile");
/* 115 */     if (configFile == null) {
/* 116 */       configFile = AxisProperties.getProperty("axis.ServerConfigFile");
/*     */     }
/* 118 */     if (configFile == null) {
/* 119 */       configFile = "server-config.wsdd";
/*     */     }
/*     */ 
/* 139 */     String appWebInfPath = "/WEB-INF";
/*     */ 
/* 141 */     FileProvider config = null;
/*     */ 
/* 143 */     String realWebInfPath = ctx.getRealPath(appWebInfPath);
/*     */ 
/* 150 */     if ((realWebInfPath == null) || (!new File(realWebInfPath, configFile).exists()))
/*     */     {
/* 153 */       String name = appWebInfPath + "/" + configFile;
/* 154 */       InputStream is = ctx.getResourceAsStream(name);
/* 155 */       if (is != null)
/*     */       {
/* 158 */         config = new FileProvider(is);
/*     */       }
/*     */ 
/* 161 */       if (config == null) {
/* 162 */         log.error(Messages.getMessage("servletEngineWebInfError03", name));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 172 */     if ((config == null) && (realWebInfPath != null)) {
/*     */       try {
/* 174 */         config = new FileProvider(realWebInfPath, configFile);
/*     */       } catch (ConfigurationException e) {
/* 176 */         log.error(Messages.getMessage("servletEngineWebInfError00"), e);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 183 */     if (config == null) {
/* 184 */       log.warn(Messages.getMessage("servletEngineWebInfWarn00"));
/*     */       try {
/* 186 */         InputStream is = ClassUtils.getResourceAsStream(AxisServer.class, "server-config.wsdd");
/*     */ 
/* 189 */         config = new FileProvider(is);
/*     */       } catch (Exception e) {
/* 191 */         log.error(Messages.getMessage("servletEngineWebInfError02"), e);
/*     */       }
/*     */     }
/*     */ 
/* 195 */     return config;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.configuration.EngineConfigurationFactoryServlet
 * JD-Core Version:    0.6.0
 */