/*     */ package IO;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import org.apache.commons.codec.binary.Hex;
/*     */ import rc4.net.clarenceho.crypto.RC4;
/*     */ 
/*     */ public class Decrypt
/*     */ {
/*     */   public byte[] DecryptSMC(byte[] smcEncryptedData)
/*     */   {
/*  10 */     byte[] array = { 66, 117, 78, 121 };
/*  11 */     byte[] smcDecryptedData = new byte[smcEncryptedData.length];
/*  12 */     for (int i = 0; i < smcEncryptedData.length; i++)
/*     */     {
/*  14 */       byte b = smcEncryptedData[i];
/*  15 */       int num = uByte(b) * 251;
/*  16 */       byte b2 = (byte)(b ^ array[(i & 0x3)] & 0xFF);
/*  17 */       smcDecryptedData[i] = b2;
/*  18 */       byte[] expr_5F_cp_0 = array;
/*  19 */       int expr_5F_cp_1 = i + 1 & 0x3;
/*     */       int tmp89_87 = expr_5F_cp_1;
/*     */       byte[] tmp89_85 = expr_5F_cp_0; tmp89_85[tmp89_87] = (byte)(tmp89_85[tmp89_87] + (byte)num);
/*  21 */       byte[] expr_79_cp_0 = array;
/*  22 */       int expr_79_cp_1 = i + 2 & 0x3;
/*     */       int tmp112_110 = expr_79_cp_1;
/*     */       byte[] tmp112_108 = expr_79_cp_0; tmp112_108[tmp112_110] = (byte)(tmp112_108[tmp112_110] + (byte)(num >> 8));
/*     */     }
/*  25 */     return smcDecryptedData;
/*     */   }
/*     */ 
/*     */   public byte[] DecryptKV(byte[] kvEncryptedData, byte[] cpukeyarr)
/*     */   {
/*  30 */     byte[] tmp = new byte[kvEncryptedData.length - 16];
/*  31 */     byte[] header = new byte[16];
/*  32 */     for (int i = 0; i - 16 < tmp.length; i++)
/*  33 */       if (i < 16)
/*  34 */         header[i] = kvEncryptedData[i];
/*     */       else
/*  36 */         tmp[(i - 16)] = kvEncryptedData[i];
/*  37 */     byte[] key = Misc.hmacSha1(header, cpukeyarr);
/*  38 */     key = Misc.resizeArray(key, 16);
/*  39 */     RC4 rc4 = new RC4(key);
/*  40 */     tmp = rc4.rc4(tmp);
/*  41 */     byte[] kvDecryptedData = new byte[kvEncryptedData.length];
/*  42 */     for (int i = 0; i < 16; i++)
/*  43 */       kvDecryptedData[i] = header[i];
/*  44 */     for (int i = 0; i < tmp.length - 16; i++)
/*  45 */       kvDecryptedData[(i + 16)] = tmp[i];
/*  46 */     return kvDecryptedData;
/*     */   }
/*     */ 
/*     */   public byte[] DecryptCB(byte[] data, byte[] blkey) {
/*  50 */     byte[] header = new byte[16];
/*  51 */     for (int i = 0; i < header.length; i++)
/*  52 */       header[i] = data[(16 + i)];
/*  53 */     byte[] key = Misc.hmacSha1(header, blkey);
/*  54 */     key = Misc.resizeArray(key, 16);
/*  55 */     System.out.println(new String(Hex.encodeHex(key)));
/*  56 */     byte[] BLData = new byte[data.length];
/*  57 */     for (int i = 0; i < 16; i++)
/*     */     {
/*  59 */       BLData[i] = data[i];
/*     */     }
/*  61 */     for (int i = 0; i < 16; i++)
/*     */     {
/*  63 */       BLData[(16 + i)] = key[i];
/*     */     }
/*  65 */     byte[] decrypted = new byte[data.length - 32];
/*  66 */     for (int i = 0; i < decrypted.length; i++)
/*     */     {
/*  68 */       decrypted[i] = data[(32 + i)];
/*     */     }
/*  70 */     RC4 rc4 = new RC4(key);
/*  71 */     decrypted = rc4.rc4(decrypted);
/*  72 */     for (int i = 0; i < data.length - 32; i++)
/*     */     {
/*  74 */       BLData[(32 + i)] = decrypted[i];
/*     */     }
/*  76 */     return BLData;
/*     */   }
/*     */ 
/*     */   public byte[] DecryptCF(byte[] cfEncryptedData, byte[] blkey)
/*     */   {
/*  81 */     byte[] header = new byte[16];
/*  82 */     for (byte i = 0; i < 16; i = (byte)(i + 1))
/*     */     {
/*  84 */       header[i] = cfEncryptedData[(32 + i)];
/*     */     }
/*  86 */     byte[] key = Misc.hmacSha1(header, blkey);
/*  87 */     key = Misc.resizeArray(key, 16);
/*  88 */     byte[] BLData = new byte[cfEncryptedData.length];
/*  89 */     for (int i = 0; i < 32; i++)
/*     */     {
/*  91 */       BLData[i] = cfEncryptedData[i];
/*     */     }
/*  93 */     for (int i = 0; i < key.length; i++)
/*     */     {
/*  95 */       BLData[(32 + i)] = key[i];
/*     */     }
/*  97 */     byte[] decrypted = new byte[cfEncryptedData.length - 48];
/*  98 */     for (int i = 0; i < decrypted.length; i++)
/*     */     {
/* 100 */       decrypted[i] = cfEncryptedData[(48 + i)];
/*     */     }
/* 102 */     RC4 rc4 = new RC4(key);
/* 103 */     decrypted = rc4.rc4(decrypted);
/*     */ 
/* 105 */     for (int i = 0; i < cfEncryptedData.length - 48; i++)
/*     */     {
/* 107 */       BLData[(48 + i)] = decrypted[i];
/*     */     }
/* 109 */     return BLData;
/*     */   }
/*     */ 
/*     */   public byte[] DecryptCB_B(byte[] cbEncryptedData, byte[] cpukey, byte[] cb_a)
/*     */   {
/* 114 */     byte[] header = new byte[32];
/* 115 */     for (int i = 0; i < 16; i++)
/*     */     {
/* 117 */       header[i] = cbEncryptedData[(16 + i)];
/*     */     }
/* 119 */     for (int i = 0; i < 16; i++)
/*     */     {
/* 121 */       header[(16 + i)] = cpukey[i];
/*     */     }
/* 123 */     byte[] cbakey = new byte[16];
/* 124 */     for (int i = 0; i < 16; i++)
/*     */     {
/* 126 */       cbakey[i] = cb_a[(16 + i)];
/*     */     }
/* 128 */     byte[] key = Misc.hmacSha1(header, cbakey);
/* 129 */     key = Misc.resizeArray(key, 16);
/* 130 */     byte[] BLData = new byte[cbEncryptedData.length];
/* 131 */     for (int i = 0; i < 16; i++)
/*     */     {
/* 133 */       BLData[i] = cbEncryptedData[i];
/*     */     }
/* 135 */     for (int i = 0; i < 16; i++)
/*     */     {
/* 137 */       BLData[(16 + i)] = key[i];
/*     */     }
/* 139 */     byte[] decrypted = new byte[cbEncryptedData.length - 32];
/* 140 */     for (int i = 0; i < decrypted.length; i++)
/*     */     {
/* 142 */       decrypted[i] = cbEncryptedData[(32 + i)];
/*     */     }
/* 144 */     RC4 rc4 = new RC4(key);
/* 145 */     decrypted = rc4.rc4(decrypted);
/* 146 */     for (int i = 0; i < cbEncryptedData.length - 32; i++)
/*     */     {
/* 148 */       BLData[(32 + i)] = decrypted[i];
/*     */     }
/* 150 */     return BLData;
/*     */   }
/*     */ 
/*     */   private static int uByte(byte in) {
/* 154 */     if (in < 0)
/* 155 */       return 0xFF & in;
/* 156 */     return in;
/*     */   }
/*     */ 
/*     */   public static int uByte(int in) {
/* 160 */     if (in < 0)
/* 161 */       return 0xFF & in;
/* 162 */     return in;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     IO.Decrypt
 * JD-Core Version:    0.6.0
 */