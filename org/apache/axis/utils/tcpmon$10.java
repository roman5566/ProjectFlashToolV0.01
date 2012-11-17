/*      */ package org.apache.axis.utils;
/*      */ 
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ 
/*      */ class tcpmon$10
/*      */   implements ActionListener
/*      */ {
/*      */   private final String val$removeAll;
/*      */   private final tcpmon.Listener this$1;
/*      */ 
/*      */   public void actionPerformed(ActionEvent event)
/*      */   {
/* 1506 */     if (this.val$removeAll.equals(event.getActionCommand()))
/* 1507 */       this.this$1.removeAll();
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.tcpmon.10
 * JD-Core Version:    0.6.0
 */