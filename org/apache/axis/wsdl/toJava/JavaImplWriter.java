/*     */ package org.apache.axis.wsdl.toJava;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import javax.wsdl.Binding;
/*     */ import javax.wsdl.BindingOperation;
/*     */ import javax.wsdl.Operation;
/*     */ import javax.wsdl.OperationType;
/*     */ import javax.xml.rpc.holders.BooleanHolder;
/*     */ import org.apache.axis.wsdl.symbolTable.BindingEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.Parameter;
/*     */ import org.apache.axis.wsdl.symbolTable.Parameters;
/*     */ import org.apache.axis.wsdl.symbolTable.SymbolTable;
/*     */ import org.apache.axis.wsdl.symbolTable.TypeEntry;
/*     */ 
/*     */ public class JavaImplWriter extends JavaClassWriter
/*     */ {
/*     */   protected Binding binding;
/*     */   protected SymbolTable symbolTable;
/*     */   protected BindingEntry bEntry;
/*     */ 
/*     */   protected JavaImplWriter(Emitter emitter, BindingEntry bEntry, SymbolTable symbolTable)
/*     */   {
/*  59 */     super(emitter, emitter.getImplementationClassName() == null ? bEntry.getName() + "Impl" : emitter.getImplementationClassName(), "templateImpl");
/*     */ 
/*  63 */     this.binding = bEntry.getBinding();
/*  64 */     this.symbolTable = symbolTable;
/*  65 */     this.bEntry = bEntry;
/*     */   }
/*     */ 
/*     */   protected void writeFileBody(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/*  76 */     List operations = this.binding.getBindingOperations();
/*     */ 
/*  78 */     for (int i = 0; i < operations.size(); i++) {
/*  79 */       BindingOperation operation = (BindingOperation)operations.get(i);
/*  80 */       Operation ptOperation = operation.getOperation();
/*  81 */       OperationType type = ptOperation.getStyle();
/*  82 */       Parameters parameters = this.bEntry.getParameters(operation.getOperation());
/*     */ 
/*  87 */       if ((OperationType.NOTIFICATION.equals(type)) || (OperationType.SOLICIT_RESPONSE.equals(type)))
/*     */       {
/*  89 */         pw.println(parameters.signature);
/*  90 */         pw.println();
/*     */       } else {
/*  92 */         writeOperation(pw, parameters);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String getImplementsText()
/*     */   {
/* 104 */     String portTypeName = (String)this.bEntry.getDynamicVar(JavaBindingWriter.INTERFACE_NAME);
/*     */ 
/* 106 */     String implementsText = "implements " + portTypeName;
/*     */ 
/* 108 */     return implementsText;
/*     */   }
/*     */ 
/*     */   protected void writeOperation(PrintWriter pw, Parameters parms)
/*     */     throws IOException
/*     */   {
/* 121 */     pw.println(parms.signature + " {");
/*     */ 
/* 124 */     Iterator iparam = parms.list.iterator();
/*     */ 
/* 126 */     while (iparam.hasNext()) {
/* 127 */       Parameter param = (Parameter)iparam.next();
/*     */ 
/* 129 */       if (param.getMode() == 2)
/*     */       {
/* 132 */         BooleanHolder bThrow = new BooleanHolder(false);
/* 133 */         String constructorString = Utils.getConstructorForParam(param, this.symbolTable, bThrow);
/*     */ 
/* 136 */         if (bThrow.value) {
/* 137 */           pw.println("        try {");
/*     */         }
/*     */ 
/* 140 */         pw.println("        " + Utils.xmlNameToJava(param.getName()) + ".value = " + constructorString + ";");
/*     */ 
/* 143 */         if (bThrow.value) {
/* 144 */           pw.println("        } catch (Exception e) {");
/* 145 */           pw.println("        }");
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 151 */     Parameter returnParam = parms.returnParam;
/* 152 */     if (returnParam != null) {
/* 153 */       TypeEntry returnType = returnParam.getType();
/*     */ 
/* 155 */       pw.print("        return ");
/*     */ 
/* 157 */       if ((!returnParam.isOmittable()) && (Utils.isPrimitiveType(returnType)))
/*     */       {
/* 159 */         String returnString = returnType.getName();
/*     */ 
/* 161 */         if ("boolean".equals(returnString))
/* 162 */           pw.println("false;");
/* 163 */         else if ("byte".equals(returnString))
/* 164 */           pw.println("(byte)-3;");
/* 165 */         else if ("short".equals(returnString))
/* 166 */           pw.println("(short)-3;");
/*     */         else
/* 168 */           pw.println("-3;");
/*     */       }
/*     */       else {
/* 171 */         pw.println("null;");
/*     */       }
/*     */     }
/*     */ 
/* 175 */     pw.println("    }");
/* 176 */     pw.println();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.JavaImplWriter
 * JD-Core Version:    0.6.0
 */