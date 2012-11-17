/*      */ package org.apache.axis.message;
/*      */ 
/*      */ import java.io.Reader;
/*      */ import java.io.Serializable;
/*      */ import java.io.StringReader;
/*      */ import java.io.StringWriter;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Vector;
/*      */ import javax.xml.namespace.QName;
/*      */ import javax.xml.rpc.encoding.TypeMapping;
/*      */ import javax.xml.soap.Name;
/*      */ import javax.xml.soap.SOAPElement;
/*      */ import javax.xml.soap.SOAPException;
/*      */ import org.apache.axis.AxisFault;
/*      */ import org.apache.axis.Constants;
/*      */ import org.apache.axis.MessageContext;
/*      */ import org.apache.axis.components.logger.LogFactory;
/*      */ import org.apache.axis.constants.Style;
/*      */ import org.apache.axis.encoding.DeserializationContext;
/*      */ import org.apache.axis.encoding.Deserializer;
/*      */ import org.apache.axis.encoding.SerializationContext;
/*      */ import org.apache.axis.encoding.TypeMappingRegistry;
/*      */ import org.apache.axis.soap.SOAPConstants;
/*      */ import org.apache.axis.utils.Mapping;
/*      */ import org.apache.axis.utils.Messages;
/*      */ import org.apache.axis.utils.XMLUtils;
/*      */ import org.apache.commons.logging.Log;
/*      */ import org.w3c.dom.Attr;
/*      */ import org.w3c.dom.CDATASection;
/*      */ import org.w3c.dom.CharacterData;
/*      */ import org.w3c.dom.Comment;
/*      */ import org.w3c.dom.DOMException;
/*      */ import org.w3c.dom.Document;
/*      */ import org.w3c.dom.Element;
/*      */ import org.w3c.dom.NamedNodeMap;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.NodeList;
/*      */ import org.xml.sax.Attributes;
/*      */ import org.xml.sax.ContentHandler;
/*      */ import org.xml.sax.InputSource;
/*      */ import org.xml.sax.SAXException;
/*      */ import org.xml.sax.helpers.AttributesImpl;
/*      */ 
/*      */ public class MessageElement extends NodeImpl
/*      */   implements SOAPElement, Serializable, NodeList, Cloneable
/*      */ {
/*   78 */   protected static Log log = LogFactory.getLog(MessageElement.class.getName());
/*      */ 
/*   81 */   private static final Mapping enc11Mapping = new Mapping("http://schemas.xmlsoap.org/soap/encoding/", "SOAP-ENC");
/*      */ 
/*   85 */   private static final Mapping enc12Mapping = new Mapping("http://www.w3.org/2003/05/soap-encoding", "SOAP-ENC");
/*      */   protected String id;
/*      */   protected String href;
/*   91 */   protected boolean _isRoot = true;
/*   92 */   protected SOAPEnvelope message = null;
/*      */   protected transient DeserializationContext context;
/*   96 */   protected transient QName typeQName = null;
/*      */ 
/*   98 */   protected Vector qNameAttrs = null;
/*      */ 
/*  101 */   protected transient SAX2EventRecorder recorder = null;
/*  102 */   protected int startEventIndex = 0;
/*  103 */   protected int startContentsIndex = 0;
/*  104 */   protected int endEventIndex = -1;
/*      */ 
/*  106 */   public ArrayList namespaces = null;
/*      */ 
/*  109 */   protected String encodingStyle = null;
/*      */ 
/*  112 */   private Object objectValue = null;
/*      */   protected Deserializer fixupDeserializer;
/*      */ 
/*      */   public MessageElement()
/*      */   {
/*      */   }
/*      */ 
/*      */   public MessageElement(String namespace, String localPart)
/*      */   {
/*  127 */     this.namespaceURI = namespace;
/*  128 */     this.name = localPart;
/*      */   }
/*      */ 
/*      */   public MessageElement(String localPart, String prefix, String namespace)
/*      */   {
/*  139 */     this.namespaceURI = namespace;
/*  140 */     this.name = localPart;
/*  141 */     this.prefix = prefix;
/*  142 */     addMapping(new Mapping(namespace, prefix));
/*      */   }
/*      */ 
/*      */   public MessageElement(Name eltName)
/*      */   {
/*  152 */     this(eltName.getLocalName(), eltName.getPrefix(), eltName.getURI());
/*      */   }
/*      */ 
/*      */   public MessageElement(String namespace, String localPart, Object value)
/*      */   {
/*  164 */     this(namespace, localPart);
/*  165 */     this.objectValue = value;
/*      */   }
/*      */ 
/*      */   public MessageElement(QName name)
/*      */   {
/*  174 */     this(name.getNamespaceURI(), name.getLocalPart());
/*      */   }
/*      */ 
/*      */   public MessageElement(QName name, Object value)
/*      */   {
/*  185 */     this(name.getNamespaceURI(), name.getLocalPart());
/*  186 */     this.objectValue = value;
/*      */   }
/*      */ 
/*      */   public MessageElement(Element elem)
/*      */   {
/*  195 */     this.namespaceURI = elem.getNamespaceURI();
/*  196 */     this.name = elem.getLocalName();
/*  197 */     copyNode(elem);
/*      */   }
/*      */ 
/*      */   public MessageElement(CharacterData text)
/*      */   {
/*  206 */     this.textRep = text;
/*  207 */     this.namespaceURI = text.getNamespaceURI();
/*  208 */     this.name = text.getLocalName();
/*      */   }
/*      */ 
/*      */   public MessageElement(String namespace, String localPart, String prefix, Attributes attributes, DeserializationContext context)
/*      */     throws AxisFault
/*      */   {
/*  238 */     if (log.isDebugEnabled()) {
/*  239 */       log.debug(Messages.getMessage("newElem00", super.toString(), "{" + prefix + "}" + localPart));
/*      */ 
/*  241 */       for (int i = 0; (attributes != null) && (i < attributes.getLength()); i++) {
/*  242 */         log.debug("  " + attributes.getQName(i) + " = '" + attributes.getValue(i) + "'");
/*      */       }
/*      */     }
/*  245 */     this.namespaceURI = namespace;
/*  246 */     this.name = localPart;
/*  247 */     this.prefix = prefix;
/*      */ 
/*  249 */     this.context = context;
/*  250 */     this.startEventIndex = context.getStartOfMappingsPos();
/*      */ 
/*  252 */     setNSMappings(context.getCurrentNSMappings());
/*      */ 
/*  254 */     this.recorder = context.getRecorder();
/*      */ 
/*  256 */     if ((attributes != null) && (attributes.getLength() > 0)) {
/*  257 */       this.attributes = attributes;
/*      */ 
/*  259 */       this.typeQName = context.getTypeFromAttributes(namespace, localPart, attributes);
/*      */ 
/*  263 */       String rootVal = attributes.getValue(Constants.URI_DEFAULT_SOAP_ENC, "root");
/*      */ 
/*  265 */       if (rootVal != null) {
/*  266 */         this._isRoot = "1".equals(rootVal);
/*      */       }
/*      */ 
/*  269 */       this.id = attributes.getValue("id");
/*      */ 
/*  271 */       if (this.id != null) {
/*  272 */         context.registerElementByID(this.id, this);
/*  273 */         if (this.recorder == null) {
/*  274 */           this.recorder = new SAX2EventRecorder();
/*  275 */           context.setRecorder(this.recorder);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  281 */       MessageContext mc = context.getMessageContext();
/*  282 */       SOAPConstants sc = mc != null ? mc.getSOAPConstants() : SOAPConstants.SOAP11_CONSTANTS;
/*      */ 
/*  286 */       this.href = attributes.getValue(sc.getAttrHref());
/*      */ 
/*  289 */       if (attributes.getValue(Constants.URI_DEFAULT_SOAP_ENC, "arrayType") != null) {
/*  290 */         this.typeQName = Constants.SOAP_ARRAY;
/*      */       }
/*      */ 
/*  294 */       this.encodingStyle = attributes.getValue(sc.getEncodingURI(), "encodingStyle");
/*      */ 
/*  299 */       if ("http://www.w3.org/2003/05/soap-envelope/encoding/none".equals(this.encodingStyle)) {
/*  300 */         this.encodingStyle = null;
/*      */       }
/*      */ 
/*  308 */       if ((this.encodingStyle != null) && (sc.equals(SOAPConstants.SOAP12_CONSTANTS)) && (mc.getOperationStyle() != Style.MESSAGE))
/*      */       {
/*  311 */         TypeMapping tm = mc.getTypeMappingRegistry().getTypeMapping(this.encodingStyle);
/*      */ 
/*  313 */         if ((tm == null) || (tm.equals(mc.getTypeMappingRegistry().getDefaultTypeMapping())))
/*      */         {
/*  316 */           AxisFault badEncodingFault = new AxisFault(Constants.FAULT_SOAP12_DATAENCODINGUNKNOWN, "bad encoding style", null, null);
/*      */ 
/*  319 */           throw badEncodingFault;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public DeserializationContext getDeserializationContext()
/*      */   {
/*  333 */     return this.context;
/*      */   }
/*      */ 
/*      */   public void setFixupDeserializer(Deserializer dser)
/*      */   {
/*  343 */     this.fixupDeserializer = dser;
/*      */   }
/*      */ 
/*      */   public Deserializer getFixupDeserializer()
/*      */   {
/*  348 */     return this.fixupDeserializer;
/*      */   }
/*      */ 
/*      */   public void setEndIndex(int endIndex)
/*      */   {
/*  357 */     this.endEventIndex = endIndex;
/*      */   }
/*      */ 
/*      */   public boolean isRoot()
/*      */   {
/*  365 */     return this._isRoot;
/*      */   }
/*      */ 
/*      */   public String getID()
/*      */   {
/*  371 */     return this.id;
/*      */   }
/*      */ 
/*      */   public String getHref()
/*      */   {
/*  377 */     return this.href;
/*      */   }
/*      */ 
/*      */   public Attributes getAttributesEx()
/*      */   {
/*  384 */     return this.attributes;
/*      */   }
/*      */ 
/*      */   public Node cloneNode(boolean deep)
/*      */   {
/*      */     try
/*      */     {
/*  414 */       MessageElement clonedSelf = (MessageElement)cloning();
/*      */ 
/*  416 */       if ((deep) && 
/*  417 */         (this.children != null)) {
/*  418 */         for (int i = 0; i < this.children.size(); i++) {
/*  419 */           NodeImpl child = (NodeImpl)this.children.get(i);
/*  420 */           if (child != null) {
/*  421 */             NodeImpl clonedChild = (NodeImpl)child.cloneNode(deep);
/*  422 */             clonedChild.setParent(clonedSelf);
/*  423 */             clonedChild.setOwnerDocument(getOwnerDocument());
/*      */ 
/*  425 */             clonedSelf.childDeepCloned(child, clonedChild);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  430 */       return clonedSelf;
/*      */     } catch (Exception e) {
/*      */     }
/*  433 */     return null;
/*      */   }
/*      */ 
/*      */   protected void childDeepCloned(NodeImpl oldNode, NodeImpl newNode)
/*      */   {
/*      */   }
/*      */ 
/*      */   protected Object cloning()
/*      */     throws CloneNotSupportedException
/*      */   {
/*      */     try
/*      */     {
/*  477 */       MessageElement clonedME = null;
/*  478 */       clonedME = (MessageElement)clone();
/*      */ 
/*  480 */       clonedME.setName(this.name);
/*  481 */       clonedME.setNamespaceURI(this.namespaceURI);
/*  482 */       clonedME.setPrefix(this.prefix);
/*      */ 
/*  485 */       clonedME.setAllAttributes(new AttributesImpl(this.attributes));
/*      */ 
/*  488 */       clonedME.namespaces = new ArrayList();
/*  489 */       if (this.namespaces != null) {
/*  490 */         for (int i = 0; i < this.namespaces.size(); i++)
/*      */         {
/*  492 */           Mapping namespace = (Mapping)this.namespaces.get(i);
/*  493 */           clonedME.addNamespaceDeclaration(namespace.getPrefix(), namespace.getNamespaceURI());
/*      */         }
/*      */       }
/*  496 */       clonedME.children = new ArrayList();
/*      */ 
/*  499 */       clonedME.parent = null;
/*      */ 
/*  501 */       clonedME.setDirty(this._isDirty);
/*  502 */       if (this.encodingStyle != null) {
/*  503 */         clonedME.setEncodingStyle(this.encodingStyle);
/*      */       }
/*  505 */       return clonedME; } catch (Exception ex) {
/*      */     }
/*  507 */     return null;
/*      */   }
/*      */ 
/*      */   public void setAllAttributes(Attributes attrs)
/*      */   {
/*  517 */     this.attributes = attrs;
/*      */   }
/*      */ 
/*      */   public void detachAllChildren()
/*      */   {
/*  525 */     removeContents();
/*      */   }
/*      */ 
/*      */   public Attributes getCompleteAttributes()
/*      */   {
/*  535 */     if (this.namespaces == null) {
/*  536 */       return this.attributes;
/*      */     }
/*      */ 
/*  539 */     AttributesImpl attrs = null;
/*  540 */     if (this.attributes == NullAttributes.singleton)
/*  541 */       attrs = new AttributesImpl();
/*      */     else {
/*  543 */       attrs = new AttributesImpl(this.attributes);
/*      */     }
/*      */ 
/*  546 */     for (Iterator iterator = this.namespaces.iterator(); iterator.hasNext(); ) {
/*  547 */       Mapping mapping = (Mapping)iterator.next();
/*  548 */       String prefix = mapping.getPrefix();
/*  549 */       String nsURI = mapping.getNamespaceURI();
/*  550 */       attrs.addAttribute("http://www.w3.org/2000/xmlns/", prefix, "xmlns:" + prefix, nsURI, "CDATA");
/*      */     }
/*      */ 
/*  553 */     return attrs;
/*      */   }
/*      */ 
/*      */   public String getName()
/*      */   {
/*  561 */     return this.name;
/*      */   }
/*      */ 
/*      */   public void setName(String name)
/*      */   {
/*  569 */     this.name = name;
/*      */   }
/*      */ 
/*      */   public QName getQName()
/*      */   {
/*  577 */     return new QName(this.namespaceURI, this.name);
/*      */   }
/*      */ 
/*      */   public void setQName(QName qName)
/*      */   {
/*  585 */     this.name = qName.getLocalPart();
/*  586 */     this.namespaceURI = qName.getNamespaceURI();
/*      */   }
/*      */ 
/*      */   public void setNamespaceURI(String nsURI)
/*      */   {
/*  594 */     this.namespaceURI = nsURI;
/*      */   }
/*      */ 
/*      */   public QName getType()
/*      */   {
/*  605 */     if ((this.typeQName == null) && (this.href != null) && (this.context != null)) {
/*  606 */       MessageElement referent = this.context.getElementByID(this.href);
/*  607 */       if (referent != null) {
/*  608 */         this.typeQName = referent.getType();
/*      */       }
/*      */     }
/*  611 */     return this.typeQName;
/*      */   }
/*      */ 
/*      */   public void setType(QName qname)
/*      */   {
/*  619 */     this.typeQName = qname;
/*      */   }
/*      */ 
/*      */   public SAX2EventRecorder getRecorder()
/*      */   {
/*  627 */     return this.recorder;
/*      */   }
/*      */ 
/*      */   public void setRecorder(SAX2EventRecorder rec)
/*      */   {
/*  635 */     this.recorder = rec;
/*      */   }
/*      */ 
/*      */   public String getEncodingStyle()
/*      */   {
/*  645 */     if (this.encodingStyle == null) {
/*  646 */       if (this.parent == null) {
/*  647 */         return "";
/*      */       }
/*  649 */       return ((MessageElement)this.parent).getEncodingStyle();
/*      */     }
/*  651 */     return this.encodingStyle;
/*      */   }
/*      */ 
/*      */   public void removeContents()
/*      */   {
/*  660 */     if (this.children != null) {
/*  661 */       for (int i = 0; i < this.children.size(); i++) {
/*      */         try {
/*  663 */           ((NodeImpl)this.children.get(i)).setParent(null);
/*      */         } catch (SOAPException e) {
/*  665 */           log.debug("ignoring", e);
/*      */         }
/*      */       }
/*      */ 
/*  669 */       this.children.clear();
/*  670 */       setDirty();
/*      */     }
/*      */   }
/*      */ 
/*      */   public Iterator getVisibleNamespacePrefixes()
/*      */   {
/*  680 */     Vector prefixes = new Vector();
/*      */ 
/*  683 */     if (this.parent != null) {
/*  684 */       Iterator parentsPrefixes = ((MessageElement)this.parent).getVisibleNamespacePrefixes();
/*  685 */       if (parentsPrefixes != null) {
/*  686 */         while (parentsPrefixes.hasNext()) {
/*  687 */           prefixes.add(parentsPrefixes.next());
/*      */         }
/*      */       }
/*      */     }
/*  691 */     Iterator mine = getNamespacePrefixes();
/*  692 */     if (mine != null) {
/*  693 */       while (mine.hasNext()) {
/*  694 */         prefixes.add(mine.next());
/*      */       }
/*      */     }
/*  697 */     return prefixes.iterator();
/*      */   }
/*      */ 
/*      */   public void setEncodingStyle(String encodingStyle)
/*      */     throws SOAPException
/*      */   {
/*  713 */     if (encodingStyle == null) {
/*  714 */       encodingStyle = "";
/*      */     }
/*      */ 
/*  717 */     this.encodingStyle = encodingStyle;
/*      */ 
/*  721 */     if (encodingStyle.equals("http://schemas.xmlsoap.org/soap/encoding/"))
/*  722 */       addMapping(enc11Mapping);
/*  723 */     else if (encodingStyle.equals("http://www.w3.org/2003/05/soap-encoding"))
/*  724 */       addMapping(enc12Mapping);
/*      */   }
/*      */ 
/*      */   public void addChild(MessageElement el)
/*      */     throws SOAPException
/*      */   {
/*  734 */     if (this.objectValue != null) {
/*  735 */       IllegalStateException exc = new IllegalStateException(Messages.getMessage("valuePresent"));
/*      */ 
/*  737 */       log.error(Messages.getMessage("valuePresent"), exc);
/*  738 */       throw exc;
/*      */     }
/*  740 */     initializeChildren();
/*  741 */     this.children.add(el);
/*  742 */     el.parent = this;
/*      */   }
/*      */ 
/*      */   public List getChildren()
/*      */   {
/*  751 */     return this.children;
/*      */   }
/*      */ 
/*      */   public void setContentsIndex(int index)
/*      */   {
/*  761 */     this.startContentsIndex = index;
/*      */   }
/*      */ 
/*      */   public void setNSMappings(ArrayList namespaces)
/*      */   {
/*  770 */     this.namespaces = namespaces;
/*      */   }
/*      */ 
/*      */   public String getPrefix(String searchNamespaceURI)
/*      */   {
/*  779 */     if ((searchNamespaceURI == null) || ("".equals(searchNamespaceURI))) {
/*  780 */       return null;
/*      */     }
/*  782 */     if ((this.href != null) && (getRealElement() != null)) {
/*  783 */       return getRealElement().getPrefix(searchNamespaceURI);
/*      */     }
/*      */ 
/*  786 */     for (int i = 0; (this.namespaces != null) && (i < this.namespaces.size()); i++) {
/*  787 */       Mapping map = (Mapping)this.namespaces.get(i);
/*  788 */       if (map.getNamespaceURI().equals(searchNamespaceURI)) {
/*  789 */         return map.getPrefix();
/*      */       }
/*      */     }
/*      */ 
/*  793 */     if (this.parent != null) {
/*  794 */       return ((MessageElement)this.parent).getPrefix(searchNamespaceURI);
/*      */     }
/*      */ 
/*  797 */     return null;
/*      */   }
/*      */ 
/*      */   public String getNamespaceURI(String searchPrefix)
/*      */   {
/*  807 */     if (searchPrefix == null) {
/*  808 */       searchPrefix = "";
/*      */     }
/*      */ 
/*  811 */     if ((this.href != null) && (getRealElement() != null)) {
/*  812 */       return getRealElement().getNamespaceURI(searchPrefix);
/*      */     }
/*      */ 
/*  815 */     for (int i = 0; (this.namespaces != null) && (i < this.namespaces.size()); i++) {
/*  816 */       Mapping map = (Mapping)this.namespaces.get(i);
/*  817 */       if (map.getPrefix().equals(searchPrefix)) {
/*  818 */         return map.getNamespaceURI();
/*      */       }
/*      */     }
/*      */ 
/*  822 */     if (this.parent != null) {
/*  823 */       return ((MessageElement)this.parent).getNamespaceURI(searchPrefix);
/*      */     }
/*      */ 
/*  826 */     if (log.isDebugEnabled()) {
/*  827 */       log.debug(Messages.getMessage("noPrefix00", "" + this, searchPrefix));
/*      */     }
/*      */ 
/*  830 */     return null;
/*      */   }
/*      */ 
/*      */   public Object getObjectValue()
/*      */   {
/*  838 */     Object obj = null;
/*      */     try {
/*  840 */       obj = getObjectValue(null);
/*      */     } catch (Exception e) {
/*  842 */       log.debug("getValue()", e);
/*      */     }
/*  844 */     return obj;
/*      */   }
/*      */ 
/*      */   public Object getObjectValue(Class cls)
/*      */     throws Exception
/*      */   {
/*  853 */     if (this.objectValue == null) {
/*  854 */       this.objectValue = getValueAsType(getType(), cls);
/*      */     }
/*  856 */     return this.objectValue;
/*      */   }
/*      */ 
/*      */   public void setObjectValue(Object newValue)
/*      */     throws SOAPException
/*      */   {
/*  870 */     if ((this.children != null) && (!this.children.isEmpty())) {
/*  871 */       SOAPException exc = new SOAPException(Messages.getMessage("childPresent"));
/*  872 */       log.error(Messages.getMessage("childPresent"), exc);
/*  873 */       throw exc;
/*      */     }
/*  875 */     if (this.textRep != null) {
/*  876 */       SOAPException exc = new SOAPException(Messages.getMessage("xmlPresent"));
/*  877 */       log.error(Messages.getMessage("xmlPresent"), exc);
/*  878 */       throw exc;
/*      */     }
/*  880 */     this.objectValue = newValue;
/*      */   }
/*      */ 
/*      */   public Object getValueAsType(QName type) throws Exception
/*      */   {
/*  885 */     return getValueAsType(type, null);
/*      */   }
/*      */ 
/*      */   public Object getValueAsType(QName type, Class cls)
/*      */     throws Exception
/*      */   {
/*  899 */     if (this.context == null) {
/*  900 */       throw new Exception(Messages.getMessage("noContext00"));
/*      */     }
/*      */ 
/*  903 */     Deserializer dser = null;
/*  904 */     if (cls == null)
/*  905 */       dser = this.context.getDeserializerForType(type);
/*      */     else {
/*  907 */       dser = this.context.getDeserializerForClass(cls);
/*      */     }
/*  909 */     if (dser == null) {
/*  910 */       throw new Exception(Messages.getMessage("noDeser00", "" + type));
/*      */     }
/*      */ 
/*  913 */     boolean oldVal = this.context.isDoneParsing();
/*  914 */     this.context.deserializing(true);
/*  915 */     this.context.pushElementHandler(new EnvelopeHandler((SOAPHandler)dser));
/*      */ 
/*  917 */     publishToHandler(this.context);
/*      */ 
/*  919 */     this.context.deserializing(oldVal);
/*      */ 
/*  921 */     return dser.getValue();
/*      */   }
/*      */ 
/*      */   public void addAttribute(String namespace, String localName, QName value)
/*      */   {
/*  943 */     if (this.qNameAttrs == null) {
/*  944 */       this.qNameAttrs = new Vector();
/*      */     }
/*      */ 
/*  947 */     QNameAttr attr = new QNameAttr();
/*  948 */     attr.name = new QName(namespace, localName);
/*  949 */     attr.value = value;
/*      */ 
/*  951 */     this.qNameAttrs.addElement(attr);
/*      */   }
/*      */ 
/*      */   public void addAttribute(String namespace, String localName, String value)
/*      */   {
/*  965 */     AttributesImpl attributes = makeAttributesEditable();
/*  966 */     attributes.addAttribute(namespace, localName, "", "CDATA", value);
/*      */   }
/*      */ 
/*      */   public void addAttribute(String attrPrefix, String namespace, String localName, String value)
/*      */   {
/*  982 */     AttributesImpl attributes = makeAttributesEditable();
/*  983 */     String attrName = localName;
/*  984 */     if ((attrPrefix != null) && (attrPrefix.length() > 0)) {
/*  985 */       attrName = attrPrefix + ":" + localName;
/*      */     }
/*  987 */     attributes.addAttribute(namespace, localName, attrName, "CDATA", value);
/*      */   }
/*      */ 
/*      */   public void setAttribute(String namespace, String localName, String value)
/*      */   {
/*  999 */     AttributesImpl attributes = makeAttributesEditable();
/*      */ 
/* 1001 */     int idx = attributes.getIndex(namespace, localName);
/* 1002 */     if (idx > -1)
/*      */     {
/* 1004 */       if (value != null)
/* 1005 */         attributes.setValue(idx, value);
/*      */       else {
/* 1007 */         attributes.removeAttribute(idx);
/*      */       }
/* 1009 */       return;
/*      */     }
/*      */ 
/* 1012 */     addAttribute(namespace, localName, value);
/*      */   }
/*      */ 
/*      */   public String getAttributeValue(String localName)
/*      */   {
/* 1022 */     if (this.attributes == null) {
/* 1023 */       return null;
/*      */     }
/* 1025 */     return this.attributes.getValue(localName);
/*      */   }
/*      */ 
/*      */   public void setEnvelope(SOAPEnvelope env)
/*      */   {
/* 1034 */     env.setDirty();
/* 1035 */     this.message = env;
/*      */   }
/*      */ 
/*      */   public SOAPEnvelope getEnvelope()
/*      */   {
/* 1044 */     return this.message;
/*      */   }
/*      */ 
/*      */   public MessageElement getRealElement()
/*      */   {
/* 1054 */     if (this.href == null) {
/* 1055 */       return this;
/*      */     }
/*      */ 
/* 1058 */     Object obj = this.context.getObjectByRef(this.href);
/* 1059 */     if (obj == null) {
/* 1060 */       return null;
/*      */     }
/*      */ 
/* 1063 */     if (!(obj instanceof MessageElement)) {
/* 1064 */       return null;
/*      */     }
/*      */ 
/* 1067 */     return (MessageElement)obj;
/*      */   }
/*      */ 
/*      */   public Document getAsDocument()
/*      */     throws Exception
/*      */   {
/* 1079 */     String elementString = getAsString();
/*      */ 
/* 1081 */     Reader reader = new StringReader(elementString);
/* 1082 */     Document doc = XMLUtils.newDocument(new InputSource(reader));
/* 1083 */     if (doc == null) {
/* 1084 */       throw new Exception(Messages.getMessage("noDoc00", elementString));
/*      */     }
/*      */ 
/* 1087 */     return doc;
/*      */   }
/*      */ 
/*      */   public String getAsString()
/*      */     throws Exception
/*      */   {
/* 1100 */     SerializationContext serializeContext = null;
/* 1101 */     StringWriter writer = new StringWriter();
/*      */     MessageContext msgContext;
/*      */     MessageContext msgContext;
/* 1103 */     if (this.context != null)
/* 1104 */       msgContext = this.context.getMessageContext();
/*      */     else {
/* 1106 */       msgContext = MessageContext.getCurrentContext();
/*      */     }
/* 1108 */     serializeContext = new SerializationContext(writer, msgContext);
/* 1109 */     serializeContext.setSendDecl(false);
/* 1110 */     setDirty(false);
/* 1111 */     output(serializeContext);
/* 1112 */     writer.close();
/*      */ 
/* 1114 */     return writer.getBuffer().toString();
/*      */   }
/*      */ 
/*      */   public Element getAsDOM()
/*      */     throws Exception
/*      */   {
/* 1127 */     return getAsDocument().getDocumentElement();
/*      */   }
/*      */ 
/*      */   public void publishToHandler(ContentHandler handler)
/*      */     throws SAXException
/*      */   {
/* 1137 */     if (this.recorder == null) {
/* 1138 */       throw new SAXException(Messages.getMessage("noRecorder00"));
/*      */     }
/*      */ 
/* 1141 */     this.recorder.replay(this.startEventIndex, this.endEventIndex, handler);
/*      */   }
/*      */ 
/*      */   public void publishContents(ContentHandler handler)
/*      */     throws SAXException
/*      */   {
/* 1151 */     if (this.recorder == null) {
/* 1152 */       throw new SAXException(Messages.getMessage("noRecorder00"));
/*      */     }
/*      */ 
/* 1155 */     this.recorder.replay(this.startContentsIndex, this.endEventIndex - 1, handler);
/*      */   }
/*      */ 
/*      */   public final void output(SerializationContext outputContext)
/*      */     throws Exception
/*      */   {
/* 1167 */     if ((this.recorder != null) && (!this._isDirty)) {
/* 1168 */       this.recorder.replay(this.startEventIndex, this.endEventIndex, new SAXOutputter(outputContext));
/*      */ 
/* 1171 */       return;
/*      */     }
/*      */ 
/* 1175 */     if (this.qNameAttrs != null) {
/* 1176 */       for (int i = 0; i < this.qNameAttrs.size(); i++) {
/* 1177 */         QNameAttr attr = (QNameAttr)this.qNameAttrs.get(i);
/* 1178 */         QName attrName = attr.name;
/* 1179 */         setAttribute(attrName.getNamespaceURI(), attrName.getLocalPart(), outputContext.qName2String(attr.value));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1189 */     if (this.encodingStyle != null) {
/* 1190 */       MessageContext mc = outputContext.getMessageContext();
/* 1191 */       SOAPConstants soapConstants = mc != null ? mc.getSOAPConstants() : SOAPConstants.SOAP11_CONSTANTS;
/*      */ 
/* 1194 */       if (this.parent == null)
/*      */       {
/* 1196 */         if (!"".equals(this.encodingStyle)) {
/* 1197 */           setAttribute(soapConstants.getEnvelopeURI(), "encodingStyle", this.encodingStyle);
/*      */         }
/*      */ 
/*      */       }
/* 1201 */       else if (!this.encodingStyle.equals(((MessageElement)this.parent).getEncodingStyle())) {
/* 1202 */         setAttribute(soapConstants.getEnvelopeURI(), "encodingStyle", this.encodingStyle);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1208 */     outputImpl(outputContext);
/*      */   }
/*      */ 
/*      */   protected void outputImpl(SerializationContext outputContext)
/*      */     throws Exception
/*      */   {
/* 1218 */     if (this.textRep != null) {
/* 1219 */       boolean oldPretty = outputContext.getPretty();
/* 1220 */       outputContext.setPretty(false);
/* 1221 */       if ((this.textRep instanceof CDATASection)) {
/* 1222 */         outputContext.writeString("<![CDATA[");
/* 1223 */         outputContext.writeString(this.textRep.getData());
/* 1224 */         outputContext.writeString("]]>");
/* 1225 */       } else if ((this.textRep instanceof Comment)) {
/* 1226 */         outputContext.writeString("<!--");
/* 1227 */         outputContext.writeString(this.textRep.getData());
/* 1228 */         outputContext.writeString("-->");
/* 1229 */       } else if ((this.textRep instanceof org.w3c.dom.Text)) {
/* 1230 */         outputContext.writeSafeString(this.textRep.getData());
/*      */       }
/* 1232 */       outputContext.setPretty(oldPretty);
/* 1233 */       return;
/*      */     }
/*      */ 
/* 1236 */     if (this.prefix != null)
/* 1237 */       outputContext.registerPrefixForURI(this.prefix, this.namespaceURI);
/*      */     Iterator i;
/* 1239 */     if (this.namespaces != null) {
/* 1240 */       for (i = this.namespaces.iterator(); i.hasNext(); ) {
/* 1241 */         Mapping mapping = (Mapping)i.next();
/* 1242 */         outputContext.registerPrefixForURI(mapping.getPrefix(), mapping.getNamespaceURI());
/*      */       }
/*      */     }
/*      */ 
/* 1246 */     if (this.objectValue != null) {
/* 1247 */       outputContext.serialize(new QName(this.namespaceURI, this.name), this.attributes, this.objectValue);
/*      */ 
/* 1250 */       return;
/*      */     }
/*      */ 
/* 1253 */     outputContext.startElement(new QName(this.namespaceURI, this.name), this.attributes);
/*      */     Iterator it;
/* 1254 */     if (this.children != null) {
/* 1255 */       for (it = this.children.iterator(); it.hasNext(); ) {
/* 1256 */         ((NodeImpl)it.next()).output(outputContext);
/*      */       }
/*      */     }
/* 1259 */     outputContext.endElement();
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*      */     try
/*      */     {
/* 1272 */       return getAsString();
/*      */     }
/*      */     catch (Exception exp)
/*      */     {
/* 1277 */       log.error(Messages.getMessage("exception00"), exp);
/*      */     }
/* 1279 */     return super.toString();
/*      */   }
/*      */ 
/*      */   public void addMapping(Mapping map)
/*      */   {
/* 1290 */     if (this.namespaces == null) {
/* 1291 */       this.namespaces = new ArrayList();
/*      */     }
/* 1293 */     this.namespaces.add(map);
/*      */   }
/*      */ 
/*      */   public SOAPElement addChildElement(Name childName)
/*      */     throws SOAPException
/*      */   {
/* 1306 */     MessageElement child = new MessageElement(childName.getLocalName(), childName.getPrefix(), childName.getURI());
/*      */ 
/* 1309 */     addChild(child);
/* 1310 */     return child;
/*      */   }
/*      */ 
/*      */   public SOAPElement addChildElement(String localName)
/*      */     throws SOAPException
/*      */   {
/* 1322 */     MessageElement child = new MessageElement(getNamespaceURI(), localName);
/*      */ 
/* 1324 */     addChild(child);
/* 1325 */     return child;
/*      */   }
/*      */ 
/*      */   public SOAPElement addChildElement(String localName, String prefixName)
/*      */     throws SOAPException
/*      */   {
/* 1338 */     MessageElement child = new MessageElement(getNamespaceURI(prefixName), localName);
/*      */ 
/* 1340 */     child.setPrefix(prefixName);
/* 1341 */     addChild(child);
/* 1342 */     return child;
/*      */   }
/*      */ 
/*      */   public SOAPElement addChildElement(String localName, String childPrefix, String uri)
/*      */     throws SOAPException
/*      */   {
/* 1357 */     MessageElement child = new MessageElement(uri, localName);
/* 1358 */     child.setPrefix(childPrefix);
/* 1359 */     child.addNamespaceDeclaration(childPrefix, uri);
/* 1360 */     addChild(child);
/* 1361 */     return child;
/*      */   }
/*      */ 
/*      */   public SOAPElement addChildElement(SOAPElement element)
/*      */     throws SOAPException
/*      */   {
/*      */     try
/*      */     {
/* 1373 */       addChild((MessageElement)element);
/* 1374 */       setDirty();
/* 1375 */       return element; } catch (ClassCastException e) {
/*      */     }
/* 1377 */     throw new SOAPException(e);
/*      */   }
/*      */ 
/*      */   public SOAPElement addTextNode(String s)
/*      */     throws SOAPException
/*      */   {
/*      */     try
/*      */     {
/* 1388 */       org.w3c.dom.Text text = getOwnerDocument().createTextNode(s);
/* 1389 */       ((Text)text).setParentElement(this);
/* 1390 */       return this;
/*      */     } catch (IncompatibleClassChangeError e) {
/* 1392 */       org.w3c.dom.Text text = new Text(s);
/* 1393 */       appendChild(text);
/* 1394 */       return this; } catch (ClassCastException e) {
/*      */     }
/* 1396 */     throw new SOAPException(e);
/*      */   }
/*      */ 
/*      */   public SOAPElement addAttribute(Name attrName, String value)
/*      */     throws SOAPException
/*      */   {
/*      */     try
/*      */     {
/* 1411 */       addAttribute(attrName.getPrefix(), attrName.getURI(), attrName.getLocalName(), value);
/*      */     } catch (RuntimeException t) {
/* 1413 */       throw new SOAPException(t);
/*      */     }
/* 1415 */     return this;
/*      */   }
/*      */ 
/*      */   public SOAPElement addNamespaceDeclaration(String prefix, String uri)
/*      */     throws SOAPException
/*      */   {
/*      */     try
/*      */     {
/* 1433 */       Mapping map = new Mapping(uri, prefix);
/* 1434 */       addMapping(map);
/*      */     }
/*      */     catch (RuntimeException t) {
/* 1437 */       throw new SOAPException(t);
/*      */     }
/* 1439 */     return this;
/*      */   }
/*      */ 
/*      */   public String getAttributeValue(Name attrName)
/*      */   {
/* 1449 */     return this.attributes.getValue(attrName.getURI(), attrName.getLocalName());
/*      */   }
/*      */ 
/*      */   public Iterator getAllAttributes()
/*      */   {
/* 1461 */     int num = this.attributes.getLength();
/* 1462 */     Vector attrs = new Vector(num);
/* 1463 */     for (int i = 0; i < num; i++) {
/* 1464 */       String q = this.attributes.getQName(i);
/* 1465 */       String prefix = "";
/* 1466 */       if (q != null) {
/* 1467 */         int idx = q.indexOf(":");
/* 1468 */         if (idx > 0)
/* 1469 */           prefix = q.substring(0, idx);
/*      */         else {
/* 1471 */           prefix = "";
/*      */         }
/*      */       }
/*      */ 
/* 1475 */       attrs.add(new PrefixedQName(this.attributes.getURI(i), this.attributes.getLocalName(i), prefix));
/*      */     }
/*      */ 
/* 1479 */     return attrs.iterator();
/*      */   }
/*      */ 
/*      */   public Iterator getNamespacePrefixes()
/*      */   {
/* 1491 */     Vector prefixes = new Vector();
/* 1492 */     for (int i = 0; (this.namespaces != null) && (i < this.namespaces.size()); i++) {
/* 1493 */       prefixes.add(((Mapping)this.namespaces.get(i)).getPrefix());
/*      */     }
/* 1495 */     return prefixes.iterator();
/*      */   }
/*      */ 
/*      */   public Name getElementName()
/*      */   {
/* 1504 */     return new PrefixedQName(getNamespaceURI(), getName(), getPrefix());
/*      */   }
/*      */ 
/*      */   public boolean removeAttribute(Name attrName)
/*      */   {
/* 1514 */     AttributesImpl attributes = makeAttributesEditable();
/* 1515 */     boolean removed = false;
/*      */ 
/* 1517 */     for (int i = 0; (i < attributes.getLength()) && (!removed); i++) {
/* 1518 */       if ((!attributes.getURI(i).equals(attrName.getURI())) || (!attributes.getLocalName(i).equals(attrName.getLocalName())))
/*      */         continue;
/* 1520 */       attributes.removeAttribute(i);
/* 1521 */       removed = true;
/*      */     }
/*      */ 
/* 1524 */     return removed;
/*      */   }
/*      */ 
/*      */   public boolean removeNamespaceDeclaration(String namespacePrefix)
/*      */   {
/* 1534 */     makeAttributesEditable();
/* 1535 */     boolean removed = false;
/*      */ 
/* 1537 */     for (int i = 0; (this.namespaces != null) && (i < this.namespaces.size()) && (!removed); i++) {
/* 1538 */       if (((Mapping)this.namespaces.get(i)).getPrefix().equals(namespacePrefix)) {
/* 1539 */         this.namespaces.remove(i);
/* 1540 */         removed = true;
/*      */       }
/*      */     }
/* 1543 */     return removed;
/*      */   }
/*      */ 
/*      */   public Iterator getChildElements()
/*      */   {
/* 1554 */     initializeChildren();
/* 1555 */     return this.children.iterator();
/*      */   }
/*      */ 
/*      */   public MessageElement getChildElement(QName qname)
/*      */   {
/*      */     Iterator i;
/* 1566 */     if (this.children != null) {
/* 1567 */       for (i = this.children.iterator(); i.hasNext(); ) {
/* 1568 */         MessageElement child = (MessageElement)i.next();
/* 1569 */         if (child.getQName().equals(qname))
/* 1570 */           return child;
/*      */       }
/*      */     }
/* 1573 */     return null;
/*      */   }
/*      */ 
/*      */   public Iterator getChildElements(QName qname)
/*      */   {
/* 1585 */     initializeChildren();
/* 1586 */     int num = this.children.size();
/* 1587 */     Vector c = new Vector(num);
/* 1588 */     for (int i = 0; i < num; i++) {
/* 1589 */       MessageElement child = (MessageElement)this.children.get(i);
/* 1590 */       Name cname = child.getElementName();
/* 1591 */       if ((!cname.getURI().equals(qname.getNamespaceURI())) || (!cname.getLocalName().equals(qname.getLocalPart())))
/*      */         continue;
/* 1593 */       c.add(child);
/*      */     }
/*      */ 
/* 1596 */     return c.iterator();
/*      */   }
/*      */ 
/*      */   public Iterator getChildElements(Name childName)
/*      */   {
/* 1609 */     return getChildElements(new QName(childName.getURI(), childName.getLocalName()));
/*      */   }
/*      */ 
/*      */   public String getTagName()
/*      */   {
/* 1619 */     return this.prefix + ":" + this.name;
/*      */   }
/*      */ 
/*      */   public void removeAttribute(String attrName)
/*      */     throws DOMException
/*      */   {
/* 1629 */     AttributesImpl impl = (AttributesImpl)this.attributes;
/* 1630 */     int index = impl.getIndex(attrName);
/* 1631 */     if (index >= 0) {
/* 1632 */       AttributesImpl newAttrs = new AttributesImpl();
/*      */ 
/* 1634 */       for (int i = 0; i < impl.getLength(); i++) {
/* 1635 */         if (i != index) {
/* 1636 */           String uri = impl.getURI(i);
/* 1637 */           String local = impl.getLocalName(i);
/* 1638 */           String qname = impl.getQName(i);
/* 1639 */           String type = impl.getType(i);
/* 1640 */           String value = impl.getValue(i);
/* 1641 */           newAttrs.addAttribute(uri, local, qname, type, value);
/*      */         }
/*      */       }
/*      */ 
/* 1645 */       this.attributes = newAttrs;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean hasAttribute(String attrName)
/*      */   {
/* 1657 */     if (attrName == null) {
/* 1658 */       attrName = "";
/*      */     }
/* 1660 */     for (int i = 0; i < this.attributes.getLength(); i++) {
/* 1661 */       if (attrName.equals(this.attributes.getQName(i)))
/* 1662 */         return true;
/*      */     }
/* 1664 */     return false;
/*      */   }
/*      */ 
/*      */   public String getAttribute(String attrName)
/*      */   {
/* 1674 */     return this.attributes.getValue(attrName);
/*      */   }
/*      */ 
/*      */   public void removeAttributeNS(String namespace, String localName)
/*      */     throws DOMException
/*      */   {
/* 1689 */     makeAttributesEditable();
/* 1690 */     Name name = new PrefixedQName(namespace, localName, null);
/* 1691 */     removeAttribute(name);
/*      */   }
/*      */ 
/*      */   public void setAttribute(String name, String value)
/*      */     throws DOMException
/*      */   {
/* 1702 */     AttributesImpl impl = makeAttributesEditable();
/* 1703 */     int index = impl.getIndex(name);
/* 1704 */     if (index < 0) {
/* 1705 */       String uri = "";
/* 1706 */       String localname = name;
/* 1707 */       String qname = name;
/* 1708 */       String type = "CDDATA";
/* 1709 */       impl.addAttribute(uri, localname, qname, type, value);
/*      */     } else {
/* 1711 */       impl.setLocalName(index, value);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean hasAttributeNS(String namespace, String localName)
/*      */   {
/* 1723 */     if (namespace == null) {
/* 1724 */       namespace = "";
/*      */     }
/* 1726 */     if (localName == null)
/*      */     {
/* 1728 */       localName = "";
/*      */     }
/*      */ 
/* 1731 */     for (int i = 0; i < this.attributes.getLength(); i++)
/* 1732 */       if ((namespace.equals(this.attributes.getURI(i))) && (localName.equals(this.attributes.getLocalName(i))))
/*      */       {
/* 1734 */         return true;
/*      */       }
/* 1736 */     return false;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public Attr getAttributeNode(String attrName)
/*      */   {
/* 1749 */     return null;
/*      */   }
/*      */ 
/*      */   public Attr removeAttributeNode(Attr oldAttr)
/*      */     throws DOMException
/*      */   {
/* 1759 */     makeAttributesEditable();
/* 1760 */     Name name = new PrefixedQName(oldAttr.getNamespaceURI(), oldAttr.getLocalName(), oldAttr.getPrefix());
/* 1761 */     removeAttribute(name);
/* 1762 */     return oldAttr;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public Attr setAttributeNode(Attr newAttr)
/*      */     throws DOMException
/*      */   {
/* 1775 */     return newAttr;
/*      */   }
/*      */ 
/*      */   public Attr setAttributeNodeNS(Attr newAttr)
/*      */     throws DOMException
/*      */   {
/* 1788 */     AttributesImpl attributes = makeAttributesEditable();
/*      */ 
/* 1790 */     attributes.addAttribute(newAttr.getNamespaceURI(), newAttr.getLocalName(), newAttr.getLocalName(), "CDATA", newAttr.getValue());
/*      */ 
/* 1795 */     return null;
/*      */   }
/*      */ 
/*      */   public NodeList getElementsByTagName(String tagName)
/*      */   {
/* 1804 */     NodeListImpl nodelist = new NodeListImpl();
/* 1805 */     for (int i = 0; (this.children != null) && (i < this.children.size()); i++) {
/* 1806 */       if ((this.children.get(i) instanceof Node)) {
/* 1807 */         Node el = (Node)this.children.get(i);
/* 1808 */         if ((el.getLocalName() != null) && (el.getLocalName().equals(tagName)))
/*      */         {
/* 1810 */           nodelist.addNode(el);
/* 1811 */         }if ((el instanceof Element)) {
/* 1812 */           NodeList grandchildren = ((Element)el).getElementsByTagName(tagName);
/*      */ 
/* 1814 */           for (int j = 0; j < grandchildren.getLength(); j++) {
/* 1815 */             nodelist.addNode(grandchildren.item(j));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1820 */     return nodelist;
/*      */   }
/*      */ 
/*      */   public String getAttributeNS(String namespaceURI, String localName)
/*      */   {
/* 1832 */     if (namespaceURI == null) {
/* 1833 */       namespaceURI = "";
/*      */     }
/* 1835 */     for (int i = 0; i < this.attributes.getLength(); i++) {
/* 1836 */       if ((this.attributes.getURI(i).equals(namespaceURI)) && (this.attributes.getLocalName(i).equals(localName)))
/*      */       {
/* 1838 */         return this.attributes.getValue(i);
/*      */       }
/*      */     }
/* 1841 */     return null;
/*      */   }
/*      */ 
/*      */   public void setAttributeNS(String namespaceURI, String qualifiedName, String value)
/*      */     throws DOMException
/*      */   {
/* 1856 */     AttributesImpl attributes = makeAttributesEditable();
/* 1857 */     String localName = qualifiedName.substring(qualifiedName.indexOf(":") + 1, qualifiedName.length());
/*      */ 
/* 1859 */     if (namespaceURI == null) {
/* 1860 */       namespaceURI = "intentionalNullURI";
/*      */     }
/* 1862 */     attributes.addAttribute(namespaceURI, localName, qualifiedName, "CDATA", value);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public Attr getAttributeNodeNS(String namespace, String localName)
/*      */   {
/* 1877 */     return null;
/*      */   }
/*      */ 
/*      */   public NodeList getElementsByTagNameNS(String namespace, String localName)
/*      */   {
/* 1889 */     return getElementsNS(this, namespace, localName);
/*      */   }
/*      */ 
/*      */   protected NodeList getElementsNS(Element parentElement, String namespace, String localName)
/*      */   {
/* 1902 */     NodeList children = parentElement.getChildNodes();
/* 1903 */     NodeListImpl matches = new NodeListImpl();
/*      */ 
/* 1905 */     for (int i = 0; i < children.getLength(); i++) {
/* 1906 */       if ((children.item(i) instanceof org.w3c.dom.Text)) {
/*      */         continue;
/*      */       }
/* 1909 */       Element child = (Element)children.item(i);
/* 1910 */       if ((namespace.equals(child.getNamespaceURI())) && (localName.equals(child.getLocalName())))
/*      */       {
/* 1912 */         matches.addNode(child);
/*      */       }
/*      */ 
/* 1915 */       matches.addNodeList(child.getElementsByTagNameNS(namespace, localName));
/*      */     }
/*      */ 
/* 1918 */     return matches;
/*      */   }
/*      */ 
/*      */   public Node item(int index)
/*      */   {
/* 1928 */     if ((this.children != null) && (this.children.size() > index)) {
/* 1929 */       return (Node)this.children.get(index);
/*      */     }
/* 1931 */     return null;
/*      */   }
/*      */ 
/*      */   public int getLength()
/*      */   {
/* 1944 */     return this.children == null ? 0 : this.children.size();
/*      */   }
/*      */ 
/*      */   protected MessageElement findElement(Vector vec, String namespace, String localPart)
/*      */   {
/* 1954 */     if (vec.isEmpty()) {
/* 1955 */       return null;
/*      */     }
/*      */ 
/* 1958 */     QName qname = new QName(namespace, localPart);
/* 1959 */     Enumeration e = vec.elements();
/*      */ 
/* 1961 */     while (e.hasMoreElements()) {
/* 1962 */       MessageElement element = (MessageElement)e.nextElement();
/* 1963 */       if (element.getQName().equals(qname)) {
/* 1964 */         return element;
/*      */       }
/*      */     }
/*      */ 
/* 1968 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean equals(Object obj)
/*      */   {
/* 1980 */     if ((obj == null) || (!(obj instanceof MessageElement))) {
/* 1981 */       return false;
/*      */     }
/* 1983 */     if (this == obj) {
/* 1984 */       return true;
/*      */     }
/* 1986 */     if (!getLocalName().equals(((MessageElement)obj).getLocalName())) {
/* 1987 */       return false;
/*      */     }
/* 1989 */     return toString().equals(obj.toString());
/*      */   }
/*      */ 
/*      */   private void copyNode(Node element)
/*      */   {
/* 1998 */     copyNode(this, element);
/*      */   }
/*      */ 
/*      */   private void copyNode(MessageElement dest, Node source)
/*      */   {
/* 2008 */     dest.setPrefix(source.getPrefix());
/* 2009 */     if (source.getLocalName() != null) {
/* 2010 */       dest.setQName(new QName(source.getNamespaceURI(), source.getLocalName()));
/*      */     }
/*      */     else
/*      */     {
/* 2014 */       dest.setQName(new QName(source.getNamespaceURI(), source.getNodeName()));
/*      */     }
/*      */ 
/* 2017 */     NamedNodeMap attrs = source.getAttributes();
/* 2018 */     for (int i = 0; i < attrs.getLength(); i++) {
/* 2019 */       Node att = attrs.item(i);
/* 2020 */       if ((att.getNamespaceURI() != null) && (att.getPrefix() != null) && (att.getNamespaceURI().equals("http://www.w3.org/2000/xmlns/")) && ("xmlns".equals(att.getPrefix())))
/*      */       {
/* 2024 */         Mapping map = new Mapping(att.getNodeValue(), att.getLocalName());
/* 2025 */         dest.addMapping(map);
/*      */       }
/* 2027 */       if (att.getLocalName() != null) {
/* 2028 */         dest.addAttribute(att.getPrefix(), att.getNamespaceURI() != null ? att.getNamespaceURI() : "", att.getLocalName(), att.getNodeValue());
/*      */       }
/* 2032 */       else if (att.getNodeName() != null) {
/* 2033 */         dest.addAttribute(att.getPrefix(), att.getNamespaceURI() != null ? att.getNamespaceURI() : "", att.getNodeName(), att.getNodeValue());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2040 */     NodeList children = source.getChildNodes();
/* 2041 */     for (int i = 0; i < children.getLength(); i++) {
/* 2042 */       Node child = children.item(i);
/* 2043 */       if ((child.getNodeType() == 3) || (child.getNodeType() == 4) || (child.getNodeType() == 8))
/*      */       {
/* 2046 */         Text childElement = new Text((CharacterData)child);
/*      */ 
/* 2048 */         dest.appendChild(childElement);
/*      */       } else {
/* 2050 */         MessageElement childElement = new MessageElement();
/* 2051 */         dest.appendChild(childElement);
/* 2052 */         copyNode(childElement, child);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getValue()
/*      */   {
/* 2084 */     if (this.textRep != null)
/*      */     {
/* 2086 */       return this.textRep.getNodeValue();
/*      */     }
/*      */ 
/* 2089 */     if (this.objectValue != null) {
/* 2090 */       return getValueDOM();
/*      */     }
/*      */ 
/* 2093 */     for (Iterator i = getChildElements(); i.hasNext(); ) {
/* 2094 */       NodeImpl n = (NodeImpl)i.next();
/* 2095 */       if ((n instanceof Text)) {
/* 2096 */         Text textNode = (Text)n;
/* 2097 */         return textNode.getNodeValue();
/*      */       }
/*      */     }
/*      */ 
/* 2101 */     return null;
/*      */   }
/*      */ 
/*      */   protected String getValueDOM() {
/*      */     try {
/* 2106 */       Element element = getAsDOM();
/* 2107 */       if (element.hasChildNodes()) {
/* 2108 */         Node node = element.getFirstChild();
/* 2109 */         if (node.getNodeType() == 3)
/* 2110 */           return node.getNodeValue();
/*      */       }
/*      */     }
/*      */     catch (Exception t) {
/* 2114 */       log.debug("getValue()", t);
/*      */     }
/* 2116 */     return null;
/*      */   }
/*      */ 
/*      */   public void setValue(String value)
/*      */   {
/* 2122 */     if (this.children == null) {
/*      */       try {
/* 2124 */         setObjectValue(value);
/*      */       } catch (SOAPException soape) {
/* 2126 */         log.debug("setValue()", soape);
/*      */       }
/*      */     }
/* 2129 */     super.setValue(value);
/*      */   }
/*      */ 
/*      */   public Document getOwnerDocument() {
/* 2133 */     Document doc = null;
/* 2134 */     if ((this.context != null) && (this.context.getEnvelope() != null) && (this.context.getEnvelope().getOwnerDocument() != null))
/*      */     {
/* 2136 */       doc = this.context.getEnvelope().getOwnerDocument();
/*      */     }
/* 2138 */     if (doc == null) {
/* 2139 */       doc = super.getOwnerDocument();
/*      */     }
/* 2141 */     if (doc == null) {
/* 2142 */       doc = new SOAPDocumentImpl(null);
/*      */     }
/* 2144 */     return doc;
/*      */   }
/*      */ 
/*      */   protected static class QNameAttr
/*      */   {
/*      */     public QName name;
/*      */     public QName value;
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.MessageElement
 * JD-Core Version:    0.6.0
 */