/*     */ package org.apache.axis.types;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.Serializable;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class HexBinary
/*     */   implements Serializable
/*     */ {
/*  29 */   byte[] m_value = null;
/*     */ 
/*  62 */   public static final String ERROR_ODD_NUMBER_OF_DIGITS = Messages.getMessage("oddDigits00");
/*     */ 
/*  64 */   public static final String ERROR_BAD_CHARACTER_IN_HEX_STRING = Messages.getMessage("badChars01");
/*     */ 
/*  70 */   public static final int[] DEC = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
/*     */ 
/*     */   public HexBinary()
/*     */   {
/*     */   }
/*     */ 
/*     */   public HexBinary(String string)
/*     */   {
/*  35 */     this.m_value = decode(string);
/*     */   }
/*     */ 
/*     */   public HexBinary(byte[] bytes) {
/*  39 */     this.m_value = bytes;
/*     */   }
/*     */ 
/*     */   public byte[] getBytes() {
/*  43 */     return this.m_value;
/*     */   }
/*     */ 
/*     */   public String toString() {
/*  47 */     return encode(this.m_value);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  52 */     return super.hashCode();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object object)
/*     */   {
/*  57 */     String s1 = object.toString();
/*  58 */     String s2 = toString();
/*  59 */     return s1.equals(s2);
/*     */   }
/*     */ 
/*     */   public static byte[] decode(String digits)
/*     */   {
/* 101 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 102 */     for (int i = 0; i < digits.length(); i += 2) {
/* 103 */       char c1 = digits.charAt(i);
/* 104 */       if (i + 1 >= digits.length()) {
/* 105 */         throw new IllegalArgumentException(ERROR_ODD_NUMBER_OF_DIGITS);
/*     */       }
/* 107 */       char c2 = digits.charAt(i + 1);
/* 108 */       byte b = 0;
/* 109 */       if ((c1 >= '0') && (c1 <= '9'))
/* 110 */         b = (byte)(b + (c1 - '0') * 16);
/* 111 */       else if ((c1 >= 'a') && (c1 <= 'f'))
/* 112 */         b = (byte)(b + (c1 - 'a' + 10) * 16);
/* 113 */       else if ((c1 >= 'A') && (c1 <= 'F'))
/* 114 */         b = (byte)(b + (c1 - 'A' + 10) * 16);
/*     */       else {
/* 116 */         throw new IllegalArgumentException(ERROR_BAD_CHARACTER_IN_HEX_STRING);
/*     */       }
/* 118 */       if ((c2 >= '0') && (c2 <= '9'))
/* 119 */         b = (byte)(b + (c2 - '0'));
/* 120 */       else if ((c2 >= 'a') && (c2 <= 'f'))
/* 121 */         b = (byte)(b + (c2 - 'a' + 10));
/* 122 */       else if ((c2 >= 'A') && (c2 <= 'F'))
/* 123 */         b = (byte)(b + (c2 - 'A' + 10));
/*     */       else {
/* 125 */         throw new IllegalArgumentException(ERROR_BAD_CHARACTER_IN_HEX_STRING);
/*     */       }
/* 127 */       baos.write(b);
/*     */     }
/* 129 */     return baos.toByteArray();
/*     */   }
/*     */ 
/*     */   public static String encode(byte[] bytes)
/*     */   {
/* 142 */     StringBuffer sb = new StringBuffer(bytes.length * 2);
/* 143 */     for (int i = 0; i < bytes.length; i++) {
/* 144 */       sb.append(convertDigit(bytes[i] >> 4));
/* 145 */       sb.append(convertDigit(bytes[i] & 0xF));
/*     */     }
/* 147 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public static int convert2Int(byte[] hex)
/*     */   {
/* 166 */     if (hex.length < 4) return 0;
/* 167 */     if (DEC[hex[0]] < 0)
/* 168 */       throw new IllegalArgumentException(ERROR_BAD_CHARACTER_IN_HEX_STRING);
/* 169 */     int len = DEC[hex[0]];
/* 170 */     len <<= 4;
/* 171 */     if (DEC[hex[1]] < 0)
/* 172 */       throw new IllegalArgumentException(ERROR_BAD_CHARACTER_IN_HEX_STRING);
/* 173 */     len += DEC[hex[1]];
/* 174 */     len <<= 4;
/* 175 */     if (DEC[hex[2]] < 0)
/* 176 */       throw new IllegalArgumentException(ERROR_BAD_CHARACTER_IN_HEX_STRING);
/* 177 */     len += DEC[hex[2]];
/* 178 */     len <<= 4;
/* 179 */     if (DEC[hex[3]] < 0)
/* 180 */       throw new IllegalArgumentException(ERROR_BAD_CHARACTER_IN_HEX_STRING);
/* 181 */     len += DEC[hex[3]];
/* 182 */     return len;
/*     */   }
/*     */ 
/*     */   private static char convertDigit(int value)
/*     */   {
/* 193 */     value &= 15;
/* 194 */     if (value >= 10) {
/* 195 */       return (char)(value - 10 + 97);
/*     */     }
/* 197 */     return (char)(value + 48);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.HexBinary
 * JD-Core Version:    0.6.0
 */