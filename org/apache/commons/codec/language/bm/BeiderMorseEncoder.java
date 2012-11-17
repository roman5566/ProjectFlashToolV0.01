/*     */ package org.apache.commons.codec.language.bm;
/*     */ 
/*     */ import org.apache.commons.codec.EncoderException;
/*     */ import org.apache.commons.codec.StringEncoder;
/*     */ 
/*     */ public class BeiderMorseEncoder
/*     */   implements StringEncoder
/*     */ {
/*  85 */   private PhoneticEngine engine = new PhoneticEngine(NameType.GENERIC, RuleType.APPROX, true);
/*     */ 
/*     */   public Object encode(Object source) throws EncoderException {
/*  88 */     if (!(source instanceof String)) {
/*  89 */       throw new EncoderException("BeiderMorseEncoder encode parameter is not of type String");
/*     */     }
/*  91 */     return encode((String)source);
/*     */   }
/*     */ 
/*     */   public String encode(String source) throws EncoderException {
/*  95 */     if (source == null) {
/*  96 */       return null;
/*     */     }
/*  98 */     return this.engine.encode(source);
/*     */   }
/*     */ 
/*     */   public NameType getNameType()
/*     */   {
/* 107 */     return this.engine.getNameType();
/*     */   }
/*     */ 
/*     */   public RuleType getRuleType()
/*     */   {
/* 116 */     return this.engine.getRuleType();
/*     */   }
/*     */ 
/*     */   public boolean isConcat()
/*     */   {
/* 125 */     return this.engine.isConcat();
/*     */   }
/*     */ 
/*     */   public void setConcat(boolean concat)
/*     */   {
/* 135 */     this.engine = new PhoneticEngine(this.engine.getNameType(), this.engine.getRuleType(), concat);
/*     */   }
/*     */ 
/*     */   public void setNameType(NameType nameType)
/*     */   {
/* 146 */     this.engine = new PhoneticEngine(nameType, this.engine.getRuleType(), this.engine.isConcat());
/*     */   }
/*     */ 
/*     */   public void setRuleType(RuleType ruleType)
/*     */   {
/* 156 */     this.engine = new PhoneticEngine(this.engine.getNameType(), ruleType, this.engine.isConcat());
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.language.bm.BeiderMorseEncoder
 * JD-Core Version:    0.6.0
 */