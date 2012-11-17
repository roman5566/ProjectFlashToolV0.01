/*     */ package org.apache.commons.codec.binary;
/*     */ 
/*     */ import org.apache.commons.codec.BinaryDecoder;
/*     */ import org.apache.commons.codec.BinaryEncoder;
/*     */ import org.apache.commons.codec.DecoderException;
/*     */ import org.apache.commons.codec.EncoderException;
/*     */ 
/*     */ public abstract class BaseNCodec
/*     */   implements BinaryEncoder, BinaryDecoder
/*     */ {
/*     */   public static final int MIME_CHUNK_SIZE = 76;
/*     */   public static final int PEM_CHUNK_SIZE = 64;
/*     */   private static final int DEFAULT_BUFFER_RESIZE_FACTOR = 2;
/*     */   private static final int DEFAULT_BUFFER_SIZE = 8192;
/*     */   protected static final int MASK_8BITS = 255;
/*     */   protected static final byte PAD_DEFAULT = 61;
/*  75 */   protected final byte PAD = 61;
/*     */   private final int unencodedBlockSize;
/*     */   private final int encodedBlockSize;
/*     */   protected final int lineLength;
/*     */   private final int chunkSeparatorLength;
/*     */   protected byte[] buffer;
/*     */   protected int pos;
/*     */   private int readPos;
/*     */   protected boolean eof;
/*     */   protected int currentLinePos;
/*     */   protected int modulus;
/*     */ 
/*     */   protected BaseNCodec(int unencodedBlockSize, int encodedBlockSize, int lineLength, int chunkSeparatorLength)
/*     */   {
/* 137 */     this.unencodedBlockSize = unencodedBlockSize;
/* 138 */     this.encodedBlockSize = encodedBlockSize;
/* 139 */     this.lineLength = ((lineLength > 0) && (chunkSeparatorLength > 0) ? lineLength / encodedBlockSize * encodedBlockSize : 0);
/* 140 */     this.chunkSeparatorLength = chunkSeparatorLength;
/*     */   }
/*     */ 
/*     */   boolean hasData()
/*     */   {
/* 149 */     return this.buffer != null;
/*     */   }
/*     */ 
/*     */   int available()
/*     */   {
/* 158 */     return this.buffer != null ? this.pos - this.readPos : 0;
/*     */   }
/*     */ 
/*     */   protected int getDefaultBufferSize()
/*     */   {
/* 167 */     return 8192;
/*     */   }
/*     */ 
/*     */   private void resizeBuffer()
/*     */   {
/* 172 */     if (this.buffer == null) {
/* 173 */       this.buffer = new byte[getDefaultBufferSize()];
/* 174 */       this.pos = 0;
/* 175 */       this.readPos = 0;
/*     */     } else {
/* 177 */       byte[] b = new byte[this.buffer.length * 2];
/* 178 */       System.arraycopy(this.buffer, 0, b, 0, this.buffer.length);
/* 179 */       this.buffer = b;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void ensureBufferSize(int size)
/*     */   {
/* 189 */     if ((this.buffer == null) || (this.buffer.length < this.pos + size))
/* 190 */       resizeBuffer();
/*     */   }
/*     */ 
/*     */   int readResults(byte[] b, int bPos, int bAvail)
/*     */   {
/* 207 */     if (this.buffer != null) {
/* 208 */       int len = Math.min(available(), bAvail);
/* 209 */       System.arraycopy(this.buffer, this.readPos, b, bPos, len);
/* 210 */       this.readPos += len;
/* 211 */       if (this.readPos >= this.pos) {
/* 212 */         this.buffer = null;
/*     */       }
/* 214 */       return len;
/*     */     }
/* 216 */     return this.eof ? -1 : 0;
/*     */   }
/*     */ 
/*     */   protected static boolean isWhiteSpace(byte byteToCheck)
/*     */   {
/* 227 */     switch (byteToCheck) {
/*     */     case 9:
/*     */     case 10:
/*     */     case 13:
/*     */     case 32:
/* 232 */       return true;
/*     */     }
/* 234 */     return false;
/*     */   }
/*     */ 
/*     */   private void reset()
/*     */   {
/* 242 */     this.buffer = null;
/* 243 */     this.pos = 0;
/* 244 */     this.readPos = 0;
/* 245 */     this.currentLinePos = 0;
/* 246 */     this.modulus = 0;
/* 247 */     this.eof = false;
/*     */   }
/*     */ 
/*     */   public Object encode(Object pObject)
/*     */     throws EncoderException
/*     */   {
/* 261 */     if (!(pObject instanceof byte[])) {
/* 262 */       throw new EncoderException("Parameter supplied to Base-N encode is not a byte[]");
/*     */     }
/* 264 */     return encode((byte[])(byte[])pObject);
/*     */   }
/*     */ 
/*     */   public String encodeToString(byte[] pArray)
/*     */   {
/* 275 */     return StringUtils.newStringUtf8(encode(pArray));
/*     */   }
/*     */ 
/*     */   public Object decode(Object pObject)
/*     */     throws DecoderException
/*     */   {
/* 289 */     if ((pObject instanceof byte[]))
/* 290 */       return decode((byte[])(byte[])pObject);
/* 291 */     if ((pObject instanceof String)) {
/* 292 */       return decode((String)pObject);
/*     */     }
/* 294 */     throw new DecoderException("Parameter supplied to Base-N decode is not a byte[] or a String");
/*     */   }
/*     */ 
/*     */   public byte[] decode(String pArray)
/*     */   {
/* 306 */     return decode(StringUtils.getBytesUtf8(pArray));
/*     */   }
/*     */ 
/*     */   public byte[] decode(byte[] pArray)
/*     */   {
/* 317 */     reset();
/* 318 */     if ((pArray == null) || (pArray.length == 0)) {
/* 319 */       return pArray;
/*     */     }
/* 321 */     decode(pArray, 0, pArray.length);
/* 322 */     decode(pArray, 0, -1);
/* 323 */     byte[] result = new byte[this.pos];
/* 324 */     readResults(result, 0, result.length);
/* 325 */     return result;
/*     */   }
/*     */ 
/*     */   public byte[] encode(byte[] pArray)
/*     */   {
/* 336 */     reset();
/* 337 */     if ((pArray == null) || (pArray.length == 0)) {
/* 338 */       return pArray;
/*     */     }
/* 340 */     encode(pArray, 0, pArray.length);
/* 341 */     encode(pArray, 0, -1);
/* 342 */     byte[] buf = new byte[this.pos - this.readPos];
/* 343 */     readResults(buf, 0, buf.length);
/* 344 */     return buf;
/*     */   }
/*     */ 
/*     */   public String encodeAsString(byte[] pArray)
/*     */   {
/* 355 */     return StringUtils.newStringUtf8(encode(pArray));
/*     */   }
/*     */ 
/*     */   abstract void encode(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
/*     */ 
/*     */   abstract void decode(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
/*     */ 
/*     */   protected abstract boolean isInAlphabet(byte paramByte);
/*     */ 
/*     */   public boolean isInAlphabet(byte[] arrayOctet, boolean allowWSPad)
/*     */   {
/* 383 */     for (int i = 0; i < arrayOctet.length; i++) {
/* 384 */       if ((!isInAlphabet(arrayOctet[i])) && ((!allowWSPad) || ((arrayOctet[i] != 61) && (!isWhiteSpace(arrayOctet[i])))))
/*     */       {
/* 386 */         return false;
/*     */       }
/*     */     }
/* 389 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isInAlphabet(String basen)
/*     */   {
/* 402 */     return isInAlphabet(StringUtils.getBytesUtf8(basen), true);
/*     */   }
/*     */ 
/*     */   protected boolean containsAlphabetOrPad(byte[] arrayOctet)
/*     */   {
/* 415 */     if (arrayOctet == null) {
/* 416 */       return false;
/*     */     }
/* 418 */     for (byte element : arrayOctet) {
/* 419 */       if ((61 == element) || (isInAlphabet(element))) {
/* 420 */         return true;
/*     */       }
/*     */     }
/* 423 */     return false;
/*     */   }
/*     */ 
/*     */   public long getEncodedLength(byte[] pArray)
/*     */   {
/* 437 */     long len = (pArray.length + this.unencodedBlockSize - 1) / this.unencodedBlockSize * this.encodedBlockSize;
/* 438 */     if (this.lineLength > 0)
/*     */     {
/* 440 */       len += (len + this.lineLength - 1L) / this.lineLength * this.chunkSeparatorLength;
/*     */     }
/* 442 */     return len;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.binary.BaseNCodec
 * JD-Core Version:    0.6.0
 */