/*     */ package org.apache.commons.codec.language.bm;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ public class PhoneticEngine
/*     */ {
/* 229 */   private static final Map<NameType, Set<String>> NAME_PREFIXES = new EnumMap(NameType.class);
/*     */   private final Lang lang;
/*     */   private final NameType nameType;
/*     */   private final RuleType ruleType;
/*     */   private final boolean concat;
/*     */ 
/*     */   private static CharSequence cacheSubSequence(CharSequence cached)
/*     */   {
/* 248 */     CharSequence[][] cache = new CharSequence[cached.length()][cached.length()];
/* 249 */     return new CharSequence(cached, cache) {
/*     */       public char charAt(int index) {
/* 251 */         return this.val$cached.charAt(index);
/*     */       }
/*     */ 
/*     */       public int length() {
/* 255 */         return this.val$cached.length();
/*     */       }
/*     */ 
/*     */       public CharSequence subSequence(int start, int end) {
/* 259 */         if (start == end) {
/* 260 */           return "";
/*     */         }
/*     */ 
/* 263 */         CharSequence res = this.val$cache[start][(end - 1)];
/* 264 */         if (res == null) {
/* 265 */           res = this.val$cached.subSequence(start, end);
/* 266 */           this.val$cache[start][(end - 1)] = res;
/*     */         }
/* 268 */         return res;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private static String join(Iterable<String> strings, String sep)
/*     */   {
/* 280 */     StringBuilder sb = new StringBuilder();
/* 281 */     Iterator si = strings.iterator();
/* 282 */     if (si.hasNext()) {
/* 283 */       sb.append((String)si.next());
/*     */     }
/* 285 */     while (si.hasNext()) {
/* 286 */       sb.append(sep).append((String)si.next());
/*     */     }
/*     */ 
/* 289 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public PhoneticEngine(NameType nameType, RuleType ruleType, boolean concat)
/*     */   {
/* 311 */     if (ruleType == RuleType.RULES) {
/* 312 */       throw new IllegalArgumentException("ruleType must not be " + RuleType.RULES);
/*     */     }
/* 314 */     this.nameType = nameType;
/* 315 */     this.ruleType = ruleType;
/* 316 */     this.concat = concat;
/* 317 */     this.lang = Lang.instance(nameType);
/*     */   }
/*     */ 
/*     */   private PhonemeBuilder applyFinalRules(PhonemeBuilder phonemeBuilder, List<Rule> finalRules)
/*     */   {
/* 329 */     if (finalRules == null) {
/* 330 */       throw new NullPointerException("finalRules can not be null");
/*     */     }
/* 332 */     if (finalRules.isEmpty()) {
/* 333 */       return phonemeBuilder;
/*     */     }
/*     */ 
/* 336 */     Set phonemes = new TreeSet(Rule.Phoneme.COMPARATOR);
/*     */ 
/* 338 */     for (Rule.Phoneme phoneme : phonemeBuilder.getPhonemes()) {
/* 339 */       PhonemeBuilder subBuilder = PhonemeBuilder.empty(phoneme.getLanguages());
/* 340 */       CharSequence phonemeText = cacheSubSequence(phoneme.getPhonemeText());
/*     */ 
/* 343 */       for (int i = 0; i < phonemeText.length(); ) {
/* 344 */         RulesApplication rulesApplication = new RulesApplication(finalRules, phonemeText, subBuilder, i).invoke();
/* 345 */         boolean found = rulesApplication.isFound();
/* 346 */         subBuilder = rulesApplication.getPhonemeBuilder();
/*     */ 
/* 348 */         if (!found)
/*     */         {
/* 350 */           subBuilder = subBuilder.append(phonemeText.subSequence(i, i + 1));
/*     */         }
/*     */ 
/* 353 */         i = rulesApplication.getI();
/*     */       }
/*     */ 
/* 360 */       phonemes.addAll(subBuilder.getPhonemes());
/*     */     }
/*     */ 
/* 363 */     return new PhonemeBuilder(phonemes, null);
/*     */   }
/*     */ 
/*     */   public String encode(String input)
/*     */   {
/* 374 */     Languages.LanguageSet languageSet = this.lang.guessLanguages(input);
/* 375 */     return encode(input, languageSet);
/*     */   }
/*     */ 
/*     */   public String encode(String input, Languages.LanguageSet languageSet)
/*     */   {
/* 387 */     List rules = Rule.getInstance(this.nameType, RuleType.RULES, languageSet);
/*     */ 
/* 389 */     List finalRules1 = Rule.getInstance(this.nameType, this.ruleType, "common");
/*     */ 
/* 391 */     List finalRules2 = Rule.getInstance(this.nameType, this.ruleType, languageSet);
/*     */ 
/* 398 */     input = input.toLowerCase(Locale.ENGLISH).replace('-', ' ').trim();
/*     */ 
/* 400 */     if (this.nameType == NameType.GENERIC) {
/* 401 */       if ((input.length() >= 2) && (input.substring(0, 2).equals("d'"))) {
/* 402 */         String remainder = input.substring(2);
/* 403 */         String combined = "d" + remainder;
/* 404 */         return "(" + encode(remainder) + ")-(" + encode(combined) + ")";
/*     */       }
/* 406 */       for (String l : (Set)NAME_PREFIXES.get(this.nameType))
/*     */       {
/* 408 */         if (input.startsWith(l + " "))
/*     */         {
/* 410 */           String remainder = input.substring(l.length() + 1);
/* 411 */           String combined = l + remainder;
/* 412 */           return "(" + encode(remainder) + ")-(" + encode(combined) + ")";
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 417 */     List words = Arrays.asList(input.split("\\s+"));
/* 418 */     List words2 = new ArrayList();
/*     */ 
/* 421 */     switch (2.$SwitchMap$org$apache$commons$codec$language$bm$NameType[this.nameType.ordinal()]) {
/*     */     case 1:
/* 423 */       for (String aWord : words) {
/* 424 */         String[] parts = aWord.split("'");
/* 425 */         String lastPart = parts[(parts.length - 1)];
/* 426 */         words2.add(lastPart);
/*     */       }
/* 428 */       words2.removeAll((Collection)NAME_PREFIXES.get(this.nameType));
/* 429 */       break;
/*     */     case 2:
/* 431 */       words2.addAll(words);
/* 432 */       words2.removeAll((Collection)NAME_PREFIXES.get(this.nameType));
/* 433 */       break;
/*     */     case 3:
/* 435 */       words2.addAll(words);
/* 436 */       break;
/*     */     default:
/* 438 */       throw new IllegalStateException("Unreachable case: " + this.nameType);
/*     */     }
/*     */ 
/* 441 */     if (this.concat)
/*     */     {
/* 443 */       input = join(words2, " ");
/* 444 */     } else if (words2.size() == 1)
/*     */     {
/* 446 */       input = (String)words.iterator().next();
/*     */     }
/*     */     else {
/* 449 */       StringBuilder result = new StringBuilder();
/* 450 */       for (String word : words2) {
/* 451 */         result.append("-").append(encode(word));
/*     */       }
/*     */ 
/* 454 */       return result.substring(1);
/*     */     }
/*     */ 
/* 457 */     PhonemeBuilder phonemeBuilder = PhonemeBuilder.empty(languageSet);
/*     */ 
/* 460 */     CharSequence inputCache = cacheSubSequence(input);
/* 461 */     for (int i = 0; i < inputCache.length(); ) {
/* 462 */       RulesApplication rulesApplication = new RulesApplication(rules, inputCache, phonemeBuilder, i).invoke();
/* 463 */       i = rulesApplication.getI();
/* 464 */       phonemeBuilder = rulesApplication.getPhonemeBuilder();
/*     */     }
/*     */ 
/* 469 */     phonemeBuilder = applyFinalRules(phonemeBuilder, finalRules1);
/*     */ 
/* 471 */     phonemeBuilder = applyFinalRules(phonemeBuilder, finalRules2);
/*     */ 
/* 473 */     return phonemeBuilder.makeString();
/*     */   }
/*     */ 
/*     */   public Lang getLang()
/*     */   {
/* 482 */     return this.lang;
/*     */   }
/*     */ 
/*     */   public NameType getNameType()
/*     */   {
/* 491 */     return this.nameType;
/*     */   }
/*     */ 
/*     */   public RuleType getRuleType()
/*     */   {
/* 500 */     return this.ruleType;
/*     */   }
/*     */ 
/*     */   public boolean isConcat()
/*     */   {
/* 509 */     return this.concat;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 232 */     NAME_PREFIXES.put(NameType.ASHKENAZI, Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[] { "bar", "ben", "da", "de", "van", "von" }))));
/*     */ 
/* 234 */     NAME_PREFIXES.put(NameType.SEPHARDIC, Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[] { "al", "el", "da", "dal", "de", "del", "dela", "de la", "della", "des", "di", "do", "dos", "du", "van", "von" }))));
/*     */ 
/* 236 */     NAME_PREFIXES.put(NameType.GENERIC, Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[] { "da", "dal", "de", "del", "dela", "de la", "della", "des", "di", "do", "dos", "du", "van", "von" }))));
/*     */   }
/*     */ 
/*     */   private static final class RulesApplication
/*     */   {
/*     */     private final List<Rule> finalRules;
/*     */     private final CharSequence input;
/*     */     private PhoneticEngine.PhonemeBuilder phonemeBuilder;
/*     */     private int i;
/*     */     private boolean found;
/*     */ 
/*     */     public RulesApplication(List<Rule> finalRules, CharSequence input, PhoneticEngine.PhonemeBuilder phonemeBuilder, int i)
/*     */     {
/* 174 */       if (finalRules == null) {
/* 175 */         throw new NullPointerException("The finalRules argument must not be null");
/*     */       }
/* 177 */       this.finalRules = finalRules;
/* 178 */       this.phonemeBuilder = phonemeBuilder;
/* 179 */       this.input = input;
/* 180 */       this.i = i;
/*     */     }
/*     */ 
/*     */     public int getI() {
/* 184 */       return this.i;
/*     */     }
/*     */ 
/*     */     public PhoneticEngine.PhonemeBuilder getPhonemeBuilder() {
/* 188 */       return this.phonemeBuilder;
/*     */     }
/*     */ 
/*     */     public RulesApplication invoke()
/*     */     {
/* 199 */       this.found = false;
/* 200 */       int patternLength = 0;
/* 201 */       for (Rule rule : this.finalRules) {
/* 202 */         String pattern = rule.getPattern();
/* 203 */         patternLength = pattern.length();
/*     */ 
/* 206 */         if (!rule.patternAndContextMatches(this.input, this.i))
/*     */         {
/*     */           continue;
/*     */         }
/*     */ 
/* 211 */         this.phonemeBuilder = this.phonemeBuilder.apply(rule.getPhoneme());
/* 212 */         this.found = true;
/*     */       }
/*     */ 
/* 216 */       if (!this.found) {
/* 217 */         patternLength = 1;
/*     */       }
/*     */ 
/* 220 */       this.i += patternLength;
/* 221 */       return this;
/*     */     }
/*     */ 
/*     */     public boolean isFound() {
/* 225 */       return this.found;
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class PhonemeBuilder
/*     */   {
/*     */     private final Set<Rule.Phoneme> phonemes;
/*     */ 
/*     */     public static PhonemeBuilder empty(Languages.LanguageSet languages)
/*     */     {
/*  72 */       return new PhonemeBuilder(Collections.singleton(new Rule.Phoneme("", languages)));
/*     */     }
/*     */ 
/*     */     private PhonemeBuilder(Set<Rule.Phoneme> phonemes)
/*     */     {
/*  78 */       this.phonemes = phonemes;
/*     */     }
/*     */ 
/*     */     public PhonemeBuilder append(CharSequence str)
/*     */     {
/*  88 */       Set newPhonemes = new HashSet();
/*     */ 
/*  90 */       for (Rule.Phoneme ph : this.phonemes) {
/*  91 */         newPhonemes.add(ph.append(str));
/*     */       }
/*     */ 
/*  94 */       return new PhonemeBuilder(newPhonemes);
/*     */     }
/*     */ 
/*     */     public PhonemeBuilder apply(Rule.PhonemeExpr phonemeExpr)
/*     */     {
/* 108 */       Set newPhonemes = new HashSet();
/*     */ 
/* 110 */       for (Iterator i$ = this.phonemes.iterator(); i$.hasNext(); ) { left = (Rule.Phoneme)i$.next();
/* 111 */         for (Rule.Phoneme right : phonemeExpr.getPhonemes()) {
/* 112 */           Rule.Phoneme join = left.join(right);
/* 113 */           if (!join.getLanguages().isEmpty())
/* 114 */             newPhonemes.add(join);
/*     */         }
/*     */       }
/*     */       Rule.Phoneme left;
/* 119 */       return new PhonemeBuilder(newPhonemes);
/*     */     }
/*     */ 
/*     */     public Set<Rule.Phoneme> getPhonemes()
/*     */     {
/* 128 */       return this.phonemes;
/*     */     }
/*     */ 
/*     */     public String makeString()
/*     */     {
/* 140 */       StringBuilder sb = new StringBuilder();
/*     */ 
/* 143 */       for (Rule.Phoneme ph : this.phonemes) {
/* 144 */         if (sb.length() > 0) {
/* 145 */           sb.append("|");
/*     */         }
/* 147 */         sb.append(ph.getPhonemeText());
/*     */       }
/*     */ 
/* 150 */       return sb.toString();
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.language.bm.PhoneticEngine
 * JD-Core Version:    0.6.0
 */