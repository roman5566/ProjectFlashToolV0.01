/*     */ package org.apache.axis.types;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.text.NumberFormat;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class Month
/*     */   implements Serializable
/*     */ {
/*     */   int month;
/*  30 */   String timezone = null;
/*     */ 
/*     */   public Month(int month)
/*     */     throws NumberFormatException
/*     */   {
/*  37 */     setValue(month);
/*     */   }
/*     */ 
/*     */   public Month(int month, String timezone)
/*     */     throws NumberFormatException
/*     */   {
/*  46 */     setValue(month, timezone);
/*     */   }
/*     */ 
/*     */   public Month(String source)
/*     */     throws NumberFormatException
/*     */   {
/*  53 */     if (source.length() < 6) {
/*  54 */       throw new NumberFormatException(Messages.getMessage("badMonth00"));
/*     */     }
/*     */ 
/*  58 */     if ((source.charAt(0) != '-') || (source.charAt(1) != '-') || (source.charAt(4) != '-') || (source.charAt(5) != '-'))
/*     */     {
/*  62 */       throw new NumberFormatException(Messages.getMessage("badMonth00"));
/*     */     }
/*     */ 
/*  66 */     setValue(Integer.parseInt(source.substring(2, 4)), source.substring(6));
/*     */   }
/*     */ 
/*     */   public int getMonth()
/*     */   {
/*  71 */     return this.month;
/*     */   }
/*     */ 
/*     */   public void setMonth(int month)
/*     */   {
/*  76 */     if ((month < 1) || (month > 12)) {
/*  77 */       throw new NumberFormatException(Messages.getMessage("badMonth00"));
/*     */     }
/*     */ 
/*  80 */     this.month = month;
/*     */   }
/*     */ 
/*     */   public String getTimezone() {
/*  84 */     return this.timezone;
/*     */   }
/*     */ 
/*     */   public void setTimezone(String timezone)
/*     */   {
/*  89 */     if ((timezone != null) && (timezone.length() > 0))
/*     */     {
/*  91 */       if ((timezone.charAt(0) == '+') || (timezone.charAt(0) == '-')) {
/*  92 */         if ((timezone.length() != 6) || (!Character.isDigit(timezone.charAt(1))) || (!Character.isDigit(timezone.charAt(2))) || (timezone.charAt(3) != ':') || (!Character.isDigit(timezone.charAt(4))) || (!Character.isDigit(timezone.charAt(5))))
/*     */         {
/*  98 */           throw new NumberFormatException(Messages.getMessage("badTimezone00"));
/*     */         }
/*     */       }
/* 101 */       else if (!timezone.equals("Z")) {
/* 102 */         throw new NumberFormatException(Messages.getMessage("badTimezone00"));
/*     */       }
/*     */ 
/* 106 */       this.timezone = timezone;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setValue(int month, String timezone) throws NumberFormatException {
/* 111 */     setMonth(month);
/* 112 */     setTimezone(timezone);
/*     */   }
/*     */ 
/*     */   public void setValue(int month) throws NumberFormatException {
/* 116 */     setMonth(month);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 121 */     NumberFormat nf = NumberFormat.getInstance();
/* 122 */     nf.setGroupingUsed(false);
/*     */ 
/* 125 */     nf.setMinimumIntegerDigits(2);
/* 126 */     String s = "--" + nf.format(this.month) + "--";
/*     */ 
/* 129 */     if (this.timezone != null) {
/* 130 */       s = s + this.timezone;
/*     */     }
/* 132 */     return s;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj) {
/* 136 */     if (!(obj instanceof Month)) return false;
/* 137 */     Month other = (Month)obj;
/* 138 */     if (obj == null) return false;
/* 139 */     if (this == obj) return true;
/*     */ 
/* 141 */     boolean equals = this.month == other.month;
/* 142 */     if (this.timezone != null) {
/* 143 */       equals = (equals) && (this.timezone.equals(other.timezone));
/*     */     }
/* 145 */     return equals;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 155 */     return null == this.timezone ? this.month : this.month ^ this.timezone.hashCode();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.Month
 * JD-Core Version:    0.6.0
 */