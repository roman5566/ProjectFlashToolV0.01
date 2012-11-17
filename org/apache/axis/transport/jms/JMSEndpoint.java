/*     */ package org.apache.axis.transport.jms;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import javax.jms.Destination;
/*     */ import javax.jms.MessageListener;
/*     */ import javax.jms.Session;
/*     */ 
/*     */ public abstract class JMSEndpoint
/*     */ {
/*     */   private JMSConnector m_connector;
/*     */ 
/*     */   protected JMSEndpoint(JMSConnector connector)
/*     */   {
/*  38 */     this.m_connector = connector;
/*     */   }
/*     */ 
/*     */   abstract Destination getDestination(Session paramSession)
/*     */     throws Exception;
/*     */ 
/*     */   public byte[] call(byte[] message, long timeout)
/*     */     throws Exception
/*     */   {
/*  54 */     return this.m_connector.getSendConnection().call(this, message, timeout, null);
/*     */   }
/*     */ 
/*     */   public byte[] call(byte[] message, long timeout, HashMap properties)
/*     */     throws Exception
/*     */   {
/*  69 */     if (properties != null)
/*  70 */       properties = (HashMap)properties.clone();
/*  71 */     return this.m_connector.getSendConnection().call(this, message, timeout, properties);
/*     */   }
/*     */ 
/*     */   public void send(byte[] message)
/*     */     throws Exception
/*     */   {
/*  82 */     this.m_connector.getSendConnection().send(this, message, null);
/*     */   }
/*     */ 
/*     */   public void send(byte[] message, HashMap properties)
/*     */     throws Exception
/*     */   {
/*  95 */     if (properties != null)
/*  96 */       properties = (HashMap)properties.clone();
/*  97 */     this.m_connector.getSendConnection().send(this, message, properties);
/*     */   }
/*     */ 
/*     */   public void registerListener(MessageListener listener)
/*     */     throws Exception
/*     */   {
/* 109 */     this.m_connector.getReceiveConnection().subscribe(createSubscription(listener, null));
/*     */   }
/*     */ 
/*     */   public void registerListener(MessageListener listener, HashMap properties)
/*     */     throws Exception
/*     */   {
/* 122 */     if (properties != null)
/* 123 */       properties = (HashMap)properties.clone();
/* 124 */     this.m_connector.getReceiveConnection().subscribe(createSubscription(listener, properties));
/*     */   }
/*     */ 
/*     */   public void unregisterListener(MessageListener listener)
/*     */   {
/* 134 */     this.m_connector.getReceiveConnection().unsubscribe(createSubscription(listener, null));
/*     */   }
/*     */ 
/*     */   public void unregisterListener(MessageListener listener, HashMap properties)
/*     */   {
/* 145 */     if (properties != null)
/* 146 */       properties = (HashMap)properties.clone();
/* 147 */     this.m_connector.getReceiveConnection().unsubscribe(createSubscription(listener, properties));
/*     */   }
/*     */ 
/*     */   protected Subscription createSubscription(MessageListener listener, HashMap properties)
/*     */   {
/* 153 */     return new Subscription(listener, this, properties);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 158 */     return toString().hashCode();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object object)
/*     */   {
/* 164 */     return (object != null) && ((object instanceof JMSEndpoint));
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.jms.JMSEndpoint
 * JD-Core Version:    0.6.0
 */