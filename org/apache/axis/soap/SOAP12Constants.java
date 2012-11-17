/*     */ package org.apache.axis.soap;
/*     */ 
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.Constants;
/*     */ 
/*     */ public class SOAP12Constants
/*     */   implements SOAPConstants
/*     */ {
/*  29 */   private static QName headerQName = new QName("http://www.w3.org/2003/05/soap-envelope", "Header");
/*     */ 
/*  31 */   private static QName bodyQName = new QName("http://www.w3.org/2003/05/soap-envelope", "Body");
/*     */ 
/*  33 */   private static QName faultQName = new QName("http://www.w3.org/2003/05/soap-envelope", "Fault");
/*     */ 
/*  35 */   private static QName roleQName = new QName("http://www.w3.org/2003/05/soap-envelope", "role");
/*     */   public static final String PROP_WEBMETHOD = "soap12.webmethod";
/*     */ 
/*     */   public String getEnvelopeURI()
/*     */   {
/*  44 */     return "http://www.w3.org/2003/05/soap-envelope";
/*     */   }
/*     */ 
/*     */   public String getEncodingURI() {
/*  48 */     return "http://www.w3.org/2003/05/soap-encoding";
/*     */   }
/*     */ 
/*     */   public QName getHeaderQName() {
/*  52 */     return headerQName;
/*     */   }
/*     */ 
/*     */   public QName getBodyQName() {
/*  56 */     return bodyQName;
/*     */   }
/*     */ 
/*     */   public QName getFaultQName() {
/*  60 */     return faultQName;
/*     */   }
/*     */ 
/*     */   public QName getRoleAttributeQName()
/*     */   {
/*  67 */     return roleQName;
/*     */   }
/*     */ 
/*     */   public String getContentType()
/*     */   {
/*  74 */     return "application/soap+xml";
/*     */   }
/*     */ 
/*     */   public String getNextRoleURI()
/*     */   {
/*  81 */     return "http://www.w3.org/2003/05/soap-envelope/role/next";
/*     */   }
/*     */ 
/*     */   public String getAttrHref()
/*     */   {
/*  88 */     return "ref";
/*     */   }
/*     */ 
/*     */   public String getAttrItemType()
/*     */   {
/*  95 */     return "itemType";
/*     */   }
/*     */ 
/*     */   public QName getVerMismatchFaultCodeQName()
/*     */   {
/* 102 */     return Constants.FAULT_SOAP12_VERSIONMISMATCH;
/*     */   }
/*     */ 
/*     */   public QName getMustunderstandFaultQName()
/*     */   {
/* 109 */     return Constants.FAULT_SOAP12_MUSTUNDERSTAND;
/*     */   }
/*     */ 
/*     */   public QName getArrayType()
/*     */   {
/* 116 */     return Constants.SOAP_ARRAY12;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.soap.SOAP12Constants
 * JD-Core Version:    0.6.0
 */