/*    */ package org.apache.axis.deployment.wsdd;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.description.FaultDesc;
/*    */ import org.apache.axis.encoding.SerializationContext;
/*    */ import org.apache.axis.utils.XMLUtils;
/*    */ import org.w3c.dom.Element;
/*    */ import org.xml.sax.helpers.AttributesImpl;
/*    */ 
/*    */ public class WSDDFault extends WSDDElement
/*    */ {
/*    */   FaultDesc desc;
/*    */ 
/*    */   public WSDDFault(FaultDesc desc)
/*    */   {
/* 35 */     this.desc = desc;
/*    */   }
/*    */ 
/*    */   public WSDDFault(Element e)
/*    */     throws WSDDException
/*    */   {
/* 44 */     super(e);
/*    */ 
/* 46 */     this.desc = new FaultDesc();
/*    */ 
/* 48 */     String nameStr = e.getAttribute("name");
/* 49 */     if ((nameStr != null) && (!nameStr.equals(""))) {
/* 50 */       this.desc.setName(nameStr);
/*    */     }
/* 52 */     String qNameStr = e.getAttribute("qname");
/* 53 */     if ((qNameStr != null) && (!qNameStr.equals(""))) {
/* 54 */       this.desc.setQName(XMLUtils.getQNameFromString(qNameStr, e));
/*    */     }
/* 56 */     String classNameStr = e.getAttribute("class");
/* 57 */     if ((classNameStr != null) && (!classNameStr.equals(""))) {
/* 58 */       this.desc.setClassName(classNameStr);
/*    */     }
/* 60 */     String xmlTypeStr = e.getAttribute("type");
/* 61 */     if ((xmlTypeStr != null) && (!xmlTypeStr.equals("")))
/* 62 */       this.desc.setXmlType(XMLUtils.getQNameFromString(xmlTypeStr, e));
/*    */   }
/*    */ 
/*    */   protected QName getElementName()
/*    */   {
/* 69 */     return QNAME_FAULT;
/*    */   }
/*    */ 
/*    */   public void writeToContext(SerializationContext context)
/*    */     throws IOException
/*    */   {
/* 77 */     AttributesImpl attrs = new AttributesImpl();
/*    */ 
/* 79 */     attrs.addAttribute("", "qname", "qname", "CDATA", context.qName2String(this.desc.getQName()));
/*    */ 
/* 83 */     attrs.addAttribute("", "class", "class", "CDATA", this.desc.getClassName());
/*    */ 
/* 86 */     attrs.addAttribute("", "type", "type", "CDATA", context.qName2String(this.desc.getXmlType()));
/*    */ 
/* 90 */     context.startElement(getElementName(), attrs);
/* 91 */     context.endElement();
/*    */   }
/*    */ 
/*    */   public FaultDesc getFaultDesc() {
/* 95 */     return this.desc;
/*    */   }
/*    */ 
/*    */   public void setFaultDesc(FaultDesc desc) {
/* 99 */     this.desc = desc;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.WSDDFault
 * JD-Core Version:    0.6.0
 */