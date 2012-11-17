/*     */ package org.apache.axis.components.jms;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import javax.jms.ConnectionFactory;
/*     */ import javax.jms.Queue;
/*     */ import javax.jms.QueueConnectionFactory;
/*     */ import javax.jms.QueueSession;
/*     */ import javax.jms.Topic;
/*     */ import javax.jms.TopicConnectionFactory;
/*     */ import javax.jms.TopicSession;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.InitialContext;
/*     */ import org.apache.axis.transport.jms.JMSURLHelper;
/*     */ 
/*     */ public class JNDIVendorAdapter extends JMSVendorAdapter
/*     */ {
/*     */   public static final String CONTEXT_FACTORY = "java.naming.factory.initial";
/*     */   public static final String PROVIDER_URL = "java.naming.provider.url";
/*     */   public static final String _CONNECTION_FACTORY_JNDI_NAME = "ConnectionFactoryJNDIName";
/*     */   public static final String CONNECTION_FACTORY_JNDI_NAME = "transport.jms.ConnectionFactoryJNDIName";
/*     */   private Context context;
/*     */ 
/*     */   public QueueConnectionFactory getQueueConnectionFactory(HashMap cfConfig)
/*     */     throws Exception
/*     */   {
/*  55 */     return (QueueConnectionFactory)getConnectionFactory(cfConfig);
/*     */   }
/*     */ 
/*     */   public TopicConnectionFactory getTopicConnectionFactory(HashMap cfConfig)
/*     */     throws Exception
/*     */   {
/*  61 */     return (TopicConnectionFactory)getConnectionFactory(cfConfig);
/*     */   }
/*     */ 
/*     */   private ConnectionFactory getConnectionFactory(HashMap cfProps)
/*     */     throws Exception
/*     */   {
/*  67 */     if (cfProps == null)
/*  68 */       throw new IllegalArgumentException("noCFProps");
/*  69 */     String jndiName = (String)cfProps.get("transport.jms.ConnectionFactoryJNDIName");
/*  70 */     if ((jndiName == null) || (jndiName.trim().length() == 0)) {
/*  71 */       throw new IllegalArgumentException("noCFName");
/*     */     }
/*  73 */     Hashtable environment = new Hashtable(cfProps);
/*     */ 
/*  76 */     String ctxFactory = (String)cfProps.get("java.naming.factory.initial");
/*  77 */     if (ctxFactory != null) {
/*  78 */       environment.put("java.naming.factory.initial", ctxFactory);
/*     */     }
/*     */ 
/*  81 */     String providerURL = (String)cfProps.get("java.naming.provider.url");
/*  82 */     if (providerURL != null) {
/*  83 */       environment.put("java.naming.provider.url", providerURL);
/*     */     }
/*  85 */     this.context = new InitialContext(environment);
/*     */ 
/*  87 */     return (ConnectionFactory)this.context.lookup(jndiName);
/*     */   }
/*     */ 
/*     */   public void addVendorConnectionFactoryProperties(JMSURLHelper jmsurl, HashMap cfConfig)
/*     */   {
/* 101 */     String cfJNDIName = jmsurl.getPropertyValue("ConnectionFactoryJNDIName");
/* 102 */     if (cfJNDIName != null) {
/* 103 */       cfConfig.put("transport.jms.ConnectionFactoryJNDIName", cfJNDIName);
/*     */     }
/*     */ 
/* 106 */     String ctxFactory = jmsurl.getPropertyValue("java.naming.factory.initial");
/* 107 */     if (ctxFactory != null) {
/* 108 */       cfConfig.put("java.naming.factory.initial", ctxFactory);
/*     */     }
/*     */ 
/* 111 */     String providerURL = jmsurl.getPropertyValue("java.naming.provider.url");
/* 112 */     if (providerURL != null)
/* 113 */       cfConfig.put("java.naming.provider.url", providerURL);
/*     */   }
/*     */ 
/*     */   public boolean isMatchingConnectionFactory(ConnectionFactory cf, JMSURLHelper originalJMSURL, HashMap cfProps)
/*     */   {
/* 129 */     JMSURLHelper jmsurl = (JMSURLHelper)cfProps.get("transport.jms.EndpointAddress");
/*     */ 
/* 132 */     String cfJndiName = jmsurl.getPropertyValue("ConnectionFactoryJNDIName");
/* 133 */     String originalCfJndiName = originalJMSURL.getPropertyValue("ConnectionFactoryJNDIName");
/*     */ 
/* 136 */     return cfJndiName.equalsIgnoreCase(originalCfJndiName);
/*     */   }
/*     */ 
/*     */   public Queue getQueue(QueueSession session, String name)
/*     */     throws Exception
/*     */   {
/* 144 */     return (Queue)this.context.lookup(name);
/*     */   }
/*     */ 
/*     */   public Topic getTopic(TopicSession session, String name)
/*     */     throws Exception
/*     */   {
/* 150 */     return (Topic)this.context.lookup(name);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.jms.JNDIVendorAdapter
 * JD-Core Version:    0.6.0
 */