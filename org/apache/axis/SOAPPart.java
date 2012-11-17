/*      */ package org.apache.axis;
/*      */ 
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.BufferedWriter;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.PrintWriter;
/*      */ import java.io.Reader;
/*      */ import java.io.StringReader;
/*      */ import java.io.StringWriter;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.io.Writer;
/*      */ import java.util.Iterator;
/*      */ import java.util.Vector;
/*      */ import javax.xml.soap.SOAPException;
/*      */ import javax.xml.transform.Source;
/*      */ import javax.xml.transform.dom.DOMSource;
/*      */ import javax.xml.transform.stream.StreamSource;
/*      */ import org.apache.axis.components.logger.LogFactory;
/*      */ import org.apache.axis.encoding.DeserializationContext;
/*      */ import org.apache.axis.encoding.SerializationContext;
/*      */ import org.apache.axis.message.InputStreamBody;
/*      */ import org.apache.axis.message.MimeHeaders;
/*      */ import org.apache.axis.message.SOAPDocumentImpl;
/*      */ import org.apache.axis.message.SOAPHeaderElement;
/*      */ import org.apache.axis.transport.http.SocketInputStream;
/*      */ import org.apache.axis.utils.ByteArray;
/*      */ import org.apache.axis.utils.Messages;
/*      */ import org.apache.axis.utils.SessionUtils;
/*      */ import org.apache.axis.utils.XMLUtils;
/*      */ import org.apache.commons.logging.Log;
/*      */ import org.w3c.dom.Attr;
/*      */ import org.w3c.dom.CDATASection;
/*      */ import org.w3c.dom.Comment;
/*      */ import org.w3c.dom.DOMException;
/*      */ import org.w3c.dom.DOMImplementation;
/*      */ import org.w3c.dom.Document;
/*      */ import org.w3c.dom.DocumentFragment;
/*      */ import org.w3c.dom.DocumentType;
/*      */ import org.w3c.dom.Element;
/*      */ import org.w3c.dom.EntityReference;
/*      */ import org.w3c.dom.NamedNodeMap;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.NodeList;
/*      */ import org.w3c.dom.ProcessingInstruction;
/*      */ import org.w3c.dom.Text;
/*      */ import org.xml.sax.InputSource;
/*      */ import org.xml.sax.SAXException;
/*      */ 
/*      */ public class SOAPPart extends javax.xml.soap.SOAPPart
/*      */   implements Part
/*      */ {
/*   92 */   protected static Log log = LogFactory.getLog(SOAPPart.class.getName());
/*      */   public static final int FORM_STRING = 1;
/*      */   public static final int FORM_INPUTSTREAM = 2;
/*      */   public static final int FORM_SOAPENVELOPE = 3;
/*      */   public static final int FORM_BYTES = 4;
/*      */   public static final int FORM_BODYINSTREAM = 5;
/*      */   public static final int FORM_FAULT = 6;
/*      */   public static final int FORM_OPTIMIZED = 7;
/*      */   private int currentForm;
/*      */   public static final String ALLOW_FORM_OPTIMIZATION = "axis.form.optimization";
/*  111 */   private MimeHeaders mimeHeaders = new MimeHeaders();
/*      */ 
/*  113 */   private static final String[] formNames = { "", "FORM_STRING", "FORM_INPUTSTREAM", "FORM_SOAPENVELOPE", "FORM_BYTES", "FORM_BODYINSTREAM", "FORM_FAULT", "FORM_OPTIMIZED" };
/*      */   private Object currentMessage;
/*  132 */   private String currentEncoding = "UTF-8";
/*      */ 
/*  135 */   private String currentMessageAsString = null;
/*  136 */   private byte[] currentMessageAsBytes = null;
/*  137 */   private org.apache.axis.message.SOAPEnvelope currentMessageAsEnvelope = null;
/*      */   private Message msgObject;
/*  145 */   private Source contentSource = null;
/*      */ 
/* 1020 */   private Document document = new SOAPDocumentImpl(this);
/*      */   protected Document mDocument;
/*      */ 
/*      */   public SOAPPart(Message parent, Object initialContents, boolean isBodyStream)
/*      */   {
/*  164 */     setMimeHeader("Content-Id", SessionUtils.generateSessionId());
/*  165 */     setMimeHeader("Content-Type", "text/xml");
/*      */ 
/*  167 */     this.msgObject = parent;
/*      */ 
/*  169 */     int form = 1;
/*  170 */     if ((initialContents instanceof org.apache.axis.message.SOAPEnvelope)) {
/*  171 */       form = 3;
/*  172 */       ((org.apache.axis.message.SOAPEnvelope)initialContents).setOwnerDocument(this);
/*  173 */     } else if ((initialContents instanceof InputStream)) {
/*  174 */       form = isBodyStream ? 5 : 2;
/*  175 */     } else if ((initialContents instanceof byte[])) {
/*  176 */       form = 4;
/*  177 */     } else if ((initialContents instanceof AxisFault)) {
/*  178 */       form = 6;
/*      */     }
/*      */ 
/*  181 */     if (log.isDebugEnabled()) {
/*  182 */       log.debug("Enter: SOAPPart ctor(" + formNames[form] + ")");
/*      */     }
/*      */ 
/*  185 */     setCurrentMessage(initialContents, form);
/*      */ 
/*  187 */     if (log.isDebugEnabled())
/*  188 */       log.debug("Exit: SOAPPart ctor()");
/*      */   }
/*      */ 
/*      */   public Message getMessage()
/*      */   {
/*  199 */     return this.msgObject;
/*      */   }
/*      */ 
/*      */   public void setMessage(Message msg)
/*      */   {
/*  209 */     this.msgObject = msg;
/*      */   }
/*      */ 
/*      */   public String getContentType()
/*      */   {
/*  218 */     return "text/xml";
/*      */   }
/*      */ 
/*      */   public long getContentLength()
/*      */     throws AxisFault
/*      */   {
/*  229 */     saveChanges();
/*  230 */     if (this.currentForm == 7)
/*  231 */       return ((ByteArray)this.currentMessage).size();
/*  232 */     if (this.currentForm == 4) {
/*  233 */       return ((byte[])this.currentMessage).length;
/*      */     }
/*  235 */     byte[] bytes = getAsBytes();
/*  236 */     return bytes.length;
/*      */   }
/*      */ 
/*      */   public void setSOAPEnvelope(org.apache.axis.message.SOAPEnvelope env)
/*      */   {
/*  253 */     setCurrentMessage(env, 3);
/*      */   }
/*      */ 
/*      */   public void writeTo(OutputStream os)
/*      */     throws IOException
/*      */   {
/*  262 */     if (this.currentForm == 4) {
/*  263 */       os.write((byte[])this.currentMessage);
/*  264 */     } else if (this.currentForm == 7) {
/*  265 */       ((ByteArray)this.currentMessage).writeTo(os);
/*      */     } else {
/*  267 */       Writer writer = new OutputStreamWriter(os, this.currentEncoding);
/*  268 */       writer = new BufferedWriter(new PrintWriter(writer));
/*  269 */       writeTo(writer);
/*  270 */       writer.flush();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void writeTo(Writer writer)
/*      */     throws IOException
/*      */   {
/*  280 */     boolean inclXmlDecl = false;
/*      */ 
/*  282 */     if (this.msgObject.getMessageContext() != null)
/*  283 */       inclXmlDecl = true;
/*      */     else {
/*      */       try {
/*  286 */         String xmlDecl = (String)this.msgObject.getProperty("javax.xml.soap.write-xml-declaration");
/*  287 */         if ((xmlDecl != null) && (xmlDecl.equals("true")))
/*  288 */           inclXmlDecl = true;
/*      */       }
/*      */       catch (SOAPException e) {
/*  291 */         throw new IOException(e.getMessage());
/*      */       }
/*      */     }
/*      */ 
/*  295 */     if (this.currentForm == 6) {
/*  296 */       AxisFault env = (AxisFault)this.currentMessage;
/*      */       try {
/*  298 */         SerializationContext serContext = new SerializationContext(writer, getMessage().getMessageContext());
/*  299 */         serContext.setSendDecl(inclXmlDecl);
/*  300 */         serContext.setEncoding(this.currentEncoding);
/*  301 */         env.output(serContext);
/*      */       } catch (Exception e) {
/*  303 */         log.error(Messages.getMessage("exception00"), e);
/*  304 */         throw env;
/*      */       }
/*  306 */       return;
/*      */     }
/*      */ 
/*  309 */     if (this.currentForm == 3) {
/*  310 */       org.apache.axis.message.SOAPEnvelope env = (org.apache.axis.message.SOAPEnvelope)this.currentMessage;
/*      */       try {
/*  312 */         SerializationContext serContext = new SerializationContext(writer, getMessage().getMessageContext());
/*  313 */         serContext.setSendDecl(inclXmlDecl);
/*  314 */         serContext.setEncoding(this.currentEncoding);
/*  315 */         env.output(serContext);
/*      */       } catch (Exception e) {
/*  317 */         throw AxisFault.makeFault(e);
/*      */       }
/*  319 */       return;
/*      */     }
/*      */ 
/*  322 */     String xml = getAsString();
/*  323 */     if ((inclXmlDecl) && 
/*  324 */       (!xml.startsWith("<?xml"))) {
/*  325 */       writer.write("<?xml version=\"1.0\" encoding=\"");
/*  326 */       writer.write(this.currentEncoding);
/*  327 */       writer.write("\"?>");
/*      */     }
/*      */ 
/*  330 */     writer.write(xml);
/*      */   }
/*      */ 
/*      */   public Object getCurrentMessage()
/*      */   {
/*  344 */     return this.currentMessage;
/*      */   }
/*      */ 
/*      */   public void setCurrentMessage(Object currMsg, int form)
/*      */   {
/*  353 */     this.currentMessageAsString = null;
/*  354 */     this.currentMessageAsBytes = null;
/*  355 */     this.currentMessageAsEnvelope = null;
/*  356 */     setCurrentForm(currMsg, form);
/*      */   }
/*      */ 
/*      */   private void setCurrentForm(Object currMsg, int form)
/*      */   {
/*  367 */     if (log.isDebugEnabled())
/*      */     {
/*      */       String msgStr;
/*      */       String msgStr;
/*  369 */       if ((currMsg instanceof String))
/*  370 */         msgStr = (String)currMsg;
/*      */       else {
/*  372 */         msgStr = currMsg.getClass().getName();
/*      */       }
/*  374 */       log.debug(Messages.getMessage("setMsgForm", formNames[form], "" + msgStr));
/*      */     }
/*      */ 
/*  379 */     if (isFormOptimizationAllowed()) {
/*  380 */       this.currentMessage = currMsg;
/*  381 */       this.currentForm = form;
/*  382 */       if (this.currentForm == 3)
/*  383 */         this.currentMessageAsEnvelope = ((org.apache.axis.message.SOAPEnvelope)currMsg);
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean isFormOptimizationAllowed()
/*      */   {
/*  393 */     boolean allowFormOptimization = true;
/*  394 */     Message msg = getMessage();
/*  395 */     if (msg != null) {
/*  396 */       MessageContext ctx = msg.getMessageContext();
/*  397 */       if (ctx != null) {
/*  398 */         Boolean propFormOptimization = (Boolean)ctx.getProperty("axis.form.optimization");
/*  399 */         if (propFormOptimization != null) {
/*  400 */           allowFormOptimization = propFormOptimization.booleanValue();
/*      */         }
/*      */       }
/*      */     }
/*  404 */     return allowFormOptimization;
/*      */   }
/*      */ 
/*      */   public int getCurrentForm() {
/*  408 */     return this.currentForm;
/*      */   }
/*      */ 
/*      */   public byte[] getAsBytes()
/*      */     throws AxisFault
/*      */   {
/*  419 */     log.debug("Enter: SOAPPart::getAsBytes");
/*  420 */     if (this.currentForm == 7) {
/*  421 */       log.debug("Exit: SOAPPart::getAsBytes");
/*      */       try {
/*  423 */         return ((ByteArray)this.currentMessage).toByteArray();
/*      */       } catch (IOException e) {
/*  425 */         throw AxisFault.makeFault(e);
/*      */       }
/*      */     }
/*  428 */     if (this.currentForm == 4) {
/*  429 */       log.debug("Exit: SOAPPart::getAsBytes");
/*  430 */       return (byte[])this.currentMessage;
/*      */     }
/*      */ 
/*  433 */     if (this.currentForm == 5) {
/*      */       try {
/*  435 */         getAsSOAPEnvelope();
/*      */       } catch (Exception e) {
/*  437 */         log.fatal(Messages.getMessage("makeEnvFail00"), e);
/*  438 */         log.debug("Exit: SOAPPart::getAsBytes");
/*  439 */         return null;
/*      */       }
/*      */     }
/*      */ 
/*  443 */     if (this.currentForm == 2) {
/*      */       try
/*      */       {
/*  446 */         InputStream inp = null;
/*  447 */         byte[] buf = null;
/*      */         try {
/*  449 */           inp = (InputStream)this.currentMessage;
/*  450 */           ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*  451 */           buf = new byte[4096];
/*      */           int len;
/*  453 */           while ((len = inp.read(buf, 0, 4096)) != -1)
/*  454 */             baos.write(buf, 0, len);
/*  455 */           buf = baos.toByteArray();
/*      */         } finally {
/*  457 */           if ((inp != null) && ((this.currentMessage instanceof SocketInputStream)))
/*      */           {
/*  459 */             inp.close();
/*      */           }
/*      */         }
/*  461 */         setCurrentForm(buf, 4);
/*  462 */         log.debug("Exit: SOAPPart::getAsBytes");
/*  463 */         return (byte[])this.currentMessage;
/*      */       }
/*      */       catch (Exception e) {
/*  466 */         log.error(Messages.getMessage("exception00"), e);
/*      */ 
/*  468 */         log.debug("Exit: SOAPPart::getAsBytes");
/*  469 */         return null;
/*      */       }
/*      */     }
/*  472 */     if ((this.currentForm == 3) || (this.currentForm == 6))
/*      */     {
/*  474 */       this.currentEncoding = XMLUtils.getEncoding(this.msgObject, null);
/*  475 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*  476 */       BufferedOutputStream os = new BufferedOutputStream(baos);
/*      */       try {
/*  478 */         writeTo(os);
/*  479 */         os.flush();
/*      */       } catch (Exception e) {
/*  481 */         throw AxisFault.makeFault(e);
/*      */       }
/*  483 */       setCurrentForm(baos.toByteArray(), 4);
/*  484 */       if (log.isDebugEnabled()) {
/*  485 */         log.debug("Exit: SOAPPart::getAsBytes(): " + this.currentMessage);
/*      */       }
/*  487 */       return (byte[])this.currentMessage;
/*      */     }
/*      */ 
/*  490 */     if (this.currentForm == 1)
/*      */     {
/*  494 */       if ((this.currentMessage == this.currentMessageAsString) && (this.currentMessageAsBytes != null))
/*      */       {
/*  496 */         if (log.isDebugEnabled()) {
/*  497 */           log.debug("Exit: SOAPPart::getAsBytes()");
/*      */         }
/*  499 */         return this.currentMessageAsBytes;
/*      */       }
/*      */ 
/*  502 */       this.currentMessageAsString = ((String)this.currentMessage);
/*      */       try {
/*  504 */         this.currentEncoding = XMLUtils.getEncoding(this.msgObject, null);
/*  505 */         setCurrentForm(((String)this.currentMessage).getBytes(this.currentEncoding), 4);
/*      */       }
/*      */       catch (UnsupportedEncodingException ue) {
/*  508 */         setCurrentForm(((String)this.currentMessage).getBytes(), 4);
/*      */       }
/*      */ 
/*  511 */       this.currentMessageAsBytes = ((byte[])this.currentMessage);
/*      */ 
/*  513 */       log.debug("Exit: SOAPPart::getAsBytes");
/*  514 */       return (byte[])this.currentMessage;
/*      */     }
/*      */ 
/*  517 */     log.error(Messages.getMessage("cantConvert00", "" + this.currentForm));
/*      */ 
/*  519 */     log.debug("Exit: SOAPPart::getAsBytes");
/*  520 */     return null;
/*      */   }
/*      */ 
/*      */   public void saveChanges() throws AxisFault {
/*  524 */     log.debug("Enter: SOAPPart::saveChanges");
/*  525 */     if ((this.currentForm == 3) || (this.currentForm == 6))
/*      */     {
/*  527 */       this.currentEncoding = XMLUtils.getEncoding(this.msgObject, null);
/*  528 */       ByteArray array = new ByteArray();
/*      */       try {
/*  530 */         writeTo(array);
/*  531 */         array.flush();
/*      */       } catch (Exception e) {
/*  533 */         throw AxisFault.makeFault(e);
/*      */       }
/*  535 */       setCurrentForm(array, 7);
/*  536 */       if (log.isDebugEnabled())
/*  537 */         log.debug("Exit: SOAPPart::saveChanges(): " + this.currentMessage);
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getAsString()
/*      */     throws AxisFault
/*      */   {
/*  549 */     log.debug("Enter: SOAPPart::getAsString");
/*  550 */     if (this.currentForm == 1) {
/*  551 */       if (log.isDebugEnabled()) {
/*  552 */         log.debug("Exit: SOAPPart::getAsString(): " + this.currentMessage);
/*      */       }
/*  554 */       return (String)this.currentMessage;
/*      */     }
/*      */ 
/*  557 */     if ((this.currentForm == 2) || (this.currentForm == 5))
/*      */     {
/*  559 */       getAsBytes();
/*      */     }
/*      */ 
/*  563 */     if (this.currentForm == 7) {
/*      */       try {
/*  565 */         this.currentMessageAsBytes = ((ByteArray)this.currentMessage).toByteArray();
/*      */       }
/*      */       catch (IOException e) {
/*  568 */         throw AxisFault.makeFault(e);
/*      */       }
/*      */       try
/*      */       {
/*  572 */         setCurrentForm(new String((byte[])this.currentMessageAsBytes, this.currentEncoding), 1);
/*      */       }
/*      */       catch (UnsupportedEncodingException ue)
/*      */       {
/*  576 */         setCurrentForm(new String((byte[])this.currentMessageAsBytes), 1);
/*      */       }
/*      */ 
/*  579 */       if (log.isDebugEnabled()) {
/*  580 */         log.debug("Exit: SOAPPart::getAsString(): " + this.currentMessage);
/*      */       }
/*  582 */       return (String)this.currentMessage;
/*      */     }
/*      */ 
/*  585 */     if (this.currentForm == 4)
/*      */     {
/*  589 */       if ((this.currentMessage == this.currentMessageAsBytes) && (this.currentMessageAsString != null))
/*      */       {
/*  591 */         if (log.isDebugEnabled()) {
/*  592 */           log.debug("Exit: SOAPPart::getAsString(): " + this.currentMessageAsString);
/*      */         }
/*  594 */         return this.currentMessageAsString;
/*      */       }
/*      */ 
/*  598 */       this.currentMessageAsBytes = ((byte[])this.currentMessage);
/*      */       try {
/*  600 */         setCurrentForm(new String((byte[])this.currentMessage, this.currentEncoding), 1);
/*      */       }
/*      */       catch (UnsupportedEncodingException ue)
/*      */       {
/*  604 */         setCurrentForm(new String((byte[])this.currentMessage), 1);
/*      */       }
/*      */ 
/*  607 */       this.currentMessageAsString = ((String)this.currentMessage);
/*  608 */       if (log.isDebugEnabled()) {
/*  609 */         log.debug("Exit: SOAPPart::getAsString(): " + this.currentMessage);
/*      */       }
/*  611 */       return (String)this.currentMessage;
/*      */     }
/*      */ 
/*  614 */     if (this.currentForm == 6) {
/*  615 */       StringWriter writer = new StringWriter();
/*      */       try {
/*  617 */         writeTo(writer);
/*      */       } catch (Exception e) {
/*  619 */         log.error(Messages.getMessage("exception00"), e);
/*  620 */         return null;
/*      */       }
/*  622 */       setCurrentForm(writer.getBuffer().toString(), 1);
/*  623 */       if (log.isDebugEnabled()) {
/*  624 */         log.debug("Exit: SOAPPart::getAsString(): " + this.currentMessage);
/*      */       }
/*  626 */       return (String)this.currentMessage;
/*      */     }
/*      */ 
/*  629 */     if (this.currentForm == 3) {
/*  630 */       StringWriter writer = new StringWriter();
/*      */       try {
/*  632 */         writeTo(writer);
/*      */       } catch (Exception e) {
/*  634 */         throw AxisFault.makeFault(e);
/*      */       }
/*  636 */       setCurrentForm(writer.getBuffer().toString(), 1);
/*  637 */       if (log.isDebugEnabled()) {
/*  638 */         log.debug("Exit: SOAPPart::getAsString(): " + this.currentMessage);
/*      */       }
/*  640 */       return (String)this.currentMessage;
/*      */     }
/*      */ 
/*  643 */     log.error(Messages.getMessage("cantConvert01", "" + this.currentForm));
/*      */ 
/*  645 */     log.debug("Exit: SOAPPart::getAsString()");
/*  646 */     return null;
/*      */   }
/*      */ 
/*      */   public org.apache.axis.message.SOAPEnvelope getAsSOAPEnvelope()
/*      */     throws AxisFault
/*      */   {
/*  660 */     if (log.isDebugEnabled()) {
/*  661 */       log.debug("Enter: SOAPPart::getAsSOAPEnvelope()");
/*  662 */       log.debug(Messages.getMessage("currForm", formNames[this.currentForm]));
/*      */     }
/*  664 */     if (this.currentForm == 3) {
/*  665 */       return (org.apache.axis.message.SOAPEnvelope)this.currentMessage;
/*      */     }
/*      */ 
/*  668 */     if (this.currentForm == 5) {
/*  669 */       InputStreamBody bodyEl = new InputStreamBody((InputStream)this.currentMessage);
/*      */ 
/*  671 */       org.apache.axis.message.SOAPEnvelope env = new org.apache.axis.message.SOAPEnvelope();
/*  672 */       env.setOwnerDocument(this);
/*  673 */       env.addBodyElement(bodyEl);
/*  674 */       setCurrentForm(env, 3);
/*  675 */       return env;
/*      */     }
/*      */     InputSource is;
/*  680 */     if (this.currentForm == 2) {
/*  681 */       InputSource is = new InputSource((InputStream)this.currentMessage);
/*  682 */       String encoding = XMLUtils.getEncoding(this.msgObject, null, null);
/*  683 */       if (encoding != null) {
/*  684 */         this.currentEncoding = encoding;
/*  685 */         is.setEncoding(this.currentEncoding);
/*      */       }
/*      */     } else {
/*  688 */       is = new InputSource(new StringReader(getAsString()));
/*      */     }
/*  690 */     DeserializationContext dser = new DeserializationContext(is, getMessage().getMessageContext(), getMessage().getMessageType());
/*      */ 
/*  693 */     dser.getEnvelope().setOwnerDocument(this);
/*      */     try
/*      */     {
/*  696 */       dser.parse();
/*      */     } catch (SAXException e) {
/*  698 */       Exception real = e.getException();
/*  699 */       if (real == null)
/*  700 */         real = e;
/*  701 */       throw AxisFault.makeFault(real);
/*      */     }
/*      */ 
/*  704 */     org.apache.axis.message.SOAPEnvelope nse = dser.getEnvelope();
/*  705 */     if (this.currentMessageAsEnvelope != null)
/*      */     {
/*  707 */       Vector newHeaders = nse.getHeaders();
/*  708 */       Vector oldHeaders = this.currentMessageAsEnvelope.getHeaders();
/*  709 */       if ((null != newHeaders) && (null != oldHeaders)) {
/*  710 */         Iterator ohi = oldHeaders.iterator();
/*  711 */         Iterator nhi = newHeaders.iterator();
/*  712 */         while ((ohi.hasNext()) && (nhi.hasNext())) {
/*  713 */           SOAPHeaderElement nhe = (SOAPHeaderElement)nhi.next();
/*  714 */           SOAPHeaderElement ohe = (SOAPHeaderElement)ohi.next();
/*      */ 
/*  716 */           if (ohe.isProcessed()) nhe.setProcessed(true);
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  722 */     setCurrentForm(nse, 3);
/*      */ 
/*  724 */     log.debug("Exit: SOAPPart::getAsSOAPEnvelope");
/*  725 */     org.apache.axis.message.SOAPEnvelope env = (org.apache.axis.message.SOAPEnvelope)this.currentMessage;
/*  726 */     env.setOwnerDocument(this);
/*  727 */     return env;
/*      */   }
/*      */ 
/*      */   public void addMimeHeader(String header, String value)
/*      */   {
/*  737 */     this.mimeHeaders.addHeader(header, value);
/*      */   }
/*      */ 
/*      */   private String getFirstMimeHeader(String header)
/*      */   {
/*  747 */     String[] values = this.mimeHeaders.getHeader(header);
/*  748 */     if ((values != null) && (values.length > 0))
/*  749 */       return values[0];
/*  750 */     return null;
/*      */   }
/*      */ 
/*      */   public String getContentLocation()
/*      */   {
/*  764 */     return getFirstMimeHeader("Content-Location");
/*      */   }
/*      */ 
/*      */   public void setContentLocation(String loc)
/*      */   {
/*  773 */     setMimeHeader("Content-Location", loc);
/*      */   }
/*      */ 
/*      */   public void setContentId(String newCid)
/*      */   {
/*  782 */     setMimeHeader("Content-Id", newCid);
/*      */   }
/*      */ 
/*      */   public String getContentId()
/*      */   {
/*  791 */     return getFirstMimeHeader("Content-Id");
/*      */   }
/*      */ 
/*      */   public String getContentIdRef()
/*      */   {
/*  801 */     return "cid:" + getContentId();
/*      */   }
/*      */ 
/*      */   public Iterator getMatchingMimeHeaders(String[] match)
/*      */   {
/*  813 */     return this.mimeHeaders.getMatchingHeaders(match);
/*      */   }
/*      */ 
/*      */   public Iterator getNonMatchingMimeHeaders(String[] match)
/*      */   {
/*  824 */     return this.mimeHeaders.getNonMatchingHeaders(match);
/*      */   }
/*      */ 
/*      */   public void setContent(Source source)
/*      */     throws SOAPException
/*      */   {
/*  837 */     if (source == null) {
/*  838 */       throw new SOAPException(Messages.getMessage("illegalArgumentException00"));
/*      */     }
/*      */ 
/*  841 */     MessageContext ctx = getMessage().getMessageContext();
/*  842 */     if (ctx != null) {
/*  843 */       ctx.setProperty("axis.form.optimization", Boolean.TRUE);
/*      */     }
/*      */ 
/*  847 */     this.contentSource = source;
/*  848 */     InputSource in = XMLUtils.sourceToInputSource(this.contentSource);
/*  849 */     InputStream is = in.getByteStream();
/*  850 */     if (is != null) {
/*  851 */       setCurrentMessage(is, 2);
/*      */     } else {
/*  853 */       Reader r = in.getCharacterStream();
/*  854 */       if (r == null) {
/*  855 */         throw new SOAPException(Messages.getMessage("noCharacterOrByteStream"));
/*      */       }
/*  857 */       BufferedReader br = new BufferedReader(r);
/*  858 */       String line = null;
/*  859 */       StringBuffer sb = new StringBuffer();
/*      */       try {
/*  861 */         while ((line = br.readLine()) != null)
/*  862 */           sb.append(line);
/*      */       }
/*      */       catch (IOException e) {
/*  865 */         throw new SOAPException(Messages.getMessage("couldNotReadFromCharStream"), e);
/*      */       }
/*  867 */       setCurrentMessage(sb.toString(), 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Source getContent()
/*      */     throws SOAPException
/*      */   {
/*  881 */     if (this.contentSource == null) {
/*  882 */       switch (this.currentForm) {
/*      */       case 1:
/*  884 */         String s = (String)this.currentMessage;
/*  885 */         this.contentSource = new StreamSource(new StringReader(s));
/*  886 */         break;
/*      */       case 2:
/*  888 */         this.contentSource = new StreamSource((InputStream)this.currentMessage);
/*      */ 
/*  890 */         break;
/*      */       case 3:
/*  892 */         org.apache.axis.message.SOAPEnvelope se = (org.apache.axis.message.SOAPEnvelope)this.currentMessage;
/*      */         try {
/*  894 */           this.contentSource = new DOMSource(se.getAsDocument());
/*      */         } catch (Exception e) {
/*  896 */           throw new SOAPException(Messages.getMessage("errorGetDocFromSOAPEnvelope"), e);
/*      */         }
/*      */ 
/*      */       case 7:
/*      */         try
/*      */         {
/*  902 */           ByteArrayInputStream baos = new ByteArrayInputStream(((ByteArray)this.currentMessage).toByteArray());
/*  903 */           this.contentSource = new StreamSource(baos);
/*      */         } catch (IOException e) {
/*  905 */           throw new SOAPException(Messages.getMessage("errorGetDocFromSOAPEnvelope"), e);
/*      */         }
/*      */ 
/*      */       case 4:
/*  910 */         byte[] bytes = (byte[])this.currentMessage;
/*  911 */         this.contentSource = new StreamSource(new ByteArrayInputStream(bytes));
/*      */ 
/*  913 */         break;
/*      */       case 5:
/*  915 */         this.contentSource = new StreamSource((InputStream)this.currentMessage);
/*      */       case 6:
/*      */       }
/*      */     }
/*      */ 
/*  920 */     return this.contentSource;
/*      */   }
/*      */ 
/*      */   public Iterator getAllMimeHeaders()
/*      */   {
/*  931 */     return this.mimeHeaders.getAllHeaders();
/*      */   }
/*      */ 
/*      */   public void setMimeHeader(String name, String value)
/*      */   {
/*  959 */     this.mimeHeaders.setHeader(name, value);
/*      */   }
/*      */ 
/*      */   public String[] getMimeHeader(String name)
/*      */   {
/*  973 */     return this.mimeHeaders.getHeader(name);
/*      */   }
/*      */ 
/*      */   public void removeAllMimeHeaders()
/*      */   {
/*  981 */     this.mimeHeaders.removeAllHeaders();
/*      */   }
/*      */ 
/*      */   public void removeMimeHeader(String header)
/*      */   {
/*  990 */     this.mimeHeaders.removeHeader(header);
/*      */   }
/*      */ 
/*      */   public javax.xml.soap.SOAPEnvelope getEnvelope()
/*      */     throws SOAPException
/*      */   {
/*      */     try
/*      */     {
/* 1003 */       return getAsSOAPEnvelope(); } catch (AxisFault af) {
/*      */     }
/* 1005 */     throw new SOAPException(af);
/*      */   }
/*      */ 
/*      */   public Document getSOAPDocument()
/*      */   {
/* 1025 */     if (this.document == null) {
/* 1026 */       this.document = new SOAPDocumentImpl(this);
/*      */     }
/* 1028 */     return this.document;
/*      */   }
/*      */ 
/*      */   public DocumentType getDoctype()
/*      */   {
/* 1035 */     return this.document.getDoctype();
/*      */   }
/*      */ 
/*      */   public DOMImplementation getImplementation()
/*      */   {
/* 1042 */     return this.document.getImplementation();
/*      */   }
/*      */ 
/*      */   public Element getDocumentElement()
/*      */   {
/*      */     try
/*      */     {
/* 1053 */       return getEnvelope(); } catch (SOAPException se) {
/*      */     }
/* 1055 */     return null;
/*      */   }
/*      */ 
/*      */   public Element createElement(String tagName)
/*      */     throws DOMException
/*      */   {
/* 1066 */     return this.document.createElement(tagName);
/*      */   }
/*      */ 
/*      */   public DocumentFragment createDocumentFragment() {
/* 1070 */     return this.document.createDocumentFragment();
/*      */   }
/*      */ 
/*      */   public Text createTextNode(String data) {
/* 1074 */     return this.document.createTextNode(data);
/*      */   }
/*      */ 
/*      */   public Comment createComment(String data) {
/* 1078 */     return this.document.createComment(data);
/*      */   }
/*      */ 
/*      */   public CDATASection createCDATASection(String data) throws DOMException {
/* 1082 */     return this.document.createCDATASection(data);
/*      */   }
/*      */ 
/*      */   public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException
/*      */   {
/* 1087 */     return this.document.createProcessingInstruction(target, data);
/*      */   }
/*      */ 
/*      */   public Attr createAttribute(String name) throws DOMException {
/* 1091 */     return this.document.createAttribute(name);
/*      */   }
/*      */ 
/*      */   public EntityReference createEntityReference(String name) throws DOMException {
/* 1095 */     return this.document.createEntityReference(name);
/*      */   }
/*      */ 
/*      */   public NodeList getElementsByTagName(String tagname) {
/* 1099 */     return this.document.getElementsByTagName(tagname);
/*      */   }
/*      */ 
/*      */   public Node importNode(Node importedNode, boolean deep) throws DOMException
/*      */   {
/* 1104 */     return this.document.importNode(importedNode, deep);
/*      */   }
/*      */ 
/*      */   public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException
/*      */   {
/* 1109 */     return this.document.createElementNS(namespaceURI, qualifiedName);
/*      */   }
/*      */ 
/*      */   public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException
/*      */   {
/* 1114 */     return this.document.createAttributeNS(namespaceURI, qualifiedName);
/*      */   }
/*      */ 
/*      */   public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
/* 1118 */     return this.document.getElementsByTagNameNS(namespaceURI, localName);
/*      */   }
/*      */ 
/*      */   public Element getElementById(String elementId) {
/* 1122 */     return this.document.getElementById(elementId);
/*      */   }
/*      */ 
/*      */   public String getEncoding()
/*      */   {
/* 1129 */     return this.currentEncoding;
/*      */   }
/*      */ 
/*      */   public void setEncoding(String s)
/*      */   {
/* 1134 */     this.currentEncoding = s;
/*      */   }
/*      */ 
/*      */   public boolean getStandalone()
/*      */   {
/* 1139 */     throw new UnsupportedOperationException("Not yet implemented.71");
/*      */   }
/*      */ 
/*      */   public void setStandalone(boolean flag)
/*      */   {
/* 1145 */     throw new UnsupportedOperationException("Not yet implemented.72");
/*      */   }
/*      */ 
/*      */   public boolean getStrictErrorChecking()
/*      */   {
/* 1150 */     throw new UnsupportedOperationException("Not yet implemented.73");
/*      */   }
/*      */ 
/*      */   public void setStrictErrorChecking(boolean flag)
/*      */   {
/* 1156 */     throw new UnsupportedOperationException("Not yet implemented. 74");
/*      */   }
/*      */ 
/*      */   public String getVersion()
/*      */   {
/* 1162 */     throw new UnsupportedOperationException("Not yet implemented. 75");
/*      */   }
/*      */ 
/*      */   public void setVersion(String s)
/*      */   {
/* 1168 */     throw new UnsupportedOperationException("Not yet implemented.76");
/*      */   }
/*      */ 
/*      */   public Node adoptNode(Node node)
/*      */     throws DOMException
/*      */   {
/* 1175 */     throw new UnsupportedOperationException("Not yet implemented.77");
/*      */   }
/*      */ 
/*      */   public String getNodeName()
/*      */   {
/* 1183 */     return this.document.getNodeName();
/*      */   }
/*      */ 
/*      */   public String getNodeValue() throws DOMException {
/* 1187 */     return this.document.getNodeValue();
/*      */   }
/*      */ 
/*      */   public void setNodeValue(String nodeValue) throws DOMException {
/* 1191 */     this.document.setNodeValue(nodeValue);
/*      */   }
/*      */ 
/*      */   public short getNodeType() {
/* 1195 */     return this.document.getNodeType();
/*      */   }
/*      */ 
/*      */   public Node getParentNode() {
/* 1199 */     return this.document.getParentNode();
/*      */   }
/*      */ 
/*      */   public NodeList getChildNodes() {
/* 1203 */     return this.document.getChildNodes();
/*      */   }
/*      */ 
/*      */   public Node getFirstChild() {
/* 1207 */     return this.document.getFirstChild();
/*      */   }
/*      */ 
/*      */   public Node getLastChild() {
/* 1211 */     return this.document.getLastChild();
/*      */   }
/*      */ 
/*      */   public Node getPreviousSibling() {
/* 1215 */     return this.document.getPreviousSibling();
/*      */   }
/*      */ 
/*      */   public Node getNextSibling() {
/* 1219 */     return this.document.getNextSibling();
/*      */   }
/*      */ 
/*      */   public NamedNodeMap getAttributes() {
/* 1223 */     return this.document.getAttributes();
/*      */   }
/*      */ 
/*      */   public Document getOwnerDocument() {
/* 1227 */     return this.document.getOwnerDocument();
/*      */   }
/*      */ 
/*      */   public Node insertBefore(Node newChild, Node refChild) throws DOMException {
/* 1231 */     return this.document.insertBefore(newChild, refChild);
/*      */   }
/*      */ 
/*      */   public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
/* 1235 */     return this.document.replaceChild(newChild, oldChild);
/*      */   }
/*      */ 
/*      */   public Node removeChild(Node oldChild) throws DOMException {
/* 1239 */     return this.document.removeChild(oldChild);
/*      */   }
/*      */ 
/*      */   public Node appendChild(Node newChild) throws DOMException {
/* 1243 */     return this.document.appendChild(newChild);
/*      */   }
/*      */ 
/*      */   public boolean hasChildNodes() {
/* 1247 */     return this.document.hasChildNodes();
/*      */   }
/*      */   public Node cloneNode(boolean deep) {
/* 1250 */     return this.document.cloneNode(deep);
/*      */   }
/*      */ 
/*      */   public void normalize() {
/* 1254 */     this.document.normalize();
/*      */   }
/*      */ 
/*      */   public boolean isSupported(String feature, String version) {
/* 1258 */     return this.document.isSupported(feature, version);
/*      */   }
/*      */ 
/*      */   public String getNamespaceURI() {
/* 1262 */     return this.document.getNamespaceURI();
/*      */   }
/*      */ 
/*      */   public String getPrefix() {
/* 1266 */     return this.document.getPrefix();
/*      */   }
/*      */ 
/*      */   public void setPrefix(String prefix) throws DOMException {
/* 1270 */     this.document.setPrefix(prefix);
/*      */   }
/*      */   public String getLocalName() {
/* 1273 */     return this.document.getLocalName();
/*      */   }
/*      */ 
/*      */   public boolean hasAttributes() {
/* 1277 */     return this.document.hasAttributes();
/*      */   }
/*      */ 
/*      */   public boolean isBodyStream() {
/* 1281 */     return (this.currentForm == 2) || (this.currentForm == 5);
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.SOAPPart
 * JD-Core Version:    0.6.0
 */