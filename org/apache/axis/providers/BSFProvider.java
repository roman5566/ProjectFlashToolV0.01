/*    */ package org.apache.axis.providers;
/*    */ 
/*    */ import java.util.Vector;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.AxisFault;
/*    */ import org.apache.axis.Constants;
/*    */ import org.apache.axis.Message;
/*    */ import org.apache.axis.MessageContext;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.components.script.Script;
/*    */ import org.apache.axis.components.script.ScriptFactory;
/*    */ import org.apache.axis.description.OperationDesc;
/*    */ import org.apache.axis.description.ParameterDesc;
/*    */ import org.apache.axis.handlers.soap.SOAPService;
/*    */ import org.apache.axis.message.RPCElement;
/*    */ import org.apache.axis.message.RPCHeaderParam;
/*    */ import org.apache.axis.message.RPCParam;
/*    */ import org.apache.axis.message.SOAPBodyElement;
/*    */ import org.apache.axis.message.SOAPEnvelope;
/*    */ import org.apache.axis.soap.SOAPConstants;
/*    */ import org.apache.axis.utils.JavaUtils;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class BSFProvider extends BasicProvider
/*    */ {
/* 43 */   protected static Log log = LogFactory.getLog(BSFProvider.class.getName());
/*    */   public static final String OPTION_LANGUAGE = "language";
/*    */   public static final String OPTION_SRC = "src";
/*    */   public static final String OPTION_SCRIPT = "script";
/*    */ 
/*    */   public void invoke(MessageContext msgContext)
/*    */     throws AxisFault
/*    */   {
/*    */     try
/*    */     {
/* 53 */       SOAPService service = msgContext.getService();
/* 54 */       String language = (String)service.getOption("language");
/* 55 */       String scriptStr = (String)service.getOption("src");
/*    */ 
/* 57 */       if (log.isDebugEnabled()) {
/* 58 */         log.debug("Enter: BSFProvider.processMessage()");
/*    */       }
/*    */ 
/* 61 */       OperationDesc operation = msgContext.getOperation();
/*    */ 
/* 63 */       Vector bodies = msgContext.getRequestMessage().getSOAPEnvelope().getBodyElements();
/* 64 */       if (log.isDebugEnabled()) {
/* 65 */         log.debug(Messages.getMessage("bodyElems00", "" + bodies.size()));
/* 66 */         log.debug(Messages.getMessage("bodyIs00", "" + bodies.get(0)));
/*    */       }
/*    */ 
/* 69 */       RPCElement body = null;
/*    */ 
/* 72 */       for (int bNum = 0; (body == null) && (bNum < bodies.size()); bNum++)
/*    */       {
/* 78 */         if (!(bodies.get(bNum) instanceof RPCElement)) {
/* 79 */           SOAPBodyElement bodyEl = (SOAPBodyElement)bodies.get(bNum);
/*    */ 
/* 83 */           if ((bodyEl.isRoot()) && (operation != null) && (bodyEl.getID() == null)) {
/* 84 */             ParameterDesc param = operation.getParameter(bNum);
/*    */ 
/* 86 */             if (param != null) {
/* 87 */               Object val = bodyEl.getValueAsType(param.getTypeQName());
/* 88 */               body = new RPCElement("", operation.getName(), new Object[] { val });
/*    */             }
/*    */           }
/*    */         }
/*    */         else
/*    */         {
/* 94 */           body = (RPCElement)bodies.get(bNum);
/*    */         }
/*    */       }
/*    */ 
/* 98 */       String methodName = body.getMethodName();
/* 99 */       Vector args = body.getParams();
/* 100 */       int numArgs = args.size();
/*    */ 
/* 102 */       Object[] argValues = new Object[numArgs];
/*    */ 
/* 109 */       for (int i = 0; i < numArgs; i++) {
/* 110 */         RPCParam rpcParam = (RPCParam)args.get(i);
/* 111 */         Object value = rpcParam.getObjectValue();
/*    */ 
/* 114 */         ParameterDesc paramDesc = rpcParam.getParamDesc();
/*    */ 
/* 119 */         if ((paramDesc != null) && (paramDesc.getJavaType() != null))
/*    */         {
/* 122 */           Class sigType = paramDesc.getJavaType();
/*    */ 
/* 125 */           value = JavaUtils.convert(value, sigType);
/*    */ 
/* 128 */           rpcParam.setObjectValue(value);
/*    */         }
/* 130 */         argValues[i] = value;
/*    */       }
/*    */ 
/* 133 */       Script script = ScriptFactory.getScript();
/* 134 */       Object result = script.run(language, service.getName(), scriptStr, methodName, argValues);
/*    */ 
/* 136 */       RPCElement resBody = new RPCElement(methodName + "Response");
/* 137 */       resBody.setPrefix(body.getPrefix());
/* 138 */       resBody.setNamespaceURI(body.getNamespaceURI());
/* 139 */       resBody.setEncodingStyle(msgContext.getEncodingStyle());
/*    */ 
/* 141 */       Message resMsg = msgContext.getResponseMessage();
/*    */       SOAPEnvelope resEnv;
/* 145 */       if (resMsg == null) {
/* 146 */         SOAPEnvelope resEnv = new SOAPEnvelope(msgContext.getSOAPConstants());
/*    */ 
/* 148 */         resMsg = new Message(resEnv);
/* 149 */         msgContext.setResponseMessage(resMsg);
/*    */       } else {
/* 151 */         resEnv = resMsg.getSOAPEnvelope();
/*    */       }
/*    */ 
/* 154 */       QName returnQName = operation.getReturnQName();
/* 155 */       if (returnQName == null) {
/* 156 */         returnQName = new QName("", methodName + "Return");
/*    */       }
/*    */ 
/* 160 */       if (msgContext.getSOAPConstants() == SOAPConstants.SOAP12_CONSTANTS)
/*    */       {
/* 162 */         returnQName = Constants.QNAME_RPC_RESULT;
/*    */       }
/*    */ 
/* 165 */       RPCParam param = new RPCParam(returnQName, result);
/* 166 */       param.setParamDesc(operation.getReturnParamDesc());
/* 167 */       if (!operation.isReturnHeader())
/* 168 */         resBody.addParam(param);
/*    */       else {
/* 170 */         resEnv.addHeader(new RPCHeaderParam(param));
/*    */       }
/*    */ 
/* 173 */       resEnv.addBodyElement(resBody);
/*    */     }
/*    */     catch (Exception e) {
/* 176 */       entLog.debug(Messages.getMessage("toAxisFault00"), e);
/* 177 */       throw AxisFault.makeFault(e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void initServiceDesc(SOAPService service, MessageContext msgContext)
/*    */     throws AxisFault
/*    */   {
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.providers.BSFProvider
 * JD-Core Version:    0.6.0
 */