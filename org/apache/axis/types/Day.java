/*     */ package org.apache.axis.types;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.text.NumberFormat;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class Day
/*     */   implements Serializable
/*     */ {
/*     */   int day;
/*  30 */   String timezone = null;
/*     */ 
/*     */   public Day(int day)
/*     */     throws NumberFormatException
/*     */   {
/*  37 */     setValue(day);
/*     */   }
/*     */ 
/*     */   public Day(int day, String timezone)
/*     */     throws NumberFormatException
/*     */   {
/*  46 */     setValue(day, timezone);
/*     */   }
/*     */ 
/*     */   public Day(String source)
/*     */     throws NumberFormatException
/*     */   {
/*  53 */     if (source.length() < 5) {
/*  54 */       throw new NumberFormatException(Messages.getMessage("badDay00"));
/*     */     }
/*     */ 
/*  58 */     if ((source.charAt(0) != '-') || (source.charAt(1) != '-') || (source.charAt(2) != '-'))
/*     */     {
/*  61 */       throw new NumberFormatException(Messages.getMessage("badDay00"));
/*     */     }
/*     */ 
/*  65 */     setValue(Integer.parseInt(source.substring(3, 5)), source.substring(5));
/*     */   }
/*     */ 
/*     */   public int getDay()
/*     */   {
/*  70 */     return this.day;
/*     */   }
/*     */ 
/*     */   public void setDay(int day)
/*     */   {
/*  78 */     if ((day < 1) || (day > 31)) {
/*  79 */       throw new NumberFormatException(Messages.getMessage("badDay00"));
/*     */     }
/*     */ 
/*  82 */     this.day = day;
/*     */   }
/*     */ 
/*     */   public String getTimezone() {
/*  86 */     return this.timezone;
/*     */   }
/*     */ 
/*     */   public void setTimezone(String timezone)
/*     */   {
/*  91 */     if ((timezone != null) && (timezone.length() > 0))
/*     */     {
/*  93 */       if ((timezone.charAt(0) == '+') || (timezone.charAt(0) == '-')) {
/*  94 */         if ((timezone.length() != 6) || (!Character.isDigit(timezone.charAt(1))) || (!Character.isDigit(timezone.charAt(2))) || (timezone.charAt(3) != ':') || (!Character.isDigit(timezone.charAt(4))) || (!Character.isDigit(timezone.charAt(5))))
/*     */         {
/* 100 */           throw new NumberFormatException(Messages.getMessage("badTimezone00"));
/*     */         }
/*     */       }
/* 103 */       else if (!timezone.equals("Z")) {
/* 104 */         throw new NumberFormatException(Messages.getMessage("badTimezone00"));
/*     */       }
/*     */ 
/* 108 */       this.timezone = timezone;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setValue(int day, String timezone) throws NumberFormatException
/*     */   {
/* 114 */     setDay(day);
/* 115 */     setTimezone(timezone);
/*     */   }
/*     */ 
/*     */   public void setValue(int day) throws NumberFormatException {
/* 119 */     setDay(day);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 124 */     NumberFormat nf = NumberFormat.getInstance();
/* 125 */     nf.setGroupingUsed(false);
/*     */ 
/* 128 */     nf.setMinimumIntegerDigits(2);
/* 129 */     String s = "---" + nf.format(this.day);
/*     */ 
/* 132 */     if (this.timezone != null) {
/* 133 */       s = s + this.timezone;
/*     */     }
/* 135 */     return s;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj) {
/* 139 */     if (!(obj instanceof Day)) return false;
/* 140 */     Day other = (Day)obj;
/* 141 */     if (obj == null) return false;
/* 142 */     if (this == obj) return true;
/*     */ 
/* 144 */     boolean equals = this.day == other.day;
/* 145 */     if (this.timezone != null) {
/* 146 */       equals = (equals) && (this.timezone.equals(other.timezone));
/*     */     }
/* 148 */     return equals;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 158 */     return null == this.timezone ? this.day : this.day ^ this.timezone.hashCode();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.Day
 * JD-Core Version:    0.6.0
 */