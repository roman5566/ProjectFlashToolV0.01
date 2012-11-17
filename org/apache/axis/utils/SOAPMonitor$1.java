/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import javax.swing.JProgressBar;
/*     */ 
/*     */ class SOAPMonitor$1
/*     */   implements Runnable
/*     */ {
/*     */   private final SOAPMonitor.BarThread this$1;
/*     */ 
/*     */   public void run()
/*     */   {
/* 484 */     int val = this.this$1.progressBar.getValue();
/* 485 */     this.this$1.progressBar.setValue(val + 1);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.SOAPMonitor.1
 * JD-Core Version:    0.6.0
 */