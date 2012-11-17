/*    */ package org.apache.axis.wsdl.symbolTable;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import org.w3c.dom.Node;
/*    */ 
/*    */ public class DefinedElement extends Element
/*    */ {
/*    */   public DefinedElement(QName pqName, TypeEntry refType, Node pNode, String dims)
/*    */   {
/* 40 */     super(pqName, refType, pNode, dims);
/*    */   }
/*    */ 
/*    */   public DefinedElement(QName pqName, Node pNode)
/*    */   {
/* 50 */     super(pqName, pNode);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.DefinedElement
 * JD-Core Version:    0.6.0
 */