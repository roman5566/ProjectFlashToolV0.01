/*     */ package org.apache.axis.client;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.net.URL;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.InitialContext;
/*     */ import javax.naming.Name;
/*     */ import javax.naming.NamingException;
/*     */ import javax.naming.RefAddr;
/*     */ import javax.naming.Reference;
/*     */ import javax.naming.spi.ObjectFactory;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.rpc.ServiceException;
/*     */ import org.apache.axis.EngineConfiguration;
/*     */ import org.apache.axis.EngineConfigurationFactory;
/*     */ import org.apache.axis.configuration.EngineConfigurationFactoryFinder;
/*     */ import org.apache.axis.utils.ClassUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class ServiceFactory extends javax.xml.rpc.ServiceFactory
/*     */   implements ObjectFactory
/*     */ {
/*     */   public static final String SERVICE_CLASSNAME = "service classname";
/*     */   public static final String WSDL_LOCATION = "WSDL location";
/*     */   public static final String MAINTAIN_SESSION = "maintain session";
/*     */   public static final String SERVICE_NAMESPACE = "service namespace";
/*     */   public static final String SERVICE_LOCAL_PART = "service local part";
/*     */   public static final String SERVICE_IMPLEMENTATION_NAME_PROPERTY = "serviceImplementationName";
/*     */   private static final String SERVICE_IMPLEMENTATION_SUFFIX = "Locator";
/*  60 */   private static EngineConfiguration _defaultEngineConfig = null;
/*     */ 
/*  62 */   private static ThreadLocal threadDefaultConfig = new ThreadLocal();
/*     */ 
/*     */   public static void setThreadDefaultConfig(EngineConfiguration config)
/*     */   {
/*  66 */     threadDefaultConfig.set(config);
/*     */   }
/*     */ 
/*     */   private static EngineConfiguration getDefaultEngineConfig() {
/*  70 */     if (_defaultEngineConfig == null) {
/*  71 */       _defaultEngineConfig = EngineConfigurationFactoryFinder.newFactory().getClientEngineConfig();
/*     */     }
/*     */ 
/*  74 */     return _defaultEngineConfig;
/*     */   }
/*     */ 
/*     */   public static Service getService(Map environment)
/*     */   {
/*  88 */     Service service = null;
/*  89 */     InitialContext context = null;
/*     */ 
/*  91 */     EngineConfiguration configProvider = (EngineConfiguration)environment.get("engineConfig");
/*     */ 
/*  94 */     if (configProvider == null) {
/*  95 */       configProvider = (EngineConfiguration)threadDefaultConfig.get();
/*     */     }
/*  97 */     if (configProvider == null) {
/*  98 */       configProvider = getDefaultEngineConfig();
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 103 */       context = new InitialContext();
/*     */     }
/*     */     catch (NamingException e) {
/*     */     }
/* 107 */     if (context != null) {
/* 108 */       String name = (String)environment.get("jndiName");
/* 109 */       if (name == null) {
/* 110 */         name = "axisServiceName";
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 116 */         service = (Service)context.lookup(name);
/*     */       } catch (NamingException e) {
/* 118 */         service = new Service(configProvider);
/*     */         try {
/* 120 */           context.bind(name, service);
/*     */         } catch (NamingException e1) {
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 126 */       service = new Service(configProvider);
/*     */     }
/*     */ 
/* 129 */     return service;
/*     */   }
/*     */ 
/*     */   public Object getObjectInstance(Object refObject, Name name, Context nameCtx, Hashtable environment)
/*     */     throws Exception
/*     */   {
/* 135 */     Object instance = null;
/* 136 */     if ((refObject instanceof Reference)) {
/* 137 */       Reference ref = (Reference)refObject;
/*     */ 
/* 139 */       RefAddr addr = ref.get("service classname");
/* 140 */       Object obj = null;
/*     */ 
/* 143 */       if ((addr != null) && (((obj = addr.getContent()) instanceof String))) {
/* 144 */         instance = ClassUtils.forName((String)obj).newInstance();
/*     */       }
/*     */       else
/*     */       {
/* 150 */         addr = ref.get("WSDL location");
/* 151 */         if ((addr != null) && (((obj = addr.getContent()) instanceof String))) {
/* 152 */           URL wsdlLocation = new URL((String)obj);
/*     */ 
/* 155 */           addr = ref.get("service namespace");
/* 156 */           if ((addr != null) && (((obj = addr.getContent()) instanceof String)))
/*     */           {
/* 158 */             String namespace = (String)obj;
/* 159 */             addr = ref.get("service local part");
/* 160 */             if ((addr != null) && (((obj = addr.getContent()) instanceof String)))
/*     */             {
/* 162 */               String localPart = (String)obj;
/* 163 */               QName serviceName = new QName(namespace, localPart);
/*     */ 
/* 166 */               Class[] formalArgs = { URL.class, QName.class };
/*     */ 
/* 168 */               Object[] actualArgs = { wsdlLocation, serviceName };
/*     */ 
/* 170 */               Constructor ctor = Service.class.getDeclaredConstructor(formalArgs);
/*     */ 
/* 173 */               instance = ctor.newInstance(actualArgs);
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 180 */       addr = ref.get("maintain session");
/* 181 */       if ((addr != null) && ((instance instanceof Service))) {
/* 182 */         ((Service)instance).setMaintainSession(true);
/*     */       }
/*     */     }
/* 185 */     return instance;
/*     */   }
/*     */ 
/*     */   public javax.xml.rpc.Service createService(URL wsdlDocumentLocation, QName serviceName)
/*     */     throws ServiceException
/*     */   {
/* 198 */     return new Service(wsdlDocumentLocation, serviceName);
/*     */   }
/*     */ 
/*     */   public javax.xml.rpc.Service createService(QName serviceName)
/*     */     throws ServiceException
/*     */   {
/* 214 */     return new Service(serviceName);
/*     */   }
/*     */ 
/*     */   public javax.xml.rpc.Service loadService(Class serviceInterface)
/*     */     throws ServiceException
/*     */   {
/* 227 */     if (serviceInterface == null) {
/* 228 */       throw new IllegalArgumentException(Messages.getMessage("serviceFactoryIllegalServiceInterface"));
/*     */     }
/*     */ 
/* 231 */     if (!javax.xml.rpc.Service.class.isAssignableFrom(serviceInterface))
/*     */     {
/* 233 */       throw new ServiceException(Messages.getMessage("serviceFactoryServiceInterfaceRequirement", serviceInterface.getName()));
/*     */     }
/*     */ 
/* 236 */     String serviceImplementationName = serviceInterface.getName() + "Locator";
/* 237 */     Service service = createService(serviceImplementationName);
/* 238 */     return service;
/*     */   }
/*     */ 
/*     */   public javax.xml.rpc.Service loadService(URL wsdlDocumentLocation, Class serviceInterface, Properties properties)
/*     */     throws ServiceException
/*     */   {
/* 259 */     if (serviceInterface == null) {
/* 260 */       throw new IllegalArgumentException(Messages.getMessage("serviceFactoryIllegalServiceInterface"));
/*     */     }
/*     */ 
/* 263 */     if (!javax.xml.rpc.Service.class.isAssignableFrom(serviceInterface))
/*     */     {
/* 265 */       throw new ServiceException(Messages.getMessage("serviceFactoryServiceInterfaceRequirement", serviceInterface.getName()));
/*     */     }
/*     */ 
/* 268 */     String serviceImplementationName = serviceInterface.getName() + "Locator";
/* 269 */     Service service = createService(serviceImplementationName);
/* 270 */     return service;
/*     */   }
/*     */ 
/*     */   public javax.xml.rpc.Service loadService(URL wsdlDocumentLocation, QName serviceName, Properties properties)
/*     */     throws ServiceException
/*     */   {
/* 291 */     String serviceImplementationName = properties.getProperty("serviceImplementationName");
/* 292 */     javax.xml.rpc.Service service = createService(serviceImplementationName);
/* 293 */     if (service.getServiceName().equals(serviceName)) {
/* 294 */       return service;
/*     */     }
/* 296 */     throw new ServiceException(Messages.getMessage("serviceFactoryServiceImplementationNotFound", serviceImplementationName));
/*     */   }
/*     */ 
/*     */   private Service createService(String serviceImplementationName)
/*     */     throws ServiceException
/*     */   {
/* 302 */     if (serviceImplementationName == null) {
/* 303 */       throw new IllegalArgumentException(Messages.getMessage("serviceFactoryInvalidServiceName"));
/*     */     }
/*     */     try
/*     */     {
/* 307 */       Class serviceImplementationClass = Thread.currentThread().getContextClassLoader().loadClass(serviceImplementationName);
/* 308 */       if (!Service.class.isAssignableFrom(serviceImplementationClass)) {
/* 309 */         throw new ServiceException(Messages.getMessage("serviceFactoryServiceImplementationRequirement", serviceImplementationName));
/*     */       }
/*     */ 
/* 312 */       Service service = (Service)serviceImplementationClass.newInstance();
/* 313 */       if (service.getServiceName() != null) {
/* 314 */         return service;
/*     */       }
/* 316 */       throw new ServiceException(Messages.getMessage("serviceFactoryInvalidServiceName"));
/*     */     }
/*     */     catch (ServiceException e) {
/* 319 */       throw e; } catch (Exception e) {
/*     */     }
/* 321 */     throw new ServiceException(e);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.client.ServiceFactory
 * JD-Core Version:    0.6.0
 */