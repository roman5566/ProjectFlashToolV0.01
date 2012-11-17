/*     */ package org.apache.axis.attachments;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Iterator;
/*     */ import javax.activation.DataHandler;
/*     */ import javax.activation.DataSource;
/*     */ import javax.xml.soap.MimeHeader;
/*     */ import javax.xml.soap.MimeHeaders;
/*     */ import javax.xml.soap.SOAPException;
/*     */ import javax.xml.transform.stream.StreamSource;
/*     */ import org.apache.axis.Part;
/*     */ import org.apache.axis.components.image.ImageIO;
/*     */ import org.apache.axis.components.image.ImageIOFactory;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.utils.IOUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.SessionUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class AttachmentPart extends javax.xml.soap.AttachmentPart
/*     */   implements Part
/*     */ {
/*  44 */   protected static Log log = LogFactory.getLog(AttachmentPart.class.getName());
/*     */ 
/*  52 */   DataHandler datahandler = null;
/*     */ 
/*  55 */   private MimeHeaders mimeHeaders = new MimeHeaders();
/*     */   private Object contentObject;
/*     */   private String attachmentFile;
/*     */ 
/*     */   public AttachmentPart()
/*     */   {
/*  69 */     setMimeHeader("Content-Id", SessionUtils.generateSessionId());
/*     */   }
/*     */ 
/*     */   public AttachmentPart(DataHandler dh)
/*     */   {
/*  78 */     setMimeHeader("Content-Id", SessionUtils.generateSessionId());
/*     */ 
/*  80 */     this.datahandler = dh;
/*  81 */     if (dh != null) {
/*  82 */       setMimeHeader("Content-Type", dh.getContentType());
/*  83 */       DataSource ds = dh.getDataSource();
/*  84 */       if ((ds instanceof ManagedMemoryDataSource))
/*  85 */         extractFilename((ManagedMemoryDataSource)ds);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */     throws Throwable
/*     */   {
/*  98 */     dispose();
/*     */   }
/*     */ 
/*     */   public DataHandler getActivationDataHandler()
/*     */   {
/* 107 */     return this.datahandler;
/*     */   }
/*     */ 
/*     */   public String getContentType()
/*     */   {
/* 116 */     return getFirstMimeHeader("Content-Type");
/*     */   }
/*     */ 
/*     */   public void addMimeHeader(String header, String value)
/*     */   {
/* 126 */     this.mimeHeaders.addHeader(header, value);
/*     */   }
/*     */ 
/*     */   public String getFirstMimeHeader(String header)
/*     */   {
/* 137 */     String[] values = this.mimeHeaders.getHeader(header.toLowerCase());
/* 138 */     if ((values != null) && (values.length > 0)) {
/* 139 */       return values[0];
/*     */     }
/* 141 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean matches(MimeHeaders headers)
/*     */   {
/* 153 */     for (Iterator i = headers.getAllHeaders(); i.hasNext(); ) {
/* 154 */       MimeHeader hdr = (MimeHeader)i.next();
/* 155 */       String[] values = this.mimeHeaders.getHeader(hdr.getName());
/* 156 */       boolean found = false;
/* 157 */       if (values != null) {
/* 158 */         for (int j = 0; j < values.length; j++) {
/* 159 */           if (!hdr.getValue().equalsIgnoreCase(values[j])) {
/*     */             continue;
/*     */           }
/* 162 */           found = true;
/* 163 */           break;
/*     */         }
/*     */       }
/* 166 */       if (!found) {
/* 167 */         return false;
/*     */       }
/*     */     }
/* 170 */     return true;
/*     */   }
/*     */ 
/*     */   public String getContentLocation() {
/* 174 */     return getFirstMimeHeader("Content-Location");
/*     */   }
/*     */ 
/*     */   public void setContentLocation(String loc) {
/* 178 */     setMimeHeader("Content-Location", loc);
/*     */   }
/*     */ 
/*     */   public void setContentId(String newCid) {
/* 182 */     setMimeHeader("Content-Id", newCid);
/*     */   }
/*     */ 
/*     */   public String getContentId() {
/* 186 */     return getFirstMimeHeader("Content-Id");
/*     */   }
/*     */ 
/*     */   public Iterator getMatchingMimeHeaders(String[] match) {
/* 190 */     return this.mimeHeaders.getMatchingHeaders(match);
/*     */   }
/*     */ 
/*     */   public Iterator getNonMatchingMimeHeaders(String[] match) {
/* 194 */     return this.mimeHeaders.getNonMatchingHeaders(match);
/*     */   }
/*     */ 
/*     */   public Iterator getAllMimeHeaders() {
/* 198 */     return this.mimeHeaders.getAllHeaders();
/*     */   }
/*     */ 
/*     */   public void setMimeHeader(String name, String value)
/*     */   {
/* 219 */     this.mimeHeaders.setHeader(name, value);
/*     */   }
/*     */ 
/*     */   public void removeAllMimeHeaders()
/*     */   {
/* 224 */     this.mimeHeaders.removeAllHeaders();
/*     */   }
/*     */ 
/*     */   public void removeMimeHeader(String header)
/*     */   {
/* 233 */     this.mimeHeaders.removeHeader(header);
/*     */   }
/*     */ 
/*     */   public DataHandler getDataHandler()
/*     */     throws SOAPException
/*     */   {
/* 245 */     if (this.datahandler == null) {
/* 246 */       throw new SOAPException(Messages.getMessage("noContent"));
/*     */     }
/* 248 */     return this.datahandler;
/*     */   }
/*     */ 
/*     */   public void setDataHandler(DataHandler datahandler)
/*     */   {
/* 266 */     if (datahandler == null) {
/* 267 */       throw new IllegalArgumentException(Messages.getMessage("illegalArgumentException00"));
/*     */     }
/*     */ 
/* 270 */     this.datahandler = datahandler;
/* 271 */     setMimeHeader("Content-Type", datahandler.getContentType());
/*     */ 
/* 273 */     DataSource ds = datahandler.getDataSource();
/* 274 */     if ((ds instanceof ManagedMemoryDataSource))
/*     */     {
/* 276 */       extractFilename((ManagedMemoryDataSource)ds);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object getContent()
/*     */     throws SOAPException
/*     */   {
/* 316 */     if (this.contentObject != null) {
/* 317 */       return this.contentObject;
/*     */     }
/*     */ 
/* 320 */     if (this.datahandler == null) {
/* 321 */       throw new SOAPException(Messages.getMessage("noContent"));
/*     */     }
/*     */ 
/* 324 */     DataSource ds = this.datahandler.getDataSource();
/* 325 */     InputStream is = null;
/*     */     try {
/* 327 */       is = ds.getInputStream();
/*     */     } catch (IOException io) {
/* 329 */       log.error(Messages.getMessage("javaIOException00"), io);
/* 330 */       throw new SOAPException(io);
/*     */     }
/* 332 */     if (ds.getContentType().equals("text/plain"))
/*     */       try {
/* 334 */         byte[] bytes = new byte[is.available()];
/* 335 */         IOUtils.readFully(is, bytes);
/* 336 */         return new String(bytes);
/*     */       } catch (IOException io) {
/* 338 */         log.error(Messages.getMessage("javaIOException00"), io);
/* 339 */         throw new SOAPException(io);
/*     */       }
/* 341 */     if (ds.getContentType().equals("text/xml"))
/* 342 */       return new StreamSource(is);
/* 343 */     if ((ds.getContentType().equals("image/gif")) || (ds.getContentType().equals("image/jpeg"))) {
/*     */       try
/*     */       {
/* 346 */         return ImageIOFactory.getImageIO().loadImage(is);
/*     */       } catch (Exception ex) {
/* 348 */         log.error(Messages.getMessage("javaIOException00"), ex);
/* 349 */         throw new SOAPException(ex);
/*     */       }
/*     */     }
/* 352 */     return is;
/*     */   }
/*     */ 
/*     */   public void setContent(Object object, String contentType)
/*     */   {
/* 375 */     ManagedMemoryDataSource source = null;
/* 376 */     setMimeHeader("Content-Type", contentType);
/* 377 */     if ((object instanceof String)) {
/*     */       try {
/* 379 */         String s = (String)object;
/* 380 */         ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes());
/*     */ 
/* 382 */         source = new ManagedMemoryDataSource(bais, 16384, contentType, true);
/*     */ 
/* 385 */         extractFilename(source);
/* 386 */         this.datahandler = new DataHandler(source);
/* 387 */         this.contentObject = object;
/* 388 */         return;
/*     */       } catch (IOException io) {
/* 390 */         log.error(Messages.getMessage("javaIOException00"), io);
/* 391 */         throw new IllegalArgumentException(Messages.getMessage("illegalArgumentException00"));
/*     */       }
/*     */     }
/* 394 */     if ((object instanceof InputStream)) {
/*     */       try {
/* 396 */         source = new ManagedMemoryDataSource((InputStream)object, 16384, contentType, true);
/*     */ 
/* 399 */         extractFilename(source);
/* 400 */         this.datahandler = new DataHandler(source);
/* 401 */         this.contentObject = null;
/* 402 */         return;
/*     */       } catch (IOException io) {
/* 404 */         log.error(Messages.getMessage("javaIOException00"), io);
/* 405 */         throw new IllegalArgumentException(Messages.getMessage("illegalArgumentException00"));
/*     */       }
/*     */     }
/* 408 */     if ((object instanceof StreamSource)) {
/*     */       try {
/* 410 */         source = new ManagedMemoryDataSource(((StreamSource)object).getInputStream(), 16384, contentType, true);
/*     */ 
/* 413 */         extractFilename(source);
/* 414 */         this.datahandler = new DataHandler(source);
/* 415 */         this.contentObject = null;
/* 416 */         return;
/*     */       } catch (IOException io) {
/* 418 */         log.error(Messages.getMessage("javaIOException00"), io);
/* 419 */         throw new IllegalArgumentException(Messages.getMessage("illegalArgumentException00"));
/*     */       }
/*     */     }
/*     */ 
/* 423 */     throw new IllegalArgumentException(Messages.getMessage("illegalArgumentException00"));
/*     */   }
/*     */ 
/*     */   public void clearContent()
/*     */   {
/* 434 */     this.datahandler = null;
/* 435 */     this.contentObject = null;
/*     */   }
/*     */ 
/*     */   public int getSize()
/*     */     throws SOAPException
/*     */   {
/* 448 */     if (this.datahandler == null) {
/* 449 */       return 0;
/*     */     }
/* 451 */     ByteArrayOutputStream bout = new ByteArrayOutputStream();
/*     */     try {
/* 453 */       this.datahandler.writeTo(bout);
/*     */     } catch (IOException ex) {
/* 455 */       log.error(Messages.getMessage("javaIOException00"), ex);
/* 456 */       throw new SOAPException(Messages.getMessage("javaIOException01", ex.getMessage()), ex);
/*     */     }
/* 458 */     return bout.size();
/*     */   }
/*     */ 
/*     */   public String[] getMimeHeader(String name)
/*     */   {
/* 471 */     return this.mimeHeaders.getHeader(name);
/*     */   }
/*     */ 
/*     */   public String getContentIdRef()
/*     */   {
/* 482 */     return "cid:" + getContentId();
/*     */   }
/*     */ 
/*     */   private void extractFilename(ManagedMemoryDataSource source)
/*     */   {
/* 493 */     if (source.getDiskCacheFile() != null) {
/* 494 */       String path = source.getDiskCacheFile().getAbsolutePath();
/* 495 */       setAttachmentFile(path);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void setAttachmentFile(String path)
/*     */   {
/* 505 */     this.attachmentFile = path;
/*     */   }
/*     */ 
/*     */   public void detachAttachmentFile()
/*     */   {
/* 514 */     this.attachmentFile = null;
/*     */   }
/*     */ 
/*     */   public String getAttachmentFile()
/*     */   {
/* 523 */     return this.attachmentFile;
/*     */   }
/*     */ 
/*     */   public synchronized void dispose()
/*     */   {
/* 532 */     if (this.attachmentFile != null) {
/* 533 */       DataSource ds = this.datahandler.getDataSource();
/* 534 */       if ((ds instanceof ManagedMemoryDataSource)) {
/* 535 */         ((ManagedMemoryDataSource)ds).delete();
/*     */       } else {
/* 537 */         File f = new File(this.attachmentFile);
/*     */ 
/* 539 */         f.delete();
/*     */       }
/*     */ 
/* 542 */       setAttachmentFile(null);
/*     */     }
/*     */ 
/* 547 */     this.datahandler = null;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.attachments.AttachmentPart
 * JD-Core Version:    0.6.0
 */