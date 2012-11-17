/*     */ package org.apache.axis.encoding.ser;
/*     */ 
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.description.TypeDesc;
/*     */ import org.apache.axis.encoding.DeserializationContext;
/*     */ import org.apache.axis.encoding.Deserializer;
/*     */ import org.apache.axis.encoding.SimpleType;
/*     */ import org.apache.axis.encoding.TypeMapping;
/*     */ import org.apache.axis.message.SOAPHandler;
/*     */ import org.apache.axis.utils.BeanPropertyDescriptor;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class SimpleListDeserializer extends SimpleDeserializer
/*     */ {
/*  52 */   StringBuffer val = new StringBuffer();
/*  53 */   private Constructor constructor = null;
/*  54 */   private Map propertyMap = null;
/*  55 */   private HashMap attributeMap = null;
/*  56 */   private DeserializationContext context = null;
/*     */   public QName xmlType;
/*     */   public Class javaType;
/*  61 */   private TypeDesc typeDesc = null;
/*     */ 
/*  63 */   protected SimpleListDeserializer cacheStringDSer = null;
/*  64 */   protected QName cacheXMLType = null;
/*     */ 
/*     */   public SimpleListDeserializer(Class javaType, QName xmlType)
/*     */   {
/*  70 */     super(javaType, xmlType);
/*     */ 
/*  72 */     this.xmlType = xmlType;
/*  73 */     this.javaType = javaType;
/*     */   }
/*     */   public SimpleListDeserializer(Class javaType, QName xmlType, TypeDesc typeDesc) {
/*  76 */     super(javaType, xmlType, typeDesc);
/*     */ 
/*  78 */     this.xmlType = xmlType;
/*  79 */     this.javaType = javaType;
/*  80 */     this.typeDesc = typeDesc;
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/*  88 */     this.val.setLength(0);
/*  89 */     this.attributeMap = null;
/*  90 */     this.isNil = false;
/*  91 */     this.isEnded = false;
/*     */   }
/*     */ 
/*     */   public void setConstructor(Constructor c)
/*     */   {
/*  99 */     this.constructor = c;
/*     */   }
/*     */ 
/*     */   public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 112 */     throw new SAXException(Messages.getMessage("cantHandle00", "SimpleDeserializer"));
/*     */   }
/*     */ 
/*     */   public void characters(char[] chars, int start, int end)
/*     */     throws SAXException
/*     */   {
/* 123 */     this.val.append(chars, start, end);
/*     */   }
/*     */ 
/*     */   public void onEndElement(String namespace, String localName, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 134 */     if ((this.isNil) || (this.val == null)) {
/* 135 */       this.value = null;
/* 136 */       return;
/*     */     }
/*     */     try {
/* 139 */       this.value = makeValue(this.val.toString());
/*     */     } catch (InvocationTargetException ite) {
/* 141 */       Throwable realException = ite.getTargetException();
/* 142 */       if ((realException instanceof Exception)) {
/* 143 */         throw new SAXException((Exception)realException);
/*     */       }
/* 145 */       throw new SAXException(ite.getMessage());
/*     */     } catch (Exception e) {
/* 147 */       throw new SAXException(e);
/*     */     }
/*     */ 
/* 151 */     setSimpleTypeAttributes();
/*     */   }
/*     */ 
/*     */   public Object makeValue(String source)
/*     */     throws Exception
/*     */   {
/* 163 */     StringTokenizer tokenizer = new StringTokenizer(source.trim());
/* 164 */     int length = tokenizer.countTokens();
/* 165 */     Object list = Array.newInstance(this.javaType, length);
/* 166 */     for (int i = 0; i < length; i++) {
/* 167 */       String token = tokenizer.nextToken();
/* 168 */       Array.set(list, i, makeUnitValue(token));
/*     */     }
/* 170 */     return list;
/*     */   }
/*     */ 
/*     */   private Object makeUnitValue(String source)
/*     */     throws Exception
/*     */   {
/* 176 */     if ((this.javaType == Boolean.TYPE) || (this.javaType == Boolean.class))
/*     */     {
/* 178 */       switch (source.charAt(0)) { case '0':
/*     */       case 'F':
/*     */       case 'f':
/* 180 */         return Boolean.FALSE;
/*     */       case '1':
/*     */       case 'T':
/*     */       case 't':
/* 183 */         return Boolean.TRUE;
/*     */       }
/*     */ 
/* 186 */       throw new NumberFormatException(Messages.getMessage("badBool00"));
/*     */     }
/*     */ 
/* 193 */     if ((this.javaType == Float.TYPE) || (this.javaType == Float.class))
/*     */     {
/* 195 */       if (source.equals("NaN"))
/* 196 */         return new Float((0.0F / 0.0F));
/* 197 */       if (source.equals("INF"))
/* 198 */         return new Float((1.0F / 1.0F));
/* 199 */       if (source.equals("-INF")) {
/* 200 */         return new Float((1.0F / -1.0F));
/*     */       }
/*     */     }
/* 203 */     if ((this.javaType == Double.TYPE) || (this.javaType == Double.class))
/*     */     {
/* 205 */       if (source.equals("NaN"))
/* 206 */         return new Double((0.0D / 0.0D));
/* 207 */       if (source.equals("INF"))
/* 208 */         return new Double((1.0D / 0.0D));
/* 209 */       if (source.equals("-INF")) {
/* 210 */         return new Double((-1.0D / 0.0D));
/*     */       }
/*     */     }
/* 213 */     if (this.javaType == QName.class) {
/* 214 */       int colon = source.lastIndexOf(":");
/* 215 */       String namespace = colon < 0 ? "" : this.context.getNamespaceURI(source.substring(0, colon));
/*     */ 
/* 217 */       String localPart = colon < 0 ? source : source.substring(colon + 1);
/*     */ 
/* 219 */       return new QName(namespace, localPart);
/*     */     }
/*     */ 
/* 222 */     return this.constructor.newInstance(new Object[] { source });
/*     */   }
/*     */ 
/*     */   public void onStartElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 241 */     this.context = context;
/*     */ 
/* 244 */     if (this.typeDesc == null) {
/* 245 */       return;
/*     */     }
/*     */ 
/* 249 */     for (int i = 0; i < attributes.getLength(); i++) {
/* 250 */       QName attrQName = new QName(attributes.getURI(i), attributes.getLocalName(i));
/*     */ 
/* 252 */       String fieldName = this.typeDesc.getFieldNameForAttribute(attrQName);
/* 253 */       if (fieldName == null)
/*     */       {
/*     */         continue;
/*     */       }
/* 257 */       BeanPropertyDescriptor bpd = (BeanPropertyDescriptor)this.propertyMap.get(fieldName);
/*     */ 
/* 259 */       if ((bpd == null) || 
/* 260 */         (!bpd.isWriteable()) || (bpd.isIndexed())) {
/*     */         continue;
/*     */       }
/* 263 */       TypeMapping tm = context.getTypeMapping();
/* 264 */       Class type = bpd.getType();
/* 265 */       QName qn = tm.getTypeQName(type);
/* 266 */       if (qn == null) {
/* 267 */         throw new SAXException(Messages.getMessage("unregistered00", type.toString()));
/*     */       }
/*     */ 
/* 271 */       Deserializer dSer = context.getDeserializerForType(qn);
/* 272 */       if (dSer == null) {
/* 273 */         throw new SAXException(Messages.getMessage("noDeser00", type.toString()));
/*     */       }
/* 275 */       if (!(dSer instanceof SimpleListDeserializer)) {
/* 276 */         throw new SAXException(Messages.getMessage("AttrNotSimpleType00", bpd.getName(), type.toString()));
/*     */       }
/*     */ 
/* 283 */       if (this.attributeMap == null)
/* 284 */         this.attributeMap = new HashMap();
/*     */       try
/*     */       {
/* 287 */         Object val = ((SimpleListDeserializer)dSer).makeValue(attributes.getValue(i));
/*     */ 
/* 289 */         this.attributeMap.put(fieldName, val);
/*     */       } catch (Exception e) {
/* 291 */         throw new SAXException(e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setSimpleTypeAttributes()
/*     */     throws SAXException
/*     */   {
/* 302 */     if ((!SimpleType.class.isAssignableFrom(this.javaType)) || (this.attributeMap == null))
/*     */     {
/* 304 */       return;
/*     */     }
/*     */ 
/* 307 */     Set entries = this.attributeMap.entrySet();
/* 308 */     for (Iterator iterator = entries.iterator(); iterator.hasNext(); ) {
/* 309 */       Map.Entry entry = (Map.Entry)iterator.next();
/* 310 */       String name = (String)entry.getKey();
/* 311 */       Object val = entry.getValue();
/*     */ 
/* 313 */       BeanPropertyDescriptor bpd = (BeanPropertyDescriptor)this.propertyMap.get(name);
/*     */ 
/* 315 */       if ((!bpd.isWriteable()) || (bpd.isIndexed())) continue;
/*     */       try {
/* 317 */         bpd.set(this.value, val);
/*     */       } catch (Exception e) {
/* 319 */         throw new SAXException(e);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.SimpleListDeserializer
 * JD-Core Version:    0.6.0
 */