/*    */ package org.apache.axis.wsdl.toJava;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class DuplicateFileException extends IOException
/*    */ {
/* 28 */   private String filename = null;
/*    */ 
/*    */   public DuplicateFileException(String message, String filename)
/*    */   {
/* 38 */     super(message);
/*    */ 
/* 40 */     this.filename = filename;
/*    */   }
/*    */ 
/*    */   public String getFileName()
/*    */   {
/* 49 */     return this.filename;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.DuplicateFileException
 * JD-Core Version:    0.6.0
 */