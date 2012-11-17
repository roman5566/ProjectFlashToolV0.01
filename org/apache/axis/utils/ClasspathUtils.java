/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileFilter;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import java.net.URLDecoder;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.jar.Attributes;
/*     */ import java.util.jar.Attributes.Name;
/*     */ import java.util.jar.JarFile;
/*     */ import java.util.jar.JarInputStream;
/*     */ import java.util.jar.Manifest;
/*     */ import org.apache.axis.AxisProperties;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.transport.http.HTTPConstants;
/*     */ 
/*     */ public class ClasspathUtils
/*     */ {
/*     */   public static String expandDirs(String dirPaths)
/*     */   {
/*  57 */     StringTokenizer st = new StringTokenizer(dirPaths, File.pathSeparator);
/*  58 */     StringBuffer buffer = new StringBuffer();
/*  59 */     while (st.hasMoreTokens()) {
/*  60 */       String d = st.nextToken();
/*  61 */       File dir = new File(d);
/*  62 */       if (dir.isDirectory()) {
/*  63 */         File[] files = dir.listFiles(new JavaArchiveFilter(null));
/*  64 */         for (int i = 0; i < files.length; i++) {
/*  65 */           buffer.append(files[i]).append(File.pathSeparator);
/*     */         }
/*     */       }
/*     */     }
/*  69 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public static boolean isJar(InputStream is)
/*     */   {
/*     */     try
/*     */     {
/*  79 */       JarInputStream jis = new JarInputStream(is);
/*  80 */       if (jis.getNextEntry() != null)
/*  81 */         return true;
/*     */     }
/*     */     catch (IOException ioe) {
/*     */     }
/*  85 */     return false;
/*     */   }
/*     */ 
/*     */   public static String getDefaultClasspath(MessageContext msgContext)
/*     */   {
/*  94 */     StringBuffer classpath = new StringBuffer();
/*  95 */     ClassLoader cl = Thread.currentThread().getContextClassLoader();
/*  96 */     fillClassPath(cl, classpath);
/*     */ 
/* 101 */     String webBase = (String)msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLETLOCATION);
/* 102 */     if (webBase != null) {
/* 103 */       classpath.append(webBase + File.separatorChar + "classes" + File.pathSeparatorChar);
/*     */       try
/*     */       {
/* 106 */         String libBase = webBase + File.separatorChar + "lib";
/* 107 */         File libDir = new File(libBase);
/* 108 */         String[] jarFiles = libDir.list();
/* 109 */         for (int i = 0; i < jarFiles.length; i++) {
/* 110 */           String jarFile = jarFiles[i];
/* 111 */           if (jarFile.endsWith(".jar")) {
/* 112 */             classpath.append(libBase + File.separatorChar + jarFile + File.pathSeparatorChar);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 124 */     getClassPathFromDirectoryProperty(classpath, "axis.ext.dirs");
/*     */ 
/* 127 */     getClassPathFromProperty(classpath, "org.apache.catalina.jsp_classpath");
/*     */ 
/* 130 */     getClassPathFromProperty(classpath, "ws.ext.dirs");
/* 131 */     getClassPathFromProperty(classpath, "com.ibm.websphere.servlet.application.classpath");
/*     */ 
/* 134 */     getClassPathFromProperty(classpath, "java.class.path");
/*     */ 
/* 137 */     getClassPathFromDirectoryProperty(classpath, "java.ext.dirs");
/*     */ 
/* 140 */     getClassPathFromProperty(classpath, "sun.boot.class.path");
/* 141 */     return classpath.toString();
/*     */   }
/*     */ 
/*     */   private static void getClassPathFromDirectoryProperty(StringBuffer classpath, String property)
/*     */   {
/* 150 */     String dirs = AxisProperties.getProperty(property);
/* 151 */     String path = null;
/*     */     try {
/* 153 */       path = expandDirs(dirs);
/*     */     }
/*     */     catch (Exception e) {
/*     */     }
/* 157 */     if (path != null) {
/* 158 */       classpath.append(path);
/* 159 */       classpath.append(File.pathSeparatorChar);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void getClassPathFromProperty(StringBuffer classpath, String property)
/*     */   {
/* 169 */     String path = AxisProperties.getProperty(property);
/* 170 */     if (path != null) {
/* 171 */       classpath.append(path);
/* 172 */       classpath.append(File.pathSeparatorChar);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void fillClassPath(ClassLoader cl, StringBuffer classpath)
/*     */   {
/* 182 */     while (cl != null) {
/* 183 */       if ((cl instanceof URLClassLoader)) {
/* 184 */         URL[] urls = ((URLClassLoader)cl).getURLs();
/* 185 */         for (int i = 0; (urls != null) && (i < urls.length); i++) {
/* 186 */           String path = urls[i].getPath();
/*     */ 
/* 188 */           if ((path.length() >= 3) && (path.charAt(0) == '/') && (path.charAt(2) == ':'))
/* 189 */             path = path.substring(1);
/* 190 */           classpath.append(URLDecoder.decode(path));
/* 191 */           classpath.append(File.pathSeparatorChar);
/*     */ 
/* 194 */           File file = new File(urls[i].getFile());
/* 195 */           if (file.isFile()) {
/* 196 */             FileInputStream fis = null;
/*     */             try {
/* 198 */               fis = new FileInputStream(file);
/* 199 */               if (isJar(fis)) {
/* 200 */                 JarFile jar = new JarFile(file);
/* 201 */                 Manifest manifest = jar.getManifest();
/* 202 */                 if (manifest != null) {
/* 203 */                   Attributes attributes = manifest.getMainAttributes();
/* 204 */                   if (attributes != null) {
/* 205 */                     String s = attributes.getValue(Attributes.Name.CLASS_PATH);
/* 206 */                     String base = file.getParent();
/* 207 */                     if (s != null) {
/* 208 */                       StringTokenizer st = new StringTokenizer(s, " ");
/* 209 */                       while (st.hasMoreTokens()) {
/* 210 */                         String t = st.nextToken();
/* 211 */                         classpath.append(base + File.separatorChar + t);
/* 212 */                         classpath.append(File.pathSeparatorChar);
/*     */                       }
/*     */                     }
/*     */                   }
/*     */                 }
/*     */               }
/*     */             } catch (IOException ioe) {
/*     */             } finally {
/* 220 */               if (fis != null)
/*     */                 try {
/* 222 */                   fis.close();
/*     */                 }
/*     */                 catch (IOException ioe2) {
/*     */                 }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 230 */       cl = cl.getParent();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class JavaArchiveFilter implements FileFilter {
/*     */     private JavaArchiveFilter() {
/*     */     }
/*     */ 
/*     */     public boolean accept(File file) {
/* 239 */       String name = file.getName().toLowerCase();
/* 240 */       return (name.endsWith(".jar")) || (name.endsWith(".zip"));
/*     */     }
/*     */ 
/*     */     JavaArchiveFilter(ClasspathUtils.1 x0)
/*     */     {
/* 237 */       this();
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.ClasspathUtils
 * JD-Core Version:    0.6.0
 */