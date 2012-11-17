/*     */ package org.apache.axis.message;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.description.FaultDesc;
/*     */ import org.apache.axis.description.OperationDesc;
/*     */ import org.apache.axis.encoding.Callback;
/*     */ import org.apache.axis.encoding.CallbackTarget;
/*     */ import org.apache.axis.encoding.DeserializationContext;
/*     */ import org.apache.axis.encoding.Deserializer;
/*     */ import org.apache.axis.encoding.DeserializerImpl;
/*     */ import org.apache.axis.encoding.TypeMapping;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.apache.axis.utils.ClassUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class SOAPFaultDetailsBuilder extends SOAPHandler
/*     */   implements Callback
/*     */ {
/*     */   protected SOAPFaultBuilder builder;
/*     */ 
/*     */   public SOAPFaultDetailsBuilder(SOAPFaultBuilder builder)
/*     */   {
/*  48 */     this.builder = builder;
/*     */   }
/*     */ 
/*     */   public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/*  57 */     SOAPConstants soapConstants = context.getSOAPConstants();
/*     */ 
/*  59 */     if ((soapConstants == SOAPConstants.SOAP12_CONSTANTS) && (attributes.getValue("http://www.w3.org/2003/05/soap-envelope", "encodingStyle") != null))
/*     */     {
/*  62 */       AxisFault fault = new AxisFault(Constants.FAULT_SOAP12_SENDER, null, Messages.getMessage("noEncodingStyleAttrAppear", "Detail"), null, null, null);
/*     */ 
/*  65 */       throw new SAXException(fault);
/*     */     }
/*     */ 
/*  68 */     super.startElement(namespace, localName, prefix, attributes, context);
/*     */   }
/*     */ 
/*     */   public SOAPHandler onStartChild(String namespace, String name, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/*  79 */     QName qn = new QName(namespace, name);
/*     */ 
/*  84 */     if (name.equals("exceptionName"))
/*     */     {
/*  86 */       Deserializer dser = context.getDeserializerForType(Constants.XSD_STRING);
/*  87 */       dser.registerValueTarget(new CallbackTarget(this, "exceptionName"));
/*  88 */       return (SOAPHandler)dser;
/*     */     }
/*     */ 
/*  93 */     MessageContext msgContext = context.getMessageContext();
/*  94 */     SOAPConstants soapConstants = Constants.DEFAULT_SOAP_VERSION;
/*  95 */     OperationDesc op = null;
/*  96 */     if (msgContext != null) {
/*  97 */       soapConstants = msgContext.getSOAPConstants();
/*  98 */       op = msgContext.getOperation();
/*     */     }
/* 100 */     Class faultClass = null;
/* 101 */     QName faultXmlType = null;
/* 102 */     if (op != null) {
/* 103 */       FaultDesc faultDesc = null;
/*     */ 
/* 105 */       faultXmlType = context.getTypeFromAttributes(namespace, name, attributes);
/*     */ 
/* 108 */       if (faultXmlType != null) {
/* 109 */         faultDesc = op.getFaultByXmlType(faultXmlType);
/*     */       }
/*     */ 
/* 113 */       if (faultDesc == null) {
/* 114 */         faultDesc = op.getFaultByQName(qn);
/* 115 */         if ((faultXmlType == null) && (faultDesc != null)) {
/* 116 */           faultXmlType = faultDesc.getXmlType();
/*     */         }
/*     */       }
/*     */ 
/* 120 */       if ((faultDesc == null) && (op.getFaults() != null)) {
/* 121 */         Iterator i = op.getFaults().iterator();
/* 122 */         while (i.hasNext()) {
/* 123 */           FaultDesc fdesc = (FaultDesc)i.next();
/* 124 */           if (fdesc.getClassName().equals(name)) {
/* 125 */             faultDesc = fdesc;
/* 126 */             faultXmlType = fdesc.getXmlType();
/* 127 */             break;
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 133 */       if (faultDesc != null)
/*     */         try {
/* 135 */           faultClass = ClassUtils.forName(faultDesc.getClassName());
/*     */         }
/*     */         catch (ClassNotFoundException e) {
/*     */         }
/*     */     }
/*     */     else {
/* 141 */       faultXmlType = context.getTypeFromAttributes(namespace, name, attributes);
/*     */     }
/*     */ 
/* 146 */     if (faultClass == null) {
/* 147 */       faultClass = context.getTypeMapping().getClassForQName(faultXmlType);
/*     */     }
/*     */ 
/* 151 */     if ((faultClass != null) && (faultXmlType != null)) {
/* 152 */       this.builder.setFaultClass(faultClass);
/* 153 */       this.builder.setWaiting(true);
/*     */ 
/* 155 */       Deserializer dser = null;
/* 156 */       if (attributes.getValue(soapConstants.getAttrHref()) == null) {
/* 157 */         dser = context.getDeserializerForType(faultXmlType);
/*     */       } else {
/* 159 */         dser = new DeserializerImpl();
/* 160 */         dser.setDefaultType(faultXmlType);
/*     */       }
/* 162 */       if (dser != null) {
/* 163 */         dser.registerValueTarget(new CallbackTarget(this, "faultData"));
/*     */       }
/* 165 */       return (SOAPHandler)dser;
/*     */     }
/* 167 */     return null;
/*     */   }
/*     */ 
/*     */   public void setValue(Object value, Object hint)
/*     */   {
/* 178 */     if ("faultData".equals(hint)) {
/* 179 */       this.builder.setFaultData(value);
/* 180 */     } else if ("exceptionName".equals(hint)) {
/* 181 */       String faultClassName = (String)value;
/*     */       try {
/* 183 */         Class faultClass = ClassUtils.forName(faultClassName);
/* 184 */         this.builder.setFaultClass(faultClass);
/*     */       }
/*     */       catch (ClassNotFoundException e)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.SOAPFaultDetailsBuilder
 * JD-Core Version:    0.6.0
 */