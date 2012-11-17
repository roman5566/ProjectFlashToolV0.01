/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JLabel;
/*     */ 
/*     */ class tcpmon$3
/*     */   implements ActionListener
/*     */ {
/*     */   private final String val$proxySupport;
/*     */   private final tcpmon.AdminPage this$1;
/*     */ 
/*     */   public void actionPerformed(ActionEvent event)
/*     */   {
/* 275 */     if (this.val$proxySupport.equals(event.getActionCommand())) {
/* 276 */       boolean b = this.this$1.HTTPProxyBox.isSelected();
/* 277 */       Color color = b ? Color.black : Color.gray;
/*     */ 
/* 279 */       this.this$1.HTTPProxyHost.setEnabled(b);
/* 280 */       this.this$1.HTTPProxyPort.setEnabled(b);
/* 281 */       this.this$1.HTTPProxyHostLabel.setForeground(color);
/* 282 */       this.this$1.HTTPProxyPortLabel.setForeground(color);
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.tcpmon.3
 * JD-Core Version:    0.6.0
 */