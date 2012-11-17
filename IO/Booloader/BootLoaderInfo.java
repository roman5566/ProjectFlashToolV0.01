/*     */ package IO.Booloader;
/*     */ 
/*     */ import IO.Booloader.Bootloaders.CB_A;
/*     */ import IO.Booloader.Bootloaders.CB_B;
/*     */ import IO.Booloader.Bootloaders.CC;
/*     */ import IO.Booloader.Bootloaders.CD;
/*     */ import IO.Booloader.Bootloaders.CE;
/*     */ import IO.Booloader.Bootloaders.CF;
/*     */ import IO.Booloader.Bootloaders.CG;
/*     */ import IO.Decrypt;
/*     */ import IO.Misc;
/*     */ import IO.NandOps.NandIO;
/*     */ 
/*     */ public class BootLoaderInfo
/*     */ {
/*     */   private NandIO nIO;
/*     */   private CB_A cB_A;
/*     */   private CB_B cB_B;
/*     */   private CC cC;
/*     */   private CD cD;
/*     */   private CE cE;
/*     */   private CF cF;
/*     */   private CG cG;
/*  17 */   private String CPUKEY = "";
/*  18 */   private String BLKEY = "DD88AD0C9ED669E7B56794FB68563EFA";
/*     */ 
/*     */   public BootLoaderInfo() {
/*     */   }
/*  22 */   public BootLoaderInfo(byte[] nandFile, String CPUKey) { this.CPUKEY = CPUKey;
/*  23 */     getBootLoaderInfo(nandFile);
/*  24 */     getLDVnPairing(); }
/*     */ 
/*     */   private void getBootLoaderInfo(byte[] nandFile)
/*     */   {
/*  28 */     this.nIO = new NandIO(nandFile);
/*  29 */     byte[] userData = this.nIO.getUserData();
/*  30 */     if (this.nIO.getPages() != 0)
/*     */     {
/*  32 */       int pos = 32768;
/*  33 */       if (userData.length > pos)
/*     */       {
/*  35 */         if ((userData[pos] == 67) || ((userData[pos] == 83) && (userData[(pos + 1)] == 66)) || (userData[(pos + 1)] == 50))
/*     */         {
/*  38 */           this.cB_A = new CB_A();
/*  39 */           this.cB_A.setVersion(Misc.getUInt16(userData, pos + 2));
/*     */ 
/*  41 */           pos += Misc.getUInt32(userData, pos + 12);
/*  42 */           while ((userData[pos] == 67) || (userData[pos] == 83))
/*     */           {
/*  44 */             switch (userData[(pos + 1)])
/*     */             {
/*     */             case 50:
/*     */             case 66:
/*  49 */               this.cB_B = new CB_B();
/*  50 */               this.cB_B.setVersion(Misc.getUInt16(userData, pos + 2));
/*  51 */               break;
/*     */             case 51:
/*     */             case 67:
/*  54 */               this.cC = new CC();
/*  55 */               this.cC.setVersion(Misc.getUInt16(userData, pos + 2));
/*  56 */               break;
/*     */             case 52:
/*     */             case 68:
/*  60 */               this.cD = new CD();
/*  61 */               this.cD.setVersion(Misc.getUInt16(userData, pos + 2));
/*  62 */               break;
/*     */             case 53:
/*     */             case 69:
/*  66 */               this.cE = new CE();
/*  67 */               this.cE.setVersion(Misc.getUInt16(userData, pos + 2));
/*  68 */               break;
/*     */             case 54:
/*     */             case 55:
/*     */             case 56:
/*     */             case 57:
/*     */             case 58:
/*     */             case 59:
/*     */             case 60:
/*     */             case 61:
/*     */             case 62:
/*     */             case 63:
/*     */             case 64:
/*  72 */             case 65: } pos = pos + 
/*  72 */               Misc.getUInt32(userData, pos + 12);
/*     */           }
/*     */ 
/*  75 */           pos = Misc.getUInt32(userData, 100);
/*  76 */           this.cF = new CF();
/*  77 */           this.cF.setOffset0(pos);
/*  78 */           short patchCount = (short)Misc.getUInt16(userData, 104);
/*  79 */           this.cF.setLDV1(patchCount);
/*  80 */           if ((patchCount > 0) && (pos > 0))
/*     */           {
/*  82 */             if ((userData[pos] == 67) && (userData[(pos + 1)] == 70))
/*     */             {
/*  84 */               this.cF.setVersion((short)Misc.getUInt16(userData, pos + 2));
/*  85 */               if (patchCount > 1)
/*     */               {
/*  87 */                 if ((userData[(pos + 65536)] == 67) && (userData[(pos + 65537)] == 70))
/*     */                 {
/*  89 */                   this.cF.setVersion1((short)Misc.getUInt16(userData, pos + 65538));
/*  90 */                   this.cF.setOffset1(this.cF.getOffset0() + 65536);
/*     */                 }
/*  92 */                 else if ((userData[(pos + 131072)] == 67) && (userData[(pos + 131073)] == 70))
/*     */                 {
/*  94 */                   this.cF.setVersion1((short)Misc.getUInt16(userData, pos + 131074));
/*  95 */                   this.cF.setOffset1(this.cF.getOffset0() + 131072);
/*     */                 }
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 101 */             pos += Misc.getUInt32(userData, pos + 12);
/* 102 */             this.cG = new CG();
/* 103 */             if ((userData[pos] == 67) && (userData[(pos + 1)] == 71))
/*     */             {
/* 105 */               this.cG.setVersion(Misc.getUInt16(userData, pos + 2));
/* 106 */               if (patchCount > 1)
/* 107 */                 if ((userData[(pos + 65536)] == 67) && (userData[(pos + 65537)] == 71))
/* 108 */                   this.cG.setVersion1(Misc.getUInt16(userData, pos + 65538));
/* 109 */                 else if ((userData[(pos + 131072)] == 67) && (userData[(pos + 131073)] == 71))
/* 110 */                   this.cG.setVersion1(Misc.getUInt16(userData, pos + 131074));
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void getLDVnPairing()
/*     */   {
/* 121 */     if (this.cB_A.getVersion() != 0) {
/* 122 */       this.cB_A.setData(new Decrypt().DecryptCB(getBootLoader(this.nIO.getUserData(), 32768, 66), Misc.hexStringToByteArray(this.BLKEY)));
/*     */     }
/* 124 */     if ((this.cB_B != null) && 
/* 125 */       (this.cB_B.getVersion() != 0)) {
/* 126 */       this.cB_B.setData(new Decrypt().DecryptCB_B(getBootLoader(this.nIO.getUserData(), 32768 + this.cB_A.getData().length, 66), Misc.hexStringToByteArray(this.CPUKEY), this.cB_A.getData()));
/*     */     }
/* 128 */     if (this.cF != null)
/*     */     {
/* 130 */       if (this.cF.getVersion() > 0)
/* 131 */         this.cF.setData(new Decrypt().DecryptCF(getBootLoader(this.nIO.getUserData(), this.cF.getOffset0(), 70), Misc.hexStringToByteArray(this.BLKEY)));
/* 132 */       if (this.cF.getVersion1() > 0)
/* 133 */         this.cF.setData1(new Decrypt().DecryptCF(getBootLoader(this.nIO.getUserData(), this.cF.getOffset1(), 70), Misc.hexStringToByteArray(this.BLKEY)));
/*     */     }
/*     */   }
/*     */ 
/*     */   public byte[] getBootLoader(byte[] userData, int pos, int secOffset) {
/* 138 */     byte[] data = (byte[])null;
/* 139 */     if (userData.length > pos)
/*     */     {
/* 141 */       int size = Misc.getUInt32(userData, pos + 12);
/* 142 */       data = new byte[size];
/* 143 */       if (userData.length > pos + size)
/*     */       {
/* 145 */         if (((userData[pos] == 67) || (userData[pos] == 83)) && (userData[(pos + 1)] == secOffset))
/*     */         {
/* 147 */           for (int i = 0; i < size; i++)
/*     */           {
/* 149 */             data[i] = userData[(pos + i)];
/* 150 */             if (data[i] < 0)
/* 151 */               data[i] = (byte)(data[i] & 0xFF);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 156 */     return data;
/*     */   }
/*     */ 
/*     */   public CB_A getcB_A() {
/* 160 */     return this.cB_A;
/*     */   }
/*     */ 
/*     */   public void setcB_A(CB_A cB_A) {
/* 164 */     this.cB_A = cB_A;
/*     */   }
/*     */ 
/*     */   public CB_B getcB_B() {
/* 168 */     return this.cB_B;
/*     */   }
/*     */ 
/*     */   public void setcB_B(CB_B cB_B) {
/* 172 */     this.cB_B = cB_B;
/*     */   }
/*     */ 
/*     */   public CD getcD() {
/* 176 */     return this.cD;
/*     */   }
/*     */ 
/*     */   public void setcD(CD cD) {
/* 180 */     this.cD = cD;
/*     */   }
/*     */ 
/*     */   public CE getcE() {
/* 184 */     return this.cE;
/*     */   }
/*     */ 
/*     */   public void setcE(CE cE) {
/* 188 */     this.cE = cE;
/*     */   }
/*     */ 
/*     */   public CF getcF() {
/* 192 */     return this.cF;
/*     */   }
/*     */ 
/*     */   public void setcF(CF cF) {
/* 196 */     this.cF = cF;
/*     */   }
/*     */ 
/*     */   public CG getcG() {
/* 200 */     return this.cG;
/*     */   }
/*     */ 
/*     */   public void setcG(CG cG) {
/* 204 */     this.cG = cG;
/*     */   }
/*     */ 
/*     */   public CC getcC() {
/* 208 */     return this.cC;
/*     */   }
/*     */ 
/*     */   public void setcC(CC cC) {
/* 212 */     this.cC = cC;
/*     */   }
/*     */ 
/*     */   public String getCPUKEY() {
/* 216 */     return this.CPUKEY;
/*     */   }
/*     */ 
/*     */   public void setCPUKEY(String cPUKEY) {
/* 220 */     this.CPUKEY = cPUKEY;
/*     */   }
/*     */ 
/*     */   public String getBLKEY() {
/* 224 */     return this.BLKEY;
/*     */   }
/*     */ 
/*     */   public void setBLKEY(String bLKEY) {
/* 228 */     this.BLKEY = bLKEY;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     IO.Booloader.BootLoaderInfo
 * JD-Core Version:    0.6.0
 */