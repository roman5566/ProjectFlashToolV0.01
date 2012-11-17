/*    */ package org.apache.axis.wsdl.symbolTable;
/*    */ 
/*    */ import javax.wsdl.PortType;
/*    */ 
/*    */ public class PortTypeEntry extends SymTabEntry
/*    */ {
/*    */   private PortType portType;
/*    */ 
/*    */   public PortTypeEntry(PortType portType)
/*    */   {
/* 38 */     super(portType.getQName());
/*    */ 
/* 40 */     this.portType = portType;
/*    */   }
/*    */ 
/*    */   public PortType getPortType()
/*    */   {
/* 49 */     return this.portType;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.PortTypeEntry
 * JD-Core Version:    0.6.0
 */