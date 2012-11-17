/*     */ package org.apache.axis.encoding;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.util.Calendar;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.encoding.ser.CalendarDeserializerFactory;
/*     */ import org.apache.axis.encoding.ser.CalendarSerializerFactory;
/*     */ import org.apache.axis.encoding.ser.DateDeserializerFactory;
/*     */ import org.apache.axis.encoding.ser.DateSerializerFactory;
/*     */ import org.apache.axis.encoding.ser.TimeDeserializerFactory;
/*     */ import org.apache.axis.encoding.ser.TimeSerializerFactory;
/*     */ 
/*     */ public class DefaultJAXRPC11TypeMappingImpl extends DefaultTypeMappingImpl
/*     */ {
/*  33 */   private static DefaultJAXRPC11TypeMappingImpl tm = null;
/*     */ 
/*     */   public static synchronized TypeMappingImpl getSingleton()
/*     */   {
/*  39 */     if (tm == null) {
/*  40 */       tm = new DefaultJAXRPC11TypeMappingImpl();
/*     */     }
/*  42 */     return tm;
/*     */   }
/*     */ 
/*     */   protected DefaultJAXRPC11TypeMappingImpl() {
/*  46 */     registerXSDTypes();
/*     */   }
/*     */ 
/*     */   private void registerXSDTypes()
/*     */   {
/*  54 */     myRegisterSimple(Constants.XSD_UNSIGNEDINT, Long.class);
/*  55 */     myRegisterSimple(Constants.XSD_UNSIGNEDINT, Long.TYPE);
/*  56 */     myRegisterSimple(Constants.XSD_UNSIGNEDSHORT, Integer.class);
/*  57 */     myRegisterSimple(Constants.XSD_UNSIGNEDSHORT, Integer.TYPE);
/*  58 */     myRegisterSimple(Constants.XSD_UNSIGNEDBYTE, Short.class);
/*  59 */     myRegisterSimple(Constants.XSD_UNSIGNEDBYTE, Short.TYPE);
/*  60 */     myRegister(Constants.XSD_DATETIME, Calendar.class, new CalendarSerializerFactory(Calendar.class, Constants.XSD_DATETIME), new CalendarDeserializerFactory(Calendar.class, Constants.XSD_DATETIME));
/*     */ 
/*  65 */     myRegister(Constants.XSD_DATE, Calendar.class, new DateSerializerFactory(Calendar.class, Constants.XSD_DATE), new DateDeserializerFactory(Calendar.class, Constants.XSD_DATE));
/*     */ 
/*  70 */     myRegister(Constants.XSD_TIME, Calendar.class, new TimeSerializerFactory(Calendar.class, Constants.XSD_TIME), new TimeDeserializerFactory(Calendar.class, Constants.XSD_TIME));
/*     */     try
/*     */     {
/*  76 */       myRegisterSimple(Constants.XSD_ANYURI, Class.forName("java.net.URI"));
/*     */     }
/*     */     catch (ClassNotFoundException e) {
/*  79 */       myRegisterSimple(Constants.XSD_ANYURI, String.class);
/*     */     }
/*     */ 
/*  83 */     myRegisterSimple(Constants.XSD_DURATION, String.class);
/*  84 */     myRegisterSimple(Constants.XSD_YEARMONTH, String.class);
/*  85 */     myRegisterSimple(Constants.XSD_YEAR, String.class);
/*  86 */     myRegisterSimple(Constants.XSD_MONTHDAY, String.class);
/*  87 */     myRegisterSimple(Constants.XSD_DAY, String.class);
/*  88 */     myRegisterSimple(Constants.XSD_MONTH, String.class);
/*  89 */     myRegisterSimple(Constants.XSD_NORMALIZEDSTRING, String.class);
/*     */ 
/*  91 */     myRegisterSimple(Constants.XSD_TOKEN, String.class);
/*  92 */     myRegisterSimple(Constants.XSD_LANGUAGE, String.class);
/*  93 */     myRegisterSimple(Constants.XSD_NAME, String.class);
/*  94 */     myRegisterSimple(Constants.XSD_NCNAME, String.class);
/*  95 */     myRegisterSimple(Constants.XSD_ID, String.class);
/*  96 */     myRegisterSimple(Constants.XSD_NMTOKEN, String.class);
/*  97 */     myRegisterSimple(Constants.XSD_NMTOKENS, String.class);
/*  98 */     myRegisterSimple(Constants.XSD_STRING, String.class);
/*  99 */     myRegisterSimple(Constants.XSD_NONPOSITIVEINTEGER, BigInteger.class);
/*     */ 
/* 101 */     myRegisterSimple(Constants.XSD_NEGATIVEINTEGER, BigInteger.class);
/*     */ 
/* 103 */     myRegisterSimple(Constants.XSD_NONNEGATIVEINTEGER, BigInteger.class);
/*     */ 
/* 105 */     myRegisterSimple(Constants.XSD_UNSIGNEDLONG, BigInteger.class);
/*     */ 
/* 107 */     myRegisterSimple(Constants.XSD_POSITIVEINTEGER, BigInteger.class);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.DefaultJAXRPC11TypeMappingImpl
 * JD-Core Version:    0.6.0
 */