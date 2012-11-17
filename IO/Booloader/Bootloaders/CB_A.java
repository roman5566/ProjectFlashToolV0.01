/*    */ package IO.Booloader.Bootloaders;
/*    */ 
/*    */ public class CB_A extends GenericBootloader
/*    */ {
/*    */   private byte[] data;
/*    */ 
/*    */   public void setData(byte[] s)
/*    */   {
/*  9 */     this.data = s;
/* 10 */     byte[] pairing = { s[34], s[33], s[32] };
/* 11 */     setPairingData(pairing);
/* 12 */     setLDV(s[35]);
/*    */   }
/*    */ 
/*    */   public byte[] getData()
/*    */   {
/* 17 */     return this.data;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     IO.Booloader.Bootloaders.CB_A
 * JD-Core Version:    0.6.0
 */