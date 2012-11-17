/*      */ package org.apache.axis.utils;
/*      */ 
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ 
/*      */ class tcpmon$11
/*      */   implements ActionListener
/*      */ {
/*      */   private final String val$save;
/*      */   private final tcpmon.Listener this$1;
/*      */ 
/*      */   public void actionPerformed(ActionEvent event)
/*      */   {
/* 1561 */     if (this.val$save.equals(event.getActionCommand()))
/* 1562 */       this.this$1.save();
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.tcpmon.11
 * JD-Core Version:    0.6.0
 */