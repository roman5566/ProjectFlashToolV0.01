/*     */ package org.apache.axis.configuration;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import org.apache.axis.AxisProperties;
/*     */ import org.apache.axis.EngineConfigurationFactory;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.discovery.ResourceClass;
/*     */ import org.apache.commons.discovery.ResourceClassIterator;
/*     */ import org.apache.commons.discovery.tools.ClassUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class EngineConfigurationFactoryFinder
/*     */ {
/*  48 */   protected static Log log = LogFactory.getLog(EngineConfigurationFactoryFinder.class.getName());
/*     */ 
/*  51 */   private static final Class mySpi = EngineConfigurationFactory.class;
/*     */ 
/*  53 */   private static final Class[] newFactoryParamTypes = { Object.class };
/*     */   private static final String requiredMethod = "public static EngineConfigurationFactory newFactory(Object)";
/*     */ 
/*     */   public static EngineConfigurationFactory newFactory(Object obj)
/*     */   {
/* 108 */     Object[] params = { obj };
/*     */ 
/* 113 */     return (EngineConfigurationFactory)AccessController.doPrivileged(new PrivilegedAction(params) { private final Object[] val$params;
/*     */ 
/* 116 */       public Object run() { ResourceClassIterator services = AxisProperties.getResourceClassIterator(EngineConfigurationFactoryFinder.mySpi);
/*     */ 
/* 118 */         EngineConfigurationFactory factory = null;
/*     */ 
/* 120 */         while ((factory == null) && (services.hasNext())) {
/*     */           try {
/* 122 */             Class service = services.nextResourceClass().loadClass();
/*     */ 
/* 127 */             if (service != null) {
/* 128 */               factory = EngineConfigurationFactoryFinder.access$200(service, EngineConfigurationFactoryFinder.newFactoryParamTypes, this.val$params);
/*     */             }
/*     */ 
/*     */           }
/*     */           catch (Exception e)
/*     */           {
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 139 */         if (factory != null) {
/* 140 */           if (EngineConfigurationFactoryFinder.log.isDebugEnabled())
/* 141 */             EngineConfigurationFactoryFinder.log.debug(Messages.getMessage("engineFactory", factory.getClass().getName()));
/*     */         }
/*     */         else {
/* 144 */           EngineConfigurationFactoryFinder.log.error(Messages.getMessage("engineConfigFactoryMissing"));
/*     */         }
/*     */ 
/* 154 */         return factory; } } );
/*     */   }
/*     */ 
/*     */   public static EngineConfigurationFactory newFactory()
/*     */   {
/* 160 */     return newFactory(null);
/*     */   }
/*     */ 
/*     */   private static EngineConfigurationFactory newFactory(Class service, Class[] paramTypes, Object[] param)
/*     */   {
/*     */     try
/*     */     {
/* 178 */       Method method = ClassUtils.findPublicStaticMethod(service, EngineConfigurationFactory.class, "newFactory", paramTypes);
/*     */ 
/* 183 */       if (method == null) {
/* 184 */         log.warn(Messages.getMessage("engineConfigMissingNewFactory", service.getName(), "public static EngineConfigurationFactory newFactory(Object)"));
/*     */       }
/*     */       else {
/*     */         try
/*     */         {
/* 189 */           return (EngineConfigurationFactory)method.invoke(null, param);
/*     */         } catch (InvocationTargetException e) {
/* 191 */           if ((e.getTargetException() instanceof NoClassDefFoundError)) {
/* 192 */             log.debug(Messages.getMessage("engineConfigLoadFactory", service.getName()));
/*     */           }
/*     */           else {
/* 195 */             log.warn(Messages.getMessage("engineConfigInvokeNewFactory", service.getName(), "public static EngineConfigurationFactory newFactory(Object)"), e);
/*     */           }
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 200 */           log.warn(Messages.getMessage("engineConfigInvokeNewFactory", service.getName(), "public static EngineConfigurationFactory newFactory(Object)"), e);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (NoClassDefFoundError e)
/*     */     {
/* 206 */       log.debug(Messages.getMessage("engineConfigLoadFactory", service.getName()));
/*     */     }
/*     */ 
/* 210 */     return null;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  60 */     AxisProperties.setClassOverrideProperty(EngineConfigurationFactory.class, "axis.EngineConfigFactory");
/*     */ 
/*  64 */     AxisProperties.setClassDefaults(class$org$apache$axis$EngineConfigurationFactory, new String[] { "org.apache.axis.configuration.EngineConfigurationFactoryServlet", "org.apache.axis.configuration.EngineConfigurationFactoryDefault" });
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.configuration.EngineConfigurationFactoryFinder
 * JD-Core Version:    0.6.0
 */