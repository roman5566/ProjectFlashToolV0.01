/*    */ package IO.NandOps;
/*    */ 
/*    */ public class NandIO
/*    */ {
/*    */   private byte[] userData;
/*    */   private byte[] spareData;
/*    */   private int Pages;
/*    */ 
/*    */   public NandIO()
/*    */   {
/*    */   }
/*    */ 
/*    */   public NandIO(byte[] nandFile)
/*    */   {
/* 10 */     this.Pages = getPages(nandFile);
/* 11 */     readSplits(nandFile, this.Pages);
/*    */   }
/*    */ 
/*    */   private void readSplits(byte[] nandFile, int pages) {
/* 15 */     int userOffset = 0; int spareOffset = 0; int globalOffset = 0;
/* 16 */     this.userData = new byte[512 * pages];
/* 17 */     this.spareData = new byte[16 * pages];
/*    */ 
/* 19 */     if (nandFile.length >= 528 * pages)
/*    */     {
/* 21 */       for (int i = 0; i < pages; i++)
/*    */       {
/* 23 */         for (int j = 0; j < 512; j++)
/*    */         {
/* 25 */           this.userData[userOffset] = nandFile[globalOffset];
/* 26 */           userOffset++;
/* 27 */           globalOffset++;
/*    */         }
/* 29 */         for (int j = 0; j < 16; j++)
/*    */         {
/* 31 */           this.spareData[spareOffset] = nandFile[globalOffset];
/* 32 */           spareOffset++;
/* 33 */           globalOffset++;
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   private int getPages(byte[] nandFile) {
/* 40 */     switch (nandFile.length)
/*    */     {
/*    */     case 17301504:
/* 43 */       return 32768;
/*    */     case 69206016:
/*    */     case 276824064:
/*    */     case 553648128:
/* 47 */       return 131072;
/*    */     }
/* 49 */     return nandFile.length / 1024;
/*    */   }
/*    */ 
/*    */   public static byte[] getPageSquare(int page, byte[] block)
/*    */   {
/* 54 */     int pos = page * 32;
/* 55 */     byte[] ecc = new byte[32];
/* 56 */     for (int i = 0; i < 32; i++)
/* 57 */       ecc[i] = block[(pos + i)];
/* 58 */     return ecc;
/*    */   }
/*    */ 
/*    */   public byte[] getBlock(int block) {
/* 62 */     int pos = block * 512;
/* 63 */     byte[] fullblock = new byte[512];
/* 64 */     for (int i = 0; i < 512; i++)
/* 65 */       fullblock[i] = this.userData[(pos + i)];
/* 66 */     return fullblock;
/*    */   }
/*    */ 
/*    */   public byte[] getUserData() {
/* 70 */     return this.userData;
/*    */   }
/*    */ 
/*    */   public void setUserData(byte[] userData) {
/* 74 */     this.userData = userData;
/*    */   }
/*    */ 
/*    */   public byte[] getSpareData() {
/* 78 */     return this.spareData;
/*    */   }
/*    */ 
/*    */   public void setSpareData(byte[] spareData) {
/* 82 */     this.spareData = spareData;
/*    */   }
/*    */ 
/*    */   public int getPages() {
/* 86 */     return this.Pages;
/*    */   }
/*    */ 
/*    */   public void setPages(int pages) {
/* 90 */     this.Pages = pages;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     IO.NandOps.NandIO
 * JD-Core Version:    0.6.0
 */