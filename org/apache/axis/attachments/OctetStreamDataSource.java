/*    */ package org.apache.axis.attachments;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import javax.activation.DataSource;
/*    */ 
/*    */ public class OctetStreamDataSource
/*    */   implements DataSource
/*    */ {
/*    */   public static final String CONTENT_TYPE = "application/octet-stream";
/*    */   private final String name;
/*    */   private byte[] data;
/*    */   private ByteArrayOutputStream os;
/*    */ 
/*    */   public OctetStreamDataSource(String name, OctetStream data)
/*    */   {
/* 33 */     this.name = name;
/* 34 */     this.data = (data == null ? null : data.getBytes());
/* 35 */     this.os = new ByteArrayOutputStream();
/*    */   }
/*    */ 
/*    */   public String getName() {
/* 39 */     return this.name;
/*    */   }
/*    */ 
/*    */   public String getContentType() {
/* 43 */     return "application/octet-stream";
/*    */   }
/*    */ 
/*    */   public InputStream getInputStream() throws IOException {
/* 47 */     if (this.os.size() != 0) {
/* 48 */       this.data = this.os.toByteArray();
/*    */     }
/* 50 */     return new ByteArrayInputStream(this.data == null ? new byte[0] : this.data);
/*    */   }
/*    */ 
/*    */   public OutputStream getOutputStream() throws IOException {
/* 54 */     if (this.os.size() != 0) {
/* 55 */       this.data = this.os.toByteArray();
/*    */     }
/* 57 */     return new ByteArrayOutputStream();
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.attachments.OctetStreamDataSource
 * JD-Core Version:    0.6.0
 */