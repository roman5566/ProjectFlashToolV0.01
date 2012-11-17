/*     */ package org.apache.commons.codec.net;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.BitSet;
/*     */ import org.apache.commons.codec.BinaryDecoder;
/*     */ import org.apache.commons.codec.BinaryEncoder;
/*     */ import org.apache.commons.codec.DecoderException;
/*     */ import org.apache.commons.codec.EncoderException;
/*     */ import org.apache.commons.codec.StringDecoder;
/*     */ import org.apache.commons.codec.StringEncoder;
/*     */ import org.apache.commons.codec.binary.StringUtils;
/*     */ 
/*     */ public class URLCodec
/*     */   implements BinaryEncoder, BinaryDecoder, StringEncoder, StringDecoder
/*     */ {
/*     */   static final int RADIX = 16;
/*     */   protected String charset;
/*     */   protected static final byte ESCAPE_CHAR = 37;
/*  74 */   protected static final BitSet WWW_FORM_URL = new BitSet(256);
/*     */ 
/*     */   public URLCodec()
/*     */   {
/* 103 */     this("UTF-8");
/*     */   }
/*     */ 
/*     */   public URLCodec(String charset)
/*     */   {
/* 113 */     this.charset = charset;
/*     */   }
/*     */ 
/*     */   public static final byte[] encodeUrl(BitSet urlsafe, byte[] bytes)
/*     */   {
/* 126 */     if (bytes == null) {
/* 127 */       return null;
/*     */     }
/* 129 */     if (urlsafe == null) {
/* 130 */       urlsafe = WWW_FORM_URL;
/*     */     }
/*     */ 
/* 133 */     ByteArrayOutputStream buffer = new ByteArrayOutputStream();
/* 134 */     for (byte c : bytes) {
/* 135 */       int b = c;
/* 136 */       if (b < 0) {
/* 137 */         b = 256 + b;
/*     */       }
/* 139 */       if (urlsafe.get(b)) {
/* 140 */         if (b == 32) {
/* 141 */           b = 43;
/*     */         }
/* 143 */         buffer.write(b);
/*     */       } else {
/* 145 */         buffer.write(37);
/* 146 */         char hex1 = Character.toUpperCase(Character.forDigit(b >> 4 & 0xF, 16));
/* 147 */         char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
/* 148 */         buffer.write(hex1);
/* 149 */         buffer.write(hex2);
/*     */       }
/*     */     }
/* 152 */     return buffer.toByteArray();
/*     */   }
/*     */ 
/*     */   public static final byte[] decodeUrl(byte[] bytes)
/*     */     throws DecoderException
/*     */   {
/* 165 */     if (bytes == null) {
/* 166 */       return null;
/*     */     }
/* 168 */     ByteArrayOutputStream buffer = new ByteArrayOutputStream();
/* 169 */     for (int i = 0; i < bytes.length; i++) {
/* 170 */       int b = bytes[i];
/* 171 */       if (b == 43)
/* 172 */         buffer.write(32);
/* 173 */       else if (b == 37)
/*     */         try {
/* 175 */           i++; int u = Utils.digit16(bytes[i]);
/* 176 */           i++; int l = Utils.digit16(bytes[i]);
/* 177 */           buffer.write((char)((u << 4) + l));
/*     */         } catch (ArrayIndexOutOfBoundsException e) {
/* 179 */           throw new DecoderException("Invalid URL encoding: ", e);
/*     */         }
/*     */       else {
/* 182 */         buffer.write(b);
/*     */       }
/*     */     }
/* 185 */     return buffer.toByteArray();
/*     */   }
/*     */ 
/*     */   public byte[] encode(byte[] bytes)
/*     */   {
/* 196 */     return encodeUrl(WWW_FORM_URL, bytes);
/*     */   }
/*     */ 
/*     */   public byte[] decode(byte[] bytes)
/*     */     throws DecoderException
/*     */   {
/* 210 */     return decodeUrl(bytes);
/*     */   }
/*     */ 
/*     */   public String encode(String pString, String charset)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 225 */     if (pString == null) {
/* 226 */       return null;
/*     */     }
/* 228 */     return StringUtils.newStringUsAscii(encode(pString.getBytes(charset)));
/*     */   }
/*     */ 
/*     */   public String encode(String pString)
/*     */     throws EncoderException
/*     */   {
/* 242 */     if (pString == null)
/* 243 */       return null;
/*     */     try
/*     */     {
/* 246 */       return encode(pString, getDefaultCharset()); } catch (UnsupportedEncodingException e) {
/*     */     }
/* 248 */     throw new EncoderException(e.getMessage(), e);
/*     */   }
/*     */ 
/*     */   public String decode(String pString, String charset)
/*     */     throws DecoderException, UnsupportedEncodingException
/*     */   {
/* 266 */     if (pString == null) {
/* 267 */       return null;
/*     */     }
/* 269 */     return new String(decode(StringUtils.getBytesUsAscii(pString)), charset);
/*     */   }
/*     */ 
/*     */   public String decode(String pString)
/*     */     throws DecoderException
/*     */   {
/* 284 */     if (pString == null)
/* 285 */       return null;
/*     */     try
/*     */     {
/* 288 */       return decode(pString, getDefaultCharset()); } catch (UnsupportedEncodingException e) {
/*     */     }
/* 290 */     throw new DecoderException(e.getMessage(), e);
/*     */   }
/*     */ 
/*     */   public Object encode(Object pObject)
/*     */     throws EncoderException
/*     */   {
/* 305 */     if (pObject == null)
/* 306 */       return null;
/* 307 */     if ((pObject instanceof byte[]))
/* 308 */       return encode((byte[])(byte[])pObject);
/* 309 */     if ((pObject instanceof String)) {
/* 310 */       return encode((String)pObject);
/*     */     }
/* 312 */     throw new EncoderException("Objects of type " + pObject.getClass().getName() + " cannot be URL encoded");
/*     */   }
/*     */ 
/*     */   public Object decode(Object pObject)
/*     */     throws DecoderException
/*     */   {
/* 330 */     if (pObject == null)
/* 331 */       return null;
/* 332 */     if ((pObject instanceof byte[]))
/* 333 */       return decode((byte[])(byte[])pObject);
/* 334 */     if ((pObject instanceof String)) {
/* 335 */       return decode((String)pObject);
/*     */     }
/* 337 */     throw new DecoderException("Objects of type " + pObject.getClass().getName() + " cannot be URL decoded");
/*     */   }
/*     */ 
/*     */   public String getDefaultCharset()
/*     */   {
/* 348 */     return this.charset;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public String getEncoding()
/*     */   {
/* 359 */     return this.charset;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  79 */     for (int i = 97; i <= 122; i++) {
/*  80 */       WWW_FORM_URL.set(i);
/*     */     }
/*  82 */     for (int i = 65; i <= 90; i++) {
/*  83 */       WWW_FORM_URL.set(i);
/*     */     }
/*     */ 
/*  86 */     for (int i = 48; i <= 57; i++) {
/*  87 */       WWW_FORM_URL.set(i);
/*     */     }
/*     */ 
/*  90 */     WWW_FORM_URL.set(45);
/*  91 */     WWW_FORM_URL.set(95);
/*  92 */     WWW_FORM_URL.set(46);
/*  93 */     WWW_FORM_URL.set(42);
/*     */ 
/*  95 */     WWW_FORM_URL.set(32);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.net.URLCodec
 * JD-Core Version:    0.6.0
 */