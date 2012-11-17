/*     */ package org.apache.axis.types;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.text.NumberFormat;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class Year
/*     */   implements Serializable
/*     */ {
/*     */   int year;
/*  30 */   String timezone = null;
/*     */ 
/*     */   public Year(int year)
/*     */     throws NumberFormatException
/*     */   {
/*  37 */     setValue(year);
/*     */   }
/*     */ 
/*     */   public Year(int year, String timezone)
/*     */     throws NumberFormatException
/*     */   {
/*  45 */     setValue(year, timezone);
/*     */   }
/*     */ 
/*     */   public Year(String source)
/*     */     throws NumberFormatException
/*     */   {
/*  52 */     int negative = 0;
/*     */ 
/*  54 */     if (source.charAt(0) == '-') {
/*  55 */       negative = 1;
/*     */     }
/*  57 */     if (source.length() < 4 + negative) {
/*  58 */       throw new NumberFormatException(Messages.getMessage("badYear00"));
/*     */     }
/*     */ 
/*  63 */     int pos = 4 + negative;
/*  64 */     while ((pos < source.length()) && (Character.isDigit(source.charAt(pos)))) {
/*  65 */       pos++;
/*     */     }
/*     */ 
/*  68 */     setValue(Integer.parseInt(source.substring(0, pos)), source.substring(pos));
/*     */   }
/*     */ 
/*     */   public int getYear()
/*     */   {
/*  73 */     return this.year;
/*     */   }
/*     */ 
/*     */   public void setYear(int year)
/*     */   {
/*  78 */     if (year == 0) {
/*  79 */       throw new NumberFormatException(Messages.getMessage("badYear00"));
/*     */     }
/*     */ 
/*  83 */     this.year = year;
/*     */   }
/*     */ 
/*     */   public String getTimezone() {
/*  87 */     return this.timezone;
/*     */   }
/*     */ 
/*     */   public void setTimezone(String timezone)
/*     */   {
/*  92 */     if ((timezone != null) && (timezone.length() > 0))
/*     */     {
/*  94 */       if ((timezone.charAt(0) == '+') || (timezone.charAt(0) == '-')) {
/*  95 */         if ((timezone.length() != 6) || (!Character.isDigit(timezone.charAt(1))) || (!Character.isDigit(timezone.charAt(2))) || (timezone.charAt(3) != ':') || (!Character.isDigit(timezone.charAt(4))) || (!Character.isDigit(timezone.charAt(5))))
/*     */         {
/* 101 */           throw new NumberFormatException(Messages.getMessage("badTimezone00"));
/*     */         }
/*     */       }
/* 104 */       else if (!timezone.equals("Z")) {
/* 105 */         throw new NumberFormatException(Messages.getMessage("badTimezone00"));
/*     */       }
/*     */ 
/* 109 */       this.timezone = timezone;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setValue(int year, String timezone) throws NumberFormatException
/*     */   {
/* 115 */     setYear(year);
/* 116 */     setTimezone(timezone);
/*     */   }
/*     */ 
/*     */   public void setValue(int year) throws NumberFormatException {
/* 120 */     setYear(year);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 125 */     NumberFormat nf = NumberFormat.getInstance();
/* 126 */     nf.setGroupingUsed(false);
/*     */ 
/* 129 */     nf.setMinimumIntegerDigits(4);
/* 130 */     String s = nf.format(this.year);
/*     */ 
/* 133 */     if (this.timezone != null) {
/* 134 */       s = s + this.timezone;
/*     */     }
/* 136 */     return s;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj) {
/* 140 */     if (!(obj instanceof Year)) return false;
/* 141 */     Year other = (Year)obj;
/* 142 */     if (obj == null) return false;
/* 143 */     if (this == obj) return true;
/*     */ 
/* 145 */     boolean equals = this.year == other.year;
/* 146 */     if (this.timezone != null) {
/* 147 */       equals = (equals) && (this.timezone.equals(other.timezone));
/*     */     }
/* 149 */     return equals;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 159 */     return null == this.timezone ? this.year : this.year ^ this.timezone.hashCode();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.Year
 * JD-Core Version:    0.6.0
 */