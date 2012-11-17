/*      */ package org.apache.axis.utils;
/*      */ 
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import javax.swing.JSplitPane;
/*      */ 
/*      */ class tcpmon$13
/*      */   implements ActionListener
/*      */ {
/*      */   private final String val$switchStr;
/*      */   private final tcpmon.Listener this$1;
/*      */ 
/*      */   public void actionPerformed(ActionEvent event)
/*      */   {
/* 1580 */     if (this.val$switchStr.equals(event.getActionCommand())) {
/* 1581 */       int v = this.this$1.outPane.getOrientation();
/*      */ 
/* 1583 */       if (v == 0)
/*      */       {
/* 1585 */         this.this$1.outPane.setOrientation(1);
/*      */       }
/*      */       else
/*      */       {
/* 1589 */         this.this$1.outPane.setOrientation(0);
/*      */       }
/* 1591 */       this.this$1.outPane.setDividerLocation(0.5D);
/*      */     }
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.tcpmon.13
 * JD-Core Version:    0.6.0
 */