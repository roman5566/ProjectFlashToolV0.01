/*     */ package org.apache.axis.collections;
/*     */ 
/*     */ import java.io.Externalizable;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInput;
/*     */ import java.io.ObjectOutput;
/*     */ import java.util.AbstractCollection;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.ConcurrentModificationException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import org.apache.axis.i18n.Messages;
/*     */ 
/*     */ public class SequencedHashMap
/*     */   implements Map, Cloneable, Externalizable
/*     */ {
/*     */   private Entry sentinel;
/*     */   private HashMap entries;
/* 158 */   private transient long modCount = 0L;
/*     */   private static final int KEY = 0;
/*     */   private static final int VALUE = 1;
/*     */   private static final int ENTRY = 2;
/*     */   private static final int REMOVED_MASK = -2147483648;
/*     */   private static final long serialVersionUID = 3380552487888102930L;
/*     */ 
/*     */   private static final Entry createSentinel()
/*     */   {
/* 136 */     Entry s = new Entry(null, null);
/* 137 */     s.prev = s;
/* 138 */     s.next = s;
/* 139 */     return s;
/*     */   }
/*     */ 
/*     */   public SequencedHashMap()
/*     */   {
/* 165 */     this.sentinel = createSentinel();
/* 166 */     this.entries = new HashMap();
/*     */   }
/*     */ 
/*     */   public SequencedHashMap(int initialSize)
/*     */   {
/* 178 */     this.sentinel = createSentinel();
/* 179 */     this.entries = new HashMap(initialSize);
/*     */   }
/*     */ 
/*     */   public SequencedHashMap(int initialSize, float loadFactor)
/*     */   {
/* 193 */     this.sentinel = createSentinel();
/* 194 */     this.entries = new HashMap(initialSize, loadFactor);
/*     */   }
/*     */ 
/*     */   public SequencedHashMap(Map m)
/*     */   {
/* 203 */     this();
/* 204 */     putAll(m);
/*     */   }
/*     */ 
/*     */   private void removeEntry(Entry entry)
/*     */   {
/* 212 */     entry.next.prev = entry.prev;
/* 213 */     entry.prev.next = entry.next;
/*     */   }
/*     */ 
/*     */   private void insertEntry(Entry entry)
/*     */   {
/* 221 */     entry.next = this.sentinel;
/* 222 */     entry.prev = this.sentinel.prev;
/* 223 */     this.sentinel.prev.next = entry;
/* 224 */     this.sentinel.prev = entry;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 234 */     return this.entries.size();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 243 */     return this.sentinel.next == this.sentinel;
/*     */   }
/*     */ 
/*     */   public boolean containsKey(Object key)
/*     */   {
/* 251 */     return this.entries.containsKey(key);
/*     */   }
/*     */ 
/*     */   public boolean containsValue(Object value)
/*     */   {
/* 266 */     if (value == null) {
/* 267 */       for (Entry pos = this.sentinel.next; pos != this.sentinel; pos = pos.next)
/* 268 */         if (pos.getValue() == null)
/* 269 */           return true;
/*     */     }
/*     */     else {
/* 272 */       for (Entry pos = this.sentinel.next; pos != this.sentinel; pos = pos.next) {
/* 273 */         if (value.equals(pos.getValue()))
/* 274 */           return true;
/*     */       }
/*     */     }
/* 277 */     return false;
/*     */   }
/*     */ 
/*     */   public Object get(Object o)
/*     */   {
/* 285 */     Entry entry = (Entry)this.entries.get(o);
/* 286 */     if (entry == null) {
/* 287 */       return null;
/*     */     }
/* 289 */     return entry.getValue();
/*     */   }
/*     */ 
/*     */   public Map.Entry getFirst()
/*     */   {
/* 306 */     return isEmpty() ? null : this.sentinel.next;
/*     */   }
/*     */ 
/*     */   public Object getFirstKey()
/*     */   {
/* 326 */     return this.sentinel.next.getKey();
/*     */   }
/*     */ 
/*     */   public Object getFirstValue()
/*     */   {
/* 346 */     return this.sentinel.next.getValue();
/*     */   }
/*     */ 
/*     */   public Map.Entry getLast()
/*     */   {
/* 373 */     return isEmpty() ? null : this.sentinel.prev;
/*     */   }
/*     */ 
/*     */   public Object getLastKey()
/*     */   {
/* 393 */     return this.sentinel.prev.getKey();
/*     */   }
/*     */ 
/*     */   public Object getLastValue()
/*     */   {
/* 413 */     return this.sentinel.prev.getValue();
/*     */   }
/*     */ 
/*     */   public Object put(Object key, Object value)
/*     */   {
/* 420 */     this.modCount += 1L;
/*     */ 
/* 422 */     Object oldValue = null;
/*     */ 
/* 425 */     Entry e = (Entry)this.entries.get(key);
/*     */ 
/* 428 */     if (e != null)
/*     */     {
/* 430 */       removeEntry(e);
/*     */ 
/* 433 */       oldValue = e.setValue(value);
/*     */     }
/*     */     else
/*     */     {
/* 442 */       e = new Entry(key, value);
/* 443 */       this.entries.put(key, e);
/*     */     }
/*     */ 
/* 448 */     insertEntry(e);
/*     */ 
/* 450 */     return oldValue;
/*     */   }
/*     */ 
/*     */   public Object remove(Object key)
/*     */   {
/* 457 */     Entry e = removeImpl(key);
/* 458 */     return e == null ? null : e.getValue();
/*     */   }
/*     */ 
/*     */   private Entry removeImpl(Object key)
/*     */   {
/* 466 */     Entry e = (Entry)this.entries.remove(key);
/* 467 */     if (e == null)
/* 468 */       return null;
/* 469 */     this.modCount += 1L;
/* 470 */     removeEntry(e);
/* 471 */     return e;
/*     */   }
/*     */ 
/*     */   public void putAll(Map t)
/*     */   {
/* 485 */     Iterator iter = t.entrySet().iterator();
/* 486 */     while (iter.hasNext()) {
/* 487 */       Map.Entry entry = (Map.Entry)iter.next();
/* 488 */       put(entry.getKey(), entry.getValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 496 */     this.modCount += 1L;
/*     */ 
/* 499 */     this.entries.clear();
/*     */ 
/* 502 */     this.sentinel.next = this.sentinel;
/* 503 */     this.sentinel.prev = this.sentinel;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 510 */     if (obj == null)
/* 511 */       return false;
/* 512 */     if (obj == this) {
/* 513 */       return true;
/*     */     }
/* 515 */     if (!(obj instanceof Externalizable)) {
/* 516 */       return false;
/*     */     }
/* 518 */     return entrySet().equals(((Externalizable)obj).entrySet());
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 525 */     return entrySet().hashCode();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 536 */     StringBuffer buf = new StringBuffer();
/* 537 */     buf.append('[');
/* 538 */     for (Entry pos = this.sentinel.next; pos != this.sentinel; pos = pos.next) {
/* 539 */       buf.append(pos.getKey());
/* 540 */       buf.append('=');
/* 541 */       buf.append(pos.getValue());
/* 542 */       if (pos.next != this.sentinel) {
/* 543 */         buf.append(',');
/*     */       }
/*     */     }
/* 546 */     buf.append(']');
/*     */ 
/* 548 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public Set keySet()
/*     */   {
/* 555 */     return new AbstractSet()
/*     */     {
/*     */       public Iterator iterator()
/*     */       {
/* 559 */         return new SequencedHashMap.OrderedIterator(SequencedHashMap.this, 0);
/*     */       }
/*     */       public boolean remove(Object o) {
/* 562 */         SequencedHashMap.Entry e = SequencedHashMap.this.removeImpl(o);
/* 563 */         return e != null;
/*     */       }
/*     */ 
/*     */       public void clear()
/*     */       {
/* 568 */         SequencedHashMap.this.clear();
/*     */       }
/*     */       public int size() {
/* 571 */         return SequencedHashMap.this.size();
/*     */       }
/*     */       public boolean isEmpty() {
/* 574 */         return SequencedHashMap.this.isEmpty();
/*     */       }
/*     */       public boolean contains(Object o) {
/* 577 */         return SequencedHashMap.this.containsKey(o);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public Collection values()
/*     */   {
/* 587 */     return new AbstractCollection()
/*     */     {
/*     */       public Iterator iterator() {
/* 590 */         return new SequencedHashMap.OrderedIterator(SequencedHashMap.this, 1);
/*     */       }
/*     */ 
/*     */       public boolean remove(Object value)
/*     */       {
/* 596 */         if (value == null) {
/* 597 */           for (SequencedHashMap.Entry pos = SequencedHashMap.this.sentinel.next; pos != SequencedHashMap.this.sentinel; pos = pos.next)
/* 598 */             if (pos.getValue() == null) {
/* 599 */               SequencedHashMap.this.removeImpl(pos.getKey());
/* 600 */               return true;
/*     */             }
/*     */         }
/*     */         else {
/* 604 */           for (SequencedHashMap.Entry pos = SequencedHashMap.this.sentinel.next; pos != SequencedHashMap.this.sentinel; pos = pos.next) {
/* 605 */             if (value.equals(pos.getValue())) {
/* 606 */               SequencedHashMap.this.removeImpl(pos.getKey());
/* 607 */               return true;
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/* 612 */         return false;
/*     */       }
/*     */ 
/*     */       public void clear()
/*     */       {
/* 617 */         SequencedHashMap.this.clear();
/*     */       }
/*     */       public int size() {
/* 620 */         return SequencedHashMap.this.size();
/*     */       }
/*     */       public boolean isEmpty() {
/* 623 */         return SequencedHashMap.this.isEmpty();
/*     */       }
/*     */       public boolean contains(Object o) {
/* 626 */         return SequencedHashMap.this.containsValue(o);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public Set entrySet()
/*     */   {
/* 635 */     return new AbstractSet()
/*     */     {
/*     */       private SequencedHashMap.Entry findEntry(Object o) {
/* 638 */         if (o == null)
/* 639 */           return null;
/* 640 */         if (!(o instanceof Map.Entry)) {
/* 641 */           return null;
/*     */         }
/* 643 */         Map.Entry e = (Map.Entry)o;
/* 644 */         SequencedHashMap.Entry entry = (SequencedHashMap.Entry)SequencedHashMap.this.entries.get(e.getKey());
/* 645 */         if ((entry != null) && (entry.equals(e))) {
/* 646 */           return entry;
/*     */         }
/* 648 */         return null;
/*     */       }
/*     */ 
/*     */       public Iterator iterator()
/*     */       {
/* 653 */         return new SequencedHashMap.OrderedIterator(SequencedHashMap.this, 2);
/*     */       }
/*     */       public boolean remove(Object o) {
/* 656 */         SequencedHashMap.Entry e = findEntry(o);
/* 657 */         if (e == null) {
/* 658 */           return false;
/*     */         }
/* 660 */         return SequencedHashMap.this.removeImpl(e.getKey()) != null;
/*     */       }
/*     */ 
/*     */       public void clear()
/*     */       {
/* 665 */         SequencedHashMap.this.clear();
/*     */       }
/*     */       public int size() {
/* 668 */         return SequencedHashMap.this.size();
/*     */       }
/*     */       public boolean isEmpty() {
/* 671 */         return SequencedHashMap.this.isEmpty();
/*     */       }
/*     */       public boolean contains(Object o) {
/* 674 */         return findEntry(o) != null;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */     throws CloneNotSupportedException
/*     */   {
/* 817 */     SequencedHashMap map = (SequencedHashMap)super.clone();
/*     */ 
/* 820 */     map.sentinel = createSentinel();
/*     */ 
/* 824 */     map.entries = new HashMap();
/*     */ 
/* 827 */     map.putAll(this);
/*     */ 
/* 837 */     return map;
/*     */   }
/*     */ 
/*     */   private Map.Entry getEntry(int index)
/*     */   {
/* 847 */     Entry pos = this.sentinel;
/*     */ 
/* 849 */     if (index < 0) {
/* 850 */       throw new ArrayIndexOutOfBoundsException(Messages.getMessage("seqHashMapArrayIndexOutOfBoundsException01", new Integer(index).toString()));
/*     */     }
/*     */ 
/* 854 */     int i = -1;
/* 855 */     while ((i < index - 1) && (pos.next != this.sentinel)) {
/* 856 */       i++;
/* 857 */       pos = pos.next;
/*     */     }
/*     */ 
/* 862 */     if (pos.next == this.sentinel) {
/* 863 */       throw new ArrayIndexOutOfBoundsException(Messages.getMessage("seqHashMapArrayIndexOutOfBoundsException02", new Integer(index).toString(), new Integer(i + 1).toString()));
/*     */     }
/*     */ 
/* 868 */     return pos.next;
/*     */   }
/*     */ 
/*     */   public Object get(int index)
/*     */   {
/* 880 */     return getEntry(index).getKey();
/*     */   }
/*     */ 
/*     */   public Object getValue(int index)
/*     */   {
/* 892 */     return getEntry(index).getValue();
/*     */   }
/*     */ 
/*     */   public int indexOf(Object key)
/*     */   {
/* 902 */     Entry e = (Entry)this.entries.get(key);
/* 903 */     if (e == null) {
/* 904 */       return -1;
/*     */     }
/* 906 */     int pos = 0;
/* 907 */     while (e.prev != this.sentinel) {
/* 908 */       pos++;
/* 909 */       e = e.prev;
/*     */     }
/* 911 */     return pos;
/*     */   }
/*     */ 
/*     */   public Iterator iterator()
/*     */   {
/* 920 */     return keySet().iterator();
/*     */   }
/*     */ 
/*     */   public int lastIndexOf(Object key)
/*     */   {
/* 931 */     return indexOf(key);
/*     */   }
/*     */ 
/*     */   public List sequence()
/*     */   {
/* 949 */     List l = new ArrayList(size());
/* 950 */     Iterator iter = keySet().iterator();
/* 951 */     while (iter.hasNext()) {
/* 952 */       l.add(iter.next());
/*     */     }
/*     */ 
/* 955 */     return Collections.unmodifiableList(l);
/*     */   }
/*     */ 
/*     */   public Object remove(int index)
/*     */   {
/* 969 */     return remove(get(index));
/*     */   }
/*     */ 
/*     */   public void readExternal(ObjectInput in)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 982 */     int size = in.readInt();
/* 983 */     for (int i = 0; i < size; i++) {
/* 984 */       Object key = in.readObject();
/* 985 */       Object value = in.readObject();
/* 986 */       put(key, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeExternal(ObjectOutput out)
/*     */     throws IOException
/*     */   {
/* 997 */     out.writeInt(size());
/* 998 */     for (Entry pos = this.sentinel.next; pos != this.sentinel; pos = pos.next) {
/* 999 */       out.writeObject(pos.getKey());
/* 1000 */       out.writeObject(pos.getValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   private class OrderedIterator
/*     */     implements Iterator
/*     */   {
/*     */     private int returnType;
/* 701 */     private SequencedHashMap.Entry pos = SequencedHashMap.this.sentinel;
/*     */ 
/* 708 */     private transient long expectedModCount = SequencedHashMap.this.modCount;
/*     */ 
/*     */     public OrderedIterator(int returnType)
/*     */     {
/* 719 */       this.returnType = (returnType | 0x80000000);
/*     */     }
/*     */ 
/*     */     public boolean hasNext()
/*     */     {
/* 730 */       return this.pos.next != SequencedHashMap.this.sentinel;
/*     */     }
/*     */ 
/*     */     public Object next()
/*     */     {
/* 745 */       if (SequencedHashMap.this.modCount != this.expectedModCount) {
/* 746 */         throw new ConcurrentModificationException(Messages.getMessage("seqHashMapConcurrentModificationException00"));
/*     */       }
/* 748 */       if (this.pos.next == SequencedHashMap.this.sentinel) {
/* 749 */         throw new NoSuchElementException(Messages.getMessage("seqHashMapNoSuchElementException00"));
/*     */       }
/*     */ 
/* 753 */       this.returnType &= 2147483647;
/*     */ 
/* 755 */       this.pos = this.pos.next;
/* 756 */       switch (this.returnType) {
/*     */       case 0:
/* 758 */         return this.pos.getKey();
/*     */       case 1:
/* 760 */         return this.pos.getValue();
/*     */       case 2:
/* 762 */         return this.pos;
/*     */       }
/*     */ 
/* 765 */       throw new Error(Messages.getMessage("seqHashMapBadIteratorType01", new Integer(this.returnType).toString()));
/*     */     }
/*     */ 
/*     */     public void remove()
/*     */     {
/* 782 */       if ((this.returnType & 0x80000000) != 0) {
/* 783 */         throw new IllegalStateException(Messages.getMessage("seqHashMapIllegalStateException00"));
/*     */       }
/* 785 */       if (SequencedHashMap.this.modCount != this.expectedModCount) {
/* 786 */         throw new ConcurrentModificationException(Messages.getMessage("seqHashMapConcurrentModificationException00"));
/*     */       }
/*     */ 
/* 789 */       SequencedHashMap.this.removeImpl(this.pos.getKey());
/*     */ 
/* 792 */       this.expectedModCount += 1L;
/*     */ 
/* 795 */       this.returnType |= -2147483648;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class Entry
/*     */     implements Map.Entry
/*     */   {
/*     */     private final Object key;
/*     */     private Object value;
/*  80 */     Entry next = null;
/*  81 */     Entry prev = null;
/*     */ 
/*     */     public Entry(Object key, Object value) {
/*  84 */       this.key = key;
/*  85 */       this.value = value;
/*     */     }
/*     */ 
/*     */     public Object getKey()
/*     */     {
/*  90 */       return this.key;
/*     */     }
/*     */ 
/*     */     public Object getValue()
/*     */     {
/*  95 */       return this.value;
/*     */     }
/*     */ 
/*     */     public Object setValue(Object value)
/*     */     {
/* 100 */       Object oldValue = this.value;
/* 101 */       this.value = value;
/* 102 */       return oldValue;
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 107 */       return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() == null ? 0 : getValue().hashCode());
/*     */     }
/*     */ 
/*     */     public boolean equals(Object obj) {
/* 111 */       if (obj == null)
/* 112 */         return false;
/* 113 */       if (obj == this)
/* 114 */         return true;
/* 115 */       if (!(obj instanceof Map.Entry)) {
/* 116 */         return false;
/*     */       }
/* 118 */       Map.Entry other = (Map.Entry)obj;
/*     */ 
/* 121 */       return (getKey() == null ? other.getKey() == null : getKey().equals(other.getKey())) && (getValue() == null ? other.getValue() == null : getValue().equals(other.getValue()));
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 126 */       return "[" + getKey() + "=" + getValue() + "]";
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.collections.SequencedHashMap
 * JD-Core Version:    0.6.0
 */