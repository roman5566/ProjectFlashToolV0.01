/*     */ package org.apache.axis.transport.jms;
/*     */ 
/*     */ import javax.jms.Connection;
/*     */ import javax.jms.ConnectionFactory;
/*     */ import javax.jms.Destination;
/*     */ import javax.jms.JMSException;
/*     */ import javax.jms.Message;
/*     */ import javax.jms.MessageConsumer;
/*     */ import javax.jms.Queue;
/*     */ import javax.jms.QueueConnection;
/*     */ import javax.jms.QueueConnectionFactory;
/*     */ import javax.jms.QueueReceiver;
/*     */ import javax.jms.QueueSender;
/*     */ import javax.jms.QueueSession;
/*     */ import javax.jms.Session;
/*     */ import javax.jms.TemporaryQueue;
/*     */ import org.apache.axis.components.jms.JMSVendorAdapter;
/*     */ 
/*     */ public class QueueConnector extends JMSConnector
/*     */ {
/*     */   public QueueConnector(ConnectionFactory factory, int numRetries, int numSessions, long connectRetryInterval, long interactRetryInterval, long timeoutTime, boolean allowReceive, String clientID, String username, String password, JMSVendorAdapter adapter, JMSURLHelper jmsurl)
/*     */     throws JMSException
/*     */   {
/*  61 */     super(factory, numRetries, numSessions, connectRetryInterval, interactRetryInterval, timeoutTime, allowReceive, clientID, username, password, adapter, jmsurl);
/*     */   }
/*     */ 
/*     */   public JMSEndpoint createEndpoint(String destination)
/*     */   {
/*  68 */     return new QueueEndpoint(destination);
/*     */   }
/*     */ 
/*     */   public JMSEndpoint createEndpoint(Destination destination)
/*     */     throws JMSException
/*     */   {
/*  81 */     if (!(destination instanceof Queue))
/*  82 */       throw new IllegalArgumentException("The input must be a queue for this connector");
/*  83 */     return new QueueDestinationEndpoint((Queue)destination);
/*     */   }
/*     */ 
/*     */   protected Connection internalConnect(ConnectionFactory connectionFactory, String username, String password)
/*     */     throws JMSException
/*     */   {
/*  91 */     QueueConnectionFactory qcf = (QueueConnectionFactory)connectionFactory;
/*  92 */     if (username == null) {
/*  93 */       return qcf.createQueueConnection();
/*     */     }
/*  95 */     return qcf.createQueueConnection(username, password);
/*     */   }
/*     */ 
/*     */   protected JMSConnector.SyncConnection createSyncConnection(ConnectionFactory factory, Connection connection, int numSessions, String threadName, String clientID, String username, String password)
/*     */     throws JMSException
/*     */   {
/* 109 */     return new QueueSyncConnection((QueueConnectionFactory)factory, (QueueConnection)connection, numSessions, threadName, clientID, username, password);
/*     */   }
/*     */ 
/*     */   private QueueSession createQueueSession(QueueConnection connection, int ackMode)
/*     */     throws JMSException
/*     */   {
/* 117 */     return connection.createQueueSession(false, ackMode);
/*     */   }
/*     */ 
/*     */   private Queue createQueue(QueueSession session, String subject)
/*     */     throws Exception
/*     */   {
/* 123 */     return this.m_adapter.getQueue(session, subject);
/*     */   }
/*     */ 
/*     */   private QueueReceiver createReceiver(QueueSession session, Queue queue, String messageSelector)
/*     */     throws JMSException
/*     */   {
/* 131 */     return session.createReceiver(queue, messageSelector);
/*     */   }
/*     */ 
/*     */   protected JMSConnector.AsyncConnection createAsyncConnection(ConnectionFactory factory, Connection connection, String threadName, String clientID, String username, String password)
/*     */     throws JMSException
/*     */   {
/* 261 */     return new QueueAsyncConnection((QueueConnectionFactory)factory, (QueueConnection)connection, threadName, clientID, username, password);
/*     */   }
/*     */ 
/*     */   private final class QueueAsyncConnection extends JMSConnector.AsyncConnection
/*     */   {
/*     */     QueueAsyncConnection(QueueConnectionFactory connectionFactory, QueueConnection connection, String threadName, String clientID, String username, String password)
/*     */       throws JMSException
/*     */     {
/* 277 */       super(connectionFactory, connection, threadName, clientID, username, password);
/*     */     }
/*     */ 
/*     */     protected JMSConnector.AsyncConnection.ListenerSession createListenerSession(Connection connection, Subscription subscription)
/*     */       throws Exception
/*     */     {
/* 284 */       QueueSession session = QueueConnector.this.createQueueSession((QueueConnection)connection, subscription.m_ackMode);
/*     */ 
/* 286 */       QueueReceiver receiver = QueueConnector.this.createReceiver(session, (Queue)subscription.m_endpoint.getDestination(session), subscription.m_messageSelector);
/*     */ 
/* 289 */       return new JMSConnector.AsyncConnection.ListenerSession(this, session, receiver, subscription);
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class QueueDestinationEndpoint extends QueueConnector.QueueEndpoint
/*     */   {
/*     */     Queue m_queue;
/*     */ 
/*     */     QueueDestinationEndpoint(Queue queue)
/*     */       throws JMSException
/*     */     {
/* 242 */       super(queue.getQueueName());
/* 243 */       this.m_queue = queue;
/*     */     }
/*     */ 
/*     */     Destination getDestination(Session session)
/*     */     {
/* 248 */       return this.m_queue;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class QueueEndpoint extends JMSEndpoint
/*     */   {
/*     */     String m_queueName;
/*     */ 
/*     */     QueueEndpoint(String queueName)
/*     */     {
/* 204 */       super();
/* 205 */       this.m_queueName = queueName;
/*     */     }
/*     */ 
/*     */     Destination getDestination(Session session)
/*     */       throws Exception
/*     */     {
/* 211 */       return QueueConnector.this.createQueue((QueueSession)session, this.m_queueName);
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 216 */       StringBuffer buffer = new StringBuffer("QueueEndpoint:");
/* 217 */       buffer.append(this.m_queueName);
/* 218 */       return buffer.toString();
/*     */     }
/*     */ 
/*     */     public boolean equals(Object object)
/*     */     {
/* 223 */       if (!super.equals(object)) {
/* 224 */         return false;
/*     */       }
/* 226 */       if (!(object instanceof QueueEndpoint)) {
/* 227 */         return false;
/*     */       }
/* 229 */       return this.m_queueName.equals(((QueueEndpoint)object).m_queueName);
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class QueueSyncConnection extends JMSConnector.SyncConnection
/*     */   {
/*     */     QueueSyncConnection(QueueConnectionFactory connectionFactory, QueueConnection connection, int numSessions, String threadName, String clientID, String username, String password)
/*     */       throws JMSException
/*     */     {
/* 145 */       super(connectionFactory, connection, numSessions, threadName, clientID, username, password);
/*     */     }
/*     */ 
/*     */     protected JMSConnector.SyncConnection.SendSession createSendSession(Connection connection)
/*     */       throws JMSException
/*     */     {
/* 152 */       QueueSession session = QueueConnector.this.createQueueSession((QueueConnection)connection, 3);
/*     */ 
/* 154 */       QueueSender sender = session.createSender(null);
/* 155 */       return new QueueSendSession(session, sender);
/*     */     }
/*     */ 
/*     */     private final class QueueSendSession extends JMSConnector.SyncConnection.SendSession
/*     */     {
/*     */       QueueSendSession(QueueSession session, QueueSender sender)
/*     */         throws JMSException
/*     */       {
/* 164 */         super(session, sender);
/*     */       }
/*     */ 
/*     */       protected MessageConsumer createConsumer(Destination destination)
/*     */         throws JMSException
/*     */       {
/* 170 */         return QueueConnector.this.createReceiver((QueueSession)this.m_session, (Queue)destination, null);
/*     */       }
/*     */ 
/*     */       protected Destination createTemporaryDestination()
/*     */         throws JMSException
/*     */       {
/* 177 */         return ((QueueSession)this.m_session).createTemporaryQueue();
/*     */       }
/*     */ 
/*     */       protected void deleteTemporaryDestination(Destination destination)
/*     */         throws JMSException
/*     */       {
/* 183 */         ((TemporaryQueue)destination).delete();
/*     */       }
/*     */ 
/*     */       protected void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive)
/*     */         throws JMSException
/*     */       {
/* 190 */         ((QueueSender)this.m_producer).send((Queue)destination, message, deliveryMode, priority, timeToLive);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.jms.QueueConnector
 * JD-Core Version:    0.6.0
 */