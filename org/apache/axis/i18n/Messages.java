/*     */ package org.apache.axis.i18n;
/*     */ 
/*     */ import java.util.Locale;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.ResourceBundle;
/*     */ 
/*     */ public class Messages
/*     */ {
/*  42 */   private static final Class thisClass = Messages.class;
/*     */ 
/*  44 */   private static final String projectName = MessagesConstants.projectName;
/*     */ 
/*  46 */   private static final String resourceName = MessagesConstants.resourceName;
/*  47 */   private static final Locale locale = MessagesConstants.locale;
/*     */ 
/*  49 */   private static final String packageName = getPackage(thisClass.getName());
/*  50 */   private static final ClassLoader classLoader = thisClass.getClassLoader();
/*     */ 
/*  52 */   private static final ResourceBundle parent = MessagesConstants.rootPackageName == packageName ? null : MessagesConstants.rootBundle;
/*     */ 
/*  60 */   private static final MessageBundle messageBundle = new MessageBundle(projectName, packageName, resourceName, locale, classLoader, parent);
/*     */ 
/*     */   public static String getMessage(String key)
/*     */     throws MissingResourceException
/*     */   {
/*  72 */     return messageBundle.getMessage(key);
/*     */   }
/*     */ 
/*     */   public static String getMessage(String key, String arg0)
/*     */     throws MissingResourceException
/*     */   {
/*  84 */     return messageBundle.getMessage(key, arg0);
/*     */   }
/*     */ 
/*     */   public static String getMessage(String key, String arg0, String arg1)
/*     */     throws MissingResourceException
/*     */   {
/*  97 */     return messageBundle.getMessage(key, arg0, arg1);
/*     */   }
/*     */ 
/*     */   public static String getMessage(String key, String arg0, String arg1, String arg2)
/*     */     throws MissingResourceException
/*     */   {
/* 111 */     return messageBundle.getMessage(key, arg0, arg1, arg2);
/*     */   }
/*     */ 
/*     */   public static String getMessage(String key, String arg0, String arg1, String arg2, String arg3)
/*     */     throws MissingResourceException
/*     */   {
/* 126 */     return messageBundle.getMessage(key, arg0, arg1, arg2, arg3);
/*     */   }
/*     */ 
/*     */   public static String getMessage(String key, String arg0, String arg1, String arg2, String arg3, String arg4)
/*     */     throws MissingResourceException
/*     */   {
/* 142 */     return messageBundle.getMessage(key, arg0, arg1, arg2, arg3, arg4);
/*     */   }
/*     */ 
/*     */   public static String getMessage(String key, String[] args)
/*     */     throws MissingResourceException
/*     */   {
/* 154 */     return messageBundle.getMessage(key, args);
/*     */   }
/*     */ 
/*     */   public static ResourceBundle getResourceBundle() {
/* 158 */     return messageBundle.getResourceBundle();
/*     */   }
/*     */ 
/*     */   public static MessageBundle getMessageBundle() {
/* 162 */     return messageBundle;
/*     */   }
/*     */ 
/*     */   private static final String getPackage(String name) {
/* 166 */     return name.substring(0, name.lastIndexOf('.')).intern();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.i18n.Messages
 * JD-Core Version:    0.6.0
 */