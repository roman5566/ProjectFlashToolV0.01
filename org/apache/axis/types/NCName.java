/*    */ package org.apache.axis.types;
/*    */ 
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.axis.utils.XMLChar;
/*    */ 
/*    */ public class NCName extends Name
/*    */ {
/*    */   public NCName()
/*    */   {
/*    */   }
/*    */ 
/*    */   public NCName(String stValue)
/*    */     throws IllegalArgumentException
/*    */   {
/*    */     try
/*    */     {
/* 43 */       setValue(stValue);
/*    */     }
/*    */     catch (IllegalArgumentException e)
/*    */     {
/* 47 */       throw new IllegalArgumentException(Messages.getMessage("badNCNameType00") + "data=[" + stValue + "]");
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setValue(String stValue)
/*    */     throws IllegalArgumentException
/*    */   {
/* 60 */     if (!isValid(stValue)) {
/* 61 */       throw new IllegalArgumentException(Messages.getMessage("badNCNameType00") + " data=[" + stValue + "]");
/*    */     }
/*    */ 
/* 64 */     this.m_value = stValue;
/*    */   }
/*    */ 
/*    */   public static boolean isValid(String stValue)
/*    */   {
/* 76 */     boolean bValid = true;
/*    */ 
/* 78 */     for (int scan = 0; scan < stValue.length(); scan++) {
/* 79 */       if (scan == 0)
/* 80 */         bValid = XMLChar.isNCNameStart(stValue.charAt(scan));
/*    */       else
/* 82 */         bValid = XMLChar.isNCName(stValue.charAt(scan));
/* 83 */       if (!bValid)
/*    */         break;
/*    */     }
/* 86 */     return bValid;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.NCName
 * JD-Core Version:    0.6.0
 */