/*      */ package org.apache.axis.wsdl.toJava;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URL;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.Vector;
/*      */ import javax.wsdl.Binding;
/*      */ import javax.wsdl.BindingInput;
/*      */ import javax.wsdl.BindingOperation;
/*      */ import javax.wsdl.Input;
/*      */ import javax.wsdl.Message;
/*      */ import javax.wsdl.Operation;
/*      */ import javax.wsdl.Part;
/*      */ import javax.wsdl.extensions.ExtensibilityElement;
/*      */ import javax.wsdl.extensions.UnknownExtensibilityElement;
/*      */ import javax.wsdl.extensions.mime.MIMEMultipartRelated;
/*      */ import javax.wsdl.extensions.mime.MIMEPart;
/*      */ import javax.wsdl.extensions.soap.SOAPBody;
/*      */ import javax.wsdl.extensions.soap.SOAPOperation;
/*      */ import javax.xml.namespace.QName;
/*      */ import javax.xml.rpc.holders.BooleanHolder;
/*      */ import org.apache.axis.Constants;
/*      */ import org.apache.axis.components.logger.LogFactory;
/*      */ import org.apache.axis.constants.Style;
/*      */ import org.apache.axis.constants.Use;
/*      */ import org.apache.axis.utils.JavaUtils;
/*      */ import org.apache.axis.utils.Messages;
/*      */ import org.apache.axis.wsdl.symbolTable.BaseType;
/*      */ import org.apache.axis.wsdl.symbolTable.BindingEntry;
/*      */ import org.apache.axis.wsdl.symbolTable.CollectionTE;
/*      */ import org.apache.axis.wsdl.symbolTable.CollectionType;
/*      */ import org.apache.axis.wsdl.symbolTable.DefinedElement;
/*      */ import org.apache.axis.wsdl.symbolTable.MessageEntry;
/*      */ import org.apache.axis.wsdl.symbolTable.MimeInfo;
/*      */ import org.apache.axis.wsdl.symbolTable.Parameter;
/*      */ import org.apache.axis.wsdl.symbolTable.Parameters;
/*      */ import org.apache.axis.wsdl.symbolTable.SchemaUtils;
/*      */ import org.apache.axis.wsdl.symbolTable.SymbolTable;
/*      */ import org.apache.axis.wsdl.symbolTable.TypeEntry;
/*      */ import org.apache.commons.logging.Log;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.NodeList;
/*      */ 
/*      */ public class Utils extends org.apache.axis.wsdl.symbolTable.Utils
/*      */ {
/*   61 */   protected static Log log = LogFactory.getLog(Utils.class.getName());
/*      */ 
/*  647 */   private static HashMap TYPES = new HashMap(7);
/*      */   private static HashMap constructorMap;
/*      */   private static HashMap constructorThrowMap;
/*      */ 
/*      */   public static String holder(TypeEntry type, Emitter emitter)
/*      */   {
/*   67 */     Parameter arg = new Parameter();
/*      */ 
/*   69 */     arg.setType(type);
/*   70 */     return holder(arg, emitter);
/*      */   }
/*      */ 
/*      */   public static String holder(Parameter p, Emitter emitter)
/*      */   {
/*   82 */     String mimeType = p.getMIMEInfo() == null ? null : p.getMIMEInfo().getType();
/*      */ 
/*   85 */     String mimeDimensions = mimeType == null ? "" : p.getMIMEInfo().getDimensions();
/*      */ 
/*   90 */     if (mimeType != null) {
/*   91 */       if ((mimeType.equals("image/gif")) || (mimeType.equals("image/jpeg")))
/*   92 */         return "org.apache.axis.holders.ImageHolder" + mimeDimensions;
/*   93 */       if (mimeType.equals("text/plain"))
/*   94 */         return "javax.xml.rpc.holders.StringHolder" + mimeDimensions;
/*   95 */       if (mimeType.startsWith("multipart/")) {
/*   96 */         return "org.apache.axis.holders.MimeMultipartHolder" + mimeDimensions;
/*      */       }
/*   98 */       if ((mimeType.startsWith("application/octetstream")) || (mimeType.startsWith("application/octet-stream")))
/*      */       {
/*  100 */         return "org.apache.axis.holders.OctetStreamHolder" + mimeDimensions;
/*      */       }
/*  102 */       if ((mimeType.equals("text/xml")) || (mimeType.equals("application/xml")))
/*      */       {
/*  104 */         return "org.apache.axis.holders.SourceHolder" + mimeDimensions;
/*      */       }
/*  106 */       return "org.apache.axis.holders.DataHandlerHolder" + mimeDimensions;
/*      */     }
/*      */ 
/*  111 */     TypeEntry type = p.getType();
/*  112 */     String typeValue = type.getName();
/*      */ 
/*  116 */     if (((p.isOmittable()) && (p.getType().getDimensions().equals(""))) || (((p.getType() instanceof CollectionType)) && (((CollectionType)p.getType()).isWrapped())) || (p.getType().getUnderlTypeNillable()))
/*      */     {
/*  120 */       typeValue = getWrapperType(type);
/*      */     }
/*      */ 
/*  125 */     if ((typeValue.equals("byte[]")) && (type.isBaseType()))
/*      */     {
/*  127 */       return "javax.xml.rpc.holders.ByteArrayHolder";
/*      */     }
/*      */ 
/*  131 */     if (typeValue.endsWith("[]")) {
/*  132 */       String name = emitter.getJavaName(type.getQName());
/*  133 */       String packagePrefix = "";
/*      */ 
/*  137 */       if (((type instanceof CollectionType)) && ((type.getRefType() instanceof BaseType)))
/*      */       {
/*  139 */         String uri = type.getRefType().getQName().getNamespaceURI();
/*      */ 
/*  143 */         if (TYPES.get(JavaUtils.replace(name, "[]", "")) != null) {
/*  144 */           name = capitalizeFirstChar(name);
/*      */         }
/*      */ 
/*  148 */         if ((((CollectionType)type).isWrapped()) && (!typeValue.equals(type.getName()))) {
/*  149 */           name = name + "Wrapper";
/*      */         }
/*      */ 
/*  152 */         packagePrefix = emitter.getNamespaces().getCreate(uri, false);
/*      */ 
/*  154 */         if (packagePrefix == null)
/*  155 */           packagePrefix = "";
/*      */         else {
/*  157 */           packagePrefix = packagePrefix + '.';
/*      */         }
/*      */       }
/*  160 */       name = JavaUtils.replace(name, "java.lang.", "");
/*      */ 
/*  164 */       name = JavaUtils.replace(name, "[]", "Array");
/*  165 */       name = addPackageName(name, "holders");
/*      */ 
/*  167 */       return packagePrefix + name + "Holder";
/*      */     }
/*      */ 
/*  171 */     if (typeValue.equals("String"))
/*  172 */       return "javax.xml.rpc.holders.StringHolder";
/*  173 */     if (typeValue.equals("java.lang.String")) {
/*  174 */       return "javax.xml.rpc.holders.StringHolder";
/*      */     }
/*      */ 
/*  178 */     if (typeValue.equals("Object"))
/*  179 */       return "javax.xml.rpc.holders.ObjectHolder";
/*  180 */     if (typeValue.equals("java.lang.Object")) {
/*  181 */       return "javax.xml.rpc.holders.ObjectHolder";
/*      */     }
/*      */ 
/*  185 */     if ((typeValue.equals("int")) || (typeValue.equals("long")) || (typeValue.equals("short")) || (typeValue.equals("float")) || (typeValue.equals("double")) || (typeValue.equals("boolean")) || (typeValue.equals("byte")))
/*      */     {
/*  189 */       return "javax.xml.rpc.holders." + capitalizeFirstChar(typeValue) + "Holder";
/*      */     }
/*      */ 
/*  194 */     if (typeValue.startsWith("java.lang.")) {
/*  195 */       return "javax.xml.rpc.holders" + typeValue.substring(typeValue.lastIndexOf(".")) + "WrapperHolder";
/*      */     }
/*      */ 
/*  198 */     if (typeValue.indexOf(".") < 0) {
/*  199 */       return "javax.xml.rpc.holders" + typeValue + "WrapperHolder";
/*      */     }
/*      */ 
/*  204 */     if (typeValue.equals("java.math.BigDecimal"))
/*  205 */       return "javax.xml.rpc.holders.BigDecimalHolder";
/*  206 */     if (typeValue.equals("java.math.BigInteger"))
/*  207 */       return "javax.xml.rpc.holders.BigIntegerHolder";
/*  208 */     if (typeValue.equals("java.util.Date"))
/*  209 */       return "org.apache.axis.holders.DateHolder";
/*  210 */     if (typeValue.equals("java.util.Calendar"))
/*  211 */       return "javax.xml.rpc.holders.CalendarHolder";
/*  212 */     if (typeValue.equals("javax.xml.namespace.QName"))
/*  213 */       return "javax.xml.rpc.holders.QNameHolder";
/*  214 */     if (typeValue.equals("javax.activation.DataHandler")) {
/*  215 */       return "org.apache.axis.holders.DataHandlerHolder";
/*      */     }
/*      */ 
/*  219 */     if (typeValue.startsWith("org.apache.axis.types.")) {
/*  220 */       int i = typeValue.lastIndexOf('.');
/*  221 */       String t = typeValue.substring(i + 1);
/*      */ 
/*  223 */       return "org.apache.axis.holders." + t + "Holder";
/*      */     }
/*      */ 
/*  229 */     return addPackageName(typeValue, "holders") + "Holder";
/*      */   }
/*      */ 
/*      */   public static String addPackageName(String className, String newPkg)
/*      */   {
/*  242 */     int index = className.lastIndexOf(".");
/*      */ 
/*  244 */     if (index >= 0) {
/*  245 */       return className.substring(0, index) + "." + newPkg + className.substring(index);
/*      */     }
/*      */ 
/*  248 */     return newPkg + "." + className;
/*      */   }
/*      */ 
/*      */   public static String getFullExceptionName(Message faultMessage, SymbolTable symbolTable)
/*      */   {
/*  263 */     MessageEntry me = symbolTable.getMessageEntry(faultMessage.getQName());
/*      */ 
/*  265 */     return (String)me.getDynamicVar(JavaGeneratorFactory.EXCEPTION_CLASS_NAME);
/*      */   }
/*      */ 
/*      */   public static QName getFaultDataType(Message faultMessage, SymbolTable symbolTable)
/*      */   {
/*  279 */     MessageEntry me = symbolTable.getMessageEntry(faultMessage.getQName());
/*      */ 
/*  281 */     return (QName)me.getDynamicVar(JavaGeneratorFactory.EXCEPTION_DATA_TYPE);
/*      */   }
/*      */ 
/*      */   public static boolean isFaultComplex(Message faultMessage, SymbolTable symbolTable)
/*      */   {
/*  295 */     MessageEntry me = symbolTable.getMessageEntry(faultMessage.getQName());
/*  296 */     Boolean ret = (Boolean)me.getDynamicVar(JavaGeneratorFactory.COMPLEX_TYPE_FAULT);
/*      */ 
/*  299 */     if (ret != null) {
/*  300 */       return ret.booleanValue();
/*      */     }
/*  302 */     return false;
/*      */   }
/*      */ 
/*      */   public static Vector getEnumerationBaseAndValues(Node node, SymbolTable symbolTable)
/*      */   {
/*  320 */     if (node == null) {
/*  321 */       return null;
/*      */     }
/*      */ 
/*  325 */     QName nodeKind = getNodeQName(node);
/*      */     NodeList children;
/*      */     Node simpleNode;
/*      */     int j;
/*  327 */     if ((nodeKind != null) && (nodeKind.getLocalPart().equals("element")) && (Constants.isSchemaXSD(nodeKind.getNamespaceURI())))
/*      */     {
/*  329 */       children = node.getChildNodes();
/*  330 */       simpleNode = null;
/*      */ 
/*  332 */       for (j = 0; (j < children.getLength()) && (simpleNode == null); )
/*      */       {
/*  334 */         QName simpleKind = getNodeQName(children.item(j));
/*      */ 
/*  336 */         if ((simpleKind != null) && (simpleKind.getLocalPart().equals("simpleType")) && (Constants.isSchemaXSD(simpleKind.getNamespaceURI())))
/*      */         {
/*  340 */           simpleNode = children.item(j);
/*  341 */           node = simpleNode;
/*      */         }
/*  333 */         j++;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  347 */     nodeKind = getNodeQName(node);
/*      */ 
/*  349 */     if ((nodeKind != null) && (nodeKind.getLocalPart().equals("simpleType")) && (Constants.isSchemaXSD(nodeKind.getNamespaceURI())))
/*      */     {
/*  354 */       NodeList children = node.getChildNodes();
/*  355 */       Node restrictionNode = null;
/*      */ 
/*  357 */       int j = 0;
/*  358 */       while ((j < children.getLength()) && (restrictionNode == null))
/*      */       {
/*  360 */         QName restrictionKind = getNodeQName(children.item(j));
/*      */ 
/*  362 */         if ((restrictionKind != null) && (restrictionKind.getLocalPart().equals("restriction")) && (Constants.isSchemaXSD(restrictionKind.getNamespaceURI())))
/*      */         {
/*  366 */           restrictionNode = children.item(j);
/*      */         }
/*  359 */         j++;
/*      */       }
/*      */ 
/*  373 */       TypeEntry baseEType = null;
/*      */ 
/*  375 */       if (restrictionNode != null) {
/*  376 */         QName baseType = getTypeQName(restrictionNode, new BooleanHolder(), false);
/*      */ 
/*  379 */         baseEType = symbolTable.getType(baseType);
/*      */ 
/*  381 */         if (baseEType != null) {
/*  382 */           String javaName = baseEType.getName();
/*      */ 
/*  384 */           if ((javaName.equals("boolean")) || (!SchemaUtils.isSimpleSchemaType(baseEType.getQName())))
/*      */           {
/*  387 */             baseEType = null;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  393 */       if ((baseEType != null) && (restrictionNode != null)) {
/*  394 */         Vector v = new Vector();
/*  395 */         NodeList enums = restrictionNode.getChildNodes();
/*      */ 
/*  397 */         for (int i = 0; i < enums.getLength(); i++) {
/*  398 */           QName enumKind = getNodeQName(enums.item(i));
/*      */ 
/*  400 */           if ((enumKind == null) || (!enumKind.getLocalPart().equals("enumeration")) || (!Constants.isSchemaXSD(enumKind.getNamespaceURI())))
/*      */           {
/*      */             continue;
/*      */           }
/*      */ 
/*  406 */           Node enumNode = enums.item(i);
/*  407 */           String value = getAttribute(enumNode, "value");
/*      */ 
/*  409 */           if (value != null) {
/*  410 */             v.add(value);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  416 */         if (v.isEmpty()) {
/*  417 */           return null;
/*      */         }
/*      */ 
/*  421 */         v.add(0, baseEType);
/*      */ 
/*  423 */         return v;
/*      */       }
/*      */     }
/*      */ 
/*  427 */     return null;
/*      */   }
/*      */ 
/*      */   public static String capitalizeFirstChar(String name)
/*      */   {
/*  438 */     if ((name == null) || (name.equals(""))) {
/*  439 */       return name;
/*      */     }
/*      */ 
/*  442 */     char start = name.charAt(0);
/*      */ 
/*  444 */     if (Character.isLowerCase(start)) {
/*  445 */       start = Character.toUpperCase(start);
/*      */ 
/*  447 */       return start + name.substring(1);
/*      */     }
/*      */ 
/*  450 */     return name;
/*      */   }
/*      */ 
/*      */   public static String addUnderscore(String name)
/*      */   {
/*  461 */     if ((name == null) || (name.equals(""))) {
/*  462 */       return name;
/*      */     }
/*      */ 
/*  465 */     return "_" + name;
/*      */   }
/*      */ 
/*      */   public static String xmlNameToJava(String name)
/*      */   {
/*  480 */     return JavaUtils.xmlNameToJava(name);
/*      */   }
/*      */ 
/*      */   public static String xmlNameToJavaClass(String name)
/*      */   {
/*  490 */     return capitalizeFirstChar(xmlNameToJava(name));
/*      */   }
/*      */ 
/*      */   public static String makePackageName(String namespace)
/*      */   {
/*  501 */     String hostname = null;
/*  502 */     String path = "";
/*      */     try
/*      */     {
/*  506 */       URL u = new URL(namespace);
/*      */ 
/*  508 */       hostname = u.getHost();
/*  509 */       path = u.getPath();
/*      */     } catch (MalformedURLException e) {
/*  511 */       if (namespace.indexOf(":") > -1) {
/*  512 */         hostname = namespace.substring(namespace.indexOf(":") + 1);
/*      */ 
/*  514 */         if (hostname.indexOf("/") > -1)
/*  515 */           hostname = hostname.substring(0, hostname.indexOf("/"));
/*      */       }
/*      */       else {
/*  518 */         hostname = namespace;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  523 */     if (hostname == null) {
/*  524 */       return null;
/*      */     }
/*      */ 
/*  528 */     hostname = hostname.replace('-', '_');
/*  529 */     path = path.replace('-', '_');
/*      */ 
/*  532 */     if ((path.length() > 0) && (path.charAt(path.length() - 1) == '/')) {
/*  533 */       path = path.substring(0, path.length() - 1);
/*      */     }
/*      */ 
/*  537 */     StringTokenizer st = new StringTokenizer(hostname, ".:");
/*  538 */     String[] words = new String[st.countTokens()];
/*      */ 
/*  540 */     for (int i = 0; i < words.length; i++) {
/*  541 */       words[i] = st.nextToken();
/*      */     }
/*      */ 
/*  544 */     StringBuffer sb = new StringBuffer(namespace.length());
/*      */ 
/*  546 */     for (int i = words.length - 1; i >= 0; i--) {
/*  547 */       addWordToPackageBuffer(sb, words[i], i == words.length - 1);
/*      */     }
/*      */ 
/*  551 */     StringTokenizer st2 = new StringTokenizer(path, "/");
/*      */ 
/*  553 */     while (st2.hasMoreTokens()) {
/*  554 */       addWordToPackageBuffer(sb, st2.nextToken(), false);
/*      */     }
/*      */ 
/*  557 */     return sb.toString();
/*      */   }
/*      */ 
/*      */   private static void addWordToPackageBuffer(StringBuffer sb, String word, boolean firstWord)
/*      */   {
/*  572 */     if (JavaUtils.isJavaKeyword(word)) {
/*  573 */       word = JavaUtils.makeNonJavaKeyword(word);
/*      */     }
/*      */ 
/*  577 */     if (!firstWord) {
/*  578 */       sb.append('.');
/*      */     }
/*      */ 
/*  582 */     if (Character.isDigit(word.charAt(0))) {
/*  583 */       sb.append('_');
/*      */     }
/*      */ 
/*  587 */     if (word.indexOf('.') != -1) {
/*  588 */       char[] buf = word.toCharArray();
/*      */ 
/*  590 */       for (int i = 0; i < word.length(); i++) {
/*  591 */         if (buf[i] == '.') {
/*  592 */           buf[i] = '_';
/*      */         }
/*      */       }
/*      */ 
/*  596 */       word = new String(buf);
/*      */     }
/*      */ 
/*  599 */     sb.append(word);
/*      */   }
/*      */ 
/*      */   public static String getJavaLocalName(String fullName)
/*      */   {
/*  609 */     return fullName.substring(fullName.lastIndexOf('.') + 1);
/*      */   }
/*      */ 
/*      */   public static String getJavaPackageName(String fullName)
/*      */   {
/*  620 */     if (fullName.lastIndexOf('.') > 0) {
/*  621 */       return fullName.substring(0, fullName.lastIndexOf('.'));
/*      */     }
/*  623 */     return "";
/*      */   }
/*      */ 
/*      */   public static boolean fileExists(String name, String namespace, Namespaces namespaces)
/*      */     throws IOException
/*      */   {
/*  640 */     String packageName = namespaces.getAsDir(namespace);
/*  641 */     String fullName = packageName + name;
/*      */ 
/*  643 */     return new File(fullName).exists();
/*      */   }
/*      */ 
/*      */   public static String wrapPrimitiveType(TypeEntry type, String var)
/*      */   {
/*  668 */     String objType = type == null ? null : (String)TYPES.get(type.getName());
/*      */ 
/*  672 */     if (objType != null)
/*  673 */       return "new " + objType + "(" + var + ")";
/*  674 */     if ((type != null) && (type.getName().equals("byte[]")) && (type.getQName().getLocalPart().equals("hexBinary")))
/*      */     {
/*  678 */       return "new org.apache.axis.types.HexBinary(" + var + ")";
/*      */     }
/*  680 */     return var;
/*      */   }
/*      */ 
/*      */   public static String getResponseString(Parameter param, String var)
/*      */   {
/*  693 */     if (param.getType() == null) {
/*  694 */       return ";";
/*      */     }
/*  696 */     String typeName = param.getType().getName();
/*  697 */     MimeInfo mimeInfo = param.getMIMEInfo();
/*      */ 
/*  699 */     String mimeType = mimeInfo == null ? null : mimeInfo.getType();
/*      */ 
/*  702 */     String mimeDimensions = mimeInfo == null ? "" : mimeInfo.getDimensions();
/*      */ 
/*  706 */     if (mimeType != null) {
/*  707 */       if ((mimeType.equals("image/gif")) || (mimeType.equals("image/jpeg")))
/*  708 */         return "(java.awt.Image" + mimeDimensions + ") " + var + ";";
/*  709 */       if (mimeType.equals("text/plain"))
/*  710 */         return "(java.lang.String" + mimeDimensions + ") " + var + ";";
/*  711 */       if ((mimeType.equals("text/xml")) || (mimeType.equals("application/xml")))
/*      */       {
/*  713 */         return "(javax.xml.transform.Source" + mimeDimensions + ") " + var + ";";
/*      */       }
/*  715 */       if (mimeType.startsWith("multipart/")) {
/*  716 */         return "(javax.mail.internet.MimeMultipart" + mimeDimensions + ") " + var + ";";
/*      */       }
/*  718 */       if ((mimeType.startsWith("application/octetstream")) || (mimeType.startsWith("application/octet-stream")))
/*      */       {
/*  722 */         return "(org.apache.axis.attachments.OctetStream" + mimeDimensions + ") " + var + ";";
/*      */       }
/*      */ 
/*  725 */       return "(javax.activation.DataHandler" + mimeDimensions + ") " + var + ";";
/*      */     }
/*      */ 
/*  732 */     if (((param.isOmittable()) && (param.getType().getDimensions().equals(""))) || (((param.getType() instanceof CollectionType)) && (((CollectionType)param.getType()).isWrapped())) || (param.getType().getUnderlTypeNillable()))
/*      */     {
/*  737 */       typeName = getWrapperType(param.getType());
/*      */     }
/*      */ 
/*  740 */     String objType = (String)TYPES.get(typeName);
/*      */ 
/*  742 */     if (objType != null) {
/*  743 */       return "((" + objType + ") " + var + ")." + typeName + "Value();";
/*      */     }
/*      */ 
/*  746 */     return "(" + typeName + ") " + var + ";";
/*      */   }
/*      */ 
/*      */   public static boolean isPrimitiveType(TypeEntry type)
/*      */   {
/*  756 */     return TYPES.get(type.getName()) != null;
/*      */   }
/*      */ 
/*      */   public static String getWrapperType(String type)
/*      */   {
/*  769 */     String ret = (String)TYPES.get(type);
/*  770 */     return ret == null ? type : ret;
/*      */   }
/*      */ 
/*      */   public static String getWrapperType(TypeEntry type)
/*      */   {
/*  781 */     String dims = type.getDimensions();
/*  782 */     if (!dims.equals(""))
/*      */     {
/*  784 */       TypeEntry te = type.getRefType();
/*  785 */       if ((te != null) && (!te.getDimensions().equals("")))
/*      */       {
/*  788 */         return getWrapperType(te) + dims;
/*      */       }
/*  790 */       if (((te instanceof BaseType)) || (((te instanceof DefinedElement)) && ((te.getRefType() instanceof BaseType))))
/*      */       {
/*  794 */         return getWrapperType(te) + dims;
/*      */       }
/*      */     }
/*  797 */     return getWrapperType(type.getName());
/*      */   }
/*      */ 
/*      */   public static QName getOperationQName(BindingOperation bindingOper, BindingEntry bEntry, SymbolTable symbolTable)
/*      */   {
/*  813 */     Operation operation = bindingOper.getOperation();
/*  814 */     String operationName = operation.getName();
/*      */ 
/*  821 */     if ((bEntry.getBindingStyle() == Style.DOCUMENT) && (symbolTable.isWrapped()))
/*      */     {
/*  823 */       Input input = operation.getInput();
/*      */ 
/*  825 */       if (input != null) {
/*  826 */         Map parts = input.getMessage().getParts();
/*      */ 
/*  828 */         if ((parts != null) && (!parts.isEmpty())) {
/*  829 */           Iterator i = parts.values().iterator();
/*  830 */           Part p = (Part)i.next();
/*      */ 
/*  832 */           return p.getElementName();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  837 */     String ns = null;
/*      */ 
/*  842 */     BindingInput bindInput = bindingOper.getBindingInput();
/*      */ 
/*  844 */     if (bindInput != null) {
/*  845 */       Iterator it = bindInput.getExtensibilityElements().iterator();
/*      */ 
/*  847 */       while (it.hasNext()) {
/*  848 */         ExtensibilityElement elem = (ExtensibilityElement)it.next();
/*      */ 
/*  850 */         if ((elem instanceof SOAPBody)) {
/*  851 */           SOAPBody body = (SOAPBody)elem;
/*      */ 
/*  853 */           ns = body.getNamespaceURI();
/*  854 */           if ((bEntry.getInputBodyType(operation) != Use.ENCODED) || ((ns != null) && (ns.length() != 0))) break;
/*  855 */           log.warn(Messages.getMessage("badNamespaceForOperation00", bEntry.getName(), operation.getName())); break;
/*      */         }
/*      */ 
/*  861 */         if ((elem instanceof MIMEMultipartRelated)) {
/*  862 */           Object part = null;
/*  863 */           MIMEMultipartRelated mpr = (MIMEMultipartRelated)elem;
/*      */ 
/*  865 */           List l = mpr.getMIMEParts();
/*      */ 
/*  868 */           int j = 0;
/*  869 */           while ((l != null) && (j < l.size()) && (part == null))
/*      */           {
/*  871 */             MIMEPart mp = (MIMEPart)l.get(j);
/*      */ 
/*  873 */             List ll = mp.getExtensibilityElements();
/*      */ 
/*  876 */             int k = 0;
/*  877 */             for (; (ll != null) && (k < ll.size()) && (part == null); k++) {
/*  878 */               part = ll.get(k);
/*      */ 
/*  880 */               if ((part instanceof SOAPBody)) {
/*  881 */                 SOAPBody body = (SOAPBody)part;
/*      */ 
/*  883 */                 ns = body.getNamespaceURI();
/*  884 */                 if ((bEntry.getInputBodyType(operation) != Use.ENCODED) || ((ns != null) && (ns.length() != 0))) break;
/*  885 */                 log.warn(Messages.getMessage("badNamespaceForOperation00", bEntry.getName(), operation.getName())); break;
/*      */               }
/*      */ 
/*  892 */               part = null;
/*      */             }
/*  870 */             j++;
/*      */           }
/*      */ 
/*      */         }
/*  896 */         else if ((elem instanceof UnknownExtensibilityElement))
/*      */         {
/*  899 */           UnknownExtensibilityElement unkElement = (UnknownExtensibilityElement)elem;
/*      */ 
/*  901 */           QName name = unkElement.getElementType();
/*      */ 
/*  904 */           if ((name.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/")) && (name.getLocalPart().equals("body")))
/*      */           {
/*  906 */             ns = unkElement.getElement().getAttribute("namespace");
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  916 */     if (ns == null) {
/*  917 */       ns = "";
/*      */     }
/*      */ 
/*  920 */     return new QName(ns, operationName);
/*      */   }
/*      */ 
/*      */   public static String getOperationSOAPAction(BindingOperation bindingOper)
/*      */   {
/*  931 */     List elems = bindingOper.getExtensibilityElements();
/*  932 */     Iterator it = elems.iterator();
/*  933 */     boolean found = false;
/*  934 */     String action = null;
/*      */ 
/*  936 */     while ((!found) && (it.hasNext())) {
/*  937 */       ExtensibilityElement elem = (ExtensibilityElement)it.next();
/*      */ 
/*  940 */       if ((elem instanceof SOAPOperation)) {
/*  941 */         SOAPOperation soapOp = (SOAPOperation)elem;
/*  942 */         action = soapOp.getSoapActionURI();
/*  943 */         found = true;
/*  944 */       } else if ((elem instanceof UnknownExtensibilityElement))
/*      */       {
/*  947 */         UnknownExtensibilityElement unkElement = (UnknownExtensibilityElement)elem;
/*      */ 
/*  949 */         QName name = unkElement.getElementType();
/*      */ 
/*  952 */         if ((name.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/")) && (name.getLocalPart().equals("operation")))
/*      */         {
/*  955 */           action = unkElement.getElement().getAttribute("soapAction");
/*      */ 
/*  957 */           found = true;
/*      */         }
/*      */       }
/*      */     }
/*  961 */     return action;
/*      */   }
/*      */ 
/*      */   public static String getNewQName(QName qname)
/*      */   {
/*  972 */     return "new javax.xml.namespace.QName(\"" + qname.getNamespaceURI() + "\", \"" + qname.getLocalPart() + "\")";
/*      */   }
/*      */ 
/*      */   public static String getNewQNameWithLastLocalPart(QName qname)
/*      */   {
/*  977 */     return "new javax.xml.namespace.QName(\"" + qname.getNamespaceURI() + "\", \"" + getLastLocalPart(qname.getLocalPart()) + "\")";
/*      */   }
/*      */ 
/*      */   public static String getParameterTypeName(Parameter parm)
/*      */   {
/*      */     String ret;
/*  993 */     if (parm.getMIMEInfo() == null) {
/*  994 */       String ret = parm.getType().getName();
/*      */ 
/*  998 */       if (((parm.isOmittable()) && (parm.getType().getDimensions().equals(""))) || (((parm.getType() instanceof CollectionType)) && (((CollectionType)parm.getType()).isWrapped())) || (parm.getType().getUnderlTypeNillable()))
/*      */       {
/* 1003 */         ret = getWrapperType(parm.getType());
/*      */       }
/*      */     } else {
/* 1006 */       String mime = parm.getMIMEInfo().getType();
/*      */ 
/* 1008 */       ret = JavaUtils.mimeToJava(mime);
/*      */ 
/* 1010 */       if (ret == null)
/* 1011 */         ret = parm.getType().getName();
/*      */       else {
/* 1013 */         ret = ret + parm.getMIMEInfo().getDimensions();
/*      */       }
/*      */     }
/*      */ 
/* 1017 */     return ret;
/*      */   }
/*      */ 
/*      */   public static QName getXSIType(Parameter param)
/*      */   {
/* 1029 */     if (param.getMIMEInfo() != null) {
/* 1030 */       return getMIMETypeQName(param.getMIMEInfo().getType());
/*      */     }
/*      */ 
/* 1033 */     return getXSIType(param.getType());
/*      */   }
/*      */ 
/*      */   public static QName getXSIType(TypeEntry te)
/*      */   {
/* 1045 */     QName xmlType = null;
/*      */ 
/* 1049 */     if ((te != null) && ((te instanceof org.apache.axis.wsdl.symbolTable.Element)) && (te.getRefType() != null))
/*      */     {
/* 1051 */       te = te.getRefType();
/*      */     }
/*      */ 
/* 1063 */     if ((te != null) && ((te instanceof CollectionTE)) && (te.getRefType() != null))
/*      */     {
/* 1065 */       te = te.getRefType();
/*      */     }
/*      */ 
/* 1068 */     if (te != null) {
/* 1069 */       xmlType = te.getQName();
/*      */     }
/*      */ 
/* 1072 */     return xmlType;
/*      */   }
/*      */ 
/*      */   public static QName getMIMETypeQName(String mimeName)
/*      */   {
/* 1083 */     if ("text/plain".equals(mimeName))
/* 1084 */       return Constants.MIME_PLAINTEXT;
/* 1085 */     if (("image/gif".equals(mimeName)) || ("image/jpeg".equals(mimeName)))
/*      */     {
/* 1087 */       return Constants.MIME_IMAGE;
/* 1088 */     }if (("text/xml".equals(mimeName)) || ("applications/xml".equals(mimeName)))
/*      */     {
/* 1090 */       return Constants.MIME_SOURCE;
/* 1091 */     }if (("application/octet-stream".equals(mimeName)) || ("application/octetstream".equals(mimeName)))
/*      */     {
/* 1093 */       return Constants.MIME_OCTETSTREAM;
/* 1094 */     }if ((mimeName != null) && (mimeName.startsWith("multipart/"))) {
/* 1095 */       return Constants.MIME_MULTIPART;
/*      */     }
/* 1097 */     return Constants.MIME_DATA_HANDLER;
/*      */   }
/*      */ 
/*      */   public static boolean hasMIME(BindingEntry bEntry)
/*      */   {
/* 1109 */     List operations = bEntry.getBinding().getBindingOperations();
/*      */ 
/* 1111 */     for (int i = 0; i < operations.size(); i++) {
/* 1112 */       BindingOperation operation = (BindingOperation)operations.get(i);
/*      */ 
/* 1114 */       if (hasMIME(bEntry, operation)) {
/* 1115 */         return true;
/*      */       }
/*      */     }
/*      */ 
/* 1119 */     return false;
/*      */   }
/*      */ 
/*      */   public static boolean hasMIME(BindingEntry bEntry, BindingOperation operation)
/*      */   {
/* 1132 */     Parameters parameters = bEntry.getParameters(operation.getOperation());
/*      */ 
/* 1134 */     if (parameters != null) {
/* 1135 */       for (int idx = 0; idx < parameters.list.size(); idx++) {
/* 1136 */         Parameter p = (Parameter)parameters.list.get(idx);
/*      */ 
/* 1138 */         if (p.getMIMEInfo() != null) {
/* 1139 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1144 */     return false;
/*      */   }
/*      */ 
/*      */   public static String getConstructorForParam(Parameter param, SymbolTable symbolTable, BooleanHolder bThrow)
/*      */   {
/* 1244 */     String paramType = param.getType().getName();
/*      */ 
/* 1248 */     if (((param.isOmittable()) && (param.getType().getDimensions().equals(""))) || (((param.getType() instanceof CollectionType)) && (((CollectionType)param.getType()).isWrapped())) || (param.getType().getUnderlTypeNillable()))
/*      */     {
/* 1253 */       paramType = getWrapperType(param.getType());
/*      */     }
/*      */ 
/* 1256 */     String mimeType = param.getMIMEInfo() == null ? null : param.getMIMEInfo().getType();
/*      */ 
/* 1259 */     String mimeDimensions = param.getMIMEInfo() == null ? "" : param.getMIMEInfo().getDimensions();
/*      */ 
/* 1262 */     String out = null;
/*      */ 
/* 1265 */     if (mimeType != null) {
/* 1266 */       if ((mimeType.equals("image/gif")) || (mimeType.equals("image/jpeg")))
/* 1267 */         return "null";
/* 1268 */       if ((mimeType.equals("text/xml")) || (mimeType.equals("application/xml")))
/*      */       {
/* 1270 */         if (mimeDimensions.length() <= 0) {
/* 1271 */           return "new javax.xml.transform.stream.StreamSource()";
/*      */         }
/* 1273 */         return "new javax.xml.transform.stream.StreamSource[0]";
/*      */       }
/* 1275 */       if ((mimeType.equals("application/octet-stream")) || (mimeType.equals("application/octetstream")))
/*      */       {
/* 1277 */         if (mimeDimensions.length() <= 0) {
/* 1278 */           return "new org.apache.axis.attachments.OctetStream()";
/*      */         }
/* 1280 */         return "new org.apache.axis.attachments.OctetStream[0]";
/*      */       }
/*      */ 
/* 1283 */       return "new " + getParameterTypeName(param) + "()";
/*      */     }
/*      */ 
/* 1288 */     out = (String)constructorMap.get(paramType);
/*      */ 
/* 1290 */     if (out != null) {
/* 1291 */       return out;
/*      */     }
/*      */ 
/* 1295 */     out = (String)constructorThrowMap.get(paramType);
/*      */ 
/* 1297 */     if (out != null) {
/* 1298 */       bThrow.value = true;
/*      */ 
/* 1300 */       return out;
/*      */     }
/*      */ 
/* 1304 */     if (paramType.endsWith("[]")) {
/* 1305 */       return "new " + JavaUtils.replace(paramType, "[]", "[0]");
/*      */     }
/*      */ 
/* 1311 */     Vector v = getEnumerationBaseAndValues(param.getType().getNode(), symbolTable);
/*      */ 
/* 1314 */     if (v != null)
/*      */     {
/* 1317 */       String enumeration = (String)JavaEnumTypeWriter.getEnumValueIds(v).get(0);
/*      */ 
/* 1320 */       return paramType + "." + enumeration;
/*      */     }
/*      */ 
/* 1323 */     if (param.getType().getRefType() != null)
/*      */     {
/* 1325 */       Vector v2 = getEnumerationBaseAndValues(param.getType().getRefType().getNode(), symbolTable);
/*      */ 
/* 1328 */       if (v2 != null)
/*      */       {
/* 1331 */         String enumeration = (String)JavaEnumTypeWriter.getEnumValueIds(v2).get(0);
/*      */ 
/* 1334 */         return paramType + "." + enumeration;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1339 */     return "new " + paramType + "()";
/*      */   }
/*      */ 
/*      */   public static boolean shouldEmit(TypeEntry type)
/*      */   {
/* 1351 */     return ((type.getBaseType() == null) || (type.getRefType() != null)) && (!(type instanceof CollectionTE)) && (!(type instanceof org.apache.axis.wsdl.symbolTable.Element)) && (type.isReferenced()) && (!type.isOnlyLiteralReferenced()) && ((type.getNode() == null) || ((!isXsNode(type.getNode(), "group")) && (!isXsNode(type.getNode(), "attributeGroup"))));
/*      */   }
/*      */ 
/*      */   public static boolean isXsNode(Node node, String nameName)
/*      */   {
/* 1366 */     return (node.getLocalName().equals(nameName)) && (Constants.isSchemaXSD(node.getNamespaceURI()));
/*      */   }
/*      */ 
/*      */   public static QName getItemQName(TypeEntry te)
/*      */   {
/* 1372 */     if ((te instanceof DefinedElement)) {
/* 1373 */       te = te.getRefType();
/*      */     }
/* 1375 */     return te.getItemQName();
/*      */   }
/*      */ 
/*      */   public static QName getItemType(TypeEntry te) {
/* 1379 */     if ((te instanceof DefinedElement)) {
/* 1380 */       te = te.getRefType();
/*      */     }
/* 1382 */     return te.getComponentType();
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  650 */     TYPES.put("int", "java.lang.Integer");
/*  651 */     TYPES.put("float", "java.lang.Float");
/*  652 */     TYPES.put("boolean", "java.lang.Boolean");
/*  653 */     TYPES.put("double", "java.lang.Double");
/*  654 */     TYPES.put("byte", "java.lang.Byte");
/*  655 */     TYPES.put("short", "java.lang.Short");
/*  656 */     TYPES.put("long", "java.lang.Long");
/*      */ 
/* 1148 */     constructorMap = new HashMap(50);
/*      */ 
/* 1151 */     constructorThrowMap = new HashMap(50);
/*      */ 
/* 1159 */     constructorMap.put("int", "0");
/* 1160 */     constructorMap.put("float", "0");
/* 1161 */     constructorMap.put("boolean", "true");
/* 1162 */     constructorMap.put("double", "0");
/* 1163 */     constructorMap.put("byte", "(byte)0");
/* 1164 */     constructorMap.put("short", "(short)0");
/* 1165 */     constructorMap.put("long", "0");
/* 1166 */     constructorMap.put("java.lang.Boolean", "new java.lang.Boolean(false)");
/* 1167 */     constructorMap.put("java.lang.Byte", "new java.lang.Byte((byte)0)");
/* 1168 */     constructorMap.put("java.lang.Double", "new java.lang.Double(0)");
/* 1169 */     constructorMap.put("java.lang.Float", "new java.lang.Float(0)");
/* 1170 */     constructorMap.put("java.lang.Integer", "new java.lang.Integer(0)");
/* 1171 */     constructorMap.put("java.lang.Long", "new java.lang.Long(0)");
/* 1172 */     constructorMap.put("java.lang.Short", "new java.lang.Short((short)0)");
/* 1173 */     constructorMap.put("java.math.BigDecimal", "new java.math.BigDecimal(0)");
/*      */ 
/* 1175 */     constructorMap.put("java.math.BigInteger", "new java.math.BigInteger(\"0\")");
/*      */ 
/* 1177 */     constructorMap.put("java.lang.Object", "new java.lang.String()");
/* 1178 */     constructorMap.put("byte[]", "new byte[0]");
/* 1179 */     constructorMap.put("java.util.Calendar", "java.util.Calendar.getInstance()");
/*      */ 
/* 1181 */     constructorMap.put("javax.xml.namespace.QName", "new javax.xml.namespace.QName(\"http://double-double\", \"toil-and-trouble\")");
/*      */ 
/* 1184 */     constructorMap.put("org.apache.axis.types.NonNegativeInteger", "new org.apache.axis.types.NonNegativeInteger(\"0\")");
/*      */ 
/* 1187 */     constructorMap.put("org.apache.axis.types.PositiveInteger", "new org.apache.axis.types.PositiveInteger(\"1\")");
/*      */ 
/* 1189 */     constructorMap.put("org.apache.axis.types.NonPositiveInteger", "new org.apache.axis.types.NonPositiveInteger(\"0\")");
/*      */ 
/* 1192 */     constructorMap.put("org.apache.axis.types.NegativeInteger", "new org.apache.axis.types.NegativeInteger(\"-1\")");
/*      */ 
/* 1196 */     constructorThrowMap.put("org.apache.axis.types.Time", "new org.apache.axis.types.Time(\"15:45:45.275Z\")");
/*      */ 
/* 1199 */     constructorThrowMap.put("org.apache.axis.types.UnsignedLong", "new org.apache.axis.types.UnsignedLong(0)");
/*      */ 
/* 1201 */     constructorThrowMap.put("org.apache.axis.types.UnsignedInt", "new org.apache.axis.types.UnsignedInt(0)");
/*      */ 
/* 1203 */     constructorThrowMap.put("org.apache.axis.types.UnsignedShort", "new org.apache.axis.types.UnsignedShort(0)");
/*      */ 
/* 1205 */     constructorThrowMap.put("org.apache.axis.types.UnsignedByte", "new org.apache.axis.types.UnsignedByte(0)");
/*      */ 
/* 1207 */     constructorThrowMap.put("org.apache.axis.types.URI", "new org.apache.axis.types.URI(\"urn:testing\")");
/*      */ 
/* 1210 */     constructorThrowMap.put("org.apache.axis.types.Year", "new org.apache.axis.types.Year(2000)");
/*      */ 
/* 1212 */     constructorThrowMap.put("org.apache.axis.types.Month", "new org.apache.axis.types.Month(1)");
/*      */ 
/* 1214 */     constructorThrowMap.put("org.apache.axis.types.Day", "new org.apache.axis.types.Day(1)");
/*      */ 
/* 1216 */     constructorThrowMap.put("org.apache.axis.types.YearMonth", "new org.apache.axis.types.YearMonth(2000,1)");
/*      */ 
/* 1218 */     constructorThrowMap.put("org.apache.axis.types.MonthDay", "new org.apache.axis.types.MonthDay(1, 1)");
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.Utils
 * JD-Core Version:    0.6.0
 */