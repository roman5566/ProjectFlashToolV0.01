/*     */ package org.apache.axis.transport.http;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.Socket;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.xml.soap.MimeHeader;
/*     */ import javax.xml.soap.MimeHeaders;
/*     */ import javax.xml.soap.SOAPException;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.Message;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.components.net.BooleanHolder;
/*     */ import org.apache.axis.components.net.DefaultSocketFactory;
/*     */ import org.apache.axis.components.net.SocketFactory;
/*     */ import org.apache.axis.components.net.SocketFactoryFactory;
/*     */ import org.apache.axis.encoding.Base64;
/*     */ import org.apache.axis.handlers.BasicHandler;
/*     */ import org.apache.axis.message.SOAPEnvelope;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.TeeOutputStream;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class HTTPSender extends BasicHandler
/*     */ {
/*  60 */   protected static Log log = LogFactory.getLog(HTTPSender.class.getName());
/*     */ 
/*  62 */   private static final String ACCEPT_HEADERS = "Accept: application/soap+xml, application/dime, multipart/related, text/*\r\nUser-Agent: " + Messages.getMessage("axisUserAgent") + "\r\n";
/*     */   private static final String CACHE_HEADERS = "Cache-Control: no-cache\r\nPragma: no-cache\r\n";
/*  88 */   private static final String CHUNKED_HEADER = HTTPConstants.HEADER_TRANSFER_ENCODING + ": " + HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED + "\r\n";
/*     */ 
/*  94 */   private static final String HEADER_CONTENT_TYPE_LC = "Content-Type".toLowerCase();
/*     */ 
/*  97 */   private static final String HEADER_LOCATION_LC = "Location".toLowerCase();
/*     */ 
/* 100 */   private static final String HEADER_CONTENT_LOCATION_LC = "Content-Location".toLowerCase();
/*     */ 
/* 103 */   private static final String HEADER_CONTENT_LENGTH_LC = "Content-Length".toLowerCase();
/*     */ 
/* 106 */   private static final String HEADER_TRANSFER_ENCODING_LC = HTTPConstants.HEADER_TRANSFER_ENCODING.toLowerCase();
/*     */   URL targetURL;
/*     */ 
/*     */   public void invoke(MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/* 124 */     if (log.isDebugEnabled()) {
/* 125 */       log.debug(Messages.getMessage("enter00", "HTTPSender::invoke"));
/*     */     }
/*     */ 
/* 128 */     SocketHolder socketHolder = new SocketHolder(null);
/*     */     try
/*     */     {
/* 131 */       BooleanHolder useFullURL = new BooleanHolder(false);
/* 132 */       StringBuffer otherHeaders = new StringBuffer();
/* 133 */       this.targetURL = new URL(msgContext.getStrProp("transport.url"));
/* 134 */       String host = this.targetURL.getHost();
/* 135 */       int port = this.targetURL.getPort();
/*     */ 
/* 138 */       InputStream inp = writeToSocket(socketHolder, msgContext, this.targetURL, otherHeaders, host, port, msgContext.getTimeout(), useFullURL);
/*     */ 
/* 142 */       Hashtable headers = new Hashtable();
/* 143 */       inp = readHeadersFromSocket(socketHolder, msgContext, inp, headers);
/* 144 */       readFromSocket(socketHolder, msgContext, inp, headers);
/*     */     } catch (Exception e) {
/* 146 */       log.debug(e);
/*     */       try {
/* 148 */         if (socketHolder.getSocket() != null)
/* 149 */           socketHolder.getSocket().close();
/*     */       }
/*     */       catch (IOException ie)
/*     */       {
/*     */       }
/* 154 */       throw AxisFault.makeFault(e);
/*     */     }
/* 156 */     if (log.isDebugEnabled())
/* 157 */       log.debug(Messages.getMessage("exit00", "HTTPDispatchHandler::invoke"));
/*     */   }
/*     */ 
/*     */   protected void getSocket(SocketHolder sockHolder, MessageContext msgContext, String protocol, String host, int port, int timeout, StringBuffer otherHeaders, BooleanHolder useFullURL)
/*     */     throws Exception
/*     */   {
/* 180 */     Hashtable options = getOptions();
/* 181 */     if (timeout > 0) {
/* 182 */       if (options == null) {
/* 183 */         options = new Hashtable();
/*     */       }
/* 185 */       options.put(DefaultSocketFactory.CONNECT_TIMEOUT, Integer.toString(timeout));
/*     */     }
/* 187 */     SocketFactory factory = SocketFactoryFactory.getFactory(protocol, options);
/* 188 */     if (factory == null) {
/* 189 */       throw new IOException(Messages.getMessage("noSocketFactory", protocol));
/*     */     }
/* 191 */     Socket sock = factory.create(host, port, otherHeaders, useFullURL);
/* 192 */     if (timeout > 0) {
/* 193 */       sock.setSoTimeout(timeout);
/*     */     }
/* 195 */     sockHolder.setSocket(sock);
/*     */   }
/*     */ 
/*     */   private InputStream writeToSocket(SocketHolder sockHolder, MessageContext msgContext, URL tmpURL, StringBuffer otherHeaders, String host, int port, int timeout, BooleanHolder useFullURL)
/*     */     throws Exception
/*     */   {
/* 216 */     String userID = msgContext.getUsername();
/* 217 */     String passwd = msgContext.getPassword();
/*     */ 
/* 220 */     String action = msgContext.useSOAPAction() ? msgContext.getSOAPActionURI() : "";
/*     */ 
/* 224 */     if (action == null) {
/* 225 */       action = "";
/*     */     }
/*     */ 
/* 230 */     if ((userID == null) && (tmpURL.getUserInfo() != null)) {
/* 231 */       String info = tmpURL.getUserInfo();
/* 232 */       int sep = info.indexOf(':');
/*     */ 
/* 234 */       if ((sep >= 0) && (sep + 1 < info.length())) {
/* 235 */         userID = info.substring(0, sep);
/* 236 */         passwd = info.substring(sep + 1);
/*     */       } else {
/* 238 */         userID = info;
/*     */       }
/*     */     }
/* 241 */     if (userID != null) {
/* 242 */       StringBuffer tmpBuf = new StringBuffer();
/*     */ 
/* 244 */       tmpBuf.append(userID).append(":").append(passwd == null ? "" : passwd);
/*     */ 
/* 247 */       otherHeaders.append("Authorization").append(": Basic ").append(Base64.encode(tmpBuf.toString().getBytes())).append("\r\n");
/*     */     }
/*     */ 
/* 255 */     if (msgContext.getMaintainSession()) {
/* 256 */       fillHeaders(msgContext, "Cookie", otherHeaders);
/* 257 */       fillHeaders(msgContext, "Cookie2", otherHeaders);
/*     */     }
/*     */ 
/* 260 */     StringBuffer header2 = new StringBuffer();
/*     */ 
/* 262 */     String webMethod = null;
/* 263 */     boolean posting = true;
/*     */ 
/* 265 */     Message reqMessage = msgContext.getRequestMessage();
/*     */ 
/* 267 */     boolean http10 = true;
/* 268 */     boolean httpChunkStream = false;
/* 269 */     boolean httpContinueExpected = false;
/*     */ 
/* 271 */     String httpConnection = null;
/*     */ 
/* 273 */     String httpver = msgContext.getStrProp("axis.transport.version");
/* 274 */     if (null == httpver) {
/* 275 */       httpver = HTTPConstants.HEADER_PROTOCOL_V10;
/*     */     }
/* 277 */     httpver = httpver.trim();
/* 278 */     if (httpver.equals(HTTPConstants.HEADER_PROTOCOL_V11)) {
/* 279 */       http10 = false;
/*     */     }
/*     */ 
/* 283 */     Hashtable userHeaderTable = (Hashtable)msgContext.getProperty("HTTP-Request-Headers");
/*     */ 
/* 286 */     if (userHeaderTable != null) {
/* 287 */       if (null == otherHeaders) {
/* 288 */         otherHeaders = new StringBuffer(1024);
/*     */       }
/*     */ 
/* 291 */       Iterator e = userHeaderTable.entrySet().iterator();
/* 292 */       while (e.hasNext())
/*     */       {
/* 294 */         Map.Entry me = (Map.Entry)e.next();
/* 295 */         Object keyObj = me.getKey();
/* 296 */         if (null != keyObj) {
/* 297 */           String key = keyObj.toString().trim();
/*     */ 
/* 299 */           if (key.equalsIgnoreCase(HTTPConstants.HEADER_TRANSFER_ENCODING)) {
/* 300 */             if (!http10) {
/* 301 */               String val = me.getValue().toString();
/* 302 */               if ((null != val) && (val.trim().equalsIgnoreCase(HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED)))
/* 303 */                 httpChunkStream = true;
/*     */             }
/* 305 */           } else if (key.equalsIgnoreCase("Connection")) {
/* 306 */             if (!http10) {
/* 307 */               String val = me.getValue().toString();
/* 308 */               if (val.trim().equalsIgnoreCase(HTTPConstants.HEADER_CONNECTION_CLOSE))
/* 309 */                 httpConnection = HTTPConstants.HEADER_CONNECTION_CLOSE;
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/* 314 */             if ((!http10) && (key.equalsIgnoreCase("Expect"))) {
/* 315 */               String val = me.getValue().toString();
/* 316 */               if ((null != val) && (val.trim().equalsIgnoreCase("100-continue"))) {
/* 317 */                 httpContinueExpected = true;
/*     */               }
/*     */             }
/* 320 */             otherHeaders.append(key).append(": ").append(me.getValue()).append("\r\n");
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 325 */     if (!http10)
/*     */     {
/* 328 */       httpConnection = HTTPConstants.HEADER_CONNECTION_CLOSE;
/*     */     }
/*     */ 
/* 331 */     header2.append(" ");
/* 332 */     header2.append(http10 ? "HTTP/1.0" : "HTTP/1.1").append("\r\n");
/*     */ 
/* 335 */     MimeHeaders mimeHeaders = reqMessage.getMimeHeaders();
/*     */ 
/* 337 */     if (posting)
/*     */     {
/* 339 */       String[] header = mimeHeaders.getHeader("Content-Type");
/*     */       String contentType;
/*     */       String contentType;
/* 340 */       if ((header != null) && (header.length > 0))
/* 341 */         contentType = mimeHeaders.getHeader("Content-Type")[0];
/*     */       else {
/* 343 */         contentType = reqMessage.getContentType(msgContext.getSOAPConstants());
/*     */       }
/*     */ 
/* 347 */       if ((contentType == null) || (contentType.equals(""))) {
/* 348 */         throw new Exception(Messages.getMessage("missingContentType"));
/*     */       }
/* 350 */       header2.append("Content-Type").append(": ").append(contentType).append("\r\n");
/*     */     }
/*     */ 
/* 356 */     header2.append(ACCEPT_HEADERS).append("Host").append(": ").append(host).append(":" + port).append("\r\n").append("Cache-Control: no-cache\r\nPragma: no-cache\r\n").append("SOAPAction").append(": \"").append(action).append("\"\r\n");
/*     */ 
/* 368 */     if (posting)
/* 369 */       if (!httpChunkStream)
/*     */       {
/* 371 */         header2.append("Content-Length").append(": ").append(reqMessage.getContentLength()).append("\r\n");
/*     */       }
/*     */       else
/*     */       {
/* 377 */         header2.append(CHUNKED_HEADER);
/*     */       }
/*     */     Iterator i;
/* 382 */     if (mimeHeaders != null) {
/* 383 */       for (i = mimeHeaders.getAllHeaders(); i.hasNext(); ) {
/* 384 */         MimeHeader mimeHeader = (MimeHeader)i.next();
/* 385 */         String headerName = mimeHeader.getName();
/* 386 */         if ((headerName.equals("Content-Type")) || (headerName.equals("SOAPAction")))
/*     */         {
/*     */           continue;
/*     */         }
/* 390 */         header2.append(mimeHeader.getName()).append(": ").append(mimeHeader.getValue()).append("\r\n");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 397 */     if (null != httpConnection) {
/* 398 */       header2.append("Connection");
/* 399 */       header2.append(": ");
/* 400 */       header2.append(httpConnection);
/* 401 */       header2.append("\r\n");
/*     */     }
/*     */ 
/* 404 */     getSocket(sockHolder, msgContext, this.targetURL.getProtocol(), host, port, timeout, otherHeaders, useFullURL);
/*     */ 
/* 407 */     if (null != otherHeaders)
/*     */     {
/* 411 */       header2.append(otherHeaders.toString());
/*     */     }
/*     */ 
/* 414 */     header2.append("\r\n");
/*     */ 
/* 416 */     StringBuffer header = new StringBuffer();
/*     */ 
/* 420 */     if (msgContext.getSOAPConstants() == SOAPConstants.SOAP12_CONSTANTS) {
/* 421 */       webMethod = msgContext.getStrProp("soap12.webmethod");
/*     */     }
/* 423 */     if (webMethod == null)
/* 424 */       webMethod = "POST";
/*     */     else {
/* 426 */       posting = webMethod.equals("POST");
/*     */     }
/*     */ 
/* 429 */     header.append(webMethod).append(" ");
/* 430 */     if (useFullURL.value)
/* 431 */       header.append(tmpURL.toExternalForm());
/*     */     else {
/* 433 */       header.append((tmpURL.getFile() == null) || (tmpURL.getFile().equals("")) ? "/" : tmpURL.getFile());
/*     */     }
/*     */ 
/* 438 */     header.append(header2.toString());
/*     */ 
/* 440 */     OutputStream out = sockHolder.getSocket().getOutputStream();
/*     */ 
/* 442 */     if (!posting) {
/* 443 */       out.write(header.toString().getBytes("iso-8859-1"));
/*     */ 
/* 445 */       out.flush();
/* 446 */       return null;
/*     */     }
/*     */ 
/* 449 */     InputStream inp = null;
/*     */ 
/* 451 */     if ((httpChunkStream) || (httpContinueExpected)) {
/* 452 */       out.write(header.toString().getBytes("iso-8859-1"));
/*     */     }
/*     */ 
/* 456 */     if (httpContinueExpected)
/*     */     {
/* 458 */       out.flush();
/* 459 */       Hashtable cheaders = new Hashtable();
/* 460 */       inp = readHeadersFromSocket(sockHolder, msgContext, null, cheaders);
/* 461 */       int returnCode = -1;
/* 462 */       Integer Irc = (Integer)msgContext.getProperty(HTTPConstants.MC_HTTP_STATUS_CODE);
/* 463 */       if (null != Irc) {
/* 464 */         returnCode = Irc.intValue();
/*     */       }
/* 466 */       if (100 == returnCode)
/*     */       {
/* 468 */         msgContext.removeProperty(HTTPConstants.MC_HTTP_STATUS_CODE);
/* 469 */         msgContext.removeProperty(HTTPConstants.MC_HTTP_STATUS_MESSAGE);
/*     */       }
/*     */       else {
/* 472 */         String statusMessage = (String)msgContext.getProperty(HTTPConstants.MC_HTTP_STATUS_MESSAGE);
/*     */ 
/* 475 */         AxisFault fault = new AxisFault("HTTP", "(" + returnCode + ")" + statusMessage, null, null);
/*     */ 
/* 477 */         fault.setFaultDetailString(Messages.getMessage("return01", "" + returnCode, ""));
/*     */ 
/* 479 */         throw fault;
/*     */       }
/*     */     }
/* 482 */     ByteArrayOutputStream baos = null;
/* 483 */     if (log.isDebugEnabled()) {
/* 484 */       log.debug(Messages.getMessage("xmlSent00"));
/* 485 */       log.debug("---------------------------------------------------");
/* 486 */       baos = new ByteArrayOutputStream();
/*     */     }
/* 488 */     if (httpChunkStream) {
/* 489 */       ChunkedOutputStream chunkedOutputStream = new ChunkedOutputStream(out);
/* 490 */       out = new BufferedOutputStream(chunkedOutputStream, 8192);
/*     */       try {
/* 492 */         if (baos != null) {
/* 493 */           out = new TeeOutputStream(out, baos);
/*     */         }
/* 495 */         reqMessage.writeTo(out);
/*     */       } catch (SOAPException e) {
/* 497 */         log.error(Messages.getMessage("exception00"), e);
/*     */       }
/* 499 */       out.flush();
/* 500 */       chunkedOutputStream.eos();
/*     */     } else {
/* 502 */       out = new BufferedOutputStream(out, 8192);
/*     */       try {
/* 504 */         if (!httpContinueExpected) {
/* 505 */           out.write(header.toString().getBytes("iso-8859-1"));
/*     */         }
/*     */ 
/* 508 */         if (baos != null) {
/* 509 */           out = new TeeOutputStream(out, baos);
/*     */         }
/* 511 */         reqMessage.writeTo(out);
/*     */       } catch (SOAPException e) {
/* 513 */         log.error(Messages.getMessage("exception00"), e);
/*     */       }
/*     */ 
/* 516 */       out.flush();
/*     */     }
/* 518 */     if (log.isDebugEnabled()) {
/* 519 */       log.debug(header + new String(baos.toByteArray()));
/*     */     }
/*     */ 
/* 522 */     return inp;
/*     */   }
/*     */ 
/*     */   private void fillHeaders(MessageContext msgContext, String header, StringBuffer otherHeaders)
/*     */   {
/* 532 */     Object ck1 = msgContext.getProperty(header);
/* 533 */     if (ck1 != null)
/* 534 */       if ((ck1 instanceof String[])) {
/* 535 */         String[] cookies = (String[])ck1;
/* 536 */         for (int i = 0; i < cookies.length; i++)
/* 537 */           addCookie(otherHeaders, header, cookies[i]);
/*     */       }
/*     */       else {
/* 540 */         addCookie(otherHeaders, header, (String)ck1);
/*     */       }
/*     */   }
/*     */ 
/*     */   private void addCookie(StringBuffer otherHeaders, String header, String cookie)
/*     */   {
/* 552 */     otherHeaders.append(header).append(": ").append(cookie).append("\r\n");
/*     */   }
/*     */ 
/*     */   private InputStream readHeadersFromSocket(SocketHolder sockHolder, MessageContext msgContext, InputStream inp, Hashtable headers)
/*     */     throws IOException
/*     */   {
/* 561 */     byte b = 0;
/* 562 */     int len = 0;
/* 563 */     int colonIndex = -1;
/*     */ 
/* 565 */     int returnCode = 0;
/* 566 */     if (null == inp) {
/* 567 */       inp = new BufferedInputStream(sockHolder.getSocket().getInputStream());
/*     */     }
/*     */ 
/* 570 */     if (headers == null) {
/* 571 */       headers = new Hashtable();
/*     */     }
/*     */ 
/* 579 */     boolean readTooMuch = false;
/*     */ 
/* 581 */     ByteArrayOutputStream buf = new ByteArrayOutputStream(4097);
/*     */     while (true) { if (!readTooMuch) {
/* 583 */         b = (byte)inp.read();
/*     */       }
/* 585 */       if (b == -1) {
/*     */         break;
/*     */       }
/* 588 */       readTooMuch = false;
/* 589 */       if ((b != 13) && (b != 10)) {
/* 590 */         if ((b == 58) && (colonIndex == -1)) {
/* 591 */           colonIndex = len;
/*     */         }
/* 593 */         len++;
/* 594 */         buf.write(b); continue;
/* 595 */       }if (b == 13) {
/*     */         continue;
/*     */       }
/* 598 */       if (len == 0) {
/*     */         break;
/*     */       }
/* 601 */       b = (byte)inp.read();
/* 602 */       readTooMuch = true;
/*     */ 
/* 605 */       if ((b == 32) || (b == 9)) {
/*     */         continue;
/*     */       }
/* 608 */       buf.close();
/* 609 */       byte[] hdata = buf.toByteArray();
/* 610 */       buf.reset();
/*     */       String name;
/*     */       String value;
/* 611 */       if (colonIndex != -1) {
/* 612 */         String name = new String(hdata, 0, colonIndex, "iso-8859-1");
/*     */ 
/* 615 */         String value = new String(hdata, colonIndex + 1, len - 1 - colonIndex, "iso-8859-1");
/*     */ 
/* 618 */         colonIndex = -1;
/*     */       }
/*     */       else {
/* 621 */         name = new String(hdata, 0, len, "iso-8859-1");
/*     */ 
/* 624 */         value = "";
/*     */       }
/* 626 */       if (log.isDebugEnabled()) {
/* 627 */         log.debug(name + value);
/*     */       }
/* 629 */       if (msgContext.getProperty(HTTPConstants.MC_HTTP_STATUS_CODE) == null)
/*     */       {
/* 633 */         int start = name.indexOf(' ') + 1;
/* 634 */         String tmp = name.substring(start).trim();
/* 635 */         int end = tmp.indexOf(' ');
/*     */ 
/* 637 */         if (end != -1) {
/* 638 */           tmp = tmp.substring(0, end);
/*     */         }
/* 640 */         returnCode = Integer.parseInt(tmp);
/* 641 */         msgContext.setProperty(HTTPConstants.MC_HTTP_STATUS_CODE, new Integer(returnCode));
/*     */ 
/* 643 */         msgContext.setProperty(HTTPConstants.MC_HTTP_STATUS_MESSAGE, name.substring(start + end + 1));
/*     */       }
/* 648 */       else if (msgContext.getMaintainSession()) {
/* 649 */         String nameLowerCase = name.toLowerCase();
/* 650 */         if (nameLowerCase.equalsIgnoreCase("Set-Cookie"))
/* 651 */           handleCookie("Cookie", null, value, msgContext);
/* 652 */         else if (nameLowerCase.equalsIgnoreCase("Set-Cookie2"))
/* 653 */           handleCookie("Cookie2", null, value, msgContext);
/*     */         else
/* 655 */           headers.put(name.toLowerCase(), value);
/*     */       }
/*     */       else {
/* 658 */         headers.put(name.toLowerCase(), value);
/*     */       }
/*     */ 
/* 661 */       len = 0;
/*     */     }
/*     */ 
/* 665 */     return inp;
/*     */   }
/*     */ 
/*     */   private InputStream readFromSocket(SocketHolder socketHolder, MessageContext msgContext, InputStream inp, Hashtable headers)
/*     */     throws IOException
/*     */   {
/* 680 */     Message outMsg = null;
/*     */ 
/* 683 */     Integer rc = (Integer)msgContext.getProperty(HTTPConstants.MC_HTTP_STATUS_CODE);
/*     */ 
/* 685 */     int returnCode = 0;
/* 686 */     if (rc != null) {
/* 687 */       returnCode = rc.intValue();
/*     */     }
/*     */ 
/* 693 */     String contentType = (String)headers.get(HEADER_CONTENT_TYPE_LC);
/*     */ 
/* 695 */     contentType = null == contentType ? null : contentType.trim();
/*     */ 
/* 699 */     String location = (String)headers.get(HEADER_LOCATION_LC);
/*     */ 
/* 701 */     location = null == location ? null : location.trim();
/*     */ 
/* 705 */     if ((returnCode > 199) && (returnCode < 300)) {
/* 706 */       if (returnCode == 202) {
/* 707 */         return inp;
/*     */       }
/*     */     }
/* 710 */     else if (msgContext.getSOAPConstants() != SOAPConstants.SOAP12_CONSTANTS)
/*     */     {
/* 714 */       if ((contentType == null) || (contentType.startsWith("text/html")) || (returnCode <= 499) || (returnCode >= 600))
/*     */       {
/* 717 */         if ((location != null) && ((returnCode == 302) || (returnCode == 307)))
/*     */         {
/* 721 */           inp.close();
/* 722 */           socketHolder.getSocket().close();
/*     */ 
/* 724 */           msgContext.removeProperty(HTTPConstants.MC_HTTP_STATUS_CODE);
/* 725 */           msgContext.setProperty("transport.url", location);
/*     */ 
/* 727 */           invoke(msgContext);
/* 728 */           return inp;
/* 729 */         }if (returnCode == 100) {
/* 730 */           msgContext.removeProperty(HTTPConstants.MC_HTTP_STATUS_CODE);
/* 731 */           msgContext.removeProperty(HTTPConstants.MC_HTTP_STATUS_MESSAGE);
/* 732 */           readHeadersFromSocket(socketHolder, msgContext, inp, headers);
/* 733 */           return readFromSocket(socketHolder, msgContext, inp, headers);
/*     */         }
/*     */ 
/* 737 */         ByteArrayOutputStream buf = new ByteArrayOutputStream(4097);
/*     */         byte b;
/* 739 */         while (-1 != (b = (byte)inp.read())) {
/* 740 */           buf.write(b);
/*     */         }
/* 742 */         String statusMessage = msgContext.getStrProp(HTTPConstants.MC_HTTP_STATUS_MESSAGE);
/*     */ 
/* 744 */         AxisFault fault = new AxisFault("HTTP", "(" + returnCode + ")" + statusMessage, null, null);
/*     */ 
/* 747 */         fault.setFaultDetailString(Messages.getMessage("return01", "" + returnCode, buf.toString()));
/*     */ 
/* 749 */         fault.addFaultDetail(Constants.QNAME_FAULTDETAIL_HTTPERRORCODE, Integer.toString(returnCode));
/*     */ 
/* 751 */         throw fault;
/*     */       }
/*     */     }
/* 754 */     String contentLocation = (String)headers.get(HEADER_CONTENT_LOCATION_LC);
/*     */ 
/* 757 */     contentLocation = null == contentLocation ? null : contentLocation.trim();
/*     */ 
/* 761 */     String contentLength = (String)headers.get(HEADER_CONTENT_LENGTH_LC);
/*     */ 
/* 764 */     contentLength = null == contentLength ? null : contentLength.trim();
/*     */ 
/* 768 */     String transferEncoding = (String)headers.get(HEADER_TRANSFER_ENCODING_LC);
/*     */ 
/* 771 */     if (null != transferEncoding) {
/* 772 */       transferEncoding = transferEncoding.trim().toLowerCase();
/* 773 */       if (transferEncoding.equals(HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED))
/*     */       {
/* 775 */         inp = new ChunkedInputStream(inp);
/*     */       }
/*     */     }
/*     */ 
/* 779 */     outMsg = new Message(new SocketInputStream(inp, socketHolder.getSocket()), false, contentType, contentLocation);
/*     */ 
/* 782 */     MimeHeaders mimeHeaders = outMsg.getMimeHeaders();
/* 783 */     for (Enumeration e = headers.keys(); e.hasMoreElements(); ) {
/* 784 */       String key = (String)e.nextElement();
/* 785 */       mimeHeaders.addHeader(key, ((String)headers.get(key)).trim());
/*     */     }
/* 787 */     outMsg.setMessageType("response");
/* 788 */     msgContext.setResponseMessage(outMsg);
/* 789 */     if (log.isDebugEnabled()) {
/* 790 */       if (null == contentLength) {
/* 791 */         log.debug("\n" + Messages.getMessage("no00", "Content-Length"));
/*     */       }
/*     */ 
/* 794 */       log.debug("\n" + Messages.getMessage("xmlRecd00"));
/* 795 */       log.debug("-----------------------------------------------");
/* 796 */       log.debug(outMsg.getSOAPEnvelope().toString());
/*     */     }
/*     */ 
/* 799 */     return inp;
/*     */   }
/*     */ 
/*     */   public void handleCookie(String cookieName, String setCookieName, String cookie, MessageContext msgContext)
/*     */   {
/* 814 */     cookie = cleanupCookie(cookie);
/* 815 */     int keyIndex = cookie.indexOf("=");
/* 816 */     String key = keyIndex != -1 ? cookie.substring(0, keyIndex) : cookie;
/*     */ 
/* 818 */     ArrayList cookies = new ArrayList();
/* 819 */     Object oldCookies = msgContext.getProperty(cookieName);
/* 820 */     boolean alreadyExist = false;
/* 821 */     if (oldCookies != null) {
/* 822 */       if ((oldCookies instanceof String[])) {
/* 823 */         String[] oldCookiesArray = (String[])oldCookies;
/* 824 */         for (int i = 0; i < oldCookiesArray.length; i++) {
/* 825 */           String anOldCookie = oldCookiesArray[i];
/* 826 */           if ((key != null) && (anOldCookie.indexOf(key) == 0)) {
/* 827 */             anOldCookie = cookie;
/* 828 */             alreadyExist = true;
/*     */           }
/* 830 */           cookies.add(anOldCookie);
/*     */         }
/*     */       } else {
/* 833 */         String oldCookie = (String)oldCookies;
/* 834 */         if ((key != null) && (oldCookie.indexOf(key) == 0)) {
/* 835 */           oldCookie = cookie;
/* 836 */           alreadyExist = true;
/*     */         }
/* 838 */         cookies.add(oldCookie);
/*     */       }
/*     */     }
/*     */ 
/* 842 */     if (!alreadyExist) {
/* 843 */       cookies.add(cookie);
/*     */     }
/*     */ 
/* 846 */     if (cookies.size() == 1)
/* 847 */       msgContext.setProperty(cookieName, cookies.get(0));
/* 848 */     else if (cookies.size() > 1)
/* 849 */       msgContext.setProperty(cookieName, cookies.toArray(new String[cookies.size()]));
/*     */   }
/*     */ 
/*     */   private String cleanupCookie(String cookie)
/*     */   {
/* 861 */     cookie = cookie.trim();
/*     */ 
/* 863 */     int index = cookie.indexOf(';');
/* 864 */     if (index != -1) {
/* 865 */       cookie = cookie.substring(0, index);
/*     */     }
/* 867 */     return cookie;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.http.HTTPSender
 * JD-Core Version:    0.6.0
 */