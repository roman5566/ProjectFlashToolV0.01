/*     */ package org.apache.axis.deployment.wsdd;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.description.FaultDesc;
/*     */ import org.apache.axis.description.OperationDesc;
/*     */ import org.apache.axis.description.ParameterDesc;
/*     */ import org.apache.axis.description.ServiceDesc;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.w3c.dom.Element;
/*     */ import org.xml.sax.helpers.AttributesImpl;
/*     */ 
/*     */ public class WSDDOperation extends WSDDElement
/*     */ {
/*  46 */   OperationDesc desc = new OperationDesc();
/*     */ 
/*     */   public WSDDOperation(OperationDesc desc)
/*     */   {
/*  52 */     this.desc = desc;
/*     */   }
/*     */ 
/*     */   public WSDDOperation(Element e, ServiceDesc parent)
/*     */     throws WSDDException
/*     */   {
/*  65 */     super(e);
/*     */ 
/*  67 */     this.desc.setParent(parent);
/*  68 */     this.desc.setName(e.getAttribute("name"));
/*     */ 
/*  70 */     String qNameStr = e.getAttribute("qname");
/*  71 */     if ((qNameStr != null) && (!qNameStr.equals(""))) {
/*  72 */       this.desc.setElementQName(XMLUtils.getQNameFromString(qNameStr, e));
/*     */     }
/*  74 */     String retQNameStr = e.getAttribute("returnQName");
/*  75 */     if ((retQNameStr != null) && (!retQNameStr.equals(""))) {
/*  76 */       this.desc.setReturnQName(XMLUtils.getQNameFromString(retQNameStr, e));
/*     */     }
/*  78 */     String retTypeStr = e.getAttribute("returnType");
/*  79 */     if ((retTypeStr != null) && (!retTypeStr.equals(""))) {
/*  80 */       this.desc.setReturnType(XMLUtils.getQNameFromString(retTypeStr, e));
/*     */     }
/*  82 */     String retHStr = e.getAttribute("returnHeader");
/*  83 */     if (retHStr != null) {
/*  84 */       this.desc.setReturnHeader(JavaUtils.isTrueExplicitly(retHStr));
/*     */     }
/*     */ 
/*  87 */     String retItemQName = e.getAttribute("returnItemQName");
/*  88 */     if ((retItemQName != null) && (!retItemQName.equals(""))) {
/*  89 */       ParameterDesc param = this.desc.getReturnParamDesc();
/*  90 */       param.setItemQName(XMLUtils.getQNameFromString(retItemQName, e));
/*     */     }
/*     */ 
/*  93 */     String retItemType = e.getAttribute("returnItemType");
/*  94 */     if ((retItemType != null) && (!retItemType.equals(""))) {
/*  95 */       ParameterDesc param = this.desc.getReturnParamDesc();
/*  96 */       param.setItemType(XMLUtils.getQNameFromString(retItemType, e));
/*     */     }
/*     */ 
/*  99 */     String soapAction = e.getAttribute("soapAction");
/* 100 */     if (soapAction != null) {
/* 101 */       this.desc.setSoapAction(soapAction);
/*     */     }
/*     */ 
/* 104 */     String mepString = e.getAttribute("mep");
/* 105 */     if (mepString != null) {
/* 106 */       this.desc.setMep(mepString);
/*     */     }
/*     */ 
/* 109 */     Element[] parameters = getChildElements(e, "parameter");
/* 110 */     for (int i = 0; i < parameters.length; i++) {
/* 111 */       Element paramEl = parameters[i];
/* 112 */       WSDDParameter parameter = new WSDDParameter(paramEl, this.desc);
/* 113 */       this.desc.addParameter(parameter.getParameter());
/*     */     }
/*     */ 
/* 116 */     Element[] faultElems = getChildElements(e, "fault");
/* 117 */     for (int i = 0; i < faultElems.length; i++) {
/* 118 */       Element faultElem = faultElems[i];
/* 119 */       WSDDFault fault = new WSDDFault(faultElem);
/* 120 */       this.desc.addFault(fault.getFaultDesc());
/*     */     }
/*     */ 
/* 123 */     Element docElem = getChildElement(e, "documentation");
/* 124 */     if (docElem != null) {
/* 125 */       WSDDDocumentation documentation = new WSDDDocumentation(docElem);
/* 126 */       this.desc.setDocumentation(documentation.getValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeToContext(SerializationContext context)
/*     */     throws IOException
/*     */   {
/* 135 */     AttributesImpl attrs = new AttributesImpl();
/*     */ 
/* 137 */     if (this.desc.getReturnQName() != null) {
/* 138 */       attrs.addAttribute("", "returnQName", "returnQName", "CDATA", context.qName2String(this.desc.getReturnQName()));
/*     */     }
/*     */ 
/* 143 */     if (this.desc.getReturnType() != null) {
/* 144 */       attrs.addAttribute("", "returnType", "returnType", "CDATA", context.qName2String(this.desc.getReturnType()));
/*     */     }
/*     */ 
/* 148 */     if (this.desc.isReturnHeader()) {
/* 149 */       attrs.addAttribute("", "returnHeader", "returnHeader", "CDATA", "true");
/*     */     }
/*     */ 
/* 153 */     if (this.desc.getName() != null) {
/* 154 */       attrs.addAttribute("", "name", "name", "CDATA", this.desc.getName());
/*     */     }
/*     */ 
/* 157 */     if (this.desc.getElementQName() != null) {
/* 158 */       attrs.addAttribute("", "qname", "qname", "CDATA", context.qName2String(this.desc.getElementQName()));
/*     */     }
/*     */ 
/* 163 */     QName retItemQName = this.desc.getReturnParamDesc().getItemQName();
/* 164 */     if (retItemQName != null) {
/* 165 */       attrs.addAttribute("", "returnItemQName", "returnItemQName", "CDATA", context.qName2String(retItemQName));
/*     */     }
/*     */ 
/* 170 */     if (this.desc.getSoapAction() != null) {
/* 171 */       attrs.addAttribute("", "soapAction", "soapAction", "CDATA", this.desc.getSoapAction());
/*     */     }
/*     */ 
/* 174 */     context.startElement(getElementName(), attrs);
/*     */ 
/* 176 */     if (this.desc.getDocumentation() != null) {
/* 177 */       WSDDDocumentation documentation = new WSDDDocumentation(this.desc.getDocumentation());
/* 178 */       documentation.writeToContext(context);
/*     */     }
/*     */ 
/* 181 */     ArrayList params = this.desc.getParameters();
/* 182 */     for (Iterator i = params.iterator(); i.hasNext(); ) {
/* 183 */       ParameterDesc parameterDesc = (ParameterDesc)i.next();
/* 184 */       WSDDParameter p = new WSDDParameter(parameterDesc);
/* 185 */       p.writeToContext(context);
/*     */     }
/*     */ 
/* 188 */     ArrayList faults = this.desc.getFaults();
/*     */     Iterator i;
/* 189 */     if (faults != null) {
/* 190 */       for (i = faults.iterator(); i.hasNext(); ) {
/* 191 */         FaultDesc faultDesc = (FaultDesc)i.next();
/* 192 */         WSDDFault f = new WSDDFault(faultDesc);
/* 193 */         f.writeToContext(context);
/*     */       }
/*     */     }
/*     */ 
/* 197 */     context.endElement();
/*     */   }
/*     */ 
/*     */   protected QName getElementName() {
/* 201 */     return QNAME_OPERATION;
/*     */   }
/*     */ 
/*     */   public OperationDesc getOperationDesc()
/*     */   {
/* 206 */     return this.desc;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.WSDDOperation
 * JD-Core Version:    0.6.0
 */