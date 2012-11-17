/*     */ package org.apache.axis.attachments;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public abstract class IncomingAttachmentStreams
/*     */ {
/*     */   private boolean _readyToGetNextStream;
/*     */ 
/*     */   public IncomingAttachmentStreams()
/*     */   {
/*  31 */     this._readyToGetNextStream = true;
/*     */   }
/*     */ 
/*     */   public abstract IncomingAttachmentInputStream getNextStream()
/*     */     throws AxisFault;
/*     */ 
/*     */   public final boolean isReadyToGetNextStream()
/*     */   {
/*  44 */     return this._readyToGetNextStream;
/*     */   }
/*     */ 
/*     */   protected final void setReadyToGetNextStream(boolean ready)
/*     */   {
/*  53 */     this._readyToGetNextStream = ready;
/*     */   }
/*     */ 
/*     */   public final class IncomingAttachmentInputStream extends InputStream {
/*  57 */     private HashMap _headers = null;
/*     */ 
/*  59 */     private InputStream _stream = null;
/*     */ 
/*     */     public IncomingAttachmentInputStream(InputStream in)
/*     */     {
/*  65 */       this._stream = in;
/*     */     }
/*     */ 
/*     */     public Map getHeaders()
/*     */     {
/*  73 */       return this._headers;
/*     */     }
/*     */ 
/*     */     public void addHeader(String name, String value)
/*     */     {
/*  83 */       if (this._headers == null) {
/*  84 */         this._headers = new HashMap();
/*     */       }
/*  86 */       this._headers.put(name, value);
/*     */     }
/*     */ 
/*     */     public String getHeader(String name)
/*     */     {
/*  96 */       Object header = null;
/*  97 */       if ((this._headers == null) || ((header = this._headers.get(name)) == null)) {
/*  98 */         return null;
/*     */       }
/* 100 */       return header.toString();
/*     */     }
/*     */ 
/*     */     public String getContentId()
/*     */     {
/* 107 */       return getHeader("Content-Id");
/*     */     }
/*     */ 
/*     */     public String getContentLocation()
/*     */     {
/* 115 */       return getHeader("Content-Location");
/*     */     }
/*     */ 
/*     */     public String getContentType()
/*     */     {
/* 122 */       return getHeader("Content-Type");
/*     */     }
/*     */ 
/*     */     public boolean markSupported()
/*     */     {
/* 131 */       return false;
/*     */     }
/*     */ 
/*     */     public void reset() throws IOException {
/* 135 */       throw new IOException(Messages.getMessage("markNotSupported"));
/*     */     }
/*     */ 
/*     */     public void mark(int readLimit)
/*     */     {
/*     */     }
/*     */ 
/*     */     public int read() throws IOException {
/* 143 */       int retval = this._stream.read();
/* 144 */       IncomingAttachmentStreams.this.setReadyToGetNextStream(retval == -1);
/*     */ 
/* 146 */       return retval;
/*     */     }
/*     */ 
/*     */     public int read(byte[] b) throws IOException {
/* 150 */       int retval = this._stream.read(b);
/* 151 */       IncomingAttachmentStreams.this.setReadyToGetNextStream(retval == -1);
/*     */ 
/* 153 */       return retval;
/*     */     }
/*     */ 
/*     */     public int read(byte[] b, int off, int len) throws IOException {
/* 157 */       int retval = this._stream.read(b, off, len);
/* 158 */       IncomingAttachmentStreams.this.setReadyToGetNextStream(retval == -1);
/*     */ 
/* 160 */       return retval;
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.attachments.IncomingAttachmentStreams
 * JD-Core Version:    0.6.0
 */