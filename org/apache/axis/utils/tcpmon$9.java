/*      */ package org.apache.axis.utils;
/*      */ 
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ 
/*      */ class tcpmon$9
/*      */   implements ActionListener
/*      */ {
/*      */   private final String val$removeSelected;
/*      */   private final tcpmon.Listener this$1;
/*      */ 
/*      */   public void actionPerformed(ActionEvent event)
/*      */   {
/* 1496 */     if (this.val$removeSelected.equals(event.getActionCommand()))
/* 1497 */       this.this$1.remove();
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.tcpmon.9
 * JD-Core Version:    0.6.0
 */