/*     */ package org.apache.axis.components.jms;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ import javax.jms.ConnectionFactory;
/*     */ import javax.jms.InvalidDestinationException;
/*     */ import javax.jms.JMSException;
/*     */ import javax.jms.JMSSecurityException;
/*     */ import javax.jms.Message;
/*     */ import javax.jms.Queue;
/*     */ import javax.jms.QueueConnectionFactory;
/*     */ import javax.jms.QueueSession;
/*     */ import javax.jms.Topic;
/*     */ import javax.jms.TopicConnectionFactory;
/*     */ import javax.jms.TopicSession;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.client.Call;
/*     */ import org.apache.axis.transport.jms.JMSURLHelper;
/*     */ 
/*     */ public abstract class JMSVendorAdapter
/*     */ {
/*     */   public static final int SEND_ACTION = 0;
/*     */   public static final int CONNECT_ACTION = 1;
/*     */   public static final int SUBSCRIBE_ACTION = 2;
/*     */   public static final int RECEIVE_ACTION = 3;
/*     */   public static final int ON_EXCEPTION_ACTION = 4;
/*     */ 
/*     */   public abstract QueueConnectionFactory getQueueConnectionFactory(HashMap paramHashMap)
/*     */     throws Exception;
/*     */ 
/*     */   public abstract TopicConnectionFactory getTopicConnectionFactory(HashMap paramHashMap)
/*     */     throws Exception;
/*     */ 
/*     */   public abstract void addVendorConnectionFactoryProperties(JMSURLHelper paramJMSURLHelper, HashMap paramHashMap);
/*     */ 
/*     */   public abstract boolean isMatchingConnectionFactory(ConnectionFactory paramConnectionFactory, JMSURLHelper paramJMSURLHelper, HashMap paramHashMap);
/*     */ 
/*     */   public String getVendorId()
/*     */   {
/*  68 */     String name = getClass().getName();
/*     */ 
/*  71 */     if (name.endsWith("VendorAdapter"))
/*     */     {
/*  73 */       int index = name.lastIndexOf("VendorAdapter");
/*  74 */       name = name.substring(0, index);
/*     */     }
/*     */ 
/*  78 */     int index = name.lastIndexOf(".");
/*  79 */     if (index > 0) {
/*  80 */       name = name.substring(index + 1);
/*     */     }
/*  82 */     return name;
/*     */   }
/*     */ 
/*     */   public HashMap getJMSConnectorProperties(JMSURLHelper jmsurl)
/*     */   {
/*  95 */     HashMap connectorProps = new HashMap();
/*     */ 
/*  98 */     connectorProps.put("transport.jms.EndpointAddress", jmsurl);
/*     */ 
/* 101 */     String clientID = jmsurl.getPropertyValue("clientID");
/* 102 */     if (clientID != null) {
/* 103 */       connectorProps.put("transport.jms.clientID", clientID);
/*     */     }
/*     */ 
/* 106 */     String connectRetryInterval = jmsurl.getPropertyValue("connectRetryInterval");
/* 107 */     if (connectRetryInterval != null) {
/* 108 */       connectorProps.put("transport.jms.connectRetryInterval", connectRetryInterval);
/*     */     }
/*     */ 
/* 111 */     String interactRetryInterval = jmsurl.getPropertyValue("interactRetryInterval");
/* 112 */     if (interactRetryInterval != null) {
/* 113 */       connectorProps.put("transport.jms.interactRetryInterval", interactRetryInterval);
/*     */     }
/*     */ 
/* 116 */     String domain = jmsurl.getPropertyValue("domain");
/* 117 */     if (domain != null) {
/* 118 */       connectorProps.put("transport.jms.domain", domain);
/*     */     }
/*     */ 
/* 121 */     String numRetries = jmsurl.getPropertyValue("numRetries");
/* 122 */     if (numRetries != null) {
/* 123 */       connectorProps.put("transport.jms.numRetries", numRetries);
/*     */     }
/*     */ 
/* 126 */     String numSessions = jmsurl.getPropertyValue("numSessions");
/* 127 */     if (numSessions != null) {
/* 128 */       connectorProps.put("transport.jms.numSessions", numSessions);
/*     */     }
/*     */ 
/* 131 */     String timeoutTime = jmsurl.getPropertyValue("timeoutTime");
/* 132 */     if (timeoutTime != null) {
/* 133 */       connectorProps.put("transport.jms.timeoutTime", timeoutTime);
/*     */     }
/* 135 */     return connectorProps;
/*     */   }
/*     */ 
/*     */   public HashMap getJMSConnectionFactoryProperties(JMSURLHelper jmsurl)
/*     */   {
/* 148 */     HashMap cfProps = new HashMap();
/*     */ 
/* 152 */     cfProps.put("transport.jms.EndpointAddress", jmsurl);
/*     */ 
/* 155 */     String domain = jmsurl.getPropertyValue("domain");
/* 156 */     if (domain != null) {
/* 157 */       cfProps.put("transport.jms.domain", domain);
/*     */     }
/*     */ 
/* 160 */     addVendorConnectionFactoryProperties(jmsurl, cfProps);
/*     */ 
/* 162 */     return cfProps;
/*     */   }
/*     */ 
/*     */   public Queue getQueue(QueueSession session, String name)
/*     */     throws Exception
/*     */   {
/* 168 */     return session.createQueue(name);
/*     */   }
/*     */ 
/*     */   public Topic getTopic(TopicSession session, String name)
/*     */     throws Exception
/*     */   {
/* 174 */     return session.createTopic(name);
/*     */   }
/*     */ 
/*     */   public boolean isRecoverable(Throwable thrown, int action)
/*     */   {
/* 179 */     if (((thrown instanceof RuntimeException)) || ((thrown instanceof Error)) || ((thrown instanceof JMSSecurityException)) || ((thrown instanceof InvalidDestinationException)))
/*     */     {
/* 183 */       return false;
/*     */     }
/* 185 */     return action != 4;
/*     */   }
/*     */ 
/*     */   public void setProperties(Message message, HashMap props)
/*     */     throws JMSException
/*     */   {
/* 192 */     Iterator iter = props.keySet().iterator();
/* 193 */     while (iter.hasNext())
/*     */     {
/* 195 */       String key = (String)iter.next();
/* 196 */       String value = (String)props.get(key);
/*     */ 
/* 198 */       message.setStringProperty(key, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setupMessageContext(MessageContext context, Call call, JMSURLHelper jmsurl)
/*     */   {
/* 212 */     Object tmp = null;
/*     */ 
/* 214 */     String jmsurlDestination = null;
/* 215 */     if (jmsurl != null)
/* 216 */       jmsurlDestination = jmsurl.getDestination();
/* 217 */     if (jmsurlDestination != null) {
/* 218 */       context.setProperty("transport.jms.Destination", jmsurlDestination);
/*     */     }
/*     */     else {
/* 221 */       tmp = call.getProperty("transport.jms.Destination");
/* 222 */       if ((tmp != null) && ((tmp instanceof String)))
/* 223 */         context.setProperty("transport.jms.Destination", tmp);
/*     */       else {
/* 225 */         context.removeProperty("transport.jms.Destination");
/*     */       }
/*     */     }
/* 228 */     String delivMode = null;
/* 229 */     if (jmsurl != null)
/* 230 */       delivMode = jmsurl.getPropertyValue("deliveryMode");
/* 231 */     if (delivMode != null)
/*     */     {
/* 233 */       int mode = 1;
/* 234 */       if (delivMode.equalsIgnoreCase("Persistent"))
/* 235 */         mode = 2;
/* 236 */       else if (delivMode.equalsIgnoreCase("Nonpersistent"))
/* 237 */         mode = 1;
/* 238 */       context.setProperty("transport.jms.deliveryMode", new Integer(mode));
/*     */     }
/*     */     else
/*     */     {
/* 242 */       tmp = call.getProperty("transport.jms.deliveryMode");
/* 243 */       if ((tmp != null) && ((tmp instanceof Integer)))
/* 244 */         context.setProperty("transport.jms.deliveryMode", tmp);
/*     */       else {
/* 246 */         context.removeProperty("transport.jms.deliveryMode");
/*     */       }
/*     */     }
/* 249 */     String prio = null;
/* 250 */     if (jmsurl != null)
/* 251 */       prio = jmsurl.getPropertyValue("priority");
/* 252 */     if (prio != null) {
/* 253 */       context.setProperty("transport.jms.priority", Integer.valueOf(prio));
/*     */     }
/*     */     else {
/* 256 */       tmp = call.getProperty("transport.jms.priority");
/* 257 */       if ((tmp != null) && ((tmp instanceof Integer)))
/* 258 */         context.setProperty("transport.jms.priority", tmp);
/*     */       else {
/* 260 */         context.removeProperty("transport.jms.priority");
/*     */       }
/*     */     }
/* 263 */     String ttl = null;
/* 264 */     if (jmsurl != null)
/* 265 */       ttl = jmsurl.getPropertyValue("ttl");
/* 266 */     if (ttl != null) {
/* 267 */       context.setProperty("transport.jms.ttl", Long.valueOf(ttl));
/*     */     }
/*     */     else {
/* 270 */       tmp = call.getProperty("transport.jms.ttl");
/* 271 */       if ((tmp != null) && ((tmp instanceof Long)))
/* 272 */         context.setProperty("transport.jms.ttl", tmp);
/*     */       else {
/* 274 */         context.removeProperty("transport.jms.ttl");
/*     */       }
/*     */     }
/* 277 */     String wait = null;
/* 278 */     if (jmsurl != null)
/* 279 */       wait = jmsurl.getPropertyValue("waitForResponse");
/* 280 */     if (wait != null) {
/* 281 */       context.setProperty("transport.jms.waitForResponse", Boolean.valueOf(wait));
/*     */     }
/*     */     else {
/* 284 */       tmp = call.getProperty("transport.jms.waitForResponse");
/* 285 */       if ((tmp != null) && ((tmp instanceof Boolean)))
/* 286 */         context.setProperty("transport.jms.waitForResponse", tmp);
/*     */       else
/* 288 */         context.removeProperty("transport.jms.waitForResponse");
/*     */     }
/* 290 */     setupApplicationProperties(context, call, jmsurl);
/*     */   }
/*     */ 
/*     */   public void setupApplicationProperties(MessageContext context, Call call, JMSURLHelper jmsurl)
/*     */   {
/* 298 */     Map appProps = new HashMap();
/* 299 */     if ((jmsurl != null) && (jmsurl.getApplicationProperties() != null)) {
/* 300 */       Iterator itr = jmsurl.getApplicationProperties().iterator();
/* 301 */       while (itr.hasNext()) {
/* 302 */         String name = (String)itr.next();
/* 303 */         appProps.put(name, jmsurl.getPropertyValue(name));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 308 */     Map ctxProps = (Map)context.getProperty("transport.jms.msgProps");
/*     */ 
/* 310 */     if (ctxProps != null) {
/* 311 */       appProps.putAll(ctxProps);
/*     */     }
/*     */ 
/* 315 */     Map callProps = (Map)call.getProperty("transport.jms.msgProps");
/*     */ 
/* 317 */     if (callProps != null) {
/* 318 */       appProps.putAll(callProps);
/*     */     }
/*     */ 
/* 322 */     context.setProperty("transport.jms.msgProps", appProps);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.jms.JMSVendorAdapter
 * JD-Core Version:    0.6.0
 */