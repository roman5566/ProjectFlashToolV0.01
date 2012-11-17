/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.activation.DataHandler;
/*    */ import javax.xml.namespace.QName;
/*    */ import javax.xml.transform.stream.StreamSource;
/*    */ import org.apache.axis.attachments.SourceDataSource;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.encoding.SerializationContext;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.xml.sax.Attributes;
/*    */ 
/*    */ public class SourceDataHandlerSerializer extends JAFDataHandlerSerializer
/*    */ {
/* 37 */   protected static Log log = LogFactory.getLog(SourceDataHandlerSerializer.class.getName());
/*    */ 
/*    */   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context)
/*    */     throws IOException
/*    */   {
/* 47 */     if (value != null) {
/* 48 */       if (!(value instanceof StreamSource)) {
/* 49 */         throw new IOException(Messages.getMessage("badSource", value.getClass().getName()));
/*    */       }
/*    */ 
/* 52 */       DataHandler dh = new DataHandler(new SourceDataSource("source", "text/xml", (StreamSource)value));
/*    */ 
/* 54 */       super.serialize(name, attributes, dh, context);
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.SourceDataHandlerSerializer
 * JD-Core Version:    0.6.0
 */