/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.activation.DataHandler;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.attachments.PlainTextDataSource;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.encoding.SerializationContext;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.xml.sax.Attributes;
/*    */ 
/*    */ public class PlainTextDataHandlerSerializer extends JAFDataHandlerSerializer
/*    */ {
/* 35 */   protected static Log log = LogFactory.getLog(PlainTextDataHandlerSerializer.class.getName());
/*    */ 
/*    */   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context)
/*    */     throws IOException
/*    */   {
/* 45 */     DataHandler dh = new DataHandler(new PlainTextDataSource("source", (String)value));
/*    */ 
/* 47 */     super.serialize(name, attributes, dh, context);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.PlainTextDataHandlerSerializer
 * JD-Core Version:    0.6.0
 */