/*    */ package IO;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import org.apache.commons.codec.binary.Hex;
/*    */ import rc4.net.clarenceho.crypto.RC4;
/*    */ 
/*    */ public class Encrypt
/*    */ {
/*    */   public byte[] EncryptSMC(byte[] SMC)
/*    */   {
/* 10 */     byte[] EncryptedSMC = new byte[12288];
/*    */ 
/* 12 */     byte[] key = { 66, 117, 78, 121 };
/* 13 */     for (int i = 0; i < 12288; i++)
/*    */     {
/* 15 */       int byteChar = SMC[i] ^ key[(i & 0x3)] & 0xFF;
/* 16 */       int mod = byteChar * 251;
/* 17 */       EncryptedSMC[i] = (byte)byteChar;
/*    */       int tmp75_74 = (i + 1 & 0x3);
/*    */       byte[] tmp75_68 = key; tmp75_68[tmp75_74] = (byte)(tmp75_68[tmp75_74] + (byte)mod);
/*    */       int tmp90_89 = (i + 2 & 0x3);
/*    */       byte[] tmp90_83 = key; tmp90_83[tmp90_89] = (byte)(tmp90_83[tmp90_89] + ((byte)mod >> 8));
/*    */     }
/* 21 */     return EncryptedSMC;
/*    */   }
/*    */ 
/*    */   public byte[] EncryptKV(byte[] kvDecryptedData, byte[] cpukeyarr) {
/* 25 */     byte[] tmp = new byte[kvDecryptedData.length - 16];
/* 26 */     byte[] header = new byte[16];
/* 27 */     for (int i = 0; i - 16 < tmp.length; i++)
/* 28 */       if (i < 16)
/* 29 */         header[i] = kvDecryptedData[i];
/*    */       else
/* 31 */         tmp[(i - 16)] = kvDecryptedData[i];
/* 32 */     byte[] key = Misc.hmacSha1(header, cpukeyarr);
/* 33 */     key = Misc.resizeArray(key, 16);
/* 34 */     RC4 rc4 = new RC4(key);
/* 35 */     tmp = rc4.rc4(tmp);
/* 36 */     byte[] kvEncryptedData = new byte[kvDecryptedData.length];
/* 37 */     for (int i = 0; i < 16; i++)
/* 38 */       kvEncryptedData[i] = header[i];
/* 39 */     for (int i = 0; i < tmp.length - 16; i++)
/* 40 */       kvEncryptedData[(i + 16)] = tmp[i];
/* 41 */     return kvEncryptedData;
/*    */   }
/*    */ 
/*    */   public byte[] EncryptBootLoader(byte[] CbBootloader, byte[] BLKey) {
/* 45 */     byte[] key = Misc.hmacSha1(new byte[16], BLKey);
/*    */ 
/* 47 */     key = Misc.resizeArray(key, 16);
/*    */ 
/* 49 */     System.out.println(new String(Hex.encodeHex(key)));
/*    */ 
/* 51 */     byte[] newBootloader = new byte[CbBootloader.length];
/*    */ 
/* 53 */     for (int i = 0; i < 16; i++) {
/* 54 */       newBootloader[i] = CbBootloader[i];
/*    */     }
/* 56 */     for (int i = 0; i < 16; i++) {
/* 57 */       newBootloader[(16 + i)] = 0;
/*    */     }
/* 59 */     byte[] encryptedBootloader = new byte[CbBootloader.length - 32];
/*    */ 
/* 61 */     for (int i = 0; i < encryptedBootloader.length; i++) {
/* 62 */       encryptedBootloader[i] = CbBootloader[(32 + i)];
/*    */     }
/* 64 */     RC4 rc4 = new RC4(key);
/*    */ 
/* 66 */     encryptedBootloader = rc4.rc4(encryptedBootloader);
/*    */ 
/* 68 */     for (int i = 0; i < CbBootloader.length - 32; i++) {
/* 69 */       newBootloader[(32 + i)] = encryptedBootloader[i];
/*    */     }
/* 71 */     return newBootloader;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     IO.Encrypt
 * JD-Core Version:    0.6.0
 */