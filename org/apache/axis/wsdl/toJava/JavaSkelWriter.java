/*     */ package org.apache.axis.wsdl.toJava;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import javax.wsdl.Binding;
/*     */ import javax.wsdl.BindingInput;
/*     */ import javax.wsdl.BindingOperation;
/*     */ import javax.wsdl.BindingOutput;
/*     */ import javax.wsdl.Definition;
/*     */ import javax.wsdl.Operation;
/*     */ import javax.wsdl.OperationType;
/*     */ import javax.wsdl.extensions.UnknownExtensibilityElement;
/*     */ import javax.wsdl.extensions.soap.SOAPBody;
/*     */ import javax.wsdl.extensions.soap.SOAPOperation;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.symbolTable.BindingEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.FaultInfo;
/*     */ import org.apache.axis.wsdl.symbolTable.Parameter;
/*     */ import org.apache.axis.wsdl.symbolTable.Parameters;
/*     */ import org.apache.axis.wsdl.symbolTable.SymbolTable;
/*     */ import org.w3c.dom.Element;
/*     */ 
/*     */ public class JavaSkelWriter extends JavaClassWriter
/*     */ {
/*     */   private BindingEntry bEntry;
/*     */   private Binding binding;
/*     */   private SymbolTable symbolTable;
/*     */ 
/*     */   protected JavaSkelWriter(Emitter emitter, BindingEntry bEntry, SymbolTable symbolTable)
/*     */   {
/*  67 */     super(emitter, bEntry.getName() + "Skeleton", "skeleton");
/*     */ 
/*  69 */     this.bEntry = bEntry;
/*  70 */     this.binding = bEntry.getBinding();
/*  71 */     this.symbolTable = symbolTable;
/*     */   }
/*     */ 
/*     */   protected String getImplementsText()
/*     */   {
/*  81 */     return "implements " + this.bEntry.getDynamicVar(JavaBindingWriter.INTERFACE_NAME) + ", org.apache.axis.wsdl.Skeleton ";
/*     */   }
/*     */ 
/*     */   protected void writeFileBody(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/*  94 */     String portTypeName = (String)this.bEntry.getDynamicVar(JavaBindingWriter.INTERFACE_NAME);
/*     */ 
/*  96 */     String implType = portTypeName + " impl";
/*     */ 
/*  99 */     pw.println("    private " + implType + ";");
/* 100 */     pw.println("    private static java.util.Map _myOperations = new java.util.Hashtable();");
/*     */ 
/* 102 */     pw.println("    private static java.util.Collection _myOperationsList = new java.util.ArrayList();");
/*     */ 
/* 104 */     pw.println();
/* 105 */     pw.println("    /**");
/* 106 */     pw.println("    * Returns List of OperationDesc objects with this name");
/*     */ 
/* 108 */     pw.println("    */");
/* 109 */     pw.println("    public static java.util.List getOperationDescByName(java.lang.String methodName) {");
/*     */ 
/* 111 */     pw.println("        return (java.util.List)_myOperations.get(methodName);");
/*     */ 
/* 113 */     pw.println("    }");
/* 114 */     pw.println();
/* 115 */     pw.println("    /**");
/* 116 */     pw.println("    * Returns Collection of OperationDescs");
/* 117 */     pw.println("    */");
/* 118 */     pw.println("    public static java.util.Collection getOperationDescs() {");
/*     */ 
/* 120 */     pw.println("        return _myOperationsList;");
/* 121 */     pw.println("    }");
/* 122 */     pw.println();
/*     */ 
/* 125 */     pw.println("    static {");
/* 126 */     pw.println("        org.apache.axis.description.OperationDesc _oper;");
/* 127 */     pw.println("        org.apache.axis.description.FaultDesc _fault;");
/* 128 */     pw.println("        org.apache.axis.description.ParameterDesc [] _params;");
/*     */ 
/* 131 */     List operations = this.binding.getBindingOperations();
/*     */ 
/* 133 */     for (int i = 0; i < operations.size(); i++) {
/* 134 */       BindingOperation bindingOper = (BindingOperation)operations.get(i);
/* 135 */       Operation operation = bindingOper.getOperation();
/* 136 */       OperationType type = operation.getStyle();
/*     */ 
/* 140 */       if ((OperationType.NOTIFICATION.equals(type)) || (OperationType.SOLICIT_RESPONSE.equals(type)))
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/* 145 */       Parameters parameters = this.bEntry.getParameters(bindingOper.getOperation());
/*     */ 
/* 148 */       if (parameters != null)
/*     */       {
/* 151 */         String opName = bindingOper.getOperation().getName();
/* 152 */         String javaOpName = Utils.xmlNameToJava(opName);
/*     */ 
/* 154 */         pw.println("        _params = new org.apache.axis.description.ParameterDesc [] {");
/*     */ 
/* 157 */         for (int j = 0; j < parameters.list.size(); j++) {
/* 158 */           Parameter p = (Parameter)parameters.list.get(j);
/*     */           String modeStr;
/*     */           String modeStr;
/*     */           String modeStr;
/* 161 */           switch (p.getMode())
/*     */           {
/*     */           case 1:
/* 164 */             modeStr = "org.apache.axis.description.ParameterDesc.IN";
/*     */ 
/* 166 */             break;
/*     */           case 2:
/* 169 */             modeStr = "org.apache.axis.description.ParameterDesc.OUT";
/*     */ 
/* 171 */             break;
/*     */           case 3:
/* 174 */             modeStr = "org.apache.axis.description.ParameterDesc.INOUT";
/*     */ 
/* 176 */             break;
/*     */           default:
/* 179 */             throw new IOException(Messages.getMessage("badParmMode00", new Byte(p.getMode()).toString()));
/*     */           }
/*     */ 
/* 186 */           QName paramName = p.getQName();
/* 187 */           QName paramType = Utils.getXSIType(p);
/*     */ 
/* 190 */           String inHeader = p.isInHeader() ? "true" : "false";
/*     */ 
/* 193 */           String outHeader = p.isOutHeader() ? "true" : "false";
/*     */ 
/* 197 */           pw.println("            new org.apache.axis.description.ParameterDesc(" + Utils.getNewQNameWithLastLocalPart(paramName) + ", " + modeStr + ", " + Utils.getNewQName(paramType) + ", " + Utils.getParameterTypeName(p) + ".class" + ", " + inHeader + ", " + outHeader + "), ");
/*     */         }
/*     */ 
/* 206 */         pw.println("        };");
/*     */ 
/* 209 */         QName retName = null;
/* 210 */         QName retType = null;
/*     */ 
/* 212 */         if (parameters.returnParam != null) {
/* 213 */           retName = parameters.returnParam.getQName();
/* 214 */           retType = Utils.getXSIType(parameters.returnParam);
/*     */         }
/*     */         String returnStr;
/*     */         String returnStr;
/* 219 */         if (retName != null)
/* 220 */           returnStr = Utils.getNewQNameWithLastLocalPart(retName);
/*     */         else {
/* 222 */           returnStr = "null";
/*     */         }
/*     */ 
/* 225 */         pw.println("        _oper = new org.apache.axis.description.OperationDesc(\"" + javaOpName + "\", _params, " + returnStr + ");");
/*     */ 
/* 229 */         if (retType != null) {
/* 230 */           pw.println("        _oper.setReturnType(" + Utils.getNewQName(retType) + ");");
/*     */ 
/* 233 */           if ((parameters.returnParam != null) && (parameters.returnParam.isOutHeader()))
/*     */           {
/* 235 */             pw.println("        _oper.setReturnHeader(true);");
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 242 */         QName elementQName = Utils.getOperationQName(bindingOper, this.bEntry, this.symbolTable);
/*     */ 
/* 245 */         if (elementQName != null) {
/* 246 */           pw.println("        _oper.setElementQName(" + Utils.getNewQName(elementQName) + ");");
/*     */         }
/*     */ 
/* 251 */         String action = Utils.getOperationSOAPAction(bindingOper);
/* 252 */         if (action != null) {
/* 253 */           pw.println("        _oper.setSoapAction(\"" + action + "\");");
/*     */         }
/*     */ 
/* 256 */         pw.println("        _myOperationsList.add(_oper);");
/* 257 */         pw.println("        if (_myOperations.get(\"" + javaOpName + "\") == null) {");
/*     */ 
/* 259 */         pw.println("            _myOperations.put(\"" + javaOpName + "\", new java.util.ArrayList());");
/*     */ 
/* 261 */         pw.println("        }");
/* 262 */         pw.println("        ((java.util.List)_myOperations.get(\"" + javaOpName + "\")).add(_oper);");
/*     */       }
/*     */ 
/* 267 */       if (this.bEntry.getFaults() != null) {
/* 268 */         ArrayList faults = (ArrayList)this.bEntry.getFaults().get(bindingOper);
/*     */ 
/* 271 */         if (faults == null) {
/*     */           continue;
/*     */         }
/* 274 */         if (parameters == null) {
/* 275 */           String opName = bindingOper.getOperation().getName();
/*     */ 
/* 277 */           String javaOpName = Utils.xmlNameToJava(opName);
/*     */ 
/* 279 */           pw.println("        _oper = new org.apache.axis.description.OperationDesc();");
/*     */ 
/* 282 */           pw.println("        _oper.setName(\"" + javaOpName + "\");");
/*     */         }
/*     */ 
/* 287 */         Iterator it = faults.iterator();
/*     */ 
/* 289 */         while (it.hasNext()) {
/* 290 */           FaultInfo faultInfo = (FaultInfo)it.next();
/* 291 */           QName faultQName = faultInfo.getQName();
/* 292 */           QName faultXMLType = faultInfo.getXMLType();
/* 293 */           String faultName = faultInfo.getName();
/* 294 */           String className = Utils.getFullExceptionName(faultInfo.getMessage(), this.symbolTable);
/*     */ 
/* 298 */           pw.println("        _fault = new org.apache.axis.description.FaultDesc();");
/*     */ 
/* 302 */           if (faultName != null) {
/* 303 */             pw.println("        _fault.setName(\"" + faultName + "\");");
/*     */           }
/*     */ 
/* 307 */           if (faultQName != null) {
/* 308 */             pw.println("        _fault.setQName(" + Utils.getNewQName(faultQName) + ");");
/*     */           }
/*     */ 
/* 312 */           if (className != null) {
/* 313 */             pw.println("        _fault.setClassName(\"" + className + "\");");
/*     */           }
/*     */ 
/* 317 */           if (faultXMLType != null) {
/* 318 */             pw.println("        _fault.setXmlType(" + Utils.getNewQName(faultXMLType) + ");");
/*     */           }
/*     */ 
/* 323 */           pw.println("        _oper.addFault(_fault);");
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 329 */     pw.println("    }");
/* 330 */     pw.println();
/*     */ 
/* 333 */     pw.println("    public " + this.className + "() {");
/*     */ 
/* 336 */     String implementationClassName = this.emitter.getImplementationClassName();
/* 337 */     if (implementationClassName == null)
/* 338 */       implementationClassName = this.bEntry.getName() + "Impl";
/* 339 */     pw.println("        this.impl = new " + implementationClassName + "();");
/* 340 */     pw.println("    }");
/*     */ 
/* 342 */     pw.println();
/* 343 */     pw.println("    public " + this.className + "(" + implType + ") {");
/* 344 */     pw.println("        this.impl = impl;");
/* 345 */     pw.println("    }");
/*     */ 
/* 348 */     for (int i = 0; i < operations.size(); i++) {
/* 349 */       BindingOperation operation = (BindingOperation)operations.get(i);
/* 350 */       Parameters parameters = this.bEntry.getParameters(operation.getOperation());
/*     */ 
/* 354 */       String soapAction = "";
/* 355 */       Iterator operationExtensibilityIterator = operation.getExtensibilityElements().iterator();
/*     */ 
/* 358 */       while (operationExtensibilityIterator.hasNext()) {
/* 359 */         Object obj = operationExtensibilityIterator.next();
/*     */ 
/* 361 */         if ((obj instanceof SOAPOperation)) {
/* 362 */           soapAction = ((SOAPOperation)obj).getSoapActionURI();
/*     */ 
/* 364 */           break;
/* 365 */         }if ((obj instanceof UnknownExtensibilityElement))
/*     */         {
/* 368 */           UnknownExtensibilityElement unkElement = (UnknownExtensibilityElement)obj;
/*     */ 
/* 370 */           QName name = unkElement.getElementType();
/*     */ 
/* 373 */           if ((name.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/")) && (name.getLocalPart().equals("operation")))
/*     */           {
/* 375 */             if (unkElement.getElement().getAttribute("soapAction") != null)
/*     */             {
/* 377 */               soapAction = unkElement.getElement().getAttribute("soapAction");
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 386 */       String namespace = "";
/* 387 */       Iterator bindingMsgIterator = null;
/* 388 */       BindingInput input = operation.getBindingInput();
/*     */ 
/* 391 */       if (input != null) {
/* 392 */         bindingMsgIterator = input.getExtensibilityElements().iterator();
/*     */       }
/*     */       else {
/* 395 */         BindingOutput output = operation.getBindingOutput();
/*     */ 
/* 397 */         if (output != null) {
/* 398 */           bindingMsgIterator = output.getExtensibilityElements().iterator();
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 403 */       if (bindingMsgIterator != null) {
/* 404 */         while (bindingMsgIterator.hasNext()) {
/* 405 */           Object obj = bindingMsgIterator.next();
/*     */ 
/* 407 */           if ((obj instanceof SOAPBody)) {
/* 408 */             namespace = ((SOAPBody)obj).getNamespaceURI();
/*     */ 
/* 410 */             if (namespace == null) {
/* 411 */               namespace = this.symbolTable.getDefinition().getTargetNamespace();
/*     */             }
/*     */ 
/* 415 */             if (namespace != null) break;
/* 416 */             namespace = ""; break;
/*     */           }
/*     */ 
/* 420 */           if ((obj instanceof UnknownExtensibilityElement))
/*     */           {
/* 423 */             UnknownExtensibilityElement unkElement = (UnknownExtensibilityElement)obj;
/*     */ 
/* 425 */             QName name = unkElement.getElementType();
/*     */ 
/* 428 */             if ((name.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/")) && (name.getLocalPart().equals("body")))
/*     */             {
/* 431 */               namespace = unkElement.getElement().getAttribute("namespace");
/*     */ 
/* 434 */               if (namespace == null) {
/* 435 */                 namespace = this.symbolTable.getDefinition().getTargetNamespace();
/*     */               }
/*     */ 
/* 439 */               if (namespace != null) break;
/* 440 */               namespace = ""; break;
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 449 */       Operation ptOperation = operation.getOperation();
/* 450 */       OperationType type = ptOperation.getStyle();
/*     */ 
/* 454 */       if ((OperationType.NOTIFICATION.equals(type)) || (OperationType.SOLICIT_RESPONSE.equals(type)))
/*     */       {
/* 456 */         pw.println(parameters.signature);
/* 457 */         pw.println();
/*     */       } else {
/* 459 */         writeOperation(pw, operation, parameters, soapAction, namespace);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void writeOperation(PrintWriter pw, BindingOperation operation, Parameters parms, String soapAction, String namespace)
/*     */   {
/* 478 */     writeComment(pw, operation.getDocumentationElement(), true);
/*     */ 
/* 482 */     pw.println(parms.signature);
/* 483 */     pw.println("    {");
/*     */ 
/* 488 */     if (parms.returnParam == null)
/* 489 */       pw.print("        ");
/*     */     else {
/* 491 */       pw.print("        " + Utils.getParameterTypeName(parms.returnParam) + " ret = ");
/*     */     }
/*     */ 
/* 495 */     String call = "impl." + Utils.xmlNameToJava(operation.getName()) + "(";
/*     */ 
/* 497 */     boolean needComma = false;
/*     */ 
/* 499 */     for (int i = 0; i < parms.list.size(); i++) {
/* 500 */       if (needComma)
/* 501 */         call = call + ", ";
/*     */       else {
/* 503 */         needComma = true;
/*     */       }
/*     */ 
/* 506 */       Parameter p = (Parameter)parms.list.get(i);
/*     */ 
/* 508 */       call = call + Utils.xmlNameToJava(p.getName());
/*     */     }
/*     */ 
/* 511 */     call = call + ")";
/*     */ 
/* 513 */     pw.println(call + ";");
/*     */ 
/* 515 */     if (parms.returnParam != null) {
/* 516 */       pw.println("        return ret;");
/*     */     }
/*     */ 
/* 519 */     pw.println("    }");
/* 520 */     pw.println();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.JavaSkelWriter
 * JD-Core Version:    0.6.0
 */