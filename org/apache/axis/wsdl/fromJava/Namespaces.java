/*     */ package org.apache.axis.wsdl.fromJava;
/*     */ 
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class Namespaces extends HashMap
/*     */ {
/*  33 */   private int prefixCount = 1;
/*     */ 
/*  36 */   private HashMap namespacePrefixMap = null;
/*     */ 
/*     */   public Namespaces()
/*     */   {
/*  45 */     this.namespacePrefixMap = new HashMap();
/*     */   }
/*     */ 
/*     */   public String getCreate(String key)
/*     */   {
/*  57 */     Object value = super.get(key);
/*     */ 
/*  59 */     if (value == null) {
/*  60 */       value = makeNamespaceFromPackageName(key);
/*     */ 
/*  62 */       put(key, value, null);
/*     */     }
/*     */ 
/*  65 */     return (String)value;
/*     */   }
/*     */ 
/*     */   public String getCreate(String key, String prefix)
/*     */   {
/*  78 */     Object value = super.get(key);
/*     */ 
/*  80 */     if (value == null) {
/*  81 */       value = makeNamespaceFromPackageName(key);
/*     */ 
/*  83 */       put(key, value, prefix);
/*     */     }
/*     */ 
/*  86 */     return (String)value;
/*     */   }
/*     */ 
/*     */   public Object put(Object key, Object value, String prefix)
/*     */   {
/* 101 */     if (prefix != null)
/* 102 */       this.namespacePrefixMap.put(value, prefix);
/*     */     else {
/* 104 */       getCreatePrefix((String)value);
/*     */     }
/*     */ 
/* 107 */     return super.put(key, value);
/*     */   }
/*     */ 
/*     */   public void putAll(Map map)
/*     */   {
/* 119 */     Iterator i = map.entrySet().iterator();
/*     */ 
/* 121 */     while (i.hasNext()) {
/* 122 */       Map.Entry entry = (Map.Entry)i.next();
/*     */ 
/* 124 */       put(entry.getKey(), entry.getValue(), null);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getCreatePrefix(String namespace)
/*     */   {
/* 136 */     if (this.namespacePrefixMap.get(namespace) == null) {
/* 137 */       this.namespacePrefixMap.put(namespace, "tns" + this.prefixCount++);
/*     */     }
/*     */ 
/* 140 */     return (String)this.namespacePrefixMap.get(namespace);
/*     */   }
/*     */ 
/*     */   public void putPrefix(String namespace, String prefix)
/*     */   {
/* 150 */     this.namespacePrefixMap.put(namespace, prefix);
/*     */   }
/*     */ 
/*     */   public void putAllPrefix(Map map)
/*     */   {
/* 161 */     Iterator i = map.entrySet().iterator();
/*     */ 
/* 163 */     while (i.hasNext()) {
/* 164 */       Map.Entry entry = (Map.Entry)i.next();
/*     */ 
/* 166 */       put(entry.getKey(), entry.getValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String makeNamespace(String clsName)
/*     */   {
/* 178 */     return makeNamespace(clsName, "http");
/*     */   }
/*     */ 
/*     */   public static String makeNamespace(String clsName, String protocol)
/*     */   {
/* 191 */     if (clsName.startsWith("[L")) {
/* 192 */       clsName = clsName.substring(2, clsName.length() - 1);
/*     */     }
/*     */ 
/* 195 */     if (clsName.lastIndexOf('.') == -1) {
/* 196 */       return protocol + "://" + "DefaultNamespace";
/*     */     }
/*     */ 
/* 199 */     String packageName = clsName.substring(0, clsName.lastIndexOf('.'));
/*     */ 
/* 201 */     return makeNamespaceFromPackageName(packageName, protocol);
/*     */   }
/*     */ 
/*     */   public static String getPackage(String namespace)
/*     */   {
/*     */     try
/*     */     {
/* 211 */       URL url = new URL(namespace);
/* 212 */       StringTokenizer st = new StringTokenizer(url.getHost(), ".");
/* 213 */       String[] words = new String[st.countTokens()];
/* 214 */       for (int i = 0; i < words.length; i++) {
/* 215 */         words[i] = st.nextToken();
/*     */       }
/* 217 */       StringBuffer sb = new StringBuffer(80);
/* 218 */       for (int i = words.length - 1; i >= 0; i--) {
/* 219 */         String word = words[i];
/*     */ 
/* 221 */         if (i != words.length - 1) {
/* 222 */           sb.append('.');
/*     */         }
/* 224 */         sb.append(word);
/*     */       }
/* 226 */       String pkg = sb.toString();
/* 227 */       if (pkg.equals("DefaultNamespace")) {
/* 228 */         return "";
/*     */       }
/* 230 */       return pkg;
/*     */     } catch (MalformedURLException e) {
/*     */     }
/* 233 */     return null;
/*     */   }
/*     */ 
/*     */   private static String makeNamespaceFromPackageName(String packageName)
/*     */   {
/* 243 */     return makeNamespaceFromPackageName(packageName, "http");
/*     */   }
/*     */ 
/*     */   private static String makeNamespaceFromPackageName(String packageName, String protocol)
/*     */   {
/* 256 */     if ((packageName == null) || (packageName.equals(""))) {
/* 257 */       return protocol + "://" + "DefaultNamespace";
/*     */     }
/*     */ 
/* 260 */     StringTokenizer st = new StringTokenizer(packageName, ".");
/* 261 */     String[] words = new String[st.countTokens()];
/*     */ 
/* 263 */     for (int i = 0; i < words.length; i++) {
/* 264 */       words[i] = st.nextToken();
/*     */     }
/*     */ 
/* 267 */     StringBuffer sb = new StringBuffer(80);
/*     */ 
/* 269 */     for (int i = words.length - 1; i >= 0; i--) {
/* 270 */       String word = words[i];
/*     */ 
/* 273 */       if (i != words.length - 1) {
/* 274 */         sb.append('.');
/*     */       }
/*     */ 
/* 277 */       sb.append(word);
/*     */     }
/*     */ 
/* 280 */     return protocol + "://" + sb.toString();
/*     */   }
/*     */ 
/*     */   public Iterator getNamespaces()
/*     */   {
/* 289 */     return this.namespacePrefixMap.keySet().iterator();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.fromJava.Namespaces
 * JD-Core Version:    0.6.0
 */