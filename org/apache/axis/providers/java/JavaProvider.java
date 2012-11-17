/*     */ package org.apache.axis.providers.java;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
/*     */ import javax.wsdl.OperationType;
/*     */ import javax.xml.rpc.holders.IntHolder;
/*     */ import javax.xml.rpc.server.ServiceLifecycle;
/*     */ import org.apache.axis.AxisEngine;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.Handler;
/*     */ import org.apache.axis.Message;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.constants.Scope;
/*     */ import org.apache.axis.description.JavaServiceDesc;
/*     */ import org.apache.axis.description.OperationDesc;
/*     */ import org.apache.axis.handlers.soap.SOAPService;
/*     */ import org.apache.axis.message.SOAPEnvelope;
/*     */ import org.apache.axis.providers.BasicProvider;
/*     */ import org.apache.axis.session.Session;
/*     */ import org.apache.axis.utils.ClassUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.apache.axis.utils.cache.ClassCache;
/*     */ import org.apache.axis.utils.cache.JavaClass;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public abstract class JavaProvider extends BasicProvider
/*     */ {
/*  59 */   protected static Log log = LogFactory.getLog(JavaProvider.class.getName());
/*     */ 
/*  65 */   protected static Log entLog = LogFactory.getLog("org.apache.axis.enterprise");
/*     */   public static final String OPTION_CLASSNAME = "className";
/*     */   public static final String OPTION_ALLOWEDMETHODS = "allowedMethods";
/*     */   public static final String OPTION_SCOPE = "scope";
/*     */ 
/*     */   public Object getServiceObject(MessageContext msgContext, Handler service, String clsName, IntHolder scopeHolder)
/*     */     throws Exception
/*     */   {
/*  82 */     String serviceName = msgContext.getService().getName();
/*     */ 
/*  85 */     Scope scope = Scope.getScope((String)service.getOption("scope"), Scope.DEFAULT);
/*     */ 
/*  87 */     scopeHolder.value = scope.getValue();
/*     */ 
/*  89 */     if (scope == Scope.REQUEST)
/*     */     {
/*  91 */       return getNewServiceObject(msgContext, clsName);
/*  92 */     }if (scope == Scope.SESSION)
/*     */     {
/*  94 */       if (serviceName == null) {
/*  95 */         serviceName = msgContext.getService().toString();
/*     */       }
/*     */ 
/*  98 */       Session session = msgContext.getSession();
/*  99 */       if (session != null) {
/* 100 */         return getSessionServiceObject(session, serviceName, msgContext, clsName);
/*     */       }
/*     */ 
/* 104 */       scopeHolder.value = Scope.DEFAULT.getValue();
/* 105 */       return getNewServiceObject(msgContext, clsName);
/*     */     }
/* 107 */     if (scope == Scope.APPLICATION)
/*     */     {
/* 109 */       return getApplicationScopedObject(msgContext, serviceName, clsName, scopeHolder);
/* 110 */     }if (scope == Scope.FACTORY) {
/* 111 */       String objectID = msgContext.getStrProp("objectID");
/* 112 */       if (objectID == null) {
/* 113 */         return getApplicationScopedObject(msgContext, serviceName, clsName, scopeHolder);
/*     */       }
/* 115 */       SOAPService svc = (SOAPService)service;
/* 116 */       Object ret = svc.serviceObjects.get(objectID);
/* 117 */       if (ret == null) {
/* 118 */         throw new AxisFault("NoSuchObject", null, null, null);
/*     */       }
/* 120 */       return ret;
/*     */     }
/*     */ 
/* 124 */     return null;
/*     */   }
/*     */ 
/*     */   private Object getApplicationScopedObject(MessageContext msgContext, String serviceName, String clsName, IntHolder scopeHolder) throws Exception {
/* 128 */     AxisEngine engine = msgContext.getAxisEngine();
/* 129 */     Session appSession = engine.getApplicationSession();
/* 130 */     if (appSession != null) {
/* 131 */       return getSessionServiceObject(appSession, serviceName, msgContext, clsName);
/*     */     }
/*     */ 
/* 136 */     log.error(Messages.getMessage("noAppSession"));
/* 137 */     scopeHolder.value = Scope.DEFAULT.getValue();
/* 138 */     return getNewServiceObject(msgContext, clsName);
/*     */   }
/*     */ 
/*     */   private Object getSessionServiceObject(Session session, String serviceName, MessageContext msgContext, String clsName)
/*     */     throws Exception
/*     */   {
/* 170 */     Object obj = null;
/* 171 */     boolean makeNewObject = false;
/*     */ 
/* 174 */     synchronized (session.getLockObject())
/*     */     {
/* 176 */       obj = session.get(serviceName);
/*     */ 
/* 181 */       if (obj == null) {
/* 182 */         obj = new LockObject();
/* 183 */         makeNewObject = true;
/* 184 */         session.set(serviceName, obj);
/* 185 */         msgContext.getService().addSession(session);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 193 */     if (LockObject.class == obj.getClass()) {
/* 194 */       LockObject lock = (LockObject)obj;
/*     */ 
/* 199 */       if (makeNewObject) {
/*     */         try {
/* 201 */           obj = getNewServiceObject(msgContext, clsName);
/* 202 */           session.set(serviceName, obj);
/* 203 */           msgContext.getService().addSession(session);
/*     */         } catch (Exception e) {
/* 205 */           session.remove(serviceName);
/* 206 */           throw e;
/*     */         } finally {
/* 208 */           lock.complete();
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 213 */         lock.waitUntilComplete();
/*     */ 
/* 217 */         obj = session.get(serviceName);
/*     */       }
/*     */     }
/*     */ 
/* 221 */     return obj;
/*     */   }
/*     */ 
/*     */   private Object getNewServiceObject(MessageContext msgContext, String clsName)
/*     */     throws Exception
/*     */   {
/* 235 */     Object serviceObject = makeNewServiceObject(msgContext, clsName);
/* 236 */     if ((serviceObject != null) && ((serviceObject instanceof ServiceLifecycle)))
/*     */     {
/* 238 */       ((ServiceLifecycle)serviceObject).init(msgContext.getProperty("servletEndpointContext"));
/*     */     }
/*     */ 
/* 241 */     return serviceObject;
/*     */   }
/*     */ 
/*     */   public abstract void processMessage(MessageContext paramMessageContext, SOAPEnvelope paramSOAPEnvelope1, SOAPEnvelope paramSOAPEnvelope2, Object paramObject)
/*     */     throws Exception;
/*     */ 
/*     */   public void invoke(MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/* 265 */     if (log.isDebugEnabled()) {
/* 266 */       log.debug("Enter: JavaProvider::invoke (" + this + ")");
/*     */     }
/*     */ 
/* 270 */     String serviceName = msgContext.getTargetService();
/* 271 */     Handler service = msgContext.getService();
/*     */ 
/* 275 */     String clsName = getServiceClassName(service);
/*     */ 
/* 277 */     if ((clsName == null) || (clsName.equals(""))) {
/* 278 */       throw new AxisFault("Server.NoClassForService", Messages.getMessage("noOption00", getServiceClassNameOptionName(), serviceName), null, null);
/*     */     }
/*     */ 
/* 283 */     IntHolder scope = new IntHolder();
/* 284 */     Object serviceObject = null;
/*     */     try
/*     */     {
/* 287 */       serviceObject = getServiceObject(msgContext, service, clsName, scope);
/*     */ 
/* 289 */       SOAPEnvelope resEnv = null;
/*     */ 
/* 298 */       OperationDesc operation = msgContext.getOperation();
/* 299 */       if ((operation != null) && (OperationType.ONE_WAY.equals(operation.getMep())))
/*     */       {
/* 301 */         msgContext.setResponseMessage(null);
/*     */       } else {
/* 303 */         Message resMsg = msgContext.getResponseMessage();
/*     */ 
/* 307 */         if (resMsg == null) {
/* 308 */           resEnv = new SOAPEnvelope(msgContext.getSOAPConstants(), msgContext.getSchemaVersion());
/*     */ 
/* 311 */           resMsg = new Message(resEnv);
/* 312 */           String encoding = XMLUtils.getEncoding(msgContext);
/* 313 */           resMsg.setProperty("javax.xml.soap.character-set-encoding", encoding);
/* 314 */           msgContext.setResponseMessage(resMsg);
/*     */         } else {
/* 316 */           resEnv = resMsg.getSOAPEnvelope();
/*     */         }
/*     */       }
/*     */ 
/* 320 */       Message reqMsg = msgContext.getRequestMessage();
/* 321 */       SOAPEnvelope reqEnv = reqMsg.getSOAPEnvelope();
/*     */ 
/* 323 */       processMessage(msgContext, reqEnv, resEnv, serviceObject);
/*     */     } catch (SAXException exp) {
/* 325 */       entLog.debug(Messages.getMessage("toAxisFault00"), exp);
/* 326 */       Exception real = exp.getException();
/* 327 */       if (real == null) {
/* 328 */         real = exp;
/*     */       }
/* 330 */       throw AxisFault.makeFault(real);
/*     */     } catch (Exception exp) {
/* 332 */       entLog.debug(Messages.getMessage("toAxisFault00"), exp);
/* 333 */       AxisFault fault = AxisFault.makeFault(exp);
/*     */ 
/* 335 */       if ((exp instanceof RuntimeException)) {
/* 336 */         fault.addFaultDetail(Constants.QNAME_FAULTDETAIL_RUNTIMEEXCEPTION, "true");
/*     */       }
/*     */ 
/* 339 */       throw fault;
/*     */     }
/*     */     finally
/*     */     {
/* 343 */       if ((serviceObject != null) && (scope.value == Scope.REQUEST.getValue()) && ((serviceObject instanceof ServiceLifecycle)))
/*     */       {
/* 347 */         ((ServiceLifecycle)serviceObject).destroy();
/*     */       }
/*     */     }
/*     */ 
/* 351 */     if (log.isDebugEnabled())
/* 352 */       log.debug("Exit: JavaProvider::invoke (" + this + ")");
/*     */   }
/*     */ 
/*     */   private String getAllowedMethods(Handler service)
/*     */   {
/* 357 */     String val = (String)service.getOption("allowedMethods");
/* 358 */     if ((val == null) || (val.length() == 0))
/*     */     {
/* 360 */       val = (String)service.getOption("methodName");
/*     */     }
/* 362 */     return val;
/*     */   }
/*     */ 
/*     */   protected Object makeNewServiceObject(MessageContext msgContext, String clsName)
/*     */     throws Exception
/*     */   {
/* 381 */     ClassLoader cl = msgContext.getClassLoader();
/* 382 */     ClassCache cache = msgContext.getAxisEngine().getClassCache();
/* 383 */     JavaClass jc = cache.lookup(clsName, cl);
/*     */ 
/* 385 */     return jc.getJavaClass().newInstance();
/*     */   }
/*     */ 
/*     */   protected String getServiceClassName(Handler service)
/*     */   {
/* 393 */     return (String)service.getOption(getServiceClassNameOptionName());
/*     */   }
/*     */ 
/*     */   protected String getServiceClassNameOptionName()
/*     */   {
/* 402 */     return "className";
/*     */   }
/*     */ 
/*     */   protected Class getServiceClass(String clsName, SOAPService service, MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/* 412 */     ClassLoader cl = null;
/* 413 */     Class serviceClass = null;
/* 414 */     AxisEngine engine = service.getEngine();
/*     */ 
/* 418 */     if (msgContext != null)
/* 419 */       cl = msgContext.getClassLoader();
/*     */     else {
/* 421 */       cl = Thread.currentThread().getContextClassLoader();
/*     */     }
/*     */ 
/* 425 */     if (engine != null) {
/* 426 */       ClassCache cache = engine.getClassCache();
/*     */       try {
/* 428 */         JavaClass jc = cache.lookup(clsName, cl);
/* 429 */         serviceClass = jc.getJavaClass();
/*     */       } catch (ClassNotFoundException e) {
/* 431 */         log.error(Messages.getMessage("exception00"), e);
/* 432 */         throw new AxisFault(Messages.getMessage("noClassForService00", clsName), e);
/*     */       }
/*     */     }
/*     */     else {
/*     */       try {
/* 437 */         serviceClass = ClassUtils.forName(clsName, true, cl);
/*     */       } catch (ClassNotFoundException e) {
/* 439 */         log.error(Messages.getMessage("exception00"), e);
/* 440 */         throw new AxisFault(Messages.getMessage("noClassForService00", clsName), e);
/*     */       }
/*     */     }
/* 443 */     return serviceClass;
/*     */   }
/*     */ 
/*     */   public void initServiceDesc(SOAPService service, MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/* 457 */     String clsName = getServiceClassName(service);
/* 458 */     if (clsName == null) {
/* 459 */       throw new AxisFault(Messages.getMessage("noServiceClass"));
/*     */     }
/* 461 */     Class cls = getServiceClass(clsName, service, msgContext);
/* 462 */     JavaServiceDesc serviceDescription = (JavaServiceDesc)service.getServiceDescription();
/*     */ 
/* 465 */     if ((serviceDescription.getAllowedMethods() == null) && (service != null)) {
/* 466 */       String allowedMethods = getAllowedMethods(service);
/* 467 */       if ((allowedMethods != null) && (!"*".equals(allowedMethods))) {
/* 468 */         ArrayList methodList = new ArrayList();
/* 469 */         StringTokenizer tokenizer = new StringTokenizer(allowedMethods, " ,");
/* 470 */         while (tokenizer.hasMoreTokens()) {
/* 471 */           methodList.add(tokenizer.nextToken());
/*     */         }
/* 473 */         serviceDescription.setAllowedMethods(methodList);
/*     */       }
/*     */     }
/*     */ 
/* 477 */     serviceDescription.loadServiceDescByIntrospection(cls);
/*     */   }
/*     */ 
/*     */   class LockObject
/*     */     implements Serializable
/*     */   {
/* 146 */     private boolean completed = false;
/*     */ 
/*     */     LockObject() {  }
/*     */ 
/* 149 */     synchronized void waitUntilComplete() throws InterruptedException { while (!this.completed)
/* 150 */         wait();
/*     */     }
/*     */ 
/*     */     synchronized void complete()
/*     */     {
/* 155 */       this.completed = true;
/* 156 */       notifyAll();
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.providers.java.JavaProvider
 * JD-Core Version:    0.6.0
 */