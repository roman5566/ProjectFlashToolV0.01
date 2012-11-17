/*     */ package org.apache.axis.providers.java;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Vector;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.description.OperationDesc;
/*     */ import org.apache.axis.description.ServiceDesc;
/*     */ import org.apache.axis.handlers.soap.SOAPService;
/*     */ import org.apache.axis.i18n.Messages;
/*     */ import org.apache.axis.message.MessageElement;
/*     */ import org.apache.axis.message.SOAPBodyElement;
/*     */ import org.apache.axis.message.SOAPEnvelope;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ 
/*     */ public class MsgProvider extends JavaProvider
/*     */ {
/*     */   public void processMessage(MessageContext msgContext, SOAPEnvelope reqEnv, SOAPEnvelope resEnv, Object obj)
/*     */     throws Exception
/*     */   {
/*  70 */     OperationDesc operation = msgContext.getOperation();
/*  71 */     SOAPService service = msgContext.getService();
/*  72 */     ServiceDesc serviceDesc = service.getServiceDescription();
/*  73 */     QName opQName = null;
/*     */ 
/*  75 */     if (operation == null) {
/*  76 */       Vector bodyElements = reqEnv.getBodyElements();
/*  77 */       if (bodyElements.size() > 0) {
/*  78 */         MessageElement element = (MessageElement)bodyElements.get(0);
/*  79 */         if (element != null) {
/*  80 */           opQName = new QName(element.getNamespaceURI(), element.getLocalName());
/*     */ 
/*  82 */           operation = serviceDesc.getOperationByElementQName(opQName);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  87 */     if (operation == null) {
/*  88 */       throw new AxisFault(Messages.getMessage("noOperationForQName", opQName == null ? "null" : opQName.toString()));
/*     */     }
/*     */ 
/*  92 */     Method method = operation.getMethod();
/*     */ 
/*  94 */     int methodType = operation.getMessageOperationStyle();
/*     */ 
/*  96 */     if (methodType != 2)
/*     */     {
/*  98 */       Vector bodies = reqEnv.getBodyElements();
/*  99 */       Object[] argObjects = new Object[1];
/*     */ 
/* 101 */       switch (methodType)
/*     */       {
/*     */       case 1:
/* 104 */         SOAPBodyElement[] bodyElements = new SOAPBodyElement[bodies.size()];
/*     */ 
/* 106 */         bodies.toArray(bodyElements);
/* 107 */         argObjects[0] = bodyElements;
/* 108 */         SOAPBodyElement[] bodyResult = (SOAPBodyElement[])method.invoke(obj, argObjects);
/*     */ 
/* 110 */         if (bodyResult != null) {
/* 111 */           for (int i = 0; i < bodyResult.length; i++) {
/* 112 */             SOAPBodyElement bodyElement = bodyResult[i];
/* 113 */             resEnv.addBodyElement(bodyElement);
/*     */           }
/*     */         }
/* 116 */         return;
/*     */       case 3:
/* 120 */         Element[] elements = new Element[bodies.size()];
/* 121 */         for (int i = 0; i < elements.length; i++) {
/* 122 */           SOAPBodyElement body = (SOAPBodyElement)bodies.get(i);
/* 123 */           elements[i] = body.getAsDOM();
/*     */         }
/* 125 */         argObjects[0] = elements;
/* 126 */         Element[] elemResult = (Element[])method.invoke(obj, argObjects);
/*     */ 
/* 128 */         if (elemResult != null) {
/* 129 */           for (int i = 0; i < elemResult.length; i++) {
/* 130 */             if (elemResult[i] != null) {
/* 131 */               resEnv.addBodyElement(new SOAPBodyElement(elemResult[i]));
/*     */             }
/*     */           }
/*     */         }
/* 135 */         return;
/*     */       case 4:
/* 139 */         Document doc = ((SOAPBodyElement)bodies.get(0)).getAsDocument();
/* 140 */         argObjects[0] = doc;
/* 141 */         Document resultDoc = (Document)method.invoke(obj, argObjects);
/*     */ 
/* 143 */         if (resultDoc != null) {
/* 144 */           resEnv.addBodyElement(new SOAPBodyElement(resultDoc.getDocumentElement()));
/*     */         }
/*     */ 
/* 147 */         return;
/*     */       case 2:
/*     */       }
/*     */     } else {
/* 150 */       Object[] argObjects = new Object[2];
/*     */ 
/* 153 */       argObjects[0] = reqEnv;
/* 154 */       argObjects[1] = resEnv;
/* 155 */       method.invoke(obj, argObjects);
/* 156 */       return;
/*     */     }
/*     */ 
/* 160 */     throw new AxisFault(Messages.getMessage("badMsgMethodStyle"));
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.providers.java.MsgProvider
 * JD-Core Version:    0.6.0
 */