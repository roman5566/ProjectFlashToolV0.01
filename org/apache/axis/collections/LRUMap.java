/*     */ package org.apache.axis.collections;
/*     */ 
/*     */ import java.io.Externalizable;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInput;
/*     */ import java.io.ObjectOutput;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class LRUMap extends SequencedHashMap
/*     */   implements Externalizable
/*     */ {
/*  52 */   private int maximumSize = 0;
/*     */   private static final long serialVersionUID = 2197433140769957051L;
/*     */ 
/*     */   public LRUMap()
/*     */   {
/*  61 */     this(100);
/*     */   }
/*     */ 
/*     */   public LRUMap(int i)
/*     */   {
/*  72 */     super(i);
/*  73 */     this.maximumSize = i;
/*     */   }
/*     */ 
/*     */   public Object get(Object key)
/*     */   {
/*  89 */     if (!containsKey(key)) return null;
/*     */ 
/*  91 */     Object value = remove(key);
/*  92 */     super.put(key, value);
/*  93 */     return value;
/*     */   }
/*     */ 
/*     */   public Object put(Object key, Object value)
/*     */   {
/* 110 */     int mapSize = size();
/* 111 */     Object retval = null;
/*     */ 
/* 113 */     if (mapSize >= this.maximumSize)
/*     */     {
/* 117 */       if (!containsKey(key))
/*     */       {
/* 119 */         removeLRU();
/*     */       }
/*     */     }
/*     */ 
/* 123 */     retval = super.put(key, value);
/*     */ 
/* 125 */     return retval;
/*     */   }
/*     */ 
/*     */   protected void removeLRU()
/*     */   {
/* 133 */     Object key = getFirstKey();
/*     */ 
/* 136 */     Object value = super.get(key);
/*     */ 
/* 138 */     remove(key);
/*     */ 
/* 140 */     processRemovedLRU(key, value);
/*     */   }
/*     */ 
/*     */   protected void processRemovedLRU(Object key, Object value)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void readExternal(ObjectInput in)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 158 */     this.maximumSize = in.readInt();
/* 159 */     int size = in.readInt();
/*     */ 
/* 161 */     for (int i = 0; i < size; i++) {
/* 162 */       Object key = in.readObject();
/* 163 */       Object value = in.readObject();
/* 164 */       put(key, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeExternal(ObjectOutput out) throws IOException {
/* 169 */     out.writeInt(this.maximumSize);
/* 170 */     out.writeInt(size());
/* 171 */     for (Iterator iterator = keySet().iterator(); iterator.hasNext(); ) {
/* 172 */       Object key = iterator.next();
/* 173 */       out.writeObject(key);
/*     */ 
/* 176 */       Object value = super.get(key);
/* 177 */       out.writeObject(value);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getMaximumSize()
/*     */   {
/* 188 */     return this.maximumSize;
/*     */   }
/*     */ 
/*     */   public void setMaximumSize(int maximumSize)
/*     */   {
/* 194 */     this.maximumSize = maximumSize;
/* 195 */     while (size() > maximumSize)
/* 196 */       removeLRU();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.collections.LRUMap
 * JD-Core Version:    0.6.0
 */