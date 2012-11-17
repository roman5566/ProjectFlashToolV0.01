/*    */ package org.apache.axis.wsdl.symbolTable;
/*    */ 
/*    */ import javax.wsdl.Port;
/*    */ import javax.xml.namespace.QName;
/*    */ 
/*    */ public class PortEntry extends SymTabEntry
/*    */ {
/* 29 */   private Port port = null;
/*    */ 
/*    */   public PortEntry(Port port)
/*    */   {
/* 38 */     super(new QName(port.getName()));
/*    */ 
/* 40 */     this.port = port;
/*    */   }
/*    */ 
/*    */   public Port getPort()
/*    */   {
/* 49 */     return this.port;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.PortEntry
 * JD-Core Version:    0.6.0
 */