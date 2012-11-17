/*     */ package org.apache.axis.wsdl.symbolTable;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import javax.xml.namespace.QName;
/*     */ 
/*     */ public class BackslashUtil
/*     */   implements Serializable
/*     */ {
/*     */   public static QName getQNameWithBackslashlessLocal(QName suspectQName)
/*     */   {
/*  35 */     String trustedString = null;
/*     */ 
/*  38 */     trustedString = stripBackslashes(suspectQName.getLocalPart());
/*  39 */     return getQNameWithDifferentLocal(suspectQName, trustedString);
/*     */   }
/*     */ 
/*     */   public static QName getQNameWithBackslashedLocal(QName suspectQName)
/*     */   {
/*  48 */     String trustedString = null;
/*     */ 
/*  51 */     trustedString = applyBackslashes(suspectQName.getLocalPart());
/*  52 */     return getQNameWithDifferentLocal(suspectQName, trustedString);
/*     */   }
/*     */ 
/*     */   public static QName getQNameWithDifferentLocal(QName qName, String localName)
/*     */   {
/*  59 */     QName trustedQName = null;
/*     */ 
/*  62 */     trustedQName = new QName(qName.getNamespaceURI(), localName, qName.getPrefix());
/*  63 */     return trustedQName;
/*     */   }
/*     */ 
/*     */   public static String applyBackslashes(String string)
/*     */   {
/*  70 */     return transformBackslashes(string, false);
/*     */   }
/*     */ 
/*     */   public static String stripBackslashes(String string)
/*     */   {
/*  77 */     return transformBackslashes(string, true);
/*     */   }
/*     */ 
/*     */   public static String transformBackslashes(String string, boolean delete)
/*     */   {
/*  85 */     byte[] suspectBytes = null;
/*  86 */     StringBuffer stringBuffer = null;
/*     */ 
/*  88 */     suspectBytes = string.getBytes();
/*  89 */     stringBuffer = new StringBuffer(string);
/*     */ 
/*  91 */     for (int b = suspectBytes.length - 1; b >= 0; b--) {
/*  92 */       if (suspectBytes[b] == 92) {
/*  93 */         if (delete)
/*  94 */           stringBuffer.delete(b, b + 1);
/*     */         else {
/*  96 */           stringBuffer.insert(b, "\\");
/*     */         }
/*     */       }
/*     */     }
/* 100 */     return stringBuffer.toString();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.BackslashUtil
 * JD-Core Version:    0.6.0
 */