/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ 
/*     */ public class FieldPropertyDescriptor extends BeanPropertyDescriptor
/*     */ {
/*  30 */   private Field field = null;
/*     */ 
/*     */   public FieldPropertyDescriptor(String _name, Field _field)
/*     */   {
/*  40 */     this.field = _field;
/*     */     try {
/*  42 */       this.myPD = new PropertyDescriptor(_name, null, null);
/*     */     }
/*     */     catch (Exception e) {
/*     */     }
/*  46 */     if ((_field == null) || (_name == null))
/*  47 */       throw new IllegalArgumentException(Messages.getMessage(_field == null ? "badField00" : "badProp03"));
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  54 */     return this.field.getName();
/*     */   }
/*     */ 
/*     */   public boolean isReadable()
/*     */   {
/*  62 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isWriteable()
/*     */   {
/*  70 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isIndexed()
/*     */   {
/*  79 */     return this.field.getType().getComponentType() != null;
/*     */   }
/*     */ 
/*     */   public Object get(Object obj)
/*     */     throws InvocationTargetException, IllegalAccessException
/*     */   {
/*  89 */     return this.field.get(obj);
/*     */   }
/*     */ 
/*     */   public void set(Object obj, Object newValue)
/*     */     throws InvocationTargetException, IllegalAccessException
/*     */   {
/*  99 */     this.field.set(obj, newValue);
/*     */   }
/*     */ 
/*     */   public Object get(Object obj, int i)
/*     */     throws InvocationTargetException, IllegalAccessException
/*     */   {
/* 110 */     if (!isIndexed()) {
/* 111 */       throw new IllegalAccessException("Not an indexed property");
/*     */     }
/*     */ 
/* 114 */     Object array = this.field.get(obj);
/* 115 */     return Array.get(array, i);
/*     */   }
/*     */ 
/*     */   public void set(Object obj, int i, Object newValue)
/*     */     throws InvocationTargetException, IllegalAccessException
/*     */   {
/* 126 */     if (!isIndexed()) {
/* 127 */       throw new IllegalAccessException("Not an indexed field!");
/*     */     }
/* 129 */     Class componentType = this.field.getType().getComponentType();
/* 130 */     growArrayToSize(obj, componentType, i);
/* 131 */     Array.set(get(obj), i, newValue);
/*     */   }
/*     */ 
/*     */   public Class getType()
/*     */   {
/* 139 */     if (isIndexed()) {
/* 140 */       return this.field.getType().getComponentType();
/*     */     }
/* 142 */     return this.field.getType();
/*     */   }
/*     */ 
/*     */   public Class getActualType()
/*     */   {
/* 147 */     return this.field.getType();
/*     */   }
/*     */ 
/*     */   public Field getField() {
/* 151 */     return this.field;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.FieldPropertyDescriptor
 * JD-Core Version:    0.6.0
 */