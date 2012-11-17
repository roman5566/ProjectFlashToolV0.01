/*    */ package org.apache.axis.components.uuid;
/*    */ 
/*    */ import org.apache.axis.AxisProperties;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public abstract class UUIDGenFactory
/*    */ {
/* 41 */   protected static Log log = LogFactory.getLog(UUIDGenFactory.class.getName());
/*    */ 
/*    */   public static UUIDGen getUUIDGen()
/*    */   {
/* 52 */     UUIDGen uuidgen = (UUIDGen)AxisProperties.newInstance(UUIDGen.class);
/* 53 */     log.debug("axis.UUIDGenerator:" + uuidgen.getClass().getName());
/* 54 */     return uuidgen;
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 44 */     AxisProperties.setClassOverrideProperty(UUIDGen.class, "axis.UUIDGenerator");
/* 45 */     AxisProperties.setClassDefault(UUIDGen.class, "org.apache.axis.components.uuid.FastUUIDGen");
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.uuid.UUIDGenFactory
 * JD-Core Version:    0.6.0
 */