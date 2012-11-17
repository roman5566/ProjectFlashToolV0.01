/*     */ package org.apache.axis.wsdl.toJava;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLStreamHandler;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import javax.wsdl.Binding;
/*     */ import javax.wsdl.Port;
/*     */ import javax.wsdl.PortType;
/*     */ import javax.wsdl.Service;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.rpc.ServiceException;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.WSDLUtils;
/*     */ import org.apache.axis.wsdl.symbolTable.BackslashUtil;
/*     */ import org.apache.axis.wsdl.symbolTable.BindingEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.PortTypeEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.ServiceEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.SymbolTable;
/*     */ 
/*     */ public class JavaServiceImplWriter extends JavaClassWriter
/*     */ {
/*     */   private ServiceEntry sEntry;
/*     */   private SymbolTable symbolTable;
/*     */ 
/*     */   protected JavaServiceImplWriter(Emitter emitter, ServiceEntry sEntry, SymbolTable symbolTable)
/*     */   {
/*  61 */     super(emitter, sEntry.getName() + "Locator", "service");
/*     */ 
/*  63 */     this.sEntry = sEntry;
/*  64 */     this.symbolTable = symbolTable;
/*     */   }
/*     */ 
/*     */   protected String getExtendsText()
/*     */   {
/*  73 */     return "extends org.apache.axis.client.Service ";
/*     */   }
/*     */ 
/*     */   protected String getImplementsText()
/*     */   {
/*  82 */     return "implements " + this.sEntry.getName() + ' ';
/*     */   }
/*     */ 
/*     */   protected void writeFileBody(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/*  93 */     Service service = this.sEntry.getService();
/*     */ 
/*  96 */     writeComment(pw, service.getDocumentationElement(), false);
/*     */ 
/*  99 */     Vector getPortIfaces = new Vector();
/* 100 */     Vector getPortStubClasses = new Vector();
/* 101 */     Vector getPortPortNames = new Vector();
/* 102 */     Vector getPortPortXmlNames = new Vector();
/* 103 */     boolean printGetPortNotice = false;
/*     */ 
/* 106 */     Map portMap = service.getPorts();
/* 107 */     Iterator portIterator = portMap.values().iterator();
/*     */ 
/* 110 */     writeConstructors(pw);
/*     */ 
/* 113 */     while (portIterator.hasNext()) {
/* 114 */       Port p = (Port)portIterator.next();
/* 115 */       Binding binding = p.getBinding();
/*     */ 
/* 117 */       if (binding == null) {
/* 118 */         throw new IOException(Messages.getMessage("emitFailNoBinding01", new String[] { p.getName() }));
/*     */       }
/*     */ 
/* 123 */       BindingEntry bEntry = this.symbolTable.getBindingEntry(binding.getQName());
/*     */ 
/* 126 */       if (bEntry == null) {
/* 127 */         throw new IOException(Messages.getMessage("emitFailNoBindingEntry01", new String[] { binding.getQName().toString() }));
/*     */       }
/*     */ 
/* 133 */       PortTypeEntry ptEntry = this.symbolTable.getPortTypeEntry(binding.getPortType().getQName());
/*     */ 
/* 136 */       if (ptEntry == null) {
/* 137 */         throw new IOException(Messages.getMessage("emitFailNoPortType01", new String[] { binding.getPortType().getQName().toString() }));
/*     */       }
/*     */ 
/* 145 */       if (bEntry.getBindingType() != 0)
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/* 158 */       String portXmlName = p.getName();
/* 159 */       String portName = (String)bEntry.getDynamicVar("port name:" + p.getName());
/* 160 */       if (portName == null) {
/* 161 */         portName = p.getName();
/*     */       }
/*     */ 
/* 164 */       if (!JavaUtils.isJavaId(portName)) {
/* 165 */         portName = Utils.xmlNameToJavaClass(portName);
/*     */       }
/*     */ 
/* 168 */       String stubClass = bEntry.getName() + "Stub";
/* 169 */       String bindingType = (String)bEntry.getDynamicVar(JavaBindingWriter.INTERFACE_NAME);
/*     */ 
/* 174 */       if (getPortIfaces.contains(bindingType)) {
/* 175 */         printGetPortNotice = true;
/*     */       }
/*     */ 
/* 178 */       getPortIfaces.add(bindingType);
/* 179 */       getPortPortXmlNames.add(portXmlName);
/* 180 */       getPortStubClasses.add(stubClass);
/* 181 */       getPortPortNames.add(portName);
/*     */ 
/* 184 */       String address = WSDLUtils.getAddressFromPort(p);
/*     */ 
/* 186 */       if (address == null)
/*     */       {
/* 189 */         throw new IOException(Messages.getMessage("emitFail02", portName, this.className));
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 194 */         new URL(address);
/*     */       }
/*     */       catch (MalformedURLException e)
/*     */       {
/* 200 */         URL url = null;
/* 201 */         URLStreamHandler handler = null;
/* 202 */         String handlerPkgs = System.getProperty("java.protocol.handler.pkgs");
/*     */ 
/* 205 */         if (handlerPkgs != null) {
/* 206 */           int protIndex = address.indexOf(":");
/*     */ 
/* 208 */           if (protIndex > 0) {
/* 209 */             String protocol = address.substring(0, protIndex);
/*     */ 
/* 211 */             StringTokenizer st = new StringTokenizer(handlerPkgs, "|");
/*     */ 
/* 214 */             while (st.hasMoreTokens()) {
/* 215 */               String pkg = st.nextToken();
/* 216 */               String handlerClass = pkg + "." + protocol + ".Handler";
/*     */               try
/*     */               {
/* 220 */                 Class c = Class.forName(handlerClass);
/*     */ 
/* 222 */                 handler = (URLStreamHandler)c.newInstance();
/*     */ 
/* 224 */                 url = new URL(null, address, handler);
/*     */               }
/*     */               catch (Exception e2)
/*     */               {
/* 229 */                 url = null;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/* 235 */         if (url == null) {
/* 236 */           if (this.emitter.isAllowInvalidURL())
/*     */           {
/* 238 */             System.err.println(Messages.getMessage("emitWarnInvalidURL01", new String[] { portName, this.className, address }));
/*     */           }
/*     */           else
/*     */           {
/* 242 */             throw new IOException(Messages.getMessage("emitFail03", new String[] { portName, this.className, address }));
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 251 */       writeAddressInfo(pw, portName, address, p);
/*     */ 
/* 253 */       String wsddServiceName = portName + "WSDDServiceName";
/*     */ 
/* 255 */       writeWSDDServiceNameInfo(pw, wsddServiceName, portName, portXmlName);
/* 256 */       writeGetPortName(pw, bindingType, portName);
/* 257 */       writeGetPortNameURL(pw, bindingType, portName, stubClass, wsddServiceName);
/*     */ 
/* 259 */       writeSetPortEndpointAddress(pw, portName);
/*     */     }
/*     */ 
/* 262 */     writeGetPortClass(pw, getPortIfaces, getPortStubClasses, getPortPortNames, printGetPortNotice);
/*     */ 
/* 264 */     writeGetPortQNameClass(pw, getPortPortNames, getPortPortXmlNames);
/* 265 */     writeGetServiceName(pw, this.sEntry.getQName());
/* 266 */     writeGetPorts(pw, this.sEntry.getQName().getNamespaceURI(), getPortPortXmlNames);
/* 267 */     writeSetEndpointAddress(pw, getPortPortNames);
/*     */   }
/*     */ 
/*     */   protected void writeConstructors(PrintWriter pw)
/*     */   {
/* 277 */     pw.println();
/* 278 */     pw.println("    public " + Utils.getJavaLocalName(this.sEntry.getName()) + "Locator() {");
/*     */ 
/* 280 */     pw.println("    }");
/* 281 */     pw.println();
/*     */ 
/* 284 */     pw.println();
/* 285 */     pw.println("    public " + Utils.getJavaLocalName(this.sEntry.getName()) + "Locator(org.apache.axis.EngineConfiguration config) {");
/*     */ 
/* 287 */     pw.println("        super(config);");
/* 288 */     pw.println("    }");
/*     */ 
/* 291 */     pw.println();
/* 292 */     pw.println("    public " + Utils.getJavaLocalName(this.sEntry.getName()) + "Locator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) " + "throws " + ServiceException.class.getName() + " {");
/*     */ 
/* 295 */     pw.println("        super(wsdlLoc, sName);");
/* 296 */     pw.println("    }");
/*     */   }
/*     */ 
/*     */   protected void writeAddressInfo(PrintWriter pw, String portName, String address, Port p)
/*     */   {
/* 311 */     pw.println();
/* 312 */     pw.println("    // " + Messages.getMessage("getProxy00", portName));
/* 313 */     writeComment(pw, p.getDocumentationElement(), true);
/* 314 */     pw.println("    private java.lang.String " + portName + "_address = \"" + address + "\";");
/*     */ 
/* 318 */     pw.println();
/* 319 */     pw.println("    public java.lang.String get" + portName + "Address() {");
/*     */ 
/* 321 */     pw.println("        return " + portName + "_address;");
/* 322 */     pw.println("    }");
/* 323 */     pw.println();
/*     */   }
/*     */ 
/*     */   protected void writeWSDDServiceNameInfo(PrintWriter pw, String wsddServiceName, String portName, String portXmlName)
/*     */   {
/* 338 */     pw.println("    // " + Messages.getMessage("wsddServiceName00"));
/* 339 */     pw.println("    private java.lang.String " + wsddServiceName + " = \"" + portXmlName + "\";");
/*     */ 
/* 341 */     pw.println();
/*     */ 
/* 344 */     pw.println("    public java.lang.String get" + wsddServiceName + "() {");
/*     */ 
/* 346 */     pw.println("        return " + wsddServiceName + ";");
/* 347 */     pw.println("    }");
/* 348 */     pw.println();
/* 349 */     pw.println("    public void set" + wsddServiceName + "(java.lang.String name) {");
/*     */ 
/* 351 */     pw.println("        " + wsddServiceName + " = name;");
/* 352 */     pw.println("    }");
/* 353 */     pw.println();
/*     */   }
/*     */ 
/*     */   protected void writeGetPortName(PrintWriter pw, String bindingType, String portName)
/*     */   {
/* 366 */     pw.println("    public " + bindingType + " get" + portName + "() throws " + ServiceException.class.getName() + " {");
/*     */ 
/* 369 */     pw.println("       java.net.URL endpoint;");
/* 370 */     pw.println("        try {");
/* 371 */     pw.println("            endpoint = new java.net.URL(" + portName + "_address);");
/*     */ 
/* 373 */     pw.println("        }");
/* 374 */     pw.println("        catch (java.net.MalformedURLException e) {");
/* 375 */     pw.println("            throw new javax.xml.rpc.ServiceException(e);");
/* 376 */     pw.println("        }");
/* 377 */     pw.println("        return get" + portName + "(endpoint);");
/* 378 */     pw.println("    }");
/* 379 */     pw.println();
/*     */   }
/*     */ 
/*     */   protected void writeGetPortNameURL(PrintWriter pw, String bindingType, String portName, String stubClass, String wsddServiceName)
/*     */   {
/* 395 */     pw.println("    public " + bindingType + " get" + portName + "(java.net.URL portAddress) throws " + ServiceException.class.getName() + " {");
/*     */ 
/* 398 */     pw.println("        try {");
/* 399 */     pw.println("            " + stubClass + " _stub = new " + stubClass + "(portAddress, this);");
/*     */ 
/* 401 */     pw.println("            _stub.setPortName(get" + wsddServiceName + "());");
/*     */ 
/* 403 */     pw.println("            return _stub;");
/* 404 */     pw.println("        }");
/* 405 */     pw.println("        catch (org.apache.axis.AxisFault e) {");
/* 406 */     pw.println("            return null;");
/* 407 */     pw.println("        }");
/* 408 */     pw.println("    }");
/* 409 */     pw.println();
/*     */   }
/*     */ 
/*     */   protected void writeSetPortEndpointAddress(PrintWriter pw, String portName)
/*     */   {
/* 421 */     pw.println("    public void set" + portName + "EndpointAddress(java.lang.String address) {");
/*     */ 
/* 423 */     pw.println("        " + portName + "_address = address;");
/* 424 */     pw.println("    }");
/* 425 */     pw.println();
/*     */   }
/*     */ 
/*     */   protected void writeGetPortClass(PrintWriter pw, Vector getPortIfaces, Vector getPortStubClasses, Vector getPortPortNames, boolean printGetPortNotice)
/*     */   {
/* 442 */     pw.println("    /**");
/* 443 */     pw.println("     * " + Messages.getMessage("getPortDoc00"));
/* 444 */     pw.println("     * " + Messages.getMessage("getPortDoc01"));
/* 445 */     pw.println("     * " + Messages.getMessage("getPortDoc02"));
/*     */ 
/* 447 */     if (printGetPortNotice) {
/* 448 */       pw.println("     * " + Messages.getMessage("getPortDoc03"));
/* 449 */       pw.println("     * " + Messages.getMessage("getPortDoc04"));
/*     */     }
/*     */ 
/* 452 */     pw.println("     */");
/* 453 */     pw.println("    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws " + ServiceException.class.getName() + " {");
/*     */ 
/* 457 */     if (getPortIfaces.size() == 0) {
/* 458 */       pw.println("        throw new " + ServiceException.class.getName() + "(\"" + Messages.getMessage("noStub") + "  \" + (serviceEndpointInterface == null ? \"null\" : serviceEndpointInterface.getName()));");
/*     */     }
/*     */     else
/*     */     {
/* 464 */       pw.println("        try {");
/*     */ 
/* 466 */       for (int i = 0; i < getPortIfaces.size(); i++) {
/* 467 */         String iface = (String)getPortIfaces.get(i);
/* 468 */         String stubClass = (String)getPortStubClasses.get(i);
/* 469 */         String portName = (String)getPortPortNames.get(i);
/*     */ 
/* 471 */         pw.println("            if (" + iface + ".class.isAssignableFrom(serviceEndpointInterface)) {");
/*     */ 
/* 474 */         pw.println("                " + stubClass + " _stub = new " + stubClass + "(new java.net.URL(" + portName + "_address), this);");
/*     */ 
/* 477 */         pw.println("                _stub.setPortName(get" + portName + "WSDDServiceName());");
/*     */ 
/* 479 */         pw.println("                return _stub;");
/* 480 */         pw.println("            }");
/*     */       }
/*     */ 
/* 483 */       pw.println("        }");
/* 484 */       pw.println("        catch (java.lang.Throwable t) {");
/* 485 */       pw.println("            throw new " + ServiceException.class.getName() + "(t);");
/*     */ 
/* 488 */       pw.println("        }");
/* 489 */       pw.println("        throw new " + ServiceException.class.getName() + "(\"" + Messages.getMessage("noStub") + "  \" + (serviceEndpointInterface == null ? \"null\" : serviceEndpointInterface.getName()));");
/*     */     }
/*     */ 
/* 496 */     pw.println("    }");
/* 497 */     pw.println();
/*     */   }
/*     */ 
/*     */   protected void writeGetPortQNameClass(PrintWriter pw, Vector getPortPortNames, Vector getPortPortXmlNames)
/*     */   {
/* 510 */     pw.println("    /**");
/* 511 */     pw.println("     * " + Messages.getMessage("getPortDoc00"));
/* 512 */     pw.println("     * " + Messages.getMessage("getPortDoc01"));
/* 513 */     pw.println("     * " + Messages.getMessage("getPortDoc02"));
/* 514 */     pw.println("     */");
/* 515 */     pw.println("    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws " + ServiceException.class.getName() + " {");
/*     */ 
/* 518 */     pw.println("        if (portName == null) {");
/* 519 */     pw.println("            return getPort(serviceEndpointInterface);");
/* 520 */     pw.println("        }");
/* 521 */     pw.println("        java.lang.String inputPortName = portName.getLocalPart();");
/* 522 */     pw.print("        ");
/*     */ 
/* 524 */     for (int i = 0; i < getPortPortNames.size(); i++) {
/* 525 */       String portName = (String)getPortPortNames.get(i);
/* 526 */       String portXmlName = (String)getPortPortXmlNames.get(i);
/*     */ 
/* 528 */       pw.println("if (\"" + portXmlName + "\".equals(inputPortName)) {");
/* 529 */       pw.println("            return get" + portName + "();");
/* 530 */       pw.println("        }");
/* 531 */       pw.print("        else ");
/*     */     }
/*     */ 
/* 534 */     pw.println(" {");
/* 535 */     pw.println("            java.rmi.Remote _stub = getPort(serviceEndpointInterface);");
/*     */ 
/* 537 */     pw.println("            ((org.apache.axis.client.Stub) _stub).setPortName(portName);");
/*     */ 
/* 539 */     pw.println("            return _stub;");
/* 540 */     pw.println("        }");
/* 541 */     pw.println("    }");
/* 542 */     pw.println();
/*     */   }
/*     */ 
/*     */   protected void writeGetServiceName(PrintWriter pw, QName qname)
/*     */   {
/* 553 */     String originalServiceName = null;
/* 554 */     QName qNameWithDifferentLocal = null;
/* 555 */     QName qNameWithBackslashedLocal = null;
/*     */ 
/* 557 */     originalServiceName = this.sEntry.getOriginalServiceName();
/* 558 */     qNameWithDifferentLocal = BackslashUtil.getQNameWithDifferentLocal(qname, originalServiceName);
/* 559 */     qNameWithBackslashedLocal = BackslashUtil.getQNameWithBackslashedLocal(qNameWithDifferentLocal);
/*     */ 
/* 561 */     pw.println("    public javax.xml.namespace.QName getServiceName() {");
/*     */ 
/* 563 */     pw.println("        return " + Utils.getNewQName(qNameWithBackslashedLocal) + ";");
/* 564 */     pw.println("    }");
/* 565 */     pw.println();
/*     */   }
/*     */ 
/*     */   protected void writeGetPorts(PrintWriter pw, String namespaceURI, Vector portNames)
/*     */   {
/* 576 */     pw.println("    private java.util.HashSet ports = null;");
/* 577 */     pw.println();
/* 578 */     pw.println("    public java.util.Iterator getPorts() {");
/* 579 */     pw.println("        if (ports == null) {");
/* 580 */     pw.println("            ports = new java.util.HashSet();");
/*     */ 
/* 582 */     for (int i = 0; i < portNames.size(); i++) {
/* 583 */       pw.println("            ports.add(new javax.xml.namespace.QName(\"" + namespaceURI + "\", \"" + portNames.get(i) + "\"));");
/*     */     }
/*     */ 
/* 587 */     pw.println("        }");
/* 588 */     pw.println("        return ports.iterator();");
/* 589 */     pw.println("    }");
/* 590 */     pw.println();
/*     */   }
/*     */ 
/*     */   protected void writeSetEndpointAddress(PrintWriter pw, Vector portNames)
/*     */   {
/* 602 */     if (portNames.isEmpty()) {
/* 603 */       return;
/*     */     }
/*     */ 
/* 607 */     pw.println("    /**");
/* 608 */     pw.println("    * " + Messages.getMessage("setEndpointDoc00"));
/* 609 */     pw.println("    */");
/* 610 */     pw.println("    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws " + ServiceException.class.getName() + " {");
/*     */ 
/* 614 */     pw.println("        ");
/* 615 */     for (Iterator p = portNames.iterator(); p.hasNext(); ) {
/* 616 */       String name = (String)p.next();
/*     */ 
/* 618 */       pw.println("if (\"" + name + "\".equals(portName)) {");
/* 619 */       pw.println("            set" + name + "EndpointAddress(address);");
/* 620 */       pw.println("        }");
/* 621 */       pw.println("        else ");
/*     */     }
/*     */ 
/* 624 */     pw.println("{ // Unknown Port Name");
/* 625 */     pw.println("            throw new " + ServiceException.class.getName() + "(\" " + Messages.getMessage("unknownPortName") + "\" + portName);");
/*     */ 
/* 629 */     pw.println("        }");
/* 630 */     pw.println("    }");
/* 631 */     pw.println();
/*     */ 
/* 634 */     pw.println("    /**");
/* 635 */     pw.println("    * " + Messages.getMessage("setEndpointDoc00"));
/* 636 */     pw.println("    */");
/* 637 */     pw.println("    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws " + ServiceException.class.getName() + " {");
/*     */ 
/* 640 */     pw.println("        setEndpointAddress(portName.getLocalPart(), address);");
/*     */ 
/* 642 */     pw.println("    }");
/* 643 */     pw.println();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.JavaServiceImplWriter
 * JD-Core Version:    0.6.0
 */