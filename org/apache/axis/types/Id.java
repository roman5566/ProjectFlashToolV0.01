/*    */ package org.apache.axis.types;
/*    */ 
/*    */ import org.apache.axis.utils.Messages;
/*    */ 
/*    */ public class Id extends NCName
/*    */ {
/*    */   public Id()
/*    */   {
/*    */   }
/*    */ 
/*    */   public Id(String stValue)
/*    */     throws IllegalArgumentException
/*    */   {
/*    */     try
/*    */     {
/* 39 */       setValue(stValue);
/*    */     }
/*    */     catch (IllegalArgumentException e)
/*    */     {
/* 43 */       throw new IllegalArgumentException(Messages.getMessage("badIdType00") + "data=[" + stValue + "]");
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setValue(String stValue)
/*    */     throws IllegalArgumentException
/*    */   {
/* 56 */     if (!isValid(stValue)) {
/* 57 */       throw new IllegalArgumentException(Messages.getMessage("badIdType00") + " data=[" + stValue + "]");
/*    */     }
/*    */ 
/* 60 */     this.m_value = stValue;
/*    */   }
/*    */ 
/*    */   public static boolean isValid(String stValue)
/*    */   {
/* 70 */     return NCName.isValid(stValue);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.Id
 * JD-Core Version:    0.6.0
 */