/*     */ package org.apache.axis.configuration;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.AxisEngine;
/*     */ import org.apache.axis.ConfigurationException;
/*     */ import org.apache.axis.EngineConfiguration;
/*     */ import org.apache.axis.Handler;
/*     */ import org.apache.axis.encoding.TypeMapping;
/*     */ import org.apache.axis.encoding.TypeMappingRegistry;
/*     */ import org.apache.axis.encoding.TypeMappingRegistryImpl;
/*     */ import org.apache.axis.handlers.soap.SOAPService;
/*     */ 
/*     */ public class SimpleProvider
/*     */   implements EngineConfiguration
/*     */ {
/*  51 */   HashMap handlers = new HashMap();
/*     */ 
/*  53 */   HashMap transports = new HashMap();
/*     */ 
/*  55 */   HashMap services = new HashMap();
/*     */ 
/*  58 */   Hashtable globalOptions = null;
/*  59 */   Handler globalRequest = null;
/*  60 */   Handler globalResponse = null;
/*  61 */   List roles = new ArrayList();
/*     */ 
/*  64 */   TypeMappingRegistry tmr = null;
/*     */ 
/*  67 */   EngineConfiguration defaultConfiguration = null;
/*     */   private AxisEngine engine;
/*     */ 
/*     */   public SimpleProvider()
/*     */   {
/*     */   }
/*     */ 
/*     */   public SimpleProvider(EngineConfiguration defaultConfiguration)
/*     */   {
/*  81 */     this.defaultConfiguration = defaultConfiguration;
/*     */   }
/*     */ 
/*     */   public SimpleProvider(TypeMappingRegistry typeMappingRegistry)
/*     */   {
/*  90 */     this.tmr = typeMappingRegistry;
/*     */   }
/*     */ 
/*     */   public void configureEngine(AxisEngine engine)
/*     */     throws ConfigurationException
/*     */   {
/*  99 */     this.engine = engine;
/*     */ 
/* 101 */     if (this.defaultConfiguration != null) {
/* 102 */       this.defaultConfiguration.configureEngine(engine);
/*     */     }
/* 104 */     for (Iterator i = this.services.values().iterator(); i.hasNext(); )
/* 105 */       ((SOAPService)i.next()).setEngine(engine);
/*     */   }
/*     */ 
/*     */   public void writeEngineConfig(AxisEngine engine)
/*     */     throws ConfigurationException
/*     */   {
/*     */   }
/*     */ 
/*     */   public Hashtable getGlobalOptions()
/*     */     throws ConfigurationException
/*     */   {
/* 120 */     if (this.globalOptions != null) {
/* 121 */       return this.globalOptions;
/*     */     }
/* 123 */     if (this.defaultConfiguration != null) {
/* 124 */       return this.defaultConfiguration.getGlobalOptions();
/*     */     }
/* 126 */     return null;
/*     */   }
/*     */ 
/*     */   public void setGlobalOptions(Hashtable options)
/*     */   {
/* 135 */     this.globalOptions = options;
/*     */   }
/*     */ 
/*     */   public Handler getGlobalRequest()
/*     */     throws ConfigurationException
/*     */   {
/* 142 */     if (this.globalRequest != null) {
/* 143 */       return this.globalRequest;
/*     */     }
/* 145 */     if (this.defaultConfiguration != null) {
/* 146 */       return this.defaultConfiguration.getGlobalRequest();
/*     */     }
/* 148 */     return null;
/*     */   }
/*     */ 
/*     */   public void setGlobalRequest(Handler globalRequest)
/*     */   {
/* 157 */     this.globalRequest = globalRequest;
/*     */   }
/*     */ 
/*     */   public Handler getGlobalResponse()
/*     */     throws ConfigurationException
/*     */   {
/* 164 */     if (this.globalResponse != null) {
/* 165 */       return this.globalResponse;
/*     */     }
/* 167 */     if (this.defaultConfiguration != null) {
/* 168 */       return this.defaultConfiguration.getGlobalResponse();
/*     */     }
/* 170 */     return null;
/*     */   }
/*     */ 
/*     */   public void setGlobalResponse(Handler globalResponse)
/*     */   {
/* 179 */     this.globalResponse = globalResponse;
/*     */   }
/*     */ 
/*     */   public TypeMappingRegistry getTypeMappingRegistry()
/*     */     throws ConfigurationException
/*     */   {
/* 189 */     if (this.tmr != null) {
/* 190 */       return this.tmr;
/*     */     }
/* 192 */     if (this.defaultConfiguration != null) {
/* 193 */       return this.defaultConfiguration.getTypeMappingRegistry();
/*     */     }
/*     */ 
/* 197 */     this.tmr = new TypeMappingRegistryImpl();
/* 198 */     return this.tmr;
/*     */   }
/*     */ 
/*     */   public TypeMapping getTypeMapping(String encodingStyle) throws ConfigurationException {
/* 202 */     return (TypeMapping)getTypeMappingRegistry().getTypeMapping(encodingStyle);
/*     */   }
/*     */ 
/*     */   public Handler getTransport(QName qname) throws ConfigurationException {
/* 206 */     Handler transport = (Handler)this.transports.get(qname);
/* 207 */     if ((this.defaultConfiguration != null) && (transport == null))
/* 208 */       transport = this.defaultConfiguration.getTransport(qname);
/* 209 */     return transport;
/*     */   }
/*     */ 
/*     */   public SOAPService getService(QName qname) throws ConfigurationException {
/* 213 */     SOAPService service = (SOAPService)this.services.get(qname);
/* 214 */     if ((this.defaultConfiguration != null) && (service == null))
/* 215 */       service = this.defaultConfiguration.getService(qname);
/* 216 */     return service;
/*     */   }
/*     */ 
/*     */   public SOAPService getServiceByNamespaceURI(String namespace)
/*     */     throws ConfigurationException
/*     */   {
/* 227 */     SOAPService service = (SOAPService)this.services.get(new QName("", namespace));
/* 228 */     if ((service == null) && (this.defaultConfiguration != null))
/* 229 */       service = this.defaultConfiguration.getServiceByNamespaceURI(namespace);
/* 230 */     return service;
/*     */   }
/*     */ 
/*     */   public Handler getHandler(QName qname) throws ConfigurationException {
/* 234 */     Handler handler = (Handler)this.handlers.get(qname);
/* 235 */     if ((this.defaultConfiguration != null) && (handler == null))
/* 236 */       handler = this.defaultConfiguration.getHandler(qname);
/* 237 */     return handler;
/*     */   }
/*     */ 
/*     */   public void deployService(QName qname, SOAPService service)
/*     */   {
/* 242 */     this.services.put(qname, service);
/* 243 */     if (this.engine != null)
/* 244 */       service.setEngine(this.engine);
/*     */   }
/*     */ 
/*     */   public void deployService(String name, SOAPService service)
/*     */   {
/* 249 */     deployService(new QName(null, name), service);
/*     */   }
/*     */ 
/*     */   public void deployTransport(QName qname, Handler transport)
/*     */   {
/* 254 */     this.transports.put(qname, transport);
/*     */   }
/*     */ 
/*     */   public void deployTransport(String name, Handler transport)
/*     */   {
/* 259 */     deployTransport(new QName(null, name), transport);
/*     */   }
/*     */ 
/*     */   public Iterator getDeployedServices()
/*     */     throws ConfigurationException
/*     */   {
/* 266 */     ArrayList serviceDescs = new ArrayList();
/* 267 */     Iterator i = this.services.values().iterator();
/* 268 */     while (i.hasNext()) {
/* 269 */       SOAPService service = (SOAPService)i.next();
/* 270 */       serviceDescs.add(service.getServiceDescription());
/*     */     }
/* 272 */     return serviceDescs.iterator();
/*     */   }
/*     */ 
/*     */   public void setRoles(List roles)
/*     */   {
/* 284 */     this.roles = roles;
/*     */   }
/*     */ 
/*     */   public void addRole(String role)
/*     */   {
/* 293 */     this.roles.add(role);
/*     */   }
/*     */ 
/*     */   public void removeRole(String role)
/*     */   {
/* 302 */     this.roles.remove(role);
/*     */   }
/*     */ 
/*     */   public List getRoles()
/*     */   {
/* 312 */     return this.roles;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.configuration.SimpleProvider
 * JD-Core Version:    0.6.0
 */