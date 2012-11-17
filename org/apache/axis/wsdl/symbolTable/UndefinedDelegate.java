/*    */ package org.apache.axis.wsdl.symbolTable;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.Vector;
/*    */ 
/*    */ public class UndefinedDelegate
/*    */   implements Undefined
/*    */ {
/*    */   private Vector list;
/*    */   private TypeEntry undefinedType;
/*    */ 
/*    */   UndefinedDelegate(TypeEntry te)
/*    */   {
/* 38 */     this.list = new Vector();
/* 39 */     this.undefinedType = te;
/*    */   }
/*    */ 
/*    */   public void register(TypeEntry referrant)
/*    */   {
/* 49 */     this.list.add(referrant);
/*    */   }
/*    */ 
/*    */   public void update(TypeEntry def)
/*    */     throws IOException
/*    */   {
/* 61 */     boolean done = false;
/*    */ 
/* 63 */     while (!done) {
/* 64 */       done = true;
/*    */ 
/* 70 */       for (int i = 0; i < this.list.size(); i++) {
/* 71 */         TypeEntry te = (TypeEntry)this.list.elementAt(i);
/*    */ 
/* 73 */         if (te.updateUndefined(this.undefinedType, def)) {
/* 74 */           done = false;
/*    */         }
/*    */ 
/*    */       }
/*    */ 
/*    */     }
/*    */ 
/* 81 */     TypeEntry uType = def.getUndefinedTypeRef();
/*    */ 
/* 83 */     if (uType != null)
/* 84 */       for (int i = 0; i < this.list.size(); i++) {
/* 85 */         TypeEntry te = (TypeEntry)this.list.elementAt(i);
/*    */ 
/* 87 */         ((Undefined)uType).register(te);
/*    */       }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.UndefinedDelegate
 * JD-Core Version:    0.6.0
 */