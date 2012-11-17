/*      */ package org.apache.axis.utils;
/*      */ 
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ 
/*      */ class tcpmon$14
/*      */   implements ActionListener
/*      */ {
/*      */   private final String val$close;
/*      */   private final tcpmon.Listener this$1;
/*      */ 
/*      */   public void actionPerformed(ActionEvent event)
/*      */   {
/* 1599 */     if (this.val$close.equals(event.getActionCommand()))
/* 1600 */       this.this$1.close();
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.tcpmon.14
 * JD-Core Version:    0.6.0
 */