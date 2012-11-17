/*     */ package org.apache.axis;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.handlers.BasicHandler;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class FaultableHandler extends BasicHandler
/*     */ {
/*  41 */   protected static Log log = LogFactory.getLog(FaultableHandler.class.getName());
/*     */ 
/*  51 */   protected static Log entLog = LogFactory.getLog("org.apache.axis.enterprise");
/*     */   protected Handler workHandler;
/*     */ 
/*     */   public FaultableHandler(Handler workHandler)
/*     */   {
/*  67 */     this.workHandler = workHandler;
/*     */   }
/*     */ 
/*     */   public void init() {
/*  71 */     this.workHandler.init();
/*     */   }
/*     */ 
/*     */   public void cleanup() {
/*  75 */     this.workHandler.cleanup();
/*     */   }
/*     */ 
/*     */   public void invoke(MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/*  88 */     log.debug("Enter: FaultableHandler::invoke");
/*     */     try {
/*  90 */       this.workHandler.invoke(msgContext);
/*     */     }
/*     */     catch (Exception e) {
/*  93 */       entLog.info(Messages.getMessage("toAxisFault00"), e);
/*  94 */       AxisFault fault = AxisFault.makeFault(e);
/*     */ 
/* 108 */       Handler faultHandler = null;
/*     */ 
/* 110 */       Hashtable options = getOptions();
/* 111 */       if (options != null) {
/* 112 */         Enumeration enumeration = options.keys();
/* 113 */         while (enumeration.hasMoreElements()) {
/* 114 */           String s = (String)enumeration.nextElement();
/* 115 */           if (s.equals("fault-" + fault.getFaultCode().getLocalPart())) {
/* 116 */             faultHandler = (Handler)options.get(s);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 121 */       if (faultHandler != null)
/*     */       {
/* 128 */         faultHandler.invoke(msgContext);
/*     */       }
/* 130 */       else throw fault;
/*     */     }
/*     */ 
/* 133 */     log.debug("Exit: FaultableHandler::invoke");
/*     */   }
/*     */ 
/*     */   public void onFault(MessageContext msgContext)
/*     */   {
/* 142 */     log.debug("Enter: FaultableHandler::onFault");
/* 143 */     this.workHandler.onFault(msgContext);
/* 144 */     log.debug("Exit: FaultableHandler::onFault");
/*     */   }
/*     */ 
/*     */   public boolean canHandleBlock(QName qname) {
/* 148 */     return this.workHandler.canHandleBlock(qname);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.FaultableHandler
 * JD-Core Version:    0.6.0
 */