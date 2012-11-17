/*     */ package org.apache.axis.components.compiler;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public abstract class AbstractCompiler
/*     */   implements Compiler
/*     */ {
/*  37 */   protected ArrayList fileList = new ArrayList();
/*     */   protected String srcDir;
/*     */   protected String destDir;
/*     */   protected String classpath;
/*  58 */   protected String encoding = null;
/*     */   protected InputStream errors;
/*     */ 
/*     */   public void addFile(String file)
/*     */   {
/*  71 */     this.fileList.add(file);
/*     */   }
/*     */ 
/*     */   public void setSource(String srcDir)
/*     */   {
/*  80 */     this.srcDir = srcDir;
/*     */   }
/*     */ 
/*     */   public void setDestination(String destDir)
/*     */   {
/*  90 */     this.destDir = destDir;
/*     */   }
/*     */ 
/*     */   public void setClasspath(String classpath)
/*     */   {
/*  99 */     this.classpath = classpath;
/*     */   }
/*     */ 
/*     */   public void setEncoding(String encoding)
/*     */   {
/* 110 */     this.encoding = encoding;
/*     */   }
/*     */ 
/*     */   public List getErrors()
/*     */     throws IOException
/*     */   {
/* 120 */     return parseStream(new BufferedReader(new InputStreamReader(this.errors)));
/*     */   }
/*     */ 
/*     */   protected abstract List parseStream(BufferedReader paramBufferedReader)
/*     */     throws IOException;
/*     */ 
/*     */   protected List fillArguments(List arguments)
/*     */   {
/* 142 */     arguments.add("-d");
/* 143 */     arguments.add(this.destDir);
/*     */ 
/* 146 */     arguments.add("-classpath");
/* 147 */     arguments.add(this.classpath);
/*     */ 
/* 150 */     if (this.srcDir != null) {
/* 151 */       arguments.add("-sourcepath");
/* 152 */       arguments.add(this.srcDir);
/*     */     }
/*     */ 
/* 156 */     arguments.add("-O");
/*     */ 
/* 159 */     arguments.add("-g");
/*     */ 
/* 162 */     if (this.encoding != null) {
/* 163 */       arguments.add("-encoding");
/* 164 */       arguments.add(this.encoding);
/*     */     }
/*     */ 
/* 167 */     return arguments;
/*     */   }
/*     */ 
/*     */   protected String[] toStringArray(List arguments)
/*     */   {
/* 178 */     String[] args = new String[arguments.size() + this.fileList.size()];
/*     */ 
/* 180 */     for (int i = 0; i < arguments.size(); i++) {
/* 181 */       args[i] = ((String)arguments.get(i));
/*     */     }
/*     */ 
/* 184 */     for (int j = 0; j < this.fileList.size(); j++) {
/* 185 */       args[i] = ((String)this.fileList.get(j));
/*     */ 
/* 184 */       i++;
/*     */     }
/*     */ 
/* 187 */     return args;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.compiler.AbstractCompiler
 * JD-Core Version:    0.6.0
 */