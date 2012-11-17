/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.awt.Image;
/*    */ import javax.mail.internet.MimeMultipart;
/*    */ import javax.xml.namespace.QName;
/*    */ import javax.xml.transform.Source;
/*    */ import org.apache.axis.attachments.OctetStream;
/*    */ 
/*    */ public class JAFDataHandlerSerializerFactory extends BaseSerializerFactory
/*    */ {
/*    */   public JAFDataHandlerSerializerFactory(Class javaType, QName xmlType)
/*    */   {
/* 34 */     super(getSerializerClass(javaType, xmlType), xmlType, javaType);
/*    */   }
/*    */   public JAFDataHandlerSerializerFactory() {
/* 37 */     super(JAFDataHandlerSerializer.class);
/*    */   }
/*    */ 
/*    */   private static Class getSerializerClass(Class javaType, QName xmlType)
/*    */   {
/*    */     Class ser;
/*    */     Class ser;
/* 42 */     if (Image.class.isAssignableFrom(javaType)) {
/* 43 */       ser = ImageDataHandlerSerializer.class;
/*    */     }
/*    */     else
/*    */     {
/*    */       Class ser;
/* 45 */       if (String.class.isAssignableFrom(javaType)) {
/* 46 */         ser = PlainTextDataHandlerSerializer.class;
/*    */       }
/*    */       else
/*    */       {
/*    */         Class ser;
/* 48 */         if (Source.class.isAssignableFrom(javaType)) {
/* 49 */           ser = SourceDataHandlerSerializer.class;
/*    */         }
/*    */         else
/*    */         {
/*    */           Class ser;
/* 51 */           if (MimeMultipart.class.isAssignableFrom(javaType)) {
/* 52 */             ser = MimeMultipartDataHandlerSerializer.class;
/*    */           }
/*    */           else
/*    */           {
/*    */             Class ser;
/* 54 */             if (OctetStream.class.isAssignableFrom(javaType)) {
/* 55 */               ser = OctetStreamDataHandlerSerializer.class;
/*    */             }
/*    */             else
/* 58 */               ser = JAFDataHandlerSerializer.class; 
/*    */           }
/*    */         }
/*    */       }
/*    */     }
/* 60 */     return ser;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.JAFDataHandlerSerializerFactory
 * JD-Core Version:    0.6.0
 */