/*    */ package org.apache.axis.utils;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class IdentityHashMap extends HashMap
/*    */ {
/*    */   public IdentityHashMap(int initialCapacity, float loadFactor)
/*    */   {
/* 26 */     super(initialCapacity, loadFactor);
/*    */   }
/*    */ 
/*    */   public IdentityHashMap(int initialCapacity)
/*    */   {
/* 35 */     super(initialCapacity);
/*    */   }
/*    */ 
/*    */   public IdentityHashMap()
/*    */   {
/*    */   }
/*    */ 
/*    */   public IdentityHashMap(Map t)
/*    */   {
/* 52 */     super(t);
/*    */   }
/*    */ 
/*    */   public Object get(Object key)
/*    */   {
/* 60 */     return super.get(new IDKey(key));
/*    */   }
/*    */ 
/*    */   public Object put(Object key, Object value)
/*    */   {
/* 68 */     return super.put(new IDKey(key), value);
/*    */   }
/*    */ 
/*    */   public Object add(Object value)
/*    */   {
/* 76 */     Object key = new IDKey(value);
/* 77 */     if (!super.containsKey(key))
/*    */     {
/* 79 */       return super.put(key, value);
/*    */     }
/* 81 */     return null;
/*    */   }
/*    */ 
/*    */   public Object remove(Object key)
/*    */   {
/* 90 */     return super.remove(new IDKey(key));
/*    */   }
/*    */ 
/*    */   public boolean containsKey(Object key)
/*    */   {
/* 98 */     return super.containsKey(new IDKey(key));
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.IdentityHashMap
 * JD-Core Version:    0.6.0
 */