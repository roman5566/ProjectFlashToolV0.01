/*    */ package gui.mainform;
/*    */ 
/*    */ import java.awt.Dimension;
/*    */ import javax.swing.JFrame;
/*    */ 
/*    */ public class MainFormUi extends JFrame
/*    */ {
/*    */   MainFormUIPanel MFUIP;
/*    */ 
/*    */   public MainFormUi()
/*    */   {
/* 12 */     super("Project Replace 360 Flash Tool V0.01 Alpha");
/* 13 */     System.setProperty("apple.laf.useScreenMenuBar", "true");
/* 14 */     System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Project Flash Tool");
/* 15 */     this.MFUIP = new MainFormUIPanel(this);
/* 16 */     initComponents();
/*    */   }
/*    */ 
/*    */   private void initComponents() {
/* 20 */     setSize(550, 800);
/* 21 */     setMaximumSize(new Dimension(550, 800));
/* 22 */     setMinimumSize(new Dimension(550, 800));
/* 23 */     setResizable(false);
/* 24 */     setLocation(50, 50);
/* 25 */     setDefaultCloseOperation(3);
/* 26 */     setLayout(null);
/* 27 */     setName("Exploit Flash Tool");
/*    */ 
/* 29 */     add(this.MFUIP);
/* 30 */     setVisible(true);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     gui.mainform.MainFormUi
 * JD-Core Version:    0.6.0
 */