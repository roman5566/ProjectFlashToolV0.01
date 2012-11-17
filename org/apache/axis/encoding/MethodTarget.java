/*    */ package org.apache.axis.encoding;
/*    */ 
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public class MethodTarget
/*    */   implements Target
/*    */ {
/* 31 */   protected static Log log = LogFactory.getLog(MethodTarget.class.getName());
/*    */   private Object targetObject;
/*    */   private Method targetMethod;
/* 36 */   private static final Class[] objArg = { Object.class };
/*    */ 
/*    */   public MethodTarget(Object targetObject, Method targetMethod)
/*    */   {
/* 45 */     this.targetObject = targetObject;
/* 46 */     this.targetMethod = targetMethod;
/*    */   }
/*    */ 
/*    */   public MethodTarget(Object targetObject, String methodName)
/*    */     throws NoSuchMethodException
/*    */   {
/* 57 */     this.targetObject = targetObject;
/* 58 */     Class cls = targetObject.getClass();
/* 59 */     this.targetMethod = cls.getMethod(methodName, objArg);
/*    */   }
/*    */ 
/*    */   public void set(Object value)
/*    */     throws SAXException
/*    */   {
/*    */     try
/*    */     {
/* 68 */       this.targetMethod.invoke(this.targetObject, new Object[] { value });
/*    */     } catch (IllegalAccessException accEx) {
/* 70 */       log.error(Messages.getMessage("illegalAccessException00"), accEx);
/*    */ 
/* 72 */       throw new SAXException(accEx);
/*    */     } catch (IllegalArgumentException argEx) {
/* 74 */       log.error(Messages.getMessage("illegalArgumentException00"), argEx);
/*    */ 
/* 76 */       throw new SAXException(argEx);
/*    */     } catch (InvocationTargetException targetEx) {
/* 78 */       log.error(Messages.getMessage("invocationTargetException00"), targetEx);
/*    */ 
/* 80 */       throw new SAXException(targetEx);
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.MethodTarget
 * JD-Core Version:    0.6.0
 */