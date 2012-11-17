/*    */ package org.apache.axis.wsdl.symbolTable;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import org.w3c.dom.Node;
/*    */ 
/*    */ public class CollectionType extends DefinedType
/*    */   implements CollectionTE
/*    */ {
/*    */   private boolean wrapped;
/*    */ 
/*    */   public CollectionType(QName pqName, TypeEntry refType, Node pNode, String dims, boolean wrapped)
/*    */   {
/* 45 */     super(pqName, refType, pNode, dims);
/* 46 */     this.wrapped = wrapped;
/*    */   }
/*    */ 
/*    */   public boolean isWrapped() {
/* 50 */     return this.wrapped;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.CollectionType
 * JD-Core Version:    0.6.0
 */