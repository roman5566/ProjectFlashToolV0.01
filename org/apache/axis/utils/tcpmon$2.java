/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JRadioButton;
/*     */ 
/*     */ class tcpmon$2
/*     */   implements ActionListener
/*     */ {
/*     */   private final String val$proxy;
/*     */   private final tcpmon.AdminPage this$1;
/*     */ 
/*     */   public void actionPerformed(ActionEvent event)
/*     */   {
/* 218 */     if (this.val$proxy.equals(event.getActionCommand())) {
/* 219 */       boolean state = this.this$1.proxyButton.isSelected();
/*     */ 
/* 221 */       this.this$1.tport.setEnabled(!state);
/* 222 */       this.this$1.host.setEnabled(!state);
/* 223 */       this.this$1.hostLabel.setForeground(state ? Color.gray : Color.black);
/* 224 */       this.this$1.tportLabel.setForeground(state ? Color.gray : Color.black);
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.tcpmon.2
 * JD-Core Version:    0.6.0
 */