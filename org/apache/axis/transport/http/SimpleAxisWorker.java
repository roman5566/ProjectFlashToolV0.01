/*     */ package org.apache.axis.transport.http;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.soap.MimeHeader;
/*     */ import javax.xml.soap.MimeHeaders;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.EngineConfiguration;
/*     */ import org.apache.axis.Message;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.components.threadpool.ThreadPool;
/*     */ import org.apache.axis.description.OperationDesc;
/*     */ import org.apache.axis.description.ServiceDesc;
/*     */ import org.apache.axis.encoding.Base64;
/*     */ import org.apache.axis.message.SOAPEnvelope;
/*     */ import org.apache.axis.message.SOAPFault;
/*     */ import org.apache.axis.server.AxisServer;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.NetworkUtils;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Document;
/*     */ 
/*     */ public class SimpleAxisWorker
/*     */   implements Runnable
/*     */ {
/*  49 */   protected static Log log = LogFactory.getLog(SimpleAxisWorker.class.getName());
/*     */   private SimpleAxisServer server;
/*     */   private Socket socket;
/*  56 */   private static String transportName = "SimpleHTTP";
/*     */ 
/*  59 */   private static byte[] OK = ("200 " + Messages.getMessage("ok00")).getBytes();
/*  60 */   private static byte[] NOCONTENT = ("202 " + Messages.getMessage("ok00") + "\n\n").getBytes();
/*  61 */   private static byte[] UNAUTH = ("401 " + Messages.getMessage("unauth00")).getBytes();
/*  62 */   private static byte[] SENDER = "400".getBytes();
/*  63 */   private static byte[] ISE = ("500 " + Messages.getMessage("internalError01")).getBytes();
/*     */ 
/*  66 */   private static byte[] HTTP = "HTTP/1.0 ".getBytes();
/*     */ 
/*  69 */   private static byte[] XML_MIME_STUFF = "\r\nContent-Type: text/xml; charset=utf-8\r\nContent-Length: ".getBytes();
/*     */ 
/*  74 */   private static byte[] HTML_MIME_STUFF = "\r\nContent-Type: text/html; charset=utf-8\r\nContent-Length: ".getBytes();
/*     */ 
/*  79 */   private static byte[] SEPARATOR = "\r\n\r\n".getBytes();
/*     */ 
/*  90 */   private static final byte[] toLower = new byte[256];
/*     */   private static final int BUFSIZ = 4096;
/*     */   private static final byte[] lenHeader;
/*     */   private static final int lenLen;
/*     */   private static final byte[] typeHeader;
/*     */   private static final int typeLen;
/*     */   private static final byte[] locationHeader;
/*     */   private static final int locationLen;
/*     */   private static final byte[] actionHeader;
/*     */   private static final int actionLen;
/*     */   private static final byte[] cookieHeader;
/*     */   private static final int cookieLen;
/*     */   private static final byte[] cookie2Header;
/*     */   private static final int cookie2Len;
/*     */   private static final byte[] authHeader;
/*     */   private static final int authLen;
/*     */   private static final byte[] getHeader;
/*     */   private static final byte[] postHeader;
/*     */   private static final byte[] headerEnder;
/*     */   private static final byte[] basicAuth;
/*     */ 
/*     */   public SimpleAxisWorker(SimpleAxisServer server, Socket socket)
/*     */   {
/* 146 */     this.server = server;
/* 147 */     this.socket = socket;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     try
/*     */     {
/* 155 */       execute();
/*     */     } finally {
/* 157 */       SimpleAxisServer.getPool().workerDone(this, false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void execute()
/*     */   {
/* 165 */     byte[] buf = new byte[4096];
/*     */ 
/* 167 */     AxisServer engine = this.server.getAxisServer();
/*     */ 
/* 170 */     MessageContext msgContext = new MessageContext(engine);
/* 171 */     Message requestMsg = null;
/*     */ 
/* 174 */     NonBlockingBufferedInputStream is = new NonBlockingBufferedInputStream();
/*     */ 
/* 178 */     StringBuffer soapAction = new StringBuffer();
/* 179 */     StringBuffer httpRequest = new StringBuffer();
/* 180 */     StringBuffer fileName = new StringBuffer();
/* 181 */     StringBuffer cookie = new StringBuffer();
/* 182 */     StringBuffer cookie2 = new StringBuffer();
/* 183 */     StringBuffer authInfo = new StringBuffer();
/* 184 */     StringBuffer contentType = new StringBuffer();
/* 185 */     StringBuffer contentLocation = new StringBuffer();
/*     */ 
/* 187 */     Message responseMsg = null;
/*     */ 
/* 196 */     msgContext.setTransportName(transportName);
/*     */ 
/* 198 */     responseMsg = null;
/*     */     try
/*     */     {
/* 202 */       byte[] status = OK;
/*     */ 
/* 205 */       boolean doWsdl = false;
/*     */ 
/* 208 */       String cooky = null;
/*     */ 
/* 210 */       String methodName = null;
/*     */       try
/*     */       {
/* 214 */         if (this.server.isSessionUsed()) {
/* 215 */           cookie.delete(0, cookie.length());
/* 216 */           cookie2.delete(0, cookie2.length());
/*     */         }
/* 218 */         authInfo.delete(0, authInfo.length());
/*     */ 
/* 221 */         is.setInputStream(this.socket.getInputStream());
/*     */ 
/* 223 */         MimeHeaders requestHeaders = new MimeHeaders();
/* 224 */         int contentLength = parseHeaders(is, buf, contentType, contentLocation, soapAction, httpRequest, fileName, cookie, cookie2, authInfo, requestHeaders);
/*     */ 
/* 228 */         is.setContentLength(contentLength);
/*     */ 
/* 230 */         int paramIdx = fileName.toString().indexOf('?');
/* 231 */         if (paramIdx != -1)
/*     */         {
/* 233 */           String params = fileName.substring(paramIdx + 1);
/* 234 */           fileName.setLength(paramIdx);
/*     */ 
/* 236 */           log.debug(Messages.getMessage("filename00", fileName.toString()));
/*     */ 
/* 238 */           log.debug(Messages.getMessage("params00", params));
/*     */ 
/* 241 */           if ("wsdl".equalsIgnoreCase(params)) {
/* 242 */             doWsdl = true;
/*     */           }
/* 244 */           if (params.startsWith("method=")) {
/* 245 */             methodName = params.substring(7);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 251 */         msgContext.setProperty("realpath", fileName.toString());
/*     */ 
/* 253 */         msgContext.setProperty("path", fileName.toString());
/*     */ 
/* 255 */         msgContext.setProperty("jws.classDir", "jwsClasses");
/*     */ 
/* 257 */         msgContext.setProperty("home.dir", ".");
/*     */ 
/* 260 */         String url = "http://" + getLocalHost() + ":" + this.server.getServerSocket().getLocalPort() + "/" + fileName.toString();
/*     */ 
/* 263 */         msgContext.setProperty("transport.url", url);
/*     */ 
/* 265 */         String filePart = fileName.toString();
/* 266 */         if (filePart.startsWith("axis/services/")) {
/* 267 */           String servicePart = filePart.substring(14);
/* 268 */           int separator = servicePart.indexOf('/');
/* 269 */           if (separator > -1) {
/* 270 */             msgContext.setProperty("objectID", servicePart.substring(separator + 1));
/*     */ 
/* 272 */             servicePart = servicePart.substring(0, separator);
/*     */           }
/* 274 */           msgContext.setTargetService(servicePart);
/*     */         }
/*     */ 
/* 277 */         if (authInfo.length() > 0)
/*     */         {
/* 280 */           byte[] decoded = Base64.decode(authInfo.toString());
/* 281 */           StringBuffer userBuf = new StringBuffer();
/* 282 */           StringBuffer pwBuf = new StringBuffer();
/* 283 */           StringBuffer authBuf = userBuf;
/* 284 */           for (int i = 0; i < decoded.length; i++) {
/* 285 */             if ((char)(decoded[i] & 0x7F) == ':') {
/* 286 */               authBuf = pwBuf;
/*     */             }
/*     */             else {
/* 289 */               authBuf.append((char)(decoded[i] & 0x7F));
/*     */             }
/*     */           }
/* 292 */           if (log.isDebugEnabled()) {
/* 293 */             log.debug(Messages.getMessage("user00", userBuf.toString()));
/*     */           }
/*     */ 
/* 297 */           msgContext.setUsername(userBuf.toString());
/* 298 */           msgContext.setPassword(pwBuf.toString());
/*     */         }
/*     */ 
/* 302 */         if (httpRequest.toString().equals("GET"))
/*     */         {
/* 304 */           OutputStream out = this.socket.getOutputStream();
/* 305 */           out.write(HTTP);
/* 306 */           if (fileName.length() == 0) {
/* 307 */             out.write("301 Redirect\nLocation: /axis/\n\n".getBytes());
/* 308 */             out.flush();
/* 309 */             jsr 1209;
/*     */           }
/* 311 */           out.write(status);
/*     */ 
/* 313 */           if (methodName != null) {
/* 314 */             String body = "<" + methodName + ">" + "</" + methodName + ">";
/*     */ 
/* 318 */             String msgtxt = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://www.w3.org/2003/05/soap-envelope\"><SOAP-ENV:Body>" + body + "</SOAP-ENV:Body>" + "</SOAP-ENV:Envelope>";
/*     */ 
/* 324 */             ByteArrayInputStream istream = new ByteArrayInputStream(msgtxt.getBytes());
/*     */ 
/* 326 */             requestMsg = new Message(istream);
/* 327 */           } else if (doWsdl) {
/* 328 */             engine.generateWSDL(msgContext);
/*     */ 
/* 330 */             Document doc = (Document)msgContext.getProperty("WSDL");
/* 331 */             if (doc != null) {
/* 332 */               XMLUtils.normalize(doc.getDocumentElement());
/* 333 */               String response = XMLUtils.PrettyDocumentToString(doc);
/* 334 */               byte[] respBytes = response.getBytes();
/*     */ 
/* 336 */               out.write(XML_MIME_STUFF);
/* 337 */               putInt(buf, out, respBytes.length);
/* 338 */               out.write(SEPARATOR);
/* 339 */               out.write(respBytes);
/* 340 */               out.flush();
/* 341 */               jsr 1003;
/*     */             }
/*     */           } else {
/* 344 */             StringBuffer sb = new StringBuffer();
/* 345 */             sb.append("<h2>And now... Some Services</h2>\n");
/* 346 */             Iterator i = engine.getConfig().getDeployedServices();
/* 347 */             sb.append("<ul>\n");
/* 348 */             while (i.hasNext()) {
/* 349 */               ServiceDesc sd = (ServiceDesc)i.next();
/* 350 */               sb.append("<li>\n");
/* 351 */               sb.append(sd.getName());
/* 352 */               sb.append(" <a href=\"services/");
/* 353 */               sb.append(sd.getName());
/* 354 */               sb.append("?wsdl\"><i>(wsdl)</i></a></li>\n");
/* 355 */               ArrayList operations = sd.getOperations();
/* 356 */               if (!operations.isEmpty()) {
/* 357 */                 sb.append("<ul>\n");
/* 358 */                 for (Iterator it = operations.iterator(); it.hasNext(); ) {
/* 359 */                   OperationDesc desc = (OperationDesc)it.next();
/* 360 */                   sb.append("<li>" + desc.getName());
/*     */                 }
/* 362 */                 sb.append("</ul>\n");
/*     */               }
/*     */             }
/* 365 */             sb.append("</ul>\n");
/*     */ 
/* 367 */             byte[] bytes = sb.toString().getBytes();
/*     */ 
/* 369 */             out.write(HTML_MIME_STUFF);
/* 370 */             putInt(buf, out, bytes.length);
/* 371 */             out.write(SEPARATOR);
/* 372 */             out.write(bytes);
/* 373 */             out.flush();
/* 374 */             jsr 735;
/*     */           }
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 380 */           String soapActionString = soapAction.toString();
/* 381 */           if (soapActionString != null) {
/* 382 */             msgContext.setUseSOAPAction(true);
/* 383 */             msgContext.setSOAPActionURI(soapActionString);
/*     */           }
/* 385 */           requestMsg = new Message(is, false, contentType.toString(), contentLocation.toString());
/*     */         }
/*     */ 
/* 393 */         MimeHeaders requestMimeHeaders = requestMsg.getMimeHeaders();
/* 394 */         for (Iterator i = requestHeaders.getAllHeaders(); i.hasNext(); ) {
/* 395 */           MimeHeader requestHeader = (MimeHeader)i.next();
/* 396 */           requestMimeHeaders.addHeader(requestHeader.getName(), requestHeader.getValue());
/*     */         }
/* 398 */         msgContext.setRequestMessage(requestMsg);
/*     */ 
/* 401 */         String requestEncoding = (String)requestMsg.getProperty("javax.xml.soap.character-set-encoding");
/* 402 */         if (requestEncoding != null) {
/* 403 */           msgContext.setProperty("javax.xml.soap.character-set-encoding", requestEncoding);
/*     */         }
/*     */ 
/* 407 */         if (this.server.isSessionUsed())
/*     */         {
/* 409 */           if (cookie.length() > 0)
/* 410 */             cooky = cookie.toString().trim();
/* 411 */           else if (cookie2.length() > 0) {
/* 412 */             cooky = cookie2.toString().trim();
/*     */           }
/*     */ 
/* 416 */           if (cooky == null)
/*     */           {
/* 420 */             int i = SimpleAxisServer.sessionIndex++;
/* 421 */             cooky = "" + i;
/*     */           }
/*     */ 
/* 424 */           msgContext.setSession(this.server.createSession(cooky));
/*     */         }
/*     */ 
/* 428 */         engine.invoke(msgContext);
/*     */ 
/* 431 */         responseMsg = msgContext.getResponseMessage();
/*     */ 
/* 433 */         if (responseMsg == null)
/* 434 */           status = NOCONTENT;
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */         AxisFault af;
/* 438 */         if ((e instanceof AxisFault)) {
/* 439 */           AxisFault af = (AxisFault)e;
/* 440 */           log.debug(Messages.getMessage("serverFault00"), af);
/* 441 */           QName faultCode = af.getFaultCode();
/* 442 */           if (Constants.FAULT_SOAP12_SENDER.equals(faultCode))
/* 443 */             status = SENDER;
/* 444 */           else if ("Server.Unauthorized".equals(af.getFaultCode().getLocalPart()))
/* 445 */             status = UNAUTH;
/*     */           else
/* 447 */             status = ISE;
/*     */         }
/*     */         else {
/* 450 */           status = ISE;
/* 451 */           af = AxisFault.makeFault(e);
/*     */         }
/*     */ 
/* 457 */         responseMsg = msgContext.getResponseMessage();
/* 458 */         if (responseMsg == null) {
/* 459 */           responseMsg = new Message(af);
/* 460 */           responseMsg.setMessageContext(msgContext);
/*     */         } else {
/*     */           try {
/* 463 */             SOAPEnvelope env = responseMsg.getSOAPEnvelope();
/* 464 */             env.clearBody();
/* 465 */             env.addBodyElement(new SOAPFault((AxisFault)e));
/*     */           }
/*     */           catch (AxisFault fault)
/*     */           {
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 473 */       String responseEncoding = (String)msgContext.getProperty("javax.xml.soap.character-set-encoding");
/* 474 */       if ((responseEncoding != null) && (responseMsg != null)) {
/* 475 */         responseMsg.setProperty("javax.xml.soap.character-set-encoding", responseEncoding);
/*     */       }
/*     */ 
/* 478 */       OutputStream out = this.socket.getOutputStream();
/* 479 */       out.write(HTTP);
/* 480 */       out.write(status);
/*     */ 
/* 482 */       if (responseMsg != null) {
/* 483 */         if ((this.server.isSessionUsed()) && (null != cooky) && (0 != cooky.trim().length()))
/*     */         {
/* 488 */           StringBuffer cookieOut = new StringBuffer();
/* 489 */           cookieOut.append("\r\nSet-Cookie: ").append(cooky).append("\r\nSet-Cookie2: ").append(cooky);
/*     */ 
/* 494 */           out.write(cookieOut.toString().getBytes());
/*     */         }
/*     */ 
/* 498 */         out.write(("\r\nContent-Type: " + responseMsg.getContentType(msgContext.getSOAPConstants())).getBytes());
/*     */ 
/* 504 */         for (Iterator i = responseMsg.getMimeHeaders().getAllHeaders(); i.hasNext(); ) {
/* 505 */           MimeHeader responseHeader = (MimeHeader)i.next();
/* 506 */           out.write(13);
/* 507 */           out.write(10);
/* 508 */           out.write(responseHeader.getName().getBytes());
/* 509 */           out.write(headerEnder);
/* 510 */           out.write(responseHeader.getValue().getBytes());
/*     */         }
/*     */ 
/* 513 */         out.write(SEPARATOR);
/* 514 */         responseMsg.writeTo(out);
/*     */       }
/*     */ 
/* 518 */       out.flush();
/*     */     } catch (Exception e) {
/* 520 */       log.info(Messages.getMessage("exception00"), e);
/*     */     } finally {
/*     */       try {
/* 523 */         if (this.socket != null) this.socket.close(); 
/*     */       }
/*     */       catch (Exception e) {
/*     */       }
/*     */     }
/* 527 */     if (msgContext.getProperty("quit.requested") != null)
/*     */       try
/*     */       {
/* 530 */         this.server.stop();
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/*     */   }
/*     */ 
/*     */   protected void invokeMethodFromGet(String methodName, String args)
/*     */     throws Exception
/*     */   {
/*     */   }
/*     */ 
/*     */   private int parseHeaders(NonBlockingBufferedInputStream is, byte[] buf, StringBuffer contentType, StringBuffer contentLocation, StringBuffer soapAction, StringBuffer httpRequest, StringBuffer fileName, StringBuffer cookie, StringBuffer cookie2, StringBuffer authInfo, MimeHeaders headers)
/*     */     throws IOException
/*     */   {
/* 567 */     int len = 0;
/*     */ 
/* 570 */     int n = readLine(is, buf, 0, buf.length);
/* 571 */     if (n < 0)
/*     */     {
/* 573 */       throw new IOException(Messages.getMessage("unexpectedEOS00"));
/*     */     }
/*     */ 
/* 577 */     httpRequest.delete(0, httpRequest.length());
/* 578 */     fileName.delete(0, fileName.length());
/* 579 */     contentType.delete(0, contentType.length());
/* 580 */     contentLocation.delete(0, contentLocation.length());
/*     */ 
/* 582 */     if (buf[0] == getHeader[0]) {
/* 583 */       httpRequest.append("GET");
/* 584 */       for (int i = 0; i < n - 5; i++) {
/* 585 */         char c = (char)(buf[(i + 5)] & 0x7F);
/* 586 */         if (c == ' ')
/*     */           break;
/* 588 */         fileName.append(c);
/*     */       }
/* 590 */       log.debug(Messages.getMessage("filename01", "SimpleAxisServer", fileName.toString()));
/* 591 */       return 0;
/* 592 */     }if (buf[0] == postHeader[0]) {
/* 593 */       httpRequest.append("POST");
/* 594 */       for (int i = 0; i < n - 6; i++) {
/* 595 */         char c = (char)(buf[(i + 6)] & 0x7F);
/* 596 */         if (c == ' ')
/*     */           break;
/* 598 */         fileName.append(c);
/*     */       }
/* 600 */       log.debug(Messages.getMessage("filename01", "SimpleAxisServer", fileName.toString()));
/*     */     } else {
/* 602 */       throw new IOException(Messages.getMessage("badRequest00"));
/*     */     }
/*     */ 
/* 605 */     while ((n = readLine(is, buf, 0, buf.length)) > 0)
/*     */     {
/* 607 */       if ((n <= 2) && ((buf[0] == 10) || (buf[0] == 13)) && (len > 0))
/*     */       {
/*     */         break;
/*     */       }
/*     */ 
/* 615 */       int endHeaderIndex = 0;
/* 616 */       while ((endHeaderIndex < n) && (toLower[buf[endHeaderIndex]] != headerEnder[0])) {
/* 617 */         endHeaderIndex++;
/*     */       }
/* 619 */       endHeaderIndex += 2;
/*     */ 
/* 624 */       int i = endHeaderIndex - 1;
/*     */ 
/* 627 */       if ((endHeaderIndex == lenLen) && (matches(buf, lenHeader)))
/*     */       {
/*     */         while (true) {
/* 630 */           i++; if ((i >= n) || (buf[i] < 48) || (buf[i] > 57)) break;
/* 631 */           len = len * 10 + (buf[i] - 48);
/*     */         }
/* 633 */         headers.addHeader("Content-Length", String.valueOf(len));
/*     */       }
/* 635 */       else if ((endHeaderIndex == actionLen) && (matches(buf, actionHeader)))
/*     */       {
/* 638 */         soapAction.delete(0, soapAction.length());
/*     */ 
/* 640 */         i++;
/*     */         while (true) { i++; if ((i >= n) || (buf[i] == 34)) break;
/* 642 */           soapAction.append((char)(buf[i] & 0x7F));
/*     */         }
/* 644 */         headers.addHeader("SOAPAction", "\"" + soapAction.toString() + "\"");
/*     */       }
/* 646 */       else if ((this.server.isSessionUsed()) && (endHeaderIndex == cookieLen) && (matches(buf, cookieHeader)))
/*     */       {
/*     */         while (true)
/*     */         {
/* 650 */           i++; if ((i >= n) || (buf[i] == 59) || (buf[i] == 13) || (buf[i] == 10)) break;
/* 651 */           cookie.append((char)(buf[i] & 0x7F));
/*     */         }
/* 653 */         headers.addHeader("Set-Cookie", cookie.toString());
/*     */       }
/* 655 */       else if ((this.server.isSessionUsed()) && (endHeaderIndex == cookie2Len) && (matches(buf, cookie2Header)))
/*     */       {
/*     */         while (true)
/*     */         {
/* 659 */           i++; if ((i >= n) || (buf[i] == 59) || (buf[i] == 13) || (buf[i] == 10)) break;
/* 660 */           cookie2.append((char)(buf[i] & 0x7F));
/*     */         }
/* 662 */         headers.addHeader("Set-Cookie2", cookie.toString());
/*     */       }
/* 664 */       else if ((endHeaderIndex == authLen) && (matches(buf, authHeader))) {
/* 665 */         if (matches(buf, endHeaderIndex, basicAuth)) {
/* 666 */           i += basicAuth.length;
/*     */           while (true) { i++; if ((i >= n) || (buf[i] == 13) || (buf[i] == 10)) break;
/* 668 */             if (buf[i] != 32)
/* 669 */               authInfo.append((char)(buf[i] & 0x7F));
/*     */           }
/* 671 */           headers.addHeader("Authorization", new String(basicAuth) + authInfo.toString());
/*     */         } else {
/* 673 */           throw new IOException(Messages.getMessage("badAuth00"));
/*     */         }
/*     */       }
/* 676 */       else if ((endHeaderIndex == locationLen) && (matches(buf, locationHeader))) {
/*     */         while (true) { i++; if ((i >= n) || (buf[i] == 13) || (buf[i] == 10)) break;
/* 678 */           if (buf[i] != 32)
/* 679 */             contentLocation.append((char)(buf[i] & 0x7F));
/*     */         }
/* 681 */         headers.addHeader("Content-Location", contentLocation.toString());
/* 682 */       } else if ((endHeaderIndex == typeLen) && (matches(buf, typeHeader))) {
/*     */         while (true) { i++; if ((i >= n) || (buf[i] == 13) || (buf[i] == 10)) break;
/* 684 */           if (buf[i] != 32)
/* 685 */             contentType.append((char)(buf[i] & 0x7F));
/*     */         }
/* 687 */         headers.addHeader("Content-Type", contentLocation.toString());
/*     */       } else {
/* 689 */         String customHeaderName = new String(buf, 0, endHeaderIndex - 2);
/* 690 */         StringBuffer customHeaderValue = new StringBuffer();
/*     */         while (true) { i++; if ((i >= n) || (buf[i] == 13) || (buf[i] == 10)) break;
/* 692 */           if (buf[i] != 32)
/* 693 */             customHeaderValue.append((char)(buf[i] & 0x7F));
/*     */         }
/* 695 */         headers.addHeader(customHeaderName, customHeaderValue.toString());
/*     */       }
/*     */     }
/*     */ 
/* 699 */     return len;
/*     */   }
/*     */ 
/*     */   public boolean matches(byte[] buf, byte[] target)
/*     */   {
/* 706 */     for (int i = 0; i < target.length; i++) {
/* 707 */       if (toLower[buf[i]] != target[i]) {
/* 708 */         return false;
/*     */       }
/*     */     }
/* 711 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean matches(byte[] buf, int bufIdx, byte[] target)
/*     */   {
/* 719 */     for (int i = 0; i < target.length; i++) {
/* 720 */       if (toLower[buf[(bufIdx + i)]] != target[i]) {
/* 721 */         return false;
/*     */       }
/*     */     }
/* 724 */     return true;
/*     */   }
/*     */ 
/*     */   private void putInt(byte[] buf, OutputStream out, int value)
/*     */     throws IOException
/*     */   {
/* 734 */     int len = 0;
/* 735 */     int offset = buf.length;
/*     */ 
/* 738 */     if (value < 0) {
/* 739 */       offset--; buf[offset] = 45;
/* 740 */       value = -value;
/* 741 */       len++;
/*     */     }
/*     */ 
/* 745 */     if (value == 0) {
/* 746 */       offset--; buf[offset] = 48;
/* 747 */       len++;
/*     */     }
/*     */ 
/* 751 */     while (value > 0) {
/* 752 */       offset--; buf[offset] = (byte)(value % 10 + 48);
/* 753 */       value /= 10;
/* 754 */       len++;
/*     */     }
/*     */ 
/* 758 */     out.write(buf, offset, len);
/*     */   }
/*     */ 
/*     */   private int readLine(NonBlockingBufferedInputStream is, byte[] b, int off, int len)
/*     */     throws IOException
/*     */   {
/* 770 */     int count = 0;
/*     */     int c;
/* 772 */     while ((c = is.read()) != -1) {
/* 773 */       if ((c != 10) && (c != 13)) {
/* 774 */         b[(off++)] = (byte)c;
/* 775 */         count++;
/*     */       }
/* 777 */       if (count == len) break;
/* 778 */       if (10 == c) {
/* 779 */         int peek = is.peek();
/* 780 */         if ((peek != 32) && (peek != 9)) break;
/*     */       }
/*     */     }
/* 783 */     return count > 0 ? count : -1;
/*     */   }
/*     */ 
/*     */   public static String getLocalHost()
/*     */   {
/* 790 */     return NetworkUtils.getLocalHostname();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  93 */     for (int i = 0; i < 256; i++) {
/*  94 */       toLower[i] = (byte)i;
/*     */     }
/*     */ 
/*  97 */     for (int lc = 97; lc <= 122; lc++) {
/*  98 */       toLower[(lc + 65 - 97)] = (byte)lc;
/*     */     }
/*     */ 
/* 106 */     lenHeader = "content-length: ".getBytes();
/* 107 */     lenLen = lenHeader.length;
/*     */ 
/* 110 */     typeHeader = ("Content-Type".toLowerCase() + ": ").getBytes();
/* 111 */     typeLen = typeHeader.length;
/*     */ 
/* 114 */     locationHeader = ("Content-Location".toLowerCase() + ": ").getBytes();
/* 115 */     locationLen = locationHeader.length;
/*     */ 
/* 118 */     actionHeader = "soapaction: ".getBytes();
/* 119 */     actionLen = actionHeader.length;
/*     */ 
/* 122 */     cookieHeader = "cookie: ".getBytes();
/* 123 */     cookieLen = cookieHeader.length;
/*     */ 
/* 126 */     cookie2Header = "cookie2: ".getBytes();
/* 127 */     cookie2Len = cookie2Header.length;
/*     */ 
/* 130 */     authHeader = "authorization: ".getBytes();
/* 131 */     authLen = authHeader.length;
/*     */ 
/* 134 */     getHeader = "GET".getBytes();
/*     */ 
/* 137 */     postHeader = "POST".getBytes();
/*     */ 
/* 140 */     headerEnder = ": ".getBytes();
/*     */ 
/* 143 */     basicAuth = "basic ".getBytes();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.http.SimpleAxisWorker
 * JD-Core Version:    0.6.0
 */