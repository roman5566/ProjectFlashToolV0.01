/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.StringWriter;
/*     */ import java.io.Writer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.axis.InternalException;
/*     */ 
/*     */ public class StringUtils
/*     */ {
/*  34 */   public static final String[] EMPTY_STRING_ARRAY = new String[0];
/*     */ 
/*     */   public static boolean startsWithIgnoreWhitespaces(String prefix, String string)
/*     */   {
/*  43 */     int index1 = 0;
/*  44 */     int index2 = 0;
/*  45 */     int length1 = prefix.length();
/*  46 */     int length2 = string.length();
/*  47 */     char ch1 = ' ';
/*  48 */     char ch2 = ' ';
/*  49 */     while ((index1 < length1) && (index2 < length2)) {
/*  50 */       while ((index1 < length1) && (Character.isWhitespace(ch1 = prefix.charAt(index1)))) {
/*  51 */         index1++;
/*     */       }
/*  53 */       while ((index2 < length2) && (Character.isWhitespace(ch2 = string.charAt(index2)))) {
/*  54 */         index2++;
/*     */       }
/*  56 */       if ((index1 == length1) && (index2 == length2)) {
/*  57 */         return true;
/*     */       }
/*  59 */       if (ch1 != ch2) {
/*  60 */         return false;
/*     */       }
/*  62 */       index1++;
/*  63 */       index2++;
/*     */     }
/*     */ 
/*  66 */     return (index1 >= length1) || (index2 < length2);
/*     */   }
/*     */ 
/*     */   public static String[] split(String str, char separatorChar)
/*     */   {
/*  95 */     if (str == null) {
/*  96 */       return null;
/*     */     }
/*  98 */     int len = str.length();
/*  99 */     if (len == 0) {
/* 100 */       return EMPTY_STRING_ARRAY;
/*     */     }
/* 102 */     List list = new ArrayList();
/* 103 */     int i = 0; int start = 0;
/* 104 */     boolean match = false;
/* 105 */     while (i < len) {
/* 106 */       if (str.charAt(i) == separatorChar) {
/* 107 */         if (match) {
/* 108 */           list.add(str.substring(start, i));
/* 109 */           match = false;
/*     */         }
/* 111 */         i++; start = i;
/* 112 */         continue;
/*     */       }
/* 114 */       match = true;
/* 115 */       i++;
/*     */     }
/* 117 */     if (match) {
/* 118 */       list.add(str.substring(start, i));
/*     */     }
/* 120 */     return (String[])list.toArray(new String[list.size()]);
/*     */   }
/*     */ 
/*     */   public static boolean isEmpty(String str)
/*     */   {
/* 144 */     return (str == null) || (str.length() == 0);
/*     */   }
/*     */ 
/*     */   public static String strip(String str)
/*     */   {
/* 172 */     return strip(str, null);
/*     */   }
/*     */ 
/*     */   public static String strip(String str, String stripChars)
/*     */   {
/* 202 */     if (str == null) {
/* 203 */       return str;
/*     */     }
/* 205 */     int len = str.length();
/* 206 */     if (len == 0) {
/* 207 */       return str;
/*     */     }
/* 209 */     int start = getStripStart(str, stripChars);
/* 210 */     if (start == len) {
/* 211 */       return "";
/*     */     }
/* 213 */     int end = getStripEnd(str, stripChars);
/* 214 */     return (start == 0) && (end == len) ? str : str.substring(start, end);
/*     */   }
/*     */ 
/*     */   public static String stripStart(String str, String stripChars)
/*     */   {
/* 242 */     int start = getStripStart(str, stripChars);
/* 243 */     return start <= 0 ? str : str.substring(start);
/*     */   }
/*     */ 
/*     */   private static int getStripStart(String str, String stripChars)
/*     */   {
/*     */     int strLen;
/* 248 */     if ((str == null) || ((strLen = str.length()) == 0))
/* 249 */       return -1;
/*     */     int strLen;
/* 251 */     int start = 0;
/* 252 */     if (stripChars == null) {
/* 253 */       while ((start != strLen) && (Character.isWhitespace(str.charAt(start))))
/* 254 */         start++;
/*     */     }
/* 256 */     if (stripChars.length() == 0) {
/* 257 */       return start;
/*     */     }
/* 259 */     while ((start != strLen) && (stripChars.indexOf(str.charAt(start)) != -1)) {
/* 260 */       start++;
/*     */     }
/*     */ 
/* 263 */     return start;
/*     */   }
/*     */ 
/*     */   public static String stripEnd(String str, String stripChars)
/*     */   {
/* 291 */     int end = getStripEnd(str, stripChars);
/* 292 */     return end < 0 ? str : str.substring(0, end);
/*     */   }
/*     */ 
/*     */   private static int getStripEnd(String str, String stripChars)
/*     */   {
/*     */     int end;
/* 297 */     if ((str == null) || ((end = str.length()) == 0))
/* 298 */       return -1;
/*     */     int end;
/* 300 */     if (stripChars == null) {
/* 301 */       while ((end != 0) && (Character.isWhitespace(str.charAt(end - 1))))
/* 302 */         end--;
/*     */     }
/* 304 */     if (stripChars.length() == 0) {
/* 305 */       return end;
/*     */     }
/* 307 */     while ((end != 0) && (stripChars.indexOf(str.charAt(end - 1)) != -1)) {
/* 308 */       end--;
/*     */     }
/*     */ 
/* 311 */     return end;
/*     */   }
/*     */ 
/*     */   public static String escapeNumericChar(String str)
/*     */   {
/* 321 */     if (str == null)
/* 322 */       return null;
/*     */     try
/*     */     {
/* 325 */       StringWriter writer = new StringWriter(str.length());
/* 326 */       escapeNumericChar(writer, str);
/* 327 */       return writer.toString();
/*     */     }
/*     */     catch (IOException ioe) {
/* 330 */       ioe.printStackTrace();
/* 331 */     }return null;
/*     */   }
/*     */ 
/*     */   public static void escapeNumericChar(Writer out, String str)
/*     */     throws IOException
/*     */   {
/* 343 */     if (str == null) {
/* 344 */       return;
/*     */     }
/* 346 */     int length = str.length();
/*     */ 
/* 348 */     for (int i = 0; i < length; i++) {
/* 349 */       char character = str.charAt(i);
/* 350 */       if (character > '') {
/* 351 */         out.write("&#x");
/* 352 */         out.write(Integer.toHexString(character).toUpperCase());
/* 353 */         out.write(";");
/*     */       } else {
/* 355 */         out.write(character);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String unescapeNumericChar(String str)
/*     */   {
/* 370 */     if (str == null)
/* 371 */       return null;
/*     */     try
/*     */     {
/* 374 */       StringWriter writer = new StringWriter(str.length());
/* 375 */       unescapeNumericChar(writer, str);
/* 376 */       return writer.toString();
/*     */     }
/*     */     catch (IOException ioe) {
/* 379 */       ioe.printStackTrace();
/* 380 */     }return null;
/*     */   }
/*     */ 
/*     */   public static void unescapeNumericChar(Writer out, String str)
/*     */     throws IOException
/*     */   {
/* 399 */     if (out == null) {
/* 400 */       throw new IllegalArgumentException("The Writer must not be null");
/*     */     }
/* 402 */     if (str == null) {
/* 403 */       return;
/*     */     }
/*     */ 
/* 406 */     int sz = str.length();
/* 407 */     StringBuffer unicode = new StringBuffer(4);
/* 408 */     StringBuffer escapes = new StringBuffer(3);
/* 409 */     boolean inUnicode = false;
/*     */ 
/* 411 */     for (int i = 0; i < sz; i++) {
/* 412 */       char ch = str.charAt(i);
/* 413 */       if (inUnicode)
/*     */       {
/* 416 */         unicode.append(ch);
/* 417 */         if (unicode.length() != 4)
/*     */           continue;
/*     */         try
/*     */         {
/* 421 */           int value = Integer.parseInt(unicode.toString(), 16);
/* 422 */           out.write((char)value);
/* 423 */           unicode.setLength(0);
/*     */ 
/* 425 */           i += 1;
/* 426 */           inUnicode = false;
/*     */         } catch (NumberFormatException nfe) {
/* 428 */           throw new InternalException(nfe);
/*     */         }
/*     */ 
/*     */       }
/* 432 */       else if (ch == '&')
/*     */       {
/* 436 */         if (i + 7 <= sz) {
/* 437 */           escapes.append(ch);
/* 438 */           escapes.append(str.charAt(i + 1));
/* 439 */           escapes.append(str.charAt(i + 2));
/* 440 */           if ((escapes.toString().equals("&#x")) && (str.charAt(i + 7) == ';'))
/* 441 */             inUnicode = true;
/*     */           else {
/* 443 */             out.write(escapes.toString());
/*     */           }
/* 445 */           escapes.setLength(0);
/*     */ 
/* 447 */           i += 2;
/*     */         } else {
/* 449 */           out.write(ch);
/*     */         }
/*     */       }
/*     */       else {
/* 453 */         out.write(ch);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.StringUtils
 * JD-Core Version:    0.6.0
 */