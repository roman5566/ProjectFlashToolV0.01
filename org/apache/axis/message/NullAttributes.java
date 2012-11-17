/*     */ package org.apache.axis.message;
/*     */ 
/*     */ import org.xml.sax.Attributes;
/*     */ 
/*     */ public class NullAttributes
/*     */   implements Attributes
/*     */ {
/*  13 */   public static final NullAttributes singleton = new NullAttributes();
/*     */ 
/*     */   public int getLength()
/*     */   {
/*  27 */     return 0;
/*     */   }
/*     */ 
/*     */   public String getURI(int index)
/*     */   {
/*  40 */     return null;
/*     */   }
/*     */ 
/*     */   public String getLocalName(int index)
/*     */   {
/*  53 */     return null;
/*     */   }
/*     */ 
/*     */   public String getQName(int index)
/*     */   {
/*  66 */     return null;
/*     */   }
/*     */ 
/*     */   public String getType(int index)
/*     */   {
/*  79 */     return null;
/*     */   }
/*     */ 
/*     */   public String getValue(int index)
/*     */   {
/*  91 */     return null;
/*     */   }
/*     */ 
/*     */   public int getIndex(String uri, String localName)
/*     */   {
/* 109 */     return -1;
/*     */   }
/*     */ 
/*     */   public int getIndex(String qName)
/*     */   {
/* 121 */     return -1;
/*     */   }
/*     */ 
/*     */   public String getType(String uri, String localName)
/*     */   {
/* 136 */     return null;
/*     */   }
/*     */ 
/*     */   public String getType(String qName)
/*     */   {
/* 149 */     return null;
/*     */   }
/*     */ 
/*     */   public String getValue(String uri, String localName)
/*     */   {
/* 164 */     return null;
/*     */   }
/*     */ 
/*     */   public String getValue(String qName)
/*     */   {
/* 177 */     return null;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.NullAttributes
 * JD-Core Version:    0.6.0
 */