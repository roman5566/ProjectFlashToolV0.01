/*    */ package org.apache.axis.types;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ import java.util.StringTokenizer;
/*    */ 
/*    */ public class IDRefs extends NCName
/*    */ {
/*    */   private IDRef[] idrefs;
/*    */ 
/*    */   public IDRefs()
/*    */   {
/*    */   }
/*    */ 
/*    */   public IDRefs(String stValue)
/*    */     throws IllegalArgumentException
/*    */   {
/* 40 */     setValue(stValue);
/*    */   }
/*    */ 
/*    */   public void setValue(String stValue) {
/* 44 */     StringTokenizer tokenizer = new StringTokenizer(stValue);
/* 45 */     int count = tokenizer.countTokens();
/* 46 */     this.idrefs = new IDRef[count];
/* 47 */     for (int i = 0; i < count; i++)
/* 48 */       this.idrefs[i] = new IDRef(tokenizer.nextToken());
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 53 */     StringBuffer buf = new StringBuffer();
/* 54 */     for (int i = 0; i < this.idrefs.length; i++) {
/* 55 */       IDRef ref = this.idrefs[i];
/* 56 */       if (i > 0) buf.append(" ");
/* 57 */       buf.append(ref.toString());
/*    */     }
/* 59 */     return buf.toString();
/*    */   }
/*    */ 
/*    */   public boolean equals(Object object)
/*    */   {
/* 71 */     if (object == this) {
/* 72 */       return true;
/*    */     }
/* 74 */     if ((object instanceof IDRefs)) {
/* 75 */       IDRefs that = (IDRefs)object;
/* 76 */       if (this.idrefs.length == that.idrefs.length) {
/* 77 */         Set ourSet = new HashSet(Arrays.asList(this.idrefs));
/* 78 */         Set theirSet = new HashSet(Arrays.asList(that.idrefs));
/* 79 */         return ourSet.equals(theirSet);
/*    */       }
/* 81 */       return false;
/*    */     }
/*    */ 
/* 84 */     return false;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 95 */     int hash = 0;
/* 96 */     for (int i = 0; i < this.idrefs.length; i++) {
/* 97 */       hash += this.idrefs[i].hashCode();
/*    */     }
/* 99 */     return hash;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.IDRefs
 * JD-Core Version:    0.6.0
 */