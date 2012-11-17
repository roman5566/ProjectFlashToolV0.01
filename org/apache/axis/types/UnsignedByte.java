/*    */ package org.apache.axis.types;
/*    */ 
/*    */ import org.apache.axis.utils.Messages;
/*    */ 
/*    */ public class UnsignedByte extends UnsignedShort
/*    */ {
/*    */   public UnsignedByte()
/*    */   {
/*    */   }
/*    */ 
/*    */   public UnsignedByte(long sValue)
/*    */     throws NumberFormatException
/*    */   {
/* 39 */     setValue(sValue);
/*    */   }
/*    */ 
/*    */   public UnsignedByte(String sValue) throws NumberFormatException {
/* 43 */     setValue(Long.parseLong(sValue));
/*    */   }
/*    */ 
/*    */   public void setValue(long sValue)
/*    */     throws NumberFormatException
/*    */   {
/* 53 */     if (!isValid(sValue)) {
/* 54 */       throw new NumberFormatException(Messages.getMessage("badUnsignedByte00") + String.valueOf(sValue) + "]");
/*    */     }
/*    */ 
/* 57 */     this.lValue = new Long(sValue);
/*    */   }
/*    */ 
/*    */   public static boolean isValid(long sValue)
/*    */   {
/* 67 */     return (sValue >= 0L) && (sValue <= 255L);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.UnsignedByte
 * JD-Core Version:    0.6.0
 */