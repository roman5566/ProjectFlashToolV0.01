/*    */ package org.apache.axis.utils;
/*    */ 
/*    */ class Token
/*    */ {
/*    */   public static final int TOKEN_SEPARATOR = 0;
/*    */   public static final int TOKEN_STRING = 1;
/*    */   private final int m_type;
/*    */   private final String m_value;
/*    */ 
/*    */   public Token(int type, String value)
/*    */   {
/* 43 */     this.m_type = type;
/* 44 */     this.m_value = value;
/*    */   }
/*    */ 
/*    */   public final String getValue()
/*    */   {
/* 52 */     return this.m_value;
/*    */   }
/*    */ 
/*    */   public final int getType()
/*    */   {
/* 60 */     return this.m_type;
/*    */   }
/*    */ 
/*    */   public final String toString()
/*    */   {
/* 68 */     return this.m_type + ":" + this.m_value;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.Token
 * JD-Core Version:    0.6.0
 */