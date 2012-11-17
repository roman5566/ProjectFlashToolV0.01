/*    */ package org.dyno.visual.swing.layouts;
/*    */ 
/*    */ public class Trailing extends Alignment
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private int trailing;
/*    */   private int size;
/*    */ 
/*    */   public Trailing(int trailing, int size, Spring spring)
/*    */   {
/* 28 */     super(spring);
/* 29 */     this.trailing = trailing;
/* 30 */     this.size = size;
/*    */   }
/*    */   public Trailing(int trailing, int min, int pref) {
/* 33 */     this(trailing, -1, min, pref);
/*    */   }
/*    */   public Trailing(int trailing, int size, int min, int pref) {
/* 36 */     super(min, pref);
/* 37 */     this.trailing = trailing;
/* 38 */     this.size = size;
/*    */   }
/*    */   public int getTrailing() {
/* 41 */     return this.trailing;
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
/*    */   public void setTrailing(int trailing) {
/* 53 */     this.trailing = trailing;
/*    */   }
/*    */ 
/*    */   public Object clone() {
/* 57 */     return new Trailing(this.trailing, this.size, (Spring)getSpring().clone());
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.dyno.visual.swing.layouts.Trailing
 * JD-Core Version:    0.6.0
 */