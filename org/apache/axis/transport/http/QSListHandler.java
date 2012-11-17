/*    */ package org.apache.axis.transport.http;
/*    */ 
/*    */ import java.io.PrintWriter;
/*    */ import javax.servlet.http.HttpServletResponse;
/*    */ import org.apache.axis.AxisFault;
/*    */ import org.apache.axis.MessageContext;
/*    */ import org.apache.axis.server.AxisServer;
/*    */ import org.apache.axis.utils.Admin;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.axis.utils.XMLUtils;
/*    */ import org.w3c.dom.Document;
/*    */ 
/*    */ public class QSListHandler extends AbstractQueryStringHandler
/*    */ {
/*    */   public void invoke(MessageContext msgContext)
/*    */     throws AxisFault
/*    */   {
/* 56 */     boolean enableList = ((Boolean)msgContext.getProperty("transport.http.plugin.enableList")).booleanValue();
/*    */ 
/* 58 */     AxisServer engine = (AxisServer)msgContext.getProperty("transport.http.plugin.engine");
/*    */ 
/* 60 */     PrintWriter writer = (PrintWriter)msgContext.getProperty("transport.http.plugin.writer");
/*    */ 
/* 62 */     HttpServletResponse response = (HttpServletResponse)msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLETRESPONSE);
/*    */ 
/* 65 */     if (enableList) {
/* 66 */       Document doc = Admin.listConfig(engine);
/*    */ 
/* 68 */       if (doc != null) {
/* 69 */         response.setContentType("text/xml");
/* 70 */         XMLUtils.DocumentToWriter(doc, writer);
/*    */       }
/*    */       else
/*    */       {
/* 76 */         response.setStatus(404);
/* 77 */         response.setContentType("text/html");
/*    */ 
/* 79 */         writer.println("<h2>" + Messages.getMessage("error00") + "</h2>");
/*    */ 
/* 81 */         writer.println("<p>" + Messages.getMessage("noDeploy00") + "</p>");
/*    */       }
/*    */ 
/*    */     }
/*    */     else
/*    */     {
/* 90 */       response.setStatus(403);
/* 91 */       response.setContentType("text/html");
/*    */ 
/* 93 */       writer.println("<h2>" + Messages.getMessage("error00") + "</h2>");
/*    */ 
/* 95 */       writer.println("<p><i>?list</i> " + Messages.getMessage("disabled00") + "</p>");
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.http.QSListHandler
 * JD-Core Version:    0.6.0
 */