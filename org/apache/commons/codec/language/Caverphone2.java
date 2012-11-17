/*     */ package org.apache.commons.codec.language;
/*     */ 
/*     */ import java.util.Locale;
/*     */ 
/*     */ public class Caverphone2 extends AbstractCaverphone
/*     */ {
/*     */   private static final String TEN_1 = "1111111111";
/*     */ 
/*     */   public String encode(String source)
/*     */   {
/*  44 */     String txt = source;
/*  45 */     if ((txt == null) || (txt.length() == 0)) {
/*  46 */       return "1111111111";
/*     */     }
/*     */ 
/*  50 */     txt = txt.toLowerCase(Locale.ENGLISH);
/*     */ 
/*  53 */     txt = txt.replaceAll("[^a-z]", "");
/*     */ 
/*  56 */     txt = txt.replaceAll("e$", "");
/*     */ 
/*  59 */     txt = txt.replaceAll("^cough", "cou2f");
/*  60 */     txt = txt.replaceAll("^rough", "rou2f");
/*  61 */     txt = txt.replaceAll("^tough", "tou2f");
/*  62 */     txt = txt.replaceAll("^enough", "enou2f");
/*  63 */     txt = txt.replaceAll("^trough", "trou2f");
/*  64 */     txt = txt.replaceAll("^gn", "2n");
/*     */ 
/*  67 */     txt = txt.replaceAll("mb$", "m2");
/*     */ 
/*  70 */     txt = txt.replaceAll("cq", "2q");
/*  71 */     txt = txt.replaceAll("ci", "si");
/*  72 */     txt = txt.replaceAll("ce", "se");
/*  73 */     txt = txt.replaceAll("cy", "sy");
/*  74 */     txt = txt.replaceAll("tch", "2ch");
/*  75 */     txt = txt.replaceAll("c", "k");
/*  76 */     txt = txt.replaceAll("q", "k");
/*  77 */     txt = txt.replaceAll("x", "k");
/*  78 */     txt = txt.replaceAll("v", "f");
/*  79 */     txt = txt.replaceAll("dg", "2g");
/*  80 */     txt = txt.replaceAll("tio", "sio");
/*  81 */     txt = txt.replaceAll("tia", "sia");
/*  82 */     txt = txt.replaceAll("d", "t");
/*  83 */     txt = txt.replaceAll("ph", "fh");
/*  84 */     txt = txt.replaceAll("b", "p");
/*  85 */     txt = txt.replaceAll("sh", "s2");
/*  86 */     txt = txt.replaceAll("z", "s");
/*  87 */     txt = txt.replaceAll("^[aeiou]", "A");
/*  88 */     txt = txt.replaceAll("[aeiou]", "3");
/*  89 */     txt = txt.replaceAll("j", "y");
/*  90 */     txt = txt.replaceAll("^y3", "Y3");
/*  91 */     txt = txt.replaceAll("^y", "A");
/*  92 */     txt = txt.replaceAll("y", "3");
/*  93 */     txt = txt.replaceAll("3gh3", "3kh3");
/*  94 */     txt = txt.replaceAll("gh", "22");
/*  95 */     txt = txt.replaceAll("g", "k");
/*  96 */     txt = txt.replaceAll("s+", "S");
/*  97 */     txt = txt.replaceAll("t+", "T");
/*  98 */     txt = txt.replaceAll("p+", "P");
/*  99 */     txt = txt.replaceAll("k+", "K");
/* 100 */     txt = txt.replaceAll("f+", "F");
/* 101 */     txt = txt.replaceAll("m+", "M");
/* 102 */     txt = txt.replaceAll("n+", "N");
/* 103 */     txt = txt.replaceAll("w3", "W3");
/* 104 */     txt = txt.replaceAll("wh3", "Wh3");
/* 105 */     txt = txt.replaceAll("w$", "3");
/* 106 */     txt = txt.replaceAll("w", "2");
/* 107 */     txt = txt.replaceAll("^h", "A");
/* 108 */     txt = txt.replaceAll("h", "2");
/* 109 */     txt = txt.replaceAll("r3", "R3");
/* 110 */     txt = txt.replaceAll("r$", "3");
/* 111 */     txt = txt.replaceAll("r", "2");
/* 112 */     txt = txt.replaceAll("l3", "L3");
/* 113 */     txt = txt.replaceAll("l$", "3");
/* 114 */     txt = txt.replaceAll("l", "2");
/*     */ 
/* 117 */     txt = txt.replaceAll("2", "");
/* 118 */     txt = txt.replaceAll("3$", "A");
/* 119 */     txt = txt.replaceAll("3", "");
/*     */ 
/* 122 */     txt = txt + "1111111111";
/*     */ 
/* 125 */     return txt.substring(0, "1111111111".length());
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.language.Caverphone2
 * JD-Core Version:    0.6.0
 */