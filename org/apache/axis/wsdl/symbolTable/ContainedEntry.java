/*    */ package org.apache.axis.wsdl.symbolTable;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ 
/*    */ public class ContainedEntry extends SymTabEntry
/*    */ {
/*    */   protected TypeEntry type;
/*    */ 
/*    */   protected ContainedEntry(TypeEntry type, QName qname)
/*    */   {
/* 12 */     super(qname);
/* 13 */     this.type = type;
/*    */   }
/*    */ 
/*    */   public TypeEntry getType()
/*    */   {
/* 20 */     return this.type;
/*    */   }
/*    */ 
/*    */   public void setType(TypeEntry type)
/*    */   {
/* 26 */     this.type = type;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.ContainedEntry
 * JD-Core Version:    0.6.0
 */