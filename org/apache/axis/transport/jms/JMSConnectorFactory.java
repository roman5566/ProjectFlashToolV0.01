/*     */ package org.apache.axis.transport.jms;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import org.apache.axis.components.jms.JMSVendorAdapter;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public abstract class JMSConnectorFactory
/*     */ {
/*  41 */   protected static Log log = LogFactory.getLog(JMSConnectorFactory.class.getName());
/*     */ 
/*     */   public static JMSConnector matchConnector(Set connectors, HashMap connectorProps, HashMap cfProps, String username, String password, JMSVendorAdapter adapter)
/*     */   {
/*  63 */     Iterator iter = connectors.iterator();
/*  64 */     while (iter.hasNext())
/*     */     {
/*  66 */       JMSConnector conn = (JMSConnector)iter.next();
/*     */ 
/*  69 */       String connectorUsername = conn.getUsername();
/*  70 */       if (((connectorUsername != null) || (username != null)) && ((connectorUsername == null) || (username == null) || (!connectorUsername.equals(username))))
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/*  75 */       String connectorPassword = conn.getPassword();
/*  76 */       if (((connectorPassword != null) || (password != null)) && ((connectorPassword == null) || (password == null) || (!connectorPassword.equals(password))))
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/*  81 */       int connectorNumRetries = conn.getNumRetries();
/*  82 */       String propertyNumRetries = (String)connectorProps.get("transport.jms.numRetries");
/*  83 */       int numRetries = 5;
/*  84 */       if (propertyNumRetries != null)
/*  85 */         numRetries = Integer.parseInt(propertyNumRetries);
/*  86 */       if (connectorNumRetries != numRetries)
/*     */       {
/*     */         continue;
/*     */       }
/*  90 */       String connectorClientID = conn.getClientID();
/*  91 */       String clientID = (String)connectorProps.get("transport.jms.clientID");
/*  92 */       if (((connectorClientID != null) || (clientID != null)) && ((connectorClientID == null) || (clientID == null) || (!connectorClientID.equals(clientID))))
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/*  98 */       String connectorDomain = (conn instanceof QueueConnector) ? "QUEUE" : "TOPIC";
/*  99 */       String propertyDomain = (String)connectorProps.get("transport.jms.domain");
/* 100 */       String domain = "QUEUE";
/* 101 */       if (propertyDomain != null)
/* 102 */         domain = propertyDomain;
/* 103 */       if (((connectorDomain != null) || (domain != null)) && ((connectorDomain == null) || (domain == null) || (!connectorDomain.equalsIgnoreCase(domain))))
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/* 109 */       JMSURLHelper jmsurl = conn.getJMSURL();
/* 110 */       if (adapter.isMatchingConnectionFactory(conn.getConnectionFactory(), jmsurl, cfProps))
/*     */       {
/*     */         try
/*     */         {
/* 115 */           JMSConnectorManager.getInstance().reserve(conn);
/*     */ 
/* 117 */           if (log.isDebugEnabled()) {
/* 118 */             log.debug("JMSConnectorFactory: Found matching connector");
/*     */           }
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/*     */         }
/* 124 */         continue;
/*     */ 
/* 127 */         return conn;
/*     */       }
/*     */     }
/*     */ 
/* 131 */     if (log.isDebugEnabled()) {
/* 132 */       log.debug("JMSConnectorFactory: No matching connectors found");
/*     */     }
/*     */ 
/* 135 */     return null;
/*     */   }
/*     */ 
/*     */   public static JMSConnector createServerConnector(HashMap connectorConfig, HashMap cfConfig, String username, String password, JMSVendorAdapter adapter)
/*     */     throws Exception
/*     */   {
/* 156 */     return createConnector(connectorConfig, cfConfig, true, username, password, adapter);
/*     */   }
/*     */ 
/*     */   public static JMSConnector createClientConnector(HashMap connectorConfig, HashMap cfConfig, String username, String password, JMSVendorAdapter adapter)
/*     */     throws Exception
/*     */   {
/* 178 */     return createConnector(connectorConfig, cfConfig, false, username, password, adapter);
/*     */   }
/*     */ 
/*     */   private static JMSConnector createConnector(HashMap connectorConfig, HashMap cfConfig, boolean allowReceive, String username, String password, JMSVendorAdapter adapter)
/*     */     throws Exception
/*     */   {
/* 190 */     if (connectorConfig != null)
/* 191 */       connectorConfig = (HashMap)connectorConfig.clone();
/* 192 */     int numRetries = MapUtils.removeIntProperty(connectorConfig, "transport.jms.numRetries", 5);
/*     */ 
/* 196 */     int numSessions = MapUtils.removeIntProperty(connectorConfig, "transport.jms.numSessions", 5);
/*     */ 
/* 200 */     long connectRetryInterval = MapUtils.removeLongProperty(connectorConfig, "transport.jms.connectRetryInterval", 2000L);
/*     */ 
/* 204 */     long interactRetryInterval = MapUtils.removeLongProperty(connectorConfig, "transport.jms.interactRetryInterval", 250L);
/*     */ 
/* 208 */     long timeoutTime = MapUtils.removeLongProperty(connectorConfig, "transport.jms.timeoutTime", 5000L);
/*     */ 
/* 212 */     String clientID = MapUtils.removeStringProperty(connectorConfig, "transport.jms.clientID", null);
/*     */ 
/* 215 */     String domain = MapUtils.removeStringProperty(connectorConfig, "transport.jms.domain", "QUEUE");
/*     */ 
/* 220 */     JMSURLHelper jmsurl = (JMSURLHelper)connectorConfig.get("transport.jms.EndpointAddress");
/*     */ 
/* 222 */     if (cfConfig == null) {
/* 223 */       throw new IllegalArgumentException("noCfConfig");
/*     */     }
/* 225 */     if (domain.equals("QUEUE"))
/*     */     {
/* 227 */       return new QueueConnector(adapter.getQueueConnectionFactory(cfConfig), numRetries, numSessions, connectRetryInterval, interactRetryInterval, timeoutTime, allowReceive, clientID, username, password, adapter, jmsurl);
/*     */     }
/*     */ 
/* 235 */     return new TopicConnector(adapter.getTopicConnectionFactory(cfConfig), numRetries, numSessions, connectRetryInterval, interactRetryInterval, timeoutTime, allowReceive, clientID, username, password, adapter, jmsurl);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.jms.JMSConnectorFactory
 * JD-Core Version:    0.6.0
 */