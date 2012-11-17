/*    */ package org.apache.axis.wsdl.toJava;
/*    */ 
/*    */ public class NamespaceSelector
/*    */ {
/* 30 */   private String namespace_ = "";
/*    */ 
/*    */   public NamespaceSelector() {
/*    */   }
/* 34 */   public NamespaceSelector(String namespace) { this.namespace_ = namespace; }
/*    */ 
/*    */   public void setNamespace(String value)
/*    */   {
/* 38 */     this.namespace_ = value;
/*    */   }
/*    */ 
/*    */   public String getNamespace() {
/* 42 */     return this.namespace_;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 46 */     if (this.namespace_ != null) {
/* 47 */       return "namespace=" + this.namespace_;
/*    */     }
/*    */ 
/* 50 */     return "";
/*    */   }
/*    */ 
/*    */   public boolean equals(Object value)
/*    */   {
/* 55 */     boolean isEqual = false;
/* 56 */     if (value == null) {
/* 57 */       isEqual = false;
/*    */     }
/* 59 */     else if ((value instanceof String)) {
/* 60 */       isEqual = ((String)value).equals(this.namespace_);
/*    */     }
/* 62 */     else if ((value instanceof NamespaceSelector)) {
/* 63 */       isEqual = ((NamespaceSelector)value).namespace_.equals(this.namespace_);
/*    */     }
/* 65 */     return isEqual;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.NamespaceSelector
 * JD-Core Version:    0.6.0
 */