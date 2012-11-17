/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.activation.DataHandler;
/*    */ import javax.mail.internet.MimeMultipart;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.encoding.DeserializationContext;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public class MimeMultipartDataHandlerDeserializer extends JAFDataHandlerDeserializer
/*    */ {
/* 33 */   protected static Log log = LogFactory.getLog(MimeMultipartDataHandlerDeserializer.class.getName());
/*    */ 
/*    */   public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*    */     throws SAXException
/*    */   {
/* 41 */     super.startElement(namespace, localName, prefix, attributes, context);
/*    */ 
/* 43 */     if ((getValue() instanceof DataHandler))
/*    */       try {
/* 45 */         DataHandler dh = (DataHandler)getValue();
/* 46 */         MimeMultipart mmp = new MimeMultipart(dh.getDataSource());
/* 47 */         if (mmp.getCount() == 0) {
/* 48 */           mmp = null;
/*    */         }
/* 50 */         setValue(mmp);
/*    */       }
/*    */       catch (Exception e) {
/* 53 */         throw new SAXException(e);
/*    */       }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.MimeMultipartDataHandlerDeserializer
 * JD-Core Version:    0.6.0
 */