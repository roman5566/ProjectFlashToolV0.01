/*     */ package org.apache.axis.client.async;
/*     */ 
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.client.Call;
/*     */ 
/*     */ public class AsyncCall
/*     */ {
/*  33 */   private Call call = null;
/*     */ 
/*  38 */   private IAsyncCallback callback = null;
/*     */ 
/*     */   public AsyncCall(Call call)
/*     */   {
/*  46 */     this(call, null);
/*     */   }
/*     */ 
/*     */   public AsyncCall(Call call, IAsyncCallback callback)
/*     */   {
/*  56 */     this.call = call;
/*  57 */     this.callback = callback;
/*     */   }
/*     */ 
/*     */   public IAsyncCallback getCallback()
/*     */   {
/*  66 */     return this.callback;
/*     */   }
/*     */ 
/*     */   public void setCallback(IAsyncCallback callback)
/*     */   {
/*  75 */     this.callback = callback;
/*     */   }
/*     */ 
/*     */   public IAsyncResult invoke(Object[] inputParams)
/*     */   {
/*  85 */     return new AsyncResult(this, null, inputParams);
/*     */   }
/*     */ 
/*     */   public IAsyncResult invoke(QName qName, Object[] inputParams)
/*     */   {
/*  96 */     return new AsyncResult(this, qName, inputParams);
/*     */   }
/*     */ 
/*     */   public Call getCall()
/*     */   {
/* 105 */     return this.call;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.client.async.AsyncCall
 * JD-Core Version:    0.6.0
 */