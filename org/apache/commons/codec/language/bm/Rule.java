/*     */ package org.apache.commons.codec.language.bm;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.EnumMap;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Scanner;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ public class Rule
/*     */ {
/* 157 */   public static final RPattern ALL_STRINGS_RMATCHER = new RPattern() {
/*     */     public boolean isMatch(CharSequence input) {
/* 159 */       return true;
/*     */     }
/* 157 */   };
/*     */   public static final String ALL = "ALL";
/*     */   private static final String DOUBLE_QUOTE = "\"";
/*     */   private static final String HASH_INCLUDE = "#include";
/* 169 */   private static final Map<NameType, Map<RuleType, Map<String, List<Rule>>>> RULES = new EnumMap(NameType.class);
/*     */   private final RPattern lContext;
/*     */   private final String pattern;
/*     */   private final PhonemeExpr phoneme;
/*     */   private final RPattern rContext;
/*     */ 
/*     */   private static boolean contains(CharSequence chars, char input)
/*     */   {
/* 199 */     for (int i = 0; i < chars.length(); i++) {
/* 200 */       if (chars.charAt(i) == input) {
/* 201 */         return true;
/*     */       }
/*     */     }
/* 204 */     return false;
/*     */   }
/*     */ 
/*     */   private static String createResourceName(NameType nameType, RuleType rt, String lang) {
/* 208 */     return String.format("org/apache/commons/codec/language/bm/%s_%s_%s.txt", new Object[] { nameType.getName(), rt.getName(), lang });
/*     */   }
/*     */ 
/*     */   private static Scanner createScanner(NameType nameType, RuleType rt, String lang) {
/* 212 */     String resName = createResourceName(nameType, rt, lang);
/* 213 */     InputStream rulesIS = Languages.class.getClassLoader().getResourceAsStream(resName);
/*     */ 
/* 215 */     if (rulesIS == null) {
/* 216 */       throw new IllegalArgumentException("Unable to load resource: " + resName);
/*     */     }
/*     */ 
/* 219 */     return new Scanner(rulesIS, "UTF-8");
/*     */   }
/*     */ 
/*     */   private static Scanner createScanner(String lang) {
/* 223 */     String resName = String.format("org/apache/commons/codec/language/bm/%s.txt", new Object[] { lang });
/* 224 */     InputStream rulesIS = Languages.class.getClassLoader().getResourceAsStream(resName);
/*     */ 
/* 226 */     if (rulesIS == null) {
/* 227 */       throw new IllegalArgumentException("Unable to load resource: " + resName);
/*     */     }
/*     */ 
/* 230 */     return new Scanner(rulesIS, "UTF-8");
/*     */   }
/*     */ 
/*     */   private static boolean endsWith(CharSequence input, CharSequence suffix) {
/* 234 */     if (suffix.length() > input.length()) {
/* 235 */       return false;
/*     */     }
/* 237 */     int i = input.length() - 1; for (int j = suffix.length() - 1; j >= 0; j--) {
/* 238 */       if (input.charAt(i) != suffix.charAt(j))
/* 239 */         return false;
/* 237 */       i--;
/*     */     }
/*     */ 
/* 242 */     return true;
/*     */   }
/*     */ 
/*     */   public static List<Rule> getInstance(NameType nameType, RuleType rt, Languages.LanguageSet langs)
/*     */   {
/* 257 */     return langs.isSingleton() ? getInstance(nameType, rt, langs.getAny()) : getInstance(nameType, rt, "any");
/*     */   }
/*     */ 
/*     */   public static List<Rule> getInstance(NameType nameType, RuleType rt, String lang)
/*     */   {
/* 272 */     List rules = (List)((Map)((Map)RULES.get(nameType)).get(rt)).get(lang);
/*     */ 
/* 274 */     if (rules == null) {
/* 275 */       throw new IllegalArgumentException(String.format("No rules found for %s, %s, %s.", new Object[] { nameType.getName(), rt.getName(), lang }));
/*     */     }
/*     */ 
/* 278 */     return rules;
/*     */   }
/*     */ 
/*     */   private static Phoneme parsePhoneme(String ph) {
/* 282 */     int open = ph.indexOf("[");
/* 283 */     if (open >= 0) {
/* 284 */       if (!ph.endsWith("]")) {
/* 285 */         throw new IllegalArgumentException("Phoneme expression contains a '[' but does not end in ']'");
/*     */       }
/* 287 */       String before = ph.substring(0, open);
/* 288 */       String in = ph.substring(open + 1, ph.length() - 1);
/* 289 */       Set langs = new HashSet(Arrays.asList(in.split("[+]")));
/*     */ 
/* 291 */       return new Phoneme(before, Languages.LanguageSet.from(langs));
/*     */     }
/* 293 */     return new Phoneme(ph, Languages.ANY_LANGUAGE);
/*     */   }
/*     */ 
/*     */   private static PhonemeExpr parsePhonemeExpr(String ph)
/*     */   {
/* 298 */     if (ph.startsWith("(")) {
/* 299 */       if (!ph.endsWith(")")) {
/* 300 */         throw new IllegalArgumentException("Phoneme starts with '(' so must end with ')'");
/*     */       }
/*     */ 
/* 303 */       List phs = new ArrayList();
/* 304 */       String body = ph.substring(1, ph.length() - 1);
/* 305 */       for (String part : body.split("[|]")) {
/* 306 */         phs.add(parsePhoneme(part));
/*     */       }
/* 308 */       if ((body.startsWith("|")) || (body.endsWith("|"))) {
/* 309 */         phs.add(new Phoneme("", Languages.ANY_LANGUAGE));
/*     */       }
/*     */ 
/* 312 */       return new PhonemeList(phs);
/*     */     }
/* 314 */     return parsePhoneme(ph);
/*     */   }
/*     */ 
/*     */   private static List<Rule> parseRules(Scanner scanner, String location)
/*     */   {
/* 319 */     List lines = new ArrayList();
/* 320 */     int currentLine = 0;
/*     */ 
/* 322 */     boolean inMultilineComment = false;
/* 323 */     while (scanner.hasNextLine()) {
/* 324 */       currentLine++;
/* 325 */       String rawLine = scanner.nextLine();
/* 326 */       String line = rawLine;
/*     */ 
/* 328 */       if (inMultilineComment) {
/* 329 */         if (line.endsWith("*/")) {
/* 330 */           inMultilineComment = false;
/*     */         }
/*     */ 
/*     */       }
/* 335 */       else if (line.startsWith("/*")) {
/* 336 */         inMultilineComment = true;
/*     */       }
/*     */       else {
/* 339 */         int cmtI = line.indexOf("//");
/* 340 */         if (cmtI >= 0) {
/* 341 */           line = line.substring(0, cmtI);
/*     */         }
/*     */ 
/* 345 */         line = line.trim();
/*     */ 
/* 347 */         if (line.length() == 0)
/*     */         {
/*     */           continue;
/*     */         }
/* 351 */         if (line.startsWith("#include"))
/*     */         {
/* 353 */           String incl = line.substring("#include".length()).trim();
/* 354 */           if (incl.contains(" "))
/* 355 */             System.err.println("Warining: malformed import statement: " + rawLine);
/*     */           else
/* 357 */             lines.addAll(parseRules(createScanner(incl), location + "->" + incl));
/*     */         }
/*     */         else
/*     */         {
/* 361 */           String[] parts = line.split("\\s+");
/* 362 */           if (parts.length != 4)
/* 363 */             System.err.println("Warning: malformed rule statement split into " + parts.length + " parts: " + rawLine);
/*     */           else {
/*     */             try {
/* 366 */               String pat = stripQuotes(parts[0]);
/* 367 */               String lCon = stripQuotes(parts[1]);
/* 368 */               String rCon = stripQuotes(parts[2]);
/* 369 */               PhonemeExpr ph = parsePhonemeExpr(stripQuotes(parts[3]));
/* 370 */               int cLine = currentLine;
/* 371 */               Rule r = new Rule(pat, lCon, rCon, ph, cLine, location) {
/* 372 */                 private final int myLine = this.val$cLine;
/* 373 */                 private final String loc = this.val$location;
/*     */ 
/*     */                 public String toString()
/*     */                 {
/* 377 */                   StringBuilder sb = new StringBuilder();
/* 378 */                   sb.append("Rule");
/* 379 */                   sb.append("{line=").append(this.myLine);
/* 380 */                   sb.append(", loc='").append(this.loc).append('\'');
/* 381 */                   sb.append('}');
/* 382 */                   return sb.toString();
/*     */                 }
/*     */               };
/* 385 */               lines.add(r);
/*     */             } catch (IllegalArgumentException e) {
/* 387 */               throw new IllegalStateException("Problem parsing line " + currentLine, e);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 395 */     return lines;
/*     */   }
/*     */ 
/*     */   private static RPattern pattern(String regex)
/*     */   {
/* 406 */     boolean startsWith = regex.startsWith("^");
/* 407 */     boolean endsWith = regex.endsWith("$");
/* 408 */     String content = regex.substring(startsWith ? 1 : 0, endsWith ? regex.length() - 1 : regex.length());
/* 409 */     boolean boxes = content.contains("[");
/*     */ 
/* 411 */     if (!boxes) {
/* 412 */       if ((startsWith) && (endsWith))
/*     */       {
/* 414 */         if (content.length() == 0)
/*     */         {
/* 416 */           return new RPattern() {
/*     */             public boolean isMatch(CharSequence input) {
/* 418 */               return input.length() == 0;
/*     */             } } ;
/*     */         }
/* 422 */         return new RPattern(content) {
/*     */           public boolean isMatch(CharSequence input) {
/* 424 */             return input.equals(this.val$content);
/*     */           } } ;
/*     */       }
/* 428 */       if (((startsWith) || (endsWith)) && (content.length() == 0))
/*     */       {
/* 430 */         return ALL_STRINGS_RMATCHER;
/* 431 */       }if (startsWith)
/*     */       {
/* 433 */         return new RPattern(content) {
/*     */           public boolean isMatch(CharSequence input) {
/* 435 */             return Rule.access$100(input, this.val$content);
/*     */           } } ;
/*     */       }
/* 438 */       if (endsWith)
/*     */       {
/* 440 */         return new RPattern(content) {
/*     */           public boolean isMatch(CharSequence input) {
/* 442 */             return Rule.access$200(input, this.val$content);
/*     */           } } ;
/*     */       }
/*     */     } else {
/* 447 */       boolean startsWithBox = content.startsWith("[");
/* 448 */       boolean endsWithBox = content.endsWith("]");
/*     */ 
/* 450 */       if ((startsWithBox) && (endsWithBox)) {
/* 451 */         String boxContent = content.substring(1, content.length() - 1);
/* 452 */         if (!boxContent.contains("["))
/*     */         {
/* 454 */           boolean negate = boxContent.startsWith("^");
/* 455 */           if (negate) {
/* 456 */             boxContent = boxContent.substring(1);
/*     */           }
/* 458 */           String bContent = boxContent;
/* 459 */           boolean shouldMatch = !negate;
/*     */ 
/* 461 */           if ((startsWith) && (endsWith))
/*     */           {
/* 463 */             return new RPattern(bContent, shouldMatch) {
/*     */               public boolean isMatch(CharSequence input) {
/* 465 */                 return (input.length() == 1) && (Rule.access$300(this.val$bContent, input.charAt(0)) == this.val$shouldMatch);
/*     */               } } ;
/*     */           }
/* 468 */           if (startsWith)
/*     */           {
/* 470 */             return new RPattern(bContent, shouldMatch) {
/*     */               public boolean isMatch(CharSequence input) {
/* 472 */                 return (input.length() > 0) && (Rule.access$300(this.val$bContent, input.charAt(0)) == this.val$shouldMatch);
/*     */               } } ;
/*     */           }
/* 475 */           if (endsWith)
/*     */           {
/* 477 */             return new RPattern(bContent, shouldMatch) {
/*     */               public boolean isMatch(CharSequence input) {
/* 479 */                 return (input.length() > 0) && (Rule.access$300(this.val$bContent, input.charAt(input.length() - 1)) == this.val$shouldMatch);
/*     */               }
/*     */             };
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 488 */     return new RPattern(regex) {
/* 489 */       Pattern pattern = Pattern.compile(this.val$regex);
/*     */ 
/*     */       public boolean isMatch(CharSequence input) {
/* 492 */         Matcher matcher = this.pattern.matcher(input);
/* 493 */         return matcher.find();
/*     */       } } ;
/*     */   }
/*     */ 
/*     */   private static boolean startsWith(CharSequence input, CharSequence prefix) {
/* 499 */     if (prefix.length() > input.length()) {
/* 500 */       return false;
/*     */     }
/* 502 */     for (int i = 0; i < prefix.length(); i++) {
/* 503 */       if (input.charAt(i) != prefix.charAt(i)) {
/* 504 */         return false;
/*     */       }
/*     */     }
/* 507 */     return true;
/*     */   }
/*     */ 
/*     */   private static String stripQuotes(String str) {
/* 511 */     if (str.startsWith("\"")) {
/* 512 */       str = str.substring(1);
/*     */     }
/*     */ 
/* 515 */     if (str.endsWith("\"")) {
/* 516 */       str = str.substring(0, str.length() - 1);
/*     */     }
/*     */ 
/* 519 */     return str;
/*     */   }
/*     */ 
/*     */   public Rule(String pattern, String lContext, String rContext, PhonemeExpr phoneme)
/*     */   {
/* 543 */     this.pattern = pattern;
/* 544 */     this.lContext = pattern(lContext + "$");
/* 545 */     this.rContext = pattern("^" + rContext);
/* 546 */     this.phoneme = phoneme;
/*     */   }
/*     */ 
/*     */   public RPattern getLContext()
/*     */   {
/* 555 */     return this.lContext;
/*     */   }
/*     */ 
/*     */   public String getPattern()
/*     */   {
/* 564 */     return this.pattern;
/*     */   }
/*     */ 
/*     */   public PhonemeExpr getPhoneme()
/*     */   {
/* 573 */     return this.phoneme;
/*     */   }
/*     */ 
/*     */   public RPattern getRContext()
/*     */   {
/* 582 */     return this.rContext;
/*     */   }
/*     */ 
/*     */   public boolean patternAndContextMatches(CharSequence input, int i)
/*     */   {
/* 597 */     if (i < 0) {
/* 598 */       throw new IndexOutOfBoundsException("Can not match pattern at negative indexes");
/*     */     }
/*     */ 
/* 601 */     int patternLength = this.pattern.length();
/* 602 */     int ipl = i + patternLength;
/*     */ 
/* 604 */     if (ipl > input.length())
/*     */     {
/* 606 */       return false;
/*     */     }
/*     */ 
/* 612 */     boolean patternMatches = input.subSequence(i, ipl).equals(this.pattern);
/* 613 */     boolean rContextMatches = this.rContext.isMatch(input.subSequence(ipl, input.length()));
/* 614 */     boolean lContextMatches = this.lContext.isMatch(input.subSequence(0, i));
/*     */ 
/* 616 */     return (patternMatches) && (rContextMatches) && (lContextMatches);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 173 */     for (NameType s : NameType.values()) {
/* 174 */       Map rts = new EnumMap(RuleType.class);
/*     */ 
/* 176 */       for (RuleType rt : RuleType.values()) {
/* 177 */         Map rs = new HashMap();
/*     */ 
/* 179 */         Languages ls = Languages.getInstance(s);
/* 180 */         for (String l : ls.getLanguages()) {
/*     */           try {
/* 182 */             rs.put(l, parseRules(createScanner(s, rt, l), createResourceName(s, rt, l)));
/*     */           } catch (IllegalStateException e) {
/* 184 */             throw new IllegalStateException("Problem processing " + createResourceName(s, rt, l), e);
/*     */           }
/*     */         }
/* 187 */         if (!rt.equals(RuleType.RULES)) {
/* 188 */           rs.put("common", parseRules(createScanner(s, rt, "common"), createResourceName(s, rt, "common")));
/*     */         }
/*     */ 
/* 191 */         rts.put(rt, Collections.unmodifiableMap(rs));
/*     */       }
/*     */ 
/* 194 */       RULES.put(s, Collections.unmodifiableMap(rts));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract interface RPattern
/*     */   {
/*     */     public abstract boolean isMatch(CharSequence paramCharSequence);
/*     */   }
/*     */ 
/*     */   public static final class PhonemeList
/*     */     implements Rule.PhonemeExpr
/*     */   {
/*     */     private final List<Rule.Phoneme> phonemes;
/*     */ 
/*     */     public PhonemeList(List<Rule.Phoneme> phonemes)
/*     */     {
/* 142 */       this.phonemes = phonemes;
/*     */     }
/*     */ 
/*     */     public List<Rule.Phoneme> getPhonemes() {
/* 146 */       return this.phonemes;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract interface PhonemeExpr
/*     */   {
/*     */     public abstract Iterable<Rule.Phoneme> getPhonemes();
/*     */   }
/*     */ 
/*     */   public static final class Phoneme
/*     */     implements Rule.PhonemeExpr
/*     */   {
/*  85 */     public static final Comparator<Phoneme> COMPARATOR = new Comparator() {
/*     */       public int compare(Rule.Phoneme o1, Rule.Phoneme o2) {
/*  87 */         for (int i = 0; i < o1.phonemeText.length(); i++) {
/*  88 */           if (i >= o2.phonemeText.length()) {
/*  89 */             return 1;
/*     */           }
/*  91 */           int c = o1.phonemeText.charAt(i) - o2.phonemeText.charAt(i);
/*  92 */           if (c != 0) {
/*  93 */             return c;
/*     */           }
/*     */         }
/*     */ 
/*  97 */         if (o1.phonemeText.length() < o2.phonemeText.length()) {
/*  98 */           return -1;
/*     */         }
/*     */ 
/* 101 */         return 0;
/*     */       } } ;
/*     */     private final CharSequence phonemeText;
/*     */     private final Languages.LanguageSet languages;
/*     */ 
/*     */     public Phoneme(CharSequence phonemeText, Languages.LanguageSet languages) {
/* 109 */       this.phonemeText = phonemeText;
/* 110 */       this.languages = languages;
/*     */     }
/*     */ 
/*     */     public Phoneme append(CharSequence str) {
/* 114 */       return new Phoneme(this.phonemeText.toString() + str.toString(), this.languages);
/*     */     }
/*     */ 
/*     */     public Languages.LanguageSet getLanguages() {
/* 118 */       return this.languages;
/*     */     }
/*     */ 
/*     */     public Iterable<Phoneme> getPhonemes() {
/* 122 */       return Collections.singleton(this);
/*     */     }
/*     */ 
/*     */     public CharSequence getPhonemeText() {
/* 126 */       return this.phonemeText;
/*     */     }
/*     */ 
/*     */     public Phoneme join(Phoneme right) {
/* 130 */       return new Phoneme(this.phonemeText.toString() + right.phonemeText.toString(), this.languages.restrictTo(right.languages));
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.language.bm.Rule
 * JD-Core Version:    0.6.0
 */