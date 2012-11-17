/*    */ package org.apache.axis.message;
/*    */ 
/*    */ import org.apache.axis.encoding.DeserializationContext;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public class EnvelopeHandler extends SOAPHandler
/*    */ {
/*    */   SOAPHandler realHandler;
/*    */ 
/*    */   public EnvelopeHandler(SOAPHandler realHandler)
/*    */   {
/* 33 */     this.realHandler = realHandler;
/*    */   }
/*    */ 
/*    */   public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*    */     throws SAXException
/*    */   {
/* 43 */     return this.realHandler;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.EnvelopeHandler
 * JD-Core Version:    0.6.0
 */