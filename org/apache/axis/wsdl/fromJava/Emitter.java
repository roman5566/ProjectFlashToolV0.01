/*      */ package org.apache.axis.wsdl.fromJava;
/*      */ 
/*      */ import com.ibm.wsdl.extensions.soap.SOAPAddressImpl;
/*      */ import com.ibm.wsdl.extensions.soap.SOAPBindingImpl;
/*      */ import com.ibm.wsdl.extensions.soap.SOAPBodyImpl;
/*      */ import com.ibm.wsdl.extensions.soap.SOAPFaultImpl;
/*      */ import com.ibm.wsdl.extensions.soap.SOAPHeaderImpl;
/*      */ import com.ibm.wsdl.extensions.soap.SOAPOperationImpl;
/*      */ import java.io.File;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.io.StringWriter;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.StringTokenizer;
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
/*      */ import javax.wsdl.OperationType;
/*      */ import javax.wsdl.Output;
/*      */ import javax.wsdl.Part;
/*      */ import javax.wsdl.Port;
/*      */ import javax.wsdl.PortType;
/*      */ import javax.wsdl.Service;
/*      */ import javax.wsdl.WSDLException;
/*      */ import javax.wsdl.extensions.ExtensibilityElement;
/*      */ import javax.wsdl.extensions.soap.SOAPAddress;
/*      */ import javax.wsdl.extensions.soap.SOAPBinding;
/*      */ import javax.wsdl.extensions.soap.SOAPBody;
/*      */ import javax.wsdl.extensions.soap.SOAPFault;
/*      */ import javax.wsdl.extensions.soap.SOAPHeader;
/*      */ import javax.wsdl.extensions.soap.SOAPOperation;
/*      */ import javax.wsdl.factory.WSDLFactory;
/*      */ import javax.wsdl.xml.WSDLReader;
/*      */ import javax.wsdl.xml.WSDLWriter;
/*      */ import javax.xml.namespace.QName;
/*      */ import javax.xml.parsers.ParserConfigurationException;
/*      */ import org.apache.axis.AxisFault;
/*      */ import org.apache.axis.Constants;
/*      */ import org.apache.axis.InternalException;
/*      */ import org.apache.axis.Version;
/*      */ import org.apache.axis.components.logger.LogFactory;
/*      */ import org.apache.axis.constants.Style;
/*      */ import org.apache.axis.constants.Use;
/*      */ import org.apache.axis.description.FaultDesc;
/*      */ import org.apache.axis.description.JavaServiceDesc;
/*      */ import org.apache.axis.description.OperationDesc;
/*      */ import org.apache.axis.description.ParameterDesc;
/*      */ import org.apache.axis.description.ServiceDesc;
/*      */ import org.apache.axis.encoding.TypeMapping;
/*      */ import org.apache.axis.encoding.TypeMappingRegistry;
/*      */ import org.apache.axis.encoding.TypeMappingRegistryImpl;
/*      */ import org.apache.axis.utils.ClassUtils;
/*      */ import org.apache.axis.utils.JavaUtils;
/*      */ import org.apache.axis.utils.Messages;
/*      */ import org.apache.axis.utils.XMLUtils;
/*      */ import org.apache.commons.logging.Log;
/*      */ import org.w3c.dom.Comment;
/*      */ import org.w3c.dom.Document;
/*      */ import org.w3c.dom.Element;
/*      */ import org.w3c.dom.Text;
/*      */ import org.xml.sax.SAXException;
/*      */ 
/*      */ public class Emitter
/*      */ {
/*  101 */   protected static Log log = LogFactory.getLog(Emitter.class.getName());
/*      */   public static final int MODE_ALL = 0;
/*      */   public static final int MODE_INTERFACE = 1;
/*      */   public static final int MODE_IMPLEMENTATION = 2;
/*      */   private Class cls;
/*      */   private Class[] extraClasses;
/*      */   private Class implCls;
/*  123 */   private Vector allowedMethods = null;
/*      */ 
/*  126 */   private Vector disallowedMethods = null;
/*      */ 
/*  129 */   private ArrayList stopClasses = new ArrayList();
/*      */ 
/*  133 */   private boolean useInheritedMethods = false;
/*      */   private String intfNS;
/*      */   private String implNS;
/*      */   private String inputSchema;
/*      */   private String inputWSDL;
/*      */   private String locationUrl;
/*      */   private String importUrl;
/*      */   private String servicePortName;
/*      */   private String serviceElementName;
/*  160 */   private String targetService = null;
/*      */   private String description;
/*  166 */   private Style style = Style.RPC;
/*      */ 
/*  169 */   private Use use = null;
/*      */ 
/*  172 */   private TypeMapping tm = null;
/*      */ 
/*  175 */   private TypeMappingRegistry tmr = new TypeMappingRegistryImpl();
/*      */   private Namespaces namespaces;
/*  181 */   private Map exceptionMsg = null;
/*      */   private Map usedElementNames;
/*      */   private ArrayList encodingList;
/*      */   protected Types types;
/*      */   private String clsName;
/*      */   private String portTypeName;
/*      */   private String bindingName;
/*      */   private ServiceDesc serviceDesc;
/*      */   private JavaServiceDesc serviceDesc2;
/*  208 */   private String soapAction = "DEFAULT";
/*      */ 
/*  211 */   private boolean emitAllTypes = false;
/*      */ 
/*  214 */   private String versionMessage = null;
/*      */   private HashMap qName2ClassMap;
/*      */   public static final int MODE_RPC = 0;
/*      */   public static final int MODE_DOCUMENT = 1;
/*      */   public static final int MODE_DOC_WRAPPED = 2;
/*  747 */   protected static TypeMapping standardTypes = (TypeMapping)new TypeMappingRegistryImpl().getTypeMapping(null);
/*      */   Document docHolder;
/*      */ 
/*      */   public Emitter()
/*      */   {
/*  237 */     createDocumentFragment();
/*      */ 
/*  239 */     this.namespaces = new Namespaces();
/*  240 */     this.exceptionMsg = new HashMap();
/*  241 */     this.usedElementNames = new HashMap();
/*  242 */     this.qName2ClassMap = new HashMap();
/*      */   }
/*      */ 
/*      */   public void emit(String filename1, String filename2)
/*      */     throws IOException, WSDLException, SAXException, ParserConfigurationException
/*      */   {
/*  260 */     Definition intf = getIntfWSDL();
/*  261 */     Definition impl = getImplWSDL();
/*      */ 
/*  264 */     if (filename1 == null) {
/*  265 */       filename1 = getServicePortName() + "_interface.wsdl";
/*      */     }
/*      */ 
/*  268 */     if (filename2 == null) {
/*  269 */       filename2 = getServicePortName() + "_implementation.wsdl";
/*      */     }
/*      */ 
/*  272 */     for (int i = 0; (this.extraClasses != null) && (i < this.extraClasses.length); )
/*      */     {
/*  274 */       this.types.writeTypeForPart(this.extraClasses[i], null);
/*      */ 
/*  273 */       i++;
/*      */     }
/*      */ 
/*  279 */     Document doc = WSDLFactory.newInstance().newWSDLWriter().getDocument(intf);
/*      */ 
/*  282 */     this.types.insertTypesFragment(doc);
/*  283 */     prettyDocumentToFile(doc, filename1);
/*      */ 
/*  286 */     doc = WSDLFactory.newInstance().newWSDLWriter().getDocument(impl);
/*      */ 
/*  288 */     prettyDocumentToFile(doc, filename2);
/*      */   }
/*      */ 
/*      */   public void emit(String filename)
/*      */     throws IOException, WSDLException, SAXException, ParserConfigurationException
/*      */   {
/*  303 */     emit(filename, 0);
/*      */   }
/*      */ 
/*      */   public Document emit(int mode)
/*      */     throws IOException, WSDLException, SAXException, ParserConfigurationException
/*      */   {
/*      */     Document doc;
/*  327 */     switch (mode)
/*      */     {
/*      */     case 0:
/*      */     default:
/*  331 */       Definition def = getWSDL();
/*      */ 
/*  333 */       int i = 0;
/*  334 */       while ((this.extraClasses != null) && (i < this.extraClasses.length))
/*      */       {
/*  336 */         this.types.writeTypeForPart(this.extraClasses[i], null);
/*      */ 
/*  335 */         i++;
/*      */       }
/*      */ 
/*  340 */       Document doc = WSDLFactory.newInstance().newWSDLWriter().getDocument(def);
/*      */ 
/*  343 */       this.types.insertTypesFragment(doc);
/*  344 */       break;
/*      */     case 1:
/*  347 */       Definition def = getIntfWSDL();
/*      */ 
/*  349 */       int i = 0;
/*  350 */       while ((this.extraClasses != null) && (i < this.extraClasses.length))
/*      */       {
/*  352 */         this.types.writeTypeForPart(this.extraClasses[i], null);
/*      */ 
/*  351 */         i++;
/*      */       }
/*      */ 
/*  356 */       Document doc = WSDLFactory.newInstance().newWSDLWriter().getDocument(def);
/*      */ 
/*  359 */       this.types.insertTypesFragment(doc);
/*  360 */       break;
/*      */     case 2:
/*  363 */       Definition def = getImplWSDL();
/*  364 */       doc = WSDLFactory.newInstance().newWSDLWriter().getDocument(def);
/*      */     }
/*      */ 
/*  370 */     if (this.versionMessage == null) {
/*  371 */       this.versionMessage = Messages.getMessage("wsdlCreated00", XMLUtils.xmlEncodeString(Version.getVersion()));
/*      */     }
/*      */ 
/*  376 */     if ((this.versionMessage != null) && (this.versionMessage.length() > 0)) {
/*  377 */       Comment wsdlVersion = doc.createComment(this.versionMessage);
/*  378 */       doc.getDocumentElement().insertBefore(wsdlVersion, doc.getDocumentElement().getFirstChild());
/*      */     }
/*      */ 
/*  383 */     return doc;
/*      */   }
/*      */ 
/*      */   public String emitToString(int mode)
/*      */     throws IOException, WSDLException, SAXException, ParserConfigurationException
/*      */   {
/*  404 */     Document doc = emit(mode);
/*  405 */     StringWriter sw = new StringWriter();
/*      */ 
/*  407 */     XMLUtils.PrettyDocumentToWriter(doc, sw);
/*      */ 
/*  409 */     return sw.toString();
/*      */   }
/*      */ 
/*      */   public void emit(String filename, int mode)
/*      */     throws IOException, WSDLException, SAXException, ParserConfigurationException
/*      */   {
/*  430 */     Document doc = emit(mode);
/*      */ 
/*  433 */     if (filename == null) {
/*  434 */       filename = getServicePortName();
/*      */ 
/*  436 */       switch (mode)
/*      */       {
/*      */       case 0:
/*  439 */         filename = filename + ".wsdl";
/*  440 */         break;
/*      */       case 1:
/*  443 */         filename = filename + "_interface.wsdl";
/*  444 */         break;
/*      */       case 2:
/*  447 */         filename = filename + "_implementation.wsdl";
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  452 */     prettyDocumentToFile(doc, filename);
/*      */   }
/*      */ 
/*      */   public Definition getWSDL()
/*      */     throws IOException, WSDLException, SAXException, ParserConfigurationException
/*      */   {
/*  470 */     init(0);
/*      */ 
/*  473 */     Definition def = createDefinition();
/*      */ 
/*  476 */     writeDefinitions(def, this.intfNS);
/*      */ 
/*  479 */     this.types = createTypes(def);
/*      */ 
/*  482 */     Binding binding = writeBinding(def, true);
/*      */ 
/*  484 */     writePortType(def, binding);
/*  485 */     writeService(def, binding);
/*      */ 
/*  487 */     return def;
/*      */   }
/*      */ 
/*      */   public Definition getIntfWSDL()
/*      */     throws IOException, WSDLException, SAXException, ParserConfigurationException
/*      */   {
/*  505 */     init(1);
/*      */ 
/*  508 */     Definition def = createDefinition();
/*      */ 
/*  511 */     writeDefinitions(def, this.intfNS);
/*      */ 
/*  514 */     this.types = createTypes(def);
/*      */ 
/*  517 */     Binding binding = writeBinding(def, true);
/*      */ 
/*  519 */     writePortType(def, binding);
/*      */ 
/*  521 */     return def;
/*      */   }
/*      */ 
/*      */   public Definition getImplWSDL()
/*      */     throws IOException, WSDLException, SAXException, ParserConfigurationException
/*      */   {
/*  539 */     init(2);
/*      */ 
/*  542 */     Definition def = createDefinition();
/*      */ 
/*  545 */     writeDefinitions(def, this.implNS);
/*  546 */     writeImport(def, this.intfNS, this.importUrl);
/*      */ 
/*  549 */     Binding binding = writeBinding(def, false);
/*      */ 
/*  551 */     writeService(def, binding);
/*      */ 
/*  553 */     return def;
/*      */   }
/*      */ 
/*      */   protected void init(int mode)
/*      */   {
/*  565 */     if (this.use == null) {
/*  566 */       if (this.style == Style.RPC)
/*  567 */         this.use = Use.ENCODED;
/*      */       else {
/*  569 */         this.use = Use.LITERAL;
/*      */       }
/*      */     }
/*      */ 
/*  573 */     if (this.tm == null) {
/*  574 */       String encodingStyle = "";
/*  575 */       if (this.use == Use.ENCODED) {
/*  576 */         encodingStyle = "http://schemas.xmlsoap.org/soap/encoding/";
/*      */       }
/*      */ 
/*  579 */       this.tm = ((TypeMapping)this.tmr.getTypeMapping(encodingStyle));
/*      */     }
/*      */ 
/*  583 */     if (this.serviceDesc == null) {
/*  584 */       JavaServiceDesc javaServiceDesc = new JavaServiceDesc();
/*  585 */       this.serviceDesc = javaServiceDesc;
/*      */ 
/*  587 */       javaServiceDesc.setImplClass(this.cls);
/*      */ 
/*  590 */       this.serviceDesc.setTypeMapping(this.tm);
/*      */ 
/*  592 */       javaServiceDesc.setStopClasses(this.stopClasses);
/*  593 */       this.serviceDesc.setAllowedMethods(this.allowedMethods);
/*  594 */       javaServiceDesc.setDisallowedMethods(this.disallowedMethods);
/*  595 */       this.serviceDesc.setStyle(this.style);
/*  596 */       this.serviceDesc.setUse(this.use);
/*      */ 
/*  602 */       if ((this.implCls != null) && (this.implCls != this.cls) && (this.serviceDesc2 == null))
/*      */       {
/*  604 */         this.serviceDesc2 = new JavaServiceDesc();
/*      */ 
/*  606 */         this.serviceDesc2.setImplClass(this.implCls);
/*      */ 
/*  609 */         this.serviceDesc2.setTypeMapping(this.tm);
/*      */ 
/*  611 */         this.serviceDesc2.setStopClasses(this.stopClasses);
/*  612 */         this.serviceDesc2.setAllowedMethods(this.allowedMethods);
/*  613 */         this.serviceDesc2.setDisallowedMethods(this.disallowedMethods);
/*  614 */         this.serviceDesc2.setStyle(this.style);
/*      */       }
/*      */     }
/*      */ 
/*  618 */     if (this.encodingList == null)
/*      */     {
/*  622 */       if (this.cls != null) {
/*  623 */         this.clsName = this.cls.getName();
/*  624 */         this.clsName = this.clsName.substring(this.clsName.lastIndexOf('.') + 1);
/*      */       } else {
/*  626 */         this.clsName = getServiceDesc().getName();
/*      */       }
/*      */ 
/*  630 */       if (getPortTypeName() == null) {
/*  631 */         setPortTypeName(this.clsName);
/*      */       }
/*      */ 
/*  635 */       if (getServiceElementName() == null) {
/*  636 */         setServiceElementName(getPortTypeName() + "Service");
/*      */       }
/*      */ 
/*  640 */       if (getServicePortName() == null) {
/*  641 */         String name = getLocationUrl();
/*      */ 
/*  643 */         if (name != null) {
/*  644 */           if (name.lastIndexOf('/') > 0)
/*  645 */             name = name.substring(name.lastIndexOf('/') + 1);
/*  646 */           else if (name.lastIndexOf('\\') > 0)
/*  647 */             name = name.substring(name.lastIndexOf('\\') + 1);
/*      */           else {
/*  649 */             name = null;
/*      */           }
/*      */ 
/*  653 */           if ((name != null) && (name.endsWith(".jws"))) {
/*  654 */             name = name.substring(0, name.length() - ".jws".length());
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  659 */         if ((name == null) || (name.equals(""))) {
/*  660 */           name = this.clsName;
/*      */         }
/*      */ 
/*  663 */         setServicePortName(name);
/*      */       }
/*      */ 
/*  667 */       if (getBindingName() == null) {
/*  668 */         setBindingName(getServicePortName() + "SoapBinding");
/*      */       }
/*      */ 
/*  671 */       this.encodingList = new ArrayList();
/*      */ 
/*  673 */       this.encodingList.add(Constants.URI_DEFAULT_SOAP_ENC);
/*      */ 
/*  675 */       if (this.intfNS == null) {
/*  676 */         Package pkg = this.cls.getPackage();
/*      */ 
/*  678 */         this.intfNS = this.namespaces.getCreate(pkg == null ? null : pkg.getName());
/*      */       }
/*      */ 
/*  685 */       if (this.implNS == null) {
/*  686 */         if (mode == 0)
/*  687 */           this.implNS = this.intfNS;
/*      */         else {
/*  689 */           this.implNS = (this.intfNS + "-impl");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  694 */       this.serviceDesc.setDefaultNamespace(this.intfNS);
/*      */ 
/*  696 */       if (this.serviceDesc2 != null) {
/*  697 */         this.serviceDesc2.setDefaultNamespace(this.implNS);
/*      */       }
/*      */ 
/*  700 */       if (this.cls != null) {
/*  701 */         String clsName = this.cls.getName();
/*  702 */         int idx = clsName.lastIndexOf(".");
/*  703 */         if (idx > 0) {
/*  704 */           String pkgName = clsName.substring(0, idx);
/*  705 */           this.namespaces.put(pkgName, this.intfNS, "intf");
/*      */         }
/*      */       }
/*      */ 
/*  709 */       this.namespaces.putPrefix(this.implNS, "impl");
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Definition createDefinition()
/*      */     throws WSDLException, SAXException, IOException, ParserConfigurationException
/*      */   {
/*      */     Definition def;
/*      */     Definition def;
/*  729 */     if (this.inputWSDL == null) {
/*  730 */       def = WSDLFactory.newInstance().newDefinition();
/*      */     } else {
/*  732 */       WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
/*      */ 
/*  734 */       Document doc = XMLUtils.newDocument(this.inputWSDL);
/*      */ 
/*  736 */       def = reader.readWSDL(null, doc);
/*      */ 
/*  740 */       def.setTypes(null);
/*      */     }
/*      */ 
/*  743 */     return def;
/*      */   }
/*      */ 
/*      */   protected Types createTypes(Definition def)
/*      */     throws IOException, WSDLException, SAXException, ParserConfigurationException
/*      */   {
/*  765 */     this.types = new Types(def, this.tm, (TypeMapping)this.tmr.getDefaultTypeMapping(), this.namespaces, this.intfNS, this.stopClasses, this.serviceDesc, this);
/*      */ 
/*  768 */     if (this.inputWSDL != null) {
/*  769 */       this.types.loadInputTypes(this.inputWSDL);
/*      */     }
/*      */ 
/*  772 */     if (this.inputSchema != null) {
/*  773 */       StringTokenizer tokenizer = new StringTokenizer(this.inputSchema, ", ");
/*      */ 
/*  775 */       while (tokenizer.hasMoreTokens()) {
/*  776 */         String token = tokenizer.nextToken();
/*      */ 
/*  778 */         this.types.loadInputSchema(token);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  783 */     if ((this.emitAllTypes) && (this.tm != null)) {
/*  784 */       Class[] mappedTypes = this.tm.getAllClasses();
/*      */ 
/*  786 */       for (int i = 0; i < mappedTypes.length; i++) {
/*  787 */         Class mappedType = mappedTypes[i];
/*  788 */         QName name = this.tm.getTypeQName(mappedType);
/*  789 */         if (name.getLocalPart().indexOf(">") != -1)
/*      */         {
/*      */           continue;
/*      */         }
/*      */ 
/*  800 */         if (standardTypes.getSerializer(mappedType) == null) {
/*  801 */           this.types.writeTypeForPart(mappedType, name);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  807 */       this.types.mappedTypes = null;
/*      */     }
/*      */ 
/*  810 */     return this.types;
/*      */   }
/*      */ 
/*      */   protected Element createDocumentationElement(String documentation)
/*      */   {
/*  820 */     Element element = this.docHolder.createElementNS("http://schemas.xmlsoap.org/wsdl/", "documentation");
/*  821 */     element.setPrefix("wsdl");
/*  822 */     Text textNode = this.docHolder.createTextNode(documentation);
/*      */ 
/*  825 */     element.appendChild(textNode);
/*  826 */     return element;
/*      */   }
/*      */ 
/*      */   protected void writeDefinitions(Definition def, String tns)
/*      */   {
/*  837 */     def.setTargetNamespace(tns);
/*  838 */     def.addNamespace("intf", this.intfNS);
/*  839 */     def.addNamespace("impl", this.implNS);
/*  840 */     def.addNamespace("wsdlsoap", "http://schemas.xmlsoap.org/wsdl/soap/");
/*      */ 
/*  842 */     this.namespaces.putPrefix("http://schemas.xmlsoap.org/wsdl/soap/", "wsdlsoap");
/*      */ 
/*  844 */     def.addNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");
/*  845 */     this.namespaces.putPrefix("http://schemas.xmlsoap.org/wsdl/", "wsdl");
/*      */ 
/*  847 */     if (this.use == Use.ENCODED) {
/*  848 */       def.addNamespace("soapenc", Constants.URI_DEFAULT_SOAP_ENC);
/*      */ 
/*  850 */       this.namespaces.putPrefix(Constants.URI_DEFAULT_SOAP_ENC, "soapenc");
/*      */     }
/*      */ 
/*  854 */     def.addNamespace("xsd", "http://www.w3.org/2001/XMLSchema");
/*      */ 
/*  856 */     this.namespaces.putPrefix("http://www.w3.org/2001/XMLSchema", "xsd");
/*      */ 
/*  858 */     def.addNamespace("apachesoap", "http://xml.apache.org/xml-soap");
/*  859 */     this.namespaces.putPrefix("http://xml.apache.org/xml-soap", "apachesoap");
/*      */   }
/*      */ 
/*      */   protected void writeImport(Definition def, String tns, String loc)
/*      */   {
/*  872 */     Import imp = def.createImport();
/*      */ 
/*  874 */     imp.setNamespaceURI(tns);
/*      */ 
/*  876 */     if ((loc != null) && (!loc.equals(""))) {
/*  877 */       imp.setLocationURI(loc);
/*      */     }
/*      */ 
/*  880 */     def.addImport(imp);
/*      */   }
/*      */ 
/*      */   protected Binding writeBinding(Definition def, boolean add)
/*      */   {
/*  892 */     QName bindingQName = new QName(this.intfNS, getBindingName());
/*      */ 
/*  895 */     Binding binding = def.getBinding(bindingQName);
/*      */ 
/*  897 */     if (binding != null) {
/*  898 */       return binding;
/*      */     }
/*      */ 
/*  902 */     binding = def.createBinding();
/*      */ 
/*  904 */     binding.setUndefined(false);
/*  905 */     binding.setQName(bindingQName);
/*      */ 
/*  907 */     SOAPBinding soapBinding = new SOAPBindingImpl();
/*  908 */     String styleStr = this.style == Style.RPC ? "rpc" : "document";
/*      */ 
/*  912 */     soapBinding.setStyle(styleStr);
/*  913 */     soapBinding.setTransportURI("http://schemas.xmlsoap.org/soap/http");
/*  914 */     binding.addExtensibilityElement(soapBinding);
/*      */ 
/*  916 */     if (add) {
/*  917 */       def.addBinding(binding);
/*      */     }
/*      */ 
/*  920 */     return binding;
/*      */   }
/*      */ 
/*      */   private void createDocumentFragment()
/*      */   {
/*      */     try
/*      */     {
/*  932 */       this.docHolder = XMLUtils.newDocument();
/*      */     }
/*      */     catch (ParserConfigurationException e)
/*      */     {
/*  936 */       throw new InternalException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeService(Definition def, Binding binding)
/*      */   {
/*  948 */     QName serviceElementQName = new QName(this.implNS, getServiceElementName());
/*      */ 
/*  951 */     Service service = def.getService(serviceElementQName);
/*      */ 
/*  953 */     if (service == null) {
/*  954 */       service = def.createService();
/*      */ 
/*  956 */       service.setQName(serviceElementQName);
/*  957 */       def.addService(service);
/*      */     }
/*      */ 
/*  960 */     if (this.description != null) {
/*  961 */       service.setDocumentationElement(createDocumentationElement(this.description));
/*      */     }
/*  963 */     else if (this.serviceDesc.getDocumentation() != null) {
/*  964 */       service.setDocumentationElement(createDocumentationElement(this.serviceDesc.getDocumentation()));
/*      */     }
/*      */ 
/*  970 */     Port port = def.createPort();
/*      */ 
/*  972 */     port.setBinding(binding);
/*      */ 
/*  975 */     port.setName(getServicePortName());
/*      */ 
/*  977 */     SOAPAddress addr = new SOAPAddressImpl();
/*      */ 
/*  979 */     addr.setLocationURI(this.locationUrl);
/*  980 */     port.addExtensibilityElement(addr);
/*  981 */     service.addPort(port);
/*      */   }
/*      */ 
/*      */   protected void writePortType(Definition def, Binding binding)
/*      */     throws WSDLException, AxisFault
/*      */   {
/*  995 */     QName portTypeQName = new QName(this.intfNS, getPortTypeName());
/*      */ 
/*  998 */     PortType portType = def.getPortType(portTypeQName);
/*  999 */     boolean newPortType = false;
/*      */ 
/* 1001 */     if (portType == null) {
/* 1002 */       portType = def.createPortType();
/*      */ 
/* 1004 */       portType.setUndefined(false);
/* 1005 */       portType.setQName(portTypeQName);
/*      */ 
/* 1007 */       newPortType = true;
/* 1008 */     } else if (binding.getBindingOperations().size() > 0)
/*      */     {
/* 1012 */       return;
/*      */     }
/*      */ 
/* 1016 */     ArrayList operations = this.serviceDesc.getOperations();
/*      */ 
/* 1018 */     for (Iterator i = operations.iterator(); i.hasNext(); ) {
/* 1019 */       OperationDesc thisOper = (OperationDesc)i.next();
/* 1020 */       BindingOperation bindingOper = writeOperation(def, binding, thisOper);
/*      */ 
/* 1022 */       Operation oper = bindingOper.getOperation();
/* 1023 */       OperationDesc messageOper = thisOper;
/*      */ 
/* 1026 */       if (messageOper.getDocumentation() != null) {
/* 1027 */         oper.setDocumentationElement(createDocumentationElement(messageOper.getDocumentation()));
/*      */       }
/*      */ 
/* 1032 */       if (this.serviceDesc2 != null)
/*      */       {
/* 1040 */         OperationDesc[] operArray = this.serviceDesc2.getOperationsByName(thisOper.getName());
/*      */ 
/* 1042 */         boolean found = false;
/*      */ 
/* 1044 */         if (operArray != null) {
/* 1045 */           for (int j = 0; (j < operArray.length) && (!found); j++) {
/* 1046 */             OperationDesc tryOper = operArray[j];
/*      */ 
/* 1048 */             if (tryOper.getParameters().size() != thisOper.getParameters().size())
/*      */               continue;
/* 1050 */             boolean parmsMatch = true;
/*      */ 
/* 1052 */             int k = 0;
/*      */ 
/* 1054 */             for (; (k < thisOper.getParameters().size()) && (parmsMatch); k++) {
/* 1055 */               if ((tryOper.getParameter(k).getMode() == thisOper.getParameter(k).getMode()) && (tryOper.getParameter(k).getJavaType().equals(thisOper.getParameter(k).getJavaType())))
/*      */               {
/*      */                 continue;
/*      */               }
/*      */ 
/* 1062 */               parmsMatch = false;
/*      */             }
/*      */ 
/* 1066 */             if (parmsMatch) {
/* 1067 */               messageOper = tryOper;
/* 1068 */               found = true;
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1075 */       writeMessages(def, oper, messageOper, bindingOper);
/*      */ 
/* 1077 */       if (newPortType) {
/* 1078 */         portType.addOperation(oper);
/*      */       }
/*      */     }
/*      */ 
/* 1082 */     if (newPortType) {
/* 1083 */       def.addPortType(portType);
/*      */     }
/*      */ 
/* 1086 */     binding.setPortType(portType);
/*      */   }
/*      */ 
/*      */   protected void writeMessages(Definition def, Operation oper, OperationDesc desc, BindingOperation bindingOper)
/*      */     throws WSDLException, AxisFault
/*      */   {
/* 1105 */     Input input = def.createInput();
/* 1106 */     Message msg = writeRequestMessage(def, desc, bindingOper);
/*      */ 
/* 1108 */     input.setMessage(msg);
/*      */ 
/* 1113 */     String name = msg.getQName().getLocalPart();
/*      */ 
/* 1115 */     input.setName(name);
/* 1116 */     bindingOper.getBindingInput().setName(name);
/* 1117 */     oper.setInput(input);
/* 1118 */     def.addMessage(msg);
/*      */ 
/* 1120 */     if (OperationType.REQUEST_RESPONSE.equals(desc.getMep())) {
/* 1121 */       msg = writeResponseMessage(def, desc, bindingOper);
/*      */ 
/* 1123 */       Output output = def.createOutput();
/*      */ 
/* 1125 */       output.setMessage(msg);
/*      */ 
/* 1130 */       name = msg.getQName().getLocalPart();
/*      */ 
/* 1132 */       output.setName(name);
/* 1133 */       bindingOper.getBindingOutput().setName(name);
/* 1134 */       oper.setOutput(output);
/* 1135 */       def.addMessage(msg);
/*      */     }
/*      */ 
/* 1138 */     ArrayList exceptions = desc.getFaults();
/*      */ 
/* 1140 */     for (int i = 0; (exceptions != null) && (i < exceptions.size()); i++) {
/* 1141 */       FaultDesc faultDesc = (FaultDesc)exceptions.get(i);
/*      */ 
/* 1143 */       msg = writeFaultMessage(def, faultDesc);
/*      */ 
/* 1146 */       Fault fault = def.createFault();
/*      */ 
/* 1148 */       fault.setMessage(msg);
/* 1149 */       fault.setName(faultDesc.getName());
/* 1150 */       oper.addFault(fault);
/*      */ 
/* 1153 */       BindingFault bFault = def.createBindingFault();
/*      */ 
/* 1155 */       bFault.setName(faultDesc.getName());
/*      */ 
/* 1157 */       SOAPFault soapFault = writeSOAPFault(faultDesc);
/*      */ 
/* 1159 */       bFault.addExtensibilityElement(soapFault);
/* 1160 */       bindingOper.addBindingFault(bFault);
/*      */ 
/* 1163 */       if (def.getMessage(msg.getQName()) == null) {
/* 1164 */         def.addMessage(msg);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1169 */     ArrayList parameters = desc.getParameters();
/* 1170 */     Vector names = new Vector();
/*      */ 
/* 1172 */     for (int i = 0; i < parameters.size(); i++) {
/* 1173 */       ParameterDesc param = (ParameterDesc)parameters.get(i);
/*      */ 
/* 1175 */       names.add(param.getName());
/*      */     }
/*      */ 
/* 1178 */     if (names.size() > 0)
/* 1179 */       if (this.style == Style.WRAPPED)
/* 1180 */         names.clear();
/*      */       else
/* 1182 */         oper.setParameterOrdering(names);
/*      */   }
/*      */ 
/*      */   protected BindingOperation writeOperation(Definition def, Binding binding, OperationDesc desc)
/*      */   {
/* 1198 */     Operation oper = def.createOperation();
/*      */ 
/* 1200 */     QName elementQName = desc.getElementQName();
/* 1201 */     if ((elementQName != null) && (elementQName.getLocalPart() != null))
/* 1202 */       oper.setName(elementQName.getLocalPart());
/*      */     else {
/* 1204 */       oper.setName(desc.getName());
/*      */     }
/* 1206 */     oper.setUndefined(false);
/*      */ 
/* 1208 */     return writeBindingOperation(def, binding, oper, desc);
/*      */   }
/*      */ 
/*      */   protected BindingOperation writeBindingOperation(Definition def, Binding binding, Operation oper, OperationDesc desc)
/*      */   {
/* 1225 */     BindingOperation bindingOper = def.createBindingOperation();
/* 1226 */     BindingInput bindingInput = def.createBindingInput();
/* 1227 */     BindingOutput bindingOutput = null;
/*      */ 
/* 1230 */     if (OperationType.REQUEST_RESPONSE.equals(desc.getMep())) {
/* 1231 */       bindingOutput = def.createBindingOutput();
/*      */     }
/* 1233 */     bindingOper.setName(oper.getName());
/* 1234 */     bindingOper.setOperation(oper);
/*      */ 
/* 1236 */     SOAPOperation soapOper = new SOAPOperationImpl();
/*      */     String soapAction;
/*      */     String soapAction;
/* 1243 */     if (getSoapAction().equalsIgnoreCase("OPERATION")) {
/* 1244 */       soapAction = oper.getName();
/*      */     }
/*      */     else
/*      */     {
/*      */       String soapAction;
/* 1245 */       if (getSoapAction().equalsIgnoreCase("NONE")) {
/* 1246 */         soapAction = "";
/*      */       } else {
/* 1248 */         soapAction = desc.getSoapAction();
/*      */ 
/* 1250 */         if (soapAction == null) {
/* 1251 */           soapAction = "";
/*      */         }
/*      */       }
/*      */     }
/* 1255 */     soapOper.setSoapActionURI(soapAction);
/*      */ 
/* 1260 */     bindingOper.addExtensibilityElement(soapOper);
/*      */ 
/* 1263 */     ExtensibilityElement inputBody = writeSOAPBody(desc.getElementQName());
/* 1264 */     bindingInput.addExtensibilityElement(inputBody);
/*      */ 
/* 1270 */     if (bindingOutput != null) {
/* 1271 */       ExtensibilityElement outputBody = writeSOAPBody(desc.getReturnQName());
/* 1272 */       bindingOutput.addExtensibilityElement(outputBody);
/* 1273 */       bindingOper.setBindingOutput(bindingOutput);
/*      */     }
/*      */ 
/* 1280 */     bindingOper.setBindingInput(bindingInput);
/*      */ 
/* 1305 */     binding.addBindingOperation(bindingOper);
/*      */ 
/* 1307 */     return bindingOper;
/*      */   }
/*      */ 
/*      */   protected SOAPHeader writeSOAPHeader(ParameterDesc p, QName messageQName, String partName)
/*      */   {
/* 1315 */     SOAPHeaderImpl soapHeader = new SOAPHeaderImpl();
/*      */ 
/* 1318 */     if (this.use == Use.ENCODED) {
/* 1319 */       soapHeader.setUse("encoded");
/* 1320 */       soapHeader.setEncodingStyles(this.encodingList);
/*      */     } else {
/* 1322 */       soapHeader.setUse("literal");
/*      */     }
/*      */ 
/* 1326 */     if (this.targetService == null)
/* 1327 */       soapHeader.setNamespaceURI(this.intfNS);
/*      */     else {
/* 1329 */       soapHeader.setNamespaceURI(this.targetService);
/*      */     }
/* 1331 */     QName headerQName = p.getQName();
/* 1332 */     if ((headerQName != null) && (!headerQName.getNamespaceURI().equals(""))) {
/* 1333 */       soapHeader.setNamespaceURI(headerQName.getNamespaceURI());
/*      */     }
/*      */ 
/* 1337 */     soapHeader.setMessage(messageQName);
/* 1338 */     soapHeader.setPart(partName);
/*      */ 
/* 1340 */     return soapHeader;
/*      */   }
/*      */ 
/*      */   protected ExtensibilityElement writeSOAPBody(QName operQName)
/*      */   {
/* 1351 */     SOAPBody soapBody = new SOAPBodyImpl();
/*      */ 
/* 1354 */     if (this.use == Use.ENCODED) {
/* 1355 */       soapBody.setUse("encoded");
/* 1356 */       soapBody.setEncodingStyles(this.encodingList);
/*      */     } else {
/* 1358 */       soapBody.setUse("literal");
/*      */     }
/*      */ 
/* 1361 */     if (this.style == Style.RPC) {
/* 1362 */       if (this.targetService == null)
/* 1363 */         soapBody.setNamespaceURI(this.intfNS);
/*      */       else {
/* 1365 */         soapBody.setNamespaceURI(this.targetService);
/*      */       }
/*      */ 
/* 1368 */       if ((operQName != null) && (!operQName.getNamespaceURI().equals(""))) {
/* 1369 */         soapBody.setNamespaceURI(operQName.getNamespaceURI());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1377 */     return soapBody;
/*      */   }
/*      */ 
/*      */   protected SOAPFault writeSOAPFault(FaultDesc faultDesc)
/*      */   {
/* 1388 */     SOAPFault soapFault = new SOAPFaultImpl();
/*      */ 
/* 1390 */     soapFault.setName(faultDesc.getName());
/*      */ 
/* 1392 */     if (this.use != Use.ENCODED) {
/* 1393 */       soapFault.setUse("literal");
/*      */     }
/*      */     else
/*      */     {
/* 1397 */       soapFault.setUse("encoded");
/* 1398 */       soapFault.setEncodingStyles(this.encodingList);
/*      */ 
/* 1402 */       QName faultQName = faultDesc.getQName();
/*      */ 
/* 1404 */       if ((faultQName != null) && (!faultQName.getNamespaceURI().equals("")))
/*      */       {
/* 1406 */         soapFault.setNamespaceURI(faultQName.getNamespaceURI());
/*      */       }
/* 1408 */       else if (this.targetService == null)
/* 1409 */         soapFault.setNamespaceURI(this.intfNS);
/*      */       else {
/* 1411 */         soapFault.setNamespaceURI(this.targetService);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1416 */     return soapFault;
/*      */   }
/*      */ 
/*      */   protected Message writeRequestMessage(Definition def, OperationDesc oper, BindingOperation bindop)
/*      */     throws WSDLException, AxisFault
/*      */   {
/* 1433 */     ArrayList bodyParts = new ArrayList();
/* 1434 */     ArrayList parameters = oper.getAllInParams();
/*      */ 
/* 1436 */     Message msg = def.createMessage();
/* 1437 */     QName qName = createMessageName(def, getRequestQName(oper).getLocalPart() + "Request");
/*      */ 
/* 1440 */     msg.setQName(qName);
/* 1441 */     msg.setUndefined(false);
/*      */ 
/* 1444 */     boolean headers = writeHeaderParts(def, parameters, bindop, msg, true);
/*      */ 
/* 1446 */     if (oper.getStyle() == Style.MESSAGE)
/*      */     {
/* 1451 */       QName qname = oper.getElementQName();
/* 1452 */       this.types.writeElementDecl(qname, Object.class, Constants.XSD_ANYTYPE, false, null);
/*      */ 
/* 1455 */       Part part = def.createPart();
/*      */ 
/* 1457 */       part.setName("part");
/* 1458 */       part.setElementName(qname);
/* 1459 */       msg.addPart(part);
/* 1460 */       bodyParts.add(part.getName());
/*      */     }
/* 1462 */     else if (oper.getStyle() == Style.WRAPPED)
/*      */     {
/* 1467 */       String partName = writeWrapperPart(def, msg, oper, true);
/* 1468 */       bodyParts.add(partName);
/*      */     }
/*      */     else
/*      */     {
/* 1478 */       if ((oper.getStyle() == Style.DOCUMENT) && (parameters.size() > 1)) {
/* 1479 */         System.out.println(Messages.getMessage("warnDocLitInteropMultipleInputParts"));
/*      */       }
/*      */ 
/* 1483 */       for (int i = 0; i < parameters.size(); i++) {
/* 1484 */         ParameterDesc parameter = (ParameterDesc)parameters.get(i);
/* 1485 */         if ((!parameter.isInHeader()) && (!parameter.isOutHeader())) {
/* 1486 */           String partName = writePartToMessage(def, msg, true, parameter);
/* 1487 */           bodyParts.add(partName);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1494 */     if (headers)
/*      */     {
/* 1496 */       List extensibilityElements = bindop.getBindingInput().getExtensibilityElements();
/* 1497 */       for (int i = 0; i < extensibilityElements.size(); i++)
/*      */       {
/* 1499 */         Object ele = extensibilityElements.get(i);
/* 1500 */         if (!(ele instanceof SOAPBodyImpl))
/*      */           continue;
/* 1502 */         SOAPBodyImpl soapBody = (SOAPBodyImpl)ele;
/* 1503 */         soapBody.setParts(bodyParts);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1508 */     return msg;
/*      */   }
/*      */ 
/*      */   private boolean writeHeaderParts(Definition def, ArrayList parameters, BindingOperation bindop, Message msg, boolean request)
/*      */     throws WSDLException, AxisFault
/*      */   {
/* 1528 */     boolean wroteHeaderParts = false;
/*      */ 
/* 1532 */     for (int i = 0; i < parameters.size(); i++) {
/* 1533 */       ParameterDesc parameter = (ParameterDesc)parameters.get(i);
/*      */ 
/* 1536 */       if ((request) && (parameter.isInHeader()))
/*      */       {
/* 1538 */         String partName = writePartToMessage(def, msg, request, parameter);
/*      */ 
/* 1540 */         SOAPHeader hdr = writeSOAPHeader(parameter, msg.getQName(), partName);
/*      */ 
/* 1542 */         bindop.getBindingInput().addExtensibilityElement(hdr);
/* 1543 */         wroteHeaderParts = true;
/*      */       } else {
/* 1545 */         if ((request) || (!parameter.isOutHeader()))
/*      */           continue;
/* 1547 */         String partName = writePartToMessage(def, msg, request, parameter);
/*      */ 
/* 1549 */         SOAPHeader hdr = writeSOAPHeader(parameter, msg.getQName(), partName);
/*      */ 
/* 1551 */         bindop.getBindingOutput().addExtensibilityElement(hdr);
/* 1552 */         wroteHeaderParts = true;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1558 */     return wroteHeaderParts;
/*      */   }
/*      */ 
/*      */   protected QName getRequestQName(OperationDesc oper)
/*      */   {
/* 1569 */     qualifyOperation(oper);
/*      */ 
/* 1571 */     QName qname = oper.getElementQName();
/*      */ 
/* 1573 */     if (qname == null) {
/* 1574 */       qname = new QName(oper.getName());
/*      */     }
/*      */ 
/* 1577 */     return qname;
/*      */   }
/*      */ 
/*      */   private void qualifyOperation(OperationDesc oper)
/*      */   {
/* 1587 */     if ((this.style == Style.WRAPPED) && (this.use == Use.LITERAL)) {
/* 1588 */       QName qname = oper.getElementQName();
/*      */ 
/* 1590 */       if (qname == null)
/* 1591 */         qname = new QName(this.intfNS, oper.getName());
/* 1592 */       else if (qname.getNamespaceURI().equals("")) {
/* 1593 */         qname = new QName(this.intfNS, qname.getLocalPart());
/*      */       }
/*      */ 
/* 1596 */       oper.setElementQName(qname);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected QName getResponseQName(OperationDesc oper)
/*      */   {
/* 1608 */     qualifyOperation(oper);
/*      */ 
/* 1610 */     QName qname = oper.getElementQName();
/*      */ 
/* 1612 */     if (qname == null) {
/* 1613 */       return new QName(oper.getName() + "Response");
/*      */     }
/*      */ 
/* 1616 */     return new QName(qname.getNamespaceURI(), qname.getLocalPart() + "Response");
/*      */   }
/*      */ 
/*      */   public String writeWrapperPart(Definition def, Message msg, OperationDesc oper, boolean request)
/*      */     throws AxisFault
/*      */   {
/* 1635 */     QName qname = request ? getRequestQName(oper) : getResponseQName(oper);
/*      */     boolean hasParams;
/*      */     boolean hasParams;
/* 1640 */     if (request) {
/* 1641 */       hasParams = oper.getNumInParams() > 0;
/*      */     }
/*      */     else
/*      */     {
/*      */       boolean hasParams;
/* 1643 */       if (oper.getReturnClass() != Void.TYPE)
/* 1644 */         hasParams = true;
/*      */       else {
/* 1646 */         hasParams = oper.getNumOutParams() > 0;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1651 */     Element sequence = this.types.writeWrapperElement(qname, request, hasParams);
/*      */ 
/* 1655 */     if (sequence != null) {
/* 1656 */       ArrayList parameters = request ? oper.getAllInParams() : oper.getAllOutParams();
/*      */ 
/* 1660 */       if (!request)
/*      */       {
/*      */         String retName;
/*      */         String retName;
/* 1663 */         if (oper.getReturnQName() == null)
/* 1664 */           retName = oper.getName() + "Return";
/*      */         else {
/* 1666 */           retName = oper.getReturnQName().getLocalPart();
/*      */         }
/*      */ 
/* 1669 */         this.types.writeWrappedParameter(sequence, retName, oper.getReturnType(), oper.getReturnClass());
/*      */       }
/*      */ 
/* 1674 */       for (int i = 0; i < parameters.size(); i++) {
/* 1675 */         ParameterDesc parameter = (ParameterDesc)parameters.get(i);
/*      */ 
/* 1678 */         if ((parameter.isInHeader()) || (parameter.isOutHeader()))
/*      */           continue;
/* 1680 */         this.types.writeWrappedParameter(sequence, parameter.getName(), parameter.getTypeQName(), parameter.getJavaType());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1689 */     Part part = def.createPart();
/*      */ 
/* 1691 */     part.setName("parameters");
/* 1692 */     part.setElementName(qname);
/* 1693 */     msg.addPart(part);
/*      */ 
/* 1695 */     return part.getName();
/*      */   }
/*      */ 
/*      */   protected Message writeResponseMessage(Definition def, OperationDesc desc, BindingOperation bindop)
/*      */     throws WSDLException, AxisFault
/*      */   {
/* 1711 */     ArrayList bodyParts = new ArrayList();
/* 1712 */     ArrayList parameters = desc.getAllOutParams();
/*      */ 
/* 1714 */     Message msg = def.createMessage();
/* 1715 */     QName qName = createMessageName(def, getResponseQName(desc).getLocalPart());
/*      */ 
/* 1718 */     msg.setQName(qName);
/* 1719 */     msg.setUndefined(false);
/*      */ 
/* 1722 */     boolean headers = writeHeaderParts(def, parameters, bindop, msg, false);
/*      */ 
/* 1724 */     if (desc.getStyle() == Style.WRAPPED) {
/* 1725 */       String partName = writeWrapperPart(def, msg, desc, false);
/* 1726 */       bodyParts.add(partName);
/*      */     }
/*      */     else
/*      */     {
/* 1730 */       ParameterDesc retParam = new ParameterDesc();
/*      */ 
/* 1732 */       if (desc.getReturnQName() == null) {
/* 1733 */         String ns = "";
/*      */ 
/* 1735 */         if (desc.getStyle() != Style.RPC) {
/* 1736 */           ns = getServiceDesc().getDefaultNamespace();
/*      */ 
/* 1738 */           if ((ns == null) || ("".equals(ns))) {
/* 1739 */             ns = "http://ws.apache.org/axis/defaultNS";
/*      */           }
/*      */         }
/*      */ 
/* 1743 */         retParam.setQName(new QName(ns, desc.getName() + "Return"));
/*      */       } else {
/* 1745 */         retParam.setQName(desc.getReturnQName());
/*      */       }
/*      */ 
/* 1748 */       retParam.setTypeQName(desc.getReturnType());
/* 1749 */       retParam.setMode(2);
/* 1750 */       retParam.setIsReturn(true);
/* 1751 */       retParam.setJavaType(desc.getReturnClass());
/* 1752 */       String returnPartName = writePartToMessage(def, msg, false, retParam);
/* 1753 */       bodyParts.add(returnPartName);
/*      */ 
/* 1756 */       for (int i = 0; i < parameters.size(); i++) {
/* 1757 */         ParameterDesc parameter = (ParameterDesc)parameters.get(i);
/* 1758 */         if ((!parameter.isInHeader()) && (!parameter.isOutHeader())) {
/* 1759 */           String partName = writePartToMessage(def, msg, false, parameter);
/* 1760 */           bodyParts.add(partName);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1767 */     if (headers)
/*      */     {
/* 1769 */       List extensibilityElements = bindop.getBindingOutput().getExtensibilityElements();
/* 1770 */       for (int i = 0; i < extensibilityElements.size(); i++)
/*      */       {
/* 1772 */         Object ele = extensibilityElements.get(i);
/* 1773 */         if (!(ele instanceof SOAPBodyImpl))
/*      */           continue;
/* 1775 */         SOAPBodyImpl soapBody = (SOAPBodyImpl)ele;
/* 1776 */         soapBody.setParts(bodyParts);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1781 */     return msg;
/*      */   }
/*      */ 
/*      */   protected Message writeFaultMessage(Definition def, FaultDesc exception)
/*      */     throws WSDLException, AxisFault
/*      */   {
/* 1796 */     String pkgAndClsName = exception.getClassName();
/* 1797 */     String clsName = pkgAndClsName.substring(pkgAndClsName.lastIndexOf('.') + 1, pkgAndClsName.length());
/*      */ 
/* 1802 */     exception.setName(clsName);
/*      */ 
/* 1806 */     Message msg = (Message)this.exceptionMsg.get(pkgAndClsName);
/*      */ 
/* 1808 */     if (msg == null) {
/* 1809 */       msg = def.createMessage();
/*      */ 
/* 1811 */       QName qName = createMessageName(def, clsName);
/*      */ 
/* 1813 */       msg.setQName(qName);
/* 1814 */       msg.setUndefined(false);
/*      */ 
/* 1816 */       ArrayList parameters = exception.getParameters();
/*      */ 
/* 1818 */       if (parameters != null) {
/* 1819 */         for (int i = 0; i < parameters.size(); i++) {
/* 1820 */           ParameterDesc parameter = (ParameterDesc)parameters.get(i);
/*      */ 
/* 1822 */           writePartToMessage(def, msg, true, parameter);
/*      */         }
/*      */       }
/*      */ 
/* 1826 */       this.exceptionMsg.put(pkgAndClsName, msg);
/*      */     }
/*      */ 
/* 1829 */     return msg;
/*      */   }
/*      */ 
/*      */   public String writePartToMessage(Definition def, Message msg, boolean request, ParameterDesc param)
/*      */     throws WSDLException, AxisFault
/*      */   {
/* 1848 */     if ((param == null) || (param.getJavaType() == Void.TYPE)) {
/* 1849 */       return null;
/*      */     }
/*      */ 
/* 1854 */     if ((request) && (param.getMode() == 2)) {
/* 1855 */       return null;
/*      */     }
/*      */ 
/* 1858 */     if ((!request) && (param.getMode() == 1)) {
/* 1859 */       return null;
/*      */     }
/*      */ 
/* 1863 */     Part part = def.createPart();
/*      */ 
/* 1865 */     if (param.getDocumentation() != null) {
/* 1866 */       part.setDocumentationElement(createDocumentationElement(param.getDocumentation()));
/*      */     }
/*      */ 
/* 1876 */     Class javaType = param.getJavaType();
/*      */ 
/* 1878 */     if ((param.getMode() != 1) && (!param.getIsReturn()))
/*      */     {
/* 1880 */       javaType = JavaUtils.getHolderValueType(javaType);
/*      */     }
/*      */ 
/* 1883 */     if ((this.use == Use.ENCODED) || (this.style == Style.RPC))
/*      */     {
/* 1887 */       QName typeQName = param.getTypeQName();
/*      */ 
/* 1889 */       if (javaType != null) {
/* 1890 */         typeQName = this.types.writeTypeAndSubTypeForPart(javaType, typeQName);
/*      */       }
/*      */ 
/* 1894 */       if (typeQName != null) {
/* 1895 */         part.setName(param.getName());
/* 1896 */         part.setTypeName(typeQName);
/* 1897 */         msg.addPart(part);
/*      */       }
/* 1899 */     } else if (this.use == Use.LITERAL)
/*      */     {
/* 1904 */       QName qname = param.getQName();
/*      */ 
/* 1906 */       if (param.getTypeQName() == null) {
/* 1907 */         log.warn(Messages.getMessage("registerTypeMappingFor01", param.getJavaType().getName()));
/*      */ 
/* 1909 */         QName qName = this.types.writeTypeForPart(param.getJavaType(), null);
/* 1910 */         if (qName != null)
/* 1911 */           param.setTypeQName(qName);
/*      */         else {
/* 1913 */           param.setTypeQName(Constants.XSD_ANYTYPE);
/*      */         }
/*      */       }
/*      */ 
/* 1917 */       if (param.getTypeQName().getNamespaceURI().equals("")) {
/* 1918 */         param.setTypeQName(new QName(this.intfNS, param.getTypeQName().getLocalPart()));
/*      */       }
/*      */ 
/* 1922 */       if (param.getQName().getNamespaceURI().equals("")) {
/* 1923 */         qname = new QName(this.intfNS, param.getQName().getLocalPart());
/*      */ 
/* 1925 */         param.setQName(qname);
/*      */       }
/*      */ 
/* 1929 */       ArrayList names = (ArrayList)this.usedElementNames.get(qname.getNamespaceURI());
/*      */ 
/* 1931 */       if (names == null) {
/* 1932 */         names = new ArrayList(1);
/* 1933 */         this.usedElementNames.put(qname.getNamespaceURI(), names);
/*      */       }
/* 1935 */       else if (names.contains(qname.getLocalPart())) {
/* 1936 */         qname = new QName(qname.getNamespaceURI(), JavaUtils.getUniqueValue(names, qname.getLocalPart()));
/*      */       }
/*      */ 
/* 1939 */       names.add(qname.getLocalPart());
/*      */ 
/* 1941 */       this.types.writeElementDecl(qname, param.getJavaType(), param.getTypeQName(), false, param.getItemQName());
/*      */ 
/* 1947 */       part.setName(param.getName());
/* 1948 */       part.setElementName(qname);
/* 1949 */       msg.addPart(part);
/*      */     }
/*      */ 
/* 1953 */     return param.getName();
/*      */   }
/*      */ 
/*      */   protected QName createMessageName(Definition def, String methodName)
/*      */   {
/* 1969 */     QName qName = new QName(this.intfNS, methodName);
/*      */ 
/* 1972 */     int messageNumber = 1;
/*      */ 
/* 1974 */     while (def.getMessage(qName) != null) {
/* 1975 */       StringBuffer namebuf = new StringBuffer(methodName);
/*      */ 
/* 1977 */       namebuf.append(messageNumber);
/*      */ 
/* 1979 */       qName = new QName(this.intfNS, namebuf.toString());
/*      */ 
/* 1981 */       messageNumber++;
/*      */     }
/*      */ 
/* 1984 */     return qName;
/*      */   }
/*      */ 
/*      */   protected void prettyDocumentToFile(Document doc, String filename)
/*      */     throws IOException
/*      */   {
/* 1997 */     FileOutputStream fos = new FileOutputStream(new File(filename));
/*      */ 
/* 1999 */     XMLUtils.PrettyDocumentToStream(doc, fos);
/* 2000 */     fos.close();
/*      */   }
/*      */ 
/*      */   public Class getCls()
/*      */   {
/* 2011 */     return this.cls;
/*      */   }
/*      */ 
/*      */   public void setCls(Class cls)
/*      */   {
/* 2020 */     this.cls = cls;
/*      */   }
/*      */ 
/*      */   public void setClsSmart(Class cls, String location)
/*      */   {
/* 2031 */     if ((cls == null) || (location == null)) {
/* 2032 */       return;
/*      */     }
/*      */ 
/* 2036 */     if (location.lastIndexOf('/') > 0)
/* 2037 */       location = location.substring(location.lastIndexOf('/') + 1);
/* 2038 */     else if (location.lastIndexOf('\\') > 0) {
/* 2039 */       location = location.substring(location.lastIndexOf('\\') + 1);
/*      */     }
/*      */ 
/* 2043 */     Constructor[] constructors = cls.getDeclaredConstructors();
/*      */ 
/* 2045 */     Class intf = null;
/*      */ 
/* 2047 */     for (int i = 0; (i < constructors.length) && (intf == null); i++) {
/* 2048 */       Class[] parms = constructors[i].getParameterTypes();
/*      */ 
/* 2053 */       if ((parms.length != 1) || (!parms[0].isInterface()) || (parms[0].getName() == null) || (!Types.getLocalNameFromFullName(parms[0].getName()).equals(location)))
/*      */       {
/*      */         continue;
/*      */       }
/* 2057 */       intf = parms[0];
/*      */     }
/*      */ 
/* 2061 */     if (intf != null) {
/* 2062 */       setCls(intf);
/*      */ 
/* 2064 */       if (this.implCls == null)
/* 2065 */         setImplCls(cls);
/*      */     }
/*      */     else {
/* 2068 */       setCls(cls);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setCls(String className)
/*      */     throws ClassNotFoundException
/*      */   {
/* 2079 */     this.cls = ClassUtils.forName(className);
/*      */   }
/*      */ 
/*      */   public Class getImplCls()
/*      */   {
/* 2088 */     return this.implCls;
/*      */   }
/*      */ 
/*      */   public void setImplCls(Class implCls)
/*      */   {
/* 2097 */     this.implCls = implCls;
/*      */   }
/*      */ 
/*      */   public void setImplCls(String className)
/*      */   {
/*      */     try
/*      */     {
/* 2108 */       this.implCls = ClassUtils.forName(className);
/*      */     } catch (Exception ex) {
/* 2110 */       ex.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getIntfNamespace()
/*      */   {
/* 2120 */     return this.intfNS;
/*      */   }
/*      */ 
/*      */   public void setIntfNamespace(String ns)
/*      */   {
/* 2129 */     this.intfNS = ns;
/*      */   }
/*      */ 
/*      */   public String getImplNamespace()
/*      */   {
/* 2138 */     return this.implNS;
/*      */   }
/*      */ 
/*      */   public void setImplNamespace(String ns)
/*      */   {
/* 2147 */     this.implNS = ns;
/*      */   }
/*      */ 
/*      */   public Vector getAllowedMethods()
/*      */   {
/* 2156 */     return this.allowedMethods;
/*      */   }
/*      */ 
/*      */   public void setAllowedMethods(String text)
/*      */   {
/* 2166 */     if (text != null) {
/* 2167 */       StringTokenizer tokenizer = new StringTokenizer(text, " ,+");
/*      */ 
/* 2169 */       if (this.allowedMethods == null) {
/* 2170 */         this.allowedMethods = new Vector();
/*      */       }
/*      */ 
/* 2173 */       while (tokenizer.hasMoreTokens())
/* 2174 */         this.allowedMethods.add(tokenizer.nextToken());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setAllowedMethods(Vector allowedMethods)
/*      */   {
/* 2186 */     if (this.allowedMethods == null) {
/* 2187 */       this.allowedMethods = new Vector();
/*      */     }
/*      */ 
/* 2190 */     this.allowedMethods.addAll(allowedMethods);
/*      */   }
/*      */ 
/*      */   public boolean getUseInheritedMethods()
/*      */   {
/* 2199 */     return this.useInheritedMethods;
/*      */   }
/*      */ 
/*      */   public void setUseInheritedMethods(boolean useInheritedMethods)
/*      */   {
/* 2208 */     this.useInheritedMethods = useInheritedMethods;
/*      */   }
/*      */ 
/*      */   public void setDisallowedMethods(Vector disallowedMethods)
/*      */   {
/* 2218 */     if (this.disallowedMethods == null) {
/* 2219 */       this.disallowedMethods = new Vector();
/*      */     }
/*      */ 
/* 2222 */     this.disallowedMethods.addAll(disallowedMethods);
/*      */   }
/*      */ 
/*      */   public void setDisallowedMethods(String text)
/*      */   {
/* 2232 */     if (text != null) {
/* 2233 */       StringTokenizer tokenizer = new StringTokenizer(text, " ,+");
/*      */ 
/* 2235 */       if (this.disallowedMethods == null) {
/* 2236 */         this.disallowedMethods = new Vector();
/*      */       }
/*      */ 
/* 2239 */       this.disallowedMethods = new Vector();
/*      */ 
/* 2241 */       while (tokenizer.hasMoreTokens())
/* 2242 */         this.disallowedMethods.add(tokenizer.nextToken());
/*      */     }
/*      */   }
/*      */ 
/*      */   public Vector getDisallowedMethods()
/*      */   {
/* 2253 */     return this.disallowedMethods;
/*      */   }
/*      */ 
/*      */   public void setStopClasses(ArrayList stopClasses)
/*      */   {
/* 2264 */     if (this.stopClasses == null) {
/* 2265 */       this.stopClasses = new ArrayList();
/*      */     }
/*      */ 
/* 2268 */     this.stopClasses.addAll(stopClasses);
/*      */   }
/*      */ 
/*      */   public void setStopClasses(String text)
/*      */   {
/* 2279 */     if (text != null) {
/* 2280 */       StringTokenizer tokenizer = new StringTokenizer(text, " ,+");
/*      */ 
/* 2282 */       if (this.stopClasses == null) {
/* 2283 */         this.stopClasses = new ArrayList();
/*      */       }
/*      */ 
/* 2286 */       while (tokenizer.hasMoreTokens())
/* 2287 */         this.stopClasses.add(tokenizer.nextToken());
/*      */     }
/*      */   }
/*      */ 
/*      */   public ArrayList getStopClasses()
/*      */   {
/* 2298 */     return this.stopClasses;
/*      */   }
/*      */ 
/*      */   public Map getNamespaceMap()
/*      */   {
/* 2307 */     return this.namespaces;
/*      */   }
/*      */ 
/*      */   public void setNamespaceMap(Map map)
/*      */   {
/* 2317 */     if (map != null)
/* 2318 */       this.namespaces.putAll(map);
/*      */   }
/*      */ 
/*      */   public String getInputWSDL()
/*      */   {
/* 2328 */     return this.inputWSDL;
/*      */   }
/*      */ 
/*      */   public void setInputWSDL(String inputWSDL)
/*      */   {
/* 2337 */     this.inputWSDL = inputWSDL;
/*      */   }
/*      */ 
/*      */   public String getInputSchema()
/*      */   {
/* 2344 */     return this.inputSchema;
/*      */   }
/*      */ 
/*      */   public void setInputSchema(String inputSchema)
/*      */   {
/* 2353 */     this.inputSchema = inputSchema;
/*      */   }
/*      */ 
/*      */   public String getLocationUrl()
/*      */   {
/* 2362 */     return this.locationUrl;
/*      */   }
/*      */ 
/*      */   public void setLocationUrl(String locationUrl)
/*      */   {
/* 2371 */     this.locationUrl = locationUrl;
/*      */   }
/*      */ 
/*      */   public String getImportUrl()
/*      */   {
/* 2380 */     return this.importUrl;
/*      */   }
/*      */ 
/*      */   public void setImportUrl(String importUrl)
/*      */   {
/* 2391 */     this.importUrl = importUrl;
/*      */   }
/*      */ 
/*      */   public String getServicePortName()
/*      */   {
/* 2400 */     return this.servicePortName;
/*      */   }
/*      */ 
/*      */   public void setServicePortName(String servicePortName)
/*      */   {
/* 2409 */     this.servicePortName = servicePortName;
/*      */   }
/*      */ 
/*      */   public String getServiceElementName()
/*      */   {
/* 2418 */     return this.serviceElementName;
/*      */   }
/*      */ 
/*      */   public void setServiceElementName(String serviceElementName)
/*      */   {
/* 2427 */     this.serviceElementName = serviceElementName;
/*      */   }
/*      */ 
/*      */   public String getPortTypeName()
/*      */   {
/* 2436 */     return this.portTypeName;
/*      */   }
/*      */ 
/*      */   public void setPortTypeName(String portTypeName)
/*      */   {
/* 2445 */     this.portTypeName = portTypeName;
/*      */   }
/*      */ 
/*      */   public String getBindingName()
/*      */   {
/* 2454 */     return this.bindingName;
/*      */   }
/*      */ 
/*      */   public void setBindingName(String bindingName)
/*      */   {
/* 2463 */     this.bindingName = bindingName;
/*      */   }
/*      */ 
/*      */   public String getTargetService()
/*      */   {
/* 2472 */     return this.targetService;
/*      */   }
/*      */ 
/*      */   public void setTargetService(String targetService)
/*      */   {
/* 2481 */     this.targetService = targetService;
/*      */   }
/*      */ 
/*      */   public String getDescription()
/*      */   {
/* 2490 */     return this.description;
/*      */   }
/*      */ 
/*      */   public void setDescription(String description)
/*      */   {
/* 2499 */     this.description = description;
/*      */   }
/*      */ 
/*      */   public String getSoapAction()
/*      */   {
/* 2508 */     return this.soapAction;
/*      */   }
/*      */ 
/*      */   public void setSoapAction(String value)
/*      */   {
/* 2517 */     this.soapAction = value;
/*      */   }
/*      */ 
/*      */   public TypeMapping getTypeMapping()
/*      */   {
/* 2526 */     return this.tm;
/*      */   }
/*      */ 
/*      */   public void setTypeMapping(TypeMapping tm)
/*      */   {
/* 2535 */     this.tm = tm;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public TypeMapping getDefaultTypeMapping()
/*      */   {
/* 2544 */     return (TypeMapping)this.tmr.getDefaultTypeMapping();
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setDefaultTypeMapping(TypeMapping tm)
/*      */   {
/* 2553 */     this.tmr.registerDefault(tm);
/*      */   }
/*      */ 
/*      */   public void setTypeMappingRegistry(TypeMappingRegistry tmr)
/*      */   {
/* 2560 */     this.tmr = tmr;
/*      */   }
/*      */ 
/*      */   public Style getStyle()
/*      */   {
/* 2568 */     return this.style;
/*      */   }
/*      */ 
/*      */   public void setStyle(String value)
/*      */   {
/* 2583 */     setStyle(Style.getStyle(value));
/*      */   }
/*      */ 
/*      */   public void setStyle(Style value)
/*      */   {
/* 2593 */     this.style = value;
/*      */ 
/* 2595 */     if (this.style.equals(Style.WRAPPED))
/* 2596 */       setUse(Use.LITERAL);
/*      */   }
/*      */ 
/*      */   public Use getUse()
/*      */   {
/* 2606 */     return this.use;
/*      */   }
/*      */ 
/*      */   public void setUse(String value)
/*      */   {
/* 2620 */     this.use = Use.getUse(value);
/*      */   }
/*      */ 
/*      */   public void setUse(Use value)
/*      */   {
/* 2629 */     this.use = value;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setMode(int mode)
/*      */   {
/* 2640 */     if (mode == 0) {
/* 2641 */       setStyle(Style.RPC);
/* 2642 */       setUse(Use.ENCODED);
/* 2643 */     } else if (mode == 1) {
/* 2644 */       setStyle(Style.DOCUMENT);
/* 2645 */       setUse(Use.LITERAL);
/* 2646 */     } else if (mode == 2) {
/* 2647 */       setStyle(Style.WRAPPED);
/* 2648 */       setUse(Use.LITERAL);
/*      */     }
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public int getMode()
/*      */   {
/* 2660 */     if (this.style == Style.RPC)
/* 2661 */       return 0;
/* 2662 */     if (this.style == Style.DOCUMENT)
/* 2663 */       return 1;
/* 2664 */     if (this.style == Style.WRAPPED) {
/* 2665 */       return 2;
/*      */     }
/*      */ 
/* 2668 */     return -1;
/*      */   }
/*      */ 
/*      */   public ServiceDesc getServiceDesc()
/*      */   {
/* 2677 */     return this.serviceDesc;
/*      */   }
/*      */ 
/*      */   public void setServiceDesc(ServiceDesc serviceDesc)
/*      */   {
/* 2686 */     this.serviceDesc = serviceDesc;
/*      */   }
/*      */ 
/*      */   public Class[] getExtraClasses()
/*      */   {
/* 2695 */     return this.extraClasses;
/*      */   }
/*      */ 
/*      */   public void setExtraClasses(Class[] extraClasses)
/*      */   {
/* 2705 */     this.extraClasses = extraClasses;
/*      */   }
/*      */ 
/*      */   public void setExtraClasses(String text)
/*      */     throws ClassNotFoundException
/*      */   {
/* 2718 */     ArrayList clsList = new ArrayList();
/*      */ 
/* 2720 */     if (text != null) {
/* 2721 */       StringTokenizer tokenizer = new StringTokenizer(text, " ,");
/*      */ 
/* 2723 */       while (tokenizer.hasMoreTokens()) {
/* 2724 */         String clsName = tokenizer.nextToken();
/*      */ 
/* 2727 */         Class cls = ClassUtils.forName(clsName);
/*      */ 
/* 2729 */         clsList.add(cls);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2735 */     int startOffset = 0;
/*      */     Class[] ec;
/* 2737 */     if (this.extraClasses != null) {
/* 2738 */       Class[] ec = new Class[clsList.size() + this.extraClasses.length];
/*      */ 
/* 2741 */       for (int i = 0; i < this.extraClasses.length; i++) {
/* 2742 */         Class c = this.extraClasses[i];
/*      */ 
/* 2744 */         ec[i] = c;
/*      */       }
/* 2746 */       startOffset = this.extraClasses.length;
/*      */     } else {
/* 2748 */       ec = new Class[clsList.size()];
/*      */     }
/*      */ 
/* 2752 */     for (int i = 0; i < clsList.size(); i++) {
/* 2753 */       Class c = (Class)clsList.get(i);
/*      */ 
/* 2755 */       ec[(startOffset + i)] = c;
/*      */     }
/*      */ 
/* 2759 */     this.extraClasses = ec;
/*      */   }
/*      */ 
/*      */   public void setEmitAllTypes(boolean emitAllTypes) {
/* 2763 */     this.emitAllTypes = emitAllTypes;
/*      */   }
/*      */ 
/*      */   public String getVersionMessage()
/*      */   {
/* 2772 */     return this.versionMessage;
/*      */   }
/*      */ 
/*      */   public void setVersionMessage(String versionMessage)
/*      */   {
/* 2783 */     this.versionMessage = versionMessage;
/*      */   }
/*      */ 
/*      */   public HashMap getQName2ClassMap()
/*      */   {
/* 2791 */     return this.qName2ClassMap;
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.fromJava.Emitter
 * JD-Core Version:    0.6.0
 */