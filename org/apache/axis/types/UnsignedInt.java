/*     */ package org.apache.axis.types;
/*     */ 
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class UnsignedInt extends Number
/*     */   implements Comparable
/*     */ {
/*  30 */   protected Long lValue = new Long(0L);
/*     */ 
/*  88 */   private Object __equalsCalc = null;
/*     */ 
/*     */   public UnsignedInt()
/*     */   {
/*     */   }
/*     */ 
/*     */   public UnsignedInt(long iValue)
/*     */     throws NumberFormatException
/*     */   {
/*  40 */     setValue(iValue);
/*     */   }
/*     */ 
/*     */   public UnsignedInt(String stValue) throws NumberFormatException {
/*  44 */     setValue(Long.parseLong(stValue));
/*     */   }
/*     */ 
/*     */   public void setValue(long iValue)
/*     */     throws NumberFormatException
/*     */   {
/*  55 */     if (!isValid(iValue)) {
/*  56 */       throw new NumberFormatException(Messages.getMessage("badUnsignedInt00") + String.valueOf(iValue) + "]");
/*     */     }
/*     */ 
/*  59 */     this.lValue = new Long(iValue);
/*     */   }
/*     */ 
/*     */   public String toString() {
/*  63 */     if (this.lValue != null) {
/*  64 */       return this.lValue.toString();
/*     */     }
/*  66 */     return null;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/*  70 */     if (this.lValue != null) {
/*  71 */       return this.lValue.hashCode();
/*     */     }
/*  73 */     return 0;
/*     */   }
/*     */ 
/*     */   public static boolean isValid(long iValue)
/*     */   {
/*  83 */     return (iValue >= 0L) && (iValue <= 4294967295L);
/*     */   }
/*     */ 
/*     */   public synchronized boolean equals(Object obj)
/*     */   {
/*  90 */     if (!(obj instanceof UnsignedInt)) return false;
/*  91 */     UnsignedInt other = (UnsignedInt)obj;
/*  92 */     if (obj == null) return false;
/*  93 */     if (this == obj) return true;
/*  94 */     if (this.__equalsCalc != null) {
/*  95 */       return this.__equalsCalc == obj;
/*     */     }
/*  97 */     this.__equalsCalc = obj;
/*     */ 
/*  99 */     boolean _equals = ((this.lValue == null) && (other.lValue == null)) || ((this.lValue != null) && (this.lValue.equals(other.lValue)));
/*     */ 
/* 103 */     this.__equalsCalc = null;
/* 104 */     return _equals;
/*     */   }
/*     */ 
/*     */   public int compareTo(Object obj)
/*     */   {
/* 109 */     if (this.lValue != null) {
/* 110 */       return this.lValue.compareTo(obj);
/*     */     }
/* 112 */     if (equals(obj) == true) {
/* 113 */       return 0;
/*     */     }
/* 115 */     return 1;
/*     */   }
/*     */ 
/*     */   public byte byteValue() {
/* 119 */     return this.lValue.byteValue(); } 
/* 120 */   public short shortValue() { return this.lValue.shortValue(); } 
/* 121 */   public int intValue() { return this.lValue.intValue(); } 
/* 122 */   public long longValue() { return this.lValue.longValue(); } 
/* 123 */   public double doubleValue() { return this.lValue.doubleValue(); } 
/* 124 */   public float floatValue() { return this.lValue.floatValue();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.UnsignedInt
 * JD-Core Version:    0.6.0
 */