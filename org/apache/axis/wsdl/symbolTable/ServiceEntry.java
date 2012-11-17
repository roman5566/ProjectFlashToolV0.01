/*    */ package org.apache.axis.wsdl.symbolTable;
/*    */ 
/*    */ import javax.wsdl.Service;
/*    */ 
/*    */ public class ServiceEntry extends SymTabEntry
/*    */ {
/*    */   private Service service;
/*    */   private String originalServiceName;
/*    */ 
/*    */   public ServiceEntry(Service service)
/*    */   {
/* 37 */     super(service.getQName());
/*    */ 
/* 39 */     this.service = service;
/*    */   }
/*    */ 
/*    */   public String getOriginalServiceName()
/*    */   {
/* 48 */     return this.originalServiceName;
/*    */   }
/*    */   public void setOriginalServiceName(String originalName) {
/* 51 */     this.originalServiceName = originalName;
/*    */   }
/*    */ 
/*    */   public Service getService()
/*    */   {
/* 60 */     return this.service;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.ServiceEntry
 * JD-Core Version:    0.6.0
 */