/*    */ package IO.FileSystem;
/*    */ 
/*    */ import IO.Misc;
/*    */ 
/*    */ public class FileEntry
/*    */ {
/*  7 */   protected int PageNumber = -1;
/*  8 */   protected String FineName = "";
/*  9 */   protected int BlockNumber = -1;
/* 10 */   protected int Size = -1;
/* 11 */   protected int TimeStamp = -1;
/* 12 */   protected boolean Deleted = false;
/* 13 */   protected byte[] data = null;
/*    */ 
/*    */   public int getPageNumber() {
/* 16 */     return this.PageNumber;
/*    */   }
/*    */ 
/*    */   public void setPageNumber(int pageNumber) {
/* 20 */     this.PageNumber = pageNumber;
/*    */   }
/*    */ 
/*    */   public String getFineName() {
/* 24 */     return this.FineName;
/*    */   }
/*    */ 
/*    */   public void setFineName(String fineName) {
/* 28 */     this.FineName = fineName;
/*    */   }
/*    */ 
/*    */   public int getBlockNumber() {
/* 32 */     return this.BlockNumber;
/*    */   }
/*    */ 
/*    */   public void setBlockNumber(int blockNumber) {
/* 36 */     this.BlockNumber = blockNumber;
/*    */   }
/*    */ 
/*    */   public int getSize() {
/* 40 */     return this.Size;
/*    */   }
/*    */ 
/*    */   public void setSize(int size) {
/* 44 */     this.Size = size;
/*    */   }
/*    */ 
/*    */   public int getTimeStamp() {
/* 48 */     return this.TimeStamp;
/*    */   }
/*    */ 
/*    */   public void setTimeStamp(int timeStamp) {
/* 52 */     this.TimeStamp = timeStamp;
/*    */   }
/*    */ 
/*    */   public boolean isDeleted() {
/* 56 */     return this.Deleted;
/*    */   }
/*    */ 
/*    */   public void setDeleted(boolean deleted) {
/* 60 */     this.Deleted = deleted;
/*    */   }
/*    */ 
/*    */   public byte[] getData() {
/* 64 */     return this.data;
/*    */   }
/*    */ 
/*    */   public void setData(byte[] data) {
/* 68 */     this.data = data;
/* 69 */     setBlockNumber(Misc.getUInt16(data, 15));
/* 70 */     setSize(Misc.swapUInt32(Misc.getUInt32(data, 17)));
/* 71 */     setTimeStamp(Misc.getUInt32(data, 21));
/* 72 */     byte[] name = new byte[16];
/* 73 */     for (int i = 0; i < 16; i++)
/* 74 */       name[i] = data[i];
/* 75 */     this.FineName = new String(name);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     IO.FileSystem.FileEntry
 * JD-Core Version:    0.6.0
 */