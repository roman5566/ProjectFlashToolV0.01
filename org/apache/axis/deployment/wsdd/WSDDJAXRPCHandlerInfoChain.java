/*     */ package org.apache.axis.deployment.wsdd;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.rpc.handler.HandlerInfo;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.handlers.HandlerInfoChainFactory;
/*     */ import org.apache.axis.utils.ClassUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Element;
/*     */ import org.xml.sax.helpers.AttributesImpl;
/*     */ 
/*     */ public class WSDDJAXRPCHandlerInfoChain extends WSDDHandler
/*     */ {
/*  39 */   protected static Log log = LogFactory.getLog(WSDDJAXRPCHandlerInfoChain.class.getName());
/*     */   private ArrayList _hiList;
/*     */   private HandlerInfoChainFactory _hiChainFactory;
/*     */   private String[] _roles;
/*     */ 
/*     */   public WSDDJAXRPCHandlerInfoChain()
/*     */   {
/*     */   }
/*     */ 
/*     */   public WSDDJAXRPCHandlerInfoChain(Element e)
/*     */     throws WSDDException
/*     */   {
/*  58 */     super(e);
/*     */ 
/*  60 */     ArrayList infoList = new ArrayList();
/*  61 */     this._hiList = new ArrayList();
/*  62 */     Element[] elements = getChildElements(e, "handlerInfo");
/*  63 */     if (elements.length != 0) {
/*  64 */       for (int i = 0; i < elements.length; i++) {
/*  65 */         WSDDJAXRPCHandlerInfo handlerInfo = new WSDDJAXRPCHandlerInfo(elements[i]);
/*     */ 
/*  67 */         this._hiList.add(handlerInfo);
/*     */ 
/*  69 */         String handlerClassName = handlerInfo.getHandlerClassName();
/*  70 */         Class handlerClass = null;
/*     */         try {
/*  72 */           handlerClass = ClassUtils.forName(handlerClassName);
/*     */         } catch (ClassNotFoundException cnf) {
/*  74 */           log.error(Messages.getMessage("handlerInfoChainNoClass00", handlerClassName), cnf);
/*     */         }
/*     */ 
/*  77 */         Map handlerMap = handlerInfo.getHandlerMap();
/*  78 */         QName[] headers = handlerInfo.getHeaders();
/*     */ 
/*  80 */         if (handlerClass != null) {
/*  81 */           HandlerInfo hi = new HandlerInfo(handlerClass, handlerMap, headers);
/*     */ 
/*  83 */           infoList.add(hi);
/*     */         }
/*     */       }
/*     */     }
/*  87 */     this._hiChainFactory = new HandlerInfoChainFactory(infoList);
/*     */ 
/*  89 */     elements = getChildElements(e, "role");
/*  90 */     if (elements.length != 0) {
/*  91 */       ArrayList roleList = new ArrayList();
/*  92 */       for (int i = 0; i < elements.length; i++) {
/*  93 */         String role = elements[i].getAttribute("soapActorName");
/*  94 */         roleList.add(role);
/*     */       }
/*  96 */       this._roles = new String[roleList.size()];
/*  97 */       this._roles = ((String[])roleList.toArray(this._roles));
/*  98 */       this._hiChainFactory.setRoles(this._roles);
/*     */     }
/*     */   }
/*     */ 
/*     */   public HandlerInfoChainFactory getHandlerChainFactory()
/*     */   {
/* 104 */     return this._hiChainFactory;
/*     */   }
/*     */ 
/*     */   public void setHandlerChainFactory(HandlerInfoChainFactory handlerInfoChainFactory) {
/* 108 */     this._hiChainFactory = handlerInfoChainFactory;
/*     */   }
/*     */ 
/*     */   protected QName getElementName() {
/* 112 */     return WSDDConstants.QNAME_JAXRPC_HANDLERINFOCHAIN;
/*     */   }
/*     */ 
/*     */   public void writeToContext(SerializationContext context)
/*     */     throws IOException
/*     */   {
/* 120 */     context.startElement(QNAME_JAXRPC_HANDLERINFOCHAIN, null);
/*     */ 
/* 122 */     List his = this._hiList;
/* 123 */     Iterator iter = his.iterator();
/* 124 */     while (iter.hasNext()) {
/* 125 */       WSDDJAXRPCHandlerInfo hi = (WSDDJAXRPCHandlerInfo)iter.next();
/* 126 */       hi.writeToContext(context);
/*     */     }
/*     */ 
/* 129 */     if (this._roles != null) {
/* 130 */       for (int i = 0; i < this._roles.length; i++) {
/* 131 */         AttributesImpl attrs1 = new AttributesImpl();
/* 132 */         attrs1.addAttribute("", "soapActorName", "soapActorName", "CDATA", this._roles[i]);
/*     */ 
/* 134 */         context.startElement(QNAME_JAXRPC_ROLE, attrs1);
/* 135 */         context.endElement();
/*     */       }
/*     */     }
/*     */ 
/* 139 */     context.endElement();
/*     */   }
/*     */ 
/*     */   public ArrayList getHandlerInfoList() {
/* 143 */     return this._hiList;
/*     */   }
/*     */ 
/*     */   public void setHandlerInfoList(ArrayList hiList) {
/* 147 */     this._hiList = hiList;
/*     */   }
/*     */ 
/*     */   public String[] getRoles() {
/* 151 */     return this._roles;
/*     */   }
/*     */ 
/*     */   public void setRoles(String[] roles) {
/* 155 */     this._roles = roles;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.WSDDJAXRPCHandlerInfoChain
 * JD-Core Version:    0.6.0
 */