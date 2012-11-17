/*    */ package org.apache.axis.types;
/*    */ 
/*    */ import org.apache.axis.utils.Messages;
/*    */ 
/*    */ public class Language extends Token
/*    */ {
/*    */   public Language()
/*    */   {
/*    */   }
/*    */ 
/*    */   public Language(String stValue)
/*    */     throws IllegalArgumentException
/*    */   {
/*    */     try
/*    */     {
/* 44 */       setValue(stValue);
/*    */     }
/*    */     catch (IllegalArgumentException e)
/*    */     {
/* 48 */       throw new IllegalArgumentException(Messages.getMessage("badLanguage00") + "data=[" + stValue + "]");
/*    */     }
/*    */   }
/*    */ 
/*    */   public static boolean isValid(String stValue)
/*    */   {
/* 64 */     return true;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.Language
 * JD-Core Version:    0.6.0
 */