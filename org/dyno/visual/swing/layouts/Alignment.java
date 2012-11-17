/*    */ package org.dyno.visual.swing.layouts;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public abstract class Alignment
/*    */   implements Serializable, Cloneable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   public static final int PREFERRED = -1;
/*    */   private Spring spring;
/*    */ 
/*    */   public Alignment(Spring spring)
/*    */   {
/* 31 */     this.spring = spring;
/*    */   }
/*    */ 
/*    */   public Alignment(int min, int pref) {
/* 35 */     this.spring = new Spring(min, pref);
/*    */   }
/*    */ 
/*    */   public Spring getSpring() {
/* 39 */     return this.spring;
/*    */   }
/*    */ 
/*    */   public void setSpring(Spring spring) {
/* 43 */     this.spring = spring;
/*    */   }
/*    */ 
/*    */   public abstract Object clone();
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.dyno.visual.swing.layouts.Alignment
 * JD-Core Version:    0.6.0
 */