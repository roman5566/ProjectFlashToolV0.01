/*    */ package org.apache.axis.configuration;
/*    */ 
/*    */ import org.apache.axis.SimpleTargetedChain;
/*    */ import org.apache.axis.transport.http.HTTPSender;
/*    */ import org.apache.axis.transport.java.JavaSender;
/*    */ import org.apache.axis.transport.local.LocalSender;
/*    */ 
/*    */ public class BasicClientConfig extends SimpleProvider
/*    */ {
/*    */   public BasicClientConfig()
/*    */   {
/* 34 */     deployTransport("java", new SimpleTargetedChain(new JavaSender()));
/* 35 */     deployTransport("local", new SimpleTargetedChain(new LocalSender()));
/* 36 */     deployTransport("http", new SimpleTargetedChain(new HTTPSender()));
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.configuration.BasicClientConfig
 * JD-Core Version:    0.6.0
 */