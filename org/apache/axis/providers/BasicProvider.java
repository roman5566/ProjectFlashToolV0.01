/*     */ package org.apache.axis.providers;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Set;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.description.JavaServiceDesc;
/*     */ import org.apache.axis.description.ServiceDesc;
/*     */ import org.apache.axis.handlers.BasicHandler;
/*     */ import org.apache.axis.handlers.soap.SOAPService;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.fromJava.Emitter;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Document;
/*     */ 
/*     */ public abstract class BasicProvider extends BasicHandler
/*     */ {
/*     */   public static final String OPTION_WSDL_PORTTYPE = "wsdlPortType";
/*     */   public static final String OPTION_WSDL_SERVICEELEMENT = "wsdlServiceElement";
/*     */   public static final String OPTION_WSDL_SERVICEPORT = "wsdlServicePort";
/*     */   public static final String OPTION_WSDL_TARGETNAMESPACE = "wsdlTargetNamespace";
/*     */   public static final String OPTION_WSDL_INPUTSCHEMA = "wsdlInputSchema";
/*     */   public static final String OPTION_WSDL_SOAPACTION_MODE = "wsdlSoapActionMode";
/*     */   public static final String OPTION_EXTRACLASSES = "extraClasses";
/*  53 */   protected static Log log = LogFactory.getLog(BasicProvider.class.getName());
/*     */ 
/*  59 */   protected static Log entLog = LogFactory.getLog("org.apache.axis.enterprise");
/*     */ 
/*     */   public abstract void initServiceDesc(SOAPService paramSOAPService, MessageContext paramMessageContext)
/*     */     throws AxisFault;
/*     */ 
/*     */   public void addOperation(String name, QName qname)
/*     */   {
/*  71 */     Hashtable operations = (Hashtable)getOption("Operations");
/*  72 */     if (operations == null) {
/*  73 */       operations = new Hashtable();
/*  74 */       setOption("Operations", operations);
/*     */     }
/*  76 */     operations.put(qname, name);
/*     */   }
/*     */ 
/*     */   public String getOperationName(QName qname) {
/*  80 */     Hashtable operations = (Hashtable)getOption("Operations");
/*  81 */     if (operations == null) return null;
/*  82 */     return (String)operations.get(qname);
/*     */   }
/*     */ 
/*     */   public QName[] getOperationQNames() {
/*  86 */     Hashtable operations = (Hashtable)getOption("Operations");
/*  87 */     if (operations == null) return null;
/*  88 */     Object[] keys = operations.keySet().toArray();
/*  89 */     QName[] qnames = new QName[keys.length];
/*  90 */     System.arraycopy(keys, 0, qnames, 0, keys.length);
/*  91 */     return qnames;
/*     */   }
/*     */ 
/*     */   public String[] getOperationNames() {
/*  95 */     Hashtable operations = (Hashtable)getOption("Operations");
/*  96 */     if (operations == null) return null;
/*  97 */     Object[] values = operations.values().toArray();
/*  98 */     String[] names = new String[values.length];
/*  99 */     System.arraycopy(values, 0, names, 0, values.length);
/* 100 */     return names;
/*     */   }
/*     */ 
/*     */   public void generateWSDL(MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/* 110 */     if (log.isDebugEnabled()) {
/* 111 */       log.debug("Enter: BasicProvider::generateWSDL (" + this + ")");
/*     */     }
/*     */ 
/* 115 */     SOAPService service = msgContext.getService();
/*     */ 
/* 117 */     ServiceDesc serviceDesc = service.getInitializedServiceDesc(msgContext);
/*     */     try
/*     */     {
/* 131 */       String locationUrl = msgContext.getStrProp("axis.wsdlgen.serv.loc.url");
/*     */ 
/* 133 */       if (locationUrl == null)
/*     */       {
/* 135 */         locationUrl = serviceDesc.getEndpointURL();
/*     */       }
/*     */ 
/* 138 */       if (locationUrl == null)
/*     */       {
/* 140 */         locationUrl = msgContext.getStrProp("transport.url");
/*     */       }
/*     */ 
/* 144 */       String interfaceNamespace = msgContext.getStrProp("axis.wsdlgen.intfnamespace");
/*     */ 
/* 146 */       if (interfaceNamespace == null)
/*     */       {
/* 148 */         interfaceNamespace = serviceDesc.getDefaultNamespace();
/*     */       }
/*     */ 
/* 151 */       if (interfaceNamespace == null)
/*     */       {
/* 153 */         interfaceNamespace = locationUrl;
/*     */       }
/*     */ 
/* 176 */       Emitter emitter = new Emitter();
/*     */ 
/* 186 */       String alias = (String)service.getOption("alias");
/* 187 */       if (alias != null) {
/* 188 */         emitter.setServiceElementName(alias);
/*     */       }
/*     */ 
/* 191 */       emitter.setStyle(serviceDesc.getStyle());
/* 192 */       emitter.setUse(serviceDesc.getUse());
/*     */ 
/* 194 */       if ((serviceDesc instanceof JavaServiceDesc)) {
/* 195 */         emitter.setClsSmart(((JavaServiceDesc)serviceDesc).getImplClass(), locationUrl);
/*     */       }
/*     */ 
/* 201 */       String targetNamespace = (String)service.getOption("wsdlTargetNamespace");
/* 202 */       if ((targetNamespace == null) || (targetNamespace.length() == 0)) {
/* 203 */         targetNamespace = interfaceNamespace;
/*     */       }
/* 205 */       emitter.setIntfNamespace(targetNamespace);
/*     */ 
/* 207 */       emitter.setLocationUrl(locationUrl);
/* 208 */       emitter.setServiceDesc(serviceDesc);
/* 209 */       emitter.setTypeMappingRegistry(msgContext.getTypeMappingRegistry());
/*     */ 
/* 211 */       String wsdlPortType = (String)service.getOption("wsdlPortType");
/* 212 */       String wsdlServiceElement = (String)service.getOption("wsdlServiceElement");
/* 213 */       String wsdlServicePort = (String)service.getOption("wsdlServicePort");
/* 214 */       String wsdlInputSchema = (String)service.getOption("wsdlInputSchema");
/* 215 */       String wsdlSoapActinMode = (String)service.getOption("wsdlSoapActionMode");
/* 216 */       String extraClasses = (String)service.getOption("extraClasses");
/*     */ 
/* 218 */       if ((wsdlPortType != null) && (wsdlPortType.length() > 0)) {
/* 219 */         emitter.setPortTypeName(wsdlPortType);
/*     */       }
/* 221 */       if ((wsdlServiceElement != null) && (wsdlServiceElement.length() > 0)) {
/* 222 */         emitter.setServiceElementName(wsdlServiceElement);
/*     */       }
/* 224 */       if ((wsdlServicePort != null) && (wsdlServicePort.length() > 0)) {
/* 225 */         emitter.setServicePortName(wsdlServicePort);
/*     */       }
/* 227 */       if ((wsdlInputSchema != null) && (wsdlInputSchema.length() > 0)) {
/* 228 */         emitter.setInputSchema(wsdlInputSchema);
/*     */       }
/* 230 */       if ((wsdlSoapActinMode != null) && (wsdlSoapActinMode.length() > 0)) {
/* 231 */         emitter.setSoapAction(wsdlSoapActinMode);
/*     */       }
/*     */ 
/* 234 */       if ((extraClasses != null) && (extraClasses.length() > 0)) {
/* 235 */         emitter.setExtraClasses(extraClasses);
/*     */       }
/*     */ 
/* 238 */       if (msgContext.isPropertyTrue("emitAllTypesInWSDL")) {
/* 239 */         emitter.setEmitAllTypes(true);
/*     */       }
/*     */ 
/* 242 */       Document doc = emitter.emit(0);
/*     */ 
/* 244 */       msgContext.setProperty("WSDL", doc);
/*     */     } catch (NoClassDefFoundError e) {
/* 246 */       entLog.info(Messages.getMessage("toAxisFault00"), e);
/* 247 */       throw new AxisFault(e.toString(), e);
/*     */     } catch (Exception e) {
/* 249 */       entLog.info(Messages.getMessage("toAxisFault00"), e);
/* 250 */       throw AxisFault.makeFault(e);
/*     */     }
/*     */ 
/* 253 */     if (log.isDebugEnabled())
/* 254 */       log.debug("Exit: BasicProvider::generateWSDL (" + this + ")");
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.providers.BasicProvider
 * JD-Core Version:    0.6.0
 */