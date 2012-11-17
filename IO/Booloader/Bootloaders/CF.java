/*    */ package IO.Booloader.Bootloaders;
/*    */ 
/*    */ public class CF extends GenericBootloader
/*    */ {
/*    */   private int slot0;
/*    */   private int slot1;
/*    */   private int LDV1;
/*    */   private int version1;
/*    */   private int offset0;
/*    */   private int offset1;
/*    */   private byte[] data;
/*    */   private byte[] data1;
/*    */   private byte[] pairingData1;
/*    */ 
/*    */   public void setData(byte[] data)
/*    */   {
/* 10 */     this.data = data;
/* 11 */     byte[] pairing = { data[542], data[541], data[540] };
/* 12 */     setPairingData(pairing);
/* 13 */     setLDV(data[543]);
/*    */   }
/*    */ 
/*    */   public int getSlot0() {
/* 17 */     return this.slot0;
/*    */   }
/*    */ 
/*    */   public void setSlot0(int slot0)
/*    */   {
/* 22 */     this.slot0 = slot0;
/*    */   }
/*    */ 
/*    */   public int getSlot1()
/*    */   {
/* 27 */     return this.slot1;
/*    */   }
/*    */ 
/*    */   public void setSlot1(int slot1)
/*    */   {
/* 32 */     this.slot1 = slot1;
/*    */   }
/*    */ 
/*    */   public int getOffset0()
/*    */   {
/* 37 */     return this.offset0;
/*    */   }
/*    */ 
/*    */   public void setOffset0(int offset0)
/*    */   {
/* 42 */     this.offset0 = offset0;
/*    */   }
/*    */ 
/*    */   public int getOffset1()
/*    */   {
/* 47 */     return this.offset1;
/*    */   }
/*    */ 
/*    */   public void setOffset1(int offset1)
/*    */   {
/* 52 */     this.offset1 = offset1;
/*    */   }
/*    */ 
/*    */   public int getLDV1()
/*    */   {
/* 57 */     return this.LDV1;
/*    */   }
/*    */ 
/*    */   public void setLDV1(int lDV1)
/*    */   {
/* 62 */     this.LDV1 = lDV1;
/*    */   }
/*    */ 
/*    */   public byte[] getPairingData1()
/*    */   {
/* 67 */     return this.pairingData1;
/*    */   }
/*    */ 
/*    */   public void setPairingData1(byte[] pairingData1)
/*    */   {
/* 72 */     this.pairingData1 = pairingData1;
/*    */   }
/*    */ 
/*    */   public int getVersion1()
/*    */   {
/* 77 */     return this.version1;
/*    */   }
/*    */ 
/*    */   public byte[] getData()
/*    */   {
/* 82 */     return this.data;
/*    */   }
/*    */ 
/*    */   public void setVersion1(int version1) {
/* 86 */     this.version1 = version1;
/*    */   }
/*    */ 
/*    */   public byte[] getData1()
/*    */   {
/* 91 */     return this.data1;
/*    */   }
/*    */ 
/*    */   public void setData1(byte[] data) {
/* 95 */     this.data1 = data;
/* 96 */     byte[] pairing1 = { data[542], data[541], data[540] };
/* 97 */     setPairingData1(pairing1);
/* 98 */     setLDV1(data[543]);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     IO.Booloader.Bootloaders.CF
 * JD-Core Version:    0.6.0
 */