/*     */ package org.apache.axis.description;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.utils.BeanPropertyDescriptor;
/*     */ import org.apache.axis.utils.BeanUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.cache.MethodCache;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class TypeDesc
/*     */   implements Serializable
/*     */ {
/*  45 */   public static final Class[] noClasses = new Class[0];
/*  46 */   public static final Object[] noObjects = new Object[0];
/*     */ 
/*  49 */   private static Map classMap = Collections.synchronizedMap(new WeakHashMap());
/*     */ 
/*  52 */   private boolean lookedForAny = false;
/*     */ 
/*  55 */   private boolean canSearchParents = true;
/*  56 */   private boolean hasSearchedParents = false;
/*     */ 
/*  59 */   private TypeDesc parentDesc = null;
/*     */ 
/*  61 */   protected static Log log = LogFactory.getLog(TypeDesc.class.getName());
/*     */ 
/* 138 */   private WeakReference javaClassRef = null;
/*     */ 
/* 141 */   private QName xmlType = null;
/*     */   private FieldDesc[] fields;
/* 147 */   private HashMap fieldNameMap = new HashMap();
/*     */ 
/* 150 */   private HashMap fieldElementMap = null;
/*     */ 
/* 153 */   private boolean _hasAttributes = false;
/*     */ 
/* 156 */   private BeanPropertyDescriptor[] propertyDescriptors = null;
/*     */ 
/* 158 */   private Map propertyMap = null;
/*     */ 
/* 163 */   private BeanPropertyDescriptor anyDesc = null;
/*     */ 
/*     */   public TypeDesc(Class javaClass)
/*     */   {
/*  71 */     this(javaClass, true);
/*     */   }
/*     */ 
/*     */   public TypeDesc(Class javaClass, boolean canSearchParents)
/*     */   {
/*  82 */     this.javaClassRef = new WeakReference(javaClass);
/*  83 */     this.canSearchParents = canSearchParents;
/*  84 */     Class cls = javaClass.getSuperclass();
/*  85 */     if ((cls != null) && (!cls.getName().startsWith("java.")))
/*  86 */       this.parentDesc = getTypeDescForClass(cls);
/*     */   }
/*     */ 
/*     */   public static void registerTypeDescForClass(Class cls, TypeDesc td)
/*     */   {
/*  99 */     classMap.put(cls, td);
/*     */   }
/*     */ 
/*     */   public static TypeDesc getTypeDescForClass(Class cls)
/*     */   {
/* 116 */     TypeDesc result = (TypeDesc)classMap.get(cls);
/*     */ 
/* 118 */     if (result == null) {
/*     */       try {
/* 120 */         Method getTypeDesc = MethodCache.getInstance().getMethod(cls, "getTypeDesc", noClasses);
/*     */ 
/* 124 */         if (getTypeDesc != null) {
/* 125 */           result = (TypeDesc)getTypeDesc.invoke(null, noObjects);
/* 126 */           if (result != null)
/* 127 */             classMap.put(cls, result);
/*     */         }
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/*     */     }
/* 134 */     return result;
/*     */   }
/*     */ 
/*     */   public BeanPropertyDescriptor getAnyDesc()
/*     */   {
/* 166 */     return this.anyDesc;
/*     */   }
/*     */ 
/*     */   public FieldDesc[] getFields()
/*     */   {
/* 173 */     return this.fields;
/*     */   }
/*     */ 
/*     */   public FieldDesc[] getFields(boolean searchParents)
/*     */   {
/* 183 */     if ((this.canSearchParents) && (searchParents) && (!this.hasSearchedParents))
/*     */     {
/* 185 */       if (this.parentDesc != null) {
/* 186 */         FieldDesc[] parentFields = this.parentDesc.getFields(true);
/*     */ 
/* 188 */         if (parentFields != null) {
/* 189 */           if (this.fields != null) {
/* 190 */             FieldDesc[] ret = new FieldDesc[parentFields.length + this.fields.length];
/* 191 */             System.arraycopy(parentFields, 0, ret, 0, parentFields.length);
/* 192 */             System.arraycopy(this.fields, 0, ret, parentFields.length, this.fields.length);
/* 193 */             this.fields = ret;
/*     */           } else {
/* 195 */             FieldDesc[] ret = new FieldDesc[parentFields.length];
/* 196 */             System.arraycopy(parentFields, 0, ret, 0, parentFields.length);
/* 197 */             this.fields = ret;
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 203 */       this.hasSearchedParents = true;
/*     */     }
/*     */ 
/* 206 */     return this.fields;
/*     */   }
/*     */ 
/*     */   public void setFields(FieldDesc[] newFields)
/*     */   {
/* 215 */     this.fieldNameMap = new HashMap();
/* 216 */     this.fields = newFields;
/* 217 */     this._hasAttributes = false;
/* 218 */     this.fieldElementMap = null;
/*     */ 
/* 220 */     for (int i = 0; i < newFields.length; i++) {
/* 221 */       FieldDesc field = newFields[i];
/* 222 */       if (!field.isElement()) {
/* 223 */         this._hasAttributes = true;
/*     */       }
/* 225 */       this.fieldNameMap.put(field.getFieldName(), field);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addFieldDesc(FieldDesc field)
/*     */   {
/* 234 */     if (field == null) {
/* 235 */       throw new IllegalArgumentException(Messages.getMessage("nullFieldDesc"));
/*     */     }
/*     */ 
/* 239 */     int numFields = 0;
/* 240 */     if (this.fields != null) {
/* 241 */       numFields = this.fields.length;
/*     */     }
/* 243 */     FieldDesc[] newFields = new FieldDesc[numFields + 1];
/* 244 */     if (this.fields != null) {
/* 245 */       System.arraycopy(this.fields, 0, newFields, 0, numFields);
/*     */     }
/* 247 */     newFields[numFields] = field;
/* 248 */     this.fields = newFields;
/*     */ 
/* 251 */     this.fieldNameMap.put(field.getFieldName(), field);
/*     */ 
/* 253 */     if ((!this._hasAttributes) && (!field.isElement()))
/* 254 */       this._hasAttributes = true;
/*     */   }
/*     */ 
/*     */   public QName getElementNameForField(String fieldName)
/*     */   {
/* 263 */     FieldDesc desc = (FieldDesc)this.fieldNameMap.get(fieldName);
/* 264 */     if (desc == null)
/*     */     {
/* 267 */       if ((this.canSearchParents) && 
/* 268 */         (this.parentDesc != null)) {
/* 269 */         return this.parentDesc.getElementNameForField(fieldName);
/*     */       }
/*     */     }
/* 272 */     else if (desc.isElement()) {
/* 273 */       return desc.getXmlName();
/*     */     }
/* 275 */     return null;
/*     */   }
/*     */ 
/*     */   public QName getAttributeNameForField(String fieldName)
/*     */   {
/* 284 */     FieldDesc desc = (FieldDesc)this.fieldNameMap.get(fieldName);
/* 285 */     if (desc == null)
/*     */     {
/* 288 */       if ((this.canSearchParents) && 
/* 289 */         (this.parentDesc != null)) {
/* 290 */         return this.parentDesc.getAttributeNameForField(fieldName);
/*     */       }
/*     */     }
/* 293 */     else if (!desc.isElement()) {
/* 294 */       QName ret = desc.getXmlName();
/* 295 */       if (ret == null) {
/* 296 */         ret = new QName("", fieldName);
/*     */       }
/* 298 */       return ret;
/*     */     }
/* 300 */     return null;
/*     */   }
/*     */ 
/*     */   public String getFieldNameForElement(QName qname, boolean ignoreNS)
/*     */   {
/* 312 */     if (this.fieldElementMap != null) {
/* 313 */       String cached = (String)this.fieldElementMap.get(qname);
/* 314 */       if (cached != null) return cached;
/*     */     }
/*     */ 
/* 317 */     String result = null;
/*     */ 
/* 319 */     String localPart = qname.getLocalPart();
/*     */ 
/* 322 */     for (int i = 0; (this.fields != null) && (i < this.fields.length); i++) {
/* 323 */       FieldDesc field = this.fields[i];
/* 324 */       if (field.isElement()) {
/* 325 */         QName xmlName = field.getXmlName();
/* 326 */         if ((!localPart.equals(xmlName.getLocalPart())) || (
/* 327 */           (!ignoreNS) && (!qname.getNamespaceURI().equals(xmlName.getNamespaceURI()))))
/*     */           continue;
/* 329 */         result = field.getFieldName();
/* 330 */         break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 338 */     if ((result == null) && (this.canSearchParents) && 
/* 339 */       (this.parentDesc != null)) {
/* 340 */       result = this.parentDesc.getFieldNameForElement(qname, ignoreNS);
/*     */     }
/*     */ 
/* 345 */     if (result != null) {
/* 346 */       if (this.fieldElementMap == null) this.fieldElementMap = new HashMap();
/* 347 */       this.fieldElementMap.put(qname, result);
/*     */     }
/*     */ 
/* 350 */     return result;
/*     */   }
/*     */ 
/*     */   public String getFieldNameForAttribute(QName qname)
/*     */   {
/* 359 */     String possibleMatch = null;
/*     */ 
/* 361 */     for (int i = 0; (this.fields != null) && (i < this.fields.length); i++) {
/* 362 */       FieldDesc field = this.fields[i];
/* 363 */       if (field.isElement()) {
/*     */         continue;
/*     */       }
/* 366 */       if (qname.equals(field.getXmlName())) {
/* 367 */         return field.getFieldName();
/*     */       }
/*     */ 
/* 371 */       if ((!qname.getNamespaceURI().equals("")) || (!qname.getLocalPart().equals(field.getFieldName())))
/*     */         continue;
/* 373 */       possibleMatch = field.getFieldName();
/*     */     }
/*     */ 
/* 378 */     if ((possibleMatch == null) && (this.canSearchParents))
/*     */     {
/* 381 */       if (this.parentDesc != null) {
/* 382 */         possibleMatch = this.parentDesc.getFieldNameForAttribute(qname);
/*     */       }
/*     */     }
/*     */ 
/* 386 */     return possibleMatch;
/*     */   }
/*     */ 
/*     */   public FieldDesc getFieldByName(String name)
/*     */   {
/* 394 */     FieldDesc ret = (FieldDesc)this.fieldNameMap.get(name);
/* 395 */     if ((ret == null) && (this.canSearchParents) && 
/* 396 */       (this.parentDesc != null)) {
/* 397 */       ret = this.parentDesc.getFieldByName(name);
/*     */     }
/*     */ 
/* 400 */     return ret;
/*     */   }
/*     */ 
/*     */   public boolean hasAttributes()
/*     */   {
/* 407 */     if (this._hasAttributes) {
/* 408 */       return true;
/*     */     }
/* 410 */     if ((this.canSearchParents) && 
/* 411 */       (this.parentDesc != null)) {
/* 412 */       return this.parentDesc.hasAttributes();
/*     */     }
/*     */ 
/* 416 */     return false;
/*     */   }
/*     */ 
/*     */   public QName getXmlType() {
/* 420 */     return this.xmlType;
/*     */   }
/*     */ 
/*     */   public void setXmlType(QName xmlType) {
/* 424 */     this.xmlType = xmlType;
/*     */   }
/*     */ 
/*     */   public BeanPropertyDescriptor[] getPropertyDescriptors()
/*     */   {
/* 438 */     if (this.propertyDescriptors == null) {
/* 439 */       makePropertyDescriptors();
/*     */     }
/* 441 */     return this.propertyDescriptors;
/*     */   }
/*     */ 
/*     */   private synchronized void makePropertyDescriptors() {
/* 445 */     if (this.propertyDescriptors != null) {
/* 446 */       return;
/*     */     }
/*     */ 
/* 450 */     Class javaClass = (Class)this.javaClassRef.get();
/* 451 */     if (javaClass == null)
/*     */     {
/* 453 */       log.error(Messages.getMessage("classGCed"));
/* 454 */       this.propertyDescriptors = new BeanPropertyDescriptor[0];
/* 455 */       this.anyDesc = null;
/* 456 */       this.lookedForAny = true;
/* 457 */       return;
/*     */     }
/*     */ 
/* 460 */     this.propertyDescriptors = BeanUtils.getPd(javaClass, this);
/* 461 */     if (!this.lookedForAny) {
/* 462 */       this.anyDesc = BeanUtils.getAnyContentPD(javaClass);
/* 463 */       this.lookedForAny = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public BeanPropertyDescriptor getAnyContentDescriptor() {
/* 468 */     if (!this.lookedForAny)
/*     */     {
/* 471 */       Class javaClass = (Class)this.javaClassRef.get();
/* 472 */       if (javaClass != null) {
/* 473 */         this.anyDesc = BeanUtils.getAnyContentPD(javaClass);
/*     */       }
/*     */       else {
/* 476 */         log.error(Messages.getMessage("classGCed"));
/* 477 */         this.anyDesc = null;
/*     */       }
/*     */ 
/* 480 */       this.lookedForAny = true;
/*     */     }
/* 482 */     return this.anyDesc;
/*     */   }
/*     */ 
/*     */   public Map getPropertyDescriptorMap()
/*     */   {
/* 490 */     synchronized (this)
/*     */     {
/* 492 */       if (this.propertyMap != null) {
/* 493 */         return this.propertyMap;
/*     */       }
/*     */ 
/* 497 */       if (this.propertyDescriptors == null) {
/* 498 */         getPropertyDescriptors();
/*     */       }
/*     */ 
/* 502 */       this.propertyMap = new HashMap();
/* 503 */       for (int i = 0; i < this.propertyDescriptors.length; i++) {
/* 504 */         BeanPropertyDescriptor descriptor = this.propertyDescriptors[i];
/* 505 */         this.propertyMap.put(descriptor.getName(), descriptor);
/*     */       }
/*     */     }
/* 508 */     return this.propertyMap;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.description.TypeDesc
 * JD-Core Version:    0.6.0
 */