/*    */ package org.apache.axis.attachments;
/*    */ 
/*    */ import java.awt.Image;
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import javax.activation.DataSource;
/*    */ import org.apache.axis.components.image.ImageIO;
/*    */ import org.apache.axis.components.image.ImageIOFactory;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class ImageDataSource
/*    */   implements DataSource
/*    */ {
/* 32 */   protected static Log log = LogFactory.getLog(ImageDataSource.class.getName());
/*    */   public static final String CONTENT_TYPE = "image/jpeg";
/*    */   private final String name;
/*    */   private final String contentType;
/*    */   private byte[] data;
/*    */   private ByteArrayOutputStream os;
/*    */ 
/*    */   public ImageDataSource(String name, Image data)
/*    */   {
/* 43 */     this(name, "image/jpeg", data);
/*    */   }
/*    */ 
/*    */   public ImageDataSource(String name, String contentType, Image data) {
/* 47 */     this.name = name;
/* 48 */     this.contentType = (contentType == null ? "image/jpeg" : contentType);
/* 49 */     this.os = new ByteArrayOutputStream();
/*    */     try {
/* 51 */       if (data != null)
/* 52 */         ImageIOFactory.getImageIO().saveImage(this.contentType, data, this.os);
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 56 */       log.error(Messages.getMessage("exception00"), e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public String getName() {
/* 61 */     return this.name;
/*    */   }
/*    */ 
/*    */   public String getContentType() {
/* 65 */     return this.contentType;
/*    */   }
/*    */ 
/*    */   public InputStream getInputStream() throws IOException {
/* 69 */     if (this.os.size() != 0) {
/* 70 */       this.data = this.os.toByteArray();
/* 71 */       this.os.reset();
/*    */     }
/* 73 */     return new ByteArrayInputStream(this.data == null ? new byte[0] : this.data);
/*    */   }
/*    */ 
/*    */   public OutputStream getOutputStream() throws IOException {
/* 77 */     if (this.os.size() != 0) {
/* 78 */       this.data = this.os.toByteArray();
/* 79 */       this.os.reset();
/*    */     }
/* 81 */     return this.os;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.attachments.ImageDataSource
 * JD-Core Version:    0.6.0
 */