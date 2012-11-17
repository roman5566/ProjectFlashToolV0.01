/*     */ package org.apache.axis.components.threadpool;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.i18n.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class ThreadPool
/*     */ {
/*  32 */   protected static Log log = LogFactory.getLog(ThreadPool.class.getName());
/*     */   public static final int DEFAULT_MAX_THREADS = 100;
/*  37 */   protected Map threads = new Hashtable();
/*     */   protected long threadcount;
/*     */   public boolean _shutdown;
/*  40 */   private int maxPoolSize = 100;
/*     */ 
/*     */   public ThreadPool() {
/*     */   }
/*     */ 
/*     */   public ThreadPool(int maxPoolSize) {
/*  46 */     this.maxPoolSize = maxPoolSize;
/*     */   }
/*     */ 
/*     */   public void cleanup() throws InterruptedException
/*     */   {
/*  51 */     if (log.isDebugEnabled()) {
/*  52 */       log.debug("Enter: ThreadPool::cleanup");
/*     */     }
/*  54 */     if (!isShutdown()) {
/*  55 */       safeShutdown();
/*  56 */       awaitShutdown();
/*     */     }
/*  58 */     synchronized (this) {
/*  59 */       this.threads.clear();
/*  60 */       this._shutdown = false;
/*     */     }
/*  62 */     if (log.isDebugEnabled())
/*  63 */       log.debug("Exit: ThreadPool::cleanup");
/*     */   }
/*     */ 
/*     */   public boolean isShutdown()
/*     */   {
/*  71 */     synchronized (this) {
/*  72 */       return (this._shutdown) && (this.threadcount == 0L);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isShuttingDown()
/*     */   {
/*  80 */     synchronized (this) {
/*  81 */       return this._shutdown;
/*     */     }
/*     */   }
/*     */ 
/*     */   public long getWorkerCount()
/*     */   {
/*  89 */     synchronized (this) {
/*  90 */       return this.threadcount;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addWorker(Runnable worker)
/*     */   {
/*  99 */     if (log.isDebugEnabled()) {
/* 100 */       log.debug("Enter: ThreadPool::addWorker");
/*     */     }
/* 102 */     if ((this._shutdown) || (this.threadcount == this.maxPoolSize)) {
/* 103 */       throw new IllegalStateException(Messages.getMessage("illegalStateException00"));
/*     */     }
/* 105 */     Thread thread = new Thread(worker);
/* 106 */     this.threads.put(worker, thread);
/* 107 */     this.threadcount += 1L;
/* 108 */     thread.start();
/* 109 */     if (log.isDebugEnabled())
/* 110 */       log.debug("Exit: ThreadPool::addWorker");
/*     */   }
/*     */ 
/*     */   public void interruptAll()
/*     */   {
/* 118 */     if (log.isDebugEnabled())
/* 119 */       log.debug("Enter: ThreadPool::interruptAll");
/*     */     Iterator i;
/* 121 */     synchronized (this.threads) {
/* 122 */       for (i = this.threads.values().iterator(); i.hasNext(); ) {
/* 123 */         Thread t = (Thread)i.next();
/* 124 */         t.interrupt();
/*     */       }
/*     */     }
/* 127 */     if (log.isDebugEnabled())
/* 128 */       log.debug("Exit: ThreadPool::interruptAll");
/*     */   }
/*     */ 
/*     */   public void shutdown()
/*     */   {
/* 136 */     if (log.isDebugEnabled()) {
/* 137 */       log.debug("Enter: ThreadPool::shutdown");
/*     */     }
/* 139 */     synchronized (this) {
/* 140 */       this._shutdown = true;
/*     */     }
/* 142 */     interruptAll();
/* 143 */     if (log.isDebugEnabled())
/* 144 */       log.debug("Exit: ThreadPool::shutdown");
/*     */   }
/*     */ 
/*     */   public void safeShutdown()
/*     */   {
/* 152 */     if (log.isDebugEnabled()) {
/* 153 */       log.debug("Enter: ThreadPool::safeShutdown");
/*     */     }
/* 155 */     synchronized (this) {
/* 156 */       this._shutdown = true;
/*     */     }
/* 158 */     if (log.isDebugEnabled())
/* 159 */       log.debug("Exit: ThreadPool::safeShutdown");
/*     */   }
/*     */ 
/*     */   public synchronized void awaitShutdown()
/*     */     throws InterruptedException
/*     */   {
/* 168 */     if (log.isDebugEnabled()) {
/* 169 */       log.debug("Enter: ThreadPool::awaitShutdown");
/*     */     }
/* 171 */     if (!this._shutdown)
/* 172 */       throw new IllegalStateException(Messages.getMessage("illegalStateException00"));
/* 173 */     while (this.threadcount > 0L)
/* 174 */       wait();
/* 175 */     if (log.isDebugEnabled())
/* 176 */       log.debug("Exit: ThreadPool::awaitShutdown");
/*     */   }
/*     */ 
/*     */   public synchronized boolean awaitShutdown(long timeout)
/*     */     throws InterruptedException
/*     */   {
/* 185 */     if (log.isDebugEnabled()) {
/* 186 */       log.debug("Enter: ThreadPool::awaitShutdown");
/*     */     }
/* 188 */     if (!this._shutdown)
/* 189 */       throw new IllegalStateException(Messages.getMessage("illegalStateException00"));
/* 190 */     if (this.threadcount == 0L) {
/* 191 */       if (log.isDebugEnabled()) {
/* 192 */         log.debug("Exit: ThreadPool::awaitShutdown");
/*     */       }
/* 194 */       return true;
/*     */     }
/* 196 */     long waittime = timeout;
/* 197 */     if (waittime <= 0L) {
/* 198 */       if (log.isDebugEnabled()) {
/* 199 */         log.debug("Exit: ThreadPool::awaitShutdown");
/*     */       }
/* 201 */       return false;
/*     */     }
/*     */     do {
/* 204 */       wait(waittime);
/* 205 */       if (this.threadcount == 0L) {
/* 206 */         if (log.isDebugEnabled()) {
/* 207 */           log.debug("Exit: ThreadPool::awaitShutdown");
/*     */         }
/* 209 */         return true;
/*     */       }
/* 211 */       waittime = timeout - System.currentTimeMillis();
/* 212 */     }while (waittime > 0L);
/* 213 */     if (log.isDebugEnabled()) {
/* 214 */       log.debug("Exit: ThreadPool::awaitShutdown");
/*     */     }
/* 216 */     return false;
/*     */   }
/*     */ 
/*     */   public void workerDone(Runnable worker, boolean restart)
/*     */   {
/* 227 */     if (log.isDebugEnabled()) {
/* 228 */       log.debug("Enter: ThreadPool::workerDone");
/*     */     }
/* 230 */     synchronized (this) {
/* 231 */       this.threads.remove(worker);
/* 232 */       if ((--this.threadcount == 0L) && (this._shutdown)) {
/* 233 */         notifyAll();
/*     */       }
/* 235 */       if ((!this._shutdown) && (restart)) {
/* 236 */         addWorker(worker);
/*     */       }
/*     */     }
/* 239 */     if (log.isDebugEnabled())
/* 240 */       log.debug("Exit: ThreadPool::workerDone");
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.threadpool.ThreadPool
 * JD-Core Version:    0.6.0
 */