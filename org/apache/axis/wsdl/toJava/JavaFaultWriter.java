/*     */ package org.apache.axis.wsdl.toJava;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Vector;
/*     */ import javax.wsdl.Message;
/*     */ import org.apache.axis.constants.Use;
/*     */ import org.apache.axis.wsdl.symbolTable.FaultInfo;
/*     */ import org.apache.axis.wsdl.symbolTable.Parameter;
/*     */ import org.apache.axis.wsdl.symbolTable.SymbolTable;
/*     */ import org.apache.axis.wsdl.symbolTable.TypeEntry;
/*     */ 
/*     */ public class JavaFaultWriter extends JavaClassWriter
/*     */ {
/*     */   private Message faultMessage;
/*     */   private SymbolTable symbolTable;
/*     */   private boolean literal;
/*     */   private String faultName;
/*     */ 
/*     */   protected JavaFaultWriter(Emitter emitter, SymbolTable symbolTable, FaultInfo faultInfo)
/*     */   {
/*  58 */     super(emitter, Utils.getFullExceptionName(faultInfo.getMessage(), symbolTable), "fault");
/*     */ 
/*  62 */     this.literal = faultInfo.getUse().equals(Use.LITERAL);
/*  63 */     this.faultMessage = faultInfo.getMessage();
/*  64 */     this.symbolTable = symbolTable;
/*  65 */     this.faultName = faultInfo.getName();
/*     */   }
/*     */ 
/*     */   protected String getExtendsText()
/*     */   {
/*  74 */     return "extends org.apache.axis.AxisFault ";
/*     */   }
/*     */ 
/*     */   protected void writeFileBody(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/*  85 */     Vector params = new Vector();
/*     */ 
/*  87 */     this.symbolTable.getParametersFromParts(params, this.faultMessage.getOrderedParts(null), this.literal, this.faultName, null);
/*     */ 
/*  92 */     for (int i = 0; i < params.size(); i++) {
/*  93 */       Parameter param = (Parameter)params.get(i);
/*  94 */       String type = param.getType().getName();
/*  95 */       String variable = Utils.xmlNameToJava(param.getName());
/*     */ 
/*  97 */       pw.println("    public " + type + " " + variable + ";");
/*  98 */       pw.println("    public " + type + " get" + Utils.capitalizeFirstChar(variable) + "() {");
/*     */ 
/* 100 */       pw.println("        return this." + variable + ";");
/* 101 */       pw.println("    }");
/*     */     }
/*     */ 
/* 105 */     pw.println();
/* 106 */     pw.println("    public " + this.className + "() {");
/* 107 */     pw.println("    }");
/* 108 */     pw.println();
/*     */ 
/* 111 */     pw.println("    public " + this.className + "(java.lang.Exception target) {");
/* 112 */     pw.println("        super(target);");
/* 113 */     pw.println("    }");
/* 114 */     pw.println();
/* 115 */     pw.println("    public " + this.className + "(java.lang.String message, java.lang.Throwable t) {");
/* 116 */     pw.println("        super(message, t);");
/* 117 */     pw.println("    }");
/* 118 */     pw.println();
/*     */ 
/* 121 */     if (params.size() > 0) {
/* 122 */       pw.print("      public " + this.className + "(");
/*     */ 
/* 124 */       for (int i = 0; i < params.size(); i++) {
/* 125 */         if (i != 0) {
/* 126 */           pw.print(", ");
/*     */         }
/*     */ 
/* 129 */         Parameter param = (Parameter)params.get(i);
/* 130 */         String type = param.getType().getName();
/* 131 */         String variable = Utils.xmlNameToJava(param.getName());
/*     */ 
/* 133 */         pw.print(type + " " + variable);
/*     */       }
/*     */ 
/* 136 */       pw.println(") {");
/*     */ 
/* 138 */       for (int i = 0; i < params.size(); i++) {
/* 139 */         Parameter param = (Parameter)params.get(i);
/* 140 */         String variable = Utils.xmlNameToJava(param.getName());
/*     */ 
/* 142 */         pw.println("        this." + variable + " = " + variable + ";");
/*     */       }
/*     */ 
/* 145 */       pw.println("    }");
/*     */     }
/*     */ 
/* 152 */     pw.println();
/* 153 */     pw.println("    /**");
/* 154 */     pw.println("     * Writes the exception data to the faultDetails");
/* 155 */     pw.println("     */");
/* 156 */     pw.println("    public void writeDetails(javax.xml.namespace.QName qname, org.apache.axis.encoding.SerializationContext context) throws java.io.IOException {");
/*     */ 
/* 159 */     for (int i = 0; i < params.size(); i++) {
/* 160 */       Parameter param = (Parameter)params.get(i);
/* 161 */       String variable = Utils.xmlNameToJava(param.getName());
/*     */ 
/* 163 */       pw.println("        context.serialize(qname, null, " + Utils.wrapPrimitiveType(param.getType(), variable) + ");");
/*     */     }
/*     */ 
/* 168 */     pw.println("    }");
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.JavaFaultWriter
 * JD-Core Version:    0.6.0
 */