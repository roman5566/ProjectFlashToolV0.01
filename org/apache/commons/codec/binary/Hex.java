/*     */ package org.apache.commons.codec.binary;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import org.apache.commons.codec.BinaryDecoder;
/*     */ import org.apache.commons.codec.BinaryEncoder;
/*     */ import org.apache.commons.codec.DecoderException;
/*     */ import org.apache.commons.codec.EncoderException;
/*     */ 
/*     */ public class Hex
/*     */   implements BinaryEncoder, BinaryDecoder
/*     */ {
/*     */   public static final String DEFAULT_CHARSET_NAME = "UTF-8";
/*  48 */   private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
/*     */ 
/*  53 */   private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
/*     */   private final String charsetName;
/*     */ 
/*     */   public static byte[] decodeHex(char[] data)
/*     */     throws DecoderException
/*     */   {
/*  68 */     int len = data.length;
/*     */ 
/*  70 */     if ((len & 0x1) != 0) {
/*  71 */       throw new DecoderException("Odd number of characters.");
/*     */     }
/*     */ 
/*  74 */     byte[] out = new byte[len >> 1];
/*     */ 
/*  77 */     int i = 0; for (int j = 0; j < len; i++) {
/*  78 */       int f = toDigit(data[j], j) << 4;
/*  79 */       j++;
/*  80 */       f |= toDigit(data[j], j);
/*  81 */       j++;
/*  82 */       out[i] = (byte)(f & 0xFF);
/*     */     }
/*     */ 
/*  85 */     return out;
/*     */   }
/*     */ 
/*     */   public static char[] encodeHex(byte[] data)
/*     */   {
/*  98 */     return encodeHex(data, true);
/*     */   }
/*     */ 
/*     */   public static char[] encodeHex(byte[] data, boolean toLowerCase)
/*     */   {
/* 114 */     return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
/*     */   }
/*     */ 
/*     */   protected static char[] encodeHex(byte[] data, char[] toDigits)
/*     */   {
/* 130 */     int l = data.length;
/* 131 */     char[] out = new char[l << 1];
/*     */ 
/* 133 */     int i = 0; for (int j = 0; i < l; i++) {
/* 134 */       out[(j++)] = toDigits[((0xF0 & data[i]) >>> 4)];
/* 135 */       out[(j++)] = toDigits[(0xF & data[i])];
/*     */     }
/* 137 */     return out;
/*     */   }
/*     */ 
/*     */   public static String encodeHexString(byte[] data)
/*     */   {
/* 150 */     return new String(encodeHex(data));
/*     */   }
/*     */ 
/*     */   protected static int toDigit(char ch, int index)
/*     */     throws DecoderException
/*     */   {
/* 165 */     int digit = Character.digit(ch, 16);
/* 166 */     if (digit == -1) {
/* 167 */       throw new DecoderException("Illegal hexadecimal character " + ch + " at index " + index);
/*     */     }
/* 169 */     return digit;
/*     */   }
/*     */ 
/*     */   public Hex()
/*     */   {
/* 179 */     this.charsetName = "UTF-8";
/*     */   }
/*     */ 
/*     */   public Hex(String csName)
/*     */   {
/* 190 */     this.charsetName = csName;
/*     */   }
/*     */ 
/*     */   public byte[] decode(byte[] array)
/*     */     throws DecoderException
/*     */   {
/*     */     try
/*     */     {
/* 207 */       return decodeHex(new String(array, getCharsetName()).toCharArray()); } catch (UnsupportedEncodingException e) {
/*     */     }
/* 209 */     throw new DecoderException(e.getMessage(), e);
/*     */   }
/*     */ 
/*     */   public Object decode(Object object)
/*     */     throws DecoderException
/*     */   {
/*     */     try
/*     */     {
/* 228 */       char[] charArray = (object instanceof String) ? ((String)object).toCharArray() : (char[])(char[])object;
/* 229 */       return decodeHex(charArray); } catch (ClassCastException e) {
/*     */     }
/* 231 */     throw new DecoderException(e.getMessage(), e);
/*     */   }
/*     */ 
/*     */   public byte[] encode(byte[] array)
/*     */   {
/* 253 */     return StringUtils.getBytesUnchecked(encodeHexString(array), getCharsetName());
/*     */   }
/*     */ 
/*     */   public Object encode(Object object)
/*     */     throws EncoderException
/*     */   {
/*     */     try
/*     */     {
/* 274 */       byte[] byteArray = (object instanceof String) ? ((String)object).getBytes(getCharsetName()) : (byte[])(byte[])object;
/* 275 */       return encodeHex(byteArray);
/*     */     } catch (ClassCastException e) {
/* 277 */       throw new EncoderException(e.getMessage(), e); } catch (UnsupportedEncodingException e) {
/*     */     }
/* 279 */     throw new EncoderException(e.getMessage(), e);
/*     */   }
/*     */ 
/*     */   public String getCharsetName()
/*     */   {
/* 290 */     return this.charsetName;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 300 */     return super.toString() + "[charsetName=" + this.charsetName + "]";
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.binary.Hex
 * JD-Core Version:    0.6.0
 */