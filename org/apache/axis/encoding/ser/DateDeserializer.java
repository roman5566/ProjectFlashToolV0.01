/*     */ package org.apache.axis.encoding.ser;
/*     */ 
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class DateDeserializer extends SimpleDeserializer
/*     */ {
/*  35 */   private static SimpleDateFormat zulu = new SimpleDateFormat("yyyy-MM-dd");
/*     */ 
/*  39 */   private static Calendar calendar = Calendar.getInstance();
/*     */ 
/*     */   public DateDeserializer(Class javaType, QName xmlType)
/*     */   {
/*  46 */     super(javaType, xmlType);
/*     */   }
/*     */ 
/*     */   public Object makeValue(String source)
/*     */   {
/*  55 */     boolean bc = false;
/*     */ 
/*  58 */     if (source != null) {
/*  59 */       if (source.length() < 10) {
/*  60 */         throw new NumberFormatException(Messages.getMessage("badDate00"));
/*     */       }
/*     */ 
/*  63 */       if (source.charAt(0) == '+') {
/*  64 */         source = source.substring(1);
/*     */       }
/*  66 */       if (source.charAt(0) == '-') {
/*  67 */         source = source.substring(1);
/*  68 */         bc = true;
/*     */       }
/*     */ 
/*  71 */       if ((source.charAt(4) != '-') || (source.charAt(7) != '-')) {
/*  72 */         throw new NumberFormatException(Messages.getMessage("badDate00"));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  77 */     synchronized (calendar)
/*     */     {
/*     */       try {
/*  80 */         result = zulu.parse(source == null ? null : source.substring(0, 10));
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */         Object result;
/*  83 */         throw new NumberFormatException(e.toString());
/*     */       }
/*     */       Object result;
/*  87 */       if (bc) {
/*  88 */         calendar.setTime((java.util.Date)result);
/*  89 */         calendar.set(0, 0);
/*  90 */         result = calendar.getTime();
/*     */       }
/*  92 */       if (this.javaType == java.util.Date.class)
/*  93 */         return result;
/*  94 */       if (this.javaType == java.sql.Date.class) {
/*  95 */         result = new java.sql.Date(((java.util.Date)result).getTime());
/*     */       } else {
/*  97 */         calendar.setTime((java.util.Date)result);
/*  98 */         result = calendar;
/*     */       }
/*     */     }
/*     */     Object result;
/* 101 */     return result;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.DateDeserializer
 * JD-Core Version:    0.6.0
 */