/*     */ package org.apache.axis.encoding.ser;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Array;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.description.FieldDesc;
/*     */ import org.apache.axis.description.TypeDesc;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.encoding.SimpleType;
/*     */ import org.apache.axis.encoding.SimpleValueSerializer;
/*     */ import org.apache.axis.utils.BeanPropertyDescriptor;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.fromJava.Types;
/*     */ import org.w3c.dom.Element;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.helpers.AttributesImpl;
/*     */ 
/*     */ public class SimpleListSerializer
/*     */   implements SimpleValueSerializer
/*     */ {
/*     */   public QName xmlType;
/*     */   public Class javaType;
/*  50 */   private BeanPropertyDescriptor[] propertyDescriptor = null;
/*  51 */   private TypeDesc typeDesc = null;
/*     */ 
/*     */   public SimpleListSerializer(Class javaType, QName xmlType) {
/*  54 */     this.xmlType = xmlType;
/*  55 */     this.javaType = javaType;
/*     */   }
/*     */   public SimpleListSerializer(Class javaType, QName xmlType, TypeDesc typeDesc) {
/*  58 */     this.xmlType = xmlType;
/*  59 */     this.javaType = javaType;
/*  60 */     this.typeDesc = typeDesc;
/*     */   }
/*     */ 
/*     */   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context)
/*     */     throws IOException
/*     */   {
/*  71 */     if ((value != null) && (value.getClass() == Object.class)) {
/*  72 */       throw new IOException(Messages.getMessage("cantSerialize02"));
/*     */     }
/*     */ 
/*  76 */     if ((value instanceof SimpleType)) {
/*  77 */       attributes = getObjectAttributes(value, attributes, context);
/*     */     }
/*  79 */     String strValue = null;
/*  80 */     if (value != null) {
/*  81 */       strValue = getValueAsString(value, context);
/*     */     }
/*  83 */     context.startElement(name, attributes);
/*  84 */     if (strValue != null) {
/*  85 */       context.writeSafeString(strValue);
/*     */     }
/*  87 */     context.endElement();
/*     */   }
/*     */ 
/*     */   public String getValueAsString(Object value, SerializationContext context)
/*     */   {
/*  95 */     int length = Array.getLength(value);
/*  96 */     StringBuffer result = new StringBuffer();
/*  97 */     for (int i = 0; i < length; i++) {
/*  98 */       Object object = Array.get(value, i);
/*  99 */       if (((object instanceof Float)) || ((object instanceof Double)))
/*     */       {
/* 101 */         double data = 0.0D;
/* 102 */         if ((object instanceof Float))
/* 103 */           data = ((Float)object).doubleValue();
/*     */         else {
/* 105 */           data = ((Double)object).doubleValue();
/*     */         }
/* 107 */         if (Double.isNaN(data))
/* 108 */           result.append("NaN");
/* 109 */         else if (data == (1.0D / 0.0D))
/* 110 */           result.append("INF");
/* 111 */         else if (data == (-1.0D / 0.0D)) {
/* 112 */           result.append("-INF");
/*     */         }
/*     */         else {
/* 115 */           result.append(object.toString());
/*     */         }
/*     */       }
/* 118 */       else if ((object instanceof QName)) {
/* 119 */         result.append(QNameSerializer.qName2String((QName)object, context));
/*     */       }
/*     */       else {
/* 122 */         result.append(object.toString());
/*     */       }
/* 124 */       if (i < length - 1) {
/* 125 */         result.append(' ');
/*     */       }
/*     */     }
/* 128 */     return result.toString();
/*     */   }
/*     */ 
/*     */   private Attributes getObjectAttributes(Object value, Attributes attributes, SerializationContext context)
/*     */   {
/* 134 */     if ((this.typeDesc == null) || (!this.typeDesc.hasAttributes()))
/* 135 */       return attributes;
/*     */     AttributesImpl attrs;
/*     */     AttributesImpl attrs;
/* 138 */     if (attributes == null) {
/* 139 */       attrs = new AttributesImpl();
/*     */     }
/*     */     else
/*     */     {
/*     */       AttributesImpl attrs;
/* 140 */       if ((attributes instanceof AttributesImpl))
/* 141 */         attrs = (AttributesImpl)attributes;
/*     */       else {
/* 143 */         attrs = new AttributesImpl(attributes);
/*     */       }
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 149 */       for (int i = 0; i < this.propertyDescriptor.length; i++) {
/* 150 */         String propName = this.propertyDescriptor[i].getName();
/* 151 */         if (propName.equals("class")) {
/*     */           continue;
/*     */         }
/* 154 */         FieldDesc field = this.typeDesc.getFieldByName(propName);
/*     */ 
/* 156 */         if ((field == null) || (field.isElement())) {
/*     */           continue;
/*     */         }
/* 159 */         QName qname = field.getXmlName();
/* 160 */         if (qname == null) {
/* 161 */           qname = new QName("", propName);
/*     */         }
/*     */ 
/* 164 */         if ((!this.propertyDescriptor[i].isReadable()) || (this.propertyDescriptor[i].isIndexed())) {
/*     */           continue;
/*     */         }
/* 167 */         Object propValue = this.propertyDescriptor[i].get(value);
/*     */ 
/* 172 */         if (propValue != null) {
/* 173 */           String propString = getValueAsString(propValue, context);
/*     */ 
/* 175 */           String namespace = qname.getNamespaceURI();
/* 176 */           String localName = qname.getLocalPart();
/*     */ 
/* 178 */           attrs.addAttribute(namespace, localName, context.qName2String(qname), "CDATA", propString);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 188 */       return attrs;
/*     */     }
/*     */ 
/* 191 */     return attrs;
/*     */   }
/*     */   public String getMechanismType() {
/* 194 */     return "Axis SAX Mechanism";
/*     */   }
/*     */ 
/*     */   public Element writeSchema(Class javaType, Types types)
/*     */     throws Exception
/*     */   {
/* 209 */     if (!SimpleType.class.isAssignableFrom(javaType)) {
/* 210 */       return null;
/*     */     }
/*     */ 
/* 213 */     Element complexType = types.createElement("complexType");
/* 214 */     types.writeSchemaElementDecl(this.xmlType, complexType);
/* 215 */     complexType.setAttribute("name", this.xmlType.getLocalPart());
/*     */ 
/* 218 */     Element simpleContent = types.createElement("simpleContent");
/* 219 */     complexType.appendChild(simpleContent);
/* 220 */     Element extension = types.createElement("extension");
/* 221 */     simpleContent.appendChild(extension);
/*     */ 
/* 224 */     String base = "string";
/* 225 */     for (int i = 0; i < this.propertyDescriptor.length; i++) {
/* 226 */       String propName = this.propertyDescriptor[i].getName();
/* 227 */       if (!propName.equals("value")) {
/* 228 */         if (this.typeDesc != null) {
/* 229 */           FieldDesc field = this.typeDesc.getFieldByName(propName);
/* 230 */           if (field != null) {
/* 231 */             if (field.isElement());
/* 234 */             QName qname = field.getXmlName();
/* 235 */             if (qname == null)
/*     */             {
/* 237 */               qname = new QName("", propName);
/*     */             }
/*     */ 
/* 241 */             Class fieldType = this.propertyDescriptor[i].getType();
/*     */ 
/* 244 */             if (!types.isAcceptableAsAttribute(fieldType)) {
/* 245 */               throw new AxisFault(Messages.getMessage("AttrNotSimpleType00", propName, fieldType.getName()));
/*     */             }
/*     */ 
/* 252 */             Element elem = types.createAttributeElement(propName, fieldType, field.getXmlType(), false, extension.getOwnerDocument());
/*     */ 
/* 257 */             extension.appendChild(elem);
/*     */           }
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 263 */         BeanPropertyDescriptor bpd = this.propertyDescriptor[i];
/* 264 */         Class type = bpd.getType();
/*     */ 
/* 266 */         if (!types.isAcceptableAsAttribute(type)) {
/* 267 */           throw new AxisFault(Messages.getMessage("AttrNotSimpleType01", type.getName()));
/*     */         }
/*     */ 
/* 270 */         base = types.writeType(type);
/* 271 */         extension.setAttribute("base", base);
/*     */       }
/*     */     }
/*     */ 
/* 275 */     return complexType;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.SimpleListSerializer
 * JD-Core Version:    0.6.0
 */