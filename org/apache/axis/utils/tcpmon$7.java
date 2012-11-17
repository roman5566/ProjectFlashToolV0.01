/*      */ package org.apache.axis.utils;
/*      */ 
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ 
/*      */ class tcpmon$7
/*      */   implements ActionListener
/*      */ {
/*      */   private final String val$start;
/*      */   private final tcpmon.Listener this$1;
/*      */ 
/*      */   public void actionPerformed(ActionEvent event)
/*      */   {
/* 1375 */     if (tcpmon.getMessage("stop00", "Stop").equals(event.getActionCommand())) {
/* 1376 */       this.this$1.stop();
/*      */     }
/* 1378 */     if (this.val$start.equals(event.getActionCommand()))
/* 1379 */       this.this$1.start();
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.tcpmon.7
 * JD-Core Version:    0.6.0
 */