/*     */ package org.apache.axis.configuration;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileFilter;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.AxisEngine;
/*     */ import org.apache.axis.ConfigurationException;
/*     */ import org.apache.axis.Handler;
/*     */ import org.apache.axis.WSDDEngineConfiguration;
/*     */ import org.apache.axis.deployment.wsdd.WSDDDeployment;
/*     */ import org.apache.axis.deployment.wsdd.WSDDDocument;
/*     */ import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
/*     */ import org.apache.axis.encoding.TypeMappingRegistry;
/*     */ import org.apache.axis.handlers.soap.SOAPService;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ 
/*     */ public class DirProvider
/*     */   implements WSDDEngineConfiguration
/*     */ {
/*  49 */   protected static Log log = LogFactory.getLog(DirProvider.class.getName());
/*     */ 
/*  52 */   private WSDDDeployment deployment = null;
/*     */   private String configFile;
/*     */   private File dir;
/*     */   private static final String SERVER_CONFIG_FILE = "server-config.wsdd";
/*     */ 
/*     */   public DirProvider(String basepath)
/*     */     throws ConfigurationException
/*     */   {
/*  61 */     this(basepath, "server-config.wsdd");
/*     */   }
/*     */ 
/*     */   public DirProvider(String basepath, String configFile) throws ConfigurationException
/*     */   {
/*  66 */     File dir = new File(basepath);
/*     */ 
/*  72 */     if ((!dir.exists()) || (!dir.isDirectory()) || (!dir.canRead())) {
/*  73 */       throw new ConfigurationException(Messages.getMessage("invalidConfigFilePath", basepath));
/*     */     }
/*     */ 
/*  78 */     this.dir = dir;
/*  79 */     this.configFile = configFile;
/*     */   }
/*     */ 
/*     */   public WSDDDeployment getDeployment() {
/*  83 */     return this.deployment;
/*     */   }
/*     */ 
/*     */   public void configureEngine(AxisEngine engine)
/*     */     throws ConfigurationException
/*     */   {
/*  94 */     this.deployment = new WSDDDeployment();
/*  95 */     WSDDGlobalConfiguration config = new WSDDGlobalConfiguration();
/*  96 */     config.setOptionsHashtable(new Hashtable());
/*  97 */     this.deployment.setGlobalConfiguration(config);
/*  98 */     File[] dirs = this.dir.listFiles(new DirFilter(null));
/*  99 */     for (int i = 0; i < dirs.length; i++) {
/* 100 */       processWSDD(dirs[i]);
/*     */     }
/* 102 */     this.deployment.configureEngine(engine);
/* 103 */     engine.refreshGlobalOptions();
/*     */   }
/*     */ 
/*     */   private void processWSDD(File dir) throws ConfigurationException
/*     */   {
/* 108 */     File file = new File(dir, this.configFile);
/* 109 */     if (!file.exists()) {
/* 110 */       return;
/*     */     }
/* 112 */     log.debug("Loading service configuration from file: " + file);
/* 113 */     InputStream in = null;
/*     */     try {
/* 115 */       in = new FileInputStream(file);
/* 116 */       WSDDDocument doc = new WSDDDocument(XMLUtils.newDocument(in));
/* 117 */       doc.deploy(this.deployment);
/*     */     } catch (Exception e) {
/* 119 */       throw new ConfigurationException(e);
/*     */     } finally {
/* 121 */       if (in != null)
/*     */         try {
/* 123 */           in.close();
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeEngineConfig(AxisEngine engine)
/*     */     throws ConfigurationException
/*     */   {
/*     */   }
/*     */ 
/*     */   public Handler getHandler(QName qname)
/*     */     throws ConfigurationException
/*     */   {
/* 146 */     return this.deployment.getHandler(qname);
/*     */   }
/*     */ 
/*     */   public SOAPService getService(QName qname)
/*     */     throws ConfigurationException
/*     */   {
/* 156 */     SOAPService service = this.deployment.getService(qname);
/* 157 */     if (service == null) {
/* 158 */       throw new ConfigurationException(Messages.getMessage("noService10", qname.toString()));
/*     */     }
/*     */ 
/* 161 */     return service;
/*     */   }
/*     */ 
/*     */   public SOAPService getServiceByNamespaceURI(String namespace)
/*     */     throws ConfigurationException
/*     */   {
/* 172 */     return this.deployment.getServiceByNamespaceURI(namespace);
/*     */   }
/*     */ 
/*     */   public Handler getTransport(QName qname)
/*     */     throws ConfigurationException
/*     */   {
/* 182 */     return this.deployment.getTransport(qname);
/*     */   }
/*     */ 
/*     */   public TypeMappingRegistry getTypeMappingRegistry() throws ConfigurationException
/*     */   {
/* 187 */     return this.deployment.getTypeMappingRegistry();
/*     */   }
/*     */ 
/*     */   public Handler getGlobalRequest()
/*     */     throws ConfigurationException
/*     */   {
/* 194 */     return this.deployment.getGlobalRequest();
/*     */   }
/*     */ 
/*     */   public Handler getGlobalResponse()
/*     */     throws ConfigurationException
/*     */   {
/* 201 */     return this.deployment.getGlobalResponse();
/*     */   }
/*     */ 
/*     */   public Hashtable getGlobalOptions()
/*     */     throws ConfigurationException
/*     */   {
/* 208 */     WSDDGlobalConfiguration globalConfig = this.deployment.getGlobalConfiguration();
/*     */ 
/* 211 */     if (globalConfig != null) {
/* 212 */       return globalConfig.getParametersTable();
/*     */     }
/* 214 */     return null;
/*     */   }
/*     */ 
/*     */   public Iterator getDeployedServices()
/*     */     throws ConfigurationException
/*     */   {
/* 221 */     return this.deployment.getDeployedServices();
/*     */   }
/*     */ 
/*     */   public List getRoles()
/*     */   {
/* 231 */     return this.deployment.getRoles();
/*     */   }
/*     */ 
/*     */   private static class DirFilter
/*     */     implements FileFilter
/*     */   {
/*     */     private DirFilter()
/*     */     {
/*     */     }
/*     */ 
/*     */     public boolean accept(File path)
/*     */     {
/*  88 */       return path.isDirectory();
/*     */     }
/*     */ 
/*     */     DirFilter(DirProvider.1 x0)
/*     */     {
/*  86 */       this();
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.configuration.DirProvider
 * JD-Core Version:    0.6.0
 */