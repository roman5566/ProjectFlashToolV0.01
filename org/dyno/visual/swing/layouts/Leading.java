/*    */ package org.dyno.visual.swing.layouts;
/*    */ 
/*    */ public class Leading extends Alignment
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private int leading;
/*    */   private int size;
/*    */ 
/*    */   public Leading(int leading, int size, Spring spring)
/*    */   {
/* 28 */     super(spring);
/* 29 */     this.leading = leading;
/* 30 */     this.size = size;
/*    */   }
/*    */   public Leading(int leading, int min, int pref) {
/* 33 */     this(leading, -1, min, pref);
/*    */   }
/*    */   public Leading(int leading, int size, int min, int pref) {
/* 36 */     super(min, pref);
/* 37 */     this.leading = leading;
/* 38 */     this.size = size;
/*    */   }
/*    */   public int getLeading() {
/* 41 */     return this.leading;
/*    */   }
/*    */ 
/*    */   public int getSize() {
/* 45 */     return this.size;
/*    */   }
/*    */ 
/*    */   public void setSize(int size) {
/* 49 */     this.size = size;
/*    */   }
/*    */ 
/*    */   public void setLeading(int leading) {
/* 53 */     this.leading = leading;
/*    */   }
/*    */ 
/*    */   public Object clone()
/*    */   {
/* 58 */     return new Leading(this.leading, this.size, (Spring)getSpring().clone());
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.dyno.visual.swing.layouts.Leading
 * JD-Core Version:    0.6.0
 */