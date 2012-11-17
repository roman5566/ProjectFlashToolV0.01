/*    */ package org.dyno.visual.swing.layouts;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class Spring
/*    */   implements Serializable, Cloneable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private int minimum;
/*    */   private int preferred;
/*    */ 
/*    */   public Spring(int min, int pref)
/*    */   {
/* 31 */     this.minimum = min;
/* 32 */     this.preferred = pref;
/*    */   }
/*    */ 
/*    */   public Object clone()
/*    */   {
/* 37 */     return new Spring(this.minimum, this.preferred);
/*    */   }
/*    */ 
/*    */   public int getMinimum() {
/* 41 */     return this.minimum;
/*    */   }
/*    */ 
/*    */   public void setMinimum(int minimum) {
/* 45 */     this.minimum = minimum;
/*    */   }
/*    */ 
/*    */   public int getPreferred() {
/* 49 */     return this.preferred;
/*    */   }
/*    */ 
/*    */   public void setPreferred(int preferred) {
/* 53 */     this.preferred = preferred;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.dyno.visual.swing.layouts.Spring
 * JD-Core Version:    0.6.0
 */