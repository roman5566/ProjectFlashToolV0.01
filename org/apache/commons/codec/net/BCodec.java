/*     */ package org.apache.commons.codec.net;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import org.apache.commons.codec.DecoderException;
/*     */ import org.apache.commons.codec.EncoderException;
/*     */ import org.apache.commons.codec.StringDecoder;
/*     */ import org.apache.commons.codec.StringEncoder;
/*     */ import org.apache.commons.codec.binary.Base64;
/*     */ 
/*     */ public class BCodec extends RFC1522Codec
/*     */   implements StringEncoder, StringDecoder
/*     */ {
/*     */   private final String charset;
/*     */ 
/*     */   public BCodec()
/*     */   {
/*  58 */     this("UTF-8");
/*     */   }
/*     */ 
/*     */   public BCodec(String charset)
/*     */   {
/*  71 */     this.charset = charset;
/*     */   }
/*     */ 
/*     */   protected String getEncoding()
/*     */   {
/*  76 */     return "B";
/*     */   }
/*     */ 
/*     */   protected byte[] doEncoding(byte[] bytes)
/*     */   {
/*  81 */     if (bytes == null) {
/*  82 */       return null;
/*     */     }
/*  84 */     return Base64.encodeBase64(bytes);
/*     */   }
/*     */ 
/*     */   protected byte[] doDecoding(byte[] bytes)
/*     */   {
/*  89 */     if (bytes == null) {
/*  90 */       return null;
/*     */     }
/*  92 */     return Base64.decodeBase64(bytes);
/*     */   }
/*     */ 
/*     */   public String encode(String value, String charset)
/*     */     throws EncoderException
/*     */   {
/* 108 */     if (value == null)
/* 109 */       return null;
/*     */     try
/*     */     {
/* 112 */       return encodeText(value, charset); } catch (UnsupportedEncodingException e) {
/*     */     }
/* 114 */     throw new EncoderException(e.getMessage(), e);
/*     */   }
/*     */ 
/*     */   public String encode(String value)
/*     */     throws EncoderException
/*     */   {
/* 129 */     if (value == null) {
/* 130 */       return null;
/*     */     }
/* 132 */     return encode(value, getDefaultCharset());
/*     */   }
/*     */ 
/*     */   public String decode(String value)
/*     */     throws DecoderException
/*     */   {
/* 146 */     if (value == null)
/* 147 */       return null;
/*     */     try
/*     */     {
/* 150 */       return decodeText(value); } catch (UnsupportedEncodingException e) {
/*     */     }
/* 152 */     throw new DecoderException(e.getMessage(), e);
/*     */   }
/*     */ 
/*     */   public Object encode(Object value)
/*     */     throws EncoderException
/*     */   {
/* 167 */     if (value == null)
/* 168 */       return null;
/* 169 */     if ((value instanceof String)) {
/* 170 */       return encode((String)value);
/*     */     }
/* 172 */     throw new EncoderException("Objects of type " + value.getClass().getName() + " cannot be encoded using BCodec");
/*     */   }
/*     */ 
/*     */   public Object decode(Object value)
/*     */     throws DecoderException
/*     */   {
/* 192 */     if (value == null)
/* 193 */       return null;
/* 194 */     if ((value instanceof String)) {
/* 195 */       return decode((String)value);
/*     */     }
/* 197 */     throw new DecoderException("Objects of type " + value.getClass().getName() + " cannot be decoded using BCodec");
/*     */   }
/*     */ 
/*     */   public String getDefaultCharset()
/*     */   {
/* 209 */     return this.charset;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.net.BCodec
 * JD-Core Version:    0.6.0
 */