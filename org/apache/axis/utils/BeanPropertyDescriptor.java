/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import java.beans.IndexedPropertyDescriptor;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class BeanPropertyDescriptor
/*     */ {
/*  46 */   protected static Log log = LogFactory.getLog(BeanPropertyDescriptor.class.getName());
/*     */ 
/*  49 */   protected PropertyDescriptor myPD = null;
/*     */ 
/*  51 */   protected static final Object[] noArgs = new Object[0];
/*     */ 
/*     */   public BeanPropertyDescriptor(PropertyDescriptor pd)
/*     */   {
/*  59 */     this.myPD = pd;
/*     */   }
/*     */ 
/*     */   protected BeanPropertyDescriptor()
/*     */   {
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  72 */     return this.myPD.getName();
/*     */   }
/*     */ 
/*     */   public boolean isReadable()
/*     */   {
/*  80 */     return this.myPD.getReadMethod() != null;
/*     */   }
/*     */ 
/*     */   public boolean isWriteable()
/*     */   {
/*  88 */     return this.myPD.getWriteMethod() != null;
/*     */   }
/*     */ 
/*     */   public boolean isIndexed()
/*     */   {
/*  97 */     return this.myPD instanceof IndexedPropertyDescriptor;
/*     */   }
/*     */ 
/*     */   public boolean isIndexedOrArray()
/*     */   {
/* 106 */     return (isIndexed()) || (isArray());
/*     */   }
/*     */ 
/*     */   public boolean isArray()
/*     */   {
/* 114 */     return (this.myPD.getPropertyType() != null) && (this.myPD.getPropertyType().isArray());
/*     */   }
/*     */ 
/*     */   public Object get(Object obj)
/*     */     throws InvocationTargetException, IllegalAccessException
/*     */   {
/* 125 */     Method readMethod = this.myPD.getReadMethod();
/* 126 */     if (readMethod != null) {
/* 127 */       return readMethod.invoke(obj, noArgs);
/*     */     }
/* 129 */     throw new IllegalAccessException(Messages.getMessage("badGetter00"));
/*     */   }
/*     */ 
/*     */   public void set(Object obj, Object newValue)
/*     */     throws InvocationTargetException, IllegalAccessException
/*     */   {
/* 140 */     Method writeMethod = this.myPD.getWriteMethod();
/* 141 */     if (writeMethod != null)
/* 142 */       writeMethod.invoke(obj, new Object[] { newValue });
/*     */     else
/* 144 */       throw new IllegalAccessException(Messages.getMessage("badSetter00"));
/*     */   }
/*     */ 
/*     */   public Object get(Object obj, int i)
/*     */     throws InvocationTargetException, IllegalAccessException
/*     */   {
/* 156 */     if (!isIndexed()) {
/* 157 */       return Array.get(get(obj), i);
/*     */     }
/* 159 */     IndexedPropertyDescriptor id = (IndexedPropertyDescriptor)this.myPD;
/* 160 */     return id.getIndexedReadMethod().invoke(obj, new Object[] { new Integer(i) });
/*     */   }
/*     */ 
/*     */   public void set(Object obj, int i, Object newValue)
/*     */     throws InvocationTargetException, IllegalAccessException
/*     */   {
/* 175 */     if (isIndexed()) {
/* 176 */       IndexedPropertyDescriptor id = (IndexedPropertyDescriptor)this.myPD;
/* 177 */       growArrayToSize(obj, id.getIndexedPropertyType(), i);
/* 178 */       id.getIndexedWriteMethod().invoke(obj, new Object[] { new Integer(i), newValue });
/*     */     }
/*     */     else
/*     */     {
/* 185 */       Object array = get(obj);
/* 186 */       if ((array == null) || (Array.getLength(array) <= i)) {
/* 187 */         Class componentType = getType().getComponentType();
/* 188 */         Object newArray = Array.newInstance(componentType, i + 1);
/*     */ 
/* 190 */         if (array != null) {
/* 191 */           System.arraycopy(array, 0, newArray, 0, Array.getLength(array));
/*     */         }
/* 193 */         array = newArray;
/*     */       }
/* 195 */       Array.set(array, i, newValue);
/*     */ 
/* 198 */       set(obj, array);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void growArrayToSize(Object obj, Class componentType, int i)
/*     */     throws InvocationTargetException, IllegalAccessException
/*     */   {
/* 212 */     Object array = get(obj);
/* 213 */     if ((array == null) || (Array.getLength(array) <= i))
/*     */     {
/* 215 */       Object newArray = Array.newInstance(componentType, i + 1);
/*     */ 
/* 217 */       if (array != null) {
/* 218 */         System.arraycopy(array, 0, newArray, 0, Array.getLength(array));
/*     */       }
/*     */ 
/* 221 */       set(obj, newArray);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Class getType()
/*     */   {
/* 230 */     if (isIndexed()) {
/* 231 */       return ((IndexedPropertyDescriptor)this.myPD).getIndexedPropertyType();
/*     */     }
/* 233 */     return this.myPD.getPropertyType();
/*     */   }
/*     */ 
/*     */   public Class getActualType()
/*     */   {
/* 238 */     return this.myPD.getPropertyType();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.BeanPropertyDescriptor
 * JD-Core Version:    0.6.0
 */