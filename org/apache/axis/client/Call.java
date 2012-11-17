/*      */ package org.apache.axis.client;
/*      */ 
/*      */ import java.io.StringWriter;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URL;
/*      */ import java.rmi.RemoteException;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.Vector;
/*      */ import javax.wsdl.Binding;
/*      */ import javax.wsdl.BindingInput;
/*      */ import javax.wsdl.BindingOperation;
/*      */ import javax.wsdl.Input;
/*      */ import javax.wsdl.Operation;
/*      */ import javax.wsdl.Part;
/*      */ import javax.wsdl.Port;
/*      */ import javax.wsdl.PortType;
/*      */ import javax.wsdl.extensions.mime.MIMEMultipartRelated;
/*      */ import javax.wsdl.extensions.mime.MIMEPart;
/*      */ import javax.wsdl.extensions.soap.SOAPAddress;
/*      */ import javax.wsdl.extensions.soap.SOAPBody;
/*      */ import javax.wsdl.extensions.soap.SOAPOperation;
/*      */ import javax.xml.namespace.QName;
/*      */ import javax.xml.rpc.JAXRPCException;
/*      */ import javax.xml.rpc.ParameterMode;
/*      */ import javax.xml.soap.SOAPException;
/*      */ import javax.xml.soap.SOAPMessage;
/*      */ import org.apache.axis.AxisEngine;
/*      */ import org.apache.axis.AxisFault;
/*      */ import org.apache.axis.AxisProperties;
/*      */ import org.apache.axis.Handler;
/*      */ import org.apache.axis.InternalException;
/*      */ import org.apache.axis.MessageContext;
/*      */ import org.apache.axis.SOAPPart;
/*      */ import org.apache.axis.attachments.Attachments;
/*      */ import org.apache.axis.components.logger.LogFactory;
/*      */ import org.apache.axis.constants.Style;
/*      */ import org.apache.axis.constants.Use;
/*      */ import org.apache.axis.description.FaultDesc;
/*      */ import org.apache.axis.description.OperationDesc;
/*      */ import org.apache.axis.description.ParameterDesc;
/*      */ import org.apache.axis.encoding.DeserializerFactory;
/*      */ import org.apache.axis.encoding.SerializationContext;
/*      */ import org.apache.axis.encoding.SerializerFactory;
/*      */ import org.apache.axis.encoding.TypeMapping;
/*      */ import org.apache.axis.encoding.TypeMappingRegistry;
/*      */ import org.apache.axis.encoding.XMLType;
/*      */ import org.apache.axis.encoding.ser.BaseDeserializerFactory;
/*      */ import org.apache.axis.encoding.ser.BaseSerializerFactory;
/*      */ import org.apache.axis.handlers.soap.SOAPService;
/*      */ import org.apache.axis.message.RPCElement;
/*      */ import org.apache.axis.message.RPCHeaderParam;
/*      */ import org.apache.axis.message.RPCParam;
/*      */ import org.apache.axis.message.SOAPBodyElement;
/*      */ import org.apache.axis.message.SOAPEnvelope;
/*      */ import org.apache.axis.message.SOAPFault;
/*      */ import org.apache.axis.message.SOAPHeaderElement;
/*      */ import org.apache.axis.soap.SOAPConstants;
/*      */ import org.apache.axis.transport.http.HTTPTransport;
/*      */ import org.apache.axis.transport.java.JavaTransport;
/*      */ import org.apache.axis.transport.local.LocalTransport;
/*      */ import org.apache.axis.utils.ClassUtils;
/*      */ import org.apache.axis.utils.JavaUtils;
/*      */ import org.apache.axis.utils.LockableHashtable;
/*      */ import org.apache.axis.utils.Messages;
/*      */ import org.apache.axis.wsdl.gen.Parser;
/*      */ import org.apache.axis.wsdl.symbolTable.BindingEntry;
/*      */ import org.apache.axis.wsdl.symbolTable.FaultInfo;
/*      */ import org.apache.axis.wsdl.symbolTable.Parameter;
/*      */ import org.apache.axis.wsdl.symbolTable.Parameters;
/*      */ import org.apache.axis.wsdl.symbolTable.SymbolTable;
/*      */ import org.apache.axis.wsdl.symbolTable.TypeEntry;
/*      */ import org.apache.axis.wsdl.toJava.Utils;
/*      */ import org.apache.commons.logging.Log;
/*      */ 
/*      */ public class Call
/*      */   implements javax.xml.rpc.Call
/*      */ {
/*  124 */   protected static Log log = LogFactory.getLog(Call.class.getName());
/*      */ 
/*  126 */   private static Log tlog = LogFactory.getLog("org.apache.axis.TIME");
/*      */ 
/*  132 */   protected static Log entLog = LogFactory.getLog("org.apache.axis.enterprise");
/*      */ 
/*  135 */   private boolean parmAndRetReq = true;
/*  136 */   private Service service = null;
/*  137 */   private QName portName = null;
/*  138 */   private QName portTypeName = null;
/*  139 */   private QName operationName = null;
/*      */ 
/*  141 */   private MessageContext msgContext = null;
/*      */ 
/*  146 */   private LockableHashtable myProperties = new LockableHashtable();
/*  147 */   private String username = null;
/*  148 */   private String password = null;
/*  149 */   private boolean maintainSession = false;
/*  150 */   private boolean useSOAPAction = false;
/*  151 */   private String SOAPActionURI = null;
/*  152 */   private Integer timeout = null;
/*  153 */   private boolean useStreaming = false;
/*      */ 
/*  156 */   private OperationDesc operation = null;
/*      */ 
/*  158 */   private boolean operationSetManually = false;
/*      */ 
/*  161 */   private boolean invokeOneWay = false;
/*  162 */   private boolean isMsg = false;
/*      */ 
/*  165 */   private Transport transport = null;
/*  166 */   private String transportName = null;
/*      */ 
/*  170 */   private HashMap outParams = null;
/*      */ 
/*  172 */   private ArrayList outParamsList = null;
/*      */ 
/*  175 */   private Vector myHeaders = null;
/*      */   public static final String SEND_TYPE_ATTR = "sendXsiTypes";
/*      */   public static final String TRANSPORT_NAME = "transport_name";
/*      */   public static final String CHARACTER_SET_ENCODING = "javax.xml.soap.character-set-encoding";
/*      */   public static final String TRANSPORT_PROPERTY = "java.protocol.handler.pkgs";
/*      */   public static final String WSDL_SERVICE = "wsdl.service";
/*      */   public static final String WSDL_PORT_NAME = "wsdl.portName";
/*      */ 
/*      */   /** @deprecated */
/*      */   public static final String JAXRPC_SERVICE = "wsdl.service";
/*      */ 
/*      */   /** @deprecated */
/*      */   public static final String JAXRPC_PORTTYPE_NAME = "wsdl.portName";
/*      */   public static final String FAULT_ON_NO_RESPONSE = "call.FaultOnNoResponse";
/*      */   public static final String CHECK_MUST_UNDERSTAND = "call.CheckMustUnderstand";
/*      */   public static final String ATTACHMENT_ENCAPSULATION_FORMAT = "attachment_encapsulation_format";
/*      */   public static final String ATTACHMENT_ENCAPSULATION_FORMAT_MIME = "axis.attachment.style.mime";
/*      */   public static final String ATTACHMENT_ENCAPSULATION_FORMAT_DIME = "axis.attachment.style.dime";
/*      */   public static final String ATTACHMENT_ENCAPSULATION_FORMAT_MTOM = "axis.attachment.style.mtom";
/*      */   public static final String CONNECTION_TIMEOUT_PROPERTY = "axis.connection.timeout";
/*      */   public static final String STREAMING_PROPERTY = "axis.streaming";
/*      */   protected static final String ONE_WAY = "axis.one.way";
/*  285 */   private static Hashtable transports = new Hashtable();
/*      */ 
/*  287 */   static ParameterMode[] modes = { null, ParameterMode.IN, ParameterMode.OUT, ParameterMode.INOUT };
/*      */ 
/*  293 */   private boolean encodingStyleExplicitlySet = false;
/*      */ 
/*  295 */   private boolean useExplicitlySet = false;
/*      */ 
/*  300 */   private SOAPService myService = null;
/*      */ 
/*  305 */   protected Vector attachmentParts = new Vector();
/*      */ 
/*  308 */   private boolean isNeverInvoked = true;
/*      */   private static ArrayList propertyNames;
/*      */   private static ArrayList transportPackages;
/*      */ 
/*      */   public Call(Service service)
/*      */   {
/*  325 */     this.service = service;
/*  326 */     AxisEngine engine = service.getEngine();
/*  327 */     this.msgContext = new MessageContext(engine);
/*  328 */     this.myProperties.setParent(engine.getOptions());
/*  329 */     this.maintainSession = service.getMaintainSession();
/*      */   }
/*      */ 
/*      */   public Call(String url)
/*      */     throws MalformedURLException
/*      */   {
/*  344 */     this(new Service());
/*  345 */     setTargetEndpointAddress(new URL(url));
/*      */   }
/*      */ 
/*      */   public Call(URL url)
/*      */   {
/*  354 */     this(new Service());
/*  355 */     setTargetEndpointAddress(url);
/*      */   }
/*      */ 
/*      */   public void setProperty(String name, Object value)
/*      */   {
/*  397 */     if ((name == null) || (value == null)) {
/*  398 */       throw new JAXRPCException(Messages.getMessage(name == null ? "badProp03" : "badProp04"));
/*      */     }
/*      */ 
/*  402 */     if (name.equals("javax.xml.rpc.security.auth.username")) {
/*  403 */       verifyStringProperty(name, value);
/*  404 */       setUsername((String)value);
/*      */     }
/*  406 */     else if (name.equals("javax.xml.rpc.security.auth.password")) {
/*  407 */       verifyStringProperty(name, value);
/*  408 */       setPassword((String)value);
/*      */     }
/*  410 */     else if (name.equals("javax.xml.rpc.session.maintain")) {
/*  411 */       verifyBooleanProperty(name, value);
/*  412 */       setMaintainSession(((Boolean)value).booleanValue());
/*      */     }
/*  414 */     else if (name.equals("javax.xml.rpc.soap.operation.style")) {
/*  415 */       verifyStringProperty(name, value);
/*  416 */       setOperationStyle((String)value);
/*  417 */       if ((getOperationStyle() == Style.DOCUMENT) || (getOperationStyle() == Style.WRAPPED))
/*      */       {
/*  419 */         setOperationUse("literal");
/*  420 */       } else if (getOperationStyle() == Style.RPC) {
/*  421 */         setOperationUse("encoded");
/*      */       }
/*      */     }
/*  424 */     else if (name.equals("javax.xml.rpc.soap.http.soapaction.use")) {
/*  425 */       verifyBooleanProperty(name, value);
/*  426 */       setUseSOAPAction(((Boolean)value).booleanValue());
/*      */     }
/*  428 */     else if (name.equals("javax.xml.rpc.soap.http.soapaction.uri")) {
/*  429 */       verifyStringProperty(name, value);
/*  430 */       setSOAPActionURI((String)value);
/*      */     }
/*  432 */     else if (name.equals("javax.xml.rpc.encodingstyle.namespace.uri")) {
/*  433 */       verifyStringProperty(name, value);
/*  434 */       setEncodingStyle((String)value);
/*      */     }
/*  436 */     else if (name.equals("javax.xml.rpc.service.endpoint.address")) {
/*  437 */       verifyStringProperty(name, value);
/*  438 */       setTargetEndpointAddress((String)value);
/*      */     }
/*  440 */     else if (name.equals("transport_name")) {
/*  441 */       verifyStringProperty(name, value);
/*  442 */       this.transportName = ((String)value);
/*  443 */       if (this.transport != null) {
/*  444 */         this.transport.setTransportName((String)value);
/*      */       }
/*      */     }
/*  447 */     else if (name.equals("attachment_encapsulation_format")) {
/*  448 */       verifyStringProperty(name, value);
/*  449 */       if ((!value.equals("axis.attachment.style.mime")) && (!value.equals("axis.attachment.style.mtom")) && (!value.equals("axis.attachment.style.dime")))
/*      */       {
/*  452 */         throw new JAXRPCException(Messages.getMessage("badattachmenttypeerr", new String[] { (String)value, "axis.attachment.style.mime axis.attachment.style.mtom axis.attachment.style.dime" }));
/*      */       }
/*      */ 
/*      */     }
/*  458 */     else if (name.equals("axis.connection.timeout")) {
/*  459 */       verifyIntegerProperty(name, value);
/*  460 */       setTimeout((Integer)value);
/*      */     }
/*  462 */     else if (name.equals("axis.streaming")) {
/*  463 */       verifyBooleanProperty(name, value);
/*  464 */       setStreaming(((Boolean)value).booleanValue());
/*      */     }
/*  466 */     else if (name.equals("javax.xml.soap.character-set-encoding")) {
/*  467 */       verifyStringProperty(name, value);
/*      */     }
/*  469 */     else if ((name.startsWith("java.")) || (name.startsWith("javax."))) {
/*  470 */       throw new JAXRPCException(Messages.getMessage("badProp05", name));
/*      */     }
/*      */ 
/*  473 */     this.myProperties.put(name, value);
/*      */   }
/*      */ 
/*      */   private void verifyStringProperty(String name, Object value)
/*      */   {
/*  484 */     if (!(value instanceof String))
/*  485 */       throw new JAXRPCException(Messages.getMessage("badProp00", new String[] { name, "java.lang.String", value.getClass().getName() }));
/*      */   }
/*      */ 
/*      */   private void verifyBooleanProperty(String name, Object value)
/*      */   {
/*  501 */     if (!(value instanceof Boolean))
/*  502 */       throw new JAXRPCException(Messages.getMessage("badProp00", new String[] { name, "java.lang.Boolean", value.getClass().getName() }));
/*      */   }
/*      */ 
/*      */   private void verifyIntegerProperty(String name, Object value)
/*      */   {
/*  518 */     if (!(value instanceof Integer))
/*  519 */       throw new JAXRPCException(Messages.getMessage("badProp00", new String[] { name, "java.lang.Integer", value.getClass().getName() }));
/*      */   }
/*      */ 
/*      */   public Object getProperty(String name)
/*      */   {
/*  535 */     if ((name == null) || (!isPropertySupported(name))) {
/*  536 */       throw new JAXRPCException(name == null ? Messages.getMessage("badProp03") : Messages.getMessage("badProp05", name));
/*      */     }
/*      */ 
/*  540 */     return this.myProperties.get(name);
/*      */   }
/*      */ 
/*      */   public void removeProperty(String name)
/*      */   {
/*  549 */     if ((name == null) || (!isPropertySupported(name))) {
/*  550 */       throw new JAXRPCException(name == null ? Messages.getMessage("badProp03") : Messages.getMessage("badProp05", name));
/*      */     }
/*      */ 
/*  554 */     this.myProperties.remove(name);
/*      */   }
/*      */ 
/*      */   public Iterator getPropertyNames()
/*      */   {
/*  577 */     return propertyNames.iterator();
/*      */   }
/*      */ 
/*      */   public boolean isPropertySupported(String name) {
/*  581 */     return (propertyNames.contains(name)) || ((!name.startsWith("java.")) && (!name.startsWith("javax.")));
/*      */   }
/*      */ 
/*      */   public void setUsername(String username)
/*      */   {
/*  591 */     this.username = username;
/*      */   }
/*      */ 
/*      */   public String getUsername()
/*      */   {
/*  600 */     return this.username;
/*      */   }
/*      */ 
/*      */   public void setPassword(String password)
/*      */   {
/*  609 */     this.password = password;
/*      */   }
/*      */ 
/*      */   public String getPassword()
/*      */   {
/*  618 */     return this.password;
/*      */   }
/*      */ 
/*      */   public void setMaintainSession(boolean yesno)
/*      */   {
/*  630 */     this.maintainSession = yesno;
/*      */   }
/*      */ 
/*      */   public boolean getMaintainSession()
/*      */   {
/*  639 */     return this.maintainSession;
/*      */   }
/*      */ 
/*      */   public void setOperationStyle(String operationStyle)
/*      */   {
/*  647 */     Style style = Style.getStyle(operationStyle, Style.DEFAULT);
/*  648 */     setOperationStyle(style);
/*      */   }
/*      */ 
/*      */   public void setOperationStyle(Style operationStyle)
/*      */   {
/*  657 */     if (this.operation == null) {
/*  658 */       this.operation = new OperationDesc();
/*      */     }
/*      */ 
/*  661 */     this.operation.setStyle(operationStyle);
/*      */ 
/*  665 */     if ((!this.useExplicitlySet) && 
/*  666 */       (operationStyle != Style.RPC)) {
/*  667 */       this.operation.setUse(Use.LITERAL);
/*      */     }
/*      */ 
/*  674 */     if (!this.encodingStyleExplicitlySet) {
/*  675 */       String encStyle = "";
/*  676 */       if (operationStyle == Style.RPC)
/*      */       {
/*  678 */         encStyle = this.msgContext.getSOAPConstants().getEncodingURI();
/*      */       }
/*  680 */       this.msgContext.setEncodingStyle(encStyle);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Style getOperationStyle()
/*      */   {
/*  690 */     if (this.operation != null) {
/*  691 */       return this.operation.getStyle();
/*      */     }
/*  693 */     return Style.DEFAULT;
/*      */   }
/*      */ 
/*      */   public void setOperationUse(String operationUse)
/*      */   {
/*  701 */     Use use = Use.getUse(operationUse, Use.DEFAULT);
/*  702 */     setOperationUse(use);
/*      */   }
/*      */ 
/*      */   public void setOperationUse(Use operationUse)
/*      */   {
/*  710 */     this.useExplicitlySet = true;
/*      */ 
/*  712 */     if (this.operation == null) {
/*  713 */       this.operation = new OperationDesc();
/*      */     }
/*      */ 
/*  716 */     this.operation.setUse(operationUse);
/*  717 */     if (!this.encodingStyleExplicitlySet) {
/*  718 */       String encStyle = "";
/*  719 */       if (operationUse == Use.ENCODED)
/*      */       {
/*  721 */         encStyle = this.msgContext.getSOAPConstants().getEncodingURI();
/*      */       }
/*  723 */       this.msgContext.setEncodingStyle(encStyle);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Use getOperationUse()
/*      */   {
/*  733 */     if (this.operation != null) {
/*  734 */       return this.operation.getUse();
/*      */     }
/*  736 */     return Use.DEFAULT;
/*      */   }
/*      */ 
/*      */   public void setUseSOAPAction(boolean useSOAPAction)
/*      */   {
/*  746 */     this.useSOAPAction = useSOAPAction;
/*      */   }
/*      */ 
/*      */   public boolean useSOAPAction()
/*      */   {
/*  755 */     return this.useSOAPAction;
/*      */   }
/*      */ 
/*      */   public void setSOAPActionURI(String SOAPActionURI)
/*      */   {
/*  764 */     this.useSOAPAction = true;
/*  765 */     this.SOAPActionURI = SOAPActionURI;
/*      */   }
/*      */ 
/*      */   public String getSOAPActionURI()
/*      */   {
/*  774 */     return this.SOAPActionURI;
/*      */   }
/*      */ 
/*      */   public void setEncodingStyle(String namespaceURI)
/*      */   {
/*  783 */     this.encodingStyleExplicitlySet = true;
/*  784 */     this.msgContext.setEncodingStyle(namespaceURI);
/*      */   }
/*      */ 
/*      */   public String getEncodingStyle()
/*      */   {
/*  794 */     return this.msgContext.getEncodingStyle();
/*      */   }
/*      */ 
/*      */   public void setTargetEndpointAddress(String address)
/*      */   {
/*      */     try
/*      */     {
/*  808 */       urlAddress = new URL(address);
/*      */     }
/*      */     catch (MalformedURLException mue)
/*      */     {
/*      */       URL urlAddress;
/*  811 */       throw new JAXRPCException(mue);
/*      */     }
/*      */     URL urlAddress;
/*  813 */     setTargetEndpointAddress(urlAddress);
/*      */   }
/*      */ 
/*      */   public void setTargetEndpointAddress(URL address)
/*      */   {
/*      */     try
/*      */     {
/*  825 */       if (address == null) {
/*  826 */         setTransport(null);
/*  827 */         return;
/*      */       }
/*      */ 
/*  830 */       String protocol = address.getProtocol();
/*      */ 
/*  840 */       if (this.transport != null) {
/*  841 */         String oldAddr = this.transport.getUrl();
/*  842 */         if ((oldAddr != null) && (!oldAddr.equals(""))) {
/*  843 */           URL tmpURL = new URL(oldAddr);
/*  844 */           String oldProto = tmpURL.getProtocol();
/*  845 */           if (protocol.equals(oldProto)) {
/*  846 */             this.transport.setUrl(address.toString());
/*  847 */             return;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  853 */       Transport transport = this.service.getTransportForURL(address);
/*  854 */       if (transport != null) {
/*  855 */         setTransport(transport);
/*      */       }
/*      */       else
/*      */       {
/*  859 */         transport = getTransportForProtocol(protocol);
/*  860 */         if (transport == null) {
/*  861 */           throw new AxisFault("Call.setTargetEndpointAddress", Messages.getMessage("noTransport01", protocol), null, null);
/*      */         }
/*      */ 
/*  864 */         transport.setUrl(address.toString());
/*  865 */         setTransport(transport);
/*  866 */         this.service.registerTransportForURL(address, transport);
/*      */       }
/*      */     }
/*      */     catch (Exception exp) {
/*  870 */       log.error(Messages.getMessage("exception00"), exp);
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getTargetEndpointAddress()
/*      */   {
/*      */     try
/*      */     {
/*  884 */       if (this.transport == null) return null;
/*  885 */       return this.transport.getUrl();
/*      */     } catch (Exception exp) {
/*      */     }
/*  888 */     return null;
/*      */   }
/*      */ 
/*      */   public Integer getTimeout()
/*      */   {
/*  893 */     return this.timeout;
/*      */   }
/*      */ 
/*      */   public void setTimeout(Integer timeout) {
/*  897 */     this.timeout = timeout;
/*      */   }
/*      */ 
/*      */   public boolean getStreaming() {
/*  901 */     return this.useStreaming;
/*      */   }
/*      */ 
/*      */   public void setStreaming(boolean useStreaming) {
/*  905 */     this.useStreaming = useStreaming;
/*      */   }
/*      */ 
/*      */   public boolean isParameterAndReturnSpecRequired(QName operationName)
/*      */   {
/*  924 */     return this.parmAndRetReq;
/*      */   }
/*      */ 
/*      */   public void addParameter(QName paramName, QName xmlType, ParameterMode parameterMode)
/*      */   {
/*  939 */     Class javaType = null;
/*  940 */     TypeMapping tm = getTypeMapping();
/*  941 */     if (tm != null) {
/*  942 */       javaType = tm.getClassForQName(xmlType);
/*      */     }
/*  944 */     addParameter(paramName, xmlType, javaType, parameterMode);
/*      */   }
/*      */ 
/*      */   public void addParameter(QName paramName, QName xmlType, Class javaType, ParameterMode parameterMode)
/*      */   {
/*  962 */     if (this.operationSetManually) {
/*  963 */       throw new RuntimeException(Messages.getMessage("operationAlreadySet"));
/*      */     }
/*      */ 
/*  967 */     if (this.operation == null) {
/*  968 */       this.operation = new OperationDesc();
/*      */     }
/*  970 */     ParameterDesc param = new ParameterDesc();
/*  971 */     byte mode = 1;
/*  972 */     if (parameterMode == ParameterMode.INOUT) {
/*  973 */       mode = 3;
/*  974 */       param.setIsReturn(true);
/*  975 */     } else if (parameterMode == ParameterMode.OUT) {
/*  976 */       mode = 2;
/*  977 */       param.setIsReturn(true);
/*      */     }
/*  979 */     param.setMode(mode);
/*  980 */     param.setQName(new QName(paramName.getNamespaceURI(), Utils.getLastLocalPart(paramName.getLocalPart())));
/*  981 */     param.setTypeQName(xmlType);
/*  982 */     param.setJavaType(javaType);
/*      */ 
/*  984 */     this.operation.addParameter(param);
/*  985 */     this.parmAndRetReq = true;
/*      */   }
/*      */ 
/*      */   public void addParameter(String paramName, QName xmlType, ParameterMode parameterMode)
/*      */   {
/*  998 */     Class javaType = null;
/*  999 */     TypeMapping tm = getTypeMapping();
/* 1000 */     if (tm != null) {
/* 1001 */       javaType = tm.getClassForQName(xmlType);
/*      */     }
/* 1003 */     addParameter(new QName("", paramName), xmlType, javaType, parameterMode);
/*      */   }
/*      */ 
/*      */   public void addParameter(String paramName, QName xmlType, Class javaType, ParameterMode parameterMode)
/*      */   {
/* 1025 */     addParameter(new QName("", paramName), xmlType, javaType, parameterMode);
/*      */   }
/*      */ 
/*      */   public void addParameterAsHeader(QName paramName, QName xmlType, ParameterMode parameterMode, ParameterMode headerMode)
/*      */   {
/* 1046 */     Class javaType = null;
/* 1047 */     TypeMapping tm = getTypeMapping();
/* 1048 */     if (tm != null) {
/* 1049 */       javaType = tm.getClassForQName(xmlType);
/*      */     }
/* 1051 */     addParameterAsHeader(paramName, xmlType, javaType, parameterMode, headerMode);
/*      */   }
/*      */ 
/*      */   public void addParameterAsHeader(QName paramName, QName xmlType, Class javaType, ParameterMode parameterMode, ParameterMode headerMode)
/*      */   {
/* 1073 */     if (this.operationSetManually) {
/* 1074 */       throw new RuntimeException(Messages.getMessage("operationAlreadySet"));
/*      */     }
/*      */ 
/* 1078 */     if (this.operation == null) {
/* 1079 */       this.operation = new OperationDesc();
/*      */     }
/* 1081 */     ParameterDesc param = new ParameterDesc();
/* 1082 */     param.setQName(new QName(paramName.getNamespaceURI(), Utils.getLastLocalPart(paramName.getLocalPart())));
/* 1083 */     param.setTypeQName(xmlType);
/* 1084 */     param.setJavaType(javaType);
/* 1085 */     if (parameterMode == ParameterMode.IN) {
/* 1086 */       param.setMode(1);
/*      */     }
/* 1088 */     else if (parameterMode == ParameterMode.INOUT) {
/* 1089 */       param.setMode(3);
/*      */     }
/* 1091 */     else if (parameterMode == ParameterMode.OUT) {
/* 1092 */       param.setMode(2);
/*      */     }
/* 1094 */     if (headerMode == ParameterMode.IN) {
/* 1095 */       param.setInHeader(true);
/*      */     }
/* 1097 */     else if (headerMode == ParameterMode.INOUT) {
/* 1098 */       param.setInHeader(true);
/* 1099 */       param.setOutHeader(true);
/*      */     }
/* 1101 */     else if (headerMode == ParameterMode.OUT) {
/* 1102 */       param.setOutHeader(true);
/*      */     }
/* 1104 */     this.operation.addParameter(param);
/* 1105 */     this.parmAndRetReq = true;
/*      */   }
/*      */ 
/*      */   public QName getParameterTypeByName(String paramName)
/*      */   {
/* 1115 */     QName paramQName = new QName("", paramName);
/*      */ 
/* 1117 */     return getParameterTypeByQName(paramQName);
/*      */   }
/*      */ 
/*      */   public QName getParameterTypeByQName(QName paramQName)
/*      */   {
/* 1129 */     ParameterDesc param = this.operation.getParamByQName(paramQName);
/* 1130 */     if (param != null) {
/* 1131 */       return param.getTypeQName();
/*      */     }
/* 1133 */     return null;
/*      */   }
/*      */ 
/*      */   public void setReturnType(QName type)
/*      */   {
/* 1142 */     if (this.operationSetManually) {
/* 1143 */       throw new RuntimeException(Messages.getMessage("operationAlreadySet"));
/*      */     }
/*      */ 
/* 1147 */     if (this.operation == null) {
/* 1148 */       this.operation = new OperationDesc();
/*      */     }
/*      */ 
/* 1156 */     this.operation.setReturnType(type);
/* 1157 */     TypeMapping tm = getTypeMapping();
/* 1158 */     this.operation.setReturnClass(tm.getClassForQName(type));
/* 1159 */     this.parmAndRetReq = true;
/*      */   }
/*      */ 
/*      */   public void setReturnType(QName xmlType, Class javaType)
/*      */   {
/* 1176 */     setReturnType(xmlType);
/*      */ 
/* 1178 */     this.operation.setReturnClass(javaType);
/*      */   }
/*      */ 
/*      */   public void setReturnTypeAsHeader(QName xmlType)
/*      */   {
/* 1185 */     setReturnType(xmlType);
/* 1186 */     this.operation.setReturnHeader(true);
/*      */   }
/*      */ 
/*      */   public void setReturnTypeAsHeader(QName xmlType, Class javaType)
/*      */   {
/* 1193 */     setReturnType(xmlType, javaType);
/* 1194 */     this.operation.setReturnHeader(true);
/*      */   }
/*      */ 
/*      */   public QName getReturnType()
/*      */   {
/* 1206 */     if (this.operation != null) {
/* 1207 */       return this.operation.getReturnType();
/*      */     }
/* 1209 */     return null;
/*      */   }
/*      */ 
/*      */   public void setReturnQName(QName qname)
/*      */   {
/* 1218 */     if (this.operationSetManually) {
/* 1219 */       throw new RuntimeException(Messages.getMessage("operationAlreadySet"));
/*      */     }
/*      */ 
/* 1223 */     if (this.operation == null) {
/* 1224 */       this.operation = new OperationDesc();
/*      */     }
/* 1226 */     this.operation.setReturnQName(qname);
/*      */   }
/*      */ 
/*      */   public void setReturnClass(Class cls)
/*      */   {
/* 1245 */     if (this.operationSetManually) {
/* 1246 */       throw new RuntimeException(Messages.getMessage("operationAlreadySet"));
/*      */     }
/*      */ 
/* 1250 */     if (this.operation == null) {
/* 1251 */       this.operation = new OperationDesc();
/*      */     }
/* 1253 */     this.operation.setReturnClass(cls);
/* 1254 */     TypeMapping tm = getTypeMapping();
/* 1255 */     this.operation.setReturnType(tm.getTypeQName(cls));
/* 1256 */     this.parmAndRetReq = true;
/*      */   }
/*      */ 
/*      */   public void removeAllParameters()
/*      */   {
/* 1267 */     this.operation = new OperationDesc();
/* 1268 */     this.operationSetManually = false;
/* 1269 */     this.parmAndRetReq = true;
/*      */   }
/*      */ 
/*      */   public QName getOperationName()
/*      */   {
/* 1282 */     return this.operationName;
/*      */   }
/*      */ 
/*      */   public void setOperationName(QName opName)
/*      */   {
/* 1293 */     this.operationName = opName;
/*      */   }
/*      */ 
/*      */   public void setOperationName(String opName)
/*      */   {
/* 1302 */     this.operationName = new QName(opName);
/*      */   }
/*      */ 
/*      */   public void setOperation(String opName)
/*      */   {
/* 1320 */     if (this.service == null) {
/* 1321 */       throw new JAXRPCException(Messages.getMessage("noService04"));
/*      */     }
/*      */ 
/* 1326 */     setOperationName(opName);
/* 1327 */     setEncodingStyle(null);
/* 1328 */     setReturnType(null);
/* 1329 */     removeAllParameters();
/*      */ 
/* 1331 */     javax.wsdl.Service wsdlService = this.service.getWSDLService();
/*      */ 
/* 1333 */     if (wsdlService == null) {
/* 1334 */       return;
/*      */     }
/*      */ 
/* 1337 */     Port port = wsdlService.getPort(this.portName.getLocalPart());
/* 1338 */     if (port == null) {
/* 1339 */       throw new JAXRPCException(Messages.getMessage("noPort00", "" + this.portName));
/*      */     }
/*      */ 
/* 1343 */     Binding binding = port.getBinding();
/* 1344 */     PortType portType = binding.getPortType();
/* 1345 */     if (portType == null) {
/* 1346 */       throw new JAXRPCException(Messages.getMessage("noPortType00", "" + this.portName));
/*      */     }
/*      */ 
/* 1349 */     setPortTypeName(portType.getQName());
/*      */ 
/* 1351 */     List operations = portType.getOperations();
/* 1352 */     if (operations == null) {
/* 1353 */       throw new JAXRPCException(Messages.getMessage("noOperation01", opName));
/*      */     }
/*      */ 
/* 1357 */     Operation op = null;
/* 1358 */     for (int i = 0; i < operations.size(); op = null) {
/* 1359 */       op = (Operation)operations.get(i);
/* 1360 */       if (opName.equals(op.getName()))
/*      */         break;
/* 1358 */       i++;
/*      */     }
/*      */ 
/* 1364 */     if (op == null) {
/* 1365 */       throw new JAXRPCException(Messages.getMessage("noOperation01", opName));
/*      */     }
/*      */ 
/* 1371 */     List list = port.getExtensibilityElements();
/* 1372 */     String opStyle = null;
/* 1373 */     BindingOperation bop = binding.getBindingOperation(opName, null, null);
/*      */ 
/* 1375 */     if (bop == null) {
/* 1376 */       throw new JAXRPCException(Messages.getMessage("noOperation02", opName));
/*      */     }
/*      */ 
/* 1379 */     list = bop.getExtensibilityElements();
/* 1380 */     for (int i = 0; (list != null) && (i < list.size()); i++) {
/* 1381 */       Object obj = list.get(i);
/* 1382 */       if ((obj instanceof SOAPOperation)) {
/* 1383 */         SOAPOperation sop = (SOAPOperation)obj;
/* 1384 */         opStyle = ((SOAPOperation)obj).getStyle();
/* 1385 */         String action = sop.getSoapActionURI();
/* 1386 */         if (action != null) {
/* 1387 */           setUseSOAPAction(true);
/* 1388 */           setSOAPActionURI(action); break;
/*      */         }
/*      */ 
/* 1391 */         setUseSOAPAction(false);
/* 1392 */         setSOAPActionURI(null);
/*      */ 
/* 1394 */         break;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1400 */     BindingInput bIn = bop.getBindingInput();
/* 1401 */     if (bIn != null) {
/* 1402 */       list = bIn.getExtensibilityElements();
/* 1403 */       for (int i = 0; (list != null) && (i < list.size()); i++) {
/* 1404 */         Object obj = list.get(i);
/* 1405 */         if ((obj instanceof MIMEMultipartRelated)) {
/* 1406 */           MIMEMultipartRelated mpr = (MIMEMultipartRelated)obj;
/* 1407 */           Object part = null;
/* 1408 */           List l = mpr.getMIMEParts();
/*      */           List ll;
/*      */           int k;
/* 1409 */           for (int j = 0; (l != null) && (j < l.size()) && (part == null); j++) {
/* 1410 */             MIMEPart mp = (MIMEPart)l.get(j);
/* 1411 */             ll = mp.getExtensibilityElements();
/* 1412 */             for (k = 0; (ll != null) && (k < ll.size()) && (part == null); )
/*      */             {
/* 1414 */               part = ll.get(k);
/* 1415 */               if (!(part instanceof SOAPBody))
/* 1416 */                 part = null;
/* 1413 */               k++;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 1420 */           if (null != part) {
/* 1421 */             obj = part;
/*      */           }
/*      */         }
/*      */ 
/* 1425 */         if ((obj instanceof SOAPBody)) {
/* 1426 */           SOAPBody sBody = (SOAPBody)obj;
/* 1427 */           list = sBody.getEncodingStyles();
/* 1428 */           if ((list != null) && (list.size() > 0)) {
/* 1429 */             setEncodingStyle((String)list.get(0));
/*      */           }
/* 1431 */           String ns = sBody.getNamespaceURI();
/* 1432 */           if ((ns == null) || (ns.equals(""))) break;
/* 1433 */           setOperationName(new QName(ns, opName)); break;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1440 */     Service service = getService();
/* 1441 */     SymbolTable symbolTable = service.getWSDLParser().getSymbolTable();
/* 1442 */     BindingEntry bEntry = symbolTable.getBindingEntry(binding.getQName());
/* 1443 */     Parameters parameters = bEntry.getParameters(bop.getOperation());
/*      */ 
/* 1446 */     for (int j = 0; j < parameters.list.size(); j++) {
/* 1447 */       Parameter p = (Parameter)parameters.list.get(j);
/*      */ 
/* 1449 */       QName paramType = Utils.getXSIType(p);
/*      */ 
/* 1454 */       ParameterMode mode = modes[p.getMode()];
/* 1455 */       if ((p.isInHeader()) || (p.isOutHeader())) {
/* 1456 */         addParameterAsHeader(p.getQName(), paramType, mode, mode);
/*      */       }
/*      */       else {
/* 1459 */         addParameter(p.getQName(), paramType, mode);
/*      */       }
/*      */     }
/*      */ 
/* 1463 */     Map faultMap = bEntry.getFaults();
/*      */ 
/* 1465 */     ArrayList faults = (ArrayList)faultMap.get(bop);
/*      */ 
/* 1468 */     if (faults == null) {
/* 1469 */       return;
/*      */     }
/*      */ 
/* 1472 */     for (Iterator faultIt = faults.iterator(); faultIt.hasNext(); ) {
/* 1473 */       FaultInfo info = (FaultInfo)faultIt.next();
/* 1474 */       QName qname = info.getQName();
/* 1475 */       info.getMessage();
/*      */ 
/* 1478 */       if (qname == null)
/*      */       {
/*      */         continue;
/*      */       }
/* 1482 */       QName xmlType = info.getXMLType();
/* 1483 */       Class clazz = getTypeMapping().getClassForQName(xmlType);
/* 1484 */       if (clazz != null) {
/* 1485 */         addFault(qname, clazz, xmlType, true);
/*      */       }
/*      */       else
/*      */       {
/* 1490 */         log.debug(Messages.getMessage("clientNoTypemapping", xmlType.toString()));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1495 */     if (parameters.returnParam != null)
/*      */     {
/* 1497 */       QName returnType = Utils.getXSIType(parameters.returnParam);
/* 1498 */       QName returnQName = parameters.returnParam.getQName();
/*      */ 
/* 1501 */       String javaType = null;
/* 1502 */       if (parameters.returnParam.getMIMEInfo() != null) {
/* 1503 */         javaType = "javax.activation.DataHandler";
/*      */       }
/*      */       else {
/* 1506 */         javaType = parameters.returnParam.getType().getName();
/*      */       }
/* 1508 */       if (javaType == null) {
/* 1509 */         javaType = "";
/*      */       }
/*      */       else {
/* 1512 */         javaType = javaType + ".class";
/*      */       }
/* 1514 */       setReturnType(returnType);
/*      */       try {
/* 1516 */         Class clazz = ClassUtils.forName(javaType);
/* 1517 */         setReturnClass(clazz);
/*      */       }
/*      */       catch (ClassNotFoundException swallowedException) {
/* 1520 */         log.debug(Messages.getMessage("clientNoReturnClass", javaType));
/*      */       }
/*      */ 
/* 1523 */       setReturnQName(returnQName);
/*      */     }
/*      */     else {
/* 1526 */       setReturnType(XMLType.AXIS_VOID);
/*      */     }
/*      */ 
/* 1529 */     boolean hasMIME = Utils.hasMIME(bEntry, bop);
/* 1530 */     Use use = bEntry.getInputBodyType(bop.getOperation());
/* 1531 */     setOperationUse(use);
/* 1532 */     if (use == Use.LITERAL)
/*      */     {
/* 1534 */       setEncodingStyle(null);
/*      */ 
/* 1536 */       setProperty("sendXsiTypes", Boolean.FALSE);
/*      */     }
/* 1538 */     if ((hasMIME) || (use == Use.LITERAL))
/*      */     {
/* 1544 */       setProperty("sendMultiRefs", Boolean.FALSE);
/*      */     }
/*      */ 
/* 1547 */     Style style = Style.getStyle(opStyle, bEntry.getBindingStyle());
/* 1548 */     if ((style == Style.DOCUMENT) && (symbolTable.isWrapped())) {
/* 1549 */       style = Style.WRAPPED;
/*      */     }
/* 1551 */     setOperationStyle(style);
/*      */ 
/* 1554 */     if (style == Style.WRAPPED)
/*      */     {
/* 1558 */       Map partsMap = bop.getOperation().getInput().getMessage().getParts();
/* 1559 */       Part p = (Part)partsMap.values().iterator().next();
/* 1560 */       QName q = p.getElementName();
/* 1561 */       setOperationName(q);
/*      */     } else {
/* 1563 */       QName elementQName = Utils.getOperationQName(bop, bEntry, symbolTable);
/*      */ 
/* 1565 */       if (elementQName != null) {
/* 1566 */         setOperationName(elementQName);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1572 */     this.parmAndRetReq = false;
/*      */   }
/*      */ 
/*      */   public void setOperation(QName portName, String opName)
/*      */   {
/* 1592 */     setOperation(portName, new QName(opName));
/*      */   }
/*      */ 
/*      */   public void setOperation(QName portName, QName opName)
/*      */   {
/* 1610 */     if (this.service == null) {
/* 1611 */       throw new JAXRPCException(Messages.getMessage("noService04"));
/*      */     }
/*      */ 
/* 1614 */     setPortName(portName);
/* 1615 */     setOperationName(opName);
/* 1616 */     setReturnType(null);
/* 1617 */     removeAllParameters();
/*      */ 
/* 1619 */     javax.wsdl.Service wsdlService = this.service.getWSDLService();
/*      */ 
/* 1621 */     if (wsdlService == null) {
/* 1622 */       return;
/*      */     }
/*      */ 
/* 1626 */     setTargetEndpointAddress((URL)null);
/*      */ 
/* 1628 */     Port port = wsdlService.getPort(portName.getLocalPart());
/* 1629 */     if (port == null) {
/* 1630 */       throw new JAXRPCException(Messages.getMessage("noPort00", "" + portName));
/*      */     }
/*      */ 
/* 1636 */     List list = port.getExtensibilityElements();
/* 1637 */     for (int i = 0; (list != null) && (i < list.size()); i++) {
/* 1638 */       Object obj = list.get(i);
/* 1639 */       if (!(obj instanceof SOAPAddress)) continue;
/*      */       try {
/* 1641 */         SOAPAddress addr = (SOAPAddress)obj;
/* 1642 */         URL url = new URL(addr.getLocationURI());
/* 1643 */         setTargetEndpointAddress(url);
/*      */       }
/*      */       catch (Exception exp) {
/* 1646 */         throw new JAXRPCException(Messages.getMessage("cantSetURI00", "" + exp));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1652 */     setOperation(opName.getLocalPart());
/*      */   }
/*      */ 
/*      */   public QName getPortName()
/*      */   {
/* 1662 */     return this.portName;
/*      */   }
/*      */ 
/*      */   public void setPortName(QName portName)
/*      */   {
/* 1673 */     this.portName = portName;
/*      */   }
/*      */ 
/*      */   public QName getPortTypeName()
/*      */   {
/* 1683 */     return this.portTypeName == null ? new QName("") : this.portTypeName;
/*      */   }
/*      */ 
/*      */   public void setPortTypeName(QName portType)
/*      */   {
/* 1694 */     this.portTypeName = portType;
/*      */   }
/*      */ 
/*      */   public void setSOAPVersion(SOAPConstants soapConstants)
/*      */   {
/* 1705 */     this.msgContext.setSOAPConstants(soapConstants);
/*      */   }
/*      */ 
/*      */   public Object invoke(QName operationName, Object[] params)
/*      */     throws RemoteException
/*      */   {
/* 1724 */     QName origOpName = this.operationName;
/* 1725 */     this.operationName = operationName;
/*      */     try {
/* 1727 */       return invoke(params);
/*      */     }
/*      */     catch (AxisFault af) {
/* 1730 */       this.operationName = origOpName;
/* 1731 */       if ((af.detail != null) && ((af.detail instanceof RemoteException))) {
/* 1732 */         throw ((RemoteException)af.detail);
/*      */       }
/* 1734 */       throw af;
/*      */     }
/*      */     catch (RemoteException re) {
/* 1737 */       this.operationName = origOpName;
/* 1738 */       throw re;
/*      */     }
/*      */     catch (RuntimeException re) {
/* 1741 */       this.operationName = origOpName;
/* 1742 */       throw re;
/*      */     }
/*      */     catch (Error e) {
/* 1745 */       this.operationName = origOpName;
/* 1746 */     }throw e;
/*      */   }
/*      */ 
/*      */   public Object invoke(Object[] params)
/*      */     throws RemoteException
/*      */   {
/* 1764 */     long t0 = 0L; long t1 = 0L;
/* 1765 */     if (tlog.isDebugEnabled()) {
/* 1766 */       t0 = System.currentTimeMillis();
/*      */     }
/*      */ 
/* 1772 */     SOAPEnvelope env = null;
/*      */ 
/* 1775 */     for (int i = 0; (params != null) && (i < params.length) && 
/* 1776 */       ((params[i] instanceof SOAPBodyElement)); i++);
/* 1778 */     if ((params != null) && (params.length > 0) && (i == params.length))
/*      */     {
/* 1781 */       this.isMsg = true;
/* 1782 */       env = new SOAPEnvelope(this.msgContext.getSOAPConstants(), this.msgContext.getSchemaVersion());
/*      */ 
/* 1785 */       for (i = 0; i < params.length; i++) {
/* 1786 */         env.addBodyElement((SOAPBodyElement)params[i]);
/*      */       }
/*      */ 
/* 1789 */       org.apache.axis.Message msg = new org.apache.axis.Message(env);
/* 1790 */       setRequestMessage(msg);
/*      */ 
/* 1792 */       invoke();
/*      */ 
/* 1794 */       msg = this.msgContext.getResponseMessage();
/* 1795 */       if (msg == null) {
/* 1796 */         if (this.msgContext.isPropertyTrue("call.FaultOnNoResponse", false)) {
/* 1797 */           throw new AxisFault(Messages.getMessage("nullResponse00"));
/*      */         }
/* 1799 */         return null;
/*      */       }
/*      */ 
/* 1803 */       env = msg.getSOAPEnvelope();
/* 1804 */       return env.getBodyElements();
/*      */     }
/*      */ 
/* 1808 */     if (this.operationName == null)
/* 1809 */       throw new AxisFault(Messages.getMessage("noOperation00"));
/*      */     try
/*      */     {
/* 1812 */       Object res = invoke(this.operationName.getNamespaceURI(), this.operationName.getLocalPart(), params);
/*      */ 
/* 1814 */       if (tlog.isDebugEnabled()) {
/* 1815 */         t1 = System.currentTimeMillis();
/* 1816 */         tlog.debug("axis.Call.invoke: " + (t1 - t0) + " " + this.operationName);
/*      */       }
/* 1818 */       return res;
/*      */     }
/*      */     catch (AxisFault af) {
/* 1821 */       if ((af.detail != null) && ((af.detail instanceof RemoteException))) {
/* 1822 */         throw ((RemoteException)af.detail);
/*      */       }
/* 1824 */       throw af;
/*      */     }
/*      */     catch (Exception exp) {
/* 1827 */       entLog.debug(Messages.getMessage("toAxisFault00"), exp);
/* 1828 */     }throw AxisFault.makeFault(exp);
/*      */   }
/*      */ 
/*      */   public void invokeOneWay(Object[] params)
/*      */   {
/*      */     try
/*      */     {
/* 1845 */       this.invokeOneWay = true;
/* 1846 */       invoke(params);
/*      */     } catch (Exception exp) {
/* 1848 */       throw new JAXRPCException(exp.toString());
/*      */     } finally {
/* 1850 */       this.invokeOneWay = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public SOAPEnvelope invoke(org.apache.axis.Message msg)
/*      */     throws AxisFault
/*      */   {
/*      */     try
/*      */     {
/* 1869 */       setRequestMessage(msg);
/* 1870 */       invoke();
/* 1871 */       msg = this.msgContext.getResponseMessage();
/* 1872 */       if (msg == null) {
/* 1873 */         if (this.msgContext.isPropertyTrue("call.FaultOnNoResponse", false)) {
/* 1874 */           throw new AxisFault(Messages.getMessage("nullResponse00"));
/*      */         }
/* 1876 */         return null;
/*      */       }
/*      */ 
/* 1879 */       SOAPEnvelope res = null;
/* 1880 */       res = msg.getSOAPEnvelope();
/* 1881 */       return res;
/*      */     }
/*      */     catch (Exception exp) {
/* 1884 */       if ((exp instanceof AxisFault)) {
/* 1885 */         throw ((AxisFault)exp);
/*      */       }
/* 1887 */       entLog.debug(Messages.getMessage("toAxisFault00"), exp);
/* 1888 */     }throw new AxisFault(Messages.getMessage("errorInvoking00", "\n" + exp));
/*      */   }
/*      */ 
/*      */   public SOAPEnvelope invoke(SOAPEnvelope env)
/*      */     throws AxisFault
/*      */   {
/*      */     try
/*      */     {
/* 1903 */       org.apache.axis.Message msg = new org.apache.axis.Message(env);
/* 1904 */       if (getProperty("javax.xml.soap.character-set-encoding") != null)
/* 1905 */         msg.setProperty("javax.xml.soap.character-set-encoding", getProperty("javax.xml.soap.character-set-encoding"));
/* 1906 */       else if (this.msgContext.getProperty("javax.xml.soap.character-set-encoding") != null) {
/* 1907 */         msg.setProperty("javax.xml.soap.character-set-encoding", this.msgContext.getProperty("javax.xml.soap.character-set-encoding"));
/*      */       }
/* 1909 */       setRequestMessage(msg);
/* 1910 */       invoke();
/* 1911 */       msg = this.msgContext.getResponseMessage();
/* 1912 */       if (msg == null) {
/* 1913 */         if (this.msgContext.isPropertyTrue("call.FaultOnNoResponse", false)) {
/* 1914 */           throw new AxisFault(Messages.getMessage("nullResponse00"));
/*      */         }
/* 1916 */         return null;
/*      */       }
/*      */ 
/* 1919 */       return msg.getSOAPEnvelope();
/*      */     }
/*      */     catch (Exception exp) {
/* 1922 */       if ((exp instanceof AxisFault)) {
/* 1923 */         throw ((AxisFault)exp);
/*      */       }
/*      */ 
/* 1926 */       entLog.debug(Messages.getMessage("toAxisFault00"), exp);
/* 1927 */     }throw AxisFault.makeFault(exp);
/*      */   }
/*      */ 
/*      */   public static void setTransportForProtocol(String protocol, Class transportClass)
/*      */   {
/* 1943 */     if (Transport.class.isAssignableFrom(transportClass)) {
/* 1944 */       transports.put(protocol, transportClass);
/*      */     }
/*      */     else
/* 1947 */       throw new InternalException(transportClass.toString());
/*      */   }
/*      */ 
/*      */   public static synchronized void initialize()
/*      */   {
/* 1961 */     addTransportPackage("org.apache.axis.transport");
/*      */ 
/* 1963 */     setTransportForProtocol("java", JavaTransport.class);
/*      */ 
/* 1965 */     setTransportForProtocol("local", LocalTransport.class);
/*      */ 
/* 1967 */     setTransportForProtocol("http", HTTPTransport.class);
/* 1968 */     setTransportForProtocol("https", HTTPTransport.class);
/*      */   }
/*      */ 
/*      */   public static synchronized void addTransportPackage(String packageName)
/*      */   {
/* 1991 */     if (transportPackages == null) {
/* 1992 */       transportPackages = new ArrayList();
/* 1993 */       String currentPackages = AxisProperties.getProperty("java.protocol.handler.pkgs");
/*      */ 
/* 1995 */       if (currentPackages != null) {
/* 1996 */         StringTokenizer tok = new StringTokenizer(currentPackages, "|");
/*      */ 
/* 1998 */         while (tok.hasMoreTokens()) {
/* 1999 */           transportPackages.add(tok.nextToken());
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2004 */     if (transportPackages.contains(packageName)) {
/* 2005 */       return;
/*      */     }
/*      */ 
/* 2008 */     transportPackages.add(packageName);
/*      */ 
/* 2010 */     StringBuffer currentPackages = new StringBuffer();
/* 2011 */     for (Iterator i = transportPackages.iterator(); i.hasNext(); ) {
/* 2012 */       String thisPackage = (String)i.next();
/* 2013 */       currentPackages.append(thisPackage);
/* 2014 */       currentPackages.append('|');
/*      */     }
/*      */ 
/* 2017 */     String transportProperty = currentPackages.toString();
/* 2018 */     AccessController.doPrivileged(new PrivilegedAction(transportProperty) { private final String val$transportProperty;
/*      */ 
/*      */       public Object run() { try { System.setProperty("java.protocol.handler.pkgs", this.val$transportProperty);
/*      */         } catch (SecurityException se) {
/*      */         }
/* 2024 */         return null;
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private Object[] getParamList(Object[] params)
/*      */   {
/* 2038 */     int numParams = 0;
/*      */ 
/* 2042 */     if (log.isDebugEnabled()) {
/* 2043 */       log.debug("operation=" + this.operation);
/* 2044 */       if (this.operation != null) {
/* 2045 */         log.debug("operation.getNumParams()=" + this.operation.getNumParams());
/*      */       }
/*      */     }
/*      */ 
/* 2049 */     if ((this.operation == null) || (this.operation.getNumParams() == 0)) {
/* 2050 */       return params;
/*      */     }
/*      */ 
/* 2056 */     numParams = this.operation.getNumInParams();
/*      */ 
/* 2058 */     if ((params == null) || (numParams != params.length)) {
/* 2059 */       throw new JAXRPCException(Messages.getMessage("parmMismatch00", "" + params.length, "" + numParams));
/*      */     }
/*      */ 
/* 2068 */     log.debug("getParamList number of params: " + params.length);
/*      */ 
/* 2072 */     Vector result = new Vector();
/* 2073 */     int j = 0;
/* 2074 */     ArrayList parameters = this.operation.getParameters();
/*      */ 
/* 2076 */     for (int i = 0; i < parameters.size(); i++) {
/* 2077 */       ParameterDesc param = (ParameterDesc)parameters.get(i);
/* 2078 */       if (param.getMode() != 2) {
/* 2079 */         QName paramQName = param.getQName();
/*      */ 
/* 2082 */         RPCParam rpcParam = null;
/* 2083 */         Object p = params[(j++)];
/* 2084 */         if ((p instanceof RPCParam))
/* 2085 */           rpcParam = (RPCParam)p;
/*      */         else {
/* 2087 */           rpcParam = new RPCParam(paramQName.getNamespaceURI(), paramQName.getLocalPart(), p);
/*      */         }
/*      */ 
/* 2094 */         rpcParam.setParamDesc(param);
/*      */ 
/* 2098 */         if (param.isInHeader())
/* 2099 */           addHeader(new RPCHeaderParam(rpcParam));
/*      */         else {
/* 2101 */           result.add(rpcParam);
/*      */         }
/*      */       }
/*      */     }
/* 2105 */     return result.toArray();
/*      */   }
/*      */ 
/*      */   public void setTransport(Transport trans)
/*      */   {
/* 2117 */     this.transport = trans;
/* 2118 */     if (log.isDebugEnabled())
/* 2119 */       log.debug(Messages.getMessage("transport00", "" + this.transport));
/*      */   }
/*      */ 
/*      */   public Transport getTransportForProtocol(String protocol)
/*      */   {
/* 2132 */     Class transportClass = (Class)transports.get(protocol);
/* 2133 */     Transport ret = null;
/* 2134 */     if (transportClass != null)
/*      */       try {
/* 2136 */         ret = (Transport)transportClass.newInstance();
/*      */       } catch (InstantiationException e) {
/*      */       }
/*      */       catch (IllegalAccessException e) {
/*      */       }
/* 2141 */     return ret;
/*      */   }
/*      */ 
/*      */   public void setRequestMessage(org.apache.axis.Message msg)
/*      */   {
/* 2156 */     String attachformat = (String)getProperty("attachment_encapsulation_format");
/*      */ 
/* 2159 */     if (null != attachformat) {
/* 2160 */       Attachments attachments = msg.getAttachmentsImpl();
/* 2161 */       if (null != attachments) {
/* 2162 */         if ("axis.attachment.style.mime".equals(attachformat))
/* 2163 */           attachments.setSendType(2);
/* 2164 */         else if ("axis.attachment.style.mtom".equals(attachformat))
/* 2165 */           attachments.setSendType(4);
/* 2166 */         else if ("axis.attachment.style.dime".equals(attachformat)) {
/* 2167 */           attachments.setSendType(3);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2172 */     if ((null != this.attachmentParts) && (!this.attachmentParts.isEmpty())) {
/*      */       try {
/* 2174 */         Attachments attachments = msg.getAttachmentsImpl();
/* 2175 */         if (null == attachments) {
/* 2176 */           throw new RuntimeException(Messages.getMessage("noAttachments"));
/*      */         }
/*      */ 
/* 2180 */         attachments.setAttachmentParts(this.attachmentParts);
/*      */       } catch (AxisFault ex) {
/* 2182 */         log.info(Messages.getMessage("axisFault00"), ex);
/* 2183 */         throw new RuntimeException(ex.getMessage());
/*      */       }
/*      */     }
/*      */ 
/* 2187 */     this.msgContext.setRequestMessage(msg);
/* 2188 */     this.attachmentParts.clear();
/*      */   }
/*      */ 
/*      */   public org.apache.axis.Message getResponseMessage()
/*      */   {
/* 2201 */     return this.msgContext.getResponseMessage();
/*      */   }
/*      */ 
/*      */   public MessageContext getMessageContext()
/*      */   {
/* 2212 */     return this.msgContext;
/*      */   }
/*      */ 
/*      */   public void addHeader(SOAPHeaderElement header)
/*      */   {
/* 2225 */     if (this.myHeaders == null) {
/* 2226 */       this.myHeaders = new Vector();
/*      */     }
/* 2228 */     this.myHeaders.add(header);
/*      */   }
/*      */ 
/*      */   public void clearHeaders()
/*      */   {
/* 2238 */     this.myHeaders = null;
/*      */   }
/*      */ 
/*      */   public TypeMapping getTypeMapping()
/*      */   {
/* 2244 */     TypeMappingRegistry tmr = this.msgContext.getTypeMappingRegistry();
/*      */ 
/* 2247 */     return tmr.getOrMakeTypeMapping(getEncodingStyle());
/*      */   }
/*      */ 
/*      */   public void registerTypeMapping(Class javaType, QName xmlType, SerializerFactory sf, DeserializerFactory df)
/*      */   {
/* 2262 */     registerTypeMapping(javaType, xmlType, sf, df, true);
/*      */   }
/*      */ 
/*      */   public void registerTypeMapping(Class javaType, QName xmlType, SerializerFactory sf, DeserializerFactory df, boolean force)
/*      */   {
/* 2279 */     TypeMapping tm = getTypeMapping();
/* 2280 */     if ((!force) && (tm.isRegistered(javaType, xmlType))) {
/* 2281 */       return;
/*      */     }
/*      */ 
/* 2285 */     tm.register(javaType, xmlType, sf, df);
/*      */   }
/*      */ 
/*      */   public void registerTypeMapping(Class javaType, QName xmlType, Class sfClass, Class dfClass)
/*      */   {
/* 2297 */     registerTypeMapping(javaType, xmlType, sfClass, dfClass, true);
/*      */   }
/*      */ 
/*      */   public void registerTypeMapping(Class javaType, QName xmlType, Class sfClass, Class dfClass, boolean force)
/*      */   {
/* 2315 */     SerializerFactory sf = BaseSerializerFactory.createFactory(sfClass, javaType, xmlType);
/*      */ 
/* 2317 */     DeserializerFactory df = BaseDeserializerFactory.createFactory(dfClass, javaType, xmlType);
/*      */ 
/* 2321 */     if ((sf != null) || (df != null))
/* 2322 */       registerTypeMapping(javaType, xmlType, sf, df, force);
/*      */   }
/*      */ 
/*      */   public Object invoke(String namespace, String method, Object[] args)
/*      */     throws AxisFault
/*      */   {
/* 2350 */     if (log.isDebugEnabled()) {
/* 2351 */       log.debug("Enter: Call::invoke(ns, meth, args)");
/*      */     }
/*      */ 
/* 2359 */     if ((getReturnType() != null) && (args != null) && (args.length != 0) && (this.operation.getNumParams() == 0))
/*      */     {
/* 2361 */       throw new AxisFault(Messages.getMessage("mustSpecifyParms"));
/*      */     }
/*      */ 
/* 2364 */     RPCElement body = new RPCElement(namespace, method, getParamList(args));
/*      */ 
/* 2366 */     Object ret = invoke(body);
/*      */ 
/* 2368 */     if (log.isDebugEnabled()) {
/* 2369 */       log.debug("Exit: Call::invoke(ns, meth, args)");
/*      */     }
/*      */ 
/* 2372 */     return ret;
/*      */   }
/*      */ 
/*      */   public Object invoke(String method, Object[] args)
/*      */     throws AxisFault
/*      */   {
/* 2391 */     return invoke("", method, args);
/*      */   }
/*      */ 
/*      */   public Object invoke(RPCElement body)
/*      */     throws AxisFault
/*      */   {
/* 2404 */     if (log.isDebugEnabled()) {
/* 2405 */       log.debug("Enter: Call::invoke(RPCElement)");
/*      */     }
/*      */ 
/* 2413 */     if ((!this.invokeOneWay) && (this.operation != null) && (this.operation.getNumParams() > 0) && (getReturnType() == null))
/*      */     {
/* 2418 */       log.error(Messages.getMessage("mustSpecifyReturnType"));
/*      */     }
/*      */ 
/* 2421 */     SOAPEnvelope reqEnv = new SOAPEnvelope(this.msgContext.getSOAPConstants(), this.msgContext.getSchemaVersion());
/*      */ 
/* 2424 */     SOAPEnvelope resEnv = null;
/* 2425 */     org.apache.axis.Message reqMsg = new org.apache.axis.Message(reqEnv);
/* 2426 */     org.apache.axis.Message resMsg = null;
/* 2427 */     Vector resArgs = null;
/* 2428 */     Object result = null;
/*      */ 
/* 2431 */     this.outParams = new HashMap();
/* 2432 */     this.outParamsList = new ArrayList();
/*      */     try
/*      */     {
/* 2436 */       body.setEncodingStyle(getEncodingStyle());
/*      */ 
/* 2438 */       setRequestMessage(reqMsg);
/*      */ 
/* 2440 */       reqEnv.addBodyElement(body);
/* 2441 */       reqEnv.setMessageType("request");
/*      */ 
/* 2443 */       invoke();
/*      */     } catch (Exception e) {
/* 2445 */       entLog.debug(Messages.getMessage("toAxisFault00"), e);
/* 2446 */       throw AxisFault.makeFault(e);
/*      */     }
/*      */ 
/* 2449 */     resMsg = this.msgContext.getResponseMessage();
/*      */ 
/* 2451 */     if (resMsg == null) {
/* 2452 */       if (this.msgContext.isPropertyTrue("call.FaultOnNoResponse", false)) {
/* 2453 */         throw new AxisFault(Messages.getMessage("nullResponse00"));
/*      */       }
/* 2455 */       return null;
/*      */     }
/*      */ 
/* 2459 */     resEnv = resMsg.getSOAPEnvelope();
/* 2460 */     SOAPBodyElement bodyEl = resEnv.getFirstBody();
/* 2461 */     if (bodyEl == null) {
/* 2462 */       return null;
/*      */     }
/*      */ 
/* 2465 */     if ((bodyEl instanceof RPCElement)) {
/*      */       try {
/* 2467 */         resArgs = ((RPCElement)bodyEl).getParams();
/*      */       } catch (Exception e) {
/* 2469 */         log.error(Messages.getMessage("exception00"), e);
/* 2470 */         throw AxisFault.makeFault(e);
/*      */       }
/*      */ 
/* 2473 */       if ((resArgs != null) && (resArgs.size() > 0))
/*      */       {
/* 2477 */         int outParamStart = 0;
/*      */ 
/* 2502 */         boolean findReturnParam = false;
/* 2503 */         QName returnParamQName = null;
/* 2504 */         if (this.operation != null) {
/* 2505 */           returnParamQName = this.operation.getReturnQName();
/*      */         }
/*      */ 
/* 2508 */         if (!XMLType.AXIS_VOID.equals(getReturnType())) {
/* 2509 */           if (returnParamQName == null)
/*      */           {
/* 2511 */             RPCParam param = (RPCParam)resArgs.get(0);
/* 2512 */             result = param.getObjectValue();
/* 2513 */             outParamStart = 1;
/*      */           }
/*      */           else
/*      */           {
/* 2517 */             findReturnParam = true;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2528 */         for (int i = outParamStart; i < resArgs.size(); i++) {
/* 2529 */           RPCParam param = (RPCParam)resArgs.get(i);
/*      */ 
/* 2531 */           Class javaType = getJavaTypeForQName(param.getQName());
/* 2532 */           Object value = param.getObjectValue();
/*      */ 
/* 2535 */           if ((javaType != null) && (value != null) && (!javaType.isAssignableFrom(value.getClass())))
/*      */           {
/* 2537 */             value = JavaUtils.convert(value, javaType);
/*      */           }
/*      */ 
/* 2542 */           if ((findReturnParam) && (returnParamQName.equals(param.getQName())))
/*      */           {
/* 2545 */             result = value;
/* 2546 */             findReturnParam = false;
/*      */           } else {
/* 2548 */             this.outParams.put(param.getQName(), value);
/* 2549 */             this.outParamsList.add(value);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2560 */         if (findReturnParam) {
/* 2561 */           Iterator it = this.outParams.keySet().iterator();
/* 2562 */           while ((findReturnParam) && (it.hasNext())) {
/* 2563 */             QName qname = (QName)it.next();
/* 2564 */             ParameterDesc paramDesc = this.operation.getOutputParamByQName(qname);
/*      */ 
/* 2566 */             if (paramDesc == null)
/*      */             {
/* 2568 */               findReturnParam = false;
/* 2569 */               result = this.outParams.remove(qname);
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2576 */         if (findReturnParam) {
/* 2577 */           String returnParamName = returnParamQName.toString();
/* 2578 */           throw new AxisFault(Messages.getMessage("noReturnParam", returnParamName));
/*      */         }
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*      */       try {
/* 2585 */         result = bodyEl.getValueAsType(getReturnType());
/*      */       }
/*      */       catch (Exception e) {
/* 2588 */         result = bodyEl;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2593 */     if (log.isDebugEnabled()) {
/* 2594 */       log.debug("Exit: Call::invoke(RPCElement)");
/*      */     }
/*      */ 
/* 2598 */     if ((this.operation != null) && (this.operation.getReturnClass() != null)) {
/* 2599 */       result = JavaUtils.convert(result, this.operation.getReturnClass());
/*      */     }
/*      */ 
/* 2602 */     return result;
/*      */   }
/*      */ 
/*      */   private Class getJavaTypeForQName(QName name)
/*      */   {
/* 2612 */     if (this.operation == null) {
/* 2613 */       return null;
/*      */     }
/* 2615 */     ParameterDesc param = this.operation.getOutputParamByQName(name);
/* 2616 */     return param == null ? null : param.getJavaType();
/*      */   }
/*      */ 
/*      */   public void setOption(String name, Object value)
/*      */   {
/* 2625 */     this.service.getEngine().setOption(name, value);
/*      */   }
/*      */ 
/*      */   public void invoke()
/*      */     throws AxisFault
/*      */   {
/* 2637 */     if (log.isDebugEnabled()) {
/* 2638 */       log.debug("Enter: Call::invoke()");
/*      */     }
/*      */ 
/* 2641 */     this.isNeverInvoked = false;
/*      */ 
/* 2643 */     org.apache.axis.Message reqMsg = null;
/* 2644 */     SOAPEnvelope reqEnv = null;
/*      */ 
/* 2646 */     this.msgContext.reset();
/* 2647 */     this.msgContext.setResponseMessage(null);
/* 2648 */     this.msgContext.setProperty("call_object", this);
/* 2649 */     this.msgContext.setProperty("wsdl.service", this.service);
/* 2650 */     this.msgContext.setProperty("wsdl.portName", getPortName());
/* 2651 */     if (this.isMsg) {
/* 2652 */       this.msgContext.setProperty("isMsg", "true");
/*      */     }
/*      */ 
/* 2655 */     if (this.username != null) {
/* 2656 */       this.msgContext.setUsername(this.username);
/*      */     }
/* 2658 */     if (this.password != null) {
/* 2659 */       this.msgContext.setPassword(this.password);
/*      */     }
/* 2661 */     this.msgContext.setMaintainSession(this.maintainSession);
/*      */ 
/* 2663 */     if (this.operation != null) {
/* 2664 */       this.msgContext.setOperation(this.operation);
/* 2665 */       this.operation.setStyle(getOperationStyle());
/* 2666 */       this.operation.setUse(getOperationUse());
/*      */     }
/*      */ 
/* 2669 */     if (this.useSOAPAction) {
/* 2670 */       this.msgContext.setUseSOAPAction(true);
/*      */     }
/* 2672 */     if (this.SOAPActionURI != null)
/* 2673 */       this.msgContext.setSOAPActionURI(this.SOAPActionURI);
/*      */     else {
/* 2675 */       this.msgContext.setSOAPActionURI(null);
/*      */     }
/* 2677 */     if (this.timeout != null) {
/* 2678 */       this.msgContext.setTimeout(this.timeout.intValue());
/*      */     }
/* 2680 */     this.msgContext.setHighFidelity(!this.useStreaming);
/*      */ 
/* 2683 */     if (this.myService != null)
/*      */     {
/* 2685 */       this.msgContext.setService(this.myService);
/*      */     }
/* 2687 */     else if (this.portName != null)
/*      */     {
/* 2690 */       this.msgContext.setTargetService(this.portName.getLocalPart());
/*      */     }
/*      */     else {
/* 2693 */       reqMsg = this.msgContext.getRequestMessage();
/*      */ 
/* 2695 */       boolean isStream = ((SOAPPart)reqMsg.getSOAPPart()).isBodyStream();
/*      */ 
/* 2697 */       if ((reqMsg != null) && (!isStream)) {
/* 2698 */         reqEnv = reqMsg.getSOAPEnvelope();
/*      */ 
/* 2700 */         SOAPBodyElement body = reqEnv.getFirstBody();
/*      */ 
/* 2702 */         if (body != null) {
/* 2703 */           if (body.getNamespaceURI() == null) {
/* 2704 */             throw new AxisFault("Call.invoke", Messages.getMessage("cantInvoke00", body.getName()), null, null);
/*      */           }
/*      */ 
/* 2708 */           this.msgContext.setTargetService(body.getNamespaceURI());
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2714 */     if (log.isDebugEnabled()) {
/* 2715 */       log.debug(Messages.getMessage("targetService", this.msgContext.getTargetService()));
/*      */     }
/*      */ 
/* 2719 */     org.apache.axis.Message requestMessage = this.msgContext.getRequestMessage();
/* 2720 */     if (requestMessage != null) {
/*      */       try {
/* 2722 */         this.msgContext.setProperty("javax.xml.soap.character-set-encoding", requestMessage.getProperty("javax.xml.soap.character-set-encoding"));
/*      */       }
/*      */       catch (SOAPException e) {
/*      */       }
/* 2726 */       if (this.myHeaders != null) {
/* 2727 */         reqEnv = requestMessage.getSOAPEnvelope();
/*      */ 
/* 2730 */         for (int i = 0; (this.myHeaders != null) && (i < this.myHeaders.size()); i++) {
/* 2731 */           reqEnv.addHeader((SOAPHeaderElement)this.myHeaders.get(i));
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2737 */     if (this.transport != null) {
/* 2738 */       this.transport.setupMessageContext(this.msgContext, this, this.service.getEngine());
/*      */     }
/*      */     else {
/* 2741 */       this.msgContext.setTransportName(this.transportName);
/*      */     }
/*      */ 
/* 2744 */     SOAPService svc = this.msgContext.getService();
/* 2745 */     if (svc != null)
/* 2746 */       svc.setPropertyParent(this.myProperties);
/*      */     else {
/* 2748 */       this.msgContext.setPropertyParent(this.myProperties);
/*      */     }
/*      */ 
/* 2752 */     if (log.isDebugEnabled()) {
/* 2753 */       StringWriter writer = new StringWriter();
/*      */       try {
/* 2755 */         SerializationContext ctx = new SerializationContext(writer, this.msgContext);
/*      */ 
/* 2757 */         requestMessage.getSOAPEnvelope().output(ctx);
/* 2758 */         writer.close();
/*      */       } catch (Exception e) {
/* 2760 */         throw AxisFault.makeFault(e);
/*      */       } finally {
/* 2762 */         log.debug(writer.getBuffer().toString());
/*      */       }
/*      */     }
/*      */ 
/* 2766 */     if (!this.invokeOneWay)
/* 2767 */       invokeEngine(this.msgContext);
/*      */     else {
/* 2769 */       invokeEngineOneWay(this.msgContext);
/*      */     }
/*      */ 
/* 2772 */     if (log.isDebugEnabled())
/* 2773 */       log.debug("Exit: Call::invoke()");
/*      */   }
/*      */ 
/*      */   private void invokeEngine(MessageContext msgContext)
/*      */     throws AxisFault
/*      */   {
/* 2784 */     this.service.getEngine().invoke(msgContext);
/*      */ 
/* 2786 */     if (this.transport != null) {
/* 2787 */       this.transport.processReturnedMessageContext(msgContext);
/*      */     }
/*      */ 
/* 2790 */     org.apache.axis.Message resMsg = msgContext.getResponseMessage();
/*      */ 
/* 2792 */     if (resMsg == null) {
/* 2793 */       if (msgContext.isPropertyTrue("call.FaultOnNoResponse", false)) {
/* 2794 */         throw new AxisFault(Messages.getMessage("nullResponse00"));
/*      */       }
/* 2796 */       return;
/*      */     }
/*      */ 
/* 2802 */     resMsg.setMessageType("response");
/*      */ 
/* 2804 */     SOAPEnvelope resEnv = resMsg.getSOAPEnvelope();
/*      */ 
/* 2806 */     SOAPBodyElement respBody = resEnv.getFirstBody();
/* 2807 */     if ((respBody instanceof SOAPFault))
/*      */     {
/* 2809 */       if ((this.operation == null) || (this.operation.getReturnClass() == null) || (this.operation.getReturnClass() != SOAPMessage.class))
/*      */       {
/* 2816 */         throw ((SOAPFault)respBody).getFault();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void invokeEngineOneWay(MessageContext msgContext)
/*      */   {
/* 2828 */     Runnable runnable = new Runnable(msgContext) { private final MessageContext val$msgContext;
/*      */ 
/* 2830 */       public void run() { this.val$msgContext.setProperty("axis.one.way", Boolean.TRUE);
/*      */         try {
/* 2832 */           Call.this.service.getEngine().invoke(this.val$msgContext);
/*      */         }
/*      */         catch (AxisFault af) {
/* 2835 */           Call.log.debug(Messages.getMessage("exceptionPrinting"), af);
/*      */         }
/* 2837 */         this.val$msgContext.removeProperty("axis.one.way");
/*      */       }
/*      */     };
/* 2841 */     Thread thread = new Thread(runnable);
/*      */ 
/* 2843 */     thread.start();
/*      */   }
/*      */ 
/*      */   public Map getOutputParams()
/*      */   {
/* 2856 */     if (this.isNeverInvoked) {
/* 2857 */       throw new JAXRPCException(Messages.getMessage("outputParamsUnavailable"));
/*      */     }
/*      */ 
/* 2860 */     return this.outParams;
/*      */   }
/*      */ 
/*      */   public List getOutputValues()
/*      */   {
/* 2875 */     if (this.isNeverInvoked) {
/* 2876 */       throw new JAXRPCException(Messages.getMessage("outputParamsUnavailable"));
/*      */     }
/*      */ 
/* 2879 */     return this.outParamsList;
/*      */   }
/*      */ 
/*      */   public Service getService()
/*      */   {
/* 2891 */     return this.service;
/*      */   }
/*      */ 
/*      */   public void setSOAPService(SOAPService service)
/*      */   {
/* 2902 */     this.myService = service;
/* 2903 */     if (service != null)
/*      */     {
/* 2910 */       service.setEngine(this.service.getAxisClient());
/* 2911 */       service.setPropertyParent(this.myProperties);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setClientHandlers(Handler reqHandler, Handler respHandler)
/*      */   {
/* 2924 */     setSOAPService(new SOAPService(reqHandler, null, respHandler));
/*      */   }
/*      */ 
/*      */   public void addAttachmentPart(Object attachment)
/*      */   {
/* 2938 */     this.attachmentParts.add(attachment);
/*      */   }
/*      */ 
/*      */   public void addFault(QName qname, Class cls, QName xmlType, boolean isComplex)
/*      */   {
/* 2953 */     if (this.operationSetManually) {
/* 2954 */       throw new RuntimeException(Messages.getMessage("operationAlreadySet"));
/*      */     }
/*      */ 
/* 2958 */     if (this.operation == null) {
/* 2959 */       this.operation = new OperationDesc();
/*      */     }
/*      */ 
/* 2962 */     FaultDesc fault = new FaultDesc();
/* 2963 */     fault.setQName(qname);
/* 2964 */     fault.setClassName(cls.getName());
/* 2965 */     fault.setXmlType(xmlType);
/* 2966 */     fault.setComplex(isComplex);
/* 2967 */     this.operation.addFault(fault);
/*      */   }
/*      */ 
/*      */   public void setOperation(OperationDesc operation)
/*      */   {
/* 2978 */     this.operation = operation;
/* 2979 */     this.operationSetManually = true;
/*      */   }
/*      */ 
/*      */   public OperationDesc getOperation()
/*      */   {
/* 2984 */     return this.operation;
/*      */   }
/*      */ 
/*      */   public void clearOperation() {
/* 2988 */     this.operation = null;
/* 2989 */     this.operationSetManually = false;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  311 */     initialize();
/*      */ 
/*  560 */     propertyNames = new ArrayList();
/*      */ 
/*  562 */     propertyNames.add("javax.xml.rpc.security.auth.username");
/*  563 */     propertyNames.add("javax.xml.rpc.security.auth.password");
/*  564 */     propertyNames.add("javax.xml.rpc.session.maintain");
/*  565 */     propertyNames.add("javax.xml.rpc.soap.operation.style");
/*  566 */     propertyNames.add("javax.xml.rpc.soap.http.soapaction.use");
/*  567 */     propertyNames.add("javax.xml.rpc.soap.http.soapaction.uri");
/*  568 */     propertyNames.add("javax.xml.rpc.encodingstyle.namespace.uri");
/*  569 */     propertyNames.add("javax.xml.rpc.service.endpoint.address");
/*  570 */     propertyNames.add("transport_name");
/*  571 */     propertyNames.add("attachment_encapsulation_format");
/*  572 */     propertyNames.add("axis.connection.timeout");
/*  573 */     propertyNames.add("javax.xml.soap.character-set-encoding");
/*      */ 
/* 1975 */     transportPackages = null;
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.client.Call
 * JD-Core Version:    0.6.0
 */