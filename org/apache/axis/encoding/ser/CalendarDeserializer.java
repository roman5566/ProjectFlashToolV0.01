/*     */ package org.apache.axis.encoding.ser;
/*     */ 
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class CalendarDeserializer extends SimpleDeserializer
/*     */ {
/*  37 */   private static SimpleDateFormat zulu = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
/*     */ 
/*     */   public CalendarDeserializer(Class javaType, QName xmlType)
/*     */   {
/*  50 */     super(javaType, xmlType);
/*     */   }
/*     */ 
/*     */   public Object makeValue(String source)
/*     */   {
/*  58 */     Calendar calendar = Calendar.getInstance();
/*     */ 
/*  60 */     boolean bc = false;
/*     */ 
/*  63 */     if ((source == null) || (source.length() == 0)) {
/*  64 */       throw new NumberFormatException(Messages.getMessage("badDateTime00"));
/*     */     }
/*     */ 
/*  67 */     if (source.charAt(0) == '+') {
/*  68 */       source = source.substring(1);
/*     */     }
/*  70 */     if (source.charAt(0) == '-') {
/*  71 */       source = source.substring(1);
/*  72 */       bc = true;
/*     */     }
/*  74 */     if (source.length() < 19) {
/*  75 */       throw new NumberFormatException(Messages.getMessage("badDateTime00"));
/*     */     }
/*     */ 
/*  78 */     if ((source.charAt(4) != '-') || (source.charAt(7) != '-') || (source.charAt(10) != 'T'))
/*     */     {
/*  80 */       throw new NumberFormatException(Messages.getMessage("badDate00"));
/*     */     }
/*  82 */     if ((source.charAt(13) != ':') || (source.charAt(16) != ':'))
/*  83 */       throw new NumberFormatException(Messages.getMessage("badTime00"));
/*     */     try
/*     */     {
/*     */       Date date;
/*  87 */       synchronized (zulu) {
/*  88 */         date = zulu.parse(source.substring(0, 19) + ".000Z");
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */       Date date;
/*  91 */       throw new NumberFormatException(e.toString());
/*     */     }
/*     */     Date date;
/*  93 */     int pos = 19;
/*     */ 
/*  96 */     if ((pos < source.length()) && (source.charAt(pos) == '.')) {
/*  97 */       int milliseconds = 0;
/*  98 */       pos++; int start = pos;
/*  99 */       while ((pos < source.length()) && (Character.isDigit(source.charAt(pos))))
/*     */       {
/* 101 */         pos++;
/*     */       }
/* 103 */       String decimal = source.substring(start, pos);
/* 104 */       if (decimal.length() == 3) {
/* 105 */         milliseconds = Integer.parseInt(decimal);
/* 106 */       } else if (decimal.length() < 3) {
/* 107 */         milliseconds = Integer.parseInt((decimal + "000").substring(0, 3));
/*     */       }
/*     */       else {
/* 110 */         milliseconds = Integer.parseInt(decimal.substring(0, 3));
/* 111 */         if (decimal.charAt(3) >= '5') {
/* 112 */           milliseconds++;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 117 */       date.setTime(date.getTime() + milliseconds);
/*     */     }
/*     */ 
/* 121 */     if ((pos + 5 < source.length()) && ((source.charAt(pos) == '+') || (source.charAt(pos) == '-')))
/*     */     {
/* 123 */       if ((!Character.isDigit(source.charAt(pos + 1))) || (!Character.isDigit(source.charAt(pos + 2))) || (source.charAt(pos + 3) != ':') || (!Character.isDigit(source.charAt(pos + 4))) || (!Character.isDigit(source.charAt(pos + 5))))
/*     */       {
/* 128 */         throw new NumberFormatException(Messages.getMessage("badTimezone00"));
/*     */       }
/*     */ 
/* 131 */       int hours = (source.charAt(pos + 1) - '0') * 10 + source.charAt(pos + 2) - 48;
/*     */ 
/* 133 */       int mins = (source.charAt(pos + 4) - '0') * 10 + source.charAt(pos + 5) - 48;
/*     */ 
/* 135 */       int milliseconds = (hours * 60 + mins) * 60 * 1000;
/*     */ 
/* 138 */       if (source.charAt(pos) == '+') {
/* 139 */         milliseconds = -milliseconds;
/*     */       }
/* 141 */       date.setTime(date.getTime() + milliseconds);
/* 142 */       pos += 6;
/*     */     }
/* 144 */     if ((pos < source.length()) && (source.charAt(pos) == 'Z')) {
/* 145 */       pos++;
/* 146 */       calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */     }
/* 148 */     if (pos < source.length()) {
/* 149 */       throw new NumberFormatException(Messages.getMessage("badChars00"));
/*     */     }
/* 151 */     calendar.setTime(date);
/*     */ 
/* 154 */     if (bc) {
/* 155 */       calendar.set(0, 0);
/*     */     }
/* 157 */     if (this.javaType == Date.class) {
/* 158 */       return date;
/*     */     }
/* 160 */     return calendar;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  42 */     zulu.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.CalendarDeserializer
 * JD-Core Version:    0.6.0
 */