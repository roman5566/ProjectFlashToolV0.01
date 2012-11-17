/*     */ package org.apache.commons.codec.net;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.BitSet;
/*     */ import org.apache.commons.codec.DecoderException;
/*     */ import org.apache.commons.codec.EncoderException;
/*     */ import org.apache.commons.codec.StringDecoder;
/*     */ import org.apache.commons.codec.StringEncoder;
/*     */ 
/*     */ public class QCodec extends RFC1522Codec
/*     */   implements StringEncoder, StringDecoder
/*     */ {
/*     */   private final String charset;
/*  58 */   private static final BitSet PRINTABLE_CHARS = new BitSet(256);
/*     */   private static final byte BLANK = 32;
/*     */   private static final byte UNDERSCORE = 95;
/* 107 */   private boolean encodeBlanks = false;
/*     */ 
/*     */   public QCodec()
/*     */   {
/* 113 */     this("UTF-8");
/*     */   }
/*     */ 
/*     */   public QCodec(String charset)
/*     */   {
/* 126 */     this.charset = charset;
/*     */   }
/*     */ 
/*     */   protected String getEncoding()
/*     */   {
/* 131 */     return "Q";
/*     */   }
/*     */ 
/*     */   protected byte[] doEncoding(byte[] bytes)
/*     */   {
/* 136 */     if (bytes == null) {
/* 137 */       return null;
/*     */     }
/* 139 */     byte[] data = QuotedPrintableCodec.encodeQuotedPrintable(PRINTABLE_CHARS, bytes);
/* 140 */     if (this.encodeBlanks) {
/* 141 */       for (int i = 0; i < data.length; i++) {
/* 142 */         if (data[i] == 32) {
/* 143 */           data[i] = 95;
/*     */         }
/*     */       }
/*     */     }
/* 147 */     return data;
/*     */   }
/*     */ 
/*     */   protected byte[] doDecoding(byte[] bytes) throws DecoderException
/*     */   {
/* 152 */     if (bytes == null) {
/* 153 */       return null;
/*     */     }
/* 155 */     boolean hasUnderscores = false;
/* 156 */     for (byte b : bytes) {
/* 157 */       if (b == 95) {
/* 158 */         hasUnderscores = true;
/* 159 */         break;
/*     */       }
/*     */     }
/* 162 */     if (hasUnderscores) {
/* 163 */       byte[] tmp = new byte[bytes.length];
/* 164 */       for (int i = 0; i < bytes.length; i++) {
/* 165 */         byte b = bytes[i];
/* 166 */         if (b != 95)
/* 167 */           tmp[i] = b;
/*     */         else {
/* 169 */           tmp[i] = 32;
/*     */         }
/*     */       }
/* 172 */       return QuotedPrintableCodec.decodeQuotedPrintable(tmp);
/*     */     }
/* 174 */     return QuotedPrintableCodec.decodeQuotedPrintable(bytes);
/*     */   }
/*     */ 
/*     */   public String encode(String pString, String charset)
/*     */     throws EncoderException
/*     */   {
/* 190 */     if (pString == null)
/* 191 */       return null;
/*     */     try
/*     */     {
/* 194 */       return encodeText(pString, charset); } catch (UnsupportedEncodingException e) {
/*     */     }
/* 196 */     throw new EncoderException(e.getMessage(), e);
/*     */   }
/*     */ 
/*     */   public String encode(String pString)
/*     */     throws EncoderException
/*     */   {
/* 211 */     if (pString == null) {
/* 212 */       return null;
/*     */     }
/* 214 */     return encode(pString, getDefaultCharset());
/*     */   }
/*     */ 
/*     */   public String decode(String pString)
/*     */     throws DecoderException
/*     */   {
/* 230 */     if (pString == null)
/* 231 */       return null;
/*     */     try
/*     */     {
/* 234 */       return decodeText(pString); } catch (UnsupportedEncodingException e) {
/*     */     }
/* 236 */     throw new DecoderException(e.getMessage(), e);
/*     */   }
/*     */ 
/*     */   public Object encode(Object pObject)
/*     */     throws EncoderException
/*     */   {
/* 251 */     if (pObject == null)
/* 252 */       return null;
/* 253 */     if ((pObject instanceof String)) {
/* 254 */       return encode((String)pObject);
/*     */     }
/* 256 */     throw new EncoderException("Objects of type " + pObject.getClass().getName() + " cannot be encoded using Q codec");
/*     */   }
/*     */ 
/*     */   public Object decode(Object pObject)
/*     */     throws DecoderException
/*     */   {
/* 276 */     if (pObject == null)
/* 277 */       return null;
/* 278 */     if ((pObject instanceof String)) {
/* 279 */       return decode((String)pObject);
/*     */     }
/* 281 */     throw new DecoderException("Objects of type " + pObject.getClass().getName() + " cannot be decoded using Q codec");
/*     */   }
/*     */ 
/*     */   public String getDefaultCharset()
/*     */   {
/* 293 */     return this.charset;
/*     */   }
/*     */ 
/*     */   public boolean isEncodeBlanks()
/*     */   {
/* 302 */     return this.encodeBlanks;
/*     */   }
/*     */ 
/*     */   public void setEncodeBlanks(boolean b)
/*     */   {
/* 312 */     this.encodeBlanks = b;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  62 */     PRINTABLE_CHARS.set(32);
/*  63 */     PRINTABLE_CHARS.set(33);
/*  64 */     PRINTABLE_CHARS.set(34);
/*  65 */     PRINTABLE_CHARS.set(35);
/*  66 */     PRINTABLE_CHARS.set(36);
/*  67 */     PRINTABLE_CHARS.set(37);
/*  68 */     PRINTABLE_CHARS.set(38);
/*  69 */     PRINTABLE_CHARS.set(39);
/*  70 */     PRINTABLE_CHARS.set(40);
/*  71 */     PRINTABLE_CHARS.set(41);
/*  72 */     PRINTABLE_CHARS.set(42);
/*  73 */     PRINTABLE_CHARS.set(43);
/*  74 */     PRINTABLE_CHARS.set(44);
/*  75 */     PRINTABLE_CHARS.set(45);
/*  76 */     PRINTABLE_CHARS.set(46);
/*  77 */     PRINTABLE_CHARS.set(47);
/*  78 */     for (int i = 48; i <= 57; i++) {
/*  79 */       PRINTABLE_CHARS.set(i);
/*     */     }
/*  81 */     PRINTABLE_CHARS.set(58);
/*  82 */     PRINTABLE_CHARS.set(59);
/*  83 */     PRINTABLE_CHARS.set(60);
/*  84 */     PRINTABLE_CHARS.set(62);
/*  85 */     PRINTABLE_CHARS.set(64);
/*  86 */     for (int i = 65; i <= 90; i++) {
/*  87 */       PRINTABLE_CHARS.set(i);
/*     */     }
/*  89 */     PRINTABLE_CHARS.set(91);
/*  90 */     PRINTABLE_CHARS.set(92);
/*  91 */     PRINTABLE_CHARS.set(93);
/*  92 */     PRINTABLE_CHARS.set(94);
/*  93 */     PRINTABLE_CHARS.set(96);
/*  94 */     for (int i = 97; i <= 122; i++) {
/*  95 */       PRINTABLE_CHARS.set(i);
/*     */     }
/*  97 */     PRINTABLE_CHARS.set(123);
/*  98 */     PRINTABLE_CHARS.set(124);
/*  99 */     PRINTABLE_CHARS.set(125);
/* 100 */     PRINTABLE_CHARS.set(126);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.net.QCodec
 * JD-Core Version:    0.6.0
 */