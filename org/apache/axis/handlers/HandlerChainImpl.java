/*     */ package org.apache.axis.handlers;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.xml.rpc.JAXRPCException;
/*     */ import javax.xml.rpc.handler.Handler;
/*     */ import javax.xml.rpc.handler.HandlerChain;
/*     */ import javax.xml.rpc.handler.HandlerInfo;
/*     */ import javax.xml.rpc.handler.soap.SOAPMessageContext;
/*     */ import javax.xml.rpc.soap.SOAPFaultException;
/*     */ import javax.xml.soap.SOAPBody;
/*     */ import javax.xml.soap.SOAPElement;
/*     */ import javax.xml.soap.SOAPEnvelope;
/*     */ import javax.xml.soap.SOAPException;
/*     */ import javax.xml.soap.SOAPMessage;
/*     */ import javax.xml.soap.SOAPPart;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.utils.ClassUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class HandlerChainImpl extends ArrayList
/*     */   implements HandlerChain
/*     */ {
/*  45 */   protected static Log log = LogFactory.getLog(HandlerChainImpl.class.getName());
/*     */   public static final String JAXRPC_METHOD_INFO = "jaxrpc.method.info";
/*     */   private String[] _roles;
/*  52 */   private int falseIndex = -1;
/*     */ 
/*  69 */   protected List handlerInfos = new ArrayList();
/*     */ 
/*     */   public String[] getRoles()
/*     */   {
/*  55 */     return this._roles;
/*     */   }
/*     */ 
/*     */   public void setRoles(String[] roles) {
/*  59 */     if (roles != null)
/*     */     {
/*  61 */       this._roles = ((String[])roles.clone());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void init(Map map)
/*     */   {
/*     */   }
/*     */ 
/*     */   public HandlerChainImpl()
/*     */   {
/*     */   }
/*     */ 
/*     */   public HandlerChainImpl(List handlerInfos)
/*     */   {
/*  75 */     this.handlerInfos = handlerInfos;
/*  76 */     for (int i = 0; i < handlerInfos.size(); i++)
/*  77 */       add(newHandler(getHandlerInfo(i)));
/*     */   }
/*     */ 
/*     */   public void addNewHandler(String className, Map config)
/*     */   {
/*     */     try {
/*  83 */       HandlerInfo handlerInfo = new HandlerInfo(ClassUtils.forName(className), config, null);
/*     */ 
/*  85 */       this.handlerInfos.add(handlerInfo);
/*  86 */       add(newHandler(handlerInfo));
/*     */     } catch (Exception ex) {
/*  88 */       String messageText = Messages.getMessage("NoJAXRPCHandler00", className);
/*     */ 
/*  90 */       throw new JAXRPCException(messageText, ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean handleFault(javax.xml.rpc.handler.MessageContext _context) {
/*  95 */     SOAPMessageContext context = (SOAPMessageContext)_context;
/*  96 */     preInvoke(context);
/*     */     try {
/*  98 */       int endIdx = size() - 1;
/*  99 */       if (this.falseIndex != -1) {
/* 100 */         endIdx = this.falseIndex;
/*     */       }
/* 102 */       for (int i = endIdx; i >= 0; i--)
/* 103 */         if (!getHandlerInstance(i).handleFault(context)) {
/* 104 */           int i = 0;
/*     */           return i;
/*     */         }
/* 107 */       i = 1;
/*     */       return i; } finally { postInvoke(context); } throw localObject;
/*     */   }
/*     */ 
/*     */   public ArrayList getMessageInfo(SOAPMessage message)
/*     */   {
/* 114 */     ArrayList list = new ArrayList();
/*     */     try {
/* 116 */       if ((message == null) || (message.getSOAPPart() == null))
/* 117 */         return list;
/* 118 */       SOAPEnvelope env = message.getSOAPPart().getEnvelope();
/* 119 */       SOAPBody body = env.getBody();
/* 120 */       Iterator it = body.getChildElements();
/* 121 */       SOAPElement operation = (SOAPElement)it.next();
/* 122 */       list.add(operation.getElementName().toString());
/* 123 */       for (i = operation.getChildElements(); i.hasNext(); ) {
/* 124 */         SOAPElement elt = (SOAPElement)i.next();
/* 125 */         list.add(elt.getElementName().toString());
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */       Iterator i;
/* 128 */       log.debug("Exception in getMessageInfo : ", e);
/*     */     }
/* 130 */     return list;
/*     */   }
/*     */ 
/*     */   public boolean handleRequest(javax.xml.rpc.handler.MessageContext _context) {
/* 134 */     org.apache.axis.MessageContext actx = (org.apache.axis.MessageContext)_context;
/*     */ 
/* 136 */     actx.setRoles(getRoles());
/* 137 */     SOAPMessageContext context = (SOAPMessageContext)_context;
/* 138 */     preInvoke(context);
/*     */     try {
/* 140 */       for (int i = 0; i < size(); i++) {
/* 141 */         Handler currentHandler = getHandlerInstance(i);
/*     */         try {
/* 143 */           if (!currentHandler.handleRequest(context)) {
/* 144 */             this.falseIndex = i;
/* 145 */             int i = 0;
/*     */ 
/* 154 */             postInvoke(context); return i;
/*     */           }
/*     */         }
/*     */         catch (SOAPFaultException sfe)
/*     */         {
/* 148 */           this.falseIndex = i;
/* 149 */           throw sfe;
/*     */         }
/*     */       }
/* 152 */       i = 1;
/*     */       return i; } finally { postInvoke(context); } throw localObject;
/*     */   }
/*     */ 
/*     */   public boolean handleResponse(javax.xml.rpc.handler.MessageContext context)
/*     */   {
/* 159 */     SOAPMessageContext scontext = (SOAPMessageContext)context;
/* 160 */     preInvoke(scontext);
/*     */     try {
/* 162 */       int endIdx = size() - 1;
/* 163 */       if (this.falseIndex != -1) {
/* 164 */         endIdx = this.falseIndex;
/*     */       }
/* 166 */       for (int i = endIdx; i >= 0; i--)
/* 167 */         if (!getHandlerInstance(i).handleResponse(context)) {
/* 168 */           int i = 0;
/*     */           return i;
/*     */         }
/* 171 */       i = 1;
/*     */       return i; } finally { postInvoke(scontext); } throw localObject;
/*     */   }
/*     */ 
/*     */   private void preInvoke(SOAPMessageContext msgContext)
/*     */   {
/*     */     try {
/* 179 */       SOAPMessage message = msgContext.getMessage();
/*     */ 
/* 181 */       if ((message != null) && (message.getSOAPPart() != null))
/* 182 */         message.getSOAPPart().getEnvelope();
/* 183 */       msgContext.setProperty("axis.form.optimization", Boolean.FALSE);
/*     */ 
/* 185 */       msgContext.setProperty("jaxrpc.method.info", getMessageInfo(message));
/*     */     } catch (Exception e) {
/* 187 */       log.debug("Exception in preInvoke : ", e);
/* 188 */       throw new RuntimeException("Exception in preInvoke : " + e.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void postInvoke(SOAPMessageContext msgContext) {
/* 193 */     Boolean propFormOptimization = (Boolean)msgContext.getProperty("axis.form.optimization");
/* 194 */     if ((propFormOptimization != null) && (!propFormOptimization.booleanValue())) {
/* 195 */       msgContext.setProperty("axis.form.optimization", Boolean.TRUE);
/*     */ 
/* 197 */       SOAPMessage message = msgContext.getMessage();
/* 198 */       ArrayList oldList = (ArrayList)msgContext.getProperty("jaxrpc.method.info");
/*     */ 
/* 200 */       if ((oldList != null) && 
/* 201 */         (!Arrays.equals(oldList.toArray(), getMessageInfo(message).toArray())))
/*     */       {
/* 203 */         throw new RuntimeException(Messages.getMessage("invocationArgumentsModified00"));
/*     */       }
/*     */       try
/*     */       {
/* 207 */         if (message != null)
/* 208 */           message.saveChanges();
/*     */       }
/*     */       catch (SOAPException e) {
/* 211 */         log.debug("Exception in postInvoke : ", e);
/* 212 */         throw new RuntimeException("Exception in postInvoke : " + e.toString());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void destroy() {
/* 218 */     int endIdx = size() - 1;
/* 219 */     if (this.falseIndex != -1) {
/* 220 */       endIdx = this.falseIndex;
/*     */     }
/* 222 */     for (int i = endIdx; i >= 0; i--) {
/* 223 */       getHandlerInstance(i).destroy();
/*     */     }
/* 225 */     this.falseIndex = -1;
/* 226 */     clear();
/*     */   }
/*     */ 
/*     */   private Handler getHandlerInstance(int index) {
/* 230 */     return (Handler)get(index);
/*     */   }
/*     */ 
/*     */   private HandlerInfo getHandlerInfo(int index) {
/* 234 */     return (HandlerInfo)this.handlerInfos.get(index);
/*     */   }
/*     */   private Handler newHandler(HandlerInfo handlerInfo) {
/*     */     String messageText;
/*     */     try { Handler handler = (Handler)handlerInfo.getHandlerClass().newInstance();
/*     */ 
/* 241 */       handler.init(handlerInfo);
/* 242 */       return handler;
/*     */     } catch (Exception ex) {
/* 244 */       messageText = Messages.getMessage("NoJAXRPCHandler00", handlerInfo.getHandlerClass().toString());
/*     */     }
/*     */ 
/* 247 */     throw new JAXRPCException(messageText, ex);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.handlers.HandlerChainImpl
 * JD-Core Version:    0.6.0
 */