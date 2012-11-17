/*    */ package IO.FileSystem;
/*    */ 
/*    */ import IO.Misc;
/*    */ 
/*    */ public class SpareDataSmallBlock extends SpareData
/*    */ {
/*    */   private byte fsS0;
/*    */   private byte fsS1;
/*    */   private byte fsS2;
/*    */   private byte fsS3;
/*    */   private byte badblockind;
/*    */ 
/*    */   public SpareDataSmallBlock(byte[] sparedata)
/*    */   {
/* 11 */     this.BlockID = (short)Misc.getInt16(sparedata, 0);
/* 12 */     this.fsS0 = sparedata[2];
/* 13 */     this.fsS1 = sparedata[3];
/* 14 */     this.fsS2 = sparedata[4];
/* 15 */     this.badblockind = sparedata[5];
/* 16 */     this.fsS3 = sparedata[6];
/* 17 */     this.FsSize = (short)Misc.getInt16(sparedata, 7);
/* 18 */     this.FsPageCount = sparedata[9];
/* 19 */     this.EDC = new byte[] { sparedata[12], sparedata[13], sparedata[14], sparedata[15] };
/*    */   }
/*    */ 
/*    */   public boolean getBadblockind() {
/* 23 */     return this.badblockind != 255;
/*    */   }
/*    */ 
/*    */   public int getFsSequence()
/*    */   {
/* 28 */     return (this.fsS3 << 24) + (this.fsS2 << 16) + (this.fsS1 << 8) + this.fsS0;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     IO.FileSystem.SpareDataSmallBlock
 * JD-Core Version:    0.6.0
 */