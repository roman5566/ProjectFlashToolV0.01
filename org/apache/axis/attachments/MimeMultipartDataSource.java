/*    */ package org.apache.axis.attachments;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import javax.activation.DataSource;
/*    */ import javax.mail.internet.MimeMultipart;
/*    */ 
/*    */ public class MimeMultipartDataSource
/*    */   implements DataSource
/*    */ {
/*    */   public static final String CONTENT_TYPE = "multipart/mixed";
/*    */   private final String name;
/*    */   private final String contentType;
/*    */   private byte[] data;
/*    */   private ByteArrayOutputStream os;
/*    */ 
/*    */   public MimeMultipartDataSource(String name, MimeMultipart data)
/*    */   {
/* 35 */     this.name = name;
/* 36 */     this.contentType = (data == null ? "multipart/mixed" : data.getContentType());
/* 37 */     this.os = new ByteArrayOutputStream();
/*    */     try {
/* 39 */       if (data != null)
/* 40 */         data.writeTo(this.os);
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/*    */     }
/*    */   }
/*    */ 
/*    */   public String getName()
/*    */   {
/* 49 */     return this.name;
/*    */   }
/*    */ 
/*    */   public String getContentType() {
/* 53 */     return this.contentType;
/*    */   }
/*    */ 
/*    */   public InputStream getInputStream() throws IOException {
/* 57 */     if (this.os.size() != 0) {
/* 58 */       this.data = this.os.toByteArray();
/* 59 */       this.os.reset();
/*    */     }
/* 61 */     return new ByteArrayInputStream(this.data == null ? new byte[0] : this.data);
/*    */   }
/*    */ 
/*    */   public OutputStream getOutputStream() throws IOException {
/* 65 */     if (this.os.size() != 0) {
/* 66 */       this.data = this.os.toByteArray();
/* 67 */       this.os.reset();
/*    */     }
/* 69 */     return this.os;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.attachments.MimeMultipartDataSource
 * JD-Core Version:    0.6.0
 */