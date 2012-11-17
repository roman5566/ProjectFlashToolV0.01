/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import java.util.Random;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class SessionUtils
/*     */ {
/*  29 */   protected static Log log = LogFactory.getLog(SessionUtils.class.getName());
/*     */   protected static final int SESSION_ID_BYTES = 16;
/*  40 */   protected static Random random = null;
/*     */ 
/*  46 */   protected static String randomClass = "java.security.SecureRandom";
/*     */ 
/*  51 */   private static String thisHost = null;
/*     */ 
/*     */   public static synchronized String generateSessionId()
/*     */   {
/*  60 */     byte[] bytes = new byte[16];
/*     */ 
/*  62 */     getRandom().nextBytes(bytes);
/*     */ 
/*  65 */     StringBuffer result = new StringBuffer();
/*     */ 
/*  67 */     for (int i = 0; i < bytes.length; i++) {
/*  68 */       byte b1 = (byte)((bytes[i] & 0xF0) >> 4);
/*  69 */       byte b2 = (byte)(bytes[i] & 0xF);
/*     */ 
/*  71 */       if (b1 < 10)
/*  72 */         result.append((char)(48 + b1));
/*     */       else {
/*  74 */         result.append((char)(65 + (b1 - 10)));
/*     */       }
/*  76 */       if (b2 < 10)
/*  77 */         result.append((char)(48 + b2));
/*     */       else {
/*  79 */         result.append((char)(65 + (b2 - 10)));
/*     */       }
/*     */     }
/*  82 */     return result.toString();
/*     */   }
/*     */ 
/*     */   public static synchronized Long generateSession()
/*     */   {
/*  91 */     return new Long(getRandom().nextLong());
/*     */   }
/*     */ 
/*     */   private static synchronized Random getRandom()
/*     */   {
/* 102 */     if (random == null) {
/*     */       try {
/* 104 */         Class clazz = Class.forName(randomClass);
/* 105 */         random = (Random)clazz.newInstance();
/*     */       } catch (Exception e) {
/* 107 */         random = new Random();
/*     */       }
/*     */     }
/* 110 */     return random;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.SessionUtils
 * JD-Core Version:    0.6.0
 */