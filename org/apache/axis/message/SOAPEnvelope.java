/*     */ package org.apache.axis.message;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.soap.Name;
/*     */ import javax.xml.soap.SOAPException;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.SOAPPart;
/*     */ import org.apache.axis.client.AxisClient;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.configuration.NullProvider;
/*     */ import org.apache.axis.encoding.DeserializationContext;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.schema.SchemaVersion;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.apache.axis.utils.Mapping;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.DOMException;
/*     */ import org.w3c.dom.Node;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class SOAPEnvelope extends MessageElement
/*     */   implements javax.xml.soap.SOAPEnvelope
/*     */ {
/*  53 */   protected static Log log = LogFactory.getLog(SOAPEnvelope.class.getName());
/*     */   private SOAPHeader header;
/*     */   private SOAPBody body;
/*  59 */   public Vector trailers = new Vector();
/*     */   private SOAPConstants soapConstants;
/*  61 */   private SchemaVersion schemaVersion = SchemaVersion.SCHEMA_2001;
/*     */   public String messageType;
/*     */   private boolean recorded;
/*     */ 
/*     */   public SOAPEnvelope()
/*     */   {
/*  74 */     this(true, SOAPConstants.SOAP11_CONSTANTS);
/*     */   }
/*     */ 
/*     */   public SOAPEnvelope(SOAPConstants soapConstants)
/*     */   {
/*  79 */     this(true, soapConstants);
/*     */   }
/*     */ 
/*     */   public SOAPEnvelope(SOAPConstants soapConstants, SchemaVersion schemaVersion)
/*     */   {
/*  85 */     this(true, soapConstants, schemaVersion);
/*     */   }
/*     */ 
/*     */   public SOAPEnvelope(boolean registerPrefixes, SOAPConstants soapConstants)
/*     */   {
/*  90 */     this(registerPrefixes, soapConstants, SchemaVersion.SCHEMA_2001);
/*     */   }
/*     */ 
/*     */   public SOAPEnvelope(boolean registerPrefixes, SOAPConstants soapConstants, SchemaVersion schemaVersion)
/*     */   {
/*  98 */     super("Envelope", "soapenv", soapConstants != null ? soapConstants.getEnvelopeURI() : Constants.DEFAULT_SOAP_VERSION.getEnvelopeURI());
/*     */ 
/* 102 */     if (soapConstants == null) {
/* 103 */       soapConstants = Constants.DEFAULT_SOAP_VERSION;
/*     */     }
/*     */ 
/* 106 */     this.soapConstants = soapConstants;
/* 107 */     this.schemaVersion = schemaVersion;
/* 108 */     this.header = new SOAPHeader(this, soapConstants);
/* 109 */     this.body = new SOAPBody(this, soapConstants);
/*     */ 
/* 111 */     if (registerPrefixes) {
/* 112 */       if (this.namespaces == null) {
/* 113 */         this.namespaces = new ArrayList();
/*     */       }
/* 115 */       this.namespaces.add(new Mapping(soapConstants.getEnvelopeURI(), "soapenv"));
/*     */ 
/* 117 */       this.namespaces.add(new Mapping(schemaVersion.getXsdURI(), "xsd"));
/*     */ 
/* 119 */       this.namespaces.add(new Mapping(schemaVersion.getXsiURI(), "xsi"));
/*     */     }
/*     */ 
/* 123 */     setDirty();
/*     */   }
/*     */ 
/*     */   public SOAPEnvelope(InputStream input) throws SAXException {
/* 127 */     InputSource is = new InputSource(input);
/*     */ 
/* 130 */     this.header = new SOAPHeader(this, Constants.DEFAULT_SOAP_VERSION);
/*     */ 
/* 132 */     DeserializationContext dser = null;
/* 133 */     AxisClient tmpEngine = new AxisClient(new NullProvider());
/* 134 */     MessageContext msgContext = new MessageContext(tmpEngine);
/* 135 */     dser = new DeserializationContext(is, msgContext, "request", this);
/*     */ 
/* 137 */     dser.parse();
/*     */   }
/*     */ 
/*     */   public String getMessageType()
/*     */   {
/* 146 */     return this.messageType;
/*     */   }
/*     */ 
/*     */   public void setMessageType(String messageType)
/*     */   {
/* 155 */     this.messageType = messageType;
/*     */   }
/*     */ 
/*     */   public Vector getBodyElements()
/*     */     throws AxisFault
/*     */   {
/* 165 */     if (this.body != null) {
/* 166 */       return this.body.getBodyElements();
/*     */     }
/* 168 */     return new Vector();
/*     */   }
/*     */ 
/*     */   public Vector getTrailers()
/*     */   {
/* 178 */     return this.trailers;
/*     */   }
/*     */ 
/*     */   public SOAPBodyElement getFirstBody()
/*     */     throws AxisFault
/*     */   {
/* 188 */     if (this.body == null) {
/* 189 */       return null;
/*     */     }
/* 191 */     return this.body.getFirstBody();
/*     */   }
/*     */ 
/*     */   public Vector getHeaders()
/*     */     throws AxisFault
/*     */   {
/* 202 */     if (this.header != null) {
/* 203 */       return this.header.getHeaders();
/*     */     }
/* 205 */     return new Vector();
/*     */   }
/*     */ 
/*     */   public Vector getHeadersByActor(ArrayList actors)
/*     */   {
/* 214 */     if (this.header != null) {
/* 215 */       return this.header.getHeadersByActor(actors);
/*     */     }
/* 217 */     return new Vector();
/*     */   }
/*     */ 
/*     */   public void addHeader(SOAPHeaderElement hdr)
/*     */   {
/* 227 */     if (this.header == null) {
/* 228 */       this.header = new SOAPHeader(this, this.soapConstants);
/*     */     }
/* 230 */     hdr.setEnvelope(this);
/* 231 */     this.header.addHeader(hdr);
/* 232 */     this._isDirty = true;
/*     */   }
/*     */ 
/*     */   public void addBodyElement(SOAPBodyElement element)
/*     */   {
/* 241 */     if (this.body == null) {
/* 242 */       this.body = new SOAPBody(this, this.soapConstants);
/*     */     }
/* 244 */     element.setEnvelope(this);
/* 245 */     this.body.addBodyElement(element);
/*     */ 
/* 247 */     this._isDirty = true;
/*     */   }
/*     */ 
/*     */   public void removeHeaders()
/*     */   {
/* 254 */     if (this.header != null) {
/* 255 */       removeChild(this.header);
/*     */     }
/* 257 */     this.header = null;
/*     */   }
/*     */ 
/*     */   public void setHeader(SOAPHeader hdr)
/*     */   {
/* 265 */     if (this.header != null) {
/* 266 */       removeChild(this.header);
/*     */     }
/* 268 */     this.header = hdr;
/*     */     try {
/* 270 */       this.header.setParentElement(this);
/*     */     }
/*     */     catch (SOAPException ex) {
/* 273 */       log.fatal(Messages.getMessage("exception00"), ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeHeader(SOAPHeaderElement hdr)
/*     */   {
/* 283 */     if (this.header != null) {
/* 284 */       this.header.removeHeader(hdr);
/* 285 */       this._isDirty = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeBody()
/*     */   {
/* 293 */     if (this.body != null) {
/* 294 */       removeChild(this.body);
/*     */     }
/* 296 */     this.body = null;
/*     */   }
/*     */ 
/*     */   public void setBody(SOAPBody body)
/*     */   {
/* 304 */     if (this.body != null) {
/* 305 */       removeChild(this.body);
/*     */     }
/* 307 */     this.body = body;
/*     */     try {
/* 309 */       body.setParentElement(this);
/*     */     }
/*     */     catch (SOAPException ex) {
/* 312 */       log.fatal(Messages.getMessage("exception00"), ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeBodyElement(SOAPBodyElement element)
/*     */   {
/* 322 */     if (this.body != null) {
/* 323 */       this.body.removeBodyElement(element);
/* 324 */       this._isDirty = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeTrailer(MessageElement element)
/*     */   {
/* 334 */     if (log.isDebugEnabled())
/* 335 */       log.debug(Messages.getMessage("removeTrailer00"));
/* 336 */     this.trailers.removeElement(element);
/* 337 */     this._isDirty = true;
/*     */   }
/*     */ 
/*     */   public void clearBody()
/*     */   {
/* 345 */     if (this.body != null) {
/* 346 */       this.body.clearBody();
/* 347 */       this._isDirty = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addTrailer(MessageElement element)
/*     */   {
/* 357 */     if (log.isDebugEnabled())
/* 358 */       log.debug(Messages.getMessage("removeTrailer00"));
/* 359 */     element.setEnvelope(this);
/* 360 */     this.trailers.addElement(element);
/* 361 */     this._isDirty = true;
/*     */   }
/*     */ 
/*     */   public SOAPHeaderElement getHeaderByName(String namespace, String localPart)
/*     */     throws AxisFault
/*     */   {
/* 372 */     return getHeaderByName(namespace, localPart, false);
/*     */   }
/*     */ 
/*     */   public SOAPHeaderElement getHeaderByName(String namespace, String localPart, boolean accessAllHeaders)
/*     */     throws AxisFault
/*     */   {
/* 384 */     if (this.header != null) {
/* 385 */       return this.header.getHeaderByName(namespace, localPart, accessAllHeaders);
/*     */     }
/*     */ 
/* 389 */     return null;
/*     */   }
/*     */ 
/*     */   public SOAPBodyElement getBodyByName(String namespace, String localPart)
/*     */     throws AxisFault
/*     */   {
/* 403 */     if (this.body == null) {
/* 404 */       return null;
/*     */     }
/* 406 */     return this.body.getBodyByName(namespace, localPart);
/*     */   }
/*     */ 
/*     */   public Enumeration getHeadersByName(String namespace, String localPart)
/*     */     throws AxisFault
/*     */   {
/* 420 */     return getHeadersByName(namespace, localPart, false);
/*     */   }
/*     */ 
/*     */   public Enumeration getHeadersByName(String namespace, String localPart, boolean accessAllHeaders)
/*     */     throws AxisFault
/*     */   {
/* 437 */     if (this.header != null) {
/* 438 */       return this.header.getHeadersByName(namespace, localPart, accessAllHeaders);
/*     */     }
/*     */ 
/* 442 */     return new Vector().elements();
/*     */   }
/*     */ 
/*     */   public void outputImpl(SerializationContext context)
/*     */     throws Exception
/*     */   {
/* 451 */     boolean oldPretty = context.getPretty();
/* 452 */     context.setPretty(true);
/*     */     Iterator i;
/* 455 */     if (this.namespaces != null) {
/* 456 */       for (i = this.namespaces.iterator(); i.hasNext(); ) {
/* 457 */         Mapping mapping = (Mapping)i.next();
/* 458 */         context.registerPrefixForURI(mapping.getPrefix(), mapping.getNamespaceURI());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 466 */     context.startElement(new QName(this.soapConstants.getEnvelopeURI(), "Envelope"), this.attributes);
/*     */ 
/* 471 */     Iterator i = getChildElements();
/* 472 */     while (i.hasNext()) {
/* 473 */       NodeImpl node = (NodeImpl)i.next();
/*     */ 
/* 475 */       if ((node instanceof SOAPHeader))
/* 476 */         this.header.outputImpl(context);
/* 477 */       else if ((node instanceof SOAPBody))
/* 478 */         this.body.outputImpl(context);
/* 479 */       else if ((node instanceof MessageElement))
/* 480 */         ((MessageElement)node).output(context);
/*     */       else {
/* 482 */         node.output(context);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 487 */     Enumeration enumeration = this.trailers.elements();
/* 488 */     while (enumeration.hasMoreElements()) {
/* 489 */       MessageElement element = (MessageElement)enumeration.nextElement();
/* 490 */       element.output(context);
/*     */     }
/*     */ 
/* 495 */     context.endElement();
/*     */ 
/* 497 */     context.setPretty(oldPretty);
/*     */   }
/*     */ 
/*     */   public SOAPConstants getSOAPConstants()
/*     */   {
/* 505 */     return this.soapConstants;
/*     */   }
/*     */ 
/*     */   public void setSoapConstants(SOAPConstants soapConstants)
/*     */   {
/* 513 */     this.soapConstants = soapConstants;
/*     */   }
/*     */ 
/*     */   public SchemaVersion getSchemaVersion()
/*     */   {
/* 521 */     return this.schemaVersion;
/*     */   }
/*     */ 
/*     */   public void setSchemaVersion(SchemaVersion schemaVersion)
/*     */   {
/* 529 */     this.schemaVersion = schemaVersion;
/*     */   }
/*     */ 
/*     */   public javax.xml.soap.SOAPBody addBody()
/*     */     throws SOAPException
/*     */   {
/* 538 */     if (this.body == null) {
/* 539 */       this.body = new SOAPBody(this, this.soapConstants);
/* 540 */       this._isDirty = true;
/* 541 */       this.body.setOwnerDocument(getOwnerDocument());
/* 542 */       return this.body;
/*     */     }
/* 544 */     throw new SOAPException(Messages.getMessage("bodyPresent"));
/*     */   }
/*     */ 
/*     */   public javax.xml.soap.SOAPHeader addHeader()
/*     */     throws SOAPException
/*     */   {
/* 554 */     if (this.header == null) {
/* 555 */       this.header = new SOAPHeader(this, this.soapConstants);
/* 556 */       this.header.setOwnerDocument(getOwnerDocument());
/* 557 */       return this.header;
/*     */     }
/* 559 */     throw new SOAPException(Messages.getMessage("headerPresent"));
/*     */   }
/*     */ 
/*     */   public Name createName(String localName)
/*     */     throws SOAPException
/*     */   {
/* 571 */     return new PrefixedQName(null, localName, null);
/*     */   }
/*     */ 
/*     */   public Name createName(String localName, String prefix, String uri)
/*     */     throws SOAPException
/*     */   {
/* 586 */     return new PrefixedQName(uri, localName, prefix);
/*     */   }
/*     */ 
/*     */   public javax.xml.soap.SOAPBody getBody()
/*     */     throws SOAPException
/*     */   {
/* 595 */     return this.body;
/*     */   }
/*     */ 
/*     */   public javax.xml.soap.SOAPHeader getHeader()
/*     */     throws SOAPException
/*     */   {
/* 604 */     return this.header;
/*     */   }
/*     */ 
/*     */   public void setSAAJEncodingCompliance(boolean comply) {
/* 608 */     this.body.setSAAJEncodingCompliance(comply);
/*     */   }
/*     */ 
/*     */   public Node removeChild(Node oldChild) throws DOMException {
/* 612 */     if (oldChild == this.header)
/* 613 */       this.header = null;
/* 614 */     else if (oldChild == this.body) {
/* 615 */       this.body = null;
/*     */     }
/* 617 */     return super.removeChild(oldChild);
/*     */   }
/*     */ 
/*     */   public Node cloneNode(boolean deep)
/*     */   {
/* 622 */     SOAPEnvelope envelope = (SOAPEnvelope)super.cloneNode(deep);
/*     */ 
/* 624 */     if (!deep)
/*     */     {
/* 626 */       envelope.body = null;
/* 627 */       envelope.header = null;
/*     */     }
/*     */ 
/* 630 */     return envelope;
/*     */   }
/*     */ 
/*     */   protected void childDeepCloned(NodeImpl oldNode, NodeImpl newNode)
/*     */   {
/* 635 */     if (oldNode == this.body)
/*     */     {
/* 637 */       this.body = ((SOAPBody)newNode);
/*     */       try
/*     */       {
/* 640 */         this.body.setParentElement(this);
/*     */       }
/*     */       catch (SOAPException ex) {
/* 643 */         log.fatal(Messages.getMessage("exception00"), ex);
/*     */       }
/*     */ 
/*     */     }
/* 647 */     else if (oldNode == this.header)
/*     */     {
/* 649 */       this.header = ((SOAPHeader)newNode);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setOwnerDocument(SOAPPart sp) {
/* 654 */     super.setOwnerDocument(sp);
/* 655 */     if (this.body != null) {
/* 656 */       this.body.setOwnerDocument(sp);
/* 657 */       setOwnerDocumentForChildren(this.body.children, sp);
/*     */     }
/* 659 */     if (this.header != null) {
/* 660 */       this.header.setOwnerDocument(sp);
/* 661 */       setOwnerDocumentForChildren(this.body.children, sp);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setOwnerDocumentForChildren(List children, SOAPPart sp) {
/* 666 */     if (children == null) {
/* 667 */       return;
/*     */     }
/* 669 */     int size = children.size();
/* 670 */     for (int i = 0; i < size; i++) {
/* 671 */       NodeImpl node = (NodeImpl)children.get(i);
/* 672 */       node.setOwnerDocument(sp);
/* 673 */       setOwnerDocumentForChildren(node.children, sp);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setRecorded(boolean recorded) {
/* 678 */     this.recorded = recorded;
/*     */   }
/*     */ 
/*     */   public boolean isRecorded() {
/* 682 */     return this.recorded;
/*     */   }
/*     */ 
/*     */   public void setDirty(boolean dirty) {
/* 686 */     if ((this.recorder != null) && (!this._isDirty) && (dirty) && (isRecorded())) {
/* 687 */       this.recorder.clear();
/* 688 */       this.recorder = null;
/*     */     }
/* 690 */     setDirty();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.SOAPEnvelope
 * JD-Core Version:    0.6.0
 */