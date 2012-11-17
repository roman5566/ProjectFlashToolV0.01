/*     */ package org.apache.axis.wsdl.toJava;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Vector;
/*     */ import javax.wsdl.Binding;
/*     */ import javax.wsdl.Definition;
/*     */ import javax.wsdl.Import;
/*     */ import javax.wsdl.Message;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.gen.Generator;
/*     */ import org.apache.axis.wsdl.symbolTable.BindingEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.FaultInfo;
/*     */ import org.apache.axis.wsdl.symbolTable.MessageEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.SymbolTable;
/*     */ 
/*     */ public class JavaDefinitionWriter
/*     */   implements Generator
/*     */ {
/*     */   protected Emitter emitter;
/*     */   protected Definition definition;
/*     */   protected SymbolTable symbolTable;
/* 140 */   private HashSet importedFiles = new HashSet();
/*     */ 
/*     */   public JavaDefinitionWriter(Emitter emitter, Definition definition, SymbolTable symbolTable)
/*     */   {
/*  62 */     this.emitter = emitter;
/*  63 */     this.definition = definition;
/*  64 */     this.symbolTable = symbolTable;
/*     */   }
/*     */ 
/*     */   public void generate()
/*     */     throws IOException
/*     */   {
/*  73 */     writeFaults();
/*     */   }
/*     */ 
/*     */   protected void writeFaults()
/*     */     throws IOException
/*     */   {
/*  85 */     ArrayList faults = new ArrayList();
/*     */ 
/*  87 */     collectFaults(this.definition, faults);
/*     */ 
/*  90 */     HashSet generatedFaults = new HashSet();
/*     */ 
/*  93 */     Iterator fi = faults.iterator();
/*     */ 
/*  95 */     while (fi.hasNext()) {
/*  96 */       FaultInfo faultInfo = (FaultInfo)fi.next();
/*  97 */       Message message = faultInfo.getMessage();
/*  98 */       String name = Utils.getFullExceptionName(message, this.symbolTable);
/*     */ 
/* 101 */       if (generatedFaults.contains(name))
/*     */       {
/*     */         continue;
/*     */       }
/* 105 */       generatedFaults.add(name);
/*     */ 
/* 110 */       MessageEntry me = this.symbolTable.getMessageEntry(message.getQName());
/*     */ 
/* 112 */       boolean emitSimpleFault = true;
/*     */ 
/* 114 */       if (me != null) {
/* 115 */         Boolean complexTypeFault = (Boolean)me.getDynamicVar(JavaGeneratorFactory.COMPLEX_TYPE_FAULT);
/*     */ 
/* 118 */         if ((complexTypeFault != null) && (complexTypeFault.booleanValue()))
/*     */         {
/* 120 */           emitSimpleFault = false;
/*     */         }
/*     */       }
/*     */ 
/* 124 */       if (emitSimpleFault)
/*     */         try {
/* 126 */           JavaFaultWriter writer = new JavaFaultWriter(this.emitter, this.symbolTable, faultInfo);
/*     */ 
/* 130 */           writer.generate();
/*     */         } catch (DuplicateFileException dfe) {
/* 132 */           System.err.println(Messages.getMessage("fileExistError00", dfe.getFileName()));
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void collectFaults(Definition def, ArrayList faults)
/*     */     throws IOException
/*     */   {
/* 152 */     Map imports = def.getImports();
/* 153 */     Object[] importValues = imports.values().toArray();
/*     */ 
/* 155 */     for (int i = 0; i < importValues.length; i++) {
/* 156 */       Vector v = (Vector)importValues[i];
/*     */ 
/* 158 */       for (int j = 0; j < v.size(); j++) {
/* 159 */         Import imp = (Import)v.get(j);
/*     */ 
/* 161 */         if (!this.importedFiles.contains(imp.getLocationURI())) {
/* 162 */           this.importedFiles.add(imp.getLocationURI());
/*     */ 
/* 164 */           Definition importDef = imp.getDefinition();
/*     */ 
/* 166 */           if (importDef != null) {
/* 167 */             collectFaults(importDef, faults);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 174 */     Map bindings = def.getBindings();
/* 175 */     Iterator bindi = bindings.values().iterator();
/*     */ 
/* 177 */     while (bindi.hasNext()) {
/* 178 */       Binding binding = (Binding)bindi.next();
/* 179 */       BindingEntry entry = this.symbolTable.getBindingEntry(binding.getQName());
/*     */ 
/* 182 */       if (entry.isReferenced())
/*     */       {
/* 186 */         Map faultMap = entry.getFaults();
/* 187 */         Iterator it = faultMap.values().iterator();
/*     */ 
/* 189 */         while (it.hasNext()) {
/* 190 */           ArrayList list = (ArrayList)it.next();
/*     */ 
/* 193 */           faults.addAll(list);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.JavaDefinitionWriter
 * JD-Core Version:    0.6.0
 */