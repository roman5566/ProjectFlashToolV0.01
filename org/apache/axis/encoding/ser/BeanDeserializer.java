/*     */ package org.apache.axis.encoding.ser;
/*     */ 
/*     */ import java.io.CharArrayWriter;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.Map;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.description.ElementDesc;
/*     */ import org.apache.axis.description.FieldDesc;
/*     */ import org.apache.axis.description.TypeDesc;
/*     */ import org.apache.axis.encoding.ConstructorTarget;
/*     */ import org.apache.axis.encoding.DeserializationContext;
/*     */ import org.apache.axis.encoding.Deserializer;
/*     */ import org.apache.axis.encoding.DeserializerImpl;
/*     */ import org.apache.axis.encoding.Target;
/*     */ import org.apache.axis.encoding.TypeMapping;
/*     */ import org.apache.axis.message.MessageElement;
/*     */ import org.apache.axis.message.SOAPHandler;
/*     */ import org.apache.axis.message.Text;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.apache.axis.utils.BeanPropertyDescriptor;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class BeanDeserializer extends DeserializerImpl
/*     */   implements Serializable
/*     */ {
/*  54 */   protected static Log log = LogFactory.getLog(BeanDeserializer.class.getName());
/*     */ 
/*  57 */   private final CharArrayWriter val = new CharArrayWriter();
/*     */   QName xmlType;
/*     */   Class javaType;
/*  61 */   protected Map propertyMap = null;
/*     */   protected QName prevQName;
/*  67 */   protected Constructor constructorToUse = null;
/*     */ 
/*  72 */   protected Target constructorTarget = null;
/*     */ 
/*  75 */   protected TypeDesc typeDesc = null;
/*     */ 
/*  78 */   protected int collectionIndex = -1;
/*     */ 
/*  80 */   protected SimpleDeserializer cacheStringDSer = null;
/*  81 */   protected QName cacheXMLType = null;
/*     */ 
/*     */   public BeanDeserializer(Class javaType, QName xmlType)
/*     */   {
/*  85 */     this(javaType, xmlType, TypeDesc.getTypeDescForClass(javaType));
/*     */   }
/*     */ 
/*     */   public BeanDeserializer(Class javaType, QName xmlType, TypeDesc typeDesc)
/*     */   {
/*  90 */     this(javaType, xmlType, typeDesc, BeanDeserializerFactory.getProperties(javaType, typeDesc));
/*     */   }
/*     */ 
/*     */   public BeanDeserializer(Class javaType, QName xmlType, TypeDesc typeDesc, Map propertyMap)
/*     */   {
/*  97 */     this.xmlType = xmlType;
/*  98 */     this.javaType = javaType;
/*  99 */     this.typeDesc = typeDesc;
/* 100 */     this.propertyMap = propertyMap;
/*     */     try
/*     */     {
/* 104 */       this.value = javaType.newInstance();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 133 */     if (this.value == null) {
/*     */       try {
/* 135 */         this.value = this.javaType.newInstance();
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 140 */         Constructor[] constructors = this.javaType.getConstructors();
/* 141 */         if (constructors.length > 0) {
/* 142 */           this.constructorToUse = constructors[0];
/*     */         }
/*     */ 
/* 146 */         if (this.constructorToUse == null) {
/* 147 */           throw new SAXException(Messages.getMessage("cantCreateBean00", this.javaType.getName(), e.toString()));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 154 */     super.startElement(namespace, localName, prefix, attributes, context);
/*     */   }
/*     */ 
/*     */   public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 177 */     handleMixedContent();
/*     */ 
/* 179 */     BeanPropertyDescriptor propDesc = null;
/* 180 */     FieldDesc fieldDesc = null;
/*     */ 
/* 182 */     SOAPConstants soapConstants = context.getSOAPConstants();
/* 183 */     String encodingStyle = context.getEncodingStyle();
/* 184 */     boolean isEncoded = Constants.isSOAP_ENC(encodingStyle);
/*     */ 
/* 186 */     QName elemQName = new QName(namespace, localName);
/*     */ 
/* 188 */     if ((this.prevQName == null) || (!this.prevQName.equals(elemQName))) {
/* 189 */       this.collectionIndex = -1;
/*     */     }
/*     */ 
/* 192 */     boolean isArray = false;
/* 193 */     QName itemQName = null;
/* 194 */     if (this.typeDesc != null)
/*     */     {
/* 197 */       String fieldName = this.typeDesc.getFieldNameForElement(elemQName, isEncoded);
/*     */ 
/* 199 */       propDesc = (BeanPropertyDescriptor)this.propertyMap.get(fieldName);
/* 200 */       fieldDesc = this.typeDesc.getFieldByName(fieldName);
/*     */ 
/* 202 */       if (fieldDesc != null) {
/* 203 */         ElementDesc element = (ElementDesc)fieldDesc;
/* 204 */         isArray = element.isMaxOccursUnbounded();
/* 205 */         itemQName = element.getItemQName();
/*     */       }
/*     */     }
/*     */ 
/* 209 */     if (propDesc == null)
/*     */     {
/* 211 */       propDesc = (BeanPropertyDescriptor)this.propertyMap.get(localName);
/*     */     }
/*     */ 
/* 216 */     if ((propDesc == null) || ((this.prevQName != null) && (this.prevQName.equals(elemQName)) && (!propDesc.isIndexed()) && (!isArray) && (getAnyPropertyDesc() != null)))
/*     */     {
/* 222 */       this.prevQName = elemQName;
/* 223 */       propDesc = getAnyPropertyDesc();
/* 224 */       if (propDesc != null) {
/*     */         try {
/* 226 */           MessageElement[] curElements = (MessageElement[])propDesc.get(this.value);
/* 227 */           int length = 0;
/* 228 */           if (curElements != null) {
/* 229 */             length = curElements.length;
/*     */           }
/* 231 */           MessageElement[] newElements = new MessageElement[length + 1];
/* 232 */           if (curElements != null) {
/* 233 */             System.arraycopy(curElements, 0, newElements, 0, length);
/*     */           }
/*     */ 
/* 236 */           MessageElement thisEl = context.getCurElement();
/*     */ 
/* 238 */           newElements[length] = thisEl;
/* 239 */           propDesc.set(this.value, newElements);
/*     */ 
/* 245 */           if (!localName.equals(thisEl.getName())) {
/* 246 */             return new SOAPHandler(newElements, length);
/*     */           }
/* 248 */           return new SOAPHandler();
/*     */         } catch (Exception e) {
/* 250 */           throw new SAXException(e);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 256 */     if (propDesc == null)
/*     */     {
/* 258 */       throw new SAXException(Messages.getMessage("badElem00", this.javaType.getName(), localName));
/*     */     }
/*     */ 
/* 263 */     this.prevQName = elemQName;
/*     */ 
/* 265 */     QName childXMLType = context.getTypeFromAttributes(namespace, localName, attributes);
/*     */ 
/* 269 */     String href = attributes.getValue(soapConstants.getAttrHref());
/* 270 */     Class fieldType = propDesc.getType();
/*     */ 
/* 273 */     if ((childXMLType == null) && (fieldDesc != null) && (href == null)) {
/* 274 */       childXMLType = fieldDesc.getXmlType();
/* 275 */       if (itemQName != null)
/*     */       {
/* 278 */         childXMLType = Constants.SOAP_ARRAY;
/* 279 */         fieldType = propDesc.getActualType();
/*     */       } else {
/* 281 */         childXMLType = fieldDesc.getXmlType();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 286 */     Deserializer dSer = getDeserializer(childXMLType, fieldType, href, context);
/*     */ 
/* 295 */     if (dSer == null) {
/* 296 */       dSer = context.getDeserializerForClass(propDesc.getType());
/*     */     }
/*     */ 
/* 300 */     if (context.isNil(attributes)) {
/* 301 */       if ((propDesc != null) && ((propDesc.isIndexed()) || (isArray)) && (
/* 302 */         (dSer == null) || (!(dSer instanceof ArrayDeserializer)))) {
/* 303 */         this.collectionIndex += 1;
/* 304 */         dSer.registerValueTarget(new BeanPropertyTarget(this.value, propDesc, this.collectionIndex));
/*     */ 
/* 306 */         addChildDeserializer(dSer);
/* 307 */         return (SOAPHandler)dSer;
/*     */       }
/*     */ 
/* 310 */       return null;
/*     */     }
/*     */ 
/* 313 */     if (dSer == null) {
/* 314 */       throw new SAXException(Messages.getMessage("noDeser00", childXMLType.toString()));
/*     */     }
/*     */ 
/* 318 */     if (this.constructorToUse != null) {
/* 319 */       if (this.constructorTarget == null) {
/* 320 */         this.constructorTarget = new ConstructorTarget(this.constructorToUse, this);
/*     */       }
/* 322 */       dSer.registerValueTarget(this.constructorTarget);
/* 323 */     } else if (propDesc.isWriteable())
/*     */     {
/* 335 */       if (((itemQName != null) || (propDesc.isIndexed()) || (isArray)) && (!(dSer instanceof ArrayDeserializer))) {
/* 336 */         this.collectionIndex += 1;
/* 337 */         dSer.registerValueTarget(new BeanPropertyTarget(this.value, propDesc, this.collectionIndex));
/*     */       }
/*     */       else
/*     */       {
/* 343 */         this.collectionIndex = -1;
/* 344 */         dSer.registerValueTarget(new BeanPropertyTarget(this.value, propDesc));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 351 */     addChildDeserializer(dSer);
/*     */ 
/* 353 */     return (SOAPHandler)dSer;
/*     */   }
/*     */ 
/*     */   public BeanPropertyDescriptor getAnyPropertyDesc()
/*     */   {
/* 364 */     if (this.typeDesc == null) {
/* 365 */       return null;
/*     */     }
/* 367 */     return this.typeDesc.getAnyDesc();
/*     */   }
/*     */ 
/*     */   public void onStartElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 390 */     if ((this.value == null) && (this.constructorToUse == null)) {
/*     */       try
/*     */       {
/* 393 */         this.value = this.javaType.newInstance();
/*     */       } catch (Exception e) {
/* 395 */         throw new SAXException(Messages.getMessage("cantCreateBean00", this.javaType.getName(), e.toString()));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 403 */     if (this.typeDesc == null) {
/* 404 */       return;
/*     */     }
/*     */ 
/* 408 */     for (int i = 0; i < attributes.getLength(); i++) {
/* 409 */       QName attrQName = new QName(attributes.getURI(i), attributes.getLocalName(i));
/*     */ 
/* 411 */       String fieldName = this.typeDesc.getFieldNameForAttribute(attrQName);
/* 412 */       if (fieldName == null) {
/*     */         continue;
/*     */       }
/* 415 */       FieldDesc fieldDesc = this.typeDesc.getFieldByName(fieldName);
/*     */ 
/* 418 */       BeanPropertyDescriptor bpd = (BeanPropertyDescriptor)this.propertyMap.get(fieldName);
/*     */ 
/* 420 */       if ((bpd == null) || (
/* 421 */         (this.constructorToUse == null) && (
/* 423 */         (!bpd.isWriteable()) || (bpd.isIndexed()))))
/*     */       {
/*     */         continue;
/*     */       }
/* 427 */       Deserializer dSer = getDeserializer(fieldDesc.getXmlType(), bpd.getType(), null, context);
/*     */ 
/* 431 */       if (dSer == null) {
/* 432 */         dSer = context.getDeserializerForClass(bpd.getType());
/*     */ 
/* 437 */         if ((dSer instanceof ArrayDeserializer))
/*     */         {
/* 439 */           SimpleListDeserializerFactory factory = new SimpleListDeserializerFactory(bpd.getType(), fieldDesc.getXmlType());
/*     */ 
/* 442 */           dSer = (Deserializer)factory.getDeserializerAs(dSer.getMechanismType());
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 447 */       if (dSer == null) {
/* 448 */         throw new SAXException(Messages.getMessage("unregistered00", bpd.getType().toString()));
/*     */       }
/*     */ 
/* 452 */       if (!(dSer instanceof SimpleDeserializer)) {
/* 453 */         throw new SAXException(Messages.getMessage("AttrNotSimpleType00", bpd.getName(), bpd.getType().toString()));
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 461 */         dSer.onStartElement(namespace, localName, prefix, attributes, context);
/*     */ 
/* 463 */         Object val = ((SimpleDeserializer)dSer).makeValue(attributes.getValue(i));
/*     */ 
/* 465 */         if (this.constructorToUse == null) {
/* 466 */           bpd.set(this.value, val);
/*     */         }
/*     */         else {
/* 469 */           if (this.constructorTarget == null) {
/* 470 */             this.constructorTarget = new ConstructorTarget(this.constructorToUse, this);
/*     */           }
/* 472 */           this.constructorTarget.set(val);
/*     */         }
/*     */       } catch (Exception e) {
/* 475 */         throw new SAXException(e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Deserializer getDeserializer(QName xmlType, Class javaType, String href, DeserializationContext context)
/*     */   {
/* 496 */     if (javaType.isArray()) {
/* 497 */       context.setDestinationClass(javaType);
/*     */     }
/*     */ 
/* 500 */     if ((this.cacheStringDSer != null) && 
/* 501 */       (String.class.equals(javaType)) && (href == null) && (
/* 501 */       ((this.cacheXMLType == null) && (xmlType == null)) || ((this.cacheXMLType != null) && (this.cacheXMLType.equals(xmlType)))))
/*     */     {
/* 505 */       this.cacheStringDSer.reset();
/* 506 */       return this.cacheStringDSer;
/*     */     }
/*     */ 
/* 510 */     Deserializer dSer = null;
/*     */ 
/* 512 */     if ((xmlType != null) && (href == null))
/*     */     {
/* 514 */       dSer = context.getDeserializerForType(xmlType);
/*     */     }
/*     */     else {
/* 517 */       TypeMapping tm = context.getTypeMapping();
/* 518 */       QName defaultXMLType = tm.getTypeQName(javaType);
/*     */ 
/* 525 */       if (href == null) {
/* 526 */         dSer = context.getDeserializer(javaType, defaultXMLType);
/*     */       } else {
/* 528 */         dSer = new DeserializerImpl();
/* 529 */         context.setDestinationClass(javaType);
/* 530 */         dSer.setDefaultType(defaultXMLType);
/*     */       }
/*     */     }
/* 533 */     if ((javaType.equals(String.class)) && ((dSer instanceof SimpleDeserializer)))
/*     */     {
/* 535 */       this.cacheStringDSer = ((SimpleDeserializer)dSer);
/* 536 */       this.cacheXMLType = xmlType;
/*     */     }
/* 538 */     return dSer;
/*     */   }
/*     */ 
/*     */   public void characters(char[] chars, int start, int end) throws SAXException {
/* 542 */     this.val.write(chars, start, end);
/*     */   }
/*     */ 
/*     */   public void onEndElement(String namespace, String localName, DeserializationContext context) throws SAXException
/*     */   {
/* 547 */     handleMixedContent();
/*     */   }
/*     */ 
/*     */   protected void handleMixedContent() throws SAXException {
/* 551 */     BeanPropertyDescriptor propDesc = getAnyPropertyDesc();
/* 552 */     if ((propDesc == null) || (this.val.size() == 0)) {
/* 553 */       return;
/*     */     }
/* 555 */     String textValue = this.val.toString().trim();
/* 556 */     this.val.reset();
/* 557 */     if (textValue.length() == 0)
/* 558 */       return;
/*     */     try
/*     */     {
/* 561 */       MessageElement[] curElements = (MessageElement[])propDesc.get(this.value);
/* 562 */       int length = 0;
/* 563 */       if (curElements != null) {
/* 564 */         length = curElements.length;
/*     */       }
/* 566 */       MessageElement[] newElements = new MessageElement[length + 1];
/* 567 */       if (curElements != null) {
/* 568 */         System.arraycopy(curElements, 0, newElements, 0, length);
/*     */       }
/*     */ 
/* 571 */       MessageElement thisEl = new MessageElement(new Text(textValue));
/* 572 */       newElements[length] = thisEl;
/* 573 */       propDesc.set(this.value, newElements);
/*     */     } catch (Exception e) {
/* 575 */       throw new SAXException(e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.BeanDeserializer
 * JD-Core Version:    0.6.0
 */