/*     */ package org.apache.axis.transport.http;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.Message;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Element;
/*     */ 
/*     */ public abstract class AbstractQueryStringHandler
/*     */   implements QSHandler
/*     */ {
/*     */   private boolean development;
/*     */   protected Log exceptionLog;
/*     */   protected Log log;
/*     */ 
/*     */   protected boolean isDevelopment()
/*     */   {
/*  50 */     return this.development;
/*     */   }
/*     */ 
/*     */   protected void configureFromContext(MessageContext msgContext)
/*     */   {
/*  59 */     this.development = ((Boolean)msgContext.getProperty("transport.http.plugin.isDevelopment")).booleanValue();
/*     */ 
/*  61 */     this.exceptionLog = ((Log)msgContext.getProperty("transport.http.plugin.exceptionLog"));
/*     */ 
/*  63 */     this.log = ((Log)msgContext.getProperty("transport.http.plugin.log"));
/*     */   }
/*     */ 
/*     */   protected void processAxisFault(AxisFault fault)
/*     */   {
/*  76 */     Element runtimeException = fault.lookupFaultDetail(Constants.QNAME_FAULTDETAIL_RUNTIMEEXCEPTION);
/*     */ 
/*  79 */     if (runtimeException != null) {
/*  80 */       this.exceptionLog.info(Messages.getMessage("axisFault00"), fault);
/*     */ 
/*  84 */       fault.removeFaultDetail(Constants.QNAME_FAULTDETAIL_RUNTIMEEXCEPTION);
/*     */     }
/*  88 */     else if (this.exceptionLog.isDebugEnabled()) {
/*  89 */       this.exceptionLog.debug(Messages.getMessage("axisFault00"), fault);
/*     */     }
/*     */ 
/*  94 */     if (!isDevelopment())
/*     */     {
/*  97 */       fault.removeFaultDetail(Constants.QNAME_FAULTDETAIL_STACKTRACE);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void configureResponseFromAxisFault(HttpServletResponse response, AxisFault fault)
/*     */   {
/* 114 */     int status = getHttpServletResponseStatus(fault);
/*     */ 
/* 116 */     if (status == 401)
/*     */     {
/* 120 */       response.setHeader("WWW-Authenticate", "Basic realm=\"AXIS\"");
/*     */     }
/*     */ 
/* 123 */     response.setStatus(status);
/*     */   }
/*     */ 
/*     */   protected Message convertExceptionToAxisFault(Exception exception, Message responseMsg)
/*     */   {
/* 139 */     logException(exception);
/*     */ 
/* 141 */     if (responseMsg == null) {
/* 142 */       AxisFault fault = AxisFault.makeFault(exception);
/*     */ 
/* 144 */       processAxisFault(fault);
/*     */ 
/* 146 */       responseMsg = new Message(fault);
/*     */     }
/*     */ 
/* 149 */     return responseMsg;
/*     */   }
/*     */ 
/*     */   private int getHttpServletResponseStatus(AxisFault af)
/*     */   {
/* 163 */     return af.getFaultCode().getLocalPart().startsWith("Server.Unauth") ? 401 : 500;
/*     */   }
/*     */ 
/*     */   private void logException(Exception e)
/*     */   {
/* 177 */     this.exceptionLog.info(Messages.getMessage("exception00"), e);
/*     */   }
/*     */ 
/*     */   protected void writeFault(PrintWriter writer, AxisFault axisFault)
/*     */   {
/* 188 */     String localizedMessage = XMLUtils.xmlEncodeString(axisFault.getLocalizedMessage());
/*     */ 
/* 191 */     writer.println("<pre>Fault - " + localizedMessage + "<br>");
/* 192 */     writer.println(axisFault.dumpToString());
/* 193 */     writer.println("</pre>");
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.http.AbstractQueryStringHandler
 * JD-Core Version:    0.6.0
 */