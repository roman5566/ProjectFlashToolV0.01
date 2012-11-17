/*      */ package org.apache.axis.wsdl.symbolTable;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.Vector;
/*      */ import javax.wsdl.Binding;
/*      */ import javax.wsdl.BindingFault;
/*      */ import javax.wsdl.BindingInput;
/*      */ import javax.wsdl.BindingOperation;
/*      */ import javax.wsdl.BindingOutput;
/*      */ import javax.wsdl.Definition;
/*      */ import javax.wsdl.Fault;
/*      */ import javax.wsdl.Import;
/*      */ import javax.wsdl.Input;
/*      */ import javax.wsdl.Message;
/*      */ import javax.wsdl.Operation;
/*      */ import javax.wsdl.Output;
/*      */ import javax.wsdl.Part;
/*      */ import javax.wsdl.Port;
/*      */ import javax.wsdl.PortType;
/*      */ import javax.wsdl.Service;
/*      */ import javax.wsdl.WSDLException;
/*      */ import javax.wsdl.extensions.ExtensibilityElement;
/*      */ import javax.wsdl.extensions.UnknownExtensibilityElement;
/*      */ import javax.wsdl.extensions.http.HTTPBinding;
/*      */ import javax.wsdl.extensions.mime.MIMEContent;
/*      */ import javax.wsdl.extensions.mime.MIMEMultipartRelated;
/*      */ import javax.wsdl.extensions.mime.MIMEPart;
/*      */ import javax.wsdl.extensions.soap.SOAPBinding;
/*      */ import javax.wsdl.extensions.soap.SOAPBody;
/*      */ import javax.wsdl.extensions.soap.SOAPFault;
/*      */ import javax.wsdl.extensions.soap.SOAPHeader;
/*      */ import javax.wsdl.extensions.soap.SOAPHeaderFault;
/*      */ import javax.wsdl.factory.WSDLFactory;
/*      */ import javax.wsdl.xml.WSDLReader;
/*      */ import javax.xml.namespace.QName;
/*      */ import javax.xml.parsers.ParserConfigurationException;
/*      */ import javax.xml.rpc.holders.BooleanHolder;
/*      */ import javax.xml.rpc.holders.IntHolder;
/*      */ import javax.xml.rpc.holders.QNameHolder;
/*      */ import org.apache.axis.Constants;
/*      */ import org.apache.axis.constants.Style;
/*      */ import org.apache.axis.constants.Use;
/*      */ import org.apache.axis.utils.Messages;
/*      */ import org.apache.axis.utils.URLHashSet;
/*      */ import org.apache.axis.utils.XMLUtils;
/*      */ import org.w3c.dom.Document;
/*      */ import org.w3c.dom.NamedNodeMap;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.NodeList;
/*      */ import org.xml.sax.SAXException;
/*      */ 
/*      */ public class SymbolTable
/*      */ {
/*   94 */   protected HashMap derivedTypes = new HashMap();
/*      */   private boolean addImports;
/*  111 */   private HashMap symbolTable = new HashMap();
/*      */ 
/*  116 */   private final Map elementTypeEntries = new HashMap();
/*      */ 
/*  121 */   private final Map elementIndex = Collections.unmodifiableMap(this.elementTypeEntries);
/*      */ 
/*  127 */   private final Map typeTypeEntries = new HashMap();
/*      */ 
/*  132 */   private final Map typeIndex = Collections.unmodifiableMap(this.typeTypeEntries);
/*      */ 
/*  139 */   protected final Map node2ExtensionBase = new HashMap();
/*      */   private boolean verbose;
/*      */   protected boolean quiet;
/*  149 */   private BaseTypeMapping btm = null;
/*      */   private boolean nowrap;
/*  159 */   private boolean wrapped = false;
/*      */   public static final String ANON_TOKEN = ">";
/*  165 */   private Definition def = null;
/*      */ 
/*  168 */   private String wsdlURI = null;
/*      */   private boolean wrapArrays;
/*  176 */   Set arrayTypeQNames = new HashSet();
/*      */ 
/*  179 */   private final Map elementFormDefaults = new HashMap();
/*      */ 
/*  685 */   private URLHashSet importedFiles = new URLHashSet();
/*      */   private static final int ABOVE_SCHEMA_LEVEL = -1;
/*      */   private static final int SCHEMA_LEVEL = 0;
/*      */ 
/*      */   public SymbolTable(BaseTypeMapping btm, boolean addImports, boolean verbose, boolean nowrap)
/*      */   {
/*  191 */     this.btm = btm;
/*  192 */     this.addImports = addImports;
/*  193 */     this.verbose = verbose;
/*  194 */     this.nowrap = nowrap;
/*      */   }
/*      */ 
/*      */   public boolean isQuiet()
/*      */   {
/*  203 */     return this.quiet;
/*      */   }
/*      */ 
/*      */   public void setQuiet(boolean quiet)
/*      */   {
/*  212 */     this.quiet = quiet;
/*      */   }
/*      */ 
/*      */   public HashMap getHashMap()
/*      */   {
/*  221 */     return this.symbolTable;
/*      */   }
/*      */ 
/*      */   public Vector getSymbols(QName qname)
/*      */   {
/*  232 */     return (Vector)this.symbolTable.get(qname);
/*      */   }
/*      */ 
/*      */   public SymTabEntry get(QName qname, Class cls)
/*      */   {
/*  244 */     Vector v = (Vector)this.symbolTable.get(qname);
/*      */ 
/*  246 */     if (v == null) {
/*  247 */       return null;
/*      */     }
/*  249 */     for (int i = 0; i < v.size(); i++) {
/*  250 */       SymTabEntry entry = (SymTabEntry)v.elementAt(i);
/*      */ 
/*  252 */       if (cls.isInstance(entry)) {
/*  253 */         return entry;
/*      */       }
/*      */     }
/*      */ 
/*  257 */     return null;
/*      */   }
/*      */ 
/*      */   public TypeEntry getTypeEntry(QName qname, boolean wantElementType)
/*      */   {
/*  270 */     if (wantElementType) {
/*  271 */       return getElement(qname);
/*      */     }
/*  273 */     return getType(qname);
/*      */   }
/*      */ 
/*      */   public Type getType(QName qname)
/*      */   {
/*  285 */     return (Type)this.typeTypeEntries.get(qname);
/*      */   }
/*      */ 
/*      */   public Element getElement(QName qname)
/*      */   {
/*  296 */     return (Element)this.elementTypeEntries.get(qname);
/*      */   }
/*      */ 
/*      */   public MessageEntry getMessageEntry(QName qname)
/*      */   {
/*  306 */     return (MessageEntry)get(qname, MessageEntry.class);
/*      */   }
/*      */ 
/*      */   public PortTypeEntry getPortTypeEntry(QName qname)
/*      */   {
/*  316 */     return (PortTypeEntry)get(qname, PortTypeEntry.class);
/*      */   }
/*      */ 
/*      */   public BindingEntry getBindingEntry(QName qname)
/*      */   {
/*  326 */     return (BindingEntry)get(qname, BindingEntry.class);
/*      */   }
/*      */ 
/*      */   public ServiceEntry getServiceEntry(QName qname)
/*      */   {
/*  336 */     return (ServiceEntry)get(qname, ServiceEntry.class);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public Vector getTypes()
/*      */   {
/*  348 */     Vector v = new Vector();
/*      */ 
/*  350 */     v.addAll(this.elementTypeEntries.values());
/*  351 */     v.addAll(this.typeTypeEntries.values());
/*      */ 
/*  353 */     return v;
/*      */   }
/*      */ 
/*      */   public Map getElementIndex()
/*      */   {
/*  363 */     return this.elementIndex;
/*      */   }
/*      */ 
/*      */   public Map getTypeIndex()
/*      */   {
/*  373 */     return this.typeIndex;
/*      */   }
/*      */ 
/*      */   public int getTypeEntryCount()
/*      */   {
/*  382 */     return this.elementTypeEntries.size() + this.typeTypeEntries.size();
/*      */   }
/*      */ 
/*      */   public Definition getDefinition()
/*      */   {
/*  392 */     return this.def;
/*      */   }
/*      */ 
/*      */   public String getWSDLURI()
/*      */   {
/*  402 */     return this.wsdlURI;
/*      */   }
/*      */ 
/*      */   public boolean isWrapped()
/*      */   {
/*  411 */     return this.wrapped;
/*      */   }
/*      */ 
/*      */   public void setWrapped(boolean wrapped)
/*      */   {
/*  420 */     this.wrapped = wrapped;
/*      */   }
/*      */ 
/*      */   public void dump(PrintStream out)
/*      */   {
/*  430 */     out.println();
/*  431 */     out.println(Messages.getMessage("symbolTable00"));
/*  432 */     out.println("-----------------------");
/*      */ 
/*  434 */     Iterator it = this.symbolTable.values().iterator();
/*      */ 
/*  436 */     while (it.hasNext()) {
/*  437 */       Vector v = (Vector)it.next();
/*      */ 
/*  439 */       for (int i = 0; i < v.size(); i++) {
/*  440 */         out.println(v.elementAt(i).getClass().getName());
/*  441 */         out.println(v.elementAt(i));
/*      */       }
/*      */     }
/*      */ 
/*  445 */     out.println("-----------------------");
/*      */   }
/*      */ 
/*      */   public void populate(String uri)
/*      */     throws IOException, WSDLException, SAXException, ParserConfigurationException
/*      */   {
/*  460 */     populate(uri, null, null);
/*      */   }
/*      */ 
/*      */   public void populate(String uri, String username, String password)
/*      */     throws IOException, WSDLException, SAXException, ParserConfigurationException
/*      */   {
/*  478 */     if (this.verbose) {
/*  479 */       System.out.println(Messages.getMessage("parsing00", uri));
/*      */     }
/*      */ 
/*  482 */     Document doc = XMLUtils.newDocument(uri, username, password);
/*      */ 
/*  484 */     this.wsdlURI = uri;
/*      */     try
/*      */     {
/*  487 */       File f = new File(uri);
/*      */ 
/*  489 */       if (f.exists())
/*  490 */         uri = f.toURL().toString();
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*      */     }
/*  495 */     populate(uri, doc);
/*      */   }
/*      */ 
/*      */   public void populate(String context, Document doc)
/*      */     throws IOException, SAXException, WSDLException, ParserConfigurationException
/*      */   {
/*  512 */     WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
/*      */ 
/*  514 */     reader.setFeature("javax.wsdl.verbose", this.verbose);
/*      */ 
/*  516 */     this.def = reader.readWSDL(context, doc);
/*      */ 
/*  518 */     add(context, this.def, doc);
/*      */   }
/*      */ 
/*      */   protected void add(String context, Definition def, Document doc)
/*      */     throws IOException, SAXException, WSDLException, ParserConfigurationException
/*      */   {
/*  539 */     URL contextURL = context == null ? null : getURL(null, context);
/*      */ 
/*  543 */     populate(contextURL, def, doc, null);
/*  544 */     processTypes();
/*  545 */     checkForUndefined();
/*  546 */     populateParameters();
/*  547 */     setReferences(def, doc);
/*      */   }
/*      */ 
/*      */   private void checkForUndefined(Definition def, String filename)
/*      */     throws IOException
/*      */   {
/*  560 */     if (def != null)
/*      */     {
/*  563 */       Iterator ib = def.getBindings().values().iterator();
/*      */ 
/*  565 */       while (ib.hasNext()) {
/*  566 */         Binding binding = (Binding)ib.next();
/*      */ 
/*  568 */         if (binding.isUndefined()) {
/*  569 */           if (filename == null) {
/*  570 */             throw new IOException(Messages.getMessage("emitFailtUndefinedBinding01", binding.getQName().getLocalPart()));
/*      */           }
/*      */ 
/*  575 */           throw new IOException(Messages.getMessage("emitFailtUndefinedBinding02", binding.getQName().getLocalPart(), filename));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  584 */       Iterator ip = def.getPortTypes().values().iterator();
/*      */ 
/*  586 */       while (ip.hasNext()) {
/*  587 */         PortType portType = (PortType)ip.next();
/*      */ 
/*  589 */         if (portType.isUndefined()) {
/*  590 */           if (filename == null) {
/*  591 */             throw new IOException(Messages.getMessage("emitFailtUndefinedPort01", portType.getQName().getLocalPart()));
/*      */           }
/*      */ 
/*  596 */           throw new IOException(Messages.getMessage("emitFailtUndefinedPort02", portType.getQName().getLocalPart(), filename));
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkForUndefined()
/*      */     throws IOException
/*      */   {
/*  631 */     Iterator it = this.symbolTable.values().iterator();
/*      */ 
/*  633 */     while (it.hasNext()) {
/*  634 */       Vector v = (Vector)it.next();
/*      */ 
/*  636 */       for (int i = 0; i < v.size(); i++) {
/*  637 */         SymTabEntry entry = (SymTabEntry)v.get(i);
/*      */ 
/*  640 */         if ((entry instanceof UndefinedType)) {
/*  641 */           QName qn = entry.getQName();
/*      */ 
/*  645 */           if (((qn.getLocalPart().equals("dateTime")) && (!qn.getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema"))) || ((qn.getLocalPart().equals("timeInstant")) && (qn.getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema"))))
/*      */           {
/*  650 */             throw new IOException(Messages.getMessage("wrongNamespace00", qn.getLocalPart(), qn.getNamespaceURI()));
/*      */           }
/*      */ 
/*  658 */           if (SchemaUtils.isSimpleSchemaType(qn)) {
/*  659 */             throw new IOException(Messages.getMessage("unsupportedSchemaType00", qn.getLocalPart()));
/*      */           }
/*      */ 
/*  665 */           throw new IOException(Messages.getMessage("undefined00", qn.toString()));
/*      */         }
/*      */ 
/*  669 */         if ((entry instanceof UndefinedElement))
/*  670 */           throw new IOException(Messages.getMessage("undefinedElem00", entry.getQName().toString()));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void populate(URL context, Definition def, Document doc, String filename)
/*      */     throws IOException, ParserConfigurationException, SAXException, WSDLException
/*      */   {
/*  704 */     if (doc != null) {
/*  705 */       populateTypes(context, doc);
/*      */ 
/*  707 */       if (this.addImports)
/*      */       {
/*  710 */         lookForImports(context, doc);
/*      */       }
/*      */     }
/*      */ 
/*  714 */     if (def != null) {
/*  715 */       checkForUndefined(def, filename);
/*      */ 
/*  717 */       if (this.addImports)
/*      */       {
/*  720 */         Map imports = def.getImports();
/*  721 */         Object[] importKeys = imports.keySet().toArray();
/*      */ 
/*  723 */         for (int i = 0; i < importKeys.length; i++) {
/*  724 */           Vector v = (Vector)imports.get(importKeys[i]);
/*      */ 
/*  726 */           for (int j = 0; j < v.size(); j++) {
/*  727 */             Import imp = (Import)v.get(j);
/*      */ 
/*  729 */             if (!this.importedFiles.contains(imp.getLocationURI())) {
/*  730 */               this.importedFiles.add(imp.getLocationURI());
/*      */ 
/*  732 */               URL url = getURL(context, imp.getLocationURI());
/*      */ 
/*  734 */               populate(url, imp.getDefinition(), XMLUtils.newDocument(url.toString()), url.toString());
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  742 */       populateMessages(def);
/*  743 */       populatePortTypes(def);
/*  744 */       populateBindings(def);
/*  745 */       populateServices(def);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static URL getURL(URL contextURL, String spec)
/*      */     throws IOException
/*      */   {
/*  763 */     String path = spec.replace('\\', '/');
/*      */ 
/*  766 */     URL url = null;
/*      */     try
/*      */     {
/*  771 */       url = new URL(contextURL, path);
/*      */ 
/*  775 */       if ((contextURL != null) && (url.getProtocol().equals("file")) && (contextURL.getProtocol().equals("file")))
/*      */       {
/*  777 */         url = getFileURL(contextURL, path);
/*      */       }
/*      */     }
/*      */     catch (MalformedURLException me)
/*      */     {
/*  782 */       url = getFileURL(contextURL, path);
/*      */     }
/*      */ 
/*  788 */     return url;
/*      */   }
/*      */ 
/*      */   private static URL getFileURL(URL contextURL, String path)
/*      */     throws IOException
/*      */   {
/*  802 */     if (contextURL != null)
/*      */     {
/*  806 */       String contextFileName = contextURL.getFile();
/*  807 */       URL parent = null;
/*  808 */       File parentFile = new File(contextFileName).getParentFile();
/*  809 */       if (parentFile != null) {
/*  810 */         parent = parentFile.toURL();
/*      */       }
/*  812 */       if (parent != null) {
/*  813 */         return new URL(parent, path);
/*      */       }
/*      */     }
/*      */ 
/*  817 */     return new URL("file", "", path);
/*      */   }
/*      */ 
/*      */   private void lookForImports(URL context, Node node)
/*      */     throws IOException, ParserConfigurationException, SAXException, WSDLException
/*      */   {
/*  834 */     NodeList children = node.getChildNodes();
/*      */ 
/*  836 */     for (int i = 0; i < children.getLength(); i++) {
/*  837 */       Node child = children.item(i);
/*      */ 
/*  839 */       if ("import".equals(child.getLocalName())) {
/*  840 */         NamedNodeMap attributes = child.getAttributes();
/*  841 */         Node namespace = attributes.getNamedItem("namespace");
/*      */ 
/*  844 */         if ((namespace != null) && (isKnownNamespace(namespace.getNodeValue())))
/*      */         {
/*      */           continue;
/*      */         }
/*      */ 
/*  849 */         Node importFile = attributes.getNamedItem("schemaLocation");
/*      */ 
/*  851 */         if (importFile != null) {
/*  852 */           URL url = getURL(context, importFile.getNodeValue());
/*      */ 
/*  854 */           if (!this.importedFiles.contains(url)) {
/*  855 */             this.importedFiles.add(url);
/*      */ 
/*  857 */             String filename = url.toString();
/*      */ 
/*  859 */             populate(url, null, XMLUtils.newDocument(filename), filename);
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  865 */       lookForImports(context, child);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isKnownNamespace(String namespace)
/*      */   {
/*  877 */     if (Constants.isSOAP_ENC(namespace)) {
/*  878 */       return true;
/*      */     }
/*      */ 
/*  881 */     if (Constants.isSchemaXSD(namespace)) {
/*  882 */       return true;
/*      */     }
/*      */ 
/*  885 */     if (Constants.isSchemaXSI(namespace)) {
/*  886 */       return true;
/*      */     }
/*      */ 
/*  890 */     return namespace.equals("http://www.w3.org/XML/1998/namespace");
/*      */   }
/*      */ 
/*      */   public void populateTypes(URL context, Document doc)
/*      */     throws IOException, SAXException, WSDLException, ParserConfigurationException
/*      */   {
/*  909 */     addTypes(context, doc, -1);
/*      */   }
/*      */ 
/*      */   private void addTypes(URL context, Node node, int level)
/*      */     throws IOException, ParserConfigurationException, WSDLException, SAXException
/*      */   {
/*  944 */     if (node == null) {
/*  945 */       return;
/*      */     }
/*      */ 
/*  949 */     String localPart = node.getLocalName();
/*      */ 
/*  951 */     if (localPart != null) {
/*  952 */       boolean isXSD = Constants.isSchemaXSD(node.getNamespaceURI());
/*      */ 
/*  955 */       if (((isXSD) && (localPart.equals("complexType"))) || (localPart.equals("simpleType")))
/*      */       {
/*  960 */         Node re = SchemaUtils.getRestrictionOrExtensionNode(node);
/*      */ 
/*  962 */         if ((re != null) && (Utils.getAttribute(re, "base") != null)) {
/*  963 */           createTypeFromRef(re);
/*      */         }
/*      */ 
/*  966 */         Node list = SchemaUtils.getListNode(node);
/*  967 */         if ((list != null) && (Utils.getAttribute(list, "itemType") != null)) {
/*  968 */           createTypeFromRef(list);
/*      */         }
/*      */ 
/*  971 */         Node union = SchemaUtils.getUnionNode(node);
/*  972 */         if (union != null) {
/*  973 */           QName[] memberTypes = Utils.getMemberTypeQNames(union);
/*  974 */           if (memberTypes != null) {
/*  975 */             for (int i = 0; i < memberTypes.length; i++) {
/*  976 */               if ((!SchemaUtils.isSimpleSchemaType(memberTypes[i])) || (getType(memberTypes[i]) != null))
/*      */                 continue;
/*  978 */               symbolTablePut(new BaseType(memberTypes[i]));
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  986 */         createTypeFromDef(node, false, false);
/*  987 */       } else if ((isXSD) && (localPart.equals("element")))
/*      */       {
/*  990 */         createTypeFromRef(node);
/*      */ 
/*  994 */         Node re = SchemaUtils.getRestrictionOrExtensionNode(node);
/*      */ 
/*  996 */         if ((re != null) && (Utils.getAttribute(re, "base") != null)) {
/*  997 */           createTypeFromRef(re);
/*      */         }
/*      */ 
/* 1003 */         createTypeFromDef(node, true, level > 0);
/* 1004 */       } else if ((isXSD) && (localPart.equals("attributeGroup")))
/*      */       {
/* 1008 */         createTypeFromRef(node);
/*      */ 
/* 1011 */         createTypeFromDef(node, false, level > 0);
/* 1012 */       } else if ((isXSD) && (localPart.equals("group")))
/*      */       {
/* 1014 */         createTypeFromRef(node);
/*      */ 
/* 1016 */         createTypeFromDef(node, false, level > 0);
/* 1017 */       } else if ((isXSD) && (localPart.equals("attribute")))
/*      */       {
/* 1020 */         BooleanHolder forElement = new BooleanHolder();
/* 1021 */         QName refQName = Utils.getTypeQName(node, forElement, false);
/*      */ 
/* 1024 */         if ((refQName != null) && (!forElement.value)) {
/* 1025 */           createTypeFromRef(node);
/*      */ 
/* 1029 */           if (refQName != null) {
/* 1030 */             TypeEntry refType = getTypeEntry(refQName, false);
/*      */ 
/* 1032 */             if ((refType != null) && ((refType instanceof Undefined)))
/*      */             {
/* 1037 */               refType.setSimpleType(true);
/* 1038 */             } else if ((refType == null) || ((!(refType instanceof BaseType)) && (!refType.isSimpleType())))
/*      */             {
/* 1043 */               throw new IOException(Messages.getMessage("AttrNotSimpleType01", refQName.toString()));
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1050 */         createTypeFromDef(node, true, level > 0);
/* 1051 */       } else if ((isXSD) && (localPart.equals("any")))
/*      */       {
/* 1054 */         if (getType(Constants.XSD_ANY) == null) {
/* 1055 */           Type type = new BaseType(Constants.XSD_ANY);
/*      */ 
/* 1057 */           symbolTablePut(type);
/*      */         }
/* 1059 */       } else if ((localPart.equals("part")) && (Constants.isWSDL(node.getNamespaceURI())))
/*      */       {
/* 1063 */         createTypeFromRef(node);
/* 1064 */       } else if ((isXSD) && (localPart.equals("include"))) {
/* 1065 */         String includeName = Utils.getAttribute(node, "schemaLocation");
/*      */ 
/* 1067 */         if (includeName != null) {
/* 1068 */           URL url = getURL(context, includeName);
/* 1069 */           Document includeDoc = XMLUtils.newDocument(url.toString());
/*      */ 
/* 1072 */           org.w3c.dom.Element schemaEl = includeDoc.getDocumentElement();
/*      */ 
/* 1075 */           if (!schemaEl.hasAttribute("targetNamespace")) {
/* 1076 */             org.w3c.dom.Element parentSchemaEl = (org.w3c.dom.Element)node.getParentNode();
/*      */ 
/* 1079 */             if (parentSchemaEl.hasAttribute("targetNamespace"))
/*      */             {
/* 1084 */               String tns = parentSchemaEl.getAttribute("targetNamespace");
/*      */ 
/* 1087 */               schemaEl.setAttribute("targetNamespace", tns);
/* 1088 */               schemaEl.setAttribute("xmlns", tns);
/*      */             }
/*      */           }
/*      */ 
/* 1092 */           populate(url, null, includeDoc, url.toString());
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1097 */     if (level == -1) {
/* 1098 */       if ((localPart != null) && (localPart.equals("schema")))
/*      */       {
/* 1100 */         level = 0;
/* 1101 */         String targetNamespace = ((org.w3c.dom.Element)node).getAttribute("targetNamespace");
/* 1102 */         String elementFormDefault = ((org.w3c.dom.Element)node).getAttribute("elementFormDefault");
/* 1103 */         if ((targetNamespace != null) && (targetNamespace.length() > 0)) {
/* 1104 */           elementFormDefault = (elementFormDefault == null) || (elementFormDefault.length() == 0) ? "unqualified" : elementFormDefault;
/*      */ 
/* 1106 */           if (this.elementFormDefaults.get(targetNamespace) == null)
/* 1107 */             this.elementFormDefaults.put(targetNamespace, elementFormDefault);
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 1112 */       level++;
/*      */     }
/*      */ 
/* 1116 */     NodeList children = node.getChildNodes();
/*      */ 
/* 1118 */     for (int i = 0; i < children.getLength(); i++)
/* 1119 */       addTypes(context, children.item(i), level);
/*      */   }
/*      */ 
/*      */   private void createTypeFromDef(Node node, boolean isElement, boolean belowSchemaLevel)
/*      */     throws IOException
/*      */   {
/* 1137 */     QName qName = Utils.getNodeNameQName(node);
/*      */ 
/* 1139 */     if (qName != null)
/*      */     {
/* 1143 */       if ((!isElement) && (this.btm.getBaseName(qName) != null)) {
/* 1144 */         return;
/*      */       }
/*      */ 
/* 1149 */       BooleanHolder forElement = new BooleanHolder();
/* 1150 */       QName refQName = Utils.getTypeQName(node, forElement, false);
/*      */ 
/* 1153 */       if (refQName != null)
/*      */       {
/* 1156 */         if (qName.getLocalPart().length() == 0) {
/* 1157 */           String name = Utils.getAttribute(node, "name");
/*      */ 
/* 1159 */           if (name == null) {
/* 1160 */             name = "unknown";
/*      */           }
/*      */ 
/* 1163 */           throw new IOException(Messages.getMessage("emptyref00", name));
/*      */         }
/*      */ 
/* 1168 */         TypeEntry refType = getTypeEntry(refQName, forElement.value);
/*      */ 
/* 1170 */         if (!belowSchemaLevel) {
/* 1171 */           if (refType == null) {
/* 1172 */             throw new IOException(Messages.getMessage("absentRef00", refQName.toString(), qName.toString()));
/*      */           }
/*      */ 
/* 1178 */           symbolTablePut(new DefinedElement(qName, refType, node, ""));
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1185 */         IntHolder numDims = new IntHolder();
/* 1186 */         BooleanHolder underlTypeNillable = new BooleanHolder();
/*      */ 
/* 1190 */         QNameHolder itemQName = this.wrapArrays ? null : new QNameHolder();
/* 1191 */         BooleanHolder forElement2 = new BooleanHolder();
/*      */ 
/* 1193 */         numDims.value = 0;
/*      */ 
/* 1195 */         QName arrayEQName = SchemaUtils.getArrayComponentQName(node, numDims, underlTypeNillable, itemQName, forElement2, this);
/*      */ 
/* 1203 */         if (arrayEQName != null)
/*      */         {
/* 1206 */           refQName = arrayEQName;
/*      */ 
/* 1208 */           TypeEntry refType = getTypeEntry(refQName, forElement2.value);
/*      */ 
/* 1210 */           if (refType == null)
/*      */           {
/* 1214 */             String baseName = this.btm.getBaseName(refQName);
/*      */ 
/* 1216 */             if (baseName != null)
/* 1217 */               refType = new BaseType(refQName);
/* 1218 */             else if (forElement2.value)
/* 1219 */               refType = new UndefinedElement(refQName);
/*      */             else {
/* 1221 */               refType = new UndefinedType(refQName);
/*      */             }
/*      */ 
/* 1224 */             symbolTablePut(refType);
/*      */           }
/*      */ 
/* 1228 */           String dims = "";
/*      */ 
/* 1230 */           while (numDims.value > 0) {
/* 1231 */             dims = dims + "[]";
/*      */ 
/* 1233 */             numDims.value -= 1;
/*      */           }
/*      */ 
/* 1236 */           TypeEntry defType = null;
/*      */ 
/* 1238 */           if (isElement) {
/* 1239 */             if (!belowSchemaLevel) {
/* 1240 */               defType = new DefinedElement(qName, refType, node, dims);
/*      */ 
/* 1243 */               defType.setComponentType(arrayEQName);
/* 1244 */               if (itemQName != null)
/* 1245 */                 defType.setItemQName(itemQName.value);
/*      */             }
/*      */           } else {
/* 1248 */             defType = new DefinedType(qName, refType, node, dims);
/*      */ 
/* 1250 */             defType.setComponentType(arrayEQName);
/* 1251 */             defType.setUnderlTypeNillable(underlTypeNillable.value);
/* 1252 */             if (itemQName != null) {
/* 1253 */               defType.setItemQName(itemQName.value);
/*      */             }
/*      */           }
/* 1256 */           if (defType != null) {
/* 1257 */             symbolTablePut(defType);
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 1262 */           String baseName = this.btm.getBaseName(qName);
/*      */ 
/* 1264 */           if (baseName != null) {
/* 1265 */             symbolTablePut(new BaseType(qName));
/*      */           }
/*      */           else
/*      */           {
/* 1271 */             TypeEntry te = null;
/* 1272 */             TypeEntry parentType = null;
/*      */ 
/* 1274 */             if (!isElement) {
/* 1275 */               te = new DefinedType(qName, node);
/*      */ 
/* 1280 */               if (qName.getLocalPart().indexOf(">") >= 0) {
/* 1281 */                 Node parent = node.getParentNode();
/* 1282 */                 QName parentQName = Utils.getNodeNameQName(parent);
/*      */ 
/* 1284 */                 parentType = getElement(parentQName);
/*      */               }
/*      */             }
/* 1287 */             else if (!belowSchemaLevel) {
/* 1288 */               te = new DefinedElement(qName, node);
/*      */             }
/*      */ 
/* 1292 */             if (te != null) {
/* 1293 */               if (SchemaUtils.isSimpleTypeOrSimpleContent(node)) {
/* 1294 */                 te.setSimpleType(true);
/*      */               }
/* 1296 */               te = (TypeEntry)symbolTablePut(te);
/*      */ 
/* 1298 */               if (parentType != null)
/* 1299 */                 parentType.setRefType(te);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void createTypeFromRef(Node node)
/*      */     throws IOException
/*      */   {
/* 1318 */     BooleanHolder forElement = new BooleanHolder();
/* 1319 */     QName qName = Utils.getTypeQName(node, forElement, false);
/*      */ 
/* 1321 */     if ((qName == null) || ((Constants.isSchemaXSD(qName.getNamespaceURI())) && (qName.getLocalPart().equals("simpleRestrictionModel"))))
/*      */     {
/* 1323 */       return;
/*      */     }
/*      */ 
/* 1327 */     if (qName.getLocalPart().length() == 0) {
/* 1328 */       String name = Utils.getAttribute(node, "name");
/*      */ 
/* 1330 */       if (name == null) {
/* 1331 */         name = "unknown";
/*      */       }
/*      */ 
/* 1334 */       throw new IOException(Messages.getMessage("emptyref00", name));
/*      */     }
/*      */ 
/* 1338 */     TypeEntry type = getTypeEntry(qName, forElement.value);
/*      */ 
/* 1341 */     if (type == null)
/*      */     {
/* 1344 */       if (qName.getLocalPart().indexOf("[") > 0) {
/* 1345 */         QName containedQName = Utils.getTypeQName(node, forElement, true);
/*      */ 
/* 1347 */         TypeEntry containedTE = getTypeEntry(containedQName, forElement.value);
/*      */ 
/* 1350 */         if (!forElement.value)
/*      */         {
/* 1353 */           if (containedTE == null)
/*      */           {
/* 1356 */             String baseName = this.btm.getBaseName(containedQName);
/*      */ 
/* 1358 */             if (baseName != null)
/* 1359 */               containedTE = new BaseType(containedQName);
/*      */             else {
/* 1361 */               containedTE = new UndefinedType(containedQName);
/*      */             }
/*      */ 
/* 1364 */             symbolTablePut(containedTE);
/*      */           }
/* 1366 */           boolean wrapped = qName.getLocalPart().endsWith("wrapped");
/* 1367 */           symbolTablePut(new CollectionType(qName, containedTE, node, "[]", wrapped));
/*      */         }
/*      */         else
/*      */         {
/* 1372 */           if (containedTE == null) {
/* 1373 */             containedTE = new UndefinedElement(containedQName);
/*      */ 
/* 1375 */             symbolTablePut(containedTE);
/*      */           }
/*      */ 
/* 1378 */           symbolTablePut(new CollectionElement(qName, containedTE, node, "[]"));
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1385 */         String baseName = this.btm.getBaseName(qName);
/*      */ 
/* 1387 */         if (baseName != null) {
/* 1388 */           symbolTablePut(new BaseType(qName));
/*      */         }
/* 1393 */         else if (qName.equals(Constants.SOAP_COMMON_ATTRS11)) {
/* 1394 */           symbolTablePut(new BaseType(qName));
/*      */ 
/* 1399 */           if (getTypeEntry(Constants.XSD_ID, false) == null) {
/* 1400 */             symbolTablePut(new BaseType(Constants.XSD_ID));
/*      */           }
/*      */ 
/* 1404 */           if (getTypeEntry(Constants.XSD_ANYURI, false) == null)
/* 1405 */             symbolTablePut(new BaseType(Constants.XSD_ANYURI));
/*      */         }
/* 1407 */         else if (qName.equals(Constants.SOAP_COMMON_ATTRS12)) {
/* 1408 */           symbolTablePut(new BaseType(qName));
/*      */ 
/* 1413 */           if (getTypeEntry(Constants.XSD_ID, false) == null)
/* 1414 */             symbolTablePut(new BaseType(Constants.XSD_ID));
/*      */         }
/* 1416 */         else if (qName.equals(Constants.SOAP_ARRAY_ATTRS11)) {
/* 1417 */           symbolTablePut(new BaseType(qName));
/*      */ 
/* 1422 */           if (getTypeEntry(Constants.XSD_STRING, false) == null) {
/* 1423 */             symbolTablePut(new BaseType(Constants.XSD_STRING));
/*      */           }
/*      */ 
/*      */         }
/* 1428 */         else if (qName.equals(Constants.SOAP_ARRAY_ATTRS12)) {
/* 1429 */           symbolTablePut(new BaseType(qName));
/*      */ 
/* 1436 */           if (getTypeEntry(Constants.XSD_STRING, false) == null) {
/* 1437 */             symbolTablePut(new BaseType(Constants.XSD_STRING));
/*      */           }
/*      */ 
/* 1441 */           if (getTypeEntry(Constants.XSD_QNAME, false) == null)
/* 1442 */             symbolTablePut(new BaseType(Constants.XSD_QNAME));
/*      */         }
/* 1444 */         else if (!forElement.value) {
/* 1445 */           symbolTablePut(new UndefinedType(qName));
/*      */         } else {
/* 1447 */           symbolTablePut(new UndefinedElement(qName));
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void populateMessages(Definition def)
/*      */     throws IOException
/*      */   {
/* 1461 */     Iterator i = def.getMessages().values().iterator();
/*      */ 
/* 1463 */     while (i.hasNext()) {
/* 1464 */       Message message = (Message)i.next();
/* 1465 */       MessageEntry mEntry = new MessageEntry(message);
/*      */ 
/* 1467 */       symbolTablePut(mEntry);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void ensureOperationMessageValid(Message message)
/*      */     throws IOException
/*      */   {
/* 1495 */     if (message == null) {
/* 1496 */       throw new IOException("<input>,<output>, or <fault> in <operation ..> without attribute 'message' found. Attribute 'message' is required.");
/*      */     }
/*      */ 
/* 1503 */     if (message.isUndefined())
/* 1504 */       throw new IOException("<input ..>, <output ..> or <fault ..> in <portType> with undefined message found. message name is '" + message.getQName().toString() + "'");
/*      */   }
/*      */ 
/*      */   protected void ensureOperationValid(Operation operation)
/*      */     throws IOException
/*      */   {
/* 1525 */     if (operation == null) {
/* 1526 */       throw new IllegalArgumentException("parameter 'operation' must not be null");
/*      */     }
/*      */ 
/* 1530 */     Input input = operation.getInput();
/*      */ 
/* 1533 */     if (input != null) {
/* 1534 */       Message message = input.getMessage();
/* 1535 */       if (message == null) {
/* 1536 */         throw new IOException("No 'message' attribute in <input> for operation '" + operation.getName() + "'");
/*      */       }
/*      */ 
/* 1540 */       ensureOperationMessageValid(message);
/*      */     }
/*      */ 
/* 1543 */     Output output = operation.getOutput();
/*      */ 
/* 1545 */     if (output != null) {
/* 1546 */       Message message = output.getMessage();
/* 1547 */       if (message == null) {
/* 1548 */         throw new IOException("No 'message' attribute in <output> for operation '" + operation.getName() + "'");
/*      */       }
/*      */ 
/* 1552 */       ensureOperationMessageValid(output.getMessage());
/*      */     }
/*      */ 
/* 1555 */     Map faults = operation.getFaults();
/*      */ 
/* 1557 */     if (faults != null) {
/* 1558 */       Iterator it = faults.values().iterator();
/*      */ 
/* 1560 */       while (it.hasNext()) {
/* 1561 */         Fault fault = (Fault)it.next();
/* 1562 */         Message message = fault.getMessage();
/* 1563 */         if (message == null) {
/* 1564 */           throw new IOException("No 'message' attribute in <fault> named '" + fault.getName() + "' for operation '" + operation.getName() + "'");
/*      */         }
/*      */ 
/* 1569 */         ensureOperationMessageValid(message);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void ensureOperationsOfPortTypeValid(PortType portType)
/*      */     throws IOException
/*      */   {
/* 1588 */     if (portType == null) {
/* 1589 */       throw new IllegalArgumentException("parameter 'portType' must not be null");
/*      */     }
/*      */ 
/* 1593 */     List operations = portType.getOperations();
/*      */ 
/* 1597 */     if ((operations == null) || (operations.size() == 0)) {
/* 1598 */       return;
/*      */     }
/*      */ 
/* 1603 */     Iterator it = operations.iterator();
/*      */ 
/* 1605 */     while (it.hasNext()) {
/* 1606 */       Operation operation = (Operation)it.next();
/*      */ 
/* 1608 */       ensureOperationValid(operation);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void populatePortTypes(Definition def)
/*      */     throws IOException
/*      */   {
/* 1620 */     Iterator i = def.getPortTypes().values().iterator();
/*      */ 
/* 1622 */     while (i.hasNext()) {
/* 1623 */       PortType portType = (PortType)i.next();
/*      */ 
/* 1628 */       if (!portType.isUndefined()) {
/* 1629 */         ensureOperationsOfPortTypeValid(portType);
/*      */ 
/* 1631 */         PortTypeEntry ptEntry = new PortTypeEntry(portType);
/*      */ 
/* 1633 */         symbolTablePut(ptEntry);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void populateParameters()
/*      */     throws IOException
/*      */   {
/* 1645 */     Iterator it = this.symbolTable.values().iterator();
/*      */ 
/* 1647 */     while (it.hasNext()) {
/* 1648 */       Vector v = (Vector)it.next();
/*      */ 
/* 1650 */       for (int i = 0; i < v.size(); i++)
/* 1651 */         if ((v.get(i) instanceof BindingEntry)) {
/* 1652 */           BindingEntry bEntry = (BindingEntry)v.get(i);
/*      */ 
/* 1655 */           if (bEntry.getBindingType() != 0)
/*      */           {
/*      */             continue;
/*      */           }
/* 1659 */           Binding binding = bEntry.getBinding();
/* 1660 */           Collection bindOperations = bEntry.getOperations();
/* 1661 */           PortType portType = binding.getPortType();
/* 1662 */           HashMap parameters = new HashMap();
/* 1663 */           Iterator operations = portType.getOperations().iterator();
/*      */ 
/* 1667 */           while (operations.hasNext()) {
/* 1668 */             Operation operation = (Operation)operations.next();
/*      */ 
/* 1672 */             if (!bindOperations.contains(operation)) {
/* 1673 */               throw new IOException(Messages.getMessage("emitFailNoMatchingBindOperation01", operation.getName(), portType.getQName().getLocalPart()));
/*      */             }
/*      */ 
/* 1680 */             String namespace = portType.getQName().getNamespaceURI();
/*      */ 
/* 1682 */             Parameters parms = getOperationParameters(operation, namespace, bEntry);
/*      */ 
/* 1684 */             parameters.put(operation, parms);
/*      */           }
/*      */ 
/* 1687 */           bEntry.setParameters(parameters);
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   public Parameters getOperationParameters(Operation operation, String namespace, BindingEntry bindingEntry)
/*      */     throws IOException
/*      */   {
/* 1709 */     Parameters parameters = new Parameters();
/*      */ 
/* 1712 */     Vector inputs = new Vector();
/* 1713 */     Vector outputs = new Vector();
/* 1714 */     List parameterOrder = operation.getParameterOrdering();
/*      */ 
/* 1717 */     if ((parameterOrder != null) && (parameterOrder.isEmpty())) {
/* 1718 */       parameterOrder = null;
/*      */     }
/*      */ 
/* 1721 */     Input input = operation.getInput();
/* 1722 */     Output output = operation.getOutput();
/*      */ 
/* 1724 */     parameters.mep = operation.getStyle();
/*      */ 
/* 1727 */     if ((parameterOrder != null) && (!this.wrapped) && 
/* 1728 */       (input != null)) {
/* 1729 */       Message inputMsg = input.getMessage();
/* 1730 */       Map allInputs = inputMsg.getParts();
/* 1731 */       Collection orderedInputs = inputMsg.getOrderedParts(parameterOrder);
/*      */ 
/* 1734 */       if (allInputs.size() != orderedInputs.size()) {
/* 1735 */         throw new IOException(Messages.getMessage("emitFail00", operation.getName()));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1741 */     boolean literalInput = false;
/* 1742 */     boolean literalOutput = false;
/*      */ 
/* 1744 */     if (bindingEntry != null) {
/* 1745 */       literalInput = bindingEntry.getInputBodyType(operation) == Use.LITERAL;
/*      */ 
/* 1747 */       literalOutput = bindingEntry.getOutputBodyType(operation) == Use.LITERAL;
/*      */     }
/*      */ 
/* 1752 */     if ((input != null) && (input.getMessage() != null)) {
/* 1753 */       getParametersFromParts(inputs, input.getMessage().getOrderedParts(null), literalInput, operation.getName(), bindingEntry);
/*      */     }
/*      */ 
/* 1760 */     if ((output != null) && (output.getMessage() != null)) {
/* 1761 */       getParametersFromParts(outputs, output.getMessage().getOrderedParts(null), literalOutput, operation.getName(), bindingEntry);
/*      */     }
/*      */ 
/* 1767 */     if ((parameterOrder != null) && (!this.wrapped))
/*      */     {
/* 1771 */       for (int i = 0; i < parameterOrder.size(); i++) {
/* 1772 */         String name = (String)parameterOrder.get(i);
/*      */ 
/* 1775 */         int index = getPartIndex(name, inputs);
/*      */ 
/* 1778 */         int outdex = getPartIndex(name, outputs);
/*      */ 
/* 1780 */         if (index >= 0)
/*      */         {
/* 1783 */           addInishParm(inputs, outputs, index, outdex, parameters, true);
/*      */         }
/* 1785 */         else if (outdex >= 0)
/* 1786 */           addOutParm(outputs, outdex, parameters, true);
/*      */         else {
/* 1788 */           System.err.println(Messages.getMessage("noPart00", name));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1798 */     if ((this.wrapped) && (inputs.size() == 1) && (outputs.size() == 1) && (Utils.getLastLocalPart(((Parameter)inputs.get(0)).getName()).equals(Utils.getLastLocalPart(((Parameter)outputs.get(0)).getName()))))
/*      */     {
/* 1805 */       addInishParm(inputs, null, 0, -1, parameters, false);
/*      */     }
/*      */     else
/*      */     {
/* 1812 */       for (int i = 0; i < inputs.size(); i++) {
/* 1813 */         Parameter p = (Parameter)inputs.get(i);
/* 1814 */         int outdex = getPartIndex(p.getName(), outputs);
/*      */ 
/* 1816 */         addInishParm(inputs, outputs, i, outdex, parameters, false);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1824 */     if (outputs.size() == 1) {
/* 1825 */       parameters.returnParam = ((Parameter)outputs.get(0));
/*      */ 
/* 1827 */       parameters.returnParam.setMode(2);
/*      */ 
/* 1829 */       if ((parameters.returnParam.getType() instanceof DefinedElement)) {
/* 1830 */         parameters.returnParam.setQName(parameters.returnParam.getType().getQName());
/*      */       }
/*      */ 
/* 1834 */       parameters.outputs += 1;
/*      */     } else {
/* 1836 */       for (int i = 0; i < outputs.size(); i++) {
/* 1837 */         addOutParm(outputs, i, parameters, false);
/*      */       }
/*      */     }
/*      */ 
/* 1841 */     parameters.faults = operation.getFaults();
/*      */ 
/* 1845 */     Vector used = new Vector(parameters.list.size());
/* 1846 */     Iterator i = parameters.list.iterator();
/*      */ 
/* 1848 */     while (i.hasNext()) {
/* 1849 */       Parameter parameter = (Parameter)i.next();
/* 1850 */       int count = 2;
/*      */ 
/* 1852 */       while (used.contains(parameter.getName()))
/*      */       {
/* 1855 */         parameter.setName(parameter.getName() + Integer.toString(count++));
/*      */       }
/*      */ 
/* 1859 */       used.add(parameter.getName());
/*      */     }
/*      */ 
/* 1862 */     return parameters;
/*      */   }
/*      */ 
/*      */   private int getPartIndex(String name, Vector v)
/*      */   {
/* 1873 */     name = Utils.getLastLocalPart(name);
/* 1874 */     for (int i = 0; i < v.size(); i++) {
/* 1875 */       String paramName = ((Parameter)v.get(i)).getName();
/* 1876 */       paramName = Utils.getLastLocalPart(paramName);
/* 1877 */       if (name.equals(paramName)) {
/* 1878 */         return i;
/*      */       }
/*      */     }
/*      */ 
/* 1882 */     return -1;
/*      */   }
/*      */ 
/*      */   private void addInishParm(Vector inputs, Vector outputs, int index, int outdex, Parameters parameters, boolean trimInput)
/*      */   {
/* 1899 */     Parameter p = (Parameter)inputs.get(index);
/*      */ 
/* 1903 */     if ((p.getType() instanceof DefinedElement)) {
/* 1904 */       DefinedElement de = (DefinedElement)p.getType();
/*      */ 
/* 1906 */       p.setQName(de.getQName());
/*      */     }
/*      */ 
/* 1912 */     if ((p.getType() instanceof CollectionElement)) {
/* 1913 */       p.setQName(p.getType().getRefType().getQName());
/*      */     }
/*      */ 
/* 1917 */     if (trimInput) {
/* 1918 */       inputs.remove(index);
/*      */     }
/*      */ 
/* 1924 */     if (outdex >= 0) {
/* 1925 */       Parameter outParam = (Parameter)outputs.get(outdex);
/*      */ 
/* 1927 */       TypeEntry paramEntry = p.getType();
/* 1928 */       TypeEntry outParamEntry = outParam.getType();
/*      */ 
/* 1931 */       if (paramEntry.equals(outParamEntry)) {
/* 1932 */         outputs.remove(outdex);
/* 1933 */         p.setMode(3);
/*      */ 
/* 1935 */         parameters.inouts += 1;
/*      */       }
/*      */       else
/*      */       {
/* 1982 */         parameters.inputs += 1;
/*      */       }
/*      */     } else {
/* 1985 */       parameters.inputs += 1;
/*      */     }
/*      */ 
/* 1988 */     parameters.list.add(p);
/*      */   }
/*      */ 
/*      */   private void addOutParm(Vector outputs, int outdex, Parameters parameters, boolean trim)
/*      */   {
/* 2002 */     Parameter p = (Parameter)outputs.get(outdex);
/*      */ 
/* 2006 */     if ((p.getType() instanceof DefinedElement)) {
/* 2007 */       DefinedElement de = (DefinedElement)p.getType();
/*      */ 
/* 2009 */       p.setQName(de.getQName());
/*      */     }
/*      */ 
/* 2015 */     if ((p.getType() instanceof CollectionElement)) {
/* 2016 */       p.setQName(p.getType().getRefType().getQName());
/*      */     }
/*      */ 
/* 2019 */     if (trim) {
/* 2020 */       outputs.remove(outdex);
/*      */     }
/*      */ 
/* 2023 */     p.setMode(2);
/*      */ 
/* 2025 */     parameters.outputs += 1;
/*      */ 
/* 2027 */     parameters.list.add(p);
/*      */   }
/*      */ 
/*      */   public void getParametersFromParts(Vector v, Collection parts, boolean literal, String opName, BindingEntry bindingEntry)
/*      */     throws IOException
/*      */   {
/* 2062 */     int numberOfElements = 0;
/* 2063 */     boolean possiblyWrapped = false;
/* 2064 */     Iterator i = parts.iterator();
/*      */ 
/* 2066 */     while (i.hasNext()) {
/* 2067 */       Part part = (Part)i.next();
/*      */ 
/* 2069 */       if (part.getElementName() != null) {
/* 2070 */         numberOfElements++;
/*      */ 
/* 2072 */         if (part.getElementName().getLocalPart().equals(opName)) {
/* 2073 */           possiblyWrapped = true;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2086 */     if ((!this.nowrap) && (literal) && (numberOfElements == 1) && (possiblyWrapped)) {
/* 2087 */       this.wrapped = true;
/*      */     }
/*      */ 
/* 2090 */     i = parts.iterator();
/*      */ 
/* 2092 */     while (i.hasNext()) {
/* 2093 */       Parameter param = new Parameter();
/* 2094 */       Part part = (Part)i.next();
/* 2095 */       QName elementName = part.getElementName();
/* 2096 */       QName typeName = part.getTypeName();
/* 2097 */       String partName = part.getName();
/*      */ 
/* 2102 */       if ((!literal) || (!this.wrapped) || (elementName == null)) {
/* 2103 */         param.setName(partName);
/*      */ 
/* 2106 */         if (typeName != null)
/* 2107 */           param.setType(getType(typeName));
/* 2108 */         else if (elementName != null)
/*      */         {
/* 2115 */           param.setType(getElement(elementName));
/*      */         }
/*      */         else
/*      */         {
/* 2119 */           throw new IOException(Messages.getMessage("noTypeOrElement00", new String[] { partName, opName }));
/*      */         }
/*      */ 
/* 2125 */         fillParamInfo(param, bindingEntry, opName, partName);
/* 2126 */         v.add(param);
/*      */ 
/* 2128 */         continue;
/*      */       }
/*      */ 
/* 2134 */       Node node = null;
/* 2135 */       TypeEntry typeEntry = null;
/*      */ 
/* 2137 */       if ((typeName != null) && ((bindingEntry == null) || (bindingEntry.getMIMETypes().size() == 0)))
/*      */       {
/* 2146 */         String bindingName = bindingEntry == null ? "unknown" : bindingEntry.getBinding().getQName().toString();
/*      */ 
/* 2150 */         throw new IOException(Messages.getMessage("literalTypePart00", new String[] { partName, opName, bindingName }));
/*      */       }
/*      */ 
/* 2163 */       typeEntry = getTypeEntry(elementName, true);
/* 2164 */       node = typeEntry.getNode();
/*      */ 
/* 2168 */       BooleanHolder forElement = new BooleanHolder();
/* 2169 */       QName type = Utils.getTypeQName(node, forElement, false);
/*      */ 
/* 2171 */       if ((type != null) && (!forElement.value))
/*      */       {
/* 2175 */         typeEntry = getTypeEntry(type, false);
/* 2176 */         node = typeEntry.getNode();
/*      */       }
/*      */ 
/* 2179 */       Vector vTypes = null;
/*      */ 
/* 2182 */       if (node == null)
/*      */       {
/* 2185 */         this.wrapped = false;
/* 2186 */         if (this.verbose) {
/* 2187 */           System.out.println(Messages.getMessage("cannotDoWrappedMode00", elementName.toString()));
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 2192 */         if (typeEntry.getContainedAttributes() != null)
/*      */         {
/* 2194 */           this.wrapped = false;
/*      */         }
/*      */ 
/* 2197 */         if (!SchemaUtils.isWrappedType(node))
/*      */         {
/* 2204 */           typeEntry.setOnlyLiteralReference(false);
/* 2205 */           this.wrapped = false;
/*      */         }
/*      */ 
/* 2212 */         vTypes = typeEntry.getContainedElements();
/*      */       }
/*      */ 
/* 2217 */       if ((vTypes != null) && (this.wrapped))
/*      */       {
/* 2220 */         for (int j = 0; j < vTypes.size(); j++) {
/* 2221 */           ElementDecl elem = (ElementDecl)vTypes.elementAt(j);
/* 2222 */           Parameter p = new Parameter();
/*      */ 
/* 2224 */           p.setQName(elem.getQName());
/*      */ 
/* 2227 */           String paramName = p.getName();
/* 2228 */           int gt = paramName.lastIndexOf(">");
/* 2229 */           if (gt != 1) {
/* 2230 */             paramName = paramName.substring(gt + 1);
/*      */           }
/* 2232 */           p.setName(paramName);
/* 2233 */           p.setType(elem.getType());
/* 2234 */           p.setOmittable(elem.getMinOccursIs0());
/* 2235 */           p.setNillable(elem.getNillable());
/* 2236 */           fillParamInfo(p, bindingEntry, opName, partName);
/* 2237 */           v.add(p);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 2244 */         param.setName(partName);
/*      */ 
/* 2246 */         if (typeName != null)
/* 2247 */           param.setType(getType(typeName));
/* 2248 */         else if (elementName != null) {
/* 2249 */           param.setType(getElement(elementName));
/*      */         }
/*      */ 
/* 2252 */         fillParamInfo(param, bindingEntry, opName, partName);
/* 2253 */         v.add(param);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void fillParamInfo(Parameter param, BindingEntry bindingEntry, String opName, String partName)
/*      */   {
/* 2270 */     if (bindingEntry == null) {
/* 2271 */       return;
/*      */     }
/* 2273 */     setMIMEInfo(param, bindingEntry.getMIMEInfo(opName, partName));
/*      */ 
/* 2275 */     boolean isHeader = false;
/*      */ 
/* 2278 */     if (bindingEntry.isInHeaderPart(opName, partName)) {
/* 2279 */       isHeader = true;
/* 2280 */       param.setInHeader(true);
/*      */     }
/*      */ 
/* 2284 */     if (bindingEntry.isOutHeaderPart(opName, partName)) {
/* 2285 */       isHeader = true;
/* 2286 */       param.setOutHeader(true);
/*      */     }
/*      */ 
/* 2291 */     if ((isHeader) && (bindingEntry.getBinding() != null)) {
/* 2292 */       List list = bindingEntry.getBinding().getBindingOperations();
/*      */ 
/* 2294 */       for (int i = 0; (list != null) && (i < list.size()); i++) {
/* 2295 */         BindingOperation operation = (BindingOperation)list.get(i);
/*      */ 
/* 2297 */         if (operation.getName().equals(opName))
/* 2298 */           if (param.isInHeader()) {
/* 2299 */             QName qName = getBindedParameterName(operation.getBindingInput().getExtensibilityElements(), param);
/*      */ 
/* 2303 */             if (qName != null)
/* 2304 */               param.setQName(qName);
/*      */           }
/* 2306 */           else if (param.isOutHeader()) {
/* 2307 */             QName qName = getBindedParameterName(operation.getBindingOutput().getExtensibilityElements(), param);
/*      */ 
/* 2311 */             if (qName != null)
/* 2312 */               param.setQName(qName);
/*      */           }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private QName getBindedParameterName(List elements, Parameter p)
/*      */   {
/* 2338 */     QName paramName = null;
/* 2339 */     String defaultNamespace = null;
/* 2340 */     String parameterPartName = p.getName();
/*      */ 
/* 2342 */     for (Iterator k = elements.iterator(); k.hasNext(); ) {
/* 2343 */       ExtensibilityElement element = (ExtensibilityElement)k.next();
/*      */ 
/* 2345 */       if ((element instanceof SOAPBody)) {
/* 2346 */         SOAPBody bodyElement = (SOAPBody)element;
/* 2347 */         List parts = bodyElement.getParts();
/*      */ 
/* 2349 */         if ((parts == null) || (parts.size() == 0)) {
/* 2350 */           defaultNamespace = bodyElement.getNamespaceURI();
/*      */         } else {
/* 2352 */           boolean found = false;
/*      */ 
/* 2354 */           for (Iterator l = parts.iterator(); l.hasNext(); ) {
/* 2355 */             Object o = l.next();
/*      */ 
/* 2357 */             if (((o instanceof String)) && 
/* 2358 */               (parameterPartName.equals((String)o))) {
/* 2359 */               paramName = new QName(bodyElement.getNamespaceURI(), parameterPartName);
/*      */ 
/* 2362 */               found = true;
/* 2363 */               break;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 2368 */           if (found)
/*      */             break;
/*      */         }
/*      */       }
/* 2372 */       else if ((element instanceof SOAPHeader)) {
/* 2373 */         SOAPHeader headerElement = (SOAPHeader)element;
/* 2374 */         String part = headerElement.getPart();
/*      */ 
/* 2376 */         if (parameterPartName.equals(part)) {
/* 2377 */           paramName = new QName(headerElement.getNamespaceURI(), parameterPartName);
/*      */ 
/* 2379 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2384 */     if ((paramName == null) && (!p.isInHeader()) && (!p.isOutHeader())) {
/* 2385 */       if (defaultNamespace != null)
/* 2386 */         paramName = new QName(defaultNamespace, parameterPartName);
/*      */       else {
/* 2388 */         paramName = p.getQName();
/*      */       }
/*      */     }
/*      */ 
/* 2392 */     return paramName;
/*      */   }
/*      */ 
/*      */   private void setMIMEInfo(Parameter p, MimeInfo mimeInfo)
/*      */   {
/* 2407 */     if ((mimeInfo == null) && (p.getType() != null)) {
/* 2408 */       QName mimeQName = p.getType().getQName();
/*      */ 
/* 2410 */       if (mimeQName.getNamespaceURI().equals("http://xml.apache.org/xml-soap")) {
/* 2411 */         if (Constants.MIME_IMAGE.equals(mimeQName))
/* 2412 */           mimeInfo = new MimeInfo("image/jpeg", "");
/* 2413 */         else if (Constants.MIME_PLAINTEXT.equals(mimeQName))
/* 2414 */           mimeInfo = new MimeInfo("text/plain", "");
/* 2415 */         else if (Constants.MIME_MULTIPART.equals(mimeQName))
/* 2416 */           mimeInfo = new MimeInfo("multipart/related", "");
/* 2417 */         else if (Constants.MIME_SOURCE.equals(mimeQName))
/* 2418 */           mimeInfo = new MimeInfo("text/xml", "");
/* 2419 */         else if (Constants.MIME_OCTETSTREAM.equals(mimeQName)) {
/* 2420 */           mimeInfo = new MimeInfo("application/octet-stream", "");
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2425 */     p.setMIMEInfo(mimeInfo);
/*      */   }
/*      */ 
/*      */   private void populateBindings(Definition def)
/*      */     throws IOException
/*      */   {
/* 2436 */     Iterator i = def.getBindings().values().iterator();
/*      */ 
/* 2438 */     while (i.hasNext()) {
/* 2439 */       Binding binding = (Binding)i.next();
/* 2440 */       BindingEntry bEntry = new BindingEntry(binding);
/*      */ 
/* 2442 */       symbolTablePut(bEntry);
/*      */ 
/* 2444 */       Iterator extensibilityElementsIterator = binding.getExtensibilityElements().iterator();
/*      */ 
/* 2447 */       while (extensibilityElementsIterator.hasNext()) {
/* 2448 */         Object obj = extensibilityElementsIterator.next();
/*      */ 
/* 2450 */         if ((obj instanceof SOAPBinding)) {
/* 2451 */           bEntry.setBindingType(0);
/*      */ 
/* 2453 */           SOAPBinding sb = (SOAPBinding)obj;
/* 2454 */           String style = sb.getStyle();
/*      */ 
/* 2456 */           if ("rpc".equalsIgnoreCase(style))
/* 2457 */             bEntry.setBindingStyle(Style.RPC);
/*      */         }
/* 2459 */         else if ((obj instanceof HTTPBinding)) {
/* 2460 */           HTTPBinding hb = (HTTPBinding)obj;
/*      */ 
/* 2462 */           if (hb.getVerb().equalsIgnoreCase("post"))
/* 2463 */             bEntry.setBindingType(2);
/*      */           else
/* 2465 */             bEntry.setBindingType(1);
/*      */         }
/* 2467 */         else if ((obj instanceof UnknownExtensibilityElement))
/*      */         {
/* 2470 */           UnknownExtensibilityElement unkElement = (UnknownExtensibilityElement)obj;
/*      */ 
/* 2472 */           QName name = unkElement.getElementType();
/*      */ 
/* 2475 */           if ((name.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/")) && (name.getLocalPart().equals("binding")))
/*      */           {
/* 2477 */             bEntry.setBindingType(0);
/*      */ 
/* 2479 */             String style = unkElement.getElement().getAttribute("style");
/*      */ 
/* 2482 */             if ("rpc".equalsIgnoreCase(style)) {
/* 2483 */               bEntry.setBindingStyle(Style.RPC);
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2494 */       HashMap attributes = new HashMap();
/* 2495 */       List bindList = binding.getBindingOperations();
/* 2496 */       HashMap faultMap = new HashMap();
/*      */ 
/* 2498 */       Iterator opIterator = bindList.iterator();
/* 2499 */       while (opIterator.hasNext()) {
/* 2500 */         BindingOperation bindOp = (BindingOperation)opIterator.next();
/*      */ 
/* 2502 */         Operation operation = bindOp.getOperation();
/* 2503 */         BindingInput bindingInput = bindOp.getBindingInput();
/* 2504 */         BindingOutput bindingOutput = bindOp.getBindingOutput();
/* 2505 */         String opName = bindOp.getName();
/*      */ 
/* 2508 */         String inputName = bindingInput == null ? null : bindingInput.getName();
/*      */ 
/* 2511 */         String outputName = bindingOutput == null ? null : bindingOutput.getName();
/*      */ 
/* 2515 */         if (binding.getPortType().getOperation(opName, inputName, outputName) == null)
/*      */         {
/* 2517 */           throw new IOException(Messages.getMessage("unmatchedOp", new String[] { opName, inputName, outputName }));
/*      */         }
/*      */ 
/* 2524 */         ArrayList faults = new ArrayList();
/*      */ 
/* 2527 */         if ((bindingInput != null) && 
/* 2528 */           (bindingInput.getExtensibilityElements() != null)) {
/* 2529 */           Iterator inIter = bindingInput.getExtensibilityElements().iterator();
/*      */ 
/* 2532 */           fillInBindingInfo(bEntry, operation, inIter, faults, true);
/*      */         }
/*      */ 
/* 2538 */         if ((bindingOutput != null) && 
/* 2539 */           (bindingOutput.getExtensibilityElements() != null)) {
/* 2540 */           Iterator outIter = bindingOutput.getExtensibilityElements().iterator();
/*      */ 
/* 2543 */           fillInBindingInfo(bEntry, operation, outIter, faults, false);
/*      */         }
/*      */ 
/* 2549 */         faultsFromSOAPFault(binding, bindOp, operation, faults);
/*      */ 
/* 2552 */         faultMap.put(bindOp, faults);
/*      */ 
/* 2554 */         Use inputBodyType = bEntry.getInputBodyType(operation);
/* 2555 */         Use outputBodyType = bEntry.getOutputBodyType(operation);
/*      */ 
/* 2559 */         attributes.put(bindOp.getOperation(), new BindingEntry.OperationAttr(inputBodyType, outputBodyType, faultMap));
/*      */ 
/* 2565 */         if ((inputBodyType == Use.LITERAL) || (outputBodyType == Use.LITERAL))
/*      */         {
/* 2567 */           bEntry.setHasLiteral(true);
/*      */         }
/*      */ 
/* 2570 */         bEntry.setFaultBodyTypeMap(operation, faultMap);
/*      */       }
/*      */ 
/* 2573 */       bEntry.setFaults(faultMap);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void fillInBindingInfo(BindingEntry bEntry, Operation operation, Iterator it, ArrayList faults, boolean input)
/*      */     throws IOException
/*      */   {
/* 2591 */     while (it.hasNext()) {
/* 2592 */       Object obj = it.next();
/*      */ 
/* 2594 */       if ((obj instanceof SOAPBody)) {
/* 2595 */         setBodyType(((SOAPBody)obj).getUse(), bEntry, operation, input);
/*      */       }
/* 2597 */       else if ((obj instanceof SOAPHeader)) {
/* 2598 */         SOAPHeader header = (SOAPHeader)obj;
/*      */ 
/* 2600 */         setBodyType(header.getUse(), bEntry, operation, input);
/*      */ 
/* 2608 */         bEntry.setHeaderPart(operation.getName(), header.getPart(), input ? 1 : 2);
/*      */ 
/* 2614 */         Iterator headerFaults = header.getSOAPHeaderFaults().iterator();
/*      */ 
/* 2616 */         while (headerFaults.hasNext()) {
/* 2617 */           SOAPHeaderFault headerFault = (SOAPHeaderFault)headerFaults.next();
/*      */ 
/* 2620 */           faults.add(new FaultInfo(headerFault, this));
/*      */         }
/* 2622 */       } else if ((obj instanceof MIMEMultipartRelated)) {
/* 2623 */         bEntry.setBodyType(operation, addMIMETypes(bEntry, (MIMEMultipartRelated)obj, operation), input);
/*      */       }
/* 2627 */       else if ((obj instanceof UnknownExtensibilityElement)) {
/* 2628 */         UnknownExtensibilityElement unkElement = (UnknownExtensibilityElement)obj;
/*      */ 
/* 2630 */         QName name = unkElement.getElementType();
/*      */ 
/* 2633 */         if ((name.getNamespaceURI().equals("http://schemas.xmlsoap.org/ws/2002/04/dime/wsdl/")) && (name.getLocalPart().equals("message")))
/*      */         {
/* 2635 */           fillInDIMEInformation(unkElement, input, operation, bEntry);
/*      */         }
/*      */ 
/* 2639 */         if ((name.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/")) && (name.getLocalPart().equals("body")))
/*      */         {
/* 2641 */           setBodyType(unkElement.getElement().getAttribute("use"), bEntry, operation, input);
/*      */         }
/*      */ 
/* 2646 */         if ((name.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/")) && (name.getLocalPart().equals("header")))
/*      */         {
/* 2648 */           setBodyType(unkElement.getElement().getAttribute("use"), bEntry, operation, input);
/*      */ 
/* 2657 */           bEntry.setHeaderPart(operation.getName(), unkElement.getElement().getAttribute("part"), input ? 1 : 2);
/*      */ 
/* 2664 */           NodeList headerFaults = unkElement.getElement().getChildNodes();
/*      */ 
/* 2667 */           for (int i = 0; i < headerFaults.getLength(); i++) {
/* 2668 */             String faultMessage = unkElement.getElement().getAttribute("message");
/*      */ 
/* 2670 */             String faultPart = unkElement.getElement().getAttribute("part");
/*      */ 
/* 2672 */             String faultUse = unkElement.getElement().getAttribute("use");
/*      */ 
/* 2674 */             String faultNamespaceURI = unkElement.getElement().getAttribute("namespace");
/*      */ 
/* 2676 */             QName faultMessageQName = null;
/* 2677 */             int sep = faultMessage.indexOf(':');
/*      */ 
/* 2679 */             if (sep == -1)
/* 2680 */               faultMessageQName = new QName(faultMessage);
/*      */             else {
/* 2682 */               faultMessageQName = new QName(faultMessage.substring(0, sep), faultMessage.substring(sep + 1));
/*      */             }
/*      */ 
/* 2687 */             faults.add(new FaultInfo(faultMessageQName, faultPart, faultUse, faultNamespaceURI, this));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void fillInDIMEInformation(UnknownExtensibilityElement unkElement, boolean input, Operation operation, BindingEntry bEntry)
/*      */   {
/* 2708 */     String layout = unkElement.getElement().getAttribute("layout");
/*      */ 
/* 2711 */     if ((!layout.equals("http://schemas.xmlsoap.org/ws/2002/04/dime/closed-layout")) && 
/* 2712 */       (layout.equals("http://schemas.xmlsoap.org/ws/2002/04/dime/open-layout")));
/* 2715 */     Map parts = null;
/*      */ 
/* 2717 */     if (input)
/* 2718 */       parts = operation.getInput().getMessage().getParts();
/*      */     else {
/* 2720 */       parts = operation.getOutput().getMessage().getParts();
/*      */     }
/*      */ 
/* 2723 */     if (parts != null) {
/* 2724 */       Iterator iterator = parts.values().iterator();
/*      */ 
/* 2726 */       while (iterator.hasNext()) {
/* 2727 */         Part part = (Part)iterator.next();
/*      */ 
/* 2729 */         if (part != null) {
/* 2730 */           String dims = "";
/* 2731 */           org.w3c.dom.Element element = null;
/*      */ 
/* 2733 */           if (part.getTypeName() != null) {
/* 2734 */             TypeEntry partType = getType(part.getTypeName());
/*      */ 
/* 2736 */             if (partType.getDimensions().length() > 0) {
/* 2737 */               dims = partType.getDimensions();
/* 2738 */               partType = partType.getRefType();
/*      */             }
/*      */ 
/* 2741 */             element = (org.w3c.dom.Element)partType.getNode();
/* 2742 */           } else if (part.getElementName() != null) {
/* 2743 */             TypeEntry partElement = getElement(part.getElementName()).getRefType();
/*      */ 
/* 2746 */             element = (org.w3c.dom.Element)partElement.getNode();
/*      */ 
/* 2748 */             QName name = getInnerCollectionComponentQName(element);
/*      */ 
/* 2750 */             if (name != null) {
/* 2751 */               dims = dims + "[]";
/* 2752 */               partElement = getType(name);
/* 2753 */               element = (org.w3c.dom.Element)partElement.getNode();
/*      */             }
/*      */             else {
/* 2756 */               name = getInnerTypeQName(element);
/*      */ 
/* 2758 */               if (name != null) {
/* 2759 */                 partElement = getType(name);
/* 2760 */                 element = (org.w3c.dom.Element)partElement.getNode();
/*      */               }
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 2766 */           if (element != null) {
/* 2767 */             org.w3c.dom.Element e = (org.w3c.dom.Element)XMLUtils.findNode(element, new QName("http://schemas.xmlsoap.org/ws/2002/04/content-type/", "mediaType"));
/*      */ 
/* 2773 */             if (e != null) {
/* 2774 */               String value = e.getAttribute("value");
/*      */ 
/* 2776 */               bEntry.setOperationDIME(operation.getName());
/* 2777 */               bEntry.setMIMEInfo(operation.getName(), part.getName(), value, dims);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void faultsFromSOAPFault(Binding binding, BindingOperation bindOp, Operation operation, ArrayList faults)
/*      */     throws IOException
/*      */   {
/* 2799 */     Iterator faultMapIter = bindOp.getBindingFaults().values().iterator();
/*      */ 
/* 2801 */     while (faultMapIter.hasNext()) {
/* 2802 */       BindingFault bFault = (BindingFault)faultMapIter.next();
/*      */ 
/* 2805 */       String faultName = bFault.getName();
/*      */ 
/* 2808 */       if ((faultName == null) || (faultName.length() == 0)) {
/* 2809 */         throw new IOException(Messages.getMessage("unNamedFault00", bindOp.getName(), binding.getQName().toString()));
/*      */       }
/*      */ 
/* 2815 */       boolean foundSOAPFault = false;
/* 2816 */       String soapFaultUse = "";
/* 2817 */       String soapFaultNamespace = "";
/* 2818 */       Iterator faultIter = bFault.getExtensibilityElements().iterator();
/*      */ 
/* 2821 */       while (faultIter.hasNext()) {
/* 2822 */         Object obj = faultIter.next();
/*      */ 
/* 2824 */         if ((obj instanceof SOAPFault)) {
/* 2825 */           foundSOAPFault = true;
/* 2826 */           soapFaultUse = ((SOAPFault)obj).getUse();
/* 2827 */           soapFaultNamespace = ((SOAPFault)obj).getNamespaceURI();
/*      */ 
/* 2829 */           break;
/* 2830 */         }if ((obj instanceof UnknownExtensibilityElement))
/*      */         {
/* 2833 */           UnknownExtensibilityElement unkElement = (UnknownExtensibilityElement)obj;
/*      */ 
/* 2835 */           QName name = unkElement.getElementType();
/*      */ 
/* 2838 */           if ((name.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/")) && (name.getLocalPart().equals("fault")))
/*      */           {
/* 2840 */             if (unkElement.getElement().getAttribute("use") != null)
/*      */             {
/* 2842 */               soapFaultUse = unkElement.getElement().getAttribute("use");
/*      */             }
/*      */ 
/* 2846 */             if (unkElement.getElement().getAttribute("namespace") != null)
/*      */             {
/* 2848 */               soapFaultNamespace = unkElement.getElement().getAttribute("namespace");
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2857 */       if (!foundSOAPFault) {
/* 2858 */         throw new IOException(Messages.getMessage("missingSoapFault00", faultName, bindOp.getName(), binding.getQName().toString()));
/*      */       }
/*      */ 
/* 2869 */       Fault opFault = operation.getFault(bFault.getName());
/*      */ 
/* 2871 */       if (opFault == null) {
/* 2872 */         throw new IOException(Messages.getMessage("noPortTypeFault", new String[] { bFault.getName(), bindOp.getName(), binding.getQName().toString() }));
/*      */       }
/*      */ 
/* 2880 */       faults.add(new FaultInfo(opFault, Use.getUse(soapFaultUse), soapFaultNamespace, this));
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setBodyType(String use, BindingEntry bEntry, Operation operation, boolean input)
/*      */   {
/* 2899 */     if (use == null)
/*      */     {
/* 2905 */       use = "literal";
/*      */     }
/*      */ 
/* 2908 */     if (use.equalsIgnoreCase("literal"))
/* 2909 */       bEntry.setBodyType(operation, Use.LITERAL, input);
/*      */   }
/*      */ 
/*      */   private Use addMIMETypes(BindingEntry bEntry, MIMEMultipartRelated mpr, Operation op)
/*      */     throws IOException
/*      */   {
/* 2928 */     Use bodyType = Use.ENCODED;
/* 2929 */     List parts = mpr.getMIMEParts();
/* 2930 */     Iterator i = parts.iterator();
/*      */ 
/* 2932 */     while (i.hasNext()) {
/* 2933 */       MIMEPart part = (MIMEPart)i.next();
/* 2934 */       List elems = part.getExtensibilityElements();
/* 2935 */       Iterator j = elems.iterator();
/*      */ 
/* 2937 */       while (j.hasNext()) {
/* 2938 */         Object obj = j.next();
/*      */ 
/* 2940 */         if ((obj instanceof MIMEContent)) {
/* 2941 */           MIMEContent content = (MIMEContent)obj;
/* 2942 */           TypeEntry typeEntry = findPart(op, content.getPart());
/* 2943 */           if (typeEntry == null) {
/* 2944 */             throw new RuntimeException(Messages.getMessage("cannotFindPartForOperation00", content.getPart(), op.getName(), content.getType()));
/*      */           }
/*      */ 
/* 2947 */           String dims = typeEntry.getDimensions();
/*      */ 
/* 2949 */           if ((dims.length() <= 0) && (typeEntry.getRefType() != null))
/*      */           {
/* 2951 */             Node node = typeEntry.getRefType().getNode();
/*      */ 
/* 2953 */             if (getInnerCollectionComponentQName(node) != null) {
/* 2954 */               dims = dims + "[]";
/*      */             }
/*      */           }
/*      */ 
/* 2958 */           String type = content.getType();
/*      */ 
/* 2960 */           if ((type == null) || (type.length() == 0)) {
/* 2961 */             type = "text/plain";
/*      */           }
/*      */ 
/* 2964 */           bEntry.setMIMEInfo(op.getName(), content.getPart(), type, dims);
/*      */         }
/* 2966 */         else if ((obj instanceof SOAPBody)) {
/* 2967 */           String use = ((SOAPBody)obj).getUse();
/*      */ 
/* 2969 */           if (use == null) {
/* 2970 */             throw new IOException(Messages.getMessage("noUse", op.getName()));
/*      */           }
/*      */ 
/* 2974 */           if (use.equalsIgnoreCase("literal"))
/* 2975 */             bodyType = Use.LITERAL;
/*      */         }
/* 2977 */         else if ((obj instanceof UnknownExtensibilityElement))
/*      */         {
/* 2980 */           UnknownExtensibilityElement unkElement = (UnknownExtensibilityElement)obj;
/*      */ 
/* 2982 */           QName name = unkElement.getElementType();
/*      */ 
/* 2985 */           if ((name.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/")) && (name.getLocalPart().equals("body")))
/*      */           {
/* 2987 */             String use = unkElement.getElement().getAttribute("use");
/*      */ 
/* 2990 */             if (use == null) {
/* 2991 */               throw new IOException(Messages.getMessage("noUse", op.getName()));
/*      */             }
/*      */ 
/* 2995 */             if (use.equalsIgnoreCase("literal")) {
/* 2996 */               bodyType = Use.LITERAL;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3003 */     return bodyType;
/*      */   }
/*      */ 
/*      */   private TypeEntry findPart(Operation operation, String partName)
/*      */   {
/* 3015 */     Map parts = operation.getInput().getMessage().getParts();
/* 3016 */     Iterator iterator = parts.values().iterator();
/* 3017 */     TypeEntry part = findPart(iterator, partName);
/*      */ 
/* 3019 */     if (part == null) {
/* 3020 */       parts = operation.getOutput().getMessage().getParts();
/* 3021 */       iterator = parts.values().iterator();
/* 3022 */       part = findPart(iterator, partName);
/*      */     }
/*      */ 
/* 3025 */     return part;
/*      */   }
/*      */ 
/*      */   private TypeEntry findPart(Iterator iterator, String partName)
/*      */   {
/* 3037 */     while (iterator.hasNext()) {
/* 3038 */       Part part = (Part)iterator.next();
/*      */ 
/* 3040 */       if (part != null) {
/* 3041 */         String typeName = part.getName();
/*      */ 
/* 3043 */         if (partName.equals(typeName)) {
/* 3044 */           if (part.getTypeName() != null)
/* 3045 */             return getType(part.getTypeName());
/* 3046 */           if (part.getElementName() != null) {
/* 3047 */             return getElement(part.getElementName());
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3053 */     return null;
/*      */   }
/*      */ 
/*      */   private void populateServices(Definition def)
/*      */     throws IOException
/*      */   {
/* 3064 */     String originalName = null;
/* 3065 */     Iterator i = def.getServices().values().iterator();
/*      */ 
/* 3067 */     while (i.hasNext()) {
/* 3068 */       Service service = (Service)i.next();
/* 3069 */       originalName = service.getQName().getLocalPart();
/*      */ 
/* 3071 */       if ((service.getQName() == null) || (service.getQName().getLocalPart() == null) || (service.getQName().getLocalPart().equals("")))
/*      */       {
/* 3074 */         throw new IOException(Messages.getMessage("BadServiceName00"));
/*      */       }
/*      */ 
/* 3078 */       service.setQName(BackslashUtil.getQNameWithBackslashlessLocal(service.getQName()));
/* 3079 */       ServiceEntry sEntry = new ServiceEntry(service);
/*      */ 
/* 3081 */       sEntry.setOriginalServiceName(originalName);
/* 3082 */       symbolTablePut(sEntry);
/* 3083 */       populatePorts(service.getPorts());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void populatePorts(Map ports)
/*      */     throws IOException
/*      */   {
/* 3099 */     if (ports == null) {
/* 3100 */       return;
/*      */     }
/*      */ 
/* 3103 */     Iterator it = ports.values().iterator();
/*      */ 
/* 3105 */     while (it.hasNext()) {
/* 3106 */       Port port = (Port)it.next();
/* 3107 */       String portName = port.getName();
/* 3108 */       Binding portBinding = port.getBinding();
/*      */ 
/* 3113 */       if (portName == null)
/*      */       {
/* 3116 */         throw new IOException(Messages.getMessage("missingPortNameException"));
/*      */       }
/*      */ 
/* 3123 */       if (portBinding == null)
/*      */       {
/* 3126 */         throw new IOException(Messages.getMessage("missingBindingException"));
/*      */       }
/*      */ 
/* 3145 */       if (existsPortWithName(new QName(portName)))
/*      */       {
/* 3148 */         throw new IOException(Messages.getMessage("twoPortsWithSameName", portName));
/*      */       }
/*      */ 
/* 3152 */       PortEntry portEntry = new PortEntry(port);
/*      */ 
/* 3154 */       symbolTablePut(portEntry);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setReferences(Definition def, Document doc)
/*      */   {
/* 3170 */     Map stuff = def.getServices();
/*      */ 
/* 3172 */     if (stuff.isEmpty()) {
/* 3173 */       stuff = def.getBindings();
/*      */ 
/* 3175 */       if (stuff.isEmpty()) {
/* 3176 */         stuff = def.getPortTypes();
/*      */ 
/* 3178 */         if (stuff.isEmpty()) {
/* 3179 */           stuff = def.getMessages();
/*      */ 
/* 3181 */           if (stuff.isEmpty()) {
/* 3182 */             Iterator i = this.elementTypeEntries.values().iterator();
/* 3183 */             while (i.hasNext()) {
/* 3184 */               setTypeReferences((TypeEntry)i.next(), doc, false);
/*      */             }
/*      */ 
/* 3187 */             Iterator i = this.typeTypeEntries.values().iterator();
/* 3188 */             while (i.hasNext())
/* 3189 */               setTypeReferences((TypeEntry)i.next(), doc, false);
/*      */           }
/*      */           else {
/* 3192 */             Iterator i = stuff.values().iterator();
/*      */ 
/* 3194 */             while (i.hasNext()) {
/* 3195 */               Message message = (Message)i.next();
/* 3196 */               MessageEntry mEntry = getMessageEntry(message.getQName());
/*      */ 
/* 3199 */               setMessageReferences(mEntry, def, doc, false);
/*      */             }
/*      */           }
/*      */         } else {
/* 3203 */           Iterator i = stuff.values().iterator();
/*      */ 
/* 3205 */           while (i.hasNext()) {
/* 3206 */             PortType portType = (PortType)i.next();
/* 3207 */             PortTypeEntry ptEntry = getPortTypeEntry(portType.getQName());
/*      */ 
/* 3210 */             setPortTypeReferences(ptEntry, null, def, doc);
/*      */           }
/*      */         }
/*      */       } else {
/* 3214 */         Iterator i = stuff.values().iterator();
/*      */ 
/* 3216 */         while (i.hasNext()) {
/* 3217 */           Binding binding = (Binding)i.next();
/* 3218 */           BindingEntry bEntry = getBindingEntry(binding.getQName());
/*      */ 
/* 3220 */           setBindingReferences(bEntry, def, doc);
/*      */         }
/*      */       }
/*      */     } else {
/* 3224 */       Iterator i = stuff.values().iterator();
/*      */ 
/* 3226 */       while (i.hasNext()) {
/* 3227 */         Service service = (Service)i.next();
/* 3228 */         ServiceEntry sEntry = getServiceEntry(service.getQName());
/*      */ 
/* 3230 */         setServiceReferences(sEntry, def, doc);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setTypeReferences(TypeEntry entry, Document doc, boolean literal)
/*      */   {
/* 3247 */     if (((entry.isReferenced()) && (!literal)) || ((entry.isOnlyLiteralReferenced()) && (literal)))
/*      */     {
/* 3249 */       return;
/*      */     }
/*      */ 
/* 3252 */     if (this.wrapped)
/*      */     {
/* 3256 */       if ((!entry.isReferenced()) && (literal)) {
/* 3257 */         entry.setOnlyLiteralReference(true);
/*      */       }
/* 3263 */       else if ((entry.isOnlyLiteralReferenced()) && (!literal)) {
/* 3264 */         entry.setOnlyLiteralReference(false);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3270 */     Node node = entry.getNode();
/*      */ 
/* 3272 */     if ((this.addImports) || (node == null) || (node.getOwnerDocument() == doc)) {
/* 3273 */       entry.setIsReferenced(true);
/*      */ 
/* 3275 */       if ((entry instanceof DefinedElement)) {
/* 3276 */         BooleanHolder forElement = new BooleanHolder();
/* 3277 */         QName referentName = Utils.getTypeQName(node, forElement, false);
/*      */ 
/* 3280 */         if (referentName != null) {
/* 3281 */           TypeEntry referent = getTypeEntry(referentName, forElement.value);
/*      */ 
/* 3284 */           if (referent != null) {
/* 3285 */             setTypeReferences(referent, doc, literal);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 3291 */         QName anonQName = SchemaUtils.getElementAnonQName(entry.getNode());
/*      */ 
/* 3294 */         if (anonQName != null) {
/* 3295 */           TypeEntry anonType = getType(anonQName);
/*      */ 
/* 3297 */           if (anonType != null) {
/* 3298 */             setTypeReferences(anonType, doc, literal);
/*      */ 
/* 3300 */             return;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3306 */     HashSet nestedTypes = entry.getNestedTypes(this, true);
/* 3307 */     Iterator it = nestedTypes.iterator();
/*      */ 
/* 3309 */     while (it.hasNext()) {
/* 3310 */       TypeEntry nestedType = (TypeEntry)it.next();
/* 3311 */       TypeEntry refType = entry.getRefType();
/*      */ 
/* 3313 */       if (nestedType == null)
/*      */       {
/*      */         continue;
/*      */       }
/*      */ 
/* 3322 */       if ((refType != null) && (!refType.equals(nestedType)) && (nestedType.isOnlyLiteralReferenced()))
/*      */       {
/* 3325 */         nestedType.setOnlyLiteralReference(false);
/*      */       }
/*      */ 
/* 3328 */       if (!nestedType.isReferenced())
/*      */       {
/* 3331 */         if (nestedType != entry)
/* 3332 */           setTypeReferences(nestedType, doc, false);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setMessageReferences(MessageEntry entry, Definition def, Document doc, boolean literal)
/*      */   {
/* 3352 */     Message message = entry.getMessage();
/*      */ 
/* 3354 */     if (this.addImports) {
/* 3355 */       entry.setIsReferenced(true);
/*      */     }
/*      */     else
/*      */     {
/* 3361 */       Map messages = def.getMessages();
/*      */ 
/* 3363 */       if (messages.containsValue(message)) {
/* 3364 */         entry.setIsReferenced(true);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3369 */     Iterator parts = message.getParts().values().iterator();
/*      */ 
/* 3371 */     while (parts.hasNext()) {
/* 3372 */       Part part = (Part)parts.next();
/* 3373 */       TypeEntry type = getType(part.getTypeName());
/*      */ 
/* 3375 */       if (type != null) {
/* 3376 */         setTypeReferences(type, doc, literal);
/*      */       }
/*      */ 
/* 3379 */       type = getElement(part.getElementName());
/*      */ 
/* 3381 */       if (type != null) {
/* 3382 */         setTypeReferences(type, doc, literal);
/*      */ 
/* 3384 */         TypeEntry refType = type.getRefType();
/*      */ 
/* 3386 */         if (refType != null)
/* 3387 */           setTypeReferences(refType, doc, literal);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setPortTypeReferences(PortTypeEntry entry, BindingEntry bEntry, Definition def, Document doc)
/*      */   {
/* 3408 */     PortType portType = entry.getPortType();
/*      */ 
/* 3410 */     if (this.addImports) {
/* 3411 */       entry.setIsReferenced(true);
/*      */     }
/*      */     else
/*      */     {
/* 3417 */       Map portTypes = def.getPortTypes();
/*      */ 
/* 3419 */       if (portTypes.containsValue(portType)) {
/* 3420 */         entry.setIsReferenced(true);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3425 */     Iterator operations = portType.getOperations().iterator();
/*      */ 
/* 3428 */     while (operations.hasNext()) {
/* 3429 */       Operation operation = (Operation)operations.next();
/* 3430 */       Input input = operation.getInput();
/* 3431 */       Output output = operation.getOutput();
/*      */ 
/* 3434 */       boolean literalInput = false;
/* 3435 */       boolean literalOutput = false;
/*      */ 
/* 3437 */       if (bEntry != null) {
/* 3438 */         literalInput = bEntry.getInputBodyType(operation) == Use.LITERAL;
/*      */ 
/* 3440 */         literalOutput = bEntry.getOutputBodyType(operation) == Use.LITERAL;
/*      */       }
/*      */ 
/* 3445 */       if (input != null) {
/* 3446 */         Message message = input.getMessage();
/*      */ 
/* 3448 */         if (message != null) {
/* 3449 */           MessageEntry mEntry = getMessageEntry(message.getQName());
/*      */ 
/* 3451 */           if (mEntry != null) {
/* 3452 */             setMessageReferences(mEntry, def, doc, literalInput);
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 3458 */       if (output != null) {
/* 3459 */         Message message = output.getMessage();
/*      */ 
/* 3461 */         if (message != null) {
/* 3462 */           MessageEntry mEntry = getMessageEntry(message.getQName());
/*      */ 
/* 3464 */           if (mEntry != null) {
/* 3465 */             setMessageReferences(mEntry, def, doc, literalOutput);
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 3471 */       Iterator faults = operation.getFaults().values().iterator();
/*      */ 
/* 3473 */       while (faults.hasNext()) {
/* 3474 */         Message message = ((Fault)faults.next()).getMessage();
/*      */ 
/* 3476 */         if (message != null) {
/* 3477 */           MessageEntry mEntry = getMessageEntry(message.getQName());
/*      */ 
/* 3479 */           if (mEntry != null)
/* 3480 */             setMessageReferences(mEntry, def, doc, false);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setBindingReferences(BindingEntry entry, Definition def, Document doc)
/*      */   {
/* 3498 */     if (entry.getBindingType() == 0)
/*      */     {
/* 3502 */       Binding binding = entry.getBinding();
/*      */ 
/* 3504 */       if (this.addImports) {
/* 3505 */         entry.setIsReferenced(true);
/*      */       }
/*      */       else
/*      */       {
/* 3511 */         Map bindings = def.getBindings();
/*      */ 
/* 3513 */         if (bindings.containsValue(binding)) {
/* 3514 */           entry.setIsReferenced(true);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 3519 */       PortType portType = binding.getPortType();
/* 3520 */       PortTypeEntry ptEntry = getPortTypeEntry(portType.getQName());
/*      */ 
/* 3522 */       if (ptEntry != null)
/* 3523 */         setPortTypeReferences(ptEntry, entry, def, doc);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setServiceReferences(ServiceEntry entry, Definition def, Document doc)
/*      */   {
/* 3541 */     Service service = entry.getService();
/*      */ 
/* 3543 */     if (this.addImports) {
/* 3544 */       entry.setIsReferenced(true);
/*      */     }
/*      */     else
/*      */     {
/* 3550 */       Map services = def.getServices();
/*      */ 
/* 3552 */       if (services.containsValue(service)) {
/* 3553 */         entry.setIsReferenced(true);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3558 */     Iterator ports = service.getPorts().values().iterator();
/*      */ 
/* 3560 */     while (ports.hasNext()) {
/* 3561 */       Port port = (Port)ports.next();
/* 3562 */       Binding binding = port.getBinding();
/*      */ 
/* 3564 */       if (binding != null) {
/* 3565 */         BindingEntry bEntry = getBindingEntry(binding.getQName());
/*      */ 
/* 3567 */         if (bEntry != null)
/* 3568 */           setBindingReferences(bEntry, def, doc);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private SymTabEntry symbolTablePut(SymTabEntry entry)
/*      */     throws IOException
/*      */   {
/* 3582 */     QName name = entry.getQName();
/*      */ 
/* 3584 */     SymTabEntry e = get(name, entry.getClass());
/*      */ 
/* 3586 */     if (e == null) {
/* 3587 */       e = entry;
/*      */ 
/* 3590 */       if (((entry instanceof Type)) && (get(name, UndefinedType.class) != null))
/*      */       {
/* 3597 */         if ((((TypeEntry)get(name, UndefinedType.class)).isSimpleType()) && (!((TypeEntry)entry).isSimpleType()))
/*      */         {
/* 3602 */           throw new IOException(Messages.getMessage("AttrNotSimpleType01", name.toString()));
/*      */         }
/*      */ 
/* 3607 */         Vector v = (Vector)this.symbolTable.get(name);
/*      */ 
/* 3609 */         for (int i = 0; i < v.size(); i++) {
/* 3610 */           Object oldEntry = v.elementAt(i);
/*      */ 
/* 3612 */           if (!(oldEntry instanceof UndefinedType)) {
/*      */             continue;
/*      */           }
/* 3615 */           v.setElementAt(entry, i);
/*      */ 
/* 3618 */           this.typeTypeEntries.put(name, entry);
/*      */ 
/* 3621 */           ((UndefinedType)oldEntry).update((Type)entry);
/*      */         }
/*      */       }
/* 3624 */       else if (((entry instanceof Element)) && (get(name, UndefinedElement.class) != null))
/*      */       {
/* 3631 */         Vector v = (Vector)this.symbolTable.get(name);
/*      */ 
/* 3633 */         for (int i = 0; i < v.size(); i++) {
/* 3634 */           Object oldEntry = v.elementAt(i);
/*      */ 
/* 3636 */           if (!(oldEntry instanceof UndefinedElement)) {
/*      */             continue;
/*      */           }
/* 3639 */           v.setElementAt(entry, i);
/*      */ 
/* 3642 */           this.elementTypeEntries.put(name, entry);
/*      */ 
/* 3645 */           ((Undefined)oldEntry).update((Element)entry);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 3651 */         Vector v = (Vector)this.symbolTable.get(name);
/*      */ 
/* 3653 */         if (v == null) {
/* 3654 */           v = new Vector();
/*      */ 
/* 3656 */           this.symbolTable.put(name, v);
/*      */         }
/*      */ 
/* 3659 */         v.add(entry);
/*      */ 
/* 3663 */         if ((entry instanceof Element))
/* 3664 */           this.elementTypeEntries.put(name, entry);
/* 3665 */         else if ((entry instanceof Type)) {
/* 3666 */           this.typeTypeEntries.put(name, entry);
/*      */         }
/*      */       }
/*      */     }
/* 3670 */     else if (!this.quiet) {
/* 3671 */       System.out.println(Messages.getMessage("alreadyExists00", "" + name));
/*      */     }
/*      */ 
/* 3676 */     return e;
/*      */   }
/*      */ 
/*      */   protected boolean existsPortWithName(QName name)
/*      */   {
/* 3690 */     Vector v = (Vector)this.symbolTable.get(name);
/*      */ 
/* 3692 */     if (v == null) {
/* 3693 */       return false;
/*      */     }
/*      */ 
/* 3696 */     Iterator it = v.iterator();
/*      */ 
/* 3698 */     while (it.hasNext()) {
/* 3699 */       Object o = it.next();
/*      */ 
/* 3701 */       if ((o instanceof PortEntry)) {
/* 3702 */         return true;
/*      */       }
/*      */     }
/*      */ 
/* 3706 */     return false;
/*      */   }
/*      */ 
/*      */   private QName getInnerCollectionComponentQName(Node node)
/*      */   {
/* 3717 */     if (node == null) {
/* 3718 */       return null;
/*      */     }
/*      */ 
/* 3721 */     QName name = SchemaUtils.getCollectionComponentQName(node, new QNameHolder(), new BooleanHolder(), this);
/*      */ 
/* 3723 */     if (name != null) {
/* 3724 */       return name;
/*      */     }
/*      */ 
/* 3728 */     NodeList children = node.getChildNodes();
/*      */ 
/* 3730 */     for (int i = 0; i < children.getLength(); i++) {
/* 3731 */       name = getInnerCollectionComponentQName(children.item(i));
/*      */ 
/* 3733 */       if (name != null) {
/* 3734 */         return name;
/*      */       }
/*      */     }
/*      */ 
/* 3738 */     return null;
/*      */   }
/*      */ 
/*      */   private static QName getInnerTypeQName(Node node)
/*      */   {
/* 3749 */     if (node == null) {
/* 3750 */       return null;
/*      */     }
/*      */ 
/* 3753 */     BooleanHolder forElement = new BooleanHolder();
/* 3754 */     QName name = Utils.getTypeQName(node, forElement, true);
/*      */ 
/* 3756 */     if (name != null) {
/* 3757 */       return name;
/*      */     }
/*      */ 
/* 3761 */     NodeList children = node.getChildNodes();
/*      */ 
/* 3763 */     for (int i = 0; i < children.getLength(); i++) {
/* 3764 */       name = getInnerTypeQName(children.item(i));
/*      */ 
/* 3766 */       if (name != null) {
/* 3767 */         return name;
/*      */       }
/*      */     }
/*      */ 
/* 3771 */     return null;
/*      */   }
/*      */ 
/*      */   protected void processTypes() {
/* 3775 */     for (Iterator i = this.typeTypeEntries.values().iterator(); i.hasNext(); ) {
/* 3776 */       Type type = (Type)i.next();
/* 3777 */       Node node = type.getNode();
/*      */ 
/* 3780 */       Vector attributes = SchemaUtils.getContainedAttributeTypes(node, this);
/*      */ 
/* 3783 */       if (attributes != null) {
/* 3784 */         type.setContainedAttributes(attributes);
/*      */       }
/*      */ 
/* 3788 */       Vector elements = SchemaUtils.getContainedElementDeclarations(node, this);
/*      */ 
/* 3791 */       if (elements != null)
/* 3792 */         type.setContainedElements(elements);
/*      */     }
/*      */   }
/*      */ 
/*      */   public List getMessageEntries()
/*      */   {
/* 3798 */     List messageEntries = new ArrayList();
/* 3799 */     Iterator iter = this.symbolTable.values().iterator();
/* 3800 */     while (iter.hasNext()) {
/* 3801 */       Vector v = (Vector)iter.next();
/* 3802 */       for (int i = 0; i < v.size(); i++) {
/* 3803 */         SymTabEntry entry = (SymTabEntry)v.elementAt(i);
/* 3804 */         if ((entry instanceof MessageEntry)) {
/* 3805 */           messageEntries.add(entry);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3810 */     return messageEntries;
/*      */   }
/*      */ 
/*      */   public void setWrapArrays(boolean wrapArrays) {
/* 3814 */     this.wrapArrays = wrapArrays;
/*      */   }
/*      */ 
/*      */   public Map getElementFormDefaults() {
/* 3818 */     return this.elementFormDefaults;
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.SymbolTable
 * JD-Core Version:    0.6.0
 */