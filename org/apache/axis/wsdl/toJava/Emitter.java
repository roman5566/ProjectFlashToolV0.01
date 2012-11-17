/*      */ package org.apache.axis.wsdl.toJava;
/*      */ 
/*      */ import java.io.FileInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Properties;
/*      */ import java.util.Vector;
/*      */ import javax.wsdl.WSDLException;
/*      */ import javax.xml.namespace.QName;
/*      */ import javax.xml.parsers.ParserConfigurationException;
/*      */ import org.apache.axis.constants.Scope;
/*      */ import org.apache.axis.description.ServiceDesc;
/*      */ import org.apache.axis.encoding.TypeMapping;
/*      */ import org.apache.axis.encoding.TypeMappingRegistryImpl;
/*      */ import org.apache.axis.i18n.Messages;
/*      */ import org.apache.axis.utils.ClassUtils;
/*      */ import org.apache.axis.utils.JavaUtils;
/*      */ import org.apache.axis.wsdl.gen.GeneratorFactory;
/*      */ import org.apache.axis.wsdl.gen.Parser;
/*      */ import org.apache.axis.wsdl.symbolTable.BaseTypeMapping;
/*      */ import org.apache.axis.wsdl.symbolTable.SymTabEntry;
/*      */ import org.apache.axis.wsdl.symbolTable.SymbolTable;
/*      */ import org.w3c.dom.Document;
/*      */ import org.xml.sax.SAXException;
/*      */ 
/*      */ public class Emitter extends Parser
/*      */ {
/*      */   public static final String DEFAULT_NSTOPKG_FILE = "NStoPkg.properties";
/*   64 */   protected HashMap namespaceMap = new HashMap();
/*      */ 
/*   67 */   protected String typeMappingVersion = "1.2";
/*      */ 
/*   70 */   protected BaseTypeMapping baseTypeMapping = null;
/*      */ 
/*   73 */   protected Namespaces namespaces = null;
/*      */ 
/*   76 */   protected String NStoPkgFilename = null;
/*      */ 
/*   79 */   private boolean bEmitServer = false;
/*      */ 
/*   82 */   private boolean bDeploySkeleton = false;
/*      */ 
/*   85 */   private boolean bEmitTestCase = false;
/*      */ 
/*   88 */   private boolean bGenerateAll = false;
/*      */ 
/*   91 */   private boolean bHelperGeneration = false;
/*      */ 
/*   93 */   private boolean bBuildFileGeneration = false;
/*      */ 
/*   95 */   private boolean typeCollisionProtection = true;
/*      */ 
/*   98 */   private boolean allowInvalidURL = false;
/*      */ 
/*  101 */   private String packageName = null;
/*      */ 
/*  104 */   private Scope scope = null;
/*      */ 
/*  107 */   private GeneratedFileInfo fileInfo = new GeneratedFileInfo();
/*      */ 
/*  110 */   private HashMap delayedNamespacesMap = new HashMap();
/*      */ 
/*  113 */   private String outputDir = null;
/*      */ 
/*  120 */   protected List nsIncludes = new ArrayList();
/*      */ 
/*  127 */   protected List nsExcludes = new ArrayList();
/*      */ 
/*  132 */   protected List properties = new ArrayList();
/*      */ 
/*  139 */   private String implementationClassName = null;
/*      */ 
/*  142 */   private TypeMapping defaultTM = null;
/*      */ 
/*  144 */   private TypeMappingRegistryImpl tmr = new TypeMappingRegistryImpl();
/*      */   private HashMap qName2ClassMap;
/*      */   private ServiceDesc serviceDesc;
/*      */   private boolean isDeploy;
/*      */ 
/*      */   public Emitter()
/*      */   {
/*  159 */     setFactory(new JavaGeneratorFactory(this));
/*      */   }
/*      */ 
/*      */   public void setServerSide(boolean value)
/*      */   {
/*  173 */     this.bEmitServer = value;
/*      */   }
/*      */ 
/*      */   public boolean isServerSide()
/*      */   {
/*  182 */     return this.bEmitServer;
/*      */   }
/*      */ 
/*      */   public void setSkeletonWanted(boolean value)
/*      */   {
/*  191 */     this.bDeploySkeleton = value;
/*      */   }
/*      */ 
/*      */   public boolean isSkeletonWanted()
/*      */   {
/*  200 */     return this.bDeploySkeleton;
/*      */   }
/*      */ 
/*      */   public void setHelperWanted(boolean value)
/*      */   {
/*  209 */     this.bHelperGeneration = value;
/*      */   }
/*      */ 
/*      */   public boolean isHelperWanted()
/*      */   {
/*  218 */     return this.bHelperGeneration;
/*      */   }
/*      */ 
/*      */   public void setTestCaseWanted(boolean value)
/*      */   {
/*  227 */     this.bEmitTestCase = value;
/*      */   }
/*      */ 
/*      */   public boolean isTestCaseWanted()
/*      */   {
/*  236 */     return this.bEmitTestCase;
/*      */   }
/*      */ 
/*      */   public boolean isBuildFileWanted()
/*      */   {
/*  244 */     return this.bBuildFileGeneration;
/*      */   }
/*      */ 
/*      */   public void setBuildFileWanted(boolean value)
/*      */   {
/*  252 */     this.bBuildFileGeneration = value;
/*      */   }
/*      */ 
/*      */   public void setAllWanted(boolean all)
/*      */   {
/*  265 */     this.bGenerateAll = all;
/*      */   }
/*      */ 
/*      */   public boolean isAllWanted()
/*      */   {
/*  274 */     return this.bGenerateAll;
/*      */   }
/*      */ 
/*      */   public Namespaces getNamespaces()
/*      */   {
/*  283 */     return this.namespaces;
/*      */   }
/*      */ 
/*      */   public void setOutputDir(String outputDir)
/*      */   {
/*  292 */     this.outputDir = outputDir;
/*      */   }
/*      */ 
/*      */   public String getOutputDir()
/*      */   {
/*  301 */     return this.outputDir;
/*      */   }
/*      */ 
/*      */   public String getPackageName()
/*      */   {
/*  310 */     return this.packageName;
/*      */   }
/*      */ 
/*      */   public void setPackageName(String packageName)
/*      */   {
/*  319 */     this.packageName = packageName;
/*      */   }
/*      */ 
/*      */   public void setScope(Scope scope)
/*      */   {
/*  331 */     this.scope = scope;
/*      */   }
/*      */ 
/*      */   public Scope getScope()
/*      */   {
/*  340 */     return this.scope;
/*      */   }
/*      */ 
/*      */   public void setNStoPkg(String NStoPkgFilename)
/*      */   {
/*  350 */     if (NStoPkgFilename != null)
/*  351 */       this.NStoPkgFilename = NStoPkgFilename;
/*      */   }
/*      */ 
/*      */   public void setNamespaceMap(HashMap map)
/*      */   {
/*  361 */     this.delayedNamespacesMap = map;
/*      */   }
/*      */ 
/*      */   public HashMap getNamespaceMap()
/*      */   {
/*  370 */     return this.delayedNamespacesMap;
/*      */   }
/*      */ 
/*      */   public void setNamespaceIncludes(List nsIncludes)
/*      */   {
/*  377 */     this.nsIncludes = nsIncludes;
/*      */   }
/*      */ 
/*      */   public List getNamespaceIncludes()
/*      */   {
/*  384 */     return this.nsIncludes;
/*      */   }
/*      */ 
/*      */   public void setNamespaceExcludes(List nsExcludes)
/*      */   {
/*  391 */     this.nsExcludes = nsExcludes;
/*      */   }
/*      */ 
/*      */   public List getNamespaceExcludes()
/*      */   {
/*  398 */     return this.nsExcludes;
/*      */   }
/*      */ 
/*      */   public void setProperties(List properties)
/*      */   {
/*  405 */     this.properties = properties;
/*      */   }
/*      */ 
/*      */   public List getProperties()
/*      */   {
/*  412 */     return this.properties;
/*      */   }
/*      */ 
/*      */   public TypeMapping getDefaultTypeMapping()
/*      */   {
/*  421 */     if (this.defaultTM == null) {
/*  422 */       this.defaultTM = ((TypeMapping)this.tmr.getTypeMapping("http://schemas.xmlsoap.org/soap/encoding/"));
/*      */     }
/*      */ 
/*  425 */     return this.defaultTM;
/*      */   }
/*      */ 
/*      */   public void setDefaultTypeMapping(TypeMapping defaultTM)
/*      */   {
/*  434 */     this.defaultTM = defaultTM;
/*      */   }
/*      */ 
/*      */   public void setFactory(String factory) {
/*      */     try {
/*  445 */       Class clazz = ClassUtils.forName(factory);
/*      */       GeneratorFactory genFac;
/*      */       try {
/*  448 */         Constructor ctor = clazz.getConstructor(new Class[] { getClass() });
/*      */ 
/*  451 */         genFac = (GeneratorFactory)ctor.newInstance(new Object[] { this });
/*      */       }
/*      */       catch (NoSuchMethodException ex)
/*      */       {
/*      */         GeneratorFactory genFac;
/*  454 */         genFac = (GeneratorFactory)clazz.newInstance();
/*      */       }
/*      */ 
/*  457 */       setFactory(genFac);
/*      */     } catch (Exception ex) {
/*  459 */       ex.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   public GeneratedFileInfo getGeneratedFileInfo()
/*      */   {
/*  476 */     return this.fileInfo;
/*      */   }
/*      */ 
/*      */   public List getGeneratedClassNames()
/*      */   {
/*  485 */     return this.fileInfo.getClassNames();
/*      */   }
/*      */ 
/*      */   public List getGeneratedFileNames()
/*      */   {
/*  494 */     return this.fileInfo.getFileNames();
/*      */   }
/*      */ 
/*      */   public String getPackage(String namespace)
/*      */   {
/*  504 */     return this.namespaces.getCreate(namespace);
/*      */   }
/*      */ 
/*      */   public String getPackage(QName qName)
/*      */   {
/*  514 */     return getPackage(qName.getNamespaceURI());
/*      */   }
/*      */ 
/*      */   public String getJavaName(QName qName)
/*      */   {
/*  527 */     if (qName.getLocalPart().indexOf("[") > 0) {
/*  528 */       String localPart = qName.getLocalPart().substring(0, qName.getLocalPart().indexOf("["));
/*      */ 
/*  530 */       QName eQName = new QName(qName.getNamespaceURI(), localPart);
/*      */ 
/*  532 */       return getJavaName(eQName) + "[]";
/*      */     }
/*      */ 
/*  536 */     if (qName.getNamespaceURI().equalsIgnoreCase("java")) {
/*  537 */       return qName.getLocalPart();
/*      */     }
/*      */ 
/*  541 */     String fullJavaName = getFactory().getBaseTypeMapping().getBaseName(qName);
/*      */ 
/*  544 */     if (fullJavaName != null) {
/*  545 */       return fullJavaName;
/*      */     }
/*      */ 
/*  548 */     fullJavaName = getJavaNameHook(qName);
/*  549 */     if (fullJavaName != null) {
/*  550 */       return fullJavaName;
/*      */     }
/*      */ 
/*  553 */     String pkg = getPackage(qName.getNamespaceURI());
/*      */ 
/*  555 */     if ((pkg != null) && (pkg.length() > 0)) {
/*  556 */       fullJavaName = pkg + "." + Utils.xmlNameToJavaClass(qName.getLocalPart());
/*      */     }
/*      */     else {
/*  559 */       fullJavaName = Utils.xmlNameToJavaClass(qName.getLocalPart());
/*      */     }
/*      */ 
/*  562 */     return fullJavaName;
/*      */   }
/*      */   protected String getJavaNameHook(QName qname) {
/*  565 */     return null;
/*      */   }
/*      */ 
/*      */   public String getJavaVariableName(QName typeQName, QName xmlName, boolean isElement)
/*      */   {
/*  574 */     String javaName = getJavaVariableNameHook(typeQName, xmlName, isElement);
/*  575 */     if (javaName == null) {
/*  576 */       String elemName = Utils.getLastLocalPart(xmlName.getLocalPart());
/*  577 */       javaName = Utils.xmlNameToJava(elemName);
/*      */     }
/*  579 */     return javaName;
/*      */   }
/*      */ 
/*      */   protected String getJavaVariableNameHook(QName typeQName, QName xmlName, boolean isElement) {
/*  583 */     return null;
/*      */   }
/*      */ 
/*      */   public void run(String wsdlURL)
/*      */     throws Exception
/*      */   {
/*  598 */     setup();
/*  599 */     super.run(wsdlURL);
/*      */   }
/*      */ 
/*      */   public void run(String context, Document doc)
/*      */     throws IOException, SAXException, WSDLException, ParserConfigurationException
/*      */   {
/*  619 */     setup();
/*  620 */     super.run(context, doc);
/*      */   }
/*      */ 
/*      */   private void setup()
/*      */     throws IOException
/*      */   {
/*  630 */     if (this.baseTypeMapping == null) {
/*  631 */       setTypeMappingVersion(this.typeMappingVersion);
/*      */     }
/*      */ 
/*  634 */     getFactory().setBaseTypeMapping(this.baseTypeMapping);
/*      */ 
/*  636 */     this.namespaces = new Namespaces(this.outputDir);
/*      */ 
/*  638 */     if (this.packageName != null) {
/*  639 */       this.namespaces.setDefaultPackage(this.packageName);
/*      */     }
/*      */     else
/*      */     {
/*  645 */       getNStoPkgFromPropsFile(this.namespaces);
/*      */ 
/*  647 */       if (this.delayedNamespacesMap != null)
/*  648 */         this.namespaces.putAll(this.delayedNamespacesMap);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void sanityCheck(SymbolTable symbolTable)
/*      */   {
/*  660 */     Iterator it = symbolTable.getHashMap().values().iterator();
/*      */ 
/*  662 */     while (it.hasNext()) {
/*  663 */       Vector v = (Vector)it.next();
/*      */ 
/*  665 */       for (int i = 0; i < v.size(); i++) {
/*  666 */         SymTabEntry entry = (SymTabEntry)v.elementAt(i);
/*  667 */         String namespace = entry.getQName().getNamespaceURI();
/*  668 */         String packageName = Utils.makePackageName(namespace);
/*      */ 
/*  671 */         String localName = entry.getQName().getLocalPart();
/*      */ 
/*  673 */         if ((!localName.equals(packageName)) || (!packageName.equals(this.namespaces.getCreate(namespace)))) {
/*      */           continue;
/*      */         }
/*  676 */         packageName = packageName + "_pkg";
/*      */ 
/*  678 */         this.namespaces.put(namespace, packageName);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void getNStoPkgFromPropsFile(HashMap namespaces)
/*      */     throws IOException
/*      */   {
/*  705 */     Properties mappings = new Properties();
/*      */ 
/*  707 */     if (this.NStoPkgFilename != null) {
/*      */       try {
/*  709 */         mappings.load(new FileInputStream(this.NStoPkgFilename));
/*      */ 
/*  711 */         if (this.verbose) {
/*  712 */           System.out.println(Messages.getMessage("nsToPkgFileLoaded00", this.NStoPkgFilename));
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (Throwable t)
/*      */       {
/*  720 */         throw new IOException(Messages.getMessage("nsToPkgFileNotFound00", this.NStoPkgFilename));
/*      */       }
/*      */     }
/*      */     else {
/*      */       try
/*      */       {
/*  726 */         mappings.load(new FileInputStream("NStoPkg.properties"));
/*      */ 
/*  728 */         if (this.verbose)
/*  729 */           System.out.println(Messages.getMessage("nsToPkgFileLoaded00", "NStoPkg.properties"));
/*      */       }
/*      */       catch (Throwable t)
/*      */       {
/*      */         try
/*      */         {
/*  735 */           mappings.load(ClassUtils.getResourceAsStream(Emitter.class, "NStoPkg.properties"));
/*      */ 
/*  738 */           if (this.verbose) {
/*  739 */             System.out.println(Messages.getMessage("nsToPkgDefaultFileLoaded00", "NStoPkg.properties"));
/*      */           }
/*      */ 
/*      */         }
/*      */         catch (Throwable t1)
/*      */         {
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  753 */     Enumeration keys = mappings.propertyNames();
/*      */ 
/*  755 */     while (keys.hasMoreElements()) {
/*  756 */       String key = (String)keys.nextElement();
/*      */ 
/*  758 */       namespaces.put(key, mappings.getProperty(key));
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getTypeMappingVersion()
/*      */   {
/*  766 */     return this.typeMappingVersion;
/*      */   }
/*      */ 
/*      */   public void setTypeMappingVersion(String typeMappingVersion)
/*      */   {
/*  775 */     this.typeMappingVersion = typeMappingVersion;
/*  776 */     this.tmr.doRegisterFromVersion(typeMappingVersion);
/*  777 */     this.baseTypeMapping = new BaseTypeMapping()
/*      */     {
/*  779 */       final TypeMapping defaultTM = Emitter.this.getDefaultTypeMapping();
/*      */ 
/*      */       public String getBaseName(QName qNameIn)
/*      */       {
/*  783 */         QName qName = new QName(qNameIn.getNamespaceURI(), qNameIn.getLocalPart());
/*      */ 
/*  786 */         Class cls = this.defaultTM.getClassForQName(qName);
/*      */ 
/*  789 */         if (cls == null) {
/*  790 */           return null;
/*      */         }
/*  792 */         return JavaUtils.getTextClassName(cls.getName());
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public GeneratorFactory getWriterFactory()
/*      */   {
/*  808 */     return getFactory();
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void emit(String uri)
/*      */     throws Exception
/*      */   {
/*  819 */     run(uri);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void emit(String context, Document doc)
/*      */     throws IOException, SAXException, WSDLException, ParserConfigurationException
/*      */   {
/*  840 */     run(context, doc);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void generateServerSide(boolean value)
/*      */   {
/*  850 */     setServerSide(value);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public boolean getGenerateServerSide()
/*      */   {
/*  860 */     return isServerSide();
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void deploySkeleton(boolean value)
/*      */   {
/*  870 */     setSkeletonWanted(value);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public boolean getDeploySkeleton()
/*      */   {
/*  880 */     return isSkeletonWanted();
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setHelperGeneration(boolean value)
/*      */   {
/*  890 */     setHelperWanted(value);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public boolean getHelperGeneration()
/*      */   {
/*  900 */     return isHelperWanted();
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void generateImports(boolean generateImports)
/*      */   {
/*  910 */     setImports(generateImports);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void debug(boolean value)
/*      */   {
/*  920 */     setDebug(value);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public boolean getDebug()
/*      */   {
/*  930 */     return isDebug();
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void verbose(boolean value)
/*      */   {
/*  940 */     setVerbose(value);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public boolean getVerbose()
/*      */   {
/*  950 */     return isVerbose();
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void generateTestCase(boolean value)
/*      */   {
/*  960 */     setTestCaseWanted(value);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void generateAll(boolean all)
/*      */   {
/*  968 */     setAllWanted(all);
/*      */   }
/*      */ 
/*      */   public boolean isTypeCollisionProtection()
/*      */   {
/*  976 */     return this.typeCollisionProtection;
/*      */   }
/*      */ 
/*      */   public void setTypeCollisionProtection(boolean value)
/*      */   {
/*  984 */     this.typeCollisionProtection = value;
/*      */   }
/*      */ 
/*      */   public String getImplementationClassName()
/*      */   {
/*  992 */     return this.implementationClassName;
/*      */   }
/*      */ 
/*      */   public void setImplementationClassName(String implementationClassName)
/*      */   {
/* 1001 */     this.implementationClassName = implementationClassName;
/*      */   }
/*      */ 
/*      */   public boolean isAllowInvalidURL()
/*      */   {
/* 1008 */     return this.allowInvalidURL;
/*      */   }
/*      */ 
/*      */   public void setAllowInvalidURL(boolean allowInvalidURL)
/*      */   {
/* 1015 */     this.allowInvalidURL = allowInvalidURL;
/*      */   }
/*      */ 
/*      */   public void setQName2ClassMap(HashMap map)
/*      */   {
/* 1023 */     this.qName2ClassMap = map;
/*      */   }
/*      */ 
/*      */   public HashMap getQName2ClassMap()
/*      */   {
/* 1031 */     return this.qName2ClassMap;
/*      */   }
/*      */ 
/*      */   public ServiceDesc getServiceDesc()
/*      */   {
/* 1039 */     return this.serviceDesc;
/*      */   }
/*      */ 
/*      */   public void setServiceDesc(ServiceDesc serviceDesc)
/*      */   {
/* 1047 */     this.serviceDesc = serviceDesc;
/*      */   }
/*      */ 
/*      */   public boolean isDeploy()
/*      */   {
/* 1055 */     return this.isDeploy;
/*      */   }
/*      */ 
/*      */   public void setDeploy(boolean isDeploy)
/*      */   {
/* 1063 */     this.isDeploy = isDeploy;
/*      */   }
/*      */ 
/*      */   protected boolean doesExist(String className)
/*      */   {
/*      */     try
/*      */     {
/* 1074 */       ClassUtils.forName(className);
/*      */     } catch (ClassNotFoundException e) {
/* 1076 */       return false;
/*      */     }
/*      */ 
/* 1079 */     return true;
/*      */   }
/*      */ 
/*      */   public void setWrapArrays(boolean wrapArrays) {
/* 1083 */     this.wrapArrays = wrapArrays;
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.Emitter
 * JD-Core Version:    0.6.0
 */