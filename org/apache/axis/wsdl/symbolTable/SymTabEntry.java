/*     */ package org.apache.axis.wsdl.symbolTable;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.xml.namespace.QName;
/*     */ 
/*     */ public abstract class SymTabEntry
/*     */ {
/*     */   protected QName qname;
/*     */   protected String name;
/*  46 */   private boolean isReferenced = false;
/*     */ 
/*  49 */   private HashMap dynamicVars = new HashMap();
/*     */ 
/*     */   protected SymTabEntry(QName qname)
/*     */   {
/*  57 */     this.qname = qname;
/*     */   }
/*     */ 
/*     */   public final QName getQName()
/*     */   {
/*  66 */     return this.qname;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  76 */     return this.name;
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/*  86 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public final boolean isReferenced()
/*     */   {
/*  95 */     return this.isReferenced;
/*     */   }
/*     */ 
/*     */   public final void setIsReferenced(boolean isReferenced)
/*     */   {
/* 104 */     this.isReferenced = isReferenced;
/*     */   }
/*     */ 
/*     */   public Object getDynamicVar(Object key)
/*     */   {
/* 120 */     return this.dynamicVars.get(key);
/*     */   }
/*     */ 
/*     */   public void setDynamicVar(Object key, Object value)
/*     */   {
/* 130 */     this.dynamicVars.put(key, value);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 139 */     return toString("");
/*     */   }
/*     */ 
/*     */   protected String toString(String indent)
/*     */   {
/* 150 */     String string = indent + "QName:         " + this.qname + '\n' + indent + "name:          " + this.name + '\n' + indent + "isReferenced?  " + this.isReferenced + '\n';
/*     */ 
/* 153 */     String prefix = indent + "dynamicVars:   ";
/* 154 */     Iterator entries = this.dynamicVars.entrySet().iterator();
/*     */ 
/* 156 */     while (entries.hasNext()) {
/* 157 */       Map.Entry entry = (Map.Entry)entries.next();
/* 158 */       Object key = entry.getKey();
/*     */ 
/* 160 */       string = string + prefix + key + " = " + entry.getValue() + '\n';
/* 161 */       prefix = indent + "               ";
/*     */     }
/*     */ 
/* 164 */     return string;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.SymTabEntry
 * JD-Core Version:    0.6.0
 */