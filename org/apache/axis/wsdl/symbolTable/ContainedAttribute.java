/*    */ package org.apache.axis.wsdl.symbolTable;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ 
/*    */ public class ContainedAttribute extends ContainedEntry
/*    */ {
/* 24 */   private boolean optional = false;
/*    */ 
/*    */   protected ContainedAttribute(TypeEntry type, QName qname)
/*    */   {
/* 30 */     super(type, qname);
/*    */   }
/*    */ 
/*    */   public void setOptional(boolean optional)
/*    */   {
/* 39 */     this.optional = optional;
/*    */   }
/*    */ 
/*    */   public boolean getOptional()
/*    */   {
/* 48 */     return this.optional;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.ContainedAttribute
 * JD-Core Version:    0.6.0
 */