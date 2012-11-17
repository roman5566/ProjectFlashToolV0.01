/*    */ package org.apache.axis.wsdl.symbolTable;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import org.w3c.dom.Node;
/*    */ 
/*    */ public class DefinedType extends Type
/*    */ {
/*    */   protected TypeEntry extensionBase;
/* 34 */   protected boolean searchedForExtensionBase = false;
/*    */ 
/*    */   public DefinedType(QName pqName, Node pNode)
/*    */   {
/* 43 */     super(pqName, pNode);
/*    */   }
/*    */ 
/*    */   public DefinedType(QName pqName, TypeEntry refType, Node pNode, String dims)
/*    */   {
/* 56 */     super(pqName, refType, pNode, dims);
/*    */   }
/*    */ 
/*    */   public TypeEntry getComplexTypeExtensionBase(SymbolTable symbolTable)
/*    */   {
/* 67 */     if (!this.searchedForExtensionBase) {
/* 68 */       if (null == this.extensionBase) {
/* 69 */         this.extensionBase = SchemaUtils.getComplexElementExtensionBase(getNode(), symbolTable);
/*    */       }
/*    */ 
/* 73 */       this.searchedForExtensionBase = true;
/*    */     }
/*    */ 
/* 76 */     return this.extensionBase;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.DefinedType
 * JD-Core Version:    0.6.0
 */