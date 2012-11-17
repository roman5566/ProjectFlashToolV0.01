/*      */ package org.apache.axis.utils;
/*      */ 
/*      */ import javax.swing.AbstractButton;
/*      */ import javax.swing.JCheckBox;
/*      */ import javax.swing.JTextField;
/*      */ import javax.swing.event.ChangeEvent;
/*      */ import javax.swing.plaf.basic.BasicButtonListener;
/*      */ 
/*      */ class tcpmon$6 extends BasicButtonListener
/*      */ {
/*      */   private final tcpmon.Listener this$1;
/*      */ 
/*      */   public void stateChanged(ChangeEvent event)
/*      */   {
/* 1355 */     JCheckBox box = (JCheckBox)event.getSource();
/* 1356 */     boolean state = box.isSelected();
/*      */ 
/* 1358 */     this.this$1.tPortField.setEnabled(!state);
/* 1359 */     this.this$1.hostField.setEnabled(!state);
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.tcpmon.6
 * JD-Core Version:    0.6.0
 */