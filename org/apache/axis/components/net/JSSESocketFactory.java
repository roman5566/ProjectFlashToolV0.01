/*     */ package org.apache.axis.components.net;
/*     */ 
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.net.Socket;
/*     */ import java.util.Hashtable;
/*     */ import javax.net.ssl.SSLSocket;
/*     */ import javax.net.ssl.SSLSocketFactory;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.StringUtils;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class JSSESocketFactory extends DefaultSocketFactory
/*     */   implements SecureSocketFactory
/*     */ {
/*  45 */   protected SSLSocketFactory sslFactory = null;
/*     */ 
/*     */   public JSSESocketFactory(Hashtable attributes)
/*     */   {
/*  53 */     super(attributes);
/*     */   }
/*     */ 
/*     */   protected void initFactory()
/*     */     throws IOException
/*     */   {
/*  61 */     this.sslFactory = ((SSLSocketFactory)SSLSocketFactory.getDefault());
/*     */   }
/*     */ 
/*     */   public Socket create(String host, int port, StringBuffer otherHeaders, BooleanHolder useFullURL)
/*     */     throws Exception
/*     */   {
/*  78 */     if (this.sslFactory == null) {
/*  79 */       initFactory();
/*     */     }
/*  81 */     if (port == -1) {
/*  82 */       port = 443;
/*     */     }
/*     */ 
/*  85 */     TransportClientProperties tcp = TransportClientPropertiesFactory.create("https");
/*     */ 
/*  87 */     boolean hostInNonProxyList = isHostInNonProxyList(host, tcp.getNonProxyHosts());
/*     */ 
/*  89 */     Socket sslSocket = null;
/*  90 */     if ((tcp.getProxyHost().length() == 0) || (hostInNonProxyList))
/*     */     {
/*  92 */       sslSocket = this.sslFactory.createSocket(host, port);
/*     */     }
/*     */     else
/*     */     {
/*  96 */       int tunnelPort = tcp.getProxyPort().length() != 0 ? Integer.parseInt(tcp.getProxyPort()) : 80;
/*     */ 
/*  99 */       if (tunnelPort < 0) {
/* 100 */         tunnelPort = 80;
/*     */       }
/*     */ 
/* 103 */       Socket tunnel = new Socket(tcp.getProxyHost(), tunnelPort);
/*     */ 
/* 106 */       OutputStream tunnelOutputStream = tunnel.getOutputStream();
/* 107 */       PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(tunnelOutputStream)));
/*     */ 
/* 122 */       out.print("CONNECT " + host + ":" + port + " HTTP/1.0\r\n" + "User-Agent: AxisClient");
/*     */ 
/* 124 */       if ((tcp.getProxyUser().length() != 0) && (tcp.getProxyPassword().length() != 0))
/*     */       {
/* 128 */         String encodedPassword = XMLUtils.base64encode((tcp.getProxyUser() + ":" + tcp.getProxyPassword()).getBytes());
/*     */ 
/* 132 */         out.print("\nProxy-Authorization: Basic " + encodedPassword);
/*     */       }
/* 134 */       out.print("\nContent-Length: 0");
/* 135 */       out.print("\nPragma: no-cache");
/* 136 */       out.print("\r\n\r\n");
/* 137 */       out.flush();
/* 138 */       InputStream tunnelInputStream = tunnel.getInputStream();
/*     */ 
/* 140 */       if (log.isDebugEnabled()) {
/* 141 */         log.debug(Messages.getMessage("isNull00", "tunnelInputStream", "" + (tunnelInputStream == null)));
/*     */       }
/*     */ 
/* 145 */       String replyStr = "";
/*     */ 
/* 149 */       int newlinesSeen = 0;
/* 150 */       boolean headerDone = false;
/*     */ 
/* 152 */       while (newlinesSeen < 2) {
/* 153 */         int i = tunnelInputStream.read();
/*     */ 
/* 155 */         if (i < 0) {
/* 156 */           throw new IOException("Unexpected EOF from proxy");
/*     */         }
/* 158 */         if (i == 10) {
/* 159 */           headerDone = true;
/* 160 */           newlinesSeen++;
/* 161 */         } else if (i != 13) {
/* 162 */           newlinesSeen = 0;
/* 163 */           if (!headerDone) {
/* 164 */             replyStr = replyStr + String.valueOf((char)i);
/*     */           }
/*     */         }
/*     */       }
/* 168 */       if ((StringUtils.startsWithIgnoreWhitespaces("HTTP/1.0 200", replyStr)) && (StringUtils.startsWithIgnoreWhitespaces("HTTP/1.1 200", replyStr)))
/*     */       {
/* 170 */         throw new IOException(Messages.getMessage("cantTunnel00", new String[] { tcp.getProxyHost(), "" + tunnelPort, replyStr }));
/*     */       }
/*     */ 
/* 178 */       sslSocket = this.sslFactory.createSocket(tunnel, host, port, true);
/* 179 */       if (log.isDebugEnabled()) {
/* 180 */         log.debug(Messages.getMessage("setupTunnel00", tcp.getProxyHost(), "" + tunnelPort));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 186 */     ((SSLSocket)sslSocket).startHandshake();
/* 187 */     if (log.isDebugEnabled()) {
/* 188 */       log.debug(Messages.getMessage("createdSSL00"));
/*     */     }
/* 190 */     return sslSocket;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.net.JSSESocketFactory
 * JD-Core Version:    0.6.0
 */