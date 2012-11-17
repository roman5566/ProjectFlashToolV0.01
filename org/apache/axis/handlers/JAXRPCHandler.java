/*    */ package org.apache.axis.handlers;
/*    */ 
/*    */ import java.util.Map;
/*    */ import org.apache.axis.AxisFault;
/*    */ import org.apache.axis.MessageContext;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class JAXRPCHandler extends BasicHandler
/*    */ {
/* 31 */   protected static Log log = LogFactory.getLog(JAXRPCHandler.class.getName());
/*    */ 
/* 34 */   protected HandlerChainImpl impl = new HandlerChainImpl();
/*    */ 
/*    */   public void init() {
/* 37 */     super.init();
/* 38 */     String className = (String)getOption("className");
/* 39 */     if (className != null)
/* 40 */       addNewHandler(className, getOptions());
/*    */   }
/*    */ 
/*    */   public void addNewHandler(String className, Map options)
/*    */   {
/* 45 */     this.impl.addNewHandler(className, options);
/*    */   }
/*    */ 
/*    */   public void invoke(MessageContext msgContext) throws AxisFault {
/* 49 */     log.debug("Enter: JAXRPCHandler::enter invoke");
/* 50 */     if (!msgContext.getPastPivot())
/* 51 */       this.impl.handleRequest(msgContext);
/*    */     else {
/* 53 */       this.impl.handleResponse(msgContext);
/*    */     }
/* 55 */     log.debug("Enter: JAXRPCHandler::exit invoke");
/*    */   }
/*    */ 
/*    */   public void onFault(MessageContext msgContext) {
/* 59 */     this.impl.handleFault(msgContext);
/*    */   }
/*    */ 
/*    */   public void cleanup() {
/* 63 */     this.impl.destroy();
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.handlers.JAXRPCHandler
 * JD-Core Version:    0.6.0
 */