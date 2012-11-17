/*     */ package org.apache.axis.wsdl.toJava;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ public class GeneratedFileInfo
/*     */ {
/*  31 */   protected ArrayList list = new ArrayList();
/*     */ 
/*     */   public List getList()
/*     */   {
/* 101 */     return this.list;
/*     */   }
/*     */ 
/*     */   public void add(String name, String className, String type)
/*     */   {
/* 112 */     this.list.add(new Entry(name, className, type));
/*     */   }
/*     */ 
/*     */   public List findType(String type)
/*     */   {
/* 128 */     ArrayList ret = null;
/*     */ 
/* 130 */     for (Iterator i = this.list.iterator(); i.hasNext(); ) {
/* 131 */       Entry e = (Entry)i.next();
/*     */ 
/* 133 */       if (e.type.equals(type)) {
/* 134 */         if (ret == null) {
/* 135 */           ret = new ArrayList();
/*     */         }
/*     */ 
/* 138 */         ret.add(e);
/*     */       }
/*     */     }
/*     */ 
/* 142 */     return ret;
/*     */   }
/*     */ 
/*     */   public Entry findName(String fileName)
/*     */   {
/* 155 */     for (Iterator i = this.list.iterator(); i.hasNext(); ) {
/* 156 */       Entry e = (Entry)i.next();
/*     */ 
/* 158 */       if (e.fileName.equals(fileName)) {
/* 159 */         return e;
/*     */       }
/*     */     }
/*     */ 
/* 163 */     return null;
/*     */   }
/*     */ 
/*     */   public Entry findClass(String className)
/*     */   {
/* 176 */     for (Iterator i = this.list.iterator(); i.hasNext(); ) {
/* 177 */       Entry e = (Entry)i.next();
/*     */ 
/* 179 */       if (e.className.equals(className)) {
/* 180 */         return e;
/*     */       }
/*     */     }
/*     */ 
/* 184 */     return null;
/*     */   }
/*     */ 
/*     */   public List getClassNames()
/*     */   {
/* 195 */     ArrayList ret = new ArrayList(this.list.size());
/*     */ 
/* 197 */     for (Iterator i = this.list.iterator(); i.hasNext(); ) {
/* 198 */       Entry e = (Entry)i.next();
/*     */ 
/* 200 */       ret.add(e.className);
/*     */     }
/*     */ 
/* 203 */     return ret;
/*     */   }
/*     */ 
/*     */   public List getFileNames()
/*     */   {
/* 214 */     ArrayList ret = new ArrayList(this.list.size());
/*     */ 
/* 216 */     for (Iterator i = this.list.iterator(); i.hasNext(); ) {
/* 217 */       Entry e = (Entry)i.next();
/*     */ 
/* 219 */       ret.add(e.fileName);
/*     */     }
/*     */ 
/* 222 */     return ret;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 232 */     String s = "";
/*     */ 
/* 234 */     for (Iterator i = this.list.iterator(); i.hasNext(); ) {
/* 235 */       Entry entry = (Entry)i.next();
/*     */ 
/* 237 */       s = s + entry.toString() + "\n";
/*     */     }
/*     */ 
/* 240 */     return s;
/*     */   }
/*     */ 
/*     */   public class Entry
/*     */   {
/*     */     public String fileName;
/*     */     public String className;
/*     */     public String type;
/*     */ 
/*     */     public Entry(String name, String className, String type)
/*     */     {
/*  73 */       this.fileName = name;
/*  74 */       this.className = className;
/*  75 */       this.type = type;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/*  84 */       return "Name: " + this.fileName + " Class: " + this.className + " Type: " + this.type;
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.GeneratedFileInfo
 * JD-Core Version:    0.6.0
 */