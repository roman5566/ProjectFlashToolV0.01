/*     */ package org.apache.axis.attachments;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import javax.activation.DataHandler;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Part;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class MultiPartDimeInputStream extends MultiPartInputStream
/*     */ {
/*  35 */   protected static Log log = LogFactory.getLog(MultiPartDimeInputStream.class.getName());
/*     */ 
/*  38 */   protected HashMap parts = new HashMap();
/*  39 */   protected LinkedList orderedParts = new LinkedList();
/*  40 */   protected int rootPartLength = 0;
/*  41 */   protected boolean closed = false;
/*  42 */   protected boolean eos = false;
/*     */ 
/*  44 */   protected DimeDelimitedInputStream dimeDelimitedStream = null;
/*  45 */   protected InputStream soapStream = null;
/*  46 */   protected byte[] boundary = null;
/*  47 */   protected ByteArrayInputStream cachedSOAPEnvelope = null;
/*     */ 
/*  49 */   protected String contentId = null;
/*     */ 
/*  96 */   protected static final String[] READ_ALL = { "".intern() };
/*     */ 
/*     */   public MultiPartDimeInputStream(InputStream is)
/*     */     throws IOException
/*     */   {
/*  59 */     super(null);
/*  60 */     this.soapStream = (this.dimeDelimitedStream = new DimeDelimitedInputStream(is));
/*  61 */     this.contentId = this.dimeDelimitedStream.getContentId();
/*     */   }
/*     */ 
/*     */   public Part getAttachmentByReference(String[] id)
/*     */     throws AxisFault
/*     */   {
/*  67 */     Part ret = null;
/*     */     try
/*     */     {
/*  70 */       for (int i = id.length - 1; (ret == null) && (i > -1); i--) {
/*  71 */         ret = (AttachmentPart)this.parts.get(id[i]);
/*     */       }
/*     */ 
/*  74 */       if (null == ret) {
/*  75 */         ret = readTillFound(id);
/*     */       }
/*  77 */       log.debug(Messages.getMessage("return02", "getAttachmentByReference(\"" + id + "\"", ret == null ? "null" : ret.toString()));
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*  81 */       throw new AxisFault(e.getClass().getName() + e.getMessage());
/*     */     }
/*     */ 
/*  84 */     return ret;
/*     */   }
/*     */ 
/*     */   protected void addPart(String contentId, String locationId, AttachmentPart ap)
/*     */   {
/*  90 */     if ((contentId != null) && (contentId.trim().length() != 0))
/*  91 */       this.parts.put(contentId, ap);
/*  92 */     this.orderedParts.add(ap);
/*     */   }
/*     */ 
/*     */   protected void readAll()
/*     */     throws AxisFault
/*     */   {
/*     */     try
/*     */     {
/* 100 */       readTillFound(READ_ALL);
/*     */     } catch (Exception e) {
/* 102 */       throw AxisFault.makeFault(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Collection getAttachments() throws AxisFault
/*     */   {
/* 108 */     readAll();
/* 109 */     return new LinkedList(this.orderedParts);
/*     */   }
/*     */ 
/*     */   protected Part readTillFound(String[] id)
/*     */     throws IOException
/*     */   {
/* 120 */     if (this.dimeDelimitedStream == null)
/*     */     {
/* 122 */       return null;
/*     */     }
/* 124 */     Part ret = null;
/*     */     try
/*     */     {
/* 128 */       if (this.soapStream != null) {
/* 129 */         if (!this.eos)
/*     */         {
/* 131 */           ByteArrayOutputStream soapdata = new ByteArrayOutputStream(8192);
/*     */ 
/* 134 */           byte[] buf = new byte[16384];
/* 135 */           int byteread = 0;
/*     */           do
/*     */           {
/* 138 */             byteread = this.soapStream.read(buf);
/* 139 */             if (byteread > 0) {
/* 140 */               soapdata.write(buf, 0, byteread);
/*     */             }
/*     */           }
/* 143 */           while (byteread > -1);
/* 144 */           soapdata.close();
/* 145 */           this.soapStream.close();
/* 146 */           this.soapStream = new ByteArrayInputStream(soapdata.toByteArray());
/*     */         }
/*     */ 
/* 149 */         this.dimeDelimitedStream = this.dimeDelimitedStream.getNextStream();
/*     */       }
/*     */ 
/* 153 */       if (null != this.dimeDelimitedStream)
/*     */         do {
/* 155 */           String contentId = this.dimeDelimitedStream.getContentId();
/* 156 */           String type = this.dimeDelimitedStream.getType();
/*     */ 
/* 158 */           if ((type != null) && (!this.dimeDelimitedStream.getDimeTypeNameFormat().equals(DimeTypeNameFormat.MIME))) {
/* 159 */             type = "application/uri; uri=\"" + type + "\"";
/*     */           }
/*     */ 
/* 163 */           ManagedMemoryDataSource source = new ManagedMemoryDataSource(this.dimeDelimitedStream, 16384, type, true);
/*     */ 
/* 165 */           DataHandler dh = new DataHandler(source);
/*     */ 
/* 167 */           AttachmentPart ap = new AttachmentPart(dh);
/* 168 */           if (contentId != null) {
/* 169 */             ap.setMimeHeader("Content-Id", contentId);
/*     */           }
/*     */ 
/* 172 */           addPart(contentId, "", ap);
/*     */ 
/* 174 */           for (int i = id.length - 1; (ret == null) && (i > -1); i--) {
/* 175 */             if ((contentId != null) && (id[i].equals(contentId))) {
/* 176 */               ret = ap;
/*     */             }
/*     */           }
/*     */ 
/* 180 */           this.dimeDelimitedStream = this.dimeDelimitedStream.getNextStream();
/*     */ 
/* 184 */           if (null != ret) break; 
/* 184 */         }while (null != this.dimeDelimitedStream);
/*     */     }
/*     */     catch (Exception e) {
/* 187 */       throw AxisFault.makeFault(e);
/*     */     }
/*     */ 
/* 190 */     return ret;
/*     */   }
/*     */ 
/*     */   public String getContentLocation()
/*     */   {
/* 199 */     return null;
/*     */   }
/*     */ 
/*     */   public String getContentId()
/*     */   {
/* 209 */     return this.contentId;
/*     */   }
/*     */ 
/*     */   public int read(byte[] b, int off, int len) throws IOException
/*     */   {
/* 214 */     if (this.closed) {
/* 215 */       throw new IOException(Messages.getMessage("streamClosed"));
/*     */     }
/*     */ 
/* 218 */     if (this.eos) {
/* 219 */       return -1;
/*     */     }
/* 221 */     int read = this.soapStream.read(b, off, len);
/*     */ 
/* 223 */     if (read < 0) {
/* 224 */       this.eos = true;
/*     */     }
/* 226 */     return read;
/*     */   }
/*     */ 
/*     */   public int read(byte[] b) throws IOException {
/* 230 */     return read(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   public int read() throws IOException {
/* 234 */     if (this.closed) {
/* 235 */       throw new IOException(Messages.getMessage("streamClosed"));
/*     */     }
/*     */ 
/* 238 */     if (this.eos) {
/* 239 */       return -1;
/*     */     }
/* 241 */     int ret = this.soapStream.read();
/*     */ 
/* 243 */     if (ret < 0) {
/* 244 */       this.eos = true;
/*     */     }
/* 246 */     return ret;
/*     */   }
/*     */ 
/*     */   public void close() throws IOException {
/* 250 */     this.closed = true;
/* 251 */     this.soapStream.close();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.attachments.MultiPartDimeInputStream
 * JD-Core Version:    0.6.0
 */