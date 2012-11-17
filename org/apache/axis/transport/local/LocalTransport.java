/*    */ package org.apache.axis.transport.local;
/*    */ 
/*    */ import org.apache.axis.AxisEngine;
/*    */ import org.apache.axis.MessageContext;
/*    */ import org.apache.axis.client.Call;
/*    */ import org.apache.axis.client.Transport;
/*    */ import org.apache.axis.server.AxisServer;
/*    */ 
/*    */ public class LocalTransport extends Transport
/*    */ {
/*    */   public static final String LOCAL_SERVER = "LocalTransport.AxisServer";
/*    */   public static final String REMOTE_SERVICE = "LocalTransport.RemoteService";
/*    */   private AxisServer server;
/*    */   private String remoteServiceName;
/*    */ 
/*    */   public LocalTransport()
/*    */   {
/* 56 */     this.transportName = "local";
/*    */   }
/*    */ 
/*    */   public LocalTransport(AxisServer server)
/*    */   {
/* 67 */     this.transportName = "local";
/* 68 */     this.server = server;
/*    */   }
/*    */ 
/*    */   public void setRemoteService(String remoteServiceName)
/*    */   {
/* 79 */     this.remoteServiceName = remoteServiceName;
/*    */   }
/*    */ 
/*    */   public void setupMessageContextImpl(MessageContext mc, Call call, AxisEngine engine)
/*    */   {
/* 92 */     if (this.server != null)
/* 93 */       mc.setProperty("LocalTransport.AxisServer", this.server);
/* 94 */     if (this.remoteServiceName != null)
/* 95 */       mc.setProperty("LocalTransport.RemoteService", this.remoteServiceName);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.local.LocalTransport
 * JD-Core Version:    0.6.0
 */