/*    */ package org.apache.axis.wsdl.toJava;
/*    */ 
/*    */ public class FactoryProperty
/*    */ {
/*    */   private String name_;
/*    */   private String value_;
/*    */ 
/*    */   public String getName()
/*    */   {
/* 32 */     return this.name_;
/*    */   }
/*    */ 
/*    */   public String getValue() {
/* 36 */     return this.value_;
/*    */   }
/*    */ 
/*    */   public void setName(String string) {
/* 40 */     this.name_ = string;
/*    */   }
/*    */ 
/*    */   public void setValue(String string) {
/* 44 */     this.value_ = string;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 48 */     return this.name_ + "=" + this.value_;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object rhs) {
/* 52 */     if (rhs == null) {
/* 53 */       return false;
/*    */     }
/* 55 */     if ((rhs instanceof String)) {
/* 56 */       return ((String)rhs).equals(this.name_);
/*    */     }
/* 58 */     if ((rhs instanceof FactoryProperty)) {
/* 59 */       return ((FactoryProperty)rhs).equals(this.name_);
/*    */     }
/*    */ 
/* 62 */     return false;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.FactoryProperty
 * JD-Core Version:    0.6.0
 */