/*    */ package org.dyno.visual.swing.layouts;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class Constraints
/*    */   implements Serializable, Cloneable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private Alignment horizontal;
/*    */   private Alignment vertical;
/*    */ 
/*    */   public Constraints(Alignment h, Alignment v)
/*    */   {
/* 31 */     this.horizontal = h;
/* 32 */     this.vertical = v;
/*    */   }
/*    */ 
/*    */   public Alignment getHorizontal() {
/* 36 */     return this.horizontal;
/*    */   }
/*    */ 
/*    */   public Object clone()
/*    */   {
/* 41 */     return new Constraints((Alignment)(this.horizontal == null ? null : this.horizontal.clone()), (Alignment)(this.vertical == null ? null : this.vertical.clone()));
/*    */   }
/*    */ 
/*    */   public Alignment getVertical() {
/* 45 */     return this.vertical;
/*    */   }
/*    */ 
/*    */   public void setHorizontal(Alignment horizontal) {
/* 49 */     this.horizontal = horizontal;
/*    */   }
/*    */ 
/*    */   public void setVertical(Alignment vertical) {
/* 53 */     this.vertical = vertical;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.dyno.visual.swing.layouts.Constraints
 * JD-Core Version:    0.6.0
 */