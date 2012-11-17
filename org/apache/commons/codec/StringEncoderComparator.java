/*    */ package org.apache.commons.codec;
/*    */ 
/*    */ import java.util.Comparator;
/*    */ 
/*    */ public class StringEncoderComparator
/*    */   implements Comparator
/*    */ {
/*    */   private final StringEncoder stringEncoder;
/*    */ 
/*    */   /** @deprecated */
/*    */   public StringEncoderComparator()
/*    */   {
/* 44 */     this.stringEncoder = null;
/*    */   }
/*    */ 
/*    */   public StringEncoderComparator(StringEncoder stringEncoder)
/*    */   {
/* 54 */     this.stringEncoder = stringEncoder;
/*    */   }
/*    */ 
/*    */   public int compare(Object o1, Object o2)
/*    */   {
/* 72 */     int compareCode = 0;
/*    */     try
/*    */     {
/* 75 */       Comparable s1 = (Comparable)this.stringEncoder.encode(o1);
/* 76 */       Comparable s2 = (Comparable)this.stringEncoder.encode(o2);
/* 77 */       compareCode = s1.compareTo(s2);
/*    */     } catch (EncoderException ee) {
/* 79 */       compareCode = 0;
/*    */     }
/* 81 */     return compareCode;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.StringEncoderComparator
 * JD-Core Version:    0.6.0
 */