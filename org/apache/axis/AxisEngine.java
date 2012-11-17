/*     */ package org.apache.axis;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.rpc.server.ServiceLifecycle;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.encoding.TypeMappingRegistry;
/*     */ import org.apache.axis.handlers.BasicHandler;
/*     */ import org.apache.axis.handlers.soap.SOAPService;
/*     */ import org.apache.axis.session.Session;
/*     */ import org.apache.axis.session.SimpleSession;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.cache.ClassCache;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public abstract class AxisEngine extends BasicHandler
/*     */ {
/*  51 */   protected static Log log = LogFactory.getLog(AxisEngine.class.getName());
/*     */   public static final String PROP_XML_DECL = "sendXMLDeclaration";
/*     */   public static final String PROP_DEBUG_LEVEL = "debugLevel";
/*     */   public static final String PROP_DEBUG_FILE = "debugFile";
/*     */   public static final String PROP_DOMULTIREFS = "sendMultiRefs";
/*     */   public static final String PROP_DISABLE_PRETTY_XML = "disablePrettyXML";
/*     */   public static final String PROP_ENABLE_NAMESPACE_PREFIX_OPTIMIZATION = "enableNamespacePrefixOptimization";
/*     */   public static final String PROP_PASSWORD = "adminPassword";
/*     */   public static final String PROP_SYNC_CONFIG = "syncConfiguration";
/*     */   public static final String PROP_SEND_XSI = "sendXsiTypes";
/*     */   public static final String PROP_ATTACHMENT_DIR = "attachments.Directory";
/*     */   public static final String PROP_ATTACHMENT_IMPLEMENTATION = "attachments.implementation";
/*     */   public static final String PROP_ATTACHMENT_CLEANUP = "attachment.DirectoryCleanUp";
/*     */   public static final String PROP_DEFAULT_CONFIG_CLASS = "axis.engineConfigClass";
/*     */   public static final String PROP_SOAP_VERSION = "defaultSOAPVersion";
/*     */   public static final String PROP_SOAP_ALLOWED_VERSION = "singleSOAPVersion";
/*     */   public static final String PROP_TWOD_ARRAY_ENCODING = "enable2DArrayEncoding";
/*     */   public static final String PROP_XML_ENCODING = "axis.xmlEncoding";
/*     */   public static final String PROP_XML_REUSE_SAX_PARSERS = "axis.xml.reuseParsers";
/*     */   public static final String PROP_BYTE_BUFFER_BACKING = "axis.byteBuffer.backing";
/*     */   public static final String PROP_BYTE_BUFFER_CACHE_INCREMENT = "axis.byteBuffer.cacheIncrement";
/*     */   public static final String PROP_BYTE_BUFFER_RESIDENT_MAX_SIZE = "axis.byteBuffer.residentMaxSize";
/*     */   public static final String PROP_BYTE_BUFFER_WORK_BUFFER_SIZE = "axis.byteBuffer.workBufferSize";
/*     */   public static final String PROP_EMIT_ALL_TYPES = "emitAllTypesInWSDL";
/*     */   public static final String PROP_DOTNET_SOAPENC_FIX = "dotNetSoapEncFix";
/*     */   public static final String PROP_BP10_COMPLIANCE = "ws-i.bp10Compliance";
/*     */   public static final String DEFAULT_ATTACHMENT_IMPL = "org.apache.axis.attachments.AttachmentsImpl";
/*     */   public static final String ENV_ATTACHMENT_DIR = "axis.attachments.Directory";
/*     */   public static final String ENV_SERVLET_REALPATH = "servlet.realpath";
/*     */   public static final String ENV_SERVLET_CONTEXT = "servletContext";
/*     */   private static final String DEFAULT_ADMIN_PASSWORD = "admin";
/*     */   protected EngineConfiguration config;
/* 101 */   protected boolean _hasSafePassword = false;
/*     */ 
/* 107 */   protected boolean shouldSaveConfig = false;
/*     */ 
/* 110 */   protected transient ClassCache classCache = new ClassCache();
/*     */ 
/* 117 */   private Session session = new SimpleSession();
/*     */ 
/* 122 */   private ArrayList actorURIs = new ArrayList();
/*     */ 
/* 128 */   private static ThreadLocal currentMessageContext = new ThreadLocal();
/*     */ 
/* 440 */   private static final String[] BOOLEAN_OPTIONS = { "sendMultiRefs", "sendXsiTypes", "sendXMLDeclaration", "disablePrettyXML", "enableNamespacePrefixOptimization" };
/*     */ 
/*     */   protected static void setCurrentMessageContext(MessageContext mc)
/*     */   {
/* 136 */     currentMessageContext.set(mc);
/*     */   }
/*     */ 
/*     */   public static MessageContext getCurrentMessageContext()
/*     */   {
/* 145 */     return (MessageContext)currentMessageContext.get();
/*     */   }
/*     */ 
/*     */   public AxisEngine(EngineConfiguration config)
/*     */   {
/* 155 */     this.config = config;
/* 156 */     init();
/*     */   }
/*     */ 
/*     */   public void init()
/*     */   {
/* 164 */     if (log.isDebugEnabled()) {
/* 165 */       log.debug("Enter: AxisEngine::init");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 172 */       this.config.configureEngine(this);
/*     */     } catch (Exception e) {
/* 174 */       throw new InternalException(e);
/*     */     }
/*     */ 
/* 178 */     setOptionDefault("attachments.implementation", AxisProperties.getProperty("axis.attachments.implementation"));
/*     */ 
/* 181 */     setOptionDefault("attachments.implementation", "org.apache.axis.attachments.AttachmentsImpl");
/*     */ 
/* 186 */     Object dotnet = getOption("dotNetSoapEncFix");
/* 187 */     if (JavaUtils.isTrue(dotnet))
/*     */     {
/* 191 */       org.apache.axis.encoding.TypeMappingImpl.dotnet_soapenc_bugfix = true;
/*     */     }
/*     */ 
/* 194 */     if (log.isDebugEnabled())
/* 195 */       log.debug("Exit: AxisEngine::init");
/*     */   }
/*     */ 
/*     */   public void cleanup()
/*     */   {
/* 207 */     super.cleanup();
/*     */ 
/* 211 */     Enumeration keys = this.session.getKeys();
/* 212 */     if (keys != null)
/* 213 */       while (keys.hasMoreElements()) {
/* 214 */         String key = (String)keys.nextElement();
/* 215 */         Object obj = this.session.get(key);
/* 216 */         if ((obj != null) && ((obj instanceof ServiceLifecycle))) {
/* 217 */           ((ServiceLifecycle)obj).destroy();
/*     */         }
/* 219 */         this.session.remove(key);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void saveConfiguration()
/*     */   {
/* 228 */     if (!this.shouldSaveConfig)
/* 229 */       return;
/*     */     try
/*     */     {
/* 232 */       this.config.writeEngineConfig(this);
/*     */     } catch (Exception e) {
/* 234 */       log.error(Messages.getMessage("saveConfigFail00"), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public EngineConfiguration getConfig()
/*     */   {
/* 245 */     return this.config;
/*     */   }
/*     */ 
/*     */   public boolean hasSafePassword()
/*     */   {
/* 255 */     return this._hasSafePassword;
/*     */   }
/*     */ 
/*     */   public void setAdminPassword(String pw)
/*     */   {
/* 265 */     setOption("adminPassword", pw);
/* 266 */     this._hasSafePassword = true;
/* 267 */     saveConfiguration();
/*     */   }
/*     */ 
/*     */   public void setShouldSaveConfig(boolean shouldSaveConfig)
/*     */   {
/* 278 */     this.shouldSaveConfig = shouldSaveConfig;
/*     */   }
/*     */ 
/*     */   public Handler getHandler(String name)
/*     */     throws AxisFault
/*     */   {
/*     */     try
/*     */     {
/* 293 */       return this.config.getHandler(new QName(null, name)); } catch (ConfigurationException e) {
/*     */     }
/* 295 */     throw new AxisFault(e);
/*     */   }
/*     */ 
/*     */   public SOAPService getService(String name)
/*     */     throws AxisFault
/*     */   {
/*     */     try
/*     */     {
/* 311 */       return this.config.getService(new QName(null, name));
/*     */     } catch (ConfigurationException e) {
/*     */       try {
/* 314 */         return this.config.getServiceByNamespaceURI(name); } catch (ConfigurationException e1) {
/*     */       }
/*     */     }
/* 316 */     throw new AxisFault(e);
/*     */   }
/*     */ 
/*     */   public Handler getTransport(String name)
/*     */     throws AxisFault
/*     */   {
/*     */     try
/*     */     {
/* 332 */       return this.config.getTransport(new QName(null, name)); } catch (ConfigurationException e) {
/*     */     }
/* 334 */     throw new AxisFault(e);
/*     */   }
/*     */ 
/*     */   public TypeMappingRegistry getTypeMappingRegistry()
/*     */   {
/* 346 */     TypeMappingRegistry tmr = null;
/*     */     try {
/* 348 */       tmr = this.config.getTypeMappingRegistry();
/*     */     } catch (ConfigurationException e) {
/* 350 */       log.error(Messages.getMessage("axisConfigurationException00"), e);
/*     */     }
/*     */ 
/* 353 */     return tmr;
/*     */   }
/*     */ 
/*     */   public Handler getGlobalRequest()
/*     */     throws ConfigurationException
/*     */   {
/* 365 */     return this.config.getGlobalRequest();
/*     */   }
/*     */ 
/*     */   public Handler getGlobalResponse()
/*     */     throws ConfigurationException
/*     */   {
/* 377 */     return this.config.getGlobalResponse();
/*     */   }
/*     */ 
/*     */   public ArrayList getActorURIs()
/*     */   {
/* 390 */     return (ArrayList)this.actorURIs.clone();
/*     */   }
/*     */ 
/*     */   public void addActorURI(String uri)
/*     */   {
/* 400 */     this.actorURIs.add(uri);
/*     */   }
/*     */ 
/*     */   public void removeActorURI(String uri)
/*     */   {
/* 410 */     this.actorURIs.remove(uri);
/*     */   }
/*     */ 
/*     */   public abstract AxisEngine getClientEngine();
/*     */ 
/*     */   public static void normaliseOptions(Handler handler)
/*     */   {
/* 460 */     for (int i = 0; i < BOOLEAN_OPTIONS.length; i++) {
/* 461 */       Object val = handler.getOption(BOOLEAN_OPTIONS[i]);
/* 462 */       if (val != null) {
/* 463 */         if ((val instanceof Boolean))
/*     */           continue;
/* 465 */         if (JavaUtils.isFalse(val)) {
/* 466 */           handler.setOption(BOOLEAN_OPTIONS[i], Boolean.FALSE);
/* 467 */           continue;
/*     */         }
/*     */       } else {
/* 470 */         if (!(handler instanceof AxisEngine)) {
/*     */           continue;
/*     */         }
/*     */       }
/* 474 */       handler.setOption(BOOLEAN_OPTIONS[i], Boolean.TRUE);
/*     */     }
/*     */ 
/* 478 */     if ((handler instanceof AxisEngine)) {
/* 479 */       AxisEngine engine = (AxisEngine)handler;
/* 480 */       if (!engine.setOptionDefault("adminPassword", "admin"))
/*     */       {
/* 482 */         engine.setAdminPassword((String)engine.getOption("adminPassword"));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void refreshGlobalOptions()
/*     */     throws ConfigurationException
/*     */   {
/* 494 */     Hashtable globalOptions = this.config.getGlobalOptions();
/* 495 */     if (globalOptions != null) {
/* 496 */       setOptions(globalOptions);
/*     */     }
/* 498 */     normaliseOptions(this);
/*     */ 
/* 502 */     this.actorURIs = new ArrayList(this.config.getRoles());
/*     */   }
/*     */ 
/*     */   public Session getApplicationSession()
/*     */   {
/* 512 */     return this.session;
/*     */   }
/*     */ 
/*     */   public ClassCache getClassCache()
/*     */   {
/* 521 */     return this.classCache;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.AxisEngine
 * JD-Core Version:    0.6.0
 */