/*     */ package org.apache.axis.components.compiler;
/*     */ 
/*     */ public class CompilerError
/*     */ {
/*     */   private boolean error;
/*     */   private int startline;
/*     */   private int startcolumn;
/*     */   private int endline;
/*     */   private int endcolumn;
/*     */   private String file;
/*     */   private String message;
/*     */ 
/*     */   public CompilerError(String file, boolean error, int startline, int startcolumn, int endline, int endcolumn, String message)
/*     */   {
/*  78 */     this.file = file;
/*  79 */     this.error = error;
/*  80 */     this.startline = startline;
/*  81 */     this.startcolumn = startcolumn;
/*  82 */     this.endline = endline;
/*  83 */     this.endcolumn = endcolumn;
/*  84 */     this.message = message;
/*     */   }
/*     */ 
/*     */   public CompilerError(String message)
/*     */   {
/*  93 */     this.message = message;
/*     */   }
/*     */ 
/*     */   public String getFile()
/*     */   {
/* 102 */     return this.file;
/*     */   }
/*     */ 
/*     */   public boolean isError()
/*     */   {
/* 111 */     return this.error;
/*     */   }
/*     */ 
/*     */   public int getStartLine()
/*     */   {
/* 120 */     return this.startline;
/*     */   }
/*     */ 
/*     */   public int getStartColumn()
/*     */   {
/* 131 */     return this.startcolumn;
/*     */   }
/*     */ 
/*     */   public int getEndLine()
/*     */   {
/* 140 */     return this.endline;
/*     */   }
/*     */ 
/*     */   public int getEndColumn()
/*     */   {
/* 151 */     return this.endcolumn;
/*     */   }
/*     */ 
/*     */   public String getMessage()
/*     */   {
/* 160 */     return this.message;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.compiler.CompilerError
 * JD-Core Version:    0.6.0
 */