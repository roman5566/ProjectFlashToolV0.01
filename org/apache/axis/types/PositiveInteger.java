/*    */ package org.apache.axis.types;
/*    */ 
/*    */ import java.io.ObjectStreamException;
/*    */ import java.io.Serializable;
/*    */ import java.math.BigInteger;
/*    */ import java.util.Random;
/*    */ import org.apache.axis.utils.Messages;
/*    */ 
/*    */ public class PositiveInteger extends NonNegativeInteger
/*    */ {
/* 69 */   private BigInteger iMinInclusive = new BigInteger("1");
/*    */ 
/*    */   public PositiveInteger(byte[] val)
/*    */   {
/* 37 */     super(val);
/* 38 */     checkValidity();
/*    */   }
/*    */ 
/*    */   public PositiveInteger(int signum, byte[] magnitude) {
/* 42 */     super(signum, magnitude);
/* 43 */     checkValidity();
/*    */   }
/*    */ 
/*    */   public PositiveInteger(int bitLength, int certainty, Random rnd) {
/* 47 */     super(bitLength, certainty, rnd);
/* 48 */     checkValidity();
/*    */   }
/*    */ 
/*    */   public PositiveInteger(int numBits, Random rnd) {
/* 52 */     super(numBits, rnd);
/* 53 */     checkValidity();
/*    */   }
/*    */ 
/*    */   public PositiveInteger(String val) {
/* 57 */     super(val);
/* 58 */     checkValidity();
/*    */   }
/*    */ 
/*    */   public PositiveInteger(String val, int radix) {
/* 62 */     super(val, radix);
/* 63 */     checkValidity();
/*    */   }
/*    */ 
/*    */   private void checkValidity()
/*    */   {
/* 71 */     if (compareTo(this.iMinInclusive) < 0)
/* 72 */       throw new NumberFormatException(Messages.getMessage("badposInt00") + ":  " + this);
/*    */   }
/*    */ 
/*    */   public Object writeReplace()
/*    */     throws ObjectStreamException
/*    */   {
/* 84 */     return new BigIntegerRep(toByteArray());
/*    */   }
/*    */   protected static class BigIntegerRep implements Serializable {
/*    */     private byte[] array;
/*    */ 
/* 90 */     protected BigIntegerRep(byte[] array) { this.array = array; }
/*    */ 
/*    */     protected Object readResolve() throws ObjectStreamException {
/* 93 */       return new PositiveInteger(this.array);
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.PositiveInteger
 * JD-Core Version:    0.6.0
 */