/*    */ package org.apache.axis.soap;
/*    */ 
/*    */ import javax.xml.soap.Name;
/*    */ import javax.xml.soap.SOAPElement;
/*    */ import javax.xml.soap.SOAPException;
/*    */ import javax.xml.soap.SOAPFactory;
/*    */ import org.apache.axis.message.MessageElement;
/*    */ import org.apache.axis.message.PrefixedQName;
/*    */ 
/*    */ public class SOAPFactoryImpl extends SOAPFactory
/*    */ {
/*    */   public SOAPElement createElement(Name name)
/*    */     throws SOAPException
/*    */   {
/* 43 */     return new MessageElement(name);
/*    */   }
/*    */ 
/*    */   public SOAPElement createElement(String localName)
/*    */     throws SOAPException
/*    */   {
/* 57 */     return new MessageElement("", localName);
/*    */   }
/*    */ 
/*    */   public SOAPElement createElement(String localName, String prefix, String uri)
/*    */     throws SOAPException
/*    */   {
/* 77 */     return new MessageElement(localName, prefix, uri);
/*    */   }
/*    */ 
/*    */   public javax.xml.soap.Detail createDetail() throws SOAPException
/*    */   {
/* 82 */     return new org.apache.axis.message.Detail();
/*    */   }
/*    */ 
/*    */   public Name createName(String localName, String prefix, String uri) throws SOAPException
/*    */   {
/* 87 */     return new PrefixedQName(uri, localName, prefix);
/*    */   }
/*    */ 
/*    */   public Name createName(String localName) throws SOAPException
/*    */   {
/* 92 */     return new PrefixedQName("", localName, "");
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.soap.SOAPFactoryImpl
 * JD-Core Version:    0.6.0
 */