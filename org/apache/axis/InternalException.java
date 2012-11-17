/*    */ package org.apache.axis;
/*    */ 
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class InternalException extends RuntimeException
/*    */ {
/* 36 */   protected static Log log = LogFactory.getLog(InternalException.class.getName());
/*    */ 
/* 45 */   private static boolean shouldLog = true;
/*    */ 
/*    */   public static void setLogging(boolean logging)
/*    */   {
/* 53 */     shouldLog = logging;
/*    */   }
/*    */ 
/*    */   public static boolean getLogging()
/*    */   {
/* 62 */     return shouldLog;
/*    */   }
/*    */ 
/*    */   public InternalException(String message)
/*    */   {
/* 71 */     this(new Exception(message));
/*    */   }
/*    */ 
/*    */   public InternalException(Exception e)
/*    */   {
/* 80 */     super(e.toString());
/*    */ 
/* 82 */     if (shouldLog)
/*    */     {
/* 85 */       if ((e instanceof InternalException))
/* 86 */         log.debug("InternalException: ", e);
/*    */       else
/* 88 */         log.fatal(Messages.getMessage("exception00"), e);
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.InternalException
 * JD-Core Version:    0.6.0
 */