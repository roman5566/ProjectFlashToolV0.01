/*     */ package org.apache.axis.i18n;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Locale;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class RB
/*     */ {
/*  70 */   static Hashtable propertyCache = new Hashtable();
/*     */   public static final String BASE_NAME = "resource";
/*     */   public static final String PROPERTY_EXT = ".properties";
/*     */   protected String basePropertyFileName;
/*     */   protected Properties resourceProperties;
/*     */ 
/*     */   public RB(String name)
/*     */     throws MissingResourceException
/*     */   {
/*  90 */     this(null, name, null);
/*     */   }
/*     */ 
/*     */   public RB(Object caller, String name)
/*     */     throws MissingResourceException
/*     */   {
/* 101 */     this(caller, name, null);
/*     */   }
/*     */ 
/*     */   public RB(Object caller, String name, Locale locale)
/*     */     throws MissingResourceException
/*     */   {
/* 113 */     ClassLoader cl = null;
/*     */ 
/* 115 */     if (caller != null)
/*     */     {
/*     */       Class c;
/*     */       Class c;
/* 118 */       if ((caller instanceof Class)) {
/* 119 */         c = (Class)caller;
/*     */       }
/*     */       else {
/* 122 */         c = caller.getClass();
/*     */       }
/*     */ 
/* 126 */       cl = c.getClassLoader();
/*     */ 
/* 128 */       if (name.indexOf("/") == -1)
/*     */       {
/* 131 */         String fullName = c.getName();
/*     */ 
/* 133 */         int pos = fullName.lastIndexOf(".");
/* 134 */         if (pos > 0) {
/* 135 */           name = fullName.substring(0, pos + 1).replace('.', '/') + name;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/* 140 */     else if (name.indexOf("/") == -1) {
/* 141 */       name = "org/apache/axis/default-resource";
/*     */     }
/*     */ 
/* 145 */     Locale defaultLocale = Locale.getDefault();
/*     */ 
/* 148 */     if ((locale != null) && 
/* 149 */       (locale.equals(defaultLocale))) {
/* 150 */       locale = null;
/*     */     }
/*     */ 
/* 156 */     loadProperties(name, cl, locale, defaultLocale);
/*     */   }
/*     */ 
/*     */   public String getString(String key)
/*     */     throws MissingResourceException
/*     */   {
/* 166 */     return getString(key, (Object[])null);
/*     */   }
/*     */ 
/*     */   public String getString(String key, Object arg0)
/*     */     throws MissingResourceException
/*     */   {
/* 182 */     Object[] o = new Object[1];
/* 183 */     o[0] = arg0;
/* 184 */     return getString(key, o);
/*     */   }
/*     */ 
/*     */   public String getString(String key, Object arg0, Object arg1)
/*     */     throws MissingResourceException
/*     */   {
/* 201 */     Object[] o = new Object[2];
/* 202 */     o[0] = arg0;
/* 203 */     o[1] = arg1;
/* 204 */     return getString(key, o);
/*     */   }
/*     */ 
/*     */   public String getString(String key, Object arg0, Object arg1, Object arg2)
/*     */     throws MissingResourceException
/*     */   {
/* 222 */     Object[] o = new Object[3];
/* 223 */     o[0] = arg0;
/* 224 */     o[1] = arg1;
/* 225 */     o[2] = arg2;
/* 226 */     return getString(key, o);
/*     */   }
/*     */ 
/*     */   public String getString(String key, Object[] array)
/*     */     throws MissingResourceException
/*     */   {
/* 242 */     String msg = null;
/* 243 */     if (this.resourceProperties != null) {
/* 244 */       msg = this.resourceProperties.getProperty(key);
/*     */     }
/*     */ 
/* 247 */     if (msg == null) {
/* 248 */       throw new MissingResourceException("Cannot find resource key \"" + key + "\" in base name " + this.basePropertyFileName, this.basePropertyFileName, key);
/*     */     }
/*     */ 
/* 253 */     msg = MessageFormat.format(msg, array);
/* 254 */     return msg;
/*     */   }
/*     */ 
/*     */   protected void loadProperties(String basename, ClassLoader loader, Locale locale, Locale defaultLocale)
/*     */     throws MissingResourceException
/*     */   {
/* 262 */     String loaderName = "";
/* 263 */     if (loader != null) {
/* 264 */       loaderName = ":" + loader.hashCode();
/*     */     }
/* 266 */     String cacheKey = basename + ":" + locale + ":" + defaultLocale + loaderName;
/* 267 */     Properties p = (Properties)propertyCache.get(cacheKey);
/* 268 */     this.basePropertyFileName = (basename + ".properties");
/*     */ 
/* 270 */     if (p == null)
/*     */     {
/* 273 */       if (locale != null) {
/* 274 */         p = loadProperties(basename, loader, locale, p);
/*     */       }
/*     */ 
/* 278 */       if (defaultLocale != null) {
/* 279 */         p = loadProperties(basename, loader, defaultLocale, p);
/*     */       }
/*     */ 
/* 283 */       p = merge(p, loadProperties(this.basePropertyFileName, loader));
/*     */ 
/* 285 */       if (p == null) {
/* 286 */         throw new MissingResourceException("Cannot find resource for base name " + this.basePropertyFileName, this.basePropertyFileName, "");
/*     */       }
/*     */ 
/* 291 */       propertyCache.put(cacheKey, p);
/*     */     }
/*     */ 
/* 295 */     this.resourceProperties = p;
/*     */   }
/*     */ 
/*     */   protected Properties loadProperties(String basename, ClassLoader loader, Locale locale, Properties props)
/*     */   {
/* 302 */     String language = locale.getLanguage();
/* 303 */     String country = locale.getCountry();
/* 304 */     String variant = locale.getVariant();
/* 305 */     if ((variant != null) && 
/* 306 */       (variant.trim().length() == 0)) {
/* 307 */       variant = null;
/*     */     }
/*     */ 
/* 311 */     if (language != null)
/*     */     {
/* 313 */       if (country != null)
/*     */       {
/* 315 */         if (variant != null) {
/* 316 */           props = merge(props, loadProperties(basename + "_" + language + "_" + country + "_" + variant + ".properties", loader));
/*     */         }
/*     */ 
/* 319 */         props = merge(props, loadProperties(basename + "_" + language + "_" + country + ".properties", loader));
/*     */       }
/*     */ 
/* 322 */       props = merge(props, loadProperties(basename + "_" + language + ".properties", loader));
/*     */     }
/* 324 */     return props;
/*     */   }
/*     */ 
/*     */   protected Properties loadProperties(String resname, ClassLoader loader)
/*     */   {
/* 329 */     Properties props = null;
/*     */ 
/* 332 */     InputStream in = null;
/*     */     try {
/* 334 */       if (loader != null) {
/* 335 */         in = loader.getResourceAsStream(resname);
/*     */       }
/*     */ 
/* 340 */       if (in == null) {
/* 341 */         in = ClassLoader.getSystemResourceAsStream(resname);
/*     */       }
/* 343 */       if (in != null) {
/* 344 */         props = new Properties();
/*     */         try {
/* 346 */           props.load(in);
/*     */         }
/*     */         catch (IOException ex)
/*     */         {
/* 350 */           props = null;
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 355 */       if (in != null) {
/*     */         try {
/* 357 */           in.close();
/*     */         }
/*     */         catch (Exception ex)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/* 364 */     return props;
/*     */   }
/*     */ 
/*     */   protected Properties merge(Properties p1, Properties p2)
/*     */   {
/* 372 */     if ((p1 == null) && (p2 == null))
/*     */     {
/* 374 */       return null;
/*     */     }
/* 376 */     if (p1 == null) {
/* 377 */       return p2;
/*     */     }
/* 379 */     if (p2 == null) {
/* 380 */       return p1;
/*     */     }
/*     */ 
/* 384 */     Enumeration enumeration = p2.keys();
/* 385 */     while (enumeration.hasMoreElements()) {
/* 386 */       String key = (String)enumeration.nextElement();
/* 387 */       if (p1.getProperty(key) == null) {
/* 388 */         p1.put(key, p2.getProperty(key));
/*     */       }
/*     */     }
/*     */ 
/* 392 */     return p1;
/*     */   }
/*     */ 
/*     */   public Properties getProperties()
/*     */   {
/* 400 */     return this.resourceProperties;
/*     */   }
/*     */ 
/*     */   public static String getString(Object caller, String key)
/*     */     throws MissingResourceException
/*     */   {
/* 414 */     return getMessage(caller, "resource", null, key, null);
/*     */   }
/*     */ 
/*     */   public static String getString(Object caller, String key, Object arg0)
/*     */     throws MissingResourceException
/*     */   {
/* 427 */     Object[] o = new Object[1];
/* 428 */     o[0] = arg0;
/* 429 */     return getMessage(caller, "resource", null, key, o);
/*     */   }
/*     */ 
/*     */   public static String getString(Object caller, String key, Object arg0, Object arg1)
/*     */     throws MissingResourceException
/*     */   {
/* 443 */     Object[] o = new Object[2];
/* 444 */     o[0] = arg0;
/* 445 */     o[1] = arg1;
/* 446 */     return getMessage(caller, "resource", null, key, o);
/*     */   }
/*     */ 
/*     */   public static String getString(Object caller, String key, Object arg0, Object arg1, Object arg2)
/*     */     throws MissingResourceException
/*     */   {
/* 461 */     Object[] o = new Object[3];
/* 462 */     o[0] = arg0;
/* 463 */     o[1] = arg1;
/* 464 */     o[2] = arg2;
/* 465 */     return getMessage(caller, "resource", null, key, o);
/*     */   }
/*     */ 
/*     */   public static String getString(Object caller, String key, Object arg0, Object arg1, Object arg2, Object arg3)
/*     */     throws MissingResourceException
/*     */   {
/* 481 */     Object[] o = new Object[4];
/* 482 */     o[0] = arg0;
/* 483 */     o[1] = arg1;
/* 484 */     o[2] = arg2;
/* 485 */     o[3] = arg3;
/* 486 */     return getMessage(caller, "resource", null, key, o);
/*     */   }
/*     */ 
/*     */   public static String getString(Object caller, String key, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4)
/*     */     throws MissingResourceException
/*     */   {
/* 504 */     Object[] o = new Object[5];
/* 505 */     o[0] = arg0;
/* 506 */     o[1] = arg1;
/* 507 */     o[2] = arg2;
/* 508 */     o[3] = arg3;
/* 509 */     o[4] = arg4;
/* 510 */     return getMessage(caller, "resource", null, key, o);
/*     */   }
/*     */ 
/*     */   public static String getString(Object caller, String key, Object[] args)
/*     */     throws MissingResourceException
/*     */   {
/* 524 */     return getMessage(caller, "resource", null, key, args);
/*     */   }
/*     */ 
/*     */   public static String getString(Object caller, Locale locale, String key)
/*     */     throws MissingResourceException
/*     */   {
/* 538 */     return getMessage(caller, "resource", locale, key, null);
/*     */   }
/*     */ 
/*     */   public static String getString(Object caller, Locale locale, String key, Object arg0)
/*     */     throws MissingResourceException
/*     */   {
/* 552 */     Object[] o = new Object[1];
/* 553 */     o[0] = arg0;
/* 554 */     return getMessage(caller, "resource", locale, key, o);
/*     */   }
/*     */ 
/*     */   public static String getString(Object caller, Locale locale, String key, Object arg0, Object arg1)
/*     */     throws MissingResourceException
/*     */   {
/* 569 */     Object[] o = new Object[2];
/* 570 */     o[0] = arg0;
/* 571 */     o[1] = arg1;
/* 572 */     return getMessage(caller, "resource", locale, key, o);
/*     */   }
/*     */ 
/*     */   public static String getString(Object caller, Locale locale, String key, Object arg0, Object arg1, Object arg2)
/*     */     throws MissingResourceException
/*     */   {
/* 588 */     Object[] o = new Object[3];
/* 589 */     o[0] = arg0;
/* 590 */     o[1] = arg1;
/* 591 */     o[2] = arg2;
/* 592 */     return getMessage(caller, "resource", locale, key, o);
/*     */   }
/*     */ 
/*     */   public static String getString(Object caller, Locale locale, String key, Object arg0, Object arg1, Object arg2, Object arg3)
/*     */     throws MissingResourceException
/*     */   {
/* 609 */     Object[] o = new Object[4];
/* 610 */     o[0] = arg0;
/* 611 */     o[1] = arg1;
/* 612 */     o[2] = arg2;
/* 613 */     o[3] = arg3;
/* 614 */     return getMessage(caller, "resource", locale, key, o);
/*     */   }
/*     */ 
/*     */   public static String getString(Object caller, Locale locale, String key, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4)
/*     */     throws MissingResourceException
/*     */   {
/* 631 */     Object[] o = new Object[5];
/* 632 */     o[0] = arg0;
/* 633 */     o[1] = arg1;
/* 634 */     o[2] = arg2;
/* 635 */     o[3] = arg3;
/* 636 */     o[4] = arg4;
/* 637 */     return getMessage(caller, "resource", locale, key, o);
/*     */   }
/*     */ 
/*     */   public static String getString(Object caller, Locale locale, String key, Object[] args)
/*     */     throws MissingResourceException
/*     */   {
/* 651 */     return getMessage(caller, "resource", locale, key, args);
/*     */   }
/*     */ 
/*     */   public static String getMessage(Object caller, String basename, Locale locale, String key, Object[] args)
/*     */     throws MissingResourceException
/*     */   {
/* 659 */     String msg = null;
/* 660 */     MissingResourceException firstEx = null;
/* 661 */     String fullName = null;
/* 662 */     Class curClass = null;
/* 663 */     boolean didNull = false;
/*     */ 
/* 665 */     if (caller != null) {
/* 666 */       if ((caller instanceof Class))
/* 667 */         curClass = (Class)caller;
/*     */       else {
/* 669 */         curClass = caller.getClass();
/*     */       }
/*     */     }
/* 672 */     while (msg == null)
/*     */     {
/* 675 */       if (curClass != null)
/*     */       {
/* 678 */         String pkgName = curClass.getName();
/*     */ 
/* 680 */         int pos = pkgName.lastIndexOf(".");
/* 681 */         if (pos > 0) {
/* 682 */           fullName = pkgName.substring(0, pos + 1).replace('.', '/') + basename;
/*     */         }
/*     */         else
/* 685 */           fullName = basename;
/*     */       }
/*     */       else
/*     */       {
/* 689 */         fullName = basename;
/*     */       }
/*     */       try
/*     */       {
/* 693 */         RB rb = new RB(caller, fullName, locale);
/* 694 */         msg = rb.getString(key, args);
/*     */       }
/*     */       catch (MissingResourceException ex) {
/* 697 */         if (curClass == null) {
/* 698 */           throw ex;
/*     */         }
/*     */ 
/* 702 */         if (firstEx == null) {
/* 703 */           firstEx = ex;
/*     */         }
/*     */ 
/* 707 */         curClass = curClass.getSuperclass();
/* 708 */         if (curClass == null) {
/* 709 */           if (didNull)
/* 710 */             throw firstEx;
/* 711 */           didNull = true;
/* 712 */           caller = null;
/*     */         } else {
/* 714 */           String cname = curClass.getName();
/* 715 */           if ((cname.startsWith("java.")) || (cname.startsWith("javax.")))
/*     */           {
/* 717 */             if (didNull)
/* 718 */               throw firstEx;
/* 719 */             didNull = true;
/* 720 */             caller = null;
/* 721 */             curClass = null;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 727 */     return msg;
/*     */   }
/*     */ 
/*     */   public static void clearCache()
/*     */   {
/* 735 */     propertyCache.clear();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.i18n.RB
 * JD-Core Version:    0.6.0
 */