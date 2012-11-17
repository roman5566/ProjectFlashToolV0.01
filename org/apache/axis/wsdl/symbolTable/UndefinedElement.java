/*    */ package org.apache.axis.wsdl.symbolTable;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.xml.namespace.QName;
/*    */ 
/*    */ public class UndefinedElement extends Element
/*    */   implements Undefined
/*    */ {
/* 28 */   private UndefinedDelegate delegate = null;
/*    */ 
/*    */   public UndefinedElement(QName pqName)
/*    */   {
/* 37 */     super(pqName, null);
/*    */ 
/* 39 */     this.undefined = true;
/* 40 */     this.delegate = new UndefinedDelegate(this);
/*    */   }
/*    */ 
/*    */   public void register(TypeEntry referrant)
/*    */   {
/* 50 */     this.delegate.register(referrant);
/*    */   }
/*    */ 
/*    */   public void update(TypeEntry def)
/*    */     throws IOException
/*    */   {
/* 61 */     this.delegate.update(def);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.UndefinedElement
 * JD-Core Version:    0.6.0
 */