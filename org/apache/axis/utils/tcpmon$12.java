/*      */ package org.apache.axis.utils;
/*      */ 
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ 
/*      */ class tcpmon$12
/*      */   implements ActionListener
/*      */ {
/*      */   private final String val$resend;
/*      */   private final tcpmon.Listener this$1;
/*      */ 
/*      */   public void actionPerformed(ActionEvent event)
/*      */   {
/* 1571 */     if (this.val$resend.equals(event.getActionCommand()))
/* 1572 */       this.this$1.resend();
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.tcpmon.12
 * JD-Core Version:    0.6.0
 */