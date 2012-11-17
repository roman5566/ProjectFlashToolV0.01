/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.activation.DataHandler;
/*    */ import javax.mail.internet.MimeMultipart;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.attachments.MimeMultipartDataSource;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.encoding.SerializationContext;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.xml.sax.Attributes;
/*    */ 
/*    */ public class MimeMultipartDataHandlerSerializer extends JAFDataHandlerSerializer
/*    */ {
/* 36 */   protected static Log log = LogFactory.getLog(MimeMultipartDataHandlerSerializer.class.getName());
/*    */ 
/*    */   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context)
/*    */     throws IOException
/*    */   {
/* 46 */     if (value != null) {
/* 47 */       DataHandler dh = new DataHandler(new MimeMultipartDataSource("Multipart", (MimeMultipart)value));
/* 48 */       super.serialize(name, attributes, dh, context);
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.MimeMultipartDataHandlerSerializer
 * JD-Core Version:    0.6.0
 */