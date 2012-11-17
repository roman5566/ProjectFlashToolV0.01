/*     */ package org.apache.axis.encoding.ser;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.rmi.RemoteException;
/*     */ import java.util.List;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.description.ElementDesc;
/*     */ import org.apache.axis.description.FieldDesc;
/*     */ import org.apache.axis.description.TypeDesc;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.encoding.Serializer;
/*     */ import org.apache.axis.message.MessageElement;
/*     */ import org.apache.axis.utils.BeanPropertyDescriptor;
/*     */ import org.apache.axis.utils.BeanUtils;
/*     */ import org.apache.axis.utils.FieldPropertyDescriptor;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.fromJava.Namespaces;
/*     */ import org.apache.axis.wsdl.fromJava.Types;
/*     */ import org.apache.axis.wsdl.symbolTable.SchemaUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Element;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.helpers.AttributesImpl;
/*     */ 
/*     */ public class BeanSerializer
/*     */   implements Serializer, Serializable
/*     */ {
/*  57 */   protected static Log log = LogFactory.getLog(BeanSerializer.class.getName());
/*     */ 
/*  60 */   private static final QName MUST_UNDERSTAND_QNAME = new QName("http://schemas.xmlsoap.org/soap/envelope/", "mustUnderstand");
/*     */ 
/*  62 */   private static final Object[] ZERO_ARGS = { "0" };
/*     */   QName xmlType;
/*     */   Class javaType;
/*  68 */   protected BeanPropertyDescriptor[] propertyDescriptor = null;
/*  69 */   protected TypeDesc typeDesc = null;
/*     */ 
/*     */   public BeanSerializer(Class javaType, QName xmlType)
/*     */   {
/*  73 */     this(javaType, xmlType, TypeDesc.getTypeDescForClass(javaType));
/*     */   }
/*     */ 
/*     */   public BeanSerializer(Class javaType, QName xmlType, TypeDesc typeDesc)
/*     */   {
/*  78 */     this(javaType, xmlType, typeDesc, null);
/*     */ 
/*  80 */     if (typeDesc != null)
/*  81 */       this.propertyDescriptor = typeDesc.getPropertyDescriptors();
/*     */     else
/*  83 */       this.propertyDescriptor = BeanUtils.getPd(javaType, null);
/*     */   }
/*     */ 
/*     */   public BeanSerializer(Class javaType, QName xmlType, TypeDesc typeDesc, BeanPropertyDescriptor[] propertyDescriptor)
/*     */   {
/*  90 */     this.xmlType = xmlType;
/*  91 */     this.javaType = javaType;
/*  92 */     this.typeDesc = typeDesc;
/*  93 */     this.propertyDescriptor = propertyDescriptor;
/*     */   }
/*     */ 
/*     */   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context)
/*     */     throws IOException
/*     */   {
/* 110 */     Attributes beanAttrs = getObjectAttributes(value, attributes, context);
/*     */ 
/* 113 */     boolean isEncoded = context.isEncoded();
/*     */ 
/* 116 */     boolean suppressElement = (!isEncoded) && (name.getNamespaceURI().equals("")) && (name.getLocalPart().equals("any"));
/*     */ 
/* 120 */     if (!suppressElement) {
/* 121 */       context.startElement(name, beanAttrs);
/*     */     }
/*     */ 
/* 124 */     if ((value != null) && (value.getClass().isArray())) {
/* 125 */       Object newVal = JavaUtils.convert(value, this.javaType);
/* 126 */       if ((newVal != null) && (this.javaType.isAssignableFrom(newVal.getClass()))) {
/* 127 */         value = newVal;
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 132 */       for (int i = 0; i < this.propertyDescriptor.length; i++) {
/* 133 */         String propName = this.propertyDescriptor[i].getName();
/* 134 */         if (propName.equals("class"))
/*     */           continue;
/* 136 */         QName qname = null;
/* 137 */         QName xmlType = null;
/* 138 */         Class javaType = this.propertyDescriptor[i].getType();
/*     */ 
/* 140 */         boolean isOmittable = false;
/*     */ 
/* 142 */         boolean isNillable = Types.isNullable(javaType);
/*     */ 
/* 144 */         boolean isArray = false;
/* 145 */         QName itemQName = null;
/*     */ 
/* 151 */         if (this.typeDesc != null) {
/* 152 */           FieldDesc field = this.typeDesc.getFieldByName(propName);
/* 153 */           if (field != null) {
/* 154 */             if (!field.isElement())
/*     */             {
/*     */               continue;
/*     */             }
/* 158 */             ElementDesc element = (ElementDesc)field;
/*     */ 
/* 163 */             if (isEncoded)
/* 164 */               qname = new QName(element.getXmlName().getLocalPart());
/*     */             else {
/* 166 */               qname = element.getXmlName();
/*     */             }
/* 168 */             isOmittable = element.isMinOccursZero();
/* 169 */             isNillable = element.isNillable();
/* 170 */             isArray = element.isMaxOccursUnbounded();
/* 171 */             xmlType = element.getXmlType();
/* 172 */             itemQName = element.getItemQName();
/* 173 */             context.setItemQName(itemQName);
/*     */           }
/*     */         }
/*     */ 
/* 177 */         if (qname == null) {
/* 178 */           qname = new QName(isEncoded ? "" : name.getNamespaceURI(), propName);
/*     */         }
/*     */ 
/* 182 */         if (xmlType == null)
/*     */         {
/* 184 */           xmlType = context.getQNameForClass(javaType);
/*     */         }
/*     */ 
/* 188 */         if (this.propertyDescriptor[i].isReadable()) {
/* 189 */           if ((itemQName != null) || ((!this.propertyDescriptor[i].isIndexed()) && (!isArray)))
/*     */           {
/* 192 */             Object propValue = this.propertyDescriptor[i].get(value);
/*     */ 
/* 196 */             if (propValue == null)
/*     */             {
/* 199 */               if ((!isNillable) && (!isOmittable)) {
/* 200 */                 if (Number.class.isAssignableFrom(javaType))
/*     */                 {
/*     */                   try
/*     */                   {
/* 205 */                     Constructor constructor = javaType.getConstructor(SimpleDeserializer.STRING_CLASS);
/*     */ 
/* 208 */                     propValue = constructor.newInstance(ZERO_ARGS);
/*     */                   }
/*     */                   catch (Exception e)
/*     */                   {
/*     */                   }
/*     */                 }
/* 214 */                 if (propValue == null) {
/* 215 */                   throw new IOException(Messages.getMessage("nullNonNillableElement", propName));
/*     */                 }
/*     */ 
/*     */               }
/*     */ 
/* 225 */               if ((isOmittable) && (!isEncoded))
/*     */               {
/*     */                 continue;
/*     */               }
/*     */             }
/* 230 */             context.serialize(qname, null, propValue, xmlType, javaType);
/*     */           }
/*     */           else
/*     */           {
/* 236 */             int j = 0;
/* 237 */             while (j >= 0) {
/* 238 */               Object propValue = null;
/*     */               try {
/* 240 */                 propValue = this.propertyDescriptor[i].get(value, j);
/*     */ 
/* 242 */                 j++;
/*     */               } catch (Exception e) {
/* 244 */                 j = -1;
/*     */               }
/* 246 */               if (j >= 0) {
/* 247 */                 context.serialize(qname, null, propValue, xmlType, this.propertyDescriptor[i].getType());
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 255 */       BeanPropertyDescriptor anyDesc = this.typeDesc == null ? null : this.typeDesc.getAnyDesc();
/*     */ 
/* 257 */       if (anyDesc != null)
/*     */       {
/* 260 */         Object anyVal = anyDesc.get(value);
/* 261 */         if ((anyVal != null) && ((anyVal instanceof MessageElement[]))) {
/* 262 */           MessageElement[] anyContent = (MessageElement[])anyVal;
/* 263 */           for (int i = 0; i < anyContent.length; i++) {
/* 264 */             MessageElement element = anyContent[i];
/* 265 */             element.output(context);
/*     */           }
/*     */         }
/*     */       }
/*     */     } catch (InvocationTargetException ite) {
/* 270 */       Throwable target = ite.getTargetException();
/* 271 */       log.error(Messages.getMessage("exception00"), target);
/* 272 */       throw new IOException(target.toString());
/*     */     } catch (Exception e) {
/* 274 */       log.error(Messages.getMessage("exception00"), e);
/* 275 */       throw new IOException(e.toString());
/*     */     }
/*     */ 
/* 278 */     if (!suppressElement)
/* 279 */       context.endElement();
/*     */   }
/*     */ 
/*     */   public String getMechanismType()
/*     */   {
/* 284 */     return "Axis SAX Mechanism";
/*     */   }
/*     */ 
/*     */   public Element writeSchema(Class javaType, Types types)
/*     */     throws Exception
/*     */   {
/* 300 */     Element complexType = types.createElement("complexType");
/*     */ 
/* 303 */     Element e = null;
/* 304 */     Class superClass = javaType.getSuperclass();
/* 305 */     BeanPropertyDescriptor[] superPd = null;
/* 306 */     List stopClasses = types.getStopClasses();
/* 307 */     if ((superClass != null) && (superClass != Object.class) && (superClass != Exception.class) && (superClass != Throwable.class) && (superClass != RuntimeException.class) && (superClass != RemoteException.class) && (superClass != AxisFault.class) && ((stopClasses == null) || (!stopClasses.contains(superClass.getName()))))
/*     */     {
/* 317 */       String base = types.writeType(superClass);
/* 318 */       Element complexContent = types.createElement("complexContent");
/* 319 */       complexType.appendChild(complexContent);
/* 320 */       Element extension = types.createElement("extension");
/* 321 */       complexContent.appendChild(extension);
/* 322 */       extension.setAttribute("base", base);
/* 323 */       e = extension;
/*     */ 
/* 325 */       TypeDesc superTypeDesc = TypeDesc.getTypeDescForClass(superClass);
/* 326 */       if (superTypeDesc != null)
/* 327 */         superPd = superTypeDesc.getPropertyDescriptors();
/*     */       else
/* 329 */         superPd = BeanUtils.getPd(superClass, null);
/*     */     }
/*     */     else {
/* 332 */       e = complexType;
/*     */     }
/*     */ 
/* 342 */     Element all = types.createElement("sequence");
/* 343 */     e.appendChild(all);
/*     */ 
/* 345 */     if (Modifier.isAbstract(javaType.getModifiers())) {
/* 346 */       complexType.setAttribute("abstract", "true");
/*     */     }
/*     */ 
/* 350 */     for (int i = 0; i < this.propertyDescriptor.length; i++) {
/* 351 */       String propName = this.propertyDescriptor[i].getName();
/*     */ 
/* 354 */       boolean writeProperty = true;
/* 355 */       if (propName.equals("class")) {
/* 356 */         writeProperty = false;
/*     */       }
/*     */ 
/* 361 */       if ((superPd != null) && (writeProperty)) {
/* 362 */         for (int j = 0; (j < superPd.length) && (writeProperty); j++) {
/* 363 */           if (propName.equals(superPd[j].getName())) {
/* 364 */             writeProperty = false;
/*     */           }
/*     */         }
/*     */       }
/* 368 */       if (!writeProperty)
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/* 377 */       if (this.typeDesc != null) {
/* 378 */         Class fieldType = this.propertyDescriptor[i].getType();
/* 379 */         FieldDesc field = this.typeDesc.getFieldByName(propName);
/*     */ 
/* 381 */         if (field != null) {
/* 382 */           QName qname = field.getXmlName();
/* 383 */           QName fieldXmlType = field.getXmlType();
/* 384 */           boolean isAnonymous = (fieldXmlType != null) && (fieldXmlType.getLocalPart().startsWith(">"));
/*     */ 
/* 386 */           if (qname != null)
/*     */           {
/* 393 */             propName = qname.getLocalPart();
/*     */           }
/* 395 */           if (!field.isElement()) {
/* 396 */             writeAttribute(types, propName, fieldType, fieldXmlType, complexType);
/*     */           }
/*     */           else
/*     */           {
/* 402 */             writeField(types, propName, fieldXmlType, fieldType, this.propertyDescriptor[i].isIndexed(), field.isMinOccursZero(), all, isAnonymous, ((ElementDesc)field).getItemQName());
/*     */           }
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 412 */           writeField(types, propName, null, fieldType, this.propertyDescriptor[i].isIndexed(), false, all, false, null);
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 419 */         boolean done = false;
/* 420 */         if ((this.propertyDescriptor[i] instanceof FieldPropertyDescriptor)) {
/* 421 */           FieldPropertyDescriptor fpd = (FieldPropertyDescriptor)this.propertyDescriptor[i];
/* 422 */           Class clazz = fpd.getField().getType();
/* 423 */           if (types.getTypeQName(clazz) != null) {
/* 424 */             writeField(types, propName, null, clazz, false, false, all, false, null);
/*     */ 
/* 430 */             done = true;
/*     */           }
/*     */         }
/* 433 */         if (!done) {
/* 434 */           writeField(types, propName, null, this.propertyDescriptor[i].getType(), this.propertyDescriptor[i].isIndexed(), false, all, false, null);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 445 */     return complexType;
/*     */   }
/*     */ 
/*     */   protected void writeField(Types types, String fieldName, QName xmlType, Class fieldType, boolean isUnbounded, boolean isOmittable, Element where, boolean isAnonymous, QName itemQName)
/*     */     throws Exception
/*     */   {
/* 469 */     String elementType = null;
/*     */     Element elem;
/*     */     Element elem;
/* 471 */     if (isAnonymous) {
/* 472 */       elem = types.createElementWithAnonymousType(fieldName, fieldType, isOmittable, where.getOwnerDocument());
/*     */     }
/*     */     else
/*     */     {
/* 478 */       if ((!SchemaUtils.isSimpleSchemaType(xmlType)) && (Types.isArray(fieldType)))
/*     */       {
/* 480 */         xmlType = null;
/*     */       }
/*     */ 
/* 483 */       if ((itemQName != null) && (SchemaUtils.isSimpleSchemaType(xmlType)) && (Types.isArray(fieldType)))
/*     */       {
/* 486 */         xmlType = null;
/*     */       }
/*     */ 
/* 489 */       QName typeQName = types.writeTypeAndSubTypeForPart(fieldType, xmlType);
/* 490 */       elementType = types.getQNameString(typeQName);
/*     */ 
/* 492 */       if (elementType == null)
/*     */       {
/* 494 */         QName anyQN = Constants.XSD_ANYTYPE;
/* 495 */         String prefix = types.getNamespaces().getCreatePrefix(anyQN.getNamespaceURI());
/*     */ 
/* 497 */         elementType = prefix + ":" + anyQN.getLocalPart();
/*     */       }
/*     */ 
/* 501 */       boolean isNillable = Types.isNullable(fieldType);
/* 502 */       if (this.typeDesc != null) {
/* 503 */         FieldDesc field = this.typeDesc.getFieldByName(fieldName);
/* 504 */         if ((field != null) && (field.isElement())) {
/* 505 */           isNillable = ((ElementDesc)field).isNillable();
/*     */         }
/*     */       }
/*     */ 
/* 509 */       elem = types.createElement(fieldName, elementType, isNillable, isOmittable, where.getOwnerDocument());
/*     */     }
/*     */ 
/* 516 */     if (isUnbounded) {
/* 517 */       elem.setAttribute("maxOccurs", "unbounded");
/*     */     }
/*     */ 
/* 520 */     where.appendChild(elem);
/*     */   }
/*     */ 
/*     */   protected void writeAttribute(Types types, String fieldName, Class fieldType, QName fieldXmlType, Element where)
/*     */     throws Exception
/*     */   {
/* 537 */     if (!types.isAcceptableAsAttribute(fieldType)) {
/* 538 */       throw new AxisFault(Messages.getMessage("AttrNotSimpleType00", fieldName, fieldType.getName()));
/*     */     }
/*     */ 
/* 542 */     Element elem = types.createAttributeElement(fieldName, fieldType, fieldXmlType, false, where.getOwnerDocument());
/*     */ 
/* 546 */     where.appendChild(elem);
/*     */   }
/*     */ 
/*     */   protected Attributes getObjectAttributes(Object value, Attributes attributes, SerializationContext context)
/*     */   {
/* 561 */     if ((this.typeDesc == null) || (!this.typeDesc.hasAttributes()))
/* 562 */       return attributes;
/*     */     AttributesImpl attrs;
/*     */     AttributesImpl attrs;
/* 565 */     if (attributes == null) {
/* 566 */       attrs = new AttributesImpl();
/*     */     }
/*     */     else
/*     */     {
/*     */       AttributesImpl attrs;
/* 567 */       if ((attributes instanceof AttributesImpl))
/* 568 */         attrs = (AttributesImpl)attributes;
/*     */       else {
/* 570 */         attrs = new AttributesImpl(attributes);
/*     */       }
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 576 */       for (int i = 0; i < this.propertyDescriptor.length; i++) {
/* 577 */         String propName = this.propertyDescriptor[i].getName();
/* 578 */         if (propName.equals("class")) {
/*     */           continue;
/*     */         }
/* 581 */         FieldDesc field = this.typeDesc.getFieldByName(propName);
/*     */ 
/* 583 */         if ((field == null) || (field.isElement())) {
/*     */           continue;
/*     */         }
/* 586 */         QName qname = field.getXmlName();
/* 587 */         if (qname == null) {
/* 588 */           qname = new QName("", propName);
/*     */         }
/*     */ 
/* 591 */         if ((!this.propertyDescriptor[i].isReadable()) || (this.propertyDescriptor[i].isIndexed())) {
/*     */           continue;
/*     */         }
/* 594 */         Object propValue = this.propertyDescriptor[i].get(value);
/*     */ 
/* 596 */         if (qname.equals(MUST_UNDERSTAND_QNAME)) {
/* 597 */           if (propValue.equals(Boolean.TRUE))
/* 598 */             propValue = "1";
/* 599 */           else if (propValue.equals(Boolean.FALSE)) {
/* 600 */             propValue = "0";
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 607 */         if (propValue != null) {
/* 608 */           setAttributeProperty(propValue, qname, field.getXmlType(), field.getJavaType(), attrs, context);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 619 */       return attrs;
/*     */     }
/*     */ 
/* 622 */     return attrs;
/*     */   }
/*     */ 
/*     */   private void setAttributeProperty(Object propValue, QName qname, QName xmlType, Class javaType, AttributesImpl attrs, SerializationContext context)
/*     */     throws Exception
/*     */   {
/* 632 */     String namespace = qname.getNamespaceURI();
/* 633 */     String localName = qname.getLocalPart();
/*     */ 
/* 640 */     if (attrs.getIndex(namespace, localName) != -1) {
/* 641 */       return;
/*     */     }
/*     */ 
/* 644 */     String propString = context.getValueAsString(propValue, xmlType, javaType);
/*     */ 
/* 646 */     attrs.addAttribute(namespace, localName, context.attributeQName2String(qname), "CDATA", propString);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.BeanSerializer
 * JD-Core Version:    0.6.0
 */