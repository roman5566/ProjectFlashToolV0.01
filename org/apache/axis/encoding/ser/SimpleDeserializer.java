/*     */ package org.apache.axis.encoding.ser;
/*     */ 
/*     */ import java.io.CharArrayWriter;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.description.TypeDesc;
/*     */ import org.apache.axis.encoding.DeserializationContext;
/*     */ import org.apache.axis.encoding.Deserializer;
/*     */ import org.apache.axis.encoding.DeserializerImpl;
/*     */ import org.apache.axis.encoding.SimpleType;
/*     */ import org.apache.axis.encoding.TypeMapping;
/*     */ import org.apache.axis.message.SOAPHandler;
/*     */ import org.apache.axis.types.URI;
/*     */ import org.apache.axis.utils.BeanPropertyDescriptor;
/*     */ import org.apache.axis.utils.BeanUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class SimpleDeserializer extends DeserializerImpl
/*     */ {
/*  52 */   private static final Class[] STRING_STRING_CLASS = { String.class, String.class };
/*     */ 
/*  55 */   public static final Class[] STRING_CLASS = { String.class };
/*     */ 
/*  58 */   private final CharArrayWriter val = new CharArrayWriter();
/*  59 */   private Constructor constructor = null;
/*  60 */   private Map propertyMap = null;
/*  61 */   private HashMap attributeMap = null;
/*     */   public QName xmlType;
/*     */   public Class javaType;
/*  66 */   private TypeDesc typeDesc = null;
/*     */ 
/*  68 */   protected DeserializationContext context = null;
/*  69 */   protected SimpleDeserializer cacheStringDSer = null;
/*  70 */   protected QName cacheXMLType = null;
/*     */ 
/*     */   public SimpleDeserializer(Class javaType, QName xmlType)
/*     */   {
/*  76 */     this.xmlType = xmlType;
/*  77 */     this.javaType = javaType;
/*     */ 
/*  79 */     init();
/*     */   }
/*     */ 
/*     */   public SimpleDeserializer(Class javaType, QName xmlType, TypeDesc typeDesc) {
/*  83 */     this.xmlType = xmlType;
/*  84 */     this.javaType = javaType;
/*  85 */     this.typeDesc = typeDesc;
/*     */ 
/*  87 */     init();
/*     */   }
/*     */ 
/*     */   private void init()
/*     */   {
/*  96 */     if (SimpleType.class.isAssignableFrom(this.javaType))
/*     */     {
/*  98 */       if (this.typeDesc == null) {
/*  99 */         this.typeDesc = TypeDesc.getTypeDescForClass(this.javaType);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 105 */     if (this.typeDesc != null) {
/* 106 */       this.propertyMap = this.typeDesc.getPropertyDescriptorMap();
/*     */     } else {
/* 108 */       BeanPropertyDescriptor[] pd = BeanUtils.getPd(this.javaType, null);
/* 109 */       this.propertyMap = new HashMap();
/* 110 */       for (int i = 0; i < pd.length; i++) {
/* 111 */         BeanPropertyDescriptor descriptor = pd[i];
/* 112 */         this.propertyMap.put(descriptor.getName(), descriptor);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/* 121 */     this.val.reset();
/* 122 */     this.attributeMap = null;
/* 123 */     this.isNil = false;
/* 124 */     this.isEnded = false;
/*     */   }
/*     */ 
/*     */   public void setConstructor(Constructor c)
/*     */   {
/* 132 */     this.constructor = c;
/*     */   }
/*     */ 
/*     */   public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 145 */     throw new SAXException(Messages.getMessage("cantHandle00", "SimpleDeserializer"));
/*     */   }
/*     */ 
/*     */   public void characters(char[] chars, int start, int end)
/*     */     throws SAXException
/*     */   {
/* 156 */     this.val.write(chars, start, end);
/*     */   }
/*     */ 
/*     */   public void onEndElement(String namespace, String localName, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 167 */     if (this.isNil) {
/* 168 */       this.value = null;
/* 169 */       return;
/*     */     }
/*     */     try {
/* 172 */       this.value = makeValue(this.val.toString());
/*     */     } catch (InvocationTargetException ite) {
/* 174 */       Throwable realException = ite.getTargetException();
/* 175 */       if ((realException instanceof Exception)) {
/* 176 */         throw new SAXException((Exception)realException);
/*     */       }
/* 178 */       throw new SAXException(ite.getMessage());
/*     */     } catch (Exception e) {
/* 180 */       throw new SAXException(e);
/*     */     }
/*     */ 
/* 184 */     setSimpleTypeAttributes();
/*     */   }
/*     */ 
/*     */   public Object makeValue(String source)
/*     */     throws Exception
/*     */   {
/* 196 */     if (this.javaType == String.class) {
/* 197 */       return source;
/*     */     }
/*     */ 
/* 201 */     source = source.trim();
/*     */ 
/* 203 */     if ((source.length() == 0) && (this.typeDesc == null)) {
/* 204 */       return null;
/*     */     }
/*     */ 
/* 208 */     if (this.constructor == null) {
/* 209 */       Object value = makeBasicValue(source);
/* 210 */       if (value != null) {
/* 211 */         return value;
/*     */       }
/*     */     }
/*     */ 
/* 215 */     Object[] args = null;
/*     */ 
/* 217 */     boolean isQNameSubclass = QName.class.isAssignableFrom(this.javaType);
/*     */ 
/* 219 */     if (isQNameSubclass) {
/* 220 */       int colon = source.lastIndexOf(":");
/* 221 */       String namespace = colon < 0 ? "" : this.context.getNamespaceURI(source.substring(0, colon));
/*     */ 
/* 223 */       String localPart = colon < 0 ? source : source.substring(colon + 1);
/* 224 */       args = new Object[] { namespace, localPart };
/*     */     }
/*     */ 
/* 227 */     if (this.constructor == null) {
/*     */       try {
/* 229 */         if (isQNameSubclass) {
/* 230 */           this.constructor = this.javaType.getDeclaredConstructor(STRING_STRING_CLASS);
/*     */         }
/*     */         else
/* 233 */           this.constructor = this.javaType.getDeclaredConstructor(STRING_CLASS);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 237 */         return null;
/*     */       }
/*     */     }
/*     */ 
/* 241 */     if (this.constructor.getParameterTypes().length == 0)
/*     */       try {
/* 243 */         Object obj = this.constructor.newInstance(new Object[0]);
/* 244 */         obj.getClass().getMethod("set_value", new Class[] { String.class }).invoke(obj, new Object[] { source });
/*     */ 
/* 246 */         return obj;
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/* 251 */     if (args == null) {
/* 252 */       args = new Object[] { source };
/*     */     }
/* 254 */     return this.constructor.newInstance(args);
/*     */   }
/*     */ 
/*     */   private Object makeBasicValue(String source) throws Exception
/*     */   {
/* 259 */     if ((this.javaType == Boolean.TYPE) || (this.javaType == Boolean.class))
/*     */     {
/* 262 */       switch (source.charAt(0)) { case '0':
/*     */       case 'F':
/*     */       case 'f':
/* 264 */         return Boolean.FALSE;
/*     */       case '1':
/*     */       case 'T':
/*     */       case 't':
/* 267 */         return Boolean.TRUE;
/*     */       }
/*     */ 
/* 270 */       throw new NumberFormatException(Messages.getMessage("badBool00"));
/*     */     }
/*     */ 
/* 277 */     if ((this.javaType == Float.TYPE) || (this.javaType == Float.class))
/*     */     {
/* 279 */       if (source.equals("NaN"))
/* 280 */         return new Float((0.0F / 0.0F));
/* 281 */       if (source.equals("INF"))
/* 282 */         return new Float((1.0F / 1.0F));
/* 283 */       if (source.equals("-INF")) {
/* 284 */         return new Float((1.0F / -1.0F));
/*     */       }
/* 286 */       return new Float(source);
/*     */     }
/*     */ 
/* 290 */     if ((this.javaType == Double.TYPE) || (this.javaType == Double.class))
/*     */     {
/* 292 */       if (source.equals("NaN"))
/* 293 */         return new Double((0.0D / 0.0D));
/* 294 */       if (source.equals("INF"))
/* 295 */         return new Double((1.0D / 0.0D));
/* 296 */       if (source.equals("-INF")) {
/* 297 */         return new Double((-1.0D / 0.0D));
/*     */       }
/* 299 */       return new Double(source);
/*     */     }
/*     */ 
/* 303 */     if ((this.javaType == Integer.TYPE) || (this.javaType == Integer.class))
/*     */     {
/* 305 */       return new Integer(source);
/*     */     }
/*     */ 
/* 308 */     if ((this.javaType == Short.TYPE) || (this.javaType == Short.class))
/*     */     {
/* 310 */       return new Short(source);
/*     */     }
/*     */ 
/* 313 */     if ((this.javaType == Long.TYPE) || (this.javaType == Long.class))
/*     */     {
/* 315 */       return new Long(source);
/*     */     }
/*     */ 
/* 318 */     if ((this.javaType == Byte.TYPE) || (this.javaType == Byte.class))
/*     */     {
/* 320 */       return new Byte(source);
/*     */     }
/*     */ 
/* 323 */     if (this.javaType == URI.class) {
/* 324 */       return new URI(source);
/*     */     }
/*     */ 
/* 327 */     return null;
/*     */   }
/*     */ 
/*     */   public void onStartElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 347 */     this.context = context;
/*     */ 
/* 351 */     for (int i = 0; i < attributes.getLength(); i++) {
/* 352 */       QName attrQName = new QName(attributes.getURI(i), attributes.getLocalName(i));
/*     */ 
/* 355 */       String fieldName = attributes.getLocalName(i);
/*     */ 
/* 357 */       if (this.typeDesc != null) {
/* 358 */         fieldName = this.typeDesc.getFieldNameForAttribute(attrQName);
/* 359 */         if (fieldName == null) {
/*     */           continue;
/*     */         }
/*     */       }
/* 363 */       if (this.propertyMap == null)
/*     */       {
/*     */         continue;
/*     */       }
/* 367 */       BeanPropertyDescriptor bpd = (BeanPropertyDescriptor)this.propertyMap.get(fieldName);
/*     */ 
/* 369 */       if ((bpd == null) || 
/* 370 */         (!bpd.isWriteable()) || (bpd.isIndexed())) {
/*     */         continue;
/*     */       }
/* 373 */       TypeMapping tm = context.getTypeMapping();
/* 374 */       Class type = bpd.getType();
/* 375 */       QName qn = tm.getTypeQName(type);
/* 376 */       if (qn == null) {
/* 377 */         throw new SAXException(Messages.getMessage("unregistered00", type.toString()));
/*     */       }
/*     */ 
/* 381 */       Deserializer dSer = context.getDeserializerForType(qn);
/* 382 */       if (dSer == null) {
/* 383 */         throw new SAXException(Messages.getMessage("noDeser00", type.toString()));
/*     */       }
/* 385 */       if (!(dSer instanceof SimpleDeserializer)) {
/* 386 */         throw new SAXException(Messages.getMessage("AttrNotSimpleType00", bpd.getName(), type.toString()));
/*     */       }
/*     */ 
/* 393 */       if (this.attributeMap == null)
/* 394 */         this.attributeMap = new HashMap();
/*     */       try
/*     */       {
/* 397 */         Object val = ((SimpleDeserializer)dSer).makeValue(attributes.getValue(i));
/*     */ 
/* 399 */         this.attributeMap.put(fieldName, val);
/*     */       } catch (Exception e) {
/* 401 */         throw new SAXException(e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setSimpleTypeAttributes()
/*     */     throws SAXException
/*     */   {
/* 411 */     if (this.attributeMap == null) {
/* 412 */       return;
/*     */     }
/*     */ 
/* 415 */     Set entries = this.attributeMap.entrySet();
/* 416 */     for (Iterator iterator = entries.iterator(); iterator.hasNext(); ) {
/* 417 */       Map.Entry entry = (Map.Entry)iterator.next();
/* 418 */       String name = (String)entry.getKey();
/* 419 */       Object val = entry.getValue();
/*     */ 
/* 421 */       BeanPropertyDescriptor bpd = (BeanPropertyDescriptor)this.propertyMap.get(name);
/*     */ 
/* 423 */       if ((!bpd.isWriteable()) || (bpd.isIndexed())) continue;
/*     */       try {
/* 425 */         bpd.set(this.value, val);
/*     */       } catch (Exception e) {
/* 427 */         throw new SAXException(e);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.SimpleDeserializer
 * JD-Core Version:    0.6.0
 */