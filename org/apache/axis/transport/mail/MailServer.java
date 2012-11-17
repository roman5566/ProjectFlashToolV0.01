/*     */ package org.apache.axis.transport.mail;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.io.Reader;
/*     */ import java.net.MalformedURLException;
/*     */ import java.util.Properties;
/*     */ import javax.mail.Session;
/*     */ import javax.mail.internet.MimeMessage;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.i18n.Messages;
/*     */ import org.apache.axis.server.AxisServer;
/*     */ import org.apache.axis.utils.Options;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.net.pop3.POP3Client;
/*     */ import org.apache.commons.net.pop3.POP3MessageInfo;
/*     */ 
/*     */ public class MailServer
/*     */   implements Runnable
/*     */ {
/*  46 */   protected static Log log = LogFactory.getLog(MailServer.class.getName());
/*     */   private String host;
/*     */   private int port;
/*     */   private String userid;
/*     */   private String password;
/*  62 */   private static boolean doThreads = true;
/*     */ 
/*  77 */   private static AxisServer myAxisServer = null;
/*     */ 
/*  88 */   private boolean stopped = false;
/*     */   private POP3Client pop3;
/*     */ 
/*     */   public MailServer(String host, int port, String userid, String password)
/*     */   {
/*  55 */     this.host = host;
/*  56 */     this.port = port;
/*  57 */     this.userid = userid;
/*  58 */     this.password = password;
/*     */   }
/*     */ 
/*     */   public void setDoThreads(boolean value)
/*     */   {
/*  65 */     doThreads = value;
/*     */   }
/*     */ 
/*     */   public boolean getDoThreads() {
/*  69 */     return doThreads;
/*     */   }
/*     */ 
/*     */   public String getHost() {
/*  73 */     return this.host;
/*     */   }
/*     */ 
/*     */   protected static synchronized AxisServer getAxisServer()
/*     */   {
/*  80 */     if (myAxisServer == null) {
/*  81 */       myAxisServer = new AxisServer();
/*     */     }
/*  83 */     return myAxisServer;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  95 */     log.info(Messages.getMessage("start00", "MailServer", this.host + ":" + this.port));
/*     */     while (true)
/*     */     {
/*  98 */       if (!this.stopped)
/*     */         try {
/* 100 */           this.pop3.connect(this.host, this.port);
/* 101 */           this.pop3.login(this.userid, this.password);
/*     */ 
/* 103 */           POP3MessageInfo[] messages = this.pop3.listMessages();
/* 104 */           if ((messages != null) && (messages.length > 0))
/* 105 */             for (int i = 0; i < messages.length; i++) {
/* 106 */               Reader reader = this.pop3.retrieveMessage(messages[i].number);
/* 107 */               if (reader == null)
/*     */               {
/*     */                 continue;
/*     */               }
/* 111 */               StringBuffer buffer = new StringBuffer();
/* 112 */               BufferedReader bufferedReader = new BufferedReader(reader);
/*     */               int ch;
/* 115 */               while ((ch = bufferedReader.read()) != -1) {
/* 116 */                 buffer.append((char)ch);
/*     */               }
/* 118 */               bufferedReader.close();
/* 119 */               ByteArrayInputStream bais = new ByteArrayInputStream(buffer.toString().getBytes());
/* 120 */               Properties prop = new Properties();
/* 121 */               Session session = Session.getDefaultInstance(prop, null);
/*     */ 
/* 123 */               MimeMessage mimeMsg = new MimeMessage(session, bais);
/* 124 */               this.pop3.deleteMessage(messages[i].number);
/* 125 */               if (mimeMsg != null) {
/* 126 */                 MailWorker worker = new MailWorker(this, mimeMsg);
/* 127 */                 if (doThreads) {
/* 128 */                   Thread thread = new Thread(worker);
/* 129 */                   thread.setDaemon(true);
/* 130 */                   thread.start();
/*     */                 } else {
/* 132 */                   worker.run();
/*     */                 }
/*     */               }
/*     */             }
/*     */         } catch (InterruptedIOException iie) {
/*     */         }
/*     */         catch (Exception e) {
/* 139 */           log.debug(Messages.getMessage("exception00"), e);
/*     */         }
/*     */         finally {
/*     */           try {
/* 143 */             this.pop3.logout();
/* 144 */             this.pop3.disconnect();
/* 145 */             Thread.sleep(3000L);
/*     */           } catch (Exception e) {
/* 147 */             log.error(Messages.getMessage("exception00"), e);
/*     */           }
/*     */         }
/*     */     }
/* 151 */     log.info(Messages.getMessage("quit00", "MailServer"));
/*     */   }
/*     */ 
/*     */   public POP3Client getPOP3()
/*     */   {
/* 163 */     return this.pop3;
/*     */   }
/*     */ 
/*     */   public void setPOP3(POP3Client pop3)
/*     */   {
/* 172 */     this.pop3 = pop3;
/*     */   }
/*     */ 
/*     */   public void start(boolean daemon)
/*     */     throws Exception
/*     */   {
/* 183 */     if (doThreads) {
/* 184 */       Thread thread = new Thread(this);
/* 185 */       thread.setDaemon(daemon);
/* 186 */       thread.start();
/*     */     } else {
/* 188 */       run();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void start()
/*     */     throws Exception
/*     */   {
/* 196 */     start(false);
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */     throws Exception
/*     */   {
/* 209 */     this.stopped = true;
/* 210 */     log.info(Messages.getMessage("quit00", "MailServer"));
/*     */ 
/* 213 */     System.exit(0);
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 220 */     Options opts = null;
/*     */     try {
/* 222 */       opts = new Options(args);
/*     */     } catch (MalformedURLException e) {
/* 224 */       log.error(Messages.getMessage("malformedURLException00"), e);
/* 225 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 229 */       doThreads = opts.isFlagSet('t') > 0;
/* 230 */       String host = opts.getHost();
/* 231 */       int port = opts.isFlagSet('p') > 0 ? opts.getPort() : 110;
/* 232 */       POP3Client pop3 = new POP3Client();
/* 233 */       MailServer sas = new MailServer(host, port, opts.getUser(), opts.getPassword());
/*     */ 
/* 235 */       sas.setPOP3(pop3);
/* 236 */       sas.start();
/*     */     } catch (Exception e) {
/* 238 */       log.error(Messages.getMessage("exception00"), e);
/* 239 */       return;
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.mail.MailServer
 * JD-Core Version:    0.6.0
 */