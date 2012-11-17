/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.activation.DataHandler;
/*    */ import javax.xml.transform.stream.StreamSource;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.encoding.DeserializationContext;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public class SourceDataHandlerDeserializer extends JAFDataHandlerDeserializer
/*    */ {
/* 35 */   protected static Log log = LogFactory.getLog(SourceDataHandlerDeserializer.class.getName());
/*    */ 
/*    */   public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*    */     throws SAXException
/*    */   {
/* 43 */     super.startElement(namespace, localName, prefix, attributes, context);
/*    */ 
/* 45 */     if ((getValue() instanceof DataHandler))
/*    */       try {
/* 47 */         DataHandler dh = (DataHandler)getValue();
/* 48 */         StreamSource ss = new StreamSource(dh.getInputStream());
/* 49 */         setValue(ss);
/*    */       }
/*    */       catch (IOException ioe)
/*    */       {
/*    */       }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.SourceDataHandlerDeserializer
 * JD-Core Version:    0.6.0
 */