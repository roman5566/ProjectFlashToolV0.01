/*    */ package org.apache.axis.types;
/*    */ 
/*    */ import java.io.ObjectStreamException;
/*    */ import java.io.Serializable;
/*    */ import java.math.BigInteger;
/*    */ import java.util.Random;
/*    */ import org.apache.axis.utils.Messages;
/*    */ 
/*    */ public class NonNegativeInteger extends BigInteger
/*    */ {
/* 65 */   private BigInteger zero = new BigInteger("0");
/*    */ 
/*    */   public NonNegativeInteger(byte[] val)
/*    */   {
/* 33 */     super(val);
/* 34 */     checkValidity();
/*    */   }
/*    */ 
/*    */   public NonNegativeInteger(int signum, byte[] magnitude) {
/* 38 */     super(signum, magnitude);
/* 39 */     checkValidity();
/*    */   }
/*    */ 
/*    */   public NonNegativeInteger(int bitLength, int certainty, Random rnd) {
/* 43 */     super(bitLength, certainty, rnd);
/* 44 */     checkValidity();
/*    */   }
/*    */ 
/*    */   public NonNegativeInteger(int numBits, Random rnd) {
/* 48 */     super(numBits, rnd);
/* 49 */     checkValidity();
/*    */   }
/*    */ 
/*    */   public NonNegativeInteger(String val) {
/* 53 */     super(val);
/* 54 */     checkValidity();
/*    */   }
/*    */ 
/*    */   public NonNegativeInteger(String val, int radix) {
/* 58 */     super(val, radix);
/* 59 */     checkValidity();
/*    */   }
/*    */ 
/*    */   private void checkValidity()
/*    */   {
/* 67 */     if (compareTo(this.zero) < 0)
/* 68 */       throw new NumberFormatException(Messages.getMessage("badNonNegInt00") + ":  " + this);
/*    */   }
/*    */ 
/*    */   public Object writeReplace()
/*    */     throws ObjectStreamException
/*    */   {
/* 80 */     return new BigIntegerRep(toByteArray());
/*    */   }
/*    */   protected static class BigIntegerRep implements Serializable {
/*    */     private byte[] array;
/*    */ 
/* 86 */     protected BigIntegerRep(byte[] array) { this.array = array; }
/*    */ 
/*    */     protected Object readResolve() throws ObjectStreamException {
/* 89 */       return new NonNegativeInteger(this.array);
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.NonNegativeInteger
 * JD-Core Version:    0.6.0
 */