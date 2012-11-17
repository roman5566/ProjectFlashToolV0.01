/*    */ package org.apache.axis.types;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ import java.util.StringTokenizer;
/*    */ 
/*    */ public class NMTokens extends NCName
/*    */ {
/*    */   private NMToken[] tokens;
/*    */ 
/*    */   public NMTokens()
/*    */   {
/*    */   }
/*    */ 
/*    */   public NMTokens(String stValue)
/*    */     throws IllegalArgumentException
/*    */   {
/* 39 */     setValue(stValue);
/*    */   }
/*    */ 
/*    */   public void setValue(String stValue) {
/* 43 */     StringTokenizer tokenizer = new StringTokenizer(stValue);
/* 44 */     int count = tokenizer.countTokens();
/* 45 */     this.tokens = new NMToken[count];
/* 46 */     for (int i = 0; i < count; i++)
/* 47 */       this.tokens[i] = new NMToken(tokenizer.nextToken());
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 52 */     StringBuffer buf = new StringBuffer();
/* 53 */     for (int i = 0; i < this.tokens.length; i++) {
/* 54 */       NMToken token = this.tokens[i];
/* 55 */       if (i > 0) buf.append(" ");
/* 56 */       buf.append(token.toString());
/*    */     }
/* 58 */     return buf.toString();
/*    */   }
/*    */ 
/*    */   public boolean equals(Object object)
/*    */   {
/* 70 */     if (object == this) {
/* 71 */       return true;
/*    */     }
/* 73 */     if ((object instanceof NMTokens)) {
/* 74 */       NMTokens that = (NMTokens)object;
/* 75 */       if (this.tokens.length == that.tokens.length) {
/* 76 */         Set ourSet = new HashSet(Arrays.asList(this.tokens));
/* 77 */         Set theirSet = new HashSet(Arrays.asList(that.tokens));
/* 78 */         return ourSet.equals(theirSet);
/*    */       }
/* 80 */       return false;
/*    */     }
/*    */ 
/* 83 */     return false;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 94 */     int hash = 0;
/* 95 */     for (int i = 0; i < this.tokens.length; i++) {
/* 96 */       hash += this.tokens[i].hashCode();
/*    */     }
/* 98 */     return hash;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.NMTokens
 * JD-Core Version:    0.6.0
 */