/*    */ package org.apache.axis.utils;
/*    */ 
/*    */ public class IDKey
/*    */ {
/* 23 */   private Object value = null;
/* 24 */   private int id = 0;
/*    */ 
/*    */   public IDKey(Object _value)
/*    */   {
/* 32 */     this.id = System.identityHashCode(_value);
/*    */ 
/* 36 */     this.value = _value;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 44 */     return this.id;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object other)
/*    */   {
/* 53 */     if (!(other instanceof IDKey)) {
/* 54 */       return false;
/*    */     }
/* 56 */     IDKey idKey = (IDKey)other;
/* 57 */     if (this.id != idKey.id) {
/* 58 */       return false;
/*    */     }
/*    */ 
/* 61 */     return this.value == idKey.value;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.IDKey
 * JD-Core Version:    0.6.0
 */