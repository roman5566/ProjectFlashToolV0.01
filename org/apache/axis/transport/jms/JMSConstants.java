package org.apache.axis.transport.jms;

public abstract interface JMSConstants
{
  public static final String PROTOCOL = "jms";
  public static final String _WAIT_FOR_RESPONSE = "waitForResponse";
  public static final String _CLIENT_ID = "clientID";
  public static final String _VENDOR = "vendor";
  public static final String _DOMAIN = "domain";
  public static final String _JMS_CORRELATION_ID = "jmsCorrelationID";
  public static final String _JMS_CORRELATION_ID_AS_BYTES = "jmsCorrelationIDAsBytes";
  public static final String _JMS_TYPE = "jmsType";
  public static final String _TIME_TO_LIVE = "ttl";
  public static final String _PRIORITY = "priority";
  public static final String _DELIVERY_MODE = "deliveryMode";
  public static final String _MESSAGE_SELECTOR = "messageSelector";
  public static final String _ACKNOWLEDGE_MODE = "acknowledgeMode";
  public static final String _SUBSCRIPTION_NAME = "subscriptionName";
  public static final String _UNSUBSCRIBE = "unsubscribe";
  public static final String _NO_LOCAL = "noLocal";
  public static final String _NUM_RETRIES = "numRetries";
  public static final String _NUM_SESSIONS = "numSessions";
  public static final String _CONNECT_RETRY_INTERVAL = "connectRetryInterval";
  public static final String _INTERACT_RETRY_INTERVAL = "interactRetryInterval";
  public static final String _TIMEOUT_TIME = "timeoutTime";
  public static final String _MIN_TIMEOUT_TIME = "minTimeoutTime";
  public static final String _MSG_PROP_PREFIX = "msgProp.";
  public static final String JMS_PROPERTY_PREFIX = "transport.jms.";
  public static final String WAIT_FOR_RESPONSE = "transport.jms.waitForResponse";
  public static final String CLIENT_ID = "transport.jms.clientID";
  public static final String DESTINATION = "transport.jms.Destination";
  public static final String VENDOR = "transport.jms.vendor";
  public static final String JNDI_VENDOR_ID = "JNDI";
  public static final String DOMAIN = "transport.jms.domain";
  public static final String DOMAIN_QUEUE = "QUEUE";
  public static final String DOMAIN_TOPIC = "TOPIC";
  public static final String DOMAIN_DEFAULT = "QUEUE";
  public static final String JMS_CORRELATION_ID = "transport.jms.jmsCorrelationID";
  public static final String JMS_CORRELATION_ID_AS_BYTES = "transport.jms.jmsCorrelationIDAsBytes";
  public static final String JMS_TYPE = "transport.jms.jmsType";
  public static final String TIME_TO_LIVE = "transport.jms.ttl";
  public static final String PRIORITY = "transport.jms.priority";
  public static final String DELIVERY_MODE = "transport.jms.deliveryMode";
  public static final String DELIVERY_MODE_PERSISTENT = "Persistent";
  public static final String DELIVERY_MODE_NONPERSISTENT = "Nonpersistent";
  public static final String DELIVERY_MODE_DISCARDABLE = "Discardable";
  public static final int DEFAULT_DELIVERY_MODE = 1;
  public static final int DEFAULT_PRIORITY = 4;
  public static final long DEFAULT_TIME_TO_LIVE = 0L;
  public static final String MESSAGE_SELECTOR = "transport.jms.messageSelector";
  public static final String ACKNOWLEDGE_MODE = "transport.jms.acknowledgeMode";
  public static final int DEFAULT_ACKNOWLEDGE_MODE = 3;
  public static final String SUBSCRIPTION_NAME = "transport.jms.subscriptionName";
  public static final String UNSUBSCRIBE = "transport.jms.unsubscribe";
  public static final String NO_LOCAL = "transport.jms.noLocal";
  public static final boolean DEFAULT_NO_LOCAL = false;
  public static final boolean DEFAULT_UNSUBSCRIBE = false;
  public static final String NUM_RETRIES = "transport.jms.numRetries";
  public static final String NUM_SESSIONS = "transport.jms.numSessions";
  public static final String CONNECT_RETRY_INTERVAL = "transport.jms.connectRetryInterval";
  public static final String INTERACT_RETRY_INTERVAL = "transport.jms.interactRetryInterval";
  public static final String TIMEOUT_TIME = "transport.jms.timeoutTime";
  public static final String MIN_TIMEOUT_TIME = "transport.jms.minTimeoutTime";
  public static final int DEFAULT_NUM_RETRIES = 5;
  public static final int DEFAULT_NUM_SESSIONS = 5;
  public static final long DEFAULT_CONNECT_RETRY_INTERVAL = 2000L;
  public static final long DEFAULT_TIMEOUT_TIME = 5000L;
  public static final long DEFAULT_MIN_TIMEOUT_TIME = 1000L;
  public static final long DEFAULT_INTERACT_RETRY_INTERVAL = 250L;
  public static final String CONNECTOR = "transport.jms.Connector";
  public static final String VENDOR_ADAPTER = "transport.jms.VendorAdapter";
  public static final String JMS_URL = "transport.jms.EndpointAddress";
  public static final String JMS_APPLICATION_MSG_PROPS = "transport.jms.msgProps";
  public static final String ADAPTER_POSTFIX = "VendorAdapter";
}

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.jms.JMSConstants
 * JD-Core Version:    0.6.0
 */