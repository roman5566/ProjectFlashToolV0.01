/*     */ package org.apache.axis.server;
/*     */ 
/*     */ import java.util.Map;
/*     */ import org.apache.axis.AxisEngine;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.AxisProperties;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.EngineConfiguration;
/*     */ import org.apache.axis.EngineConfigurationFactory;
/*     */ import org.apache.axis.Handler;
/*     */ import org.apache.axis.Message;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.SimpleTargetedChain;
/*     */ import org.apache.axis.client.AxisClient;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.configuration.EngineConfigurationFactoryFinder;
/*     */ import org.apache.axis.description.OperationDesc;
/*     */ import org.apache.axis.message.SOAPEnvelope;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.apache.axis.utils.ClassUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class AxisServer extends AxisEngine
/*     */ {
/*  45 */   protected static Log log = LogFactory.getLog(AxisServer.class.getName());
/*     */ 
/*  47 */   private static Log tlog = LogFactory.getLog("org.apache.axis.TIME");
/*     */ 
/*  50 */   private static AxisServerFactory factory = null;
/*     */   private AxisEngine clientEngine;
/*  96 */   private boolean running = true;
/*     */ 
/*     */   public static AxisServer getServer(Map environment)
/*     */     throws AxisFault
/*     */   {
/*  54 */     if (factory == null) {
/*  55 */       String factoryClassName = AxisProperties.getProperty("axis.ServerFactory");
/*  56 */       if (factoryClassName != null) {
/*     */         try {
/*  58 */           Class factoryClass = ClassUtils.forName(factoryClassName);
/*  59 */           if (AxisServerFactory.class.isAssignableFrom(factoryClass))
/*  60 */             factory = (AxisServerFactory)factoryClass.newInstance();
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/*  64 */           log.error(Messages.getMessage("exception00"), e);
/*     */         }
/*     */       }
/*     */ 
/*  68 */       if (factory == null) {
/*  69 */         factory = new DefaultAxisServerFactory();
/*     */       }
/*     */     }
/*     */ 
/*  73 */     return factory.getServer(environment);
/*     */   }
/*     */ 
/*     */   public AxisServer()
/*     */   {
/*  83 */     this(EngineConfigurationFactoryFinder.newFactory().getServerEngineConfig());
/*     */   }
/*     */ 
/*     */   public AxisServer(EngineConfiguration config)
/*     */   {
/*  88 */     super(config);
/*     */ 
/*  90 */     setShouldSaveConfig(true);
/*     */   }
/*     */ 
/*     */   public boolean isRunning()
/*     */   {
/*  98 */     return this.running;
/*     */   }
/*     */ 
/*     */   public void start()
/*     */   {
/* 105 */     init();
/* 106 */     this.running = true;
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/* 113 */     this.running = false;
/*     */   }
/*     */ 
/*     */   public synchronized AxisEngine getClientEngine()
/*     */   {
/* 121 */     if (this.clientEngine == null) {
/* 122 */       this.clientEngine = new AxisClient();
/*     */     }
/* 124 */     return this.clientEngine;
/*     */   }
/*     */ 
/*     */   public void invoke(MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/* 132 */     long t0 = 0L; long t1 = 0L; long t2 = 0L; long t3 = 0L; long t4 = 0L; long t5 = 0L;
/* 133 */     if (tlog.isDebugEnabled()) {
/* 134 */       t0 = System.currentTimeMillis();
/*     */     }
/*     */ 
/* 137 */     if (log.isDebugEnabled()) {
/* 138 */       log.debug("Enter: AxisServer::invoke");
/*     */     }
/*     */ 
/* 141 */     if (!isRunning()) {
/* 142 */       throw new AxisFault("Server.disabled", Messages.getMessage("serverDisabled00"), null, null);
/*     */     }
/*     */ 
/* 147 */     String hName = null;
/* 148 */     Handler h = null;
/*     */ 
/* 151 */     MessageContext previousContext = getCurrentMessageContext();
/*     */     try
/*     */     {
/* 155 */       setCurrentMessageContext(msgContext);
/*     */ 
/* 157 */       hName = msgContext.getStrProp("engine.handler");
/* 158 */       if (hName != null) {
/* 159 */         if ((h = getHandler(hName)) == null) {
/* 160 */           ClassLoader cl = msgContext.getClassLoader();
/*     */           try {
/* 162 */             log.debug(Messages.getMessage("tryingLoad00", hName));
/* 163 */             Class cls = ClassUtils.forName(hName, true, cl);
/* 164 */             h = (Handler)cls.newInstance();
/*     */           }
/*     */           catch (Exception e) {
/* 167 */             h = null;
/*     */           }
/*     */         }
/* 170 */         if (tlog.isDebugEnabled()) {
/* 171 */           t1 = System.currentTimeMillis();
/*     */         }
/* 173 */         if (h != null)
/* 174 */           h.invoke(msgContext);
/*     */         else {
/* 176 */           throw new AxisFault("Server.error", Messages.getMessage("noHandler00", hName), null, null);
/*     */         }
/*     */ 
/* 179 */         if (tlog.isDebugEnabled()) {
/* 180 */           t2 = System.currentTimeMillis();
/* 181 */           tlog.debug("AxisServer.invoke " + hName + " invoke=" + (t2 - t1) + " pre=" + (t1 - t0));
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 202 */         if (log.isDebugEnabled()) {
/* 203 */           log.debug(Messages.getMessage("defaultLogic00"));
/*     */         }
/*     */ 
/* 225 */         hName = msgContext.getTransportName();
/* 226 */         SimpleTargetedChain transportChain = null;
/*     */ 
/* 228 */         if (log.isDebugEnabled()) {
/* 229 */           log.debug(Messages.getMessage("transport01", "AxisServer.invoke", hName));
/*     */         }
/* 231 */         if (tlog.isDebugEnabled()) {
/* 232 */           t1 = System.currentTimeMillis();
/*     */         }
/* 234 */         if ((hName != null) && ((h = getTransport(hName)) != null) && 
/* 235 */           ((h instanceof SimpleTargetedChain))) {
/* 236 */           transportChain = (SimpleTargetedChain)h;
/* 237 */           h = transportChain.getRequestHandler();
/* 238 */           if (h != null) {
/* 239 */             h.invoke(msgContext);
/*     */           }
/*     */         }
/*     */ 
/* 243 */         if (tlog.isDebugEnabled()) {
/* 244 */           t2 = System.currentTimeMillis();
/*     */         }
/*     */ 
/* 248 */         if ((h = getGlobalRequest()) != null) {
/* 249 */           h.invoke(msgContext);
/*     */         }
/*     */ 
/* 258 */         h = msgContext.getService();
/* 259 */         if (h == null)
/*     */         {
/* 265 */           Message rm = msgContext.getRequestMessage();
/* 266 */           rm.getSOAPEnvelope().getFirstBody();
/*     */ 
/* 268 */           h = msgContext.getService();
/* 269 */           if (h == null) {
/* 270 */             throw new AxisFault("Server.NoService", Messages.getMessage("noService05", "" + msgContext.getTargetService()), null, null);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 275 */         if (tlog.isDebugEnabled()) {
/* 276 */           t3 = System.currentTimeMillis();
/*     */         }
/*     */ 
/* 279 */         initSOAPConstants(msgContext);
/*     */         try {
/* 281 */           h.invoke(msgContext);
/*     */         } catch (AxisFault ae) {
/* 283 */           if ((h = getGlobalRequest()) != null) {
/* 284 */             h.onFault(msgContext);
/*     */           }
/* 286 */           throw ae;
/*     */         }
/*     */ 
/* 289 */         if (tlog.isDebugEnabled()) {
/* 290 */           t4 = System.currentTimeMillis();
/*     */         }
/*     */ 
/* 295 */         if ((h = getGlobalResponse()) != null) {
/* 296 */           h.invoke(msgContext);
/*     */         }
/*     */ 
/* 300 */         if (transportChain != null) {
/* 301 */           h = transportChain.getResponseHandler();
/* 302 */           if (h != null) {
/* 303 */             h.invoke(msgContext);
/*     */           }
/*     */         }
/* 306 */         if (tlog.isDebugEnabled()) {
/* 307 */           t5 = System.currentTimeMillis();
/* 308 */           tlog.debug("AxisServer.invoke2  preTr=" + (t1 - t0) + " tr=" + (t2 - t1) + " preInvoke=" + (t3 - t2) + " invoke=" + (t4 - t3) + " postInvoke=" + (t5 - t4) + " " + msgContext.getTargetService() + "." + (msgContext.getOperation() == null ? "" : msgContext.getOperation().getName()));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (AxisFault e)
/*     */     {
/* 321 */       throw e;
/*     */     }
/*     */     catch (Exception e) {
/* 324 */       throw AxisFault.makeFault(e);
/*     */     }
/*     */     finally
/*     */     {
/* 328 */       setCurrentMessageContext(previousContext);
/*     */     }
/*     */ 
/* 331 */     if (log.isDebugEnabled())
/* 332 */       log.debug("Exit: AxisServer::invoke");
/*     */   }
/*     */ 
/*     */   private void initSOAPConstants(MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/* 342 */     Message msg = msgContext.getRequestMessage();
/* 343 */     if (msg == null)
/* 344 */       return;
/* 345 */     SOAPEnvelope env = msg.getSOAPEnvelope();
/* 346 */     if (env == null)
/* 347 */       return;
/* 348 */     SOAPConstants constants = env.getSOAPConstants();
/* 349 */     if (constants == null) {
/* 350 */       return;
/*     */     }
/* 352 */     msgContext.setSOAPConstants(constants);
/*     */   }
/*     */ 
/*     */   public void generateWSDL(MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/* 359 */     if (log.isDebugEnabled()) {
/* 360 */       log.debug("Enter: AxisServer::generateWSDL");
/*     */     }
/*     */ 
/* 363 */     if (!isRunning()) {
/* 364 */       throw new AxisFault("Server.disabled", Messages.getMessage("serverDisabled00"), null, null);
/*     */     }
/*     */ 
/* 369 */     String hName = null;
/* 370 */     Handler h = null;
/*     */ 
/* 373 */     MessageContext previousContext = getCurrentMessageContext();
/*     */     try
/*     */     {
/* 377 */       setCurrentMessageContext(msgContext);
/*     */ 
/* 379 */       hName = msgContext.getStrProp("engine.handler");
/* 380 */       if (hName != null) {
/* 381 */         if ((h = getHandler(hName)) == null) {
/* 382 */           ClassLoader cl = msgContext.getClassLoader();
/*     */           try {
/* 384 */             log.debug(Messages.getMessage("tryingLoad00", hName));
/* 385 */             Class cls = ClassUtils.forName(hName, true, cl);
/* 386 */             h = (Handler)cls.newInstance();
/*     */           }
/*     */           catch (Exception e) {
/* 389 */             throw new AxisFault("Server.error", Messages.getMessage("noHandler00", hName), null, null);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 395 */         h.generateWSDL(msgContext);
/*     */       }
/*     */       else
/*     */       {
/* 413 */         log.debug(Messages.getMessage("defaultLogic00"));
/*     */ 
/* 434 */         hName = msgContext.getTransportName();
/* 435 */         SimpleTargetedChain transportChain = null;
/*     */ 
/* 437 */         if (log.isDebugEnabled()) {
/* 438 */           log.debug(Messages.getMessage("transport01", "AxisServer.generateWSDL", hName));
/*     */         }
/*     */ 
/* 441 */         if ((hName != null) && ((h = getTransport(hName)) != null) && 
/* 442 */           ((h instanceof SimpleTargetedChain))) {
/* 443 */           transportChain = (SimpleTargetedChain)h;
/* 444 */           h = transportChain.getRequestHandler();
/* 445 */           if (h != null) {
/* 446 */             h.generateWSDL(msgContext);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 453 */         if ((h = getGlobalRequest()) != null) {
/* 454 */           h.generateWSDL(msgContext);
/*     */         }
/*     */ 
/* 462 */         h = msgContext.getService();
/* 463 */         if (h == null)
/*     */         {
/* 469 */           Message rm = msgContext.getRequestMessage();
/* 470 */           if (rm != null) {
/* 471 */             rm.getSOAPEnvelope().getFirstBody();
/* 472 */             h = msgContext.getService();
/*     */           }
/* 474 */           if (h == null) {
/* 475 */             throw new AxisFault(Constants.QNAME_NO_SERVICE_FAULT_CODE, Messages.getMessage("noService05", "" + msgContext.getTargetService()), null, null);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 482 */         h.generateWSDL(msgContext);
/*     */ 
/* 486 */         if ((h = getGlobalResponse()) != null) {
/* 487 */           h.generateWSDL(msgContext);
/*     */         }
/*     */ 
/* 491 */         if (transportChain != null) {
/* 492 */           h = transportChain.getResponseHandler();
/* 493 */           if (h != null)
/* 494 */             h.generateWSDL(msgContext);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (AxisFault e) {
/* 499 */       throw e;
/*     */     }
/*     */     catch (Exception e) {
/* 502 */       throw AxisFault.makeFault(e);
/*     */     }
/*     */     finally {
/* 505 */       setCurrentMessageContext(previousContext);
/*     */     }
/*     */ 
/* 508 */     if (log.isDebugEnabled())
/* 509 */       log.debug("Exit: AxisServer::generateWSDL");
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.server.AxisServer
 * JD-Core Version:    0.6.0
 */