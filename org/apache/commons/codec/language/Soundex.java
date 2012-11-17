/*     */ package org.apache.commons.codec.language;
/*     */ 
/*     */ import org.apache.commons.codec.EncoderException;
/*     */ import org.apache.commons.codec.StringEncoder;
/*     */ 
/*     */ public class Soundex
/*     */   implements StringEncoder
/*     */ {
/*     */   public static final String US_ENGLISH_MAPPING_STRING = "01230120022455012623010202";
/*  50 */   private static final char[] US_ENGLISH_MAPPING = "01230120022455012623010202".toCharArray();
/*     */ 
/*  57 */   public static final Soundex US_ENGLISH = new Soundex();
/*     */ 
/*     */   /** @deprecated */
/*  64 */   private int maxLength = 4;
/*     */   private final char[] soundexMapping;
/*     */ 
/*     */   public Soundex()
/*     */   {
/*  79 */     this.soundexMapping = US_ENGLISH_MAPPING;
/*     */   }
/*     */ 
/*     */   public Soundex(char[] mapping)
/*     */   {
/*  93 */     this.soundexMapping = new char[mapping.length];
/*  94 */     System.arraycopy(mapping, 0, this.soundexMapping, 0, mapping.length);
/*     */   }
/*     */ 
/*     */   public Soundex(String mapping)
/*     */   {
/* 106 */     this.soundexMapping = mapping.toCharArray();
/*     */   }
/*     */ 
/*     */   public int difference(String s1, String s2)
/*     */     throws EncoderException
/*     */   {
/* 129 */     return SoundexUtils.difference(this, s1, s2);
/*     */   }
/*     */ 
/*     */   public Object encode(Object pObject)
/*     */     throws EncoderException
/*     */   {
/* 146 */     if (!(pObject instanceof String)) {
/* 147 */       throw new EncoderException("Parameter supplied to Soundex encode is not of type java.lang.String");
/*     */     }
/* 149 */     return soundex((String)pObject);
/*     */   }
/*     */ 
/*     */   public String encode(String pString)
/*     */   {
/* 162 */     return soundex(pString);
/*     */   }
/*     */ 
/*     */   private char getMappingCode(String str, int index)
/*     */   {
/* 180 */     char mappedChar = map(str.charAt(index));
/*     */ 
/* 182 */     if ((index > 1) && (mappedChar != '0')) {
/* 183 */       char hwChar = str.charAt(index - 1);
/* 184 */       if (('H' == hwChar) || ('W' == hwChar)) {
/* 185 */         char preHWChar = str.charAt(index - 2);
/* 186 */         char firstCode = map(preHWChar);
/* 187 */         if ((firstCode == mappedChar) || ('H' == preHWChar) || ('W' == preHWChar)) {
/* 188 */           return '\000';
/*     */         }
/*     */       }
/*     */     }
/* 192 */     return mappedChar;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public int getMaxLength()
/*     */   {
/* 202 */     return this.maxLength;
/*     */   }
/*     */ 
/*     */   private char[] getSoundexMapping()
/*     */   {
/* 211 */     return this.soundexMapping;
/*     */   }
/*     */ 
/*     */   private char map(char ch)
/*     */   {
/* 224 */     int index = ch - 'A';
/* 225 */     if ((index < 0) || (index >= getSoundexMapping().length)) {
/* 226 */       throw new IllegalArgumentException("The character is not mapped: " + ch);
/*     */     }
/* 228 */     return getSoundexMapping()[index];
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void setMaxLength(int maxLength)
/*     */   {
/* 239 */     this.maxLength = maxLength;
/*     */   }
/*     */ 
/*     */   public String soundex(String str)
/*     */   {
/* 252 */     if (str == null) {
/* 253 */       return null;
/*     */     }
/* 255 */     str = SoundexUtils.clean(str);
/* 256 */     if (str.length() == 0) {
/* 257 */       return str;
/*     */     }
/* 259 */     char[] out = { '0', '0', '0', '0' };
/*     */ 
/* 261 */     int incount = 1; int count = 1;
/* 262 */     out[0] = str.charAt(0);
/*     */ 
/* 264 */     char last = getMappingCode(str, 0);
/* 265 */     while ((incount < str.length()) && (count < out.length)) {
/* 266 */       char mapped = getMappingCode(str, incount++);
/* 267 */       if (mapped != 0) {
/* 268 */         if ((mapped != '0') && (mapped != last)) {
/* 269 */           out[(count++)] = mapped;
/*     */         }
/* 271 */         last = mapped;
/*     */       }
/*     */     }
/* 274 */     return new String(out);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.language.Soundex
 * JD-Core Version:    0.6.0
 */