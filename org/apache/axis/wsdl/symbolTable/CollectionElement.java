/*    */ package org.apache.axis.wsdl.symbolTable;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import org.w3c.dom.Node;
/*    */ 
/*    */ public class CollectionElement extends DefinedElement
/*    */   implements CollectionTE
/*    */ {
/*    */   public CollectionElement(QName pqName, TypeEntry refType, Node pNode, String dims)
/*    */   {
/* 42 */     super(pqName, refType, pNode, dims);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.CollectionElement
 * JD-Core Version:    0.6.0
 */