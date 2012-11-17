/*     */ package IO.NandOps.Files;
/*     */ 
/*     */ public class KV
/*     */ {
/*     */   private byte[] data;
/*     */   private byte[] consoleID;
/*     */   private byte[] serialNumber;
/*     */   private byte[] DVDKey;
/*     */   private byte[] manfactureDate;
/*     */   private byte[] DVDManfacture;
/*     */   private byte[] consoleRegion;
/*     */ 
/*     */   public KV()
/*     */   {
/*     */   }
/*     */ 
/*     */   public KV(byte[] data)
/*     */   {
/*  10 */     this.data = data;
/*  11 */     getenConsoleID();
/*  12 */     getenSerialNumber();
/*  13 */     getenDVDKey();
/*  14 */     getenManfactureDate();
/*  15 */     getenDVDManifacture();
/*  16 */     getenRegion();
/*     */   }
/*     */ 
/*     */   public byte[] getData()
/*     */   {
/*  21 */     return this.data;
/*     */   }
/*     */ 
/*     */   public void setData(byte[] data)
/*     */   {
/*  26 */     this.data = data;
/*     */   }
/*     */ 
/*     */   public byte[] getenConsoleID()
/*     */   {
/*  31 */     byte[] temp = new byte[5];
/*  32 */     for (int i = 0; i < 5; i++)
/*  33 */       temp[i] = this.data[(2506 - getSize() + i)];
/*  34 */     this.consoleID = temp;
/*  35 */     return temp;
/*     */   }
/*     */ 
/*     */   public byte[] getenSerialNumber()
/*     */   {
/*  40 */     byte[] temp = new byte[16];
/*  41 */     for (int i = 0; i < 16; i++)
/*  42 */       temp[i] = this.data[(176 - getSize() + i)];
/*  43 */     this.serialNumber = temp;
/*  44 */     return temp;
/*     */   }
/*     */ 
/*     */   public byte[] getenDVDKey()
/*     */   {
/*  49 */     byte[] temp = new byte[16];
/*  50 */     for (int i = 0; i < 16; i++)
/*  51 */       temp[i] = this.data[(256 - getSize() + i)];
/*  52 */     this.DVDKey = temp;
/*  53 */     return temp;
/*     */   }
/*     */ 
/*     */   public byte[] getenManfactureDate()
/*     */   {
/*  58 */     byte[] temp = new byte[8];
/*  59 */     for (int i = 0; i < 8; i++)
/*  60 */       temp[i] = this.data[(2532 - getSize() + i)];
/*  61 */     this.manfactureDate = temp;
/*  62 */     return temp;
/*     */   }
/*     */ 
/*     */   public byte[] getenDVDManifacture()
/*     */   {
/*  67 */     byte[] temp = new byte[28];
/*  68 */     for (int i = 0; i < 28; i++)
/*  69 */       temp[i] = this.data[(3218 - getSize() + i)];
/*  70 */     this.DVDManfacture = temp;
/*  71 */     return temp;
/*     */   }
/*     */ 
/*     */   public byte[] getenRegion()
/*     */   {
/*  76 */     byte[] temp = new byte[2];
/*  77 */     for (int i = 0; i < 2; i++)
/*  78 */       temp[i] = this.data[(200 - getSize() + i)];
/*  79 */     this.consoleRegion = temp;
/*  80 */     return temp;
/*     */   }
/*     */ 
/*     */   private int getSize() {
/*  84 */     return 16384 - this.data.length;
/*     */   }
/*     */ 
/*     */   public byte[] getDVDManfacture()
/*     */   {
/*  89 */     return this.DVDManfacture;
/*     */   }
/*     */ 
/*     */   public void setDVDManfacture(byte[] dVDManfacture)
/*     */   {
/*  94 */     this.DVDManfacture = dVDManfacture;
/*     */   }
/*     */ 
/*     */   public byte[] getConsoleRegion()
/*     */   {
/*  99 */     return this.consoleRegion;
/*     */   }
/*     */ 
/*     */   public void setConsoleRegion(byte[] consoleRegion)
/*     */   {
/* 104 */     this.consoleRegion = consoleRegion;
/*     */   }
/*     */ 
/*     */   public void setConsoleID(byte[] consoleID)
/*     */   {
/* 109 */     this.consoleID = consoleID;
/*     */   }
/*     */ 
/*     */   public void setSerialNumber(byte[] serialNumber)
/*     */   {
/* 114 */     this.serialNumber = serialNumber;
/*     */   }
/*     */ 
/*     */   public void setDVDKey(byte[] dVDKey)
/*     */   {
/* 119 */     this.DVDKey = dVDKey;
/*     */   }
/*     */ 
/*     */   public void setManfactureDate(byte[] manfactureDate)
/*     */   {
/* 124 */     this.manfactureDate = manfactureDate;
/*     */   }
/*     */ 
/*     */   public byte[] getConsoleID()
/*     */   {
/* 129 */     return this.consoleID;
/*     */   }
/*     */ 
/*     */   public byte[] getSerialNumber()
/*     */   {
/* 134 */     return this.serialNumber;
/*     */   }
/*     */ 
/*     */   public byte[] getDVDKey()
/*     */   {
/* 139 */     return this.DVDKey;
/*     */   }
/*     */ 
/*     */   public byte[] getManfactureDate()
/*     */   {
/* 144 */     return this.manfactureDate;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     IO.NandOps.Files.KV
 * JD-Core Version:    0.6.0
 */