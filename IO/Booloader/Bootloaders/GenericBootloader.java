/*    */ package IO.Booloader.Bootloaders;
/*    */ 
/*    */ public class GenericBootloader
/*    */ {
/*    */   private int version;
/*    */   private int LDV;
/*    */   private byte[] data;
/*    */   private byte[] pairingData;
/*    */ 
/*    */   public int getVersion()
/*    */   {
/*  9 */     return this.version;
/*    */   }
/*    */ 
/*    */   public void setVersion(int version)
/*    */   {
/* 14 */     this.version = version;
/*    */   }
/*    */ 
/*    */   public int getLDV()
/*    */   {
/* 19 */     return this.LDV;
/*    */   }
/*    */ 
/*    */   public void setLDV(int lDV)
/*    */   {
/* 24 */     this.LDV = lDV;
/*    */   }
/*    */ 
/*    */   public byte[] getPairingData()
/*    */   {
/* 29 */     return this.pairingData;
/*    */   }
/*    */ 
/*    */   public void setPairingData(byte[] pairing)
/*    */   {
/* 34 */     this.pairingData = pairing;
/*    */   }
/*    */ 
/*    */   public byte[] getData()
/*    */   {
/* 39 */     return this.data;
/*    */   }
/*    */ 
/*    */   public void setData(byte[] data)
/*    */   {
/* 44 */     this.data = data;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     IO.Booloader.Bootloaders.GenericBootloader
 * JD-Core Version:    0.6.0
 */