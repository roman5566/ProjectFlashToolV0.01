/*     */ package org.apache.axis.attachments;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.net.URL;
/*     */ import javax.activation.DataSource;
/*     */ import javax.xml.transform.stream.StreamSource;
/*     */ 
/*     */ public class SourceDataSource
/*     */   implements DataSource
/*     */ {
/*     */   public static final String CONTENT_TYPE = "text/xml";
/*     */   private final String name;
/*     */   private final String contentType;
/*     */   private byte[] data;
/*     */   private ByteArrayOutputStream os;
/*     */ 
/*     */   public SourceDataSource(String name, StreamSource data)
/*     */   {
/*  39 */     this(name, "text/xml", data);
/*     */   }
/*     */ 
/*     */   public SourceDataSource(String name, String contentType, StreamSource data) {
/*  43 */     this.name = name;
/*  44 */     this.contentType = (contentType == null ? "text/xml" : contentType);
/*  45 */     this.os = new ByteArrayOutputStream();
/*     */     try {
/*  47 */       if (data != null)
/*     */       {
/*  49 */         Reader reader = data.getReader();
/*  50 */         if (reader != null) {
/*  51 */           reader = new BufferedReader(reader);
/*     */           int ch;
/*  53 */           while ((ch = reader.read()) != -1)
/*  54 */             this.os.write(ch);
/*     */         }
/*     */         else
/*     */         {
/*  58 */           InputStream is = data.getInputStream();
/*  59 */           if (is == null)
/*     */           {
/*  61 */             String id = data.getSystemId();
/*  62 */             if (id != null) {
/*  63 */               URL url = new URL(id);
/*  64 */               is = url.openStream();
/*     */             }
/*     */           }
/*  67 */           if (is != null) {
/*  68 */             is = new BufferedInputStream(is);
/*     */ 
/*  70 */             byte[] bytes = null;
/*     */             int avail;
/*  72 */             while ((avail = is.available()) > 0) {
/*  73 */               if ((bytes == null) || (avail > bytes.length))
/*  74 */                 bytes = new byte[avail];
/*  75 */               is.read(bytes, 0, avail);
/*  76 */               this.os.write(bytes, 0, avail);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName() {
/*  87 */     return this.name;
/*     */   }
/*     */ 
/*     */   public String getContentType() {
/*  91 */     return this.contentType;
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream() throws IOException {
/*  95 */     if (this.os.size() != 0) {
/*  96 */       this.data = this.os.toByteArray();
/*  97 */       this.os.reset();
/*     */     }
/*  99 */     return new ByteArrayInputStream(this.data == null ? new byte[0] : this.data);
/*     */   }
/*     */ 
/*     */   public OutputStream getOutputStream() throws IOException {
/* 103 */     if (this.os.size() != 0) {
/* 104 */       this.data = this.os.toByteArray();
/* 105 */       this.os.reset();
/*     */     }
/* 107 */     return this.os;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.attachments.SourceDataSource
 * JD-Core Version:    0.6.0
 */