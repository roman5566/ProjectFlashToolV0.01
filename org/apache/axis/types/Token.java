/*     */ package org.apache.axis.types;
/*     */ 
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class Token extends NormalizedString
/*     */ {
/*     */   public Token()
/*     */   {
/*     */   }
/*     */ 
/*     */   public Token(String stValue)
/*     */     throws IllegalArgumentException
/*     */   {
/*     */     try
/*     */     {
/*  40 */       setValue(stValue);
/*     */     }
/*     */     catch (IllegalArgumentException e)
/*     */     {
/*  44 */       throw new IllegalArgumentException(Messages.getMessage("badToken00") + "data=[" + stValue + "]");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static boolean isValid(String stValue)
/*     */   {
/*  66 */     if ((stValue == null) || (stValue.length() == 0)) {
/*  67 */       return true;
/*     */     }
/*     */ 
/*  70 */     if (stValue.charAt(0) == ' ') {
/*  71 */       return false;
/*     */     }
/*     */ 
/*  74 */     if (stValue.charAt(stValue.length() - 1) == ' ') {
/*  75 */       return false;
/*     */     }
/*  77 */     for (int scan = 0; scan < stValue.length(); scan++) {
/*  78 */       char cDigit = stValue.charAt(scan);
/*  79 */       switch (cDigit) {
/*     */       case '\t':
/*     */       case '\n':
/*  82 */         return false;
/*     */       case ' ':
/*  85 */         if ((scan + 1 < stValue.length()) && 
/*  86 */           (stValue.charAt(scan + 1) == ' ')) {
/*  87 */           return false;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  93 */     return true;
/*     */   }
/*     */ 
/*     */   public void setValue(String stValue)
/*     */     throws IllegalArgumentException
/*     */   {
/* 103 */     if (!isValid(stValue)) {
/* 104 */       throw new IllegalArgumentException(Messages.getMessage("badToken00") + " data=[" + stValue + "]");
/*     */     }
/*     */ 
/* 107 */     this.m_value = stValue;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.Token
 * JD-Core Version:    0.6.0
 */