/*     */ package rc4.net.clarenceho.crypto;
/*     */ 
/*     */ public class RC4
/*     */ {
/*  57 */   private byte[] state = new byte[256];
/*     */   private int x;
/*     */   private int y;
/*     */ 
/*     */   public RC4(String key)
/*     */     throws NullPointerException
/*     */   {
/*  69 */     this(key.getBytes());
/*     */   }
/*     */ 
/*     */   public RC4(byte[] key)
/*     */     throws NullPointerException
/*     */   {
/*  81 */     for (int i = 0; i < 256; i++) {
/*  82 */       this.state[i] = (byte)i;
/*     */     }
/*     */ 
/*  85 */     this.x = 0;
/*  86 */     this.y = 0;
/*     */ 
/*  88 */     int index1 = 0;
/*  89 */     int index2 = 0;
/*     */ 
/*  93 */     if ((key == null) || (key.length == 0)) {
/*  94 */       throw new NullPointerException();
/*     */     }
/*     */ 
/*  97 */     for (int i = 0; i < 256; i++)
/*     */     {
/*  99 */       index2 = (key[index1] & 0xFF) + (this.state[i] & 0xFF) + index2 & 0xFF;
/*     */ 
/* 101 */       byte tmp = this.state[i];
/* 102 */       this.state[i] = this.state[index2];
/* 103 */       this.state[index2] = tmp;
/*     */ 
/* 105 */       index1 = (index1 + 1) % key.length;
/*     */     }
/*     */   }
/*     */ 
/*     */   public byte[] rc4(String data)
/*     */   {
/* 120 */     if (data == null) {
/* 121 */       return null;
/*     */     }
/*     */ 
/* 124 */     byte[] tmp = data.getBytes();
/*     */ 
/* 126 */     rc4(tmp);
/*     */ 
/* 128 */     return tmp;
/*     */   }
/*     */ 
/*     */   public byte[] rc4(byte[] buf)
/*     */   {
/* 145 */     if (buf == null) {
/* 146 */       return null;
/*     */     }
/*     */ 
/* 149 */     byte[] result = new byte[buf.length];
/*     */ 
/* 151 */     for (int i = 0; i < buf.length; i++)
/*     */     {
/* 153 */       this.x = (this.x + 1 & 0xFF);
/* 154 */       this.y = ((this.state[this.x] & 0xFF) + this.y & 0xFF);
/*     */ 
/* 156 */       byte tmp = this.state[this.x];
/* 157 */       this.state[this.x] = this.state[this.y];
/* 158 */       this.state[this.y] = tmp;
/*     */ 
/* 160 */       int xorIndex = (this.state[this.x] & 0xFF) + (this.state[this.y] & 0xFF) & 0xFF;
/* 161 */       result[i] = (byte)(buf[i] ^ this.state[xorIndex]);
/*     */     }
/*     */ 
/* 167 */     return result;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     rc4.net.clarenceho.crypto.RC4
 * JD-Core Version:    0.6.0
 */