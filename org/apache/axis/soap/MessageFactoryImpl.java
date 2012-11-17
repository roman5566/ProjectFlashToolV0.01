/*    */ package org.apache.axis.soap;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import javax.xml.soap.MessageFactory;
/*    */ import javax.xml.soap.MimeHeaders;
/*    */ import javax.xml.soap.SOAPException;
/*    */ import javax.xml.soap.SOAPMessage;
/*    */ import org.apache.axis.Message;
/*    */ import org.apache.axis.message.SOAPEnvelope;
/*    */ 
/*    */ public class MessageFactoryImpl extends MessageFactory
/*    */ {
/*    */   public SOAPMessage createMessage()
/*    */     throws SOAPException
/*    */   {
/* 52 */     SOAPEnvelope env = new SOAPEnvelope();
/* 53 */     env.setSAAJEncodingCompliance(true);
/* 54 */     Message message = new Message(env);
/* 55 */     message.setMessageType("request");
/* 56 */     return message;
/*    */   }
/*    */ 
/*    */   public SOAPMessage createMessage(MimeHeaders mimeheaders, InputStream inputstream)
/*    */     throws IOException, SOAPException
/*    */   {
/* 77 */     Message message = new Message(inputstream, false, mimeheaders);
/* 78 */     message.setMessageType("request");
/* 79 */     return message;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.soap.MessageFactoryImpl
 * JD-Core Version:    0.6.0
 */