/*    */ package org.apache.axis.wsdl.symbolTable;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import org.w3c.dom.Node;
/*    */ 
/*    */ public abstract class Type extends TypeEntry
/*    */ {
/*    */   private boolean generated;
/*    */ 
/*    */   protected Type(QName pqName)
/*    */   {
/* 36 */     super(pqName);
/*    */   }
/*    */ 
/*    */   protected Type(QName pqName, TypeEntry refType, Node pNode, String dims)
/*    */   {
/* 49 */     super(pqName, refType, pNode, dims);
/*    */   }
/*    */ 
/*    */   protected Type(QName pqName, Node pNode)
/*    */   {
/* 59 */     super(pqName, pNode);
/*    */   }
/*    */   public void setGenerated(boolean b) {
/* 62 */     this.generated = b;
/*    */   }
/*    */ 
/*    */   public boolean isGenerated() {
/* 66 */     return this.generated;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.Type
 * JD-Core Version:    0.6.0
 */