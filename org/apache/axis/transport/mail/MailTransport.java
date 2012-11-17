/*    */ package org.apache.axis.transport.mail;
/*    */ 
/*    */ import org.apache.axis.AxisEngine;
/*    */ import org.apache.axis.MessageContext;
/*    */ import org.apache.axis.client.Call;
/*    */ import org.apache.axis.client.Transport;
/*    */ 
/*    */ public class MailTransport extends Transport
/*    */ {
/*    */   public MailTransport()
/*    */   {
/* 31 */     this.transportName = "mail";
/*    */   }
/*    */ 
/*    */   public void setupMessageContextImpl(MessageContext mc, Call call, AxisEngine engine)
/*    */   {
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.mail.MailTransport
 * JD-Core Version:    0.6.0
 */