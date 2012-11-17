/*     */ package org.apache.axis;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class ConfigurationException extends IOException
/*     */ {
/*  36 */   private Exception containedException = null;
/*     */ 
/*  38 */   private String stackTrace = "";
/*     */ 
/*  43 */   protected static boolean copyStackByDefault = true;
/*     */ 
/*  48 */   protected static Log log = LogFactory.getLog(ConfigurationException.class.getName());
/*     */ 
/*     */   public ConfigurationException(String message)
/*     */   {
/*  57 */     super(message);
/*  58 */     if (copyStackByDefault) {
/*  59 */       this.stackTrace = JavaUtils.stackToString(this);
/*     */     }
/*  61 */     logException(this);
/*     */   }
/*     */ 
/*     */   public ConfigurationException(Exception exception)
/*     */   {
/*  69 */     this(exception, copyStackByDefault);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*     */     String stack;
/*     */     String stack;
/*  79 */     if (this.stackTrace.length() == 0)
/*  80 */       stack = "";
/*     */     else {
/*  82 */       stack = "\n" + this.stackTrace;
/*     */     }
/*  84 */     return super.toString() + stack;
/*     */   }
/*     */ 
/*     */   public ConfigurationException(Exception exception, boolean copyStack)
/*     */   {
/*  93 */     super(exception.toString() + (copyStack ? "\n" + JavaUtils.stackToString(exception) : ""));
/*     */ 
/*  95 */     this.containedException = exception;
/*  96 */     if (copyStack) {
/*  97 */       this.stackTrace = JavaUtils.stackToString(this);
/*     */     }
/*     */ 
/* 100 */     if (!(exception instanceof ConfigurationException))
/* 101 */       logException(exception);
/*     */   }
/*     */ 
/*     */   private void logException(Exception exception)
/*     */   {
/* 110 */     log.debug("Exception: ", exception);
/*     */   }
/*     */ 
/*     */   public Exception getContainedException()
/*     */   {
/* 120 */     return this.containedException;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.ConfigurationException
 * JD-Core Version:    0.6.0
 */