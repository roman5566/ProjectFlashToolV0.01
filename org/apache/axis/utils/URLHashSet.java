/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.net.URL;
/*     */ import java.util.HashSet;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class URLHashSet extends HashSet
/*     */ {
/*     */   public boolean add(URL url)
/*     */   {
/*  37 */     return super.add(normalize(url));
/*     */   }
/*     */ 
/*     */   public boolean remove(URL url)
/*     */   {
/*  47 */     return super.remove(normalize(url));
/*     */   }
/*     */ 
/*     */   public boolean contains(URL url)
/*     */   {
/*  57 */     return super.contains(normalize(url));
/*     */   }
/*     */ 
/*     */   public static URL normalize(URL url)
/*     */   {
/*  67 */     if (url.getProtocol().equals("file"))
/*     */       try {
/*  69 */         File f = new File(cleanup(url.getFile()));
/*  70 */         if (f.exists())
/*  71 */           return f.toURL();
/*     */       } catch (Exception e) {
/*     */       }
/*  74 */     return url;
/*     */   }
/*     */ 
/*     */   private static String cleanup(String uri)
/*     */   {
/*  84 */     String[] dirty = tokenize(uri, "/\\", false);
/*  85 */     int length = dirty.length;
/*  86 */     String[] clean = new String[length];
/*     */     while (true)
/*     */     {
/*  91 */       boolean path = false;
/*  92 */       boolean finished = true;
/*  93 */       int i = 0; for (int j = 0; (i < length) && (dirty[i] != null); i++) {
/*  94 */         if (".".equals(dirty[i]))
/*     */           continue;
/*  96 */         if ("..".equals(dirty[i])) {
/*  97 */           clean[(j++)] = dirty[i];
/*  98 */           if (path) {
/*  99 */             finished = false;
/*     */           }
/*     */         }
/* 102 */         else if ((i + 1 < length) && ("..".equals(dirty[(i + 1)]))) {
/* 103 */           i++;
/*     */         } else {
/* 105 */           clean[(j++)] = dirty[i];
/* 106 */           path = true;
/*     */         }
/*     */       }
/*     */ 
/* 110 */       if (finished) {
/*     */         break;
/*     */       }
/* 113 */       dirty = clean;
/* 114 */       clean = new String[length];
/*     */     }
/*     */ 
/* 117 */     StringBuffer b = new StringBuffer(uri.length());
/*     */ 
/* 119 */     for (int i = 0; (i < length) && (clean[i] != null); i++) {
/* 120 */       b.append(clean[i]);
/* 121 */       if ((i + 1 < length) && (clean[(i + 1)] != null)) {
/* 122 */         b.append("/");
/*     */       }
/*     */     }
/* 125 */     return b.toString();
/*     */   }
/*     */ 
/*     */   private static String[] tokenize(String str, String delim, boolean returnTokens)
/*     */   {
/* 145 */     StringTokenizer tokenizer = new StringTokenizer(str, delim, returnTokens);
/* 146 */     String[] tokens = new String[tokenizer.countTokens()];
/* 147 */     int i = 0;
/* 148 */     while (tokenizer.hasMoreTokens()) {
/* 149 */       tokens[i] = tokenizer.nextToken();
/* 150 */       i++;
/*     */     }
/* 152 */     return tokens;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.URLHashSet
 * JD-Core Version:    0.6.0
 */