/*     */ package org.apache.axis.types;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.text.NumberFormat;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class YearMonth
/*     */   implements Serializable
/*     */ {
/*     */   int year;
/*     */   int month;
/*  31 */   String timezone = null;
/*     */ 
/*     */   public YearMonth(int year, int month)
/*     */     throws NumberFormatException
/*     */   {
/*  38 */     setValue(year, month);
/*     */   }
/*     */ 
/*     */   public YearMonth(int year, int month, String timezone)
/*     */     throws NumberFormatException
/*     */   {
/*  46 */     setValue(year, month, timezone);
/*     */   }
/*     */ 
/*     */   public YearMonth(String source)
/*     */     throws NumberFormatException
/*     */   {
/*  53 */     int negative = 0;
/*     */ 
/*  55 */     if (source.charAt(0) == '-') {
/*  56 */       negative = 1;
/*     */     }
/*  58 */     if (source.length() < 7 + negative) {
/*  59 */       throw new NumberFormatException(Messages.getMessage("badYearMonth00"));
/*     */     }
/*     */ 
/*  64 */     int pos = source.substring(negative).indexOf('-');
/*  65 */     if (pos < 0) {
/*  66 */       throw new NumberFormatException(Messages.getMessage("badYearMonth00"));
/*     */     }
/*     */ 
/*  69 */     if (negative > 0) pos++;
/*     */ 
/*  71 */     setValue(Integer.parseInt(source.substring(0, pos)), Integer.parseInt(source.substring(pos + 1, pos + 3)), source.substring(pos + 3));
/*     */   }
/*     */ 
/*     */   public int getYear()
/*     */   {
/*  77 */     return this.year;
/*     */   }
/*     */ 
/*     */   public void setYear(int year)
/*     */   {
/*  82 */     if (year == 0) {
/*  83 */       throw new NumberFormatException(Messages.getMessage("badYearMonth00"));
/*     */     }
/*     */ 
/*  87 */     this.year = year;
/*     */   }
/*     */ 
/*     */   public int getMonth() {
/*  91 */     return this.month;
/*     */   }
/*     */ 
/*     */   public void setMonth(int month)
/*     */   {
/*  96 */     if ((month < 1) || (month > 12)) {
/*  97 */       throw new NumberFormatException(Messages.getMessage("badYearMonth00"));
/*     */     }
/*     */ 
/* 100 */     this.month = month;
/*     */   }
/*     */ 
/*     */   public String getTimezone() {
/* 104 */     return this.timezone;
/*     */   }
/*     */ 
/*     */   public void setTimezone(String timezone)
/*     */   {
/* 109 */     if ((timezone != null) && (timezone.length() > 0))
/*     */     {
/* 111 */       if ((timezone.charAt(0) == '+') || (timezone.charAt(0) == '-')) {
/* 112 */         if ((timezone.length() != 6) || (!Character.isDigit(timezone.charAt(1))) || (!Character.isDigit(timezone.charAt(2))) || (timezone.charAt(3) != ':') || (!Character.isDigit(timezone.charAt(4))) || (!Character.isDigit(timezone.charAt(5))))
/*     */         {
/* 118 */           throw new NumberFormatException(Messages.getMessage("badTimezone00"));
/*     */         }
/*     */       }
/* 121 */       else if (!timezone.equals("Z")) {
/* 122 */         throw new NumberFormatException(Messages.getMessage("badTimezone00"));
/*     */       }
/*     */ 
/* 126 */       this.timezone = timezone;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setValue(int year, int month, String timezone) throws NumberFormatException {
/* 131 */     setYear(year);
/* 132 */     setMonth(month);
/* 133 */     setTimezone(timezone);
/*     */   }
/*     */ 
/*     */   public void setValue(int year, int month) throws NumberFormatException {
/* 137 */     setYear(year);
/* 138 */     setMonth(month);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 143 */     NumberFormat nf = NumberFormat.getInstance();
/* 144 */     nf.setGroupingUsed(false);
/*     */ 
/* 147 */     nf.setMinimumIntegerDigits(4);
/* 148 */     String s = nf.format(this.year) + "-";
/*     */ 
/* 151 */     nf.setMinimumIntegerDigits(2);
/* 152 */     s = s + nf.format(this.month);
/*     */ 
/* 155 */     if (this.timezone != null) {
/* 156 */       s = s + this.timezone;
/*     */     }
/* 158 */     return s;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj) {
/* 162 */     if (!(obj instanceof YearMonth)) return false;
/* 163 */     YearMonth other = (YearMonth)obj;
/* 164 */     if (obj == null) return false;
/* 165 */     if (this == obj) return true;
/*     */ 
/* 167 */     boolean equals = (this.year == other.year) && (this.month == other.month);
/* 168 */     if (this.timezone != null) {
/* 169 */       equals = (equals) && (this.timezone.equals(other.timezone));
/*     */     }
/* 171 */     return equals;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 181 */     return null == this.timezone ? this.month + this.year : this.month + this.year ^ this.timezone.hashCode();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.YearMonth
 * JD-Core Version:    0.6.0
 */