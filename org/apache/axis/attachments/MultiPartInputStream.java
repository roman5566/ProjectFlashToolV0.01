/*    */ package org.apache.axis.attachments;
/*    */ 
/*    */ import java.io.FilterInputStream;
/*    */ import java.io.InputStream;
/*    */ import java.util.Collection;
/*    */ import org.apache.axis.AxisFault;
/*    */ import org.apache.axis.Part;
/*    */ 
/*    */ public abstract class MultiPartInputStream extends FilterInputStream
/*    */ {
/*    */   MultiPartInputStream(InputStream is)
/*    */   {
/* 32 */     super(is);
/*    */   }
/*    */ 
/*    */   public abstract Part getAttachmentByReference(String[] paramArrayOfString)
/*    */     throws AxisFault;
/*    */ 
/*    */   public abstract Collection getAttachments()
/*    */     throws AxisFault;
/*    */ 
/*    */   public abstract String getContentLocation();
/*    */ 
/*    */   public abstract String getContentId();
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.attachments.MultiPartInputStream
 * JD-Core Version:    0.6.0
 */