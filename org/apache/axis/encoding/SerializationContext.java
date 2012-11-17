/*      */ package org.apache.axis.encoding;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.Writer;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.Stack;
/*      */ import javax.xml.namespace.QName;
/*      */ import javax.xml.rpc.JAXRPCException;
/*      */ import javax.xml.rpc.holders.QNameHolder;
/*      */ import org.apache.axis.AxisProperties;
/*      */ import org.apache.axis.Constants;
/*      */ import org.apache.axis.Message;
/*      */ import org.apache.axis.MessageContext;
/*      */ import org.apache.axis.attachments.Attachments;
/*      */ import org.apache.axis.components.encoding.XMLEncoder;
/*      */ import org.apache.axis.components.logger.LogFactory;
/*      */ import org.apache.axis.constants.Use;
/*      */ import org.apache.axis.description.OperationDesc;
/*      */ import org.apache.axis.description.TypeDesc;
/*      */ import org.apache.axis.encoding.ser.ArraySerializer;
/*      */ import org.apache.axis.encoding.ser.BaseSerializerFactory;
/*      */ import org.apache.axis.encoding.ser.SimpleListSerializerFactory;
/*      */ import org.apache.axis.handlers.soap.SOAPService;
/*      */ import org.apache.axis.schema.SchemaVersion;
/*      */ import org.apache.axis.soap.SOAPConstants;
/*      */ import org.apache.axis.types.HexBinary;
/*      */ import org.apache.axis.utils.IDKey;
/*      */ import org.apache.axis.utils.JavaUtils;
/*      */ import org.apache.axis.utils.Mapping;
/*      */ import org.apache.axis.utils.Messages;
/*      */ import org.apache.axis.utils.NSStack;
/*      */ import org.apache.axis.utils.XMLUtils;
/*      */ import org.apache.axis.utils.cache.MethodCache;
/*      */ import org.apache.axis.wsdl.symbolTable.SchemaUtils;
/*      */ import org.apache.axis.wsdl.symbolTable.Utils;
/*      */ import org.apache.commons.logging.Log;
/*      */ import org.w3c.dom.Attr;
/*      */ import org.w3c.dom.CDATASection;
/*      */ import org.w3c.dom.CharacterData;
/*      */ import org.w3c.dom.Comment;
/*      */ import org.w3c.dom.Element;
/*      */ import org.w3c.dom.NamedNodeMap;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.NodeList;
/*      */ import org.xml.sax.Attributes;
/*      */ import org.xml.sax.helpers.AttributesImpl;
/*      */ 
/*      */ public class SerializationContext
/*      */   implements javax.xml.rpc.encoding.SerializationContext
/*      */ {
/*   86 */   protected static Log log = LogFactory.getLog(SerializationContext.class.getName());
/*      */ 
/*   92 */   private final boolean debugEnabled = log.isDebugEnabled();
/*      */ 
/*   94 */   private NSStack nsStack = null;
/*   95 */   private boolean writingStartTag = false;
/*   96 */   private boolean onlyXML = true;
/*   97 */   private int indent = 0;
/*   98 */   private Stack elementStack = new Stack();
/*      */   private Writer writer;
/*  100 */   private int lastPrefixIndex = 1;
/*      */   private MessageContext msgContext;
/*      */   private QName currentXMLType;
/*      */   private QName itemQName;
/*      */   private QName itemType;
/*  109 */   private SOAPConstants soapConstants = SOAPConstants.SOAP11_CONSTANTS;
/*      */ 
/*  111 */   private static QName multirefQName = new QName("", "multiRef");
/*  112 */   private static Class[] SERIALIZER_CLASSES = { String.class, Class.class, QName.class };
/*      */   private static final String SERIALIZER_METHOD = "getSerializer";
/*  124 */   private boolean doMultiRefs = false;
/*      */ 
/*  129 */   private boolean disablePrettyXML = false;
/*      */ 
/*  135 */   private boolean enableNamespacePrefixOptimization = false;
/*      */ 
/*  140 */   private boolean pretty = false;
/*      */ 
/*  145 */   private boolean sendXMLDecl = true;
/*      */ 
/*  150 */   private boolean sendXSIType = true;
/*      */ 
/*  156 */   private Boolean sendNull = Boolean.TRUE;
/*      */ 
/*  162 */   private HashMap multiRefValues = null;
/*  163 */   private int multiRefIndex = -1;
/*  164 */   private boolean noNamespaceMappings = true;
/*      */   private QName writeXMLType;
/*  166 */   private XMLEncoder encoder = null;
/*      */ 
/*  169 */   protected boolean startOfDocument = true;
/*      */ 
/*  172 */   private String encoding = "UTF-8";
/*      */ 
/*  200 */   private HashSet secondLevelObjects = null;
/*  201 */   private Object forceSer = null;
/*  202 */   private boolean outputMultiRefsFlag = false;
/*      */ 
/*  207 */   SchemaVersion schemaVersion = SchemaVersion.SCHEMA_2001;
/*      */ 
/*  213 */   HashMap preferredPrefixes = new HashMap();
/*      */ 
/*      */   public SerializationContext(Writer writer)
/*      */   {
/*  221 */     this.writer = writer;
/*  222 */     initialize();
/*      */   }
/*      */ 
/*      */   private void initialize()
/*      */   {
/*  228 */     this.preferredPrefixes.put(this.soapConstants.getEncodingURI(), "soapenc");
/*      */ 
/*  230 */     this.preferredPrefixes.put("http://www.w3.org/XML/1998/namespace", "xml");
/*      */ 
/*  232 */     this.preferredPrefixes.put(this.schemaVersion.getXsdURI(), "xsd");
/*      */ 
/*  234 */     this.preferredPrefixes.put(this.schemaVersion.getXsiURI(), "xsi");
/*      */ 
/*  236 */     this.preferredPrefixes.put(this.soapConstants.getEnvelopeURI(), "soapenv");
/*      */ 
/*  238 */     this.nsStack = new NSStack(this.enableNamespacePrefixOptimization);
/*      */   }
/*      */ 
/*      */   public SerializationContext(Writer writer, MessageContext msgContext)
/*      */   {
/*  249 */     this.writer = writer;
/*  250 */     this.msgContext = msgContext;
/*      */ 
/*  252 */     if (msgContext != null) {
/*  253 */       this.soapConstants = msgContext.getSOAPConstants();
/*      */ 
/*  256 */       this.schemaVersion = msgContext.getSchemaVersion();
/*      */ 
/*  258 */       Boolean shouldSendDecl = (Boolean)msgContext.getProperty("sendXMLDeclaration");
/*      */ 
/*  260 */       if (shouldSendDecl != null) {
/*  261 */         this.sendXMLDecl = shouldSendDecl.booleanValue();
/*      */       }
/*  263 */       Boolean shouldSendMultiRefs = (Boolean)msgContext.getProperty("sendMultiRefs");
/*      */ 
/*  265 */       if (shouldSendMultiRefs != null) {
/*  266 */         this.doMultiRefs = shouldSendMultiRefs.booleanValue();
/*      */       }
/*  268 */       Boolean shouldDisablePrettyXML = (Boolean)msgContext.getProperty("disablePrettyXML");
/*      */ 
/*  270 */       if (shouldDisablePrettyXML != null) {
/*  271 */         this.disablePrettyXML = shouldDisablePrettyXML.booleanValue();
/*      */       }
/*  273 */       Boolean shouldDisableNamespacePrefixOptimization = (Boolean)msgContext.getProperty("enableNamespacePrefixOptimization");
/*      */ 
/*  275 */       if (shouldDisableNamespacePrefixOptimization != null)
/*  276 */         this.enableNamespacePrefixOptimization = shouldDisableNamespacePrefixOptimization.booleanValue();
/*      */       else {
/*  278 */         this.enableNamespacePrefixOptimization = JavaUtils.isTrue(AxisProperties.getProperty("enableNamespacePrefixOptimization", "true"));
/*      */       }
/*      */ 
/*  281 */       boolean sendTypesDefault = this.sendXSIType;
/*      */ 
/*  285 */       OperationDesc operation = msgContext.getOperation();
/*  286 */       if (operation != null) {
/*  287 */         if (operation.getUse() != Use.ENCODED) {
/*  288 */           this.doMultiRefs = false;
/*  289 */           sendTypesDefault = false;
/*      */         }
/*      */       }
/*      */       else {
/*  293 */         SOAPService service = msgContext.getService();
/*  294 */         if ((service != null) && 
/*  295 */           (service.getUse() != Use.ENCODED)) {
/*  296 */           this.doMultiRefs = false;
/*  297 */           sendTypesDefault = false;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  305 */       if (!msgContext.isPropertyTrue("sendXsiTypes", sendTypesDefault)) {
/*  306 */         this.sendXSIType = false;
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  316 */       this.enableNamespacePrefixOptimization = JavaUtils.isTrue(AxisProperties.getProperty("enableNamespacePrefixOptimization", "true"));
/*      */ 
/*  318 */       this.disablePrettyXML = JavaUtils.isTrue(AxisProperties.getProperty("disablePrettyXML", "true"));
/*      */     }
/*      */ 
/*  323 */     initialize();
/*      */   }
/*      */ 
/*      */   public boolean getPretty()
/*      */   {
/*  331 */     return this.pretty;
/*      */   }
/*      */ 
/*      */   public void setPretty(boolean pretty)
/*      */   {
/*  339 */     if (!this.disablePrettyXML)
/*  340 */       this.pretty = pretty;
/*      */   }
/*      */ 
/*      */   public boolean getDoMultiRefs()
/*      */   {
/*  349 */     return this.doMultiRefs;
/*      */   }
/*      */ 
/*      */   public void setDoMultiRefs(boolean shouldDo)
/*      */   {
/*  357 */     this.doMultiRefs = shouldDo;
/*      */   }
/*      */ 
/*      */   public void setSendDecl(boolean sendDecl)
/*      */   {
/*  366 */     this.sendXMLDecl = sendDecl;
/*      */   }
/*      */ 
/*      */   public boolean shouldSendXSIType()
/*      */   {
/*  374 */     return this.sendXSIType;
/*      */   }
/*      */ 
/*      */   public TypeMapping getTypeMapping()
/*      */   {
/*  384 */     if (this.msgContext == null) {
/*  385 */       return DefaultTypeMappingImpl.getSingletonDelegate();
/*      */     }
/*  387 */     String encodingStyle = this.msgContext.getEncodingStyle();
/*  388 */     if (encodingStyle == null)
/*  389 */       encodingStyle = this.soapConstants.getEncodingURI();
/*  390 */     return (TypeMapping)this.msgContext.getTypeMappingRegistry().getTypeMapping(encodingStyle);
/*      */   }
/*      */ 
/*      */   public TypeMappingRegistry getTypeMappingRegistry()
/*      */   {
/*  399 */     if (this.msgContext == null)
/*  400 */       return null;
/*  401 */     return this.msgContext.getTypeMappingRegistry();
/*      */   }
/*      */ 
/*      */   public String getPrefixForURI(String uri)
/*      */   {
/*  415 */     return getPrefixForURI(uri, null, false);
/*      */   }
/*      */ 
/*      */   public String getPrefixForURI(String uri, String defaultPrefix)
/*      */   {
/*  426 */     return getPrefixForURI(uri, defaultPrefix, false);
/*      */   }
/*      */ 
/*      */   public String getPrefixForURI(String uri, String defaultPrefix, boolean attribute)
/*      */   {
/*  437 */     if ((uri == null) || (uri.length() == 0)) {
/*  438 */       return null;
/*      */     }
/*      */ 
/*  442 */     String prefix = this.nsStack.getPrefix(uri, attribute);
/*      */ 
/*  444 */     if (prefix == null) {
/*  445 */       prefix = (String)this.preferredPrefixes.get(uri);
/*      */ 
/*  447 */       if (prefix == null) {
/*  448 */         if (defaultPrefix == null) {
/*  449 */           prefix = "ns" + this.lastPrefixIndex++;
/*  450 */           while (this.nsStack.getNamespaceURI(prefix) != null) {
/*  451 */             prefix = "ns" + this.lastPrefixIndex++;
/*      */           }
/*      */         }
/*  454 */         prefix = defaultPrefix;
/*      */       }
/*      */ 
/*  458 */       registerPrefixForURI(prefix, uri);
/*      */     }
/*      */ 
/*  461 */     return prefix;
/*      */   }
/*      */ 
/*      */   public void registerPrefixForURI(String prefix, String uri)
/*      */   {
/*  471 */     if (this.debugEnabled) {
/*  472 */       log.debug(Messages.getMessage("register00", prefix, uri));
/*      */     }
/*      */ 
/*  475 */     if ((uri != null) && (prefix != null)) {
/*  476 */       if (this.noNamespaceMappings) {
/*  477 */         this.nsStack.push();
/*  478 */         this.noNamespaceMappings = false;
/*      */       }
/*  480 */       String activePrefix = this.nsStack.getPrefix(uri, true);
/*  481 */       if ((activePrefix == null) || (!activePrefix.equals(prefix)))
/*  482 */         this.nsStack.add(uri, prefix);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Message getCurrentMessage()
/*      */   {
/*  492 */     if (this.msgContext == null)
/*  493 */       return null;
/*  494 */     return this.msgContext.getCurrentMessage();
/*      */   }
/*      */ 
/*      */   public MessageContext getMessageContext()
/*      */   {
/*  501 */     return this.msgContext;
/*      */   }
/*      */ 
/*      */   public String getEncodingStyle()
/*      */   {
/*  512 */     return this.msgContext == null ? Use.DEFAULT.getEncoding() : this.msgContext.getEncodingStyle();
/*      */   }
/*      */ 
/*      */   public boolean isEncoded()
/*      */   {
/*  521 */     return Constants.isSOAP_ENC(getEncodingStyle());
/*      */   }
/*      */ 
/*      */   public String qName2String(QName qName, boolean writeNS)
/*      */   {
/*  531 */     String prefix = null;
/*  532 */     String namespaceURI = qName.getNamespaceURI();
/*  533 */     String localPart = qName.getLocalPart();
/*      */ 
/*  535 */     if ((localPart != null) && (localPart.length() > 0)) {
/*  536 */       int index = localPart.indexOf(':');
/*  537 */       if (index != -1) {
/*  538 */         prefix = localPart.substring(0, index);
/*  539 */         if ((prefix.length() > 0) && (!prefix.equals("urn"))) {
/*  540 */           registerPrefixForURI(prefix, namespaceURI);
/*  541 */           localPart = localPart.substring(index + 1);
/*      */         } else {
/*  543 */           prefix = null;
/*      */         }
/*      */       }
/*  546 */       localPart = Utils.getLastLocalPart(localPart);
/*      */     }
/*      */ 
/*  549 */     if (namespaceURI.length() == 0) {
/*  550 */       if (writeNS)
/*      */       {
/*  553 */         String defaultNS = this.nsStack.getNamespaceURI("");
/*  554 */         if ((defaultNS != null) && (defaultNS.length() > 0))
/*  555 */           registerPrefixForURI("", "");
/*      */       }
/*      */     }
/*      */     else {
/*  559 */       prefix = getPrefixForURI(namespaceURI);
/*      */     }
/*      */ 
/*  562 */     if ((prefix == null) || (prefix.length() == 0)) {
/*  563 */       return localPart;
/*      */     }
/*  565 */     return prefix + ':' + localPart;
/*      */   }
/*      */ 
/*      */   public String qName2String(QName qName)
/*      */   {
/*  570 */     return qName2String(qName, false);
/*      */   }
/*      */ 
/*      */   public String attributeQName2String(QName qName)
/*      */   {
/*  583 */     String prefix = null;
/*  584 */     String uri = qName.getNamespaceURI();
/*  585 */     if (uri.length() > 0) {
/*  586 */       prefix = getPrefixForURI(uri, null, true);
/*      */     }
/*      */ 
/*  589 */     if ((prefix == null) || (prefix.length() == 0)) {
/*  590 */       return qName.getLocalPart();
/*      */     }
/*  592 */     return prefix + ':' + qName.getLocalPart();
/*      */   }
/*      */ 
/*      */   public QName getQNameForClass(Class cls)
/*      */   {
/*  602 */     return getTypeMapping().getTypeQName(cls);
/*      */   }
/*      */ 
/*      */   public boolean isPrimitive(Object value)
/*      */   {
/*  615 */     if (value == null) return true;
/*      */ 
/*  617 */     Class javaType = value.getClass();
/*      */ 
/*  619 */     if (javaType.isPrimitive()) return true;
/*      */ 
/*  621 */     if (javaType == String.class) return true;
/*  622 */     if (Calendar.class.isAssignableFrom(javaType)) return true;
/*  623 */     if (Date.class.isAssignableFrom(javaType)) return true;
/*  624 */     if (HexBinary.class.isAssignableFrom(javaType)) return true;
/*  625 */     if (Element.class.isAssignableFrom(javaType)) return true;
/*  626 */     if (javaType == new byte[0].getClass()) return true;
/*      */ 
/*  644 */     if (javaType.isArray()) return true;
/*      */ 
/*  650 */     QName qName = getQNameForClass(javaType);
/*      */ 
/*  653 */     return (qName != null) && (Constants.isSchemaXSD(qName.getNamespaceURI())) && 
/*  652 */       (SchemaUtils.isSimpleSchemaType(qName));
/*      */   }
/*      */ 
/*      */   public void serialize(QName elemQName, Attributes attributes, Object value)
/*      */     throws IOException
/*      */   {
/*  680 */     serialize(elemQName, attributes, value, null, null, null, null);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void serialize(QName elemQName, Attributes attributes, Object value, QName xmlType)
/*      */     throws IOException
/*      */   {
/*  707 */     serialize(elemQName, attributes, value, xmlType, null, null, null);
/*      */   }
/*      */ 
/*      */   public void serialize(QName elemQName, Attributes attributes, Object value, QName xmlType, Class javaType)
/*      */     throws IOException
/*      */   {
/*  734 */     serialize(elemQName, attributes, value, xmlType, javaType, null, null);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void serialize(QName elemQName, Attributes attributes, Object value, QName xmlType, boolean sendNull, Boolean sendType)
/*      */     throws IOException
/*      */   {
/*  768 */     serialize(elemQName, attributes, value, xmlType, null, sendNull ? Boolean.TRUE : Boolean.FALSE, sendType);
/*      */   }
/*      */ 
/*      */   public void serialize(QName elemQName, Attributes attributes, Object value, QName xmlType, Boolean sendNull, Boolean sendType)
/*      */     throws IOException
/*      */   {
/*  801 */     serialize(elemQName, attributes, value, xmlType, null, sendNull, sendType);
/*      */   }
/*      */ 
/*      */   public void serialize(QName elemQName, Attributes attributes, Object value, QName xmlType, Class javaClass, Boolean sendNull, Boolean sendType)
/*      */     throws IOException
/*      */   {
/*  835 */     boolean sendXSITypeCache = this.sendXSIType;
/*  836 */     if (sendType != null) {
/*  837 */       this.sendXSIType = sendType.booleanValue();
/*      */     }
/*  839 */     boolean shouldSendType = shouldSendXSIType();
/*      */     try
/*      */     {
/*  842 */       Boolean sendNullCache = this.sendNull;
/*  843 */       if (sendNull != null)
/*  844 */         this.sendNull = sendNull;
/*      */       else {
/*  846 */         sendNull = this.sendNull;
/*      */       }
/*      */ 
/*  849 */       if (value == null)
/*      */       {
/*  852 */         if (this.sendNull.booleanValue()) {
/*  853 */           AttributesImpl attrs = new AttributesImpl();
/*  854 */           if ((attributes != null) && (0 < attributes.getLength()))
/*  855 */             attrs.setAttributes(attributes);
/*  856 */           if (shouldSendType)
/*  857 */             attrs = (AttributesImpl)setTypeAttribute(attrs, xmlType);
/*  858 */           String nil = this.schemaVersion.getNilQName().getLocalPart();
/*  859 */           attrs.addAttribute(this.schemaVersion.getXsiURI(), nil, "xsi:" + nil, "CDATA", "true");
/*      */ 
/*  861 */           startElement(elemQName, attrs);
/*  862 */           endElement();
/*  864 */         }this.sendNull = sendNullCache;
/*      */         return;
/*      */       }
/*  868 */       Message msg = getCurrentMessage();
/*  869 */       if (null != msg)
/*      */       {
/*  871 */         Attachments attachments = getCurrentMessage().getAttachmentsImpl();
/*      */ 
/*  873 */         if ((null != attachments) && (attachments.isAttachment(value)))
/*      */         {
/*  877 */           serializeActual(elemQName, attributes, value, xmlType, javaClass, sendType);
/*      */ 
/*  882 */           this.sendNull = sendNullCache;
/*      */           return;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  901 */       if ((this.doMultiRefs) && (isEncoded()) && (value != this.forceSer) && (!isPrimitive(value)))
/*      */       {
/*  903 */         if (this.multiRefIndex == -1) {
/*  904 */           this.multiRefValues = new HashMap();
/*      */         }
/*      */ 
/*  909 */         MultiRefItem mri = (MultiRefItem)this.multiRefValues.get(getIdentityKey(value));
/*      */         String id;
/*  911 */         if (mri == null)
/*      */         {
/*  914 */           this.multiRefIndex += 1;
/*  915 */           String id = "id" + this.multiRefIndex;
/*  916 */           mri = new MultiRefItem(id, xmlType, sendType, value);
/*  917 */           this.multiRefValues.put(getIdentityKey(value), mri);
/*      */ 
/*  923 */           if (this.soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
/*  924 */             AttributesImpl attrs = new AttributesImpl();
/*  925 */             if ((attributes != null) && (0 < attributes.getLength())) {
/*  926 */               attrs.setAttributes(attributes);
/*      */             }
/*      */ 
/*  927 */             attrs.addAttribute("", "id", "id", "CDATA", id);
/*      */ 
/*  929 */             serializeActual(elemQName, attrs, value, xmlType, javaClass, sendType);
/*  930 */             this.sendNull = sendNullCache;
/*      */             return;
/*      */           }
/*      */ 
/*  948 */           if (this.outputMultiRefsFlag) {
/*  949 */             if (this.secondLevelObjects == null)
/*  950 */               this.secondLevelObjects = new HashSet();
/*  951 */             this.secondLevelObjects.add(getIdentityKey(value));
/*      */           }
/*      */         }
/*      */         else {
/*  955 */           id = mri.id;
/*      */         }
/*      */ 
/*  959 */         AttributesImpl attrs = new AttributesImpl();
/*  960 */         if ((attributes != null) && (0 < attributes.getLength())) {
/*  961 */           attrs.setAttributes(attributes);
/*      */         }
/*  962 */         attrs.addAttribute("", this.soapConstants.getAttrHref(), this.soapConstants.getAttrHref(), "CDATA", '#' + id);
/*      */ 
/*  965 */         startElement(elemQName, attrs);
/*  966 */         endElement();
/*  967 */         this.sendNull = sendNullCache;
/*      */         return;
/*      */       }
/*      */ 
/*  976 */       if (value == this.forceSer) {
/*  977 */         this.forceSer = null;
/*      */       }
/*      */ 
/*  980 */       serializeActual(elemQName, attributes, value, xmlType, javaClass, sendType);
/*      */     } finally {
/*  982 */       this.sendXSIType = sendXSITypeCache;
/*      */     }
/*      */   }
/*      */ 
/*      */   private IDKey getIdentityKey(Object value)
/*      */   {
/*  996 */     return new IDKey(value);
/*      */   }
/*      */ 
/*      */   public void outputMultiRefs()
/*      */     throws IOException
/*      */   {
/* 1006 */     if ((!this.doMultiRefs) || (this.multiRefValues == null) || (this.soapConstants == SOAPConstants.SOAP12_CONSTANTS))
/*      */     {
/* 1008 */       return;
/* 1009 */     }this.outputMultiRefsFlag = true;
/* 1010 */     AttributesImpl attrs = new AttributesImpl();
/* 1011 */     attrs.addAttribute("", "", "", "", "");
/*      */ 
/* 1013 */     String encodingURI = this.soapConstants.getEncodingURI();
/*      */ 
/* 1015 */     String prefix = getPrefixForURI(encodingURI);
/* 1016 */     String root = prefix + ":root";
/* 1017 */     attrs.addAttribute(encodingURI, "root", root, "CDATA", "0");
/*      */     String encodingStyle;
/*      */     String encodingStyle;
/* 1023 */     if (this.msgContext != null)
/* 1024 */       encodingStyle = this.msgContext.getEncodingStyle();
/*      */     else {
/* 1026 */       encodingStyle = this.soapConstants.getEncodingURI();
/*      */     }
/* 1028 */     String encStyle = getPrefixForURI(this.soapConstants.getEnvelopeURI()) + ':' + "encodingStyle";
/*      */ 
/* 1030 */     attrs.addAttribute(this.soapConstants.getEnvelopeURI(), "encodingStyle", encStyle, "CDATA", encodingStyle);
/*      */ 
/* 1038 */     HashSet keys = new HashSet();
/* 1039 */     keys.addAll(this.multiRefValues.keySet());
/* 1040 */     Iterator i = keys.iterator();
/* 1041 */     while (i.hasNext()) {
/* 1042 */       while (i.hasNext()) {
/* 1043 */         AttributesImpl attrs2 = new AttributesImpl(attrs);
/* 1044 */         Object val = i.next();
/* 1045 */         MultiRefItem mri = (MultiRefItem)this.multiRefValues.get(val);
/* 1046 */         attrs2.setAttribute(0, "", "id", "id", "CDATA", mri.id);
/*      */ 
/* 1049 */         this.forceSer = mri.value;
/*      */ 
/* 1055 */         serialize(multirefQName, attrs2, mri.value, mri.xmlType, null, this.sendNull, Boolean.TRUE);
/*      */       }
/*      */ 
/* 1066 */       if (this.secondLevelObjects != null) {
/* 1067 */         i = this.secondLevelObjects.iterator();
/* 1068 */         this.secondLevelObjects = null;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1073 */     this.forceSer = null;
/* 1074 */     this.outputMultiRefsFlag = false;
/* 1075 */     this.multiRefValues = null;
/* 1076 */     this.multiRefIndex = -1;
/* 1077 */     this.secondLevelObjects = null;
/*      */   }
/*      */ 
/*      */   public void writeXMLDeclaration() throws IOException {
/* 1081 */     this.writer.write("<?xml version=\"1.0\" encoding=\"");
/* 1082 */     this.writer.write(this.encoding);
/* 1083 */     this.writer.write("\"?>");
/* 1084 */     this.startOfDocument = false;
/*      */   }
/*      */ 
/*      */   public void startElement(QName qName, Attributes attributes)
/*      */     throws IOException
/*      */   {
/* 1096 */     ArrayList vecQNames = null;
/* 1097 */     if (this.debugEnabled) {
/* 1098 */       log.debug(Messages.getMessage("startElem00", "[" + qName.getNamespaceURI() + "]:" + qName.getLocalPart()));
/*      */     }
/*      */ 
/* 1102 */     if ((this.startOfDocument) && (this.sendXMLDecl)) {
/* 1103 */       writeXMLDeclaration();
/*      */     }
/*      */ 
/* 1106 */     if (this.writingStartTag) {
/* 1107 */       this.writer.write(62);
/* 1108 */       if (this.pretty) this.writer.write(10);
/* 1109 */       this.indent += 1;
/*      */     }
/*      */ 
/* 1112 */     if (this.pretty) for (int i = 0; i < this.indent; i++) this.writer.write(32);
/* 1113 */     String elementQName = qName2String(qName, true);
/* 1114 */     this.writer.write(60);
/*      */ 
/* 1116 */     this.writer.write(elementQName);
/*      */ 
/* 1118 */     if (this.writeXMLType != null) {
/* 1119 */       attributes = setTypeAttribute(attributes, this.writeXMLType);
/* 1120 */       this.writeXMLType = null;
/*      */     }
/*      */ 
/* 1123 */     if (attributes != null) {
/* 1124 */       for (int i = 0; i < attributes.getLength(); i++) {
/* 1125 */         String qname = attributes.getQName(i);
/* 1126 */         this.writer.write(32);
/*      */ 
/* 1128 */         String prefix = "";
/* 1129 */         String uri = attributes.getURI(i);
/* 1130 */         if ((uri != null) && (uri.length() > 0)) {
/* 1131 */           if (qname.length() == 0)
/*      */           {
/* 1133 */             prefix = getPrefixForURI(uri);
/*      */           }
/*      */           else {
/* 1136 */             int idx = qname.indexOf(':');
/* 1137 */             if (idx > -1) {
/* 1138 */               prefix = qname.substring(0, idx);
/* 1139 */               prefix = getPrefixForURI(uri, prefix, true);
/*      */             }
/*      */           }
/*      */ 
/* 1143 */           if (prefix.length() > 0)
/* 1144 */             qname = prefix + ':' + attributes.getLocalName(i);
/*      */           else
/* 1146 */             qname = attributes.getLocalName(i);
/*      */         }
/*      */         else {
/* 1149 */           qname = attributes.getQName(i);
/* 1150 */           if (qname.length() == 0) {
/* 1151 */             qname = attributes.getLocalName(i);
/*      */           }
/*      */         }
/* 1154 */         if (qname.startsWith("xmlns")) {
/* 1155 */           if (vecQNames == null) vecQNames = new ArrayList();
/* 1156 */           vecQNames.add(qname);
/*      */         }
/* 1158 */         this.writer.write(qname);
/* 1159 */         this.writer.write("=\"");
/*      */ 
/* 1161 */         getEncoder().writeEncoded(this.writer, attributes.getValue(i));
/*      */ 
/* 1163 */         this.writer.write(34);
/*      */       }
/*      */     }
/*      */ 
/* 1167 */     if (this.noNamespaceMappings) {
/* 1168 */       this.nsStack.push();
/*      */     } else {
/* 1170 */       for (Mapping map = this.nsStack.topOfFrame(); map != null; map = this.nsStack.next()) {
/* 1171 */         if (((map.getNamespaceURI().equals("http://www.w3.org/2000/xmlns/")) && (map.getPrefix().equals("xmlns"))) || ((map.getNamespaceURI().equals("http://www.w3.org/XML/1998/namespace")) && (map.getPrefix().equals("xml")))) {
/*      */           continue;
/*      */         }
/* 1174 */         StringBuffer sb = new StringBuffer("xmlns");
/* 1175 */         if (map.getPrefix().length() > 0) {
/* 1176 */           sb.append(':');
/* 1177 */           sb.append(map.getPrefix());
/*      */         }
/* 1179 */         if ((vecQNames == null) || (vecQNames.indexOf(sb.toString()) == -1)) {
/* 1180 */           this.writer.write(32);
/* 1181 */           sb.append("=\"");
/* 1182 */           sb.append(map.getNamespaceURI());
/* 1183 */           sb.append('"');
/* 1184 */           this.writer.write(sb.toString());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1189 */       this.noNamespaceMappings = true;
/*      */     }
/*      */ 
/* 1192 */     this.writingStartTag = true;
/*      */ 
/* 1194 */     this.elementStack.push(elementQName);
/*      */ 
/* 1196 */     this.onlyXML = true;
/*      */   }
/*      */ 
/*      */   public void endElement()
/*      */     throws IOException
/*      */   {
/* 1205 */     String elementQName = (String)this.elementStack.pop();
/*      */ 
/* 1207 */     if (this.debugEnabled) {
/* 1208 */       log.debug(Messages.getMessage("endElem00", "" + elementQName));
/*      */     }
/*      */ 
/* 1211 */     this.nsStack.pop();
/*      */ 
/* 1213 */     if (this.writingStartTag) {
/* 1214 */       this.writer.write("/>");
/* 1215 */       if (this.pretty) this.writer.write(10);
/* 1216 */       this.writingStartTag = false;
/* 1217 */       return;
/*      */     }
/*      */ 
/* 1220 */     if (this.onlyXML) {
/* 1221 */       this.indent -= 1;
/* 1222 */       if (this.pretty) for (int i = 0; i < this.indent; i++) this.writer.write(32);
/*      */     }
/* 1224 */     this.writer.write("</");
/* 1225 */     this.writer.write(elementQName);
/* 1226 */     this.writer.write(62);
/* 1227 */     if ((this.pretty) && (this.indent > 0)) this.writer.write(10);
/* 1228 */     this.onlyXML = true;
/*      */   }
/*      */ 
/*      */   public void writeChars(char[] p1, int p2, int p3)
/*      */     throws IOException
/*      */   {
/* 1241 */     if ((this.startOfDocument) && (this.sendXMLDecl)) {
/* 1242 */       writeXMLDeclaration();
/*      */     }
/*      */ 
/* 1245 */     if (this.writingStartTag) {
/* 1246 */       this.writer.write(62);
/* 1247 */       this.writingStartTag = false;
/*      */     }
/* 1249 */     writeSafeString(String.valueOf(p1, p2, p3));
/* 1250 */     this.onlyXML = false;
/*      */   }
/*      */ 
/*      */   public void writeString(String string)
/*      */     throws IOException
/*      */   {
/* 1260 */     if ((this.startOfDocument) && (this.sendXMLDecl)) {
/* 1261 */       writeXMLDeclaration();
/*      */     }
/*      */ 
/* 1264 */     if (this.writingStartTag) {
/* 1265 */       this.writer.write(62);
/* 1266 */       this.writingStartTag = false;
/*      */     }
/* 1268 */     this.writer.write(string);
/* 1269 */     this.onlyXML = false;
/*      */   }
/*      */ 
/*      */   public void writeSafeString(String string)
/*      */     throws IOException
/*      */   {
/* 1280 */     if ((this.startOfDocument) && (this.sendXMLDecl)) {
/* 1281 */       writeXMLDeclaration();
/*      */     }
/*      */ 
/* 1284 */     if (this.writingStartTag) {
/* 1285 */       this.writer.write(62);
/* 1286 */       this.writingStartTag = false;
/*      */     }
/*      */ 
/* 1289 */     getEncoder().writeEncoded(this.writer, string);
/* 1290 */     this.onlyXML = false;
/*      */   }
/*      */ 
/*      */   public void writeDOMElement(Element el)
/*      */     throws IOException
/*      */   {
/* 1300 */     if ((this.startOfDocument) && (this.sendXMLDecl)) {
/* 1301 */       writeXMLDeclaration();
/*      */     }
/*      */ 
/* 1305 */     if ((el instanceof org.apache.axis.message.Text)) {
/* 1306 */       writeSafeString(((org.w3c.dom.Text)el).getData());
/* 1307 */       return;
/*      */     }
/*      */ 
/* 1310 */     AttributesImpl attributes = null;
/* 1311 */     NamedNodeMap attrMap = el.getAttributes();
/*      */ 
/* 1313 */     if (attrMap.getLength() > 0) {
/* 1314 */       attributes = new AttributesImpl();
/* 1315 */       for (int i = 0; i < attrMap.getLength(); i++) {
/* 1316 */         Attr attr = (Attr)attrMap.item(i);
/* 1317 */         String tmp = attr.getNamespaceURI();
/* 1318 */         if ((tmp != null) && (tmp.equals("http://www.w3.org/2000/xmlns/"))) {
/* 1319 */           String prefix = attr.getLocalName();
/* 1320 */           if (prefix != null) {
/* 1321 */             if (prefix.equals("xmlns"))
/* 1322 */               prefix = "";
/* 1323 */             String nsURI = attr.getValue();
/* 1324 */             registerPrefixForURI(prefix, nsURI);
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 1329 */           attributes.addAttribute(attr.getNamespaceURI(), attr.getLocalName(), attr.getName(), "CDATA", attr.getValue());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1336 */     String namespaceURI = el.getNamespaceURI();
/* 1337 */     String localPart = el.getLocalName();
/* 1338 */     if ((namespaceURI == null) || (namespaceURI.length() == 0))
/* 1339 */       localPart = el.getNodeName();
/* 1340 */     QName qName = new QName(namespaceURI, localPart);
/*      */ 
/* 1342 */     startElement(qName, attributes);
/*      */ 
/* 1344 */     NodeList children = el.getChildNodes();
/* 1345 */     for (int i = 0; i < children.getLength(); i++) {
/* 1346 */       Node child = children.item(i);
/* 1347 */       if ((child instanceof Element)) {
/* 1348 */         writeDOMElement((Element)child);
/* 1349 */       } else if ((child instanceof CDATASection)) {
/* 1350 */         writeString("<![CDATA[");
/* 1351 */         writeString(((org.w3c.dom.Text)child).getData());
/* 1352 */         writeString("]]>");
/* 1353 */       } else if ((child instanceof Comment)) {
/* 1354 */         writeString("<!--");
/* 1355 */         writeString(((CharacterData)child).getData());
/* 1356 */         writeString("-->");
/* 1357 */       } else if ((child instanceof org.w3c.dom.Text)) {
/* 1358 */         writeSafeString(((org.w3c.dom.Text)child).getData());
/*      */       }
/*      */     }
/*      */ 
/* 1362 */     endElement();
/*      */   }
/*      */ 
/*      */   public final Serializer getSerializerForJavaType(Class javaType)
/*      */   {
/* 1372 */     SerializerFactory serF = null;
/* 1373 */     Serializer ser = null;
/*      */     try {
/* 1375 */       serF = (SerializerFactory)getTypeMapping().getSerializer(javaType);
/* 1376 */       if (serF != null)
/* 1377 */         ser = (Serializer)serF.getSerializerAs("Axis SAX Mechanism");
/*      */     }
/*      */     catch (JAXRPCException e)
/*      */     {
/*      */     }
/* 1382 */     return ser;
/*      */   }
/*      */ 
/*      */   public Attributes setTypeAttribute(Attributes attributes, QName type)
/*      */   {
/* 1393 */     SchemaVersion schema = SchemaVersion.SCHEMA_2001;
/* 1394 */     if (this.msgContext != null) {
/* 1395 */       schema = this.msgContext.getSchemaVersion();
/*      */     }
/*      */ 
/* 1398 */     if ((type == null) || (type.getLocalPart().indexOf(">") >= 0) || ((attributes != null) && (attributes.getIndex(schema.getXsiURI(), "type") != -1)))
/*      */     {
/* 1403 */       return attributes;
/*      */     }
/* 1405 */     AttributesImpl attrs = new AttributesImpl();
/* 1406 */     if ((attributes != null) && (0 < attributes.getLength())) {
/* 1407 */       attrs.setAttributes(attributes);
/*      */     }
/* 1409 */     String prefix = getPrefixForURI(schema.getXsiURI(), "xsi");
/*      */ 
/* 1412 */     attrs.addAttribute(schema.getXsiURI(), "type", prefix + ":type", "CDATA", attributeQName2String(type));
/*      */ 
/* 1416 */     return attrs;
/*      */   }
/*      */ 
/*      */   private void serializeActual(QName elemQName, Attributes attributes, Object value, QName xmlType, Class javaClass, Boolean sendType)
/*      */     throws IOException
/*      */   {
/* 1436 */     boolean shouldSendType = sendType == null ? shouldSendXSIType() : sendType.booleanValue();
/*      */ 
/* 1439 */     if (value != null) {
/* 1440 */       TypeMapping tm = getTypeMapping();
/*      */ 
/* 1442 */       if (tm == null) {
/* 1443 */         throw new IOException(Messages.getMessage("noSerializer00", value.getClass().getName(), "" + this));
/*      */       }
/*      */ 
/* 1452 */       this.currentXMLType = xmlType;
/*      */ 
/* 1455 */       if (Constants.equals(Constants.XSD_ANYTYPE, xmlType)) {
/* 1456 */         xmlType = null;
/* 1457 */         shouldSendType = true;
/*      */       }
/*      */ 
/* 1461 */       QNameHolder actualXMLType = new QNameHolder();
/*      */ 
/* 1463 */       Class javaType = getActualJavaClass(xmlType, javaClass, value);
/*      */ 
/* 1465 */       Serializer ser = getSerializer(javaType, xmlType, actualXMLType);
/*      */ 
/* 1468 */       if (ser != null)
/*      */       {
/* 1472 */         if ((shouldSendType) || ((xmlType != null) && (!xmlType.equals(actualXMLType.value))))
/*      */         {
/* 1476 */           if (!isEncoded()) {
/* 1477 */             if (!Constants.isSOAP_ENC(actualXMLType.value.getNamespaceURI()))
/*      */             {
/* 1479 */               if ((!javaType.isPrimitive()) || (javaClass == null) || (JavaUtils.getWrapperClass(javaType) != javaClass))
/*      */               {
/* 1483 */                 if ((!javaType.isArray()) || (xmlType == null) || (!Constants.isSchemaXSD(xmlType.getNamespaceURI())))
/* 1484 */                   this.writeXMLType = actualXMLType.value;
/*      */               }
/*      */             }
/*      */           }
/* 1488 */           else this.writeXMLType = actualXMLType.value;
/*      */ 
/*      */         }
/*      */ 
/* 1504 */         ser.serialize(elemQName, attributes, value, this);
/* 1505 */         return;
/*      */       }
/* 1507 */       throw new IOException(Messages.getMessage("noSerializer00", value.getClass().getName(), "" + tm));
/*      */     }
/*      */   }
/*      */ 
/*      */   private Class getActualJavaClass(QName xmlType, Class javaType, Object obj)
/*      */   {
/* 1527 */     Class cls = obj.getClass();
/*      */ 
/* 1529 */     if (((xmlType != null) && (Constants.isSchemaXSD(xmlType.getNamespaceURI())) && ("anyType".equals(xmlType.getLocalPart()))) || ((javaType != null) && ((javaType.isArray()) || (javaType == Object.class))))
/*      */     {
/* 1533 */       return cls;
/*      */     }
/*      */ 
/* 1536 */     if ((javaType != null) && (!javaType.isAssignableFrom(cls)) && (!cls.isArray())) {
/* 1537 */       return javaType;
/*      */     }
/*      */ 
/* 1540 */     return cls;
/*      */   }
/*      */ 
/*      */   private Serializer getSerializerFromClass(Class javaType, QName qname)
/*      */   {
/* 1545 */     Serializer serializer = null;
/*      */     try {
/* 1547 */       Method method = MethodCache.getInstance().getMethod(javaType, "getSerializer", SERIALIZER_CLASSES);
/*      */ 
/* 1551 */       if (method != null)
/* 1552 */         serializer = (Serializer)method.invoke(null, new Object[] { getEncodingStyle(), javaType, qname });
/*      */     } catch (NoSuchMethodException e) {
/*      */     }
/*      */     catch (IllegalAccessException e) {
/*      */     }
/*      */     catch (InvocationTargetException e) {
/*      */     }
/* 1559 */     return serializer;
/*      */   }
/*      */ 
/*      */   public QName getCurrentXMLType()
/*      */   {
/* 1567 */     return this.currentXMLType;
/*      */   }
/*      */ 
/*      */   private SerializerFactory getSerializerFactoryFromInterface(Class javaType, QName xmlType, TypeMapping tm)
/*      */   {
/* 1579 */     SerializerFactory serFactory = null;
/* 1580 */     Class[] interfaces = javaType.getInterfaces();
/* 1581 */     if (interfaces != null) {
/* 1582 */       for (int i = 0; i < interfaces.length; i++) {
/* 1583 */         Class iface = interfaces[i];
/* 1584 */         serFactory = (SerializerFactory)tm.getSerializer(iface, xmlType);
/*      */ 
/* 1586 */         if (serFactory == null)
/* 1587 */           serFactory = getSerializerFactoryFromInterface(iface, xmlType, tm);
/* 1588 */         if (serFactory != null) {
/*      */           break;
/*      */         }
/*      */       }
/*      */     }
/* 1593 */     return serFactory;
/*      */   }
/*      */ 
/*      */   private Serializer getSerializer(Class javaType, QName xmlType, QNameHolder actualXMLType)
/*      */   {
/* 1607 */     SerializerFactory serFactory = null;
/* 1608 */     TypeMapping tm = getTypeMapping();
/* 1609 */     if (actualXMLType != null) {
/* 1610 */       actualXMLType.value = null;
/*      */     }
/*      */ 
/* 1613 */     while (javaType != null)
/*      */     {
/* 1615 */       serFactory = (SerializerFactory)tm.getSerializer(javaType, xmlType);
/* 1616 */       if (serFactory != null)
/*      */       {
/*      */         break;
/*      */       }
/*      */ 
/* 1621 */       Serializer serializer = getSerializerFromClass(javaType, xmlType);
/* 1622 */       if (serializer != null) {
/* 1623 */         if (actualXMLType != null) {
/* 1624 */           TypeDesc typedesc = TypeDesc.getTypeDescForClass(javaType);
/* 1625 */           if (typedesc != null) {
/* 1626 */             actualXMLType.value = typedesc.getXmlType();
/*      */           }
/*      */         }
/* 1629 */         return serializer;
/*      */       }
/*      */ 
/* 1633 */       serFactory = getSerializerFactoryFromInterface(javaType, xmlType, tm);
/* 1634 */       if (serFactory != null)
/*      */       {
/*      */         break;
/*      */       }
/*      */ 
/* 1639 */       javaType = javaType.getSuperclass();
/*      */     }
/*      */ 
/* 1643 */     Serializer ser = null;
/* 1644 */     if (serFactory != null) {
/* 1645 */       ser = (Serializer)serFactory.getSerializerAs("Axis SAX Mechanism");
/*      */ 
/* 1647 */       if (actualXMLType != null)
/*      */       {
/* 1651 */         if ((serFactory instanceof BaseSerializerFactory)) {
/* 1652 */           actualXMLType.value = ((BaseSerializerFactory)serFactory).getXMLType();
/*      */         }
/*      */ 
/* 1655 */         boolean encoded = isEncoded();
/* 1656 */         if ((actualXMLType.value == null) || ((!encoded) && ((actualXMLType.value.equals(Constants.SOAP_ARRAY)) || (actualXMLType.value.equals(Constants.SOAP_ARRAY12)))))
/*      */         {
/* 1660 */           actualXMLType.value = tm.getXMLType(javaType, xmlType, encoded);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1667 */     return ser;
/*      */   }
/*      */ 
/*      */   public String getValueAsString(Object value, QName xmlType, Class javaClass) throws IOException {
/* 1671 */     Class cls = value.getClass();
/* 1672 */     cls = getActualJavaClass(xmlType, javaClass, value);
/*      */ 
/* 1674 */     Serializer ser = getSerializer(cls, xmlType, null);
/*      */ 
/* 1677 */     if ((ser instanceof ArraySerializer))
/*      */     {
/* 1679 */       SimpleListSerializerFactory factory = new SimpleListSerializerFactory(cls, xmlType);
/*      */ 
/* 1681 */       ser = (Serializer)factory.getSerializerAs(getEncodingStyle());
/*      */     }
/*      */ 
/* 1685 */     if (!(ser instanceof SimpleValueSerializer)) {
/* 1686 */       throw new IOException(Messages.getMessage("needSimpleValueSer", ser.getClass().getName()));
/*      */     }
/*      */ 
/* 1690 */     SimpleValueSerializer simpleSer = (SimpleValueSerializer)ser;
/* 1691 */     return simpleSer.getValueAsString(value, this);
/*      */   }
/*      */ 
/*      */   public void setWriteXMLType(QName type) {
/* 1695 */     this.writeXMLType = type;
/*      */   }
/*      */ 
/*      */   public XMLEncoder getEncoder() {
/* 1699 */     if (this.encoder == null) {
/* 1700 */       this.encoder = XMLUtils.getXMLEncoder(this.encoding);
/*      */     }
/* 1702 */     return this.encoder;
/*      */   }
/*      */ 
/*      */   public String getEncoding()
/*      */   {
/* 1710 */     return this.encoding;
/*      */   }
/*      */ 
/*      */   public void setEncoding(String encoding)
/*      */   {
/* 1717 */     this.encoding = encoding;
/*      */   }
/*      */ 
/*      */   public QName getItemQName() {
/* 1721 */     return this.itemQName;
/*      */   }
/*      */ 
/*      */   public void setItemQName(QName itemQName) {
/* 1725 */     this.itemQName = itemQName;
/*      */   }
/*      */ 
/*      */   public QName getItemType() {
/* 1729 */     return this.itemType;
/*      */   }
/*      */ 
/*      */   public void setItemType(QName itemType) {
/* 1733 */     this.itemType = itemType;
/*      */   }
/*      */ 
/*      */   class MultiRefItem
/*      */   {
/*      */     String id;
/*      */     QName xmlType;
/*      */     Boolean sendType;
/*      */     Object value;
/*      */ 
/*      */     MultiRefItem(String id, QName xmlType, Boolean sendType, Object value)
/*      */     {
/*  182 */       this.id = id;
/*  183 */       this.xmlType = xmlType;
/*  184 */       this.sendType = sendType;
/*  185 */       this.value = value;
/*      */     }
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.SerializationContext
 * JD-Core Version:    0.6.0
 */