/*    */ package org.apache.axis.soap;
/*    */ 
/*    */ import javax.xml.soap.SOAPConnection;
/*    */ import javax.xml.soap.SOAPConnectionFactory;
/*    */ import javax.xml.soap.SOAPException;
/*    */ 
/*    */ public class SOAPConnectionFactoryImpl extends SOAPConnectionFactory
/*    */ {
/*    */   public SOAPConnection createConnection()
/*    */     throws SOAPException
/*    */   {
/* 34 */     return new SOAPConnectionImpl();
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.soap.SOAPConnectionFactoryImpl
 * JD-Core Version:    0.6.0
 */