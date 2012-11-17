/*    */ package org.apache.axis.attachments;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.axis.AxisFault;
/*    */ import org.apache.axis.utils.Messages;
/*    */ 
/*    */ public final class DimeAttachmentStreams extends IncomingAttachmentStreams
/*    */ {
/* 24 */   private DimeDelimitedInputStream _delimitedStream = null;
/*    */ 
/*    */   public DimeAttachmentStreams(DimeDelimitedInputStream stream)
/*    */     throws AxisFault
/*    */   {
/* 29 */     if (stream == null)
/*    */     {
/* 31 */       throw new AxisFault(Messages.getMessage("nullDelimitedStream"));
/*    */     }
/* 33 */     this._delimitedStream = stream;
/*    */   }
/*    */ 
/*    */   public IncomingAttachmentStreams.IncomingAttachmentInputStream getNextStream()
/*    */     throws AxisFault
/*    */   {
/* 41 */     IncomingAttachmentStreams.IncomingAttachmentInputStream stream = null;
/*    */ 
/* 43 */     if (!isReadyToGetNextStream())
/*    */     {
/* 45 */       throw new IllegalStateException(Messages.getMessage("nextStreamNotReady"));
/*    */     }
/*    */     try
/*    */     {
/* 49 */       this._delimitedStream = this._delimitedStream.getNextStream();
/* 50 */       if (this._delimitedStream == null)
/*    */       {
/* 52 */         return null;
/*    */       }
/* 54 */       stream = new IncomingAttachmentStreams.IncomingAttachmentInputStream(this, this._delimitedStream);
/*    */     }
/*    */     catch (IOException e)
/*    */     {
/* 58 */       throw new AxisFault(Messages.getMessage("failedToGetDelimitedAttachmentStream"), e);
/*    */     }
/*    */ 
/* 61 */     String value = this._delimitedStream.getContentId();
/* 62 */     if ((value != null) && (value.length() > 0))
/*    */     {
/* 64 */       stream.addHeader("Content-Id", value);
/*    */     }
/* 66 */     value = this._delimitedStream.getType();
/* 67 */     if ((value != null) && (value.length() > 0))
/*    */     {
/* 69 */       stream.addHeader("Content-Type", value);
/*    */     }
/* 71 */     setReadyToGetNextStream(false);
/* 72 */     return stream;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.attachments.DimeAttachmentStreams
 * JD-Core Version:    0.6.0
 */