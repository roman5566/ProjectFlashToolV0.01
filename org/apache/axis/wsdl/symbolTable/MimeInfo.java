/*    */ package org.apache.axis.wsdl.symbolTable;
/*    */ 
/*    */ public class MimeInfo
/*    */ {
/*    */   String type;
/*    */   String dims;
/*    */ 
/*    */   public MimeInfo(String type, String dims)
/*    */   {
/* 38 */     this.type = type;
/* 39 */     this.dims = dims;
/*    */   }
/*    */ 
/*    */   public String getDimensions()
/*    */   {
/* 48 */     return this.dims;
/*    */   }
/*    */ 
/*    */   public String getType()
/*    */   {
/* 57 */     return this.type;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 66 */     return "(" + this.type + "," + this.dims + ")";
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.MimeInfo
 * JD-Core Version:    0.6.0
 */