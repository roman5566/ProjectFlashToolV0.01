/*     */ package org.apache.axis.deployment.wsdd;
/*     */ 
/*     */ import java.util.Hashtable;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.EngineConfiguration;
/*     */ import org.apache.axis.Handler;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.deployment.wsdd.providers.WSDDBsfProvider;
/*     */ import org.apache.axis.deployment.wsdd.providers.WSDDComProvider;
/*     */ import org.apache.axis.deployment.wsdd.providers.WSDDHandlerProvider;
/*     */ import org.apache.axis.deployment.wsdd.providers.WSDDJavaCORBAProvider;
/*     */ import org.apache.axis.deployment.wsdd.providers.WSDDJavaEJBProvider;
/*     */ import org.apache.axis.deployment.wsdd.providers.WSDDJavaMsgProvider;
/*     */ import org.apache.axis.deployment.wsdd.providers.WSDDJavaRMIProvider;
/*     */ import org.apache.axis.deployment.wsdd.providers.WSDDJavaRPCProvider;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.discovery.ResourceNameIterator;
/*     */ import org.apache.commons.discovery.resource.ClassLoaders;
/*     */ import org.apache.commons.discovery.resource.names.DiscoverServiceNames;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public abstract class WSDDProvider
/*     */ {
/*  54 */   protected static Log log = LogFactory.getLog(WSDDProvider.class.getName());
/*     */   private static final String PLUGABLE_PROVIDER_FILENAME = "org.apache.axis.deployment.wsdd.Provider";
/*  62 */   private static Hashtable providers = new Hashtable();
/*     */ 
/*     */   private static void loadPluggableProviders()
/*     */   {
/*  98 */     ClassLoader clzLoader = WSDDProvider.class.getClassLoader();
/*  99 */     ClassLoaders loaders = new ClassLoaders();
/* 100 */     loaders.put(clzLoader);
/* 101 */     DiscoverServiceNames dsn = new DiscoverServiceNames(loaders);
/* 102 */     ResourceNameIterator iter = dsn.findResourceNames("org.apache.axis.deployment.wsdd.Provider");
/* 103 */     while (iter.hasNext()) {
/* 104 */       String className = iter.nextResourceName();
/*     */       try {
/* 106 */         Object o = Class.forName(className).newInstance();
/* 107 */         if ((o instanceof WSDDProvider)) {
/* 108 */           WSDDProvider provider = (WSDDProvider)o;
/* 109 */           String providerName = provider.getName();
/* 110 */           QName q = new QName("http://xml.apache.org/axis/wsdd/providers/java", providerName);
/* 111 */           providers.put(q, provider);
/*     */         }
/*     */       } catch (Exception e) {
/* 114 */         String msg = e + JavaUtils.LS + JavaUtils.stackToString(e);
/* 115 */         log.info(Messages.getMessage("exception01", msg));
/* 116 */       }continue;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void registerProvider(QName uri, WSDDProvider prov)
/*     */   {
/* 128 */     providers.put(uri, prov);
/*     */   }
/*     */ 
/*     */   public WSDDOperation[] getOperations()
/*     */   {
/* 137 */     return null;
/*     */   }
/*     */ 
/*     */   public WSDDOperation getOperation(String name)
/*     */   {
/* 147 */     return null;
/*     */   }
/*     */ 
/*     */   public static Handler getInstance(QName providerType, WSDDService service, EngineConfiguration registry)
/*     */     throws Exception
/*     */   {
/* 161 */     if (providerType == null) {
/* 162 */       throw new WSDDException(Messages.getMessage("nullProvider00"));
/*     */     }
/* 164 */     WSDDProvider provider = (WSDDProvider)providers.get(providerType);
/* 165 */     if (provider == null) {
/* 166 */       throw new WSDDException(Messages.getMessage("noMatchingProvider00", providerType.toString()));
/*     */     }
/*     */ 
/* 170 */     return provider.newProviderInstance(service, registry);
/*     */   }
/*     */ 
/*     */   public abstract Handler newProviderInstance(WSDDService paramWSDDService, EngineConfiguration paramEngineConfiguration)
/*     */     throws Exception;
/*     */ 
/*     */   public abstract String getName();
/*     */ 
/*     */   static
/*     */   {
/*  65 */     providers.put(WSDDConstants.QNAME_JAVARPC_PROVIDER, new WSDDJavaRPCProvider());
/*  66 */     providers.put(WSDDConstants.QNAME_JAVAMSG_PROVIDER, new WSDDJavaMsgProvider());
/*  67 */     providers.put(WSDDConstants.QNAME_HANDLER_PROVIDER, new WSDDHandlerProvider());
/*  68 */     providers.put(WSDDConstants.QNAME_EJB_PROVIDER, new WSDDJavaEJBProvider());
/*  69 */     providers.put(WSDDConstants.QNAME_COM_PROVIDER, new WSDDComProvider());
/*  70 */     providers.put(WSDDConstants.QNAME_BSF_PROVIDER, new WSDDBsfProvider());
/*  71 */     providers.put(WSDDConstants.QNAME_CORBA_PROVIDER, new WSDDJavaCORBAProvider());
/*  72 */     providers.put(WSDDConstants.QNAME_RMI_PROVIDER, new WSDDJavaRMIProvider());
/*     */     try {
/*  74 */       loadPluggableProviders();
/*     */     } catch (Throwable t) {
/*  76 */       String msg = t + JavaUtils.LS + JavaUtils.stackToString(t);
/*  77 */       log.info(Messages.getMessage("exception01", msg));
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.WSDDProvider
 * JD-Core Version:    0.6.0
 */