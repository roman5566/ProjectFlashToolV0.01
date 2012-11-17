/*     */ package org.apache.axis.deployment.wsdd;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.utils.ClassUtils;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.w3c.dom.Attr;
/*     */ import org.w3c.dom.Element;
/*     */ import org.xml.sax.helpers.AttributesImpl;
/*     */ 
/*     */ public class WSDDTypeMapping extends WSDDElement
/*     */ {
/*  38 */   protected QName qname = null;
/*  39 */   protected String serializer = null;
/*  40 */   protected String deserializer = null;
/*  41 */   protected QName typeQName = null;
/*  42 */   protected String ref = null;
/*  43 */   protected String encodingStyle = null;
/*     */ 
/*     */   public WSDDTypeMapping()
/*     */   {
/*     */   }
/*     */ 
/*     */   public WSDDTypeMapping(Element e)
/*     */     throws WSDDException
/*     */   {
/*  61 */     this.serializer = e.getAttribute("serializer");
/*  62 */     this.deserializer = e.getAttribute("deserializer");
/*  63 */     Attr attrNode = e.getAttributeNode("encodingStyle");
/*     */ 
/*  65 */     if (attrNode == null)
/*  66 */       this.encodingStyle = Constants.URI_DEFAULT_SOAP_ENC;
/*     */     else {
/*  68 */       this.encodingStyle = attrNode.getValue();
/*     */     }
/*     */ 
/*  71 */     String qnameStr = e.getAttribute("qname");
/*  72 */     this.qname = XMLUtils.getQNameFromString(qnameStr, e);
/*     */ 
/*  76 */     String typeStr = e.getAttribute("type");
/*  77 */     this.typeQName = XMLUtils.getQNameFromString(typeStr, e);
/*  78 */     if ((typeStr == null) || (typeStr.equals(""))) {
/*  79 */       typeStr = e.getAttribute("languageSpecificType");
/*  80 */       this.typeQName = XMLUtils.getQNameFromString(typeStr, e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeToContext(SerializationContext context)
/*     */     throws IOException
/*     */   {
/*  89 */     AttributesImpl attrs = new AttributesImpl();
/*  90 */     attrs.addAttribute("", "encodingStyle", "encodingStyle", "CDATA", this.encodingStyle);
/*  91 */     attrs.addAttribute("", "serializer", "serializer", "CDATA", this.serializer);
/*  92 */     attrs.addAttribute("", "deserializer", "deserializer", "CDATA", this.deserializer);
/*     */ 
/*  94 */     String typeStr = context.qName2String(this.typeQName);
/*     */ 
/*  96 */     attrs.addAttribute("", "type", "type", "CDATA", typeStr);
/*     */ 
/*  99 */     String qnameStr = context.attributeQName2String(this.qname);
/* 100 */     attrs.addAttribute("", "qname", "qname", "CDATA", qnameStr);
/*     */ 
/* 102 */     context.startElement(QNAME_TYPEMAPPING, attrs);
/* 103 */     context.endElement();
/*     */   }
/*     */ 
/*     */   protected QName getElementName() {
/* 107 */     return QNAME_TYPEMAPPING;
/*     */   }
/*     */ 
/*     */   public String getRef()
/*     */   {
/* 116 */     return this.ref;
/*     */   }
/*     */ 
/*     */   public void setRef(String ref)
/*     */   {
/* 125 */     this.ref = ref;
/*     */   }
/*     */ 
/*     */   public String getEncodingStyle()
/*     */   {
/* 134 */     return this.encodingStyle;
/*     */   }
/*     */ 
/*     */   public void setEncodingStyle(String es)
/*     */   {
/* 143 */     this.encodingStyle = es;
/*     */   }
/*     */ 
/*     */   public QName getQName()
/*     */   {
/* 152 */     return this.qname;
/*     */   }
/*     */ 
/*     */   public void setQName(QName name)
/*     */   {
/* 161 */     this.qname = name;
/*     */   }
/*     */ 
/*     */   public Class getLanguageSpecificType()
/*     */     throws ClassNotFoundException
/*     */   {
/* 172 */     if (this.typeQName != null) {
/* 173 */       if (!"http://xml.apache.org/axis/wsdd/providers/java".equals(this.typeQName.getNamespaceURI())) {
/* 174 */         throw new ClassNotFoundException(Messages.getMessage("badTypeNamespace00", this.typeQName.getNamespaceURI(), "http://xml.apache.org/axis/wsdd/providers/java"));
/*     */       }
/*     */ 
/* 178 */       String loadName = JavaUtils.getLoadableClassName(this.typeQName.getLocalPart());
/* 179 */       if (JavaUtils.getWrapper(loadName) != null) {
/* 180 */         Class cls = JavaUtils.getPrimitiveClassFromName(loadName);
/* 181 */         return cls;
/*     */       }
/* 183 */       return ClassUtils.forName(loadName);
/*     */     }
/*     */ 
/* 186 */     throw new ClassNotFoundException(Messages.getMessage("noTypeQName00"));
/*     */   }
/*     */ 
/*     */   public void setLanguageSpecificType(Class javaType)
/*     */   {
/* 195 */     String type = javaType.getName();
/* 196 */     this.typeQName = new QName("http://xml.apache.org/axis/wsdd/providers/java", type);
/*     */   }
/*     */ 
/*     */   public void setLanguageSpecificType(String javaType)
/*     */   {
/* 206 */     this.typeQName = new QName("http://xml.apache.org/axis/wsdd/providers/java", javaType);
/*     */   }
/*     */ 
/*     */   public Class getSerializer()
/*     */     throws ClassNotFoundException
/*     */   {
/* 217 */     return ClassUtils.forName(this.serializer);
/*     */   }
/*     */ 
/*     */   public String getSerializerName()
/*     */   {
/* 226 */     return this.serializer;
/*     */   }
/*     */ 
/*     */   public void setSerializer(Class ser)
/*     */   {
/* 234 */     this.serializer = ser.getName();
/*     */   }
/*     */ 
/*     */   public void setSerializer(String ser)
/*     */   {
/* 243 */     this.serializer = ser;
/*     */   }
/*     */ 
/*     */   public Class getDeserializer()
/*     */     throws ClassNotFoundException
/*     */   {
/* 254 */     return ClassUtils.forName(this.deserializer);
/*     */   }
/*     */ 
/*     */   public String getDeserializerName()
/*     */   {
/* 263 */     return this.deserializer;
/*     */   }
/*     */ 
/*     */   public void setDeserializer(Class deser)
/*     */   {
/* 272 */     this.deserializer = deser.getName();
/*     */   }
/*     */ 
/*     */   public void setDeserializer(String deser)
/*     */   {
/* 281 */     this.deserializer = deser;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.WSDDTypeMapping
 * JD-Core Version:    0.6.0
 */