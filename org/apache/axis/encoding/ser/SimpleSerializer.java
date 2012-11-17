/*     */ package org.apache.axis.encoding.ser;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.description.FieldDesc;
/*     */ import org.apache.axis.description.TypeDesc;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.encoding.SimpleType;
/*     */ import org.apache.axis.encoding.SimpleValueSerializer;
/*     */ import org.apache.axis.utils.BeanPropertyDescriptor;
/*     */ import org.apache.axis.utils.BeanUtils;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.fromJava.Types;
/*     */ import org.w3c.dom.Element;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.helpers.AttributesImpl;
/*     */ 
/*     */ public class SimpleSerializer
/*     */   implements SimpleValueSerializer
/*     */ {
/*     */   public QName xmlType;
/*     */   public Class javaType;
/*  47 */   private BeanPropertyDescriptor[] propertyDescriptor = null;
/*  48 */   private TypeDesc typeDesc = null;
/*     */   public static final String VALUE_PROPERTY = "_value";
/*     */ 
/*     */   public SimpleSerializer(Class javaType, QName xmlType)
/*     */   {
/*  52 */     this.xmlType = xmlType;
/*  53 */     this.javaType = javaType;
/*  54 */     init();
/*     */   }
/*     */   public SimpleSerializer(Class javaType, QName xmlType, TypeDesc typeDesc) {
/*  57 */     this.xmlType = xmlType;
/*  58 */     this.javaType = javaType;
/*  59 */     this.typeDesc = typeDesc;
/*  60 */     init();
/*     */   }
/*     */ 
/*     */   private void init()
/*     */   {
/*  68 */     if (this.typeDesc == null) {
/*  69 */       this.typeDesc = TypeDesc.getTypeDescForClass(this.javaType);
/*     */     }
/*     */ 
/*  73 */     if (this.typeDesc != null)
/*  74 */       this.propertyDescriptor = this.typeDesc.getPropertyDescriptors();
/*  75 */     else if (!JavaUtils.isBasic(this.javaType))
/*  76 */       this.propertyDescriptor = BeanUtils.getPd(this.javaType, null);
/*     */   }
/*     */ 
/*     */   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context)
/*     */     throws IOException
/*     */   {
/*  91 */     if ((value != null) && (value.getClass() == Object.class)) {
/*  92 */       throw new IOException(Messages.getMessage("cantSerialize02"));
/*     */     }
/*     */ 
/*  96 */     attributes = getObjectAttributes(value, attributes, context);
/*     */ 
/*  98 */     String valueStr = null;
/*  99 */     if (value != null) {
/* 100 */       valueStr = getValueAsString(value, context);
/*     */     }
/* 102 */     context.startElement(name, attributes);
/* 103 */     if (valueStr != null) {
/* 104 */       context.writeSafeString(valueStr);
/*     */     }
/* 106 */     context.endElement();
/*     */   }
/*     */ 
/*     */   public String getValueAsString(Object value, SerializationContext context)
/*     */   {
/* 113 */     if (((value instanceof Float)) || ((value instanceof Double)))
/*     */     {
/* 115 */       double data = 0.0D;
/* 116 */       if ((value instanceof Float))
/* 117 */         data = ((Float)value).doubleValue();
/*     */       else {
/* 119 */         data = ((Double)value).doubleValue();
/*     */       }
/* 121 */       if (Double.isNaN(data))
/* 122 */         return "NaN";
/* 123 */       if (data == (1.0D / 0.0D))
/* 124 */         return "INF";
/* 125 */       if (data == (-1.0D / 0.0D))
/* 126 */         return "-INF";
/*     */     }
/* 128 */     else if ((value instanceof QName)) {
/* 129 */       return context.qName2String((QName)value);
/*     */     }
/*     */ 
/* 132 */     if ((this.propertyDescriptor != null) && (!(value instanceof SimpleType))) {
/* 133 */       BeanPropertyDescriptor pd = BeanUtils.getSpecificPD(this.propertyDescriptor, "_value");
/* 134 */       if (pd != null)
/*     */         try {
/* 136 */           return pd.get(value).toString();
/*     */         }
/*     */         catch (Exception e) {
/*     */         }
/*     */     }
/* 141 */     return value.toString();
/*     */   }
/*     */ 
/*     */   private Attributes getObjectAttributes(Object value, Attributes attributes, SerializationContext context)
/*     */   {
/* 147 */     if ((this.typeDesc != null) && (!this.typeDesc.hasAttributes()))
/* 148 */       return attributes;
/*     */     AttributesImpl attrs;
/*     */     AttributesImpl attrs;
/* 151 */     if (attributes == null) {
/* 152 */       attrs = new AttributesImpl();
/*     */     }
/*     */     else
/*     */     {
/*     */       AttributesImpl attrs;
/* 153 */       if ((attributes instanceof AttributesImpl))
/* 154 */         attrs = (AttributesImpl)attributes;
/*     */       else {
/* 156 */         attrs = new AttributesImpl(attributes);
/*     */       }
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 162 */       int i = 0;
/* 163 */       while ((this.propertyDescriptor != null) && (i < this.propertyDescriptor.length))
/*     */       {
/* 165 */         String propName = this.propertyDescriptor[i].getName();
/* 166 */         if (!propName.equals("class"))
/*     */         {
/* 169 */           QName qname = null;
/* 170 */           if (this.typeDesc != null) {
/* 171 */             FieldDesc field = this.typeDesc.getFieldByName(propName);
/*     */ 
/* 173 */             if ((field == null) || (field.isElement())) break label270;
/* 175 */             qname = field.getXmlName();
/*     */           } else {
/* 177 */             if (propName.equals("_value")) break label270;
/*     */           }
/* 180 */           if (qname == null) {
/* 181 */             qname = new QName("", propName);
/*     */           }
/*     */ 
/* 184 */           if ((this.propertyDescriptor[i].isReadable()) && (!this.propertyDescriptor[i].isIndexed()))
/*     */           {
/* 187 */             Object propValue = this.propertyDescriptor[i].get(value);
/*     */ 
/* 192 */             if (propValue != null) {
/* 193 */               String propString = getValueAsString(propValue, context);
/*     */ 
/* 195 */               String namespace = qname.getNamespaceURI();
/* 196 */               String localName = qname.getLocalPart();
/*     */ 
/* 198 */               attrs.addAttribute(namespace, localName, context.qName2String(qname), "CDATA", propString);
/*     */             }
/*     */           }
/*     */         }
/* 164 */         label270: i++;
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 208 */       return attrs;
/*     */     }
/*     */ 
/* 211 */     return attrs;
/*     */   }
/*     */   public String getMechanismType() {
/* 214 */     return "Axis SAX Mechanism";
/*     */   }
/*     */ 
/*     */   public Element writeSchema(Class javaType, Types types)
/*     */     throws Exception
/*     */   {
/* 229 */     Element complexType = types.createElement("complexType");
/* 230 */     types.writeSchemaTypeDecl(this.xmlType, complexType);
/* 231 */     complexType.setAttribute("name", this.xmlType.getLocalPart());
/*     */ 
/* 234 */     Element simpleContent = types.createElement("simpleContent");
/* 235 */     complexType.appendChild(simpleContent);
/* 236 */     Element extension = types.createElement("extension");
/* 237 */     simpleContent.appendChild(extension);
/*     */ 
/* 240 */     String base = "string";
/* 241 */     for (int i = 0; (this.propertyDescriptor != null) && (i < this.propertyDescriptor.length); i++) {
/* 242 */       String propName = this.propertyDescriptor[i].getName();
/* 243 */       if (!propName.equals("value")) {
/* 244 */         if (this.typeDesc != null) {
/* 245 */           FieldDesc field = this.typeDesc.getFieldByName(propName);
/* 246 */           if (field != null) {
/* 247 */             if (field.isElement());
/* 250 */             QName qname = field.getXmlName();
/* 251 */             if (qname == null)
/*     */             {
/* 253 */               qname = new QName("", propName);
/*     */             }
/*     */ 
/* 257 */             Class fieldType = this.propertyDescriptor[i].getType();
/*     */ 
/* 260 */             if (!types.isAcceptableAsAttribute(fieldType)) {
/* 261 */               throw new AxisFault(Messages.getMessage("AttrNotSimpleType00", propName, fieldType.getName()));
/*     */             }
/*     */ 
/* 268 */             Element elem = types.createAttributeElement(propName, fieldType, field.getXmlType(), false, extension.getOwnerDocument());
/*     */ 
/* 273 */             extension.appendChild(elem);
/*     */           }
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 279 */         BeanPropertyDescriptor bpd = this.propertyDescriptor[i];
/* 280 */         Class type = bpd.getType();
/*     */ 
/* 282 */         if (!types.isAcceptableAsAttribute(type)) {
/* 283 */           throw new AxisFault(Messages.getMessage("AttrNotSimpleType01", type.getName()));
/*     */         }
/*     */ 
/* 286 */         base = types.writeType(type);
/* 287 */         extension.setAttribute("base", base);
/*     */       }
/*     */     }
/*     */ 
/* 291 */     return complexType;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.SimpleSerializer
 * JD-Core Version:    0.6.0
 */