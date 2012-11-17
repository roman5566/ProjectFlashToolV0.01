/*     */ package IO.FileSystem;
/*     */ 
/*     */ import IO.Misc;
/*     */ import IO.NandOps.NandIO;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class RootFileTable
/*     */ {
/*     */   private int[] BlockMap;
/*     */   private int blocks;
/*     */   private int BlockNumber;
/*     */   private NandIO nio;
/*  14 */   private ArrayList<FileEntry> entries = new ArrayList();
/*     */ 
/*     */   public RootFileTable(NandIO nio, int blocks, int blockNumber) {
/*  17 */     this.BlockNumber = blockNumber;
/*  18 */     this.blocks = blocks;
/*  19 */     this.nio = nio;
/*  20 */     read();
/*     */   }
/*     */ 
/*     */   public int[] GetBlockChain(int startblock, int limit, int blocks) {
/*  24 */     ArrayList blockList = new ArrayList();
/*  25 */     int currentBlock = startblock;
/*     */     do
/*     */     {
/*  28 */       blockList.add(Integer.valueOf(currentBlock));
/*  29 */       currentBlock = this.BlockMap[currentBlock];
/*  30 */       currentBlock &= 32767;
/*     */     }
/*  32 */     while ((currentBlock != 0) && (blocks > currentBlock) && (blockList.size() < limit));
/*  33 */     int[] k = new int[blockList.size()];
/*  34 */     for (int i = 0; i < blockList.size(); i++)
/*  35 */       k[i] = ((Integer)blockList.get(i)).intValue();
/*  36 */     return k;
/*     */   }
/*     */ 
/*     */   public void createDefaults() {
/*  40 */     this.BlockMap = new int[this.blocks];
/*  41 */     for (int i = 0; i < this.blocks; i++)
/*  42 */       this.BlockMap[i] = 8190;
/*  43 */     this.BlockMap[this.BlockNumber] = 8191;
/*  44 */     for (int i = 0; i < 34; i++)
/*  45 */       this.BlockMap[i] = 8187;
/*     */   }
/*     */ 
/*     */   public void read() {
/*  49 */     ArrayList blockMapPages = new ArrayList();
/*  50 */     ArrayList fileNamePages = new ArrayList();
/*  51 */     int currentBlock = this.BlockNumber;
/*  52 */     for (int i = 0; i < 32; i++)
/*  53 */       if (i % 2 == 0)
/*  54 */         blockMapPages.add(Integer.valueOf(currentBlock * 32 + i));
/*     */       else
/*  56 */         fileNamePages.add(Integer.valueOf(currentBlock * 32 + i));
/*  57 */     boolean breakk = false;
/*  58 */     for (int i = 0; i < fileNamePages.size(); i++)
/*     */     {
/*  60 */       if (breakk)
/*     */         break;
/*  62 */       for (int j = 0; j < 16; j++)
/*     */       {
/*  64 */         FileEntry fE = new FileEntry();
/*     */ 
/*  66 */         fE.setData(NandIO.getPageSquare(j, this.nio.getBlock(((Integer)fileNamePages.get(i)).intValue())));
/*     */ 
/*  68 */         if (!Misc.isUTF8(fE.getFineName()))
/*     */         {
/*  72 */           breakk = true;
/*     */         }
/*     */         else
/*     */         {
/*  77 */           System.out.print("\n" + fE.getFineName() + " " + fE.getSize() + " " + fE.BlockNumber);
/*  78 */           if (!this.entries.contains(fE.getFineName()))
/*  79 */             this.entries.add(fE);
/*     */         }
/*     */       }
/*     */     }
/*  83 */     System.err.print("-");
/*  84 */     this.BlockMap = new int[1024];
/*  85 */     for (int i = 0; i < blockMapPages.size(); i++)
/*     */     {
/*  87 */       byte[] data = this.nio.getBlock(currentBlock);
/*  88 */       for (int j = 0; j < 269; j++)
/*     */       {
/*  90 */         if (j >= 1024)
/*     */           break;
/*  92 */         this.BlockMap[j] = Misc.getUInt16(data, i * 4);
/*     */       }
/*     */     }
/*  95 */     if (this.BlockNumber < 1024)
/*  96 */       this.BlockMap[this.BlockNumber] = 8191;
/*     */   }
/*     */ 
/*     */   public ArrayList<FileEntry> getEntries()
/*     */   {
/* 101 */     return this.entries;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     IO.FileSystem.RootFileTable
 * JD-Core Version:    0.6.0
 */