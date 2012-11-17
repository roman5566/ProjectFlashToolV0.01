/*     */ package org.apache.axis.client;
/*     */ 
/*     */ import java.util.List;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.rpc.handler.HandlerChain;
/*     */ import javax.xml.rpc.handler.HandlerRegistry;
/*     */ import org.apache.axis.AxisEngine;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.EngineConfiguration;
/*     */ import org.apache.axis.EngineConfigurationFactory;
/*     */ import org.apache.axis.Handler;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.configuration.EngineConfigurationFactoryFinder;
/*     */ import org.apache.axis.handlers.HandlerInfoChainFactory;
/*     */ import org.apache.axis.handlers.soap.MustUnderstandChecker;
/*     */ import org.apache.axis.handlers.soap.SOAPService;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class AxisClient extends AxisEngine
/*     */ {
/*  46 */   protected static Log log = LogFactory.getLog(AxisClient.class.getName());
/*     */ 
/*  49 */   MustUnderstandChecker checker = new MustUnderstandChecker(null);
/*     */ 
/*     */   public AxisClient(EngineConfiguration config) {
/*  52 */     super(config);
/*     */   }
/*     */ 
/*     */   public AxisClient() {
/*  56 */     this(EngineConfigurationFactoryFinder.newFactory().getClientEngineConfig());
/*     */   }
/*     */ 
/*     */   public AxisEngine getClientEngine()
/*     */   {
/*  64 */     return this;
/*     */   }
/*     */ 
/*     */   public void invoke(MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/*  76 */     if (log.isDebugEnabled()) {
/*  77 */       log.debug("Enter: AxisClient::invoke");
/*     */     }
/*  79 */     String hName = null;
/*  80 */     Handler h = null;
/*  81 */     HandlerChain handlerImpl = null;
/*     */ 
/*  84 */     MessageContext previousContext = getCurrentMessageContext();
/*     */     try
/*     */     {
/*  87 */       setCurrentMessageContext(msgContext);
/*  88 */       hName = msgContext.getStrProp("engine.handler");
/*  89 */       if (log.isDebugEnabled()) {
/*  90 */         log.debug("EngineHandler: " + hName);
/*     */       }
/*  92 */       if (hName != null) {
/*  93 */         h = getHandler(hName);
/*  94 */         if (h != null)
/*  95 */           h.invoke(msgContext);
/*     */         else {
/*  97 */           throw new AxisFault("Client.error", Messages.getMessage("noHandler00", hName), null, null);
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 112 */         SOAPService service = null;
/* 113 */         msgContext.setPastPivot(false);
/*     */ 
/* 117 */         service = msgContext.getService();
/* 118 */         if (service != null) {
/* 119 */           h = service.getRequestHandler();
/* 120 */           if (h != null) {
/* 121 */             h.invoke(msgContext);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 126 */         if ((h = getGlobalRequest()) != null) {
/* 127 */           h.invoke(msgContext);
/*     */         }
/*     */ 
/* 140 */         handlerImpl = getJAXRPChandlerChain(msgContext);
/* 141 */         if (handlerImpl != null) {
/*     */           try {
/* 143 */             if (!handlerImpl.handleRequest(msgContext))
/* 144 */               msgContext.setPastPivot(true);
/*     */           }
/*     */           catch (RuntimeException re) {
/* 147 */             handlerImpl.destroy();
/* 148 */             throw re;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 161 */         if (!msgContext.getPastPivot()) {
/* 162 */           hName = msgContext.getTransportName();
/* 163 */           if ((hName != null) && ((h = getTransport(hName)) != null))
/*     */             try {
/* 165 */               h.invoke(msgContext);
/*     */             } catch (AxisFault e) {
/* 167 */               throw e;
/*     */             }
/*     */           else {
/* 170 */             throw new AxisFault(Messages.getMessage("noTransport00", hName));
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 175 */         msgContext.setPastPivot(true);
/* 176 */         if (!msgContext.isPropertyTrue("axis.one.way")) {
/* 177 */           if ((handlerImpl != null) && (!msgContext.isPropertyTrue("axis.one.way"))) {
/*     */             try
/*     */             {
/* 180 */               handlerImpl.handleResponse(msgContext);
/*     */             } catch (RuntimeException ex) {
/* 182 */               handlerImpl.destroy();
/* 183 */               throw ex;
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 189 */           if ((h = getGlobalResponse()) != null) {
/* 190 */             h.invoke(msgContext);
/*     */           }
/*     */ 
/* 195 */           if (service != null) {
/* 196 */             h = service.getResponseHandler();
/* 197 */             if (h != null) {
/* 198 */               h.invoke(msgContext);
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 204 */           if (msgContext.isPropertyTrue("call.CheckMustUnderstand", true))
/*     */           {
/* 206 */             this.checker.invoke(msgContext);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 212 */       if ((e instanceof AxisFault)) {
/* 213 */         throw ((AxisFault)e);
/*     */       }
/* 215 */       log.debug(Messages.getMessage("exception00"), e);
/* 216 */       throw AxisFault.makeFault(e);
/*     */     }
/*     */     finally {
/* 219 */       if (handlerImpl != null) {
/* 220 */         handlerImpl.destroy();
/*     */       }
/*     */ 
/* 223 */       setCurrentMessageContext(previousContext);
/*     */     }
/* 225 */     if (log.isDebugEnabled())
/* 226 */       log.debug("Exit: AxisClient::invoke");
/*     */   }
/*     */ 
/*     */   protected HandlerChain getJAXRPChandlerChain(MessageContext context)
/*     */   {
/* 235 */     List chain = null;
/* 236 */     HandlerInfoChainFactory hiChainFactory = null;
/* 237 */     boolean clientSpecified = false;
/*     */ 
/* 239 */     Service service = (Service)context.getProperty("wsdl.service");
/* 240 */     if (service == null) {
/* 241 */       return null;
/*     */     }
/*     */ 
/* 244 */     QName portName = (QName)context.getProperty("wsdl.portName");
/* 245 */     if (portName == null) {
/* 246 */       return null;
/*     */     }
/*     */ 
/* 250 */     HandlerRegistry registry = service.getHandlerRegistry();
/* 251 */     if (registry != null) {
/* 252 */       chain = registry.getHandlerChain(portName);
/* 253 */       if ((chain != null) && (!chain.isEmpty())) {
/* 254 */         hiChainFactory = new HandlerInfoChainFactory(chain);
/* 255 */         clientSpecified = true;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 260 */     if (!clientSpecified) {
/* 261 */       SOAPService soapService = context.getService();
/* 262 */       if (soapService != null)
/*     */       {
/* 265 */         hiChainFactory = (HandlerInfoChainFactory)soapService.getOption("handlerInfoChain");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 270 */     if (hiChainFactory == null) {
/* 271 */       return null;
/*     */     }
/*     */ 
/* 274 */     return hiChainFactory.createHandlerChain();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.client.AxisClient
 * JD-Core Version:    0.6.0
 */