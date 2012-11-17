/*     */ package org.apache.axis.types;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.text.NumberFormat;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class MonthDay
/*     */   implements Serializable
/*     */ {
/*     */   int month;
/*     */   int day;
/*  31 */   String timezone = null;
/*     */ 
/*     */   public MonthDay(int month, int day)
/*     */     throws NumberFormatException
/*     */   {
/*  39 */     setValue(month, day);
/*     */   }
/*     */ 
/*     */   public MonthDay(int month, int day, String timezone)
/*     */     throws NumberFormatException
/*     */   {
/*  48 */     setValue(month, day, timezone);
/*     */   }
/*     */ 
/*     */   public MonthDay(String source)
/*     */     throws NumberFormatException
/*     */   {
/*  55 */     if (source.length() < 6) {
/*  56 */       throw new NumberFormatException(Messages.getMessage("badMonthDay00"));
/*     */     }
/*     */ 
/*  60 */     if ((source.charAt(0) != '-') || (source.charAt(1) != '-') || (source.charAt(4) != '-'))
/*     */     {
/*  63 */       throw new NumberFormatException(Messages.getMessage("badMonthDay00"));
/*     */     }
/*     */ 
/*  67 */     setValue(Integer.parseInt(source.substring(2, 4)), Integer.parseInt(source.substring(5, 7)), source.substring(7));
/*     */   }
/*     */ 
/*     */   public int getMonth()
/*     */   {
/*  73 */     return this.month;
/*     */   }
/*     */ 
/*     */   public void setMonth(int month)
/*     */   {
/*  78 */     if ((month < 1) || (month > 12)) {
/*  79 */       throw new NumberFormatException(Messages.getMessage("badMonthDay00"));
/*     */     }
/*     */ 
/*  82 */     this.month = month;
/*     */   }
/*     */ 
/*     */   public int getDay() {
/*  86 */     return this.day;
/*     */   }
/*     */ 
/*     */   public void setDay(int day)
/*     */   {
/*  95 */     if ((day < 1) || (day > 31)) {
/*  96 */       throw new NumberFormatException(Messages.getMessage("badMonthDay00"));
/*     */     }
/*     */ 
/* 101 */     if (((this.month == 2) && (day > 29)) || (((this.month == 9) || (this.month == 4) || (this.month == 6) || (this.month == 11)) && (day > 30)))
/*     */     {
/* 103 */       throw new NumberFormatException(Messages.getMessage("badMonthDay00"));
/*     */     }
/*     */ 
/* 106 */     this.day = day;
/*     */   }
/*     */ 
/*     */   public String getTimezone() {
/* 110 */     return this.timezone;
/*     */   }
/*     */ 
/*     */   public void setTimezone(String timezone)
/*     */   {
/* 115 */     if ((timezone != null) && (timezone.length() > 0))
/*     */     {
/* 117 */       if ((timezone.charAt(0) == '+') || (timezone.charAt(0) == '-')) {
/* 118 */         if ((timezone.length() != 6) || (!Character.isDigit(timezone.charAt(1))) || (!Character.isDigit(timezone.charAt(2))) || (timezone.charAt(3) != ':') || (!Character.isDigit(timezone.charAt(4))) || (!Character.isDigit(timezone.charAt(5))))
/*     */         {
/* 124 */           throw new NumberFormatException(Messages.getMessage("badTimezone00"));
/*     */         }
/*     */       }
/* 127 */       else if (!timezone.equals("Z")) {
/* 128 */         throw new NumberFormatException(Messages.getMessage("badTimezone00"));
/*     */       }
/*     */ 
/* 132 */       this.timezone = timezone;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setValue(int month, int day, String timezone) throws NumberFormatException
/*     */   {
/* 138 */     setMonth(month);
/* 139 */     setDay(day);
/* 140 */     setTimezone(timezone);
/*     */   }
/*     */ 
/*     */   public void setValue(int month, int day) throws NumberFormatException {
/* 144 */     setMonth(month);
/* 145 */     setDay(day);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 150 */     NumberFormat nf = NumberFormat.getInstance();
/* 151 */     nf.setGroupingUsed(false);
/*     */ 
/* 154 */     nf.setMinimumIntegerDigits(2);
/* 155 */     String s = "--" + nf.format(this.month) + "-" + nf.format(this.day);
/*     */ 
/* 158 */     if (this.timezone != null) {
/* 159 */       s = s + this.timezone;
/*     */     }
/* 161 */     return s;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj) {
/* 165 */     if (!(obj instanceof MonthDay)) return false;
/* 166 */     MonthDay other = (MonthDay)obj;
/* 167 */     if (obj == null) return false;
/* 168 */     if (this == obj) return true;
/*     */ 
/* 170 */     boolean equals = (this.month == other.month) && (this.day == other.day);
/* 171 */     if (this.timezone != null) {
/* 172 */       equals = (equals) && (this.timezone.equals(other.timezone));
/*     */     }
/* 174 */     return equals;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 184 */     return null == this.timezone ? this.month + this.day : this.month + this.day ^ this.timezone.hashCode();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.MonthDay
 * JD-Core Version:    0.6.0
 */