/*     */ package org.apache.axis.providers.java;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.Vector;
/*     */ import javax.wsdl.OperationType;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.rpc.holders.Holder;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.constants.Style;
/*     */ import org.apache.axis.description.OperationDesc;
/*     */ import org.apache.axis.description.ParameterDesc;
/*     */ import org.apache.axis.description.ServiceDesc;
/*     */ import org.apache.axis.handlers.soap.SOAPService;
/*     */ import org.apache.axis.message.RPCElement;
/*     */ import org.apache.axis.message.RPCHeaderParam;
/*     */ import org.apache.axis.message.RPCParam;
/*     */ import org.apache.axis.message.SOAPBodyElement;
/*     */ import org.apache.axis.message.SOAPEnvelope;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class RPCProvider extends JavaProvider
/*     */ {
/*  54 */   protected static Log log = LogFactory.getLog(RPCProvider.class.getName());
/*     */ 
/*     */   public void processMessage(MessageContext msgContext, SOAPEnvelope reqEnv, SOAPEnvelope resEnv, Object obj)
/*     */     throws Exception
/*     */   {
/*  71 */     if (log.isDebugEnabled()) {
/*  72 */       log.debug("Enter: RPCProvider.processMessage()");
/*     */     }
/*     */ 
/*  75 */     SOAPService service = msgContext.getService();
/*  76 */     ServiceDesc serviceDesc = service.getServiceDescription();
/*  77 */     RPCElement body = getBody(reqEnv, msgContext);
/*     */ 
/*  79 */     Vector args = null;
/*     */     try {
/*  81 */       args = body.getParams();
/*     */     } catch (SAXException e) {
/*  83 */       if (e.getException() != null)
/*  84 */         throw e.getException();
/*  85 */       throw e;
/*     */     }
/*  87 */     int numArgs = args.size();
/*  88 */     OperationDesc operation = getOperationDesc(msgContext, body);
/*     */ 
/*  92 */     Object[] argValues = new Object[operation.getNumParams()];
/*     */ 
/*  95 */     ArrayList outs = new ArrayList();
/*     */ 
/* 102 */     for (int i = 0; i < numArgs; i++) {
/* 103 */       RPCParam rpcParam = (RPCParam)args.get(i);
/* 104 */       Object value = rpcParam.getObjectValue();
/*     */ 
/* 107 */       ParameterDesc paramDesc = rpcParam.getParamDesc();
/*     */ 
/* 112 */       if ((paramDesc != null) && (paramDesc.getJavaType() != null))
/*     */       {
/* 115 */         Class sigType = paramDesc.getJavaType();
/*     */ 
/* 118 */         value = JavaUtils.convert(value, sigType);
/*     */ 
/* 120 */         rpcParam.setObjectValue(value);
/* 121 */         if (paramDesc.getMode() == 3) {
/* 122 */           outs.add(rpcParam);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 128 */       if ((paramDesc == null) || (paramDesc.getOrder() == -1))
/* 129 */         argValues[i] = value;
/*     */       else {
/* 131 */         argValues[paramDesc.getOrder()] = value;
/*     */       }
/*     */ 
/* 134 */       if (log.isDebugEnabled()) {
/* 135 */         log.debug("  " + Messages.getMessage("value00", new StringBuffer().append("").append(argValues[i]).toString()));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 142 */     String allowedMethods = (String)service.getOption("allowedMethods");
/* 143 */     checkMethodName(msgContext, allowedMethods, operation.getName());
/*     */ 
/* 146 */     int count = numArgs;
/* 147 */     for (int i = 0; i < argValues.length; i++)
/*     */     {
/* 150 */       ParameterDesc param = operation.getParameter(i);
/* 151 */       if (param.getMode() == 1) {
/*     */         continue;
/*     */       }
/* 154 */       Class holderClass = param.getJavaType();
/* 155 */       if ((holderClass != null) && (Holder.class.isAssignableFrom(holderClass)))
/*     */       {
/* 157 */         int index = count;
/*     */ 
/* 159 */         if (param.getOrder() != -1)
/* 160 */           index = param.getOrder();
/*     */         else {
/* 162 */           count++;
/*     */         }
/*     */ 
/* 165 */         if (argValues[index] != null) {
/*     */           continue;
/*     */         }
/* 168 */         argValues[index] = holderClass.newInstance();
/*     */ 
/* 172 */         RPCParam p = new RPCParam(param.getQName(), argValues[index]);
/*     */ 
/* 174 */         p.setParamDesc(param);
/* 175 */         outs.add(p);
/*     */       } else {
/* 177 */         throw new AxisFault(Messages.getMessage("badOutParameter00", "" + param.getQName(), operation.getName()));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 184 */     Object objRes = null;
/*     */     try {
/* 186 */       objRes = invokeMethod(msgContext, operation.getMethod(), obj, argValues);
/*     */     }
/*     */     catch (IllegalArgumentException e)
/*     */     {
/* 190 */       String methodSig = operation.getMethod().toString();
/* 191 */       String argClasses = "";
/* 192 */       for (int i = 0; i < argValues.length; i++) {
/* 193 */         if (argValues[i] == null)
/* 194 */           argClasses = argClasses + "null";
/*     */         else {
/* 196 */           argClasses = argClasses + argValues[i].getClass().getName();
/*     */         }
/* 198 */         if (i + 1 < argValues.length) {
/* 199 */           argClasses = argClasses + ",";
/*     */         }
/*     */       }
/* 202 */       log.info(Messages.getMessage("dispatchIAE00", new String[] { methodSig, argClasses }), e);
/*     */ 
/* 205 */       throw new AxisFault(Messages.getMessage("dispatchIAE00", new String[] { methodSig, argClasses }), e);
/*     */     }
/*     */ 
/* 212 */     if (OperationType.ONE_WAY.equals(operation.getMep())) {
/* 213 */       return;
/*     */     }
/* 215 */     RPCElement resBody = createResponseBody(body, msgContext, operation, serviceDesc, objRes, resEnv, outs);
/* 216 */     resEnv.addBodyElement(resBody);
/*     */   }
/*     */ 
/*     */   protected RPCElement getBody(SOAPEnvelope reqEnv, MessageContext msgContext) throws Exception {
/* 220 */     SOAPService service = msgContext.getService();
/* 221 */     ServiceDesc serviceDesc = service.getServiceDescription();
/* 222 */     OperationDesc operation = msgContext.getOperation();
/* 223 */     Vector bodies = reqEnv.getBodyElements();
/* 224 */     if (log.isDebugEnabled()) {
/* 225 */       log.debug(Messages.getMessage("bodyElems00", "" + bodies.size()));
/* 226 */       if (bodies.size() > 0) {
/* 227 */         log.debug(Messages.getMessage("bodyIs00", "" + bodies.get(0)));
/*     */       }
/*     */     }
/* 230 */     RPCElement body = null;
/* 231 */     for (int bNum = 0; (body == null) && (bNum < bodies.size()); bNum++)
/*     */     {
/* 237 */       if (!(bodies.get(bNum) instanceof RPCElement)) {
/* 238 */         SOAPBodyElement bodyEl = (SOAPBodyElement)bodies.get(bNum);
/*     */ 
/* 242 */         if ((bodyEl.isRoot()) && (operation != null) && (bodyEl.getID() == null)) {
/* 243 */           ParameterDesc param = operation.getParameter(bNum);
/*     */ 
/* 245 */           if (param != null) {
/* 246 */             Object val = bodyEl.getValueAsType(param.getTypeQName());
/* 247 */             body = new RPCElement("", operation.getName(), new Object[] { val });
/*     */           }
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 253 */         body = (RPCElement)bodies.get(bNum);
/*     */       }
/*     */     }
/*     */ 
/* 257 */     if (body == null)
/*     */     {
/* 259 */       if (!serviceDesc.getStyle().equals(Style.DOCUMENT)) {
/* 260 */         throw new Exception(Messages.getMessage("noBody00"));
/*     */       }
/*     */ 
/* 265 */       ArrayList ops = serviceDesc.getOperations();
/* 266 */       for (Iterator iterator = ops.iterator(); iterator.hasNext(); ) {
/* 267 */         OperationDesc desc = (OperationDesc)iterator.next();
/* 268 */         if (desc.getNumInParams() == 0)
/*     */         {
/* 270 */           msgContext.setOperation(desc);
/*     */ 
/* 272 */           body = new RPCElement(desc.getName());
/*     */ 
/* 274 */           break;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 279 */       if (body == null) {
/* 280 */         throw new Exception(Messages.getMessage("noBody00"));
/*     */       }
/*     */     }
/* 283 */     return body;
/*     */   }
/*     */ 
/*     */   protected OperationDesc getOperationDesc(MessageContext msgContext, RPCElement body) throws SAXException, AxisFault {
/* 287 */     SOAPService service = msgContext.getService();
/* 288 */     ServiceDesc serviceDesc = service.getServiceDescription();
/* 289 */     String methodName = body.getMethodName();
/*     */ 
/* 292 */     OperationDesc operation = msgContext.getOperation();
/* 293 */     if (operation == null) {
/* 294 */       QName qname = new QName(body.getNamespaceURI(), body.getName());
/*     */ 
/* 296 */       operation = serviceDesc.getOperationByElementQName(qname);
/*     */ 
/* 298 */       if (operation == null) {
/* 299 */         SOAPConstants soapConstants = msgContext == null ? SOAPConstants.SOAP11_CONSTANTS : msgContext.getSOAPConstants();
/*     */ 
/* 302 */         if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
/* 303 */           AxisFault fault = new AxisFault(Constants.FAULT_SOAP12_SENDER, Messages.getMessage("noSuchOperation", methodName), null, null);
/*     */ 
/* 309 */           fault.addFaultSubCode(Constants.FAULT_SUBCODE_PROC_NOT_PRESENT);
/* 310 */           throw new SAXException(fault);
/*     */         }
/* 312 */         throw new AxisFault("Client", Messages.getMessage("noSuchOperation", methodName), null, null);
/*     */       }
/*     */ 
/* 316 */       msgContext.setOperation(operation);
/*     */     }
/*     */ 
/* 319 */     return operation;
/*     */   }
/*     */ 
/*     */   protected RPCElement createResponseBody(RPCElement body, MessageContext msgContext, OperationDesc operation, ServiceDesc serviceDesc, Object objRes, SOAPEnvelope resEnv, ArrayList outs) throws Exception
/*     */   {
/* 324 */     String methodName = body.getMethodName();
/*     */ 
/* 326 */     RPCElement resBody = new RPCElement(methodName + "Response");
/* 327 */     resBody.setPrefix(body.getPrefix());
/* 328 */     resBody.setNamespaceURI(body.getNamespaceURI());
/* 329 */     resBody.setEncodingStyle(msgContext.getEncodingStyle());
/*     */     try
/*     */     {
/* 332 */       if (operation.getMethod().getReturnType() != Void.TYPE) {
/* 333 */         QName returnQName = operation.getReturnQName();
/* 334 */         if (returnQName == null) {
/* 335 */           String nsp = body.getNamespaceURI();
/* 336 */           if ((nsp == null) || (nsp.length() == 0)) {
/* 337 */             nsp = serviceDesc.getDefaultNamespace();
/*     */           }
/* 339 */           returnQName = new QName(msgContext.isEncoded() ? "" : nsp, methodName + "Return");
/*     */         }
/*     */ 
/* 344 */         RPCParam param = new RPCParam(returnQName, objRes);
/* 345 */         param.setParamDesc(operation.getReturnParamDesc());
/*     */ 
/* 347 */         if (!operation.isReturnHeader())
/*     */         {
/* 349 */           if ((msgContext.getSOAPConstants() == SOAPConstants.SOAP12_CONSTANTS) && (serviceDesc.getStyle().equals(Style.RPC)))
/*     */           {
/* 351 */             RPCParam resultParam = new RPCParam(Constants.QNAME_RPC_RESULT, returnQName);
/* 352 */             resultParam.setXSITypeGeneration(Boolean.FALSE);
/* 353 */             resBody.addParam(resultParam);
/*     */           }
/* 355 */           resBody.addParam(param);
/*     */         } else {
/* 357 */           resEnv.addHeader(new RPCHeaderParam(param));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 363 */       if (!outs.isEmpty())
/* 364 */         for (i = outs.iterator(); i.hasNext(); )
/*     */         {
/* 366 */           RPCParam param = (RPCParam)i.next();
/* 367 */           Holder holder = (Holder)param.getObjectValue();
/* 368 */           Object value = JavaUtils.getHolderValue(holder);
/* 369 */           ParameterDesc paramDesc = param.getParamDesc();
/*     */ 
/* 371 */           param.setObjectValue(value);
/* 372 */           if ((paramDesc != null) && (paramDesc.isOutHeader()))
/* 373 */             resEnv.addHeader(new RPCHeaderParam(param));
/*     */           else
/* 375 */             resBody.addParam(param);
/*     */         }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */       Iterator i;
/* 380 */       throw e;
/*     */     }
/* 382 */     return resBody;
/*     */   }
/*     */ 
/*     */   protected Object invokeMethod(MessageContext msgContext, Method method, Object obj, Object[] argValues)
/*     */     throws Exception
/*     */   {
/* 397 */     return method.invoke(obj, argValues);
/*     */   }
/*     */ 
/*     */   protected void checkMethodName(MessageContext msgContext, String allowedMethods, String methodName)
/*     */     throws Exception
/*     */   {
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.providers.java.RPCProvider
 * JD-Core Version:    0.6.0
 */