/*     */ package org.apache.axis.wsdl.toJava;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import javax.wsdl.Service;
/*     */ import org.apache.axis.wsdl.gen.Generator;
/*     */ import org.apache.axis.wsdl.symbolTable.ServiceEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.SymbolTable;
/*     */ 
/*     */ public class JavaServiceWriter
/*     */   implements Generator
/*     */ {
/*  32 */   protected Generator serviceIfaceWriter = null;
/*     */ 
/*  35 */   protected Generator serviceImplWriter = null;
/*     */ 
/*  38 */   protected Generator testCaseWriter = null;
/*     */   public static final String PORT_NAME = "port name";
/*     */   protected Emitter emitter;
/*     */   protected Service service;
/*     */   protected SymbolTable symbolTable;
/*     */ 
/*     */   public JavaServiceWriter(Emitter emitter, Service service, SymbolTable symbolTable)
/*     */   {
/*  61 */     this.emitter = emitter;
/*  62 */     this.service = service;
/*  63 */     this.symbolTable = symbolTable;
/*     */   }
/*     */ 
/*     */   protected void setGenerators()
/*     */   {
/*  73 */     ServiceEntry sEntry = this.symbolTable.getServiceEntry(this.service.getQName());
/*     */ 
/*  75 */     if (sEntry.isReferenced()) {
/*  76 */       this.serviceIfaceWriter = new JavaServiceIfaceWriter(this.emitter, sEntry, this.symbolTable);
/*     */ 
/*  78 */       this.serviceImplWriter = new JavaServiceImplWriter(this.emitter, sEntry, this.symbolTable);
/*     */ 
/*  81 */       if (this.emitter.isTestCaseWanted())
/*  82 */         this.testCaseWriter = new JavaTestCaseWriter(this.emitter, sEntry, this.symbolTable);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void postSetGenerators()
/*     */   {
/*  92 */     if (this.emitter.isDeploy()) {
/*  93 */       this.serviceIfaceWriter = null;
/*  94 */       this.serviceImplWriter = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void generate()
/*     */     throws IOException
/*     */   {
/* 104 */     setGenerators();
/* 105 */     postSetGenerators();
/*     */ 
/* 107 */     if (this.serviceIfaceWriter != null) {
/* 108 */       this.serviceIfaceWriter.generate();
/*     */     }
/*     */ 
/* 111 */     if (this.serviceImplWriter != null) {
/* 112 */       this.serviceImplWriter.generate();
/*     */     }
/*     */ 
/* 115 */     if (this.testCaseWriter != null)
/* 116 */       this.testCaseWriter.generate();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.JavaServiceWriter
 * JD-Core Version:    0.6.0
 */