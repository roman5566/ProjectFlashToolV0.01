/*     */ package org.apache.axis.wsdl.toJava;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ import javax.wsdl.Binding;
/*     */ import javax.wsdl.BindingOperation;
/*     */ import javax.wsdl.Definition;
/*     */ import javax.wsdl.Operation;
/*     */ import javax.wsdl.OperationType;
/*     */ import javax.wsdl.Port;
/*     */ import javax.wsdl.PortType;
/*     */ import javax.wsdl.Service;
/*     */ import javax.wsdl.extensions.UnknownExtensibilityElement;
/*     */ import javax.wsdl.extensions.soap.SOAPBinding;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.constants.Scope;
/*     */ import org.apache.axis.constants.Style;
/*     */ import org.apache.axis.constants.Use;
/*     */ import org.apache.axis.description.OperationDesc;
/*     */ import org.apache.axis.description.ServiceDesc;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.symbolTable.BindingEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.FaultInfo;
/*     */ import org.apache.axis.wsdl.symbolTable.Parameter;
/*     */ import org.apache.axis.wsdl.symbolTable.Parameters;
/*     */ import org.apache.axis.wsdl.symbolTable.SchemaUtils;
/*     */ import org.apache.axis.wsdl.symbolTable.SymbolTable;
/*     */ import org.apache.axis.wsdl.symbolTable.TypeEntry;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class JavaDeployWriter extends JavaWriter
/*     */ {
/*  65 */   protected static Log log = LogFactory.getLog(JavaDeployWriter.class.getName());
/*     */   protected Definition definition;
/*     */   protected SymbolTable symbolTable;
/*     */   protected Emitter emitter;
/*  77 */   Use use = Use.DEFAULT;
/*     */ 
/* 789 */   private static final Map mepStrings = new HashMap();
/*     */ 
/*     */   public JavaDeployWriter(Emitter emitter, Definition definition, SymbolTable symbolTable)
/*     */   {
/*  89 */     super(emitter, "deploy");
/*     */ 
/*  91 */     this.emitter = emitter;
/*  92 */     this.definition = definition;
/*  93 */     this.symbolTable = symbolTable;
/*     */   }
/*     */ 
/*     */   public void generate()
/*     */     throws IOException
/*     */   {
/* 104 */     if (this.emitter.isServerSide())
/* 105 */       super.generate();
/*     */   }
/*     */ 
/*     */   protected String getFileName()
/*     */   {
/* 117 */     String dir = this.emitter.getNamespaces().getAsDir(this.definition.getTargetNamespace());
/*     */ 
/* 120 */     return dir + "deploy.wsdd";
/*     */   }
/*     */ 
/*     */   protected void writeFileHeader(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/* 131 */     pw.println(Messages.getMessage("deploy00"));
/* 132 */     pw.println(Messages.getMessage("deploy02"));
/* 133 */     pw.println(Messages.getMessage("deploy03"));
/* 134 */     pw.println(Messages.getMessage("deploy05"));
/* 135 */     pw.println(Messages.getMessage("deploy06"));
/* 136 */     pw.println(Messages.getMessage("deploy07"));
/* 137 */     pw.println(Messages.getMessage("deploy09"));
/* 138 */     pw.println();
/* 139 */     pw.println("<deployment");
/* 140 */     pw.println("    xmlns=\"http://xml.apache.org/axis/wsdd/\"");
/* 141 */     pw.println("    xmlns:java=\"http://xml.apache.org/axis/wsdd/providers/java\">");
/*     */   }
/*     */ 
/*     */   protected void writeFileBody(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/* 152 */     writeDeployServices(pw);
/* 153 */     pw.println("</deployment>");
/*     */   }
/*     */ 
/*     */   protected void writeDeployServices(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/* 165 */     Map serviceMap = this.definition.getServices();
/*     */ 
/* 167 */     Iterator mapIterator = serviceMap.values().iterator();
/* 168 */     while (mapIterator.hasNext()) {
/* 169 */       Service myService = (Service)mapIterator.next();
/*     */ 
/* 171 */       pw.println();
/* 172 */       pw.println("  <!-- " + Messages.getMessage("wsdlService00", myService.getQName().getLocalPart()) + " -->");
/*     */ 
/* 176 */       pw.println();
/*     */ 
/* 178 */       Iterator portIterator = myService.getPorts().values().iterator();
/* 179 */       while (portIterator.hasNext()) {
/* 180 */         Port myPort = (Port)portIterator.next();
/* 181 */         BindingEntry bEntry = this.symbolTable.getBindingEntry(myPort.getBinding().getQName());
/*     */ 
/* 185 */         if (bEntry.getBindingType() != 0)
/*     */         {
/*     */           continue;
/*     */         }
/* 189 */         writeDeployPort(pw, myPort, myService, bEntry);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void writeDeployTypes(PrintWriter pw, Binding binding, boolean hasLiteral, boolean hasMIME, Use use)
/*     */     throws IOException
/*     */   {
/* 208 */     pw.println();
/*     */ 
/* 210 */     if (hasMIME) {
/* 211 */       QName bQName = binding.getQName();
/*     */ 
/* 213 */       writeTypeMapping(pw, bQName.getNamespaceURI(), "DataHandler", "javax.activation.DataHandler", "org.apache.axis.encoding.ser.JAFDataHandlerSerializerFactory", "org.apache.axis.encoding.ser.JAFDataHandlerDeserializerFactory", use.getEncoding());
/*     */     }
/*     */ 
/* 221 */     Map types = this.symbolTable.getTypeIndex();
/* 222 */     Collection typeCollection = types.values();
/* 223 */     for (Iterator i = typeCollection.iterator(); i.hasNext(); ) {
/* 224 */       TypeEntry type = (TypeEntry)i.next();
/*     */ 
/* 227 */       boolean process = true;
/*     */ 
/* 231 */       if (!Utils.shouldEmit(type)) {
/* 232 */         process = false;
/*     */       }
/*     */ 
/* 235 */       if (process) {
/* 236 */         String namespaceURI = type.getQName().getNamespaceURI();
/* 237 */         String localPart = type.getQName().getLocalPart();
/* 238 */         String javaType = type.getName();
/*     */ 
/* 241 */         String encodingStyle = "";
/* 242 */         QName innerType = null;
/*     */ 
/* 244 */         if (!hasLiteral)
/* 245 */           encodingStyle = use.getEncoding();
/*     */         String serializerFactory;
/*     */         String deserializerFactory;
/* 248 */         if (javaType.endsWith("[]"))
/*     */         {
/*     */           String deserializerFactory;
/* 249 */           if (SchemaUtils.isListWithItemType(type.getNode())) {
/* 250 */             String serializerFactory = "org.apache.axis.encoding.ser.SimpleListSerializerFactory";
/*     */ 
/* 252 */             deserializerFactory = "org.apache.axis.encoding.ser.SimpleListDeserializerFactory";
/*     */           }
/*     */           else {
/* 255 */             String serializerFactory = "org.apache.axis.encoding.ser.ArraySerializerFactory";
/*     */ 
/* 257 */             String deserializerFactory = "org.apache.axis.encoding.ser.ArrayDeserializerFactory";
/*     */ 
/* 259 */             innerType = type.getComponentType();
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/*     */           String deserializerFactory;
/* 261 */           if ((type.getNode() != null) && (Utils.getEnumerationBaseAndValues(type.getNode(), this.symbolTable) != null))
/*     */           {
/* 263 */             String serializerFactory = "org.apache.axis.encoding.ser.EnumSerializerFactory";
/*     */ 
/* 265 */             deserializerFactory = "org.apache.axis.encoding.ser.EnumDeserializerFactory";
/*     */           }
/*     */           else
/*     */           {
/*     */             String deserializerFactory;
/* 267 */             if (type.isSimpleType()) {
/* 268 */               String serializerFactory = "org.apache.axis.encoding.ser.SimpleSerializerFactory";
/*     */ 
/* 270 */               deserializerFactory = "org.apache.axis.encoding.ser.SimpleDeserializerFactory";
/*     */             }
/*     */             else
/*     */             {
/*     */               String deserializerFactory;
/* 272 */               if (type.getBaseType() != null) {
/* 273 */                 String serializerFactory = "org.apache.axis.encoding.ser.SimpleSerializerFactory";
/*     */ 
/* 275 */                 deserializerFactory = "org.apache.axis.encoding.ser.SimpleDeserializerFactory";
/*     */               }
/*     */               else {
/* 278 */                 serializerFactory = "org.apache.axis.encoding.ser.BeanSerializerFactory";
/*     */ 
/* 280 */                 deserializerFactory = "org.apache.axis.encoding.ser.BeanDeserializerFactory";
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 284 */         if (innerType == null)
/*     */         {
/* 286 */           writeTypeMapping(pw, namespaceURI, localPart, javaType, serializerFactory, deserializerFactory, encodingStyle);
/*     */         }
/*     */         else
/*     */         {
/* 291 */           writeArrayTypeMapping(pw, namespaceURI, localPart, javaType, encodingStyle, innerType);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void writeArrayTypeMapping(PrintWriter pw, String namespaceURI, String localPart, String javaType, String encodingStyle, QName innerType)
/*     */     throws IOException
/*     */   {
/* 314 */     pw.println("      <arrayMapping");
/* 315 */     pw.println("        xmlns:ns=\"" + namespaceURI + "\"");
/* 316 */     pw.println("        qname=\"ns:" + localPart + '"');
/* 317 */     pw.println("        type=\"java:" + javaType + '"');
/* 318 */     pw.println("        innerType=\"" + Utils.genQNameAttributeString(innerType, "cmp-ns") + '"');
/* 319 */     pw.println("        encodingStyle=\"" + encodingStyle + "\"");
/* 320 */     pw.println("      />");
/*     */   }
/*     */ 
/*     */   protected void writeTypeMapping(PrintWriter pw, String namespaceURI, String localPart, String javaType, String serializerFactory, String deserializerFactory, String encodingStyle)
/*     */     throws IOException
/*     */   {
/* 339 */     pw.println("      <typeMapping");
/* 340 */     pw.println("        xmlns:ns=\"" + namespaceURI + "\"");
/* 341 */     pw.println("        qname=\"ns:" + localPart + '"');
/* 342 */     pw.println("        type=\"java:" + javaType + '"');
/* 343 */     pw.println("        serializer=\"" + serializerFactory + "\"");
/* 344 */     pw.println("        deserializer=\"" + deserializerFactory + "\"");
/* 345 */     pw.println("        encodingStyle=\"" + encodingStyle + "\"");
/* 346 */     pw.println("      />");
/*     */   }
/*     */ 
/*     */   protected void writeDeployPort(PrintWriter pw, Port port, Service service, BindingEntry bEntry)
/*     */     throws IOException
/*     */   {
/* 362 */     String serviceName = port.getName();
/* 363 */     boolean hasLiteral = bEntry.hasLiteral();
/* 364 */     boolean hasMIME = Utils.hasMIME(bEntry);
/* 365 */     String prefix = "java";
/* 366 */     String styleStr = "";
/* 367 */     Iterator iterator = bEntry.getBinding().getExtensibilityElements().iterator();
/*     */ 
/* 370 */     while (iterator.hasNext()) {
/* 371 */       Object obj = iterator.next();
/*     */ 
/* 373 */       if ((obj instanceof SOAPBinding)) {
/* 374 */         this.use = Use.ENCODED;
/* 375 */       } else if ((obj instanceof UnknownExtensibilityElement))
/*     */       {
/* 378 */         UnknownExtensibilityElement unkElement = (UnknownExtensibilityElement)obj;
/*     */ 
/* 380 */         QName name = unkElement.getElementType();
/*     */ 
/* 383 */         if ((name.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/")) && (name.getLocalPart().equals("binding")))
/*     */         {
/* 385 */           this.use = Use.ENCODED;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 390 */     if (this.symbolTable.isWrapped()) {
/* 391 */       styleStr = " style=\"" + Style.WRAPPED + "\"";
/* 392 */       this.use = Use.LITERAL;
/*     */     } else {
/* 394 */       styleStr = " style=\"" + bEntry.getBindingStyle().getName() + "\"";
/*     */ 
/* 396 */       if (hasLiteral) {
/* 397 */         this.use = Use.LITERAL;
/*     */       }
/*     */     }
/*     */ 
/* 401 */     String useStr = " use=\"" + this.use + "\"";
/*     */ 
/* 403 */     pw.println("  <service name=\"" + serviceName + "\" provider=\"" + prefix + ":RPC" + "\"" + styleStr + useStr + ">");
/*     */ 
/* 405 */     pw.println("      <parameter name=\"wsdlTargetNamespace\" value=\"" + service.getQName().getNamespaceURI() + "\"/>");
/*     */ 
/* 407 */     pw.println("      <parameter name=\"wsdlServiceElement\" value=\"" + service.getQName().getLocalPart() + "\"/>");
/*     */ 
/* 410 */     if (hasMIME) {
/* 411 */       pw.println("      <parameter name=\"sendMultiRefs\" value=\"false\"/>");
/*     */     }
/*     */ 
/* 414 */     ArrayList qualified = new ArrayList();
/* 415 */     ArrayList unqualified = new ArrayList();
/* 416 */     Map elementFormDefaults = this.symbolTable.getElementFormDefaults();
/* 417 */     for (Iterator it = elementFormDefaults.entrySet().iterator(); it.hasNext(); ) {
/* 418 */       Map.Entry entry = (Map.Entry)it.next();
/* 419 */       if (entry.getValue().equals("qualified"))
/* 420 */         qualified.add(entry.getKey());
/*     */       else {
/* 422 */         unqualified.add(entry.getKey());
/*     */       }
/*     */     }
/* 425 */     if (qualified.size() > 0) {
/* 426 */       pw.print("      <parameter name=\"schemaQualified\" value=\"");
/* 427 */       for (int i = 0; i < qualified.size(); i++) {
/* 428 */         pw.print(qualified.get(i));
/* 429 */         if (i != qualified.size() - 1) {
/* 430 */           pw.print(',');
/*     */         }
/*     */       }
/* 433 */       pw.println("\"/>");
/*     */     }
/* 435 */     if (unqualified.size() > 0) {
/* 436 */       pw.print("      <parameter name=\"schemaUnqualified\" value=\"");
/* 437 */       for (int i = 0; i < unqualified.size(); i++) {
/* 438 */         pw.print(unqualified.get(i));
/* 439 */         if (i != unqualified.size() - 1) {
/* 440 */           pw.print(',');
/*     */         }
/*     */       }
/* 443 */       pw.println("\"/>");
/*     */     }
/* 445 */     pw.println("      <parameter name=\"wsdlServicePort\" value=\"" + serviceName + "\"/>");
/*     */ 
/* 448 */     writeDeployBinding(pw, bEntry);
/* 449 */     writeDeployTypes(pw, bEntry.getBinding(), hasLiteral, hasMIME, this.use);
/* 450 */     pw.println("  </service>");
/*     */   }
/*     */ 
/*     */   protected void writeDeployBinding(PrintWriter pw, BindingEntry bEntry)
/*     */     throws IOException
/*     */   {
/* 463 */     Binding binding = bEntry.getBinding();
/* 464 */     String className = bEntry.getName();
/*     */ 
/* 466 */     if (this.emitter.isSkeletonWanted()) {
/* 467 */       className = className + "Skeleton";
/*     */     }
/*     */     else {
/* 470 */       String customClassName = this.emitter.getImplementationClassName();
/* 471 */       if (customClassName != null)
/* 472 */         className = customClassName;
/*     */       else {
/* 474 */         className = className + "Impl";
/*     */       }
/*     */     }
/* 477 */     pw.println("      <parameter name=\"className\" value=\"" + className + "\"/>");
/*     */ 
/* 480 */     pw.println("      <parameter name=\"wsdlPortType\" value=\"" + binding.getPortType().getQName().getLocalPart() + "\"/>");
/*     */ 
/* 483 */     pw.println("      <parameter name=\"typeMappingVersion\" value=\"" + this.emitter.getTypeMappingVersion() + "\"/>");
/*     */ 
/* 486 */     HashSet allowedMethods = new HashSet();
/*     */ 
/* 488 */     String namespaceURI = binding.getQName().getNamespaceURI();
/*     */ 
/* 490 */     if (!this.emitter.isSkeletonWanted()) {
/* 491 */       Iterator operationsIterator = binding.getBindingOperations().iterator();
/*     */ 
/* 494 */       while (operationsIterator.hasNext()) {
/* 495 */         BindingOperation bindingOper = (BindingOperation)operationsIterator.next();
/*     */ 
/* 497 */         Operation operation = bindingOper.getOperation();
/* 498 */         OperationType type = operation.getStyle();
/*     */ 
/* 502 */         if ((OperationType.NOTIFICATION.equals(type)) || (OperationType.SOLICIT_RESPONSE.equals(type)))
/*     */         {
/*     */           continue;
/*     */         }
/* 506 */         String javaOperName = null;
/*     */ 
/* 508 */         ServiceDesc serviceDesc = this.emitter.getServiceDesc();
/* 509 */         if ((this.emitter.isDeploy()) && (serviceDesc != null))
/*     */         {
/* 511 */           OperationDesc[] operDescs = serviceDesc.getOperationsByQName(new QName(namespaceURI, operation.getName()));
/* 512 */           if (operDescs.length == 0) {
/* 513 */             log.warn("Can't find operation in the Java Class for WSDL binding operation : " + operation.getName());
/* 514 */             continue;
/*     */           }
/* 516 */           OperationDesc operDesc = operDescs[0];
/* 517 */           if (operDesc.getMethod() == null) {
/* 518 */             log.warn("Can't find Java method for operation descriptor : " + operDesc.getName());
/* 519 */             continue;
/*     */           }
/*     */ 
/* 522 */           javaOperName = operDesc.getMethod().getName();
/*     */         } else {
/* 524 */           javaOperName = JavaUtils.xmlNameToJava(operation.getName());
/*     */         }
/*     */ 
/* 528 */         allowedMethods.add(javaOperName);
/*     */ 
/* 532 */         Parameters params = this.symbolTable.getOperationParameters(operation, "", bEntry);
/*     */ 
/* 535 */         if (params != null)
/*     */         {
/* 541 */           QName elementQName = Utils.getOperationQName(bindingOper, bEntry, this.symbolTable);
/*     */ 
/* 545 */           QName returnQName = null;
/* 546 */           QName returnType = null;
/*     */ 
/* 548 */           if (params.returnParam != null) {
/* 549 */             returnQName = params.returnParam.getQName();
/* 550 */             returnType = Utils.getXSIType(params.returnParam);
/*     */           }
/*     */ 
/* 554 */           Map faultMap = bEntry.getFaults();
/* 555 */           ArrayList faults = null;
/*     */ 
/* 557 */           if (faultMap != null) {
/* 558 */             faults = (ArrayList)faultMap.get(bindingOper);
/*     */           }
/*     */ 
/* 562 */           String SOAPAction = Utils.getOperationSOAPAction(bindingOper);
/*     */ 
/* 565 */           writeOperation(pw, javaOperName, elementQName, returnQName, returnType, params, binding.getQName(), faults, SOAPAction);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 572 */     pw.print("      <parameter name=\"allowedMethods\" value=\"");
/*     */ 
/* 574 */     if (allowedMethods.isEmpty()) {
/* 575 */       pw.println("*\"/>");
/*     */     } else {
/* 577 */       boolean first = true;
/*     */ 
/* 579 */       for (Iterator i = allowedMethods.iterator(); i.hasNext(); ) {
/* 580 */         String method = (String)i.next();
/*     */ 
/* 582 */         if (first) {
/* 583 */           pw.print(method);
/*     */ 
/* 585 */           first = false;
/*     */         } else {
/* 587 */           pw.print(" " + method);
/*     */         }
/*     */       }
/*     */ 
/* 591 */       pw.println("\"/>");
/*     */     }
/*     */ 
/* 594 */     Scope scope = this.emitter.getScope();
/*     */ 
/* 596 */     if (scope != null)
/* 597 */       pw.println("      <parameter name=\"scope\" value=\"" + scope.getName() + "\"/>");
/*     */   }
/*     */ 
/*     */   protected void writeOperation(PrintWriter pw, String javaOperName, QName elementQName, QName returnQName, QName returnType, Parameters params, QName bindingQName, ArrayList faults, String SOAPAction)
/*     */   {
/* 620 */     pw.print("      <operation name=\"" + javaOperName + "\"");
/*     */ 
/* 622 */     if (elementQName != null) {
/* 623 */       pw.print(" qname=\"" + Utils.genQNameAttributeString(elementQName, "operNS") + "\"");
/*     */     }
/*     */ 
/* 628 */     if (returnQName != null) {
/* 629 */       pw.print(" returnQName=\"" + Utils.genQNameAttributeStringWithLastLocalPart(returnQName, "retNS") + "\"");
/*     */     }
/*     */ 
/* 634 */     if (returnType != null) {
/* 635 */       pw.print(" returnType=\"" + Utils.genQNameAttributeString(returnType, "rtns") + "\"");
/*     */     }
/*     */ 
/* 640 */     Parameter retParam = params.returnParam;
/* 641 */     if (retParam != null) {
/* 642 */       TypeEntry type = retParam.getType();
/* 643 */       QName returnItemQName = Utils.getItemQName(type);
/* 644 */       if (returnItemQName != null) {
/* 645 */         pw.print(" returnItemQName=\"");
/* 646 */         pw.print(Utils.genQNameAttributeString(returnItemQName, "tns"));
/* 647 */         pw.print("\"");
/*     */       }
/* 649 */       QName returnItemType = Utils.getItemType(type);
/* 650 */       if ((returnItemType != null) && (this.use == Use.ENCODED)) {
/* 651 */         pw.print(" returnItemType=\"");
/* 652 */         pw.print(Utils.genQNameAttributeString(returnItemType, "tns2"));
/* 653 */         pw.print("\"");
/*     */       }
/*     */     }
/*     */ 
/* 657 */     if (SOAPAction != null) {
/* 658 */       pw.print(" soapAction=\"" + SOAPAction + "\"");
/*     */     }
/*     */ 
/* 663 */     if (!OperationType.REQUEST_RESPONSE.equals(params.mep)) {
/* 664 */       String mepString = getMepString(params.mep);
/* 665 */       if (mepString != null) {
/* 666 */         pw.print(" mep=\"" + mepString + "\"");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 672 */     if ((params.returnParam != null) && (params.returnParam.isOutHeader())) {
/* 673 */       pw.print(" returnHeader=\"true\"");
/*     */     }
/*     */ 
/* 676 */     pw.println(" >");
/*     */ 
/* 678 */     Vector paramList = params.list;
/*     */ 
/* 680 */     for (int i = 0; i < paramList.size(); i++) {
/* 681 */       Parameter param = (Parameter)paramList.elementAt(i);
/*     */ 
/* 684 */       QName paramQName = param.getQName();
/* 685 */       QName paramType = Utils.getXSIType(param);
/*     */ 
/* 687 */       pw.print("        <parameter");
/*     */ 
/* 689 */       if (paramQName == null)
/* 690 */         pw.print(" name=\"" + param.getName() + "\"");
/*     */       else {
/* 692 */         pw.print(" qname=\"" + Utils.genQNameAttributeStringWithLastLocalPart(paramQName, "pns") + "\"");
/*     */       }
/*     */ 
/* 697 */       pw.print(" type=\"" + Utils.genQNameAttributeString(paramType, "tns") + "\"");
/*     */ 
/* 701 */       if (param.getMode() != 1) {
/* 702 */         pw.print(" mode=\"" + getModeString(param.getMode()) + "\"");
/*     */       }
/*     */ 
/* 706 */       if (param.isInHeader()) {
/* 707 */         pw.print(" inHeader=\"true\"");
/*     */       }
/*     */ 
/* 710 */       if (param.isOutHeader()) {
/* 711 */         pw.print(" outHeader=\"true\"");
/*     */       }
/*     */ 
/* 714 */       QName itemQName = Utils.getItemQName(param.getType());
/* 715 */       if (itemQName != null) {
/* 716 */         pw.print(" itemQName=\"");
/* 717 */         pw.print(Utils.genQNameAttributeString(itemQName, "itns"));
/* 718 */         pw.print("\"");
/*     */       }
/*     */ 
/* 721 */       pw.println("/>");
/*     */     }
/*     */     Iterator iterator;
/* 724 */     if (faults != null) {
/* 725 */       for (iterator = faults.iterator(); iterator.hasNext(); ) {
/* 726 */         FaultInfo faultInfo = (FaultInfo)iterator.next();
/* 727 */         QName faultQName = faultInfo.getQName();
/*     */ 
/* 729 */         if (faultQName != null) {
/* 730 */           String className = Utils.getFullExceptionName(faultInfo.getMessage(), this.symbolTable);
/*     */ 
/* 734 */           pw.print("        <fault");
/* 735 */           pw.print(" name=\"" + faultInfo.getName() + "\"");
/* 736 */           pw.print(" qname=\"" + Utils.genQNameAttributeString(faultQName, "fns") + "\"");
/*     */ 
/* 739 */           pw.print(" class=\"" + className + "\"");
/* 740 */           pw.print(" type=\"" + Utils.genQNameAttributeString(faultInfo.getXMLType(), "tns") + "\"");
/*     */ 
/* 744 */           pw.println("/>");
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 749 */     pw.println("      </operation>");
/*     */   }
/*     */ 
/*     */   public String getModeString(byte mode)
/*     */   {
/* 760 */     if (mode == 1)
/* 761 */       return "IN";
/* 762 */     if (mode == 3) {
/* 763 */       return "INOUT";
/*     */     }
/* 765 */     return "OUT";
/*     */   }
/*     */ 
/*     */   protected PrintWriter getPrintWriter(String filename)
/*     */     throws IOException
/*     */   {
/* 778 */     File file = new File(filename);
/* 779 */     File parent = new File(file.getParent());
/*     */ 
/* 781 */     parent.mkdirs();
/*     */ 
/* 783 */     FileOutputStream out = new FileOutputStream(file);
/* 784 */     OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
/*     */ 
/* 786 */     return new PrintWriter(writer);
/*     */   }
/*     */ 
/*     */   String getMepString(OperationType mep)
/*     */   {
/* 796 */     return (String)mepStrings.get(mep.toString());
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 791 */     mepStrings.put(OperationType.REQUEST_RESPONSE.toString(), "request-response");
/* 792 */     mepStrings.put(OperationType.ONE_WAY.toString(), "oneway");
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.JavaDeployWriter
 * JD-Core Version:    0.6.0
 */