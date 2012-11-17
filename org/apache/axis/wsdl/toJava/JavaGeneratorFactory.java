/*      */ package org.apache.axis.wsdl.toJava;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.Vector;
/*      */ import javax.wsdl.Binding;
/*      */ import javax.wsdl.Definition;
/*      */ import javax.wsdl.Fault;
/*      */ import javax.wsdl.Message;
/*      */ import javax.wsdl.Operation;
/*      */ import javax.wsdl.OperationType;
/*      */ import javax.wsdl.Port;
/*      */ import javax.wsdl.PortType;
/*      */ import javax.wsdl.Service;
/*      */ import javax.xml.namespace.QName;
/*      */ import javax.xml.rpc.holders.BooleanHolder;
/*      */ import org.apache.axis.components.logger.LogFactory;
/*      */ import org.apache.axis.encoding.TypeMapping;
/*      */ import org.apache.axis.utils.JavaUtils;
/*      */ import org.apache.axis.utils.Messages;
/*      */ import org.apache.axis.wsdl.gen.Generator;
/*      */ import org.apache.axis.wsdl.gen.GeneratorFactory;
/*      */ import org.apache.axis.wsdl.gen.NoopGenerator;
/*      */ import org.apache.axis.wsdl.symbolTable.BaseTypeMapping;
/*      */ import org.apache.axis.wsdl.symbolTable.BindingEntry;
/*      */ import org.apache.axis.wsdl.symbolTable.ContainedAttribute;
/*      */ import org.apache.axis.wsdl.symbolTable.Element;
/*      */ import org.apache.axis.wsdl.symbolTable.ElementDecl;
/*      */ import org.apache.axis.wsdl.symbolTable.FaultInfo;
/*      */ import org.apache.axis.wsdl.symbolTable.MessageEntry;
/*      */ import org.apache.axis.wsdl.symbolTable.Parameter;
/*      */ import org.apache.axis.wsdl.symbolTable.Parameters;
/*      */ import org.apache.axis.wsdl.symbolTable.PortTypeEntry;
/*      */ import org.apache.axis.wsdl.symbolTable.SchemaUtils;
/*      */ import org.apache.axis.wsdl.symbolTable.ServiceEntry;
/*      */ import org.apache.axis.wsdl.symbolTable.SymTabEntry;
/*      */ import org.apache.axis.wsdl.symbolTable.SymbolTable;
/*      */ import org.apache.axis.wsdl.symbolTable.Type;
/*      */ import org.apache.axis.wsdl.symbolTable.TypeEntry;
/*      */ import org.apache.commons.logging.Log;
/*      */ 
/*      */ public class JavaGeneratorFactory
/*      */   implements GeneratorFactory
/*      */ {
/*   67 */   private static final Log log_ = LogFactory.getLog(JavaGeneratorFactory.class.getName());
/*      */   protected Emitter emitter;
/*      */   protected SymbolTable symbolTable;
/*   77 */   public static String COMPLEX_TYPE_FAULT = "ComplexTypeFault";
/*      */ 
/*   80 */   public static String EXCEPTION_CLASS_NAME = "ExceptionClassName";
/*      */ 
/*   83 */   public static String EXCEPTION_DATA_TYPE = "ExceptionDataType";
/*      */   private static final String SERVICE_SUFFIX = "_Service";
/*      */   private static final String PORT_TYPE_SUFFIX = "_PortType";
/*      */   private static final String TYPE_SUFFIX = "_Type";
/*      */   private static final String ELEMENT_SUFFIX = "_Element";
/*      */   private static final String EXCEPTION_SUFFIX = "_Exception";
/*      */   private static final String BINDING_SUFFIX = "_Binding";
/*  218 */   private Writers messageWriters = new Writers();
/*      */ 
/*  239 */   private Writers portTypeWriters = new Writers();
/*      */ 
/*  261 */   protected Writers bindingWriters = new Writers();
/*      */ 
/*  284 */   protected Writers serviceWriters = new Writers();
/*      */ 
/*  307 */   private Writers typeWriters = new Writers();
/*      */ 
/*  328 */   private Writers defWriters = new Writers();
/*      */ 
/* 1577 */   BaseTypeMapping btm = null;
/*      */ 
/*      */   public JavaGeneratorFactory()
/*      */   {
/*   99 */     addGenerators();
/*      */   }
/*      */ 
/*      */   public JavaGeneratorFactory(Emitter emitter)
/*      */   {
/*  109 */     this.emitter = emitter;
/*      */ 
/*  111 */     addGenerators();
/*      */   }
/*      */ 
/*      */   public void setEmitter(Emitter emitter)
/*      */   {
/*  120 */     this.emitter = emitter;
/*      */   }
/*      */ 
/*      */   private void addGenerators()
/*      */   {
/*  128 */     addMessageGenerators();
/*  129 */     addPortTypeGenerators();
/*  130 */     addBindingGenerators();
/*  131 */     addServiceGenerators();
/*  132 */     addTypeGenerators();
/*  133 */     addDefinitionGenerators();
/*      */   }
/*      */ 
/*      */   protected void addMessageGenerators()
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void addPortTypeGenerators()
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void addBindingGenerators()
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void addServiceGenerators()
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void addTypeGenerators()
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void addDefinitionGenerators()
/*      */   {
/*  179 */     addGenerator(Definition.class, JavaDefinitionWriter.class);
/*  180 */     addGenerator(Definition.class, JavaDeployWriter.class);
/*      */ 
/*  182 */     addGenerator(Definition.class, JavaUndeployWriter.class);
/*      */ 
/*  184 */     addGenerator(Definition.class, JavaBuildFileWriter.class);
/*      */   }
/*      */ 
/*      */   public void generatorPass(Definition def, SymbolTable symbolTable)
/*      */   {
/*  200 */     this.symbolTable = symbolTable;
/*      */ 
/*  202 */     javifyNames(symbolTable);
/*  203 */     setFaultContext(symbolTable);
/*  204 */     resolveNameClashes(symbolTable);
/*  205 */     determineInterfaceNames(symbolTable);
/*      */ 
/*  207 */     if (this.emitter.isAllWanted())
/*  208 */       setAllReferencesToTrue();
/*      */     else {
/*  210 */       ignoreNonSOAPBindings(symbolTable);
/*      */     }
/*      */ 
/*  213 */     constructSignatures(symbolTable);
/*  214 */     determineIfHoldersNeeded(symbolTable);
/*      */   }
/*      */ 
/*      */   public Generator getGenerator(Message message, SymbolTable symbolTable)
/*      */   {
/*  228 */     if (include(message.getQName())) {
/*  229 */       MessageEntry mEntry = symbolTable.getMessageEntry(message.getQName());
/*  230 */       this.messageWriters.addStuff(new NoopGenerator(), mEntry, symbolTable);
/*  231 */       return this.messageWriters;
/*      */     }
/*      */ 
/*  234 */     return new NoopGenerator();
/*      */   }
/*      */ 
/*      */   public Generator getGenerator(PortType portType, SymbolTable symbolTable)
/*      */   {
/*  249 */     if (include(portType.getQName())) {
/*  250 */       PortTypeEntry ptEntry = symbolTable.getPortTypeEntry(portType.getQName());
/*      */ 
/*  252 */       this.portTypeWriters.addStuff(new NoopGenerator(), ptEntry, symbolTable);
/*  253 */       return this.portTypeWriters;
/*      */     }
/*      */ 
/*  256 */     return new NoopGenerator();
/*      */   }
/*      */ 
/*      */   public Generator getGenerator(Binding binding, SymbolTable symbolTable)
/*      */   {
/*  271 */     if (include(binding.getQName())) {
/*  272 */       Generator writer = new JavaBindingWriter(this.emitter, binding, symbolTable);
/*      */ 
/*  274 */       BindingEntry bEntry = symbolTable.getBindingEntry(binding.getQName());
/*  275 */       this.bindingWriters.addStuff(writer, bEntry, symbolTable);
/*  276 */       return this.bindingWriters;
/*      */     }
/*      */ 
/*  279 */     return new NoopGenerator();
/*      */   }
/*      */ 
/*      */   public Generator getGenerator(Service service, SymbolTable symbolTable)
/*      */   {
/*  294 */     if (include(service.getQName())) {
/*  295 */       Generator writer = new JavaServiceWriter(this.emitter, service, symbolTable);
/*      */ 
/*  297 */       ServiceEntry sEntry = symbolTable.getServiceEntry(service.getQName());
/*  298 */       this.serviceWriters.addStuff(writer, sEntry, symbolTable);
/*  299 */       return this.serviceWriters;
/*      */     }
/*      */ 
/*  302 */     return new NoopGenerator();
/*      */   }
/*      */ 
/*      */   public Generator getGenerator(TypeEntry type, SymbolTable symbolTable)
/*      */   {
/*  317 */     if (include(type.getQName())) {
/*  318 */       Generator writer = new JavaTypeWriter(this.emitter, type, symbolTable);
/*  319 */       this.typeWriters.addStuff(writer, type, symbolTable);
/*  320 */       return this.typeWriters;
/*      */     }
/*      */ 
/*  323 */     return new NoopGenerator();
/*      */   }
/*      */ 
/*      */   public Generator getGenerator(Definition definition, SymbolTable symbolTable)
/*      */   {
/*  339 */     if (include(definition.getQName())) {
/*  340 */       this.defWriters.addStuff(null, definition, symbolTable);
/*  341 */       return this.defWriters;
/*      */     }
/*      */ 
/*  344 */     return new NoopGenerator();
/*      */   }
/*      */ 
/*      */   public void addGenerator(Class wsdlClass, Class generator)
/*      */   {
/*  464 */     if (Message.class.isAssignableFrom(wsdlClass))
/*  465 */       this.messageWriters.addGenerator(generator);
/*  466 */     else if (PortType.class.isAssignableFrom(wsdlClass))
/*  467 */       this.portTypeWriters.addGenerator(generator);
/*  468 */     else if (Binding.class.isAssignableFrom(wsdlClass))
/*  469 */       this.bindingWriters.addGenerator(generator);
/*  470 */     else if (Service.class.isAssignableFrom(wsdlClass))
/*  471 */       this.serviceWriters.addGenerator(generator);
/*  472 */     else if (TypeEntry.class.isAssignableFrom(wsdlClass))
/*  473 */       this.typeWriters.addGenerator(generator);
/*  474 */     else if (Definition.class.isAssignableFrom(wsdlClass))
/*  475 */       this.defWriters.addGenerator(generator);
/*      */   }
/*      */ 
/*      */   protected void javifyNames(SymbolTable symbolTable)
/*      */   {
/*  488 */     int uniqueNum = 0;
/*  489 */     HashMap anonQNames = new HashMap();
/*  490 */     Iterator it = symbolTable.getHashMap().values().iterator();
/*      */ 
/*  492 */     while (it.hasNext()) {
/*  493 */       Vector v = (Vector)it.next();
/*      */ 
/*  495 */       for (int i = 0; i < v.size(); i++) {
/*  496 */         SymTabEntry entry = (SymTabEntry)v.elementAt(i);
/*      */ 
/*  498 */         if (entry.getName() != null)
/*      */         {
/*      */           continue;
/*      */         }
/*      */ 
/*  503 */         if ((entry instanceof TypeEntry)) {
/*  504 */           uniqueNum = javifyTypeEntryName(symbolTable, (TypeEntry)entry, anonQNames, uniqueNum);
/*      */         }
/*      */         else
/*      */         {
/*  510 */           entry.setName(this.emitter.getJavaName(entry.getQName()));
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected int javifyTypeEntryName(SymbolTable symbolTable, TypeEntry entry, HashMap anonQNames, int uniqueNum)
/*      */   {
/*  518 */     TypeEntry tEntry = entry;
/*  519 */     String dims = tEntry.getDimensions();
/*  520 */     TypeEntry refType = tEntry.getRefType();
/*  521 */     while (refType != null) {
/*  522 */       tEntry = refType;
/*  523 */       dims = dims + tEntry.getDimensions();
/*  524 */       refType = tEntry.getRefType();
/*      */     }
/*      */ 
/*  527 */     TypeEntry te = tEntry;
/*  528 */     while (te != null) {
/*  529 */       TypeEntry base = SchemaUtils.getBaseType(te, symbolTable);
/*  530 */       if (base == null) {
/*      */         break;
/*      */       }
/*  533 */       uniqueNum = javifyTypeEntryName(symbolTable, base, anonQNames, uniqueNum);
/*      */ 
/*  535 */       if ((Utils.getEnumerationBaseAndValues(te.getNode(), symbolTable) == null) && (SchemaUtils.getComplexElementExtensionBase(te.getNode(), symbolTable) == null) && (te.getContainedAttributes() == null))
/*      */       {
/*  538 */         if (!SchemaUtils.isSimpleTypeWithUnion(te.getNode())) {
/*  539 */           if (base.isSimpleType())
/*      */           {
/*  545 */             te.setSimpleType(true);
/*  546 */             te.setName(base.getName());
/*  547 */             te.setRefType(base);
/*      */           }
/*      */ 
/*  550 */           if (base.isBaseType())
/*      */           {
/*  556 */             te.setBaseType(true);
/*  557 */             te.setName(base.getName());
/*  558 */             te.setRefType(base);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  563 */       if (!te.isSimpleType()) {
/*      */         break;
/*      */       }
/*  566 */       te = base;
/*      */     }
/*      */ 
/*  571 */     if (tEntry.getName() == null) {
/*  572 */       boolean processed = false;
/*      */ 
/*  575 */       QName typeQName = tEntry.getQName();
/*      */ 
/*  579 */       QName itemType = SchemaUtils.getListItemType(tEntry.getNode());
/*  580 */       if (itemType != null)
/*      */       {
/*  582 */         TypeEntry itemEntry = symbolTable.getTypeEntry(itemType, false);
/*      */ 
/*  585 */         javifyTypeEntryName(symbolTable, itemEntry, anonQNames, uniqueNum);
/*      */ 
/*  587 */         TypeEntry refedEntry = itemEntry.getRefType();
/*  588 */         QName baseName = refedEntry == null ? itemEntry.getQName() : refedEntry.getQName();
/*      */ 
/*  590 */         typeQName = new QName(baseName.getNamespaceURI(), baseName.getLocalPart() + "[]");
/*      */       }
/*      */ 
/*  594 */       if (this.emitter.isDeploy()) {
/*  595 */         Class class1 = (Class)this.emitter.getQName2ClassMap().get(typeQName);
/*  596 */         if ((class1 != null) && (!class1.isArray())) {
/*  597 */           tEntry.setName(getJavaClassName(class1));
/*  598 */           processed = true;
/*      */         }
/*      */       }
/*      */ 
/*  602 */       if (!processed) {
/*  603 */         if (typeQName.getLocalPart().indexOf(">") < 0)
/*      */         {
/*  608 */           tEntry.setName(this.emitter.getJavaName(typeQName));
/*      */         }
/*      */         else
/*      */         {
/*  622 */           String localName = typeQName.getLocalPart();
/*      */ 
/*  629 */           StringBuffer sb = new StringBuffer(localName);
/*      */           int aidx;
/*  632 */           while ((aidx = sb.toString().indexOf(">")) > -1) {
/*  633 */             sb.replace(aidx, aidx + ">".length(), "");
/*  634 */             char c = sb.charAt(aidx);
/*  635 */             if ((Character.isLetter(c)) && (Character.isLowerCase(c))) {
/*  636 */               sb.setCharAt(aidx, Character.toUpperCase(c));
/*      */             }
/*      */           }
/*      */ 
/*  640 */           localName = sb.toString();
/*  641 */           typeQName = new QName(typeQName.getNamespaceURI(), localName);
/*      */ 
/*  644 */           if ((this.emitter.isTypeCollisionProtection()) && (!this.emitter.getNamespaceExcludes().contains(new NamespaceSelector(typeQName.getNamespaceURI()))))
/*      */           {
/*  651 */             if ((symbolTable.getType(typeQName) != null) || (anonQNames.get(typeQName) != null))
/*      */             {
/*  653 */               localName = localName + "Type" + uniqueNum++;
/*  654 */               typeQName = new QName(typeQName.getNamespaceURI(), localName);
/*      */             }
/*      */ 
/*  659 */             anonQNames.put(typeQName, typeQName);
/*      */           }
/*      */ 
/*  663 */           tEntry.setName(this.emitter.getJavaName(typeQName));
/*      */         }
/*      */       }
/*      */ 
/*  667 */       Vector elements = tEntry.getContainedElements();
/*  668 */       if (elements != null) {
/*  669 */         for (int i = 0; i < elements.size(); i++) {
/*  670 */           ElementDecl elem = (ElementDecl)elements.get(i);
/*  671 */           String varName = this.emitter.getJavaVariableName(typeQName, elem.getQName(), true);
/*  672 */           elem.setName(varName);
/*      */         }
/*      */       }
/*      */ 
/*  676 */       Vector attributes = tEntry.getContainedAttributes();
/*  677 */       if (attributes != null) {
/*  678 */         for (int i = 0; i < attributes.size(); i++) {
/*  679 */           ContainedAttribute attr = (ContainedAttribute)attributes.get(i);
/*  680 */           String varName = this.emitter.getJavaVariableName(typeQName, attr.getQName(), false);
/*  681 */           attr.setName(varName);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  688 */     entry.setName(tEntry.getName() + dims);
/*      */ 
/*  690 */     return uniqueNum;
/*      */   }
/*      */ 
/*      */   private static String getJavaClassName(Class clazz)
/*      */   {
/*  700 */     Class class1 = clazz;
/*      */ 
/*  702 */     while (class1.isArray()) {
/*  703 */       class1 = class1.getComponentType();
/*      */     }
/*      */ 
/*  706 */     String name = class1.getName();
/*  707 */     name.replace('$', '.');
/*  708 */     return name;
/*      */   }
/*      */ 
/*      */   private void setFaultContext(SymbolTable symbolTable)
/*      */   {
/*  726 */     Iterator it = symbolTable.getHashMap().values().iterator();
/*      */ 
/*  728 */     while (it.hasNext()) {
/*  729 */       Vector v = (Vector)it.next();
/*      */ 
/*  731 */       for (int i = 0; i < v.size(); i++) {
/*  732 */         SymTabEntry entry = (SymTabEntry)v.elementAt(i);
/*      */ 
/*  735 */         if ((entry instanceof BindingEntry)) {
/*  736 */           BindingEntry bEntry = (BindingEntry)entry;
/*  737 */           HashMap allOpFaults = bEntry.getFaults();
/*  738 */           Iterator ops = allOpFaults.values().iterator();
/*      */ 
/*  741 */           while (ops.hasNext()) {
/*  742 */             ArrayList faults = (ArrayList)ops.next();
/*      */ 
/*  744 */             for (int j = 0; j < faults.size(); j++) {
/*  745 */               FaultInfo info = (FaultInfo)faults.get(j);
/*      */ 
/*  747 */               setFaultContext(info, symbolTable);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setFaultContext(FaultInfo fault, SymbolTable symbolTable)
/*      */   {
/*  766 */     QName faultXmlType = null;
/*  767 */     Vector parts = new Vector();
/*      */     try
/*      */     {
/*  773 */       symbolTable.getParametersFromParts(parts, fault.getMessage().getOrderedParts(null), false, fault.getName(), null);
/*      */     }
/*      */     catch (IOException e)
/*      */     {
/*      */     }
/*      */ 
/*  780 */     String exceptionClassName = null;
/*      */ 
/*  782 */     for (int j = 0; j < parts.size(); j++) {
/*  783 */       TypeEntry te = ((Parameter)parts.elementAt(j)).getType();
/*      */ 
/*  787 */       TypeEntry elementTE = null;
/*      */ 
/*  789 */       if ((te instanceof Element)) {
/*  790 */         elementTE = te;
/*  791 */         te = te.getRefType();
/*      */       }
/*      */ 
/*  795 */       faultXmlType = te.getQName();
/*      */ 
/*  800 */       if ((te.getBaseType() != null) || (te.isSimpleType()) || ((te.getDimensions().length() > 0) && (te.getRefType().getBaseType() != null)))
/*      */       {
/*      */         continue;
/*      */       }
/*      */ 
/*  808 */       Boolean isComplexFault = (Boolean)te.getDynamicVar(COMPLEX_TYPE_FAULT);
/*      */ 
/*  811 */       if ((isComplexFault == null) || (!isComplexFault.booleanValue()))
/*      */       {
/*  814 */         te.setDynamicVar(COMPLEX_TYPE_FAULT, Boolean.TRUE);
/*      */ 
/*  817 */         if (elementTE != null) {
/*  818 */           te.setDynamicVar(COMPLEX_TYPE_FAULT, Boolean.TRUE);
/*      */         }
/*      */ 
/*  824 */         HashSet derivedSet = org.apache.axis.wsdl.symbolTable.Utils.getDerivedTypes(te, symbolTable);
/*      */ 
/*  827 */         Iterator derivedI = derivedSet.iterator();
/*      */ 
/*  829 */         while (derivedI.hasNext()) {
/*  830 */           TypeEntry derivedTE = (TypeEntry)derivedI.next();
/*      */ 
/*  832 */           derivedTE.setDynamicVar(COMPLEX_TYPE_FAULT, Boolean.TRUE);
/*      */         }
/*      */ 
/*  838 */         TypeEntry base = SchemaUtils.getComplexElementExtensionBase(te.getNode(), symbolTable);
/*      */ 
/*  842 */         while (base != null) {
/*  843 */           base.setDynamicVar(COMPLEX_TYPE_FAULT, Boolean.TRUE);
/*      */ 
/*  847 */           base = SchemaUtils.getComplexElementExtensionBase(base.getNode(), symbolTable);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  853 */       exceptionClassName = te.getName();
/*      */     }
/*      */ 
/*  857 */     String excName = getExceptionJavaNameHook(fault.getMessage().getQName());
/*  858 */     if (excName != null) {
/*  859 */       exceptionClassName = excName;
/*      */     }
/*      */ 
/*  864 */     MessageEntry me = symbolTable.getMessageEntry(fault.getMessage().getQName());
/*      */ 
/*  867 */     if (me != null) {
/*  868 */       me.setDynamicVar(EXCEPTION_DATA_TYPE, faultXmlType);
/*      */ 
/*  871 */       if (exceptionClassName != null) {
/*  872 */         me.setDynamicVar(COMPLEX_TYPE_FAULT, Boolean.TRUE);
/*      */ 
/*  874 */         me.setDynamicVar(EXCEPTION_CLASS_NAME, exceptionClassName);
/*      */       }
/*      */       else {
/*  877 */         me.setDynamicVar(EXCEPTION_CLASS_NAME, this.emitter.getJavaName(me.getQName()));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected String getExceptionJavaNameHook(QName qname)
/*      */   {
/*  884 */     return null;
/*      */   }
/*      */ 
/*      */   protected void determineInterfaceNames(SymbolTable symbolTable)
/*      */   {
/*  894 */     Iterator it = symbolTable.getHashMap().values().iterator();
/*      */ 
/*  896 */     while (it.hasNext()) {
/*  897 */       Vector v = (Vector)it.next();
/*      */ 
/*  899 */       for (int i = 0; i < v.size(); i++) {
/*  900 */         SymTabEntry entry = (SymTabEntry)v.elementAt(i);
/*      */ 
/*  902 */         if ((entry instanceof BindingEntry))
/*      */         {
/*  906 */           BindingEntry bEntry = (BindingEntry)entry;
/*  907 */           PortTypeEntry ptEntry = symbolTable.getPortTypeEntry(bEntry.getBinding().getPortType().getQName());
/*      */ 
/*  910 */           String seiName = getServiceEndpointInterfaceJavaNameHook(ptEntry, bEntry);
/*  911 */           if (seiName == null) {
/*  912 */             seiName = ptEntry.getName();
/*      */           }
/*      */ 
/*  915 */           bEntry.setDynamicVar(JavaBindingWriter.INTERFACE_NAME, seiName);
/*      */         }
/*  917 */         else if ((entry instanceof ServiceEntry)) {
/*  918 */           ServiceEntry sEntry = (ServiceEntry)entry;
/*  919 */           String siName = getServiceInterfaceJavaNameHook(sEntry);
/*  920 */           if (siName != null) {
/*  921 */             sEntry.setName(siName);
/*      */           }
/*      */ 
/*  924 */           Service service = sEntry.getService();
/*  925 */           Map portMap = service.getPorts();
/*  926 */           Iterator portIterator = portMap.values().iterator();
/*      */ 
/*  928 */           while (portIterator.hasNext()) {
/*  929 */             Port p = (Port)portIterator.next();
/*      */ 
/*  931 */             Binding binding = p.getBinding();
/*  932 */             BindingEntry bEntry = symbolTable.getBindingEntry(binding.getQName());
/*      */ 
/*  936 */             if (bEntry.getBindingType() != 0)
/*      */             {
/*      */               continue;
/*      */             }
/*  940 */             String portName = getPortJavaNameHook(p.getName());
/*  941 */             if (portName != null)
/*  942 */               bEntry.setDynamicVar("port name:" + p.getName(), portName);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected String getServiceEndpointInterfaceJavaNameHook(PortTypeEntry ptEntry, BindingEntry bEntry)
/*      */   {
/*  952 */     return null;
/*      */   }
/*      */ 
/*      */   protected String getServiceInterfaceJavaNameHook(ServiceEntry sEntry) {
/*  956 */     return null;
/*      */   }
/*      */ 
/*      */   protected String getPortJavaNameHook(String portName) {
/*  960 */     return null;
/*      */   }
/*      */ 
/*      */   protected void resolveNameClashes(SymbolTable symbolTable)
/*      */   {
/*  972 */     HashSet anonTypes = new HashSet();
/*  973 */     List collisionCandidates = new ArrayList();
/*      */ 
/*  975 */     List localParts = new ArrayList();
/*  976 */     for (Iterator i = symbolTable.getHashMap().keySet().iterator(); i.hasNext(); ) {
/*  977 */       QName qName = (QName)i.next();
/*  978 */       String localPart = qName.getLocalPart();
/*  979 */       if (!localParts.contains(localPart)) {
/*  980 */         localParts.add(localPart);
/*      */       }
/*      */     }
/*  983 */     Map pkg2NamespacesMap = this.emitter.getNamespaces().getPkg2NamespacesMap();
/*  984 */     for (Iterator i = pkg2NamespacesMap.values().iterator(); i.hasNext(); ) {
/*  985 */       Vector namespaces = (Vector)i.next();
/*      */ 
/*  988 */       for (int j = 0; j < localParts.size(); j++) {
/*  989 */         Vector v = new Vector();
/*  990 */         for (int k = 0; k < namespaces.size(); k++) {
/*  991 */           QName qName = new QName((String)namespaces.get(k), (String)localParts.get(j));
/*  992 */           if (symbolTable.getHashMap().get(qName) != null) {
/*  993 */             v.addAll((Vector)symbolTable.getHashMap().get(qName));
/*      */           }
/*      */         }
/*  996 */         if (v.size() > 0) {
/*  997 */           collisionCandidates.add(v);
/*      */         }
/*      */       }
/*      */     }
/* 1001 */     Iterator it = collisionCandidates.iterator();
/*      */ 
/* 1003 */     while (it.hasNext()) {
/* 1004 */       Vector v = new Vector((Vector)it.next());
/*      */ 
/* 1008 */       int index = 0;
/*      */ 
/* 1010 */       while (index < v.size()) {
/* 1011 */         if ((v.elementAt(index) instanceof MessageEntry))
/*      */         {
/* 1013 */           MessageEntry msgEntry = (MessageEntry)v.elementAt(index);
/* 1014 */           if (msgEntry.getDynamicVar(EXCEPTION_CLASS_NAME) == null)
/* 1015 */             v.removeElementAt(index);
/*      */           else
/* 1017 */             index++; continue;
/*      */         }
/*      */ 
/* 1020 */         index++;
/*      */       }
/*      */ 
/* 1024 */       if (v.size() > 1) {
/* 1025 */         boolean resolve = true;
/*      */ 
/* 1031 */         if ((v.size() == 2) && ((((v.elementAt(0) instanceof Element)) && ((v.elementAt(1) instanceof Type))) || (((v.elementAt(1) instanceof Element)) && ((v.elementAt(0) instanceof Type)))))
/*      */         {
/*      */           Element e;
/*      */           Element e;
/* 1037 */           if ((v.elementAt(0) instanceof Element))
/* 1038 */             e = (Element)v.elementAt(0);
/*      */           else {
/* 1040 */             e = (Element)v.elementAt(1);
/*      */           }
/*      */ 
/* 1043 */           BooleanHolder forElement = new BooleanHolder();
/* 1044 */           QName eType = Utils.getTypeQName(e.getNode(), forElement, false);
/*      */ 
/* 1047 */           if ((eType != null) && (!forElement.value))
/*      */           {
/* 1049 */             resolve = false;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1055 */         if (resolve) {
/* 1056 */           resolve = false;
/*      */ 
/* 1058 */           String name = null;
/*      */ 
/* 1060 */           for (int i = 0; (i < v.size()) && (!resolve); i++) {
/* 1061 */             SymTabEntry entry = (SymTabEntry)v.elementAt(i);
/*      */ 
/* 1063 */             if (((entry instanceof MessageEntry)) || ((entry instanceof BindingEntry)))
/*      */             {
/* 1066 */               String exceptionClassName = (String)entry.getDynamicVar(EXCEPTION_CLASS_NAME);
/* 1067 */               if (exceptionClassName != null) {
/* 1068 */                 if (name == null)
/* 1069 */                   name = exceptionClassName;
/* 1070 */                 else if (name.equals(exceptionClassName))
/* 1071 */                   resolve = true;
/*      */               }
/*      */             }
/* 1074 */             else if (name == null) {
/* 1075 */               name = entry.getName();
/* 1076 */             } else if (name.equals(entry.getName())) {
/* 1077 */               resolve = true;
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1083 */         if (resolve) {
/* 1084 */           boolean firstType = true;
/*      */ 
/* 1086 */           for (int i = 0; i < v.size(); i++) {
/* 1087 */             SymTabEntry entry = (SymTabEntry)v.elementAt(i);
/*      */ 
/* 1089 */             if ((entry instanceof Element)) {
/* 1090 */               entry.setName(mangleName(entry.getName(), "_Element"));
/*      */ 
/* 1095 */               QName anonQName = new QName(entry.getQName().getNamespaceURI(), ">" + entry.getQName().getLocalPart());
/*      */ 
/* 1100 */               TypeEntry anonType = symbolTable.getType(anonQName);
/*      */ 
/* 1103 */               if (anonType != null) {
/* 1104 */                 anonType.setName(entry.getName());
/* 1105 */                 anonTypes.add(anonType);
/*      */               }
/* 1107 */             } else if ((entry instanceof TypeEntry))
/*      */             {
/* 1112 */               if (firstType) {
/* 1113 */                 firstType = false;
/*      */ 
/* 1115 */                 Iterator types = symbolTable.getTypeIndex().values().iterator();
/*      */ 
/* 1118 */                 while (types.hasNext()) {
/* 1119 */                   TypeEntry type = (TypeEntry)types.next();
/*      */ 
/* 1121 */                   if ((type != entry) && (type.getBaseType() == null) && (sameJavaClass(entry.getName(), type.getName())))
/*      */                   {
/* 1125 */                     v.add(type);
/*      */                   }
/*      */ 
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/* 1132 */               if (anonTypes.contains(entry))
/*      */               {
/*      */                 continue;
/*      */               }
/* 1136 */               boolean needResolve = false;
/*      */ 
/* 1139 */               for (int j = 0; j < v.size(); j++) {
/* 1140 */                 SymTabEntry e = (SymTabEntry)v.elementAt(j);
/* 1141 */                 if ((!(e instanceof PortTypeEntry)) && (!(e instanceof ServiceEntry)) && (!(e instanceof BindingEntry))) {
/*      */                   continue;
/*      */                 }
/* 1144 */                 needResolve = true;
/* 1145 */                 break;
/*      */               }
/*      */ 
/* 1149 */               if (!needResolve)
/*      */               {
/*      */                 continue;
/*      */               }
/*      */ 
/* 1154 */               Boolean isComplexTypeFault = (Boolean)entry.getDynamicVar(COMPLEX_TYPE_FAULT);
/* 1155 */               if ((isComplexTypeFault != null) && (isComplexTypeFault.booleanValue())) {
/* 1156 */                 entry.setName(mangleName(entry.getName(), "_Exception"));
/*      */               }
/*      */               else {
/* 1159 */                 entry.setName(mangleName(entry.getName(), "_Type"));
/*      */               }
/*      */ 
/* 1163 */               Map elementIndex = symbolTable.getElementIndex();
/* 1164 */               List elements = new ArrayList(elementIndex.values());
/* 1165 */               for (int j = 0; j < elementIndex.size(); j++) {
/* 1166 */                 TypeEntry te = (TypeEntry)elements.get(j);
/* 1167 */                 TypeEntry ref = te.getRefType();
/* 1168 */                 if ((ref != null) && (entry.getQName().equals(ref.getQName()))) {
/* 1169 */                   te.setName(entry.getName());
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/* 1174 */               if ((isComplexTypeFault != null) && (isComplexTypeFault.booleanValue()))
/*      */               {
/* 1176 */                 List messageEntries = symbolTable.getMessageEntries();
/* 1177 */                 for (int j = 0; j < messageEntries.size(); j++) {
/* 1178 */                   MessageEntry messageEntry = (MessageEntry)messageEntries.get(j);
/* 1179 */                   Boolean isComplexTypeFaultMsg = (Boolean)messageEntry.getDynamicVar(COMPLEX_TYPE_FAULT);
/* 1180 */                   if ((isComplexTypeFaultMsg != null) && (isComplexTypeFaultMsg.booleanValue())) {
/* 1181 */                     QName exceptionDataType = (QName)messageEntry.getDynamicVar(EXCEPTION_DATA_TYPE);
/* 1182 */                     if (((TypeEntry)entry).getQName().equals(exceptionDataType)) {
/* 1183 */                       String className = (String)messageEntry.getDynamicVar(EXCEPTION_CLASS_NAME);
/* 1184 */                       messageEntry.setDynamicVar(EXCEPTION_CLASS_NAME, className + "_Exception");
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/* 1190 */             else if ((entry instanceof PortTypeEntry)) {
/* 1191 */               entry.setName(mangleName(entry.getName(), "_PortType"));
/* 1192 */             } else if ((entry instanceof ServiceEntry)) {
/* 1193 */               entry.setName(mangleName(entry.getName(), "_Service"));
/* 1194 */             } else if ((entry instanceof MessageEntry)) {
/* 1195 */               Boolean complexTypeFault = (Boolean)entry.getDynamicVar(COMPLEX_TYPE_FAULT);
/*      */ 
/* 1197 */               if ((complexTypeFault == null) || (!complexTypeFault.booleanValue())) {
/* 1198 */                 String exceptionClassName = (String)entry.getDynamicVar(EXCEPTION_CLASS_NAME);
/* 1199 */                 entry.setDynamicVar(EXCEPTION_CLASS_NAME, exceptionClassName + "_Exception");
/*      */               }
/*      */ 
/*      */             }
/* 1205 */             else if ((entry instanceof BindingEntry)) {
/* 1206 */               BindingEntry bEntry = (BindingEntry)entry;
/*      */ 
/* 1214 */               if (bEntry.hasLiteral())
/* 1215 */                 entry.setName(mangleName(entry.getName(), "_Binding"));
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private String mangleName(String name, String mangle)
/*      */   {
/* 1234 */     int index = name.indexOf("[");
/*      */ 
/* 1236 */     if (index >= 0) {
/* 1237 */       String pre = name.substring(0, index);
/* 1238 */       String post = name.substring(index);
/*      */ 
/* 1240 */       return pre + mangle + post;
/*      */     }
/* 1242 */     return name + mangle;
/*      */   }
/*      */ 
/*      */   private boolean sameJavaClass(String one, String two)
/*      */   {
/* 1255 */     int index1 = one.indexOf("[");
/* 1256 */     int index2 = two.indexOf("[");
/*      */ 
/* 1258 */     if (index1 > 0) {
/* 1259 */       one = one.substring(0, index1);
/*      */     }
/*      */ 
/* 1262 */     if (index2 > 0) {
/* 1263 */       two = two.substring(0, index2);
/*      */     }
/*      */ 
/* 1266 */     return one.equals(two);
/*      */   }
/*      */ 
/*      */   protected void setAllReferencesToTrue()
/*      */   {
/* 1276 */     Iterator it = this.symbolTable.getHashMap().values().iterator();
/*      */ 
/* 1278 */     while (it.hasNext()) {
/* 1279 */       Vector v = (Vector)it.next();
/*      */ 
/* 1281 */       for (int i = 0; i < v.size(); i++) {
/* 1282 */         SymTabEntry entry = (SymTabEntry)v.elementAt(i);
/*      */ 
/* 1284 */         if (((entry instanceof BindingEntry)) && (((BindingEntry)entry).getBindingType() != 0))
/*      */         {
/* 1287 */           entry.setIsReferenced(false);
/*      */         }
/* 1289 */         else entry.setIsReferenced(true);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void ignoreNonSOAPBindings(SymbolTable symbolTable)
/*      */   {
/* 1305 */     Vector unusedPortTypes = new Vector();
/* 1306 */     Vector usedPortTypes = new Vector();
/* 1307 */     Iterator it = symbolTable.getHashMap().values().iterator();
/*      */ 
/* 1309 */     while (it.hasNext()) {
/* 1310 */       Vector v = (Vector)it.next();
/*      */ 
/* 1312 */       for (int i = 0; i < v.size(); i++) {
/* 1313 */         SymTabEntry entry = (SymTabEntry)v.elementAt(i);
/*      */ 
/* 1315 */         if ((entry instanceof BindingEntry)) {
/* 1316 */           BindingEntry bEntry = (BindingEntry)entry;
/* 1317 */           Binding binding = bEntry.getBinding();
/* 1318 */           PortType portType = binding.getPortType();
/* 1319 */           PortTypeEntry ptEntry = symbolTable.getPortTypeEntry(portType.getQName());
/*      */ 
/* 1322 */           if (bEntry.getBindingType() == 0)
/*      */           {
/* 1328 */             usedPortTypes.add(ptEntry);
/*      */ 
/* 1330 */             if (unusedPortTypes.contains(ptEntry))
/* 1331 */               unusedPortTypes.remove(ptEntry);
/*      */           }
/*      */           else {
/* 1334 */             bEntry.setIsReferenced(false);
/*      */ 
/* 1338 */             if (!usedPortTypes.contains(ptEntry)) {
/* 1339 */               unusedPortTypes.add(ptEntry);
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1348 */     for (int i = 0; i < unusedPortTypes.size(); i++) {
/* 1349 */       PortTypeEntry ptEntry = (PortTypeEntry)unusedPortTypes.get(i);
/*      */ 
/* 1351 */       ptEntry.setIsReferenced(false);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void constructSignatures(SymbolTable symbolTable)
/*      */   {
/* 1362 */     Iterator it = symbolTable.getHashMap().values().iterator();
/*      */ 
/* 1364 */     while (it.hasNext()) {
/* 1365 */       Vector v = (Vector)it.next();
/*      */ 
/* 1367 */       for (int i = 0; i < v.size(); i++) {
/* 1368 */         SymTabEntry entry = (SymTabEntry)v.elementAt(i);
/*      */ 
/* 1370 */         if ((entry instanceof BindingEntry)) {
/* 1371 */           BindingEntry bEntry = (BindingEntry)entry;
/* 1372 */           Binding binding = bEntry.getBinding();
/* 1373 */           PortType portType = binding.getPortType();
/* 1374 */           Iterator operations = portType.getOperations().iterator();
/*      */ 
/* 1377 */           while (operations.hasNext()) {
/* 1378 */             Operation operation = (Operation)operations.next();
/*      */ 
/* 1380 */             String wsdlOpName = operation.getName();
/* 1381 */             OperationType type = operation.getStyle();
/*      */ 
/* 1383 */             String javaOpName = getOperationJavaNameHook(bEntry, wsdlOpName);
/* 1384 */             if (javaOpName == null) {
/* 1385 */               javaOpName = operation.getName();
/*      */             }
/*      */ 
/* 1388 */             Parameters parameters = bEntry.getParameters(operation);
/*      */ 
/* 1391 */             if (OperationType.SOLICIT_RESPONSE.equals(type)) {
/* 1392 */               parameters.signature = ("    // " + Messages.getMessage("invalidSolResp00", javaOpName));
/*      */ 
/* 1396 */               System.err.println(Messages.getMessage("invalidSolResp00", javaOpName));
/*      */             }
/* 1398 */             else if (OperationType.NOTIFICATION.equals(type)) {
/* 1399 */               parameters.signature = ("    // " + Messages.getMessage("invalidNotif00", javaOpName));
/*      */ 
/* 1403 */               System.err.println(Messages.getMessage("invalidNotif00", javaOpName));
/*      */             }
/* 1406 */             else if (parameters != null) {
/* 1407 */               String returnType = getReturnTypeJavaNameHook(bEntry, wsdlOpName);
/* 1408 */               if ((returnType != null) && 
/* 1409 */                 (parameters.returnParam != null)) {
/* 1410 */                 parameters.returnParam.getType().setName(returnType);
/*      */               }
/*      */ 
/* 1413 */               for (int j = 0; j < parameters.list.size(); j++) {
/* 1414 */                 Parameter p = (Parameter)parameters.list.get(j);
/* 1415 */                 String paramType = getParameterTypeJavaNameHook(bEntry, wsdlOpName, j);
/* 1416 */                 if (paramType != null) {
/* 1417 */                   p.getType().setName(paramType);
/*      */                 }
/*      */               }
/* 1420 */               parameters.signature = constructSignature(parameters, javaOpName);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected String getOperationJavaNameHook(BindingEntry bEntry, String wsdlOpName)
/*      */   {
/* 1431 */     return null;
/*      */   }
/*      */ 
/*      */   protected String getReturnTypeJavaNameHook(BindingEntry bEntry, String wsdlOpName) {
/* 1435 */     return null;
/*      */   }
/*      */ 
/*      */   protected String getParameterTypeJavaNameHook(BindingEntry bEntry, String wsdlOpName, int pos) {
/* 1439 */     return null;
/*      */   }
/*      */ 
/*      */   private String constructSignature(Parameters parms, String opName)
/*      */   {
/* 1451 */     String name = Utils.xmlNameToJava(opName);
/* 1452 */     String ret = "void";
/*      */ 
/* 1454 */     if ((parms != null) && (parms.returnParam != null)) {
/* 1455 */       ret = Utils.getParameterTypeName(parms.returnParam);
/*      */     }
/*      */ 
/* 1458 */     String signature = "    public " + ret + " " + name + "(";
/* 1459 */     boolean needComma = false;
/*      */ 
/* 1461 */     for (int i = 0; (parms != null) && (i < parms.list.size()); i++) {
/* 1462 */       Parameter p = (Parameter)parms.list.get(i);
/*      */ 
/* 1464 */       if (needComma)
/* 1465 */         signature = signature + ", ";
/*      */       else {
/* 1467 */         needComma = true;
/*      */       }
/*      */ 
/* 1470 */       String javifiedName = Utils.xmlNameToJava(p.getName());
/*      */ 
/* 1472 */       if (p.getMode() == 1)
/* 1473 */         signature = signature + Utils.getParameterTypeName(p) + " " + javifiedName;
/*      */       else {
/* 1475 */         signature = signature + Utils.holder(p, this.emitter) + " " + javifiedName;
/*      */       }
/*      */     }
/*      */ 
/* 1479 */     signature = signature + ") throws java.rmi.RemoteException";
/*      */ 
/* 1481 */     if ((parms != null) && (parms.faults != null))
/*      */     {
/* 1484 */       Iterator i = parms.faults.values().iterator();
/*      */ 
/* 1486 */       while (i.hasNext()) {
/* 1487 */         Fault fault = (Fault)i.next();
/* 1488 */         String exceptionName = Utils.getFullExceptionName(fault.getMessage(), this.symbolTable);
/*      */ 
/* 1491 */         if (exceptionName != null) {
/* 1492 */           signature = signature + ", " + exceptionName;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1497 */     return signature;
/*      */   }
/*      */ 
/*      */   protected void determineIfHoldersNeeded(SymbolTable symbolTable)
/*      */   {
/* 1508 */     Iterator it = symbolTable.getHashMap().values().iterator();
/*      */ 
/* 1510 */     while (it.hasNext()) {
/* 1511 */       Vector v = (Vector)it.next();
/*      */ 
/* 1513 */       for (int i = 0; i < v.size(); i++) {
/* 1514 */         if (!(v.get(i) instanceof BindingEntry))
/*      */         {
/*      */           continue;
/*      */         }
/* 1518 */         BindingEntry bEntry = (BindingEntry)v.get(i);
/*      */ 
/* 1522 */         Iterator operations = bEntry.getParameters().values().iterator();
/*      */ 
/* 1525 */         while (operations.hasNext()) {
/* 1526 */           Parameters parms = (Parameters)operations.next();
/*      */ 
/* 1528 */           for (int j = 0; j < parms.list.size(); j++) {
/* 1529 */             Parameter p = (Parameter)parms.list.get(j);
/*      */ 
/* 1533 */             if (p.getMode() != 1) {
/* 1534 */               TypeEntry typeEntry = p.getType();
/*      */ 
/* 1536 */               typeEntry.setDynamicVar("Holder is needed", Boolean.TRUE);
/*      */ 
/* 1542 */               if ((!typeEntry.isSimpleType()) && (typeEntry.getRefType() != null))
/*      */               {
/* 1544 */                 typeEntry.getRefType().setDynamicVar("Holder is needed", Boolean.TRUE);
/*      */               }
/*      */ 
/* 1551 */               QName anonQName = SchemaUtils.getElementAnonQName(p.getType().getNode());
/*      */ 
/* 1555 */               if (anonQName != null) {
/* 1556 */                 TypeEntry anonType = symbolTable.getType(anonQName);
/*      */ 
/* 1559 */                 if (anonType != null)
/* 1560 */                   anonType.setDynamicVar("Holder is needed", Boolean.TRUE);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBaseTypeMapping(BaseTypeMapping btm)
/*      */   {
/* 1585 */     this.btm = btm;
/*      */   }
/*      */ 
/*      */   public BaseTypeMapping getBaseTypeMapping()
/*      */   {
/* 1594 */     if (this.btm == null) {
/* 1595 */       this.btm = new BaseTypeMapping()
/*      */       {
/*      */         public String getBaseName(QName qNameIn)
/*      */         {
/* 1599 */           QName qName = new QName(qNameIn.getNamespaceURI(), qNameIn.getLocalPart());
/*      */ 
/* 1602 */           Class cls = JavaGeneratorFactory.this.emitter.getDefaultTypeMapping().getClassForQName(qName);
/*      */ 
/* 1605 */           if (cls == null) {
/* 1606 */             return null;
/*      */           }
/* 1608 */           return JavaUtils.getTextClassName(cls.getName());
/*      */         }
/*      */       };
/*      */     }
/*      */ 
/* 1614 */     return this.btm;
/*      */   }
/*      */ 
/*      */   protected boolean include(QName qName)
/*      */   {
/* 1636 */     String namespace = (qName != null) && (qName.getNamespaceURI() != null) ? qName.getNamespaceURI() : "";
/*      */ 
/* 1641 */     boolean doInclude = false;
/* 1642 */     NamespaceSelector selector = new NamespaceSelector(namespace);
/* 1643 */     if ((qName == null) || (this.emitter == null) || (this.emitter.getNamespaceIncludes().contains(selector)) || ((this.emitter.getNamespaceIncludes().size() == 0) && (!this.emitter.getNamespaceExcludes().contains(selector))))
/*      */     {
/* 1648 */       doInclude = true;
/*      */     }
/*      */     else {
/* 1651 */       log_.info("excluding code generation for non-included QName:" + qName);
/*      */     }
/*      */ 
/* 1655 */     return doInclude;
/*      */   }
/*      */ 
/*      */   protected class Writers
/*      */     implements Generator
/*      */   {
/*  358 */     Vector writers = new Vector();
/*      */ 
/*  361 */     SymbolTable symbolTable = null;
/*      */ 
/*  364 */     Generator baseWriter = null;
/*      */ 
/*  369 */     SymTabEntry entry = null;
/*      */ 
/*  372 */     Definition def = null;
/*      */ 
/*      */     protected Writers()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void addGenerator(Class writer)
/*      */     {
/*  380 */       this.writers.add(writer);
/*      */     }
/*      */ 
/*      */     public void addStuff(Generator baseWriter, SymTabEntry entry, SymbolTable symbolTable)
/*      */     {
/*  393 */       this.baseWriter = baseWriter;
/*  394 */       this.entry = entry;
/*  395 */       this.symbolTable = symbolTable;
/*      */     }
/*      */ 
/*      */     public void addStuff(Generator baseWriter, Definition def, SymbolTable symbolTable)
/*      */     {
/*  408 */       this.baseWriter = baseWriter;
/*  409 */       this.def = def;
/*  410 */       this.symbolTable = symbolTable;
/*      */     }
/*      */ 
/*      */     public void generate()
/*      */       throws IOException
/*      */     {
/*  420 */       if (this.baseWriter != null)
/*  421 */         this.baseWriter.generate();
/*      */       Object[] actualArgs;
/*      */       Class[] formalArgs;
/*      */       Object[] actualArgs;
/*  427 */       if (this.entry != null) {
/*  428 */         Class[] formalArgs = { Emitter.class, this.entry.getClass(), SymbolTable.class };
/*      */ 
/*  430 */         actualArgs = new Object[] { JavaGeneratorFactory.this.emitter, this.entry, this.symbolTable };
/*      */       } else {
/*  432 */         formalArgs = new Class[] { Emitter.class, Definition.class, SymbolTable.class };
/*      */ 
/*  434 */         actualArgs = new Object[] { JavaGeneratorFactory.this.emitter, this.def, this.symbolTable };
/*      */       }
/*      */ 
/*  437 */       for (int i = 0; i < this.writers.size(); i++) {
/*  438 */         Class wClass = (Class)this.writers.get(i);
/*      */         try
/*      */         {
/*  442 */           Constructor ctor = wClass.getConstructor(formalArgs);
/*      */ 
/*  444 */           gen = (Generator)ctor.newInstance(actualArgs);
/*      */         }
/*      */         catch (Throwable t)
/*      */         {
/*      */           Generator gen;
/*  446 */           throw new IOException(Messages.getMessage("exception01", t.getMessage()));
/*      */         }
/*      */         Generator gen;
/*  450 */         gen.generate();
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.JavaGeneratorFactory
 * JD-Core Version:    0.6.0
 */