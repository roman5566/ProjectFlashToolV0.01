/*    */ package org.apache.axis.encoding;
/*    */ 
/*    */ import java.lang.reflect.Field;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public class FieldTarget
/*    */   implements Target
/*    */ {
/* 30 */   protected static Log log = LogFactory.getLog(FieldTarget.class.getName());
/*    */   private Object targetObject;
/*    */   private Field targetField;
/*    */ 
/*    */   public FieldTarget(Object targetObject, Field targetField)
/*    */   {
/* 38 */     this.targetObject = targetObject;
/* 39 */     this.targetField = targetField;
/*    */   }
/*    */ 
/*    */   public FieldTarget(Object targetObject, String fieldName)
/*    */     throws NoSuchFieldException
/*    */   {
/* 45 */     Class cls = targetObject.getClass();
/* 46 */     this.targetField = cls.getField(fieldName);
/* 47 */     this.targetObject = targetObject;
/*    */   }
/*    */ 
/*    */   public void set(Object value) throws SAXException {
/*    */     try {
/* 52 */       this.targetField.set(this.targetObject, value);
/*    */     } catch (IllegalAccessException accEx) {
/* 54 */       log.error(Messages.getMessage("illegalAccessException00"), accEx);
/*    */ 
/* 56 */       throw new SAXException(accEx);
/*    */     } catch (IllegalArgumentException argEx) {
/* 58 */       log.error(Messages.getMessage("illegalArgumentException00"), argEx);
/*    */ 
/* 60 */       throw new SAXException(argEx);
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.FieldTarget
 * JD-Core Version:    0.6.0
 */