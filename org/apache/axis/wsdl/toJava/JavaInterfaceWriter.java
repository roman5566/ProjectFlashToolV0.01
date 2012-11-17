/*     */ package org.apache.axis.wsdl.toJava;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.wsdl.Operation;
/*     */ import javax.wsdl.PortType;
/*     */ import org.apache.axis.wsdl.symbolTable.BindingEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.Parameters;
/*     */ import org.apache.axis.wsdl.symbolTable.PortTypeEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.SymbolTable;
/*     */ 
/*     */ public class JavaInterfaceWriter extends JavaClassWriter
/*     */ {
/*     */   protected PortType portType;
/*     */   protected BindingEntry bEntry;
/*     */ 
/*     */   protected JavaInterfaceWriter(Emitter emitter, PortTypeEntry ptEntry, BindingEntry bEntry, SymbolTable symbolTable)
/*     */   {
/*  53 */     super(emitter, (String)bEntry.getDynamicVar(JavaBindingWriter.INTERFACE_NAME), "interface");
/*     */ 
/*  57 */     this.portType = ptEntry.getPortType();
/*  58 */     this.bEntry = bEntry;
/*     */   }
/*     */ 
/*     */   public void generate()
/*     */     throws IOException
/*     */   {
/*  69 */     String fqClass = getPackage() + "." + getClassName();
/*     */ 
/*  72 */     if (!this.emitter.getGeneratedFileInfo().getClassNames().contains(fqClass))
/*  73 */       super.generate();
/*     */   }
/*     */ 
/*     */   protected String getClassText()
/*     */   {
/*  83 */     return "interface ";
/*     */   }
/*     */ 
/*     */   protected String getExtendsText()
/*     */   {
/*  92 */     return "extends java.rmi.Remote ";
/*     */   }
/*     */ 
/*     */   protected void writeFileBody(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/* 103 */     Iterator operations = this.portType.getOperations().iterator();
/*     */ 
/* 105 */     while (operations.hasNext()) {
/* 106 */       Operation operation = (Operation)operations.next();
/*     */ 
/* 108 */       writeOperation(pw, operation);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void writeOperation(PrintWriter pw, Operation operation)
/*     */     throws IOException
/*     */   {
/* 122 */     writeComment(pw, operation.getDocumentationElement(), true);
/*     */ 
/* 124 */     Parameters parms = this.bEntry.getParameters(operation);
/*     */ 
/* 126 */     pw.println(parms.signature + ";");
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.JavaInterfaceWriter
 * JD-Core Version:    0.6.0
 */