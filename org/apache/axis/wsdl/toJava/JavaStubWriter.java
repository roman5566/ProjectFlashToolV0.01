/*      */ package org.apache.axis.wsdl.toJava;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.PrintWriter;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Vector;
/*      */ import javax.wsdl.Binding;
/*      */ import javax.wsdl.BindingOperation;
/*      */ import javax.wsdl.Fault;
/*      */ import javax.wsdl.Input;
/*      */ import javax.wsdl.Message;
/*      */ import javax.wsdl.Operation;
/*      */ import javax.wsdl.OperationType;
/*      */ import javax.wsdl.Part;
/*      */ import javax.wsdl.PortType;
/*      */ import javax.wsdl.extensions.UnknownExtensibilityElement;
/*      */ import javax.wsdl.extensions.soap.SOAPBinding;
/*      */ import javax.wsdl.extensions.soap.SOAPOperation;
/*      */ import javax.xml.namespace.QName;
/*      */ import org.apache.axis.components.logger.LogFactory;
/*      */ import org.apache.axis.constants.Style;
/*      */ import org.apache.axis.constants.Use;
/*      */ import org.apache.axis.utils.Messages;
/*      */ import org.apache.axis.utils.StringUtils;
/*      */ import org.apache.axis.wsdl.symbolTable.BindingEntry;
/*      */ import org.apache.axis.wsdl.symbolTable.CollectionType;
/*      */ import org.apache.axis.wsdl.symbolTable.DefinedType;
/*      */ import org.apache.axis.wsdl.symbolTable.FaultInfo;
/*      */ import org.apache.axis.wsdl.symbolTable.Parameter;
/*      */ import org.apache.axis.wsdl.symbolTable.Parameters;
/*      */ import org.apache.axis.wsdl.symbolTable.SchemaUtils;
/*      */ import org.apache.axis.wsdl.symbolTable.SymbolTable;
/*      */ import org.apache.axis.wsdl.symbolTable.TypeEntry;
/*      */ import org.apache.commons.logging.Log;
/*      */ import org.w3c.dom.Element;
/*      */ 
/*      */ public class JavaStubWriter extends JavaClassWriter
/*      */ {
/*   70 */   protected static Log log = LogFactory.getLog(JavaStubWriter.class.getName());
/*      */   private BindingEntry bEntry;
/*      */   private Binding binding;
/*      */   private SymbolTable symbolTable;
/*      */   private static final int MAXIMUM_BINDINGS_PER_METHOD = 100;
/*   90 */   static String[] modeStrings = { "", "org.apache.axis.description.ParameterDesc.IN", "org.apache.axis.description.ParameterDesc.OUT", "org.apache.axis.description.ParameterDesc.INOUT" };
/*      */ 
/*   96 */   static Map styles = new HashMap();
/*      */ 
/*   99 */   static Map uses = new HashMap();
/*      */   static int OPERDESC_PER_BLOCK;
/*      */ 
/*      */   public JavaStubWriter(Emitter emitter, BindingEntry bEntry, SymbolTable symbolTable)
/*      */   {
/*  123 */     super(emitter, bEntry.getName() + "Stub", "stub");
/*      */ 
/*  125 */     this.bEntry = bEntry;
/*  126 */     this.binding = bEntry.getBinding();
/*  127 */     this.symbolTable = symbolTable;
/*      */   }
/*      */ 
/*      */   protected String getExtendsText()
/*      */   {
/*  136 */     return "extends org.apache.axis.client.Stub ";
/*      */   }
/*      */ 
/*      */   protected String getImplementsText()
/*      */   {
/*  145 */     return "implements " + this.bEntry.getDynamicVar(JavaBindingWriter.INTERFACE_NAME) + " ";
/*      */   }
/*      */ 
/*      */   protected void writeFileBody(PrintWriter pw)
/*      */     throws IOException
/*      */   {
/*  157 */     PortType portType = this.binding.getPortType();
/*  158 */     HashSet types = getTypesInPortType(portType);
/*  159 */     boolean hasMIME = Utils.hasMIME(this.bEntry);
/*      */ 
/*  161 */     if ((types.size() > 0) || (hasMIME)) {
/*  162 */       pw.println("    private java.util.Vector cachedSerClasses = new java.util.Vector();");
/*      */ 
/*  164 */       pw.println("    private java.util.Vector cachedSerQNames = new java.util.Vector();");
/*      */ 
/*  166 */       pw.println("    private java.util.Vector cachedSerFactories = new java.util.Vector();");
/*      */ 
/*  168 */       pw.println("    private java.util.Vector cachedDeserFactories = new java.util.Vector();");
/*      */     }
/*      */ 
/*  172 */     pw.println();
/*  173 */     pw.println("    static org.apache.axis.description.OperationDesc [] _operations;");
/*      */ 
/*  175 */     pw.println();
/*  176 */     writeOperationMap(pw);
/*  177 */     pw.println();
/*  178 */     pw.println("    public " + this.className + "() throws org.apache.axis.AxisFault {");
/*      */ 
/*  180 */     pw.println("         this(null);");
/*  181 */     pw.println("    }");
/*  182 */     pw.println();
/*  183 */     pw.println("    public " + this.className + "(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {");
/*      */ 
/*  186 */     pw.println("         this(service);");
/*  187 */     pw.println("         super.cachedEndpoint = endpointURL;");
/*  188 */     pw.println("    }");
/*  189 */     pw.println();
/*  190 */     pw.println("    public " + this.className + "(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {");
/*      */ 
/*  193 */     pw.println("        if (service == null) {");
/*  194 */     pw.println("            super.service = new org.apache.axis.client.Service();");
/*      */ 
/*  196 */     pw.println("        } else {");
/*  197 */     pw.println("            super.service = service;");
/*  198 */     pw.println("        }");
/*  199 */     pw.println("        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion(\"" + this.emitter.getTypeMappingVersion() + "\");");
/*      */ 
/*  201 */     List deferredBindings = new ArrayList();
/*      */ 
/*  204 */     int typeMappingCount = 0;
/*      */ 
/*  206 */     if (types.size() > 0) {
/*  207 */       Iterator it = types.iterator();
/*      */ 
/*  209 */       while (it.hasNext()) {
/*  210 */         TypeEntry type = (TypeEntry)it.next();
/*      */ 
/*  212 */         if (!Utils.shouldEmit(type))
/*      */         {
/*      */           continue;
/*      */         }
/*      */ 
/*  217 */         if (typeMappingCount == 0) {
/*  218 */           writeSerializationDecls(pw, hasMIME, this.binding.getQName().getNamespaceURI());
/*      */         }
/*      */ 
/*  224 */         deferredBindings.add(type);
/*      */ 
/*  227 */         typeMappingCount++;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  232 */     Collections.sort(deferredBindings, new Comparator() {
/*      */       public int compare(Object a, Object b) {
/*  234 */         TypeEntry type1 = (TypeEntry)a;
/*  235 */         TypeEntry type2 = (TypeEntry)b;
/*  236 */         return type1.getQName().toString().compareToIgnoreCase(type2.getQName().toString());
/*      */       }
/*      */     });
/*  242 */     if ((typeMappingCount == 0) && (hasMIME)) {
/*  243 */       writeSerializationDecls(pw, hasMIME, this.binding.getQName().getNamespaceURI());
/*      */ 
/*  246 */       typeMappingCount++;
/*      */     }
/*      */ 
/*  251 */     boolean needsMultipleBindingMethods = false;
/*      */     Iterator it;
/*  253 */     if (deferredBindings.size() < 100)
/*      */     {
/*  256 */       for (it = deferredBindings.iterator(); it.hasNext(); )
/*  257 */         writeSerializationInit(pw, (TypeEntry)it.next());
/*      */     }
/*      */     else {
/*  260 */       needsMultipleBindingMethods = true;
/*      */ 
/*  262 */       int methodCount = calculateBindingMethodCount(deferredBindings);
/*      */ 
/*  266 */       for (int i = 0; i < methodCount; i++) {
/*  267 */         pw.println("        addBindings" + i + "();");
/*      */       }
/*      */     }
/*      */ 
/*  271 */     pw.println("    }");
/*  272 */     pw.println();
/*      */ 
/*  275 */     if (needsMultipleBindingMethods) {
/*  276 */       writeBindingMethods(pw, deferredBindings);
/*  277 */       pw.println();
/*      */     }
/*      */ 
/*  280 */     pw.println("    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {");
/*      */ 
/*  282 */     pw.println("        try {");
/*  283 */     pw.println("            org.apache.axis.client.Call _call = super._createCall();");
/*  284 */     pw.println("            if (super.maintainSessionSet) {");
/*  285 */     pw.println("                _call.setMaintainSession(super.maintainSession);");
/*      */ 
/*  287 */     pw.println("            }");
/*  288 */     pw.println("            if (super.cachedUsername != null) {");
/*  289 */     pw.println("                _call.setUsername(super.cachedUsername);");
/*  290 */     pw.println("            }");
/*  291 */     pw.println("            if (super.cachedPassword != null) {");
/*  292 */     pw.println("                _call.setPassword(super.cachedPassword);");
/*  293 */     pw.println("            }");
/*  294 */     pw.println("            if (super.cachedEndpoint != null) {");
/*  295 */     pw.println("                _call.setTargetEndpointAddress(super.cachedEndpoint);");
/*      */ 
/*  297 */     pw.println("            }");
/*  298 */     pw.println("            if (super.cachedTimeout != null) {");
/*  299 */     pw.println("                _call.setTimeout(super.cachedTimeout);");
/*  300 */     pw.println("            }");
/*  301 */     pw.println("            if (super.cachedPortName != null) {");
/*  302 */     pw.println("                _call.setPortName(super.cachedPortName);");
/*  303 */     pw.println("            }");
/*  304 */     pw.println("            java.util.Enumeration keys = super.cachedProperties.keys();");
/*      */ 
/*  306 */     pw.println("            while (keys.hasMoreElements()) {");
/*  307 */     pw.println("                java.lang.String key = (java.lang.String) keys.nextElement();");
/*      */ 
/*  309 */     pw.println("                _call.setProperty(key, super.cachedProperties.get(key));");
/*      */ 
/*  311 */     pw.println("            }");
/*      */ 
/*  313 */     if (typeMappingCount > 0) {
/*  314 */       pw.println("            // " + Messages.getMessage("typeMap00"));
/*  315 */       pw.println("            // " + Messages.getMessage("typeMap01"));
/*  316 */       pw.println("            // " + Messages.getMessage("typeMap02"));
/*  317 */       pw.println("            // " + Messages.getMessage("typeMap03"));
/*  318 */       pw.println("            // " + Messages.getMessage("typeMap04"));
/*  319 */       pw.println("            synchronized (this) {");
/*  320 */       pw.println("                if (firstCall()) {");
/*      */ 
/*  324 */       pw.println("                    // " + Messages.getMessage("mustSetStyle"));
/*      */ 
/*  327 */       if (this.bEntry.hasLiteral()) {
/*  328 */         pw.println("                    _call.setEncodingStyle(null);");
/*      */       } else {
/*  330 */         Iterator iterator = this.bEntry.getBinding().getExtensibilityElements().iterator();
/*      */ 
/*  333 */         while (iterator.hasNext()) {
/*  334 */           Object obj = iterator.next();
/*      */ 
/*  336 */           if ((obj instanceof SOAPBinding)) {
/*  337 */             pw.println("                    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);");
/*      */ 
/*  339 */             pw.println("                    _call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP11_ENC);");
/*      */           }
/*  341 */           else if ((obj instanceof UnknownExtensibilityElement))
/*      */           {
/*  344 */             UnknownExtensibilityElement unkElement = (UnknownExtensibilityElement)obj;
/*      */ 
/*  346 */             QName name = unkElement.getElementType();
/*      */ 
/*  349 */             if ((name.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/")) && (name.getLocalPart().equals("binding")))
/*      */             {
/*  352 */               pw.println("                    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);");
/*      */ 
/*  354 */               pw.println("                    _call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP12_ENC);");
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  361 */       pw.println("                    for (int i = 0; i < cachedSerFactories.size(); ++i) {");
/*      */ 
/*  363 */       pw.println("                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);");
/*      */ 
/*  365 */       pw.println("                        javax.xml.namespace.QName qName =");
/*      */ 
/*  367 */       pw.println("                                (javax.xml.namespace.QName) cachedSerQNames.get(i);");
/*      */ 
/*  369 */       pw.println("                        java.lang.Object x = cachedSerFactories.get(i);");
/*      */ 
/*  371 */       pw.println("                        if (x instanceof Class) {");
/*      */ 
/*  373 */       pw.println("                            java.lang.Class sf = (java.lang.Class)");
/*      */ 
/*  375 */       pw.println("                                 cachedSerFactories.get(i);");
/*      */ 
/*  377 */       pw.println("                            java.lang.Class df = (java.lang.Class)");
/*      */ 
/*  379 */       pw.println("                                 cachedDeserFactories.get(i);");
/*      */ 
/*  381 */       pw.println("                            _call.registerTypeMapping(cls, qName, sf, df, false);");
/*      */ 
/*  384 */       pw.println("                        }");
/*  385 */       pw.println("                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {");
/*      */ 
/*  387 */       pw.println("                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)");
/*      */ 
/*  389 */       pw.println("                                 cachedSerFactories.get(i);");
/*      */ 
/*  391 */       pw.println("                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)");
/*      */ 
/*  393 */       pw.println("                                 cachedDeserFactories.get(i);");
/*      */ 
/*  395 */       pw.println("                            _call.registerTypeMapping(cls, qName, sf, df, false);");
/*      */ 
/*  398 */       pw.println("                        }");
/*  399 */       pw.println("                    }");
/*  400 */       pw.println("                }");
/*  401 */       pw.println("            }");
/*      */     }
/*      */ 
/*  404 */     pw.println("            return _call;");
/*  405 */     pw.println("        }");
/*  406 */     pw.println("        catch (java.lang.Throwable _t) {");
/*  407 */     pw.println("            throw new org.apache.axis.AxisFault(\"" + Messages.getMessage("badCall01") + "\", _t);");
/*      */ 
/*  409 */     pw.println("        }");
/*  410 */     pw.println("    }");
/*  411 */     pw.println();
/*      */ 
/*  413 */     List operations = this.binding.getBindingOperations();
/*      */ 
/*  415 */     for (int i = 0; i < operations.size(); i++) {
/*  416 */       BindingOperation operation = (BindingOperation)operations.get(i);
/*  417 */       Parameters parameters = this.bEntry.getParameters(operation.getOperation());
/*      */ 
/*  421 */       String soapAction = "";
/*  422 */       String opStyle = null;
/*  423 */       Iterator operationExtensibilityIterator = operation.getExtensibilityElements().iterator();
/*      */ 
/*  426 */       while (operationExtensibilityIterator.hasNext()) {
/*  427 */         Object obj = operationExtensibilityIterator.next();
/*      */ 
/*  429 */         if ((obj instanceof SOAPOperation)) {
/*  430 */           soapAction = ((SOAPOperation)obj).getSoapActionURI();
/*  431 */           opStyle = ((SOAPOperation)obj).getStyle();
/*      */ 
/*  433 */           break;
/*  434 */         }if ((obj instanceof UnknownExtensibilityElement))
/*      */         {
/*  437 */           UnknownExtensibilityElement unkElement = (UnknownExtensibilityElement)obj;
/*      */ 
/*  439 */           QName name = unkElement.getElementType();
/*      */ 
/*  442 */           if ((name.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/")) && (name.getLocalPart().equals("operation")))
/*      */           {
/*  444 */             if (unkElement.getElement().getAttribute("soapAction") != null)
/*      */             {
/*  446 */               soapAction = unkElement.getElement().getAttribute("soapAction");
/*      */             }
/*      */ 
/*  450 */             opStyle = unkElement.getElement().getAttribute("style");
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  455 */       Operation ptOperation = operation.getOperation();
/*  456 */       OperationType type = ptOperation.getStyle();
/*      */ 
/*  460 */       if ((OperationType.NOTIFICATION.equals(type)) || (OperationType.SOLICIT_RESPONSE.equals(type)))
/*      */       {
/*  462 */         pw.println(parameters.signature);
/*  463 */         pw.println();
/*      */       } else {
/*  465 */         writeOperation(pw, operation, parameters, soapAction, opStyle, type == OperationType.ONE_WAY, i);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private int calculateBindingMethodCount(List deferredBindings)
/*      */   {
/*  480 */     int methodCount = deferredBindings.size() / 100;
/*      */ 
/*  482 */     if (deferredBindings.size() % 100 != 0) {
/*  483 */       methodCount++;
/*      */     }
/*      */ 
/*  486 */     return methodCount;
/*      */   }
/*      */ 
/*      */   protected void writeBindingMethods(PrintWriter pw, List deferredBindings)
/*      */   {
/*  502 */     int methodCount = calculateBindingMethodCount(deferredBindings);
/*      */ 
/*  504 */     for (int i = 0; i < methodCount; i++) {
/*  505 */       pw.println("    private void addBindings" + i + "() {");
/*      */ 
/*  509 */       writeSerializationDecls(pw, false, null);
/*      */ 
/*  511 */       for (int j = 0; j < 100; j++) {
/*  512 */         int absolute = i * 100 + j;
/*      */ 
/*  514 */         if (absolute == deferredBindings.size())
/*      */         {
/*      */           break;
/*      */         }
/*  518 */         writeSerializationInit(pw, (TypeEntry)deferredBindings.get(absolute));
/*      */       }
/*      */ 
/*  522 */       pw.println("    }");
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeOperationMap(PrintWriter pw)
/*      */   {
/*  533 */     List operations = this.binding.getBindingOperations();
/*      */ 
/*  535 */     pw.println("    static {");
/*  536 */     pw.println("        _operations = new org.apache.axis.description.OperationDesc[" + operations.size() + "];");
/*      */ 
/*  540 */     int j = 0; for (int k = 0; j < operations.size(); j++) {
/*  541 */       if (j % OPERDESC_PER_BLOCK == 0) {
/*  542 */         k++;
/*      */ 
/*  544 */         pw.println("        _initOperationDesc" + k + "();");
/*      */       }
/*      */     }
/*      */ 
/*  548 */     int i = 0; for (int k = 0; i < operations.size(); i++) {
/*  549 */       if (i % OPERDESC_PER_BLOCK == 0) {
/*  550 */         k++;
/*      */ 
/*  552 */         pw.println("    }\n");
/*  553 */         pw.println("    private static void _initOperationDesc" + k + "(){");
/*      */ 
/*  555 */         pw.println("        org.apache.axis.description.OperationDesc oper;");
/*      */ 
/*  557 */         pw.println("        org.apache.axis.description.ParameterDesc param;");
/*      */       }
/*      */ 
/*  561 */       BindingOperation operation = (BindingOperation)operations.get(i);
/*  562 */       Parameters parameters = this.bEntry.getParameters(operation.getOperation());
/*      */ 
/*  566 */       String opStyle = null;
/*  567 */       Iterator operationExtensibilityIterator = operation.getExtensibilityElements().iterator();
/*      */ 
/*  570 */       while (operationExtensibilityIterator.hasNext()) {
/*  571 */         Object obj = operationExtensibilityIterator.next();
/*      */ 
/*  573 */         if ((obj instanceof SOAPOperation)) {
/*  574 */           opStyle = ((SOAPOperation)obj).getStyle();
/*      */ 
/*  576 */           break;
/*  577 */         }if ((obj instanceof UnknownExtensibilityElement))
/*      */         {
/*  580 */           UnknownExtensibilityElement unkElement = (UnknownExtensibilityElement)obj;
/*      */ 
/*  582 */           QName name = unkElement.getElementType();
/*      */ 
/*  585 */           if ((name.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/")) && (name.getLocalPart().equals("operation")))
/*      */           {
/*  587 */             opStyle = unkElement.getElement().getAttribute("style");
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  592 */       Operation ptOperation = operation.getOperation();
/*  593 */       OperationType type = ptOperation.getStyle();
/*      */ 
/*  597 */       if ((OperationType.NOTIFICATION.equals(type)) || (OperationType.SOLICIT_RESPONSE.equals(type)))
/*      */       {
/*  599 */         pw.println(parameters.signature);
/*  600 */         pw.println();
/*      */       }
/*      */ 
/*  603 */       String operName = operation.getName();
/*  604 */       String indent = "        ";
/*      */ 
/*  606 */       pw.println(indent + "oper = new org.apache.axis.description.OperationDesc();");
/*      */ 
/*  609 */       pw.println(indent + "oper.setName(\"" + operName + "\");");
/*      */ 
/*  612 */       for (int j = 0; j < parameters.list.size(); j++) {
/*  613 */         Parameter p = (Parameter)parameters.list.get(j);
/*      */ 
/*  616 */         QName paramType = Utils.getXSIType(p);
/*      */ 
/*  619 */         String javaType = Utils.getParameterTypeName(p);
/*  620 */         if (javaType != null)
/*  621 */           javaType = javaType + ".class, ";
/*      */         else {
/*  623 */           javaType = "null, ";
/*      */         }
/*      */ 
/*  627 */         String paramNameText = Utils.getNewQNameWithLastLocalPart(p.getQName());
/*  628 */         String paramTypeText = Utils.getNewQName(paramType);
/*      */ 
/*  632 */         boolean isInHeader = p.isInHeader();
/*  633 */         boolean isOutHeader = p.isOutHeader();
/*      */ 
/*  635 */         pw.println("        param = new org.apache.axis.description.ParameterDesc(" + paramNameText + ", " + modeStrings[p.getMode()] + ", " + paramTypeText + ", " + javaType + isInHeader + ", " + isOutHeader + ");");
/*      */ 
/*  642 */         QName itemQName = Utils.getItemQName(p.getType());
/*  643 */         if (itemQName != null) {
/*  644 */           pw.println("        param.setItemQName(" + Utils.getNewQName(itemQName) + ");");
/*      */         }
/*      */ 
/*  648 */         if (p.isOmittable()) {
/*  649 */           pw.println("        param.setOmittable(true);");
/*      */         }
/*  651 */         if (p.isNillable()) {
/*  652 */           pw.println("        param.setNillable(true);");
/*      */         }
/*  654 */         pw.println("        oper.addParameter(param);");
/*      */       }
/*      */ 
/*  658 */       Parameter returnParam = parameters.returnParam;
/*  659 */       if (returnParam != null)
/*      */       {
/*  662 */         QName returnType = Utils.getXSIType(returnParam);
/*      */ 
/*  665 */         String javaType = Utils.getParameterTypeName(returnParam);
/*      */ 
/*  667 */         if (javaType == null)
/*  668 */           javaType = "";
/*      */         else {
/*  670 */           javaType = javaType + ".class";
/*      */         }
/*      */ 
/*  673 */         pw.println("        oper.setReturnType(" + Utils.getNewQName(returnType) + ");");
/*      */ 
/*  675 */         pw.println("        oper.setReturnClass(" + javaType + ");");
/*      */ 
/*  677 */         QName returnQName = returnParam.getQName();
/*      */ 
/*  679 */         if (returnQName != null) {
/*  680 */           pw.println("        oper.setReturnQName(" + Utils.getNewQNameWithLastLocalPart(returnQName) + ");");
/*      */         }
/*      */ 
/*  684 */         if (returnParam.isOutHeader()) {
/*  685 */           pw.println("        oper.setReturnHeader(true);");
/*      */         }
/*      */ 
/*  688 */         QName itemQName = Utils.getItemQName(returnParam.getType());
/*  689 */         if (itemQName != null) {
/*  690 */           pw.println("        param = oper.getReturnParamDesc();");
/*  691 */           pw.println("        param.setItemQName(" + Utils.getNewQName(itemQName) + ");");
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  696 */         pw.println("        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);");
/*      */       }
/*      */ 
/*  700 */       boolean hasMIME = Utils.hasMIME(this.bEntry, operation);
/*  701 */       Style style = Style.getStyle(opStyle, this.bEntry.getBindingStyle());
/*  702 */       Use use = this.bEntry.getInputBodyType(operation.getOperation());
/*      */ 
/*  704 */       if ((style == Style.DOCUMENT) && (this.symbolTable.isWrapped())) {
/*  705 */         style = Style.WRAPPED;
/*      */       }
/*      */ 
/*  708 */       if (!hasMIME) {
/*  709 */         pw.println("        oper.setStyle(" + styles.get(style) + ");");
/*  710 */         pw.println("        oper.setUse(" + uses.get(use) + ");");
/*      */       }
/*      */ 
/*  714 */       writeFaultInfo(pw, operation);
/*  715 */       pw.println(indent + "_operations[" + i + "] = oper;");
/*  716 */       pw.println("");
/*      */     }
/*      */ 
/*  719 */     pw.println("    }");
/*      */   }
/*      */ 
/*      */   private HashSet getTypesInPortType(PortType portType)
/*      */   {
/*  731 */     HashSet types = new HashSet();
/*  732 */     HashSet firstPassTypes = new HashSet();
/*      */ 
/*  735 */     List operations = portType.getOperations();
/*      */ 
/*  737 */     for (int i = 0; i < operations.size(); i++) {
/*  738 */       Operation op = (Operation)operations.get(i);
/*      */ 
/*  740 */       firstPassTypes.addAll(getTypesInOperation(op));
/*      */     }
/*      */ 
/*  745 */     Iterator i = firstPassTypes.iterator();
/*      */ 
/*  747 */     while (i.hasNext()) {
/*  748 */       TypeEntry type = (TypeEntry)i.next();
/*      */ 
/*  750 */       if (!types.contains(type)) {
/*  751 */         types.add(type);
/*  752 */         types.addAll(type.getNestedTypes(this.symbolTable, true));
/*      */       }
/*      */     }
/*      */     Iterator j;
/*  756 */     if (this.emitter.isAllWanted()) {
/*  757 */       HashMap rawSymbolTable = this.symbolTable.getHashMap();
/*  758 */       for (j = rawSymbolTable.values().iterator(); j.hasNext(); ) {
/*  759 */         Vector typeVector = (Vector)j.next();
/*  760 */         for (k = typeVector.iterator(); k.hasNext(); ) {
/*  761 */           Object symbol = k.next();
/*  762 */           if ((symbol instanceof DefinedType)) {
/*  763 */             TypeEntry type = (TypeEntry)symbol;
/*  764 */             if (!types.contains(type))
/*  765 */               types.add(type);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     Iterator k;
/*  771 */     return types;
/*      */   }
/*      */ 
/*      */   private HashSet getTypesInOperation(Operation operation)
/*      */   {
/*  783 */     HashSet types = new HashSet();
/*  784 */     Vector v = new Vector();
/*  785 */     Parameters params = this.bEntry.getParameters(operation);
/*      */ 
/*  788 */     for (int i = 0; i < params.list.size(); i++) {
/*  789 */       Parameter p = (Parameter)params.list.get(i);
/*      */ 
/*  791 */       v.add(p.getType());
/*      */     }
/*      */ 
/*  795 */     if (params.returnParam != null) {
/*  796 */       v.add(params.returnParam.getType());
/*      */     }
/*      */ 
/*  800 */     Map faults = operation.getFaults();
/*      */ 
/*  802 */     if (faults != null) {
/*  803 */       Iterator i = faults.values().iterator();
/*      */ 
/*  805 */       while (i.hasNext()) {
/*  806 */         Fault f = (Fault)i.next();
/*      */ 
/*  808 */         partTypes(v, f.getMessage().getOrderedParts(null));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  813 */     for (int i = 0; i < v.size(); i++) {
/*  814 */       types.add(v.get(i));
/*      */     }
/*      */ 
/*  817 */     return types;
/*      */   }
/*      */ 
/*      */   private void partTypes(Vector v, Collection parts)
/*      */   {
/*  828 */     Iterator i = parts.iterator();
/*      */ 
/*  830 */     while (i.hasNext()) {
/*  831 */       Part part = (Part)i.next();
/*  832 */       QName qType = part.getTypeName();
/*      */ 
/*  834 */       if (qType != null) {
/*  835 */         v.add(this.symbolTable.getType(qType));
/*      */       } else {
/*  837 */         qType = part.getElementName();
/*      */ 
/*  839 */         if (qType != null)
/*  840 */           v.add(this.symbolTable.getElement(qType));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeFaultInfo(PrintWriter pw, BindingOperation bindOp)
/*      */   {
/*  854 */     Map faultMap = this.bEntry.getFaults();
/*      */ 
/*  857 */     ArrayList faults = (ArrayList)faultMap.get(bindOp);
/*      */ 
/*  860 */     if (faults == null) {
/*  861 */       return;
/*      */     }
/*      */ 
/*  865 */     for (Iterator faultIt = faults.iterator(); faultIt.hasNext(); ) {
/*  866 */       FaultInfo info = (FaultInfo)faultIt.next();
/*  867 */       QName qname = info.getQName();
/*  868 */       Message message = info.getMessage();
/*      */ 
/*  871 */       if (qname == null)
/*      */       {
/*      */         continue;
/*      */       }
/*      */ 
/*  876 */       String className = Utils.getFullExceptionName(message, this.symbolTable);
/*      */ 
/*  879 */       pw.println("        oper.addFault(new org.apache.axis.description.FaultDesc(");
/*      */ 
/*  881 */       pw.println("                      " + Utils.getNewQName(qname) + ",");
/*      */ 
/*  883 */       pw.println("                      \"" + className + "\",");
/*  884 */       pw.println("                      " + Utils.getNewQName(info.getXMLType()) + ", ");
/*      */ 
/*  886 */       pw.println("                      " + Utils.isFaultComplex(message, this.symbolTable));
/*      */ 
/*  888 */       pw.println("                     ));");
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeSerializationDecls(PrintWriter pw, boolean hasMIME, String namespace)
/*      */   {
/*  902 */     pw.println("            java.lang.Class cls;");
/*  903 */     pw.println("            javax.xml.namespace.QName qName;");
/*  904 */     pw.println("            javax.xml.namespace.QName qName2;");
/*  905 */     pw.println("            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;");
/*      */ 
/*  907 */     pw.println("            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;");
/*      */ 
/*  909 */     pw.println("            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;");
/*      */ 
/*  911 */     pw.println("            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;");
/*      */ 
/*  913 */     pw.println("            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;");
/*      */ 
/*  915 */     pw.println("            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;");
/*      */ 
/*  917 */     pw.println("            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;");
/*      */ 
/*  919 */     pw.println("            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;");
/*      */ 
/*  921 */     pw.println("            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;");
/*      */ 
/*  923 */     pw.println("            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;");
/*      */ 
/*  926 */     if (hasMIME) {
/*  927 */       pw.println("            java.lang.Class mimesf = org.apache.axis.encoding.ser.JAFDataHandlerSerializerFactory.class;");
/*      */ 
/*  929 */       pw.println("            java.lang.Class mimedf = org.apache.axis.encoding.ser.JAFDataHandlerDeserializerFactory.class;");
/*      */ 
/*  931 */       pw.println();
/*      */ 
/*  933 */       QName qname = new QName(namespace, "DataHandler");
/*      */ 
/*  935 */       pw.println("            qName = new javax.xml.namespace.QName(\"" + qname.getNamespaceURI() + "\", \"" + qname.getLocalPart() + "\");");
/*      */ 
/*  938 */       pw.println("            cachedSerQNames.add(qName);");
/*  939 */       pw.println("            cls = javax.activation.DataHandler.class;");
/*  940 */       pw.println("            cachedSerClasses.add(cls);");
/*  941 */       pw.println("            cachedSerFactories.add(mimesf);");
/*  942 */       pw.println("            cachedDeserFactories.add(mimedf);");
/*  943 */       pw.println();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeSerializationInit(PrintWriter pw, TypeEntry type)
/*      */   {
/*  955 */     QName qname = type.getQName();
/*      */ 
/*  957 */     pw.println("            qName = new javax.xml.namespace.QName(\"" + qname.getNamespaceURI() + "\", \"" + qname.getLocalPart() + "\");");
/*      */ 
/*  960 */     pw.println("            cachedSerQNames.add(qName);");
/*  961 */     pw.println("            cls = " + type.getName() + ".class;");
/*  962 */     pw.println("            cachedSerClasses.add(cls);");
/*      */ 
/*  964 */     if (type.getName().endsWith("[]")) {
/*  965 */       if (SchemaUtils.isListWithItemType(type.getNode())) {
/*  966 */         pw.println("            cachedSerFactories.add(simplelistsf);");
/*  967 */         pw.println("            cachedDeserFactories.add(simplelistdf);");
/*      */       }
/*  971 */       else if (type.getComponentType() != null) {
/*  972 */         QName ct = type.getComponentType();
/*  973 */         QName name = type.getItemQName();
/*  974 */         pw.println("            qName = new javax.xml.namespace.QName(\"" + ct.getNamespaceURI() + "\", \"" + ct.getLocalPart() + "\");");
/*      */ 
/*  977 */         if (name != null) {
/*  978 */           pw.println("            qName2 = new javax.xml.namespace.QName(\"" + name.getNamespaceURI() + "\", \"" + name.getLocalPart() + "\");");
/*      */         }
/*      */         else
/*      */         {
/*  982 */           pw.println("            qName2 = null;");
/*      */         }
/*  984 */         pw.println("            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));");
/*  985 */         pw.println("            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());");
/*      */       } else {
/*  987 */         pw.println("            cachedSerFactories.add(arraysf);");
/*  988 */         pw.println("            cachedDeserFactories.add(arraydf);");
/*      */       }
/*      */     }
/*  991 */     else if ((type.getNode() != null) && (Utils.getEnumerationBaseAndValues(type.getNode(), this.symbolTable) != null))
/*      */     {
/*  993 */       pw.println("            cachedSerFactories.add(enumsf);");
/*  994 */       pw.println("            cachedDeserFactories.add(enumdf);");
/*  995 */     } else if (type.isSimpleType()) {
/*  996 */       pw.println("            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));");
/*      */ 
/*  998 */       pw.println("            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));");
/*      */     }
/* 1000 */     else if (type.getBaseType() != null)
/*      */     {
/* 1010 */       pw.println("            cachedSerFactories.add(null);");
/* 1011 */       pw.println("            cachedDeserFactories.add(simpledf);");
/*      */     } else {
/* 1013 */       pw.println("            cachedSerFactories.add(beansf);");
/* 1014 */       pw.println("            cachedDeserFactories.add(beandf);");
/*      */     }
/*      */ 
/* 1017 */     pw.println();
/*      */   }
/*      */ 
/*      */   protected void writeOperation(PrintWriter pw, BindingOperation operation, Parameters parms, String soapAction, String opStyle, boolean oneway, int opIndex)
/*      */   {
/* 1035 */     writeComment(pw, operation.getDocumentationElement(), true);
/*      */ 
/* 1037 */     if (parms.signature == null) {
/* 1038 */       return;
/*      */     }
/* 1040 */     pw.println(parms.signature + " {");
/* 1041 */     pw.println("        if (super.cachedEndpoint == null) {");
/* 1042 */     pw.println("            throw new org.apache.axis.NoEndPointException();");
/*      */ 
/* 1044 */     pw.println("        }");
/* 1045 */     pw.println("        org.apache.axis.client.Call _call = createCall();");
/* 1046 */     pw.println("        _call.setOperation(_operations[" + opIndex + "]);");
/*      */ 
/* 1049 */     if (soapAction != null) {
/* 1050 */       pw.println("        _call.setUseSOAPAction(true);");
/* 1051 */       pw.println("        _call.setSOAPActionURI(\"" + soapAction + "\");");
/*      */     }
/*      */ 
/* 1055 */     boolean hasMIME = Utils.hasMIME(this.bEntry, operation);
/*      */ 
/* 1058 */     Use use = this.bEntry.getInputBodyType(operation.getOperation());
/*      */ 
/* 1060 */     if (use == Use.LITERAL)
/*      */     {
/* 1063 */       pw.println("        _call.setEncodingStyle(null);");
/*      */ 
/* 1066 */       pw.println("        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);");
/*      */     }
/*      */ 
/* 1070 */     if ((hasMIME) || (use == Use.LITERAL))
/*      */     {
/* 1077 */       pw.println("        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);");
/*      */     }
/*      */ 
/* 1081 */     Style style = Style.getStyle(opStyle, this.bEntry.getBindingStyle());
/*      */ 
/* 1083 */     if ((style == Style.DOCUMENT) && (this.symbolTable.isWrapped())) {
/* 1084 */       style = Style.WRAPPED;
/*      */     }
/*      */ 
/* 1087 */     Iterator iterator = this.bEntry.getBinding().getExtensibilityElements().iterator();
/*      */ 
/* 1090 */     while (iterator.hasNext()) {
/* 1091 */       Object obj = iterator.next();
/*      */ 
/* 1093 */       if ((obj instanceof SOAPBinding)) {
/* 1094 */         pw.println("        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);");
/*      */       }
/* 1096 */       else if ((obj instanceof UnknownExtensibilityElement))
/*      */       {
/* 1099 */         UnknownExtensibilityElement unkElement = (UnknownExtensibilityElement)obj;
/*      */ 
/* 1101 */         QName name = unkElement.getElementType();
/*      */ 
/* 1104 */         if ((name.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/")) && (name.getLocalPart().equals("binding")))
/*      */         {
/* 1106 */           pw.println("        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1113 */     if (style == Style.WRAPPED)
/*      */     {
/* 1118 */       Map partsMap = operation.getOperation().getInput().getMessage().getParts();
/*      */ 
/* 1120 */       Iterator i = partsMap.values().iterator();
/* 1121 */       QName q = null;
/* 1122 */       while ((q == null) && (i.hasNext())) {
/* 1123 */         Part p = (Part)i.next();
/* 1124 */         q = p.getElementName();
/*      */       }
/* 1126 */       if (q != null) {
/* 1127 */         pw.println("        _call.setOperationName(" + Utils.getNewQName(q) + ");");
/*      */       }
/*      */       else
/* 1130 */         log.warn(Messages.getMessage("missingPartsForMessage00", operation.getOperation().getInput().getMessage().getQName().toString()));
/*      */     }
/*      */     else {
/* 1133 */       QName elementQName = Utils.getOperationQName(operation, this.bEntry, this.symbolTable);
/*      */ 
/* 1136 */       if (elementQName != null) {
/* 1137 */         pw.println("        _call.setOperationName(" + Utils.getNewQName(elementQName) + ");");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1142 */     pw.println();
/*      */ 
/* 1145 */     pw.println("        setRequestHeaders(_call);");
/*      */ 
/* 1148 */     pw.println("        setAttachments(_call);");
/*      */ 
/* 1151 */     if (this.bEntry.isOperationDIME(operation.getOperation().getName())) {
/* 1152 */       pw.println("        _call.setProperty(_call.ATTACHMENT_ENCAPSULATION_FORMAT, _call.ATTACHMENT_ENCAPSULATION_FORMAT_DIME);");
/*      */     }
/*      */ 
/* 1157 */     if (oneway) {
/* 1158 */       pw.print("        _call.invokeOneWay(");
/*      */     } else {
/* 1160 */       pw.print(" try {");
/* 1161 */       pw.print("        java.lang.Object _resp = _call.invoke(");
/*      */     }
/*      */ 
/* 1164 */     pw.print("new java.lang.Object[] {");
/* 1165 */     writeParameters(pw, parms);
/* 1166 */     pw.println("});");
/* 1167 */     pw.println();
/*      */ 
/* 1169 */     if (!oneway) {
/* 1170 */       writeResponseHandling(pw, parms);
/*      */     }
/*      */ 
/* 1173 */     pw.println("    }");
/* 1174 */     pw.println();
/*      */   }
/*      */ 
/*      */   protected void writeParameters(PrintWriter pw, Parameters parms)
/*      */   {
/* 1186 */     boolean needComma = false;
/*      */ 
/* 1188 */     for (int i = 0; i < parms.list.size(); i++) {
/* 1189 */       Parameter p = (Parameter)parms.list.get(i);
/*      */ 
/* 1191 */       if (p.getMode() != 2) {
/* 1192 */         if (needComma)
/* 1193 */           pw.print(", ");
/*      */         else {
/* 1195 */           needComma = true;
/*      */         }
/*      */ 
/* 1198 */         String javifiedName = Utils.xmlNameToJava(p.getName());
/*      */ 
/* 1200 */         if (p.getMode() != 1) {
/* 1201 */           javifiedName = javifiedName + ".value";
/*      */         }
/*      */ 
/* 1204 */         if ((p.getMIMEInfo() == null) && (!p.isOmittable())) {
/* 1205 */           javifiedName = Utils.wrapPrimitiveType(p.getType(), javifiedName);
/*      */         }
/*      */ 
/* 1209 */         pw.print(javifiedName);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeResponseHandling(PrintWriter pw, Parameters parms)
/*      */   {
/* 1222 */     pw.println("        if (_resp instanceof java.rmi.RemoteException) {");
/* 1223 */     pw.println("            throw (java.rmi.RemoteException)_resp;");
/* 1224 */     pw.println("        }");
/*      */ 
/* 1226 */     int allOuts = parms.outputs + parms.inouts;
/*      */ 
/* 1228 */     if (allOuts > 0) {
/* 1229 */       pw.println("        else {");
/* 1230 */       pw.println("            extractAttachments(_call);");
/*      */ 
/* 1232 */       if (allOuts == 1) {
/* 1233 */         if (parms.returnParam != null) {
/* 1234 */           writeOutputAssign(pw, "return ", parms.returnParam, "_resp");
/*      */         }
/*      */         else
/*      */         {
/* 1239 */           int i = 0;
/* 1240 */           Parameter p = (Parameter)parms.list.get(i);
/*      */ 
/* 1242 */           while (p.getMode() == 1) {
/* 1243 */             i++; p = (Parameter)parms.list.get(i);
/*      */           }
/*      */ 
/* 1246 */           String javifiedName = Utils.xmlNameToJava(p.getName());
/* 1247 */           String qnameName = Utils.getNewQNameWithLastLocalPart(p.getQName());
/*      */ 
/* 1249 */           pw.println("            java.util.Map _output;");
/* 1250 */           pw.println("            _output = _call.getOutputParams();");
/*      */ 
/* 1252 */           writeOutputAssign(pw, javifiedName + ".value = ", p, "_output.get(" + qnameName + ")");
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1260 */         pw.println("            java.util.Map _output;");
/* 1261 */         pw.println("            _output = _call.getOutputParams();");
/*      */ 
/* 1263 */         for (int i = 0; i < parms.list.size(); i++) {
/* 1264 */           Parameter p = (Parameter)parms.list.get(i);
/* 1265 */           String javifiedName = Utils.xmlNameToJava(p.getName());
/* 1266 */           String qnameName = Utils.getNewQNameWithLastLocalPart(p.getQName());
/*      */ 
/* 1269 */           if (p.getMode() != 1) {
/* 1270 */             writeOutputAssign(pw, javifiedName + ".value = ", p, "_output.get(" + qnameName + ")");
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1277 */         if (parms.returnParam != null) {
/* 1278 */           writeOutputAssign(pw, "return ", parms.returnParam, "_resp");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1285 */       pw.println("        }");
/*      */     } else {
/* 1287 */       pw.println("        extractAttachments(_call);");
/*      */     }
/*      */ 
/* 1292 */     Map faults = parms.faults;
/*      */ 
/* 1294 */     List exceptionsThrowsList = new ArrayList();
/*      */ 
/* 1296 */     int index = parms.signature.indexOf("throws");
/* 1297 */     if (index != -1) {
/* 1298 */       String[] thrExcep = StringUtils.split(parms.signature.substring(index + 6), ',');
/* 1299 */       for (int i = 0; i < thrExcep.length; i++) {
/* 1300 */         exceptionsThrowsList.add(thrExcep[i].trim());
/*      */       }
/*      */     }
/* 1303 */     pw.println("  } catch (org.apache.axis.AxisFault axisFaultException) {");
/* 1304 */     if ((faults != null) && (faults.size() > 0)) {
/* 1305 */       pw.println("    if (axisFaultException.detail != null) {");
/* 1306 */       Iterator faultIt = exceptionsThrowsList.iterator();
/* 1307 */       while (faultIt.hasNext()) {
/* 1308 */         String exceptionFullName = (String)faultIt.next();
/* 1309 */         pw.println("        if (axisFaultException.detail instanceof " + exceptionFullName + ") {");
/*      */ 
/* 1311 */         pw.println("              throw (" + exceptionFullName + ") axisFaultException.detail;");
/*      */ 
/* 1313 */         pw.println("         }");
/*      */       }
/* 1315 */       pw.println("   }");
/*      */     }
/* 1317 */     pw.println("  throw axisFaultException;");
/* 1318 */     pw.println("}");
/*      */   }
/*      */ 
/*      */   protected void writeOutputAssign(PrintWriter pw, String target, Parameter param, String source)
/*      */   {
/* 1332 */     TypeEntry type = param.getType();
/*      */ 
/* 1334 */     if ((type != null) && (type.getName() != null))
/*      */     {
/* 1336 */       String typeName = type.getName();
/*      */ 
/* 1339 */       if (((param.isOmittable()) && (param.getType().getDimensions().equals(""))) || (((param.getType() instanceof CollectionType)) && (((CollectionType)param.getType()).isWrapped())) || (param.getType().getUnderlTypeNillable()))
/*      */       {
/* 1344 */         typeName = Utils.getWrapperType(type);
/*      */       }
/*      */ 
/* 1349 */       pw.println("            try {");
/* 1350 */       pw.println("                " + target + Utils.getResponseString(param, source));
/*      */ 
/* 1352 */       pw.println("            } catch (java.lang.Exception _exception) {");
/*      */ 
/* 1354 */       pw.println("                " + target + Utils.getResponseString(param, new StringBuffer().append("org.apache.axis.utils.JavaUtils.convert(").append(source).append(", ").append(typeName).append(".class)").toString()));
/*      */ 
/* 1359 */       pw.println("            }");
/*      */     } else {
/* 1361 */       pw.println("              " + target + Utils.getResponseString(param, source));
/*      */     }
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  102 */     styles.put(Style.DOCUMENT, "org.apache.axis.constants.Style.DOCUMENT");
/*  103 */     styles.put(Style.RPC, "org.apache.axis.constants.Style.RPC");
/*  104 */     styles.put(Style.MESSAGE, "org.apache.axis.constants.Style.MESSAGE");
/*  105 */     styles.put(Style.WRAPPED, "org.apache.axis.constants.Style.WRAPPED");
/*  106 */     uses.put(Use.ENCODED, "org.apache.axis.constants.Use.ENCODED");
/*  107 */     uses.put(Use.LITERAL, "org.apache.axis.constants.Use.LITERAL");
/*      */ 
/*  111 */     OPERDESC_PER_BLOCK = 10;
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.JavaStubWriter
 * JD-Core Version:    0.6.0
 */