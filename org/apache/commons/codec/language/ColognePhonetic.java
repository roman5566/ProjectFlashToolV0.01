/*     */ package org.apache.commons.codec.language;
/*     */ 
/*     */ import java.util.Locale;
/*     */ import org.apache.commons.codec.EncoderException;
/*     */ import org.apache.commons.codec.StringEncoder;
/*     */ 
/*     */ public class ColognePhonetic
/*     */   implements StringEncoder
/*     */ {
/* 274 */   private static final char[][] PREPROCESS_MAP = { { 'Ä', 'A' }, { 'Ü', 'U' }, { 'Ö', 'O' }, { 'ß', 'S' } };
/*     */ 
/*     */   private static boolean arrayContains(char[] arr, char key)
/*     */   {
/* 285 */     for (char element : arr) {
/* 286 */       if (element == key) {
/* 287 */         return true;
/*     */       }
/*     */     }
/* 290 */     return false;
/*     */   }
/*     */ 
/*     */   public String colognePhonetic(String text)
/*     */   {
/* 305 */     if (text == null) {
/* 306 */       return null;
/*     */     }
/*     */ 
/* 309 */     text = preprocess(text);
/*     */ 
/* 311 */     CologneOutputBuffer output = new CologneOutputBuffer(text.length() * 2);
/* 312 */     CologneInputBuffer input = new CologneInputBuffer(text.toCharArray());
/*     */ 
/* 316 */     char lastChar = '-';
/* 317 */     char lastCode = '/';
/*     */ 
/* 321 */     int rightLength = input.length();
/*     */ 
/* 323 */     while (rightLength > 0) {
/* 324 */       char chr = input.removeNext();
/*     */       char nextChar;
/*     */       char nextChar;
/* 326 */       if ((rightLength = input.length()) > 0)
/* 327 */         nextChar = input.getNextChar();
/*     */       else
/* 329 */         nextChar = '-';
/*     */       char code;
/*     */       char code;
/* 332 */       if (arrayContains(new char[] { 'A', 'E', 'I', 'J', 'O', 'U', 'Y' }, chr)) {
/* 333 */         code = '0';
/*     */       }
/*     */       else
/*     */       {
/*     */         char code;
/* 334 */         if ((chr == 'H') || (chr < 'A') || (chr > 'Z')) {
/* 335 */           if (lastCode == '/') {
/*     */             continue;
/*     */           }
/* 338 */           code = '-';
/*     */         }
/*     */         else
/*     */         {
/*     */           char code;
/* 339 */           if ((chr == 'B') || ((chr == 'P') && (nextChar != 'H'))) {
/* 340 */             code = '1'; } else {
/* 341 */             if ((chr == 'D') || (chr == 'T')) if (!arrayContains(new char[] { 'S', 'C', 'Z' }, nextChar)) {
/* 342 */                 char code = '2'; break label654;
/*     */               }
/*     */             char code;
/* 343 */             if (arrayContains(new char[] { 'W', 'F', 'P', 'V' }, chr)) {
/* 344 */               code = '3';
/*     */             }
/*     */             else
/*     */             {
/*     */               char code;
/* 345 */               if (arrayContains(new char[] { 'G', 'K', 'Q' }, chr)) {
/* 346 */                 code = '4'; } else {
/* 347 */                 if (chr == 'X') if (!arrayContains(new char[] { 'C', 'K', 'Q' }, lastChar)) {
/* 348 */                     char code = '4';
/* 349 */                     input.addLeft('S');
/* 350 */                     rightLength++; break label654;
/*     */                   }
/*     */                 char code;
/* 351 */                 if ((chr == 'S') || (chr == 'Z')) {
/* 352 */                   code = '8';
/*     */                 }
/*     */                 else
/*     */                 {
/*     */                   char code;
/* 353 */                   if (chr == 'C')
/*     */                   {
/*     */                     char code;
/* 354 */                     if (lastCode == '/')
/*     */                     {
/*     */                       char code;
/* 355 */                       if (arrayContains(new char[] { 'A', 'H', 'K', 'L', 'O', 'Q', 'R', 'U', 'X' }, nextChar))
/* 356 */                         code = '4';
/*     */                       else
/* 358 */                         code = '8';
/*     */                     }
/*     */                     else {
/* 361 */                       if (!arrayContains(new char[] { 'S', 'Z' }, lastChar)) { if (arrayContains(new char[] { 'A', 'H', 'O', 'U', 'K', 'Q', 'X' }, nextChar)); } else {
/* 363 */                         char code = '8'; break label654;
/*     */                       }
/* 365 */                       code = '4';
/*     */                     }
/*     */                   }
/*     */                   else
/*     */                   {
/*     */                     char code;
/* 368 */                     if (arrayContains(new char[] { 'T', 'D', 'X' }, chr)) {
/* 369 */                       code = '8';
/*     */                     }
/*     */                     else
/*     */                     {
/*     */                       char code;
/* 370 */                       if (chr == 'R') {
/* 371 */                         code = '7';
/*     */                       }
/*     */                       else
/*     */                       {
/*     */                         char code;
/* 372 */                         if (chr == 'L') {
/* 373 */                           code = '5';
/*     */                         }
/*     */                         else
/*     */                         {
/*     */                           char code;
/* 374 */                           if ((chr == 'M') || (chr == 'N'))
/* 375 */                             code = '6';
/*     */                           else
/* 377 */                             code = chr; 
/*     */                         }
/*     */                       }
/*     */                     }
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 380 */       label654: if ((code != '-') && (((lastCode != code) && ((code != '0') || (lastCode == '/'))) || (code < '0') || (code > '8'))) {
/* 381 */         output.addRight(code);
/*     */       }
/*     */ 
/* 384 */       lastChar = chr;
/* 385 */       lastCode = code;
/*     */     }
/* 387 */     return output.toString();
/*     */   }
/*     */ 
/*     */   public Object encode(Object object) throws EncoderException {
/* 391 */     if (!(object instanceof String)) {
/* 392 */       throw new EncoderException("This method's parameter was expected to be of the type " + String.class.getName() + ". But actually it was of the type " + object.getClass().getName() + ".");
/*     */     }
/*     */ 
/* 398 */     return encode((String)object);
/*     */   }
/*     */ 
/*     */   public String encode(String text) {
/* 402 */     return colognePhonetic(text);
/*     */   }
/*     */ 
/*     */   public boolean isEncodeEqual(String text1, String text2) {
/* 406 */     return colognePhonetic(text1).equals(colognePhonetic(text2));
/*     */   }
/*     */ 
/*     */   private String preprocess(String text)
/*     */   {
/* 413 */     text = text.toUpperCase(Locale.GERMAN);
/*     */ 
/* 415 */     char[] chrs = text.toCharArray();
/*     */ 
/* 417 */     for (int index = 0; index < chrs.length; index++) {
/* 418 */       if (chrs[index] > 'Z') {
/* 419 */         for (char[] element : PREPROCESS_MAP) {
/* 420 */           if (chrs[index] == element[0]) {
/* 421 */             chrs[index] = element[1];
/* 422 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 427 */     return new String(chrs);
/*     */   }
/*     */ 
/*     */   private class CologneInputBuffer extends ColognePhonetic.CologneBuffer
/*     */   {
/*     */     public CologneInputBuffer(char[] data)
/*     */     {
/* 235 */       super(data);
/*     */     }
/*     */ 
/*     */     public void addLeft(char ch) {
/* 239 */       this.length += 1;
/* 240 */       this.data[getNextPos()] = ch;
/*     */     }
/*     */ 
/*     */     protected char[] copyData(int start, int length)
/*     */     {
/* 245 */       char[] newData = new char[length];
/* 246 */       System.arraycopy(this.data, this.data.length - this.length + start, newData, 0, length);
/* 247 */       return newData;
/*     */     }
/*     */ 
/*     */     public char getNextChar() {
/* 251 */       return this.data[getNextPos()];
/*     */     }
/*     */ 
/*     */     protected int getNextPos() {
/* 255 */       return this.data.length - this.length;
/*     */     }
/*     */ 
/*     */     public char removeNext() {
/* 259 */       char ch = getNextChar();
/* 260 */       this.length -= 1;
/* 261 */       return ch;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class CologneOutputBuffer extends ColognePhonetic.CologneBuffer
/*     */   {
/*     */     public CologneOutputBuffer(int buffSize)
/*     */     {
/* 216 */       super(buffSize);
/*     */     }
/*     */ 
/*     */     public void addRight(char chr) {
/* 220 */       this.data[this.length] = chr;
/* 221 */       this.length += 1;
/*     */     }
/*     */ 
/*     */     protected char[] copyData(int start, int length)
/*     */     {
/* 226 */       char[] newData = new char[length];
/* 227 */       System.arraycopy(this.data, start, newData, 0, length);
/* 228 */       return newData;
/*     */     }
/*     */   }
/*     */ 
/*     */   private abstract class CologneBuffer
/*     */   {
/*     */     protected final char[] data;
/* 189 */     protected int length = 0;
/*     */ 
/*     */     public CologneBuffer(char[] data) {
/* 192 */       this.data = data;
/* 193 */       this.length = data.length;
/*     */     }
/*     */ 
/*     */     public CologneBuffer(int buffSize) {
/* 197 */       this.data = new char[buffSize];
/* 198 */       this.length = 0;
/*     */     }
/*     */     protected abstract char[] copyData(int paramInt1, int paramInt2);
/*     */ 
/*     */     public int length() {
/* 204 */       return this.length;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 209 */       return new String(copyData(0, this.length));
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.language.ColognePhonetic
 * JD-Core Version:    0.6.0
 */