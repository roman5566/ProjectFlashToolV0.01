/*     */ package org.apache.axis.transport.jms;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Properties;
/*     */ import javax.jms.BytesMessage;
/*     */ import javax.jms.Message;
/*     */ import javax.jms.MessageListener;
/*     */ import org.apache.axis.components.jms.JMSVendorAdapter;
/*     */ import org.apache.axis.components.jms.JMSVendorAdapterFactory;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.server.AxisServer;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.Options;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class SimpleJMSListener
/*     */   implements MessageListener
/*     */ {
/*  51 */   protected static Log log = LogFactory.getLog(SimpleJMSListener.class.getName());
/*     */   private static boolean doThreads;
/*     */   private JMSConnector connector;
/*     */   private JMSEndpoint endpoint;
/*     */   private AxisServer server;
/*     */   private HashMap connectorProps;
/*  88 */   private static AxisServer myAxisServer = new AxisServer();
/*     */ 
/*     */   public SimpleJMSListener(HashMap connectorMap, HashMap cfMap, String destination, String username, String password, boolean doThreads)
/*     */     throws Exception
/*     */   {
/*  67 */     doThreads = doThreads;
/*     */     try
/*     */     {
/*  71 */       JMSVendorAdapter adapter = JMSVendorAdapterFactory.getJMSVendorAdapter();
/*  72 */       this.connector = JMSConnectorFactory.createServerConnector(connectorMap, cfMap, username, password, adapter);
/*     */ 
/*  77 */       this.connectorProps = connectorMap;
/*     */     } catch (Exception e) {
/*  79 */       log.error(Messages.getMessage("exception00"), e);
/*  80 */       throw e;
/*     */     }
/*     */ 
/*  84 */     this.endpoint = this.connector.createEndpoint(destination);
/*     */   }
/*     */ 
/*     */   protected static AxisServer getAxisServer()
/*     */   {
/*  92 */     return myAxisServer;
/*     */   }
/*     */ 
/*     */   protected JMSConnector getConnector()
/*     */   {
/*  97 */     return this.connector;
/*     */   }
/*     */ 
/*     */   public void onMessage(Message message)
/*     */   {
/*     */     try
/*     */     {
/* 109 */       SimpleJMSWorker worker = new SimpleJMSWorker(this, (BytesMessage)message);
/*     */ 
/* 112 */       if (doThreads) {
/* 113 */         Thread t = new Thread(worker);
/* 114 */         t.start();
/*     */       } else {
/* 116 */         worker.run();
/*     */       }
/*     */     }
/*     */     catch (ClassCastException cce)
/*     */     {
/* 121 */       log.error(Messages.getMessage("exception00"), cce);
/* 122 */       cce.printStackTrace();
/* 123 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void start()
/*     */     throws Exception
/*     */   {
/* 130 */     this.endpoint.registerListener(this, this.connectorProps);
/* 131 */     this.connector.start();
/*     */   }
/*     */ 
/*     */   public void shutdown()
/*     */     throws Exception
/*     */   {
/* 137 */     this.endpoint.unregisterListener(this);
/* 138 */     this.connector.stop();
/* 139 */     this.connector.shutdown();
/*     */   }
/*     */ 
/*     */   public static final HashMap createConnectorMap(Options options)
/*     */   {
/* 144 */     HashMap connectorMap = new HashMap();
/* 145 */     if (options.isFlagSet('t') > 0)
/*     */     {
/* 148 */       connectorMap.put("transport.jms.domain", "TOPIC");
/*     */     }
/* 150 */     return connectorMap;
/*     */   }
/*     */ 
/*     */   public static final HashMap createCFMap(Options options)
/*     */     throws IOException
/*     */   {
/* 156 */     String cfFile = options.isValueSet('c');
/* 157 */     if (cfFile == null) {
/* 158 */       return null;
/*     */     }
/* 160 */     Properties cfProps = new Properties();
/* 161 */     cfProps.load(new BufferedInputStream(new FileInputStream(cfFile)));
/* 162 */     HashMap cfMap = new HashMap(cfProps);
/* 163 */     return cfMap;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) throws Exception
/*     */   {
/* 168 */     Options options = new Options(args);
/*     */ 
/* 171 */     if ((options.isFlagSet('?') > 0) || (options.isFlagSet('h') > 0)) {
/* 172 */       printUsage();
/*     */     }
/* 174 */     SimpleJMSListener listener = new SimpleJMSListener(createConnectorMap(options), createCFMap(options), options.isValueSet('d'), options.getUser(), options.getPassword(), options.isFlagSet('s') > 0);
/*     */ 
/* 180 */     listener.start();
/*     */   }
/*     */ 
/*     */   public static void printUsage()
/*     */   {
/* 185 */     System.out.println("Usage: SimpleJMSListener [options]");
/* 186 */     System.out.println(" Opts: -? this message");
/* 187 */     System.out.println();
/* 188 */     System.out.println("       -c connection factory properties filename");
/* 189 */     System.out.println("       -d destination");
/* 190 */     System.out.println("       -t topic [absence of -t indicates queue]");
/* 191 */     System.out.println();
/* 192 */     System.out.println("       -u username");
/* 193 */     System.out.println("       -w password");
/* 194 */     System.out.println();
/* 195 */     System.out.println("       -s single-threaded listener");
/* 196 */     System.out.println("          [absence of option => multithreaded]");
/*     */ 
/* 198 */     System.exit(1);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.jms.SimpleJMSListener
 * JD-Core Version:    0.6.0
 */