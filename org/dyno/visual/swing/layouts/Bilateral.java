/*    */ package org.dyno.visual.swing.layouts;
/*    */ 
/*    */ public class Bilateral extends Alignment
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private int leading;
/*    */   private int trailing;
/*    */ 
/*    */   public Bilateral(int leading, int trailing, Spring spring)
/*    */   {
/* 28 */     super(spring);
/* 29 */     this.leading = leading;
/* 30 */     this.trailing = trailing;
/*    */   }
/*    */   public Bilateral(int leading, int trailing, int min) {
/* 33 */     this(leading, trailing, min, -1);
/*    */   }
/*    */   public Bilateral(int leading, int trailing, int min, int pref) {
/* 36 */     super(min, pref);
/* 37 */     this.leading = leading;
/* 38 */     this.trailing = trailing;
/*    */   }
/*    */   public int getLeading() {
/* 41 */     return this.leading;
/*    */   }
/*    */ 
/*    */   public int getTrailing() {
/* 45 */     return this.trailing;
/*    */   }
/*    */ 
/*    */   public void setLeading(int leading) {
/* 49 */     this.leading = leading;
/*    */   }
/*    */ 
/*    */   public void setTrailing(int trailing) {
/* 53 */     this.trailing = trailing;
/*    */   }
/*    */ 
/*    */   public Object clone() {
/* 57 */     return new Bilateral(this.leading, this.trailing, (Spring)getSpring().clone());
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.dyno.visual.swing.layouts.Bilateral
 * JD-Core Version:    0.6.0
 */