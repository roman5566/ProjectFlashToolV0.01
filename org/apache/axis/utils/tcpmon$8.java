/*      */ package org.apache.axis.utils;
/*      */ 
/*      */ import java.util.Vector;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JSplitPane;
/*      */ import javax.swing.ListSelectionModel;
/*      */ import javax.swing.event.ListSelectionEvent;
/*      */ import javax.swing.event.ListSelectionListener;
/*      */ 
/*      */ class tcpmon$8
/*      */   implements ListSelectionListener
/*      */ {
/*      */   private final tcpmon.Listener this$1;
/*      */ 
/*      */   public void valueChanged(ListSelectionEvent event)
/*      */   {
/* 1420 */     if (event.getValueIsAdjusting()) {
/* 1421 */       return;
/*      */     }
/* 1423 */     ListSelectionModel m = (ListSelectionModel)event.getSource();
/* 1424 */     int divLoc = this.this$1.outPane.getDividerLocation();
/*      */ 
/* 1426 */     if (m.isSelectionEmpty()) {
/* 1427 */       this.this$1.setLeft(new JLabel(" " + tcpmon.getMessage("wait00", "Waiting for Connection...")));
/* 1428 */       this.this$1.setRight(new JLabel(""));
/* 1429 */       this.this$1.removeButton.setEnabled(false);
/* 1430 */       this.this$1.removeAllButton.setEnabled(false);
/* 1431 */       this.this$1.saveButton.setEnabled(false);
/* 1432 */       this.this$1.resendButton.setEnabled(false);
/*      */     }
/*      */     else {
/* 1435 */       int row = m.getLeadSelectionIndex();
/*      */ 
/* 1437 */       if (row == 0) {
/* 1438 */         if (this.this$1.connections.size() == 0) {
/* 1439 */           this.this$1.setLeft(new JLabel(" " + tcpmon.getMessage("wait00", "Waiting for connection...")));
/* 1440 */           this.this$1.setRight(new JLabel(""));
/* 1441 */           this.this$1.removeButton.setEnabled(false);
/* 1442 */           this.this$1.removeAllButton.setEnabled(false);
/* 1443 */           this.this$1.saveButton.setEnabled(false);
/* 1444 */           this.this$1.resendButton.setEnabled(false);
/*      */         }
/*      */         else {
/* 1447 */           tcpmon.Connection conn = (tcpmon.Connection)this.this$1.connections.lastElement();
/*      */ 
/* 1449 */           this.this$1.setLeft(conn.inputScroll);
/* 1450 */           this.this$1.setRight(conn.outputScroll);
/* 1451 */           this.this$1.removeButton.setEnabled(false);
/* 1452 */           this.this$1.removeAllButton.setEnabled(true);
/* 1453 */           this.this$1.saveButton.setEnabled(true);
/* 1454 */           this.this$1.resendButton.setEnabled(true);
/*      */         }
/*      */       }
/*      */       else {
/* 1458 */         tcpmon.Connection conn = (tcpmon.Connection)this.this$1.connections.get(row - 1);
/*      */ 
/* 1460 */         this.this$1.setLeft(conn.inputScroll);
/* 1461 */         this.this$1.setRight(conn.outputScroll);
/* 1462 */         this.this$1.removeButton.setEnabled(true);
/* 1463 */         this.this$1.removeAllButton.setEnabled(true);
/* 1464 */         this.this$1.saveButton.setEnabled(true);
/* 1465 */         this.this$1.resendButton.setEnabled(true);
/*      */       }
/*      */     }
/* 1468 */     this.this$1.outPane.setDividerLocation(divLoc);
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.tcpmon.8
 * JD-Core Version:    0.6.0
 */