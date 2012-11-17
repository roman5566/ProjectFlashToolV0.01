/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JRadioButton;
/*     */ 
/*     */ class tcpmon$5
/*     */   implements ActionListener
/*     */ {
/*     */   private final String val$add;
/*     */   private final tcpmon val$this$0;
/*     */   private final tcpmon.AdminPage this$1;
/*     */ 
/*     */   public void actionPerformed(ActionEvent event)
/*     */   {
/* 377 */     if (this.val$add.equals(event.getActionCommand()))
/*     */     {
/* 379 */       tcpmon.Listener l = null;
/*     */ 
/* 381 */       int lPort = this.this$1.port.getValue(0);
/* 382 */       if (lPort == 0)
/*     */       {
/* 384 */         return;
/*     */       }
/* 386 */       String tHost = this.this$1.host.getText();
/* 387 */       int tPort = 0;
/* 388 */       tPort = this.this$1.tport.getValue(0);
/* 389 */       tcpmon.SlowLinkSimulator slowLink = null;
/* 390 */       if (this.this$1.delayBox.isSelected()) {
/* 391 */         int bytes = this.this$1.delayBytes.getValue(0);
/* 392 */         int time = this.this$1.delayTime.getValue(0);
/* 393 */         slowLink = new tcpmon.SlowLinkSimulator(bytes, time);
/*     */       }
/*     */       try {
/* 396 */         l = new tcpmon.Listener(tcpmon.AdminPage.access$000(this.this$1), this.this$1.noteb, null, lPort, tHost, tPort, this.this$1.proxyButton.isSelected(), slowLink);
/*     */       }
/*     */       catch (Exception e) {
/* 399 */         e.printStackTrace();
/*     */       }
/*     */ 
/* 403 */       String text = this.this$1.HTTPProxyHost.getText();
/* 404 */       if ("".equals(text)) {
/* 405 */         text = null;
/*     */       }
/* 407 */       l.HTTPProxyHost = text;
/* 408 */       text = this.this$1.HTTPProxyPort.getText();
/* 409 */       int proxyPort = this.this$1.HTTPProxyPort.getValue(-1);
/* 410 */       if (proxyPort != -1) {
/* 411 */         l.HTTPProxyPort = Integer.parseInt(text);
/*     */       }
/*     */ 
/* 414 */       this.this$1.port.setText(null);
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.tcpmon.5
 * JD-Core Version:    0.6.0
 */