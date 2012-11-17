/*     */ package org.apache.axis.transport.jms;
/*     */ 
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.HashMap;
/*     */ import org.apache.axis.AxisEngine;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.client.Call;
/*     */ import org.apache.axis.client.Transport;
/*     */ import org.apache.axis.components.jms.JMSVendorAdapter;
/*     */ import org.apache.axis.components.jms.JMSVendorAdapterFactory;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class JMSTransport extends Transport
/*     */ {
/*  49 */   protected static Log log = LogFactory.getLog(JMSTransport.class.getName());
/*     */ 
/*  52 */   private static HashMap vendorConnectorPools = new HashMap();
/*     */   private HashMap defaultConnectorProps;
/*     */   private HashMap defaultConnectionFactoryProps;
/*     */ 
/*     */   public JMSTransport()
/*     */   {
/*  73 */     this.transportName = "JMSTransport";
/*     */   }
/*     */ 
/*     */   public JMSTransport(HashMap connectorProps, HashMap connectionFactoryProps)
/*     */   {
/*  80 */     this();
/*  81 */     this.defaultConnectorProps = connectorProps;
/*  82 */     this.defaultConnectionFactoryProps = connectionFactoryProps;
/*     */   }
/*     */ 
/*     */   public void setupMessageContextImpl(MessageContext context, Call message, AxisEngine engine)
/*     */     throws AxisFault
/*     */   {
/*  97 */     if (log.isDebugEnabled()) {
/*  98 */       log.debug("Enter: JMSTransport::setupMessageContextImpl");
/*     */     }
/*     */ 
/* 101 */     JMSConnector connector = null;
/* 102 */     HashMap connectorProperties = null;
/* 103 */     HashMap connectionFactoryProperties = null;
/*     */ 
/* 105 */     JMSVendorAdapter vendorAdapter = null;
/* 106 */     JMSURLHelper jmsurl = null;
/*     */ 
/* 109 */     String username = message.getUsername();
/* 110 */     String password = message.getPassword();
/*     */ 
/* 114 */     String endpointAddr = message.getTargetEndpointAddress();
/* 115 */     if (endpointAddr != null)
/*     */     {
/*     */       try
/*     */       {
/* 120 */         jmsurl = new JMSURLHelper(new URL(endpointAddr));
/*     */ 
/* 123 */         String vendorId = jmsurl.getVendor();
/* 124 */         if (vendorId == null) {
/* 125 */           vendorId = "JNDI";
/*     */         }
/* 127 */         if (log.isDebugEnabled()) {
/* 128 */           log.debug("JMSTransport.setupMessageContextImpl(): endpt=" + endpointAddr + ", vendor=" + vendorId);
/*     */         }
/*     */ 
/* 131 */         vendorAdapter = JMSVendorAdapterFactory.getJMSVendorAdapter(vendorId);
/* 132 */         if (vendorAdapter == null)
/*     */         {
/* 134 */           throw new AxisFault("cannotLoadAdapterClass:" + vendorId);
/*     */         }
/*     */ 
/* 138 */         connectorProperties = vendorAdapter.getJMSConnectorProperties(jmsurl);
/* 139 */         connectionFactoryProperties = vendorAdapter.getJMSConnectionFactoryProperties(jmsurl);
/*     */       }
/*     */       catch (MalformedURLException e)
/*     */       {
/* 143 */         log.error(Messages.getMessage("malformedURLException00"), e);
/* 144 */         throw new AxisFault(Messages.getMessage("malformedURLException00"), e);
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 150 */       vendorAdapter = JMSVendorAdapterFactory.getJMSVendorAdapter();
/* 151 */       if (vendorAdapter == null)
/*     */       {
/* 153 */         throw new AxisFault("cannotLoadAdapterClass");
/*     */       }
/*     */ 
/* 157 */       connectorProperties = this.defaultConnectorProps;
/* 158 */       connectionFactoryProperties = this.defaultConnectionFactoryProps;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 163 */       connector = JMSConnectorManager.getInstance().getConnector(connectorProperties, connectionFactoryProperties, username, password, vendorAdapter);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 168 */       log.error(Messages.getMessage("cannotConnectError"), e);
/*     */ 
/* 170 */       if ((e instanceof AxisFault))
/* 171 */         throw ((AxisFault)e);
/* 172 */       throw new AxisFault("cannotConnect", e);
/*     */     }
/*     */ 
/* 176 */     context.setProperty("transport.jms.Connector", connector);
/* 177 */     context.setProperty("transport.jms.VendorAdapter", vendorAdapter);
/*     */ 
/* 180 */     vendorAdapter.setupMessageContext(context, message, jmsurl);
/*     */ 
/* 182 */     if (log.isDebugEnabled())
/* 183 */       log.debug("Exit: JMSTransport::setupMessageContextImpl");
/*     */   }
/*     */ 
/*     */   public void shutdown()
/*     */   {
/* 192 */     if (log.isDebugEnabled()) {
/* 193 */       log.debug("Enter: JMSTransport::shutdown");
/*     */     }
/*     */ 
/* 196 */     closeAllConnectors();
/*     */ 
/* 198 */     if (log.isDebugEnabled())
/* 199 */       log.debug("Exit: JMSTransport::shutdown");
/*     */   }
/*     */ 
/*     */   public static void closeAllConnectors()
/*     */   {
/* 208 */     if (log.isDebugEnabled()) {
/* 209 */       log.debug("Enter: JMSTransport::closeAllConnectors");
/*     */     }
/*     */ 
/* 212 */     JMSConnectorManager.getInstance().closeAllConnectors();
/*     */ 
/* 214 */     if (log.isDebugEnabled())
/* 215 */       log.debug("Exit: JMSTransport::closeAllConnectors");
/*     */   }
/*     */ 
/*     */   public static void closeMatchingJMSConnectors(String endpointAddr, String username, String password)
/*     */   {
/* 228 */     if (log.isDebugEnabled()) {
/* 229 */       log.debug("Enter: JMSTransport::closeMatchingJMSConnectors");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 234 */       JMSURLHelper jmsurl = new JMSURLHelper(new URL(endpointAddr));
/* 235 */       String vendorId = jmsurl.getVendor();
/*     */ 
/* 237 */       JMSVendorAdapter vendorAdapter = null;
/* 238 */       if (vendorId == null)
/* 239 */         vendorId = "JNDI";
/* 240 */       vendorAdapter = JMSVendorAdapterFactory.getJMSVendorAdapter(vendorId);
/*     */ 
/* 243 */       if (vendorAdapter == null) {
/* 244 */         return;
/*     */       }
/*     */ 
/* 247 */       HashMap connectorProps = vendorAdapter.getJMSConnectorProperties(jmsurl);
/* 248 */       HashMap cfProps = vendorAdapter.getJMSConnectionFactoryProperties(jmsurl);
/*     */ 
/* 250 */       JMSConnectorManager.getInstance().closeMatchingJMSConnectors(connectorProps, cfProps, username, password, vendorAdapter);
/*     */     }
/*     */     catch (MalformedURLException e)
/*     */     {
/* 256 */       log.warn(Messages.getMessage("malformedURLException00"), e);
/*     */     }
/*     */ 
/* 259 */     if (log.isDebugEnabled())
/* 260 */       log.debug("Exit: JMSTransport::closeMatchingJMSConnectors");
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  60 */     Runtime.getRuntime().addShutdownHook(new Thread()
/*     */     {
/*     */       public void run()
/*     */       {
/*  65 */         JMSTransport.closeAllConnectors();
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.jms.JMSTransport
 * JD-Core Version:    0.6.0
 */