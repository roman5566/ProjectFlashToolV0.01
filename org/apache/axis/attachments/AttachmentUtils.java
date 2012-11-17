/*    */ package org.apache.axis.attachments;
/*    */ 
/*    */ import javax.activation.DataHandler;
/*    */ import org.apache.axis.AxisFault;
/*    */ import org.apache.axis.Part;
/*    */ import org.apache.axis.utils.Messages;
/*    */ 
/*    */ public class AttachmentUtils
/*    */ {
/*    */   public static DataHandler getActivationDataHandler(Part part)
/*    */     throws AxisFault
/*    */   {
/* 45 */     if (null == part) {
/* 46 */       throw new AxisFault(Messages.getMessage("gotNullPart"));
/*    */     }
/*    */ 
/* 49 */     if (!(part instanceof AttachmentPart)) {
/* 50 */       throw new AxisFault(Messages.getMessage("unsupportedAttach", part.getClass().getName(), AttachmentPart.class.getName()));
/*    */     }
/*    */ 
/* 56 */     return ((AttachmentPart)part).getActivationDataHandler();
/*    */   }
/*    */ 
/*    */   public static boolean isAttachment(Object value)
/*    */   {
/* 69 */     if (null == value) {
/* 70 */       return false;
/*    */     }
/*    */ 
/* 73 */     return value instanceof DataHandler;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.attachments.AttachmentUtils
 * JD-Core Version:    0.6.0
 */