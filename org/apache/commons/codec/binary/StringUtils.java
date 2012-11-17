/*     */ package org.apache.commons.codec.binary;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ 
/*     */ public class StringUtils
/*     */ {
/*     */   public static byte[] getBytesIso8859_1(String string)
/*     */   {
/*  49 */     return getBytesUnchecked(string, "ISO-8859-1");
/*     */   }
/*     */ 
/*     */   public static byte[] getBytesUsAscii(String string)
/*     */   {
/*  65 */     return getBytesUnchecked(string, "US-ASCII");
/*     */   }
/*     */ 
/*     */   public static byte[] getBytesUtf16(String string)
/*     */   {
/*  81 */     return getBytesUnchecked(string, "UTF-16");
/*     */   }
/*     */ 
/*     */   public static byte[] getBytesUtf16Be(String string)
/*     */   {
/*  97 */     return getBytesUnchecked(string, "UTF-16BE");
/*     */   }
/*     */ 
/*     */   public static byte[] getBytesUtf16Le(String string)
/*     */   {
/* 113 */     return getBytesUnchecked(string, "UTF-16LE");
/*     */   }
/*     */ 
/*     */   public static byte[] getBytesUtf8(String string)
/*     */   {
/* 129 */     return getBytesUnchecked(string, "UTF-8");
/*     */   }
/*     */ 
/*     */   public static byte[] getBytesUnchecked(String string, String charsetName)
/*     */   {
/* 152 */     if (string == null)
/* 153 */       return null;
/*     */     try
/*     */     {
/* 156 */       return string.getBytes(charsetName); } catch (UnsupportedEncodingException e) {
/*     */     }
/* 158 */     throw newIllegalStateException(charsetName, e);
/*     */   }
/*     */ 
/*     */   private static IllegalStateException newIllegalStateException(String charsetName, UnsupportedEncodingException e)
/*     */   {
/* 163 */     return new IllegalStateException(charsetName + ": " + e);
/*     */   }
/*     */ 
/*     */   public static String newString(byte[] bytes, String charsetName)
/*     */   {
/* 186 */     if (bytes == null)
/* 187 */       return null;
/*     */     try
/*     */     {
/* 190 */       return new String(bytes, charsetName); } catch (UnsupportedEncodingException e) {
/*     */     }
/* 192 */     throw newIllegalStateException(charsetName, e);
/*     */   }
/*     */ 
/*     */   public static String newStringIso8859_1(byte[] bytes)
/*     */   {
/* 208 */     return newString(bytes, "ISO-8859-1");
/*     */   }
/*     */ 
/*     */   public static String newStringUsAscii(byte[] bytes)
/*     */   {
/* 223 */     return newString(bytes, "US-ASCII");
/*     */   }
/*     */ 
/*     */   public static String newStringUtf16(byte[] bytes)
/*     */   {
/* 238 */     return newString(bytes, "UTF-16");
/*     */   }
/*     */ 
/*     */   public static String newStringUtf16Be(byte[] bytes)
/*     */   {
/* 253 */     return newString(bytes, "UTF-16BE");
/*     */   }
/*     */ 
/*     */   public static String newStringUtf16Le(byte[] bytes)
/*     */   {
/* 268 */     return newString(bytes, "UTF-16LE");
/*     */   }
/*     */ 
/*     */   public static String newStringUtf8(byte[] bytes)
/*     */   {
/* 283 */     return newString(bytes, "UTF-8");
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.binary.StringUtils
 * JD-Core Version:    0.6.0
 */