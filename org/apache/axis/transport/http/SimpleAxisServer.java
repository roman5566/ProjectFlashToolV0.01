/*     */ package org.apache.axis.transport.http;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.net.BindException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.util.Map;
/*     */ import org.apache.axis.EngineConfiguration;
/*     */ import org.apache.axis.EngineConfigurationFactory;
/*     */ import org.apache.axis.collections.LRUMap;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.components.threadpool.ThreadPool;
/*     */ import org.apache.axis.configuration.EngineConfigurationFactoryFinder;
/*     */ import org.apache.axis.management.ServiceAdmin;
/*     */ import org.apache.axis.server.AxisServer;
/*     */ import org.apache.axis.session.Session;
/*     */ import org.apache.axis.session.SimpleSession;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.NetworkUtils;
/*     */ import org.apache.axis.utils.Options;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class SimpleAxisServer
/*     */   implements Runnable
/*     */ {
/*  54 */   protected static Log log = LogFactory.getLog(SimpleAxisServer.class.getName());
/*     */   private Map sessions;
/*     */   private int maxSessions;
/*     */   public static final int MAX_SESSIONS_DEFAULT = 100;
/*     */   private static ThreadPool pool;
/*  82 */   private static boolean doThreads = true;
/*     */ 
/*  87 */   private static boolean doSessions = true;
/*     */ 
/* 192 */   public static int sessionIndex = 0;
/*     */ 
/* 195 */   private static AxisServer myAxisServer = null;
/*     */ 
/* 197 */   private EngineConfiguration myConfig = null;
/*     */ 
/* 220 */   private boolean stopped = false;
/*     */   private ServerSocket serverSocket;
/*     */ 
/*     */   public static ThreadPool getPool()
/*     */   {
/*  72 */     return pool;
/*     */   }
/*     */ 
/*     */   public SimpleAxisServer()
/*     */   {
/*  93 */     this(100);
/*     */   }
/*     */ 
/*     */   public SimpleAxisServer(int maxPoolSize)
/*     */   {
/* 102 */     this(maxPoolSize, 100);
/*     */   }
/*     */ 
/*     */   public SimpleAxisServer(int maxPoolSize, int maxSessions)
/*     */   {
/* 111 */     this.maxSessions = maxSessions;
/* 112 */     this.sessions = new LRUMap(maxSessions);
/* 113 */     pool = new ThreadPool(maxPoolSize);
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */     throws Throwable
/*     */   {
/* 122 */     stop();
/* 123 */     super.finalize();
/*     */   }
/*     */ 
/*     */   public int getMaxSessions()
/*     */   {
/* 131 */     return this.maxSessions;
/*     */   }
/*     */ 
/*     */   public void setMaxSessions(int maxSessions)
/*     */   {
/* 139 */     this.maxSessions = maxSessions;
/* 140 */     ((LRUMap)this.sessions).setMaximumSize(maxSessions);
/*     */   }
/*     */ 
/*     */   protected boolean isSessionUsed()
/*     */   {
/* 145 */     return doSessions;
/*     */   }
/*     */ 
/*     */   public void setDoThreads(boolean value)
/*     */   {
/* 153 */     doThreads = value;
/*     */   }
/*     */ 
/*     */   public boolean getDoThreads() {
/* 157 */     return doThreads;
/*     */   }
/*     */ 
/*     */   public EngineConfiguration getMyConfig() {
/* 161 */     return this.myConfig;
/*     */   }
/*     */ 
/*     */   public void setMyConfig(EngineConfiguration myConfig) {
/* 165 */     this.myConfig = myConfig;
/*     */   }
/*     */ 
/*     */   protected Session createSession(String cooky)
/*     */   {
/* 176 */     Session session = null;
/* 177 */     if (this.sessions.containsKey(cooky)) {
/* 178 */       session = (Session)this.sessions.get(cooky);
/*     */     }
/*     */     else {
/* 181 */       session = new SimpleSession();
/*     */ 
/* 184 */       this.sessions.put(cooky, session);
/*     */     }
/* 186 */     return session;
/*     */   }
/*     */ 
/*     */   public synchronized AxisServer getAxisServer()
/*     */   {
/* 206 */     if (myAxisServer == null) {
/* 207 */       if (this.myConfig == null) {
/* 208 */         this.myConfig = EngineConfigurationFactoryFinder.newFactory().getServerEngineConfig();
/*     */       }
/* 210 */       myAxisServer = new AxisServer(this.myConfig);
/* 211 */       ServiceAdmin.setEngine(myAxisServer, NetworkUtils.getLocalHostname() + "@" + this.serverSocket.getLocalPort());
/*     */     }
/* 213 */     return myAxisServer;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 227 */     log.info(Messages.getMessage("start01", "SimpleAxisServer", new Integer(getServerSocket().getLocalPort()).toString(), getCurrentDirectory()));
/*     */ 
/* 231 */     while (!this.stopped) {
/* 232 */       Socket socket = null;
/*     */       try {
/* 234 */         socket = this.serverSocket.accept();
/*     */       } catch (InterruptedIOException iie) {
/*     */       } catch (Exception e) {
/* 237 */         log.debug(Messages.getMessage("exception00"), e);
/* 238 */         break;
/*     */       }
/* 240 */       if (socket != null) {
/* 241 */         SimpleAxisWorker worker = new SimpleAxisWorker(this, socket);
/* 242 */         if (doThreads)
/* 243 */           pool.addWorker(worker);
/*     */         else {
/* 245 */           worker.run();
/*     */         }
/*     */       }
/*     */     }
/* 249 */     log.info(Messages.getMessage("quit00", "SimpleAxisServer"));
/*     */   }
/*     */ 
/*     */   private String getCurrentDirectory()
/*     */   {
/* 257 */     return System.getProperty("user.dir");
/*     */   }
/*     */ 
/*     */   public ServerSocket getServerSocket()
/*     */   {
/* 270 */     return this.serverSocket;
/*     */   }
/*     */ 
/*     */   public void setServerSocket(ServerSocket serverSocket)
/*     */   {
/* 279 */     this.serverSocket = serverSocket;
/*     */   }
/*     */ 
/*     */   public void start(boolean daemon)
/*     */     throws Exception
/*     */   {
/* 290 */     this.stopped = false;
/* 291 */     if (doThreads) {
/* 292 */       Thread thread = new Thread(this);
/* 293 */       thread.setDaemon(daemon);
/* 294 */       thread.start();
/*     */     } else {
/* 296 */       run();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void start()
/*     */     throws Exception
/*     */   {
/* 304 */     start(false);
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/* 315 */     if (this.stopped) {
/* 316 */       return;
/*     */     }
/*     */ 
/* 322 */     this.stopped = true;
/*     */     try
/*     */     {
/* 325 */       if (this.serverSocket != null)
/* 326 */         this.serverSocket.close();
/*     */     }
/*     */     catch (IOException e) {
/* 329 */       log.info(Messages.getMessage("exception00"), e);
/*     */     } finally {
/* 331 */       this.serverSocket = null;
/*     */     }
/*     */ 
/* 334 */     log.info(Messages.getMessage("quit00", "SimpleAxisServer"));
/*     */ 
/* 337 */     pool.shutdown();
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 345 */     Options opts = null;
/*     */     try {
/* 347 */       opts = new Options(args);
/*     */     } catch (MalformedURLException e) {
/* 349 */       log.error(Messages.getMessage("malformedURLException00"), e);
/* 350 */       return;
/*     */     }
/*     */ 
/* 353 */     String maxPoolSize = opts.isValueSet('t');
/* 354 */     if (maxPoolSize == null) maxPoolSize = "100";
/*     */ 
/* 356 */     String maxSessions = opts.isValueSet('m');
/* 357 */     if (maxSessions == null) maxSessions = "100";
/*     */ 
/* 359 */     SimpleAxisServer sas = new SimpleAxisServer(Integer.parseInt(maxPoolSize), Integer.parseInt(maxSessions));
/*     */     try
/*     */     {
/* 363 */       doThreads = opts.isFlagSet('t') > 0;
/*     */ 
/* 365 */       int port = opts.getPort();
/* 366 */       ServerSocket ss = null;
/*     */ 
/* 368 */       int retries = 5;
/* 369 */       for (int i = 0; i < 5; i++) {
/*     */         try {
/* 371 */           ss = new ServerSocket(port);
/*     */         }
/*     */         catch (BindException be) {
/* 374 */           log.debug(Messages.getMessage("exception00"), be);
/* 375 */           if (i < 4)
/*     */           {
/* 377 */             Thread.sleep(3000L);
/*     */           }
/* 379 */           else throw new Exception(Messages.getMessage("unableToStartServer00", Integer.toString(port)));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 384 */       sas.setServerSocket(ss);
/* 385 */       sas.start();
/*     */     } catch (Exception e) {
/* 387 */       log.error(Messages.getMessage("exception00"), e);
/* 388 */       return;
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.http.SimpleAxisServer
 * JD-Core Version:    0.6.0
 */