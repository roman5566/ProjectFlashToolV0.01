/*     */ package org.apache.axis.encoding.ser;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.constants.Use;
/*     */ import org.apache.axis.description.ServiceDesc;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.encoding.Serializer;
/*     */ import org.apache.axis.encoding.SerializerFactory;
/*     */ import org.apache.axis.encoding.TypeMapping;
/*     */ import org.apache.axis.schema.SchemaVersion;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.fromJava.Types;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Element;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.helpers.AttributesImpl;
/*     */ 
/*     */ public class ArraySerializer
/*     */   implements Serializer
/*     */ {
/*  55 */   QName xmlType = null;
/*  56 */   Class javaType = null;
/*  57 */   QName componentType = null;
/*  58 */   QName componentQName = null;
/*     */ 
/*  87 */   protected static Log log = LogFactory.getLog(ArraySerializer.class.getName());
/*     */ 
/*     */   public ArraySerializer(Class javaType, QName xmlType)
/*     */   {
/*  65 */     this.javaType = javaType;
/*  66 */     this.xmlType = xmlType;
/*     */   }
/*     */ 
/*     */   public ArraySerializer(Class javaType, QName xmlType, QName componentType)
/*     */   {
/*  74 */     this(javaType, xmlType);
/*  75 */     this.componentType = componentType;
/*     */   }
/*     */ 
/*     */   public ArraySerializer(Class javaType, QName xmlType, QName componentType, QName componentQName)
/*     */   {
/*  83 */     this(javaType, xmlType, componentType);
/*  84 */     this.componentQName = componentQName;
/*     */   }
/*     */ 
/*     */   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context)
/*     */     throws IOException
/*     */   {
/* 101 */     if (value == null) {
/* 102 */       throw new IOException(Messages.getMessage("cantDoNullArray00"));
/*     */     }
/* 104 */     MessageContext msgContext = context.getMessageContext();
/* 105 */     SchemaVersion schema = SchemaVersion.SCHEMA_2001;
/* 106 */     SOAPConstants soap = SOAPConstants.SOAP11_CONSTANTS;
/* 107 */     boolean encoded = context.isEncoded();
/*     */ 
/* 109 */     if (msgContext != null) {
/* 110 */       schema = msgContext.getSchemaVersion();
/* 111 */       soap = msgContext.getSOAPConstants();
/*     */     }
/*     */ 
/* 114 */     Class cls = value.getClass();
/* 115 */     Collection list = null;
/*     */ 
/* 117 */     if (!cls.isArray()) {
/* 118 */       if (!(value instanceof Collection)) {
/* 119 */         throw new IOException(Messages.getMessage("cantSerialize00", cls.getName()));
/*     */       }
/*     */ 
/* 122 */       list = (Collection)value;
/*     */     }
/*     */     Class componentClass;
/*     */     Class componentClass;
/* 127 */     if (list == null)
/* 128 */       componentClass = cls.getComponentType();
/*     */     else {
/* 130 */       componentClass = Object.class;
/*     */     }
/*     */ 
/* 135 */     QName componentTypeQName = this.componentType;
/*     */ 
/* 144 */     String dims = "";
/*     */ 
/* 146 */     if (componentTypeQName != null)
/*     */     {
/* 149 */       TypeMapping tm = context.getTypeMapping();
/* 150 */       SerializerFactory factory = (SerializerFactory)tm.getSerializer(componentClass, componentTypeQName);
/*     */ 
/* 153 */       while ((componentClass.isArray()) && ((factory instanceof ArraySerializerFactory))) {
/* 154 */         ArraySerializerFactory asf = (ArraySerializerFactory)factory;
/* 155 */         componentClass = componentClass.getComponentType();
/* 156 */         QName componentType = null;
/* 157 */         if (asf.getComponentType() != null) {
/* 158 */           componentType = asf.getComponentType();
/* 159 */           if (encoded) {
/* 160 */             componentTypeQName = componentType;
/*     */           }
/*     */         }
/*     */ 
/* 164 */         factory = (SerializerFactory)tm.getSerializer(componentClass, componentType);
/*     */ 
/* 166 */         if (soap == SOAPConstants.SOAP12_CONSTANTS)
/* 167 */           dims = dims + "* ";
/*     */         else
/* 169 */           dims = dims + "[]";
/*     */       }
/*     */     }
/*     */     else {
/* 173 */       while (componentClass.isArray()) {
/* 174 */         componentClass = componentClass.getComponentType();
/* 175 */         if (soap == SOAPConstants.SOAP12_CONSTANTS) {
/* 176 */           dims = dims + "* "; continue;
/*     */         }
/* 178 */         dims = dims + "[]";
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 183 */     if (componentTypeQName == null) {
/* 184 */       componentTypeQName = context.getCurrentXMLType();
/* 185 */       if ((componentTypeQName != null) && (
/* 186 */         (componentTypeQName.equals(this.xmlType)) || (componentTypeQName.equals(Constants.XSD_ANYTYPE)) || (componentTypeQName.equals(soap.getArrayType()))))
/*     */       {
/* 189 */         componentTypeQName = null;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 194 */     if (componentTypeQName == null) {
/* 195 */       componentTypeQName = context.getItemType();
/*     */     }
/*     */ 
/* 199 */     if (componentTypeQName == null) {
/* 200 */       componentTypeQName = context.getQNameForClass(componentClass);
/*     */     }
/*     */ 
/* 204 */     if (componentTypeQName == null) {
/* 205 */       Class searchCls = componentClass;
/* 206 */       while ((searchCls != null) && (componentTypeQName == null)) {
/* 207 */         searchCls = searchCls.getSuperclass();
/* 208 */         componentTypeQName = context.getQNameForClass(searchCls);
/*     */       }
/* 210 */       if (componentTypeQName != null) {
/* 211 */         componentClass = searchCls;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 216 */     if (componentTypeQName == null) {
/* 217 */       throw new IOException(Messages.getMessage("noType00", componentClass.getName()));
/*     */     }
/*     */ 
/* 221 */     int len = list == null ? Array.getLength(value) : list.size();
/* 222 */     String arrayType = "";
/* 223 */     int dim2Len = -1;
/* 224 */     if (encoded) {
/* 225 */       if (soap == SOAPConstants.SOAP12_CONSTANTS)
/* 226 */         arrayType = dims + len;
/*     */       else {
/* 228 */         arrayType = dims + "[" + len + "]";
/*     */       }
/*     */ 
/* 260 */       boolean enable2Dim = false;
/*     */ 
/* 263 */       if (msgContext != null) {
/* 264 */         enable2Dim = JavaUtils.isTrueExplicitly(msgContext.getProperty("enable2DArrayEncoding"));
/*     */       }
/*     */ 
/* 268 */       if ((enable2Dim) && (!dims.equals("")) && 
/* 269 */         (cls.isArray()) && (len > 0)) {
/* 270 */         boolean okay = true;
/*     */ 
/* 272 */         for (int i = 0; (i < len) && (okay); i++)
/*     */         {
/* 274 */           Object elementValue = Array.get(value, i);
/* 275 */           if (elementValue == null) {
/* 276 */             okay = false;
/* 277 */           } else if (dim2Len < 0) {
/* 278 */             dim2Len = Array.getLength(elementValue);
/* 279 */             if (dim2Len <= 0)
/* 280 */               okay = false;
/*     */           }
/* 282 */           else if (dim2Len != Array.getLength(elementValue)) {
/* 283 */             okay = false;
/*     */           }
/*     */         }
/*     */ 
/* 287 */         if (okay) {
/* 288 */           dims = dims.substring(0, dims.length() - 2);
/* 289 */           if (soap == SOAPConstants.SOAP12_CONSTANTS)
/* 290 */             arrayType = dims + len + " " + dim2Len;
/*     */           else
/* 292 */             arrayType = dims + "[" + len + "," + dim2Len + "]";
/*     */         } else {
/* 294 */           dim2Len = -1;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 304 */     QName itemQName = context.getItemQName();
/* 305 */     boolean maxOccursUsage = (!encoded) && (itemQName == null) && (componentTypeQName.equals(context.getCurrentXMLType()));
/*     */ 
/* 308 */     if (encoded)
/*     */     {
/*     */       AttributesImpl attrs;
/*     */       AttributesImpl attrs;
/* 310 */       if (attributes == null) {
/* 311 */         attrs = new AttributesImpl();
/*     */       }
/*     */       else
/*     */       {
/*     */         AttributesImpl attrs;
/* 312 */         if ((attributes instanceof AttributesImpl))
/* 313 */           attrs = (AttributesImpl)attributes;
/*     */         else {
/* 315 */           attrs = new AttributesImpl(attributes);
/*     */         }
/*     */       }
/* 318 */       String compType = context.attributeQName2String(componentTypeQName);
/*     */ 
/* 320 */       if (attrs.getIndex(soap.getEncodingURI(), soap.getAttrItemType()) == -1) {
/* 321 */         String encprefix = context.getPrefixForURI(soap.getEncodingURI());
/*     */ 
/* 324 */         if (soap != SOAPConstants.SOAP12_CONSTANTS) {
/* 325 */           compType = compType + arrayType;
/*     */ 
/* 327 */           attrs.addAttribute(soap.getEncodingURI(), soap.getAttrItemType(), encprefix + ":arrayType", "CDATA", compType);
/*     */         }
/*     */         else
/*     */         {
/* 334 */           attrs.addAttribute(soap.getEncodingURI(), soap.getAttrItemType(), encprefix + ":itemType", "CDATA", compType);
/*     */ 
/* 340 */           attrs.addAttribute(soap.getEncodingURI(), "arraySize", encprefix + ":arraySize", "CDATA", arrayType);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 362 */       String qname = context.getPrefixForURI(schema.getXsiURI(), "xsi") + ":type";
/*     */       QName soapArray;
/*     */       QName soapArray;
/* 366 */       if (soap == SOAPConstants.SOAP12_CONSTANTS)
/* 367 */         soapArray = Constants.SOAP_ARRAY12;
/*     */       else {
/* 369 */         soapArray = Constants.SOAP_ARRAY;
/*     */       }
/*     */ 
/* 372 */       int typeI = attrs.getIndex(schema.getXsiURI(), "type");
/*     */ 
/* 374 */       if (typeI != -1) {
/* 375 */         attrs.setAttribute(typeI, schema.getXsiURI(), "type", qname, "CDATA", context.qName2String(soapArray));
/*     */       }
/*     */       else
/*     */       {
/* 382 */         attrs.addAttribute(schema.getXsiURI(), "type", qname, "CDATA", context.qName2String(soapArray));
/*     */       }
/*     */ 
/* 389 */       attributes = attrs;
/*     */     }
/*     */ 
/* 395 */     QName elementName = name;
/* 396 */     Attributes serializeAttr = attributes;
/* 397 */     if (!maxOccursUsage) {
/* 398 */       serializeAttr = null;
/* 399 */       context.startElement(name, attributes);
/* 400 */       if (itemQName != null)
/* 401 */         elementName = itemQName;
/* 402 */       else if (this.componentQName != null)
/* 403 */         elementName = this.componentQName;
/*     */     }
/*     */     Iterator iterator;
/* 407 */     if (dim2Len < 0)
/*     */     {
/* 409 */       if (list == null) {
/* 410 */         for (int index = 0; index < len; index++) {
/* 411 */           Object aValue = Array.get(value, index);
/*     */ 
/* 414 */           context.serialize(elementName, serializeAttr == null ? serializeAttr : new AttributesImpl(serializeAttr), aValue, componentTypeQName, componentClass);
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 421 */         for (iterator = list.iterator(); iterator.hasNext(); ) {
/* 422 */           Object aValue = iterator.next();
/*     */ 
/* 425 */           context.serialize(elementName, serializeAttr == null ? serializeAttr : new AttributesImpl(serializeAttr), aValue, componentTypeQName, componentClass);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 434 */       for (int index = 0; index < len; index++) {
/* 435 */         for (int index2 = 0; index2 < dim2Len; index2++) {
/* 436 */           Object aValue = Array.get(Array.get(value, index), index2);
/* 437 */           context.serialize(elementName, null, aValue, componentTypeQName, componentClass);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 442 */     if (!maxOccursUsage)
/* 443 */       context.endElement(); 
/*     */   }
/*     */ 
/*     */   public String getMechanismType() {
/* 446 */     return "Axis SAX Mechanism";
/*     */   }
/*     */ 
/*     */   private static boolean isArray(Class clazz) {
/* 450 */     return (clazz.isArray()) || (Collection.class.isAssignableFrom(clazz));
/*     */   }
/*     */ 
/*     */   private static Class getComponentType(Class clazz)
/*     */   {
/* 455 */     if (clazz.isArray())
/*     */     {
/* 457 */       return clazz.getComponentType();
/*     */     }
/* 459 */     if (Collection.class.isAssignableFrom(clazz))
/*     */     {
/* 461 */       return Object.class;
/*     */     }
/*     */ 
/* 465 */     return null;
/*     */   }
/*     */ 
/*     */   public Element writeSchema(Class javaType, Types types)
/*     */     throws Exception
/*     */   {
/* 482 */     boolean encoded = true;
/* 483 */     MessageContext mc = MessageContext.getCurrentContext();
/* 484 */     if (mc != null)
/* 485 */       encoded = mc.isEncoded();
/*     */     else {
/* 487 */       encoded = types.getServiceDesc().getUse() == Use.ENCODED;
/*     */     }
/*     */ 
/* 490 */     if (!encoded) {
/* 491 */       Class cType = Object.class;
/* 492 */       if (javaType.isArray()) {
/* 493 */         cType = javaType.getComponentType();
/*     */       }
/*     */ 
/* 496 */       String typeName = types.writeType(cType);
/* 497 */       return types.createLiteralArrayElement(typeName, null);
/*     */     }
/*     */ 
/* 501 */     String componentTypeName = null;
/* 502 */     Class componentType = null;
/* 503 */     if (isArray(javaType)) {
/* 504 */       String dimString = "[]";
/* 505 */       componentType = getComponentType(javaType);
/* 506 */       while (isArray(componentType)) {
/* 507 */         dimString = dimString + "[]";
/* 508 */         componentType = getComponentType(componentType);
/*     */       }
/* 510 */       types.writeType(componentType, null);
/*     */ 
/* 512 */       componentTypeName = types.getQNameString(types.getTypeQName(componentType)) + dimString;
/*     */     }
/*     */ 
/* 518 */     return types.createArrayElement(componentTypeName);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.ArraySerializer
 * JD-Core Version:    0.6.0
 */