/*    */ package org.apache.axis.handlers.http;
/*    */ 
/*    */ import org.apache.axis.AxisFault;
/*    */ import org.apache.axis.MessageContext;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.handlers.BasicHandler;
/*    */ import org.apache.axis.transport.http.HTTPConstants;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class URLMapper extends BasicHandler
/*    */ {
/* 34 */   protected static Log log = LogFactory.getLog(URLMapper.class.getName());
/*    */ 
/*    */   public void invoke(MessageContext msgContext)
/*    */     throws AxisFault
/*    */   {
/* 39 */     log.debug("Enter: URLMapper::invoke");
/*    */ 
/* 43 */     if (msgContext.getService() == null)
/*    */     {
/* 45 */       String path = (String)msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLETPATHINFO);
/* 46 */       if ((path != null) && (path.length() >= 1)) {
/* 47 */         if (path.startsWith("/")) {
/* 48 */           path = path.substring(1);
/*    */         }
/* 50 */         msgContext.setTargetService(path);
/*    */       }
/*    */     }
/*    */ 
/* 54 */     log.debug("Exit: URLMapper::invoke");
/*    */   }
/*    */ 
/*    */   public void generateWSDL(MessageContext msgContext) throws AxisFault {
/* 58 */     invoke(msgContext);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.handlers.http.URLMapper
 * JD-Core Version:    0.6.0
 */