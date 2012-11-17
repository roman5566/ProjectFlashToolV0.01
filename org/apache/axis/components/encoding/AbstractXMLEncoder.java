/*     */ package org.apache.axis.components.encoding;
/*     */ 
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public abstract class AbstractXMLEncoder
/*     */   implements XMLEncoder
/*     */ {
/*     */   protected static final String AMP = "&amp;";
/*     */   protected static final String QUOTE = "&quot;";
/*     */   protected static final String LESS = "&lt;";
/*     */   protected static final String GREATER = "&gt;";
/*     */   protected static final String LF = "\n";
/*     */   protected static final String CR = "\r";
/*     */   protected static final String TAB = "\t";
/*     */ 
/*     */   public abstract String getEncoding();
/*     */ 
/*     */   public String encode(String xmlString)
/*     */   {
/*  54 */     if (xmlString == null) {
/*  55 */       return "";
/*     */     }
/*  57 */     char[] characters = xmlString.toCharArray();
/*  58 */     StringBuffer out = null;
/*     */ 
/*  61 */     for (int i = 0; i < characters.length; i++) {
/*  62 */       char character = characters[i];
/*  63 */       switch (character)
/*     */       {
/*     */       case '&':
/*  67 */         if (out == null) {
/*  68 */           out = getInitialByteArray(xmlString, i);
/*     */         }
/*  70 */         out.append("&amp;");
/*  71 */         break;
/*     */       case '"':
/*  73 */         if (out == null) {
/*  74 */           out = getInitialByteArray(xmlString, i);
/*     */         }
/*  76 */         out.append("&quot;");
/*  77 */         break;
/*     */       case '<':
/*  79 */         if (out == null) {
/*  80 */           out = getInitialByteArray(xmlString, i);
/*     */         }
/*  82 */         out.append("&lt;");
/*  83 */         break;
/*     */       case '>':
/*  85 */         if (out == null) {
/*  86 */           out = getInitialByteArray(xmlString, i);
/*     */         }
/*  88 */         out.append("&gt;");
/*  89 */         break;
/*     */       case '\n':
/*  91 */         if (out == null) {
/*  92 */           out = getInitialByteArray(xmlString, i);
/*     */         }
/*  94 */         out.append("\n");
/*  95 */         break;
/*     */       case '\r':
/*  97 */         if (out == null) {
/*  98 */           out = getInitialByteArray(xmlString, i);
/*     */         }
/* 100 */         out.append("\r");
/* 101 */         break;
/*     */       case '\t':
/* 103 */         if (out == null) {
/* 104 */           out = getInitialByteArray(xmlString, i);
/*     */         }
/* 106 */         out.append("\t");
/* 107 */         break;
/*     */       default:
/* 109 */         if (character < ' ') {
/* 110 */           throw new IllegalArgumentException(Messages.getMessage("invalidXmlCharacter00", Integer.toHexString(character), xmlString.substring(0, i)));
/*     */         }
/* 112 */         if (out == null) continue;
/* 113 */         out.append(character);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 119 */     if (out == null) {
/* 120 */       return xmlString;
/*     */     }
/* 122 */     return out.toString();
/*     */   }
/*     */ 
/*     */   protected StringBuffer getInitialByteArray(String aXmlString, int pos) {
/* 126 */     return new StringBuffer(aXmlString.substring(0, pos));
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.encoding.AbstractXMLEncoder
 * JD-Core Version:    0.6.0
 */