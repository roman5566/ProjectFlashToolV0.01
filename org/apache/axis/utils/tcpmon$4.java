/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JLabel;
/*     */ 
/*     */ class tcpmon$4
/*     */   implements ActionListener
/*     */ {
/*     */   private final String val$delaySupport;
/*     */   private final tcpmon.AdminPage this$1;
/*     */ 
/*     */   public void actionPerformed(ActionEvent event)
/*     */   {
/* 346 */     if (this.val$delaySupport.equals(event.getActionCommand())) {
/* 347 */       boolean b = this.this$1.delayBox.isSelected();
/* 348 */       Color color = b ? Color.black : Color.gray;
/*     */ 
/* 350 */       this.this$1.delayBytes.setEnabled(b);
/* 351 */       this.this$1.delayTime.setEnabled(b);
/* 352 */       this.this$1.delayBytesLabel.setForeground(color);
/* 353 */       this.this$1.delayTimeLabel.setForeground(color);
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.tcpmon.4
 * JD-Core Version:    0.6.0
 */