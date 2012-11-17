/*      */ package org.apache.axis.wsdl.fromJava;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.net.URL;
/*      */ import java.rmi.RemoteException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import javax.wsdl.Definition;
/*      */ import javax.wsdl.WSDLException;
/*      */ import javax.xml.namespace.QName;
/*      */ import javax.xml.parsers.ParserConfigurationException;
/*      */ import javax.xml.rpc.holders.Holder;
/*      */ import org.apache.axis.AxisFault;
/*      */ import org.apache.axis.AxisProperties;
/*      */ import org.apache.axis.Constants;
/*      */ import org.apache.axis.InternalException;
/*      */ import org.apache.axis.MessageContext;
/*      */ import org.apache.axis.components.logger.LogFactory;
/*      */ import org.apache.axis.constants.Style;
/*      */ import org.apache.axis.description.ServiceDesc;
/*      */ import org.apache.axis.encoding.Serializer;
/*      */ import org.apache.axis.encoding.SerializerFactory;
/*      */ import org.apache.axis.encoding.SimpleType;
/*      */ import org.apache.axis.encoding.TypeMapping;
/*      */ import org.apache.axis.encoding.ser.BeanSerializerFactory;
/*      */ import org.apache.axis.encoding.ser.EnumSerializerFactory;
/*      */ import org.apache.axis.handlers.soap.SOAPService;
/*      */ import org.apache.axis.soap.SOAPConstants;
/*      */ import org.apache.axis.utils.JavaUtils;
/*      */ import org.apache.axis.utils.Messages;
/*      */ import org.apache.axis.utils.StringUtils;
/*      */ import org.apache.axis.utils.XMLUtils;
/*      */ import org.apache.axis.wsdl.symbolTable.BaseTypeMapping;
/*      */ import org.apache.axis.wsdl.symbolTable.SymbolTable;
/*      */ import org.apache.axis.wsdl.symbolTable.TypeEntry;
/*      */ import org.apache.commons.logging.Log;
/*      */ import org.w3c.dom.Attr;
/*      */ import org.w3c.dom.Document;
/*      */ import org.w3c.dom.Element;
/*      */ import org.w3c.dom.NamedNodeMap;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.NodeList;
/*      */ import org.xml.sax.SAXException;
/*      */ 
/*      */ public class Types
/*      */ {
/*   78 */   protected static Log log = LogFactory.getLog(Types.class.getName());
/*      */   Definition def;
/*   84 */   Namespaces namespaces = null;
/*      */   TypeMapping tm;
/*      */   TypeMapping defaultTM;
/*      */   String targetNamespace;
/*   96 */   Element wsdlTypesElem = null;
/*      */ 
/*   99 */   HashMap schemaTypes = null;
/*      */ 
/*  102 */   HashMap schemaElementNames = null;
/*      */ 
/*  105 */   HashMap schemaUniqueElementNames = null;
/*      */ 
/*  108 */   HashMap wrapperMap = new HashMap();
/*      */ 
/*  111 */   List stopClasses = null;
/*      */ 
/*  114 */   List beanCompatErrs = new ArrayList();
/*      */ 
/*  117 */   private ServiceDesc serviceDesc = null;
/*      */ 
/*  120 */   private Set writtenElementQNames = new HashSet();
/*      */ 
/*  123 */   Class[] mappedTypes = null;
/*      */ 
/*  126 */   Emitter emitter = null;
/*      */   Document docHolder;
/*      */ 
/*      */   public static boolean isArray(Class clazz)
/*      */   {
/*  130 */     return (clazz.isArray()) || (Collection.class.isAssignableFrom(clazz));
/*      */   }
/*      */ 
/*      */   private static Class getComponentType(Class clazz)
/*      */   {
/*  135 */     if (clazz.isArray())
/*      */     {
/*  137 */       return clazz.getComponentType();
/*      */     }
/*  139 */     if (Collection.class.isAssignableFrom(clazz))
/*      */     {
/*  141 */       return Object.class;
/*      */     }
/*      */ 
/*  145 */     return null;
/*      */   }
/*      */ 
/*      */   public Types(Definition def, TypeMapping tm, TypeMapping defaultTM, Namespaces namespaces, String targetNamespace, List stopClasses, ServiceDesc serviceDesc)
/*      */   {
/*  165 */     this.def = def;
/*  166 */     this.serviceDesc = serviceDesc;
/*      */ 
/*  168 */     createDocumentFragment();
/*      */ 
/*  170 */     this.tm = tm;
/*  171 */     this.defaultTM = defaultTM;
/*      */ 
/*  173 */     this.mappedTypes = tm.getAllClasses();
/*      */ 
/*  175 */     this.namespaces = namespaces;
/*  176 */     this.targetNamespace = targetNamespace;
/*  177 */     this.stopClasses = stopClasses;
/*  178 */     this.schemaElementNames = new HashMap();
/*  179 */     this.schemaUniqueElementNames = new HashMap();
/*  180 */     this.schemaTypes = new HashMap();
/*      */   }
/*      */ 
/*      */   public Types(Definition def, TypeMapping tm, TypeMapping defaultTM, Namespaces namespaces, String targetNamespace, List stopClasses, ServiceDesc serviceDesc, Emitter emitter)
/*      */   {
/*  199 */     this(def, tm, defaultTM, namespaces, targetNamespace, stopClasses, serviceDesc);
/*  200 */     this.emitter = emitter;
/*      */   }
/*      */ 
/*      */   public Namespaces getNamespaces()
/*      */   {
/*  209 */     return this.namespaces;
/*      */   }
/*      */ 
/*      */   public void loadInputSchema(String inputSchema)
/*      */     throws IOException, WSDLException, SAXException, ParserConfigurationException
/*      */   {
/*  226 */     Document doc = XMLUtils.newDocument(inputSchema);
/*      */ 
/*  229 */     Element root = doc.getDocumentElement();
/*      */ 
/*  231 */     if ((root.getLocalName().equals("schema")) && (Constants.isSchemaXSD(root.getNamespaceURI())))
/*      */     {
/*  233 */       Node schema = this.docHolder.importNode(root, true);
/*      */ 
/*  235 */       if (null == this.wsdlTypesElem) {
/*  236 */         writeWsdlTypesElement();
/*      */       }
/*      */ 
/*  239 */       this.wsdlTypesElem.appendChild(schema);
/*      */ 
/*  242 */       BaseTypeMapping btm = new BaseTypeMapping()
/*      */       {
/*      */         public String getBaseName(QName qNameIn)
/*      */         {
/*  246 */           QName qName = new QName(qNameIn.getNamespaceURI(), qNameIn.getLocalPart());
/*      */ 
/*  248 */           Class cls = Types.this.defaultTM.getClassForQName(qName);
/*      */ 
/*  250 */           if (cls == null) {
/*  251 */             return null;
/*      */           }
/*  253 */           return JavaUtils.getTextClassName(cls.getName());
/*      */         }
/*      */       };
/*  257 */       SymbolTable symbolTable = new SymbolTable(btm, true, false, false);
/*      */ 
/*  259 */       symbolTable.populateTypes(new URL(inputSchema), doc);
/*  260 */       processSymTabEntries(symbolTable);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void processSymTabEntries(SymbolTable symbolTable)
/*      */   {
/*  278 */     Iterator iterator = symbolTable.getElementIndex().entrySet().iterator();
/*      */ 
/*  280 */     while (iterator.hasNext()) {
/*  281 */       Map.Entry me = (Map.Entry)iterator.next();
/*  282 */       QName name = (QName)me.getKey();
/*  283 */       TypeEntry te = (TypeEntry)me.getValue();
/*  284 */       String prefix = XMLUtils.getPrefix(name.getNamespaceURI(), te.getNode());
/*      */ 
/*  287 */       if ((null != prefix) && (!"".equals(prefix))) {
/*  288 */         this.namespaces.putPrefix(name.getNamespaceURI(), prefix);
/*  289 */         this.def.addNamespace(prefix, name.getNamespaceURI());
/*      */       }
/*      */ 
/*  292 */       addToElementsList(name);
/*      */     }
/*      */ 
/*  295 */     iterator = symbolTable.getTypeIndex().entrySet().iterator();
/*      */ 
/*  297 */     while (iterator.hasNext()) {
/*  298 */       Map.Entry me = (Map.Entry)iterator.next();
/*  299 */       QName name = (QName)me.getKey();
/*  300 */       TypeEntry te = (TypeEntry)me.getValue();
/*  301 */       String prefix = XMLUtils.getPrefix(name.getNamespaceURI(), te.getNode());
/*      */ 
/*  304 */       if ((null != prefix) && (!"".equals(prefix))) {
/*  305 */         this.namespaces.putPrefix(name.getNamespaceURI(), prefix);
/*  306 */         this.def.addNamespace(prefix, name.getNamespaceURI());
/*      */       }
/*      */ 
/*  309 */       addToTypesList(name);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void loadInputTypes(String inputWSDL)
/*      */     throws IOException, WSDLException, SAXException, ParserConfigurationException
/*      */   {
/*  327 */     Document doc = XMLUtils.newDocument(inputWSDL);
/*      */ 
/*  330 */     NodeList elements = doc.getChildNodes();
/*      */ 
/*  332 */     if ((elements.getLength() > 0) && (elements.item(0).getLocalName().equals("definitions")))
/*      */     {
/*  334 */       elements = elements.item(0).getChildNodes();
/*      */ 
/*  336 */       int i = 0;
/*  337 */       while ((i < elements.getLength()) && (this.wsdlTypesElem == null))
/*      */       {
/*  339 */         Node node = elements.item(i);
/*      */ 
/*  341 */         if ((node.getLocalName() != null) && (node.getLocalName().equals("types")))
/*      */         {
/*  343 */           this.wsdlTypesElem = ((Element)node);
/*      */         }
/*  338 */         i++;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  349 */     if (this.wsdlTypesElem == null) {
/*  350 */       return;
/*      */     }
/*      */ 
/*  354 */     this.wsdlTypesElem = ((Element)this.docHolder.importNode(this.wsdlTypesElem, true));
/*      */ 
/*  356 */     this.docHolder.appendChild(this.wsdlTypesElem);
/*      */ 
/*  359 */     BaseTypeMapping btm = new BaseTypeMapping()
/*      */     {
/*      */       public String getBaseName(QName qNameIn)
/*      */       {
/*  363 */         QName qName = new QName(qNameIn.getNamespaceURI(), qNameIn.getLocalPart());
/*      */ 
/*  365 */         Class cls = Types.this.tm.getClassForQName(qName);
/*      */ 
/*  367 */         if (cls == null) {
/*  368 */           return null;
/*      */         }
/*  370 */         return JavaUtils.getTextClassName(cls.getName());
/*      */       }
/*      */     };
/*  374 */     SymbolTable symbolTable = new SymbolTable(btm, true, false, false);
/*      */ 
/*  376 */     symbolTable.populate(null, doc);
/*  377 */     processSymTabEntries(symbolTable);
/*      */   }
/*      */ 
/*      */   public QName writeTypeForPart(Class type, QName qname)
/*      */     throws AxisFault
/*      */   {
/*  402 */     if (type.getName().equals("void")) {
/*  403 */       return null;
/*      */     }
/*      */ 
/*  406 */     if (Holder.class.isAssignableFrom(type)) {
/*  407 */       type = JavaUtils.getHolderValueType(type);
/*      */     }
/*      */ 
/*  411 */     if ((qname == null) || ((Constants.isSOAP_ENC(qname.getNamespaceURI())) && ("Array".equals(qname.getLocalPart()))))
/*      */     {
/*  414 */       qname = getTypeQName(type);
/*      */ 
/*  416 */       if (qname == null) {
/*  417 */         throw new AxisFault("Class:" + type.getName());
/*      */       }
/*      */     }
/*      */ 
/*  421 */     if (!makeTypeElement(type, qname, null)) {
/*  422 */       qname = Constants.XSD_ANYTYPE;
/*      */     }
/*      */ 
/*  425 */     return qname;
/*      */   }
/*      */ 
/*      */   public QName writeTypeAndSubTypeForPart(Class type, QName qname)
/*      */     throws AxisFault
/*      */   {
/*  442 */     QName qNameRet = writeTypeForPart(type, qname);
/*      */ 
/*  446 */     if (this.mappedTypes != null) {
/*  447 */       for (int i = 0; i < this.mappedTypes.length; i++) {
/*  448 */         Class tempMappedType = this.mappedTypes[i];
/*      */ 
/*  454 */         if ((tempMappedType == null) || (type == Object.class) || (tempMappedType == type) || (!type.isAssignableFrom(tempMappedType)))
/*      */         {
/*      */           continue;
/*      */         }
/*  458 */         QName name = this.tm.getTypeQName(tempMappedType);
/*  459 */         if (!isAnonymousType(name)) {
/*  460 */           writeTypeForPart(tempMappedType, name);
/*      */         }
/*      */ 
/*  465 */         this.mappedTypes[i] = null;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  470 */     return qNameRet;
/*      */   }
/*      */ 
/*      */   public QName writeElementForPart(Class type, QName qname)
/*      */     throws AxisFault
/*      */   {
/*  493 */     if (type.getName().equals("void")) {
/*  494 */       return null;
/*      */     }
/*      */ 
/*  497 */     if (Holder.class.isAssignableFrom(type)) {
/*  498 */       type = JavaUtils.getHolderValueType(type);
/*      */     }
/*      */ 
/*  502 */     if ((qname == null) || ((Constants.isSOAP_ENC(qname.getNamespaceURI())) && ("Array".equals(qname.getLocalPart()))))
/*      */     {
/*  505 */       qname = getTypeQName(type);
/*      */ 
/*  507 */       if (qname == null) {
/*  508 */         throw new AxisFault("Class:" + type.getName());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  513 */     String nsURI = qname.getNamespaceURI();
/*      */ 
/*  515 */     if ((Constants.isSchemaXSD(nsURI)) || ((Constants.isSOAP_ENC(nsURI)) && (!"Array".equals(qname.getLocalPart()))))
/*      */     {
/*  518 */       return null;
/*      */     }
/*      */ 
/*  522 */     if (this.wsdlTypesElem == null) {
/*  523 */       writeWsdlTypesElement();
/*      */     }
/*      */ 
/*  527 */     if (writeTypeAsElement(type, qname) == null) {
/*  528 */       qname = null;
/*      */     }
/*      */ 
/*  531 */     return qname;
/*      */   }
/*      */ 
/*      */   public Element writeWrapperElement(QName qname, boolean request, boolean hasParams)
/*      */     throws AxisFault
/*      */   {
/*  556 */     if (this.wsdlTypesElem == null) {
/*  557 */       writeWsdlTypesElement();
/*      */     }
/*      */ 
/*  561 */     writeTypeNamespace(qname.getNamespaceURI());
/*      */ 
/*  564 */     Element wrapperElement = this.docHolder.createElement("element");
/*      */ 
/*  566 */     writeSchemaElementDecl(qname, wrapperElement);
/*  567 */     wrapperElement.setAttribute("name", qname.getLocalPart());
/*      */ 
/*  570 */     Element complexType = this.docHolder.createElement("complexType");
/*      */ 
/*  572 */     wrapperElement.appendChild(complexType);
/*      */ 
/*  576 */     if (hasParams) {
/*  577 */       Element sequence = this.docHolder.createElement("sequence");
/*      */ 
/*  579 */       complexType.appendChild(sequence);
/*      */ 
/*  581 */       return sequence;
/*      */     }
/*      */ 
/*  584 */     return null;
/*      */   }
/*      */ 
/*      */   public void writeWrappedParameter(Element sequence, String name, QName type, Class javaType)
/*      */     throws AxisFault
/*      */   {
/*  602 */     if (javaType == Void.TYPE) {
/*  603 */       return;
/*      */     }
/*      */ 
/*  609 */     if ((javaType.isArray()) && (!javaType.equals(new byte[0].getClass())))
/*  610 */       type = writeTypeForPart(javaType.getComponentType(), null);
/*      */     else {
/*  612 */       type = writeTypeForPart(javaType, type);
/*      */     }
/*      */ 
/*  615 */     if (type == null)
/*      */     {
/*  617 */       return;
/*      */     }
/*      */     Element childElem;
/*      */     Element childElem;
/*  622 */     if (isAnonymousType(type)) {
/*  623 */       childElem = createElementWithAnonymousType(name, javaType, false, this.docHolder);
/*      */     }
/*      */     else
/*      */     {
/*  628 */       childElem = this.docHolder.createElement("element");
/*      */ 
/*  630 */       childElem.setAttribute("name", name);
/*      */ 
/*  632 */       String prefix = this.namespaces.getCreatePrefix(type.getNamespaceURI());
/*      */ 
/*  634 */       String prefixedName = prefix + ":" + type.getLocalPart();
/*      */ 
/*  636 */       childElem.setAttribute("type", prefixedName);
/*      */ 
/*  641 */       if ((javaType.isArray()) && (!javaType.equals(new byte[0].getClass()))) {
/*  642 */         childElem.setAttribute("maxOccurs", "unbounded");
/*      */       }
/*      */     }
/*      */ 
/*  646 */     sequence.appendChild(childElem);
/*      */   }
/*      */ 
/*      */   private boolean isAnonymousType(QName type)
/*      */   {
/*  656 */     return type.getLocalPart().indexOf(">") != -1;
/*      */   }
/*      */ 
/*      */   private QName writeTypeAsElement(Class type, QName qName)
/*      */     throws AxisFault
/*      */   {
/*  669 */     if ((qName == null) || (Constants.equals(Constants.SOAP_ARRAY, qName))) {
/*  670 */       qName = getTypeQName(type);
/*      */     }
/*      */ 
/*  673 */     writeTypeNamespace(type, qName);
/*  674 */     String elementType = writeType(type, qName);
/*      */ 
/*  676 */     if (elementType != null)
/*      */     {
/*  681 */       return qName;
/*      */     }
/*      */ 
/*  684 */     return null;
/*      */   }
/*      */ 
/*      */   private QName writeTypeNamespace(Class type, QName qName)
/*      */   {
/*  697 */     if (qName == null) {
/*  698 */       qName = getTypeQName(type);
/*      */     }
/*      */ 
/*  701 */     writeTypeNamespace(qName.getNamespaceURI());
/*      */ 
/*  703 */     return qName;
/*      */   }
/*      */ 
/*      */   private void writeTypeNamespace(String namespaceURI)
/*      */   {
/*  713 */     if ((namespaceURI != null) && (!namespaceURI.equals(""))) {
/*  714 */       String pref = this.def.getPrefix(namespaceURI);
/*      */ 
/*  716 */       if (pref == null)
/*  717 */         this.def.addNamespace(this.namespaces.getCreatePrefix(namespaceURI), namespaceURI);
/*      */     }
/*      */   }
/*      */ 
/*      */   public QName getTypeQName(Class javaType)
/*      */   {
/*  730 */     QName qName = null;
/*      */ 
/*  733 */     qName = this.tm.getTypeQName(javaType);
/*      */ 
/*  738 */     if ((isArray(javaType)) && (Constants.equals(Constants.SOAP_ARRAY, qName)))
/*      */     {
/*  740 */       Class componentType = getComponentType(javaType);
/*      */ 
/*  744 */       String arrayTypePrefix = "ArrayOf";
/*      */ 
/*  746 */       boolean isWSICompliant = JavaUtils.isTrue(AxisProperties.getProperty("axis.ws-i.bp11.compatibility"));
/*      */ 
/*  748 */       if (isWSICompliant) {
/*  749 */         arrayTypePrefix = "MyArrayOf";
/*      */       }
/*      */ 
/*  756 */       QName cqName = getTypeQName(componentType);
/*      */ 
/*  758 */       if (this.targetNamespace.equals(cqName.getNamespaceURI())) {
/*  759 */         qName = new QName(this.targetNamespace, arrayTypePrefix + cqName.getLocalPart());
/*      */       }
/*      */       else {
/*  762 */         String pre = this.namespaces.getCreatePrefix(cqName.getNamespaceURI());
/*      */ 
/*  765 */         qName = new QName(this.targetNamespace, arrayTypePrefix + "_" + pre + "_" + cqName.getLocalPart());
/*      */       }
/*      */ 
/*  770 */       return qName;
/*      */     }
/*      */ 
/*  775 */     if (qName == null) {
/*  776 */       String pkg = getPackageNameFromFullName(javaType.getName());
/*  777 */       String lcl = getLocalNameFromFullName(javaType.getName());
/*  778 */       String ns = this.namespaces.getCreate(pkg);
/*      */ 
/*  780 */       this.namespaces.getCreatePrefix(ns);
/*      */ 
/*  782 */       String localPart = lcl.replace('$', '_');
/*      */ 
/*  784 */       qName = new QName(ns, localPart);
/*      */     }
/*      */ 
/*  787 */     return qName;
/*      */   }
/*      */ 
/*      */   public String getQNameString(QName qname)
/*      */   {
/*  800 */     String prefix = this.namespaces.getCreatePrefix(qname.getNamespaceURI());
/*      */ 
/*  802 */     return prefix + ":" + qname.getLocalPart();
/*      */   }
/*      */ 
/*      */   public static String getPackageNameFromFullName(String full)
/*      */   {
/*  813 */     if (full.lastIndexOf('.') < 0) {
/*  814 */       return "";
/*      */     }
/*  816 */     return full.substring(0, full.lastIndexOf('.'));
/*      */   }
/*      */ 
/*      */   public static String getLocalNameFromFullName(String full)
/*      */   {
/*  828 */     String end = "";
/*      */ 
/*  830 */     if (full.startsWith("[L")) {
/*  831 */       end = "[]";
/*  832 */       full = full.substring(3, full.length() - 1);
/*      */     }
/*      */ 
/*  835 */     if (full.lastIndexOf('.') < 0) {
/*  836 */       return full + end;
/*      */     }
/*  838 */     return full.substring(full.lastIndexOf('.') + 1) + end;
/*      */   }
/*      */ 
/*      */   public void writeSchemaTypeDecl(QName qname, Element element)
/*      */     throws AxisFault
/*      */   {
/*  851 */     writeSchemaElement(qname.getNamespaceURI(), element);
/*      */   }
/*      */ 
/*      */   public void writeSchemaElementDecl(QName qname, Element element)
/*      */     throws AxisFault
/*      */   {
/*  864 */     if (this.writtenElementQNames.contains(qname)) {
/*  865 */       throw new AxisFault("Server.generalException", Messages.getMessage("duplicateSchemaElement", qname.toString()), null, null);
/*      */     }
/*      */ 
/*  871 */     writeSchemaElement(qname.getNamespaceURI(), element);
/*  872 */     this.writtenElementQNames.add(qname);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void writeSchemaElement(QName qName, Element element)
/*      */     throws AxisFault
/*      */   {
/*  885 */     writeSchemaElement(qName.getNamespaceURI(), element);
/*      */   }
/*      */ 
/*      */   public void writeSchemaElement(String namespaceURI, Element element)
/*      */     throws AxisFault
/*      */   {
/*  899 */     if (this.wsdlTypesElem == null) {
/*      */       try {
/*  901 */         writeWsdlTypesElement();
/*      */       } catch (Exception e) {
/*  903 */         log.error(e);
/*      */ 
/*  905 */         return;
/*      */       }
/*      */     }
/*      */ 
/*  909 */     if ((namespaceURI == null) || (namespaceURI.equals(""))) {
/*  910 */       throw new AxisFault("Server.generalException", Messages.getMessage("noNamespace00", namespaceURI), null, null);
/*      */     }
/*      */ 
/*  915 */     Element schemaElem = null;
/*  916 */     NodeList nl = this.wsdlTypesElem.getChildNodes();
/*      */ 
/*  918 */     for (int i = 0; i < nl.getLength(); i++) {
/*  919 */       NamedNodeMap attrs = nl.item(i).getAttributes();
/*      */ 
/*  921 */       if (attrs != null) {
/*  922 */         for (int n = 0; n < attrs.getLength(); n++) {
/*  923 */           Attr a = (Attr)attrs.item(n);
/*      */ 
/*  925 */           if ((!a.getName().equals("targetNamespace")) || (!a.getValue().equals(namespaceURI)))
/*      */             continue;
/*  927 */           schemaElem = (Element)nl.item(i);
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  933 */     if (schemaElem == null) {
/*  934 */       schemaElem = this.docHolder.createElement("schema");
/*      */ 
/*  936 */       this.wsdlTypesElem.appendChild(schemaElem);
/*  937 */       schemaElem.setAttribute("xmlns", "http://www.w3.org/2001/XMLSchema");
/*  938 */       schemaElem.setAttribute("targetNamespace", namespaceURI);
/*      */ 
/*  941 */       if (this.serviceDesc.getStyle() == Style.RPC) {
/*  942 */         Element importElem = this.docHolder.createElement("import");
/*      */ 
/*  944 */         schemaElem.appendChild(importElem);
/*  945 */         importElem.setAttribute("namespace", Constants.URI_DEFAULT_SOAP_ENC);
/*      */       }
/*      */ 
/*  949 */       SOAPService service = null;
/*  950 */       if (MessageContext.getCurrentContext() != null) {
/*  951 */         service = MessageContext.getCurrentContext().getService();
/*      */       }
/*  953 */       if ((service != null) && (isPresent((String)service.getOption("schemaQualified"), namespaceURI)))
/*  954 */         schemaElem.setAttribute("elementFormDefault", "qualified");
/*  955 */       else if ((service == null) || (!isPresent((String)service.getOption("schemaUnqualified"), namespaceURI)))
/*      */       {
/*  957 */         if ((this.serviceDesc.getStyle() == Style.DOCUMENT) || (this.serviceDesc.getStyle() == Style.WRAPPED))
/*      */         {
/*  959 */           schemaElem.setAttribute("elementFormDefault", "qualified");
/*      */         }
/*      */       }
/*  962 */       writeTypeNamespace(namespaceURI);
/*      */     }
/*      */ 
/*  965 */     schemaElem.appendChild(element);
/*      */   }
/*      */ 
/*      */   private boolean isPresent(String list, String namespace)
/*      */   {
/*  975 */     if ((list == null) || (list.length() == 0))
/*  976 */       return false;
/*  977 */     String[] array = StringUtils.split(list, ',');
/*  978 */     for (int i = 0; i < array.length; i++) {
/*  979 */       if (array[i].equals(namespace))
/*  980 */         return true;
/*      */     }
/*  982 */     return false;
/*      */   }
/*      */ 
/*      */   private void writeWsdlTypesElement()
/*      */   {
/*  990 */     if (this.wsdlTypesElem == null)
/*      */     {
/*  993 */       this.wsdlTypesElem = this.docHolder.createElementNS("http://schemas.xmlsoap.org/wsdl/", "types");
/*      */ 
/*  996 */       this.wsdlTypesElem.setPrefix("wsdl");
/*      */     }
/*      */   }
/*      */ 
/*      */   public String writeType(Class type)
/*      */     throws AxisFault
/*      */   {
/* 1012 */     return writeType(type, null);
/*      */   }
/*      */ 
/*      */   public String writeType(Class type, QName qName)
/*      */     throws AxisFault
/*      */   {
/* 1030 */     if ((qName == null) || (Constants.equals(Constants.SOAP_ARRAY, qName))) {
/* 1031 */       qName = getTypeQName(type);
/*      */     }
/*      */ 
/* 1034 */     if (!makeTypeElement(type, qName, null)) {
/* 1035 */       return null;
/*      */     }
/*      */ 
/* 1038 */     return getQNameString(qName);
/*      */   }
/*      */ 
/*      */   public Element createArrayElement(String componentTypeName)
/*      */   {
/* 1050 */     MessageContext mc = MessageContext.getCurrentContext();
/*      */     SOAPConstants constants;
/*      */     SOAPConstants constants;
/* 1051 */     if ((mc == null) || (mc.getSOAPConstants() == null))
/* 1052 */       constants = SOAPConstants.SOAP11_CONSTANTS;
/*      */     else {
/* 1054 */       constants = mc.getSOAPConstants();
/*      */     }
/* 1056 */     String prefix = this.namespaces.getCreatePrefix(constants.getEncodingURI());
/*      */ 
/* 1058 */     Element complexType = this.docHolder.createElement("complexType");
/* 1059 */     Element complexContent = this.docHolder.createElement("complexContent");
/*      */ 
/* 1061 */     complexType.appendChild(complexContent);
/*      */ 
/* 1063 */     Element restriction = this.docHolder.createElement("restriction");
/*      */ 
/* 1065 */     complexContent.appendChild(restriction);
/* 1066 */     restriction.setAttribute("base", prefix + ":Array");
/*      */ 
/* 1069 */     Element attribute = this.docHolder.createElement("attribute");
/*      */ 
/* 1071 */     restriction.appendChild(attribute);
/*      */ 
/* 1073 */     attribute.setAttribute("ref", prefix + ":arrayType");
/*      */ 
/* 1076 */     prefix = this.namespaces.getCreatePrefix("http://schemas.xmlsoap.org/wsdl/");
/* 1077 */     attribute.setAttribute(prefix + ":arrayType", componentTypeName);
/*      */ 
/* 1080 */     return complexType;
/*      */   }
/*      */ 
/*      */   public Element createLiteralArrayElement(String componentType, QName itemName)
/*      */   {
/* 1094 */     String itemLocalName = "item";
/* 1095 */     if (itemName != null) {
/* 1096 */       itemLocalName = itemName.getLocalPart();
/*      */     }
/*      */ 
/* 1099 */     Element complexType = this.docHolder.createElement("complexType");
/* 1100 */     Element sequence = this.docHolder.createElement("sequence");
/*      */ 
/* 1102 */     complexType.appendChild(sequence);
/*      */ 
/* 1104 */     Element elem = this.docHolder.createElement("element");
/* 1105 */     elem.setAttribute("name", itemLocalName);
/* 1106 */     elem.setAttribute("type", componentType);
/* 1107 */     elem.setAttribute("minOccurs", "0");
/* 1108 */     elem.setAttribute("maxOccurs", "unbounded");
/*      */ 
/* 1110 */     sequence.appendChild(elem);
/*      */ 
/* 1112 */     return complexType;
/*      */   }
/*      */ 
/*      */   public static boolean isEnumClass(Class cls)
/*      */   {
/*      */     try
/*      */     {
/* 1125 */       Method m = cls.getMethod("getValue", null);
/* 1126 */       Method m2 = cls.getMethod("toString", null);
/*      */ 
/* 1128 */       if ((m != null) && (m2 != null)) {
/* 1129 */         Method m3 = cls.getDeclaredMethod("fromString", new Class[] { String.class });
/*      */ 
/* 1133 */         Method m4 = cls.getDeclaredMethod("fromValue", new Class[] { m.getReturnType() });
/*      */ 
/* 1137 */         if ((m3 != null) && (Modifier.isStatic(m3.getModifiers())) && (Modifier.isPublic(m3.getModifiers())) && (m4 != null) && (Modifier.isStatic(m4.getModifiers())) && (Modifier.isPublic(m4.getModifiers())))
/*      */         {
/*      */           try
/*      */           {
/* 1146 */             return cls.getMethod("setValue", new Class[] { m.getReturnType() }) == null;
/*      */           }
/*      */           catch (NoSuchMethodException e)
/*      */           {
/* 1151 */             return true;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (NoSuchMethodException e) {
/*      */     }
/* 1158 */     return false;
/*      */   }
/*      */ 
/*      */   public Element writeEnumType(QName qName, Class cls)
/*      */     throws NoSuchMethodException, IllegalAccessException, AxisFault
/*      */   {
/* 1175 */     if (!isEnumClass(cls)) {
/* 1176 */       return null;
/*      */     }
/*      */ 
/* 1180 */     Method m = cls.getMethod("getValue", null);
/* 1181 */     Class base = m.getReturnType();
/*      */ 
/* 1184 */     Element simpleType = this.docHolder.createElement("simpleType");
/*      */ 
/* 1186 */     simpleType.setAttribute("name", qName.getLocalPart());
/*      */ 
/* 1188 */     Element restriction = this.docHolder.createElement("restriction");
/*      */ 
/* 1190 */     simpleType.appendChild(restriction);
/*      */ 
/* 1192 */     String baseType = writeType(base, null);
/*      */ 
/* 1194 */     restriction.setAttribute("base", baseType);
/*      */ 
/* 1197 */     Field[] fields = cls.getDeclaredFields();
/*      */ 
/* 1199 */     for (int i = 0; i < fields.length; i++) {
/* 1200 */       Field field = fields[i];
/* 1201 */       int mod = field.getModifiers();
/*      */ 
/* 1205 */       if ((!Modifier.isPublic(mod)) || (!Modifier.isStatic(mod)) || (!Modifier.isFinal(mod)) || (field.getType() != base))
/*      */       {
/*      */         continue;
/*      */       }
/* 1209 */       Element enumeration = this.docHolder.createElement("enumeration");
/*      */ 
/* 1211 */       enumeration.setAttribute("value", field.get(null).toString());
/* 1212 */       restriction.appendChild(enumeration);
/*      */     }
/*      */ 
/* 1216 */     return simpleType;
/*      */   }
/*      */ 
/*      */   public void writeElementDecl(QName qname, Class javaType, QName typeQName, boolean nillable, QName itemQName)
/*      */     throws AxisFault
/*      */   {
/* 1236 */     if (this.writtenElementQNames.contains(qname)) {
/* 1237 */       return;
/*      */     }
/*      */ 
/* 1240 */     String name = qname.getLocalPart();
/*      */ 
/* 1242 */     Element element = this.docHolder.createElement("element");
/*      */ 
/* 1245 */     element.setAttribute("name", name);
/*      */ 
/* 1247 */     if (nillable) {
/* 1248 */       element.setAttribute("nillable", "true");
/*      */     }
/*      */ 
/* 1264 */     if (javaType.isArray())
/*      */     {
/* 1266 */       String componentType = writeType(javaType.getComponentType());
/* 1267 */       Element complexType = createLiteralArrayElement(componentType, itemQName);
/*      */ 
/* 1269 */       element.appendChild(complexType);
/*      */     }
/*      */     else
/*      */     {
/* 1273 */       makeTypeElement(javaType, typeQName, element);
/*      */     }
/*      */ 
/* 1276 */     writeSchemaElementDecl(qname, element);
/*      */   }
/*      */ 
/*      */   public Element createElement(String elementName, String elementType, boolean nullable, boolean omittable, Document docHolder)
/*      */   {
/* 1293 */     Element element = docHolder.createElement("element");
/*      */ 
/* 1295 */     element.setAttribute("name", elementName);
/*      */ 
/* 1297 */     if (nullable) {
/* 1298 */       element.setAttribute("nillable", "true");
/*      */     }
/*      */ 
/* 1301 */     if (omittable) {
/* 1302 */       element.setAttribute("minOccurs", "0");
/* 1303 */       element.setAttribute("maxOccurs", "1");
/*      */     }
/*      */ 
/* 1306 */     if (elementType != null) {
/* 1307 */       element.setAttribute("type", elementType);
/*      */     }
/*      */ 
/* 1310 */     return element;
/*      */   }
/*      */ 
/*      */   public Element createAttributeElement(String elementName, Class javaType, QName xmlType, boolean nullable, Document docHolder)
/*      */     throws AxisFault
/*      */   {
/* 1328 */     Element element = docHolder.createElement("attribute");
/*      */ 
/* 1330 */     element.setAttribute("name", elementName);
/*      */ 
/* 1332 */     if (nullable) {
/* 1333 */       element.setAttribute("nillable", "true");
/*      */     }
/*      */ 
/* 1336 */     makeTypeElement(javaType, xmlType, element);
/*      */ 
/* 1338 */     return element;
/*      */   }
/*      */ 
/*      */   boolean isSimpleType(Class type)
/*      */   {
/* 1351 */     QName qname = this.tm.getTypeQName(type);
/*      */ 
/* 1353 */     if (qname == null) {
/* 1354 */       return false;
/*      */     }
/*      */ 
/* 1357 */     String nsURI = qname.getNamespaceURI();
/*      */ 
/* 1359 */     return (Constants.isSchemaXSD(nsURI)) || (Constants.isSOAP_ENC(nsURI));
/*      */   }
/*      */ 
/*      */   public boolean isAcceptableAsAttribute(Class type)
/*      */   {
/* 1369 */     return (isSimpleType(type)) || (isEnumClass(type)) || (implementsSimpleType(type));
/*      */   }
/*      */ 
/*      */   boolean implementsSimpleType(Class type)
/*      */   {
/* 1381 */     Class[] impls = type.getInterfaces();
/*      */ 
/* 1383 */     for (int i = 0; i < impls.length; i++) {
/* 1384 */       if (impls[i] == SimpleType.class) {
/* 1385 */         return true;
/*      */       }
/*      */     }
/*      */ 
/* 1389 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean addToTypesList(QName qName)
/*      */   {
/* 1424 */     boolean added = false;
/* 1425 */     String namespaceURI = qName.getNamespaceURI();
/* 1426 */     ArrayList types = (ArrayList)this.schemaTypes.get(namespaceURI);
/*      */ 
/* 1429 */     if ((Constants.isSchemaXSD(namespaceURI)) || ((Constants.isSOAP_ENC(namespaceURI)) && (!"Array".equals(qName.getLocalPart()))))
/*      */     {
/* 1434 */       writeTypeNamespace(namespaceURI);
/*      */ 
/* 1436 */       return false;
/*      */     }
/*      */ 
/* 1439 */     if (types == null) {
/* 1440 */       types = new ArrayList();
/*      */ 
/* 1442 */       types.add(qName.getLocalPart());
/*      */ 
/* 1444 */       writeTypeNamespace(namespaceURI);
/* 1445 */       this.schemaTypes.put(namespaceURI, types);
/*      */ 
/* 1447 */       added = true;
/*      */     }
/* 1449 */     else if (!types.contains(qName.getLocalPart())) {
/* 1450 */       types.add(qName.getLocalPart());
/*      */ 
/* 1452 */       added = true;
/*      */     }
/*      */ 
/* 1458 */     if (added) {
/* 1459 */       String prefix = this.namespaces.getCreatePrefix(namespaceURI);
/*      */ 
/* 1466 */       return (!prefix.equals("soapenv")) && (!prefix.equals("soapenc")) && (!prefix.equals("xsd")) && (!prefix.equals("wsdl")) && (!prefix.equals("wsdlsoap"));
/*      */     }
/*      */ 
/* 1472 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean addToElementsList(QName qName)
/*      */   {
/* 1486 */     if (qName == null) {
/* 1487 */       return false;
/*      */     }
/*      */ 
/* 1490 */     boolean added = false;
/* 1491 */     ArrayList elements = (ArrayList)this.schemaElementNames.get(qName.getNamespaceURI());
/*      */ 
/* 1494 */     if (elements == null) {
/* 1495 */       elements = new ArrayList();
/*      */ 
/* 1497 */       elements.add(qName.getLocalPart());
/* 1498 */       this.schemaElementNames.put(qName.getNamespaceURI(), elements);
/*      */ 
/* 1500 */       added = true;
/*      */     }
/* 1502 */     else if (!elements.contains(qName.getLocalPart())) {
/* 1503 */       elements.add(qName.getLocalPart());
/*      */ 
/* 1505 */       added = true;
/*      */     }
/*      */ 
/* 1509 */     return added;
/*      */   }
/*      */ 
/*      */   public static boolean isNullable(Class type)
/*      */   {
/* 1520 */     return !type.isPrimitive();
/*      */   }
/*      */ 
/*      */   private void createDocumentFragment()
/*      */   {
/*      */     try
/*      */     {
/* 1550 */       this.docHolder = XMLUtils.newDocument();
/*      */     }
/*      */     catch (ParserConfigurationException e)
/*      */     {
/* 1554 */       throw new InternalException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void updateNamespaces()
/*      */   {
/* 1563 */     Namespaces namespaces = getNamespaces();
/* 1564 */     Iterator nspIterator = namespaces.getNamespaces();
/*      */ 
/* 1566 */     while (nspIterator.hasNext()) {
/* 1567 */       String nsp = (String)nspIterator.next();
/* 1568 */       String pref = this.def.getPrefix(nsp);
/*      */ 
/* 1570 */       if (pref == null)
/* 1571 */         this.def.addNamespace(namespaces.getCreatePrefix(nsp), nsp);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void insertTypesFragment(Document doc)
/*      */   {
/* 1584 */     updateNamespaces();
/*      */ 
/* 1586 */     if (this.wsdlTypesElem == null) {
/* 1587 */       return;
/*      */     }
/*      */ 
/* 1591 */     Element schemaElem = null;
/* 1592 */     String tns = null;
/* 1593 */     NodeList nl = this.wsdlTypesElem.getChildNodes();
/* 1594 */     for (int i = 0; i < nl.getLength(); i++) {
/* 1595 */       NamedNodeMap attrs = nl.item(i).getAttributes();
/* 1596 */       if (attrs != null) {
/* 1597 */         for (int n = 0; n < attrs.getLength(); n++) {
/* 1598 */           Attr a = (Attr)attrs.item(n);
/* 1599 */           if (a.getName().equals("targetNamespace")) {
/* 1600 */             tns = a.getValue();
/* 1601 */             schemaElem = (Element)nl.item(i);
/* 1602 */             break;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1608 */         if ((tns != null) && (!"".equals(tns.trim())))
/*      */         {
/* 1613 */           Iterator it = this.schemaTypes.keySet().iterator();
/*      */ 
/* 1616 */           while (it.hasNext())
/*      */           {
/*      */             String otherTns;
/* 1617 */             if (!tns.equals(otherTns = (String)it.next())) {
/* 1618 */               Element importElem = this.docHolder.createElement("import");
/* 1619 */               importElem.setAttribute("namespace", otherTns);
/* 1620 */               schemaElem.insertBefore(importElem, schemaElem.getFirstChild());
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/* 1625 */         schemaElem = null;
/* 1626 */         tns = null;
/*      */       }
/*      */     }
/*      */ 
/* 1630 */     Node node = doc.importNode(this.wsdlTypesElem, true);
/*      */ 
/* 1632 */     doc.getDocumentElement().insertBefore(node, doc.getDocumentElement().getFirstChild());
/*      */   }
/*      */ 
/*      */   public List getStopClasses()
/*      */   {
/* 1643 */     return this.stopClasses;
/*      */   }
/*      */ 
/*      */   public Element createElement(String elementName)
/*      */   {
/* 1653 */     return this.docHolder.createElement(elementName);
/*      */   }
/*      */ 
/*      */   protected boolean isBeanCompatible(Class javaType, boolean issueErrors)
/*      */   {
/* 1668 */     if ((javaType.isArray()) || (javaType.isPrimitive())) {
/* 1669 */       if ((issueErrors) && (!this.beanCompatErrs.contains(javaType))) {
/* 1670 */         log.warn(Messages.getMessage("beanCompatType00", javaType.getName()));
/*      */ 
/* 1672 */         this.beanCompatErrs.add(javaType);
/*      */       }
/*      */ 
/* 1675 */       return false;
/*      */     }
/*      */ 
/* 1680 */     if ((javaType.getName().startsWith("java.")) || (javaType.getName().startsWith("javax.")))
/*      */     {
/* 1682 */       if ((issueErrors) && (!this.beanCompatErrs.contains(javaType))) {
/* 1683 */         log.warn(Messages.getMessage("beanCompatPkg00", javaType.getName()));
/*      */ 
/* 1685 */         this.beanCompatErrs.add(javaType);
/*      */       }
/*      */ 
/* 1688 */       return false;
/*      */     }
/*      */ 
/* 1692 */     if (JavaUtils.isEnumClass(javaType)) {
/* 1693 */       return true;
/*      */     }
/*      */ 
/* 1698 */     if (!Throwable.class.isAssignableFrom(javaType)) {
/*      */       try {
/* 1700 */         javaType.getConstructor(new Class[0]);
/*      */       }
/*      */       catch (NoSuchMethodException e) {
/* 1703 */         if ((issueErrors) && (!this.beanCompatErrs.contains(javaType))) {
/* 1704 */           log.warn(Messages.getMessage("beanCompatConstructor00", javaType.getName()));
/*      */ 
/* 1706 */           this.beanCompatErrs.add(javaType);
/*      */         }
/*      */ 
/* 1709 */         return false;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1714 */     Class superClass = javaType.getSuperclass();
/*      */ 
/* 1716 */     if ((superClass != null) && (superClass != Object.class) && (superClass != Exception.class) && (superClass != Throwable.class) && (superClass != RemoteException.class) && (superClass != AxisFault.class) && ((this.stopClasses == null) || (!this.stopClasses.contains(superClass.getName()))))
/*      */     {
/* 1723 */       if (!isBeanCompatible(superClass, false)) {
/* 1724 */         if ((issueErrors) && (!this.beanCompatErrs.contains(javaType))) {
/* 1725 */           log.warn(Messages.getMessage("beanCompatExtends00", javaType.getName(), superClass.getName(), javaType.getName()));
/*      */ 
/* 1729 */           this.beanCompatErrs.add(javaType);
/*      */         }
/*      */ 
/* 1732 */         return false;
/*      */       }
/*      */     }
/*      */ 
/* 1736 */     return true;
/*      */   }
/*      */ 
/*      */   public Element createElementWithAnonymousType(String elementName, Class fieldType, boolean omittable, Document ownerDocument)
/*      */     throws AxisFault
/*      */   {
/* 1755 */     Element element = this.docHolder.createElement("element");
/*      */ 
/* 1757 */     element.setAttribute("name", elementName);
/*      */ 
/* 1759 */     if (isNullable(fieldType)) {
/* 1760 */       element.setAttribute("nillable", "true");
/*      */     }
/*      */ 
/* 1763 */     if (omittable) {
/* 1764 */       element.setAttribute("minOccurs", "0");
/* 1765 */       element.setAttribute("maxOccurs", "1");
/*      */     }
/*      */ 
/* 1768 */     makeTypeElement(fieldType, null, element);
/*      */ 
/* 1770 */     return element;
/*      */   }
/*      */ 
/*      */   private boolean makeTypeElement(Class type, QName qName, Element containingElement)
/*      */     throws AxisFault
/*      */   {
/* 1797 */     if ((qName == null) || (Constants.equals(Constants.SOAP_ARRAY, qName))) {
/* 1798 */       qName = getTypeQName(type);
/*      */     }
/*      */ 
/* 1801 */     boolean anonymous = isAnonymousType(qName);
/*      */ 
/* 1804 */     if ((anonymous) && (containingElement == null)) {
/* 1805 */       throw new AxisFault(Messages.getMessage("noContainerForAnonymousType", qName.toString()));
/*      */     }
/*      */ 
/* 1813 */     if ((!addToTypesList(qName)) && (!anonymous)) {
/* 1814 */       if (containingElement != null) {
/* 1815 */         containingElement.setAttribute("type", getQNameString(qName));
/*      */       }
/*      */ 
/* 1818 */       return true;
/*      */     }
/*      */ 
/* 1823 */     SerializerFactory factory = (SerializerFactory)this.tm.getSerializer(type, qName);
/*      */ 
/* 1827 */     if (factory == null) {
/* 1828 */       if (isEnumClass(type))
/* 1829 */         factory = new EnumSerializerFactory(type, qName);
/* 1830 */       else if (isBeanCompatible(type, true))
/* 1831 */         factory = new BeanSerializerFactory(type, qName);
/*      */       else {
/* 1833 */         return false;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1838 */     Serializer ser = (Serializer)factory.getSerializerAs("Axis SAX Mechanism");
/*      */ 
/* 1841 */     if (ser == null) {
/* 1842 */       throw new AxisFault(Messages.getMessage("NoSerializer00", type.getName()));
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1849 */       typeEl = ser.writeSchema(type, this);
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*      */       Element typeEl;
/* 1851 */       throw AxisFault.makeFault(e);
/*      */     }
/*      */     Element typeEl;
/* 1858 */     if (anonymous) {
/* 1859 */       if (typeEl == null)
/* 1860 */         containingElement.setAttribute("type", getQNameString(getTypeQName(type)));
/*      */       else
/* 1862 */         containingElement.appendChild(typeEl);
/*      */     }
/*      */     else {
/* 1865 */       if (typeEl != null) {
/* 1866 */         typeEl.setAttribute("name", qName.getLocalPart());
/*      */ 
/* 1869 */         writeSchemaTypeDecl(qName, typeEl);
/*      */       }
/*      */ 
/* 1872 */       if (containingElement != null) {
/* 1873 */         containingElement.setAttribute("type", getQNameString(qName));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1878 */     if (this.emitter != null) {
/* 1879 */       this.emitter.getQName2ClassMap().put(qName, type);
/*      */     }
/*      */ 
/* 1882 */     return true;
/*      */   }
/*      */ 
/*      */   public ServiceDesc getServiceDesc()
/*      */   {
/* 1890 */     return this.serviceDesc;
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.fromJava.Types
 * JD-Core Version:    0.6.0
 */