/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import java.util.Locale;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.ResourceBundle;
/*     */ import org.apache.axis.i18n.MessageBundle;
/*     */ import org.apache.axis.i18n.MessagesConstants;
/*     */ 
/*     */ public class Messages
/*     */ {
/*  34 */   private static final Class thisClass = Messages.class;
/*     */ 
/*  36 */   private static final String projectName = MessagesConstants.projectName;
/*     */ 
/*  38 */   private static final String resourceName = MessagesConstants.resourceName;
/*  39 */   private static final Locale locale = MessagesConstants.locale;
/*     */ 
/*  41 */   private static final String packageName = getPackage(thisClass.getName());
/*  42 */   private static final ClassLoader classLoader = thisClass.getClassLoader();
/*     */ 
/*  44 */   private static final ResourceBundle parent = MessagesConstants.rootPackageName == packageName ? null : MessagesConstants.rootBundle;
/*     */ 
/*  52 */   private static final MessageBundle messageBundle = new MessageBundle(projectName, packageName, resourceName, locale, classLoader, parent);
/*     */ 
/*     */   public static String getMessage(String key)
/*     */     throws MissingResourceException
/*     */   {
/*  66 */     return messageBundle.getMessage(key);
/*     */   }
/*     */ 
/*     */   public static String getMessage(String key, String arg0)
/*     */     throws MissingResourceException
/*     */   {
/*  80 */     return messageBundle.getMessage(key, arg0);
/*     */   }
/*     */ 
/*     */   public static String getMessage(String key, String arg0, String arg1)
/*     */     throws MissingResourceException
/*     */   {
/*  95 */     return messageBundle.getMessage(key, arg0, arg1);
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
/* 128 */     return messageBundle.getMessage(key, arg0, arg1, arg2, arg3);
/*     */   }
/*     */ 
/*     */   public static String getMessage(String key, String arg0, String arg1, String arg2, String arg3, String arg4)
/*     */     throws MissingResourceException
/*     */   {
/* 146 */     return messageBundle.getMessage(key, arg0, arg1, arg2, arg3, arg4);
/*     */   }
/*     */ 
/*     */   public static String getMessage(String key, String[] args)
/*     */     throws MissingResourceException
/*     */   {
/* 160 */     return messageBundle.getMessage(key, args);
/*     */   }
/*     */ 
/*     */   public static ResourceBundle getResourceBundle() {
/* 164 */     return messageBundle.getResourceBundle();
/*     */   }
/*     */ 
/*     */   public static MessageBundle getMessageBundle() {
/* 168 */     return messageBundle;
/*     */   }
/*     */ 
/*     */   private static final String getPackage(String name) {
/* 172 */     return name.substring(0, name.lastIndexOf('.')).intern();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.Messages
 * JD-Core Version:    0.6.0
 */