/*    */ package org.apache.axis.wsdl.symbolTable;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import org.w3c.dom.Node;
/*    */ 
/*    */ public abstract class Element extends TypeEntry
/*    */ {
/*    */   protected Element(QName pqName, TypeEntry refType, Node pNode, String dims)
/*    */   {
/* 40 */     super(pqName, refType, pNode, dims);
/*    */   }
/*    */ 
/*    */   protected Element(QName pqName, Node pNode)
/*    */   {
/* 50 */     super(pqName, pNode);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.Element
 * JD-Core Version:    0.6.0
 */