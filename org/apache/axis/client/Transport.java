/*    */ package org.apache.axis.client;
/*    */ 
/*    */ import org.apache.axis.AxisEngine;
/*    */ import org.apache.axis.AxisFault;
/*    */ import org.apache.axis.MessageContext;
/*    */ 
/*    */ public class Transport
/*    */ {
/* 28 */   public String transportName = null;
/*    */ 
/* 33 */   public String url = null;
/*    */ 
/*    */   public final void setupMessageContext(MessageContext context, Call message, AxisEngine engine)
/*    */     throws AxisFault
/*    */   {
/* 40 */     if (this.url != null) {
/* 41 */       context.setProperty("transport.url", this.url);
/*    */     }
/* 43 */     if (this.transportName != null) {
/* 44 */       context.setTransportName(this.transportName);
/*    */     }
/* 46 */     setupMessageContextImpl(context, message, engine);
/*    */   }
/*    */ 
/*    */   public void setupMessageContextImpl(MessageContext context, Call message, AxisEngine engine)
/*    */     throws AxisFault
/*    */   {
/*    */   }
/*    */ 
/*    */   public void processReturnedMessageContext(MessageContext context)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void setTransportName(String name)
/*    */   {
/* 71 */     this.transportName = name;
/*    */   }
/*    */ 
/*    */   public String getTransportName()
/*    */   {
/* 79 */     return this.transportName;
/*    */   }
/*    */ 
/*    */   public String getUrl()
/*    */   {
/* 86 */     return this.url;
/*    */   }
/*    */ 
/*    */   public void setUrl(String url)
/*    */   {
/* 93 */     this.url = url;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.client.Transport
 * JD-Core Version:    0.6.0
 */