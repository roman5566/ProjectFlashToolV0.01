/*     */ package org.apache.axis.handlers.soap;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ import java.util.WeakHashMap;
/*     */ import javax.xml.rpc.soap.SOAPFaultException;
/*     */ import org.apache.axis.AxisEngine;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Handler;
/*     */ import org.apache.axis.Message;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.SimpleTargetedChain;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.constants.Style;
/*     */ import org.apache.axis.constants.Use;
/*     */ import org.apache.axis.description.JavaServiceDesc;
/*     */ import org.apache.axis.description.ServiceDesc;
/*     */ import org.apache.axis.encoding.TypeMappingRegistry;
/*     */ import org.apache.axis.handlers.HandlerChainImpl;
/*     */ import org.apache.axis.handlers.HandlerInfoChainFactory;
/*     */ import org.apache.axis.message.SOAPEnvelope;
/*     */ import org.apache.axis.message.SOAPFault;
/*     */ import org.apache.axis.providers.BasicProvider;
/*     */ import org.apache.axis.session.Session;
/*     */ import org.apache.axis.utils.ClassUtils;
/*     */ import org.apache.axis.utils.LockableHashtable;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Document;
/*     */ 
/*     */ public class SOAPService extends SimpleTargetedChain
/*     */ {
/*  70 */   private static Log log = LogFactory.getLog(SOAPService.class.getName());
/*     */ 
/*  79 */   private Vector validTransports = null;
/*     */ 
/*  85 */   private boolean highFidelityRecording = true;
/*     */ 
/*  93 */   private int sendType = 1;
/*     */ 
/*  99 */   private ServiceDesc serviceDescription = new JavaServiceDesc();
/*     */   private AxisEngine engine;
/* 106 */   public Map serviceObjects = new HashMap();
/* 107 */   public int nextObjectID = 1;
/*     */ 
/* 112 */   private static Hashtable sessions = new Hashtable();
/*     */ 
/* 114 */   private boolean isRunning = true;
/*     */ 
/* 144 */   ArrayList actors = new ArrayList();
/*     */ 
/*     */   public void addSession(Session session)
/*     */   {
/* 120 */     WeakHashMap map = (WeakHashMap)sessions.get(getName());
/* 121 */     if (map == null) {
/* 122 */       map = new WeakHashMap();
/* 123 */       sessions.put(getName(), map);
/*     */     }
/* 125 */     if (!map.containsKey(session)) map.put(session, null);
/*     */   }
/*     */ 
/*     */   public void clearSessions()
/*     */   {
/* 132 */     WeakHashMap map = (WeakHashMap)sessions.get(getName());
/* 133 */     if (map == null) return;
/* 134 */     Iterator iter = map.keySet().iterator();
/* 135 */     while (iter.hasNext()) {
/* 136 */       Session session = (Session)iter.next();
/* 137 */       session.remove(getName());
/*     */     }
/*     */   }
/*     */ 
/*     */   public ArrayList getServiceActors()
/*     */   {
/* 151 */     return this.actors;
/*     */   }
/*     */ 
/*     */   public ArrayList getActors()
/*     */   {
/* 161 */     ArrayList acts = (ArrayList)this.actors.clone();
/*     */ 
/* 163 */     if (this.engine != null) {
/* 164 */       acts.addAll(this.engine.getActorURIs());
/*     */     }
/*     */ 
/* 167 */     return acts;
/*     */   }
/*     */ 
/*     */   public List getRoles() {
/* 171 */     return getActors();
/*     */   }
/*     */ 
/*     */   public void setRoles(List roles)
/*     */   {
/* 180 */     this.actors = new ArrayList(roles);
/*     */   }
/*     */ 
/*     */   public SOAPService()
/*     */   {
/* 187 */     setOptionsLockable(true);
/* 188 */     initHashtable();
/*     */ 
/* 191 */     this.actors.add("");
/*     */   }
/*     */ 
/*     */   public SOAPService(Handler reqHandler, Handler pivHandler, Handler respHandler)
/*     */   {
/* 200 */     this();
/* 201 */     init(reqHandler, new MustUnderstandChecker(this), pivHandler, null, respHandler);
/*     */   }
/*     */ 
/*     */   public TypeMappingRegistry getTypeMappingRegistry()
/*     */   {
/* 206 */     return this.serviceDescription.getTypeMappingRegistry();
/*     */   }
/*     */ 
/*     */   public SOAPService(Handler serviceHandler)
/*     */   {
/* 214 */     this();
/* 215 */     init(null, new MustUnderstandChecker(this), serviceHandler, null, null);
/*     */   }
/*     */ 
/*     */   public void setEngine(AxisEngine engine)
/*     */   {
/* 223 */     if (engine == null) {
/* 224 */       throw new IllegalArgumentException(Messages.getMessage("nullEngine"));
/*     */     }
/*     */ 
/* 227 */     this.engine = engine;
/* 228 */     ((LockableHashtable)this.options).setParent(engine.getOptions());
/* 229 */     TypeMappingRegistry tmr = engine.getTypeMappingRegistry();
/* 230 */     getTypeMappingRegistry().delegate(tmr);
/*     */   }
/*     */ 
/*     */   public AxisEngine getEngine() {
/* 234 */     return this.engine;
/*     */   }
/*     */ 
/*     */   public boolean availableFromTransport(String transportName)
/*     */   {
/* 239 */     if (this.validTransports != null) {
/* 240 */       for (int i = 0; i < this.validTransports.size(); i++) {
/* 241 */         if (this.validTransports.elementAt(i).equals(transportName))
/* 242 */           return true;
/*     */       }
/* 244 */       return false;
/*     */     }
/*     */ 
/* 247 */     return true;
/*     */   }
/*     */ 
/*     */   public Style getStyle() {
/* 251 */     return this.serviceDescription.getStyle();
/*     */   }
/*     */ 
/*     */   public void setStyle(Style style) {
/* 255 */     this.serviceDescription.setStyle(style);
/*     */   }
/*     */ 
/*     */   public Use getUse() {
/* 259 */     return this.serviceDescription.getUse();
/*     */   }
/*     */ 
/*     */   public void setUse(Use style) {
/* 263 */     this.serviceDescription.setUse(style);
/*     */   }
/*     */ 
/*     */   public ServiceDesc getServiceDescription() {
/* 267 */     return this.serviceDescription;
/*     */   }
/*     */ 
/*     */   public synchronized ServiceDesc getInitializedServiceDesc(MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/* 278 */     if (!this.serviceDescription.isInitialized())
/*     */     {
/* 285 */       if ((this.pivotHandler instanceof BasicProvider)) {
/* 286 */         ((BasicProvider)this.pivotHandler).initServiceDesc(this, msgContext);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 291 */     return this.serviceDescription;
/*     */   }
/*     */ 
/*     */   public void setServiceDescription(ServiceDesc serviceDescription) {
/* 295 */     if (serviceDescription == null)
/*     */     {
/* 297 */       return;
/*     */     }
/* 299 */     this.serviceDescription = serviceDescription;
/*     */   }
/*     */ 
/*     */   public void setPropertyParent(Hashtable parent)
/*     */   {
/* 305 */     if (this.options == null) {
/* 306 */       this.options = new LockableHashtable();
/*     */     }
/* 308 */     ((LockableHashtable)this.options).setParent(parent);
/*     */   }
/*     */ 
/*     */   public void generateWSDL(MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/* 318 */     if ((this.serviceDescription == null) || (this.serviceDescription.getWSDLFile() == null))
/*     */     {
/* 320 */       super.generateWSDL(msgContext);
/* 321 */       return;
/*     */     }
/* 323 */     InputStream instream = null;
/*     */     try
/*     */     {
/* 327 */       String filename = this.serviceDescription.getWSDLFile();
/* 328 */       File file = new File(filename);
/* 329 */       if (file.exists())
/*     */       {
/* 331 */         instream = new FileInputStream(filename);
/* 332 */       } else if (msgContext.getStrProp("home.dir") != null) {
/* 333 */         String path = msgContext.getStrProp("home.dir") + '/' + filename;
/* 334 */         file = new File(path);
/* 335 */         if (file.exists())
/*     */         {
/* 337 */           instream = new FileInputStream(path);
/*     */         }
/*     */       }
/*     */ 
/* 341 */       if (instream == null)
/*     */       {
/* 343 */         instream = ClassUtils.getResourceAsStream(getClass(), filename);
/* 344 */         if (instream == null) {
/* 345 */           String errorText = Messages.getMessage("wsdlFileMissing", filename);
/* 346 */           throw new AxisFault(errorText);
/*     */         }
/*     */       }
/* 349 */       Document doc = XMLUtils.newDocument(instream);
/* 350 */       msgContext.setProperty("WSDL", doc);
/*     */     } catch (Exception e) {
/* 352 */       throw AxisFault.makeFault(e);
/*     */     } finally {
/* 354 */       if (instream != null)
/*     */         try {
/* 356 */           instream.close();
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void start()
/*     */   {
/* 374 */     this.isRunning = true;
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/* 381 */     this.isRunning = false;
/*     */   }
/*     */ 
/*     */   public boolean isRunning()
/*     */   {
/* 389 */     return this.isRunning;
/*     */   }
/*     */ 
/*     */   public void enableTransport(String transportName)
/*     */   {
/* 397 */     if (log.isDebugEnabled()) {
/* 398 */       log.debug(Messages.getMessage("enableTransport00", "" + this, transportName));
/*     */     }
/*     */ 
/* 402 */     if (this.validTransports == null)
/* 403 */       this.validTransports = new Vector();
/* 404 */     this.validTransports.addElement(transportName);
/*     */   }
/*     */ 
/*     */   public void disableTransport(String transportName)
/*     */   {
/* 412 */     if (this.validTransports != null)
/* 413 */       this.validTransports.removeElement(transportName);
/*     */   }
/*     */ 
/*     */   public boolean needsHighFidelityRecording()
/*     */   {
/* 418 */     return this.highFidelityRecording;
/*     */   }
/*     */ 
/*     */   public void setHighFidelityRecording(boolean highFidelityRecording) {
/* 422 */     this.highFidelityRecording = highFidelityRecording;
/*     */   }
/*     */ 
/*     */   public int getSendType()
/*     */   {
/* 427 */     return this.sendType;
/*     */   }
/*     */ 
/*     */   public void setSendType(int sendType) {
/* 431 */     this.sendType = sendType;
/*     */   }
/*     */ 
/*     */   public void invoke(MessageContext msgContext) throws AxisFault {
/* 435 */     HandlerInfoChainFactory handlerFactory = (HandlerInfoChainFactory)getOption("handlerInfoChain");
/* 436 */     HandlerChainImpl handlerImpl = null;
/* 437 */     if (handlerFactory != null) handlerImpl = (HandlerChainImpl)handlerFactory.createHandlerChain();
/* 438 */     boolean result = true;
/*     */     try
/*     */     {
/* 441 */       if (handlerImpl != null)
/*     */         try {
/* 443 */           result = handlerImpl.handleRequest(msgContext);
/*     */         }
/*     */         catch (SOAPFaultException e) {
/* 446 */           msgContext.setPastPivot(true);
/* 447 */           handlerImpl.handleFault(msgContext);
/*     */ 
/* 483 */           if (handlerImpl != null)
/* 484 */             handlerImpl.destroy(); return;
/*     */         }
/* 452 */       if (result)
/*     */         try {
/* 454 */           super.invoke(msgContext);
/*     */         } catch (AxisFault e) {
/* 456 */           msgContext.setPastPivot(true);
/* 457 */           if (handlerImpl != null) {
/* 458 */             handlerImpl.handleFault(msgContext);
/*     */           }
/* 460 */           throw e;
/*     */         }
/*     */       else {
/* 463 */         msgContext.setPastPivot(true);
/*     */       }
/*     */ 
/* 466 */       if (handlerImpl != null)
/* 467 */         handlerImpl.handleResponse(msgContext);
/*     */     }
/*     */     catch (SOAPFaultException e) {
/* 470 */       msgContext.setPastPivot(true);
/* 471 */       throw AxisFault.makeFault(e);
/*     */     }
/*     */     catch (RuntimeException e) {
/* 474 */       SOAPFault fault = new SOAPFault(new AxisFault("Server", "Server Error", null, null));
/* 475 */       SOAPEnvelope env = new SOAPEnvelope();
/* 476 */       env.addBodyElement(fault);
/* 477 */       Message message = new Message(env);
/* 478 */       message.setMessageType("response");
/* 479 */       msgContext.setResponseMessage(message);
/* 480 */       throw AxisFault.makeFault(e);
/*     */     }
/*     */     finally {
/* 483 */       if (handlerImpl != null)
/* 484 */         handlerImpl.destroy();
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.handlers.soap.SOAPService
 * JD-Core Version:    0.6.0
 */