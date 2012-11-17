/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.awt.Image;
/*    */ import java.io.IOException;
/*    */ import javax.activation.DataHandler;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.attachments.ImageDataSource;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.encoding.SerializationContext;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.xml.sax.Attributes;
/*    */ 
/*    */ public class ImageDataHandlerSerializer extends JAFDataHandlerSerializer
/*    */ {
/* 36 */   protected static Log log = LogFactory.getLog(ImageDataHandlerSerializer.class.getName());
/*    */ 
/*    */   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context)
/*    */     throws IOException
/*    */   {
/* 46 */     DataHandler dh = new DataHandler(new ImageDataSource("source", (Image)value));
/*    */ 
/* 48 */     super.serialize(name, attributes, dh, context);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.ImageDataHandlerSerializer
 * JD-Core Version:    0.6.0
 */