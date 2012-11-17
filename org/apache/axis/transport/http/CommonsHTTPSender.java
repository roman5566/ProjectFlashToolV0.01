/*     */ package org.apache.axis.transport.http;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.zip.GZIPInputStream;
/*     */ import java.util.zip.GZIPOutputStream;
/*     */ import javax.xml.soap.MimeHeader;
/*     */ import javax.xml.soap.MimeHeaders;
/*     */ import javax.xml.soap.SOAPException;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.Message;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.components.net.CommonsHTTPClientProperties;
/*     */ import org.apache.axis.components.net.CommonsHTTPClientPropertiesFactory;
/*     */ import org.apache.axis.components.net.TransportClientProperties;
/*     */ import org.apache.axis.components.net.TransportClientPropertiesFactory;
/*     */ import org.apache.axis.handlers.BasicHandler;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.NetworkUtils;
/*     */ import org.apache.commons.httpclient.Cookie;
/*     */ import org.apache.commons.httpclient.Credentials;
/*     */ import org.apache.commons.httpclient.Header;
/*     */ import org.apache.commons.httpclient.HostConfiguration;
/*     */ import org.apache.commons.httpclient.HttpClient;
/*     */ import org.apache.commons.httpclient.HttpConnectionManager;
/*     */ import org.apache.commons.httpclient.HttpMethodBase;
/*     */ import org.apache.commons.httpclient.HttpState;
/*     */ import org.apache.commons.httpclient.HttpVersion;
/*     */ import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
/*     */ import org.apache.commons.httpclient.NTCredentials;
/*     */ import org.apache.commons.httpclient.UsernamePasswordCredentials;
/*     */ import org.apache.commons.httpclient.auth.AuthScope;
/*     */ import org.apache.commons.httpclient.methods.GetMethod;
/*     */ import org.apache.commons.httpclient.methods.PostMethod;
/*     */ import org.apache.commons.httpclient.methods.RequestEntity;
/*     */ import org.apache.commons.httpclient.params.HttpClientParams;
/*     */ import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
/*     */ import org.apache.commons.httpclient.params.HttpMethodParams;
/*     */ import org.apache.commons.httpclient.protocol.Protocol;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class CommonsHTTPSender extends BasicHandler
/*     */ {
/*  84 */   protected static Log log = LogFactory.getLog(CommonsHTTPSender.class.getName());
/*     */   protected HttpConnectionManager connectionManager;
/*     */   protected CommonsHTTPClientProperties clientProperties;
/*  89 */   boolean httpChunkStream = true;
/*     */ 
/*     */   public CommonsHTTPSender() {
/*  92 */     initialize();
/*     */   }
/*     */ 
/*     */   protected void initialize() {
/*  96 */     MultiThreadedHttpConnectionManager cm = new MultiThreadedHttpConnectionManager();
/*  97 */     this.clientProperties = CommonsHTTPClientPropertiesFactory.create();
/*  98 */     cm.getParams().setDefaultMaxConnectionsPerHost(this.clientProperties.getMaximumConnectionsPerHost());
/*  99 */     cm.getParams().setMaxTotalConnections(this.clientProperties.getMaximumTotalConnections());
/*     */ 
/* 102 */     if (this.clientProperties.getDefaultConnectionTimeout() > 0) {
/* 103 */       cm.getParams().setConnectionTimeout(this.clientProperties.getDefaultConnectionTimeout());
/*     */     }
/* 105 */     if (this.clientProperties.getDefaultSoTimeout() > 0) {
/* 106 */       cm.getParams().setSoTimeout(this.clientProperties.getDefaultSoTimeout());
/*     */     }
/* 108 */     this.connectionManager = cm;
/*     */   }
/*     */ 
/*     */   public void invoke(MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/* 120 */     HttpMethodBase method = null;
/* 121 */     if (log.isDebugEnabled()) {
/* 122 */       log.debug(Messages.getMessage("enter00", "CommonsHTTPSender::invoke"));
/*     */     }
/*     */     try
/*     */     {
/* 126 */       URL targetURL = new URL(msgContext.getStrProp("transport.url"));
/*     */ 
/* 133 */       HttpClient httpClient = new HttpClient(this.connectionManager);
/*     */ 
/* 135 */       httpClient.getParams().setConnectionManagerTimeout(this.clientProperties.getConnectionPoolTimeout());
/*     */ 
/* 137 */       HostConfiguration hostConfiguration = getHostConfiguration(httpClient, msgContext, targetURL);
/*     */ 
/* 140 */       boolean posting = true;
/*     */ 
/* 144 */       if (msgContext.getSOAPConstants() == SOAPConstants.SOAP12_CONSTANTS) {
/* 145 */         String webMethod = msgContext.getStrProp("soap12.webmethod");
/* 146 */         if (webMethod != null) {
/* 147 */           posting = webMethod.equals("POST");
/*     */         }
/*     */       }
/*     */ 
/* 151 */       if (posting) {
/* 152 */         Message reqMessage = msgContext.getRequestMessage();
/* 153 */         method = new PostMethod(targetURL.toString());
/*     */ 
/* 156 */         method.getParams().setBooleanParameter("http.protocol.expect-continue", false);
/*     */ 
/* 159 */         addContextInfo(method, httpClient, msgContext, targetURL);
/*     */ 
/* 161 */         MessageRequestEntity requestEntity = null;
/* 162 */         if (msgContext.isPropertyTrue("transport.http.gzipRequest"))
/* 163 */           requestEntity = new GzipMessageRequestEntity(method, reqMessage, this.httpChunkStream);
/*     */         else {
/* 165 */           requestEntity = new MessageRequestEntity(method, reqMessage, this.httpChunkStream);
/*     */         }
/* 167 */         ((PostMethod)method).setRequestEntity(requestEntity);
/*     */       } else {
/* 169 */         method = new GetMethod(targetURL.toString());
/* 170 */         addContextInfo(method, httpClient, msgContext, targetURL);
/*     */       }
/*     */ 
/* 173 */       String httpVersion = msgContext.getStrProp("axis.transport.version");
/*     */ 
/* 175 */       if ((httpVersion != null) && 
/* 176 */         (httpVersion.equals(HTTPConstants.HEADER_PROTOCOL_V10))) {
/* 177 */         method.getParams().setVersion(HttpVersion.HTTP_1_0);
/*     */       }
/*     */ 
/* 185 */       if (msgContext.getMaintainSession()) {
/* 186 */         HttpState state = httpClient.getState();
/* 187 */         method.getParams().setCookiePolicy("compatibility");
/* 188 */         String host = hostConfiguration.getHost();
/* 189 */         String path = targetURL.getPath();
/* 190 */         boolean secure = hostConfiguration.getProtocol().isSecure();
/* 191 */         fillHeaders(msgContext, state, "Cookie", host, path, secure);
/* 192 */         fillHeaders(msgContext, state, "Cookie2", host, path, secure);
/* 193 */         httpClient.setState(state);
/*     */       }
/*     */ 
/* 196 */       int returnCode = httpClient.executeMethod(hostConfiguration, method, null);
/*     */ 
/* 198 */       String contentType = getHeader(method, "Content-Type");
/*     */ 
/* 200 */       String contentLocation = getHeader(method, "Content-Location");
/*     */ 
/* 202 */       String contentLength = getHeader(method, "Content-Length");
/*     */ 
/* 205 */       if ((returnCode <= 199) || (returnCode >= 300))
/*     */       {
/* 208 */         if (msgContext.getSOAPConstants() != SOAPConstants.SOAP12_CONSTANTS)
/*     */         {
/* 212 */           if ((contentType == null) || (contentType.equals("text/html")) || (returnCode <= 499) || (returnCode >= 600))
/*     */           {
/* 217 */             String statusMessage = method.getStatusText();
/* 218 */             AxisFault fault = new AxisFault("HTTP", "(" + returnCode + ")" + statusMessage, null, null);
/*     */             try
/*     */             {
/* 224 */               fault.setFaultDetailString(Messages.getMessage("return01", "" + returnCode, method.getResponseBodyAsString()));
/*     */ 
/* 228 */               fault.addFaultDetail(Constants.QNAME_FAULTDETAIL_HTTPERRORCODE, Integer.toString(returnCode));
/*     */ 
/* 230 */               throw fault;
/*     */             } finally {
/* 232 */               method.releaseConnection();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 238 */       InputStream releaseConnectionOnCloseStream = createConnectionReleasingInputStream(method);
/*     */ 
/* 241 */       Header contentEncoding = method.getResponseHeader("Content-Encoding");
/*     */ 
/* 243 */       if (contentEncoding != null) {
/* 244 */         if (contentEncoding.getValue().equalsIgnoreCase("gzip"))
/*     */         {
/* 246 */           releaseConnectionOnCloseStream = new GZIPInputStream(releaseConnectionOnCloseStream);
/*     */         }
/*     */         else {
/* 249 */           AxisFault fault = new AxisFault("HTTP", "unsupported content-encoding of '" + contentEncoding.getValue() + "' found", null, null);
/*     */ 
/* 253 */           throw fault;
/*     */         }
/*     */       }
/*     */ 
/* 257 */       Message outMsg = new Message(releaseConnectionOnCloseStream, false, contentType, contentLocation);
/*     */ 
/* 260 */       Header[] responseHeaders = method.getResponseHeaders();
/* 261 */       MimeHeaders responseMimeHeaders = outMsg.getMimeHeaders();
/* 262 */       for (int i = 0; i < responseHeaders.length; i++) {
/* 263 */         Header responseHeader = responseHeaders[i];
/* 264 */         responseMimeHeaders.addHeader(responseHeader.getName(), responseHeader.getValue());
/*     */       }
/*     */ 
/* 267 */       outMsg.setMessageType("response");
/* 268 */       msgContext.setResponseMessage(outMsg);
/* 269 */       if (log.isDebugEnabled()) {
/* 270 */         if (null == contentLength) {
/* 271 */           log.debug("\n" + Messages.getMessage("no00", "Content-Length"));
/*     */         }
/*     */ 
/* 274 */         log.debug("\n" + Messages.getMessage("xmlRecd00"));
/* 275 */         log.debug("-----------------------------------------------");
/* 276 */         log.debug(outMsg.getSOAPPartAsString());
/*     */       }
/*     */ 
/* 281 */       if (msgContext.getMaintainSession()) {
/* 282 */         Header[] headers = method.getResponseHeaders();
/*     */ 
/* 284 */         for (int i = 0; i < headers.length; i++) {
/* 285 */           if (headers[i].getName().equalsIgnoreCase("Set-Cookie"))
/* 286 */             handleCookie("Cookie", headers[i].getValue(), msgContext);
/* 287 */           else if (headers[i].getName().equalsIgnoreCase("Set-Cookie2")) {
/* 288 */             handleCookie("Cookie2", headers[i].getValue(), msgContext);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 295 */       if (msgContext.isPropertyTrue("axis.one.way"))
/* 296 */         method.releaseConnection();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 300 */       log.debug(e);
/* 301 */       throw AxisFault.makeFault(e);
/*     */     }
/*     */ 
/* 304 */     if (log.isDebugEnabled())
/* 305 */       log.debug(Messages.getMessage("exit00", "CommonsHTTPSender::invoke"));
/*     */   }
/*     */ 
/*     */   public void handleCookie(String cookieName, String cookie, MessageContext msgContext)
/*     */   {
/* 322 */     cookie = cleanupCookie(cookie);
/* 323 */     int keyIndex = cookie.indexOf("=");
/* 324 */     String key = keyIndex != -1 ? cookie.substring(0, keyIndex) : cookie;
/*     */ 
/* 326 */     ArrayList cookies = new ArrayList();
/* 327 */     Object oldCookies = msgContext.getProperty(cookieName);
/* 328 */     boolean alreadyExist = false;
/* 329 */     if (oldCookies != null) {
/* 330 */       if ((oldCookies instanceof String[])) {
/* 331 */         String[] oldCookiesArray = (String[])oldCookies;
/* 332 */         for (int i = 0; i < oldCookiesArray.length; i++) {
/* 333 */           String anOldCookie = oldCookiesArray[i];
/* 334 */           if ((key != null) && (anOldCookie.indexOf(key) == 0)) {
/* 335 */             anOldCookie = cookie;
/* 336 */             alreadyExist = true;
/*     */           }
/* 338 */           cookies.add(anOldCookie);
/*     */         }
/*     */       } else {
/* 341 */         String oldCookie = (String)oldCookies;
/* 342 */         if ((key != null) && (oldCookie.indexOf(key) == 0)) {
/* 343 */           oldCookie = cookie;
/* 344 */           alreadyExist = true;
/*     */         }
/* 346 */         cookies.add(oldCookie);
/*     */       }
/*     */     }
/*     */ 
/* 350 */     if (!alreadyExist) {
/* 351 */       cookies.add(cookie);
/*     */     }
/*     */ 
/* 354 */     if (cookies.size() == 1)
/* 355 */       msgContext.setProperty(cookieName, cookies.get(0));
/* 356 */     else if (cookies.size() > 1)
/* 357 */       msgContext.setProperty(cookieName, cookies.toArray(new String[cookies.size()]));
/*     */   }
/*     */ 
/*     */   private void fillHeaders(MessageContext msgContext, HttpState state, String header, String host, String path, boolean secure)
/*     */   {
/* 372 */     Object ck1 = msgContext.getProperty(header);
/* 373 */     if (ck1 != null)
/* 374 */       if ((ck1 instanceof String[])) {
/* 375 */         String[] cookies = (String[])ck1;
/* 376 */         for (int i = 0; i < cookies.length; i++)
/* 377 */           addCookie(state, cookies[i], host, path, secure);
/*     */       }
/*     */       else {
/* 380 */         addCookie(state, (String)ck1, host, path, secure);
/*     */       }
/*     */   }
/*     */ 
/*     */   private void addCookie(HttpState state, String cookie, String host, String path, boolean secure)
/*     */   {
/* 391 */     int index = cookie.indexOf('=');
/* 392 */     state.addCookie(new Cookie(host, cookie.substring(0, index), cookie.substring(index + 1), path, null, secure));
/*     */   }
/*     */ 
/*     */   private String cleanupCookie(String cookie)
/*     */   {
/* 405 */     cookie = cookie.trim();
/*     */ 
/* 407 */     int index = cookie.indexOf(';');
/* 408 */     if (index != -1) {
/* 409 */       cookie = cookie.substring(0, index);
/*     */     }
/* 411 */     return cookie;
/*     */   }
/*     */ 
/*     */   protected HostConfiguration getHostConfiguration(HttpClient client, MessageContext context, URL targetURL)
/*     */   {
/* 417 */     TransportClientProperties tcp = TransportClientPropertiesFactory.create(targetURL.getProtocol());
/*     */ 
/* 419 */     int port = targetURL.getPort();
/* 420 */     boolean hostInNonProxyList = isHostInNonProxyList(targetURL.getHost(), tcp.getNonProxyHosts());
/*     */ 
/* 423 */     HostConfiguration config = new HostConfiguration();
/*     */ 
/* 425 */     if (port == -1) {
/* 426 */       if (targetURL.getProtocol().equalsIgnoreCase("https"))
/* 427 */         port = 443;
/*     */       else {
/* 429 */         port = 80;
/*     */       }
/*     */     }
/*     */ 
/* 433 */     if (hostInNonProxyList) {
/* 434 */       config.setHost(targetURL.getHost(), port, targetURL.getProtocol());
/*     */     }
/* 436 */     else if ((tcp.getProxyHost().length() == 0) || (tcp.getProxyPort().length() == 0))
/*     */     {
/* 438 */       config.setHost(targetURL.getHost(), port, targetURL.getProtocol());
/*     */     } else {
/* 440 */       if (tcp.getProxyUser().length() != 0) {
/* 441 */         Credentials proxyCred = new UsernamePasswordCredentials(tcp.getProxyUser(), tcp.getProxyPassword());
/*     */ 
/* 446 */         int domainIndex = tcp.getProxyUser().indexOf("\\");
/* 447 */         if (domainIndex > 0) {
/* 448 */           String domain = tcp.getProxyUser().substring(0, domainIndex);
/* 449 */           if (tcp.getProxyUser().length() > domainIndex + 1) {
/* 450 */             String user = tcp.getProxyUser().substring(domainIndex + 1);
/* 451 */             proxyCred = new NTCredentials(user, tcp.getProxyPassword(), tcp.getProxyHost(), domain);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 456 */         client.getState().setProxyCredentials(AuthScope.ANY, proxyCred);
/*     */       }
/* 458 */       int proxyPort = new Integer(tcp.getProxyPort()).intValue();
/* 459 */       config.setProxy(tcp.getProxyHost(), proxyPort);
/*     */     }
/*     */ 
/* 462 */     return config;
/*     */   }
/*     */ 
/*     */   private void addContextInfo(HttpMethodBase method, HttpClient httpClient, MessageContext msgContext, URL tmpURL)
/*     */     throws Exception
/*     */   {
/* 482 */     if (msgContext.getTimeout() != 0)
/*     */     {
/* 486 */       httpClient.getHttpConnectionManager().getParams().setSoTimeout(msgContext.getTimeout());
/*     */ 
/* 488 */       httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(msgContext.getTimeout());
/*     */     }
/*     */ 
/* 492 */     String action = msgContext.useSOAPAction() ? msgContext.getSOAPActionURI() : "";
/*     */ 
/* 496 */     if (action == null) {
/* 497 */       action = "";
/*     */     }
/*     */ 
/* 500 */     Message msg = msgContext.getRequestMessage();
/* 501 */     if (msg != null) {
/* 502 */       method.setRequestHeader(new Header("Content-Type", msg.getContentType(msgContext.getSOAPConstants())));
/*     */     }
/*     */ 
/* 505 */     method.setRequestHeader(new Header("SOAPAction", "\"" + action + "\""));
/*     */ 
/* 507 */     method.setRequestHeader(new Header("User-Agent", Messages.getMessage("axisUserAgent")));
/* 508 */     String userID = msgContext.getUsername();
/* 509 */     String passwd = msgContext.getPassword();
/*     */ 
/* 513 */     if ((userID == null) && (tmpURL.getUserInfo() != null)) {
/* 514 */       String info = tmpURL.getUserInfo();
/* 515 */       int sep = info.indexOf(':');
/*     */ 
/* 517 */       if ((sep >= 0) && (sep + 1 < info.length())) {
/* 518 */         userID = info.substring(0, sep);
/* 519 */         passwd = info.substring(sep + 1);
/*     */       } else {
/* 521 */         userID = info;
/*     */       }
/*     */     }
/* 524 */     if (userID != null) {
/* 525 */       Credentials proxyCred = new UsernamePasswordCredentials(userID, passwd);
/*     */ 
/* 530 */       int domainIndex = userID.indexOf("\\");
/* 531 */       if (domainIndex > 0) {
/* 532 */         String domain = userID.substring(0, domainIndex);
/* 533 */         if (userID.length() > domainIndex + 1) {
/* 534 */           String user = userID.substring(domainIndex + 1);
/* 535 */           proxyCred = new NTCredentials(user, passwd, NetworkUtils.getLocalHostname(), domain);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 540 */       httpClient.getState().setCredentials(AuthScope.ANY, proxyCred);
/*     */     }
/*     */ 
/* 544 */     if (msgContext.isPropertyTrue("transport.http.acceptGzip")) {
/* 545 */       method.addRequestHeader("Accept-Encoding", "gzip");
/*     */     }
/*     */ 
/* 548 */     if (msgContext.isPropertyTrue("transport.http.gzipRequest")) {
/* 549 */       method.addRequestHeader("Content-Encoding", "gzip");
/*     */     }
/*     */ 
/* 554 */     MimeHeaders mimeHeaders = msg.getMimeHeaders();
/*     */     Iterator i;
/* 555 */     if (mimeHeaders != null) {
/* 556 */       for (i = mimeHeaders.getAllHeaders(); i.hasNext(); ) {
/* 557 */         MimeHeader mimeHeader = (MimeHeader)i.next();
/*     */ 
/* 560 */         String headerName = mimeHeader.getName();
/* 561 */         if ((headerName.equals("Content-Type")) || (headerName.equals("SOAPAction")))
/*     */         {
/*     */           continue;
/*     */         }
/* 565 */         method.addRequestHeader(mimeHeader.getName(), mimeHeader.getValue());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 571 */     Hashtable userHeaderTable = (Hashtable)msgContext.getProperty("HTTP-Request-Headers");
/*     */ 
/* 574 */     if (userHeaderTable != null) {
/* 575 */       Iterator e = userHeaderTable.entrySet().iterator();
/* 576 */       while (e.hasNext()) {
/* 577 */         Map.Entry me = (Map.Entry)e.next();
/* 578 */         Object keyObj = me.getKey();
/*     */ 
/* 580 */         if (null == keyObj) {
/*     */           continue;
/*     */         }
/* 583 */         String key = keyObj.toString().trim();
/* 584 */         String value = me.getValue().toString().trim();
/*     */ 
/* 586 */         if ((key.equalsIgnoreCase("Expect")) && (value.equalsIgnoreCase("100-continue")))
/*     */         {
/* 588 */           method.getParams().setBooleanParameter("http.protocol.expect-continue", true);
/*     */         }
/* 590 */         else if (key.equalsIgnoreCase(HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED)) {
/* 591 */           String val = me.getValue().toString();
/* 592 */           if (null != val)
/* 593 */             this.httpChunkStream = JavaUtils.isTrue(val);
/*     */         }
/*     */         else {
/* 596 */           method.addRequestHeader(key, value);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean isHostInNonProxyList(String host, String nonProxyHosts)
/*     */   {
/* 612 */     if ((nonProxyHosts == null) || (host == null)) {
/* 613 */       return false;
/*     */     }
/*     */ 
/* 620 */     StringTokenizer tokenizer = new StringTokenizer(nonProxyHosts, "|\"");
/*     */ 
/* 622 */     while (tokenizer.hasMoreTokens()) {
/* 623 */       String pattern = tokenizer.nextToken();
/*     */ 
/* 625 */       if (log.isDebugEnabled()) {
/* 626 */         log.debug(Messages.getMessage("match00", new String[] { "HTTPSender", host, pattern }));
/*     */       }
/*     */ 
/* 631 */       if (match(pattern, host, false)) {
/* 632 */         return true;
/*     */       }
/*     */     }
/* 635 */     return false;
/*     */   }
/*     */ 
/*     */   protected static boolean match(String pattern, String str, boolean isCaseSensitive)
/*     */   {
/* 654 */     char[] patArr = pattern.toCharArray();
/* 655 */     char[] strArr = str.toCharArray();
/* 656 */     int patIdxStart = 0;
/* 657 */     int patIdxEnd = patArr.length - 1;
/* 658 */     int strIdxStart = 0;
/* 659 */     int strIdxEnd = strArr.length - 1;
/*     */ 
/* 661 */     boolean containsStar = false;
/*     */ 
/* 663 */     for (int i = 0; i < patArr.length; i++) {
/* 664 */       if (patArr[i] == '*') {
/* 665 */         containsStar = true;
/* 666 */         break;
/*     */       }
/*     */     }
/* 669 */     if (!containsStar)
/*     */     {
/* 672 */       if (patIdxEnd != strIdxEnd) {
/* 673 */         return false;
/*     */       }
/* 675 */       for (int i = 0; i <= patIdxEnd; i++) {
/* 676 */         char ch = patArr[i];
/* 677 */         if ((isCaseSensitive) && (ch != strArr[i])) {
/* 678 */           return false;
/*     */         }
/* 680 */         if ((!isCaseSensitive) && (Character.toUpperCase(ch) != Character.toUpperCase(strArr[i])))
/*     */         {
/* 683 */           return false;
/*     */         }
/*     */       }
/* 686 */       return true;
/*     */     }
/* 688 */     if (patIdxEnd == 0)
/* 689 */       return true;
/*     */     char ch;
/* 694 */     while (((ch = patArr[patIdxStart]) != '*') && (strIdxStart <= strIdxEnd)) {
/* 695 */       if ((isCaseSensitive) && (ch != strArr[strIdxStart])) {
/* 696 */         return false;
/*     */       }
/* 698 */       if ((!isCaseSensitive) && (Character.toUpperCase(ch) != Character.toUpperCase(strArr[strIdxStart])))
/*     */       {
/* 701 */         return false;
/*     */       }
/* 703 */       patIdxStart++;
/* 704 */       strIdxStart++;
/*     */     }
/* 706 */     if (strIdxStart > strIdxEnd)
/*     */     {
/* 710 */       for (int i = patIdxStart; i <= patIdxEnd; i++) {
/* 711 */         if (patArr[i] != '*') {
/* 712 */           return false;
/*     */         }
/*     */       }
/* 715 */       return true;
/*     */     }
/*     */ 
/* 719 */     while (((ch = patArr[patIdxEnd]) != '*') && (strIdxStart <= strIdxEnd)) {
/* 720 */       if ((isCaseSensitive) && (ch != strArr[strIdxEnd])) {
/* 721 */         return false;
/*     */       }
/* 723 */       if ((!isCaseSensitive) && (Character.toUpperCase(ch) != Character.toUpperCase(strArr[strIdxEnd])))
/*     */       {
/* 726 */         return false;
/*     */       }
/* 728 */       patIdxEnd--;
/* 729 */       strIdxEnd--;
/*     */     }
/* 731 */     if (strIdxStart > strIdxEnd)
/*     */     {
/* 735 */       for (int i = patIdxStart; i <= patIdxEnd; i++) {
/* 736 */         if (patArr[i] != '*') {
/* 737 */           return false;
/*     */         }
/*     */       }
/* 740 */       return true;
/*     */     }
/*     */ 
/* 745 */     while ((patIdxStart != patIdxEnd) && (strIdxStart <= strIdxEnd)) {
/* 746 */       int patIdxTmp = -1;
/*     */ 
/* 748 */       for (int i = patIdxStart + 1; i <= patIdxEnd; i++) {
/* 749 */         if (patArr[i] == '*') {
/* 750 */           patIdxTmp = i;
/* 751 */           break;
/*     */         }
/*     */       }
/* 754 */       if (patIdxTmp == patIdxStart + 1)
/*     */       {
/* 757 */         patIdxStart++;
/* 758 */         continue;
/*     */       }
/*     */ 
/* 763 */       int patLength = patIdxTmp - patIdxStart - 1;
/* 764 */       int strLength = strIdxEnd - strIdxStart + 1;
/* 765 */       int foundIdx = -1;
/*     */ 
/* 768 */       for (int i = 0; i <= strLength - patLength; i++) {
/* 769 */         int j = 0;
/*     */         while (true) if (j < patLength) {
/* 770 */             ch = patArr[(patIdxStart + j + 1)];
/* 771 */             if ((isCaseSensitive) && (ch != strArr[(strIdxStart + i + j)]))
/*     */             {
/*     */               break;
/*     */             }
/* 775 */             if ((!isCaseSensitive) && (Character.toUpperCase(ch) != Character.toUpperCase(strArr[(strIdxStart + i + j)])))
/*     */               break;
/* 769 */             j++; continue;
/*     */           }
/*     */           else
/*     */           {
/* 781 */             foundIdx = strIdxStart + i;
/* 782 */             break label549;
/*     */           } 
/*     */       }
/* 784 */       label549: if (foundIdx == -1) {
/* 785 */         return false;
/*     */       }
/* 787 */       patIdxStart = patIdxTmp;
/* 788 */       strIdxStart = foundIdx + patLength;
/*     */     }
/*     */ 
/* 793 */     for (int i = patIdxStart; i <= patIdxEnd; i++) {
/* 794 */       if (patArr[i] != '*') {
/* 795 */         return false;
/*     */       }
/*     */     }
/* 798 */     return true;
/*     */   }
/*     */ 
/*     */   private static String getHeader(HttpMethodBase method, String headerName) {
/* 802 */     Header header = method.getResponseHeader(headerName);
/* 803 */     return header == null ? null : header.getValue().trim();
/*     */   }
/*     */ 
/*     */   private InputStream createConnectionReleasingInputStream(HttpMethodBase method) throws IOException {
/* 807 */     return new FilterInputStream(method.getResponseBodyAsStream(), method) { private final HttpMethodBase val$method;
/*     */ 
/*     */       public void close() throws IOException { try { super.close();
/*     */         } finally {
/* 812 */           this.val$method.releaseConnection();
/*     */         }
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private static class GzipMessageRequestEntity extends CommonsHTTPSender.MessageRequestEntity
/*     */   {
/*     */     private ByteArrayOutputStream cachedStream;
/*     */ 
/*     */     public GzipMessageRequestEntity(HttpMethodBase method, Message message)
/*     */     {
/* 870 */       super(message);
/*     */     }
/*     */ 
/*     */     public GzipMessageRequestEntity(HttpMethodBase method, Message message, boolean httpChunkStream) {
/* 874 */       super(message, httpChunkStream);
/*     */     }
/*     */ 
/*     */     public void writeRequest(OutputStream out) throws IOException {
/* 878 */       if (this.cachedStream != null) {
/* 879 */         this.cachedStream.writeTo(out);
/*     */       } else {
/* 881 */         GZIPOutputStream gzStream = new GZIPOutputStream(out);
/* 882 */         super.writeRequest(gzStream);
/* 883 */         gzStream.finish();
/*     */       }
/*     */     }
/*     */ 
/*     */     public long getContentLength() {
/* 888 */       if (isContentLengthNeeded()) {
/* 889 */         ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*     */         try {
/* 891 */           writeRequest(baos);
/* 892 */           this.cachedStream = baos;
/* 893 */           return baos.size();
/*     */         }
/*     */         catch (IOException e) {
/*     */         }
/*     */       }
/* 898 */       return -1L;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class MessageRequestEntity
/*     */     implements RequestEntity
/*     */   {
/*     */     private HttpMethodBase method;
/*     */     private Message message;
/* 822 */     boolean httpChunkStream = true;
/*     */ 
/*     */     public MessageRequestEntity(HttpMethodBase method, Message message) {
/* 825 */       this.message = message;
/* 826 */       this.method = method;
/*     */     }
/*     */ 
/*     */     public MessageRequestEntity(HttpMethodBase method, Message message, boolean httpChunkStream) {
/* 830 */       this.message = message;
/* 831 */       this.method = method;
/* 832 */       this.httpChunkStream = httpChunkStream;
/*     */     }
/*     */ 
/*     */     public boolean isRepeatable() {
/* 836 */       return true;
/*     */     }
/*     */ 
/*     */     public void writeRequest(OutputStream out) throws IOException {
/*     */       try {
/* 841 */         this.message.writeTo(out);
/*     */       } catch (SOAPException e) {
/* 843 */         throw new IOException(e.getMessage());
/*     */       }
/*     */     }
/*     */ 
/*     */     protected boolean isContentLengthNeeded() {
/* 848 */       return (this.method.getParams().getVersion() == HttpVersion.HTTP_1_0) || (!this.httpChunkStream);
/*     */     }
/*     */ 
/*     */     public long getContentLength() {
/* 852 */       if (isContentLengthNeeded())
/*     */         try {
/* 854 */           return this.message.getContentLength();
/*     */         }
/*     */         catch (Exception e) {
/*     */         }
/* 858 */       return -1L;
/*     */     }
/*     */ 
/*     */     public String getContentType() {
/* 862 */       return null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.http.CommonsHTTPSender
 * JD-Core Version:    0.6.0
 */