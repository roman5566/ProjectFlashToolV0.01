/*    */ package org.apache.commons.codec.language.bm;
/*    */ 
/*    */ public enum RuleType
/*    */ {
/* 29 */   APPROX("approx"), 
/*    */ 
/* 31 */   EXACT("exact"), 
/*    */ 
/* 33 */   RULES("rules");
/*    */ 
/*    */   private final String name;
/*    */ 
/* 38 */   private RuleType(String name) { this.name = name; }
/*    */ 
/*    */   public String getName()
/*    */   {
/* 42 */     return this.name;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.language.bm.RuleType
 * JD-Core Version:    0.6.0
 */