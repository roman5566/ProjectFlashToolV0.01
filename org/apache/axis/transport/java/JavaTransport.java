/*    */ package org.apache.axis.transport.java;
/*    */ 
/*    */ import org.apache.axis.AxisEngine;
/*    */ import org.apache.axis.MessageContext;
/*    */ import org.apache.axis.client.Call;
/*    */ import org.apache.axis.client.Transport;
/*    */ 
/*    */ public class JavaTransport extends Transport
/*    */ {
/*    */   public JavaTransport()
/*    */   {
/* 33 */     this.transportName = "java";
/*    */   }
/*    */ 
/*    */   public void setupMessageContextImpl(MessageContext mc, Call call, AxisEngine engine)
/*    */   {
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.java.JavaTransport
 * JD-Core Version:    0.6.0
 */