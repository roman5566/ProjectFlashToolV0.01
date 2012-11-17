/*    */ package org.apache.axis.wsdl.symbolTable;
/*    */ 
/*    */ import javax.wsdl.Message;
/*    */ 
/*    */ public class MessageEntry extends SymTabEntry
/*    */ {
/*    */   private Message message;
/*    */ 
/*    */   public MessageEntry(Message message)
/*    */   {
/* 36 */     super(message.getQName());
/*    */ 
/* 38 */     this.message = message;
/*    */   }
/*    */ 
/*    */   public Message getMessage()
/*    */   {
/* 47 */     return this.message;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.MessageEntry
 * JD-Core Version:    0.6.0
 */