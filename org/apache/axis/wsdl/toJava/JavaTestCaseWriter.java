/*     */ package org.apache.axis.wsdl.toJava;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Vector;
/*     */ import javax.wsdl.Binding;
/*     */ import javax.wsdl.Fault;
/*     */ import javax.wsdl.Operation;
/*     */ import javax.wsdl.OperationType;
/*     */ import javax.wsdl.Port;
/*     */ import javax.wsdl.PortType;
/*     */ import javax.wsdl.Service;
/*     */ import javax.xml.rpc.ServiceException;
/*     */ import javax.xml.rpc.holders.BooleanHolder;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.symbolTable.BindingEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.Parameter;
/*     */ import org.apache.axis.wsdl.symbolTable.Parameters;
/*     */ import org.apache.axis.wsdl.symbolTable.ServiceEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.SymbolTable;
/*     */ import org.apache.axis.wsdl.symbolTable.TypeEntry;
/*     */ 
/*     */ public class JavaTestCaseWriter extends JavaClassWriter
/*     */ {
/*     */   private ServiceEntry sEntry;
/*     */   private SymbolTable symbolTable;
/* 141 */   private int counter = 1;
/*     */ 
/*     */   protected JavaTestCaseWriter(Emitter emitter, ServiceEntry sEntry, SymbolTable symbolTable)
/*     */   {
/*  60 */     super(emitter, sEntry.getName() + "TestCase", "testCase");
/*     */ 
/*  62 */     this.sEntry = sEntry;
/*  63 */     this.symbolTable = symbolTable;
/*     */   }
/*     */ 
/*     */   protected String getExtendsText()
/*     */   {
/*  72 */     return "extends junit.framework.TestCase ";
/*     */   }
/*     */ 
/*     */   protected void writeFileBody(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/*  84 */     pw.print("    public ");
/*  85 */     pw.print(getClassName());
/*  86 */     pw.println("(java.lang.String name) {");
/*  87 */     pw.println("        super(name);");
/*  88 */     pw.println("    }");
/*  89 */     pw.println("");
/*     */ 
/*  92 */     Map portMap = this.sEntry.getService().getPorts();
/*  93 */     Iterator portIterator = portMap.values().iterator();
/*     */ 
/*  95 */     while (portIterator.hasNext()) {
/*  96 */       Port p = (Port)portIterator.next();
/*  97 */       Binding binding = p.getBinding();
/*  98 */       BindingEntry bEntry = this.symbolTable.getBindingEntry(binding.getQName());
/*     */ 
/* 102 */       if (bEntry.getBindingType() != 0)
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/* 113 */       String portName = p.getName();
/*     */ 
/* 115 */       if (!JavaUtils.isJavaId(portName)) {
/* 116 */         portName = Utils.xmlNameToJavaClass(portName);
/*     */       }
/*     */ 
/* 119 */       pw.println("    public void test" + portName + "WSDL() throws Exception {");
/* 120 */       pw.println("        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();");
/* 121 */       pw.println("        java.net.URL url = new java.net.URL(new " + this.sEntry.getName() + "Locator" + "().get" + portName + "Address() + \"?WSDL\");");
/* 122 */       pw.println("        javax.xml.rpc.Service service = serviceFactory.createService(url, new " + this.sEntry.getName() + "Locator().getServiceName());");
/* 123 */       pw.println("        assertTrue(service != null);");
/* 124 */       pw.println("    }");
/* 125 */       pw.println("");
/*     */ 
/* 127 */       PortType portType = binding.getPortType();
/*     */ 
/* 129 */       writeComment(pw, p.getDocumentationElement(), true);
/*     */ 
/* 131 */       writeServiceTestCode(pw, portName, portType, bEntry);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected final void writeServiceTestCode(PrintWriter pw, String portName, PortType portType, BindingEntry bEntry)
/*     */   {
/* 154 */     Iterator ops = portType.getOperations().iterator();
/*     */ 
/* 156 */     while (ops.hasNext()) {
/* 157 */       Operation op = (Operation)ops.next();
/* 158 */       OperationType type = op.getStyle();
/* 159 */       Parameters params = bEntry.getParameters(op);
/*     */ 
/* 162 */       BooleanHolder bThrow = new BooleanHolder(false);
/*     */ 
/* 166 */       if ((OperationType.NOTIFICATION.equals(type)) || (OperationType.SOLICIT_RESPONSE.equals(type)))
/*     */       {
/* 168 */         pw.println("    " + params.signature);
/*     */ 
/* 170 */         continue;
/*     */       }
/*     */ 
/* 173 */       String javaOpName = Utils.xmlNameToJavaClass(op.getName());
/* 174 */       String testMethodName = "test" + this.counter++ + portName + javaOpName;
/*     */ 
/* 176 */       pw.println("    public void " + testMethodName + "() throws Exception {");
/*     */ 
/* 179 */       String bindingType = bEntry.getName() + "Stub";
/*     */ 
/* 181 */       writeBindingAssignment(pw, bindingType, portName);
/* 182 */       pw.println("        // Test operation");
/*     */ 
/* 184 */       String indent = "";
/* 185 */       Map faultMap = op.getFaults();
/*     */ 
/* 187 */       if ((faultMap != null) && (faultMap.size() > 0))
/*     */       {
/* 190 */         pw.println("        try {");
/*     */ 
/* 192 */         indent = "    ";
/*     */       }
/*     */ 
/* 195 */       Parameter returnParam = params.returnParam;
/* 196 */       if (returnParam != null) {
/* 197 */         TypeEntry returnType = returnParam.getType();
/*     */ 
/* 199 */         pw.print("        " + indent);
/* 200 */         pw.print(Utils.getParameterTypeName(returnParam));
/* 201 */         pw.print(" value = ");
/*     */ 
/* 203 */         if ((returnParam.getMIMEInfo() == null) && (!returnParam.isOmittable()) && (Utils.isPrimitiveType(returnType)))
/*     */         {
/* 206 */           if ("boolean".equals(returnType.getName()))
/* 207 */             pw.println("false;");
/*     */           else
/* 209 */             pw.println("-3;");
/*     */         }
/*     */         else {
/* 212 */           pw.println("null;");
/*     */         }
/*     */       }
/*     */ 
/* 216 */       pw.print("        " + indent);
/*     */ 
/* 218 */       if (returnParam != null) {
/* 219 */         pw.print("value = ");
/*     */       }
/*     */ 
/* 222 */       pw.print("binding.");
/* 223 */       pw.print(Utils.xmlNameToJava(op.getName()));
/* 224 */       pw.print("(");
/*     */ 
/* 226 */       Iterator iparam = params.list.iterator();
/* 227 */       boolean isFirst = true;
/*     */ 
/* 229 */       while (iparam.hasNext()) {
/* 230 */         if (isFirst)
/* 231 */           isFirst = false;
/*     */         else {
/* 233 */           pw.print(", ");
/*     */         }
/*     */ 
/* 236 */         Parameter param = (Parameter)iparam.next();
/* 237 */         String suffix = "";
/*     */ 
/* 240 */         if (param.getMode() != 1) {
/* 241 */           pw.print("new " + Utils.holder(param, this.emitter) + "(");
/* 242 */           suffix = ")";
/*     */         }
/*     */ 
/* 246 */         if (param.getMode() != 2) {
/* 247 */           String constructorString = Utils.getConstructorForParam(param, this.symbolTable, bThrow);
/*     */ 
/* 251 */           pw.print(constructorString);
/*     */         }
/*     */ 
/* 254 */         pw.print(suffix);
/*     */       }
/*     */ 
/* 257 */       pw.println(");");
/*     */ 
/* 259 */       if ((faultMap != null) && (faultMap.size() > 0)) {
/* 260 */         pw.println("        }");
/*     */       }
/*     */ 
/* 263 */       if (faultMap != null) {
/* 264 */         Iterator i = faultMap.values().iterator();
/* 265 */         int count = 0;
/*     */ 
/* 267 */         while (i.hasNext()) {
/* 268 */           count++;
/*     */ 
/* 270 */           Fault f = (Fault)i.next();
/*     */ 
/* 272 */           pw.print("        catch (");
/* 273 */           pw.print(Utils.getFullExceptionName(f.getMessage(), this.symbolTable));
/*     */ 
/* 275 */           pw.println(" e" + count + ") {");
/* 276 */           pw.print("            ");
/* 277 */           pw.println("throw new junit.framework.AssertionFailedError(\"" + f.getName() + " Exception caught: \" + e" + count + ");");
/*     */ 
/* 281 */           pw.println("        }");
/*     */         }
/*     */       }
/*     */ 
/* 285 */       pw.println("        " + indent + "// TBD - validate results");
/*     */ 
/* 298 */       pw.println("    }");
/* 299 */       pw.println();
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void writeBindingAssignment(PrintWriter pw, String bindingType, String portName)
/*     */   {
/* 313 */     pw.println("        " + bindingType + " binding;");
/* 314 */     pw.println("        try {");
/* 315 */     pw.println("            binding = (" + bindingType + ")");
/* 316 */     pw.print("                          new " + this.sEntry.getName());
/* 317 */     pw.println("Locator().get" + portName + "();");
/* 318 */     pw.println("        }");
/* 319 */     pw.println("        catch (" + ServiceException.class.getName() + " jre) {");
/*     */ 
/* 322 */     pw.println("            if(jre.getLinkedCause()!=null)");
/* 323 */     pw.println("                jre.getLinkedCause().printStackTrace();");
/* 324 */     pw.println("            throw new junit.framework.AssertionFailedError(\"JAX-RPC ServiceException caught: \" + jre);");
/*     */ 
/* 326 */     pw.println("        }");
/* 327 */     pw.println("        assertNotNull(\"" + Messages.getMessage("null00", "binding") + "\", binding);");
/*     */ 
/* 330 */     pw.println();
/* 331 */     pw.println("        // Time out after a minute");
/* 332 */     pw.println("        binding.setTimeout(60000);");
/* 333 */     pw.println();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.JavaTestCaseWriter
 * JD-Core Version:    0.6.0
 */