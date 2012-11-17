/*    */ package org.apache.axis.encoding;
/*    */ 
/*    */ import java.lang.reflect.Constructor;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.apache.axis.i18n.Messages;
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public class ConstructorTarget
/*    */   implements Target
/*    */ {
/* 35 */   private Constructor constructor = null;
/*    */ 
/* 40 */   private Deserializer deSerializer = null;
/*    */ 
/* 46 */   private List values = null;
/*    */ 
/*    */   public ConstructorTarget(Constructor constructor, Deserializer deSerializer) {
/* 49 */     this.deSerializer = deSerializer;
/* 50 */     this.constructor = constructor;
/* 51 */     this.values = new ArrayList();
/*    */   }
/*    */ 
/*    */   public void set(Object value)
/*    */     throws SAXException
/*    */   {
/*    */     try
/*    */     {
/* 63 */       this.values.add(value);
/*    */ 
/* 66 */       if (this.constructor.getParameterTypes().length == this.values.size())
/*    */       {
/* 68 */         Class[] classes = this.constructor.getParameterTypes();
/*    */ 
/* 71 */         Object[] args = new Object[this.constructor.getParameterTypes().length];
/*    */ 
/* 74 */         for (int c = 0; c < classes.length; c++) {
/* 75 */           boolean found = false;
/* 76 */           int i = 0;
/* 77 */           while ((!found) && (i < this.values.size()))
/*    */           {
/* 79 */             if (this.values.get(i).getClass().getName().toLowerCase().indexOf(classes[c].getName().toLowerCase()) != -1) {
/* 80 */               found = true;
/* 81 */               args[c] = this.values.get(i);
/*    */             }
/* 83 */             i++;
/*    */           }
/*    */ 
/* 87 */           if (!found) {
/* 88 */             throw new SAXException(Messages.getMessage("cannotFindObjectForClass00", classes[c].toString()));
/*    */           }
/*    */ 
/*    */         }
/*    */ 
/* 93 */         Object o = this.constructor.newInstance(args);
/* 94 */         this.deSerializer.setValue(o);
/*    */       }
/*    */     } catch (Exception e) {
/* 97 */       throw new SAXException(e);
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ConstructorTarget
 * JD-Core Version:    0.6.0
 */