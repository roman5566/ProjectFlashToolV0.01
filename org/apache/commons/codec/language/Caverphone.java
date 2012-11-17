/*     */ package org.apache.commons.codec.language;
/*     */ 
/*     */ import org.apache.commons.codec.EncoderException;
/*     */ import org.apache.commons.codec.StringEncoder;
/*     */ 
/*     */ /** @deprecated */
/*     */ public class Caverphone
/*     */   implements StringEncoder
/*     */ {
/*  41 */   private final Caverphone2 encoder = new Caverphone2();
/*     */ 
/*     */   public String caverphone(String source)
/*     */   {
/*  58 */     return this.encoder.encode(source);
/*     */   }
/*     */ 
/*     */   public Object encode(Object pObject)
/*     */     throws EncoderException
/*     */   {
/*  73 */     if (!(pObject instanceof String)) {
/*  74 */       throw new EncoderException("Parameter supplied to Caverphone encode is not of type java.lang.String");
/*     */     }
/*  76 */     return caverphone((String)pObject);
/*     */   }
/*     */ 
/*     */   public String encode(String pString)
/*     */   {
/*  87 */     return caverphone(pString);
/*     */   }
/*     */ 
/*     */   public boolean isCaverphoneEqual(String str1, String str2)
/*     */   {
/* 100 */     return caverphone(str1).equals(caverphone(str2));
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.language.Caverphone
 * JD-Core Version:    0.6.0
 */