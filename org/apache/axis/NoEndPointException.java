/*    */ package org.apache.axis;
/*    */ 
/*    */ import org.apache.axis.utils.Messages;
/*    */ 
/*    */ public class NoEndPointException extends AxisFault
/*    */ {
/*    */   public NoEndPointException()
/*    */   {
/* 32 */     super("Server.NoEndpoint", Messages.getMessage("noEndpoint"), null, null);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.NoEndPointException
 * JD-Core Version:    0.6.0
 */