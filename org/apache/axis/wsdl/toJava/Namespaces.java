/*     */ package org.apache.axis.wsdl.toJava;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ 
/*     */ public class Namespaces extends HashMap
/*     */ {
/*     */   private String root;
/*  37 */   private String defaultPackage = null;
/*     */ 
/*  40 */   private static final char[] pkgSeparators = { '.', ':' };
/*     */ 
/*  43 */   private static final char javaPkgSeparator = pkgSeparators[0];
/*     */ 
/*  46 */   private Map pkg2NamespacesMap = new HashMap();
/*     */ 
/*     */   private static String normalizePackageName(String pkg, char separator)
/*     */   {
/*  57 */     for (int i = 0; i < pkgSeparators.length; i++) {
/*  58 */       pkg = pkg.replace(pkgSeparators[i], separator);
/*     */     }
/*     */ 
/*  61 */     return pkg;
/*     */   }
/*     */ 
/*     */   public Namespaces(String root)
/*     */   {
/*  73 */     this.root = root;
/*     */   }
/*     */ 
/*     */   private Namespaces(Namespaces clone)
/*     */   {
/*  83 */     super(clone);
/*     */ 
/*  85 */     this.root = clone.root;
/*  86 */     this.defaultPackage = clone.defaultPackage;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*  95 */     return new Namespaces(this);
/*     */   }
/*     */ 
/*     */   public String getCreate(String key)
/*     */   {
/* 106 */     return getCreate(key, true);
/*     */   }
/*     */ 
/*     */   String getCreate(String key, boolean create)
/*     */   {
/* 119 */     if (this.defaultPackage != null) {
/* 120 */       put(key, this.defaultPackage);
/* 121 */       return this.defaultPackage;
/*     */     }
/*     */ 
/* 124 */     String value = (String)super.get(key);
/*     */ 
/* 126 */     if ((value == null) && (create)) {
/* 127 */       value = normalizePackageName(Utils.makePackageName(key), javaPkgSeparator);
/*     */ 
/* 130 */       put(key, value);
/*     */     }
/*     */ 
/* 133 */     return value;
/*     */   }
/*     */ 
/*     */   public String getAsDir(String key)
/*     */   {
/* 145 */     if (this.defaultPackage != null) {
/* 146 */       return toDir(this.defaultPackage);
/*     */     }
/*     */ 
/* 149 */     String pkg = (String)get(key);
/*     */ 
/* 151 */     return toDir(pkg);
/*     */   }
/*     */ 
/*     */   public String toDir(String pkg)
/*     */   {
/* 163 */     String dir = null;
/*     */ 
/* 165 */     if (pkg != null) {
/* 166 */       pkg = normalizePackageName(pkg, File.separatorChar);
/*     */     }
/*     */ 
/* 169 */     if (this.root == null)
/* 170 */       dir = pkg;
/*     */     else {
/* 172 */       dir = this.root + File.separatorChar + pkg;
/*     */     }
/*     */ 
/* 175 */     return dir + File.separatorChar;
/*     */   }
/*     */ 
/*     */   public void putAll(Map map)
/*     */   {
/* 188 */     Iterator i = map.entrySet().iterator();
/*     */ 
/* 190 */     while (i.hasNext()) {
/* 191 */       Map.Entry entry = (Map.Entry)i.next();
/* 192 */       Object key = entry.getKey();
/* 193 */       String pkg = (String)entry.getValue();
/*     */ 
/* 195 */       pkg = javify(pkg);
/*     */ 
/* 197 */       put(key, pkg);
/*     */     }
/*     */   }
/*     */ 
/*     */   private String javify(String pkg)
/*     */   {
/* 210 */     StringTokenizer st = new StringTokenizer(pkg, ".");
/*     */ 
/* 212 */     pkg = "";
/*     */ 
/* 214 */     while (st.hasMoreTokens()) {
/* 215 */       String token = st.nextToken();
/*     */ 
/* 217 */       if (JavaUtils.isJavaKeyword(token)) {
/* 218 */         token = JavaUtils.makeNonJavaKeyword(token);
/*     */       }
/*     */ 
/* 221 */       pkg = pkg + token;
/*     */ 
/* 223 */       if (st.hasMoreTokens()) {
/* 224 */         pkg = pkg + '.';
/*     */       }
/*     */     }
/*     */ 
/* 228 */     return pkg;
/*     */   }
/*     */ 
/*     */   public void mkdir(String pkg)
/*     */   {
/* 238 */     String pkgDirString = toDir(pkg);
/* 239 */     File packageDir = new File(pkgDirString);
/*     */ 
/* 241 */     packageDir.mkdirs();
/*     */   }
/*     */ 
/*     */   public void setDefaultPackage(String defaultPackage)
/*     */   {
/* 250 */     this.defaultPackage = defaultPackage;
/*     */   }
/*     */ 
/*     */   public Object put(Object key, Object value)
/*     */   {
/* 255 */     Vector v = null;
/* 256 */     if (!this.pkg2NamespacesMap.containsKey(value))
/* 257 */       v = new Vector();
/*     */     else {
/* 259 */       v = (Vector)this.pkg2NamespacesMap.get(value);
/*     */     }
/*     */ 
/* 262 */     if (!v.contains(key)) {
/* 263 */       v.add(key);
/*     */     }
/* 265 */     this.pkg2NamespacesMap.put(value, v);
/*     */ 
/* 267 */     return super.put(key, value);
/*     */   }
/*     */ 
/*     */   public Map getPkg2NamespacesMap() {
/* 271 */     return this.pkg2NamespacesMap;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.Namespaces
 * JD-Core Version:    0.6.0
 */