/*      */ package org.apache.axis;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.Set;
/*      */ import javax.xml.namespace.QName;
/*      */ import javax.xml.rpc.handler.soap.SOAPMessageContext;
/*      */ import javax.xml.soap.SOAPMessage;
/*      */ import org.apache.axis.attachments.Attachments;
/*      */ import org.apache.axis.client.AxisClient;
/*      */ import org.apache.axis.components.logger.LogFactory;
/*      */ import org.apache.axis.constants.Style;
/*      */ import org.apache.axis.constants.Use;
/*      */ import org.apache.axis.description.OperationDesc;
/*      */ import org.apache.axis.description.ServiceDesc;
/*      */ import org.apache.axis.encoding.TypeMapping;
/*      */ import org.apache.axis.encoding.TypeMappingRegistry;
/*      */ import org.apache.axis.handlers.soap.SOAPService;
/*      */ import org.apache.axis.schema.SchemaVersion;
/*      */ import org.apache.axis.session.Session;
/*      */ import org.apache.axis.soap.SOAPConstants;
/*      */ import org.apache.axis.utils.JavaUtils;
/*      */ import org.apache.axis.utils.LockableHashtable;
/*      */ import org.apache.axis.utils.Messages;
/*      */ import org.apache.commons.logging.Log;
/*      */ 
/*      */ public class MessageContext
/*      */   implements SOAPMessageContext
/*      */ {
/*   75 */   protected static Log log = LogFactory.getLog(MessageContext.class.getName());
/*      */   private Message requestMessage;
/*      */   private Message responseMessage;
/*      */   private String targetService;
/*      */   private String transportName;
/*      */   private ClassLoader classLoader;
/*      */   private AxisEngine axisEngine;
/*      */   private Session session;
/*  125 */   private boolean maintainSession = false;
/*      */ 
/*  134 */   private boolean havePassedPivot = false;
/*      */ 
/*  139 */   private int timeout = 600000;
/*      */ 
/*  146 */   private boolean highFidelity = true;
/*      */ 
/*  152 */   private LockableHashtable bag = new LockableHashtable();
/*      */ 
/*  163 */   private String username = null;
/*  164 */   private String password = null;
/*  165 */   private String encodingStyle = Use.ENCODED.getEncoding();
/*  166 */   private boolean useSOAPAction = false;
/*  167 */   private String SOAPActionURI = null;
/*      */   private String[] roles;
/*  175 */   private SOAPConstants soapConstants = Constants.DEFAULT_SOAP_VERSION;
/*      */ 
/*  178 */   private SchemaVersion schemaVersion = SchemaVersion.SCHEMA_2001;
/*      */ 
/*  181 */   private OperationDesc currentOperation = null;
/*      */ 
/*  301 */   protected static String systemTempDir = null;
/*      */ 
/*  390 */   private TypeMappingRegistry mappingRegistry = null;
/*      */   private SOAPService serviceHandler;
/*      */   public static final String ENGINE_HANDLER = "engine.handler";
/*      */   public static final String TRANS_URL = "transport.url";
/*      */   public static final String QUIT_REQUESTED = "quit.requested";
/*      */   public static final String AUTHUSER = "authenticatedUser";
/*      */   public static final String CALL = "call_object";
/*      */   public static final String IS_MSG = "isMsg";
/*      */   public static final String ATTACHMENTS_DIR = "attachments.directory";
/*      */   public static final String ACCEPTMISSINGPARAMS = "acceptMissingParams";
/*      */   public static final String WSDLGEN_INTFNAMESPACE = "axis.wsdlgen.intfnamespace";
/*      */   public static final String WSDLGEN_SERV_LOC_URL = "axis.wsdlgen.serv.loc.url";
/*      */   public static final String HTTP_TRANSPORT_VERSION = "axis.transport.version";
/*      */   public static final String SECURITY_PROVIDER = "securityProvider";
/*      */ 
/*      */   public OperationDesc getOperation()
/*      */   {
/*  190 */     return this.currentOperation;
/*      */   }
/*      */ 
/*      */   public void setOperation(OperationDesc operation)
/*      */   {
/*  200 */     this.currentOperation = operation;
/*      */   }
/*      */ 
/*      */   public OperationDesc[] getPossibleOperationsByQName(QName qname)
/*      */     throws AxisFault
/*      */   {
/*  216 */     if (this.currentOperation != null) {
/*  217 */       return new OperationDesc[] { this.currentOperation };
/*      */     }
/*      */ 
/*  220 */     OperationDesc[] possibleOperations = null;
/*      */ 
/*  222 */     if (this.serviceHandler == null) {
/*      */       try {
/*  224 */         if (log.isDebugEnabled()) {
/*  225 */           log.debug(Messages.getMessage("dispatching00", qname.getNamespaceURI()));
/*      */         }
/*      */ 
/*  230 */         setService(this.axisEngine.getConfig().getServiceByNamespaceURI(qname.getNamespaceURI()));
/*      */       }
/*      */       catch (ConfigurationException e)
/*      */       {
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  238 */     if (this.serviceHandler != null) {
/*  239 */       ServiceDesc desc = this.serviceHandler.getInitializedServiceDesc(this);
/*      */ 
/*  241 */       if (desc != null) {
/*  242 */         if (desc.getStyle() != Style.DOCUMENT) {
/*  243 */           possibleOperations = desc.getOperationsByQName(qname);
/*      */         }
/*      */         else
/*      */         {
/*  248 */           ArrayList allOperations = desc.getOperations();
/*  249 */           ArrayList foundOperations = new ArrayList();
/*  250 */           for (int i = 0; i < allOperations.size(); i++) {
/*  251 */             OperationDesc tryOp = (OperationDesc)allOperations.get(i);
/*      */ 
/*  253 */             if (tryOp.getParamByQName(qname) != null) {
/*  254 */               foundOperations.add(tryOp);
/*      */             }
/*      */           }
/*  257 */           if (foundOperations.size() > 0) {
/*  258 */             possibleOperations = (OperationDesc[])JavaUtils.convert(foundOperations, new OperationDesc[0].getClass());
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  265 */     return possibleOperations;
/*      */   }
/*      */ 
/*      */   public OperationDesc getOperationByQName(QName qname)
/*      */     throws AxisFault
/*      */   {
/*  279 */     if (this.currentOperation == null) {
/*  280 */       OperationDesc[] possibleOperations = getPossibleOperationsByQName(qname);
/*  281 */       if ((possibleOperations != null) && (possibleOperations.length > 0)) {
/*  282 */         this.currentOperation = possibleOperations[0];
/*      */       }
/*      */     }
/*      */ 
/*  286 */     return this.currentOperation;
/*      */   }
/*      */ 
/*      */   public static MessageContext getCurrentContext()
/*      */   {
/*  295 */     return AxisEngine.getCurrentMessageContext();
/*      */   }
/*      */ 
/*      */   public MessageContext(AxisEngine engine)
/*      */   {
/*  341 */     this.axisEngine = engine;
/*      */ 
/*  343 */     if (null != engine) {
/*  344 */       Hashtable opts = engine.getOptions();
/*  345 */       String attachmentsdir = null;
/*  346 */       if (null != opts) {
/*  347 */         attachmentsdir = (String)opts.get("attachments.Directory");
/*      */       }
/*  349 */       if (null == attachmentsdir) {
/*  350 */         attachmentsdir = systemTempDir;
/*      */       }
/*  352 */       if (attachmentsdir != null) {
/*  353 */         setProperty("attachments.directory", attachmentsdir);
/*      */       }
/*      */ 
/*  358 */       String defaultSOAPVersion = (String)engine.getOption("defaultSOAPVersion");
/*      */ 
/*  360 */       if ((defaultSOAPVersion != null) && ("1.2".equals(defaultSOAPVersion))) {
/*  361 */         setSOAPConstants(SOAPConstants.SOAP12_CONSTANTS);
/*      */       }
/*      */ 
/*  364 */       String singleSOAPVersion = (String)engine.getOption("singleSOAPVersion");
/*      */ 
/*  366 */       if (singleSOAPVersion != null)
/*  367 */         if ("1.2".equals(singleSOAPVersion)) {
/*  368 */           setProperty("SingleSOAPVersion", SOAPConstants.SOAP12_CONSTANTS);
/*      */         }
/*  370 */         else if ("1.1".equals(singleSOAPVersion))
/*  371 */           setProperty("SingleSOAPVersion", SOAPConstants.SOAP11_CONSTANTS);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void finalize()
/*      */   {
/*  383 */     dispose();
/*      */   }
/*      */ 
/*      */   public void setTypeMappingRegistry(TypeMappingRegistry reg)
/*      */   {
/*  399 */     this.mappingRegistry = reg;
/*      */   }
/*      */ 
/*      */   public TypeMappingRegistry getTypeMappingRegistry()
/*      */   {
/*  412 */     if (this.mappingRegistry == null) {
/*  413 */       return this.axisEngine.getTypeMappingRegistry();
/*      */     }
/*      */ 
/*  416 */     return this.mappingRegistry;
/*      */   }
/*      */ 
/*      */   public TypeMapping getTypeMapping()
/*      */   {
/*  426 */     return (TypeMapping)getTypeMappingRegistry().getTypeMapping(this.encodingStyle);
/*      */   }
/*      */ 
/*      */   public String getTransportName()
/*      */   {
/*  437 */     return this.transportName;
/*      */   }
/*      */ 
/*      */   public void setTransportName(String transportName)
/*      */   {
/*  450 */     this.transportName = transportName;
/*      */   }
/*      */ 
/*      */   public SOAPConstants getSOAPConstants()
/*      */   {
/*  459 */     return this.soapConstants;
/*      */   }
/*      */ 
/*      */   public void setSOAPConstants(SOAPConstants soapConstants)
/*      */   {
/*  471 */     if (this.soapConstants.getEncodingURI().equals(this.encodingStyle)) {
/*  472 */       this.encodingStyle = soapConstants.getEncodingURI();
/*      */     }
/*      */ 
/*  475 */     this.soapConstants = soapConstants;
/*      */   }
/*      */ 
/*      */   public SchemaVersion getSchemaVersion()
/*      */   {
/*  484 */     return this.schemaVersion;
/*      */   }
/*      */ 
/*      */   public void setSchemaVersion(SchemaVersion schemaVersion)
/*      */   {
/*  493 */     this.schemaVersion = schemaVersion;
/*      */   }
/*      */ 
/*      */   public Session getSession()
/*      */   {
/*  503 */     return this.session;
/*      */   }
/*      */ 
/*      */   public void setSession(Session session)
/*      */   {
/*  513 */     this.session = session;
/*      */   }
/*      */ 
/*      */   public boolean isEncoded()
/*      */   {
/*  522 */     return getOperationUse() == Use.ENCODED;
/*      */   }
/*      */ 
/*      */   public void setMaintainSession(boolean yesno)
/*      */   {
/*  532 */     this.maintainSession = yesno;
/*      */   }
/*      */ 
/*      */   public boolean getMaintainSession()
/*      */   {
/*  542 */     return this.maintainSession;
/*      */   }
/*      */ 
/*      */   public Message getRequestMessage()
/*      */   {
/*  551 */     return this.requestMessage;
/*      */   }
/*      */ 
/*      */   public void setRequestMessage(Message reqMsg)
/*      */   {
/*  561 */     this.requestMessage = reqMsg;
/*  562 */     if (this.requestMessage != null)
/*  563 */       this.requestMessage.setMessageContext(this);
/*      */   }
/*      */ 
/*      */   public Message getResponseMessage()
/*      */   {
/*  572 */     return this.responseMessage;
/*      */   }
/*      */ 
/*      */   public void setResponseMessage(Message respMsg)
/*      */   {
/*  581 */     this.responseMessage = respMsg;
/*  582 */     if (this.responseMessage != null) {
/*  583 */       this.responseMessage.setMessageContext(this);
/*      */ 
/*  587 */       Message reqMsg = getRequestMessage();
/*  588 */       if (null != reqMsg) {
/*  589 */         Attachments reqAttch = reqMsg.getAttachmentsImpl();
/*  590 */         Attachments respAttch = respMsg.getAttachmentsImpl();
/*  591 */         if ((null != reqAttch) && (null != respAttch) && 
/*  592 */           (respAttch.getSendType() == 1))
/*      */         {
/*  594 */           respAttch.setSendType(reqAttch.getSendType());
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public Message getCurrentMessage()
/*      */   {
/*  608 */     return this.havePassedPivot ? this.responseMessage : this.requestMessage;
/*      */   }
/*      */ 
/*      */   public SOAPMessage getMessage()
/*      */   {
/*  619 */     return getCurrentMessage();
/*      */   }
/*      */ 
/*      */   public void setCurrentMessage(Message curMsg)
/*      */   {
/*  630 */     if (curMsg != null) {
/*  631 */       curMsg.setMessageContext(this);
/*      */     }
/*  633 */     if (this.havePassedPivot)
/*  634 */       this.responseMessage = curMsg;
/*      */     else
/*  636 */       this.requestMessage = curMsg;
/*      */   }
/*      */ 
/*      */   public void setMessage(SOAPMessage message)
/*      */   {
/*  649 */     setCurrentMessage((Message)message);
/*      */   }
/*      */ 
/*      */   public boolean getPastPivot()
/*      */   {
/*  659 */     return this.havePassedPivot;
/*      */   }
/*      */ 
/*      */   public void setPastPivot(boolean pastPivot)
/*      */   {
/*  673 */     this.havePassedPivot = pastPivot;
/*      */   }
/*      */ 
/*      */   public void setTimeout(int value)
/*      */   {
/*  682 */     this.timeout = value;
/*      */   }
/*      */ 
/*      */   public int getTimeout()
/*      */   {
/*  691 */     return this.timeout;
/*      */   }
/*      */ 
/*      */   public ClassLoader getClassLoader()
/*      */   {
/*  701 */     if (this.classLoader == null) {
/*  702 */       this.classLoader = Thread.currentThread().getContextClassLoader();
/*      */     }
/*  704 */     return this.classLoader;
/*      */   }
/*      */ 
/*      */   public void setClassLoader(ClassLoader cl)
/*      */   {
/*  714 */     this.classLoader = cl;
/*      */   }
/*      */ 
/*      */   public String getTargetService()
/*      */   {
/*  723 */     return this.targetService;
/*      */   }
/*      */ 
/*      */   public AxisEngine getAxisEngine()
/*      */   {
/*  734 */     return this.axisEngine;
/*      */   }
/*      */ 
/*      */   public void setTargetService(String tServ)
/*      */     throws AxisFault
/*      */   {
/*  749 */     log.debug("MessageContext: setTargetService(" + tServ + ")");
/*      */ 
/*  751 */     if (tServ == null)
/*  752 */       setService(null);
/*      */     else {
/*      */       try
/*      */       {
/*  756 */         setService(getAxisEngine().getService(tServ));
/*      */       }
/*      */       catch (AxisFault fault) {
/*  759 */         if (!isClient()) {
/*  760 */           throw fault;
/*      */         }
/*      */       }
/*      */     }
/*  764 */     this.targetService = tServ;
/*      */   }
/*      */ 
/*      */   public SOAPService getService()
/*      */   {
/*  780 */     return this.serviceHandler;
/*      */   }
/*      */ 
/*      */   public void setService(SOAPService sh)
/*      */     throws AxisFault
/*      */   {
/*  793 */     log.debug("MessageContext: setServiceHandler(" + sh + ")");
/*  794 */     this.serviceHandler = sh;
/*  795 */     if (sh != null) {
/*  796 */       if (!sh.isRunning()) {
/*  797 */         throw new AxisFault(Messages.getMessage("disabled00"));
/*      */       }
/*  799 */       this.targetService = sh.getName();
/*  800 */       SOAPService service = sh;
/*  801 */       TypeMappingRegistry tmr = service.getTypeMappingRegistry();
/*  802 */       setTypeMappingRegistry(tmr);
/*      */ 
/*  805 */       setEncodingStyle(service.getUse().getEncoding());
/*      */ 
/*  809 */       this.bag.setParent(sh.getOptions());
/*      */ 
/*  814 */       this.highFidelity = service.needsHighFidelityRecording();
/*      */ 
/*  816 */       service.getInitializedServiceDesc(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isClient()
/*      */   {
/*  827 */     return this.axisEngine instanceof AxisClient;
/*      */   }
/*      */ 
/*      */   public String getStrProp(String propName)
/*      */   {
/*  909 */     return (String)getProperty(propName);
/*      */   }
/*      */ 
/*      */   public boolean isPropertyTrue(String propName)
/*      */   {
/*  921 */     return isPropertyTrue(propName, false);
/*      */   }
/*      */ 
/*      */   public boolean isPropertyTrue(String propName, boolean defaultVal)
/*      */   {
/*  944 */     return JavaUtils.isTrue(getProperty(propName), defaultVal);
/*      */   }
/*      */ 
/*      */   public void setProperty(String name, Object value)
/*      */   {
/*  961 */     if ((name == null) || (value == null)) {
/*  962 */       return;
/*      */     }
/*      */ 
/*  966 */     if (name.equals("javax.xml.rpc.security.auth.username")) {
/*  967 */       if (!(value instanceof String)) {
/*  968 */         throw new IllegalArgumentException(Messages.getMessage("badProp00", new String[] { name, "java.lang.String", value.getClass().getName() }));
/*      */       }
/*      */ 
/*  972 */       setUsername((String)value);
/*      */     }
/*  974 */     else if (name.equals("javax.xml.rpc.security.auth.password")) {
/*  975 */       if (!(value instanceof String)) {
/*  976 */         throw new IllegalArgumentException(Messages.getMessage("badProp00", new String[] { name, "java.lang.String", value.getClass().getName() }));
/*      */       }
/*      */ 
/*  980 */       setPassword((String)value);
/*      */     }
/*  982 */     else if (name.equals("javax.xml.rpc.session.maintain")) {
/*  983 */       if (!(value instanceof Boolean)) {
/*  984 */         throw new IllegalArgumentException(Messages.getMessage("badProp00", new String[] { name, "java.lang.Boolean", value.getClass().getName() }));
/*      */       }
/*      */ 
/*  990 */       setMaintainSession(((Boolean)value).booleanValue());
/*      */     }
/*  992 */     else if (name.equals("javax.xml.rpc.soap.http.soapaction.use")) {
/*  993 */       if (!(value instanceof Boolean)) {
/*  994 */         throw new IllegalArgumentException(Messages.getMessage("badProp00", new String[] { name, "java.lang.Boolean", value.getClass().getName() }));
/*      */       }
/*      */ 
/* 1000 */       setUseSOAPAction(((Boolean)value).booleanValue());
/*      */     }
/* 1002 */     else if (name.equals("javax.xml.rpc.soap.http.soapaction.uri")) {
/* 1003 */       if (!(value instanceof String)) {
/* 1004 */         throw new IllegalArgumentException(Messages.getMessage("badProp00", new String[] { name, "java.lang.String", value.getClass().getName() }));
/*      */       }
/*      */ 
/* 1010 */       setSOAPActionURI((String)value);
/*      */     }
/* 1012 */     else if (name.equals("javax.xml.rpc.encodingstyle.namespace.uri")) {
/* 1013 */       if (!(value instanceof String)) {
/* 1014 */         throw new IllegalArgumentException(Messages.getMessage("badProp00", new String[] { name, "java.lang.String", value.getClass().getName() }));
/*      */       }
/*      */ 
/* 1020 */       setEncodingStyle((String)value);
/*      */     }
/*      */     else {
/* 1023 */       this.bag.put(name, value);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean containsProperty(String name)
/*      */   {
/* 1034 */     Object propertyValue = getProperty(name);
/* 1035 */     return propertyValue != null;
/*      */   }
/*      */ 
/*      */   public Iterator getPropertyNames()
/*      */   {
/* 1048 */     return this.bag.keySet().iterator();
/*      */   }
/*      */ 
/*      */   public Iterator getAllPropertyNames()
/*      */   {
/* 1057 */     return this.bag.getAllKeys().iterator();
/*      */   }
/*      */ 
/*      */   public Object getProperty(String name)
/*      */   {
/* 1068 */     if (name != null) {
/* 1069 */       if (name.equals("javax.xml.rpc.security.auth.username")) {
/* 1070 */         return getUsername();
/*      */       }
/* 1072 */       if (name.equals("javax.xml.rpc.security.auth.password")) {
/* 1073 */         return getPassword();
/*      */       }
/* 1075 */       if (name.equals("javax.xml.rpc.session.maintain")) {
/* 1076 */         return getMaintainSession() ? Boolean.TRUE : Boolean.FALSE;
/*      */       }
/* 1078 */       if (name.equals("javax.xml.rpc.soap.operation.style")) {
/* 1079 */         return getOperationStyle() == null ? null : getOperationStyle().getName();
/*      */       }
/* 1081 */       if (name.equals("javax.xml.rpc.soap.http.soapaction.use")) {
/* 1082 */         return useSOAPAction() ? Boolean.TRUE : Boolean.FALSE;
/*      */       }
/* 1084 */       if (name.equals("javax.xml.rpc.soap.http.soapaction.uri")) {
/* 1085 */         return getSOAPActionURI();
/*      */       }
/* 1087 */       if (name.equals("javax.xml.rpc.encodingstyle.namespace.uri")) {
/* 1088 */         return getEncodingStyle();
/*      */       }
/* 1090 */       if (this.bag == null) {
/* 1091 */         return null;
/*      */       }
/*      */ 
/* 1094 */       return this.bag.get(name);
/*      */     }
/*      */ 
/* 1098 */     return null;
/*      */   }
/*      */ 
/*      */   public void setPropertyParent(Hashtable parent)
/*      */   {
/* 1113 */     this.bag.setParent(parent);
/*      */   }
/*      */ 
/*      */   public void setUsername(String username)
/*      */   {
/* 1122 */     this.username = username;
/*      */   }
/*      */ 
/*      */   public String getUsername()
/*      */   {
/* 1131 */     return this.username;
/*      */   }
/*      */ 
/*      */   public void setPassword(String password)
/*      */   {
/* 1140 */     this.password = password;
/*      */   }
/*      */ 
/*      */   public String getPassword()
/*      */   {
/* 1149 */     return this.password;
/*      */   }
/*      */ 
/*      */   public Style getOperationStyle()
/*      */   {
/* 1160 */     if (this.currentOperation != null) {
/* 1161 */       return this.currentOperation.getStyle();
/*      */     }
/*      */ 
/* 1164 */     if (this.serviceHandler != null) {
/* 1165 */       return this.serviceHandler.getStyle();
/*      */     }
/*      */ 
/* 1168 */     return Style.RPC;
/*      */   }
/*      */ 
/*      */   public Use getOperationUse()
/*      */   {
/* 1177 */     if (this.currentOperation != null) {
/* 1178 */       return this.currentOperation.getUse();
/*      */     }
/*      */ 
/* 1181 */     if (this.serviceHandler != null) {
/* 1182 */       return this.serviceHandler.getUse();
/*      */     }
/*      */ 
/* 1185 */     return Use.ENCODED;
/*      */   }
/*      */ 
/*      */   public void setUseSOAPAction(boolean useSOAPAction)
/*      */   {
/* 1198 */     this.useSOAPAction = useSOAPAction;
/*      */   }
/*      */ 
/*      */   public boolean useSOAPAction()
/*      */   {
/* 1209 */     return this.useSOAPAction;
/*      */   }
/*      */ 
/*      */   public void setSOAPActionURI(String SOAPActionURI)
/*      */     throws IllegalArgumentException
/*      */   {
/* 1225 */     this.SOAPActionURI = SOAPActionURI;
/*      */   }
/*      */ 
/*      */   public String getSOAPActionURI()
/*      */   {
/* 1234 */     return this.SOAPActionURI;
/*      */   }
/*      */ 
/*      */   public void setEncodingStyle(String namespaceURI)
/*      */   {
/* 1243 */     if (namespaceURI == null) {
/* 1244 */       namespaceURI = "";
/*      */     }
/* 1246 */     else if (Constants.isSOAP_ENC(namespaceURI)) {
/* 1247 */       namespaceURI = this.soapConstants.getEncodingURI();
/*      */     }
/*      */ 
/* 1250 */     this.encodingStyle = namespaceURI;
/*      */   }
/*      */ 
/*      */   public String getEncodingStyle()
/*      */   {
/* 1260 */     return this.encodingStyle;
/*      */   }
/*      */ 
/*      */   public void removeProperty(String propName)
/*      */   {
/* 1265 */     if (this.bag != null)
/* 1266 */       this.bag.remove(propName);
/*      */   }
/*      */ 
/*      */   public void reset()
/*      */   {
/* 1275 */     if (this.bag != null) {
/* 1276 */       this.bag.clear();
/*      */     }
/* 1278 */     this.serviceHandler = null;
/* 1279 */     this.havePassedPivot = false;
/* 1280 */     this.currentOperation = null;
/*      */   }
/*      */ 
/*      */   public boolean isHighFidelity()
/*      */   {
/* 1293 */     return this.highFidelity;
/*      */   }
/*      */ 
/*      */   public void setHighFidelity(boolean highFidelity)
/*      */   {
/* 1304 */     this.highFidelity = highFidelity;
/*      */   }
/*      */ 
/*      */   public String[] getRoles()
/*      */   {
/* 1328 */     return this.roles;
/*      */   }
/*      */ 
/*      */   public void setRoles(String[] roles)
/*      */   {
/* 1340 */     this.roles = roles;
/*      */   }
/*      */ 
/*      */   public synchronized void dispose()
/*      */   {
/* 1349 */     log.debug("disposing of message context");
/* 1350 */     if (this.requestMessage != null) {
/* 1351 */       this.requestMessage.dispose();
/* 1352 */       this.requestMessage = null;
/*      */     }
/* 1354 */     if (this.responseMessage != null) {
/* 1355 */       this.responseMessage.dispose();
/* 1356 */       this.responseMessage = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*      */     try
/*      */     {
/*  310 */       systemTempDir = AxisProperties.getProperty("axis.attachments.Directory");
/*      */     } catch (Throwable t) {
/*  312 */       systemTempDir = null;
/*      */     }
/*      */ 
/*  315 */     if (systemTempDir == null)
/*      */     {
/*      */       try
/*      */       {
/*  319 */         File tf = File.createTempFile("Axis", ".tmp");
/*  320 */         File dir = tf.getParentFile();
/*  321 */         if (tf.exists()) {
/*  322 */           tf.delete();
/*      */         }
/*  324 */         if (dir != null)
/*  325 */           systemTempDir = dir.getCanonicalPath();
/*      */       }
/*      */       catch (Throwable t) {
/*  328 */         log.debug("Unable to find a temp dir with write access");
/*  329 */         systemTempDir = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.MessageContext
 * JD-Core Version:    0.6.0
 */