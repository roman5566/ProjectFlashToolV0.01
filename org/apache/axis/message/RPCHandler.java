/*     */ package org.apache.axis.message;
/*     */ 
/*     */ import java.util.Vector;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.constants.Style;
/*     */ import org.apache.axis.description.OperationDesc;
/*     */ import org.apache.axis.description.ParameterDesc;
/*     */ import org.apache.axis.encoding.DeserializationContext;
/*     */ import org.apache.axis.encoding.Deserializer;
/*     */ import org.apache.axis.encoding.DeserializerImpl;
/*     */ import org.apache.axis.encoding.TypeMapping;
/*     */ import org.apache.axis.encoding.XMLType;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Element;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class RPCHandler extends SOAPHandler
/*     */ {
/*  60 */   protected static Log log = LogFactory.getLog(RPCHandler.class.getName());
/*     */   private RPCElement rpcElem;
/*  64 */   private RPCParam currentParam = null;
/*     */   private boolean isResponse;
/*     */   private OperationDesc operation;
/*     */   private boolean isHeaderElement;
/*     */ 
/*     */   public RPCHandler(RPCElement rpcElem, boolean isResponse)
/*     */     throws SAXException
/*     */   {
/*  72 */     this.rpcElem = rpcElem;
/*  73 */     this.isResponse = isResponse;
/*     */   }
/*     */ 
/*     */   public void setOperation(OperationDesc myOperation) {
/*  77 */     this.operation = myOperation;
/*     */   }
/*     */ 
/*     */   public void setHeaderElement(boolean value)
/*     */   {
/*  86 */     this.isHeaderElement = true;
/*     */   }
/*     */ 
/*     */   public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 104 */     super.startElement(namespace, localName, prefix, attributes, context);
/* 105 */     this.currentParam = null;
/*     */   }
/*     */ 
/*     */   public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 124 */     if (log.isDebugEnabled()) {
/* 125 */       log.debug("Enter: RPCHandler.onStartChild()");
/*     */     }
/*     */ 
/* 128 */     if (!context.isDoneParsing()) {
/*     */       try {
/* 130 */         context.pushNewElement(new MessageElement(namespace, localName, prefix, attributes, context));
/*     */       }
/*     */       catch (AxisFault axisFault)
/*     */       {
/* 134 */         throw new SAXException(axisFault);
/*     */       }
/*     */     }
/*     */ 
/* 138 */     MessageElement curEl = context.getCurElement();
/* 139 */     QName type = null;
/* 140 */     QName qname = new QName(namespace, localName);
/* 141 */     ParameterDesc paramDesc = null;
/*     */ 
/* 143 */     SOAPConstants soapConstants = context.getSOAPConstants();
/* 144 */     if ((soapConstants == SOAPConstants.SOAP12_CONSTANTS) && (Constants.QNAME_RPC_RESULT.equals(qname)))
/*     */     {
/* 147 */       return new DeserializerImpl();
/*     */     }
/*     */ 
/* 151 */     if ((this.currentParam == null) || (!this.currentParam.getQName().getNamespaceURI().equals(namespace)) || (!this.currentParam.getQName().getLocalPart().equals(localName)))
/*     */     {
/* 154 */       this.currentParam = new RPCParam(namespace, localName, null);
/* 155 */       this.rpcElem.addParam(this.currentParam);
/*     */     }
/*     */ 
/* 161 */     type = curEl.getType();
/* 162 */     if (type == null) {
/* 163 */       type = context.getTypeFromAttributes(namespace, localName, attributes);
/*     */     }
/*     */ 
/* 168 */     if (log.isDebugEnabled()) {
/* 169 */       log.debug(Messages.getMessage("typeFromAttr00", "" + type));
/*     */     }
/*     */ 
/* 173 */     Class destClass = null;
/*     */ 
/* 177 */     if (this.operation != null)
/*     */     {
/* 180 */       if (this.isResponse)
/* 181 */         paramDesc = this.operation.getOutputParamByQName(qname);
/*     */       else {
/* 183 */         paramDesc = this.operation.getInputParamByQName(qname);
/*     */       }
/*     */ 
/* 192 */       if (paramDesc == null) {
/* 193 */         if (this.isResponse) {
/* 194 */           paramDesc = this.operation.getReturnParamDesc();
/*     */         }
/*     */         else {
/* 197 */           paramDesc = this.operation.getParameter(this.rpcElem.getParams().size() - 1);
/*     */         }
/*     */       }
/*     */ 
/* 201 */       if (paramDesc == null) {
/* 202 */         throw new SAXException(Messages.getMessage("noParmDesc"));
/*     */       }
/*     */ 
/* 206 */       if ((!this.isHeaderElement) && (((this.isResponse) && (paramDesc.isOutHeader())) || ((!this.isResponse) && (paramDesc.isInHeader()))))
/*     */       {
/* 209 */         throw new SAXException(Messages.getMessage("expectedHeaderParam", paramDesc.getQName().toString()));
/*     */       }
/*     */ 
/* 214 */       destClass = paramDesc.getJavaType();
/* 215 */       if ((destClass != null) && (destClass.isArray())) {
/* 216 */         context.setDestinationClass(destClass);
/*     */       }
/*     */ 
/* 221 */       this.currentParam.setParamDesc(paramDesc);
/*     */ 
/* 223 */       if (type == null) {
/* 224 */         type = paramDesc.getTypeQName();
/*     */       }
/*     */     }
/*     */ 
/* 228 */     if ((type != null) && (type.equals(XMLType.AXIS_VOID))) {
/* 229 */       Deserializer nilDSer = new DeserializerImpl();
/* 230 */       return (SOAPHandler)nilDSer;
/*     */     }
/*     */ 
/* 245 */     if (context.isNil(attributes)) {
/* 246 */       Deserializer nilDSer = new DeserializerImpl();
/* 247 */       nilDSer.registerValueTarget(new RPCParamTarget(this.currentParam));
/* 248 */       return (SOAPHandler)nilDSer;
/*     */     }
/*     */ 
/* 251 */     Deserializer dser = null;
/* 252 */     if ((type == null) && (namespace != null) && (!namespace.equals(""))) {
/* 253 */       dser = context.getDeserializerForType(qname);
/*     */     } else {
/* 255 */       dser = context.getDeserializer(destClass, type);
/*     */ 
/* 257 */       if ((dser == null) && (destClass != null) && (destClass.isArray()) && (this.operation.getStyle() == Style.DOCUMENT))
/*     */       {
/* 259 */         dser = context.getDeserializerForClass(destClass);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 264 */     if (dser == null) {
/* 265 */       if (type != null) {
/* 266 */         dser = context.getDeserializerForType(type);
/* 267 */         if ((null != destClass) && (dser == null) && (Element.class.isAssignableFrom(destClass)))
/*     */         {
/* 270 */           dser = context.getDeserializerForType(Constants.SOAP_ELEMENT);
/*     */         }
/*     */ 
/* 273 */         if (dser == null) {
/* 274 */           dser = context.getDeserializerForClass(destClass);
/*     */         }
/* 276 */         if (dser == null) {
/* 277 */           throw new SAXException(Messages.getMessage("noDeser01", localName, "" + type));
/*     */         }
/*     */ 
/* 280 */         if ((paramDesc != null) && (paramDesc.getJavaType() != null))
/*     */         {
/* 283 */           Class xsiClass = context.getTypeMapping().getClassForQName(type);
/*     */ 
/* 285 */           if ((null != xsiClass) && (!JavaUtils.isConvertable(xsiClass, destClass)))
/* 286 */             throw new SAXException("Bad types (" + xsiClass + " -> " + destClass + ")");
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 291 */         dser = context.getDeserializerForClass(destClass);
/* 292 */         if (dser == null) {
/* 293 */           dser = new DeserializerImpl();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 298 */     dser.setDefaultType(type);
/*     */ 
/* 300 */     dser.registerValueTarget(new RPCParamTarget(this.currentParam));
/*     */ 
/* 302 */     if (log.isDebugEnabled()) {
/* 303 */       log.debug("Exit: RPCHandler.onStartChild()");
/*     */     }
/* 305 */     return (SOAPHandler)dser;
/*     */   }
/*     */ 
/*     */   public void endElement(String namespace, String localName, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 316 */     if (log.isDebugEnabled()) {
/* 317 */       log.debug(Messages.getMessage("setProp00", "MessageContext", "RPCHandler.endElement()."));
/*     */     }
/*     */ 
/* 320 */     context.getMessageContext().setProperty("RPC", this.rpcElem);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.RPCHandler
 * JD-Core Version:    0.6.0
 */