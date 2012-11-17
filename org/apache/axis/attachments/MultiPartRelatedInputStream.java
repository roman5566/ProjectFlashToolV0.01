/*     */ package org.apache.axis.attachments;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import javax.activation.DataHandler;
/*     */ import javax.mail.Header;
/*     */ import javax.mail.MessagingException;
/*     */ import javax.mail.internet.ContentType;
/*     */ import javax.mail.internet.InternetHeaders;
/*     */ import javax.mail.internet.MimeUtility;
/*     */ import javax.mail.internet.ParseException;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Part;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.utils.IOUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class MultiPartRelatedInputStream extends MultiPartInputStream
/*     */ {
/*  38 */   protected static Log log = LogFactory.getLog(MultiPartRelatedInputStream.class.getName());
/*     */   public static final String MIME_MULTIPART_RELATED = "multipart/related";
/*  45 */   protected HashMap parts = new HashMap();
/*     */ 
/*  48 */   protected LinkedList orderedParts = new LinkedList();
/*     */ 
/*  51 */   protected int rootPartLength = 0;
/*     */ 
/*  54 */   protected boolean closed = false;
/*     */ 
/*  57 */   protected boolean eos = false;
/*     */ 
/*  64 */   protected BoundaryDelimitedStream boundaryDelimitedStream = null;
/*     */ 
/*  68 */   protected InputStream soapStream = null;
/*     */ 
/*  72 */   protected InputStream soapStreamBDS = null;
/*     */ 
/*  76 */   protected byte[] boundary = null;
/*     */ 
/*  79 */   protected ByteArrayInputStream cachedSOAPEnvelope = null;
/*     */ 
/*  85 */   protected String contentLocation = null;
/*     */ 
/*  88 */   protected String contentId = null;
/*     */   private static final int MAX_CACHED = 16384;
/* 423 */   protected static final String[] READ_ALL = { "".intern() };
/*     */ 
/*     */   public MultiPartRelatedInputStream(String contentType, InputStream stream)
/*     */     throws AxisFault
/*     */   {
/* 104 */     super(null);
/*     */ 
/* 106 */     if (!(stream instanceof BufferedInputStream)) {
/* 107 */       stream = new BufferedInputStream(stream);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 114 */       ContentType ct = new ContentType(contentType);
/*     */ 
/* 116 */       String rootPartContentId = ct.getParameter("start");
/*     */ 
/* 119 */       if (rootPartContentId != null) {
/* 120 */         rootPartContentId = rootPartContentId.trim();
/*     */ 
/* 122 */         if (rootPartContentId.startsWith("<")) {
/* 123 */           rootPartContentId = rootPartContentId.substring(1);
/*     */         }
/*     */ 
/* 126 */         if (rootPartContentId.endsWith(">"))
/* 127 */           rootPartContentId = rootPartContentId.substring(0, rootPartContentId.length() - 1);
/*     */       }
/*     */       boolean found;
/* 133 */       if (ct.getParameter("boundary") != null) {
/* 134 */         String boundaryStr = "--" + ct.getParameter("boundary");
/*     */ 
/* 143 */         byte[][] boundaryMarker = new byte[2][boundaryStr.length() + 2];
/*     */ 
/* 145 */         IOUtils.readFully(stream, boundaryMarker[0]);
/*     */ 
/* 147 */         this.boundary = (boundaryStr + "\r\n").getBytes("US-ASCII");
/*     */ 
/* 149 */         int current = 0;
/*     */ 
/* 153 */         for (boolean found = false; !found; current++) {
/* 154 */           if ((found = Arrays.equals(boundaryMarker[(current & 0x1)], this.boundary))) {
/*     */             continue;
/*     */           }
/* 157 */           System.arraycopy(boundaryMarker[(current & 0x1)], 1, boundaryMarker[(current + 1 & 0x1)], 0, boundaryMarker[0].length - 1);
/*     */ 
/* 161 */           if (stream.read(boundaryMarker[(current + 1 & 0x1)], boundaryMarker[0].length - 1, 1) >= 1) {
/*     */             continue;
/*     */           }
/* 164 */           throw new AxisFault(Messages.getMessage("mimeErrorNoBoundary", new String(this.boundary)));
/*     */         }
/*     */ 
/* 173 */         boundaryStr = "\r\n" + boundaryStr;
/* 174 */         this.boundary = boundaryStr.getBytes("US-ASCII");
/*     */       }
/*     */       else {
/* 177 */         for (found = false; !found; ) {
/* 178 */           this.boundary = readLine(stream);
/* 179 */           if (this.boundary == null) {
/* 180 */             throw new AxisFault(Messages.getMessage("mimeErrorNoBoundary", "--"));
/*     */           }
/*     */ 
/* 183 */           found = (this.boundary.length > 4) && (this.boundary[2] == 45) && (this.boundary[3] == 45);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 188 */       this.boundaryDelimitedStream = new BoundaryDelimitedStream(stream, this.boundary, 1024);
/*     */ 
/* 193 */       String contentTransferEncoding = null;
/*     */       do
/*     */       {
/* 196 */         this.contentId = null;
/* 197 */         this.contentLocation = null;
/* 198 */         contentTransferEncoding = null;
/*     */ 
/* 201 */         InternetHeaders headers = new InternetHeaders(this.boundaryDelimitedStream);
/*     */ 
/* 206 */         this.contentId = headers.getHeader("Content-Id", null);
/*     */ 
/* 210 */         if (this.contentId != null) {
/* 211 */           this.contentId = this.contentId.trim();
/*     */ 
/* 213 */           if (this.contentId.startsWith("<")) {
/* 214 */             this.contentId = this.contentId.substring(1);
/*     */           }
/*     */ 
/* 217 */           if (this.contentId.endsWith(">")) {
/* 218 */             this.contentId = this.contentId.substring(0, this.contentId.length() - 1);
/*     */           }
/*     */ 
/* 222 */           this.contentId = this.contentId.trim();
/*     */         }
/*     */ 
/* 231 */         this.contentLocation = headers.getHeader("Content-Location", null);
/*     */ 
/* 235 */         if (this.contentLocation != null) {
/* 236 */           this.contentLocation = this.contentLocation.trim();
/*     */ 
/* 238 */           if (this.contentLocation.startsWith("<")) {
/* 239 */             this.contentLocation = this.contentLocation.substring(1);
/*     */           }
/*     */ 
/* 242 */           if (this.contentLocation.endsWith(">")) {
/* 243 */             this.contentLocation = this.contentLocation.substring(0, this.contentLocation.length() - 1);
/*     */           }
/*     */ 
/* 247 */           this.contentLocation = this.contentLocation.trim();
/*     */         }
/*     */ 
/* 250 */         contentType = headers.getHeader("Content-Type", null);
/*     */ 
/* 253 */         if (contentType != null) {
/* 254 */           contentType = contentType.trim();
/*     */         }
/*     */ 
/* 257 */         contentTransferEncoding = headers.getHeader("Content-Transfer-Encoding", null);
/*     */ 
/* 260 */         if (contentTransferEncoding != null) {
/* 261 */           contentTransferEncoding = contentTransferEncoding.trim();
/*     */         }
/*     */ 
/* 264 */         InputStream decodedStream = this.boundaryDelimitedStream;
/*     */ 
/* 266 */         if ((contentTransferEncoding != null) && (0 != contentTransferEncoding.length()))
/*     */         {
/* 268 */           decodedStream = MimeUtility.decode(decodedStream, contentTransferEncoding);
/*     */         }
/*     */ 
/* 272 */         if ((rootPartContentId == null) || (rootPartContentId.equals(this.contentId)))
/*     */           continue;
/* 274 */         DataHandler dh = new DataHandler(new ManagedMemoryDataSource(decodedStream, 16384, contentType, true));
/*     */ 
/* 278 */         AttachmentPart ap = new AttachmentPart(dh);
/*     */ 
/* 280 */         if (this.contentId != null) {
/* 281 */           ap.setMimeHeader("Content-Id", this.contentId);
/*     */         }
/*     */ 
/* 285 */         if (this.contentLocation != null) {
/* 286 */           ap.setMimeHeader("Content-Location", this.contentLocation);
/*     */         }
/*     */ 
/* 290 */         Enumeration en = headers.getNonMatchingHeaders(new String[] { "Content-Id", "Content-Location", "Content-Type" });
/*     */ 
/* 294 */         while (en.hasMoreElements()) {
/* 295 */           Header header = (Header)en.nextElement();
/*     */ 
/* 297 */           String name = header.getName();
/* 298 */           String value = header.getValue();
/*     */ 
/* 300 */           if ((name != null) && (value != null)) {
/* 301 */             name = name.trim();
/*     */ 
/* 303 */             if (name.length() != 0) {
/* 304 */               ap.addMimeHeader(name, value);
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/* 309 */         addPart(this.contentId, this.contentLocation, ap);
/*     */ 
/* 311 */         this.boundaryDelimitedStream = this.boundaryDelimitedStream.getNextStream();
/*     */       }
/*     */ 
/* 316 */       while ((null != this.boundaryDelimitedStream) && (rootPartContentId != null) && (!rootPartContentId.equals(this.contentId)));
/*     */ 
/* 318 */       if (this.boundaryDelimitedStream == null) {
/* 319 */         throw new AxisFault(Messages.getMessage("noRoot", rootPartContentId));
/*     */       }
/*     */ 
/* 323 */       this.soapStreamBDS = this.boundaryDelimitedStream;
/*     */ 
/* 325 */       if ((contentTransferEncoding != null) && (0 != contentTransferEncoding.length()))
/*     */       {
/* 327 */         this.soapStream = MimeUtility.decode(this.boundaryDelimitedStream, contentTransferEncoding);
/*     */       }
/*     */       else {
/* 330 */         this.soapStream = this.boundaryDelimitedStream;
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (ParseException e)
/*     */     {
/* 336 */       throw new AxisFault(Messages.getMessage("mimeErrorParsing", e.getMessage()));
/*     */     }
/*     */     catch (IOException e) {
/* 339 */       throw new AxisFault(Messages.getMessage("readError", e.getMessage()));
/*     */     }
/*     */     catch (MessagingException e) {
/* 342 */       throw new AxisFault(Messages.getMessage("readError", e.getMessage()));
/*     */     }
/*     */   }
/*     */ 
/*     */   private final byte[] readLine(InputStream is)
/*     */     throws IOException
/*     */   {
/* 350 */     ByteArrayOutputStream input = new ByteArrayOutputStream(1024);
/* 351 */     int c = 0;
/* 352 */     input.write(13);
/* 353 */     input.write(10);
/*     */ 
/* 355 */     int next = -1;
/* 356 */     while (c != -1) {
/* 357 */       c = -1 != next ? next : is.read();
/* 358 */       next = -1;
/* 359 */       switch (c) {
/*     */       case -1:
/* 361 */         break;
/*     */       case 13:
/* 363 */         next = is.read();
/* 364 */         if (next == 10)
/* 365 */           return input.toByteArray();
/* 366 */         if (next == -1) return null;
/*     */ 
/*     */       default:
/* 369 */         input.write((byte)c);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 374 */     return null;
/*     */   }
/*     */ 
/*     */   public Part getAttachmentByReference(String[] id)
/*     */     throws AxisFault
/*     */   {
/* 381 */     Part ret = null;
/*     */ 
/* 383 */     for (int i = id.length - 1; (ret == null) && (i > -1); i--) {
/* 384 */       ret = (AttachmentPart)this.parts.get(id[i]);
/*     */     }
/*     */ 
/* 387 */     if (null == ret) {
/* 388 */       ret = readTillFound(id);
/*     */     }
/*     */ 
/* 391 */     log.debug(Messages.getMessage("return02", "getAttachmentByReference(\"" + id + "\"", ret == null ? "null" : ret.toString()));
/*     */ 
/* 397 */     return ret;
/*     */   }
/*     */ 
/*     */   protected void addPart(String contentId, String locationId, AttachmentPart ap)
/*     */   {
/* 411 */     if ((contentId != null) && (contentId.trim().length() != 0)) {
/* 412 */       this.parts.put(contentId, ap);
/*     */     }
/*     */ 
/* 415 */     if ((locationId != null) && (locationId.trim().length() != 0)) {
/* 416 */       this.parts.put(locationId, ap);
/*     */     }
/*     */ 
/* 419 */     this.orderedParts.add(ap);
/*     */   }
/*     */ 
/*     */   protected void readAll()
/*     */     throws AxisFault
/*     */   {
/* 433 */     readTillFound(READ_ALL);
/*     */   }
/*     */ 
/*     */   public Collection getAttachments()
/*     */     throws AxisFault
/*     */   {
/* 439 */     readAll();
/*     */ 
/* 441 */     return this.orderedParts;
/*     */   }
/*     */ 
/*     */   protected Part readTillFound(String[] id)
/*     */     throws AxisFault
/*     */   {
/* 456 */     if (this.boundaryDelimitedStream == null) {
/* 457 */       return null;
/*     */     }
/*     */ 
/* 460 */     Part ret = null;
/*     */     try
/*     */     {
/* 463 */       if (this.soapStreamBDS == this.boundaryDelimitedStream)
/*     */       {
/* 465 */         if (!this.eos) {
/* 466 */           ByteArrayOutputStream soapdata = new ByteArrayOutputStream(8192);
/*     */ 
/* 468 */           byte[] buf = new byte[16384];
/*     */ 
/* 470 */           int byteread = 0;
/*     */           do
/*     */           {
/* 473 */             byteread = this.soapStream.read(buf);
/*     */ 
/* 475 */             if (byteread > 0)
/* 476 */               soapdata.write(buf, 0, byteread);
/*     */           }
/* 478 */           while (byteread > -1);
/*     */ 
/* 480 */           soapdata.close();
/*     */ 
/* 482 */           this.soapStream = new ByteArrayInputStream(soapdata.toByteArray());
/*     */         }
/*     */ 
/* 486 */         this.boundaryDelimitedStream = this.boundaryDelimitedStream.getNextStream();
/*     */       }
/*     */ 
/* 491 */       if (null != this.boundaryDelimitedStream)
/*     */         do {
/* 493 */           String contentType = null;
/* 494 */           String contentId = null;
/* 495 */           String contentTransferEncoding = null;
/* 496 */           String contentLocation = null;
/*     */ 
/* 499 */           InternetHeaders headers = new InternetHeaders(this.boundaryDelimitedStream);
/*     */ 
/* 503 */           contentId = headers.getHeader("Content-Id", null);
/*     */ 
/* 505 */           if (contentId != null) {
/* 506 */             contentId = contentId.trim();
/*     */ 
/* 508 */             if (contentId.startsWith("<")) {
/* 509 */               contentId = contentId.substring(1);
/*     */             }
/*     */ 
/* 512 */             if (contentId.endsWith(">")) {
/* 513 */               contentId = contentId.substring(0, contentId.length() - 1);
/*     */             }
/*     */ 
/* 521 */             contentId = contentId.trim();
/*     */           }
/*     */ 
/* 524 */           contentType = headers.getHeader("Content-Type", null);
/*     */ 
/* 528 */           if (contentType != null) {
/* 529 */             contentType = contentType.trim();
/*     */           }
/*     */ 
/* 532 */           contentLocation = headers.getHeader("Content-Location", null);
/*     */ 
/* 536 */           if (contentLocation != null) {
/* 537 */             contentLocation = contentLocation.trim();
/*     */           }
/*     */ 
/* 540 */           contentTransferEncoding = headers.getHeader("Content-Transfer-Encoding", null);
/*     */ 
/* 543 */           if (contentTransferEncoding != null) {
/* 544 */             contentTransferEncoding = contentTransferEncoding.trim();
/*     */           }
/*     */ 
/* 548 */           InputStream decodedStream = this.boundaryDelimitedStream;
/*     */ 
/* 550 */           if ((contentTransferEncoding != null) && (0 != contentTransferEncoding.length()))
/*     */           {
/* 552 */             decodedStream = MimeUtility.decode(decodedStream, contentTransferEncoding);
/*     */           }
/*     */ 
/* 557 */           ManagedMemoryDataSource source = new ManagedMemoryDataSource(decodedStream, 16384, contentType, true);
/*     */ 
/* 559 */           DataHandler dh = new DataHandler(source);
/* 560 */           AttachmentPart ap = new AttachmentPart(dh);
/*     */ 
/* 562 */           if (contentId != null) {
/* 563 */             ap.setMimeHeader("Content-Id", contentId);
/*     */           }
/*     */ 
/* 567 */           if (contentLocation != null) {
/* 568 */             ap.setMimeHeader("Content-Location", contentLocation);
/*     */           }
/*     */ 
/* 572 */           Enumeration en = headers.getNonMatchingHeaders(new String[] { "Content-Id", "Content-Location", "Content-Type" });
/*     */ 
/* 576 */           while (en.hasMoreElements()) {
/* 577 */             Header header = (Header)en.nextElement();
/*     */ 
/* 579 */             String name = header.getName();
/* 580 */             String value = header.getValue();
/*     */ 
/* 582 */             if ((name != null) && (value != null)) {
/* 583 */               name = name.trim();
/*     */ 
/* 585 */               if (name.length() != 0) {
/* 586 */                 ap.addMimeHeader(name, value);
/*     */               }
/*     */             }
/*     */           }
/*     */ 
/* 591 */           addPart(contentId, contentLocation, ap);
/*     */ 
/* 593 */           for (int i = id.length - 1; (ret == null) && (i > -1); )
/*     */           {
/* 595 */             if ((contentId != null) && (id[i].equals(contentId)))
/*     */             {
/* 597 */               ret = ap;
/* 598 */             } else if ((contentLocation != null) && (id[i].equals(contentLocation)))
/*     */             {
/* 600 */               ret = ap;
/*     */             }
/* 594 */             i--;
/*     */           }
/*     */ 
/* 604 */           this.boundaryDelimitedStream = this.boundaryDelimitedStream.getNextStream();
/*     */ 
/* 606 */           if (null != ret) break; 
/* 606 */         }while (null != this.boundaryDelimitedStream);
/*     */     }
/*     */     catch (Exception e) {
/* 609 */       throw AxisFault.makeFault(e);
/*     */     }
/*     */ 
/* 612 */     return ret;
/*     */   }
/*     */ 
/*     */   public String getContentLocation() {
/* 616 */     return this.contentLocation;
/*     */   }
/*     */ 
/*     */   public String getContentId() {
/* 620 */     return this.contentId;
/*     */   }
/*     */ 
/*     */   public int read(byte[] b, int off, int len) throws IOException
/*     */   {
/* 625 */     if (this.closed) {
/* 626 */       throw new IOException(Messages.getMessage("streamClosed"));
/*     */     }
/*     */ 
/* 629 */     if (this.eos) {
/* 630 */       return -1;
/*     */     }
/*     */ 
/* 633 */     int read = this.soapStream.read(b, off, len);
/*     */ 
/* 635 */     if (read < 0) {
/* 636 */       this.eos = true;
/*     */     }
/*     */ 
/* 639 */     return read;
/*     */   }
/*     */ 
/*     */   public int read(byte[] b) throws IOException {
/* 643 */     return read(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   public int read() throws IOException
/*     */   {
/* 648 */     if (this.closed) {
/* 649 */       throw new IOException(Messages.getMessage("streamClosed"));
/*     */     }
/*     */ 
/* 652 */     if (this.eos) {
/* 653 */       return -1;
/*     */     }
/*     */ 
/* 656 */     int ret = this.soapStream.read();
/*     */ 
/* 658 */     if (ret < 0) {
/* 659 */       this.eos = true;
/*     */     }
/*     */ 
/* 662 */     return ret;
/*     */   }
/*     */ 
/*     */   public void close() throws IOException
/*     */   {
/* 667 */     this.closed = true;
/*     */ 
/* 669 */     this.soapStream.close();
/*     */   }
/*     */ 
/*     */   public int available() throws IOException {
/* 673 */     return (this.closed) || (this.eos) ? 0 : this.soapStream.available();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.attachments.MultiPartRelatedInputStream
 * JD-Core Version:    0.6.0
 */