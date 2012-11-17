/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import javax.activation.DataHandler;
/*    */ import org.apache.axis.attachments.OctetStream;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.encoding.DeserializationContext;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public class OctetStreamDataHandlerDeserializer extends JAFDataHandlerDeserializer
/*    */ {
/* 36 */   protected static Log log = LogFactory.getLog(OctetStreamDataHandlerDeserializer.class.getName());
/*    */ 
/*    */   public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*    */     throws SAXException
/*    */   {
/* 44 */     super.startElement(namespace, localName, prefix, attributes, context);
/*    */ 
/* 46 */     if ((getValue() instanceof DataHandler))
/*    */       try {
/* 48 */         DataHandler dh = (DataHandler)getValue();
/* 49 */         InputStream in = dh.getInputStream();
/* 50 */         ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 51 */         int byte1 = -1;
/* 52 */         while ((byte1 = in.read()) != -1)
/* 53 */           baos.write(byte1);
/* 54 */         OctetStream os = new OctetStream(baos.toByteArray());
/* 55 */         setValue(os);
/*    */       }
/*    */       catch (IOException ioe)
/*    */       {
/*    */       }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.OctetStreamDataHandlerDeserializer
 * JD-Core Version:    0.6.0
 */