/*    */ package org.apache.axis.types;
/*    */ 
/*    */ import java.io.ObjectStreamException;
/*    */ import java.io.Serializable;
/*    */ import java.math.BigInteger;
/*    */ import java.util.Random;
/*    */ import org.apache.axis.utils.Messages;
/*    */ 
/*    */ public class NegativeInteger extends NonPositiveInteger
/*    */ {
/* 71 */   private BigInteger zero = new BigInteger("0");
/*    */ 
/*    */   public NegativeInteger(byte[] val)
/*    */   {
/* 39 */     super(val);
/* 40 */     checkValidity();
/*    */   }
/*    */ 
/*    */   public NegativeInteger(int signum, byte[] magnitude) {
/* 44 */     super(signum, magnitude);
/* 45 */     checkValidity();
/*    */   }
/*    */ 
/*    */   public NegativeInteger(int bitLength, int certainty, Random rnd) {
/* 49 */     super(bitLength, certainty, rnd);
/* 50 */     checkValidity();
/*    */   }
/*    */ 
/*    */   public NegativeInteger(int numBits, Random rnd) {
/* 54 */     super(numBits, rnd);
/* 55 */     checkValidity();
/*    */   }
/*    */ 
/*    */   public NegativeInteger(String val) {
/* 59 */     super(val);
/* 60 */     checkValidity();
/*    */   }
/*    */ 
/*    */   public NegativeInteger(String val, int radix) {
/* 64 */     super(val, radix);
/* 65 */     checkValidity();
/*    */   }
/*    */ 
/*    */   private void checkValidity()
/*    */   {
/* 73 */     if (compareTo(this.zero) >= 0)
/* 74 */       throw new NumberFormatException(Messages.getMessage("badnegInt00") + ":  " + this);
/*    */   }
/*    */ 
/*    */   public Object writeReplace()
/*    */     throws ObjectStreamException
/*    */   {
/* 86 */     return new BigIntegerRep(toByteArray());
/*    */   }
/*    */   protected static class BigIntegerRep implements Serializable {
/*    */     private byte[] array;
/*    */ 
/* 92 */     protected BigIntegerRep(byte[] array) { this.array = array; }
/*    */ 
/*    */     protected Object readResolve() throws ObjectStreamException {
/* 95 */       return new NegativeInteger(this.array);
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.NegativeInteger
 * JD-Core Version:    0.6.0
 */