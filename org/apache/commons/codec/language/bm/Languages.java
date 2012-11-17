/*     */ package org.apache.commons.codec.language.bm;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Scanner;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class Languages
/*     */ {
/*     */   public static final String ANY = "any";
/* 141 */   private static final Map<NameType, Languages> LANGUAGES = new EnumMap(NameType.class);
/*     */   private final Set<String> languages;
/*     */   public static final LanguageSet NO_LANGUAGES;
/*     */   public static final LanguageSet ANY_LANGUAGE;
/*     */ 
/*     */   public static Languages getInstance(NameType nameType)
/*     */   {
/* 150 */     return (Languages)LANGUAGES.get(nameType);
/*     */   }
/*     */ 
/*     */   public static Languages getInstance(String languagesResourceName)
/*     */   {
/* 155 */     Set ls = new HashSet();
/* 156 */     InputStream langIS = Languages.class.getClassLoader().getResourceAsStream(languagesResourceName);
/*     */ 
/* 158 */     if (langIS == null) {
/* 159 */       throw new IllegalArgumentException("Unable to resolve required resource: " + languagesResourceName);
/*     */     }
/*     */ 
/* 162 */     Scanner lsScanner = new Scanner(langIS, "UTF-8");
/* 163 */     boolean inExtendedComment = false;
/* 164 */     while (lsScanner.hasNextLine()) {
/* 165 */       String line = lsScanner.nextLine().trim();
/* 166 */       if (inExtendedComment) {
/* 167 */         if (line.endsWith("*/")) {
/* 168 */           inExtendedComment = false;
/*     */         }
/*     */ 
/*     */       }
/* 173 */       else if (line.startsWith("/*"))
/* 174 */         inExtendedComment = true;
/* 175 */       else if (line.length() > 0) {
/* 176 */         ls.add(line);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 183 */     return new Languages(Collections.unmodifiableSet(ls));
/*     */   }
/*     */ 
/*     */   private static String langResourceName(NameType nameType) {
/* 187 */     return String.format("org/apache/commons/codec/language/bm/%s_languages.txt", new Object[] { nameType.getName() });
/*     */   }
/*     */ 
/*     */   private Languages(Set<String> languages)
/*     */   {
/* 263 */     this.languages = languages;
/*     */   }
/*     */ 
/*     */   public Set<String> getLanguages() {
/* 267 */     return this.languages;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 144 */     for (NameType s : NameType.values()) {
/* 145 */       LANGUAGES.put(s, getInstance(langResourceName(s)));
/*     */     }
/*     */ 
/* 195 */     NO_LANGUAGES = new LanguageSet()
/*     */     {
/*     */       public boolean contains(String language) {
/* 198 */         return false;
/*     */       }
/*     */ 
/*     */       public String getAny()
/*     */       {
/* 203 */         throw new NoSuchElementException("Can't fetch any language from the empty language set.");
/*     */       }
/*     */ 
/*     */       public boolean isEmpty()
/*     */       {
/* 208 */         return true;
/*     */       }
/*     */ 
/*     */       public boolean isSingleton()
/*     */       {
/* 213 */         return false;
/*     */       }
/*     */ 
/*     */       public Languages.LanguageSet restrictTo(Languages.LanguageSet other)
/*     */       {
/* 218 */         return this;
/*     */       }
/*     */ 
/*     */       public String toString()
/*     */       {
/* 223 */         return "NO_LANGUAGES";
/*     */       }
/*     */     };
/* 230 */     ANY_LANGUAGE = new LanguageSet()
/*     */     {
/*     */       public boolean contains(String language) {
/* 233 */         return true;
/*     */       }
/*     */ 
/*     */       public String getAny()
/*     */       {
/* 238 */         throw new NoSuchElementException("Can't fetch any language from the any language set.");
/*     */       }
/*     */ 
/*     */       public boolean isEmpty()
/*     */       {
/* 243 */         return false;
/*     */       }
/*     */ 
/*     */       public boolean isSingleton()
/*     */       {
/* 248 */         return false;
/*     */       }
/*     */ 
/*     */       public Languages.LanguageSet restrictTo(Languages.LanguageSet other)
/*     */       {
/* 253 */         return other;
/*     */       }
/*     */ 
/*     */       public String toString()
/*     */       {
/* 258 */         return "ANY_LANGUAGE";
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static final class SomeLanguages extends Languages.LanguageSet
/*     */   {
/*     */     private final Set<String> languages;
/*     */ 
/*     */     private SomeLanguages(Set<String> languages)
/*     */     {
/*  87 */       this.languages = Collections.unmodifiableSet(languages);
/*     */     }
/*     */ 
/*     */     public boolean contains(String language)
/*     */     {
/*  92 */       return this.languages.contains(language);
/*     */     }
/*     */ 
/*     */     public String getAny()
/*     */     {
/*  97 */       return (String)this.languages.iterator().next();
/*     */     }
/*     */ 
/*     */     public Set<String> getLanguages() {
/* 101 */       return this.languages;
/*     */     }
/*     */ 
/*     */     public boolean isEmpty()
/*     */     {
/* 106 */       return this.languages.isEmpty();
/*     */     }
/*     */ 
/*     */     public boolean isSingleton()
/*     */     {
/* 111 */       return this.languages.size() == 1;
/*     */     }
/*     */ 
/*     */     public Languages.LanguageSet restrictTo(Languages.LanguageSet other)
/*     */     {
/* 116 */       if (other == Languages.NO_LANGUAGES)
/* 117 */         return other;
/* 118 */       if (other == Languages.ANY_LANGUAGE) {
/* 119 */         return this;
/*     */       }
/* 121 */       SomeLanguages sl = (SomeLanguages)other;
/* 122 */       if (sl.languages.containsAll(this.languages)) {
/* 123 */         return this;
/*     */       }
/* 125 */       Set ls = new HashSet(this.languages);
/* 126 */       ls.retainAll(sl.languages);
/* 127 */       return from(ls);
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 134 */       return "Languages(" + this.languages.toString() + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract class LanguageSet
/*     */   {
/*     */     public static LanguageSet from(Set<String> langs)
/*     */     {
/*  66 */       return langs.isEmpty() ? Languages.NO_LANGUAGES : new Languages.SomeLanguages(langs, null);
/*     */     }
/*     */ 
/*     */     public abstract boolean contains(String paramString);
/*     */ 
/*     */     public abstract String getAny();
/*     */ 
/*     */     public abstract boolean isEmpty();
/*     */ 
/*     */     public abstract boolean isSingleton();
/*     */ 
/*     */     public abstract LanguageSet restrictTo(LanguageSet paramLanguageSet);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.language.bm.Languages
 * JD-Core Version:    0.6.0
 */