/*     */ package org.apache.axis.encoding;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.encoding.ser.ArrayDeserializerFactory;
/*     */ import org.apache.axis.encoding.ser.ArraySerializerFactory;
/*     */ import org.apache.axis.encoding.ser.Base64DeserializerFactory;
/*     */ import org.apache.axis.encoding.ser.Base64SerializerFactory;
/*     */ 
/*     */ public class DefaultSOAPEncodingTypeMappingImpl extends DefaultTypeMappingImpl
/*     */ {
/*  34 */   private static DefaultSOAPEncodingTypeMappingImpl tm = null;
/*     */ 
/*     */   public static synchronized TypeMappingImpl getSingleton()
/*     */   {
/*  39 */     if (tm == null) {
/*  40 */       tm = new DefaultSOAPEncodingTypeMappingImpl();
/*     */     }
/*  42 */     return tm;
/*     */   }
/*     */ 
/*     */   public static TypeMappingDelegate createWithDelegate() {
/*  46 */     TypeMappingDelegate ret = new TypeMappingDelegate(new DefaultSOAPEncodingTypeMappingImpl());
/*  47 */     MessageContext mc = MessageContext.getCurrentContext();
/*  48 */     TypeMappingDelegate tm = null;
/*  49 */     if (mc != null)
/*  50 */       tm = (TypeMappingDelegate)mc.getTypeMappingRegistry().getDefaultTypeMapping();
/*     */     else {
/*  52 */       tm = DefaultTypeMappingImpl.getSingletonDelegate();
/*     */     }
/*  54 */     ret.setNext(tm);
/*  55 */     return ret;
/*     */   }
/*     */ 
/*     */   protected DefaultSOAPEncodingTypeMappingImpl() {
/*  59 */     super(true);
/*  60 */     registerSOAPTypes();
/*     */   }
/*     */ 
/*     */   private void registerSOAPTypes()
/*     */   {
/*  70 */     myRegisterSimple(Constants.SOAP_STRING, String.class);
/*  71 */     myRegisterSimple(Constants.SOAP_BOOLEAN, Boolean.class);
/*  72 */     myRegisterSimple(Constants.SOAP_DOUBLE, Double.class);
/*  73 */     myRegisterSimple(Constants.SOAP_FLOAT, Float.class);
/*  74 */     myRegisterSimple(Constants.SOAP_INT, Integer.class);
/*  75 */     myRegisterSimple(Constants.SOAP_INTEGER, BigInteger.class);
/*  76 */     myRegisterSimple(Constants.SOAP_DECIMAL, BigDecimal.class);
/*  77 */     myRegisterSimple(Constants.SOAP_LONG, Long.class);
/*  78 */     myRegisterSimple(Constants.SOAP_SHORT, Short.class);
/*  79 */     myRegisterSimple(Constants.SOAP_BYTE, Byte.class);
/*     */ 
/*  81 */     myRegister(Constants.SOAP_BASE64, new byte[0].getClass(), new Base64SerializerFactory(new byte[0].getClass(), Constants.SOAP_BASE64), new Base64DeserializerFactory(new byte[0].getClass(), Constants.SOAP_BASE64));
/*     */ 
/*  87 */     myRegister(Constants.SOAP_BASE64BINARY, new byte[0].getClass(), new Base64SerializerFactory(new byte[0].getClass(), Constants.SOAP_BASE64BINARY), new Base64DeserializerFactory(new byte[0].getClass(), Constants.SOAP_BASE64BINARY));
/*     */ 
/*  94 */     myRegister(Constants.SOAP_ARRAY12, Collection.class, new ArraySerializerFactory(), new ArrayDeserializerFactory());
/*     */ 
/*  98 */     myRegister(Constants.SOAP_ARRAY12, ArrayList.class, new ArraySerializerFactory(), new ArrayDeserializerFactory());
/*     */ 
/* 103 */     myRegister(Constants.SOAP_ARRAY12, new Object[0].getClass(), new ArraySerializerFactory(), new ArrayDeserializerFactory());
/*     */ 
/* 108 */     myRegister(Constants.SOAP_ARRAY, ArrayList.class, new ArraySerializerFactory(), new ArrayDeserializerFactory());
/*     */ 
/* 120 */     myRegister(Constants.SOAP_ARRAY, Collection.class, new ArraySerializerFactory(), new ArrayDeserializerFactory());
/*     */ 
/* 125 */     myRegister(Constants.SOAP_ARRAY, new Object[0].getClass(), new ArraySerializerFactory(), new ArrayDeserializerFactory());
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.DefaultSOAPEncodingTypeMappingImpl
 * JD-Core Version:    0.6.0
 */