/*     */ package org.apache.axis.components.net;
/*     */ 
/*     */ import com.sun.net.ssl.SSLContext;
/*     */ import com.sun.net.ssl.TrustManager;
/*     */ import com.sun.net.ssl.X509TrustManager;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Hashtable;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class SunFakeTrustSocketFactory extends SunJSSESocketFactory
/*     */ {
/*  34 */   protected static Log log = LogFactory.getLog(SunFakeTrustSocketFactory.class.getName());
/*     */ 
/*     */   public SunFakeTrustSocketFactory(Hashtable attributes)
/*     */   {
/*  43 */     super(attributes);
/*     */   }
/*     */ 
/*     */   protected SSLContext getContext()
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/*  56 */       SSLContext sc = SSLContext.getInstance("SSL");
/*     */ 
/*  58 */       sc.init(null, new TrustManager[] { new FakeX509TrustManager() }, new SecureRandom());
/*     */ 
/*  61 */       if (log.isDebugEnabled()) {
/*  62 */         log.debug(Messages.getMessage("ftsf00"));
/*     */       }
/*  64 */       return sc;
/*     */     } catch (Exception exc) {
/*  66 */       log.error(Messages.getMessage("ftsf01"), exc);
/*  67 */     }throw new Exception(Messages.getMessage("ftsf02"));
/*     */   }
/*     */ 
/*     */   public static class FakeX509TrustManager
/*     */     implements X509TrustManager
/*     */   {
/*  77 */     protected static Log log = LogFactory.getLog(FakeX509TrustManager.class.getName());
/*     */ 
/*     */     public boolean isClientTrusted(X509Certificate[] chain)
/*     */     {
/*  90 */       if (log.isDebugEnabled()) {
/*  91 */         log.debug(Messages.getMessage("ftsf03"));
/*     */       }
/*  93 */       return true;
/*     */     }
/*     */ 
/*     */     public boolean isServerTrusted(X509Certificate[] chain)
/*     */     {
/* 106 */       if (log.isDebugEnabled()) {
/* 107 */         log.debug(Messages.getMessage("ftsf04"));
/*     */       }
/* 109 */       return true;
/*     */     }
/*     */ 
/*     */     public X509Certificate[] getAcceptedIssuers()
/*     */     {
/* 119 */       if (log.isDebugEnabled()) {
/* 120 */         log.debug(Messages.getMessage("ftsf05"));
/*     */       }
/* 122 */       return null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.net.SunFakeTrustSocketFactory
 * JD-Core Version:    0.6.0
 */