/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ final class Base64
/*     */ {
/*     */   private static final int BASELENGTH = 255;
/*     */   private static final int LOOKUPLENGTH = 64;
/*     */   private static final int TWENTYFOURBITGROUP = 24;
/*     */   private static final int EIGHTBIT = 8;
/*     */   private static final int SIXTEENBIT = 16;
/*     */   private static final int SIXBIT = 6;
/*     */   private static final int FOURBYTE = 4;
/*     */   private static final int SIGN = -128;
/*     */   private static final byte PAD = 61;
/*  50 */   private static byte[] base64Alphabet = new byte['ÿ'];
/*  51 */   private static byte[] lookUpBase64Alphabet = new byte[64];
/*     */ 
/*     */   static boolean isBase64(String isValidString)
/*     */   {
/*  89 */     return isArrayByteBase64(isValidString.getBytes());
/*     */   }
/*     */ 
/*     */   static boolean isBase64(byte octect)
/*     */   {
/*  95 */     return (octect == 61) || (base64Alphabet[octect] != -1);
/*     */   }
/*     */ 
/*     */   static boolean isArrayByteBase64(byte[] arrayOctect)
/*     */   {
/* 100 */     int length = arrayOctect.length;
/* 101 */     if (length == 0) {
/* 102 */       return true;
/*     */     }
/* 104 */     for (int i = 0; i < length; i++) {
/* 105 */       if (!isBase64(arrayOctect[i])) {
/* 106 */         return false;
/*     */       }
/*     */     }
/* 109 */     return true;
/*     */   }
/*     */ 
/*     */   static byte[] encode(byte[] binaryData)
/*     */   {
/* 120 */     int lengthDataBits = binaryData.length * 8;
/* 121 */     int fewerThan24bits = lengthDataBits % 24;
/* 122 */     int numberTriplets = lengthDataBits / 24;
/* 123 */     byte[] encodedData = null;
/*     */ 
/* 126 */     if (fewerThan24bits != 0)
/*     */     {
/* 128 */       encodedData = new byte[(numberTriplets + 1) * 4];
/*     */     }
/*     */     else
/*     */     {
/* 132 */       encodedData = new byte[numberTriplets * 4];
/*     */     }
/*     */ 
/* 135 */     byte k = 0; byte l = 0; byte b1 = 0; byte b2 = 0; byte b3 = 0;
/*     */ 
/* 137 */     int encodedIndex = 0;
/* 138 */     int dataIndex = 0;
/* 139 */     int i = 0;
/* 140 */     for (i = 0; i < numberTriplets; i++)
/*     */     {
/* 142 */       dataIndex = i * 3;
/* 143 */       b1 = binaryData[dataIndex];
/* 144 */       b2 = binaryData[(dataIndex + 1)];
/* 145 */       b3 = binaryData[(dataIndex + 2)];
/*     */ 
/* 147 */       l = (byte)(b2 & 0xF);
/* 148 */       k = (byte)(b1 & 0x3);
/*     */ 
/* 150 */       encodedIndex = i * 4;
/* 151 */       byte val1 = (b1 & 0xFFFFFF80) == 0 ? (byte)(b1 >> 2) : (byte)(b1 >> 2 ^ 0xC0);
/*     */ 
/* 153 */       byte val2 = (b2 & 0xFFFFFF80) == 0 ? (byte)(b2 >> 4) : (byte)(b2 >> 4 ^ 0xF0);
/* 154 */       byte val3 = (b3 & 0xFFFFFF80) == 0 ? (byte)(b3 >> 6) : (byte)(b3 >> 6 ^ 0xFC);
/*     */ 
/* 156 */       encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
/* 157 */       encodedData[(encodedIndex + 1)] = lookUpBase64Alphabet[(val2 | k << 4)];
/* 158 */       encodedData[(encodedIndex + 2)] = lookUpBase64Alphabet[(l << 2 | val3)];
/* 159 */       encodedData[(encodedIndex + 3)] = lookUpBase64Alphabet[(b3 & 0x3F)];
/*     */     }
/*     */ 
/* 163 */     dataIndex = i * 3;
/* 164 */     encodedIndex = i * 4;
/* 165 */     if (fewerThan24bits == 8) {
/* 166 */       b1 = binaryData[dataIndex];
/* 167 */       k = (byte)(b1 & 0x3);
/* 168 */       byte val1 = (b1 & 0xFFFFFF80) == 0 ? (byte)(b1 >> 2) : (byte)(b1 >> 2 ^ 0xC0);
/* 169 */       encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
/* 170 */       encodedData[(encodedIndex + 1)] = lookUpBase64Alphabet[(k << 4)];
/* 171 */       encodedData[(encodedIndex + 2)] = 61;
/* 172 */       encodedData[(encodedIndex + 3)] = 61;
/* 173 */     } else if (fewerThan24bits == 16) {
/* 174 */       b1 = binaryData[dataIndex];
/* 175 */       b2 = binaryData[(dataIndex + 1)];
/* 176 */       l = (byte)(b2 & 0xF);
/* 177 */       k = (byte)(b1 & 0x3);
/*     */ 
/* 179 */       byte val1 = (b1 & 0xFFFFFF80) == 0 ? (byte)(b1 >> 2) : (byte)(b1 >> 2 ^ 0xC0);
/* 180 */       byte val2 = (b2 & 0xFFFFFF80) == 0 ? (byte)(b2 >> 4) : (byte)(b2 >> 4 ^ 0xF0);
/*     */ 
/* 182 */       encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
/* 183 */       encodedData[(encodedIndex + 1)] = lookUpBase64Alphabet[(val2 | k << 4)];
/* 184 */       encodedData[(encodedIndex + 2)] = lookUpBase64Alphabet[(l << 2)];
/* 185 */       encodedData[(encodedIndex + 3)] = 61;
/*     */     }
/* 187 */     return encodedData;
/*     */   }
/*     */ 
/*     */   static byte[] decode(byte[] base64Data)
/*     */   {
/* 201 */     if (base64Data.length == 0) return new byte[0];
/*     */ 
/* 203 */     int numberQuadruple = base64Data.length / 4;
/* 204 */     byte[] decodedData = null;
/* 205 */     byte b1 = 0; byte b2 = 0; byte b3 = 0; byte b4 = 0; byte marker0 = 0; byte marker1 = 0;
/*     */ 
/* 207 */     int encodedIndex = 0;
/* 208 */     int dataIndex = 0;
/*     */ 
/* 211 */     int lastData = base64Data.length;
/*     */ 
/* 213 */     while (base64Data[(lastData - 1)] == 61) {
/* 214 */       lastData--; if (lastData == 0) return new byte[0];
/*     */     }
/* 216 */     decodedData = new byte[lastData - numberQuadruple];
/*     */ 
/* 219 */     for (int i = 0; i < numberQuadruple; i++) {
/* 220 */       dataIndex = i * 4;
/* 221 */       marker0 = base64Data[(dataIndex + 2)];
/* 222 */       marker1 = base64Data[(dataIndex + 3)];
/*     */ 
/* 224 */       b1 = base64Alphabet[base64Data[dataIndex]];
/* 225 */       b2 = base64Alphabet[base64Data[(dataIndex + 1)]];
/*     */ 
/* 227 */       if ((marker0 != 61) && (marker1 != 61)) {
/* 228 */         b3 = base64Alphabet[marker0];
/* 229 */         b4 = base64Alphabet[marker1];
/*     */ 
/* 231 */         decodedData[encodedIndex] = (byte)(b1 << 2 | b2 >> 4);
/* 232 */         decodedData[(encodedIndex + 1)] = (byte)((b2 & 0xF) << 4 | b3 >> 2 & 0xF);
/* 233 */         decodedData[(encodedIndex + 2)] = (byte)(b3 << 6 | b4);
/* 234 */       } else if (marker0 == 61) {
/* 235 */         decodedData[encodedIndex] = (byte)(b1 << 2 | b2 >> 4);
/* 236 */       } else if (marker1 == 61) {
/* 237 */         b3 = base64Alphabet[marker0];
/*     */ 
/* 239 */         decodedData[encodedIndex] = (byte)(b1 << 2 | b2 >> 4);
/* 240 */         decodedData[(encodedIndex + 1)] = (byte)((b2 & 0xF) << 4 | b3 >> 2 & 0xF);
/*     */       }
/* 242 */       encodedIndex += 3;
/*     */     }
/* 244 */     return decodedData;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  55 */     for (int i = 0; i < 255; i++) {
/*  56 */       base64Alphabet[i] = -1;
/*     */     }
/*  58 */     for (int i = 90; i >= 65; i--) {
/*  59 */       base64Alphabet[i] = (byte)(i - 65);
/*     */     }
/*  61 */     for (int i = 122; i >= 97; i--) {
/*  62 */       base64Alphabet[i] = (byte)(i - 97 + 26);
/*     */     }
/*     */ 
/*  65 */     for (int i = 57; i >= 48; i--) {
/*  66 */       base64Alphabet[i] = (byte)(i - 48 + 52);
/*     */     }
/*     */ 
/*  69 */     base64Alphabet[43] = 62;
/*  70 */     base64Alphabet[47] = 63;
/*     */ 
/*  72 */     for (int i = 0; i <= 25; i++) {
/*  73 */       lookUpBase64Alphabet[i] = (byte)(65 + i);
/*     */     }
/*     */ 
/*  76 */     int i = 26; for (int j = 0; i <= 51; j++) {
/*  77 */       lookUpBase64Alphabet[i] = (byte)(97 + j);
/*     */ 
/*  76 */       i++;
/*     */     }
/*     */ 
/*  80 */     int i = 52; for (int j = 0; i <= 61; j++) {
/*  81 */       lookUpBase64Alphabet[i] = (byte)(48 + j);
/*     */ 
/*  80 */       i++;
/*     */     }
/*     */ 
/*  83 */     lookUpBase64Alphabet[62] = 43;
/*  84 */     lookUpBase64Alphabet[63] = 47;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.Base64
 * JD-Core Version:    0.6.0
 */