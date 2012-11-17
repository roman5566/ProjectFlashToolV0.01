/*     */ package IO.NandOps;
/*     */ 
/*     */ import IO.Decrypt;
/*     */ import IO.Misc;
/*     */ import IO.NandOps.Files.FCRT;
/*     */ import IO.NandOps.Files.KV;
/*     */ import IO.NandOps.Files.SMC;
/*     */ import IO.NandOps.Files.SMC_Config;
/*     */ 
/*     */ public class FileExtract
/*     */ {
/*     */   private KV kv;
/*  10 */   private SMC smc = new SMC();
/*  11 */   private SMC_Config conf = new SMC_Config();
/*  12 */   private FCRT crt = new FCRT();
/*     */ 
/*     */   public FileExtract() {
/*     */   }
/*  16 */   public FileExtract(byte[] inData, String CPUKey) { byte[] userData = new NandIO(inData).getUserData();
/*  17 */     Decrypt cr = new Decrypt();
/*  18 */     this.conf.setData(extractConfig(inData));
/*  19 */     this.crt.setData(extractFCRT(userData));
/*  20 */     this.smc.setData(cr.DecryptSMC(extractSMC(userData)));
/*  21 */     this.kv = new KV(cr.DecryptKV(extractKV(userData), Misc.hexStringToByteArray(CPUKey))); }
/*     */ 
/*     */   private byte[] extractSMC(byte[] inFile)
/*     */   {
/*  25 */     int smcStart = Misc.getUInt32(inFile, 124);
/*  26 */     int smcSize = Misc.getUInt32(inFile, 120);
/*  27 */     byte[] smc = new byte[smcSize];
/*  28 */     for (int i = 0; i < smcSize; i++)
/*  29 */       smc[i] = inFile[(i + smcStart)];
/*  30 */     return smc;
/*     */   }
/*     */ 
/*     */   public byte[] extractKV(byte[] inFile) {
/*  34 */     byte[] kv = new byte[16384];
/*  35 */     for (int i = 0; i < 16384; i++)
/*  36 */       kv[i] = inFile[(16384 + i)];
/*  37 */     return kv;
/*     */   }
/*     */ 
/*     */   public byte[] extractConfig(byte[] inFile) {
/*  41 */     NandIO nIO = new NandIO(inFile);
/*  42 */     int pages = nIO.getPages(); int offset = 62783488;
/*  43 */     byte[] config = new byte[1024];
/*  44 */     if (pages == 32768)
/*  45 */       offset = 16236544;
/*  46 */     if (pages != 0)
/*  47 */       for (int i = 0; i < config.length; i++)
/*  48 */         config[i] = nIO.getUserData()[(offset + i)];
/*  49 */     return config;
/*     */   }
/*     */ 
/*     */   public byte[] extractFCRT(byte[] inFile) {
/*  53 */     if (inFile != null)
/*     */     {
/*  55 */       for (int i = 0; i < inFile.length; i++)
/*     */       {
/*  57 */         if (Decrypt.uByte(inFile[i]) != 102)
/*     */           continue;
/*  59 */         if ((Decrypt.uByte(inFile[(i + 1)]) != 99) || 
/*  60 */           (Decrypt.uByte(inFile[(i + 2)]) != 114) || 
/*  61 */           (Decrypt.uByte(inFile[(i + 3)]) != 116) || 
/*  62 */           (Decrypt.uByte(inFile[(i + 4)]) != 46) || 
/*  63 */           (Decrypt.uByte(inFile[(i + 5)]) != 98) || 
/*  64 */           (Decrypt.uByte(inFile[(i + 6)]) != 105) || 
/*  65 */           (Decrypt.uByte(inFile[(i + 7)]) != 110))
/*     */           continue;
/*  67 */         int offset = Misc.getUInt16(inFile, i + 22);
/*  68 */         int length = Misc.getUInt32(inFile, i + 24);
/*  69 */         byte[] fcrt = new byte[length];
/*  70 */         for (int j = 0; j < length; j++)
/*  71 */           fcrt[j] = inFile[(j + offset)];
/*  72 */         return fcrt;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  77 */     return null;
/*     */   }
/*     */ 
/*     */   public KV getKv() {
/*  81 */     return this.kv;
/*     */   }
/*     */ 
/*     */   public void setKv(KV kv) {
/*  85 */     this.kv = kv;
/*     */   }
/*     */ 
/*     */   public SMC getSmc() {
/*  89 */     return this.smc;
/*     */   }
/*     */ 
/*     */   public void setSmc(SMC smc) {
/*  93 */     this.smc = smc;
/*     */   }
/*     */ 
/*     */   public SMC_Config getConf() {
/*  97 */     return this.conf;
/*     */   }
/*     */ 
/*     */   public void setConf(SMC_Config conf) {
/* 101 */     this.conf = conf;
/*     */   }
/*     */ 
/*     */   public FCRT getCrt() {
/* 105 */     return this.crt;
/*     */   }
/*     */ 
/*     */   public void setCrt(FCRT crt) {
/* 109 */     this.crt = crt;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     IO.NandOps.FileExtract
 * JD-Core Version:    0.6.0
 */