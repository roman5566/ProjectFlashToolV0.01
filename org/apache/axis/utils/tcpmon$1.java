/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JRadioButton;
/*     */ 
/*     */ class tcpmon$1
/*     */   implements ActionListener
/*     */ {
/*     */   private final String val$listener;
/*     */   private final tcpmon.AdminPage this$1;
/*     */ 
/*     */   public void actionPerformed(ActionEvent event)
/*     */   {
/* 173 */     if (this.val$listener.equals(event.getActionCommand())) {
/* 174 */       boolean state = this.this$1.listenerButton.isSelected();
/*     */ 
/* 176 */       this.this$1.tport.setEnabled(state);
/* 177 */       this.this$1.host.setEnabled(state);
/* 178 */       this.this$1.hostLabel.setForeground(state ? Color.black : Color.gray);
/* 179 */       this.this$1.tportLabel.setForeground(state ? Color.black : Color.gray);
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.tcpmon.1
 * JD-Core Version:    0.6.0
 */