/*    */ package org.apache.commons.codec.language.bm;
/*    */ 
/*    */ public enum NameType
/*    */ {
/* 31 */   ASHKENAZI("ash"), 
/*    */ 
/* 34 */   GENERIC("gen"), 
/*    */ 
/* 37 */   SEPHARDIC("sep");
/*    */ 
/*    */   private final String name;
/*    */ 
/* 42 */   private NameType(String name) { this.name = name;
/*    */   }
/*    */ 
/*    */   public String getName()
/*    */   {
/* 51 */     return this.name;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.language.bm.NameType
 * JD-Core Version:    0.6.0
 */