/*    */ package IO.FileSystem;
/*    */ 
/*    */ import IO.NandOps.NandIO;
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ public class LoadFileTable
/*    */ {
/*    */   private RootFileTable[] rft;
/*    */ 
/*    */   public LoadFileTable(NandIO nio)
/*    */   {
/* 12 */     ArrayList aLRFT = new ArrayList();
/* 13 */     int blocks = nio.getSpareData().length / 512;
/* 14 */     for (int i = 0; i < blocks; i++)
/*    */     {
/* 16 */       int pos = i * 512;
/* 17 */       byte[] blockSpareData = new byte[512];
/* 18 */       for (int j = 0; j < 512; j++)
/* 19 */         blockSpareData[j] = nio.getSpareData()[(pos + j)];
/* 20 */       SpareData sd = new SpareDataSmallBlock(blockSpareData);
/* 21 */       if (((sd.getFsSequence() == 0) || (sd.getFsSize() != 0) || (sd.getFsPageCount() != 0) || (sd.getBlockID() <= 0)) && (sd.getFsBlockType() != 48))
/*    */         continue;
/* 23 */       aLRFT.add(new RootFileTable(nio, blocks, i));
/*    */     }
/*    */ 
/* 26 */     this.rft = ((RootFileTable[])aLRFT.toArray(new RootFileTable[aLRFT.size()]));
/*    */   }
/*    */ 
/*    */   public RootFileTable[] getRft() {
/* 30 */     return this.rft;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     IO.FileSystem.LoadFileTable
 * JD-Core Version:    0.6.0
 */