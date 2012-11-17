/*    */ package org.apache.axis.types;
/*    */ 
/*    */ import java.io.ObjectStreamException;
/*    */ import java.io.Serializable;
/*    */ import java.math.BigInteger;
/*    */ import java.util.Random;
/*    */ import org.apache.axis.utils.Messages;
/*    */ 
/*    */ public class NonPositiveInteger extends BigInteger
/*    */ {
/* 70 */   private BigInteger zero = new BigInteger("0");
/*    */ 
/*    */   public NonPositiveInteger(byte[] val)
/*    */   {
/* 38 */     super(val);
/* 39 */     checkValidity();
/*    */   }
/*    */ 
/*    */   public NonPositiveInteger(int signum, byte[] magnitude) {
/* 43 */     super(signum, magnitude);
/* 44 */     checkValidity();
/*    */   }
/*    */ 
/*    */   public NonPositiveInteger(int bitLength, int certainty, Random rnd) {
/* 48 */     super(bitLength, certainty, rnd);
/* 49 */     checkValidity();
/*    */   }
/*    */ 
/*    */   public NonPositiveInteger(int numBits, Random rnd) {
/* 53 */     super(numBits, rnd);
/* 54 */     checkValidity();
/*    */   }
/*    */ 
/*    */   public NonPositiveInteger(String val) {
/* 58 */     super(val);
/* 59 */     checkValidity();
/*    */   }
/*    */ 
/*    */   public NonPositiveInteger(String val, int radix) {
/* 63 */     super(val, radix);
/* 64 */     checkValidity();
/*    */   }
/*    */ 
/*    */   private void checkValidity()
/*    */   {
/* 72 */     if (compareTo(this.zero) > 0)
/* 73 */       throw new NumberFormatException(Messages.getMessage("badNonPosInt00") + ":  " + this);
/*    */   }
/*    */ 
/*    */   public Object writeReplace()
/*    */     throws ObjectStreamException
/*    */   {
/* 85 */     return new BigIntegerRep(toByteArray());
/*    */   }
/*    */   protected static class BigIntegerRep implements Serializable {
/*    */     private byte[] array;
/*    */ 
/* 91 */     protected BigIntegerRep(byte[] array) { this.array = array; }
/*    */ 
/*    */     protected Object readResolve() throws ObjectStreamException {
/* 94 */       return new NonPositiveInteger(this.array);
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.NonPositiveInteger
 * JD-Core Version:    0.6.0
 */