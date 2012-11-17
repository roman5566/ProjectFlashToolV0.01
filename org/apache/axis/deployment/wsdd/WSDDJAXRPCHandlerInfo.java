/*     */ package org.apache.axis.deployment.wsdd;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.w3c.dom.Element;
/*     */ import org.xml.sax.helpers.AttributesImpl;
/*     */ 
/*     */ public class WSDDJAXRPCHandlerInfo extends WSDDElement
/*     */ {
/*     */   private String _classname;
/*     */   private Map _map;
/*     */   private QName[] _headers;
/*     */ 
/*     */   public WSDDJAXRPCHandlerInfo()
/*     */   {
/*     */   }
/*     */ 
/*     */   public WSDDJAXRPCHandlerInfo(Element e)
/*     */     throws WSDDException
/*     */   {
/*  57 */     super(e);
/*     */ 
/*  59 */     String classnameStr = e.getAttribute("classname");
/*  60 */     if ((classnameStr != null) && (!classnameStr.equals(""))) {
/*  61 */       this._classname = classnameStr;
/*     */     }
/*     */     else {
/*  64 */       throw new WSDDException(Messages.getMessage("noClassNameAttr00"));
/*     */     }
/*  66 */     Element[] elements = getChildElements(e, "parameter");
/*     */ 
/*  69 */     this._map = new HashMap();
/*  70 */     if (elements.length != 0)
/*     */     {
/*  72 */       for (int i = 0; i < elements.length; i++) {
/*  73 */         Element param = elements[i];
/*  74 */         String pname = param.getAttribute("name");
/*  75 */         String value = param.getAttribute("value");
/*  76 */         this._map.put(pname, value);
/*     */       }
/*     */     }
/*     */ 
/*  80 */     elements = getChildElements(e, "header");
/*  81 */     if (elements.length != 0) {
/*  82 */       ArrayList headerList = new ArrayList();
/*  83 */       for (int i = 0; i < elements.length; i++) {
/*  84 */         Element qElem = elements[i];
/*  85 */         String headerStr = qElem.getAttribute("qname");
/*  86 */         if ((headerStr == null) || (headerStr.equals(""))) {
/*  87 */           throw new WSDDException(Messages.getMessage("noValidHeader"));
/*     */         }
/*  89 */         QName headerQName = XMLUtils.getQNameFromString(headerStr, qElem);
/*  90 */         if (headerQName != null)
/*  91 */           headerList.add(headerQName);
/*     */       }
/*  93 */       QName[] headers = new QName[headerList.size()];
/*  94 */       this._headers = ((QName[])headerList.toArray(headers));
/*     */     }
/*     */   }
/*     */ 
/*     */   protected QName getElementName()
/*     */   {
/* 100 */     return QNAME_JAXRPC_HANDLERINFO;
/*     */   }
/*     */ 
/*     */   public String getHandlerClassName() {
/* 104 */     return this._classname;
/*     */   }
/*     */ 
/*     */   public void setHandlerClassName(String classname) {
/* 108 */     this._classname = classname;
/*     */   }
/*     */ 
/*     */   public Map getHandlerMap() {
/* 112 */     return this._map;
/*     */   }
/*     */ 
/*     */   public void setHandlerMap(Map map) {
/* 116 */     this._map = map;
/*     */   }
/*     */ 
/*     */   public QName[] getHeaders() {
/* 120 */     return this._headers;
/*     */   }
/*     */ 
/*     */   public void setHeaders(QName[] headers) {
/* 124 */     this._headers = headers;
/*     */   }
/*     */ 
/*     */   public void writeToContext(SerializationContext context)
/*     */     throws IOException
/*     */   {
/* 130 */     AttributesImpl attrs = new AttributesImpl();
/* 131 */     attrs.addAttribute("", "classname", "classname", "CDATA", this._classname);
/*     */ 
/* 133 */     context.startElement(WSDDConstants.QNAME_JAXRPC_HANDLERINFO, attrs);
/*     */ 
/* 135 */     Map ht = this._map;
/* 136 */     if (ht != null) {
/* 137 */       Set keys = ht.keySet();
/* 138 */       Iterator iter = keys.iterator();
/* 139 */       while (iter.hasNext()) {
/* 140 */         String name = (String)iter.next();
/* 141 */         String value = (String)ht.get(name);
/* 142 */         attrs = new AttributesImpl();
/* 143 */         attrs.addAttribute("", "name", "name", "CDATA", name);
/* 144 */         attrs.addAttribute("", "value", "value", "CDATA", value);
/* 145 */         context.startElement(WSDDConstants.QNAME_PARAM, attrs);
/* 146 */         context.endElement();
/*     */       }
/*     */     }
/*     */ 
/* 150 */     if (this._headers != null) {
/* 151 */       for (int i = 0; i < this._headers.length; i++) {
/* 152 */         QName qname = this._headers[i];
/* 153 */         attrs = new AttributesImpl();
/* 154 */         attrs.addAttribute("", "qname", "qname", "CDATA", context.qName2String(qname));
/* 155 */         context.startElement(WSDDConstants.QNAME_JAXRPC_HEADER, attrs);
/* 156 */         context.endElement();
/*     */       }
/*     */     }
/*     */ 
/* 160 */     context.endElement();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.WSDDJAXRPCHandlerInfo
 * JD-Core Version:    0.6.0
 */