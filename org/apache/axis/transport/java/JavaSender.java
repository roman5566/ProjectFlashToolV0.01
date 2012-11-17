/*    */ package org.apache.axis.transport.java;
/*    */ 
/*    */ import org.apache.axis.AxisFault;
/*    */ import org.apache.axis.MessageContext;
/*    */ import org.apache.axis.client.Call;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.constants.Scope;
/*    */ import org.apache.axis.description.OperationDesc;
/*    */ import org.apache.axis.handlers.BasicHandler;
/*    */ import org.apache.axis.handlers.soap.SOAPService;
/*    */ import org.apache.axis.providers.java.MsgProvider;
/*    */ import org.apache.axis.providers.java.RPCProvider;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class JavaSender extends BasicHandler
/*    */ {
/* 32 */   protected static Log log = LogFactory.getLog(JavaSender.class.getName());
/*    */ 
/*    */   public void invoke(MessageContext msgContext) throws AxisFault
/*    */   {
/* 36 */     if (log.isDebugEnabled()) {
/* 37 */       log.debug("Enter: JavaSender::invoke");
/*    */     }
/*    */ 
/* 40 */     SOAPService service = null;
/* 41 */     SOAPService saveService = msgContext.getService();
/* 42 */     OperationDesc saveOp = msgContext.getOperation();
/*    */ 
/* 44 */     Call call = (Call)msgContext.getProperty("call_object");
/* 45 */     String url = call.getTargetEndpointAddress();
/* 46 */     String cls = url.substring(5);
/*    */ 
/* 48 */     msgContext.setService(null);
/* 49 */     msgContext.setOperation(null);
/*    */ 
/* 51 */     if (msgContext.getProperty("isMsg") == null)
/* 52 */       service = new SOAPService(new RPCProvider());
/*    */     else {
/* 54 */       service = new SOAPService(new MsgProvider());
/*    */     }
/* 56 */     if (cls.startsWith("//")) cls = cls.substring(2);
/* 57 */     service.setOption("className", cls);
/* 58 */     service.setEngine(msgContext.getAxisEngine());
/*    */ 
/* 60 */     service.setOption("allowedMethods", "*");
/* 61 */     service.setOption("scope", Scope.DEFAULT.getName());
/* 62 */     service.getInitializedServiceDesc(msgContext);
/* 63 */     service.init();
/*    */ 
/* 65 */     msgContext.setService(service);
/*    */ 
/* 67 */     service.invoke(msgContext);
/*    */ 
/* 69 */     msgContext.setService(saveService);
/* 70 */     msgContext.setOperation(saveOp);
/*    */ 
/* 72 */     if (log.isDebugEnabled())
/* 73 */       log.debug("Exit: JavaSender::invoke");
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.java.JavaSender
 * JD-Core Version:    0.6.0
 */