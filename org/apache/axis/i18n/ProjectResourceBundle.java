/*     */ package org.apache.axis.i18n;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashSet;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Locale;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.ResourceBundle;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class ProjectResourceBundle extends ResourceBundle
/*     */ {
/*  53 */   protected static Log log = LogFactory.getLog(ProjectResourceBundle.class.getName());
/*     */ 
/*  60 */   private static final Hashtable bundleCache = new Hashtable();
/*     */ 
/*  62 */   private static final Locale defaultLocale = Locale.getDefault();
/*     */   private final ResourceBundle resourceBundle;
/*     */   private final String resourceName;
/*     */ 
/*     */   protected Object handleGetObject(String key)
/*     */     throws MissingResourceException
/*     */   {
/*  71 */     if (log.isDebugEnabled())
/*  72 */       log.debug(toString() + "::handleGetObject(" + key + ")");
/*     */     Object obj;
/*     */     try
/*     */     {
/*  77 */       obj = this.resourceBundle.getObject(key);
/*     */     }
/*     */     catch (MissingResourceException e)
/*     */     {
/*     */       Object obj;
/*  83 */       obj = null;
/*     */     }
/*  85 */     return obj;
/*     */   }
/*     */ 
/*     */   public Enumeration getKeys() {
/*  89 */     Enumeration myKeys = this.resourceBundle.getKeys();
/*  90 */     if (this.parent == null) {
/*  91 */       return myKeys;
/*     */     }
/*  93 */     HashSet set = new HashSet();
/*  94 */     while (myKeys.hasMoreElements()) {
/*  95 */       set.add(myKeys.nextElement());
/*     */     }
/*     */ 
/*  98 */     Enumeration pKeys = this.parent.getKeys();
/*  99 */     while (pKeys.hasMoreElements()) {
/* 100 */       set.add(pKeys.nextElement());
/*     */     }
/*     */ 
/* 103 */     return new Enumeration(set) { private Iterator it;
/*     */       private final HashSet val$set;
/*     */ 
/* 105 */       public boolean hasMoreElements() { return this.it.hasNext(); } 
/* 106 */       public Object nextElement() { return this.it.next();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static ProjectResourceBundle getBundle(String projectName, String packageName, String resourceName)
/*     */     throws MissingResourceException
/*     */   {
/* 134 */     return getBundle(projectName, packageName, resourceName, null, null, null);
/*     */   }
/*     */ 
/*     */   public static ProjectResourceBundle getBundle(String projectName, Class caller, String resourceName, Locale locale)
/*     */     throws MissingResourceException
/*     */   {
/* 160 */     return getBundle(projectName, caller, resourceName, locale, null);
/*     */   }
/*     */ 
/*     */   public static ProjectResourceBundle getBundle(String projectName, String packageName, String resourceName, Locale locale, ClassLoader loader)
/*     */     throws MissingResourceException
/*     */   {
/* 193 */     return getBundle(projectName, packageName, resourceName, locale, loader, null);
/*     */   }
/*     */ 
/*     */   public static ProjectResourceBundle getBundle(String projectName, Class caller, String resourceName, Locale locale, ResourceBundle extendsBundle)
/*     */     throws MissingResourceException
/*     */   {
/* 225 */     return getBundle(projectName, getPackage(caller.getClass().getName()), resourceName, locale, caller.getClass().getClassLoader(), extendsBundle);
/*     */   }
/*     */ 
/*     */   public static ProjectResourceBundle getBundle(String projectName, String packageName, String resourceName, Locale locale, ClassLoader loader, ResourceBundle extendsBundle)
/*     */     throws MissingResourceException
/*     */   {
/* 263 */     if (log.isDebugEnabled()) {
/* 264 */       log.debug("getBundle(" + projectName + "," + packageName + "," + resourceName + "," + String.valueOf(locale) + ",...)");
/*     */     }
/*     */ 
/* 270 */     Context context = new Context(null);
/* 271 */     context.setLocale(locale);
/* 272 */     context.setLoader(loader);
/* 273 */     context.setProjectName(projectName);
/* 274 */     context.setResourceName(resourceName);
/* 275 */     context.setParentBundle(extendsBundle);
/*     */ 
/* 277 */     packageName = context.validate(packageName);
/*     */ 
/* 279 */     ProjectResourceBundle bundle = null;
/*     */     try {
/* 281 */       bundle = getBundle(context, packageName);
/*     */     } catch (RuntimeException e) {
/* 283 */       log.debug("Exception: ", e);
/* 284 */       throw e;
/*     */     }
/*     */ 
/* 287 */     if (bundle == null) {
/* 288 */       throw new MissingResourceException("Cannot find resource '" + packageName + '.' + resourceName + "'", resourceName, "");
/*     */     }
/*     */ 
/* 293 */     return bundle;
/*     */   }
/*     */ 
/*     */   private static synchronized ProjectResourceBundle getBundle(Context context, String packageName)
/*     */     throws MissingResourceException
/*     */   {
/* 305 */     String cacheKey = context.getCacheKey(packageName);
/*     */ 
/* 307 */     ProjectResourceBundle prb = (ProjectResourceBundle)bundleCache.get(cacheKey);
/*     */ 
/* 309 */     if (prb == null) {
/* 310 */       String name = packageName + '.' + context.getResourceName();
/* 311 */       ResourceBundle rb = context.loadBundle(packageName);
/* 312 */       ResourceBundle parent = context.getParentBundle(packageName);
/*     */ 
/* 314 */       if (rb != null) {
/* 315 */         prb = new ProjectResourceBundle(name, rb);
/* 316 */         prb.setParent(parent);
/* 317 */         if (log.isDebugEnabled()) {
/* 318 */           log.debug("Created " + prb + ", linked to parent " + String.valueOf(parent));
/*     */         }
/*     */       }
/* 321 */       else if (parent != null) {
/* 322 */         if ((parent instanceof ProjectResourceBundle))
/* 323 */           prb = (ProjectResourceBundle)parent;
/*     */         else {
/* 325 */           prb = new ProjectResourceBundle(name, parent);
/*     */         }
/* 327 */         if (log.isDebugEnabled()) {
/* 328 */           log.debug("Root package not found, cross link to " + parent);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 333 */       if (prb != null)
/*     */       {
/* 335 */         bundleCache.put(cacheKey, prb);
/*     */       }
/*     */     }
/*     */ 
/* 339 */     return prb;
/*     */   }
/*     */ 
/*     */   private static final String getPackage(String name) {
/* 343 */     return name.substring(0, name.lastIndexOf('.')).intern();
/*     */   }
/*     */ 
/*     */   private ProjectResourceBundle(String name, ResourceBundle bundle)
/*     */     throws MissingResourceException
/*     */   {
/* 352 */     this.resourceBundle = bundle;
/* 353 */     this.resourceName = name;
/*     */   }
/*     */ 
/*     */   public String getResourceName() {
/* 357 */     return this.resourceName;
/*     */   }
/*     */ 
/*     */   public static void clearCache()
/*     */   {
/* 365 */     bundleCache.clear();
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 369 */     return this.resourceName;
/*     */   }
/*     */   private static class Context {
/*     */     private Locale _locale;
/*     */     private ClassLoader _loader;
/*     */     private String _projectName;
/*     */     private String _resourceName;
/*     */     private ResourceBundle _parent;
/*     */ 
/*     */     private Context() {
/*     */     }
/*     */ 
/*     */     void setLocale(Locale l) {
/* 387 */       this._locale = (l == null ? ProjectResourceBundle.defaultLocale : l);
/*     */     }
/*     */ 
/*     */     void setLoader(ClassLoader l) {
/* 391 */       this._loader = (l != null ? l : getClass().getClassLoader());
/*     */ 
/* 393 */       if (this._loader == null)
/* 394 */         this._loader = ClassLoader.getSystemClassLoader();
/*     */     }
/*     */ 
/*     */     void setProjectName(String name)
/*     */     {
/* 399 */       this._projectName = name.intern(); } 
/* 400 */     void setResourceName(String name) { this._resourceName = name.intern(); } 
/* 401 */     void setParentBundle(ResourceBundle b) { this._parent = b; } 
/*     */     Locale getLocale() {
/* 403 */       return this._locale; } 
/* 404 */     ClassLoader getLoader() { return this._loader; } 
/* 405 */     String getProjectName() { return this._projectName; } 
/* 406 */     String getResourceName() { return this._resourceName; } 
/* 407 */     ResourceBundle getParentBundle() { return this._parent; }
/*     */ 
/*     */     String getCacheKey(String packageName)
/*     */     {
/* 411 */       String loaderName = ":" + this._loader.hashCode();
/* 412 */       return packageName + "." + this._resourceName + ":" + this._locale + ":" + ProjectResourceBundle.defaultLocale + loaderName;
/*     */     }
/*     */ 
/*     */     ResourceBundle loadBundle(String packageName)
/*     */     {
/*     */       try {
/* 418 */         return ResourceBundle.getBundle(packageName + '.' + this._resourceName, this._locale, this._loader);
/*     */       }
/*     */       catch (MissingResourceException e)
/*     */       {
/* 423 */         ProjectResourceBundle.log.debug("loadBundle: Ignoring MissingResourceException: " + e.getMessage());
/*     */       }
/* 425 */       return null;
/*     */     }
/*     */ 
/*     */     ResourceBundle getParentBundle(String packageName)
/*     */     {
/*     */       ResourceBundle p;
/*     */       ResourceBundle p;
/* 431 */       if (packageName != this._projectName) {
/* 432 */         p = ProjectResourceBundle.access$300(this, ProjectResourceBundle.access$200(packageName));
/*     */       } else {
/* 434 */         p = this._parent;
/* 435 */         this._parent = null;
/*     */       }
/* 437 */       return p;
/*     */     }
/*     */ 
/*     */     String validate(String packageName)
/*     */       throws MissingResourceException
/*     */     {
/* 443 */       if ((this._projectName == null) || (this._projectName.length() == 0)) {
/* 444 */         ProjectResourceBundle.log.debug("Project name not specified");
/* 445 */         throw new MissingResourceException("Project name not specified", "", "");
/*     */       }
/*     */ 
/* 449 */       if ((packageName == null) || (packageName.length() == 0)) {
/* 450 */         ProjectResourceBundle.log.debug("Package name not specified");
/* 451 */         throw new MissingResourceException("Package not specified", packageName, "");
/*     */       }
/*     */ 
/* 454 */       packageName = packageName.intern();
/*     */ 
/* 459 */       if ((packageName != this._projectName) && (!packageName.startsWith(this._projectName + '.'))) {
/* 460 */         ProjectResourceBundle.log.debug("Project not a prefix of Package");
/* 461 */         throw new MissingResourceException("Project '" + this._projectName + "' must be a prefix of Package '" + packageName + "'", packageName + '.' + this._resourceName, "");
/*     */       }
/*     */ 
/* 467 */       return packageName;
/*     */     }
/*     */ 
/*     */     Context(ProjectResourceBundle.1 x0)
/*     */     {
/* 373 */       this();
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.i18n.ProjectResourceBundle
 * JD-Core Version:    0.6.0
 */