/*     */ package org.apache.axis.encoding;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.rpc.JAXRPCException;
/*     */ import org.apache.axis.AxisEngine;
/*     */ import org.apache.axis.AxisProperties;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.encoding.ser.ArrayDeserializerFactory;
/*     */ import org.apache.axis.encoding.ser.ArraySerializerFactory;
/*     */ import org.apache.axis.encoding.ser.BeanDeserializerFactory;
/*     */ import org.apache.axis.encoding.ser.BeanSerializerFactory;
/*     */ import org.apache.axis.handlers.soap.SOAPService;
/*     */ import org.apache.axis.utils.ArrayUtil;
/*     */ import org.apache.axis.utils.ClassUtils;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.fromJava.Namespaces;
/*     */ import org.apache.axis.wsdl.fromJava.Types;
/*     */ import org.apache.axis.wsdl.toJava.Utils;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class TypeMappingImpl
/*     */   implements Serializable
/*     */ {
/*  77 */   protected static Log log = LogFactory.getLog(TypeMappingImpl.class.getName());
/*     */ 
/*  86 */   public static boolean dotnet_soapenc_bugfix = false;
/*     */   private HashMap qName2Pair;
/*     */   private HashMap class2Pair;
/*     */   private HashMap pair2SF;
/*     */   private HashMap pair2DF;
/*     */   private ArrayList namespaces;
/* 124 */   protected Boolean doAutoTypes = null;
/*     */ 
/*     */   public TypeMappingImpl()
/*     */   {
/* 130 */     this.qName2Pair = new HashMap();
/* 131 */     this.class2Pair = new HashMap();
/* 132 */     this.pair2SF = new HashMap();
/* 133 */     this.pair2DF = new HashMap();
/* 134 */     this.namespaces = new ArrayList();
/*     */   }
/*     */ 
/*     */   private static boolean isArray(Class clazz)
/*     */   {
/* 139 */     return (clazz.isArray()) || (Collection.class.isAssignableFrom(clazz));
/*     */   }
/*     */ 
/*     */   public String[] getSupportedEncodings()
/*     */   {
/* 152 */     String[] stringArray = new String[this.namespaces.size()];
/* 153 */     return (String[])this.namespaces.toArray(stringArray);
/*     */   }
/*     */ 
/*     */   public void setSupportedEncodings(String[] namespaceURIs)
/*     */   {
/* 164 */     this.namespaces.clear();
/* 165 */     for (int i = 0; i < namespaceURIs.length; i++)
/* 166 */       if (!this.namespaces.contains(namespaceURIs[i]))
/* 167 */         this.namespaces.add(namespaceURIs[i]);
/*     */   }
/*     */ 
/*     */   public boolean isRegistered(Class javaType, QName xmlType)
/*     */   {
/* 188 */     if ((javaType == null) || (xmlType == null))
/*     */     {
/* 191 */       throw new JAXRPCException(Messages.getMessage(javaType == null ? "badJavaType" : "badXmlType"));
/*     */     }
/*     */ 
/* 196 */     return this.pair2SF.keySet().contains(new Pair(javaType, xmlType));
/*     */   }
/*     */ 
/*     */   public void register(Class javaType, QName xmlType, javax.xml.rpc.encoding.SerializerFactory sf, javax.xml.rpc.encoding.DeserializerFactory dsf)
/*     */     throws JAXRPCException
/*     */   {
/* 217 */     if ((sf == null) && (dsf == null)) {
/* 218 */       throw new JAXRPCException(Messages.getMessage("badSerFac"));
/*     */     }
/*     */ 
/* 221 */     internalRegister(javaType, xmlType, sf, dsf);
/*     */   }
/*     */ 
/*     */   protected void internalRegister(Class javaType, QName xmlType, javax.xml.rpc.encoding.SerializerFactory sf, javax.xml.rpc.encoding.DeserializerFactory dsf)
/*     */     throws JAXRPCException
/*     */   {
/* 238 */     if ((javaType == null) || (xmlType == null)) {
/* 239 */       throw new JAXRPCException(Messages.getMessage(javaType == null ? "badJavaType" : "badXmlType"));
/*     */     }
/*     */ 
/* 254 */     Pair pair = new Pair(javaType, xmlType);
/*     */ 
/* 263 */     this.qName2Pair.put(xmlType, pair);
/* 264 */     this.class2Pair.put(javaType, pair);
/*     */ 
/* 266 */     if (sf != null)
/* 267 */       this.pair2SF.put(pair, sf);
/* 268 */     if (dsf != null)
/* 269 */       this.pair2DF.put(pair, dsf);
/*     */   }
/*     */ 
/*     */   public javax.xml.rpc.encoding.SerializerFactory getSerializer(Class javaType, QName xmlType)
/*     */     throws JAXRPCException
/*     */   {
/* 290 */     javax.xml.rpc.encoding.SerializerFactory sf = null;
/*     */ 
/* 293 */     if (xmlType == null) {
/* 294 */       xmlType = getTypeQName(javaType, null);
/*     */ 
/* 297 */       if (xmlType == null) {
/* 298 */         return null;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 303 */     Pair pair = new Pair(javaType, xmlType);
/*     */ 
/* 306 */     sf = (javax.xml.rpc.encoding.SerializerFactory)this.pair2SF.get(pair);
/*     */ 
/* 311 */     if ((sf == null) && (javaType.isArray())) {
/* 312 */       int dimension = 1;
/* 313 */       Class componentType = javaType.getComponentType();
/* 314 */       while (componentType.isArray()) {
/* 315 */         dimension++;
/* 316 */         componentType = componentType.getComponentType();
/*     */       }
/* 318 */       int[] dimensions = new int[dimension];
/* 319 */       componentType = componentType.getSuperclass();
/* 320 */       Class superJavaType = null;
/* 321 */       while (componentType != null) {
/* 322 */         superJavaType = Array.newInstance(componentType, dimensions).getClass();
/* 323 */         pair = new Pair(superJavaType, xmlType);
/* 324 */         sf = (javax.xml.rpc.encoding.SerializerFactory)this.pair2SF.get(pair);
/* 325 */         if (sf != null) {
/*     */           break;
/*     */         }
/* 328 */         componentType = componentType.getSuperclass();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 333 */     if ((sf == null) && (javaType.isArray()) && (xmlType != null)) {
/* 334 */       Pair pair2 = (Pair)this.qName2Pair.get(xmlType);
/* 335 */       if ((pair2 != null) && (pair2.javaType != null) && (!pair2.javaType.isPrimitive()) && (ArrayUtil.isConvertable(pair2.javaType, javaType)))
/*     */       {
/* 339 */         sf = (javax.xml.rpc.encoding.SerializerFactory)this.pair2SF.get(pair2);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 344 */     if ((sf == null) && (!javaType.isArray()) && (!Constants.isSchemaXSD(xmlType.getNamespaceURI())) && (!Constants.isSOAP_ENC(xmlType.getNamespaceURI())))
/*     */     {
/* 347 */       Pair pair2 = (Pair)this.qName2Pair.get(xmlType);
/* 348 */       if ((pair2 != null) && (pair2.javaType != null) && (!pair2.javaType.isArray()) && ((javaType.isAssignableFrom(pair2.javaType)) || ((pair2.javaType.isPrimitive()) && (javaType == JavaUtils.getWrapperClass(pair2.javaType)))))
/*     */       {
/* 353 */         sf = (javax.xml.rpc.encoding.SerializerFactory)this.pair2SF.get(pair2);
/*     */       }
/*     */     }
/*     */ 
/* 357 */     return sf;
/*     */   }
/*     */ 
/*     */   public SerializerFactory finalGetSerializer(Class javaType)
/*     */   {
/*     */     Pair pair;
/*     */     Pair pair;
/* 362 */     if (isArray(javaType))
/* 363 */       pair = (Pair)this.qName2Pair.get(Constants.SOAP_ARRAY);
/*     */     else {
/* 365 */       pair = (Pair)this.class2Pair.get(javaType);
/*     */     }
/* 367 */     if (pair != null) {
/* 368 */       return (SerializerFactory)this.pair2SF.get(pair);
/*     */     }
/*     */ 
/* 371 */     return null;
/*     */   }
/*     */ 
/*     */   public QName getXMLType(Class javaType, QName xmlType, boolean encoded)
/*     */     throws JAXRPCException
/*     */   {
/* 394 */     javax.xml.rpc.encoding.SerializerFactory sf = null;
/*     */ 
/* 397 */     if (xmlType == null) {
/* 398 */       xmlType = getTypeQNameRecursive(javaType);
/*     */ 
/* 402 */       if (xmlType == null) {
/* 403 */         return null;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 408 */     Pair pair = new Pair(javaType, xmlType);
/*     */ 
/* 411 */     sf = (javax.xml.rpc.encoding.SerializerFactory)this.pair2SF.get(pair);
/* 412 */     if (sf != null) {
/* 413 */       return xmlType;
/*     */     }
/*     */ 
/* 418 */     if (isArray(javaType)) {
/* 419 */       if (encoded) {
/* 420 */         return Constants.SOAP_ARRAY;
/*     */       }
/* 422 */       pair = (Pair)this.qName2Pair.get(xmlType);
/*     */     }
/*     */ 
/* 426 */     if (pair == null) {
/* 427 */       pair = (Pair)this.class2Pair.get(javaType);
/*     */     }
/*     */ 
/* 430 */     if (pair != null) {
/* 431 */       xmlType = pair.xmlType;
/*     */     }
/* 433 */     return xmlType;
/*     */   }
/*     */ 
/*     */   public javax.xml.rpc.encoding.DeserializerFactory getDeserializer(Class javaType, QName xmlType, TypeMappingDelegate start)
/*     */     throws JAXRPCException
/*     */   {
/* 453 */     if (javaType == null) {
/* 454 */       javaType = start.getClassForQName(xmlType);
/*     */ 
/* 457 */       if (javaType == null) {
/* 458 */         return null;
/*     */       }
/*     */     }
/*     */ 
/* 462 */     Pair pair = new Pair(javaType, xmlType);
/*     */ 
/* 464 */     return (javax.xml.rpc.encoding.DeserializerFactory)this.pair2DF.get(pair);
/*     */   }
/*     */ 
/*     */   public DeserializerFactory finalGetDeserializer(Class javaType, QName xmlType, TypeMappingDelegate start)
/*     */   {
/* 470 */     DeserializerFactory df = null;
/* 471 */     if ((javaType != null) && (javaType.isArray())) {
/* 472 */       Class componentType = javaType.getComponentType();
/*     */ 
/* 480 */       if (xmlType != null) {
/* 481 */         Class actualClass = start.getClassForQName(xmlType);
/* 482 */         if ((actualClass == componentType) || ((actualClass != null) && ((componentType.isAssignableFrom(actualClass)) || (Utils.getWrapperType(actualClass.getName()).equals(componentType.getName())))))
/*     */         {
/* 485 */           return null;
/*     */         }
/*     */       }
/* 488 */       Pair pair = (Pair)this.qName2Pair.get(Constants.SOAP_ARRAY);
/* 489 */       df = (DeserializerFactory)this.pair2DF.get(pair);
/* 490 */       if (((df instanceof ArrayDeserializerFactory)) && (javaType.isArray())) {
/* 491 */         QName componentXmlType = start.getTypeQName(componentType);
/* 492 */         if (componentXmlType != null) {
/* 493 */           df = new ArrayDeserializerFactory(componentXmlType);
/*     */         }
/*     */       }
/*     */     }
/* 497 */     return df;
/*     */   }
/*     */ 
/*     */   public void removeSerializer(Class javaType, QName xmlType)
/*     */     throws JAXRPCException
/*     */   {
/* 512 */     if ((javaType == null) || (xmlType == null)) {
/* 513 */       throw new JAXRPCException(Messages.getMessage(javaType == null ? "badJavaType" : "badXmlType"));
/*     */     }
/*     */ 
/* 518 */     Pair pair = new Pair(javaType, xmlType);
/* 519 */     this.pair2SF.remove(pair);
/*     */   }
/*     */ 
/*     */   public void removeDeserializer(Class javaType, QName xmlType)
/*     */     throws JAXRPCException
/*     */   {
/* 534 */     if ((javaType == null) || (xmlType == null)) {
/* 535 */       throw new JAXRPCException(Messages.getMessage(javaType == null ? "badJavaType" : "badXmlType"));
/*     */     }
/*     */ 
/* 539 */     Pair pair = new Pair(javaType, xmlType);
/* 540 */     this.pair2DF.remove(pair);
/*     */   }
/*     */ 
/*     */   public QName getTypeQNameRecursive(Class javaType)
/*     */   {
/* 552 */     QName ret = null;
/* 553 */     while (javaType != null) {
/* 554 */       ret = getTypeQName(javaType, null);
/* 555 */       if (ret != null) {
/* 556 */         return ret;
/*     */       }
/*     */ 
/* 559 */       Class[] interfaces = javaType.getInterfaces();
/* 560 */       if (interfaces != null) {
/* 561 */         for (int i = 0; i < interfaces.length; i++) {
/* 562 */           Class iface = interfaces[i];
/* 563 */           ret = getTypeQName(iface, null);
/* 564 */           if (ret != null) {
/* 565 */             return ret;
/*     */           }
/*     */         }
/*     */       }
/* 569 */       javaType = javaType.getSuperclass();
/*     */     }
/* 571 */     return null;
/*     */   }
/*     */ 
/*     */   public QName getTypeQNameExact(Class javaType, TypeMappingDelegate next)
/*     */   {
/* 583 */     if (javaType == null) {
/* 584 */       return null;
/*     */     }
/* 586 */     QName xmlType = null;
/* 587 */     Pair pair = (Pair)this.class2Pair.get(javaType);
/*     */ 
/* 589 */     if ((isDotNetSoapEncFixNeeded()) && (pair != null))
/*     */     {
/* 593 */       xmlType = pair.xmlType;
/* 594 */       if ((Constants.isSOAP_ENC(xmlType.getNamespaceURI())) && (!xmlType.getLocalPart().equals("Array")))
/*     */       {
/* 596 */         pair = null;
/*     */       }
/*     */     }
/*     */ 
/* 600 */     if ((pair == null) && (next != null))
/*     */     {
/* 602 */       xmlType = next.delegate.getTypeQNameExact(javaType, next.next);
/*     */     }
/*     */ 
/* 606 */     if (pair != null) {
/* 607 */       xmlType = pair.xmlType;
/*     */     }
/*     */ 
/* 610 */     return xmlType;
/*     */   }
/*     */ 
/*     */   private boolean isDotNetSoapEncFixNeeded()
/*     */   {
/* 619 */     MessageContext msgContext = MessageContext.getCurrentContext();
/* 620 */     if (msgContext != null) {
/* 621 */       SOAPService service = msgContext.getService();
/* 622 */       if (service != null) {
/* 623 */         String dotNetSoapEncFix = (String)service.getOption("dotNetSoapEncFix");
/* 624 */         if (dotNetSoapEncFix != null) {
/* 625 */           return JavaUtils.isTrue(dotNetSoapEncFix);
/*     */         }
/*     */       }
/*     */     }
/* 629 */     return dotnet_soapenc_bugfix;
/*     */   }
/*     */ 
/*     */   public QName getTypeQName(Class javaType, TypeMappingDelegate next) {
/* 633 */     QName xmlType = getTypeQNameExact(javaType, next);
/*     */ 
/* 640 */     if ((shouldDoAutoTypes()) && (javaType != List.class) && (!List.class.isAssignableFrom(javaType)) && (xmlType != null) && (xmlType.equals(Constants.SOAP_ARRAY)))
/*     */     {
/* 646 */       xmlType = new QName(Namespaces.makeNamespace(javaType.getName()), Types.getLocalNameFromFullName(javaType.getName()));
/*     */ 
/* 650 */       internalRegister(javaType, xmlType, new ArraySerializerFactory(), new ArrayDeserializerFactory());
/*     */     }
/*     */ 
/* 657 */     if ((xmlType == null) && (isArray(javaType)))
/*     */     {
/* 660 */       Pair pair = (Pair)this.class2Pair.get(new Object[0].getClass());
/*     */ 
/* 664 */       if (pair != null)
/* 665 */         xmlType = pair.xmlType;
/*     */       else {
/* 667 */         xmlType = Constants.SOAP_ARRAY;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 674 */     if ((xmlType == null) && (shouldDoAutoTypes()))
/*     */     {
/* 676 */       xmlType = new QName(Namespaces.makeNamespace(javaType.getName()), Types.getLocalNameFromFullName(javaType.getName()));
/*     */ 
/* 685 */       internalRegister(javaType, xmlType, new BeanSerializerFactory(javaType, xmlType), new BeanDeserializerFactory(javaType, xmlType));
/*     */     }
/*     */ 
/* 692 */     return xmlType;
/*     */   }
/*     */ 
/*     */   public Class getClassForQName(QName xmlType, Class javaType, TypeMappingDelegate next)
/*     */   {
/* 697 */     if (xmlType == null) {
/* 698 */       return null;
/*     */     }
/*     */ 
/* 703 */     if (javaType != null)
/*     */     {
/* 705 */       Pair pair = new Pair(javaType, xmlType);
/* 706 */       if ((this.pair2DF.get(pair) == null) && 
/* 707 */         (next != null)) {
/* 708 */         javaType = next.getClassForQName(xmlType, javaType);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 713 */     if (javaType == null)
/*     */     {
/* 715 */       Pair pair = (Pair)this.qName2Pair.get(xmlType);
/* 716 */       if ((pair == null) && (next != null))
/*     */       {
/* 718 */         javaType = next.getClassForQName(xmlType);
/* 719 */       } else if (pair != null) {
/* 720 */         javaType = pair.javaType;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 725 */     if ((javaType == null) && (shouldDoAutoTypes())) {
/* 726 */       String pkg = Namespaces.getPackage(xmlType.getNamespaceURI());
/* 727 */       if (pkg != null) {
/* 728 */         String className = xmlType.getLocalPart();
/* 729 */         if (pkg.length() > 0)
/* 730 */           className = pkg + "." + className;
/*     */         try
/*     */         {
/* 733 */           javaType = ClassUtils.forName(className);
/* 734 */           internalRegister(javaType, xmlType, new BeanSerializerFactory(javaType, xmlType), new BeanDeserializerFactory(javaType, xmlType));
/*     */         }
/*     */         catch (ClassNotFoundException e)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 742 */     return javaType;
/*     */   }
/*     */ 
/*     */   public void setDoAutoTypes(boolean doAutoTypes) {
/* 746 */     this.doAutoTypes = (doAutoTypes ? Boolean.TRUE : Boolean.FALSE);
/*     */   }
/*     */ 
/*     */   public boolean shouldDoAutoTypes() {
/* 750 */     if (this.doAutoTypes != null) {
/* 751 */       return this.doAutoTypes.booleanValue();
/*     */     }
/* 753 */     MessageContext msgContext = MessageContext.getCurrentContext();
/* 754 */     if ((msgContext != null) && (
/* 755 */       (msgContext.isPropertyTrue("axis.doAutoTypes")) || ((msgContext.getAxisEngine() != null) && (JavaUtils.isTrue(msgContext.getAxisEngine().getOption("axis.doAutoTypes"))))))
/*     */     {
/* 757 */       this.doAutoTypes = Boolean.TRUE;
/*     */     }
/*     */ 
/* 760 */     if (this.doAutoTypes == null) {
/* 761 */       this.doAutoTypes = (AxisProperties.getProperty("axis.doAutoTypes", "false").equals("true") ? Boolean.TRUE : Boolean.FALSE);
/*     */     }
/*     */ 
/* 766 */     return this.doAutoTypes.booleanValue();
/*     */   }
/*     */ 
/*     */   public Class[] getAllClasses(TypeMappingDelegate next)
/*     */   {
/* 774 */     HashSet temp = new HashSet();
/* 775 */     if (next != null)
/*     */     {
/* 777 */       temp.addAll(Arrays.asList(next.getAllClasses()));
/*     */     }
/* 779 */     temp.addAll(this.class2Pair.keySet());
/* 780 */     return (Class[])temp.toArray(new Class[temp.size()]);
/*     */   }
/*     */ 
/*     */   public static class Pair
/*     */     implements Serializable
/*     */   {
/*     */     public Class javaType;
/*     */     public QName xmlType;
/*     */ 
/*     */     public Pair(Class javaType, QName xmlType)
/*     */     {
/*  92 */       this.javaType = javaType;
/*  93 */       this.xmlType = xmlType;
/*     */     }
/*     */     public boolean equals(Object o) {
/*  96 */       if (o == null) return false;
/*  97 */       Pair p = (Pair)o;
/*     */ 
/*  99 */       if ((p.xmlType == this.xmlType) && (p.javaType == this.javaType))
/*     */       {
/* 101 */         return true;
/*     */       }
/* 103 */       return (p.xmlType.equals(this.xmlType)) && (p.javaType.equals(this.javaType));
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 107 */       int hashcode = 0;
/* 108 */       if (this.javaType != null) {
/* 109 */         hashcode ^= this.javaType.hashCode();
/*     */       }
/* 111 */       if (this.xmlType != null) {
/* 112 */         hashcode ^= this.xmlType.hashCode();
/*     */       }
/* 114 */       return hashcode;
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.TypeMappingImpl
 * JD-Core Version:    0.6.0
 */