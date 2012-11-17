/*    */ package IO.FileSystem;
/*    */ 
/*    */ public class SpareData
/*    */ {
/*  5 */   protected short BlockID = -1;
/*  6 */   protected boolean BadBlock = false;
/*  7 */   protected int FsSequence = -1;
/*  8 */   protected short FsSize = -1;
/*  9 */   protected byte FsPageCount = -1;
/* 10 */   protected byte FsBlockType = -1;
/* 11 */   protected byte[] EDC = null;
/*    */ 
/*    */   public short getBlockID() {
/* 14 */     return this.BlockID;
/*    */   }
/*    */ 
/*    */   public short getFsSize() {
/* 18 */     return this.FsSize;
/*    */   }
/*    */ 
/*    */   public byte getFsPageCount() {
/* 22 */     return this.FsPageCount;
/*    */   }
/*    */ 
/*    */   public byte getFsBlockType() {
/* 26 */     return this.FsBlockType;
/*    */   }
/*    */ 
/*    */   public byte[] getEDC() {
/* 30 */     return this.EDC;
/*    */   }
/*    */ 
/*    */   public int getFsSequence() {
/* 34 */     return this.FsSequence;
/*    */   }
/*    */ 
/*    */   public void setFsSequence(int fsSequence) {
/* 38 */     this.FsSequence = fsSequence;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     IO.FileSystem.SpareData
 * JD-Core Version:    0.6.0
 */