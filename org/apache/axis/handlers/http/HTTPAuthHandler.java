/*    */ package org.apache.axis.handlers.http;
/*    */ 
/*    */ import org.apache.axis.AxisFault;
/*    */ import org.apache.axis.MessageContext;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.encoding.Base64;
/*    */ import org.apache.axis.handlers.BasicHandler;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class HTTPAuthHandler extends BasicHandler
/*    */ {
/* 37 */   protected static Log log = LogFactory.getLog(HTTPAuthHandler.class.getName());
/*    */ 
/*    */   public void invoke(MessageContext msgContext)
/*    */     throws AxisFault
/*    */   {
/* 42 */     log.debug("Enter: HTTPAuthHandler::invoke");
/*    */ 
/* 46 */     String tmp = (String)msgContext.getProperty("Authorization");
/* 47 */     if (tmp != null) tmp = tmp.trim();
/* 48 */     if ((tmp != null) && (tmp.startsWith("Basic "))) {
/* 49 */       String user = null;
/*    */ 
/* 52 */       tmp = new String(Base64.decode(tmp.substring(6)));
/* 53 */       int i = tmp.indexOf(':');
/* 54 */       if (i == -1) user = tmp; else
/* 55 */         user = tmp.substring(0, i);
/* 56 */       msgContext.setUsername(user);
/* 57 */       log.debug(Messages.getMessage("httpUser00", user));
/* 58 */       if (i != -1) {
/* 59 */         String pwd = tmp.substring(i + 1);
/* 60 */         if ((pwd != null) && (pwd.equals(""))) pwd = null;
/* 61 */         if (pwd != null) {
/* 62 */           msgContext.setPassword(pwd);
/* 63 */           log.debug(Messages.getMessage("httpPassword00", pwd));
/*    */         }
/*    */       }
/*    */     }
/*    */ 
/* 68 */     log.debug("Exit: HTTPAuthHandler::invoke");
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.handlers.http.HTTPAuthHandler
 * JD-Core Version:    0.6.0
 */