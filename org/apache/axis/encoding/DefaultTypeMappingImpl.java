/*     */ package org.apache.axis.encoding;
/*     */ 
/*     */ import java.awt.Image;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Map;
/*     */ import java.util.Vector;
/*     */ import javax.activation.DataHandler;
/*     */ import javax.mail.internet.MimeMultipart;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.rpc.JAXRPCException;
/*     */ import javax.xml.rpc.encoding.DeserializerFactory;
/*     */ import javax.xml.transform.Source;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.attachments.OctetStream;
/*     */ import org.apache.axis.encoding.ser.ArrayDeserializerFactory;
/*     */ import org.apache.axis.encoding.ser.ArraySerializerFactory;
/*     */ import org.apache.axis.encoding.ser.Base64DeserializerFactory;
/*     */ import org.apache.axis.encoding.ser.Base64SerializerFactory;
/*     */ import org.apache.axis.encoding.ser.BeanDeserializerFactory;
/*     */ import org.apache.axis.encoding.ser.BeanSerializerFactory;
/*     */ import org.apache.axis.encoding.ser.DateDeserializerFactory;
/*     */ import org.apache.axis.encoding.ser.DateSerializerFactory;
/*     */ import org.apache.axis.encoding.ser.DocumentDeserializerFactory;
/*     */ import org.apache.axis.encoding.ser.DocumentSerializerFactory;
/*     */ import org.apache.axis.encoding.ser.ElementDeserializerFactory;
/*     */ import org.apache.axis.encoding.ser.ElementSerializerFactory;
/*     */ import org.apache.axis.encoding.ser.HexDeserializerFactory;
/*     */ import org.apache.axis.encoding.ser.HexSerializerFactory;
/*     */ import org.apache.axis.encoding.ser.JAFDataHandlerDeserializerFactory;
/*     */ import org.apache.axis.encoding.ser.JAFDataHandlerSerializerFactory;
/*     */ import org.apache.axis.encoding.ser.MapDeserializerFactory;
/*     */ import org.apache.axis.encoding.ser.MapSerializerFactory;
/*     */ import org.apache.axis.encoding.ser.QNameDeserializerFactory;
/*     */ import org.apache.axis.encoding.ser.QNameSerializerFactory;
/*     */ import org.apache.axis.encoding.ser.SimpleDeserializerFactory;
/*     */ import org.apache.axis.encoding.ser.SimpleSerializerFactory;
/*     */ import org.apache.axis.encoding.ser.VectorDeserializerFactory;
/*     */ import org.apache.axis.encoding.ser.VectorSerializerFactory;
/*     */ import org.apache.axis.schema.SchemaVersion;
/*     */ import org.apache.axis.types.Day;
/*     */ import org.apache.axis.types.Duration;
/*     */ import org.apache.axis.types.Entities;
/*     */ import org.apache.axis.types.Entity;
/*     */ import org.apache.axis.types.HexBinary;
/*     */ import org.apache.axis.types.IDRef;
/*     */ import org.apache.axis.types.IDRefs;
/*     */ import org.apache.axis.types.Id;
/*     */ import org.apache.axis.types.Language;
/*     */ import org.apache.axis.types.Month;
/*     */ import org.apache.axis.types.MonthDay;
/*     */ import org.apache.axis.types.NCName;
/*     */ import org.apache.axis.types.NMToken;
/*     */ import org.apache.axis.types.NMTokens;
/*     */ import org.apache.axis.types.Name;
/*     */ import org.apache.axis.types.NegativeInteger;
/*     */ import org.apache.axis.types.NonNegativeInteger;
/*     */ import org.apache.axis.types.NonPositiveInteger;
/*     */ import org.apache.axis.types.NormalizedString;
/*     */ import org.apache.axis.types.Notation;
/*     */ import org.apache.axis.types.PositiveInteger;
/*     */ import org.apache.axis.types.Schema;
/*     */ import org.apache.axis.types.Time;
/*     */ import org.apache.axis.types.Token;
/*     */ import org.apache.axis.types.URI;
/*     */ import org.apache.axis.types.UnsignedByte;
/*     */ import org.apache.axis.types.UnsignedInt;
/*     */ import org.apache.axis.types.UnsignedLong;
/*     */ import org.apache.axis.types.UnsignedShort;
/*     */ import org.apache.axis.types.Year;
/*     */ import org.apache.axis.types.YearMonth;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ 
/*     */ public class DefaultTypeMappingImpl extends TypeMappingImpl
/*     */ {
/*  77 */   private static DefaultTypeMappingImpl tm = null;
/*  78 */   private boolean inInitMappings = false;
/*     */ 
/*     */   public static synchronized TypeMappingDelegate getSingletonDelegate()
/*     */   {
/*  84 */     if (tm == null) {
/*  85 */       tm = new DefaultTypeMappingImpl();
/*     */     }
/*  87 */     return new TypeMappingDelegate(tm);
/*     */   }
/*     */ 
/*     */   protected DefaultTypeMappingImpl() {
/*  91 */     initMappings();
/*     */   }
/*     */ 
/*     */   protected DefaultTypeMappingImpl(boolean noMappings) {
/*  95 */     if (!noMappings)
/*  96 */       initMappings();
/*     */   }
/*     */ 
/*     */   protected void initMappings()
/*     */   {
/* 101 */     this.inInitMappings = true;
/*     */ 
/* 119 */     if (JavaUtils.isAttachmentSupported()) {
/* 120 */       myRegister(Constants.MIME_PLAINTEXT, String.class, new JAFDataHandlerSerializerFactory(String.class, Constants.MIME_PLAINTEXT), new JAFDataHandlerDeserializerFactory(String.class, Constants.MIME_PLAINTEXT));
/*     */     }
/*     */ 
/* 130 */     myRegister(Constants.XSD_HEXBIN, HexBinary.class, new HexSerializerFactory(HexBinary.class, Constants.XSD_HEXBIN), new HexDeserializerFactory(HexBinary.class, Constants.XSD_HEXBIN));
/*     */ 
/* 135 */     myRegister(Constants.XSD_HEXBIN, new byte[0].getClass(), new HexSerializerFactory(new byte[0].getClass(), Constants.XSD_HEXBIN), new HexDeserializerFactory(new byte[0].getClass(), Constants.XSD_HEXBIN));
/*     */ 
/* 154 */     myRegister(Constants.XSD_BYTE, new byte[0].getClass(), new ArraySerializerFactory(), null);
/*     */ 
/* 159 */     myRegister(Constants.XSD_BASE64, new byte[0].getClass(), new Base64SerializerFactory(new byte[0].getClass(), Constants.XSD_BASE64), new Base64DeserializerFactory(new byte[0].getClass(), Constants.XSD_BASE64));
/*     */ 
/* 166 */     myRegisterSimple(Constants.XSD_ANYSIMPLETYPE, String.class);
/*     */ 
/* 169 */     myRegisterSimple(Constants.XSD_STRING, String.class);
/* 170 */     myRegisterSimple(Constants.XSD_BOOLEAN, Boolean.class);
/* 171 */     myRegisterSimple(Constants.XSD_DOUBLE, Double.class);
/* 172 */     myRegisterSimple(Constants.XSD_FLOAT, Float.class);
/* 173 */     myRegisterSimple(Constants.XSD_INT, Integer.class);
/* 174 */     myRegisterSimple(Constants.XSD_INTEGER, BigInteger.class);
/*     */ 
/* 176 */     myRegisterSimple(Constants.XSD_DECIMAL, BigDecimal.class);
/*     */ 
/* 178 */     myRegisterSimple(Constants.XSD_LONG, Long.class);
/* 179 */     myRegisterSimple(Constants.XSD_SHORT, Short.class);
/* 180 */     myRegisterSimple(Constants.XSD_BYTE, Byte.class);
/*     */ 
/* 183 */     myRegisterSimple(Constants.XSD_BOOLEAN, Boolean.TYPE);
/* 184 */     myRegisterSimple(Constants.XSD_DOUBLE, Double.TYPE);
/* 185 */     myRegisterSimple(Constants.XSD_FLOAT, Float.TYPE);
/* 186 */     myRegisterSimple(Constants.XSD_INT, Integer.TYPE);
/* 187 */     myRegisterSimple(Constants.XSD_LONG, Long.TYPE);
/* 188 */     myRegisterSimple(Constants.XSD_SHORT, Short.TYPE);
/* 189 */     myRegisterSimple(Constants.XSD_BYTE, Byte.TYPE);
/*     */ 
/* 192 */     myRegister(Constants.XSD_QNAME, QName.class, new QNameSerializerFactory(QName.class, Constants.XSD_QNAME), new QNameDeserializerFactory(QName.class, Constants.XSD_QNAME));
/*     */ 
/* 201 */     myRegister(Constants.XSD_ANYTYPE, Object.class, null, null);
/*     */ 
/* 206 */     myRegister(Constants.XSD_DATE, java.sql.Date.class, new DateSerializerFactory(java.sql.Date.class, Constants.XSD_DATE), new DateDeserializerFactory(java.sql.Date.class, Constants.XSD_DATE));
/*     */ 
/* 215 */     myRegister(Constants.XSD_DATE, java.util.Date.class, new DateSerializerFactory(java.util.Date.class, Constants.XSD_DATE), new DateDeserializerFactory(java.util.Date.class, Constants.XSD_DATE));
/*     */ 
/* 223 */     myRegister(Constants.XSD_TIME, Time.class, new SimpleSerializerFactory(Time.class, Constants.XSD_TIME), new SimpleDeserializerFactory(Time.class, Constants.XSD_TIME));
/*     */ 
/* 230 */     myRegister(Constants.XSD_YEARMONTH, YearMonth.class, new SimpleSerializerFactory(YearMonth.class, Constants.XSD_YEARMONTH), new SimpleDeserializerFactory(YearMonth.class, Constants.XSD_YEARMONTH));
/*     */ 
/* 236 */     myRegister(Constants.XSD_YEAR, Year.class, new SimpleSerializerFactory(Year.class, Constants.XSD_YEAR), new SimpleDeserializerFactory(Year.class, Constants.XSD_YEAR));
/*     */ 
/* 242 */     myRegister(Constants.XSD_MONTH, Month.class, new SimpleSerializerFactory(Month.class, Constants.XSD_MONTH), new SimpleDeserializerFactory(Month.class, Constants.XSD_MONTH));
/*     */ 
/* 248 */     myRegister(Constants.XSD_DAY, Day.class, new SimpleSerializerFactory(Day.class, Constants.XSD_DAY), new SimpleDeserializerFactory(Day.class, Constants.XSD_DAY));
/*     */ 
/* 254 */     myRegister(Constants.XSD_MONTHDAY, MonthDay.class, new SimpleSerializerFactory(MonthDay.class, Constants.XSD_MONTHDAY), new SimpleDeserializerFactory(MonthDay.class, Constants.XSD_MONTHDAY));
/*     */ 
/* 263 */     myRegister(Constants.SOAP_MAP, Hashtable.class, new MapSerializerFactory(Hashtable.class, Constants.SOAP_MAP), null);
/*     */ 
/* 268 */     myRegister(Constants.SOAP_MAP, Map.class, new MapSerializerFactory(Map.class, Constants.SOAP_MAP), null);
/*     */ 
/* 274 */     myRegister(Constants.SOAP_MAP, HashMap.class, new MapSerializerFactory(Map.class, Constants.SOAP_MAP), new MapDeserializerFactory(HashMap.class, Constants.SOAP_MAP));
/*     */ 
/* 282 */     myRegister(Constants.SOAP_ELEMENT, Element.class, new ElementSerializerFactory(), new ElementDeserializerFactory());
/*     */ 
/* 287 */     myRegister(Constants.SOAP_DOCUMENT, Document.class, new DocumentSerializerFactory(), new DocumentDeserializerFactory());
/*     */ 
/* 291 */     myRegister(Constants.SOAP_VECTOR, Vector.class, new VectorSerializerFactory(Vector.class, Constants.SOAP_VECTOR), new VectorDeserializerFactory(Vector.class, Constants.SOAP_VECTOR));
/*     */ 
/* 300 */     if (JavaUtils.isAttachmentSupported()) {
/* 301 */       myRegister(Constants.MIME_IMAGE, Image.class, new JAFDataHandlerSerializerFactory(Image.class, Constants.MIME_IMAGE), new JAFDataHandlerDeserializerFactory(Image.class, Constants.MIME_IMAGE));
/*     */ 
/* 308 */       myRegister(Constants.MIME_MULTIPART, MimeMultipart.class, new JAFDataHandlerSerializerFactory(MimeMultipart.class, Constants.MIME_MULTIPART), new JAFDataHandlerDeserializerFactory(MimeMultipart.class, Constants.MIME_MULTIPART));
/*     */ 
/* 315 */       myRegister(Constants.MIME_SOURCE, Source.class, new JAFDataHandlerSerializerFactory(Source.class, Constants.MIME_SOURCE), new JAFDataHandlerDeserializerFactory(Source.class, Constants.MIME_SOURCE));
/*     */ 
/* 322 */       myRegister(Constants.MIME_OCTETSTREAM, OctetStream.class, new JAFDataHandlerSerializerFactory(OctetStream.class, Constants.MIME_OCTETSTREAM), new JAFDataHandlerDeserializerFactory(OctetStream.class, Constants.MIME_OCTETSTREAM));
/*     */ 
/* 329 */       myRegister(Constants.MIME_DATA_HANDLER, DataHandler.class, new JAFDataHandlerSerializerFactory(), new JAFDataHandlerDeserializerFactory());
/*     */     }
/*     */ 
/* 335 */     myRegister(Constants.XSD_TOKEN, Token.class, new SimpleSerializerFactory(Token.class, Constants.XSD_TOKEN), new SimpleDeserializerFactory(Token.class, Constants.XSD_TOKEN));
/*     */ 
/* 343 */     myRegister(Constants.XSD_NORMALIZEDSTRING, NormalizedString.class, new SimpleSerializerFactory(NormalizedString.class, Constants.XSD_NORMALIZEDSTRING), new SimpleDeserializerFactory(NormalizedString.class, Constants.XSD_NORMALIZEDSTRING));
/*     */ 
/* 351 */     myRegister(Constants.XSD_UNSIGNEDLONG, UnsignedLong.class, new SimpleSerializerFactory(UnsignedLong.class, Constants.XSD_UNSIGNEDLONG), new SimpleDeserializerFactory(UnsignedLong.class, Constants.XSD_UNSIGNEDLONG));
/*     */ 
/* 359 */     myRegister(Constants.XSD_UNSIGNEDINT, UnsignedInt.class, new SimpleSerializerFactory(UnsignedInt.class, Constants.XSD_UNSIGNEDINT), new SimpleDeserializerFactory(UnsignedInt.class, Constants.XSD_UNSIGNEDINT));
/*     */ 
/* 367 */     myRegister(Constants.XSD_UNSIGNEDSHORT, UnsignedShort.class, new SimpleSerializerFactory(UnsignedShort.class, Constants.XSD_UNSIGNEDSHORT), new SimpleDeserializerFactory(UnsignedShort.class, Constants.XSD_UNSIGNEDSHORT));
/*     */ 
/* 375 */     myRegister(Constants.XSD_UNSIGNEDBYTE, UnsignedByte.class, new SimpleSerializerFactory(UnsignedByte.class, Constants.XSD_UNSIGNEDBYTE), new SimpleDeserializerFactory(UnsignedByte.class, Constants.XSD_UNSIGNEDBYTE));
/*     */ 
/* 383 */     myRegister(Constants.XSD_NONNEGATIVEINTEGER, NonNegativeInteger.class, new SimpleSerializerFactory(NonNegativeInteger.class, Constants.XSD_NONNEGATIVEINTEGER), new SimpleDeserializerFactory(NonNegativeInteger.class, Constants.XSD_NONNEGATIVEINTEGER));
/*     */ 
/* 391 */     myRegister(Constants.XSD_NEGATIVEINTEGER, NegativeInteger.class, new SimpleSerializerFactory(NegativeInteger.class, Constants.XSD_NEGATIVEINTEGER), new SimpleDeserializerFactory(NegativeInteger.class, Constants.XSD_NEGATIVEINTEGER));
/*     */ 
/* 399 */     myRegister(Constants.XSD_POSITIVEINTEGER, PositiveInteger.class, new SimpleSerializerFactory(PositiveInteger.class, Constants.XSD_POSITIVEINTEGER), new SimpleDeserializerFactory(PositiveInteger.class, Constants.XSD_POSITIVEINTEGER));
/*     */ 
/* 407 */     myRegister(Constants.XSD_NONPOSITIVEINTEGER, NonPositiveInteger.class, new SimpleSerializerFactory(NonPositiveInteger.class, Constants.XSD_NONPOSITIVEINTEGER), new SimpleDeserializerFactory(NonPositiveInteger.class, Constants.XSD_NONPOSITIVEINTEGER));
/*     */ 
/* 415 */     myRegister(Constants.XSD_NAME, Name.class, new SimpleSerializerFactory(Name.class, Constants.XSD_NAME), new SimpleDeserializerFactory(Name.class, Constants.XSD_NAME));
/*     */ 
/* 423 */     myRegister(Constants.XSD_NCNAME, NCName.class, new SimpleSerializerFactory(NCName.class, Constants.XSD_NCNAME), new SimpleDeserializerFactory(NCName.class, Constants.XSD_NCNAME));
/*     */ 
/* 431 */     myRegister(Constants.XSD_ID, Id.class, new SimpleSerializerFactory(Id.class, Constants.XSD_ID), new SimpleDeserializerFactory(Id.class, Constants.XSD_ID));
/*     */ 
/* 439 */     myRegister(Constants.XML_LANG, Language.class, new SimpleSerializerFactory(Language.class, Constants.XML_LANG), new SimpleDeserializerFactory(Language.class, Constants.XML_LANG));
/*     */ 
/* 447 */     myRegister(Constants.XSD_LANGUAGE, Language.class, new SimpleSerializerFactory(Language.class, Constants.XSD_LANGUAGE), new SimpleDeserializerFactory(Language.class, Constants.XSD_LANGUAGE));
/*     */ 
/* 455 */     myRegister(Constants.XSD_NMTOKEN, NMToken.class, new SimpleSerializerFactory(NMToken.class, Constants.XSD_NMTOKEN), new SimpleDeserializerFactory(NMToken.class, Constants.XSD_NMTOKEN));
/*     */ 
/* 463 */     myRegister(Constants.XSD_NMTOKENS, NMTokens.class, new SimpleSerializerFactory(NMTokens.class, Constants.XSD_NMTOKENS), new SimpleDeserializerFactory(NMTokens.class, Constants.XSD_NMTOKENS));
/*     */ 
/* 471 */     myRegister(Constants.XSD_NOTATION, Notation.class, new BeanSerializerFactory(Notation.class, Constants.XSD_NOTATION), new BeanDeserializerFactory(Notation.class, Constants.XSD_NOTATION));
/*     */ 
/* 479 */     myRegister(Constants.XSD_ENTITY, Entity.class, new SimpleSerializerFactory(Entity.class, Constants.XSD_ENTITY), new SimpleDeserializerFactory(Entity.class, Constants.XSD_ENTITY));
/*     */ 
/* 487 */     myRegister(Constants.XSD_ENTITIES, Entities.class, new SimpleSerializerFactory(Entities.class, Constants.XSD_ENTITIES), new SimpleDeserializerFactory(Entities.class, Constants.XSD_ENTITIES));
/*     */ 
/* 495 */     myRegister(Constants.XSD_IDREF, IDRef.class, new SimpleSerializerFactory(IDRef.class, Constants.XSD_IDREF), new SimpleDeserializerFactory(IDRef.class, Constants.XSD_IDREF));
/*     */ 
/* 503 */     myRegister(Constants.XSD_IDREFS, IDRefs.class, new SimpleSerializerFactory(IDRefs.class, Constants.XSD_IDREFS), new SimpleDeserializerFactory(IDRefs.class, Constants.XSD_IDREFS));
/*     */ 
/* 511 */     myRegister(Constants.XSD_DURATION, Duration.class, new SimpleSerializerFactory(Duration.class, Constants.XSD_DURATION), new SimpleDeserializerFactory(Duration.class, Constants.XSD_DURATION));
/*     */ 
/* 519 */     myRegister(Constants.XSD_ANYURI, URI.class, new SimpleSerializerFactory(URI.class, Constants.XSD_ANYURI), new SimpleDeserializerFactory(URI.class, Constants.XSD_ANYURI));
/*     */ 
/* 527 */     myRegister(Constants.XSD_SCHEMA, Schema.class, new BeanSerializerFactory(Schema.class, Constants.XSD_SCHEMA), new BeanDeserializerFactory(Schema.class, Constants.XSD_SCHEMA));
/*     */ 
/* 538 */     myRegister(Constants.SOAP_ARRAY, ArrayList.class, new ArraySerializerFactory(), new ArrayDeserializerFactory());
/*     */ 
/* 546 */     SchemaVersion.SCHEMA_1999.registerSchemaSpecificTypes(this);
/* 547 */     SchemaVersion.SCHEMA_2000.registerSchemaSpecificTypes(this);
/* 548 */     SchemaVersion.SCHEMA_2001.registerSchemaSpecificTypes(this);
/*     */ 
/* 550 */     this.inInitMappings = false;
/*     */   }
/*     */ 
/*     */   protected void myRegisterSimple(QName xmlType, Class javaType)
/*     */   {
/* 559 */     SerializerFactory sf = new SimpleSerializerFactory(javaType, xmlType);
/* 560 */     DeserializerFactory df = null;
/* 561 */     if (javaType != Object.class) {
/* 562 */       df = new SimpleDeserializerFactory(javaType, xmlType);
/*     */     }
/*     */ 
/* 565 */     myRegister(xmlType, javaType, sf, df);
/*     */   }
/*     */ 
/*     */   protected void myRegister(QName xmlType, Class javaType, SerializerFactory sf, DeserializerFactory df)
/*     */   {
/*     */     try
/*     */     {
/* 584 */       if (xmlType.getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema"))
/*     */       {
/* 586 */         for (int i = 0; i < Constants.URIS_SCHEMA_XSD.length; i++) {
/* 587 */           QName qName = new QName(Constants.URIS_SCHEMA_XSD[i], xmlType.getLocalPart());
/*     */ 
/* 589 */           super.internalRegister(javaType, qName, sf, df);
/*     */         }
/*     */       }
/* 592 */       else if (xmlType.getNamespaceURI().equals(Constants.URI_DEFAULT_SOAP_ENC))
/*     */       {
/* 594 */         for (int i = 0; i < Constants.URIS_SOAP_ENC.length; i++) {
/* 595 */           QName qName = new QName(Constants.URIS_SOAP_ENC[i], xmlType.getLocalPart());
/*     */ 
/* 597 */           super.internalRegister(javaType, qName, sf, df);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 602 */         super.internalRegister(javaType, xmlType, sf, df);
/*     */       }
/*     */     }
/*     */     catch (JAXRPCException e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public void register(Class javaType, QName xmlType, javax.xml.rpc.encoding.SerializerFactory sf, DeserializerFactory dsf)
/*     */     throws JAXRPCException
/*     */   {
/* 613 */     super.register(javaType, xmlType, sf, dsf);
/*     */   }
/*     */ 
/*     */   public void removeSerializer(Class javaType, QName xmlType) throws JAXRPCException {
/* 617 */     throw new JAXRPCException(Messages.getMessage("fixedTypeMapping"));
/*     */   }
/*     */ 
/*     */   public void removeDeserializer(Class javaType, QName xmlType) throws JAXRPCException {
/* 621 */     throw new JAXRPCException(Messages.getMessage("fixedTypeMapping"));
/*     */   }
/*     */ 
/*     */   public void setSupportedEncodings(String[] namespaceURIs)
/*     */   {
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.DefaultTypeMappingImpl
 * JD-Core Version:    0.6.0
 */