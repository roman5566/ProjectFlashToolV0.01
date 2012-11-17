/*     */ package org.apache.axis.client.async;
/*     */ 
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.client.Call;
/*     */ 
/*     */ public class AsyncResult
/*     */   implements IAsyncResult, Runnable
/*     */ {
/*  31 */   private Thread thread = null;
/*     */ 
/*  36 */   private Object response = null;
/*     */ 
/*  41 */   private Throwable exception = null;
/*     */ 
/*  46 */   private AsyncCall ac = null;
/*     */ 
/*  51 */   private QName opName = null;
/*     */ 
/*  56 */   private Object[] params = null;
/*     */ 
/*  61 */   private Status status = Status.NONE;
/*     */ 
/*     */   public AsyncResult(AsyncCall ac, QName opName, Object[] params)
/*     */   {
/*  71 */     this.ac = ac;
/*  72 */     this.opName = opName;
/*  73 */     this.params = params;
/*     */ 
/*  75 */     if (opName == null) {
/*  76 */       this.opName = ac.getCall().getOperationName();
/*     */     }
/*     */ 
/*  79 */     this.thread = new Thread(this);
/*  80 */     this.thread.setDaemon(true);
/*  81 */     this.thread.start();
/*     */   }
/*     */ 
/*     */   public void abort()
/*     */   {
/*  88 */     this.thread.interrupt();
/*  89 */     this.status = Status.INTERRUPTED;
/*     */   }
/*     */ 
/*     */   public Status getStatus()
/*     */   {
/*  98 */     return this.status;
/*     */   }
/*     */ 
/*     */   public void waitFor(long timeout)
/*     */     throws InterruptedException
/*     */   {
/* 108 */     this.thread.wait(timeout);
/*     */   }
/*     */ 
/*     */   public Object getResponse()
/*     */   {
/* 117 */     return this.response;
/*     */   }
/*     */ 
/*     */   public Throwable getException()
/*     */   {
/* 126 */     return this.exception;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     try
/*     */     {
/* 134 */       this.response = this.ac.getCall().invoke(this.opName, this.params);
/* 135 */       this.status = Status.COMPLETED;
/*     */     } catch (Throwable e) {
/* 137 */       this.exception = e;
/* 138 */       this.status = Status.EXCEPTION;
/*     */     } finally {
/* 140 */       IAsyncCallback callback = this.ac.getCallback();
/* 141 */       if (callback != null)
/* 142 */         callback.onCompletion(this);
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.client.async.AsyncResult
 * JD-Core Version:    0.6.0
 */