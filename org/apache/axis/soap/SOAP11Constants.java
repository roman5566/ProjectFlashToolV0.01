/*     */ package org.apache.axis.soap;
/*     */ 
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.Constants;
/*     */ 
/*     */ public class SOAP11Constants
/*     */   implements SOAPConstants
/*     */ {
/*  29 */   private static QName headerQName = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Header");
/*     */ 
/*  31 */   private static QName bodyQName = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Body");
/*     */ 
/*  33 */   private static QName faultQName = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Fault");
/*     */ 
/*  35 */   private static QName roleQName = new QName("http://schemas.xmlsoap.org/soap/envelope/", "actor");
/*     */ 
/*     */   public String getEnvelopeURI()
/*     */   {
/*  39 */     return "http://schemas.xmlsoap.org/soap/envelope/";
/*     */   }
/*     */ 
/*     */   public String getEncodingURI() {
/*  43 */     return "http://schemas.xmlsoap.org/soap/encoding/";
/*     */   }
/*     */ 
/*     */   public QName getHeaderQName() {
/*  47 */     return headerQName;
/*     */   }
/*     */ 
/*     */   public QName getBodyQName() {
/*  51 */     return bodyQName;
/*     */   }
/*     */ 
/*     */   public QName getFaultQName() {
/*  55 */     return faultQName;
/*     */   }
/*     */ 
/*     */   public QName getRoleAttributeQName()
/*     */   {
/*  62 */     return roleQName;
/*     */   }
/*     */ 
/*     */   public String getContentType()
/*     */   {
/*  69 */     return "text/xml";
/*     */   }
/*     */ 
/*     */   public String getNextRoleURI()
/*     */   {
/*  76 */     return "http://schemas.xmlsoap.org/soap/actor/next";
/*     */   }
/*     */ 
/*     */   public String getAttrHref()
/*     */   {
/*  83 */     return "href";
/*     */   }
/*     */ 
/*     */   public String getAttrItemType()
/*     */   {
/*  90 */     return "arrayType";
/*     */   }
/*     */ 
/*     */   public QName getVerMismatchFaultCodeQName()
/*     */   {
/*  97 */     return Constants.FAULT_VERSIONMISMATCH;
/*     */   }
/*     */ 
/*     */   public QName getMustunderstandFaultQName()
/*     */   {
/* 104 */     return Constants.FAULT_MUSTUNDERSTAND;
/*     */   }
/*     */ 
/*     */   public QName getArrayType()
/*     */   {
/* 111 */     return Constants.SOAP_ARRAY;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.soap.SOAP11Constants
 * JD-Core Version:    0.6.0
 */