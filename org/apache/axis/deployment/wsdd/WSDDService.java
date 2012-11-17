/*     */ package org.apache.axis.deployment.wsdd;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.AxisEngine;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.ConfigurationException;
/*     */ import org.apache.axis.EngineConfiguration;
/*     */ import org.apache.axis.FaultableHandler;
/*     */ import org.apache.axis.Handler;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.attachments.AttachmentsImpl;
/*     */ import org.apache.axis.constants.Style;
/*     */ import org.apache.axis.constants.Use;
/*     */ import org.apache.axis.description.JavaServiceDesc;
/*     */ import org.apache.axis.description.ServiceDesc;
/*     */ import org.apache.axis.encoding.DeserializerFactory;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.encoding.SerializerFactory;
/*     */ import org.apache.axis.encoding.TypeMapping;
/*     */ import org.apache.axis.encoding.TypeMappingRegistry;
/*     */ import org.apache.axis.encoding.TypeMappingRegistryImpl;
/*     */ import org.apache.axis.encoding.ser.ArraySerializerFactory;
/*     */ import org.apache.axis.encoding.ser.BaseDeserializerFactory;
/*     */ import org.apache.axis.encoding.ser.BaseSerializerFactory;
/*     */ import org.apache.axis.handlers.HandlerInfoChainFactory;
/*     */ import org.apache.axis.handlers.soap.SOAPService;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Element;
/*     */ import org.xml.sax.helpers.AttributesImpl;
/*     */ 
/*     */ public class WSDDService extends WSDDTargetedChain
/*     */   implements WSDDTypeMappingContainer
/*     */ {
/*  65 */   private TypeMappingRegistry tmr = null;
/*     */ 
/*  67 */   private Vector faultFlows = new Vector();
/*  68 */   private Vector typeMappings = new Vector();
/*  69 */   private Vector operations = new Vector();
/*     */ 
/*  72 */   private Vector namespaces = new Vector();
/*     */ 
/*  75 */   private List roles = new ArrayList();
/*     */   private String descriptionURL;
/*  80 */   private Style style = Style.DEFAULT;
/*     */ 
/*  82 */   private Use use = Use.DEFAULT;
/*     */ 
/*  84 */   private transient SOAPService cachedService = null;
/*     */   private QName providerQName;
/*     */   private WSDDJAXRPCHandlerInfoChain _wsddHIchain;
/*  95 */   JavaServiceDesc desc = new JavaServiceDesc();
/*     */ 
/* 101 */   private boolean streaming = false;
/*     */ 
/* 106 */   private int sendType = 1;
/*     */ 
/*     */   public WSDDService()
/*     */   {
/*     */   }
/*     */ 
/*     */   public WSDDService(Element e)
/*     */     throws WSDDException
/*     */   {
/* 123 */     super(e);
/*     */ 
/* 125 */     this.desc.setName(getQName().getLocalPart());
/*     */ 
/* 127 */     String styleStr = e.getAttribute("style");
/* 128 */     if ((styleStr != null) && (!styleStr.equals(""))) {
/* 129 */       this.style = Style.getStyle(styleStr, Style.DEFAULT);
/* 130 */       this.desc.setStyle(this.style);
/* 131 */       this.providerQName = this.style.getProvider();
/*     */     }
/*     */ 
/* 134 */     String useStr = e.getAttribute("use");
/* 135 */     if ((useStr != null) && (!useStr.equals(""))) {
/* 136 */       this.use = Use.getUse(useStr, Use.DEFAULT);
/* 137 */       this.desc.setUse(this.use);
/*     */     }
/* 139 */     else if (this.style != Style.RPC)
/*     */     {
/* 141 */       this.use = Use.LITERAL;
/* 142 */       this.desc.setUse(this.use);
/*     */     }
/*     */ 
/* 146 */     String streamStr = e.getAttribute("streaming");
/* 147 */     if ((streamStr != null) && (streamStr.equals("on"))) {
/* 148 */       this.streaming = true;
/*     */     }
/*     */ 
/* 151 */     String attachmentStr = e.getAttribute("attachment");
/* 152 */     if ((attachmentStr != null) && (!attachmentStr.equals(""))) {
/* 153 */       this.sendType = AttachmentsImpl.getSendType(attachmentStr);
/*     */     }
/*     */ 
/* 156 */     Element[] operationElements = getChildElements(e, "operation");
/* 157 */     for (int i = 0; i < operationElements.length; i++) {
/* 158 */       WSDDOperation operation = new WSDDOperation(operationElements[i], this.desc);
/*     */ 
/* 160 */       addOperation(operation);
/*     */     }
/*     */ 
/* 163 */     Element[] typeMappingElements = getChildElements(e, "typeMapping");
/* 164 */     for (int i = 0; i < typeMappingElements.length; i++) {
/* 165 */       WSDDTypeMapping mapping = new WSDDTypeMapping(typeMappingElements[i]);
/*     */ 
/* 167 */       this.typeMappings.add(mapping);
/*     */     }
/*     */ 
/* 170 */     Element[] beanMappingElements = getChildElements(e, "beanMapping");
/* 171 */     for (int i = 0; i < beanMappingElements.length; i++) {
/* 172 */       WSDDBeanMapping mapping = new WSDDBeanMapping(beanMappingElements[i]);
/*     */ 
/* 174 */       this.typeMappings.add(mapping);
/*     */     }
/*     */ 
/* 177 */     Element[] arrayMappingElements = getChildElements(e, "arrayMapping");
/* 178 */     for (int i = 0; i < arrayMappingElements.length; i++) {
/* 179 */       WSDDArrayMapping mapping = new WSDDArrayMapping(arrayMappingElements[i]);
/*     */ 
/* 181 */       this.typeMappings.add(mapping);
/*     */     }
/*     */ 
/* 184 */     Element[] namespaceElements = getChildElements(e, "namespace");
/* 185 */     for (int i = 0; i < namespaceElements.length; i++)
/*     */     {
/* 187 */       String ns = XMLUtils.getChildCharacterData(namespaceElements[i]);
/* 188 */       this.namespaces.add(ns);
/*     */     }
/* 190 */     if (!this.namespaces.isEmpty()) {
/* 191 */       this.desc.setNamespaceMappings(this.namespaces);
/*     */     }
/* 193 */     Element[] roleElements = getChildElements(e, "role");
/* 194 */     for (int i = 0; i < roleElements.length; i++) {
/* 195 */       String role = XMLUtils.getChildCharacterData(roleElements[i]);
/* 196 */       this.roles.add(role);
/*     */     }
/*     */ 
/* 199 */     Element wsdlElem = getChildElement(e, "wsdlFile");
/* 200 */     if (wsdlElem != null) {
/* 201 */       String fileName = XMLUtils.getChildCharacterData(wsdlElem);
/* 202 */       this.desc.setWSDLFile(fileName.trim());
/*     */     }
/*     */ 
/* 205 */     Element docElem = getChildElement(e, "documentation");
/* 206 */     if (docElem != null) {
/* 207 */       WSDDDocumentation documentation = new WSDDDocumentation(docElem);
/* 208 */       this.desc.setDocumentation(documentation.getValue());
/*     */     }
/*     */ 
/* 211 */     Element urlElem = getChildElement(e, "endpointURL");
/* 212 */     if (urlElem != null) {
/* 213 */       String endpointURL = XMLUtils.getChildCharacterData(urlElem);
/* 214 */       this.desc.setEndpointURL(endpointURL);
/*     */     }
/*     */ 
/* 217 */     String providerStr = e.getAttribute("provider");
/* 218 */     if ((providerStr != null) && (!providerStr.equals(""))) {
/* 219 */       this.providerQName = XMLUtils.getQNameFromString(providerStr, e);
/* 220 */       if (WSDDConstants.QNAME_JAVAMSG_PROVIDER.equals(this.providerQName))
/*     */       {
/* 222 */         this.desc.setStyle(Style.MESSAGE);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 227 */     Element hcEl = getChildElement(e, "handlerInfoChain");
/* 228 */     if (hcEl != null) {
/* 229 */       this._wsddHIchain = new WSDDJAXRPCHandlerInfoChain(hcEl);
/*     */     }
/*     */ 
/* 233 */     initTMR();
/*     */ 
/* 236 */     validateDescriptors();
/*     */   }
/*     */ 
/*     */   protected void initTMR()
/*     */     throws WSDDException
/*     */   {
/* 250 */     if (this.tmr == null) {
/* 251 */       createTMR();
/* 252 */       for (int i = 0; i < this.typeMappings.size(); i++)
/* 253 */         deployTypeMapping((WSDDTypeMapping)this.typeMappings.get(i));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void createTMR()
/*     */   {
/* 260 */     this.tmr = new TypeMappingRegistryImpl(false);
/* 261 */     String version = getParameter("typeMappingVersion");
/* 262 */     ((TypeMappingRegistryImpl)this.tmr).doRegisterFromVersion(version);
/*     */   }
/*     */ 
/*     */   public void validateDescriptors()
/*     */     throws WSDDException
/*     */   {
/* 272 */     if (this.tmr == null) {
/* 273 */       initTMR();
/*     */     }
/* 275 */     this.desc.setTypeMappingRegistry(this.tmr);
/* 276 */     this.desc.setTypeMapping(getTypeMapping(this.desc.getUse().getEncoding()));
/*     */ 
/* 278 */     String allowedMethods = getParameter("allowedMethods");
/* 279 */     if ((allowedMethods != null) && (!"*".equals(allowedMethods))) {
/* 280 */       ArrayList methodList = new ArrayList();
/* 281 */       StringTokenizer tokenizer = new StringTokenizer(allowedMethods, " ,");
/* 282 */       while (tokenizer.hasMoreTokens()) {
/* 283 */         methodList.add(tokenizer.nextToken());
/*     */       }
/* 285 */       this.desc.setAllowedMethods(methodList);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addTypeMapping(WSDDTypeMapping mapping)
/*     */   {
/* 294 */     this.typeMappings.add(mapping);
/*     */   }
/*     */ 
/*     */   public void addOperation(WSDDOperation operation)
/*     */   {
/* 302 */     this.operations.add(operation);
/* 303 */     this.desc.addOperationDesc(operation.getOperationDesc());
/*     */   }
/*     */ 
/*     */   protected QName getElementName()
/*     */   {
/* 308 */     return QNAME_SERVICE;
/*     */   }
/*     */ 
/*     */   public String getServiceDescriptionURL()
/*     */   {
/* 319 */     return this.descriptionURL;
/*     */   }
/*     */ 
/*     */   public void setServiceDescriptionURL(String sdUrl)
/*     */   {
/* 329 */     this.descriptionURL = sdUrl;
/*     */   }
/*     */ 
/*     */   public QName getProviderQName() {
/* 333 */     return this.providerQName;
/*     */   }
/*     */ 
/*     */   public void setProviderQName(QName providerQName) {
/* 337 */     this.providerQName = providerQName;
/*     */   }
/*     */ 
/*     */   public ServiceDesc getServiceDesc() {
/* 341 */     return this.desc;
/*     */   }
/*     */ 
/*     */   public Style getStyle()
/*     */   {
/* 348 */     return this.style;
/*     */   }
/*     */ 
/*     */   public void setStyle(Style style)
/*     */   {
/* 355 */     this.style = style;
/*     */   }
/*     */ 
/*     */   public Use getUse()
/*     */   {
/* 362 */     return this.use;
/*     */   }
/*     */ 
/*     */   public void setUse(Use use)
/*     */   {
/* 369 */     this.use = use;
/*     */   }
/*     */ 
/*     */   public WSDDFaultFlow[] getFaultFlows()
/*     */   {
/* 377 */     WSDDFaultFlow[] t = new WSDDFaultFlow[this.faultFlows.size()];
/* 378 */     this.faultFlows.toArray(t);
/* 379 */     return t;
/*     */   }
/*     */ 
/*     */   public Vector getNamespaces()
/*     */   {
/* 389 */     return this.namespaces;
/*     */   }
/*     */ 
/*     */   public WSDDFaultFlow getFaultFlow(QName name)
/*     */   {
/* 399 */     WSDDFaultFlow[] t = getFaultFlows();
/*     */ 
/* 401 */     for (int n = 0; n < t.length; n++) {
/* 402 */       if (t[n].getQName().equals(name)) {
/* 403 */         return t[n];
/*     */       }
/*     */     }
/*     */ 
/* 407 */     return null;
/*     */   }
/*     */ 
/*     */   public Handler makeNewInstance(EngineConfiguration registry)
/*     */     throws ConfigurationException
/*     */   {
/* 419 */     if (this.cachedService != null) {
/* 420 */       return this.cachedService;
/*     */     }
/*     */ 
/* 424 */     initTMR();
/*     */ 
/* 426 */     Handler reqHandler = null;
/* 427 */     WSDDChain request = getRequestFlow();
/*     */ 
/* 429 */     if (request != null) {
/* 430 */       reqHandler = request.getInstance(registry);
/*     */     }
/*     */ 
/* 433 */     Handler providerHandler = null;
/*     */ 
/* 435 */     if (this.providerQName != null) {
/*     */       try {
/* 437 */         providerHandler = WSDDProvider.getInstance(this.providerQName, this, registry);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 441 */         throw new ConfigurationException(e);
/*     */       }
/* 443 */       if (providerHandler == null) {
/* 444 */         throw new WSDDException(Messages.getMessage("couldntConstructProvider00"));
/*     */       }
/*     */     }
/*     */ 
/* 448 */     Handler respHandler = null;
/* 449 */     WSDDChain response = getResponseFlow();
/*     */ 
/* 451 */     if (response != null) {
/* 452 */       respHandler = response.getInstance(registry);
/*     */     }
/*     */ 
/* 455 */     SOAPService service = new SOAPService(reqHandler, providerHandler, respHandler);
/*     */ 
/* 457 */     service.setStyle(this.style);
/* 458 */     service.setUse(this.use);
/* 459 */     service.setServiceDescription(this.desc);
/*     */ 
/* 461 */     service.setHighFidelityRecording(!this.streaming);
/* 462 */     service.setSendType(this.sendType);
/*     */ 
/* 464 */     if (getQName() != null)
/* 465 */       service.setName(getQName().getLocalPart());
/* 466 */     service.setOptions(getParametersTable());
/*     */ 
/* 468 */     service.setRoles(this.roles);
/*     */ 
/* 470 */     service.setEngine(((WSDDDeployment)registry).getEngine());
/*     */ 
/* 472 */     if (this.use != Use.ENCODED)
/*     */     {
/* 475 */       service.setOption("sendMultiRefs", Boolean.FALSE);
/* 476 */       service.setOption("sendXsiTypes", Boolean.FALSE);
/*     */     }
/*     */ 
/* 480 */     if (this._wsddHIchain != null) {
/* 481 */       HandlerInfoChainFactory hiChainFactory = this._wsddHIchain.getHandlerChainFactory();
/*     */ 
/* 483 */       service.setOption("handlerInfoChain", hiChainFactory);
/*     */     }
/*     */ 
/* 486 */     AxisEngine.normaliseOptions(service);
/*     */ 
/* 488 */     WSDDFaultFlow[] faultFlows = getFaultFlows();
/* 489 */     if ((faultFlows != null) && (faultFlows.length > 0)) {
/* 490 */       FaultableHandler wrapper = new FaultableHandler(service);
/* 491 */       for (int i = 0; i < faultFlows.length; i++) {
/* 492 */         WSDDFaultFlow flow = faultFlows[i];
/* 493 */         Handler faultHandler = flow.getInstance(registry);
/* 494 */         wrapper.setOption("fault-" + flow.getQName().getLocalPart(), faultHandler);
/*     */       }
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 500 */       service.getInitializedServiceDesc(MessageContext.getCurrentContext());
/*     */     } catch (AxisFault axisFault) {
/* 502 */       throw new ConfigurationException(axisFault);
/*     */     }
/*     */ 
/* 505 */     this.cachedService = service;
/* 506 */     return service;
/*     */   }
/*     */ 
/*     */   public void deployTypeMapping(WSDDTypeMapping mapping)
/*     */     throws WSDDException
/*     */   {
/* 512 */     if (!this.typeMappings.contains(mapping)) {
/* 513 */       this.typeMappings.add(mapping);
/*     */     }
/* 515 */     if (this.tmr == null) {
/* 516 */       createTMR();
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 521 */       String encodingStyle = mapping.getEncodingStyle();
/* 522 */       if (encodingStyle == null) {
/* 523 */         encodingStyle = this.use.getEncoding();
/*     */       }
/* 525 */       TypeMapping tm = this.tmr.getOrMakeTypeMapping(encodingStyle);
/* 526 */       this.desc.setTypeMappingRegistry(this.tmr);
/* 527 */       this.desc.setTypeMapping(tm);
/*     */ 
/* 529 */       SerializerFactory ser = null;
/* 530 */       DeserializerFactory deser = null;
/*     */ 
/* 540 */       if ((mapping.getSerializerName() != null) && (!mapping.getSerializerName().equals("")))
/*     */       {
/* 542 */         ser = BaseSerializerFactory.createFactory(mapping.getSerializer(), mapping.getLanguageSpecificType(), mapping.getQName());
/*     */       }
/*     */ 
/* 546 */       if (((mapping instanceof WSDDArrayMapping)) && ((ser instanceof ArraySerializerFactory))) {
/* 547 */         WSDDArrayMapping am = (WSDDArrayMapping)mapping;
/* 548 */         ArraySerializerFactory factory = (ArraySerializerFactory)ser;
/* 549 */         factory.setComponentType(am.getInnerType());
/*     */       }
/*     */ 
/* 552 */       if ((mapping.getDeserializerName() != null) && (!mapping.getDeserializerName().equals("")))
/*     */       {
/* 554 */         deser = BaseDeserializerFactory.createFactory(mapping.getDeserializer(), mapping.getLanguageSpecificType(), mapping.getQName());
/*     */       }
/*     */ 
/* 558 */       tm.register(mapping.getLanguageSpecificType(), mapping.getQName(), ser, deser);
/*     */     } catch (ClassNotFoundException e) {
/* 560 */       log.error(Messages.getMessage("unabletoDeployTypemapping00", mapping.getQName().toString()), e);
/* 561 */       throw new WSDDNonFatalException(e);
/*     */     } catch (Exception e) {
/* 563 */       throw new WSDDException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeToContext(SerializationContext context)
/*     */     throws IOException
/*     */   {
/* 572 */     AttributesImpl attrs = new AttributesImpl();
/* 573 */     QName name = getQName();
/* 574 */     if (name != null) {
/* 575 */       attrs.addAttribute("", "name", "name", "CDATA", context.qName2String(name));
/*     */     }
/*     */ 
/* 578 */     if (this.providerQName != null) {
/* 579 */       attrs.addAttribute("", "provider", "provider", "CDATA", context.qName2String(this.providerQName));
/*     */     }
/*     */ 
/* 582 */     if (this.style != Style.DEFAULT) {
/* 583 */       attrs.addAttribute("", "style", "style", "CDATA", this.style.getName());
/*     */     }
/*     */ 
/* 587 */     if (this.use != Use.DEFAULT) {
/* 588 */       attrs.addAttribute("", "use", "use", "CDATA", this.use.getName());
/*     */     }
/*     */ 
/* 592 */     if (this.streaming) {
/* 593 */       attrs.addAttribute("", "streaming", "streaming", "CDATA", "on");
/*     */     }
/*     */ 
/* 597 */     if (this.sendType != 1) {
/* 598 */       attrs.addAttribute("", "attachment", "attachment", "CDATA", AttachmentsImpl.getSendTypeString(this.sendType));
/*     */     }
/*     */ 
/* 602 */     context.startElement(WSDDConstants.QNAME_SERVICE, attrs);
/*     */ 
/* 604 */     if (this.desc.getWSDLFile() != null) {
/* 605 */       context.startElement(QNAME_WSDLFILE, null);
/* 606 */       context.writeSafeString(this.desc.getWSDLFile());
/* 607 */       context.endElement();
/*     */     }
/*     */ 
/* 610 */     if (this.desc.getDocumentation() != null) {
/* 611 */       WSDDDocumentation documentation = new WSDDDocumentation(this.desc.getDocumentation());
/* 612 */       documentation.writeToContext(context);
/*     */     }
/*     */ 
/* 615 */     for (int i = 0; i < this.operations.size(); i++) {
/* 616 */       WSDDOperation operation = (WSDDOperation)this.operations.elementAt(i);
/* 617 */       operation.writeToContext(context);
/*     */     }
/* 619 */     writeFlowsToContext(context);
/* 620 */     writeParamsToContext(context);
/*     */ 
/* 623 */     for (int i = 0; i < this.typeMappings.size(); i++) {
/* 624 */       ((WSDDTypeMapping)this.typeMappings.elementAt(i)).writeToContext(context);
/*     */     }
/*     */ 
/* 627 */     for (int i = 0; i < this.namespaces.size(); i++) {
/* 628 */       context.startElement(QNAME_NAMESPACE, null);
/* 629 */       context.writeString((String)this.namespaces.get(i));
/* 630 */       context.endElement();
/*     */     }
/*     */ 
/* 633 */     String endpointURL = this.desc.getEndpointURL();
/* 634 */     if (endpointURL != null) {
/* 635 */       context.startElement(QNAME_ENDPOINTURL, null);
/* 636 */       context.writeSafeString(endpointURL);
/* 637 */       context.endElement();
/*     */     }
/*     */ 
/* 640 */     if (this._wsddHIchain != null) {
/* 641 */       this._wsddHIchain.writeToContext(context);
/*     */     }
/*     */ 
/* 645 */     context.endElement();
/*     */   }
/*     */ 
/*     */   public void setCachedService(SOAPService service)
/*     */   {
/* 652 */     this.cachedService = service;
/*     */   }
/*     */ 
/*     */   public Vector getTypeMappings() {
/* 656 */     return this.typeMappings;
/*     */   }
/*     */ 
/*     */   public void setTypeMappings(Vector typeMappings) {
/* 660 */     this.typeMappings = typeMappings;
/*     */   }
/*     */ 
/*     */   public void deployToRegistry(WSDDDeployment registry)
/*     */   {
/* 665 */     registry.addService(this);
/*     */ 
/* 669 */     registry.registerNamespaceForService(getQName().getLocalPart(), this);
/*     */ 
/* 671 */     for (int i = 0; i < this.namespaces.size(); i++) {
/* 672 */       String namespace = (String)this.namespaces.elementAt(i);
/* 673 */       registry.registerNamespaceForService(namespace, this);
/*     */     }
/*     */ 
/* 676 */     super.deployToRegistry(registry);
/*     */   }
/*     */ 
/*     */   public void removeNamespaceMappings(WSDDDeployment registry)
/*     */   {
/* 681 */     for (int i = 0; i < this.namespaces.size(); i++) {
/* 682 */       String namespace = (String)this.namespaces.elementAt(i);
/* 683 */       registry.removeNamespaceMapping(namespace);
/*     */     }
/* 685 */     registry.removeNamespaceMapping(getQName().getLocalPart());
/*     */   }
/*     */ 
/*     */   public TypeMapping getTypeMapping(String encodingStyle)
/*     */   {
/* 690 */     if (this.tmr == null) {
/* 691 */       return null;
/*     */     }
/* 693 */     return this.tmr.getOrMakeTypeMapping(encodingStyle);
/*     */   }
/*     */ 
/*     */   public WSDDJAXRPCHandlerInfoChain getHandlerInfoChain()
/*     */   {
/* 698 */     return this._wsddHIchain;
/*     */   }
/*     */ 
/*     */   public void setHandlerInfoChain(WSDDJAXRPCHandlerInfoChain hichain) {
/* 702 */     this._wsddHIchain = hichain;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.WSDDService
 * JD-Core Version:    0.6.0
 */