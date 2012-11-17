/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import org.apache.axis.AxisProperties;
/*     */ 
/*     */ public class ByteArray extends OutputStream
/*     */ {
/*  37 */   protected static double DEFAULT_CACHE_INCREMENT = 2.5D;
/*  38 */   protected static int DEFAULT_RESIDENT_SIZE = 536870912;
/*  39 */   protected static boolean DEFAULT_ENABLE_BACKING_STORE = true;
/*  40 */   protected static int WORKING_BUFFER_SIZE = 8192;
/*     */ 
/*  42 */   protected ByteArrayOutputStream cache = null;
/*     */ 
/*  44 */   protected int max_size = 0;
/*  45 */   protected File bs_handle = null;
/*  46 */   protected OutputStream bs_stream = null;
/*  47 */   protected long count = 0L;
/*  48 */   protected boolean enableBackingStore = DEFAULT_ENABLE_BACKING_STORE;
/*     */ 
/*     */   public boolean isEnableBackingStore() {
/*  51 */     return this.enableBackingStore;
/*     */   }
/*     */ 
/*     */   public void setEnableBackingStore(boolean enableBackingStore) {
/*  55 */     this.enableBackingStore = enableBackingStore;
/*     */   }
/*     */ 
/*     */   public static boolean isDEFAULT_ENABLE_BACKING_STORE() {
/*  59 */     return DEFAULT_ENABLE_BACKING_STORE;
/*     */   }
/*     */ 
/*     */   public static void setDEFAULT_ENABLE_BACKING_STORE(boolean DEFAULT_ENABLE_BACKING_STORE)
/*     */   {
/*  64 */     DEFAULT_ENABLE_BACKING_STORE = DEFAULT_ENABLE_BACKING_STORE;
/*     */   }
/*     */ 
/*     */   public static int getDEFAULT_RESIDENT_SIZE() {
/*  68 */     return DEFAULT_RESIDENT_SIZE;
/*     */   }
/*     */ 
/*     */   public static void setDEFAULT_RESIDENT_SIZE(int DEFAULT_RESIDENT_SIZE) {
/*  72 */     DEFAULT_RESIDENT_SIZE = DEFAULT_RESIDENT_SIZE;
/*     */   }
/*     */ 
/*     */   public static double getDEFAULT_CACHE_INCREMENT() {
/*  76 */     return DEFAULT_CACHE_INCREMENT;
/*     */   }
/*     */ 
/*     */   public static void setDEFAULT_CACHE_INCREMENT(double DEFAULT_CACHE_INCREMENT)
/*     */   {
/*  81 */     DEFAULT_CACHE_INCREMENT = DEFAULT_CACHE_INCREMENT;
/*     */   }
/*     */ 
/*     */   public ByteArray()
/*     */   {
/* 114 */     this(DEFAULT_RESIDENT_SIZE);
/*     */   }
/*     */ 
/*     */   public ByteArray(int max_resident_size)
/*     */   {
/* 123 */     this(0, max_resident_size);
/*     */   }
/*     */ 
/*     */   public ByteArray(int probable_size, int max_resident_size)
/*     */   {
/* 133 */     if (probable_size > max_resident_size) {
/* 134 */       probable_size = 0;
/*     */     }
/* 136 */     if (probable_size < WORKING_BUFFER_SIZE) {
/* 137 */       probable_size = WORKING_BUFFER_SIZE;
/*     */     }
/* 139 */     this.cache = new ByteArrayOutputStream(probable_size);
/* 140 */     this.max_size = max_resident_size;
/*     */   }
/*     */ 
/*     */   public void write(byte[] bytes)
/*     */     throws IOException
/*     */   {
/* 150 */     write(bytes, 0, bytes.length);
/*     */   }
/*     */ 
/*     */   public void write(byte[] bytes, int start, int length)
/*     */     throws IOException
/*     */   {
/* 162 */     this.count += length;
/* 163 */     if (this.cache != null) {
/* 164 */       increaseCapacity(length);
/*     */     }
/* 166 */     if (this.cache != null)
/* 167 */       this.cache.write(bytes, start, length);
/* 168 */     else if (this.bs_stream != null)
/* 169 */       this.bs_stream.write(bytes, start, length);
/*     */     else
/* 171 */       throw new IOException("ByteArray does not have a backing store!");
/*     */   }
/*     */ 
/*     */   public void write(int b)
/*     */     throws IOException
/*     */   {
/* 182 */     this.count += 1L;
/* 183 */     if (this.cache != null) {
/* 184 */       increaseCapacity(1);
/*     */     }
/* 186 */     if (this.cache != null)
/* 187 */       this.cache.write(b);
/* 188 */     else if (this.bs_stream != null)
/* 189 */       this.bs_stream.write(b);
/*     */     else
/* 191 */       throw new IOException("ByteArray does not have a backing store!");
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 201 */     if (this.bs_stream != null) {
/* 202 */       this.bs_stream.close();
/* 203 */       this.bs_stream = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public long size()
/*     */   {
/* 213 */     return this.count;
/*     */   }
/*     */ 
/*     */   public void flush()
/*     */     throws IOException
/*     */   {
/* 222 */     if (this.bs_stream != null)
/* 223 */       this.bs_stream.flush();
/*     */   }
/*     */ 
/*     */   protected void increaseCapacity(int count)
/*     */     throws IOException
/*     */   {
/* 234 */     if (this.cache == null) {
/* 235 */       return;
/*     */     }
/* 237 */     if (count + this.cache.size() <= this.max_size)
/* 238 */       return;
/* 239 */     if (this.enableBackingStore)
/* 240 */       switchToBackingStore();
/*     */     else
/* 242 */       throw new IOException("ByteArray can not increase capacity by " + count + " due to max size limit of " + this.max_size);
/*     */   }
/*     */ 
/*     */   public synchronized void discardBuffer()
/*     */   {
/* 252 */     this.cache = null;
/* 253 */     if (this.bs_stream != null) {
/*     */       try {
/* 255 */         this.bs_stream.close();
/*     */       }
/*     */       catch (IOException e) {
/*     */       }
/* 259 */       this.bs_stream = null;
/*     */     }
/* 261 */     discardBackingStore();
/*     */   }
/*     */ 
/*     */   protected InputStream makeInputStream()
/*     */     throws IOException, FileNotFoundException
/*     */   {
/* 273 */     close();
/* 274 */     if (this.cache != null)
/* 275 */       return new ByteArrayInputStream(this.cache.toByteArray());
/* 276 */     if (this.bs_handle != null) {
/* 277 */       return createBackingStoreInputStream();
/*     */     }
/* 279 */     return null;
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */   {
/* 287 */     discardBuffer();
/*     */   }
/*     */ 
/*     */   protected void switchToBackingStore()
/*     */     throws IOException
/*     */   {
/* 296 */     this.bs_handle = File.createTempFile("Axis", ".msg");
/* 297 */     this.bs_handle.createNewFile();
/* 298 */     this.bs_handle.deleteOnExit();
/* 299 */     this.bs_stream = new FileOutputStream(this.bs_handle);
/* 300 */     this.bs_stream.write(this.cache.toByteArray());
/* 301 */     this.cache = null;
/*     */   }
/*     */ 
/*     */   public String getBackingStoreFileName()
/*     */     throws IOException
/*     */   {
/* 310 */     String fileName = null;
/* 311 */     if (this.bs_handle != null) {
/* 312 */       fileName = this.bs_handle.getCanonicalPath();
/*     */     }
/* 314 */     return fileName;
/*     */   }
/*     */ 
/*     */   protected void discardBackingStore()
/*     */   {
/* 321 */     if (this.bs_handle != null) {
/* 322 */       this.bs_handle.delete();
/* 323 */       this.bs_handle = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected InputStream createBackingStoreInputStream()
/*     */     throws FileNotFoundException
/*     */   {
/*     */     try
/*     */     {
/* 336 */       return new BufferedInputStream(new FileInputStream(this.bs_handle.getCanonicalPath()));
/*     */     } catch (IOException e) {
/*     */     }
/* 339 */     throw new FileNotFoundException(this.bs_handle.getAbsolutePath());
/*     */   }
/*     */ 
/*     */   public byte[] toByteArray()
/*     */     throws IOException
/*     */   {
/* 350 */     InputStream inp = makeInputStream();
/* 351 */     byte[] buf = null;
/* 352 */     java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
/* 353 */     buf = new byte[WORKING_BUFFER_SIZE];
/*     */     int len;
/* 355 */     while ((len = inp.read(buf, 0, WORKING_BUFFER_SIZE)) != -1) {
/* 356 */       baos.write(buf, 0, len);
/*     */     }
/* 358 */     inp.close();
/* 359 */     discardBackingStore();
/* 360 */     return baos.toByteArray();
/*     */   }
/*     */ 
/*     */   public void writeTo(OutputStream os)
/*     */     throws IOException
/*     */   {
/* 370 */     InputStream inp = makeInputStream();
/* 371 */     byte[] buf = null;
/* 372 */     buf = new byte[WORKING_BUFFER_SIZE];
/*     */     int len;
/* 374 */     while ((len = inp.read(buf, 0, WORKING_BUFFER_SIZE)) != -1) {
/* 375 */       os.write(buf, 0, len);
/*     */     }
/* 377 */     inp.close();
/* 378 */     discardBackingStore();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  86 */     String value = AxisProperties.getProperty("axis.byteBuffer.cacheIncrement", "" + DEFAULT_CACHE_INCREMENT);
/*     */ 
/*  89 */     DEFAULT_CACHE_INCREMENT = Double.parseDouble(value);
/*  90 */     value = AxisProperties.getProperty("axis.byteBuffer.residentMaxSize", "" + DEFAULT_RESIDENT_SIZE);
/*     */ 
/*  93 */     DEFAULT_RESIDENT_SIZE = Integer.parseInt(value);
/*  94 */     value = AxisProperties.getProperty("axis.byteBuffer.workBufferSize", "" + WORKING_BUFFER_SIZE);
/*     */ 
/*  97 */     WORKING_BUFFER_SIZE = Integer.parseInt(value);
/*  98 */     value = AxisProperties.getProperty("axis.byteBuffer.backing", "" + DEFAULT_ENABLE_BACKING_STORE);
/*     */ 
/* 101 */     if ((value.equalsIgnoreCase("true")) || (value.equals("1")) || (value.equalsIgnoreCase("yes")))
/*     */     {
/* 104 */       DEFAULT_ENABLE_BACKING_STORE = true;
/*     */     }
/* 106 */     else DEFAULT_ENABLE_BACKING_STORE = false;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.ByteArray
 * JD-Core Version:    0.6.0
 */