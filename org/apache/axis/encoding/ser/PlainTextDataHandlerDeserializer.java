/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.activation.DataHandler;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.encoding.DeserializationContext;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public class PlainTextDataHandlerDeserializer extends JAFDataHandlerDeserializer
/*    */ {
/* 33 */   protected static Log log = LogFactory.getLog(PlainTextDataHandlerDeserializer.class.getName());
/*    */ 
/*    */   public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*    */     throws SAXException
/*    */   {
/* 41 */     super.startElement(namespace, localName, prefix, attributes, context);
/*    */ 
/* 43 */     if ((getValue() instanceof DataHandler))
/*    */       try {
/* 45 */         DataHandler dh = (DataHandler)getValue();
/* 46 */         setValue(dh.getContent());
/*    */       }
/*    */       catch (IOException ioe)
/*    */       {
/*    */       }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.PlainTextDataHandlerDeserializer
 * JD-Core Version:    0.6.0
 */