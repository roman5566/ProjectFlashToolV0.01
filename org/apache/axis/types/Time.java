/*     */ package org.apache.axis.types;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class Time
/*     */   implements Serializable
/*     */ {
/*     */   private Calendar _value;
/*  36 */   private static SimpleDateFormat zulu = new SimpleDateFormat("HH:mm:ss.SSS'Z'");
/*     */ 
/*     */   public Time(Calendar value)
/*     */   {
/*  49 */     this._value = value;
/*  50 */     this._value.set(0, 0, 0);
/*     */   }
/*     */ 
/*     */   public Time(String value)
/*     */     throws NumberFormatException
/*     */   {
/*  57 */     this._value = makeValue(value);
/*     */   }
/*     */ 
/*     */   public Calendar getAsCalendar()
/*     */   {
/*  65 */     return this._value;
/*     */   }
/*     */ 
/*     */   public void setTime(Calendar date)
/*     */   {
/*  73 */     this._value = date;
/*  74 */     this._value.set(0, 0, 0);
/*     */   }
/*     */ 
/*     */   public void setTime(Date date)
/*     */   {
/*  82 */     this._value.setTime(date);
/*  83 */     this._value.set(0, 0, 0);
/*     */   }
/*     */ 
/*     */   private Calendar makeValue(String source)
/*     */     throws NumberFormatException
/*     */   {
/*  90 */     Calendar calendar = Calendar.getInstance();
/*     */ 
/*  93 */     validateSource(source);
/*     */ 
/*  96 */     Date date = ParseHoursMinutesSeconds(source);
/*     */ 
/*  98 */     int pos = 8;
/*     */ 
/* 101 */     if (source != null) {
/* 102 */       if ((pos < source.length()) && (source.charAt(pos) == '.')) {
/* 103 */         int milliseconds = 0;
/* 104 */         pos++; int start = pos;
/* 105 */         while ((pos < source.length()) && (Character.isDigit(source.charAt(pos))))
/*     */         {
/* 107 */           pos++;
/*     */         }
/*     */ 
/* 111 */         String decimal = source.substring(start, pos);
/* 112 */         if (decimal.length() == 3) {
/* 113 */           milliseconds = Integer.parseInt(decimal);
/* 114 */         } else if (decimal.length() < 3) {
/* 115 */           milliseconds = Integer.parseInt((decimal + "000").substring(0, 3));
/*     */         }
/*     */         else {
/* 118 */           milliseconds = Integer.parseInt(decimal.substring(0, 3));
/* 119 */           if (decimal.charAt(3) >= '5') {
/* 120 */             milliseconds++;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 125 */         date.setTime(date.getTime() + milliseconds);
/*     */       }
/*     */ 
/* 129 */       if ((pos + 5 < source.length()) && ((source.charAt(pos) == '+') || (source.charAt(pos) == '-')))
/*     */       {
/* 131 */         if ((!Character.isDigit(source.charAt(pos + 1))) || (!Character.isDigit(source.charAt(pos + 2))) || (source.charAt(pos + 3) != ':') || (!Character.isDigit(source.charAt(pos + 4))) || (!Character.isDigit(source.charAt(pos + 5))))
/*     */         {
/* 137 */           throw new NumberFormatException(Messages.getMessage("badTimezone00"));
/*     */         }
/*     */ 
/* 141 */         int hours = (source.charAt(pos + 1) - '0') * 10 + source.charAt(pos + 2) - 48;
/*     */ 
/* 143 */         int mins = (source.charAt(pos + 4) - '0') * 10 + source.charAt(pos + 5) - 48;
/*     */ 
/* 145 */         int milliseconds = (hours * 60 + mins) * 60 * 1000;
/*     */ 
/* 148 */         if (source.charAt(pos) == '+') {
/* 149 */           milliseconds = -milliseconds;
/*     */         }
/* 151 */         date.setTime(date.getTime() + milliseconds);
/* 152 */         pos += 6;
/*     */       }
/*     */ 
/* 155 */       if ((pos < source.length()) && (source.charAt(pos) == 'Z')) {
/* 156 */         pos++;
/* 157 */         calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */       }
/*     */ 
/* 160 */       if (pos < source.length()) {
/* 161 */         throw new NumberFormatException(Messages.getMessage("badChars00"));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 166 */     calendar.setTime(date);
/* 167 */     calendar.set(0, 0, 0);
/*     */ 
/* 169 */     return calendar;
/*     */   }
/*     */ 
/*     */   private int getTimezoneNumberValue(char c) {
/* 173 */     int n = c - '0';
/* 174 */     if ((n < 0) || (n > 9))
/*     */     {
/* 176 */       throw new NumberFormatException(Messages.getMessage("badTimezone00"));
/*     */     }
/*     */ 
/* 179 */     return n;
/*     */   }
/*     */ 
/*     */   private static Date ParseHoursMinutesSeconds(String source)
/*     */   {
/*     */     try
/*     */     {
/*     */       Date date;
/* 194 */       synchronized (zulu) {
/* 195 */         String fulltime = source.substring(0, 8) + ".000Z";
/*     */ 
/* 197 */         date = zulu.parse(fulltime);
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */       Date date;
/* 200 */       throw new NumberFormatException(e.toString());
/*     */     }
/*     */     Date date;
/* 202 */     return date;
/*     */   }
/*     */ 
/*     */   private void validateSource(String source)
/*     */   {
/* 211 */     if (source != null) {
/* 212 */       if ((source.charAt(2) != ':') || (source.charAt(5) != ':')) {
/* 213 */         throw new NumberFormatException(Messages.getMessage("badTime00"));
/*     */       }
/*     */ 
/* 216 */       if (source.length() < 8)
/* 217 */         throw new NumberFormatException(Messages.getMessage("badTime00"));
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 229 */     if (this._value == null) {
/* 230 */       return "unassigned Time";
/*     */     }
/* 232 */     synchronized (zulu) {
/* 233 */       return zulu.format(this._value.getTime());
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 239 */     if (obj == null) return false;
/* 240 */     if (!(obj instanceof Time)) return false;
/* 241 */     Time other = (Time)obj;
/* 242 */     if (this == obj) return true;
/*     */ 
/* 245 */     boolean _equals = ((this._value == null) && (other._value == null)) || ((this._value != null) && (this._value.getTime().equals(other._value.getTime())));
/*     */ 
/* 250 */     return _equals;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 260 */     return this._value == null ? 0 : this._value.hashCode();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  41 */     zulu.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.Time
 * JD-Core Version:    0.6.0
 */