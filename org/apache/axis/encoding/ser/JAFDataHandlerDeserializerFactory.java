/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.awt.Image;
/*    */ import javax.mail.internet.MimeMultipart;
/*    */ import javax.xml.namespace.QName;
/*    */ import javax.xml.transform.Source;
/*    */ import org.apache.axis.attachments.OctetStream;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class JAFDataHandlerDeserializerFactory extends BaseDeserializerFactory
/*    */ {
/* 34 */   protected static Log log = LogFactory.getLog(JAFDataHandlerDeserializerFactory.class.getName());
/*    */ 
/*    */   public JAFDataHandlerDeserializerFactory(Class javaType, QName xmlType)
/*    */   {
/* 38 */     super(getDeserializerClass(javaType, xmlType), xmlType, javaType);
/* 39 */     log.debug("Enter/Exit: JAFDataHandlerDeserializerFactory(" + javaType + ", " + xmlType + ")");
/*    */   }
/*    */ 
/*    */   public JAFDataHandlerDeserializerFactory() {
/* 43 */     super(JAFDataHandlerDeserializer.class);
/* 44 */     log.debug("Enter/Exit: JAFDataHandlerDeserializerFactory()");
/*    */   }
/*    */ 
/*    */   private static Class getDeserializerClass(Class javaType, QName xmlType)
/*    */   {
/*    */     Class deser;
/*    */     Class deser;
/* 49 */     if (Image.class.isAssignableFrom(javaType)) {
/* 50 */       deser = ImageDataHandlerDeserializer.class;
/*    */     }
/*    */     else
/*    */     {
/*    */       Class deser;
/* 52 */       if (String.class.isAssignableFrom(javaType)) {
/* 53 */         deser = PlainTextDataHandlerDeserializer.class;
/*    */       }
/*    */       else
/*    */       {
/*    */         Class deser;
/* 55 */         if (Source.class.isAssignableFrom(javaType)) {
/* 56 */           deser = SourceDataHandlerDeserializer.class;
/*    */         }
/*    */         else
/*    */         {
/*    */           Class deser;
/* 58 */           if (MimeMultipart.class.isAssignableFrom(javaType)) {
/* 59 */             deser = MimeMultipartDataHandlerDeserializer.class;
/*    */           }
/*    */           else
/*    */           {
/*    */             Class deser;
/* 61 */             if (OctetStream.class.isAssignableFrom(javaType)) {
/* 62 */               deser = OctetStreamDataHandlerDeserializer.class;
/*    */             }
/*    */             else
/* 65 */               deser = JAFDataHandlerDeserializer.class; 
/*    */           }
/*    */         }
/*    */       }
/*    */     }
/* 67 */     return deser;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.JAFDataHandlerDeserializerFactory
 * JD-Core Version:    0.6.0
 */