/*    */ package org.apache.axis.transport.http;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import javax.servlet.http.HttpSession;
/*    */ import javax.servlet.http.HttpSessionEvent;
/*    */ import javax.servlet.http.HttpSessionListener;
/*    */ import javax.xml.rpc.server.ServiceLifecycle;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class AxisHTTPSessionListener
/*    */   implements HttpSessionListener
/*    */ {
/* 33 */   protected static Log log = LogFactory.getLog(AxisHTTPSessionListener.class.getName());
/*    */ 
/*    */   static void destroySession(HttpSession session)
/*    */   {
/* 43 */     if (session.getAttribute("axis.isAxisSession") == null) {
/* 44 */       return;
/*    */     }
/* 46 */     if (log.isDebugEnabled()) {
/* 47 */       log.debug("Got destroySession event : " + session);
/*    */     }
/*    */ 
/* 50 */     Enumeration e = session.getAttributeNames();
/* 51 */     while (e.hasMoreElements()) {
/* 52 */       Object next = e.nextElement();
/* 53 */       if ((next instanceof ServiceLifecycle))
/* 54 */         ((ServiceLifecycle)next).destroy();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void sessionCreated(HttpSessionEvent event)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void sessionDestroyed(HttpSessionEvent event)
/*    */   {
/* 71 */     HttpSession session = event.getSession();
/* 72 */     destroySession(session);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.http.AxisHTTPSessionListener
 * JD-Core Version:    0.6.0
 */