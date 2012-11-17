/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.lang.reflect.Array;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.encoding.Target;
/*    */ import org.apache.axis.utils.BeanPropertyDescriptor;
/*    */ import org.apache.axis.utils.JavaUtils;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public class BeanPropertyTarget
/*    */   implements Target
/*    */ {
/* 34 */   protected static Log log = LogFactory.getLog(BeanPropertyTarget.class.getName());
/*    */   private Object object;
/*    */   private BeanPropertyDescriptor pd;
/* 39 */   private int index = -1;
/*    */ 
/*    */   public BeanPropertyTarget(Object object, BeanPropertyDescriptor pd)
/*    */   {
/* 47 */     this.object = object;
/* 48 */     this.pd = pd;
/* 49 */     this.index = -1;
/*    */   }
/*    */ 
/*    */   public BeanPropertyTarget(Object object, BeanPropertyDescriptor pd, int i)
/*    */   {
/* 59 */     this.object = object;
/* 60 */     this.pd = pd;
/* 61 */     this.index = i;
/*    */   }
/*    */ 
/*    */   public void set(Object value)
/*    */     throws SAXException
/*    */   {
/*    */     try
/*    */     {
/* 74 */       if (this.index < 0)
/* 75 */         this.pd.set(this.object, value);
/*    */       else {
/* 77 */         this.pd.set(this.object, this.index, value);
/*    */       }
/*    */ 
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/*    */       try
/*    */       {
/* 85 */         Class type = this.pd.getType();
/*    */ 
/* 88 */         if ((value.getClass().isArray()) && (value.getClass().getComponentType().isPrimitive()) && (type.isArray()) && (type.getComponentType().equals(Object.class)))
/*    */         {
/* 94 */           type = Array.newInstance(JavaUtils.getWrapperClass(value.getClass().getComponentType()), 0).getClass();
/*    */         }
/*    */ 
/* 97 */         if (JavaUtils.isConvertable(value, type)) {
/* 98 */           value = JavaUtils.convert(value, type);
/* 99 */           if (this.index < 0)
/* 100 */             this.pd.set(this.object, value);
/*    */           else {
/* 102 */             this.pd.set(this.object, this.index, value);
/*    */           }
/*    */ 
/*    */         }
/* 109 */         else if ((this.index == 0) && (value.getClass().isArray()) && (!type.getClass().isArray()))
/*    */         {
/* 112 */           for (int i = 0; i < Array.getLength(value); i++) {
/* 113 */             Object item = JavaUtils.convert(Array.get(value, i), type);
/*    */ 
/* 115 */             this.pd.set(this.object, i, item);
/*    */           }
/*    */         }
/*    */         else
/*    */         {
/* 120 */           throw e;
/*    */         }
/*    */ 
/*    */       }
/*    */       catch (Exception ex)
/*    */       {
/* 126 */         String field = this.pd.getName();
/* 127 */         if (this.index >= 0) {
/* 128 */           field = field + "[" + this.index + "]";
/*    */         }
/* 130 */         if (log.isErrorEnabled())
/*    */         {
/* 132 */           String valueType = "null";
/* 133 */           if (value != null)
/* 134 */             valueType = value.getClass().getName();
/* 135 */           log.error(Messages.getMessage("cantConvert02", new String[] { valueType, field, this.index >= 0 ? this.pd.getType().getComponentType().getName() : this.pd.getType().getName() }));
/*    */         }
/*    */ 
/* 144 */         if ((ex instanceof InvocationTargetException)) {
/* 145 */           Throwable t = ((InvocationTargetException)ex).getTargetException();
/* 146 */           if (t != null) {
/* 147 */             String classname = this.object.getClass().getName();
/*    */ 
/* 149 */             throw new SAXException(Messages.getMessage("cantConvert04", new String[] { classname, field, value == null ? null : value.toString(), t.getMessage() }));
/*    */           }
/*    */ 
/*    */         }
/*    */ 
/* 157 */         throw new SAXException(ex);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.BeanPropertyTarget
 * JD-Core Version:    0.6.0
 */