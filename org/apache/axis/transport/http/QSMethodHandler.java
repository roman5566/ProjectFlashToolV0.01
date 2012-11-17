/*     */ package org.apache.axis.transport.http;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Enumeration;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Message;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.server.AxisServer;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class QSMethodHandler extends AbstractQueryStringHandler
/*     */ {
/*     */   public void invoke(MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/*  56 */     configureFromContext(msgContext);
/*  57 */     AxisServer engine = (AxisServer)msgContext.getProperty("transport.http.plugin.engine");
/*     */ 
/*  59 */     PrintWriter writer = (PrintWriter)msgContext.getProperty("transport.http.plugin.writer");
/*     */ 
/*  61 */     HttpServletRequest request = (HttpServletRequest)msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
/*     */ 
/*  63 */     HttpServletResponse response = (HttpServletResponse)msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLETRESPONSE);
/*     */ 
/*  67 */     String method = null;
/*  68 */     String args = "";
/*  69 */     Enumeration e = request.getParameterNames();
/*     */ 
/*  71 */     while (e.hasMoreElements()) {
/*  72 */       String param = (String)e.nextElement();
/*  73 */       if (param.equalsIgnoreCase("method")) {
/*  74 */         method = request.getParameter(param);
/*     */       }
/*     */       else
/*     */       {
/*  78 */         args = args + "<" + param + ">" + request.getParameter(param) + "</" + param + ">";
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  83 */     if (method == null) {
/*  84 */       response.setContentType("text/html");
/*  85 */       response.setStatus(400);
/*     */ 
/*  87 */       writer.println("<h2>" + Messages.getMessage("error00") + ":  " + Messages.getMessage("invokeGet00") + "</h2>");
/*     */ 
/*  89 */       writer.println("<p>" + Messages.getMessage("noMethod01") + "</p>");
/*     */     }
/*     */     else
/*     */     {
/*  94 */       invokeEndpointFromGet(msgContext, response, writer, method, args);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void invokeEndpointFromGet(MessageContext msgContext, HttpServletResponse response, PrintWriter writer, String method, String args)
/*     */     throws AxisFault
/*     */   {
/* 114 */     String body = "<" + method + ">" + args + "</" + method + ">";
/* 115 */     String msgtxt = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Body>" + body + "</SOAP-ENV:Body>" + "</SOAP-ENV:Envelope>";
/*     */ 
/* 119 */     ByteArrayInputStream istream = new ByteArrayInputStream(msgtxt.getBytes());
/*     */ 
/* 121 */     Message responseMsg = null;
/*     */     try
/*     */     {
/* 124 */       AxisServer engine = (AxisServer)msgContext.getProperty("transport.http.plugin.engine");
/*     */ 
/* 126 */       Message msg = new Message(istream, false);
/*     */ 
/* 128 */       msgContext.setRequestMessage(msg);
/* 129 */       engine.invoke(msgContext);
/*     */ 
/* 131 */       responseMsg = msgContext.getResponseMessage();
/*     */ 
/* 135 */       response.setHeader("Cache-Control", "no-cache");
/* 136 */       response.setHeader("Pragma", "no-cache");
/*     */ 
/* 138 */       if (responseMsg == null)
/*     */       {
/* 141 */         throw new Exception(Messages.getMessage("noResponse01"));
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (AxisFault fault)
/*     */     {
/* 147 */       processAxisFault(fault);
/*     */ 
/* 149 */       configureResponseFromAxisFault(response, fault);
/*     */ 
/* 151 */       if (responseMsg == null) {
/* 152 */         responseMsg = new Message(fault);
/* 153 */         responseMsg.setMessageContext(msgContext);
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 158 */       response.setStatus(500);
/* 159 */       responseMsg = convertExceptionToAxisFault(e, responseMsg);
/*     */     }
/*     */ 
/* 165 */     response.setContentType("text/xml");
/*     */ 
/* 167 */     writer.println(responseMsg.getSOAPPartAsString());
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.http.QSMethodHandler
 * JD-Core Version:    0.6.0
 */