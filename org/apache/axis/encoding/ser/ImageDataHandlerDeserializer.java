/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.awt.Image;
/*    */ import java.io.InputStream;
/*    */ import javax.activation.DataHandler;
/*    */ import org.apache.axis.components.image.ImageIO;
/*    */ import org.apache.axis.components.image.ImageIOFactory;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.encoding.DeserializationContext;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public class ImageDataHandlerDeserializer extends JAFDataHandlerDeserializer
/*    */ {
/* 35 */   protected static Log log = LogFactory.getLog(ImageDataHandlerDeserializer.class.getName());
/*    */ 
/*    */   public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*    */     throws SAXException
/*    */   {
/* 43 */     super.startElement(namespace, localName, prefix, attributes, context);
/*    */ 
/* 45 */     if ((getValue() instanceof DataHandler))
/*    */       try {
/* 47 */         DataHandler dh = (DataHandler)getValue();
/*    */ 
/* 49 */         InputStream is = dh.getInputStream();
/* 50 */         Image image = ImageIOFactory.getImageIO().loadImage(is);
/* 51 */         setValue(image);
/*    */       }
/*    */       catch (Exception e)
/*    */       {
/*    */       }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.ImageDataHandlerDeserializer
 * JD-Core Version:    0.6.0
 */