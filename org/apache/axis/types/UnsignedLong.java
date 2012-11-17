/*     */ package org.apache.axis.types;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class UnsignedLong extends Number
/*     */   implements Comparable
/*     */ {
/*  31 */   protected BigInteger lValue = BigInteger.ZERO;
/*  32 */   private static BigInteger MAX = new BigInteger("18446744073709551615");
/*     */ 
/*  81 */   private Object __equalsCalc = null;
/*     */ 
/*     */   public UnsignedLong()
/*     */   {
/*     */   }
/*     */ 
/*     */   public UnsignedLong(double value)
/*     */     throws NumberFormatException
/*     */   {
/*  38 */     setValue(new BigInteger(Double.toString(value)));
/*     */   }
/*     */ 
/*     */   public UnsignedLong(BigInteger value) throws NumberFormatException {
/*  42 */     setValue(value);
/*     */   }
/*     */ 
/*     */   public UnsignedLong(long lValue) throws NumberFormatException {
/*  46 */     setValue(BigInteger.valueOf(lValue));
/*     */   }
/*     */ 
/*     */   public UnsignedLong(String stValue) throws NumberFormatException {
/*  50 */     setValue(new BigInteger(stValue));
/*     */   }
/*     */ 
/*     */   private void setValue(BigInteger val) {
/*  54 */     if (!isValid(val)) {
/*  55 */       throw new NumberFormatException(Messages.getMessage("badUnsignedLong00") + String.valueOf(val) + "]");
/*     */     }
/*     */ 
/*  59 */     this.lValue = val;
/*     */   }
/*     */ 
/*     */   public static boolean isValid(BigInteger value)
/*     */   {
/*  65 */     return (value.compareTo(BigInteger.ZERO) != -1) && (value.compareTo(MAX) != 1);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  71 */     return this.lValue.toString();
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/*  75 */     if (this.lValue != null) {
/*  76 */       return this.lValue.hashCode();
/*     */     }
/*  78 */     return 0;
/*     */   }
/*     */ 
/*     */   public synchronized boolean equals(Object obj)
/*     */   {
/*  84 */     if (!(obj instanceof UnsignedLong)) return false;
/*  85 */     UnsignedLong other = (UnsignedLong)obj;
/*  86 */     if (obj == null) return false;
/*  87 */     if (this == obj) return true;
/*  88 */     if (this.__equalsCalc != null) {
/*  89 */       return this.__equalsCalc == obj;
/*     */     }
/*  91 */     this.__equalsCalc = obj;
/*     */ 
/*  93 */     boolean _equals = ((this.lValue == null) && (other.lValue == null)) || ((this.lValue != null) && (this.lValue.equals(other.lValue)));
/*     */ 
/*  97 */     this.__equalsCalc = null;
/*  98 */     return _equals;
/*     */   }
/*     */ 
/*     */   public int compareTo(Object obj)
/*     */   {
/* 103 */     if (this.lValue != null)
/* 104 */       return this.lValue.compareTo(obj);
/* 105 */     if (equals(obj) == true) {
/* 106 */       return 0;
/*     */     }
/* 108 */     return 1;
/*     */   }
/*     */ 
/*     */   public byte byteValue()
/*     */   {
/* 113 */     return this.lValue.byteValue();
/*     */   }
/*     */ 
/*     */   public short shortValue() {
/* 117 */     return this.lValue.shortValue();
/*     */   }
/*     */ 
/*     */   public int intValue() {
/* 121 */     return this.lValue.intValue();
/*     */   }
/*     */ 
/*     */   public long longValue() {
/* 125 */     return this.lValue.longValue();
/*     */   }
/*     */ 
/*     */   public double doubleValue() {
/* 129 */     return this.lValue.doubleValue();
/*     */   }
/*     */ 
/*     */   public float floatValue() {
/* 133 */     return this.lValue.floatValue();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.UnsignedLong
 * JD-Core Version:    0.6.0
 */