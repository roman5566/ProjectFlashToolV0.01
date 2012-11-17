/*    */ package org.apache.axis.soap;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import javax.xml.namespace.QName;
/*    */ 
/*    */ public abstract interface SOAPConstants extends Serializable
/*    */ {
/* 35 */   public static final SOAP11Constants SOAP11_CONSTANTS = new SOAP11Constants();
/*    */ 
/* 37 */   public static final SOAP12Constants SOAP12_CONSTANTS = new SOAP12Constants();
/*    */ 
/*    */   public abstract String getEnvelopeURI();
/*    */ 
/*    */   public abstract String getEncodingURI();
/*    */ 
/*    */   public abstract QName getFaultQName();
/*    */ 
/*    */   public abstract QName getHeaderQName();
/*    */ 
/*    */   public abstract QName getBodyQName();
/*    */ 
/*    */   public abstract QName getRoleAttributeQName();
/*    */ 
/*    */   public abstract String getContentType();
/*    */ 
/*    */   public abstract String getNextRoleURI();
/*    */ 
/*    */   public abstract String getAttrHref();
/*    */ 
/*    */   public abstract String getAttrItemType();
/*    */ 
/*    */   public abstract QName getVerMismatchFaultCodeQName();
/*    */ 
/*    */   public abstract QName getMustunderstandFaultQName();
/*    */ 
/*    */   public abstract QName getArrayType();
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.soap.SOAPConstants
 * JD-Core Version:    0.6.0
 */