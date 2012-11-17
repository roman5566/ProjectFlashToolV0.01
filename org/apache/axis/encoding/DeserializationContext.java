/*      */ package org.apache.axis.encoding;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.Method;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import javax.xml.namespace.QName;
/*      */ import javax.xml.parsers.SAXParser;
/*      */ import javax.xml.rpc.JAXRPCException;
/*      */ import javax.xml.rpc.holders.Holder;
/*      */ import org.apache.axis.AxisFault;
/*      */ import org.apache.axis.Constants;
/*      */ import org.apache.axis.Message;
/*      */ import org.apache.axis.MessageContext;
/*      */ import org.apache.axis.attachments.Attachments;
/*      */ import org.apache.axis.components.logger.LogFactory;
/*      */ import org.apache.axis.constants.Use;
/*      */ import org.apache.axis.description.TypeDesc;
/*      */ import org.apache.axis.message.EnvelopeBuilder;
/*      */ import org.apache.axis.message.EnvelopeHandler;
/*      */ import org.apache.axis.message.IDResolver;
/*      */ import org.apache.axis.message.MessageElement;
/*      */ import org.apache.axis.message.NullAttributes;
/*      */ import org.apache.axis.message.SAX2EventRecorder;
/*      */ import org.apache.axis.message.SOAPEnvelope;
/*      */ import org.apache.axis.message.SOAPHandler;
/*      */ import org.apache.axis.schema.SchemaVersion;
/*      */ import org.apache.axis.soap.SOAPConstants;
/*      */ import org.apache.axis.utils.JavaUtils;
/*      */ import org.apache.axis.utils.Messages;
/*      */ import org.apache.axis.utils.NSStack;
/*      */ import org.apache.axis.utils.XMLUtils;
/*      */ import org.apache.axis.utils.cache.MethodCache;
/*      */ import org.apache.commons.logging.Log;
/*      */ import org.xml.sax.Attributes;
/*      */ import org.xml.sax.InputSource;
/*      */ import org.xml.sax.Locator;
/*      */ import org.xml.sax.SAXException;
/*      */ import org.xml.sax.ext.LexicalHandler;
/*      */ import org.xml.sax.helpers.AttributesImpl;
/*      */ import org.xml.sax.helpers.DefaultHandler;
/*      */ 
/*      */ public class DeserializationContext extends DefaultHandler
/*      */   implements javax.xml.rpc.encoding.DeserializationContext, LexicalHandler
/*      */ {
/*   67 */   protected static Log log = LogFactory.getLog(DeserializationContext.class.getName());
/*      */ 
/*   73 */   private final boolean debugEnabled = log.isDebugEnabled();
/*      */ 
/*   75 */   static final SchemaVersion[] schemaVersions = { SchemaVersion.SCHEMA_1999, SchemaVersion.SCHEMA_2000, SchemaVersion.SCHEMA_2001 };
/*      */ 
/*   81 */   private NSStack namespaces = new NSStack();
/*      */   private Locator locator;
/*      */   private Class destClass;
/*   91 */   private SOAPHandler topHandler = null;
/*   92 */   private ArrayList pushedDownHandlers = new ArrayList();
/*      */ 
/*   95 */   private SAX2EventRecorder recorder = null;
/*      */   private SOAPEnvelope envelope;
/*      */   private HashMap idMap;
/*      */   private LocalIDResolver localIDs;
/*      */   private HashMap fixups;
/*  104 */   static final SOAPHandler nullHandler = new SOAPHandler();
/*      */   protected MessageContext msgContext;
/*  108 */   private boolean doneParsing = false;
/*  109 */   protected InputSource inputSource = null;
/*      */   private MessageElement curElement;
/*  113 */   protected int startOfMappingsPos = -1;
/*      */ 
/*  115 */   private static final Class[] DESERIALIZER_CLASSES = { String.class, Class.class, QName.class };
/*      */   private static final String DESERIALIZER_METHOD = "getDeserializer";
/*  122 */   protected boolean haveSeenSchemaNS = false;
/*      */ 
/*  174 */   private SOAPConstants soapConstants = null;
/*      */ 
/*  836 */   boolean processingRef = false;
/*      */ 
/* 1218 */   private static final NullLexicalHandler nullLexicalHandler = new NullLexicalHandler(null);
/*      */ 
/*      */   public void deserializing(boolean isDeserializing)
/*      */   {
/*  125 */     this.doneParsing = isDeserializing;
/*      */   }
/*      */ 
/*      */   public DeserializationContext(MessageContext ctx, SOAPHandler initialHandler)
/*      */   {
/*  136 */     this.msgContext = ctx;
/*      */ 
/*  139 */     if ((ctx == null) || (ctx.isHighFidelity())) {
/*  140 */       this.recorder = new SAX2EventRecorder();
/*      */     }
/*  142 */     if ((initialHandler instanceof EnvelopeBuilder)) {
/*  143 */       this.envelope = ((EnvelopeBuilder)initialHandler).getEnvelope();
/*  144 */       this.envelope.setRecorder(this.recorder);
/*      */     }
/*      */ 
/*  147 */     pushElementHandler(new EnvelopeHandler(initialHandler));
/*      */   }
/*      */ 
/*      */   public DeserializationContext(InputSource is, MessageContext ctx, String messageType)
/*      */   {
/*  160 */     this.msgContext = ctx;
/*  161 */     EnvelopeBuilder builder = new EnvelopeBuilder(messageType, ctx != null ? ctx.getSOAPConstants() : null);
/*      */ 
/*  163 */     if ((ctx == null) || (ctx.isHighFidelity())) {
/*  164 */       this.recorder = new SAX2EventRecorder();
/*      */     }
/*  166 */     this.envelope = builder.getEnvelope();
/*  167 */     this.envelope.setRecorder(this.recorder);
/*      */ 
/*  169 */     pushElementHandler(new EnvelopeHandler(builder));
/*      */ 
/*  171 */     this.inputSource = is;
/*      */   }
/*      */ 
/*      */   public SOAPConstants getSOAPConstants()
/*      */   {
/*  180 */     if (this.soapConstants != null)
/*  181 */       return this.soapConstants;
/*  182 */     if (this.msgContext != null) {
/*  183 */       this.soapConstants = this.msgContext.getSOAPConstants();
/*  184 */       return this.soapConstants;
/*      */     }
/*  186 */     return Constants.DEFAULT_SOAP_VERSION;
/*      */   }
/*      */ 
/*      */   public DeserializationContext(InputSource is, MessageContext ctx, String messageType, SOAPEnvelope env)
/*      */   {
/*  202 */     EnvelopeBuilder builder = new EnvelopeBuilder(env, messageType);
/*      */ 
/*  204 */     this.msgContext = ctx;
/*      */ 
/*  207 */     if ((ctx == null) || (ctx.isHighFidelity())) {
/*  208 */       this.recorder = new SAX2EventRecorder();
/*      */     }
/*  210 */     this.envelope = builder.getEnvelope();
/*  211 */     this.envelope.setRecorder(this.recorder);
/*      */ 
/*  213 */     pushElementHandler(new EnvelopeHandler(builder));
/*      */ 
/*  215 */     this.inputSource = is;
/*      */   }
/*      */ 
/*      */   public void parse()
/*      */     throws SAXException
/*      */   {
/*  223 */     if (this.inputSource != null) {
/*  224 */       SAXParser parser = XMLUtils.getSAXParser();
/*      */       try {
/*  226 */         parser.setProperty("http://xml.org/sax/properties/lexical-handler", this);
/*  227 */         parser.parse(this.inputSource, this);
/*      */         try
/*      */         {
/*  231 */           parser.setProperty("http://xml.org/sax/properties/lexical-handler", nullLexicalHandler);
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/*      */         }
/*      */ 
/*  239 */         XMLUtils.releaseSAXParser(parser);
/*      */       } catch (IOException e) {
/*  241 */         throw new SAXException(e);
/*      */       }
/*  243 */       this.inputSource = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public MessageElement getCurElement()
/*      */   {
/*  251 */     return this.curElement;
/*      */   }
/*      */ 
/*      */   public void setCurElement(MessageElement el)
/*      */   {
/*  259 */     this.curElement = el;
/*  260 */     if ((this.curElement != null) && (this.curElement.getRecorder() != this.recorder))
/*  261 */       this.recorder = this.curElement.getRecorder();
/*      */   }
/*      */ 
/*      */   public MessageContext getMessageContext()
/*      */   {
/*  271 */     return this.msgContext;
/*      */   }
/*      */ 
/*      */   public String getEncodingStyle()
/*      */   {
/*  283 */     return this.msgContext == null ? Use.ENCODED.getEncoding() : this.msgContext.getEncodingStyle();
/*      */   }
/*      */ 
/*      */   public SOAPEnvelope getEnvelope()
/*      */   {
/*  292 */     return this.envelope;
/*      */   }
/*      */ 
/*      */   public SAX2EventRecorder getRecorder()
/*      */   {
/*  300 */     return this.recorder;
/*      */   }
/*      */ 
/*      */   public void setRecorder(SAX2EventRecorder recorder)
/*      */   {
/*  308 */     this.recorder = recorder;
/*      */   }
/*      */ 
/*      */   public ArrayList getCurrentNSMappings()
/*      */   {
/*  316 */     return this.namespaces.cloneFrame();
/*      */   }
/*      */ 
/*      */   public String getNamespaceURI(String prefix)
/*      */   {
/*  324 */     String result = this.namespaces.getNamespaceURI(prefix);
/*  325 */     if (result != null) {
/*  326 */       return result;
/*      */     }
/*  328 */     if (this.curElement != null) {
/*  329 */       return this.curElement.getNamespaceURI(prefix);
/*      */     }
/*  331 */     return null;
/*      */   }
/*      */ 
/*      */   public QName getQNameFromString(String qNameStr)
/*      */   {
/*  341 */     if (qNameStr == null) {
/*  342 */       return null;
/*      */     }
/*      */ 
/*  345 */     int i = qNameStr.indexOf(':');
/*      */     String nsURI;
/*      */     String nsURI;
/*  347 */     if (i == -1)
/*  348 */       nsURI = getNamespaceURI("");
/*      */     else {
/*  350 */       nsURI = getNamespaceURI(qNameStr.substring(0, i));
/*      */     }
/*      */ 
/*  353 */     return new QName(nsURI, qNameStr.substring(i + 1));
/*      */   }
/*      */ 
/*      */   public QName getTypeFromXSITypeAttr(String namespace, String localName, Attributes attrs)
/*      */   {
/*  366 */     String type = Constants.getValue(attrs, Constants.URIS_SCHEMA_XSI, "type");
/*      */ 
/*  368 */     if (type != null)
/*      */     {
/*  370 */       return getQNameFromString(type);
/*      */     }
/*  372 */     return null;
/*      */   }
/*      */ 
/*      */   public QName getTypeFromAttributes(String namespace, String localName, Attributes attrs)
/*      */   {
/*  385 */     QName typeQName = getTypeFromXSITypeAttr(namespace, localName, attrs);
/*  386 */     if ((typeQName == null) && (Constants.isSOAP_ENC(namespace)))
/*      */     {
/*  393 */       if (namespace.equals("http://www.w3.org/2003/05/soap-encoding"))
/*  394 */         typeQName = new QName(namespace, localName);
/*  395 */       else if (localName.equals(Constants.SOAP_ARRAY.getLocalPart()))
/*  396 */         typeQName = Constants.SOAP_ARRAY;
/*  397 */       else if (localName.equals(Constants.SOAP_STRING.getLocalPart()))
/*  398 */         typeQName = Constants.SOAP_STRING;
/*  399 */       else if (localName.equals(Constants.SOAP_BOOLEAN.getLocalPart()))
/*  400 */         typeQName = Constants.SOAP_BOOLEAN;
/*  401 */       else if (localName.equals(Constants.SOAP_DOUBLE.getLocalPart()))
/*  402 */         typeQName = Constants.SOAP_DOUBLE;
/*  403 */       else if (localName.equals(Constants.SOAP_FLOAT.getLocalPart()))
/*  404 */         typeQName = Constants.SOAP_FLOAT;
/*  405 */       else if (localName.equals(Constants.SOAP_INT.getLocalPart()))
/*  406 */         typeQName = Constants.SOAP_INT;
/*  407 */       else if (localName.equals(Constants.SOAP_LONG.getLocalPart()))
/*  408 */         typeQName = Constants.SOAP_LONG;
/*  409 */       else if (localName.equals(Constants.SOAP_SHORT.getLocalPart()))
/*  410 */         typeQName = Constants.SOAP_SHORT;
/*  411 */       else if (localName.equals(Constants.SOAP_BYTE.getLocalPart())) {
/*  412 */         typeQName = Constants.SOAP_BYTE;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  420 */     if ((typeQName == null) && (attrs != null)) {
/*  421 */       String encURI = getSOAPConstants().getEncodingURI();
/*  422 */       String itemType = getSOAPConstants().getAttrItemType();
/*  423 */       for (int i = 0; i < attrs.getLength(); i++) {
/*  424 */         if ((encURI.equals(attrs.getURI(i))) && (itemType.equals(attrs.getLocalName(i))))
/*      */         {
/*  426 */           return new QName(encURI, "Array");
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  431 */     return typeQName;
/*      */   }
/*      */ 
/*      */   public boolean isNil(Attributes attrs)
/*      */   {
/*  441 */     return JavaUtils.isTrueExplicitly(Constants.getValue(attrs, Constants.QNAMES_NIL), false);
/*      */   }
/*      */ 
/*      */   public final Deserializer getDeserializer(Class cls, QName xmlType)
/*      */   {
/*  451 */     if (xmlType == null) {
/*  452 */       return null;
/*      */     }
/*  454 */     DeserializerFactory dserF = null;
/*  455 */     Deserializer dser = null;
/*      */     try {
/*  457 */       dserF = (DeserializerFactory)getTypeMapping().getDeserializer(cls, xmlType);
/*      */     }
/*      */     catch (JAXRPCException e) {
/*  460 */       log.error(Messages.getMessage("noFactory00", xmlType.toString()));
/*      */     }
/*  462 */     if (dserF != null) {
/*      */       try {
/*  464 */         dser = (Deserializer)dserF.getDeserializerAs("Axis SAX Mechanism");
/*      */       } catch (JAXRPCException e) {
/*  466 */         log.error(Messages.getMessage("noDeser00", xmlType.toString()));
/*      */       }
/*      */     }
/*  469 */     return dser;
/*      */   }
/*      */ 
/*      */   public Deserializer getDeserializerForClass(Class cls)
/*      */   {
/*  479 */     if (cls == null) {
/*  480 */       cls = this.destClass;
/*      */     }
/*  482 */     if (cls == null) {
/*  483 */       return null;
/*      */     }
/*      */ 
/*  488 */     if (Holder.class.isAssignableFrom(cls))
/*      */       try {
/*  490 */         cls = cls.getField("value").getType();
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*      */       }
/*  495 */     Deserializer dser = null;
/*      */ 
/*  497 */     QName type = getTypeMapping().getTypeQName(cls);
/*  498 */     dser = getDeserializer(cls, type);
/*  499 */     if (dser != null)
/*  500 */       return dser;
/*      */     try
/*      */     {
/*  503 */       Method method = MethodCache.getInstance().getMethod(cls, "getDeserializer", DESERIALIZER_CLASSES);
/*      */ 
/*  507 */       if (method != null) {
/*  508 */         TypeDesc typedesc = TypeDesc.getTypeDescForClass(cls);
/*  509 */         if (typedesc != null)
/*  510 */           dser = (Deserializer)method.invoke(null, new Object[] { getEncodingStyle(), cls, typedesc.getXmlType() });
/*      */       }
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*  515 */       log.error(Messages.getMessage("noDeser00", cls.getName()));
/*      */     }
/*  517 */     return dser;
/*      */   }
/*      */ 
/*      */   public void setDestinationClass(Class destClass)
/*      */   {
/*  527 */     this.destClass = destClass;
/*      */   }
/*      */ 
/*      */   public Class getDestinationClass()
/*      */   {
/*  537 */     return this.destClass;
/*      */   }
/*      */ 
/*      */   public final Deserializer getDeserializerForType(QName xmlType)
/*      */   {
/*  547 */     return getDeserializer(null, xmlType);
/*      */   }
/*      */ 
/*      */   public TypeMapping getTypeMapping()
/*      */   {
/*  555 */     if ((this.msgContext == null) || (this.msgContext.getTypeMappingRegistry() == null)) {
/*  556 */       return (TypeMapping)new TypeMappingRegistryImpl().getTypeMapping(null);
/*      */     }
/*      */ 
/*  559 */     TypeMappingRegistry tmr = this.msgContext.getTypeMappingRegistry();
/*  560 */     return (TypeMapping)tmr.getTypeMapping(getEncodingStyle());
/*      */   }
/*      */ 
/*      */   public TypeMappingRegistry getTypeMappingRegistry()
/*      */   {
/*  568 */     return this.msgContext.getTypeMappingRegistry();
/*      */   }
/*      */ 
/*      */   public MessageElement getElementByID(String id)
/*      */   {
/*  581 */     if (this.idMap != null) {
/*  582 */       IDResolver resolver = (IDResolver)this.idMap.get(id);
/*  583 */       if (resolver != null) {
/*  584 */         Object ret = resolver.getReferencedObject(id);
/*  585 */         if ((ret instanceof MessageElement)) {
/*  586 */           return (MessageElement)ret;
/*      */         }
/*      */       }
/*      */     }
/*  590 */     return null;
/*      */   }
/*      */ 
/*      */   public Object getObjectByRef(String href)
/*      */   {
/*  603 */     Object ret = null;
/*  604 */     if (href != null) {
/*  605 */       if (this.idMap != null) {
/*  606 */         IDResolver resolver = (IDResolver)this.idMap.get(href);
/*  607 */         if (resolver != null)
/*  608 */           ret = resolver.getReferencedObject(href);
/*      */       }
/*  610 */       if ((null == ret) && (!href.startsWith("#")))
/*      */       {
/*  612 */         Message msg = null;
/*  613 */         if (null != (msg = this.msgContext.getCurrentMessage())) {
/*  614 */           Attachments attch = null;
/*  615 */           if (null != (attch = msg.getAttachmentsImpl())) {
/*      */             try {
/*  617 */               ret = attch.getAttachmentByReference(href);
/*      */             } catch (AxisFault e) {
/*  619 */               throw new RuntimeException(e.toString() + JavaUtils.stackToString(e));
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  626 */     return ret;
/*      */   }
/*      */ 
/*      */   public void addObjectById(String id, Object obj)
/*      */   {
/*  640 */     String idStr = '#' + id;
/*  641 */     if ((this.idMap == null) || (id == null)) {
/*  642 */       return;
/*      */     }
/*  644 */     IDResolver resolver = (IDResolver)this.idMap.get(idStr);
/*  645 */     if (resolver == null) {
/*  646 */       return;
/*      */     }
/*  648 */     resolver.addReferencedObject(idStr, obj);
/*      */   }
/*      */ 
/*      */   public void registerFixup(String href, Deserializer dser)
/*      */   {
/*  665 */     if (this.fixups == null) {
/*  666 */       this.fixups = new HashMap();
/*      */     }
/*  668 */     Deserializer prev = (Deserializer)this.fixups.put(href, dser);
/*      */ 
/*  674 */     if ((prev != null) && (prev != dser)) {
/*  675 */       dser.moveValueTargets(prev);
/*  676 */       if (dser.getDefaultType() == null)
/*  677 */         dser.setDefaultType(prev.getDefaultType());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void registerElementByID(String id, MessageElement elem)
/*      */   {
/*  694 */     if (this.localIDs == null) {
/*  695 */       this.localIDs = new LocalIDResolver(null);
/*      */     }
/*  697 */     String absID = '#' + id;
/*      */ 
/*  699 */     this.localIDs.addReferencedObject(absID, elem);
/*      */ 
/*  701 */     registerResolverForID(absID, this.localIDs);
/*      */ 
/*  703 */     if (this.fixups != null) {
/*  704 */       Deserializer dser = (Deserializer)this.fixups.get(absID);
/*  705 */       if (dser != null)
/*  706 */         elem.setFixupDeserializer(dser);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void registerResolverForID(String id, IDResolver resolver)
/*      */   {
/*  717 */     if ((id == null) || (resolver == null))
/*      */     {
/*  719 */       return;
/*      */     }
/*      */ 
/*  722 */     if (this.idMap == null) {
/*  723 */       this.idMap = new HashMap();
/*      */     }
/*  725 */     this.idMap.put(id, resolver);
/*      */   }
/*      */ 
/*      */   public boolean hasElementsByID()
/*      */   {
/*  735 */     return this.idMap != null;
/*      */   }
/*      */ 
/*      */   public int getCurrentRecordPos()
/*      */   {
/*  743 */     if (this.recorder == null) return -1;
/*  744 */     return this.recorder.getLength() - 1;
/*      */   }
/*      */ 
/*      */   public int getStartOfMappingsPos()
/*      */   {
/*  752 */     if (this.startOfMappingsPos == -1) {
/*  753 */       return getCurrentRecordPos() + 1;
/*      */     }
/*      */ 
/*  756 */     return this.startOfMappingsPos;
/*      */   }
/*      */ 
/*      */   public void pushNewElement(MessageElement elem)
/*      */   {
/*  764 */     if (this.debugEnabled) {
/*  765 */       log.debug("Pushing element " + elem.getName());
/*      */     }
/*      */ 
/*  768 */     if ((!this.doneParsing) && (this.recorder != null)) {
/*  769 */       this.recorder.newElement(elem);
/*      */     }
/*      */     try
/*      */     {
/*  773 */       if (this.curElement != null) {
/*  774 */         elem.setParentElement(this.curElement);
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*  781 */       log.fatal(Messages.getMessage("exception00"), e);
/*      */     }
/*  783 */     this.curElement = elem;
/*      */ 
/*  785 */     if (elem.getRecorder() != this.recorder)
/*  786 */       this.recorder = elem.getRecorder();
/*      */   }
/*      */ 
/*      */   public void pushElementHandler(SOAPHandler handler)
/*      */   {
/*  795 */     if (this.debugEnabled) {
/*  796 */       log.debug(Messages.getMessage("pushHandler00", "" + handler));
/*      */     }
/*      */ 
/*  799 */     if (this.topHandler != null) this.pushedDownHandlers.add(this.topHandler);
/*  800 */     this.topHandler = handler;
/*      */   }
/*      */ 
/*      */   public void replaceElementHandler(SOAPHandler handler)
/*      */   {
/*  811 */     this.topHandler = handler;
/*      */   }
/*      */ 
/*      */   public SOAPHandler popElementHandler()
/*      */   {
/*  816 */     SOAPHandler result = this.topHandler;
/*      */ 
/*  818 */     int size = this.pushedDownHandlers.size();
/*  819 */     if (size > 0)
/*  820 */       this.topHandler = ((SOAPHandler)this.pushedDownHandlers.remove(size - 1));
/*      */     else {
/*  822 */       this.topHandler = null;
/*      */     }
/*      */ 
/*  825 */     if (this.debugEnabled) {
/*  826 */       if (result == null)
/*  827 */         log.debug(Messages.getMessage("popHandler00", "(null)"));
/*      */       else {
/*  829 */         log.debug(Messages.getMessage("popHandler00", "" + result));
/*      */       }
/*      */     }
/*      */ 
/*  833 */     return result;
/*      */   }
/*      */ 
/*      */   public void setProcessingRef(boolean ref)
/*      */   {
/*  838 */     this.processingRef = ref;
/*      */   }
/*      */   public boolean isProcessingRef() {
/*  841 */     return this.processingRef;
/*      */   }
/*      */ 
/*      */   public void startDocument()
/*      */     throws SAXException
/*      */   {
/*  849 */     if ((!this.doneParsing) && (this.recorder != null))
/*  850 */       this.recorder.startDocument();
/*      */   }
/*      */ 
/*      */   public void endDocument()
/*      */     throws SAXException
/*      */   {
/*  857 */     if (this.debugEnabled) {
/*  858 */       log.debug("Enter: DeserializationContext::endDocument()");
/*      */     }
/*  860 */     if ((!this.doneParsing) && (this.recorder != null)) {
/*  861 */       this.recorder.endDocument();
/*      */     }
/*  863 */     this.doneParsing = true;
/*      */ 
/*  865 */     if (this.debugEnabled)
/*  866 */       log.debug("Exit: DeserializationContext::endDocument()");
/*      */   }
/*      */ 
/*      */   public boolean isDoneParsing()
/*      */   {
/*  872 */     return this.doneParsing;
/*      */   }
/*      */ 
/*      */   public void startPrefixMapping(String prefix, String uri)
/*      */     throws SAXException
/*      */   {
/*  885 */     if (this.debugEnabled) {
/*  886 */       log.debug("Enter: DeserializationContext::startPrefixMapping(" + prefix + ", " + uri + ")");
/*      */     }
/*      */ 
/*  889 */     if ((!this.doneParsing) && (this.recorder != null)) {
/*  890 */       this.recorder.startPrefixMapping(prefix, uri);
/*      */     }
/*      */ 
/*  893 */     if (this.startOfMappingsPos == -1) {
/*  894 */       this.namespaces.push();
/*  895 */       this.startOfMappingsPos = getCurrentRecordPos();
/*      */     }
/*      */ 
/*  898 */     if (prefix != null)
/*  899 */       this.namespaces.add(uri, prefix);
/*      */     else
/*  901 */       this.namespaces.add(uri, "");
/*      */     int i;
/*  904 */     if ((!this.haveSeenSchemaNS) && (this.msgContext != null))
/*      */     {
/*  909 */       for (i = 0; (!this.haveSeenSchemaNS) && (i < schemaVersions.length); )
/*      */       {
/*  911 */         SchemaVersion schemaVersion = schemaVersions[i];
/*  912 */         if ((uri.equals(schemaVersion.getXsdURI())) || (uri.equals(schemaVersion.getXsiURI())))
/*      */         {
/*  914 */           this.msgContext.setSchemaVersion(schemaVersion);
/*  915 */           this.haveSeenSchemaNS = true;
/*      */         }
/*  910 */         i++;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  920 */     if (this.topHandler != null) {
/*  921 */       this.topHandler.startPrefixMapping(prefix, uri);
/*      */     }
/*      */ 
/*  924 */     if (this.debugEnabled)
/*  925 */       log.debug("Exit: DeserializationContext::startPrefixMapping()");
/*      */   }
/*      */ 
/*      */   public void endPrefixMapping(String prefix)
/*      */     throws SAXException
/*      */   {
/*  932 */     if (this.debugEnabled) {
/*  933 */       log.debug("Enter: DeserializationContext::endPrefixMapping(" + prefix + ")");
/*      */     }
/*      */ 
/*  936 */     if ((!this.doneParsing) && (this.recorder != null)) {
/*  937 */       this.recorder.endPrefixMapping(prefix);
/*      */     }
/*      */ 
/*  940 */     if (this.topHandler != null) {
/*  941 */       this.topHandler.endPrefixMapping(prefix);
/*      */     }
/*      */ 
/*  944 */     if (this.debugEnabled)
/*  945 */       log.debug("Exit: DeserializationContext::endPrefixMapping()");
/*      */   }
/*      */ 
/*      */   public void setDocumentLocator(Locator locator)
/*      */   {
/*  951 */     if ((!this.doneParsing) && (this.recorder != null)) {
/*  952 */       this.recorder.setDocumentLocator(locator);
/*      */     }
/*  954 */     this.locator = locator;
/*      */   }
/*      */ 
/*      */   public Locator getDocumentLocator() {
/*  958 */     return this.locator;
/*      */   }
/*      */ 
/*      */   public void characters(char[] p1, int p2, int p3) throws SAXException {
/*  962 */     if ((!this.doneParsing) && (this.recorder != null)) {
/*  963 */       this.recorder.characters(p1, p2, p3);
/*      */     }
/*  965 */     if (this.topHandler != null)
/*  966 */       this.topHandler.characters(p1, p2, p3);
/*      */   }
/*      */ 
/*      */   public void ignorableWhitespace(char[] p1, int p2, int p3) throws SAXException
/*      */   {
/*  971 */     if ((!this.doneParsing) && (this.recorder != null)) {
/*  972 */       this.recorder.ignorableWhitespace(p1, p2, p3);
/*      */     }
/*  974 */     if (this.topHandler != null)
/*  975 */       this.topHandler.ignorableWhitespace(p1, p2, p3);
/*      */   }
/*      */ 
/*      */   public void processingInstruction(String p1, String p2)
/*      */     throws SAXException
/*      */   {
/*  982 */     throw new SAXException(Messages.getMessage("noInstructions00"));
/*      */   }
/*      */ 
/*      */   public void skippedEntity(String p1) throws SAXException {
/*  986 */     if ((!this.doneParsing) && (this.recorder != null)) {
/*  987 */       this.recorder.skippedEntity(p1);
/*      */     }
/*  989 */     this.topHandler.skippedEntity(p1);
/*      */   }
/*      */ 
/*      */   public void startElement(String namespace, String localName, String qName, Attributes attributes)
/*      */     throws SAXException
/*      */   {
/* 1002 */     if (this.debugEnabled) {
/* 1003 */       log.debug("Enter: DeserializationContext::startElement(" + namespace + ", " + localName + ")");
/*      */     }
/*      */ 
/* 1006 */     if ((attributes == null) || (attributes.getLength() == 0)) {
/* 1007 */       attributes = NullAttributes.singleton;
/*      */     } else {
/* 1009 */       attributes = new AttributesImpl(attributes);
/*      */ 
/* 1011 */       SOAPConstants soapConstants = getSOAPConstants();
/* 1012 */       if ((soapConstants == SOAPConstants.SOAP12_CONSTANTS) && 
/* 1013 */         (attributes.getValue(soapConstants.getAttrHref()) != null) && (attributes.getValue("id") != null))
/*      */       {
/* 1016 */         AxisFault fault = new AxisFault(Constants.FAULT_SOAP12_SENDER, null, Messages.getMessage("noIDandHREFonSameElement"), null, null, null);
/*      */ 
/* 1019 */         throw new SAXException(fault);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1026 */     SOAPHandler nextHandler = null;
/*      */ 
/* 1028 */     String prefix = "";
/* 1029 */     int idx = qName.indexOf(':');
/* 1030 */     if (idx > 0) {
/* 1031 */       prefix = qName.substring(0, idx);
/*      */     }
/*      */ 
/* 1034 */     if (this.topHandler != null) {
/* 1035 */       nextHandler = this.topHandler.onStartChild(namespace, localName, prefix, attributes, this);
/*      */     }
/*      */ 
/* 1042 */     if (nextHandler == null) {
/* 1043 */       nextHandler = new SOAPHandler();
/*      */     }
/*      */ 
/* 1046 */     pushElementHandler(nextHandler);
/*      */ 
/* 1048 */     nextHandler.startElement(namespace, localName, prefix, attributes, this);
/*      */ 
/* 1051 */     if ((!this.doneParsing) && (this.recorder != null)) {
/* 1052 */       this.recorder.startElement(namespace, localName, qName, attributes);
/*      */ 
/* 1054 */       if (!this.doneParsing) {
/* 1055 */         this.curElement.setContentsIndex(this.recorder.getLength());
/*      */       }
/*      */     }
/*      */ 
/* 1059 */     if (this.startOfMappingsPos != -1) {
/* 1060 */       this.startOfMappingsPos = -1;
/*      */     }
/*      */     else {
/* 1063 */       this.namespaces.push();
/*      */     }
/*      */ 
/* 1066 */     if (this.debugEnabled)
/* 1067 */       log.debug("Exit: DeserializationContext::startElement()");
/*      */   }
/*      */ 
/*      */   public void endElement(String namespace, String localName, String qName)
/*      */     throws SAXException
/*      */   {
/* 1077 */     if (this.debugEnabled) {
/* 1078 */       log.debug("Enter: DeserializationContext::endElement(" + namespace + ", " + localName + ")");
/*      */     }
/*      */ 
/* 1081 */     if ((!this.doneParsing) && (this.recorder != null)) {
/* 1082 */       this.recorder.endElement(namespace, localName, qName);
/*      */     }
/*      */     try
/*      */     {
/* 1086 */       SOAPHandler handler = popElementHandler();
/* 1087 */       handler.endElement(namespace, localName, this);
/*      */ 
/* 1089 */       if (this.topHandler != null) {
/* 1090 */         this.topHandler.onEndChild(namespace, localName, this);
/*      */       }
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/* 1096 */       if (this.curElement != null) {
/* 1097 */         this.curElement = ((MessageElement)this.curElement.getParentElement());
/*      */       }
/*      */ 
/* 1100 */       this.namespaces.pop();
/*      */ 
/* 1102 */       if (this.debugEnabled) {
/* 1103 */         String name = this.curElement != null ? this.curElement.getClass().getName() + ":" + this.curElement.getName() : null;
/*      */ 
/* 1106 */         log.debug("Popped element stack to " + name);
/* 1107 */         log.debug("Exit: DeserializationContext::endElement()");
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void startDTD(String name, String publicId, String systemId)
/*      */     throws SAXException
/*      */   {
/* 1161 */     throw new SAXException(Messages.getMessage("noInstructions00"));
/*      */   }
/*      */ 
/*      */   public void endDTD()
/*      */     throws SAXException
/*      */   {
/* 1170 */     if (this.recorder != null)
/* 1171 */       this.recorder.endDTD();
/*      */   }
/*      */ 
/*      */   public void startEntity(String name)
/*      */     throws SAXException
/*      */   {
/* 1177 */     if (this.recorder != null)
/* 1178 */       this.recorder.startEntity(name);
/*      */   }
/*      */ 
/*      */   public void endEntity(String name)
/*      */     throws SAXException
/*      */   {
/* 1184 */     if (this.recorder != null)
/* 1185 */       this.recorder.endEntity(name);
/*      */   }
/*      */ 
/*      */   public void startCDATA()
/*      */     throws SAXException
/*      */   {
/* 1191 */     if (this.recorder != null)
/* 1192 */       this.recorder.startCDATA();
/*      */   }
/*      */ 
/*      */   public void endCDATA()
/*      */     throws SAXException
/*      */   {
/* 1198 */     if (this.recorder != null)
/* 1199 */       this.recorder.endCDATA();
/*      */   }
/*      */ 
/*      */   public void comment(char[] ch, int start, int length)
/*      */     throws SAXException
/*      */   {
/* 1207 */     if (this.recorder != null)
/* 1208 */       this.recorder.comment(ch, start, length);
/*      */   }
/*      */ 
/*      */   public InputSource resolveEntity(String publicId, String systemId)
/*      */   {
/* 1213 */     return XMLUtils.getEmptyInputSource();
/*      */   }
/*      */   private static class NullLexicalHandler implements LexicalHandler {
/*      */     private NullLexicalHandler() {
/*      */     }
/*      */     public void startDTD(String arg0, String arg1, String arg2) throws SAXException {
/*      */     }
/*      */     public void endDTD() throws SAXException {  }
/*      */ 
/*      */     public void startEntity(String arg0) throws SAXException {  }
/*      */ 
/*      */     public void endEntity(String arg0) throws SAXException {  }
/*      */ 
/*      */     public void startCDATA() throws SAXException {  }
/*      */ 
/*      */     public void endCDATA() throws SAXException {  }
/*      */ 
/*      */     public void comment(char[] arg0, int arg1, int arg2) throws SAXException {  }
/*      */ 
/* 1226 */     NullLexicalHandler(DeserializationContext.1 x0) { this();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class LocalIDResolver
/*      */     implements IDResolver
/*      */   {
/* 1117 */     HashMap idMap = null;
/*      */ 
/*      */     private LocalIDResolver()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void addReferencedObject(String id, Object referent) {
/* 1124 */       if (this.idMap == null) {
/* 1125 */         this.idMap = new HashMap();
/*      */       }
/*      */ 
/* 1128 */       this.idMap.put(id, referent);
/*      */     }
/*      */ 
/*      */     public Object getReferencedObject(String href)
/*      */     {
/* 1136 */       if ((this.idMap == null) || (href == null)) {
/* 1137 */         return null;
/*      */       }
/* 1139 */       return this.idMap.get(href);
/*      */     }
/*      */ 
/*      */     LocalIDResolver(DeserializationContext.1 x0)
/*      */     {
/* 1115 */       this();
/*      */     }
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.DeserializationContext
 * JD-Core Version:    0.6.0
 */