/*     */ package org.apache.axis.session;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ 
/*     */ public class SimpleSession
/*     */   implements Session
/*     */ {
/*  29 */   private Hashtable rep = null;
/*     */ 
/*  34 */   private int timeout = -1;
/*     */   private long lastTouched;
/*     */ 
/*     */   public SimpleSession()
/*     */   {
/*  42 */     this.lastTouched = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public Object get(String key)
/*     */   {
/*  51 */     if (this.rep == null)
/*  52 */       return null;
/*  53 */     this.lastTouched = System.currentTimeMillis();
/*  54 */     return this.rep.get(key);
/*     */   }
/*     */ 
/*     */   public void set(String key, Object value)
/*     */   {
/*  64 */     synchronized (this) {
/*  65 */       if (this.rep == null)
/*  66 */         this.rep = new Hashtable();
/*     */     }
/*  68 */     this.lastTouched = System.currentTimeMillis();
/*  69 */     this.rep.put(key, value);
/*     */   }
/*     */ 
/*     */   public void remove(String key)
/*     */   {
/*  78 */     if (this.rep != null)
/*  79 */       this.rep.remove(key);
/*  80 */     this.lastTouched = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public Enumeration getKeys()
/*     */   {
/*  87 */     if (this.rep != null)
/*  88 */       return this.rep.keys();
/*  89 */     return null;
/*     */   }
/*     */ 
/*     */   public void setTimeout(int timeout)
/*     */   {
/* 100 */     this.timeout = timeout;
/*     */   }
/*     */ 
/*     */   public int getTimeout()
/*     */   {
/* 105 */     return this.timeout;
/*     */   }
/*     */ 
/*     */   public void touch()
/*     */   {
/* 112 */     this.lastTouched = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public void invalidate()
/*     */   {
/* 119 */     this.rep = null;
/* 120 */     this.lastTouched = System.currentTimeMillis();
/* 121 */     this.timeout = -1;
/*     */   }
/*     */ 
/*     */   public long getLastAccessTime()
/*     */   {
/* 126 */     return this.lastTouched;
/*     */   }
/*     */ 
/*     */   public synchronized Object getLockObject()
/*     */   {
/* 138 */     if (this.rep == null) {
/* 139 */       this.rep = new Hashtable();
/*     */     }
/* 141 */     return this.rep;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.session.SimpleSession
 * JD-Core Version:    0.6.0
 */