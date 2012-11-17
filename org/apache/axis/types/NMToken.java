/*    */ package org.apache.axis.types;
/*    */ 
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.axis.utils.XMLChar;
/*    */ 
/*    */ public class NMToken extends Token
/*    */ {
/*    */   public NMToken()
/*    */   {
/*    */   }
/*    */ 
/*    */   public NMToken(String stValue)
/*    */     throws IllegalArgumentException
/*    */   {
/*    */     try
/*    */     {
/* 44 */       setValue(stValue);
/*    */     }
/*    */     catch (IllegalArgumentException e)
/*    */     {
/* 48 */       throw new IllegalArgumentException(Messages.getMessage("badNmtoken00") + "data=[" + stValue + "]");
/*    */     }
/*    */   }
/*    */ 
/*    */   public static boolean isValid(String stValue)
/*    */   {
/* 63 */     for (int scan = 0; scan < stValue.length(); scan++) {
/* 64 */       if (!XMLChar.isName(stValue.charAt(scan))) {
/* 65 */         return false;
/*    */       }
/*    */     }
/* 68 */     return true;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.NMToken
 * JD-Core Version:    0.6.0
 */