/*     */ package org.apache.axis.encoding;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Writer;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class Base64
/*     */ {
/*  29 */   private static final char[] S_BASE64CHAR = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
/*     */   private static final char S_BASE64PAD = '=';
/*  39 */   private static final byte[] S_DECODETABLE = new byte[''];
/*     */ 
/*     */   private static int decode0(char[] ibuf, byte[] obuf, int wp)
/*     */   {
/*  48 */     int outlen = 3;
/*  49 */     if (ibuf[3] == '=') outlen = 2;
/*  50 */     if (ibuf[2] == '=') outlen = 1;
/*  51 */     int b0 = S_DECODETABLE[ibuf[0]];
/*  52 */     int b1 = S_DECODETABLE[ibuf[1]];
/*  53 */     int b2 = S_DECODETABLE[ibuf[2]];
/*  54 */     int b3 = S_DECODETABLE[ibuf[3]];
/*  55 */     switch (outlen) {
/*     */     case 1:
/*  57 */       obuf[wp] = (byte)(b0 << 2 & 0xFC | b1 >> 4 & 0x3);
/*  58 */       return 1;
/*     */     case 2:
/*  60 */       obuf[(wp++)] = (byte)(b0 << 2 & 0xFC | b1 >> 4 & 0x3);
/*  61 */       obuf[wp] = (byte)(b1 << 4 & 0xF0 | b2 >> 2 & 0xF);
/*  62 */       return 2;
/*     */     case 3:
/*  64 */       obuf[(wp++)] = (byte)(b0 << 2 & 0xFC | b1 >> 4 & 0x3);
/*  65 */       obuf[(wp++)] = (byte)(b1 << 4 & 0xF0 | b2 >> 2 & 0xF);
/*  66 */       obuf[wp] = (byte)(b2 << 6 & 0xC0 | b3 & 0x3F);
/*  67 */       return 3;
/*     */     }
/*  69 */     throw new RuntimeException(Messages.getMessage("internalError00"));
/*     */   }
/*     */ 
/*     */   public static byte[] decode(char[] data, int off, int len)
/*     */   {
/*  77 */     char[] ibuf = new char[4];
/*  78 */     int ibufcount = 0;
/*  79 */     byte[] obuf = new byte[len / 4 * 3 + 3];
/*  80 */     int obufcount = 0;
/*  81 */     for (int i = off; i < off + len; i++) {
/*  82 */       char ch = data[i];
/*  83 */       if ((ch != '=') && ((ch >= S_DECODETABLE.length) || (S_DECODETABLE[ch] == 127)))
/*     */         continue;
/*  85 */       ibuf[(ibufcount++)] = ch;
/*  86 */       if (ibufcount == ibuf.length) {
/*  87 */         ibufcount = 0;
/*  88 */         obufcount += decode0(ibuf, obuf, obufcount);
/*     */       }
/*     */     }
/*     */ 
/*  92 */     if (obufcount == obuf.length)
/*  93 */       return obuf;
/*  94 */     byte[] ret = new byte[obufcount];
/*  95 */     System.arraycopy(obuf, 0, ret, 0, obufcount);
/*  96 */     return ret;
/*     */   }
/*     */ 
/*     */   public static byte[] decode(String data)
/*     */   {
/* 103 */     char[] ibuf = new char[4];
/* 104 */     int ibufcount = 0;
/* 105 */     byte[] obuf = new byte[data.length() / 4 * 3 + 3];
/* 106 */     int obufcount = 0;
/* 107 */     for (int i = 0; i < data.length(); i++) {
/* 108 */       char ch = data.charAt(i);
/* 109 */       if ((ch != '=') && ((ch >= S_DECODETABLE.length) || (S_DECODETABLE[ch] == 127)))
/*     */         continue;
/* 111 */       ibuf[(ibufcount++)] = ch;
/* 112 */       if (ibufcount == ibuf.length) {
/* 113 */         ibufcount = 0;
/* 114 */         obufcount += decode0(ibuf, obuf, obufcount);
/*     */       }
/*     */     }
/*     */ 
/* 118 */     if (obufcount == obuf.length)
/* 119 */       return obuf;
/* 120 */     byte[] ret = new byte[obufcount];
/* 121 */     System.arraycopy(obuf, 0, ret, 0, obufcount);
/* 122 */     return ret;
/*     */   }
/*     */ 
/*     */   public static void decode(char[] data, int off, int len, OutputStream ostream)
/*     */     throws IOException
/*     */   {
/* 129 */     char[] ibuf = new char[4];
/* 130 */     int ibufcount = 0;
/* 131 */     byte[] obuf = new byte[3];
/* 132 */     for (int i = off; i < off + len; i++) {
/* 133 */       char ch = data[i];
/* 134 */       if ((ch != '=') && ((ch >= S_DECODETABLE.length) || (S_DECODETABLE[ch] == 127)))
/*     */         continue;
/* 136 */       ibuf[(ibufcount++)] = ch;
/* 137 */       if (ibufcount == ibuf.length) {
/* 138 */         ibufcount = 0;
/* 139 */         int obufcount = decode0(ibuf, obuf, 0);
/* 140 */         ostream.write(obuf, 0, obufcount);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void decode(String data, OutputStream ostream)
/*     */     throws IOException
/*     */   {
/* 150 */     char[] ibuf = new char[4];
/* 151 */     int ibufcount = 0;
/* 152 */     byte[] obuf = new byte[3];
/* 153 */     for (int i = 0; i < data.length(); i++) {
/* 154 */       char ch = data.charAt(i);
/* 155 */       if ((ch != '=') && ((ch >= S_DECODETABLE.length) || (S_DECODETABLE[ch] == 127)))
/*     */         continue;
/* 157 */       ibuf[(ibufcount++)] = ch;
/* 158 */       if (ibufcount == ibuf.length) {
/* 159 */         ibufcount = 0;
/* 160 */         int obufcount = decode0(ibuf, obuf, 0);
/* 161 */         ostream.write(obuf, 0, obufcount);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String encode(byte[] data)
/*     */   {
/* 171 */     return encode(data, 0, data.length);
/*     */   }
/*     */ 
/*     */   public static String encode(byte[] data, int off, int len)
/*     */   {
/* 178 */     if (len <= 0) return "";
/* 179 */     char[] out = new char[len / 3 * 4 + 4];
/* 180 */     int rindex = off;
/* 181 */     int windex = 0;
/* 182 */     int rest = len - off;
/* 183 */     while (rest >= 3) {
/* 184 */       int i = ((data[rindex] & 0xFF) << 16) + ((data[(rindex + 1)] & 0xFF) << 8) + (data[(rindex + 2)] & 0xFF);
/*     */ 
/* 187 */       out[(windex++)] = S_BASE64CHAR[(i >> 18)];
/* 188 */       out[(windex++)] = S_BASE64CHAR[(i >> 12 & 0x3F)];
/* 189 */       out[(windex++)] = S_BASE64CHAR[(i >> 6 & 0x3F)];
/* 190 */       out[(windex++)] = S_BASE64CHAR[(i & 0x3F)];
/* 191 */       rindex += 3;
/* 192 */       rest -= 3;
/*     */     }
/* 194 */     if (rest == 1) {
/* 195 */       int i = data[rindex] & 0xFF;
/* 196 */       out[(windex++)] = S_BASE64CHAR[(i >> 2)];
/* 197 */       out[(windex++)] = S_BASE64CHAR[(i << 4 & 0x3F)];
/* 198 */       out[(windex++)] = '=';
/* 199 */       out[(windex++)] = '=';
/* 200 */     } else if (rest == 2) {
/* 201 */       int i = ((data[rindex] & 0xFF) << 8) + (data[(rindex + 1)] & 0xFF);
/* 202 */       out[(windex++)] = S_BASE64CHAR[(i >> 10)];
/* 203 */       out[(windex++)] = S_BASE64CHAR[(i >> 4 & 0x3F)];
/* 204 */       out[(windex++)] = S_BASE64CHAR[(i << 2 & 0x3F)];
/* 205 */       out[(windex++)] = '=';
/*     */     }
/* 207 */     return new String(out, 0, windex);
/*     */   }
/*     */ 
/*     */   public static void encode(byte[] data, int off, int len, OutputStream ostream)
/*     */     throws IOException
/*     */   {
/* 214 */     if (len <= 0) return;
/* 215 */     byte[] out = new byte[4];
/* 216 */     int rindex = off;
/* 217 */     int rest = len - off;
/* 218 */     while (rest >= 3) {
/* 219 */       int i = ((data[rindex] & 0xFF) << 16) + ((data[(rindex + 1)] & 0xFF) << 8) + (data[(rindex + 2)] & 0xFF);
/*     */ 
/* 222 */       out[0] = (byte)S_BASE64CHAR[(i >> 18)];
/* 223 */       out[1] = (byte)S_BASE64CHAR[(i >> 12 & 0x3F)];
/* 224 */       out[2] = (byte)S_BASE64CHAR[(i >> 6 & 0x3F)];
/* 225 */       out[3] = (byte)S_BASE64CHAR[(i & 0x3F)];
/* 226 */       ostream.write(out, 0, 4);
/* 227 */       rindex += 3;
/* 228 */       rest -= 3;
/*     */     }
/* 230 */     if (rest == 1) {
/* 231 */       int i = data[rindex] & 0xFF;
/* 232 */       out[0] = (byte)S_BASE64CHAR[(i >> 2)];
/* 233 */       out[1] = (byte)S_BASE64CHAR[(i << 4 & 0x3F)];
/* 234 */       out[2] = 61;
/* 235 */       out[3] = 61;
/* 236 */       ostream.write(out, 0, 4);
/* 237 */     } else if (rest == 2) {
/* 238 */       int i = ((data[rindex] & 0xFF) << 8) + (data[(rindex + 1)] & 0xFF);
/* 239 */       out[0] = (byte)S_BASE64CHAR[(i >> 10)];
/* 240 */       out[1] = (byte)S_BASE64CHAR[(i >> 4 & 0x3F)];
/* 241 */       out[2] = (byte)S_BASE64CHAR[(i << 2 & 0x3F)];
/* 242 */       out[3] = 61;
/* 243 */       ostream.write(out, 0, 4);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void encode(byte[] data, int off, int len, Writer writer)
/*     */     throws IOException
/*     */   {
/* 251 */     if (len <= 0) return;
/* 252 */     char[] out = new char[4];
/* 253 */     int rindex = off;
/* 254 */     int rest = len - off;
/* 255 */     int output = 0;
/* 256 */     while (rest >= 3) {
/* 257 */       int i = ((data[rindex] & 0xFF) << 16) + ((data[(rindex + 1)] & 0xFF) << 8) + (data[(rindex + 2)] & 0xFF);
/*     */ 
/* 260 */       out[0] = S_BASE64CHAR[(i >> 18)];
/* 261 */       out[1] = S_BASE64CHAR[(i >> 12 & 0x3F)];
/* 262 */       out[2] = S_BASE64CHAR[(i >> 6 & 0x3F)];
/* 263 */       out[3] = S_BASE64CHAR[(i & 0x3F)];
/* 264 */       writer.write(out, 0, 4);
/* 265 */       rindex += 3;
/* 266 */       rest -= 3;
/* 267 */       output += 4;
/* 268 */       if (output % 76 == 0)
/* 269 */         writer.write("\n");
/*     */     }
/* 271 */     if (rest == 1) {
/* 272 */       int i = data[rindex] & 0xFF;
/* 273 */       out[0] = S_BASE64CHAR[(i >> 2)];
/* 274 */       out[1] = S_BASE64CHAR[(i << 4 & 0x3F)];
/* 275 */       out[2] = '=';
/* 276 */       out[3] = '=';
/* 277 */       writer.write(out, 0, 4);
/* 278 */     } else if (rest == 2) {
/* 279 */       int i = ((data[rindex] & 0xFF) << 8) + (data[(rindex + 1)] & 0xFF);
/* 280 */       out[0] = S_BASE64CHAR[(i >> 10)];
/* 281 */       out[1] = S_BASE64CHAR[(i >> 4 & 0x3F)];
/* 282 */       out[2] = S_BASE64CHAR[(i << 2 & 0x3F)];
/* 283 */       out[3] = '=';
/* 284 */       writer.write(out, 0, 4);
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  41 */     for (int i = 0; i < S_DECODETABLE.length; i++)
/*  42 */       S_DECODETABLE[i] = 127;
/*  43 */     for (int i = 0; i < S_BASE64CHAR.length; i++)
/*  44 */       S_DECODETABLE[S_BASE64CHAR[i]] = (byte)i;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.Base64
 * JD-Core Version:    0.6.0
 */