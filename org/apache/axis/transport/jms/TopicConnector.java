/*     */ package org.apache.axis.transport.jms;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import javax.jms.Connection;
/*     */ import javax.jms.ConnectionFactory;
/*     */ import javax.jms.Destination;
/*     */ import javax.jms.JMSException;
/*     */ import javax.jms.Message;
/*     */ import javax.jms.MessageConsumer;
/*     */ import javax.jms.MessageListener;
/*     */ import javax.jms.Session;
/*     */ import javax.jms.TemporaryTopic;
/*     */ import javax.jms.Topic;
/*     */ import javax.jms.TopicConnection;
/*     */ import javax.jms.TopicConnectionFactory;
/*     */ import javax.jms.TopicPublisher;
/*     */ import javax.jms.TopicSession;
/*     */ import javax.jms.TopicSubscriber;
/*     */ import org.apache.axis.components.jms.JMSVendorAdapter;
/*     */ 
/*     */ public class TopicConnector extends JMSConnector
/*     */ {
/*     */   public TopicConnector(TopicConnectionFactory factory, int numRetries, int numSessions, long connectRetryInterval, long interactRetryInterval, long timeoutTime, boolean allowReceive, String clientID, String username, String password, JMSVendorAdapter adapter, JMSURLHelper jmsurl)
/*     */     throws JMSException
/*     */   {
/*  62 */     super(factory, numRetries, numSessions, connectRetryInterval, interactRetryInterval, timeoutTime, allowReceive, clientID, username, password, adapter, jmsurl);
/*     */   }
/*     */ 
/*     */   protected Connection internalConnect(ConnectionFactory connectionFactory, String username, String password)
/*     */     throws JMSException
/*     */   {
/*  71 */     TopicConnectionFactory tcf = (TopicConnectionFactory)connectionFactory;
/*  72 */     if (username == null) {
/*  73 */       return tcf.createTopicConnection();
/*     */     }
/*  75 */     return tcf.createTopicConnection(username, password);
/*     */   }
/*     */ 
/*     */   protected JMSConnector.SyncConnection createSyncConnection(ConnectionFactory factory, Connection connection, int numSessions, String threadName, String clientID, String username, String password)
/*     */     throws JMSException
/*     */   {
/*  88 */     return new TopicSyncConnection((TopicConnectionFactory)factory, (TopicConnection)connection, numSessions, threadName, clientID, username, password);
/*     */   }
/*     */ 
/*     */   protected JMSConnector.AsyncConnection createAsyncConnection(ConnectionFactory factory, Connection connection, String threadName, String clientID, String username, String password)
/*     */     throws JMSException
/*     */   {
/* 101 */     return new TopicAsyncConnection((TopicConnectionFactory)factory, (TopicConnection)connection, threadName, clientID, username, password);
/*     */   }
/*     */ 
/*     */   public JMSEndpoint createEndpoint(String destination)
/*     */   {
/* 108 */     return new TopicEndpoint(destination);
/*     */   }
/*     */ 
/*     */   public JMSEndpoint createEndpoint(Destination destination)
/*     */     throws JMSException
/*     */   {
/* 121 */     if (!(destination instanceof Topic))
/* 122 */       throw new IllegalArgumentException("The input be a topic for this connector");
/* 123 */     return new TopicDestinationEndpoint((Topic)destination);
/*     */   }
/*     */ 
/*     */   private TopicSession createTopicSession(TopicConnection connection, int ackMode)
/*     */     throws JMSException
/*     */   {
/* 129 */     return connection.createTopicSession(false, ackMode);
/*     */   }
/*     */ 
/*     */   private Topic createTopic(TopicSession session, String subject)
/*     */     throws Exception
/*     */   {
/* 136 */     return this.m_adapter.getTopic(session, subject);
/*     */   }
/*     */ 
/*     */   private TopicSubscriber createSubscriber(TopicSession session, TopicSubscription subscription)
/*     */     throws Exception
/*     */   {
/* 143 */     if (subscription.isDurable()) {
/* 144 */       return createDurableSubscriber(session, (Topic)subscription.m_endpoint.getDestination(session), subscription.m_subscriptionName, subscription.m_messageSelector, subscription.m_noLocal);
/*     */     }
/*     */ 
/* 150 */     return createSubscriber(session, (Topic)subscription.m_endpoint.getDestination(session), subscription.m_messageSelector, subscription.m_noLocal);
/*     */   }
/*     */ 
/*     */   private TopicSubscriber createDurableSubscriber(TopicSession session, Topic topic, String subscriptionName, String messageSelector, boolean noLocal)
/*     */     throws JMSException
/*     */   {
/* 163 */     return session.createDurableSubscriber(topic, subscriptionName, messageSelector, noLocal);
/*     */   }
/*     */ 
/*     */   private TopicSubscriber createSubscriber(TopicSession session, Topic topic, String messageSelector, boolean noLocal)
/*     */     throws JMSException
/*     */   {
/* 173 */     return session.createSubscriber(topic, messageSelector, noLocal);
/*     */   }
/*     */ 
/*     */   private final class TopicDestinationEndpoint extends TopicConnector.TopicEndpoint
/*     */   {
/*     */     Topic m_topic;
/*     */ 
/*     */     TopicDestinationEndpoint(Topic topic)
/*     */       throws JMSException
/*     */     {
/* 416 */       super(topic.getTopicName());
/* 417 */       this.m_topic = topic;
/*     */     }
/*     */ 
/*     */     Destination getDestination(Session session)
/*     */     {
/* 422 */       return this.m_topic;
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class TopicSubscription extends Subscription
/*     */   {
/*     */     String m_subscriptionName;
/*     */     boolean m_unsubscribe;
/*     */     boolean m_noLocal;
/*     */ 
/*     */     TopicSubscription(MessageListener listener, JMSEndpoint endpoint, HashMap properties)
/*     */     {
/* 356 */       super(endpoint, properties);
/* 357 */       this.m_subscriptionName = MapUtils.removeStringProperty(properties, "transport.jms.subscriptionName", null);
/*     */ 
/* 360 */       this.m_unsubscribe = MapUtils.removeBooleanProperty(properties, "transport.jms.unsubscribe", false);
/*     */ 
/* 363 */       this.m_noLocal = MapUtils.removeBooleanProperty(properties, "transport.jms.noLocal", false);
/*     */     }
/*     */ 
/*     */     boolean isDurable()
/*     */     {
/* 370 */       return this.m_subscriptionName != null;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object obj)
/*     */     {
/* 375 */       if (!super.equals(obj))
/* 376 */         return false;
/* 377 */       if (!(obj instanceof TopicSubscription)) {
/* 378 */         return false;
/*     */       }
/* 380 */       TopicSubscription other = (TopicSubscription)obj;
/* 381 */       if ((other.m_unsubscribe != this.m_unsubscribe) || (other.m_noLocal != this.m_noLocal)) {
/* 382 */         return false;
/*     */       }
/* 384 */       if (isDurable())
/*     */       {
/* 386 */         return (other.isDurable()) && (other.m_subscriptionName.equals(this.m_subscriptionName));
/*     */       }
/*     */ 
/* 389 */       return !other.isDurable();
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 396 */       StringBuffer buffer = new StringBuffer(super.toString());
/* 397 */       buffer.append(":").append(this.m_noLocal).append(":").append(this.m_unsubscribe);
/* 398 */       if (isDurable())
/*     */       {
/* 400 */         buffer.append(":");
/* 401 */         buffer.append(this.m_subscriptionName);
/*     */       }
/* 403 */       return buffer.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class TopicEndpoint extends JMSEndpoint
/*     */   {
/*     */     String m_topicName;
/*     */ 
/*     */     TopicEndpoint(String topicName)
/*     */     {
/* 311 */       super();
/* 312 */       this.m_topicName = topicName;
/*     */     }
/*     */ 
/*     */     Destination getDestination(Session session)
/*     */       throws Exception
/*     */     {
/* 318 */       return TopicConnector.this.createTopic((TopicSession)session, this.m_topicName);
/*     */     }
/*     */ 
/*     */     protected Subscription createSubscription(MessageListener listener, HashMap properties)
/*     */     {
/* 324 */       return new TopicConnector.TopicSubscription(TopicConnector.this, listener, this, properties);
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 329 */       StringBuffer buffer = new StringBuffer("TopicEndpoint:");
/* 330 */       buffer.append(this.m_topicName);
/* 331 */       return buffer.toString();
/*     */     }
/*     */ 
/*     */     public boolean equals(Object object)
/*     */     {
/* 336 */       if (!super.equals(object)) {
/* 337 */         return false;
/*     */       }
/* 339 */       if (!(object instanceof TopicEndpoint)) {
/* 340 */         return false;
/*     */       }
/* 342 */       return this.m_topicName.equals(((TopicEndpoint)object).m_topicName);
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class TopicSyncConnection extends JMSConnector.SyncConnection
/*     */   {
/*     */     TopicSyncConnection(TopicConnectionFactory connectionFactory, TopicConnection connection, int numSessions, String threadName, String clientID, String username, String password)
/*     */       throws JMSException
/*     */     {
/* 248 */       super(connectionFactory, connection, numSessions, threadName, clientID, username, password);
/*     */     }
/*     */ 
/*     */     protected JMSConnector.SyncConnection.SendSession createSendSession(Connection connection)
/*     */       throws JMSException
/*     */     {
/* 255 */       TopicSession session = TopicConnector.this.createTopicSession((TopicConnection)connection, 3);
/*     */ 
/* 257 */       TopicPublisher publisher = session.createPublisher(null);
/* 258 */       return new TopicSendSession(session, publisher);
/*     */     }
/*     */ 
/*     */     private final class TopicSendSession extends JMSConnector.SyncConnection.SendSession
/*     */     {
/*     */       TopicSendSession(TopicSession session, TopicPublisher publisher)
/*     */         throws JMSException
/*     */       {
/* 267 */         super(session, publisher);
/*     */       }
/*     */ 
/*     */       protected MessageConsumer createConsumer(Destination destination)
/*     */         throws JMSException
/*     */       {
/* 274 */         return TopicConnector.this.createSubscriber((TopicSession)this.m_session, (Topic)destination, null, false);
/*     */       }
/*     */ 
/*     */       protected void deleteTemporaryDestination(Destination destination)
/*     */         throws JMSException
/*     */       {
/* 281 */         ((TemporaryTopic)destination).delete();
/*     */       }
/*     */ 
/*     */       protected Destination createTemporaryDestination()
/*     */         throws JMSException
/*     */       {
/* 288 */         return ((TopicSession)this.m_session).createTemporaryTopic();
/*     */       }
/*     */ 
/*     */       protected void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive)
/*     */         throws JMSException
/*     */       {
/* 295 */         ((TopicPublisher)this.m_producer).publish((Topic)destination, message, deliveryMode, priority, timeToLive);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class TopicAsyncConnection extends JMSConnector.AsyncConnection
/*     */   {
/*     */     TopicAsyncConnection(TopicConnectionFactory connectionFactory, TopicConnection connection, String threadName, String clientID, String username, String password)
/*     */       throws JMSException
/*     */     {
/* 191 */       super(connectionFactory, connection, threadName, clientID, username, password);
/*     */     }
/*     */ 
/*     */     protected JMSConnector.AsyncConnection.ListenerSession createListenerSession(Connection connection, Subscription subscription)
/*     */       throws Exception
/*     */     {
/* 199 */       TopicSession session = TopicConnector.this.createTopicSession((TopicConnection)connection, subscription.m_ackMode);
/*     */ 
/* 201 */       TopicSubscriber subscriber = TopicConnector.this.createSubscriber(session, (TopicConnector.TopicSubscription)subscription);
/*     */ 
/* 203 */       return new TopicListenerSession(session, subscriber, (TopicConnector.TopicSubscription)subscription);
/*     */     }
/*     */ 
/*     */     private final class TopicListenerSession extends JMSConnector.AsyncConnection.ListenerSession
/*     */     {
/*     */       TopicListenerSession(TopicSession session, TopicSubscriber subscriber, TopicConnector.TopicSubscription subscription)
/*     */         throws Exception
/*     */       {
/* 215 */         super(session, subscriber, subscription);
/*     */       }
/*     */ 
/*     */       void cleanup() {
/*     */         try {
/* 220 */           this.m_consumer.close(); } catch (Exception ignore) {
/*     */         }
/*     */         try {
/* 223 */           TopicConnector.TopicSubscription sub = (TopicConnector.TopicSubscription)this.m_subscription;
/* 224 */           if ((sub.isDurable()) && (sub.m_unsubscribe))
/*     */           {
/* 226 */             ((TopicSession)this.m_session).unsubscribe(sub.m_subscriptionName);
/*     */           }
/*     */         } catch (Exception ignore) {
/*     */         }
/*     */         try {
/* 230 */           this.m_session.close();
/*     */         }
/*     */         catch (Exception ignore)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.jms.TopicConnector
 * JD-Core Version:    0.6.0
 */