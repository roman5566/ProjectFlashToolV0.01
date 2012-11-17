/*     */ package org.apache.axis.message;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.soap.SOAPElement;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.Message;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.constants.Style;
/*     */ import org.apache.axis.constants.Use;
/*     */ import org.apache.axis.description.OperationDesc;
/*     */ import org.apache.axis.description.ParameterDesc;
/*     */ import org.apache.axis.description.ServiceDesc;
/*     */ import org.apache.axis.encoding.DeserializationContext;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.handlers.soap.SOAPService;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.toJava.Utils;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class RPCElement extends SOAPBodyElement
/*     */ {
/*  50 */   protected boolean needDeser = false;
/*  51 */   OperationDesc[] operations = null;
/*     */ 
/*     */   public RPCElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context, OperationDesc[] operations)
/*     */     throws AxisFault
/*     */   {
/*  60 */     super(namespace, localName, prefix, attributes, context);
/*     */ 
/*  63 */     this.needDeser = true;
/*     */ 
/*  66 */     if (operations == null)
/*  67 */       updateOperationsByName();
/*     */     else
/*  69 */       this.operations = operations;
/*     */   }
/*     */ 
/*     */   public RPCElement(String namespace, String methodName, Object[] args)
/*     */   {
/*  75 */     setNamespaceURI(namespace);
/*  76 */     this.name = methodName;
/*     */ 
/*  78 */     for (int i = 0; (args != null) && (i < args.length); i++)
/*  79 */       if ((args[i] instanceof RPCParam)) {
/*  80 */         addParam((RPCParam)args[i]);
/*     */       } else {
/*  82 */         String name = null;
/*  83 */         if (name == null) name = "arg" + i;
/*  84 */         addParam(new RPCParam(namespace, name, args[i]));
/*     */       }
/*     */   }
/*     */ 
/*     */   public RPCElement(String methodName)
/*     */   {
/*  91 */     this.name = methodName;
/*     */   }
/*     */ 
/*     */   public void updateOperationsByName() throws AxisFault
/*     */   {
/*  96 */     if (this.context == null) {
/*  97 */       return;
/*     */     }
/*     */ 
/* 100 */     MessageContext msgContext = this.context.getMessageContext();
/*     */ 
/* 102 */     if (msgContext == null) {
/* 103 */       return;
/*     */     }
/*     */ 
/* 107 */     SOAPService service = msgContext.getService();
/* 108 */     if (service == null) {
/* 109 */       return;
/*     */     }
/*     */ 
/* 112 */     ServiceDesc serviceDesc = service.getInitializedServiceDesc(msgContext);
/*     */ 
/* 115 */     String lc = Utils.xmlNameToJava(this.name);
/* 116 */     if (serviceDesc == null) {
/* 117 */       throw AxisFault.makeFault(new ClassNotFoundException(Messages.getMessage("noClassForService00", lc)));
/*     */     }
/*     */ 
/* 123 */     this.operations = serviceDesc.getOperationsByName(lc);
/*     */   }
/*     */ 
/*     */   public void updateOperationsByQName() throws AxisFault
/*     */   {
/* 128 */     if (this.context == null) {
/* 129 */       return;
/*     */     }
/*     */ 
/* 132 */     MessageContext msgContext = this.context.getMessageContext();
/*     */ 
/* 134 */     if (msgContext == null) {
/* 135 */       return;
/*     */     }
/*     */ 
/* 138 */     this.operations = msgContext.getPossibleOperationsByQName(getQName());
/*     */   }
/*     */ 
/*     */   public OperationDesc[] getOperations()
/*     */   {
/* 143 */     return this.operations;
/*     */   }
/*     */ 
/*     */   public String getMethodName()
/*     */   {
/* 148 */     return this.name;
/*     */   }
/*     */ 
/*     */   public void setNeedDeser(boolean needDeser) {
/* 152 */     this.needDeser = needDeser;
/*     */   }
/*     */ 
/*     */   public void deserialize() throws SAXException
/*     */   {
/* 157 */     this.needDeser = false;
/*     */ 
/* 159 */     MessageContext msgContext = this.context.getMessageContext();
/*     */ 
/* 163 */     Message msg = msgContext.getCurrentMessage();
/* 164 */     SOAPConstants soapConstants = msgContext.getSOAPConstants();
/*     */ 
/* 166 */     boolean isResponse = (msg != null) && ("response".equals(msg.getMessageType()));
/*     */ 
/* 170 */     RPCHandler rpcHandler = new RPCHandler(this, isResponse);
/*     */ 
/* 172 */     if (this.operations != null) {
/* 173 */       int numParams = getChildren() == null ? 0 : getChildren().size();
/*     */ 
/* 175 */       SAXException savedException = null;
/*     */ 
/* 179 */       boolean acceptMissingParams = msgContext.isPropertyTrue("acceptMissingParams", true);
/*     */ 
/* 188 */       for (int i = 0; i < this.operations.length; i++) {
/* 189 */         OperationDesc operation = this.operations[i];
/*     */ 
/* 192 */         boolean needHeaderProcessing = needHeaderProcessing(operation, isResponse);
/*     */ 
/* 211 */         if ((operation.getStyle() != Style.DOCUMENT) && (operation.getStyle() != Style.WRAPPED) && (operation.getUse() != Use.LITERAL) && (acceptMissingParams ? operation.getNumInParams() < numParams : operation.getNumInParams() != numParams))
/*     */         {
/*     */           continue;
/*     */         }
/*     */ 
/* 218 */         boolean isEncoded = operation.getUse() == Use.ENCODED;
/* 219 */         rpcHandler.setOperation(operation);
/*     */         try
/*     */         {
/* 224 */           if (((msgContext.isClient()) && (operation.getStyle() == Style.DOCUMENT)) || ((!msgContext.isClient()) && (operation.getStyle() == Style.DOCUMENT) && (operation.getNumInParams() > 0)))
/*     */           {
/* 228 */             this.context.pushElementHandler(rpcHandler);
/* 229 */             this.context.setCurElement(null);
/*     */           } else {
/* 231 */             this.context.pushElementHandler(new EnvelopeHandler(rpcHandler));
/*     */ 
/* 233 */             this.context.setCurElement(this);
/*     */           }
/*     */ 
/* 236 */           publishToHandler(this.context);
/*     */ 
/* 241 */           if (needHeaderProcessing) {
/* 242 */             processHeaders(operation, isResponse, this.context, rpcHandler);
/*     */           }
/*     */ 
/* 248 */           boolean match = true;
/* 249 */           List params = getParams2();
/* 250 */           for (int j = 0; (j < params.size()) && (match); j++) {
/* 251 */             RPCParam rpcParam = (RPCParam)params.get(j);
/* 252 */             Object value = rpcParam.getObjectValue();
/*     */ 
/* 255 */             ParameterDesc paramDesc = rpcParam.getParamDesc();
/*     */ 
/* 260 */             if ((paramDesc == null) || (paramDesc.getJavaType() == null)) {
/*     */               continue;
/*     */             }
/* 263 */             Class sigType = paramDesc.getJavaType();
/*     */ 
/* 269 */             if ((sigType.isArray()) && 
/* 270 */               (value != null) && (JavaUtils.isConvertable(value, sigType.getComponentType())) && (!value.getClass().isArray()) && (!(value instanceof Collection)))
/*     */             {
/* 275 */               ArrayList list = new ArrayList();
/* 276 */               list.add(value);
/* 277 */               value = list;
/* 278 */               rpcParam.setObjectValue(value);
/*     */             }
/*     */ 
/* 282 */             if (!JavaUtils.isConvertable(value, sigType, isEncoded)) {
/* 283 */               match = false;
/*     */             }
/*     */           }
/*     */ 
/* 287 */           if (!match) {
/* 288 */             this.children = new ArrayList();
/*     */           }
/*     */           else
/*     */           {
/* 293 */             msgContext.setOperation(operation);
/* 294 */             return;
/*     */           }
/*     */         } catch (SAXException e) {
/* 297 */           savedException = e;
/* 298 */           this.children = new ArrayList();
/*     */         }
/*     */         catch (AxisFault e)
/*     */         {
/* 303 */           savedException = new SAXException(e);
/* 304 */           this.children = new ArrayList();
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 311 */       if ((!msgContext.isClient()) && (soapConstants == SOAPConstants.SOAP12_CONSTANTS)) {
/* 312 */         AxisFault fault = new AxisFault(Constants.FAULT_SOAP12_SENDER, "string", null, null);
/* 313 */         fault.addFaultSubCode(Constants.FAULT_SUBCODE_BADARGS);
/* 314 */         throw new SAXException(fault);
/*     */       }
/*     */ 
/* 317 */       if (savedException != null)
/* 318 */         throw savedException;
/* 319 */       if (!msgContext.isClient()) {
/* 320 */         QName faultCode = new QName("Server.userException");
/* 321 */         if (soapConstants == SOAPConstants.SOAP12_CONSTANTS)
/* 322 */           faultCode = Constants.FAULT_SOAP12_SENDER;
/* 323 */         AxisFault fault = new AxisFault(faultCode, null, Messages.getMessage("noSuchOperation", this.name), null, null, null);
/*     */ 
/* 326 */         throw new SAXException(fault);
/*     */       }
/*     */     }
/*     */ 
/* 330 */     if (this.operations != null) {
/* 331 */       rpcHandler.setOperation(this.operations[0]);
/*     */     }
/*     */ 
/* 336 */     if ((this.operations != null) && (this.operations.length > 0) && (this.operations[0].getStyle() == Style.DOCUMENT))
/*     */     {
/* 338 */       this.context.pushElementHandler(rpcHandler);
/* 339 */       this.context.setCurElement(null);
/*     */     } else {
/* 341 */       this.context.pushElementHandler(new EnvelopeHandler(rpcHandler));
/* 342 */       this.context.setCurElement(this);
/*     */     }
/*     */ 
/* 345 */     publishToHandler(this.context);
/*     */   }
/*     */ 
/*     */   private List getParams2() {
/* 349 */     return getParams(new ArrayList());
/*     */   }
/*     */ 
/*     */   private List getParams(List list) {
/* 353 */     for (int i = 0; (this.children != null) && (i < this.children.size()); i++) {
/* 354 */       Object child = this.children.get(i);
/* 355 */       if ((child instanceof RPCParam)) {
/* 356 */         list.add(child);
/*     */       }
/*     */     }
/* 359 */     return list;
/*     */   }
/*     */ 
/*     */   public RPCParam getParam(String name)
/*     */     throws SAXException
/*     */   {
/* 367 */     if (this.needDeser) {
/* 368 */       deserialize();
/*     */     }
/*     */ 
/* 371 */     List params = getParams2();
/* 372 */     for (int i = 0; i < params.size(); i++) {
/* 373 */       RPCParam param = (RPCParam)params.get(i);
/* 374 */       if (param.getName().equals(name)) {
/* 375 */         return param;
/*     */       }
/*     */     }
/* 378 */     return null;
/*     */   }
/*     */ 
/*     */   public Vector getParams() throws SAXException
/*     */   {
/* 383 */     if (this.needDeser) {
/* 384 */       deserialize();
/*     */     }
/*     */ 
/* 387 */     return (Vector)getParams(new Vector());
/*     */   }
/*     */ 
/*     */   public void addParam(RPCParam param)
/*     */   {
/* 392 */     param.setRPCCall(this);
/* 393 */     initializeChildren();
/* 394 */     this.children.add(param);
/*     */   }
/*     */ 
/*     */   protected void outputImpl(SerializationContext context) throws Exception
/*     */   {
/* 399 */     MessageContext msgContext = context.getMessageContext();
/* 400 */     boolean hasOperationElement = (msgContext == null) || (msgContext.getOperationStyle() == Style.RPC) || (msgContext.getOperationStyle() == Style.WRAPPED);
/*     */ 
/* 409 */     boolean noParams = getParams2().size() == 0;
/*     */ 
/* 411 */     if ((hasOperationElement) || (noParams))
/*     */     {
/* 414 */       if ((this.encodingStyle != null) && (this.encodingStyle.equals(""))) {
/* 415 */         context.registerPrefixForURI("", getNamespaceURI());
/*     */       }
/* 417 */       context.startElement(new QName(getNamespaceURI(), this.name), this.attributes);
/*     */     }
/*     */     Iterator it;
/* 420 */     if (noParams) {
/* 421 */       if (this.children != null)
/* 422 */         for (it = this.children.iterator(); it.hasNext(); )
/* 423 */           ((NodeImpl)it.next()).output(context);
/*     */     }
/*     */     else
/*     */     {
/* 427 */       List params = getParams2();
/* 428 */       for (int i = 0; i < params.size(); i++) {
/* 429 */         RPCParam param = (RPCParam)params.get(i);
/* 430 */         if ((!hasOperationElement) && (this.encodingStyle != null) && (this.encodingStyle.equals(""))) {
/* 431 */           context.registerPrefixForURI("", param.getQName().getNamespaceURI());
/*     */         }
/* 433 */         param.serialize(context);
/*     */       }
/*     */     }
/*     */ 
/* 437 */     if ((hasOperationElement) || (noParams))
/* 438 */       context.endElement();
/*     */   }
/*     */ 
/*     */   private boolean needHeaderProcessing(OperationDesc operation, boolean isResponse)
/*     */   {
/* 454 */     ArrayList paramDescs = operation.getParameters();
/* 455 */     if (paramDescs != null) {
/* 456 */       for (int j = 0; j < paramDescs.size(); j++) {
/* 457 */         ParameterDesc paramDesc = (ParameterDesc)paramDescs.get(j);
/*     */ 
/* 459 */         if (((!isResponse) && (paramDesc.isInHeader())) || ((isResponse) && (paramDesc.isOutHeader())))
/*     */         {
/* 461 */           return true;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 468 */     return (isResponse) && (operation.getReturnParamDesc() != null) && (operation.getReturnParamDesc().isOutHeader());
/*     */   }
/*     */ 
/*     */   private void processHeaders(OperationDesc operation, boolean isResponse, DeserializationContext context, RPCHandler handler)
/*     */     throws AxisFault, SAXException
/*     */   {
/*     */     try
/*     */     {
/* 490 */       handler.setHeaderElement(true);
/*     */ 
/* 492 */       SOAPElement envelope = getParentElement();
/* 493 */       while ((envelope != null) && (!(envelope instanceof SOAPEnvelope)))
/*     */       {
/* 495 */         envelope = envelope.getParentElement();
/*     */       }
/* 497 */       if (envelope == null)
/*     */       {
/*     */         return;
/*     */       }
/* 502 */       ArrayList paramDescs = operation.getParameters();
/* 503 */       if (paramDescs != null) {
/* 504 */         for (int j = 0; j < paramDescs.size(); j++) {
/* 505 */           ParameterDesc paramDesc = (ParameterDesc)paramDescs.get(j);
/*     */ 
/* 507 */           if (((isResponse) || (!paramDesc.isInHeader())) && ((!isResponse) || (!paramDesc.isOutHeader())))
/*     */           {
/*     */             continue;
/*     */           }
/* 511 */           Enumeration headers = ((SOAPEnvelope)envelope).getHeadersByName(paramDesc.getQName().getNamespaceURI(), paramDesc.getQName().getLocalPart(), true);
/*     */ 
/* 521 */           while ((headers != null) && (headers.hasMoreElements()))
/*     */           {
/* 523 */             context.pushElementHandler(handler);
/* 524 */             context.setCurElement(null);
/* 525 */             ((MessageElement)headers.nextElement()).publishToHandler(context);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 534 */       if ((isResponse) && (operation.getReturnParamDesc() != null) && (operation.getReturnParamDesc().isOutHeader()))
/*     */       {
/* 537 */         ParameterDesc paramDesc = operation.getReturnParamDesc();
/* 538 */         Enumeration headers = ((SOAPEnvelope)envelope).getHeadersByName(paramDesc.getQName().getNamespaceURI(), paramDesc.getQName().getLocalPart(), true);
/*     */ 
/* 544 */         while ((headers != null) && (headers.hasMoreElements()))
/*     */         {
/* 546 */           context.pushElementHandler(handler);
/* 547 */           context.setCurElement(null);
/*     */ 
/* 549 */           ((MessageElement)headers.nextElement()).publishToHandler(context);
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 554 */       handler.setHeaderElement(false);
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.RPCElement
 * JD-Core Version:    0.6.0
 */