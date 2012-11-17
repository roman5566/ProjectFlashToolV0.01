/*     */ package org.apache.axis.providers.java;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.rmi.ServerException;
/*     */ import java.util.Properties;
/*     */ import javax.naming.InitialContext;
/*     */ import javax.naming.NamingException;
/*     */ import javax.rmi.PortableRemoteObject;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Handler;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.handlers.soap.SOAPService;
/*     */ import org.apache.axis.utils.ClassUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class EJBProvider extends RPCProvider
/*     */ {
/*  45 */   protected static Log log = LogFactory.getLog(EJBProvider.class.getName());
/*     */ 
/*  51 */   protected static Log entLog = LogFactory.getLog("org.apache.axis.enterprise");
/*     */   public static final String OPTION_BEANNAME = "beanJndiName";
/*     */   public static final String OPTION_HOMEINTERFACENAME = "homeInterfaceName";
/*     */   public static final String OPTION_REMOTEINTERFACENAME = "remoteInterfaceName";
/*     */   public static final String OPTION_LOCALHOMEINTERFACENAME = "localHomeInterfaceName";
/*     */   public static final String OPTION_LOCALINTERFACENAME = "localInterfaceName";
/*     */   public static final String jndiContextClass = "jndiContextClass";
/*     */   public static final String jndiURL = "jndiURL";
/*     */   public static final String jndiUsername = "jndiUser";
/*     */   public static final String jndiPassword = "jndiPassword";
/*  66 */   protected static final Class[] empty_class_array = new Class[0];
/*  67 */   protected static final Object[] empty_object_array = new Object[0];
/*     */ 
/*  69 */   private static InitialContext cached_context = null;
/*     */ 
/*     */   protected Object makeNewServiceObject(MessageContext msgContext, String clsName)
/*     */     throws Exception
/*     */   {
/*  89 */     String remoteHomeName = getStrOption("homeInterfaceName", msgContext.getService());
/*     */ 
/*  91 */     String localHomeName = getStrOption("localHomeInterfaceName", msgContext.getService());
/*     */ 
/*  93 */     String homeName = remoteHomeName != null ? remoteHomeName : localHomeName;
/*     */ 
/*  95 */     if (homeName == null)
/*     */     {
/*  97 */       throw new AxisFault(Messages.getMessage("noOption00", "homeInterfaceName", msgContext.getTargetService()));
/*     */     }
/*     */ 
/* 104 */     Class homeClass = ClassUtils.forName(homeName, true, msgContext.getClassLoader());
/*     */ 
/* 107 */     if (remoteHomeName != null) {
/* 108 */       return createRemoteEJB(msgContext, clsName, homeClass);
/*     */     }
/* 110 */     return createLocalEJB(msgContext, clsName, homeClass);
/*     */   }
/*     */ 
/*     */   private Object createRemoteEJB(MessageContext msgContext, String beanJndiName, Class homeClass)
/*     */     throws Exception
/*     */   {
/* 127 */     Object ejbHome = getEJBHome(msgContext.getService(), msgContext, beanJndiName);
/*     */ 
/* 129 */     Object ehome = PortableRemoteObject.narrow(ejbHome, homeClass);
/*     */ 
/* 133 */     Method createMethod = homeClass.getMethod("create", empty_class_array);
/* 134 */     Object result = createMethod.invoke(ehome, empty_object_array);
/*     */ 
/* 136 */     return result;
/*     */   }
/*     */ 
/*     */   private Object createLocalEJB(MessageContext msgContext, String beanJndiName, Class homeClass)
/*     */     throws Exception
/*     */   {
/* 153 */     Object ejbHome = getEJBHome(msgContext.getService(), msgContext, beanJndiName);
/*     */     Object ehome;
/* 158 */     if (homeClass.isInstance(ejbHome))
/* 159 */       ehome = ejbHome;
/*     */     else
/* 161 */       throw new ClassCastException(Messages.getMessage("badEjbHomeType"));
/*     */     Object ehome;
/* 166 */     Method createMethod = homeClass.getMethod("create", empty_class_array);
/* 167 */     Object result = createMethod.invoke(ehome, empty_object_array);
/*     */ 
/* 169 */     return result;
/*     */   }
/*     */ 
/*     */   private boolean isRemoteEjb(SOAPService service)
/*     */   {
/* 178 */     return getStrOption("homeInterfaceName", service) != null;
/*     */   }
/*     */ 
/*     */   private boolean isLocalEjb(SOAPService service)
/*     */   {
/* 187 */     return (!isRemoteEjb(service)) && (getStrOption("localHomeInterfaceName", service) != null);
/*     */   }
/*     */ 
/*     */   protected String getServiceClassNameOptionName()
/*     */   {
/* 198 */     return "beanJndiName";
/*     */   }
/*     */ 
/*     */   protected String getStrOption(String optionName, Handler service)
/*     */   {
/* 213 */     String value = null;
/* 214 */     if (service != null)
/* 215 */       value = (String)service.getOption(optionName);
/* 216 */     if (value == null)
/* 217 */       value = (String)getOption(optionName);
/* 218 */     return value;
/*     */   }
/*     */ 
/*     */   private Class getRemoteInterfaceClassFromHome(String beanJndiName, SOAPService service, MessageContext msgContext)
/*     */     throws Exception
/*     */   {
/* 235 */     Object ejbHome = getEJBHome(service, msgContext, beanJndiName);
/*     */ 
/* 237 */     String homeName = getStrOption("homeInterfaceName", service);
/*     */ 
/* 239 */     if (homeName == null) {
/* 240 */       throw new AxisFault(Messages.getMessage("noOption00", "homeInterfaceName", service.getName()));
/*     */     }
/*     */ 
/* 246 */     ClassLoader cl = msgContext != null ? msgContext.getClassLoader() : Thread.currentThread().getContextClassLoader();
/*     */ 
/* 249 */     Class homeClass = ClassUtils.forName(homeName, true, cl);
/*     */ 
/* 254 */     Object ehome = PortableRemoteObject.narrow(ejbHome, homeClass);
/*     */ 
/* 263 */     Method getEJBMetaData = homeClass.getMethod("getEJBMetaData", empty_class_array);
/*     */ 
/* 265 */     Object metaData = getEJBMetaData.invoke(ehome, empty_object_array);
/* 266 */     Method getRemoteInterfaceClass = metaData.getClass().getMethod("getRemoteInterfaceClass", empty_class_array);
/*     */ 
/* 269 */     return (Class)getRemoteInterfaceClass.invoke(metaData, empty_object_array);
/*     */   }
/*     */ 
/*     */   protected Class getServiceClass(String beanJndiName, SOAPService service, MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/* 287 */     Class interfaceClass = null;
/*     */     try
/*     */     {
/* 293 */       String remoteInterfaceName = getStrOption("remoteInterfaceName", service);
/*     */ 
/* 295 */       String localInterfaceName = getStrOption("localInterfaceName", service);
/*     */ 
/* 297 */       String interfaceName = remoteInterfaceName != null ? remoteInterfaceName : localInterfaceName;
/*     */ 
/* 299 */       if (interfaceName != null) {
/* 300 */         ClassLoader cl = msgContext != null ? msgContext.getClassLoader() : Thread.currentThread().getContextClassLoader();
/*     */ 
/* 303 */         interfaceClass = ClassUtils.forName(interfaceName, true, cl);
/*     */       }
/* 311 */       else if (isRemoteEjb(service)) {
/* 312 */         interfaceClass = getRemoteInterfaceClassFromHome(beanJndiName, service, msgContext);
/*     */       }
/*     */       else
/*     */       {
/* 317 */         if (isLocalEjb(service))
/*     */         {
/* 320 */           throw new AxisFault(Messages.getMessage("noOption00", "localInterfaceName", service.getName()));
/*     */         }
/*     */ 
/* 328 */         throw new AxisFault(Messages.getMessage("noOption00", "homeInterfaceName", service.getName()));
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 334 */       throw AxisFault.makeFault(e);
/*     */     }
/*     */ 
/* 338 */     return interfaceClass;
/*     */   }
/*     */ 
/*     */   private Object getEJBHome(SOAPService serviceHandler, MessageContext msgContext, String beanJndiName)
/*     */     throws AxisFault
/*     */   {
/* 351 */     Object ejbHome = null;
/*     */     try
/*     */     {
/* 355 */       Properties properties = null;
/*     */ 
/* 361 */       String username = getStrOption("jndiUser", serviceHandler);
/* 362 */       if ((username == null) && (msgContext != null))
/* 363 */         username = msgContext.getUsername();
/* 364 */       if (username != null) {
/* 365 */         if (properties == null)
/* 366 */           properties = new Properties();
/* 367 */         properties.setProperty("java.naming.security.principal", username);
/*     */       }
/*     */ 
/* 371 */       String password = getStrOption("jndiPassword", serviceHandler);
/* 372 */       if ((password == null) && (msgContext != null))
/* 373 */         password = msgContext.getPassword();
/* 374 */       if (password != null) {
/* 375 */         if (properties == null)
/* 376 */           properties = new Properties();
/* 377 */         properties.setProperty("java.naming.security.credentials", password);
/*     */       }
/*     */ 
/* 381 */       String factoryClass = getStrOption("jndiContextClass", serviceHandler);
/* 382 */       if (factoryClass != null) {
/* 383 */         if (properties == null)
/* 384 */           properties = new Properties();
/* 385 */         properties.setProperty("java.naming.factory.initial", factoryClass);
/*     */       }
/*     */ 
/* 389 */       String contextUrl = getStrOption("jndiURL", serviceHandler);
/* 390 */       if (contextUrl != null) {
/* 391 */         if (properties == null)
/* 392 */           properties = new Properties();
/* 393 */         properties.setProperty("java.naming.provider.url", contextUrl);
/*     */       }
/*     */ 
/* 397 */       InitialContext context = getContext(properties);
/*     */ 
/* 400 */       if (context == null) {
/* 401 */         throw new AxisFault(Messages.getMessage("cannotCreateInitialContext00"));
/*     */       }
/* 403 */       ejbHome = getEJBHome(context, beanJndiName);
/*     */ 
/* 405 */       if (ejbHome == null)
/* 406 */         throw new AxisFault(Messages.getMessage("cannotFindJNDIHome00", beanJndiName));
/*     */     }
/*     */     catch (Exception exception)
/*     */     {
/* 410 */       entLog.info(Messages.getMessage("toAxisFault00"), exception);
/* 411 */       throw AxisFault.makeFault(exception);
/*     */     }
/*     */ 
/* 414 */     return ejbHome;
/*     */   }
/*     */ 
/*     */   protected InitialContext getCachedContext()
/*     */     throws NamingException
/*     */   {
/* 420 */     if (cached_context == null)
/* 421 */       cached_context = new InitialContext();
/* 422 */     return cached_context;
/*     */   }
/*     */ 
/*     */   protected InitialContext getContext(Properties properties)
/*     */     throws AxisFault, NamingException
/*     */   {
/* 432 */     return properties == null ? getCachedContext() : new InitialContext(properties);
/*     */   }
/*     */ 
/*     */   protected Object getEJBHome(InitialContext context, String beanJndiName)
/*     */     throws AxisFault, NamingException
/*     */   {
/* 441 */     return context.lookup(beanJndiName);
/*     */   }
/*     */ 
/*     */   protected Object invokeMethod(MessageContext msgContext, Method method, Object obj, Object[] argValues)
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 459 */       return super.invokeMethod(msgContext, method, obj, argValues);
/*     */     } catch (InvocationTargetException ite) {
/* 461 */       Throwable cause = getCause(ite);
/* 462 */       if ((cause instanceof ServerException))
/* 463 */         throw new InvocationTargetException(getCause(cause));
/*     */     }
/* 465 */     throw ite;
/*     */   }
/*     */ 
/*     */   private Throwable getCause(Throwable original)
/*     */   {
/*     */     try
/*     */     {
/* 478 */       Method method = original.getClass().getMethod("getCause", null);
/* 479 */       Throwable cause = (Throwable)method.invoke(original, null);
/* 480 */       if (cause != null)
/* 481 */         return cause;
/*     */     }
/*     */     catch (NoSuchMethodException nsme) {
/*     */     }
/*     */     catch (Throwable t) {
/*     */     }
/* 487 */     return original;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.providers.java.EJBProvider
 * JD-Core Version:    0.6.0
 */