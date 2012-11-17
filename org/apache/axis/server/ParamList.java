/*    */ package org.apache.axis.server;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import java.util.Vector;
/*    */ 
/*    */ public class ParamList extends Vector
/*    */ {
/*    */   public ParamList()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ParamList(Collection c)
/*    */   {
/* 29 */     super(c);
/*    */   }
/*    */ 
/*    */   public ParamList(int initialCapacity) {
/* 33 */     super(initialCapacity);
/*    */   }
/*    */ 
/*    */   public ParamList(int initialCapacity, int capacityIncrement) {
/* 37 */     super(initialCapacity, capacityIncrement);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.server.ParamList
 * JD-Core Version:    0.6.0
 */