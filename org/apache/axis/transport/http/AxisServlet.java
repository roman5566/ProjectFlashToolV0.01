/*      */ package org.apache.axis.transport.http;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintWriter;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.Set;
/*      */ import javax.servlet.ServletConfig;
/*      */ import javax.servlet.ServletContext;
/*      */ import javax.servlet.ServletException;
/*      */ import javax.servlet.http.HttpServletRequest;
/*      */ import javax.servlet.http.HttpServletResponse;
/*      */ import javax.servlet.http.HttpUtils;
/*      */ import javax.xml.namespace.QName;
/*      */ import javax.xml.soap.MimeHeader;
/*      */ import javax.xml.soap.MimeHeaders;
/*      */ import javax.xml.soap.SOAPException;
/*      */ import org.apache.axis.AxisEngine;
/*      */ import org.apache.axis.AxisFault;
/*      */ import org.apache.axis.ConfigurationException;
/*      */ import org.apache.axis.Constants;
/*      */ import org.apache.axis.EngineConfiguration;
/*      */ import org.apache.axis.Handler;
/*      */ import org.apache.axis.Message;
/*      */ import org.apache.axis.MessageContext;
/*      */ import org.apache.axis.SOAPPart;
/*      */ import org.apache.axis.SimpleTargetedChain;
/*      */ import org.apache.axis.components.logger.LogFactory;
/*      */ import org.apache.axis.description.OperationDesc;
/*      */ import org.apache.axis.description.ServiceDesc;
/*      */ import org.apache.axis.handlers.soap.SOAPService;
/*      */ import org.apache.axis.management.ServiceAdmin;
/*      */ import org.apache.axis.security.servlet.ServletSecurityProvider;
/*      */ import org.apache.axis.server.AxisServer;
/*      */ import org.apache.axis.utils.JavaUtils;
/*      */ import org.apache.axis.utils.Messages;
/*      */ import org.apache.axis.utils.XMLUtils;
/*      */ import org.apache.commons.logging.Log;
/*      */ import org.w3c.dom.Element;
/*      */ 
/*      */ public class AxisServlet extends AxisServletBase
/*      */ {
/*   75 */   protected static Log log = LogFactory.getLog(AxisServlet.class.getName());
/*      */ 
/*   81 */   private static Log tlog = LogFactory.getLog("org.apache.axis.TIME");
/*      */ 
/*   88 */   private static Log exceptionLog = LogFactory.getLog("org.apache.axis.EXCEPTIONS");
/*      */   public static final String INIT_PROPERTY_TRANSPORT_NAME = "transport.name";
/*      */   public static final String INIT_PROPERTY_USE_SECURITY = "use-servlet-security";
/*      */   public static final String INIT_PROPERTY_ENABLE_LIST = "axis.enableListQuery";
/*      */   public static final String INIT_PROPERTY_JWS_CLASS_DIR = "axis.jws.servletClassDir";
/*      */   public static final String INIT_PROPERTY_DISABLE_SERVICES_LIST = "axis.disableServiceList";
/*      */   public static final String INIT_PROPERTY_SERVICES_PATH = "axis.servicesPath";
/*      */   private String transportName;
/*      */   private Handler transport;
/*  115 */   private ServletSecurityProvider securityProvider = null;
/*      */   private String servicesPath;
/*  123 */   private static boolean isDebug = false;
/*      */ 
/*  130 */   private boolean enableList = false;
/*      */ 
/*  136 */   private boolean disableServicesList = false;
/*      */ 
/*  141 */   private String jwsClassDir = null;
/*      */ 
/*  142 */   protected String getJWSClassDir() { return this.jwsClassDir;
/*      */   }
/*      */ 
/*      */   public void init()
/*      */     throws ServletException
/*      */   {
/*  156 */     super.init();
/*  157 */     ServletContext context = getServletConfig().getServletContext();
/*      */ 
/*  159 */     isDebug = log.isDebugEnabled();
/*  160 */     if (isDebug) {
/*  161 */       log.debug("In servlet init");
/*      */     }
/*  163 */     this.transportName = getOption(context, "transport.name", "http");
/*      */ 
/*  167 */     if (JavaUtils.isTrueExplicitly(getOption(context, "use-servlet-security", null)))
/*      */     {
/*  169 */       this.securityProvider = new ServletSecurityProvider();
/*      */     }
/*      */ 
/*  172 */     this.enableList = JavaUtils.isTrueExplicitly(getOption(context, "axis.enableListQuery", null));
/*      */ 
/*  176 */     this.jwsClassDir = getOption(context, "axis.jws.servletClassDir", null);
/*      */ 
/*  179 */     this.disableServicesList = JavaUtils.isTrue(getOption(context, "axis.disableServiceList", "false"));
/*      */ 
/*  182 */     this.servicesPath = getOption(context, "axis.servicesPath", "/services/");
/*      */ 
/*  192 */     if (this.jwsClassDir != null) {
/*  193 */       if (getHomeDir() != null)
/*  194 */         this.jwsClassDir = (getHomeDir() + this.jwsClassDir);
/*      */     }
/*      */     else {
/*  197 */       this.jwsClassDir = getDefaultJWSClassDir();
/*      */     }
/*      */ 
/*  200 */     initQueryStringHandlers();
/*      */     try
/*      */     {
/*  204 */       ServiceAdmin.setEngine(getEngine(), context.getServerInfo());
/*      */     } catch (AxisFault af) {
/*  206 */       exceptionLog.info("Exception setting AxisEngine on ServiceAdmin " + af);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void doGet(HttpServletRequest request, HttpServletResponse response)
/*      */     throws ServletException, IOException
/*      */   {
/*  222 */     if (isDebug) {
/*  223 */       log.debug("Enter: doGet()");
/*      */     }
/*      */ 
/*  226 */     PrintWriter writer = new FilterPrintWriter(response);
/*      */     try
/*      */     {
/*  229 */       AxisEngine engine = getEngine();
/*  230 */       ServletContext servletContext = getServletConfig().getServletContext();
/*      */ 
/*  233 */       String pathInfo = request.getPathInfo();
/*  234 */       String realpath = servletContext.getRealPath(request.getServletPath());
/*  235 */       if (realpath == null) {
/*  236 */         realpath = request.getServletPath();
/*      */       }
/*      */ 
/*  242 */       boolean isJWSPage = request.getRequestURI().endsWith(".jws");
/*  243 */       if (isJWSPage) {
/*  244 */         pathInfo = request.getServletPath();
/*      */       }
/*      */ 
/*  249 */       if (processQuery(request, response, writer) == true) {
/*  250 */         jsr 287;
/*      */       }
/*      */ 
/*  253 */       boolean hasNoPath = (pathInfo == null) || (pathInfo.equals(""));
/*  254 */       if (!this.disableServicesList) {
/*  255 */         if (hasNoPath)
/*      */         {
/*  260 */           reportAvailableServices(response, writer, request);
/*  261 */         } else if (realpath != null)
/*      */         {
/*  265 */           MessageContext msgContext = createMessageContext(engine, request, response);
/*      */ 
/*  280 */           String url = HttpUtils.getRequestURL(request).toString();
/*      */ 
/*  282 */           msgContext.setProperty("transport.url", url);
/*      */           String serviceName;
/*      */           String serviceName;
/*  289 */           if (pathInfo.startsWith("/"))
/*  290 */             serviceName = pathInfo.substring(1);
/*      */           else {
/*  292 */             serviceName = pathInfo;
/*      */           }
/*      */ 
/*  295 */           SOAPService s = engine.getService(serviceName);
/*  296 */           if (s == null)
/*      */           {
/*  298 */             if (isJWSPage)
/*  299 */               reportCantGetJWSService(request, response, writer);
/*      */             else {
/*  301 */               reportCantGetAxisService(request, response, writer);
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/*  306 */             reportServiceInfo(response, writer, s, serviceName);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  314 */         response.setContentType("text/html; charset=utf-8");
/*  315 */         writer.println("<html><h1>Axis HTTP Servlet</h1>");
/*  316 */         writer.println(Messages.getMessage("reachedServlet00"));
/*      */ 
/*  318 */         writer.println("<p>" + Messages.getMessage("transportName00", new StringBuffer().append("<b>").append(this.transportName).append("</b>").toString()));
/*      */ 
/*  321 */         writer.println("</html>");
/*      */       }
/*      */     } catch (AxisFault fault) {
/*  324 */       reportTroubleInGet(fault, response, writer);
/*      */     } catch (Exception e) {
/*  326 */       reportTroubleInGet(e, response, writer);
/*      */     } finally {
/*  328 */       writer.close();
/*  329 */       if (isDebug)
/*  330 */         log.debug("Exit: doGet()");
/*      */     }
/*      */   }
/*      */ 
/*      */   private void reportTroubleInGet(Throwable exception, HttpServletResponse response, PrintWriter writer)
/*      */   {
/*  347 */     response.setContentType("text/html; charset=utf-8");
/*  348 */     response.setStatus(500);
/*  349 */     writer.println("<h2>" + Messages.getMessage("error00") + "</h2>");
/*      */ 
/*  352 */     writer.println("<p>" + Messages.getMessage("somethingWrong00") + "</p>");
/*      */ 
/*  355 */     if ((exception instanceof AxisFault)) {
/*  356 */       AxisFault fault = (AxisFault)exception;
/*  357 */       processAxisFault(fault);
/*  358 */       writeFault(writer, fault);
/*      */     } else {
/*  360 */       logException(exception);
/*  361 */       writer.println("<pre>Exception - " + exception + "<br>");
/*      */ 
/*  363 */       if (isDevelopment()) {
/*  364 */         writer.println(JavaUtils.stackToString(exception));
/*      */       }
/*  366 */       writer.println("</pre>");
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void processAxisFault(AxisFault fault)
/*      */   {
/*  378 */     Element runtimeException = fault.lookupFaultDetail(Constants.QNAME_FAULTDETAIL_RUNTIMEEXCEPTION);
/*      */ 
/*  380 */     if (runtimeException != null) {
/*  381 */       exceptionLog.info(Messages.getMessage("axisFault00"), fault);
/*      */ 
/*  383 */       fault.removeFaultDetail(Constants.QNAME_FAULTDETAIL_RUNTIMEEXCEPTION);
/*      */     }
/*  385 */     else if (exceptionLog.isDebugEnabled()) {
/*  386 */       exceptionLog.debug(Messages.getMessage("axisFault00"), fault);
/*      */     }
/*      */ 
/*  389 */     if (!isDevelopment())
/*      */     {
/*  391 */       fault.removeFaultDetail(Constants.QNAME_FAULTDETAIL_STACKTRACE);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void logException(Throwable e)
/*      */   {
/*  400 */     exceptionLog.info(Messages.getMessage("exception00"), e);
/*      */   }
/*      */ 
/*      */   private void writeFault(PrintWriter writer, AxisFault axisFault)
/*      */   {
/*  410 */     String localizedMessage = XMLUtils.xmlEncodeString(axisFault.getLocalizedMessage());
/*      */ 
/*  412 */     writer.println("<pre>Fault - " + localizedMessage + "<br>");
/*  413 */     writer.println(axisFault.dumpToString());
/*  414 */     writer.println("</pre>");
/*      */   }
/*      */ 
/*      */   protected void reportServiceInfo(HttpServletResponse response, PrintWriter writer, SOAPService service, String serviceName)
/*      */   {
/*  427 */     response.setContentType("text/html; charset=utf-8");
/*      */ 
/*  429 */     writer.println("<h1>" + service.getName() + "</h1>");
/*      */ 
/*  432 */     writer.println("<p>" + Messages.getMessage("axisService00") + "</p>");
/*      */ 
/*  436 */     writer.println("<i>" + Messages.getMessage("perhaps00") + "</i>");
/*      */   }
/*      */ 
/*      */   protected void reportNoWSDL(HttpServletResponse res, PrintWriter writer, String moreDetailCode, AxisFault axisFault)
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void reportAvailableServices(HttpServletResponse response, PrintWriter writer, HttpServletRequest request)
/*      */     throws ConfigurationException, AxisFault
/*      */   {
/*  475 */     AxisEngine engine = getEngine();
/*      */ 
/*  477 */     response.setContentType("text/html; charset=utf-8");
/*  478 */     writer.println("<h2>And now... Some Services</h2>");
/*      */     try
/*      */     {
/*  482 */       i = engine.getConfig().getDeployedServices();
/*      */     }
/*      */     catch (ConfigurationException configException)
/*      */     {
/*      */       Iterator i;
/*  486 */       if ((configException.getContainedException() instanceof AxisFault)) {
/*  487 */         throw ((AxisFault)configException.getContainedException());
/*      */       }
/*  489 */       throw configException;
/*      */     }
/*      */     Iterator i;
/*  494 */     String defaultBaseURL = getWebappBase(request) + this.servicesPath;
/*  495 */     writer.println("<ul>");
/*  496 */     while (i.hasNext()) {
/*  497 */       ServiceDesc sd = (ServiceDesc)i.next();
/*  498 */       StringBuffer sb = new StringBuffer();
/*  499 */       sb.append("<li>");
/*  500 */       String name = sd.getName();
/*  501 */       sb.append(name);
/*  502 */       sb.append(" <a href=\"");
/*  503 */       String endpointURL = sd.getEndpointURL();
/*  504 */       String baseURL = endpointURL == null ? defaultBaseURL : endpointURL;
/*      */ 
/*  506 */       sb.append(baseURL);
/*  507 */       sb.append(name);
/*  508 */       sb.append("?wsdl\"><i>(wsdl)</i></a></li>");
/*  509 */       writer.println(sb.toString());
/*  510 */       ArrayList operations = sd.getOperations();
/*  511 */       if (!operations.isEmpty()) {
/*  512 */         writer.println("<ul>");
/*  513 */         for (Iterator it = operations.iterator(); it.hasNext(); ) {
/*  514 */           OperationDesc desc = (OperationDesc)it.next();
/*  515 */           writer.println("<li>" + desc.getName());
/*      */         }
/*  517 */         writer.println("</ul>");
/*      */       }
/*      */     }
/*  520 */     writer.println("</ul>");
/*      */   }
/*      */ 
/*      */   protected void reportCantGetAxisService(HttpServletRequest request, HttpServletResponse response, PrintWriter writer)
/*      */   {
/*  533 */     response.setStatus(404);
/*  534 */     response.setContentType("text/html; charset=utf-8");
/*  535 */     writer.println("<h2>" + Messages.getMessage("error00") + "</h2>");
/*      */ 
/*  537 */     writer.println("<p>" + Messages.getMessage("noService06") + "</p>");
/*      */   }
/*      */ 
/*      */   protected void reportCantGetJWSService(HttpServletRequest request, HttpServletResponse response, PrintWriter writer)
/*      */   {
/*  554 */     String requestPath = request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : "");
/*      */ 
/*  556 */     String realpath = getServletConfig().getServletContext().getRealPath(requestPath);
/*      */ 
/*  558 */     log.debug("JWS real path: " + realpath);
/*  559 */     boolean foundJWSFile = (new File(realpath).exists()) && (realpath.endsWith(".jws"));
/*      */ 
/*  562 */     response.setContentType("text/html; charset=utf-8");
/*  563 */     if (foundJWSFile) {
/*  564 */       response.setStatus(200);
/*  565 */       writer.println(Messages.getMessage("foundJWS00") + "<p>");
/*  566 */       String url = request.getRequestURI();
/*  567 */       String urltext = Messages.getMessage("foundJWS01");
/*  568 */       writer.println("<a href='" + url + "?wsdl'>" + urltext + "</a>");
/*      */     } else {
/*  570 */       response.setStatus(404);
/*  571 */       writer.println(Messages.getMessage("noService06"));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void doPost(HttpServletRequest req, HttpServletResponse res)
/*      */     throws ServletException, IOException
/*      */   {
/*  586 */     long t0 = 0L; long t1 = 0L; long t2 = 0L; long t3 = 0L; long t4 = 0L;
/*  587 */     String soapAction = null;
/*  588 */     MessageContext msgContext = null;
/*  589 */     if (isDebug) {
/*  590 */       log.debug("Enter: doPost()");
/*      */     }
/*  592 */     if (tlog.isDebugEnabled()) {
/*  593 */       t0 = System.currentTimeMillis();
/*      */     }
/*      */ 
/*  596 */     Message responseMsg = null;
/*  597 */     String contentType = null;
/*      */     try
/*      */     {
/*  600 */       AxisEngine engine = getEngine();
/*      */ 
/*  602 */       if (engine == null)
/*      */       {
/*  604 */         ServletException se = new ServletException(Messages.getMessage("noEngine00"));
/*      */ 
/*  606 */         log.debug("No Engine!", se);
/*  607 */         throw se;
/*      */       }
/*      */ 
/*  610 */       res.setBufferSize(8192);
/*      */ 
/*  614 */       msgContext = createMessageContext(engine, req, res);
/*      */ 
/*  618 */       if (this.securityProvider != null) {
/*  619 */         if (isDebug) {
/*  620 */           log.debug("securityProvider:" + this.securityProvider);
/*      */         }
/*  622 */         msgContext.setProperty("securityProvider", this.securityProvider);
/*      */       }
/*      */ 
/*  628 */       Message requestMsg = new Message(req.getInputStream(), false, req.getHeader("Content-Type"), req.getHeader("Content-Location"));
/*      */ 
/*  635 */       MimeHeaders requestMimeHeaders = requestMsg.getMimeHeaders();
/*  636 */       for (Enumeration e = req.getHeaderNames(); e.hasMoreElements(); ) {
/*  637 */         String headerName = (String)e.nextElement();
/*  638 */         Enumeration f = req.getHeaders(headerName);
/*  639 */         while (f.hasMoreElements()) {
/*  640 */           String headerValue = (String)f.nextElement();
/*  641 */           requestMimeHeaders.addHeader(headerName, headerValue);
/*      */         }
/*      */       }
/*      */ 
/*  645 */       if (isDebug) {
/*  646 */         log.debug("Request Message:" + requestMsg);
/*      */       }
/*      */ 
/*  651 */       msgContext.setRequestMessage(requestMsg);
/*  652 */       String url = HttpUtils.getRequestURL(req).toString();
/*  653 */       msgContext.setProperty("transport.url", url);
/*      */       try
/*      */       {
/*  658 */         String requestEncoding = (String)requestMsg.getProperty("javax.xml.soap.character-set-encoding");
/*      */ 
/*  660 */         if (requestEncoding != null) {
/*  661 */           msgContext.setProperty("javax.xml.soap.character-set-encoding", requestEncoding);
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (SOAPException e1)
/*      */       {
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/*  678 */         soapAction = getSoapAction(req);
/*      */ 
/*  680 */         if (soapAction != null) {
/*  681 */           msgContext.setUseSOAPAction(true);
/*  682 */           msgContext.setSOAPActionURI(soapAction);
/*      */         }
/*      */ 
/*  688 */         msgContext.setSession(new AxisHttpSession(req));
/*      */ 
/*  690 */         if (tlog.isDebugEnabled()) {
/*  691 */           t1 = System.currentTimeMillis();
/*      */         }
/*      */ 
/*  695 */         if (isDebug) {
/*  696 */           log.debug("Invoking Axis Engine.");
/*      */         }
/*      */ 
/*  699 */         engine.invoke(msgContext);
/*  700 */         if (isDebug) {
/*  701 */           log.debug("Return from Axis Engine.");
/*      */         }
/*  703 */         if (tlog.isDebugEnabled()) {
/*  704 */           t2 = System.currentTimeMillis();
/*      */         }
/*  706 */         responseMsg = msgContext.getResponseMessage();
/*      */       }
/*      */       catch (AxisFault fault)
/*      */       {
/*  715 */         processAxisFault(fault);
/*  716 */         configureResponseFromAxisFault(res, fault);
/*  717 */         responseMsg = msgContext.getResponseMessage();
/*  718 */         if (responseMsg == null) {
/*  719 */           responseMsg = new Message(fault);
/*  720 */           ((SOAPPart)responseMsg.getSOAPPart()).getMessage().setMessageContext(msgContext);
/*      */         }
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*  725 */         responseMsg = msgContext.getResponseMessage();
/*  726 */         res.setStatus(500);
/*  727 */         responseMsg = convertExceptionToAxisFault(e, responseMsg);
/*  728 */         ((SOAPPart)responseMsg.getSOAPPart()).getMessage().setMessageContext(msgContext);
/*      */       }
/*      */       catch (Throwable t) {
/*  731 */         logException(t);
/*      */ 
/*  733 */         responseMsg = msgContext.getResponseMessage();
/*  734 */         res.setStatus(500);
/*  735 */         responseMsg = new Message(new AxisFault(t.toString(), t));
/*  736 */         ((SOAPPart)responseMsg.getSOAPPart()).getMessage().setMessageContext(msgContext);
/*      */       }
/*      */     }
/*      */     catch (AxisFault fault) {
/*  740 */       processAxisFault(fault);
/*  741 */       configureResponseFromAxisFault(res, fault);
/*  742 */       responseMsg = msgContext.getResponseMessage();
/*  743 */       if (responseMsg == null) {
/*  744 */         responseMsg = new Message(fault);
/*  745 */         ((SOAPPart)responseMsg.getSOAPPart()).getMessage().setMessageContext(msgContext);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  750 */     if (tlog.isDebugEnabled()) {
/*  751 */       t3 = System.currentTimeMillis();
/*      */     }
/*      */ 
/*  756 */     if (responseMsg != null)
/*      */     {
/*  758 */       MimeHeaders responseMimeHeaders = responseMsg.getMimeHeaders();
/*  759 */       for (Iterator i = responseMimeHeaders.getAllHeaders(); i.hasNext(); ) {
/*  760 */         MimeHeader responseMimeHeader = (MimeHeader)i.next();
/*  761 */         res.addHeader(responseMimeHeader.getName(), responseMimeHeader.getValue());
/*      */       }
/*      */ 
/*  765 */       String responseEncoding = (String)msgContext.getProperty("javax.xml.soap.character-set-encoding");
/*      */ 
/*  767 */       if (responseEncoding != null) {
/*      */         try {
/*  769 */           responseMsg.setProperty("javax.xml.soap.character-set-encoding", responseEncoding);
/*      */         }
/*      */         catch (SOAPException e)
/*      */         {
/*      */         }
/*      */       }
/*  775 */       contentType = responseMsg.getContentType(msgContext.getSOAPConstants());
/*      */ 
/*  777 */       sendResponse(contentType, res, responseMsg);
/*      */     }
/*      */     else {
/*  780 */       res.setStatus(202);
/*      */     }
/*      */ 
/*  783 */     if (isDebug) {
/*  784 */       log.debug("Response sent.");
/*  785 */       log.debug("Exit: doPost()");
/*      */     }
/*  787 */     if (tlog.isDebugEnabled()) {
/*  788 */       t4 = System.currentTimeMillis();
/*  789 */       tlog.debug("axisServlet.doPost: " + soapAction + " pre=" + (t1 - t0) + " invoke=" + (t2 - t1) + " post=" + (t3 - t2) + " send=" + (t4 - t3) + " " + msgContext.getTargetService() + "." + (msgContext.getOperation() == null ? "" : msgContext.getOperation().getName()));
/*      */     }
/*      */   }
/*      */ 
/*      */   private void configureResponseFromAxisFault(HttpServletResponse response, AxisFault fault)
/*      */   {
/*  812 */     int status = getHttpServletResponseStatus(fault);
/*  813 */     if (status == 401)
/*      */     {
/*  816 */       response.setHeader("WWW-Authenticate", "Basic realm=\"AXIS\"");
/*      */     }
/*  818 */     response.setStatus(status);
/*      */   }
/*      */ 
/*      */   private Message convertExceptionToAxisFault(Exception exception, Message responseMsg)
/*      */   {
/*  833 */     logException(exception);
/*  834 */     if (responseMsg == null) {
/*  835 */       AxisFault fault = AxisFault.makeFault(exception);
/*  836 */       processAxisFault(fault);
/*  837 */       responseMsg = new Message(fault);
/*      */     }
/*  839 */     return responseMsg;
/*      */   }
/*      */ 
/*      */   protected int getHttpServletResponseStatus(AxisFault af)
/*      */   {
/*  851 */     return af.getFaultCode().getLocalPart().startsWith("Server.Unauth") ? 401 : 500;
/*      */   }
/*      */ 
/*      */   private void sendResponse(String contentType, HttpServletResponse res, Message responseMsg)
/*      */     throws AxisFault, IOException
/*      */   {
/*  870 */     if (responseMsg == null) {
/*  871 */       res.setStatus(204);
/*  872 */       if (isDebug) {
/*  873 */         log.debug("NO AXIS MESSAGE TO RETURN!");
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  879 */       if (isDebug) {
/*  880 */         log.debug("Returned Content-Type:" + contentType);
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/*  887 */         res.setContentType(contentType);
/*      */ 
/*  902 */         responseMsg.writeTo(res.getOutputStream());
/*      */       } catch (SOAPException e) {
/*  904 */         logException(e);
/*      */       }
/*      */     }
/*      */ 
/*  908 */     if (!res.isCommitted())
/*  909 */       res.flushBuffer();
/*      */   }
/*      */ 
/*      */   private MessageContext createMessageContext(AxisEngine engine, HttpServletRequest req, HttpServletResponse res)
/*      */   {
/*  924 */     MessageContext msgContext = new MessageContext(engine);
/*      */ 
/*  926 */     String requestPath = getRequestPath(req);
/*      */ 
/*  928 */     if (isDebug) {
/*  929 */       log.debug("MessageContext:" + msgContext);
/*  930 */       log.debug("HEADER_CONTENT_TYPE:" + req.getHeader("Content-Type"));
/*      */ 
/*  932 */       log.debug("HEADER_CONTENT_LOCATION:" + req.getHeader("Content-Location"));
/*      */ 
/*  934 */       log.debug("Constants.MC_HOME_DIR:" + String.valueOf(getHomeDir()));
/*  935 */       log.debug("Constants.MC_RELATIVE_PATH:" + requestPath);
/*  936 */       log.debug("HTTPConstants.MC_HTTP_SERVLETLOCATION:" + String.valueOf(getWebInfPath()));
/*      */ 
/*  938 */       log.debug("HTTPConstants.MC_HTTP_SERVLETPATHINFO:" + req.getPathInfo());
/*      */ 
/*  940 */       log.debug("HTTPConstants.HEADER_AUTHORIZATION:" + req.getHeader("Authorization"));
/*      */ 
/*  942 */       log.debug("Constants.MC_REMOTE_ADDR:" + req.getRemoteAddr());
/*  943 */       log.debug("configPath:" + String.valueOf(getWebInfPath()));
/*      */     }
/*      */ 
/*  948 */     msgContext.setTransportName(this.transportName);
/*      */ 
/*  952 */     msgContext.setProperty("jws.classDir", this.jwsClassDir);
/*  953 */     msgContext.setProperty("home.dir", getHomeDir());
/*  954 */     msgContext.setProperty("path", requestPath);
/*  955 */     msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLET, this);
/*  956 */     msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST, req);
/*  957 */     msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETRESPONSE, res);
/*  958 */     msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETLOCATION, getWebInfPath());
/*      */ 
/*  960 */     msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETPATHINFO, req.getPathInfo());
/*      */ 
/*  962 */     msgContext.setProperty("Authorization", req.getHeader("Authorization"));
/*      */ 
/*  964 */     msgContext.setProperty("remoteaddr", req.getRemoteAddr());
/*      */ 
/*  967 */     ServletEndpointContextImpl sec = new ServletEndpointContextImpl();
/*      */ 
/*  969 */     msgContext.setProperty("servletEndpointContext", sec);
/*      */ 
/*  972 */     String realpath = getServletConfig().getServletContext().getRealPath(requestPath);
/*      */ 
/*  975 */     if (realpath != null) {
/*  976 */       msgContext.setProperty("realpath", realpath);
/*      */     }
/*      */ 
/*  979 */     msgContext.setProperty("configPath", getWebInfPath());
/*      */ 
/*  981 */     return msgContext;
/*      */   }
/*      */ 
/*      */   private String getSoapAction(HttpServletRequest req)
/*      */     throws AxisFault
/*      */   {
/*  993 */     String soapAction = req.getHeader("SOAPAction");
/*  994 */     if (soapAction == null) {
/*  995 */       String contentType = req.getHeader("Content-Type");
/*  996 */       if (contentType != null) {
/*  997 */         int index = contentType.indexOf("action");
/*  998 */         if (index != -1) {
/*  999 */           soapAction = contentType.substring(index + 7);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1004 */     if (isDebug) {
/* 1005 */       log.debug("HEADER_SOAP_ACTION:" + soapAction);
/*      */     }
/*      */ 
/* 1012 */     if (soapAction == null) {
/* 1013 */       AxisFault af = new AxisFault("Client.NoSOAPAction", Messages.getMessage("noHeader00", "SOAPAction"), null, null);
/*      */ 
/* 1018 */       exceptionLog.error(Messages.getMessage("genFault00"), af);
/*      */ 
/* 1020 */       throw af;
/*      */     }
/*      */ 
/* 1026 */     if ((soapAction.startsWith("\"")) && (soapAction.endsWith("\"")) && (soapAction.length() >= 2))
/*      */     {
/* 1028 */       int end = soapAction.length() - 1;
/* 1029 */       soapAction = soapAction.substring(1, end);
/*      */     }
/*      */ 
/* 1032 */     if (soapAction.length() == 0) {
/* 1033 */       soapAction = req.getContextPath();
/*      */     }
/*      */ 
/* 1036 */     return soapAction;
/*      */   }
/*      */ 
/*      */   protected String getDefaultJWSClassDir()
/*      */   {
/* 1045 */     return getWebInfPath() + File.separator + "jwsClasses";
/*      */   }
/*      */ 
/*      */   public void initQueryStringHandlers()
/*      */   {
/*      */     try
/*      */     {
/* 1057 */       this.transport = getEngine().getTransport(this.transportName);
/*      */ 
/* 1059 */       if (this.transport == null)
/*      */       {
/* 1063 */         this.transport = new SimpleTargetedChain();
/*      */ 
/* 1065 */         this.transport.setOption("qs.list", "org.apache.axis.transport.http.QSListHandler");
/*      */ 
/* 1067 */         this.transport.setOption("qs.method", "org.apache.axis.transport.http.QSMethodHandler");
/*      */ 
/* 1069 */         this.transport.setOption("qs.wsdl", "org.apache.axis.transport.http.QSWSDLHandler");
/*      */ 
/* 1072 */         return;
/*      */       }
/*      */ 
/* 1079 */       boolean defaultQueryStrings = true;
/* 1080 */       String useDefaults = (String)this.transport.getOption("useDefaultQueryStrings");
/*      */ 
/* 1083 */       if ((useDefaults != null) && (useDefaults.toLowerCase().equals("false")))
/*      */       {
/* 1085 */         defaultQueryStrings = false;
/*      */       }
/*      */ 
/* 1088 */       if (defaultQueryStrings == true)
/*      */       {
/* 1091 */         this.transport.setOption("qs.list", "org.apache.axis.transport.http.QSListHandler");
/*      */ 
/* 1093 */         this.transport.setOption("qs.method", "org.apache.axis.transport.http.QSMethodHandler");
/*      */ 
/* 1095 */         this.transport.setOption("qs.wsdl", "org.apache.axis.transport.http.QSWSDLHandler");
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (AxisFault e)
/*      */     {
/* 1104 */       this.transport = new SimpleTargetedChain();
/*      */ 
/* 1106 */       this.transport.setOption("qs.list", "org.apache.axis.transport.http.QSListHandler");
/*      */ 
/* 1108 */       this.transport.setOption("qs.method", "org.apache.axis.transport.http.QSMethodHandler");
/*      */ 
/* 1110 */       this.transport.setOption("qs.wsdl", "org.apache.axis.transport.http.QSWSDLHandler");
/*      */ 
/* 1113 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean processQuery(HttpServletRequest request, HttpServletResponse response, PrintWriter writer)
/*      */     throws AxisFault
/*      */   {
/* 1131 */     String path = request.getServletPath();
/* 1132 */     String queryString = request.getQueryString();
/*      */ 
/* 1134 */     AxisEngine engine = getEngine();
/* 1135 */     Iterator i = this.transport.getOptions().keySet().iterator();
/*      */ 
/* 1137 */     if (queryString == null) {
/* 1138 */       return false;
/*      */     }
/*      */ 
/* 1141 */     String servletURI = request.getContextPath() + path;
/* 1142 */     String reqURI = request.getRequestURI();
/*      */     String serviceName;
/*      */     String serviceName;
/* 1144 */     if (servletURI.length() + 1 < reqURI.length())
/* 1145 */       serviceName = reqURI.substring(servletURI.length() + 1);
/*      */     else
/* 1147 */       serviceName = "";
/* 1148 */     while (i.hasNext() == true) {
/* 1149 */       String queryHandler = (String)i.next();
/*      */ 
/* 1151 */       if (queryHandler.startsWith("qs.") == true)
/*      */       {
/* 1155 */         String handlerName = queryHandler.substring(queryHandler.indexOf(".") + 1).toLowerCase();
/*      */ 
/* 1163 */         int length = 0;
/* 1164 */         boolean firstParamFound = false;
/*      */ 
/* 1166 */         while ((!firstParamFound) && (length < queryString.length())) {
/* 1167 */           char ch = queryString.charAt(length++);
/*      */ 
/* 1169 */           if ((ch == '&') || (ch == '=')) {
/* 1170 */             firstParamFound = true;
/*      */ 
/* 1172 */             length--;
/*      */           }
/*      */         }
/*      */ 
/* 1176 */         if (length < queryString.length()) {
/* 1177 */           queryString = queryString.substring(0, length);
/*      */         }
/*      */ 
/* 1180 */         if (queryString.toLowerCase().equals(handlerName) == true)
/*      */         {
/* 1186 */           if (this.transport.getOption(queryHandler).equals("")) {
/* 1187 */             return false;
/*      */           }
/*      */ 
/*      */           try
/*      */           {
/* 1194 */             MessageContext msgContext = createMessageContext(engine, request, response);
/*      */ 
/* 1196 */             Class plugin = Class.forName((String)this.transport.getOption(queryHandler));
/*      */ 
/* 1198 */             Method pluginMethod = plugin.getDeclaredMethod("invoke", new Class[] { msgContext.getClass() });
/*      */ 
/* 1200 */             String url = HttpUtils.getRequestURL(request).toString();
/*      */ 
/* 1205 */             msgContext.setProperty("transport.url", url);
/* 1206 */             msgContext.setProperty("transport.http.plugin.serviceName", serviceName);
/*      */ 
/* 1208 */             msgContext.setProperty("transport.http.plugin.pluginName", handlerName);
/*      */ 
/* 1210 */             msgContext.setProperty("transport.http.plugin.isDevelopment", new Boolean(isDevelopment()));
/*      */ 
/* 1213 */             msgContext.setProperty("transport.http.plugin.enableList", new Boolean(this.enableList));
/*      */ 
/* 1215 */             msgContext.setProperty("transport.http.plugin.engine", engine);
/*      */ 
/* 1217 */             msgContext.setProperty("transport.http.plugin.writer", writer);
/*      */ 
/* 1219 */             msgContext.setProperty("transport.http.plugin.log", log);
/* 1220 */             msgContext.setProperty("transport.http.plugin.exceptionLog", exceptionLog);
/*      */ 
/* 1226 */             pluginMethod.invoke(plugin.newInstance(), new Object[] { msgContext });
/*      */ 
/* 1229 */             writer.close();
/*      */ 
/* 1231 */             return true;
/*      */           } catch (InvocationTargetException ie) {
/* 1233 */             reportTroubleInGet(ie.getTargetException(), response, writer);
/*      */ 
/* 1236 */             return true;
/*      */           } catch (Exception e) {
/* 1238 */             reportTroubleInGet(e, response, writer);
/*      */ 
/* 1240 */             return true;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1246 */     return false;
/*      */   }
/*      */ 
/*      */   private static String getRequestPath(HttpServletRequest request)
/*      */   {
/* 1259 */     return request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : "");
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.http.AxisServlet
 * JD-Core Version:    0.6.0
 */