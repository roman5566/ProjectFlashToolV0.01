/*     */ package org.apache.axis.deployment.wsdd;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.description.OperationDesc;
/*     */ import org.apache.axis.description.ParameterDesc;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.w3c.dom.Element;
/*     */ import org.xml.sax.helpers.AttributesImpl;
/*     */ 
/*     */ public class WSDDParameter extends WSDDElement
/*     */ {
/*     */   OperationDesc parent;
/*  32 */   ParameterDesc parameter = new ParameterDesc();
/*     */ 
/*     */   public WSDDParameter(Element e, OperationDesc parent) throws WSDDException
/*     */   {
/*  36 */     super(e);
/*  37 */     this.parent = parent;
/*     */ 
/*  42 */     String nameStr = e.getAttribute("qname");
/*  43 */     if ((nameStr != null) && (!nameStr.equals(""))) {
/*  44 */       this.parameter.setQName(XMLUtils.getQNameFromString(nameStr, e));
/*     */     } else {
/*  46 */       nameStr = e.getAttribute("name");
/*  47 */       if ((nameStr != null) && (!nameStr.equals(""))) {
/*  48 */         this.parameter.setQName(new QName(null, nameStr));
/*     */       }
/*     */     }
/*     */ 
/*  52 */     String modeStr = e.getAttribute("mode");
/*  53 */     if ((modeStr != null) && (!modeStr.equals(""))) {
/*  54 */       this.parameter.setMode(ParameterDesc.modeFromString(modeStr));
/*     */     }
/*     */ 
/*  57 */     String inHStr = e.getAttribute("inHeader");
/*  58 */     if (inHStr != null) {
/*  59 */       this.parameter.setInHeader(JavaUtils.isTrueExplicitly(inHStr));
/*     */     }
/*  61 */     String outHStr = e.getAttribute("outHeader");
/*  62 */     if (outHStr != null) {
/*  63 */       this.parameter.setOutHeader(JavaUtils.isTrueExplicitly(outHStr));
/*     */     }
/*     */ 
/*  66 */     String typeStr = e.getAttribute("type");
/*  67 */     if ((typeStr != null) && (!typeStr.equals(""))) {
/*  68 */       this.parameter.setTypeQName(XMLUtils.getQNameFromString(typeStr, e));
/*     */     }
/*     */ 
/*  71 */     String itemQNameStr = e.getAttribute("itemQName");
/*  72 */     if ((itemQNameStr != null) && (!itemQNameStr.equals(""))) {
/*  73 */       this.parameter.setItemQName(XMLUtils.getQNameFromString(itemQNameStr, e));
/*     */     }
/*     */ 
/*  76 */     String itemTypeStr = e.getAttribute("itemType");
/*  77 */     if ((itemTypeStr != null) && (!itemTypeStr.equals(""))) {
/*  78 */       this.parameter.setItemType(XMLUtils.getQNameFromString(itemTypeStr, e));
/*     */     }
/*     */ 
/*  81 */     Element docElem = getChildElement(e, "documentation");
/*  82 */     if (docElem != null) {
/*  83 */       WSDDDocumentation documentation = new WSDDDocumentation(docElem);
/*  84 */       this.parameter.setDocumentation(documentation.getValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   public WSDDParameter() {
/*     */   }
/*     */ 
/*     */   public WSDDParameter(ParameterDesc parameter) {
/*  92 */     this.parameter = parameter;
/*     */   }
/*     */ 
/*     */   public void writeToContext(SerializationContext context)
/*     */     throws IOException
/*     */   {
/* 100 */     AttributesImpl attrs = new AttributesImpl();
/*     */ 
/* 102 */     QName qname = this.parameter.getQName();
/* 103 */     if (qname != null) {
/* 104 */       if ((qname.getNamespaceURI() != null) && (!qname.getNamespaceURI().equals("")))
/*     */       {
/* 106 */         attrs.addAttribute("", "qname", "qname", "CDATA", context.qName2String(this.parameter.getQName()));
/*     */       }
/*     */       else
/*     */       {
/* 110 */         attrs.addAttribute("", "name", "name", "CDATA", this.parameter.getQName().getLocalPart());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 116 */     byte mode = this.parameter.getMode();
/* 117 */     if (mode != 1) {
/* 118 */       String modeStr = ParameterDesc.getModeAsString(mode);
/* 119 */       attrs.addAttribute("", "mode", "mode", "CDATA", modeStr);
/*     */     }
/*     */ 
/* 122 */     if (this.parameter.isInHeader()) {
/* 123 */       attrs.addAttribute("", "inHeader", "inHeader", "CDATA", "true");
/*     */     }
/*     */ 
/* 127 */     if (this.parameter.isOutHeader()) {
/* 128 */       attrs.addAttribute("", "outHeader", "outHeader", "CDATA", "true");
/*     */     }
/*     */ 
/* 132 */     QName typeQName = this.parameter.getTypeQName();
/* 133 */     if (typeQName != null) {
/* 134 */       attrs.addAttribute("", "type", "type", "CDATA", context.qName2String(typeQName));
/*     */     }
/*     */ 
/* 138 */     QName itemQName = this.parameter.getItemQName();
/* 139 */     if (itemQName != null) {
/* 140 */       attrs.addAttribute("", "itemQName", "itemQName", "CDATA", context.qName2String(itemQName));
/*     */     }
/*     */ 
/* 144 */     QName itemType = this.parameter.getItemType();
/* 145 */     if (itemType != null) {
/* 146 */       attrs.addAttribute("", "itemType", "itemType", "CDATA", context.qName2String(itemType));
/*     */     }
/*     */ 
/* 150 */     context.startElement(getElementName(), attrs);
/*     */ 
/* 152 */     if (this.parameter.getDocumentation() != null) {
/* 153 */       WSDDDocumentation documentation = new WSDDDocumentation(this.parameter.getDocumentation());
/* 154 */       documentation.writeToContext(context);
/*     */     }
/*     */ 
/* 157 */     context.endElement();
/*     */   }
/*     */ 
/*     */   public ParameterDesc getParameter() {
/* 161 */     return this.parameter;
/*     */   }
/*     */ 
/*     */   public void setParameter(ParameterDesc parameter) {
/* 165 */     this.parameter = parameter;
/*     */   }
/*     */ 
/*     */   protected QName getElementName()
/*     */   {
/* 172 */     return WSDDConstants.QNAME_PARAM;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.WSDDParameter
 * JD-Core Version:    0.6.0
 */