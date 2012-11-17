/*     */ package org.apache.axis.transport.jms;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.jms.BytesMessage;
/*     */ import javax.jms.Connection;
/*     */ import javax.jms.ConnectionFactory;
/*     */ import javax.jms.Destination;
/*     */ import javax.jms.ExceptionListener;
/*     */ import javax.jms.JMSException;
/*     */ import javax.jms.Message;
/*     */ import javax.jms.MessageConsumer;
/*     */ import javax.jms.MessageProducer;
/*     */ import javax.jms.Session;
/*     */ import org.apache.axis.components.jms.JMSVendorAdapter;
/*     */ 
/*     */ public abstract class JMSConnector
/*     */ {
/*     */   protected int m_numRetries;
/*     */   protected long m_connectRetryInterval;
/*     */   protected long m_interactRetryInterval;
/*     */   protected long m_timeoutTime;
/*     */   protected long m_poolTimeout;
/*     */   protected AsyncConnection m_receiveConnection;
/*     */   protected SyncConnection m_sendConnection;
/*     */   protected int m_numSessions;
/*     */   protected boolean m_allowReceive;
/*     */   protected JMSVendorAdapter m_adapter;
/*     */   protected JMSURLHelper m_jmsurl;
/*     */ 
/*     */   public JMSConnector(ConnectionFactory connectionFactory, int numRetries, int numSessions, long connectRetryInterval, long interactRetryInterval, long timeoutTime, boolean allowReceive, String clientID, String username, String password, JMSVendorAdapter adapter, JMSURLHelper jmsurl)
/*     */     throws JMSException
/*     */   {
/*  80 */     this.m_numRetries = numRetries;
/*  81 */     this.m_connectRetryInterval = connectRetryInterval;
/*  82 */     this.m_interactRetryInterval = interactRetryInterval;
/*  83 */     this.m_timeoutTime = timeoutTime;
/*  84 */     this.m_poolTimeout = (timeoutTime / numRetries);
/*  85 */     this.m_numSessions = numSessions;
/*  86 */     this.m_allowReceive = allowReceive;
/*  87 */     this.m_adapter = adapter;
/*  88 */     this.m_jmsurl = jmsurl;
/*     */ 
/*  93 */     Connection sendConnection = createConnectionWithRetry(connectionFactory, username, password);
/*     */ 
/*  97 */     this.m_sendConnection = createSyncConnection(connectionFactory, sendConnection, this.m_numSessions, "SendThread", clientID, username, password);
/*     */ 
/* 103 */     this.m_sendConnection.start();
/*     */ 
/* 105 */     if (this.m_allowReceive)
/*     */     {
/* 107 */       Connection receiveConnection = createConnectionWithRetry(connectionFactory, username, password);
/*     */ 
/* 111 */       this.m_receiveConnection = createAsyncConnection(connectionFactory, receiveConnection, "ReceiveThread", clientID, username, password);
/*     */ 
/* 117 */       this.m_receiveConnection.start();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getNumRetries()
/*     */   {
/* 123 */     return this.m_numRetries;
/*     */   }
/*     */ 
/*     */   public int numSessions()
/*     */   {
/* 128 */     return this.m_numSessions;
/*     */   }
/*     */ 
/*     */   public ConnectionFactory getConnectionFactory()
/*     */   {
/* 134 */     return getSendConnection().getConnectionFactory();
/*     */   }
/*     */ 
/*     */   public String getClientID()
/*     */   {
/* 139 */     return getSendConnection().getClientID();
/*     */   }
/*     */ 
/*     */   public String getUsername()
/*     */   {
/* 144 */     return getSendConnection().getUsername();
/*     */   }
/*     */ 
/*     */   public String getPassword()
/*     */   {
/* 149 */     return getSendConnection().getPassword();
/*     */   }
/*     */ 
/*     */   public JMSVendorAdapter getVendorAdapter()
/*     */   {
/* 154 */     return this.m_adapter;
/*     */   }
/*     */ 
/*     */   public JMSURLHelper getJMSURL()
/*     */   {
/* 159 */     return this.m_jmsurl;
/*     */   }
/*     */ 
/*     */   protected Connection createConnectionWithRetry(ConnectionFactory connectionFactory, String username, String password)
/*     */     throws JMSException
/*     */   {
/* 168 */     Connection connection = null;
/* 169 */     for (int numTries = 1; connection == null; numTries++)
/*     */     {
/*     */       try
/*     */       {
/* 173 */         connection = internalConnect(connectionFactory, username, password);
/*     */       }
/*     */       catch (JMSException jmse)
/*     */       {
/* 177 */         if ((!this.m_adapter.isRecoverable(jmse, 1)) || (numTries == this.m_numRetries))
/* 178 */           throw jmse;
/*     */         try {
/* 180 */           Thread.sleep(this.m_connectRetryInterval); } catch (InterruptedException ie) {
/*     */         }
/*     */       }
/*     */     }
/* 183 */     return connection;
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/* 188 */     JMSConnectorManager.getInstance().removeConnectorFromPool(this);
/*     */ 
/* 190 */     this.m_sendConnection.stopConnection();
/* 191 */     if (this.m_allowReceive)
/* 192 */       this.m_receiveConnection.stopConnection();
/*     */   }
/*     */ 
/*     */   public void start()
/*     */   {
/* 197 */     this.m_sendConnection.startConnection();
/* 198 */     if (this.m_allowReceive) {
/* 199 */       this.m_receiveConnection.startConnection();
/*     */     }
/* 201 */     JMSConnectorManager.getInstance().addConnectorToPool(this);
/*     */   }
/*     */ 
/*     */   public void shutdown()
/*     */   {
/* 206 */     this.m_sendConnection.shutdown();
/* 207 */     if (this.m_allowReceive)
/* 208 */       this.m_receiveConnection.shutdown();
/*     */   }
/*     */ 
/*     */   public abstract JMSEndpoint createEndpoint(String paramString)
/*     */     throws JMSException;
/*     */ 
/*     */   public abstract JMSEndpoint createEndpoint(Destination paramDestination)
/*     */     throws JMSException;
/*     */ 
/*     */   protected abstract Connection internalConnect(ConnectionFactory paramConnectionFactory, String paramString1, String paramString2)
/*     */     throws JMSException;
/*     */ 
/*     */   protected abstract SyncConnection createSyncConnection(ConnectionFactory paramConnectionFactory, Connection paramConnection, int paramInt, String paramString1, String paramString2, String paramString3, String paramString4)
/*     */     throws JMSException;
/*     */ 
/*     */   SyncConnection getSendConnection()
/*     */   {
/* 429 */     return this.m_sendConnection;
/*     */   }
/*     */ 
/*     */   AsyncConnection getReceiveConnection()
/*     */   {
/* 762 */     return this.m_receiveConnection;
/*     */   }
/*     */ 
/*     */   protected abstract AsyncConnection createAsyncConnection(ConnectionFactory paramConnectionFactory, Connection paramConnection, String paramString1, String paramString2, String paramString3, String paramString4)
/*     */     throws JMSException;
/*     */ 
/*     */   private abstract class ConnectorSession
/*     */   {
/*     */     Session m_session;
/*     */ 
/*     */     ConnectorSession(Session session)
/*     */       throws JMSException
/*     */     {
/* 973 */       this.m_session = session;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected abstract class AsyncConnection extends JMSConnector.Connection
/*     */   {
/*     */     HashMap m_subscriptions;
/*     */     Object m_subscriptionLock;
/*     */ 
/*     */     protected AsyncConnection(ConnectionFactory connectionFactory, Connection connection, String threadName, String clientID, String username, String password)
/*     */       throws JMSException
/*     */     {
/* 787 */       super(connectionFactory, connection, threadName, clientID, username, password);
/*     */ 
/* 789 */       this.m_subscriptions = new HashMap();
/* 790 */       this.m_subscriptionLock = new Object();
/*     */     }
/*     */ 
/*     */     protected abstract ListenerSession createListenerSession(Connection paramConnection, Subscription paramSubscription)
/*     */       throws Exception;
/*     */ 
/*     */     protected void onShutdown()
/*     */     {
/* 800 */       synchronized (this.m_subscriptionLock)
/*     */       {
/* 802 */         Iterator subscriptions = this.m_subscriptions.keySet().iterator();
/* 803 */         while (subscriptions.hasNext())
/*     */         {
/* 805 */           Subscription subscription = (Subscription)subscriptions.next();
/* 806 */           ListenerSession session = (ListenerSession)this.m_subscriptions.get(subscription);
/*     */ 
/* 808 */           if (session != null)
/*     */           {
/* 810 */             session.cleanup();
/*     */           }
/*     */         }
/*     */ 
/* 814 */         this.m_subscriptions.clear();
/*     */       }
/*     */     }
/*     */ 
/*     */     void subscribe(Subscription subscription)
/*     */       throws Exception
/*     */     {
/* 825 */       long timeoutTime = System.currentTimeMillis() + JMSConnector.this.m_timeoutTime;
/* 826 */       synchronized (this.m_subscriptionLock)
/*     */       {
/* 828 */         if (this.m_subscriptions.containsKey(subscription))
/* 829 */           return;
/*     */         while (true)
/*     */         {
/* 832 */           if (System.currentTimeMillis() > timeoutTime)
/*     */           {
/* 834 */             throw new InvokeTimeoutException("Cannot subscribe listener");
/*     */           }
/*     */ 
/*     */           try
/*     */           {
/* 839 */             ListenerSession session = createListenerSession(this.m_connection, subscription);
/*     */ 
/* 841 */             this.m_subscriptions.put(subscription, session);
/*     */           }
/*     */           catch (JMSException jmse)
/*     */           {
/* 846 */             if (!JMSConnector.this.m_adapter.isRecoverable(jmse, 2))
/*     */             {
/* 848 */               throw jmse;
/*     */             }
/*     */             try {
/* 851 */               this.m_subscriptionLock.wait(JMSConnector.this.m_interactRetryInterval);
/*     */             } catch (InterruptedException ignore) {
/*     */             }
/* 854 */             Thread.yield();
/* 855 */             continue;
/*     */           }
/*     */           catch (NullPointerException jmse)
/*     */           {
/*     */             try {
/* 860 */               this.m_subscriptionLock.wait(JMSConnector.this.m_interactRetryInterval);
/*     */             } catch (InterruptedException ignore) {
/*     */             }
/* 863 */             Thread.yield();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     void unsubscribe(Subscription subscription)
/*     */     {
/* 872 */       long timeoutTime = System.currentTimeMillis() + JMSConnector.this.m_timeoutTime;
/* 873 */       synchronized (this.m_subscriptionLock)
/*     */       {
/* 875 */         if (!this.m_subscriptions.containsKey(subscription))
/* 876 */           return;
/*     */         while (true)
/*     */         {
/* 879 */           if (System.currentTimeMillis() > timeoutTime)
/*     */           {
/* 881 */             throw new InvokeTimeoutException("Cannot unsubscribe listener");
/*     */           }
/*     */ 
/* 885 */           Thread.yield();
/*     */           try
/*     */           {
/* 888 */             ListenerSession session = (ListenerSession)this.m_subscriptions.get(subscription);
/*     */ 
/* 890 */             session.cleanup();
/* 891 */             this.m_subscriptions.remove(subscription);
/*     */           }
/*     */           catch (NullPointerException jmse)
/*     */           {
/*     */             try
/*     */             {
/* 897 */               this.m_subscriptionLock.wait(JMSConnector.this.m_interactRetryInterval);
/*     */             }
/*     */             catch (InterruptedException ignore)
/*     */             {
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     protected void onConnect() throws Exception {
/* 908 */       synchronized (this.m_subscriptionLock)
/*     */       {
/* 910 */         Iterator subscriptions = this.m_subscriptions.keySet().iterator();
/* 911 */         while (subscriptions.hasNext())
/*     */         {
/* 913 */           Subscription subscription = (Subscription)subscriptions.next();
/*     */ 
/* 915 */           if (this.m_subscriptions.get(subscription) == null)
/*     */           {
/* 917 */             this.m_subscriptions.put(subscription, createListenerSession(this.m_connection, subscription));
/*     */           }
/*     */         }
/*     */ 
/* 921 */         this.m_subscriptionLock.notifyAll();
/*     */       }
/*     */     }
/*     */ 
/*     */     protected void onException()
/*     */     {
/* 927 */       synchronized (this.m_subscriptionLock)
/*     */       {
/* 929 */         Iterator subscriptions = this.m_subscriptions.keySet().iterator();
/* 930 */         while (subscriptions.hasNext())
/*     */         {
/* 932 */           Subscription subscription = (Subscription)subscriptions.next();
/* 933 */           this.m_subscriptions.put(subscription, null);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     protected class ListenerSession extends JMSConnector.ConnectorSession
/*     */     {
/*     */       protected MessageConsumer m_consumer;
/*     */       protected Subscription m_subscription;
/*     */ 
/*     */       ListenerSession(Session session, MessageConsumer consumer, Subscription subscription)
/*     */         throws Exception
/*     */       {
/* 950 */         super(session);
/* 951 */         this.m_subscription = subscription;
/* 952 */         this.m_consumer = consumer;
/* 953 */         Destination destination = subscription.m_endpoint.getDestination(this.m_session);
/* 954 */         this.m_consumer.setMessageListener(subscription.m_listener);
/*     */       }
/*     */ 
/*     */       void cleanup() {
/*     */         try {
/* 959 */           this.m_consumer.close(); } catch (Exception ignore) {
/*     */         }try { this.m_session.close();
/*     */         }
/*     */         catch (Exception ignore)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected abstract class SyncConnection extends JMSConnector.Connection
/*     */   {
/*     */     LinkedList m_senders;
/*     */     int m_numSessions;
/*     */     Object m_senderLock;
/*     */ 
/*     */     SyncConnection(ConnectionFactory connectionFactory, Connection connection, int numSessions, String threadName, String clientID, String username, String password)
/*     */       throws JMSException
/*     */     {
/* 447 */       super(connectionFactory, connection, threadName, clientID, username, password);
/*     */ 
/* 449 */       this.m_senders = new LinkedList();
/* 450 */       this.m_numSessions = numSessions;
/* 451 */       this.m_senderLock = new Object();
/*     */     }
/*     */ 
/*     */     protected abstract SendSession createSendSession(Connection paramConnection)
/*     */       throws JMSException;
/*     */ 
/*     */     protected void onConnect() throws JMSException
/*     */     {
/* 460 */       synchronized (this.m_senderLock)
/*     */       {
/* 462 */         for (int i = 0; i < this.m_numSessions; i++)
/*     */         {
/* 464 */           this.m_senders.add(createSendSession(this.m_connection));
/*     */         }
/* 466 */         this.m_senderLock.notifyAll();
/*     */       }
/*     */     }
/*     */ 
/*     */     byte[] call(JMSEndpoint endpoint, byte[] message, long timeout, HashMap properties)
/*     */       throws Exception
/*     */     {
/* 473 */       long timeoutTime = System.currentTimeMillis() + timeout;
/*     */       while (true)
/*     */       {
/* 476 */         if (System.currentTimeMillis() > timeoutTime)
/*     */         {
/* 478 */           throw new InvokeTimeoutException("Unable to complete call in time allotted");
/*     */         }
/*     */ 
/* 481 */         SendSession sendSession = null;
/*     */         try
/*     */         {
/* 484 */           sendSession = getSessionFromPool(JMSConnector.this.m_poolTimeout);
/* 485 */           byte[] response = sendSession.call(endpoint, message, timeoutTime - System.currentTimeMillis(), properties);
/*     */ 
/* 489 */           returnSessionToPool(sendSession);
/* 490 */           if (response == null)
/*     */           {
/* 492 */             throw new InvokeTimeoutException("Unable to complete call in time allotted");
/*     */           }
/* 494 */           return response;
/*     */         }
/*     */         catch (JMSException jmse)
/*     */         {
/* 498 */           if (!JMSConnector.this.m_adapter.isRecoverable(jmse, 0))
/*     */           {
/* 502 */             returnSessionToPool(sendSession);
/* 503 */             throw jmse;
/*     */           }
/*     */ 
/* 509 */           Thread.yield();
/*     */         }
/*     */         catch (NullPointerException npe)
/*     */         {
/* 514 */           Thread.yield();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     void send(JMSEndpoint endpoint, byte[] message, HashMap properties)
/*     */       throws Exception
/*     */     {
/* 525 */       long timeoutTime = System.currentTimeMillis() + JMSConnector.this.m_timeoutTime;
/*     */       while (true)
/*     */       {
/* 528 */         if (System.currentTimeMillis() > timeoutTime)
/*     */         {
/* 530 */           throw new InvokeTimeoutException("Cannot complete send in time allotted");
/*     */         }
/*     */ 
/* 533 */         SendSession sendSession = null;
/*     */         try
/*     */         {
/* 536 */           sendSession = getSessionFromPool(JMSConnector.this.m_poolTimeout);
/* 537 */           sendSession.send(endpoint, message, properties);
/* 538 */           returnSessionToPool(sendSession);
/*     */         }
/*     */         catch (JMSException jmse)
/*     */         {
/* 542 */           if (!JMSConnector.this.m_adapter.isRecoverable(jmse, 0))
/*     */           {
/* 546 */             returnSessionToPool(sendSession);
/* 547 */             throw jmse;
/*     */           }
/*     */ 
/* 552 */           Thread.yield();
/* 553 */           continue;
/*     */         }
/*     */         catch (NullPointerException npe)
/*     */         {
/* 558 */           Thread.yield();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     protected void onException()
/*     */     {
/* 567 */       synchronized (this.m_senderLock)
/*     */       {
/* 569 */         this.m_senders.clear();
/*     */       }
/*     */     }
/*     */ 
/*     */     protected void onShutdown()
/*     */     {
/* 575 */       synchronized (this.m_senderLock)
/*     */       {
/* 577 */         Iterator senders = this.m_senders.iterator();
/* 578 */         while (senders.hasNext())
/*     */         {
/* 580 */           SendSession session = (SendSession)senders.next();
/* 581 */           session.cleanup();
/*     */         }
/* 583 */         this.m_senders.clear();
/*     */       }
/*     */     }
/*     */ 
/*     */     private SendSession getSessionFromPool(long timeout)
/*     */     {
/* 589 */       synchronized (this.m_senderLock)
/*     */       {
/* 591 */         while (this.m_senders.size() == 0)
/*     */         {
/*     */           try
/*     */           {
/* 595 */             this.m_senderLock.wait(timeout);
/* 596 */             if (this.m_senders.size() == 0)
/*     */             {
/* 598 */               return null;
/*     */             }
/*     */           }
/*     */           catch (InterruptedException ignore)
/*     */           {
/* 603 */             return null;
/*     */           }
/*     */         }
/* 606 */         return (SendSession)this.m_senders.removeFirst();
/*     */       }
/*     */     }
/*     */ 
/*     */     private void returnSessionToPool(SendSession sendSession)
/*     */     {
/* 612 */       synchronized (this.m_senderLock)
/*     */       {
/* 614 */         this.m_senders.addLast(sendSession);
/* 615 */         this.m_senderLock.notifyAll();
/*     */       }
/*     */     }
/*     */ 
/*     */     protected abstract class SendSession extends JMSConnector.ConnectorSession
/*     */     {
/*     */       MessageProducer m_producer;
/*     */ 
/*     */       SendSession(Session session, MessageProducer producer)
/*     */         throws JMSException
/*     */       {
/* 627 */         super(session);
/* 628 */         this.m_producer = producer;
/*     */       }
/*     */ 
/*     */       protected abstract Destination createTemporaryDestination()
/*     */         throws JMSException;
/*     */ 
/*     */       protected abstract void deleteTemporaryDestination(Destination paramDestination)
/*     */         throws JMSException;
/*     */ 
/*     */       protected abstract MessageConsumer createConsumer(Destination paramDestination)
/*     */         throws JMSException;
/*     */ 
/*     */       protected abstract void send(Destination paramDestination, Message paramMessage, int paramInt1, int paramInt2, long paramLong)
/*     */         throws JMSException;
/*     */ 
/*     */       void send(JMSEndpoint endpoint, byte[] message, HashMap properties)
/*     */         throws Exception
/*     */       {
/* 650 */         BytesMessage jmsMessage = this.m_session.createBytesMessage();
/* 651 */         jmsMessage.writeBytes(message);
/* 652 */         int deliveryMode = extractDeliveryMode(properties);
/* 653 */         int priority = extractPriority(properties);
/* 654 */         long timeToLive = extractTimeToLive(properties);
/*     */ 
/* 656 */         if ((properties != null) && (!properties.isEmpty())) {
/* 657 */           setProperties(properties, jmsMessage);
/*     */         }
/* 659 */         send(endpoint.getDestination(this.m_session), jmsMessage, deliveryMode, priority, timeToLive);
/*     */       }
/*     */ 
/*     */       void cleanup()
/*     */       {
/*     */         try
/*     */         {
/* 666 */           this.m_producer.close(); } catch (Throwable t) {
/*     */         }try { this.m_session.close();
/*     */         } catch (Throwable t)
/*     */         {
/*     */         }
/*     */       }
/*     */ 
/*     */       byte[] call(JMSEndpoint endpoint, byte[] message, long timeout, HashMap properties) throws Exception {
/* 674 */         Destination reply = createTemporaryDestination();
/* 675 */         MessageConsumer subscriber = createConsumer(reply);
/* 676 */         BytesMessage jmsMessage = this.m_session.createBytesMessage();
/* 677 */         jmsMessage.writeBytes(message);
/* 678 */         jmsMessage.setJMSReplyTo(reply);
/*     */ 
/* 680 */         int deliveryMode = extractDeliveryMode(properties);
/* 681 */         int priority = extractPriority(properties);
/* 682 */         long timeToLive = extractTimeToLive(properties);
/*     */ 
/* 684 */         if ((properties != null) && (!properties.isEmpty())) {
/* 685 */           setProperties(properties, jmsMessage);
/*     */         }
/* 687 */         send(endpoint.getDestination(this.m_session), jmsMessage, deliveryMode, priority, timeToLive);
/*     */ 
/* 689 */         BytesMessage response = null;
/*     */         try {
/* 691 */           response = (BytesMessage)subscriber.receive(timeout);
/*     */         } catch (ClassCastException cce) {
/* 693 */           throw new InvokeException("Error: unexpected message type received - expected BytesMessage");
/*     */         }
/*     */ 
/* 696 */         byte[] respBytes = null;
/* 697 */         if (response != null)
/*     */         {
/* 699 */           byte[] buffer = new byte[8192];
/* 700 */           ByteArrayOutputStream out = new ByteArrayOutputStream();
/* 701 */           int bytesRead = response.readBytes(buffer);
/* 702 */           for (; bytesRead != -1; bytesRead = response.readBytes(buffer))
/*     */           {
/* 704 */             out.write(buffer, 0, bytesRead);
/*     */           }
/* 706 */           respBytes = out.toByteArray();
/*     */         }
/* 708 */         subscriber.close();
/* 709 */         deleteTemporaryDestination(reply);
/* 710 */         return respBytes;
/*     */       }
/*     */ 
/*     */       private int extractPriority(HashMap properties)
/*     */       {
/* 715 */         return MapUtils.removeIntProperty(properties, "transport.jms.priority", 4);
/*     */       }
/*     */ 
/*     */       private int extractDeliveryMode(HashMap properties)
/*     */       {
/* 721 */         return MapUtils.removeIntProperty(properties, "transport.jms.deliveryMode", 1);
/*     */       }
/*     */ 
/*     */       private long extractTimeToLive(HashMap properties)
/*     */       {
/* 727 */         return MapUtils.removeLongProperty(properties, "transport.jms.ttl", 0L);
/*     */       }
/*     */ 
/*     */       private void setProperties(HashMap properties, Message message)
/*     */         throws JMSException
/*     */       {
/* 734 */         Iterator propertyIter = properties.entrySet().iterator();
/* 735 */         while (propertyIter.hasNext())
/*     */         {
/* 737 */           Map.Entry property = (Map.Entry)propertyIter.next();
/* 738 */           setProperty((String)property.getKey(), property.getValue(), message);
/*     */         }
/*     */       }
/*     */ 
/*     */       private void setProperty(String property, Object value, Message message)
/*     */         throws JMSException
/*     */       {
/* 746 */         if (property == null)
/* 747 */           return;
/* 748 */         if (property.equals("transport.jms.jmsCorrelationID"))
/* 749 */           message.setJMSCorrelationID((String)value);
/* 750 */         else if (property.equals("transport.jms.jmsCorrelationIDAsBytes"))
/* 751 */           message.setJMSCorrelationIDAsBytes((byte[])value);
/* 752 */         else if (property.equals("transport.jms.jmsType"))
/* 753 */           message.setJMSType((String)value);
/*     */         else
/* 755 */           message.setObjectProperty(property, value);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private abstract class Connection extends Thread
/*     */     implements ExceptionListener
/*     */   {
/*     */     private ConnectionFactory m_connectionFactory;
/*     */     protected Connection m_connection;
/*     */     protected boolean m_isActive;
/*     */     private boolean m_needsToConnect;
/*     */     private boolean m_startConnection;
/*     */     private String m_clientID;
/*     */     private String m_username;
/*     */     private String m_password;
/*     */     private Object m_jmsLock;
/*     */     private Object m_lifecycleLock;
/*     */ 
/*     */     protected Connection(ConnectionFactory connectionFactory, Connection connection, String threadName, String clientID, String username, String password)
/*     */       throws JMSException
/*     */     {
/* 247 */       super();
/* 248 */       this.m_connectionFactory = connectionFactory;
/*     */ 
/* 250 */       this.m_clientID = clientID;
/* 251 */       this.m_username = username;
/* 252 */       this.m_password = password;
/*     */ 
/* 254 */       this.m_jmsLock = new Object();
/* 255 */       this.m_lifecycleLock = new Object();
/*     */ 
/* 257 */       if (connection != null)
/*     */       {
/* 259 */         this.m_needsToConnect = false;
/* 260 */         this.m_connection = connection;
/* 261 */         this.m_connection.setExceptionListener(this);
/* 262 */         if (this.m_clientID != null)
/* 263 */           this.m_connection.setClientID(this.m_clientID);
/*     */       }
/*     */       else
/*     */       {
/* 267 */         this.m_needsToConnect = true;
/*     */       }
/*     */ 
/* 270 */       this.m_isActive = true;
/*     */     }
/*     */ 
/*     */     public ConnectionFactory getConnectionFactory()
/*     */     {
/* 275 */       return this.m_connectionFactory;
/*     */     }
/*     */ 
/*     */     public String getClientID()
/*     */     {
/* 280 */       return this.m_clientID;
/*     */     }
/*     */ 
/*     */     public String getUsername() {
/* 284 */       return this.m_username;
/*     */     }
/*     */ 
/*     */     public String getPassword() {
/* 288 */       return this.m_password;
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 299 */       while (this.m_isActive)
/*     */       {
/* 301 */         if (this.m_needsToConnect)
/*     */         {
/* 303 */           this.m_connection = null;
/*     */           try
/*     */           {
/* 306 */             this.m_connection = JMSConnector.this.internalConnect(this.m_connectionFactory, this.m_username, this.m_password);
/*     */ 
/* 308 */             this.m_connection.setExceptionListener(this);
/* 309 */             if (this.m_clientID != null)
/* 310 */               this.m_connection.setClientID(this.m_clientID);
/*     */           }
/*     */           catch (JMSException e)
/*     */           {
/*     */             try {
/* 315 */               Thread.sleep(JMSConnector.this.m_connectRetryInterval); } catch (InterruptedException ie) {
/*     */             }
/* 316 */           }continue;
/*     */         }
/*     */         else
/*     */         {
/* 320 */           this.m_needsToConnect = true;
/*     */         }
/*     */ 
/*     */         try
/*     */         {
/* 326 */           internalOnConnect();
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/*     */         }
/*     */ 
/* 332 */         continue;
/*     */ 
/* 335 */         synchronized (this.m_jmsLock) {
/*     */           try {
/* 337 */             this.m_jmsLock.wait();
/*     */           } catch (InterruptedException ie) {
/*     */           }
/*     */         }
/*     */       }
/* 342 */       internalOnShutdown();
/*     */     }
/*     */ 
/*     */     void startConnection()
/*     */     {
/* 349 */       synchronized (this.m_lifecycleLock)
/*     */       {
/* 351 */         if (this.m_startConnection)
/* 352 */           return;
/* 353 */         this.m_startConnection = true;
/*     */         try { this.m_connection.start(); } catch (Throwable e) {
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     void stopConnection() {
/* 360 */       synchronized (this.m_lifecycleLock)
/*     */       {
/* 362 */         if (!this.m_startConnection)
/* 363 */           return;
/* 364 */         this.m_startConnection = false;
/*     */         try { this.m_connection.stop(); } catch (Throwable e) {
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     void shutdown() {
/* 371 */       this.m_isActive = false;
/* 372 */       synchronized (this.m_jmsLock)
/*     */       {
/* 374 */         this.m_jmsLock.notifyAll();
/*     */       }
/*     */     }
/*     */ 
/*     */     public void onException(JMSException exception)
/*     */     {
/* 382 */       if (JMSConnector.this.m_adapter.isRecoverable(exception, 4))
/*     */       {
/* 384 */         return;
/* 385 */       }onException();
/* 386 */       synchronized (this.m_jmsLock)
/*     */       {
/* 388 */         this.m_jmsLock.notifyAll();
/*     */       }
/*     */     }
/*     */ 
/*     */     private final void internalOnConnect()
/*     */       throws Exception
/*     */     {
/* 395 */       onConnect();
/* 396 */       synchronized (this.m_lifecycleLock)
/*     */       {
/* 398 */         if (this.m_startConnection)
/*     */           try {
/* 400 */             this.m_connection.start();
/*     */           } catch (Throwable e) {
/*     */           }
/*     */       }
/*     */     }
/*     */ 
/*     */     private final void internalOnShutdown() {
/* 407 */       stopConnection();
/* 408 */       onShutdown();
/*     */       try { this.m_connection.close();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*     */       }
/*     */     }
/*     */ 
/*     */     protected abstract void onConnect()
/*     */       throws Exception;
/*     */ 
/*     */     protected abstract void onShutdown();
/*     */ 
/*     */     protected abstract void onException();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.jms.JMSConnector
 * JD-Core Version:    0.6.0
 */