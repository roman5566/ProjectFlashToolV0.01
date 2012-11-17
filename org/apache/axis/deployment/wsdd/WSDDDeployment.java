/*     */ package org.apache.axis.deployment.wsdd;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.AxisEngine;
/*     */ import org.apache.axis.ConfigurationException;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.Handler;
/*     */ import org.apache.axis.WSDDEngineConfiguration;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.encoding.DeserializerFactory;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.encoding.SerializerFactory;
/*     */ import org.apache.axis.encoding.TypeMapping;
/*     */ import org.apache.axis.encoding.TypeMappingRegistry;
/*     */ import org.apache.axis.encoding.TypeMappingRegistryImpl;
/*     */ import org.apache.axis.encoding.ser.ArraySerializerFactory;
/*     */ import org.apache.axis.encoding.ser.BaseDeserializerFactory;
/*     */ import org.apache.axis.encoding.ser.BaseSerializerFactory;
/*     */ import org.apache.axis.handlers.soap.SOAPService;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Element;
/*     */ 
/*     */ public class WSDDDeployment extends WSDDElement
/*     */   implements WSDDTypeMappingContainer, WSDDEngineConfiguration
/*     */ {
/*  57 */   protected static Log log = LogFactory.getLog(WSDDDeployment.class.getName());
/*     */ 
/*  59 */   private HashMap handlers = new HashMap();
/*  60 */   private HashMap services = new HashMap();
/*  61 */   private HashMap transports = new HashMap();
/*  62 */   private HashMap typeMappings = new HashMap();
/*  63 */   private WSDDGlobalConfiguration globalConfig = null;
/*     */ 
/*  67 */   private HashMap namespaceToServices = new HashMap();
/*     */   private AxisEngine engine;
/* 449 */   TypeMappingRegistry tmr = new TypeMappingRegistryImpl();
/*     */ 
/* 455 */   private boolean tmrDeployed = false;
/*     */ 
/*     */   protected void addHandler(WSDDHandler handler)
/*     */   {
/*  71 */     this.handlers.put(handler.getQName(), handler);
/*     */   }
/*     */ 
/*     */   protected void addService(WSDDService service) {
/*  75 */     WSDDService oldService = (WSDDService)this.services.get(service.getQName());
/*  76 */     if (oldService != null) {
/*  77 */       oldService.removeNamespaceMappings(this);
/*     */     }
/*  79 */     this.services.put(service.getQName(), service);
/*     */   }
/*     */ 
/*     */   protected void addTransport(WSDDTransport transport) {
/*  83 */     this.transports.put(transport.getQName(), transport);
/*     */   }
/*     */ 
/*     */   public void deployHandler(WSDDHandler handler)
/*     */   {
/*  93 */     handler.deployToRegistry(this);
/*     */   }
/*     */ 
/*     */   public void deployTransport(WSDDTransport transport)
/*     */   {
/* 103 */     transport.deployToRegistry(this);
/*     */   }
/*     */ 
/*     */   public void deployService(WSDDService service)
/*     */   {
/* 113 */     service.deployToRegistry(this);
/*     */   }
/*     */ 
/*     */   public void undeployHandler(QName qname)
/*     */   {
/* 122 */     this.handlers.remove(qname);
/*     */   }
/*     */ 
/*     */   public void undeployService(QName qname)
/*     */   {
/* 131 */     WSDDService service = (WSDDService)this.services.get(qname);
/* 132 */     if (service != null) {
/* 133 */       service.removeNamespaceMappings(this);
/* 134 */       this.services.remove(qname);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void undeployTransport(QName qname)
/*     */   {
/* 144 */     this.transports.remove(qname);
/*     */   }
/*     */ 
/*     */   public void deployTypeMapping(WSDDTypeMapping typeMapping) throws WSDDException
/*     */   {
/* 149 */     QName qname = typeMapping.getQName();
/* 150 */     String encoding = typeMapping.getEncodingStyle();
/*     */ 
/* 153 */     this.typeMappings.put(qname + encoding, typeMapping);
/* 154 */     if (this.tmrDeployed)
/* 155 */       deployMapping(typeMapping);
/*     */   }
/*     */ 
/*     */   public WSDDDeployment()
/*     */   {
/*     */   }
/*     */ 
/*     */   public WSDDDeployment(Element e)
/*     */     throws WSDDException
/*     */   {
/* 172 */     super(e);
/* 173 */     Element[] elements = getChildElements(e, "handler");
/*     */ 
/* 175 */     for (int i = 0; i < elements.length; i++) {
/* 176 */       WSDDHandler handler = new WSDDHandler(elements[i]);
/* 177 */       deployHandler(handler);
/*     */     }
/* 179 */     elements = getChildElements(e, "chain");
/* 180 */     for (i = 0; i < elements.length; i++) {
/* 181 */       WSDDChain chain = new WSDDChain(elements[i]);
/* 182 */       deployHandler(chain);
/*     */     }
/* 184 */     elements = getChildElements(e, "transport");
/* 185 */     for (i = 0; i < elements.length; i++) {
/* 186 */       WSDDTransport transport = new WSDDTransport(elements[i]);
/* 187 */       deployTransport(transport);
/*     */     }
/* 189 */     elements = getChildElements(e, "service");
/* 190 */     for (i = 0; i < elements.length; i++) {
/*     */       try {
/* 192 */         WSDDService service = new WSDDService(elements[i]);
/* 193 */         deployService(service);
/*     */       }
/*     */       catch (WSDDNonFatalException ex) {
/* 196 */         log.info(Messages.getMessage("ignoringNonFatalException00"), ex);
/*     */       }
/*     */       catch (WSDDException ex) {
/* 199 */         throw ex;
/*     */       }
/*     */     }
/* 202 */     elements = getChildElements(e, "typeMapping");
/* 203 */     for (i = 0; i < elements.length; i++) {
/*     */       try {
/* 205 */         WSDDTypeMapping mapping = new WSDDTypeMapping(elements[i]);
/* 206 */         deployTypeMapping(mapping);
/*     */       }
/*     */       catch (WSDDNonFatalException ex) {
/* 209 */         log.info(Messages.getMessage("ignoringNonFatalException00"), ex);
/*     */       }
/*     */       catch (WSDDException ex) {
/* 212 */         throw ex;
/*     */       }
/*     */     }
/* 215 */     elements = getChildElements(e, "beanMapping");
/* 216 */     for (i = 0; i < elements.length; i++) {
/* 217 */       WSDDBeanMapping mapping = new WSDDBeanMapping(elements[i]);
/* 218 */       deployTypeMapping(mapping);
/*     */     }
/*     */ 
/* 221 */     elements = getChildElements(e, "arrayMapping");
/* 222 */     for (i = 0; i < elements.length; i++) {
/* 223 */       WSDDArrayMapping mapping = new WSDDArrayMapping(elements[i]);
/*     */ 
/* 225 */       deployTypeMapping(mapping);
/*     */     }
/*     */ 
/* 228 */     Element el = getChildElement(e, "globalConfiguration");
/* 229 */     if (el != null)
/* 230 */       this.globalConfig = new WSDDGlobalConfiguration(el);
/*     */   }
/*     */ 
/*     */   protected QName getElementName() {
/* 234 */     return QNAME_DEPLOY;
/*     */   }
/*     */ 
/*     */   public void deployToRegistry(WSDDDeployment target) throws ConfigurationException
/*     */   {
/* 239 */     WSDDGlobalConfiguration global = getGlobalConfiguration();
/* 240 */     if (global != null) {
/* 241 */       target.setGlobalConfiguration(global);
/*     */     }
/* 243 */     Iterator i = this.handlers.values().iterator();
/* 244 */     while (i.hasNext()) {
/* 245 */       WSDDHandler handler = (WSDDHandler)i.next();
/* 246 */       target.deployHandler(handler);
/*     */     }
/* 248 */     i = this.transports.values().iterator();
/* 249 */     while (i.hasNext()) {
/* 250 */       WSDDTransport transport = (WSDDTransport)i.next();
/* 251 */       target.deployTransport(transport);
/*     */     }
/* 253 */     i = this.services.values().iterator();
/* 254 */     while (i.hasNext()) {
/* 255 */       WSDDService service = (WSDDService)i.next();
/* 256 */       service.deployToRegistry(target);
/*     */     }
/* 258 */     i = this.typeMappings.values().iterator();
/* 259 */     while (i.hasNext()) {
/* 260 */       WSDDTypeMapping mapping = (WSDDTypeMapping)i.next();
/* 261 */       target.deployTypeMapping(mapping);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void deployMapping(WSDDTypeMapping mapping) throws WSDDException
/*     */   {
/*     */     try {
/* 268 */       String encodingStyle = mapping.getEncodingStyle();
/* 269 */       if (encodingStyle == null) {
/* 270 */         encodingStyle = Constants.URI_DEFAULT_SOAP_ENC;
/*     */       }
/* 272 */       TypeMapping tm = this.tmr.getOrMakeTypeMapping(encodingStyle);
/* 273 */       SerializerFactory ser = null;
/* 274 */       DeserializerFactory deser = null;
/*     */ 
/* 284 */       if ((mapping.getSerializerName() != null) && (!mapping.getSerializerName().equals("")))
/*     */       {
/* 286 */         ser = BaseSerializerFactory.createFactory(mapping.getSerializer(), mapping.getLanguageSpecificType(), mapping.getQName());
/*     */       }
/*     */ 
/* 291 */       if (((mapping instanceof WSDDArrayMapping)) && ((ser instanceof ArraySerializerFactory))) {
/* 292 */         WSDDArrayMapping am = (WSDDArrayMapping)mapping;
/* 293 */         ArraySerializerFactory factory = (ArraySerializerFactory)ser;
/* 294 */         factory.setComponentType(am.getInnerType());
/*     */       }
/*     */ 
/* 299 */       if ((mapping.getDeserializerName() != null) && (!mapping.getDeserializerName().equals("")))
/*     */       {
/* 301 */         deser = BaseDeserializerFactory.createFactory(mapping.getDeserializer(), mapping.getLanguageSpecificType(), mapping.getQName());
/*     */       }
/*     */ 
/* 306 */       tm.register(mapping.getLanguageSpecificType(), mapping.getQName(), ser, deser);
/*     */     }
/*     */     catch (ClassNotFoundException e) {
/* 309 */       log.error(Messages.getMessage("unabletoDeployTypemapping00", mapping.getQName().toString()), e);
/* 310 */       throw new WSDDNonFatalException(e);
/*     */     } catch (Exception e) {
/* 312 */       throw new WSDDException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeToContext(SerializationContext context) throws IOException
/*     */   {
/* 318 */     context.registerPrefixForURI("", "http://xml.apache.org/axis/wsdd/");
/* 319 */     context.registerPrefixForURI("java", "http://xml.apache.org/axis/wsdd/providers/java");
/* 320 */     context.startElement(QNAME_DEPLOY, null);
/* 321 */     if (this.globalConfig != null) {
/* 322 */       this.globalConfig.writeToContext(context);
/*     */     }
/* 324 */     Iterator i = this.handlers.values().iterator();
/* 325 */     while (i.hasNext()) {
/* 326 */       WSDDHandler handler = (WSDDHandler)i.next();
/* 327 */       handler.writeToContext(context);
/*     */     }
/* 329 */     i = this.services.values().iterator();
/* 330 */     while (i.hasNext()) {
/* 331 */       WSDDService service = (WSDDService)i.next();
/* 332 */       service.writeToContext(context);
/*     */     }
/* 334 */     i = this.transports.values().iterator();
/* 335 */     while (i.hasNext()) {
/* 336 */       WSDDTransport transport = (WSDDTransport)i.next();
/* 337 */       transport.writeToContext(context);
/*     */     }
/* 339 */     i = this.typeMappings.values().iterator();
/* 340 */     while (i.hasNext()) {
/* 341 */       WSDDTypeMapping mapping = (WSDDTypeMapping)i.next();
/* 342 */       mapping.writeToContext(context);
/*     */     }
/* 344 */     context.endElement();
/*     */   }
/*     */ 
/*     */   public WSDDGlobalConfiguration getGlobalConfiguration()
/*     */   {
/* 353 */     return this.globalConfig;
/*     */   }
/*     */ 
/*     */   public void setGlobalConfiguration(WSDDGlobalConfiguration globalConfig) {
/* 357 */     this.globalConfig = globalConfig;
/*     */   }
/*     */ 
/*     */   public WSDDTypeMapping[] getTypeMappings()
/*     */   {
/* 364 */     WSDDTypeMapping[] t = new WSDDTypeMapping[this.typeMappings.size()];
/* 365 */     this.typeMappings.values().toArray(t);
/* 366 */     return t;
/*     */   }
/*     */ 
/*     */   public WSDDService[] getServices()
/*     */   {
/* 373 */     WSDDService[] serviceArray = new WSDDService[this.services.size()];
/* 374 */     this.services.values().toArray(serviceArray);
/* 375 */     return serviceArray;
/*     */   }
/*     */ 
/*     */   public WSDDService getWSDDService(QName qname)
/*     */   {
/* 382 */     return (WSDDService)this.services.get(qname);
/*     */   }
/*     */ 
/*     */   public Handler getHandler(QName name)
/*     */     throws ConfigurationException
/*     */   {
/* 392 */     WSDDHandler h = (WSDDHandler)this.handlers.get(name);
/* 393 */     if (h != null) {
/* 394 */       return h.getInstance(this);
/*     */     }
/* 396 */     return null;
/*     */   }
/*     */ 
/*     */   public Handler getTransport(QName name)
/*     */     throws ConfigurationException
/*     */   {
/* 408 */     WSDDTransport t = (WSDDTransport)this.transports.get(name);
/* 409 */     if (t != null) {
/* 410 */       return t.getInstance(this);
/*     */     }
/* 412 */     return null;
/*     */   }
/*     */ 
/*     */   public SOAPService getService(QName name)
/*     */     throws ConfigurationException
/*     */   {
/* 425 */     WSDDService s = (WSDDService)this.services.get(name);
/* 426 */     if (s != null) {
/* 427 */       return (SOAPService)s.getInstance(this);
/*     */     }
/* 429 */     return null;
/*     */   }
/*     */ 
/*     */   public SOAPService getServiceByNamespaceURI(String namespace) throws ConfigurationException
/*     */   {
/* 434 */     WSDDService s = (WSDDService)this.namespaceToServices.get(namespace);
/* 435 */     if (s != null) {
/* 436 */       return (SOAPService)s.getInstance(this);
/*     */     }
/* 438 */     return null;
/*     */   }
/*     */ 
/*     */   public void configureEngine(AxisEngine engine) throws ConfigurationException
/*     */   {
/* 443 */     this.engine = engine;
/*     */   }
/*     */ 
/*     */   public void writeEngineConfig(AxisEngine engine) throws ConfigurationException
/*     */   {
/*     */   }
/*     */ 
/*     */   public TypeMapping getTypeMapping(String encodingStyle) throws ConfigurationException
/*     */   {
/* 452 */     return (TypeMapping)getTypeMappingRegistry().getTypeMapping(encodingStyle);
/*     */   }
/*     */ 
/*     */   public TypeMappingRegistry getTypeMappingRegistry()
/*     */     throws ConfigurationException
/*     */   {
/* 458 */     if (false == this.tmrDeployed) {
/* 459 */       Iterator i = this.typeMappings.values().iterator();
/* 460 */       while (i.hasNext()) {
/* 461 */         WSDDTypeMapping mapping = (WSDDTypeMapping)i.next();
/* 462 */         deployMapping(mapping);
/*     */       }
/* 464 */       this.tmrDeployed = true;
/*     */     }
/* 466 */     return this.tmr;
/*     */   }
/*     */ 
/*     */   public Handler getGlobalRequest() throws ConfigurationException {
/* 470 */     if (this.globalConfig != null) {
/* 471 */       WSDDRequestFlow reqFlow = this.globalConfig.getRequestFlow();
/* 472 */       if (reqFlow != null)
/* 473 */         return reqFlow.getInstance(this);
/*     */     }
/* 475 */     return null;
/*     */   }
/*     */ 
/*     */   public Handler getGlobalResponse() throws ConfigurationException {
/* 479 */     if (this.globalConfig != null) {
/* 480 */       WSDDResponseFlow respFlow = this.globalConfig.getResponseFlow();
/* 481 */       if (respFlow != null)
/* 482 */         return respFlow.getInstance(this);
/*     */     }
/* 484 */     return null;
/*     */   }
/*     */ 
/*     */   public Hashtable getGlobalOptions() throws ConfigurationException {
/* 488 */     return this.globalConfig.getParametersTable();
/*     */   }
/*     */ 
/*     */   public List getRoles() {
/* 492 */     return this.globalConfig == null ? new ArrayList() : this.globalConfig.getRoles();
/*     */   }
/*     */ 
/*     */   public Iterator getDeployedServices()
/*     */     throws ConfigurationException
/*     */   {
/* 499 */     ArrayList serviceDescs = new ArrayList();
/* 500 */     for (Iterator i = this.services.values().iterator(); i.hasNext(); ) {
/* 501 */       WSDDService service = (WSDDService)i.next();
/*     */       try {
/* 503 */         service.makeNewInstance(this);
/* 504 */         serviceDescs.add(service.getServiceDesc());
/*     */       }
/*     */       catch (WSDDNonFatalException ex) {
/* 507 */         log.info(Messages.getMessage("ignoringNonFatalException00"), ex);
/*     */       }
/*     */     }
/* 510 */     return serviceDescs.iterator();
/*     */   }
/*     */ 
/*     */   public void registerNamespaceForService(String namespace, WSDDService service)
/*     */   {
/* 522 */     this.namespaceToServices.put(namespace, service);
/*     */   }
/*     */ 
/*     */   public void removeNamespaceMapping(String namespace)
/*     */   {
/* 531 */     this.namespaceToServices.remove(namespace);
/*     */   }
/*     */ 
/*     */   public AxisEngine getEngine() {
/* 535 */     return this.engine;
/*     */   }
/*     */ 
/*     */   public WSDDDeployment getDeployment() {
/* 539 */     return this;
/*     */   }
/*     */ 
/*     */   public WSDDHandler[] getHandlers() {
/* 543 */     WSDDHandler[] handlerArray = new WSDDHandler[this.handlers.size()];
/* 544 */     this.handlers.values().toArray(handlerArray);
/* 545 */     return handlerArray;
/*     */   }
/*     */ 
/*     */   public WSDDHandler getWSDDHandler(QName qname) {
/* 549 */     return (WSDDHandler)this.handlers.get(qname);
/*     */   }
/*     */ 
/*     */   public WSDDTransport[] getTransports() {
/* 553 */     WSDDTransport[] transportArray = new WSDDTransport[this.transports.size()];
/* 554 */     this.transports.values().toArray(transportArray);
/* 555 */     return transportArray;
/*     */   }
/*     */ 
/*     */   public WSDDTransport getWSDDTransport(QName qname) {
/* 559 */     return (WSDDTransport)this.transports.get(qname);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.WSDDDeployment
 * JD-Core Version:    0.6.0
 */