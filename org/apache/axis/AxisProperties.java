/*     */ package org.apache.axis;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.discovery.ResourceClass;
/*     */ import org.apache.commons.discovery.ResourceClassIterator;
/*     */ import org.apache.commons.discovery.ResourceNameDiscover;
/*     */ import org.apache.commons.discovery.ResourceNameIterator;
/*     */ import org.apache.commons.discovery.resource.ClassLoaders;
/*     */ import org.apache.commons.discovery.resource.classes.DiscoverClasses;
/*     */ import org.apache.commons.discovery.resource.names.DiscoverMappedNames;
/*     */ import org.apache.commons.discovery.resource.names.DiscoverNamesInAlternateManagedProperties;
/*     */ import org.apache.commons.discovery.resource.names.DiscoverNamesInManagedProperties;
/*     */ import org.apache.commons.discovery.resource.names.DiscoverServiceNames;
/*     */ import org.apache.commons.discovery.resource.names.NameDiscoverers;
/*     */ import org.apache.commons.discovery.tools.ClassUtils;
/*     */ import org.apache.commons.discovery.tools.DefaultClassHolder;
/*     */ import org.apache.commons.discovery.tools.DiscoverClass;
/*     */ import org.apache.commons.discovery.tools.ManagedProperties;
/*     */ import org.apache.commons.discovery.tools.PropertiesHolder;
/*     */ import org.apache.commons.discovery.tools.SPInterface;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class AxisProperties
/*     */ {
/*  79 */   protected static Log log = LogFactory.getLog(AxisProperties.class.getName());
/*     */   private static DiscoverNamesInAlternateManagedProperties altNameDiscoverer;
/*     */   private static DiscoverMappedNames mappedNames;
/*     */   private static NameDiscoverers nameDiscoverer;
/*     */   private static ClassLoaders loaders;
/*     */ 
/*     */   public static void setClassOverrideProperty(Class clazz, String propertyName)
/*     */   {
/*  88 */     getAlternatePropertyNameDiscoverer().addClassToPropertyNameMapping(clazz.getName(), propertyName);
/*     */   }
/*     */ 
/*     */   public static void setClassDefault(Class clazz, String defaultName)
/*     */   {
/*  93 */     getMappedNames().map(clazz.getName(), defaultName);
/*     */   }
/*     */ 
/*     */   public static void setClassDefaults(Class clazz, String[] defaultNames) {
/*  97 */     getMappedNames().map(clazz.getName(), defaultNames);
/*     */   }
/*     */ 
/*     */   public static synchronized ResourceNameDiscover getNameDiscoverer() {
/* 101 */     if (nameDiscoverer == null) {
/* 102 */       nameDiscoverer = new NameDiscoverers();
/* 103 */       nameDiscoverer.addResourceNameDiscover(getAlternatePropertyNameDiscoverer());
/* 104 */       nameDiscoverer.addResourceNameDiscover(new DiscoverNamesInManagedProperties());
/* 105 */       nameDiscoverer.addResourceNameDiscover(new DiscoverServiceNames(getClassLoaders()));
/* 106 */       nameDiscoverer.addResourceNameDiscover(getMappedNames());
/*     */     }
/* 108 */     return nameDiscoverer;
/*     */   }
/*     */ 
/*     */   public static ResourceClassIterator getResourceClassIterator(Class spi) {
/* 112 */     ResourceNameIterator it = getNameDiscoverer().findResourceNames(spi.getName());
/* 113 */     return new DiscoverClasses(loaders).findResourceClasses(it);
/*     */   }
/*     */ 
/*     */   private static synchronized ClassLoaders getClassLoaders() {
/* 117 */     if (loaders == null) {
/* 118 */       loaders = ClassLoaders.getAppLoaders(AxisProperties.class, null, true);
/*     */     }
/* 120 */     return loaders;
/*     */   }
/*     */ 
/*     */   private static synchronized DiscoverMappedNames getMappedNames() {
/* 124 */     if (mappedNames == null) {
/* 125 */       mappedNames = new DiscoverMappedNames();
/*     */     }
/* 127 */     return mappedNames;
/*     */   }
/*     */ 
/*     */   private static synchronized DiscoverNamesInAlternateManagedProperties getAlternatePropertyNameDiscoverer() {
/* 131 */     if (altNameDiscoverer == null) {
/* 132 */       altNameDiscoverer = new DiscoverNamesInAlternateManagedProperties();
/*     */     }
/*     */ 
/* 135 */     return altNameDiscoverer;
/*     */   }
/*     */ 
/*     */   public static Object newInstance(Class spiClass)
/*     */   {
/* 160 */     return newInstance(spiClass, null, null);
/*     */   }
/*     */ 
/*     */   public static Object newInstance(Class spiClass, Class[] constructorParamTypes, Object[] constructorParams)
/*     */   {
/* 166 */     return AccessController.doPrivileged(new PrivilegedAction(spiClass, constructorParamTypes, constructorParams) { private final Class val$spiClass;
/*     */       private final Class[] val$constructorParamTypes;
/*     */       private final Object[] val$constructorParams;
/*     */ 
/* 169 */       public Object run() { ResourceClassIterator services = AxisProperties.getResourceClassIterator(this.val$spiClass);
/*     */ 
/* 171 */         Object obj = null;
/* 172 */         while ((obj == null) && (services.hasNext())) {
/* 173 */           Class service = services.nextResourceClass().loadClass();
/*     */ 
/* 178 */           if (service != null)
/*     */           {
/*     */             try
/*     */             {
/* 182 */               ClassUtils.verifyAncestory(this.val$spiClass, service);
/* 183 */               obj = ClassUtils.newInstance(service, this.val$constructorParamTypes, this.val$constructorParams);
/*     */             } catch (InvocationTargetException e) {
/* 185 */               if ((e.getTargetException() instanceof NoClassDefFoundError))
/* 186 */                 AxisProperties.log.debug(Messages.getMessage("exception00"), e);
/*     */               else
/* 188 */                 AxisProperties.log.warn(Messages.getMessage("exception00"), e);
/*     */             }
/*     */             catch (Exception e) {
/* 191 */               AxisProperties.log.warn(Messages.getMessage("exception00"), e);
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/* 196 */         return obj;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public static String getProperty(String propertyName)
/*     */   {
/* 209 */     return ManagedProperties.getProperty(propertyName);
/*     */   }
/*     */ 
/*     */   public static String getProperty(String propertyName, String dephault)
/*     */   {
/* 221 */     return ManagedProperties.getProperty(propertyName, dephault);
/*     */   }
/*     */ 
/*     */   public static void setProperty(String propertyName, String value)
/*     */   {
/* 230 */     ManagedProperties.setProperty(propertyName, value);
/*     */   }
/*     */ 
/*     */   public static void setProperty(String propertyName, String value, boolean isDefault)
/*     */   {
/* 244 */     ManagedProperties.setProperty(propertyName, value, isDefault);
/*     */   }
/*     */ 
/*     */   public static void setProperties(Map newProperties)
/*     */   {
/* 254 */     ManagedProperties.setProperties(newProperties);
/*     */   }
/*     */ 
/*     */   public static void setProperties(Map newProperties, boolean isDefault)
/*     */   {
/* 270 */     ManagedProperties.setProperties(newProperties, isDefault);
/*     */   }
/*     */ 
/*     */   public static Enumeration propertyNames()
/*     */   {
/* 275 */     return ManagedProperties.propertyNames();
/*     */   }
/*     */ 
/*     */   public static Properties getProperties()
/*     */   {
/* 288 */     return ManagedProperties.getProperties();
/*     */   }
/*     */ 
/*     */   public static Object newInstance(Class spiClass, Class defaultClass)
/*     */   {
/* 294 */     return newInstance(new SPInterface(spiClass), new DefaultClassHolder(defaultClass));
/*     */   }
/*     */ 
/*     */   private static Object newInstance(SPInterface spi, DefaultClassHolder defaultClass)
/*     */   {
/* 314 */     return AccessController.doPrivileged(new PrivilegedAction(spi, defaultClass) { private final SPInterface val$spi;
/*     */       private final DefaultClassHolder val$defaultClass;
/*     */ 
/*     */       public Object run() { try { return DiscoverClass.newInstance(null, this.val$spi, (PropertiesHolder)null, this.val$defaultClass);
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 323 */           AxisProperties.log.error(Messages.getMessage("exception00"), e);
/*     */         }
/* 325 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.AxisProperties
 * JD-Core Version:    0.6.0
 */