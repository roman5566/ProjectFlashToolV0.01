/*     */ package org.apache.axis.configuration;
/*     */ 
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Writer;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.AxisEngine;
/*     */ import org.apache.axis.ConfigurationException;
/*     */ import org.apache.axis.Handler;
/*     */ import org.apache.axis.WSDDEngineConfiguration;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.deployment.wsdd.WSDDDeployment;
/*     */ import org.apache.axis.deployment.wsdd.WSDDDocument;
/*     */ import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
/*     */ import org.apache.axis.encoding.TypeMappingRegistry;
/*     */ import org.apache.axis.handlers.soap.SOAPService;
/*     */ import org.apache.axis.utils.Admin;
/*     */ import org.apache.axis.utils.ClassUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Document;
/*     */ 
/*     */ public class FileProvider
/*     */   implements WSDDEngineConfiguration
/*     */ {
/*  58 */   protected static Log log = LogFactory.getLog(FileProvider.class.getName());
/*     */ 
/*  61 */   private WSDDDeployment deployment = null;
/*     */   private String filename;
/*  64 */   private File configFile = null;
/*     */ 
/*  66 */   private InputStream myInputStream = null;
/*     */ 
/*  68 */   private boolean readOnly = true;
/*     */ 
/*  72 */   private boolean searchClasspath = true;
/*     */ 
/*     */   public FileProvider(String filename)
/*     */   {
/*  79 */     this.filename = filename;
/*  80 */     this.configFile = new File(filename);
/*  81 */     check();
/*     */   }
/*     */ 
/*     */   public FileProvider(String basepath, String filename)
/*     */     throws ConfigurationException
/*     */   {
/*  90 */     this.filename = filename;
/*     */ 
/*  92 */     File dir = new File(basepath);
/*     */ 
/*  98 */     if ((!dir.exists()) || (!dir.isDirectory()) || (!dir.canRead())) {
/*  99 */       throw new ConfigurationException(Messages.getMessage("invalidConfigFilePath", basepath));
/*     */     }
/*     */ 
/* 104 */     this.configFile = new File(basepath, filename);
/* 105 */     check();
/*     */   }
/*     */ 
/*     */   private void check()
/*     */   {
/*     */     try
/*     */     {
/* 114 */       this.readOnly = (this.configFile.canRead() & !this.configFile.canWrite());
/*     */     } catch (SecurityException se) {
/* 116 */       this.readOnly = true;
/*     */     }
/*     */ 
/* 123 */     if (this.readOnly)
/* 124 */       log.info(Messages.getMessage("readOnlyConfigFile"));
/*     */   }
/*     */ 
/*     */   public FileProvider(InputStream is)
/*     */   {
/* 133 */     setInputStream(is);
/*     */   }
/*     */ 
/*     */   public void setInputStream(InputStream is) {
/* 137 */     this.myInputStream = is;
/*     */   }
/*     */ 
/*     */   private InputStream getInputStream() {
/* 141 */     return this.myInputStream;
/*     */   }
/*     */ 
/*     */   public WSDDDeployment getDeployment() {
/* 145 */     return this.deployment;
/*     */   }
/*     */ 
/*     */   public void setDeployment(WSDDDeployment deployment) {
/* 149 */     this.deployment = deployment;
/*     */   }
/*     */ 
/*     */   public void setSearchClasspath(boolean searchClasspath)
/*     */   {
/* 159 */     this.searchClasspath = searchClasspath;
/*     */   }
/*     */ 
/*     */   public void configureEngine(AxisEngine engine) throws ConfigurationException
/*     */   {
/*     */     try {
/* 165 */       if (getInputStream() == null) {
/*     */         try {
/* 167 */           setInputStream(new FileInputStream(this.configFile));
/*     */         } catch (Exception e) {
/* 169 */           if (this.searchClasspath) {
/* 170 */             setInputStream(ClassUtils.getResourceAsStream(engine.getClass(), this.filename, true));
/*     */           }
/*     */         }
/*     */       }
/* 174 */       if (getInputStream() == null) {
/* 175 */         throw new ConfigurationException(Messages.getMessage("noConfigFile"));
/*     */       }
/*     */ 
/* 179 */       WSDDDocument doc = new WSDDDocument(XMLUtils.newDocument(getInputStream()));
/*     */ 
/* 181 */       this.deployment = doc.getDeployment();
/*     */ 
/* 183 */       this.deployment.configureEngine(engine);
/* 184 */       engine.refreshGlobalOptions();
/*     */ 
/* 186 */       setInputStream(null);
/*     */     } catch (Exception e) {
/* 188 */       throw new ConfigurationException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeEngineConfig(AxisEngine engine)
/*     */     throws ConfigurationException
/*     */   {
/* 199 */     if (!this.readOnly)
/*     */       try {
/* 201 */         Document doc = Admin.listConfig(engine);
/* 202 */         Writer osWriter = new OutputStreamWriter(new FileOutputStream(this.configFile), XMLUtils.getEncoding());
/*     */ 
/* 204 */         PrintWriter writer = new PrintWriter(new BufferedWriter(osWriter));
/* 205 */         XMLUtils.DocumentToWriter(doc, writer);
/* 206 */         writer.println();
/* 207 */         writer.close();
/*     */       } catch (Exception e) {
/* 209 */         throw new ConfigurationException(e);
/*     */       }
/*     */   }
/*     */ 
/*     */   public Handler getHandler(QName qname)
/*     */     throws ConfigurationException
/*     */   {
/* 221 */     return this.deployment.getHandler(qname);
/*     */   }
/*     */ 
/*     */   public SOAPService getService(QName qname)
/*     */     throws ConfigurationException
/*     */   {
/* 231 */     SOAPService service = this.deployment.getService(qname);
/* 232 */     if (service == null) {
/* 233 */       throw new ConfigurationException(Messages.getMessage("noService10", qname.toString()));
/*     */     }
/*     */ 
/* 236 */     return service;
/*     */   }
/*     */ 
/*     */   public SOAPService getServiceByNamespaceURI(String namespace)
/*     */     throws ConfigurationException
/*     */   {
/* 247 */     return this.deployment.getServiceByNamespaceURI(namespace);
/*     */   }
/*     */ 
/*     */   public Handler getTransport(QName qname)
/*     */     throws ConfigurationException
/*     */   {
/* 257 */     return this.deployment.getTransport(qname);
/*     */   }
/*     */ 
/*     */   public TypeMappingRegistry getTypeMappingRegistry() throws ConfigurationException
/*     */   {
/* 262 */     return this.deployment.getTypeMappingRegistry();
/*     */   }
/*     */ 
/*     */   public Handler getGlobalRequest()
/*     */     throws ConfigurationException
/*     */   {
/* 269 */     return this.deployment.getGlobalRequest();
/*     */   }
/*     */ 
/*     */   public Handler getGlobalResponse()
/*     */     throws ConfigurationException
/*     */   {
/* 276 */     return this.deployment.getGlobalResponse();
/*     */   }
/*     */ 
/*     */   public Hashtable getGlobalOptions()
/*     */     throws ConfigurationException
/*     */   {
/* 283 */     WSDDGlobalConfiguration globalConfig = this.deployment.getGlobalConfiguration();
/*     */ 
/* 286 */     if (globalConfig != null) {
/* 287 */       return globalConfig.getParametersTable();
/*     */     }
/* 289 */     return null;
/*     */   }
/*     */ 
/*     */   public Iterator getDeployedServices()
/*     */     throws ConfigurationException
/*     */   {
/* 296 */     return this.deployment.getDeployedServices();
/*     */   }
/*     */ 
/*     */   public List getRoles()
/*     */   {
/* 306 */     return this.deployment.getRoles();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.configuration.FileProvider
 * JD-Core Version:    0.6.0
 */