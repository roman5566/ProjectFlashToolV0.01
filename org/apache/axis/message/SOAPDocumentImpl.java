/*     */ package org.apache.axis.message;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import javax.xml.soap.SOAPException;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.SOAPPart;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.apache.axis.utils.Mapping;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.w3c.dom.Attr;
/*     */ import org.w3c.dom.CDATASection;
/*     */ import org.w3c.dom.Comment;
/*     */ import org.w3c.dom.DOMException;
/*     */ import org.w3c.dom.DOMImplementation;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.DocumentFragment;
/*     */ import org.w3c.dom.DocumentType;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.EntityReference;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.w3c.dom.ProcessingInstruction;
/*     */ 
/*     */ public class SOAPDocumentImpl
/*     */   implements Document, Serializable
/*     */ {
/*  60 */   protected Document delegate = null;
/*  61 */   protected SOAPPart soapPart = null;
/*     */ 
/* 653 */   private String[] features = { "foo", "bar" };
/* 654 */   private String version = "version 2.0";
/*     */ 
/*     */   public SOAPDocumentImpl(SOAPPart sp)
/*     */   {
/*     */     try
/*     */     {
/*  70 */       this.delegate = XMLUtils.newDocument();
/*     */     }
/*     */     catch (ParserConfigurationException e) {
/*     */     }
/*  74 */     this.soapPart = sp;
/*     */   }
/*     */ 
/*     */   public DocumentType getDoctype()
/*     */   {
/*  83 */     return this.delegate.getDoctype();
/*     */   }
/*     */ 
/*     */   public DOMImplementation getImplementation() {
/*  87 */     return this.delegate.getImplementation();
/*     */   }
/*     */ 
/*     */   public Element getDocumentElement()
/*     */   {
/*  96 */     return this.soapPart.getDocumentElement();
/*     */   }
/*     */ 
/*     */   public Element createElement(String tagName)
/*     */     throws DOMException
/*     */   {
/* 112 */     int index = tagName.indexOf(":");
/*     */     String localname;
/*     */     String prefix;
/*     */     String localname;
/* 114 */     if (index < 0) {
/* 115 */       String prefix = "";
/* 116 */       localname = tagName;
/*     */     } else {
/* 118 */       prefix = tagName.substring(0, index);
/* 119 */       localname = tagName.substring(index + 1);
/*     */     }
/*     */     try
/*     */     {
/* 123 */       SOAPEnvelope soapenv = (SOAPEnvelope)this.soapPart.getEnvelope();
/*     */ 
/* 125 */       if (soapenv != null) {
/* 126 */         if (tagName.equalsIgnoreCase("Envelope"))
/* 127 */           new SOAPEnvelope();
/* 128 */         if (tagName.equalsIgnoreCase("Header"))
/* 129 */           return new SOAPHeader(soapenv, soapenv.getSOAPConstants());
/* 130 */         if (tagName.equalsIgnoreCase("Body"))
/* 131 */           return new SOAPBody(soapenv, soapenv.getSOAPConstants());
/* 132 */         if (tagName.equalsIgnoreCase("Fault"))
/* 133 */           return new SOAPEnvelope();
/* 134 */         if (tagName.equalsIgnoreCase("detail")) {
/* 135 */           return new SOAPFault(new AxisFault(tagName));
/*     */         }
/* 137 */         return new MessageElement("", prefix, localname);
/*     */       }
/*     */ 
/* 140 */       return new MessageElement("", prefix, localname);
/*     */     }
/*     */     catch (SOAPException se) {
/*     */     }
/* 144 */     throw new DOMException(11, "");
/*     */   }
/*     */ 
/*     */   public DocumentFragment createDocumentFragment()
/*     */   {
/* 156 */     return this.delegate.createDocumentFragment();
/*     */   }
/*     */ 
/*     */   public org.w3c.dom.Text createTextNode(String data)
/*     */   {
/* 166 */     Text me = new Text(this.delegate.createTextNode(data));
/*     */ 
/* 168 */     me.setOwnerDocument(this.soapPart);
/* 169 */     return me;
/*     */   }
/*     */ 
/*     */   public Comment createComment(String data)
/*     */   {
/* 181 */     return new CommentImpl(data);
/*     */   }
/*     */ 
/*     */   public CDATASection createCDATASection(String data)
/*     */     throws DOMException
/*     */   {
/* 196 */     return new CDATAImpl(data);
/*     */   }
/*     */ 
/*     */   public ProcessingInstruction createProcessingInstruction(String target, String data)
/*     */     throws DOMException
/*     */   {
/* 217 */     throw new UnsupportedOperationException("createProcessingInstruction");
/*     */   }
/*     */ 
/*     */   public Attr createAttribute(String name)
/*     */     throws DOMException
/*     */   {
/* 225 */     return this.delegate.createAttribute(name);
/*     */   }
/*     */ 
/*     */   public EntityReference createEntityReference(String name)
/*     */     throws DOMException
/*     */   {
/* 235 */     throw new UnsupportedOperationException("createEntityReference");
/*     */   }
/*     */ 
/*     */   public Node importNode(Node importedNode, boolean deep)
/*     */     throws DOMException
/*     */   {
/* 241 */     Node targetNode = null;
/*     */ 
/* 243 */     int type = importedNode.getNodeType();
/* 244 */     switch (type) {
/*     */     case 1:
/* 246 */       Element el = (Element)importedNode;
/* 247 */       if (deep) {
/* 248 */         targetNode = new SOAPBodyElement(el);
/*     */       }
/*     */       else
/*     */       {
/* 252 */         SOAPBodyElement target = new SOAPBodyElement();
/* 253 */         NamedNodeMap attrs = el.getAttributes();
/* 254 */         for (int i = 0; i < attrs.getLength(); i++) {
/* 255 */           Node att = attrs.item(i);
/* 256 */           if ((att.getNamespaceURI() != null) && (att.getPrefix() != null) && (att.getNamespaceURI().equals("http://www.w3.org/2000/xmlns/")) && (att.getPrefix().equals("xmlns")))
/*     */           {
/* 260 */             Mapping map = new Mapping(att.getNodeValue(), att.getLocalName());
/* 261 */             target.addMapping(map);
/*     */           }
/* 263 */           if (att.getLocalName() != null) {
/* 264 */             target.addAttribute(att.getPrefix(), att.getNamespaceURI(), att.getLocalName(), att.getNodeValue());
/*     */           }
/* 268 */           else if (att.getNodeName() != null) {
/* 269 */             target.addAttribute(att.getPrefix(), att.getNamespaceURI(), att.getNodeName(), att.getNodeValue());
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 276 */         if (el.getLocalName() == null)
/* 277 */           target.setName(el.getNodeName());
/*     */         else {
/* 279 */           target.setQName(new QName(el.getNamespaceURI(), el.getLocalName()));
/*     */         }
/* 281 */         targetNode = target;
/* 282 */       }break;
/*     */     case 2:
/* 285 */       if (importedNode.getLocalName() == null)
/* 286 */         targetNode = createAttribute(importedNode.getNodeName());
/*     */       else {
/* 288 */         targetNode = createAttributeNS(importedNode.getNamespaceURI(), importedNode.getLocalName());
/*     */       }
/*     */ 
/* 291 */       break;
/*     */     case 3:
/* 294 */       targetNode = createTextNode(importedNode.getNodeValue());
/* 295 */       break;
/*     */     case 4:
/* 298 */       targetNode = createCDATASection(importedNode.getNodeValue());
/* 299 */       break;
/*     */     case 8:
/* 302 */       targetNode = createComment(importedNode.getNodeValue());
/* 303 */       break;
/*     */     case 11:
/* 306 */       targetNode = createDocumentFragment();
/* 307 */       if (!deep) break;
/* 308 */       NodeList children = importedNode.getChildNodes();
/* 309 */       for (int i = 0; i < children.getLength(); i++)
/* 310 */         targetNode.appendChild(importNode(children.item(i), true));
/* 309 */       break;
/*     */     case 5:
/* 316 */       targetNode = createEntityReference(importedNode.getNodeName());
/* 317 */       break;
/*     */     case 7:
/* 320 */       ProcessingInstruction pi = (ProcessingInstruction)importedNode;
/* 321 */       targetNode = createProcessingInstruction(pi.getTarget(), pi.getData());
/* 322 */       break;
/*     */     case 6:
/* 326 */       throw new DOMException(9, "Entity nodes are not supported.");
/*     */     case 12:
/* 330 */       throw new DOMException(9, "Notation nodes are not supported.");
/*     */     case 10:
/* 333 */       throw new DOMException(9, "DocumentType nodes cannot be imported.");
/*     */     case 9:
/* 336 */       throw new DOMException(9, "Document nodes cannot be imported.");
/*     */     default:
/* 339 */       throw new DOMException(9, "Node type (" + type + ") cannot be imported.");
/*     */     }
/*     */ 
/* 342 */     return targetNode;
/*     */   }
/*     */ 
/*     */   public Element createElementNS(String namespaceURI, String qualifiedName)
/*     */     throws DOMException
/*     */   {
/* 355 */     SOAPConstants soapConstants = null;
/* 356 */     if ("http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceURI))
/* 357 */       soapConstants = SOAPConstants.SOAP11_CONSTANTS;
/* 358 */     else if ("http://www.w3.org/2003/05/soap-envelope".equals(namespaceURI)) {
/* 359 */       soapConstants = SOAPConstants.SOAP12_CONSTANTS;
/*     */     }
/*     */ 
/* 363 */     MessageElement me = null;
/* 364 */     if (soapConstants != null) {
/* 365 */       if (qualifiedName.equals("Envelope"))
/*     */       {
/* 367 */         me = new SOAPEnvelope(soapConstants);
/* 368 */       } else if (qualifiedName.equals("Header")) {
/* 369 */         me = new SOAPHeader(null, soapConstants);
/*     */       }
/* 371 */       else if (qualifiedName.equals("Body"))
/* 372 */         me = new SOAPBody(null, soapConstants);
/* 373 */       else if (qualifiedName.equals("Fault"))
/* 374 */         me = null;
/* 375 */       else if (qualifiedName.equals("detail"))
/*     */       {
/* 377 */         me = null;
/*     */       }
/* 379 */       else throw new DOMException(11, "No such Localname for SOAP URI");
/*     */ 
/* 384 */       return null;
/*     */     }
/*     */ 
/* 387 */     me = new MessageElement(namespaceURI, qualifiedName);
/*     */ 
/* 390 */     if (me != null) {
/* 391 */       me.setOwnerDocument(this.soapPart);
/*     */     }
/* 393 */     return me;
/*     */   }
/*     */ 
/*     */   public Attr createAttributeNS(String namespaceURI, String qualifiedName)
/*     */     throws DOMException
/*     */   {
/* 403 */     return this.delegate.createAttributeNS(namespaceURI, qualifiedName);
/*     */   }
/*     */ 
/*     */   public NodeList getElementsByTagNameNS(String namespaceURI, String localName)
/*     */   {
/*     */     try
/*     */     {
/* 415 */       NodeListImpl list = new NodeListImpl();
/* 416 */       if (this.soapPart != null) {
/* 417 */         SOAPEnvelope soapEnv = (SOAPEnvelope)this.soapPart.getEnvelope();
/*     */ 
/* 420 */         SOAPHeader header = (SOAPHeader)soapEnv.getHeader();
/*     */ 
/* 422 */         if (header != null) {
/* 423 */           list.addNodeList(header.getElementsByTagNameNS(namespaceURI, localName));
/*     */         }
/*     */ 
/* 427 */         SOAPBody body = (SOAPBody)soapEnv.getBody();
/*     */ 
/* 429 */         if (body != null) {
/* 430 */           list.addNodeList(body.getElementsByTagNameNS(namespaceURI, localName));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 435 */       return list; } catch (SOAPException se) {
/*     */     }
/* 437 */     throw new DOMException(11, "");
/*     */   }
/*     */ 
/*     */   public NodeList getElementsByTagName(String localName)
/*     */   {
/*     */     try
/*     */     {
/* 449 */       NodeListImpl list = new NodeListImpl();
/* 450 */       if (this.soapPart != null) {
/* 451 */         SOAPEnvelope soapEnv = (SOAPEnvelope)this.soapPart.getEnvelope();
/*     */ 
/* 454 */         SOAPHeader header = (SOAPHeader)soapEnv.getHeader();
/*     */ 
/* 456 */         if (header != null) {
/* 457 */           list.addNodeList(header.getElementsByTagName(localName));
/*     */         }
/* 459 */         SOAPBody body = (SOAPBody)soapEnv.getBody();
/*     */ 
/* 461 */         if (body != null) {
/* 462 */           list.addNodeList(body.getElementsByTagName(localName));
/*     */         }
/*     */       }
/* 465 */       return list; } catch (SOAPException se) {
/*     */     }
/* 467 */     throw new DOMException(11, "");
/*     */   }
/*     */ 
/*     */   public Element getElementById(String elementId)
/*     */   {
/* 485 */     return this.delegate.getElementById(elementId);
/*     */   }
/*     */ 
/*     */   public String getNodeName()
/*     */   {
/* 494 */     return null;
/*     */   }
/*     */ 
/*     */   public String getNodeValue() throws DOMException {
/* 498 */     throw new DOMException(6, "Cannot use TextNode.get in " + this);
/*     */   }
/*     */ 
/*     */   public void setNodeValue(String nodeValue)
/*     */     throws DOMException
/*     */   {
/* 504 */     throw new DOMException(6, "Cannot use TextNode.set in " + this);
/*     */   }
/*     */ 
/*     */   public short getNodeType()
/*     */   {
/* 515 */     return 9;
/*     */   }
/*     */ 
/*     */   public Node getParentNode() {
/* 519 */     return null;
/*     */   }
/*     */ 
/*     */   public NodeList getChildNodes() {
/*     */     try {
/* 524 */       if (this.soapPart != null) {
/* 525 */         NodeListImpl children = new NodeListImpl();
/* 526 */         children.addNode(this.soapPart.getEnvelope());
/* 527 */         return children;
/*     */       }
/* 529 */       return NodeListImpl.EMPTY_NODELIST;
/*     */     } catch (SOAPException se) {
/*     */     }
/* 532 */     throw new DOMException(11, "");
/*     */   }
/*     */ 
/*     */   public Node getFirstChild()
/*     */   {
/*     */     try
/*     */     {
/* 544 */       if (this.soapPart != null) {
/* 545 */         return (SOAPEnvelope)this.soapPart.getEnvelope();
/*     */       }
/*     */ 
/* 548 */       return null; } catch (SOAPException se) {
/*     */     }
/* 550 */     throw new DOMException(11, "");
/*     */   }
/*     */ 
/*     */   public Node getLastChild()
/*     */   {
/*     */     try
/*     */     {
/* 560 */       if (this.soapPart != null) {
/* 561 */         return (SOAPEnvelope)this.soapPart.getEnvelope();
/*     */       }
/*     */ 
/* 564 */       return null; } catch (SOAPException se) {
/*     */     }
/* 566 */     throw new DOMException(11, "");
/*     */   }
/*     */ 
/*     */   public Node getPreviousSibling()
/*     */   {
/* 572 */     return null;
/*     */   }
/*     */ 
/*     */   public Node getNextSibling() {
/* 576 */     return null;
/*     */   }
/*     */ 
/*     */   public NamedNodeMap getAttributes() {
/* 580 */     return null;
/*     */   }
/*     */ 
/*     */   public Document getOwnerDocument()
/*     */   {
/* 588 */     return null;
/*     */   }
/*     */ 
/*     */   public Node insertBefore(Node newChild, Node refChild)
/*     */     throws DOMException
/*     */   {
/* 595 */     throw new DOMException(9, "");
/*     */   }
/*     */ 
/*     */   public Node replaceChild(Node newChild, Node oldChild) throws DOMException
/*     */   {
/* 600 */     throw new DOMException(9, "");
/*     */   }
/*     */ 
/*     */   public Node removeChild(Node oldChild) throws DOMException
/*     */   {
/*     */     try {
/* 606 */       if (this.soapPart != null) {
/* 607 */         Node envNode = this.soapPart.getEnvelope();
/* 608 */         if (envNode.equals(oldChild)) {
/* 609 */           return envNode;
/*     */         }
/*     */       }
/* 612 */       throw new DOMException(9, ""); } catch (SOAPException se) {
/*     */     }
/* 614 */     throw new DOMException(11, "");
/*     */   }
/*     */ 
/*     */   public Node appendChild(Node newChild) throws DOMException
/*     */   {
/* 619 */     throw new DOMException(9, "");
/*     */   }
/*     */ 
/*     */   public boolean hasChildNodes()
/*     */   {
/*     */     try
/*     */     {
/* 626 */       return (this.soapPart != null) && 
/* 625 */         (this.soapPart.getEnvelope() != null);
/*     */     }
/*     */     catch (SOAPException se)
/*     */     {
/*     */     }
/*     */ 
/* 631 */     throw new DOMException(11, "");
/*     */   }
/*     */ 
/*     */   public Node cloneNode(boolean deep)
/*     */   {
/* 641 */     throw new DOMException(9, "");
/*     */   }
/*     */ 
/*     */   public void normalize()
/*     */   {
/* 649 */     throw new DOMException(9, "");
/*     */   }
/*     */ 
/*     */   public boolean isSupported(String feature, String version)
/*     */   {
/* 658 */     return version.equalsIgnoreCase(version);
/*     */   }
/*     */ 
/*     */   public String getPrefix()
/*     */   {
/* 664 */     throw new DOMException(9, "");
/*     */   }
/*     */   public void setPrefix(String prefix) {
/* 667 */     throw new DOMException(9, "");
/*     */   }
/*     */ 
/*     */   public String getNamespaceURI() {
/* 671 */     throw new DOMException(9, "");
/*     */   }
/*     */   public void setNamespaceURI(String nsURI) {
/* 674 */     throw new DOMException(9, "");
/*     */   }
/*     */ 
/*     */   public String getLocalName() {
/* 678 */     throw new DOMException(9, "");
/*     */   }
/*     */ 
/*     */   public boolean hasAttributes() {
/* 682 */     throw new DOMException(9, "");
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.SOAPDocumentImpl
 * JD-Core Version:    0.6.0
 */