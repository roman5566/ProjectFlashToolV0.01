/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class LockableHashtable extends Hashtable
/*     */ {
/*     */   Vector lockedEntries;
/*  47 */   private Hashtable parent = null;
/*     */ 
/*     */   public LockableHashtable()
/*     */   {
/*     */   }
/*     */ 
/*     */   public LockableHashtable(int p1, float p2) {
/*  54 */     super(p1, p2);
/*     */   }
/*     */ 
/*     */   public LockableHashtable(Map p1) {
/*  58 */     super(p1);
/*     */   }
/*     */ 
/*     */   public LockableHashtable(int p1) {
/*  62 */     super(p1);
/*     */   }
/*     */ 
/*     */   public synchronized void setParent(Hashtable parent)
/*     */   {
/*  70 */     this.parent = parent;
/*     */   }
/*     */ 
/*     */   public synchronized Hashtable getParent()
/*     */   {
/*  77 */     return this.parent;
/*     */   }
/*     */ 
/*     */   public Set getAllKeys()
/*     */   {
/*  84 */     HashSet set = new HashSet();
/*  85 */     set.addAll(super.keySet());
/*  86 */     Hashtable p = this.parent;
/*  87 */     while (p != null) {
/*  88 */       set.addAll(p.keySet());
/*  89 */       if ((p instanceof LockableHashtable)) {
/*  90 */         p = ((LockableHashtable)p).getParent(); continue;
/*     */       }
/*  92 */       p = null;
/*     */     }
/*     */ 
/*  95 */     return set;
/*     */   }
/*     */ 
/*     */   public synchronized Object get(Object key)
/*     */   {
/* 103 */     Object ret = super.get(key);
/* 104 */     if ((ret == null) && (this.parent != null)) {
/* 105 */       ret = this.parent.get(key);
/*     */     }
/* 107 */     return ret;
/*     */   }
/*     */ 
/*     */   public synchronized Object put(Object p1, Object p2, boolean locked)
/*     */   {
/* 114 */     if ((this.lockedEntries != null) && (containsKey(p1)) && (this.lockedEntries.contains(p1)))
/*     */     {
/* 117 */       return null;
/*     */     }
/* 119 */     if (locked) {
/* 120 */       if (this.lockedEntries == null) {
/* 121 */         this.lockedEntries = new Vector();
/*     */       }
/* 123 */       this.lockedEntries.add(p1);
/*     */     }
/* 125 */     return super.put(p1, p2);
/*     */   }
/*     */ 
/*     */   public synchronized Object put(Object p1, Object p2)
/*     */   {
/* 132 */     return put(p1, p2, false);
/*     */   }
/*     */ 
/*     */   public synchronized Object remove(Object p1)
/*     */   {
/* 139 */     if ((this.lockedEntries != null) && (this.lockedEntries.contains(p1))) {
/* 140 */       return null;
/*     */     }
/* 142 */     return super.remove(p1);
/*     */   }
/*     */ 
/*     */   public boolean isKeyLocked(Object key)
/*     */   {
/* 150 */     return (this.lockedEntries != null) && (this.lockedEntries.contains(key));
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.LockableHashtable
 * JD-Core Version:    0.6.0
 */