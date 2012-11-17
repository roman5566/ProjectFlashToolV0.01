/*    */ package org.apache.axis.transport.jms;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import javax.jms.MessageListener;
/*    */ 
/*    */ public class Subscription
/*    */ {
/*    */   MessageListener m_listener;
/*    */   JMSEndpoint m_endpoint;
/*    */   String m_messageSelector;
/*    */   int m_ackMode;
/*    */ 
/*    */   Subscription(MessageListener listener, JMSEndpoint endpoint, HashMap properties)
/*    */   {
/* 40 */     this.m_listener = listener;
/* 41 */     this.m_endpoint = endpoint;
/* 42 */     this.m_messageSelector = MapUtils.removeStringProperty(properties, "transport.jms.messageSelector", null);
/*    */ 
/* 46 */     this.m_ackMode = MapUtils.removeIntProperty(properties, "transport.jms.acknowledgeMode", 3);
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 53 */     return toString().hashCode();
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 58 */     if ((obj == null) || (!(obj instanceof Subscription)))
/* 59 */       return false;
/* 60 */     Subscription other = (Subscription)obj;
/* 61 */     if (this.m_messageSelector == null)
/*    */     {
/* 63 */       if (other.m_messageSelector != null) {
/* 64 */         return false;
/*    */       }
/*    */ 
/*    */     }
/* 68 */     else if ((other.m_messageSelector == null) || (!other.m_messageSelector.equals(this.m_messageSelector)))
/*    */     {
/* 70 */       return false;
/*    */     }
/* 72 */     return (this.m_ackMode == other.m_ackMode) && (this.m_endpoint.equals(other.m_endpoint)) && (other.m_listener.equals(this.m_listener));
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 79 */     return this.m_listener.toString();
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.jms.Subscription
 * JD-Core Version:    0.6.0
 */