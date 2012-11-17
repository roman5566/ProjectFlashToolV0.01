/*     */ package org.apache.axis.wsdl.toJava;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import javax.wsdl.Binding;
/*     */ import javax.wsdl.PortType;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.gen.Generator;
/*     */ import org.apache.axis.wsdl.symbolTable.BindingEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.PortTypeEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.SymbolTable;
/*     */ 
/*     */ public class JavaBindingWriter
/*     */   implements Generator
/*     */ {
/*  34 */   protected Generator stubWriter = null;
/*     */ 
/*  37 */   protected Generator skelWriter = null;
/*     */ 
/*  40 */   protected Generator implWriter = null;
/*     */ 
/*  43 */   protected Generator interfaceWriter = null;
/*     */   protected Emitter emitter;
/*     */   protected Binding binding;
/*     */   protected SymbolTable symbolTable;
/*  61 */   public static String INTERFACE_NAME = "interface name";
/*     */ 
/*     */   public JavaBindingWriter(Emitter emitter, Binding binding, SymbolTable symbolTable)
/*     */   {
/*  73 */     this.emitter = emitter;
/*  74 */     this.binding = binding;
/*  75 */     this.symbolTable = symbolTable;
/*     */   }
/*     */ 
/*     */   protected Generator getJavaInterfaceWriter(Emitter emitter, PortTypeEntry ptEntry, BindingEntry bEntry, SymbolTable st)
/*     */   {
/*  91 */     return new JavaInterfaceWriter(emitter, ptEntry, bEntry, st);
/*     */   }
/*     */ 
/*     */   protected Generator getJavaStubWriter(Emitter emitter, BindingEntry bEntry, SymbolTable st)
/*     */   {
/* 104 */     return new JavaStubWriter(emitter, bEntry, st);
/*     */   }
/*     */ 
/*     */   protected Generator getJavaSkelWriter(Emitter emitter, BindingEntry bEntry, SymbolTable st)
/*     */   {
/* 117 */     return new JavaSkelWriter(emitter, bEntry, st);
/*     */   }
/*     */ 
/*     */   protected Generator getJavaImplWriter(Emitter emitter, BindingEntry bEntry, SymbolTable st)
/*     */   {
/* 130 */     return new JavaImplWriter(emitter, bEntry, st);
/*     */   }
/*     */ 
/*     */   public void generate()
/*     */     throws IOException
/*     */   {
/* 140 */     setGenerators();
/* 141 */     postSetGenerators();
/*     */ 
/* 143 */     if (this.interfaceWriter != null) {
/* 144 */       this.interfaceWriter.generate();
/*     */     }
/*     */ 
/* 147 */     if (this.stubWriter != null) {
/* 148 */       this.stubWriter.generate();
/*     */     }
/*     */ 
/* 151 */     if (this.skelWriter != null) {
/* 152 */       this.skelWriter.generate();
/*     */     }
/*     */ 
/* 155 */     if (this.implWriter != null)
/* 156 */       this.implWriter.generate();
/*     */   }
/*     */ 
/*     */   protected void setGenerators()
/*     */   {
/* 168 */     BindingEntry bEntry = this.symbolTable.getBindingEntry(this.binding.getQName());
/*     */ 
/* 171 */     PortTypeEntry ptEntry = this.symbolTable.getPortTypeEntry(this.binding.getPortType().getQName());
/*     */ 
/* 174 */     if (ptEntry.isReferenced()) {
/* 175 */       this.interfaceWriter = getJavaInterfaceWriter(this.emitter, ptEntry, bEntry, this.symbolTable);
/*     */     }
/*     */ 
/* 179 */     if (bEntry.isReferenced())
/*     */     {
/* 182 */       this.stubWriter = getJavaStubWriter(this.emitter, bEntry, this.symbolTable);
/*     */ 
/* 185 */       if (this.emitter.isServerSide()) {
/* 186 */         if (this.emitter.isSkeletonWanted()) {
/* 187 */           this.skelWriter = getJavaSkelWriter(this.emitter, bEntry, this.symbolTable);
/*     */         }
/*     */ 
/* 192 */         String fileName = this.emitter.getImplementationClassName();
/* 193 */         if (fileName == null) {
/* 194 */           fileName = Utils.getJavaLocalName(bEntry.getName()) + "Impl.java";
/*     */         }
/*     */         else
/* 197 */           fileName = Utils.getJavaLocalName(fileName) + ".java";
/*     */         try
/*     */         {
/* 200 */           if (Utils.fileExists(fileName, this.binding.getQName().getNamespaceURI(), this.emitter.getNamespaces()))
/*     */           {
/* 203 */             if (!this.emitter.isQuiet()) {
/* 204 */               System.out.println(Messages.getMessage("wontOverwrite", fileName));
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/* 209 */             this.implWriter = getJavaImplWriter(this.emitter, bEntry, this.symbolTable);
/*     */           }
/*     */         }
/*     */         catch (IOException ioe) {
/* 213 */           System.err.println(Messages.getMessage("fileExistError00", fileName));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void postSetGenerators()
/*     */   {
/* 224 */     if (this.emitter.isDeploy()) {
/* 225 */       this.interfaceWriter = null;
/* 226 */       this.stubWriter = null;
/* 227 */       this.skelWriter = null;
/* 228 */       this.implWriter = null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.JavaBindingWriter
 * JD-Core Version:    0.6.0
 */