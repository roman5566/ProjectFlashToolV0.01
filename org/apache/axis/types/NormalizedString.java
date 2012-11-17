/*     */ package org.apache.axis.types;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class NormalizedString
/*     */   implements Serializable
/*     */ {
/*  30 */   String m_value = null;
/*     */ 
/*     */   public NormalizedString()
/*     */   {
/*     */   }
/*     */ 
/*     */   public NormalizedString(String stValue)
/*     */     throws IllegalArgumentException
/*     */   {
/*  43 */     setValue(stValue);
/*     */   }
/*     */ 
/*     */   public void setValue(String stValue)
/*     */     throws IllegalArgumentException
/*     */   {
/*  53 */     if (!isValid(stValue)) {
/*  54 */       throw new IllegalArgumentException(Messages.getMessage("badNormalizedString00") + " data=[" + stValue + "]");
/*     */     }
/*     */ 
/*  57 */     this.m_value = stValue;
/*     */   }
/*     */ 
/*     */   public String toString() {
/*  61 */     return this.m_value;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/*  65 */     return this.m_value.hashCode();
/*     */   }
/*     */ 
/*     */   public static boolean isValid(String stValue)
/*     */   {
/*  84 */     for (int scan = 0; scan < stValue.length(); scan++) {
/*  85 */       char cDigit = stValue.charAt(scan);
/*  86 */       switch (cDigit) {
/*     */       case '\t':
/*     */       case '\n':
/*     */       case '\r':
/*  90 */         return false;
/*     */       case '\013':
/*     */       case '\f':
/*     */       }
/*     */     }
/*  95 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object object) {
/*  99 */     String s1 = object.toString();
/* 100 */     return s1.equals(this.m_value);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.NormalizedString
 * JD-Core Version:    0.6.0
 */