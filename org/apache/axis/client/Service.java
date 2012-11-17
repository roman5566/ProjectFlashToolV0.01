/*     */ package org.apache.axis.client;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.rmi.Remote;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ import java.util.WeakHashMap;
/*     */ import javax.naming.Reference;
/*     */ import javax.naming.Referenceable;
/*     */ import javax.naming.StringRefAddr;
/*     */ import javax.wsdl.Binding;
/*     */ import javax.wsdl.Operation;
/*     */ import javax.wsdl.Port;
/*     */ import javax.wsdl.PortType;
/*     */ import javax.wsdl.extensions.soap.SOAPAddress;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.rpc.ServiceException;
/*     */ import javax.xml.rpc.encoding.TypeMappingRegistry;
/*     */ import javax.xml.rpc.handler.HandlerRegistry;
/*     */ import org.apache.axis.AxisEngine;
/*     */ import org.apache.axis.EngineConfiguration;
/*     */ import org.apache.axis.EngineConfigurationFactory;
/*     */ import org.apache.axis.configuration.EngineConfigurationFactoryFinder;
/*     */ import org.apache.axis.encoding.TypeMappingRegistryImpl;
/*     */ import org.apache.axis.utils.ClassUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.WSDLUtils;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.apache.axis.wsdl.gen.Parser;
/*     */ import org.apache.axis.wsdl.symbolTable.BindingEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.ServiceEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.SymbolTable;
/*     */ import org.w3c.dom.Document;
/*     */ 
/*     */ public class Service
/*     */   implements javax.xml.rpc.Service, Serializable, Referenceable
/*     */ {
/*  73 */   private transient AxisEngine engine = null;
/*  74 */   private transient EngineConfiguration config = null;
/*     */ 
/*  76 */   private QName serviceName = null;
/*  77 */   private String wsdlLocation = null;
/*  78 */   private javax.wsdl.Service wsdlService = null;
/*  79 */   private boolean maintainSession = false;
/*  80 */   private HandlerRegistryImpl registry = new HandlerRegistryImpl();
/*  81 */   private Parser wsdlParser = null;
/*     */ 
/*  83 */   private static Map cachedWSDL = new WeakHashMap();
/*  84 */   private static boolean cachingWSDL = true;
/*     */ 
/*  87 */   protected Call _call = null;
/*     */ 
/*  92 */   private Hashtable transportImpls = new Hashtable();
/*     */ 
/*     */   protected javax.wsdl.Service getWSDLService()
/*     */   {
/*  96 */     return this.wsdlService;
/*     */   }
/*     */ 
/*     */   public Parser getWSDLParser() {
/* 100 */     return this.wsdlParser;
/*     */   }
/*     */ 
/*     */   protected AxisClient getAxisClient() {
/* 104 */     return new AxisClient(getEngineConfiguration());
/*     */   }
/*     */ 
/*     */   public Service()
/*     */   {
/* 113 */     this.engine = getAxisClient();
/*     */   }
/*     */ 
/*     */   public Service(QName serviceName)
/*     */   {
/* 122 */     this.serviceName = serviceName;
/* 123 */     this.engine = getAxisClient();
/*     */   }
/*     */ 
/*     */   public Service(EngineConfiguration engineConfiguration, AxisClient axisClient)
/*     */   {
/* 133 */     this.config = engineConfiguration;
/* 134 */     this.engine = axisClient;
/*     */   }
/*     */ 
/*     */   public Service(EngineConfiguration config)
/*     */   {
/* 143 */     this.config = config;
/* 144 */     this.engine = getAxisClient();
/*     */   }
/*     */ 
/*     */   public Service(URL wsdlDoc, QName serviceName)
/*     */     throws ServiceException
/*     */   {
/* 156 */     this.serviceName = serviceName;
/* 157 */     this.engine = getAxisClient();
/* 158 */     this.wsdlLocation = wsdlDoc.toString();
/* 159 */     Parser parser = null;
/*     */ 
/* 161 */     if ((cachingWSDL) && ((parser = (Parser)cachedWSDL.get(this.wsdlLocation.toString())) != null))
/*     */     {
/* 163 */       initService(parser, serviceName);
/*     */     }
/* 165 */     else initService(wsdlDoc.toString(), serviceName);
/*     */   }
/*     */ 
/*     */   public Service(Parser parser, QName serviceName)
/*     */     throws ServiceException
/*     */   {
/* 177 */     this.serviceName = serviceName;
/* 178 */     this.engine = getAxisClient();
/* 179 */     initService(parser, serviceName);
/*     */   }
/*     */ 
/*     */   public Service(String wsdlLocation, QName serviceName)
/*     */     throws ServiceException
/*     */   {
/* 195 */     this.serviceName = serviceName;
/* 196 */     this.wsdlLocation = wsdlLocation;
/* 197 */     this.engine = getAxisClient();
/*     */ 
/* 199 */     Parser parser = null;
/* 200 */     if ((cachingWSDL) && ((parser = (Parser)cachedWSDL.get(wsdlLocation)) != null))
/*     */     {
/* 202 */       initService(parser, serviceName);
/*     */     }
/* 204 */     else initService(wsdlLocation, serviceName);
/*     */   }
/*     */ 
/*     */   public Service(InputStream wsdlInputStream, QName serviceName)
/*     */     throws ServiceException
/*     */   {
/* 220 */     this.engine = getAxisClient();
/* 221 */     Document doc = null;
/*     */     try {
/* 223 */       doc = XMLUtils.newDocument(wsdlInputStream);
/*     */     } catch (Exception exp) {
/* 225 */       throw new ServiceException(Messages.getMessage("wsdlError00", "", "\n" + exp));
/*     */     }
/*     */ 
/* 228 */     initService(null, doc, serviceName);
/*     */   }
/*     */ 
/*     */   private void initService(String url, QName serviceName)
/*     */     throws ServiceException
/*     */   {
/*     */     try
/*     */     {
/* 242 */       Parser parser = new Parser();
/* 243 */       parser.run(url);
/*     */ 
/* 245 */       if ((cachingWSDL) && (this.wsdlLocation != null)) {
/* 246 */         cachedWSDL.put(url, parser);
/*     */       }
/* 248 */       initService(parser, serviceName);
/*     */     } catch (Exception exp) {
/* 250 */       throw new ServiceException(Messages.getMessage("wsdlError00", "", "\n" + exp), exp);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void initService(String context, Document doc, QName serviceName)
/*     */     throws ServiceException
/*     */   {
/*     */     try
/*     */     {
/* 268 */       Parser parser = new Parser();
/* 269 */       parser.run(context, doc);
/*     */ 
/* 271 */       initService(parser, serviceName);
/*     */     } catch (Exception exp) {
/* 273 */       throw new ServiceException(Messages.getMessage("wsdlError00", "", "\n" + exp));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void initService(Parser parser, QName serviceName)
/*     */     throws ServiceException
/*     */   {
/*     */     try
/*     */     {
/* 288 */       this.wsdlParser = parser;
/* 289 */       ServiceEntry serviceEntry = parser.getSymbolTable().getServiceEntry(serviceName);
/* 290 */       if (serviceEntry != null)
/* 291 */         this.wsdlService = serviceEntry.getService();
/* 292 */       if (this.wsdlService == null)
/* 293 */         throw new ServiceException(Messages.getMessage("noService00", "" + serviceName));
/*     */     }
/*     */     catch (Exception exp) {
/* 296 */       throw new ServiceException(Messages.getMessage("wsdlError00", "", "\n" + exp));
/*     */     }
/*     */   }
/*     */ 
/*     */   public Remote getPort(QName portName, Class proxyInterface)
/*     */     throws ServiceException
/*     */   {
/* 314 */     if (this.wsdlService == null) {
/* 315 */       throw new ServiceException(Messages.getMessage("wsdlMissing00"));
/*     */     }
/* 317 */     Port port = this.wsdlService.getPort(portName.getLocalPart());
/* 318 */     if (port == null) {
/* 319 */       throw new ServiceException(Messages.getMessage("noPort00", "" + portName));
/*     */     }
/*     */ 
/* 323 */     Remote stub = getGeneratedStub(portName, proxyInterface);
/* 324 */     return stub != null ? stub : getPort(null, portName, proxyInterface);
/*     */   }
/*     */ 
/*     */   private Remote getGeneratedStub(QName portName, Class proxyInterface)
/*     */   {
/*     */     try
/*     */     {
/* 338 */       String pkg = proxyInterface.getName();
/* 339 */       pkg = pkg.substring(0, pkg.lastIndexOf('.'));
/* 340 */       Port port = this.wsdlService.getPort(portName.getLocalPart());
/* 341 */       String binding = port.getBinding().getQName().getLocalPart();
/* 342 */       Class stubClass = ClassUtils.forName(pkg + "." + binding + "Stub");
/*     */ 
/* 344 */       if (proxyInterface.isAssignableFrom(stubClass)) {
/* 345 */         Class[] formalArgs = { Referenceable.class };
/* 346 */         Object[] actualArgs = { this };
/* 347 */         Constructor ctor = stubClass.getConstructor(formalArgs);
/* 348 */         Stub stub = (Stub)ctor.newInstance(actualArgs);
/* 349 */         stub._setProperty("javax.xml.rpc.service.endpoint.address", WSDLUtils.getAddressFromPort(port));
/*     */ 
/* 352 */         stub.setPortName(portName);
/* 353 */         return (Remote)stub;
/*     */       }
/* 355 */       return null;
/*     */     } catch (Throwable t) {
/*     */     }
/* 358 */     return null;
/*     */   }
/*     */ 
/*     */   public Remote getPort(Class proxyInterface)
/*     */     throws ServiceException
/*     */   {
/* 371 */     if (this.wsdlService == null) {
/* 372 */       throw new ServiceException(Messages.getMessage("wsdlMissing00"));
/*     */     }
/* 374 */     Map ports = this.wsdlService.getPorts();
/* 375 */     if ((ports == null) || (ports.size() <= 0)) {
/* 376 */       throw new ServiceException(Messages.getMessage("noPort00", ""));
/*     */     }
/*     */ 
/* 379 */     String clazzName = proxyInterface.getName();
/* 380 */     if (clazzName.lastIndexOf('.') != -1) {
/* 381 */       clazzName = clazzName.substring(clazzName.lastIndexOf('.') + 1);
/*     */     }
/*     */ 
/* 385 */     Port port = (Port)ports.get(clazzName);
/* 386 */     if (port == null)
/*     */     {
/* 388 */       port = (Port)ports.values().iterator().next();
/*     */     }
/*     */ 
/* 393 */     Remote stub = getGeneratedStub(new QName(port.getName()), proxyInterface);
/* 394 */     return stub != null ? stub : getPort(null, new QName(port.getName()), proxyInterface);
/*     */   }
/*     */ 
/*     */   public Remote getPort(String endpoint, Class proxyInterface)
/*     */     throws ServiceException
/*     */   {
/* 411 */     return getPort(endpoint, null, proxyInterface);
/*     */   }
/*     */ 
/*     */   private Remote getPort(String endpoint, QName portName, Class proxyInterface) throws ServiceException
/*     */   {
/* 416 */     if (!proxyInterface.isInterface()) {
/* 417 */       throw new ServiceException(Messages.getMessage("mustBeIface00"));
/*     */     }
/*     */ 
/* 420 */     if (!Remote.class.isAssignableFrom(proxyInterface)) {
/* 421 */       throw new ServiceException(Messages.getMessage("mustExtendRemote00"));
/*     */     }
/*     */ 
/* 426 */     if (this.wsdlParser != null) {
/* 427 */       Port port = this.wsdlService.getPort(portName.getLocalPart());
/* 428 */       if (port == null) {
/* 429 */         throw new ServiceException(Messages.getMessage("noPort00", "" + proxyInterface.getName()));
/*     */       }
/* 431 */       Binding binding = port.getBinding();
/* 432 */       SymbolTable symbolTable = this.wsdlParser.getSymbolTable();
/* 433 */       BindingEntry bEntry = symbolTable.getBindingEntry(binding.getQName());
/* 434 */       if (bEntry.getParameters().size() != proxyInterface.getMethods().length) {
/* 435 */         throw new ServiceException(Messages.getMessage("incompatibleSEI00", "" + proxyInterface.getName()));
/*     */       }
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 441 */       Call call = null;
/* 442 */       if (portName == null) {
/* 443 */         call = (Call)createCall();
/* 444 */         if (endpoint != null)
/* 445 */           call.setTargetEndpointAddress(new URL(endpoint));
/*     */       }
/*     */       else {
/* 448 */         call = (Call)createCall(portName);
/*     */       }
/* 450 */       ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
/*     */ 
/* 452 */       javax.xml.rpc.Stub stub = (javax.xml.rpc.Stub)Proxy.newProxyInstance(classLoader, new Class[] { proxyInterface, javax.xml.rpc.Stub.class }, new AxisClientProxy(call, portName));
/*     */ 
/* 455 */       if ((stub instanceof Stub)) {
/* 456 */         ((Stub)stub).setPortName(portName);
/*     */       }
/* 458 */       return (Remote)stub; } catch (Exception e) {
/*     */     }
/* 460 */     throw new ServiceException(Messages.getMessage("wsdlError00", "", "\n" + e));
/*     */   }
/*     */ 
/*     */   public javax.xml.rpc.Call createCall(QName portName)
/*     */     throws ServiceException
/*     */   {
/* 475 */     Call call = (Call)createCall();
/* 476 */     call.setPortName(portName);
/*     */ 
/* 480 */     if (this.wsdlParser == null) {
/* 481 */       return call;
/*     */     }
/* 483 */     Port port = this.wsdlService.getPort(portName.getLocalPart());
/* 484 */     if (port == null) {
/* 485 */       throw new ServiceException(Messages.getMessage("noPort00", "" + portName));
/*     */     }
/* 487 */     Binding binding = port.getBinding();
/* 488 */     PortType portType = binding.getPortType();
/* 489 */     if (portType == null) {
/* 490 */       throw new ServiceException(Messages.getMessage("noPortType00", "" + portName));
/*     */     }
/*     */ 
/* 494 */     List list = port.getExtensibilityElements();
/* 495 */     for (int i = 0; (list != null) && (i < list.size()); i++) {
/* 496 */       Object obj = list.get(i);
/* 497 */       if (!(obj instanceof SOAPAddress)) continue;
/*     */       try {
/* 499 */         SOAPAddress addr = (SOAPAddress)obj;
/* 500 */         URL url = new URL(addr.getLocationURI());
/* 501 */         call.setTargetEndpointAddress(url);
/*     */       } catch (Exception exp) {
/* 503 */         throw new ServiceException(Messages.getMessage("cantSetURI00", "" + exp));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 509 */     return call;
/*     */   }
/*     */ 
/*     */   public javax.xml.rpc.Call createCall(QName portName, String operationName)
/*     */     throws ServiceException
/*     */   {
/* 526 */     Call call = (Call)createCall();
/* 527 */     call.setOperation(portName, operationName);
/* 528 */     return call;
/*     */   }
/*     */ 
/*     */   public javax.xml.rpc.Call createCall(QName portName, QName operationName)
/*     */     throws ServiceException
/*     */   {
/* 545 */     Call call = (Call)createCall();
/* 546 */     call.setOperation(portName, operationName);
/* 547 */     return call;
/*     */   }
/*     */ 
/*     */   public javax.xml.rpc.Call createCall()
/*     */     throws ServiceException
/*     */   {
/* 559 */     this._call = new Call(this);
/* 560 */     return this._call;
/*     */   }
/*     */ 
/*     */   public javax.xml.rpc.Call[] getCalls(QName portName)
/*     */     throws ServiceException
/*     */   {
/* 577 */     if (portName == null) {
/* 578 */       throw new ServiceException(Messages.getMessage("badPort00"));
/*     */     }
/* 580 */     if (this.wsdlService == null) {
/* 581 */       throw new ServiceException(Messages.getMessage("wsdlMissing00"));
/*     */     }
/* 583 */     Port port = this.wsdlService.getPort(portName.getLocalPart());
/* 584 */     if (port == null) {
/* 585 */       throw new ServiceException(Messages.getMessage("noPort00", "" + portName));
/*     */     }
/* 587 */     Binding binding = port.getBinding();
/* 588 */     SymbolTable symbolTable = this.wsdlParser.getSymbolTable();
/* 589 */     BindingEntry bEntry = symbolTable.getBindingEntry(binding.getQName());
/*     */ 
/* 591 */     Iterator i = bEntry.getParameters().keySet().iterator();
/*     */ 
/* 593 */     Vector calls = new Vector();
/* 594 */     while (i.hasNext()) {
/* 595 */       Operation operation = (Operation)i.next();
/* 596 */       javax.xml.rpc.Call call = createCall(QName.valueOf(port.getName()), QName.valueOf(operation.getName()));
/*     */ 
/* 598 */       calls.add(call);
/*     */     }
/* 600 */     javax.xml.rpc.Call[] array = new javax.xml.rpc.Call[calls.size()];
/* 601 */     calls.toArray(array);
/* 602 */     return array;
/*     */   }
/*     */ 
/*     */   public HandlerRegistry getHandlerRegistry()
/*     */   {
/* 619 */     return this.registry;
/*     */   }
/*     */ 
/*     */   public URL getWSDLDocumentLocation()
/*     */   {
/*     */     try
/*     */     {
/* 630 */       return new URL(this.wsdlLocation); } catch (MalformedURLException e) {
/*     */     }
/* 632 */     return null;
/*     */   }
/*     */ 
/*     */   public QName getServiceName()
/*     */   {
/* 642 */     if (this.serviceName != null) return this.serviceName;
/* 643 */     if (this.wsdlService == null) return null;
/* 644 */     QName qn = this.wsdlService.getQName();
/* 645 */     return new QName(qn.getNamespaceURI(), qn.getLocalPart());
/*     */   }
/*     */ 
/*     */   public Iterator getPorts()
/*     */     throws ServiceException
/*     */   {
/* 659 */     if (this.wsdlService == null) {
/* 660 */       throw new ServiceException(Messages.getMessage("wsdlMissing00"));
/*     */     }
/* 662 */     if (this.wsdlService.getPorts() == null)
/*     */     {
/* 664 */       return new Vector().iterator();
/*     */     }
/*     */ 
/* 667 */     Map portmap = this.wsdlService.getPorts();
/* 668 */     List portlist = new ArrayList(portmap.size());
/*     */ 
/* 674 */     Iterator portiterator = portmap.values().iterator();
/* 675 */     while (portiterator.hasNext()) {
/* 676 */       Port port = (Port)portiterator.next();
/*     */ 
/* 682 */       portlist.add(new QName(this.wsdlService.getQName().getNamespaceURI(), port.getName()));
/*     */     }
/*     */ 
/* 686 */     return portlist.iterator();
/*     */   }
/*     */ 
/*     */   public void setTypeMappingRegistry(TypeMappingRegistry registry)
/*     */     throws ServiceException
/*     */   {
/*     */   }
/*     */ 
/*     */   public TypeMappingRegistry getTypeMappingRegistry()
/*     */   {
/* 705 */     return this.engine.getTypeMappingRegistry();
/*     */   }
/*     */ 
/*     */   public Reference getReference()
/*     */   {
/* 714 */     String classname = getClass().getName();
/* 715 */     Reference reference = new Reference(classname, "org.apache.axis.client.ServiceFactory", null);
/*     */ 
/* 717 */     StringRefAddr addr = null;
/* 718 */     if (!classname.equals("org.apache.axis.client.Service"))
/*     */     {
/* 721 */       addr = new StringRefAddr("service classname", classname);
/*     */ 
/* 723 */       reference.add(addr);
/*     */     } else {
/* 725 */       if (this.wsdlLocation != null) {
/* 726 */         addr = new StringRefAddr("WSDL location", this.wsdlLocation.toString());
/*     */ 
/* 728 */         reference.add(addr);
/*     */       }
/* 730 */       QName serviceName = getServiceName();
/* 731 */       if (serviceName != null) {
/* 732 */         addr = new StringRefAddr("service namespace", serviceName.getNamespaceURI());
/*     */ 
/* 734 */         reference.add(addr);
/* 735 */         addr = new StringRefAddr("service local part", serviceName.getLocalPart());
/*     */ 
/* 737 */         reference.add(addr);
/*     */       }
/*     */     }
/* 740 */     if (this.maintainSession) {
/* 741 */       addr = new StringRefAddr("maintain session", "true");
/* 742 */       reference.add(addr);
/*     */     }
/* 744 */     return reference;
/*     */   }
/*     */ 
/*     */   public void setEngine(AxisEngine engine)
/*     */   {
/* 756 */     this.engine = engine;
/*     */   }
/*     */ 
/*     */   public AxisEngine getEngine()
/*     */   {
/* 768 */     return this.engine;
/*     */   }
/*     */ 
/*     */   public void setEngineConfiguration(EngineConfiguration config)
/*     */   {
/* 805 */     this.config = config;
/*     */   }
/*     */ 
/*     */   protected EngineConfiguration getEngineConfiguration()
/*     */   {
/* 812 */     if (this.config == null) {
/* 813 */       this.config = EngineConfigurationFactoryFinder.newFactory().getClientEngineConfig();
/*     */     }
/* 815 */     return this.config;
/*     */   }
/*     */ 
/*     */   public void setMaintainSession(boolean yesno)
/*     */   {
/* 830 */     this.maintainSession = yesno;
/*     */   }
/*     */ 
/*     */   public boolean getMaintainSession()
/*     */   {
/* 837 */     return this.maintainSession;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public Call getCall()
/*     */     throws ServiceException
/*     */   {
/* 848 */     return this._call;
/*     */   }
/*     */ 
/*     */   public boolean getCacheWSDL()
/*     */   {
/* 855 */     return cachingWSDL;
/*     */   }
/*     */ 
/*     */   public void setCacheWSDL(boolean flag)
/*     */   {
/* 863 */     cachingWSDL = flag;
/*     */   }
/*     */ 
/*     */   void registerTransportForURL(URL url, Transport transport)
/*     */   {
/* 890 */     this.transportImpls.put(url.toString(), transport);
/*     */   }
/*     */ 
/*     */   Transport getTransportForURL(URL url)
/*     */   {
/* 897 */     return (Transport)this.transportImpls.get(url.toString());
/*     */   }
/*     */ 
/*     */   public void setTypeMappingVersion(String version)
/*     */   {
/* 905 */     ((TypeMappingRegistryImpl)getTypeMappingRegistry()).doRegisterFromVersion(version);
/*     */   }
/*     */ 
/*     */   protected static class HandlerRegistryImpl
/*     */     implements HandlerRegistry
/*     */   {
/* 867 */     Map map = new HashMap();
/*     */ 
/*     */     public List getHandlerChain(QName portName)
/*     */     {
/* 871 */       String key = portName.getLocalPart();
/* 872 */       List list = (List)this.map.get(key);
/* 873 */       if (list == null) {
/* 874 */         list = new ArrayList();
/* 875 */         setHandlerChain(portName, list);
/*     */       }
/* 877 */       return list;
/*     */     }
/*     */ 
/*     */     public void setHandlerChain(QName portName, List chain)
/*     */     {
/* 882 */       this.map.put(portName.getLocalPart(), chain);
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.client.Service
 * JD-Core Version:    0.6.0
 */