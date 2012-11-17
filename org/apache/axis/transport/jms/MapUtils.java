/*     */ package org.apache.axis.transport.jms;
/*     */ 
/*     */ import java.util.Map;
/*     */ 
/*     */ public class MapUtils
/*     */ {
/*     */   public static int removeIntProperty(Map properties, String key, int defaultValue)
/*     */   {
/*  40 */     int value = defaultValue;
/*  41 */     if ((properties != null) && (properties.containsKey(key)))
/*     */       try {
/*  43 */         value = ((Integer)properties.remove(key)).intValue(); } catch (Exception ignore) {
/*     */       }
/*  45 */     return value;
/*     */   }
/*     */ 
/*     */   public static long removeLongProperty(Map properties, String key, long defaultValue)
/*     */   {
/*  58 */     long value = defaultValue;
/*  59 */     if ((properties != null) && (properties.containsKey(key)))
/*     */       try {
/*  61 */         value = ((Long)properties.remove(key)).longValue(); } catch (Exception ignore) {
/*     */       }
/*  63 */     return value;
/*     */   }
/*     */ 
/*     */   public static String removeStringProperty(Map properties, String key, String defaultValue)
/*     */   {
/*  76 */     String value = defaultValue;
/*  77 */     if ((properties != null) && (properties.containsKey(key)))
/*     */       try {
/*  79 */         value = (String)properties.remove(key); } catch (Exception ignore) {
/*     */       }
/*  81 */     return value;
/*     */   }
/*     */ 
/*     */   public static boolean removeBooleanProperty(Map properties, String key, boolean defaultValue)
/*     */   {
/*  94 */     boolean value = defaultValue;
/*  95 */     if ((properties != null) && (properties.containsKey(key)))
/*     */       try {
/*  97 */         value = ((Boolean)properties.remove(key)).booleanValue(); } catch (Exception ignore) {
/*     */       }
/*  99 */     return value;
/*     */   }
/*     */ 
/*     */   public static Object removeObjectProperty(Map properties, String key, Object defaultValue)
/*     */   {
/* 112 */     Object value = defaultValue;
/* 113 */     if ((properties != null) && (properties.containsKey(key)))
/*     */     {
/* 115 */       value = properties.remove(key);
/*     */     }
/* 117 */     return value;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.jms.MapUtils
 * JD-Core Version:    0.6.0
 */