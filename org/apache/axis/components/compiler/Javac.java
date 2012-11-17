/*     */ package org.apache.axis.components.compiler;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.StringTokenizer;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.utils.ClassUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class Javac extends AbstractCompiler
/*     */ {
/*  52 */   protected static Log log = LogFactory.getLog(Javac.class.getName());
/*     */   public static final String CLASSIC_CLASS = "sun.tools.javac.Main";
/*     */   public static final String MODERN_CLASS = "com.sun.tools.javac.main.Main";
/*  58 */   private boolean modern = false;
/*     */ 
/*     */   public Javac() {
/*  61 */     ClassLoader cl = getClassLoader();
/*     */     try {
/*  63 */       ClassUtils.forName("com.sun.tools.javac.main.Main", true, cl);
/*  64 */       this.modern = true;
/*     */     } catch (ClassNotFoundException e) {
/*  66 */       log.debug(Messages.getMessage("noModernCompiler"));
/*     */       try {
/*  68 */         ClassUtils.forName("sun.tools.javac.Main", true, cl);
/*  69 */         this.modern = false;
/*     */       } catch (Exception ex) {
/*  71 */         log.error(Messages.getMessage("noCompiler00"), ex);
/*  72 */         throw new RuntimeException(Messages.getMessage("noCompiler00"));
/*     */       }
/*     */     }
/*  75 */     log.debug(Messages.getMessage("compilerClass", this.modern ? "com.sun.tools.javac.main.Main" : "sun.tools.javac.Main"));
/*     */   }
/*     */ 
/*     */   private ClassLoader getClassLoader()
/*     */   {
/*  81 */     ClassLoader cl = Thread.currentThread().getContextClassLoader();
/*     */ 
/*  83 */     URL toolsURL = null;
/*  84 */     String tools = System.getProperty("java.home");
/*  85 */     if (tools != null) {
/*  86 */       File f = new File(tools + "/../lib/tools.jar");
/*  87 */       if (f.exists())
/*     */         try {
/*  89 */           toolsURL = f.toURL();
/*  90 */           cl = new URLClassLoader(new URL[] { toolsURL }, cl);
/*     */         }
/*     */         catch (MalformedURLException e)
/*     */         {
/*     */         }
/*     */     }
/*  96 */     return cl;
/*     */   }
/*     */ 
/*     */   public boolean compile()
/*     */     throws IOException
/*     */   {
/* 105 */     ByteArrayOutputStream err = new ByteArrayOutputStream();
/* 106 */     boolean result = false;
/*     */     try
/*     */     {
/* 110 */       Class c = ClassUtils.forName(this.modern ? "com.sun.tools.javac.main.Main" : "sun.tools.javac.Main", true, getClassLoader());
/*     */       Object compiler;
/*     */       Object compiler;
/* 116 */       if (this.modern) {
/* 117 */         PrintWriter pw = new PrintWriter(new OutputStreamWriter(err));
/* 118 */         Constructor cons = c.getConstructor(new Class[] { String.class, PrintWriter.class });
/*     */ 
/* 122 */         compiler = cons.newInstance(new Object[] { "javac", pw });
/*     */       }
/*     */       else {
/* 125 */         Constructor cons = c.getConstructor(new Class[] { OutputStream.class, String.class });
/*     */ 
/* 128 */         compiler = cons.newInstance(new Object[] { err, "javac" });
/*     */       }
/*     */ 
/* 133 */       Method compile = c.getMethod("compile", new Class[] { new String[0].getClass() });
/*     */ 
/* 136 */       if (this.modern) {
/* 137 */         int compilationResult = ((Integer)compile.invoke(compiler, new Object[] { toStringArray(fillArguments(new ArrayList())) })).intValue();
/*     */ 
/* 143 */         result = compilationResult == 0;
/* 144 */         log.debug("Compilation Returned: " + Integer.toString(compilationResult));
/*     */       }
/*     */       else
/*     */       {
/* 148 */         Boolean ok = (Boolean)compile.invoke(compiler, new Object[] { toStringArray(fillArguments(new ArrayList())) });
/*     */ 
/* 152 */         result = ok.booleanValue();
/*     */       }
/*     */     } catch (Exception cnfe) {
/* 155 */       log.error(Messages.getMessage("noCompiler00"), cnfe);
/* 156 */       throw new RuntimeException(Messages.getMessage("noCompiler00"));
/*     */     }
/*     */ 
/* 159 */     this.errors = new ByteArrayInputStream(err.toByteArray());
/* 160 */     return result;
/*     */   }
/*     */ 
/*     */   protected List parseStream(BufferedReader input)
/*     */     throws IOException
/*     */   {
/* 172 */     if (this.modern) {
/* 173 */       return parseModernStream(input);
/*     */     }
/* 175 */     return parseClassicStream(input);
/*     */   }
/*     */ 
/*     */   protected List parseModernStream(BufferedReader input)
/*     */     throws IOException
/*     */   {
/* 188 */     List errors = new ArrayList();
/* 189 */     String line = null;
/* 190 */     StringBuffer buffer = null;
/*     */     while (true)
/*     */     {
/* 194 */       buffer = new StringBuffer();
/*     */       do
/*     */       {
/* 198 */         if ((line = input.readLine()) == null)
/*     */         {
/* 200 */           if (buffer.length() > 0)
/*     */           {
/* 202 */             errors.add(new CompilerError("\n" + buffer.toString()));
/*     */           }
/* 204 */           return errors;
/*     */         }
/* 206 */         log.debug(line);
/* 207 */         buffer.append(line);
/* 208 */         buffer.append('\n');
/* 209 */       }while (!line.endsWith("^"));
/*     */ 
/* 212 */       errors.add(parseModernError(buffer.toString()));
/*     */     }
/*     */   }
/*     */ 
/*     */   private CompilerError parseModernError(String error)
/*     */   {
/* 223 */     StringTokenizer tokens = new StringTokenizer(error, ":");
/*     */     try {
/* 225 */       String file = tokens.nextToken();
/* 226 */       if (file.length() == 1) file = file + ":" + tokens.nextToken();
/* 227 */       int line = Integer.parseInt(tokens.nextToken());
/*     */ 
/* 229 */       String message = tokens.nextToken("\n").substring(1);
/* 230 */       String context = tokens.nextToken("\n");
/* 231 */       String pointer = tokens.nextToken("\n");
/* 232 */       int startcolumn = pointer.indexOf("^");
/* 233 */       int endcolumn = context.indexOf(" ", startcolumn);
/* 234 */       if (endcolumn == -1) endcolumn = context.length();
/* 235 */       return new CompilerError(file, false, line, startcolumn, line, endcolumn, message);
/*     */     } catch (NoSuchElementException nse) {
/* 237 */       return new CompilerError(Messages.getMessage("noMoreTokens", error)); } catch (Exception nse) {
/*     */     }
/* 239 */     return new CompilerError(Messages.getMessage("cantParse", error));
/*     */   }
/*     */ 
/*     */   protected List parseClassicStream(BufferedReader input)
/*     */     throws IOException
/*     */   {
/* 252 */     List errors = null;
/* 253 */     String line = null;
/* 254 */     StringBuffer buffer = null;
/*     */     while (true)
/*     */     {
/* 258 */       buffer = new StringBuffer();
/*     */ 
/* 261 */       for (int i = 0; i < 3; i++) {
/* 262 */         if ((line = input.readLine()) == null) return errors;
/* 263 */         log.debug(line);
/* 264 */         buffer.append(line);
/* 265 */         buffer.append('\n');
/*     */       }
/*     */ 
/* 269 */       if (errors == null) errors = new ArrayList();
/*     */ 
/* 272 */       errors.add(parseClassicError(buffer.toString()));
/*     */     }
/*     */   }
/*     */ 
/*     */   private CompilerError parseClassicError(String error)
/*     */   {
/* 283 */     StringTokenizer tokens = new StringTokenizer(error, ":");
/*     */     try {
/* 285 */       String file = tokens.nextToken();
/* 286 */       if (file.length() == 1) {
/* 287 */         file = file + ":" + tokens.nextToken();
/*     */       }
/*     */ 
/* 290 */       int line = Integer.parseInt(tokens.nextToken());
/*     */ 
/* 292 */       String last = tokens.nextToken();
/*     */ 
/* 294 */       while (tokens.hasMoreElements()) {
/* 295 */         last = last + tokens.nextToken();
/*     */       }
/* 297 */       tokens = new StringTokenizer(last.trim(), "\n");
/* 298 */       String message = tokens.nextToken();
/* 299 */       String context = tokens.nextToken();
/* 300 */       String pointer = tokens.nextToken();
/* 301 */       int startcolumn = pointer.indexOf("^");
/* 302 */       int endcolumn = context.indexOf(" ", startcolumn);
/* 303 */       if (endcolumn == -1) endcolumn = context.length();
/*     */ 
/* 305 */       return new CompilerError(this.srcDir + File.separator + file, true, line, startcolumn, line, endcolumn, message);
/*     */     }
/*     */     catch (NoSuchElementException nse) {
/* 308 */       return new CompilerError(Messages.getMessage("noMoreTokens", error));
/*     */     } catch (Exception nse) {
/*     */     }
/* 311 */     return new CompilerError(Messages.getMessage("cantParse", error));
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 316 */     return Messages.getMessage("sunJavac");
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.compiler.Javac
 * JD-Core Version:    0.6.0
 */