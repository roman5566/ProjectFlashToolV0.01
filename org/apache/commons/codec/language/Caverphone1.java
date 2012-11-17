/*     */ package org.apache.commons.codec.language;
/*     */ 
/*     */ import java.util.Locale;
/*     */ 
/*     */ public class Caverphone1 extends AbstractCaverphone
/*     */ {
/*     */   private static final String SIX_1 = "111111";
/*     */ 
/*     */   public String encode(String source)
/*     */   {
/*  44 */     String txt = source;
/*  45 */     if ((txt == null) || (txt.length() == 0)) {
/*  46 */       return "111111";
/*     */     }
/*     */ 
/*  50 */     txt = txt.toLowerCase(Locale.ENGLISH);
/*     */ 
/*  53 */     txt = txt.replaceAll("[^a-z]", "");
/*     */ 
/*  57 */     txt = txt.replaceAll("^cough", "cou2f");
/*  58 */     txt = txt.replaceAll("^rough", "rou2f");
/*  59 */     txt = txt.replaceAll("^tough", "tou2f");
/*  60 */     txt = txt.replaceAll("^enough", "enou2f");
/*  61 */     txt = txt.replaceAll("^gn", "2n");
/*     */ 
/*  64 */     txt = txt.replaceAll("mb$", "m2");
/*     */ 
/*  67 */     txt = txt.replaceAll("cq", "2q");
/*  68 */     txt = txt.replaceAll("ci", "si");
/*  69 */     txt = txt.replaceAll("ce", "se");
/*  70 */     txt = txt.replaceAll("cy", "sy");
/*  71 */     txt = txt.replaceAll("tch", "2ch");
/*  72 */     txt = txt.replaceAll("c", "k");
/*  73 */     txt = txt.replaceAll("q", "k");
/*  74 */     txt = txt.replaceAll("x", "k");
/*  75 */     txt = txt.replaceAll("v", "f");
/*  76 */     txt = txt.replaceAll("dg", "2g");
/*  77 */     txt = txt.replaceAll("tio", "sio");
/*  78 */     txt = txt.replaceAll("tia", "sia");
/*  79 */     txt = txt.replaceAll("d", "t");
/*  80 */     txt = txt.replaceAll("ph", "fh");
/*  81 */     txt = txt.replaceAll("b", "p");
/*  82 */     txt = txt.replaceAll("sh", "s2");
/*  83 */     txt = txt.replaceAll("z", "s");
/*  84 */     txt = txt.replaceAll("^[aeiou]", "A");
/*     */ 
/*  86 */     txt = txt.replaceAll("[aeiou]", "3");
/*  87 */     txt = txt.replaceAll("3gh3", "3kh3");
/*  88 */     txt = txt.replaceAll("gh", "22");
/*  89 */     txt = txt.replaceAll("g", "k");
/*  90 */     txt = txt.replaceAll("s+", "S");
/*  91 */     txt = txt.replaceAll("t+", "T");
/*  92 */     txt = txt.replaceAll("p+", "P");
/*  93 */     txt = txt.replaceAll("k+", "K");
/*  94 */     txt = txt.replaceAll("f+", "F");
/*  95 */     txt = txt.replaceAll("m+", "M");
/*  96 */     txt = txt.replaceAll("n+", "N");
/*  97 */     txt = txt.replaceAll("w3", "W3");
/*  98 */     txt = txt.replaceAll("wy", "Wy");
/*  99 */     txt = txt.replaceAll("wh3", "Wh3");
/* 100 */     txt = txt.replaceAll("why", "Why");
/* 101 */     txt = txt.replaceAll("w", "2");
/* 102 */     txt = txt.replaceAll("^h", "A");
/* 103 */     txt = txt.replaceAll("h", "2");
/* 104 */     txt = txt.replaceAll("r3", "R3");
/* 105 */     txt = txt.replaceAll("ry", "Ry");
/* 106 */     txt = txt.replaceAll("r", "2");
/* 107 */     txt = txt.replaceAll("l3", "L3");
/* 108 */     txt = txt.replaceAll("ly", "Ly");
/* 109 */     txt = txt.replaceAll("l", "2");
/* 110 */     txt = txt.replaceAll("j", "y");
/* 111 */     txt = txt.replaceAll("y3", "Y3");
/* 112 */     txt = txt.replaceAll("y", "2");
/*     */ 
/* 115 */     txt = txt.replaceAll("2", "");
/* 116 */     txt = txt.replaceAll("3", "");
/*     */ 
/* 119 */     txt = txt + "111111";
/*     */ 
/* 122 */     return txt.substring(0, "111111".length());
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.language.Caverphone1
 * JD-Core Version:    0.6.0
 */