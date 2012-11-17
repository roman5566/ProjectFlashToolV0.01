/*     */ package org.apache.axis.transport.http;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.apache.axis.AxisEngine;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.ConfigurationException;
/*     */ import org.apache.axis.EngineConfiguration;
/*     */ import org.apache.axis.WSDDEngineConfiguration;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.deployment.wsdd.WSDDDeployment;
/*     */ import org.apache.axis.deployment.wsdd.WSDDDocument;
/*     */ import org.apache.axis.i18n.Messages;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Document;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class AutoRegisterServlet extends AxisServletBase
/*     */ {
/*  49 */   private static Log log = LogFactory.getLog(AutoRegisterServlet.class.getName());
/*     */ 
/*     */   public void init()
/*     */     throws ServletException
/*     */   {
/*  56 */     log.debug(Messages.getMessage("autoRegServletInit00"));
/*  57 */     autoRegister();
/*     */   }
/*     */ 
/*     */   public void registerStream(InputStream instream)
/*     */     throws SAXException, ParserConfigurationException, IOException
/*     */   {
/*     */     try
/*     */     {
/*  69 */       Document doc = XMLUtils.newDocument(instream);
/*  70 */       WSDDDocument wsddDoc = new WSDDDocument(doc);
/*     */ 
/*  72 */       WSDDDeployment deployment = getDeployment();
/*  73 */       if (deployment != null)
/*  74 */         wsddDoc.deploy(deployment);
/*     */     }
/*     */     finally {
/*  77 */       instream.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void registerResource(String resourcename)
/*     */     throws SAXException, ParserConfigurationException, IOException
/*     */   {
/*  90 */     InputStream in = getServletContext().getResourceAsStream(resourcename);
/*  91 */     if (in == null) {
/*  92 */       throw new FileNotFoundException(resourcename);
/*     */     }
/*  94 */     registerStream(in);
/*     */   }
/*     */ 
/*     */   public void registerFile(File file)
/*     */     throws IOException, SAXException, ParserConfigurationException
/*     */   {
/* 105 */     InputStream in = new BufferedInputStream(new FileInputStream(file));
/* 106 */     registerStream(in);
/*     */   }
/*     */ 
/*     */   public String[] getResourcesToRegister()
/*     */   {
/* 114 */     return null;
/*     */   }
/*     */ 
/*     */   private WSDDDeployment getDeployment()
/*     */     throws AxisFault
/*     */   {
/* 126 */     AxisEngine engine = getEngine();
/* 127 */     EngineConfiguration config = engine.getConfig();
/*     */     WSDDDeployment deployment;
/*     */     WSDDDeployment deployment;
/* 128 */     if ((config instanceof WSDDEngineConfiguration))
/* 129 */       deployment = ((WSDDEngineConfiguration)config).getDeployment();
/*     */     else {
/* 131 */       deployment = null;
/*     */     }
/* 133 */     return deployment;
/*     */   }
/*     */ 
/*     */   protected void logSuccess(String item)
/*     */   {
/* 142 */     log.debug(Messages.getMessage("autoRegServletLoaded01", item));
/*     */   }
/*     */ 
/*     */   protected void autoRegister()
/*     */   {
/* 149 */     String[] resources = getResourcesToRegister();
/* 150 */     if ((resources == null) || (resources.length == 0)) {
/* 151 */       return;
/*     */     }
/* 153 */     for (int i = 0; i < resources.length; i++) {
/* 154 */       String resource = resources[i];
/* 155 */       registerAndLogResource(resource);
/*     */     }
/* 157 */     registerAnythingElse();
/*     */     try {
/* 159 */       applyAndSaveSettings();
/*     */     } catch (Exception e) {
/* 161 */       log.error(Messages.getMessage("autoRegServletApplyAndSaveSettings00"), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void registerAnythingElse()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void registerAndLogResource(String resource)
/*     */   {
/*     */     try
/*     */     {
/* 177 */       registerResource(resource);
/* 178 */       logSuccess(resource);
/*     */     } catch (Exception e) {
/* 180 */       log.error(Messages.getMessage("autoRegServletLoadFailed01", resource), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void applyAndSaveSettings()
/*     */     throws AxisFault, ConfigurationException
/*     */   {
/* 191 */     AxisEngine engine = getEngine();
/* 192 */     engine.refreshGlobalOptions();
/* 193 */     engine.saveConfiguration();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.http.AutoRegisterServlet
 * JD-Core Version:    0.6.0
 */