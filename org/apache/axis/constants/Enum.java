/*     */ package org.apache.axis.constants;
/*     */ 
/*     */ import java.io.ObjectStreamException;
/*     */ import java.io.Serializable;
/*     */ import java.util.Hashtable;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public abstract class Enum
/*     */   implements Serializable
/*     */ {
/*  33 */   private static final Hashtable types = new Hashtable(13);
/*     */ 
/*  35 */   protected static Log log = LogFactory.getLog(Enum.class.getName());
/*     */   private final Type type;
/*     */   public final int value;
/*     */   public final String name;
/*     */ 
/*     */   protected Enum(Type type, int value, String name)
/*     */   {
/*  43 */     this.type = type;
/*  44 */     this.value = value;
/*  45 */     this.name = name.intern();
/*     */   }
/*     */   public final int getValue() {
/*  48 */     return this.value; } 
/*  49 */   public final String getName() { return this.name; } 
/*  50 */   public final Type getType() { return this.type; }
/*     */ 
/*     */   public String toString() {
/*  53 */     return this.name;
/*     */   }
/*     */ 
/*     */   public final boolean equals(Object obj) {
/*  57 */     return (obj != null) && ((obj instanceof Enum)) ? _equals((Enum)obj) : false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  63 */     return this.value;
/*     */   }
/*     */ 
/*     */   public final boolean equals(Enum obj) {
/*  67 */     return obj != null ? _equals(obj) : false;
/*     */   }
/*     */ 
/*     */   private final boolean _equals(Enum obj)
/*     */   {
/*  77 */     return (obj.type == this.type) && (obj.value == this.value);
/*     */   }
/*     */ 
/*     */   public static abstract class Type
/*     */     implements Serializable
/*     */   {
/*     */     private final String name;
/*     */     private final Enum[] enums;
/*  85 */     private Enum dephault = null;
/*     */ 
/*     */     protected Type(String name, Enum[] enums) {
/*  88 */       this.name = name.intern();
/*  89 */       this.enums = enums;
/*  90 */       synchronized (Enum.types) {
/*  91 */         Enum.types.put(name, this);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void setDefault(Enum dephault) {
/*  96 */       this.dephault = dephault;
/*     */     }
/*     */ 
/*     */     public Enum getDefault() {
/* 100 */       return this.dephault;
/*     */     }
/*     */ 
/*     */     public final String getName() {
/* 104 */       return this.name;
/*     */     }
/*     */ 
/*     */     public final boolean isValid(String enumName) {
/* 108 */       for (int enumElt = 0; enumElt < this.enums.length; enumElt++) {
/* 109 */         if (this.enums[enumElt].getName().equalsIgnoreCase(enumName)) {
/* 110 */           return true;
/*     */         }
/*     */       }
/* 113 */       return false;
/*     */     }
/*     */ 
/*     */     public final int size() {
/* 117 */       return this.enums.length;
/*     */     }
/*     */ 
/*     */     public final String[] getEnumNames()
/*     */     {
/* 124 */       String[] nms = new String[size()];
/*     */ 
/* 126 */       for (int idx = 0; idx < this.enums.length; idx++) {
/* 127 */         nms[idx] = this.enums[idx].getName();
/*     */       }
/* 129 */       return nms;
/*     */     }
/*     */ 
/*     */     public final Enum getEnum(int enumElt)
/*     */     {
/* 136 */       return (enumElt >= 0) && (enumElt < this.enums.length) ? this.enums[enumElt] : null;
/*     */     }
/*     */ 
/*     */     public final Enum getEnum(String enumName)
/*     */     {
/* 143 */       Enum e = getEnum(enumName, null);
/*     */ 
/* 145 */       if (e == null) {
/* 146 */         Enum.log.error(Messages.getMessage("badEnum02", this.name, enumName));
/*     */       }
/*     */ 
/* 149 */       return e;
/*     */     }
/*     */ 
/*     */     public final Enum getEnum(String enumName, Enum dephault)
/*     */     {
/* 160 */       if ((enumName != null) && (enumName.length() > 0)) {
/* 161 */         for (int enumElt = 0; enumElt < this.enums.length; enumElt++) {
/* 162 */           Enum e = this.enums[enumElt];
/* 163 */           if (e.getName().equalsIgnoreCase(enumName)) {
/* 164 */             return e;
/*     */           }
/*     */         }
/*     */       }
/* 168 */       return dephault;
/*     */     }
/*     */ 
/*     */     private Object readResolve() throws ObjectStreamException {
/* 172 */       Object type = Enum.types.get(this.name);
/* 173 */       if (type == null) {
/* 174 */         type = this;
/* 175 */         Enum.types.put(this.name, type);
/*     */       }
/* 177 */       return type;
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.constants.Enum
 * JD-Core Version:    0.6.0
 */