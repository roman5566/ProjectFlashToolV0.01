/*    */ package org.apache.axis.types;
/*    */ 
/*    */ import org.apache.axis.utils.Messages;
/*    */ 
/*    */ public class UnsignedShort extends UnsignedInt
/*    */ {
/*    */   public UnsignedShort()
/*    */   {
/*    */   }
/*    */ 
/*    */   public UnsignedShort(long sValue)
/*    */     throws NumberFormatException
/*    */   {
/* 37 */     setValue(sValue);
/*    */   }
/*    */ 
/*    */   public UnsignedShort(String sValue) throws NumberFormatException {
/* 41 */     setValue(Long.parseLong(sValue));
/*    */   }
/*    */ 
/*    */   public void setValue(long sValue)
/*    */     throws NumberFormatException
/*    */   {
/* 51 */     if (!isValid(sValue)) {
/* 52 */       throw new NumberFormatException(Messages.getMessage("badUnsignedShort00") + String.valueOf(sValue) + "]");
/*    */     }
/*    */ 
/* 55 */     this.lValue = new Long(sValue);
/*    */   }
/*    */ 
/*    */   public static boolean isValid(long sValue)
/*    */   {
/* 65 */     return (sValue >= 0L) && (sValue <= 65535L);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.UnsignedShort
 * JD-Core Version:    0.6.0
 */