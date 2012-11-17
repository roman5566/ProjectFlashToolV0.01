/*     */ package org.apache.commons.codec.binary;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ public class Base64 extends BaseNCodec
/*     */ {
/*     */   private static final int BITS_PER_ENCODED_BYTE = 6;
/*     */   private static final int BYTES_PER_UNENCODED_BLOCK = 3;
/*     */   private static final int BYTES_PER_ENCODED_BLOCK = 4;
/*  71 */   static final byte[] CHUNK_SEPARATOR = { 13, 10 };
/*     */ 
/*  80 */   private static final byte[] STANDARD_ENCODE_TABLE = { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
/*     */ 
/*  93 */   private static final byte[] URL_SAFE_ENCODE_TABLE = { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95 };
/*     */ 
/* 112 */   private static final byte[] DECODE_TABLE = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51 };
/*     */   private static final int MASK_6BITS = 63;
/*     */   private final byte[] encodeTable;
/* 140 */   private final byte[] decodeTable = DECODE_TABLE;
/*     */   private final byte[] lineSeparator;
/*     */   private final int decodeSize;
/*     */   private final int encodeSize;
/*     */   private int bitWorkArea;
/*     */ 
/*     */   public Base64()
/*     */   {
/* 176 */     this(0);
/*     */   }
/*     */ 
/*     */   public Base64(boolean urlSafe)
/*     */   {
/* 195 */     this(76, CHUNK_SEPARATOR, urlSafe);
/*     */   }
/*     */ 
/*     */   public Base64(int lineLength)
/*     */   {
/* 217 */     this(lineLength, CHUNK_SEPARATOR);
/*     */   }
/*     */ 
/*     */   public Base64(int lineLength, byte[] lineSeparator)
/*     */   {
/* 243 */     this(lineLength, lineSeparator, false);
/*     */   }
/*     */ 
/*     */   public Base64(int lineLength, byte[] lineSeparator, boolean urlSafe)
/*     */   {
/* 272 */     super(3, 4, lineLength, lineSeparator == null ? 0 : lineSeparator.length);
/*     */ 
/* 277 */     if (lineSeparator != null) {
/* 278 */       if (containsAlphabetOrPad(lineSeparator)) {
/* 279 */         String sep = StringUtils.newStringUtf8(lineSeparator);
/* 280 */         throw new IllegalArgumentException("lineSeparator must not contain base64 characters: [" + sep + "]");
/*     */       }
/* 282 */       if (lineLength > 0) {
/* 283 */         this.encodeSize = (4 + lineSeparator.length);
/* 284 */         this.lineSeparator = new byte[lineSeparator.length];
/* 285 */         System.arraycopy(lineSeparator, 0, this.lineSeparator, 0, lineSeparator.length);
/*     */       } else {
/* 287 */         this.encodeSize = 4;
/* 288 */         this.lineSeparator = null;
/*     */       }
/*     */     } else {
/* 291 */       this.encodeSize = 4;
/* 292 */       this.lineSeparator = null;
/*     */     }
/* 294 */     this.decodeSize = (this.encodeSize - 1);
/* 295 */     this.encodeTable = (urlSafe ? URL_SAFE_ENCODE_TABLE : STANDARD_ENCODE_TABLE);
/*     */   }
/*     */ 
/*     */   public boolean isUrlSafe()
/*     */   {
/* 305 */     return this.encodeTable == URL_SAFE_ENCODE_TABLE;
/*     */   }
/*     */ 
/*     */   void encode(byte[] in, int inPos, int inAvail)
/*     */   {
/* 328 */     if (this.eof) {
/* 329 */       return;
/*     */     }
/*     */ 
/* 333 */     if (inAvail < 0) {
/* 334 */       this.eof = true;
/* 335 */       if ((0 == this.modulus) && (this.lineLength == 0)) {
/* 336 */         return;
/*     */       }
/* 338 */       ensureBufferSize(this.encodeSize);
/* 339 */       int savedPos = this.pos;
/* 340 */       switch (this.modulus) {
/*     */       case 1:
/* 342 */         this.buffer[(this.pos++)] = this.encodeTable[(this.bitWorkArea >> 2 & 0x3F)];
/* 343 */         this.buffer[(this.pos++)] = this.encodeTable[(this.bitWorkArea << 4 & 0x3F)];
/*     */ 
/* 345 */         if (this.encodeTable != STANDARD_ENCODE_TABLE) break;
/* 346 */         this.buffer[(this.pos++)] = 61;
/* 347 */         this.buffer[(this.pos++)] = 61; break;
/*     */       case 2:
/* 352 */         this.buffer[(this.pos++)] = this.encodeTable[(this.bitWorkArea >> 10 & 0x3F)];
/* 353 */         this.buffer[(this.pos++)] = this.encodeTable[(this.bitWorkArea >> 4 & 0x3F)];
/* 354 */         this.buffer[(this.pos++)] = this.encodeTable[(this.bitWorkArea << 2 & 0x3F)];
/*     */ 
/* 356 */         if (this.encodeTable != STANDARD_ENCODE_TABLE) break;
/* 357 */         this.buffer[(this.pos++)] = 61;
/*     */       }
/*     */ 
/* 361 */       this.currentLinePos += this.pos - savedPos;
/*     */ 
/* 363 */       if ((this.lineLength > 0) && (this.currentLinePos > 0)) {
/* 364 */         System.arraycopy(this.lineSeparator, 0, this.buffer, this.pos, this.lineSeparator.length);
/* 365 */         this.pos += this.lineSeparator.length;
/*     */       }
/*     */     } else {
/* 368 */       for (int i = 0; i < inAvail; i++) {
/* 369 */         ensureBufferSize(this.encodeSize);
/* 370 */         this.modulus = ((this.modulus + 1) % 3);
/* 371 */         int b = in[(inPos++)];
/* 372 */         if (b < 0) {
/* 373 */           b += 256;
/*     */         }
/* 375 */         this.bitWorkArea = ((this.bitWorkArea << 8) + b);
/* 376 */         if (0 == this.modulus) {
/* 377 */           this.buffer[(this.pos++)] = this.encodeTable[(this.bitWorkArea >> 18 & 0x3F)];
/* 378 */           this.buffer[(this.pos++)] = this.encodeTable[(this.bitWorkArea >> 12 & 0x3F)];
/* 379 */           this.buffer[(this.pos++)] = this.encodeTable[(this.bitWorkArea >> 6 & 0x3F)];
/* 380 */           this.buffer[(this.pos++)] = this.encodeTable[(this.bitWorkArea & 0x3F)];
/* 381 */           this.currentLinePos += 4;
/* 382 */           if ((this.lineLength > 0) && (this.lineLength <= this.currentLinePos)) {
/* 383 */             System.arraycopy(this.lineSeparator, 0, this.buffer, this.pos, this.lineSeparator.length);
/* 384 */             this.pos += this.lineSeparator.length;
/* 385 */             this.currentLinePos = 0;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void decode(byte[] in, int inPos, int inAvail)
/*     */   {
/* 417 */     if (this.eof) {
/* 418 */       return;
/*     */     }
/* 420 */     if (inAvail < 0) {
/* 421 */       this.eof = true;
/*     */     }
/* 423 */     for (int i = 0; i < inAvail; i++) {
/* 424 */       ensureBufferSize(this.decodeSize);
/* 425 */       byte b = in[(inPos++)];
/* 426 */       if (b == 61)
/*     */       {
/* 428 */         this.eof = true;
/* 429 */         break;
/*     */       }
/* 431 */       if ((b >= 0) && (b < DECODE_TABLE.length)) {
/* 432 */         int result = DECODE_TABLE[b];
/* 433 */         if (result >= 0) {
/* 434 */           this.modulus = ((this.modulus + 1) % 4);
/* 435 */           this.bitWorkArea = ((this.bitWorkArea << 6) + result);
/* 436 */           if (this.modulus == 0) {
/* 437 */             this.buffer[(this.pos++)] = (byte)(this.bitWorkArea >> 16 & 0xFF);
/* 438 */             this.buffer[(this.pos++)] = (byte)(this.bitWorkArea >> 8 & 0xFF);
/* 439 */             this.buffer[(this.pos++)] = (byte)(this.bitWorkArea & 0xFF);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 449 */     if ((this.eof) && (this.modulus != 0)) {
/* 450 */       ensureBufferSize(this.decodeSize);
/*     */ 
/* 454 */       switch (this.modulus)
/*     */       {
/*     */       case 2:
/* 458 */         this.bitWorkArea >>= 4;
/* 459 */         this.buffer[(this.pos++)] = (byte)(this.bitWorkArea & 0xFF);
/* 460 */         break;
/*     */       case 3:
/* 462 */         this.bitWorkArea >>= 2;
/* 463 */         this.buffer[(this.pos++)] = (byte)(this.bitWorkArea >> 8 & 0xFF);
/* 464 */         this.buffer[(this.pos++)] = (byte)(this.bitWorkArea & 0xFF);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static boolean isArrayByteBase64(byte[] arrayOctet)
/*     */   {
/* 481 */     return isBase64(arrayOctet);
/*     */   }
/*     */ 
/*     */   public static boolean isBase64(byte octet)
/*     */   {
/* 493 */     return (octet == 61) || ((octet >= 0) && (octet < DECODE_TABLE.length) && (DECODE_TABLE[octet] != -1));
/*     */   }
/*     */ 
/*     */   public static boolean isBase64(String base64)
/*     */   {
/* 507 */     return isBase64(StringUtils.getBytesUtf8(base64));
/*     */   }
/*     */ 
/*     */   public static boolean isBase64(byte[] arrayOctet)
/*     */   {
/* 521 */     for (int i = 0; i < arrayOctet.length; i++) {
/* 522 */       if ((!isBase64(arrayOctet[i])) && (!isWhiteSpace(arrayOctet[i]))) {
/* 523 */         return false;
/*     */       }
/*     */     }
/* 526 */     return true;
/*     */   }
/*     */ 
/*     */   public static byte[] encodeBase64(byte[] binaryData)
/*     */   {
/* 537 */     return encodeBase64(binaryData, false);
/*     */   }
/*     */ 
/*     */   public static String encodeBase64String(byte[] binaryData)
/*     */   {
/* 552 */     return StringUtils.newStringUtf8(encodeBase64(binaryData, false));
/*     */   }
/*     */ 
/*     */   public static byte[] encodeBase64URLSafe(byte[] binaryData)
/*     */   {
/* 565 */     return encodeBase64(binaryData, false, true);
/*     */   }
/*     */ 
/*     */   public static String encodeBase64URLSafeString(byte[] binaryData)
/*     */   {
/* 578 */     return StringUtils.newStringUtf8(encodeBase64(binaryData, false, true));
/*     */   }
/*     */ 
/*     */   public static byte[] encodeBase64Chunked(byte[] binaryData)
/*     */   {
/* 589 */     return encodeBase64(binaryData, true);
/*     */   }
/*     */ 
/*     */   public static byte[] encodeBase64(byte[] binaryData, boolean isChunked)
/*     */   {
/* 604 */     return encodeBase64(binaryData, isChunked, false);
/*     */   }
/*     */ 
/*     */   public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe)
/*     */   {
/* 622 */     return encodeBase64(binaryData, isChunked, urlSafe, 2147483647);
/*     */   }
/*     */ 
/*     */   public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe, int maxResultSize)
/*     */   {
/* 642 */     if ((binaryData == null) || (binaryData.length == 0)) {
/* 643 */       return binaryData;
/*     */     }
/*     */ 
/* 648 */     Base64 b64 = isChunked ? new Base64(urlSafe) : new Base64(0, CHUNK_SEPARATOR, urlSafe);
/* 649 */     long len = b64.getEncodedLength(binaryData);
/* 650 */     if (len > maxResultSize) {
/* 651 */       throw new IllegalArgumentException("Input array too big, the output array would be bigger (" + len + ") than the specified maximum size of " + maxResultSize);
/*     */     }
/*     */ 
/* 657 */     return b64.encode(binaryData);
/*     */   }
/*     */ 
/*     */   public static byte[] decodeBase64(String base64String)
/*     */   {
/* 669 */     return new Base64().decode(base64String);
/*     */   }
/*     */ 
/*     */   public static byte[] decodeBase64(byte[] base64Data)
/*     */   {
/* 680 */     return new Base64().decode(base64Data);
/*     */   }
/*     */ 
/*     */   public static BigInteger decodeInteger(byte[] pArray)
/*     */   {
/* 695 */     return new BigInteger(1, decodeBase64(pArray));
/*     */   }
/*     */ 
/*     */   public static byte[] encodeInteger(BigInteger bigInt)
/*     */   {
/* 709 */     if (bigInt == null) {
/* 710 */       throw new NullPointerException("encodeInteger called with null parameter");
/*     */     }
/* 712 */     return encodeBase64(toIntegerBytes(bigInt), false);
/*     */   }
/*     */ 
/*     */   static byte[] toIntegerBytes(BigInteger bigInt)
/*     */   {
/* 723 */     int bitlen = bigInt.bitLength();
/*     */ 
/* 725 */     bitlen = bitlen + 7 >> 3 << 3;
/* 726 */     byte[] bigBytes = bigInt.toByteArray();
/*     */ 
/* 728 */     if ((bigInt.bitLength() % 8 != 0) && (bigInt.bitLength() / 8 + 1 == bitlen / 8)) {
/* 729 */       return bigBytes;
/*     */     }
/*     */ 
/* 732 */     int startSrc = 0;
/* 733 */     int len = bigBytes.length;
/*     */ 
/* 736 */     if (bigInt.bitLength() % 8 == 0) {
/* 737 */       startSrc = 1;
/* 738 */       len--;
/*     */     }
/* 740 */     int startDst = bitlen / 8 - len;
/* 741 */     byte[] resizedBytes = new byte[bitlen / 8];
/* 742 */     System.arraycopy(bigBytes, startSrc, resizedBytes, startDst, len);
/* 743 */     return resizedBytes;
/*     */   }
/*     */ 
/*     */   protected boolean isInAlphabet(byte octet)
/*     */   {
/* 755 */     return (octet >= 0) && (octet < this.decodeTable.length) && (this.decodeTable[octet] != -1);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.binary.Base64
 * JD-Core Version:    0.6.0
 */