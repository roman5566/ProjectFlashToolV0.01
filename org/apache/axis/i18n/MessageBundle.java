/*     */ package org.apache.axis.i18n;
/*     */ 
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Locale;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.ResourceBundle;
/*     */ 
/*     */ public class MessageBundle
/*     */ {
/*  34 */   private boolean loaded = false;
/*     */ 
/*  36 */   private ProjectResourceBundle _resourceBundle = null;
/*     */   private final String projectName;
/*     */   private final String packageName;
/*     */   private final String resourceName;
/*     */   private final Locale locale;
/*     */   private final ClassLoader classLoader;
/*     */   private final ResourceBundle parent;
/*     */ 
/*     */   public final ProjectResourceBundle getResourceBundle()
/*     */   {
/*  47 */     if (!this.loaded) {
/*  48 */       this._resourceBundle = ProjectResourceBundle.getBundle(this.projectName, this.packageName, this.resourceName, this.locale, this.classLoader, this.parent);
/*     */ 
/*  54 */       this.loaded = true;
/*     */     }
/*  56 */     return this._resourceBundle;
/*     */   }
/*     */ 
/*     */   public MessageBundle(String projectName, String packageName, String resourceName, Locale locale, ClassLoader classLoader, ResourceBundle parent)
/*     */     throws MissingResourceException
/*     */   {
/*  70 */     this.projectName = projectName;
/*  71 */     this.packageName = packageName;
/*  72 */     this.resourceName = resourceName;
/*  73 */     this.locale = locale;
/*  74 */     this.classLoader = classLoader;
/*  75 */     this.parent = parent;
/*     */   }
/*     */ 
/*     */   public String getMessage(String key)
/*     */     throws MissingResourceException
/*     */   {
/*  85 */     return getMessage(key, (String[])null);
/*     */   }
/*     */ 
/*     */   public String getMessage(String key, String arg0)
/*     */     throws MissingResourceException
/*     */   {
/* 101 */     return getMessage(key, new String[] { arg0 });
/*     */   }
/*     */ 
/*     */   public String getMessage(String key, String arg0, String arg1)
/*     */     throws MissingResourceException
/*     */   {
/* 118 */     return getMessage(key, new String[] { arg0, arg1 });
/*     */   }
/*     */ 
/*     */   public String getMessage(String key, String arg0, String arg1, String arg2)
/*     */     throws MissingResourceException
/*     */   {
/* 136 */     return getMessage(key, new String[] { arg0, arg1, arg2 });
/*     */   }
/*     */ 
/*     */   public String getMessage(String key, String arg0, String arg1, String arg2, String arg3)
/*     */     throws MissingResourceException
/*     */   {
/* 155 */     return getMessage(key, new String[] { arg0, arg1, arg2, arg3 });
/*     */   }
/*     */ 
/*     */   public String getMessage(String key, String arg0, String arg1, String arg2, String arg3, String arg4)
/*     */     throws MissingResourceException
/*     */   {
/* 175 */     return getMessage(key, new String[] { arg0, arg1, arg2, arg3, arg4 });
/*     */   }
/*     */ 
/*     */   public String getMessage(String key, String[] array)
/*     */     throws MissingResourceException
/*     */   {
/* 191 */     String msg = null;
/* 192 */     if (getResourceBundle() != null) {
/* 193 */       msg = getResourceBundle().getString(key);
/*     */     }
/*     */ 
/* 196 */     if (msg == null) {
/* 197 */       throw new MissingResourceException("Cannot find resource key \"" + key + "\" in base name " + getResourceBundle().getResourceName(), getResourceBundle().getResourceName(), key);
/*     */     }
/*     */ 
/* 203 */     return MessageFormat.format(msg, array);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.i18n.MessageBundle
 * JD-Core Version:    0.6.0
 */