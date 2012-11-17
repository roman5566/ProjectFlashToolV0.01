/*    */ package org.apache.axis.configuration;
/*    */ 
/*    */ import org.apache.axis.Handler;
/*    */ import org.apache.axis.SimpleTargetedChain;
/*    */ import org.apache.axis.transport.local.LocalResponder;
/*    */ import org.apache.axis.transport.local.LocalSender;
/*    */ 
/*    */ public class BasicServerConfig extends SimpleProvider
/*    */ {
/*    */   public BasicServerConfig()
/*    */   {
/* 34 */     Handler h = new LocalResponder();
/* 35 */     SimpleTargetedChain transport = new SimpleTargetedChain(null, null, h);
/* 36 */     deployTransport("local", transport);
/* 37 */     deployTransport("java", new SimpleTargetedChain(new LocalSender()));
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.configuration.BasicServerConfig
 * JD-Core Version:    0.6.0
 */