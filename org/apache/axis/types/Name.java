/*    */ package org.apache.axis.types;
/*    */ 
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.axis.utils.XMLChar;
/*    */ 
/*    */ public class Name extends Token
/*    */ {
/*    */   public Name()
/*    */   {
/*    */   }
/*    */ 
/*    */   public Name(String stValue)
/*    */     throws IllegalArgumentException
/*    */   {
/*    */     try
/*    */     {
/* 43 */       setValue(stValue);
/*    */     }
/*    */     catch (IllegalArgumentException e)
/*    */     {
/* 47 */       throw new IllegalArgumentException(Messages.getMessage("badNameType00") + "data=[" + stValue + "]");
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setValue(String stValue)
/*    */     throws IllegalArgumentException
/*    */   {
/* 60 */     if (!isValid(stValue)) {
/* 61 */       throw new IllegalArgumentException(Messages.getMessage("badNameType00") + " data=[" + stValue + "]");
/*    */     }
/*    */ 
/* 64 */     this.m_value = stValue;
/*    */   }
/*    */ 
/*    */   public static boolean isValid(String stValue)
/*    */   {
/* 75 */     boolean bValid = true;
/*    */ 
/* 77 */     for (int scan = 0; scan < stValue.length(); scan++) {
/* 78 */       if (scan == 0)
/* 79 */         bValid = XMLChar.isNameStart(stValue.charAt(scan));
/*    */       else
/* 81 */         bValid = XMLChar.isName(stValue.charAt(scan));
/* 82 */       if (!bValid) {
/*    */         break;
/*    */       }
/*    */     }
/* 86 */     return bValid;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.Name
 * JD-Core Version:    0.6.0
 */