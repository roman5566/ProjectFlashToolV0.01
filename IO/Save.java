/*    */ package IO;
/*    */ 
/*    */ import IO.Booloader.BootLoaderInfo;
/*    */ import IO.Booloader.Bootloaders.CB_A;
/*    */ import IO.NandOps.Files.FCRT;
/*    */ import IO.NandOps.Files.KV;
/*    */ import IO.NandOps.Files.SMC;
/*    */ import IO.NandOps.Files.SMC_Config;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class Save
/*    */ {
/*    */   public static void saveKV(KV kv, byte[] CPUKey, String Path)
/*    */     throws IOException
/*    */   {
/* 13 */     FileOutputStream fos = new FileOutputStream(Path + "KV_Dec.bin");
/* 14 */     fos.write(kv.getData());
/* 15 */     fos.close();
/* 16 */     fos = new FileOutputStream(Path + "KV_Enc.bin");
/* 17 */     fos.write(new Encrypt().EncryptKV(kv.getData(), CPUKey));
/* 18 */     fos.close();
/*    */   }
/*    */ 
/*    */   public static void saveSMC(SMC smc, String Path) throws IOException {
/* 22 */     FileOutputStream fos = new FileOutputStream(Path + "SMC_Dec.bin");
/* 23 */     fos.write(smc.getData());
/* 24 */     fos.close();
/* 25 */     fos = new FileOutputStream(Path + "SMC_Enc.bin");
/* 26 */     fos.write(new Encrypt().EncryptSMC(smc.getData()));
/* 27 */     fos.close();
/*    */   }
/*    */ 
/*    */   public static void saveSMCConfig(SMC_Config smc_config, String Path) throws IOException {
/* 31 */     FileOutputStream fos = new FileOutputStream(Path + "SMC_Config.bin");
/* 32 */     fos.write(smc_config.getData());
/* 33 */     fos.close();
/*    */   }
/*    */ 
/*    */   public static void saveFCRT(FCRT fcrt, String Path) throws IOException {
/* 37 */     FileOutputStream fos = new FileOutputStream(Path + "FCRT.bin");
/* 38 */     fos.write(fcrt.getData());
/* 39 */     fos.close();
/*    */   }
/*    */ 
/*    */   public static void saveBootLoaders(BootLoaderInfo BLI, String Path) throws IOException {
/* 43 */     FileOutputStream fos = new FileOutputStream(Path + "CB_A_" + BLI.getcB_A().getVersion() + "_Dec.bin");
/* 44 */     fos.write(BLI.getcB_A().getData());
/* 45 */     fos.close();
/* 46 */     fos = new FileOutputStream(Path + "CB_A_" + BLI.getcB_A().getVersion() + "_Enc.bin");
/* 47 */     fos.write(new Encrypt().EncryptBootLoader(BLI.getcB_A().getData(), Misc.hexStringToByteArray(BLI.getBLKEY())));
/* 48 */     fos.close();
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     IO.Save
 * JD-Core Version:    0.6.0
 */