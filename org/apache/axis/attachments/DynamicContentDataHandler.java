/*    */ package org.apache.axis.attachments;
/*    */ 
/*    */ import java.net.URL;
/*    */ import javax.activation.DataHandler;
/*    */ import javax.activation.DataSource;
/*    */ 
/*    */ public class DynamicContentDataHandler extends DataHandler
/*    */ {
/* 33 */   int chunkSize = 1048576;
/*    */ 
/*    */   public DynamicContentDataHandler(DataSource arg0)
/*    */   {
/* 39 */     super(arg0);
/*    */   }
/*    */ 
/*    */   public DynamicContentDataHandler(Object arg0, String arg1)
/*    */   {
/* 47 */     super(arg0, arg1);
/*    */   }
/*    */ 
/*    */   public DynamicContentDataHandler(URL arg0)
/*    */   {
/* 54 */     super(arg0);
/*    */   }
/*    */ 
/*    */   public int getChunkSize()
/*    */   {
/* 62 */     return this.chunkSize;
/*    */   }
/*    */ 
/*    */   public void setChunkSize(int chunkSize)
/*    */   {
/* 70 */     this.chunkSize = chunkSize;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.attachments.DynamicContentDataHandler
 * JD-Core Version:    0.6.0
 */