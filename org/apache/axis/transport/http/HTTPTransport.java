/*    */ package org.apache.axis.transport.http;
/*    */ 
/*    */ import org.apache.axis.AxisEngine;
/*    */ import org.apache.axis.AxisFault;
/*    */ import org.apache.axis.MessageContext;
/*    */ import org.apache.axis.client.Call;
/*    */ import org.apache.axis.client.Transport;
/*    */ 
/*    */ public class HTTPTransport extends Transport
/*    */ {
/*    */   public static final String DEFAULT_TRANSPORT_NAME = "http";
/*    */   public static final String URL = "transport.url";
/*    */   private Object cookie;
/*    */   private Object cookie2;
/*    */   private String action;
/*    */ 
/*    */   public HTTPTransport()
/*    */   {
/* 49 */     this.transportName = "http";
/*    */   }
/*    */ 
/*    */   public HTTPTransport(String url, String action)
/*    */   {
/* 57 */     this.transportName = "http";
/* 58 */     this.url = url;
/* 59 */     this.action = action;
/*    */   }
/*    */ 
/*    */   public void setupMessageContextImpl(MessageContext mc, Call call, AxisEngine engine)
/*    */     throws AxisFault
/*    */   {
/* 74 */     if (this.action != null) {
/* 75 */       mc.setUseSOAPAction(true);
/* 76 */       mc.setSOAPActionURI(this.action);
/*    */     }
/*    */ 
/* 80 */     if (this.cookie != null)
/* 81 */       mc.setProperty("Cookie", this.cookie);
/* 82 */     if (this.cookie2 != null) {
/* 83 */       mc.setProperty("Cookie2", this.cookie2);
/*    */     }
/*    */ 
/* 88 */     if (mc.getService() == null)
/* 89 */       mc.setTargetService(mc.getSOAPActionURI());
/*    */   }
/*    */ 
/*    */   public void processReturnedMessageContext(MessageContext context)
/*    */   {
/* 94 */     this.cookie = context.getProperty("Cookie");
/* 95 */     this.cookie2 = context.getProperty("Cookie2");
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.http.HTTPTransport
 * JD-Core Version:    0.6.0
 */