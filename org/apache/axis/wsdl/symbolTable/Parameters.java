/*    */ package org.apache.axis.wsdl.symbolTable;
/*    */ 
/*    */ import java.util.Map;
/*    */ import java.util.Vector;
/*    */ import javax.wsdl.OperationType;
/*    */ 
/*    */ public class Parameters
/*    */ {
/* 27 */   public OperationType mep = OperationType.REQUEST_RESPONSE;
/*    */ 
/* 32 */   public Vector list = new Vector();
/*    */ 
/* 37 */   public Parameter returnParam = null;
/*    */ 
/* 42 */   public Map faults = null;
/*    */ 
/* 47 */   public String signature = null;
/*    */ 
/* 52 */   public int inputs = 0;
/*    */ 
/* 55 */   public int inouts = 0;
/*    */ 
/* 58 */   public int outputs = 0;
/*    */ 
/*    */   public String toString()
/*    */   {
/* 67 */     return "\nreturnParam = " + this.returnParam + "\nfaults = " + this.faults + "\nsignature = " + this.signature + "\n(inputs, inouts, outputs) = (" + this.inputs + ", " + this.inouts + ", " + this.outputs + ")" + "\nlist = " + this.list;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.Parameters
 * JD-Core Version:    0.6.0
 */