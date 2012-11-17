/*     */ package org.apache.axis.components.net;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.Socket;
/*     */ import java.util.Hashtable;
/*     */ import java.util.StringTokenizer;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.encoding.Base64;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class DefaultSocketFactory
/*     */   implements SocketFactory
/*     */ {
/*  38 */   protected static Log log = LogFactory.getLog(DefaultSocketFactory.class.getName());
/*     */ 
/*  42 */   public static String CONNECT_TIMEOUT = "axis.client.connect.timeout";
/*     */ 
/*  45 */   protected Hashtable attributes = null;
/*     */   private static boolean plain;
/*     */   private static Class inetClass;
/*     */   private static Constructor inetConstructor;
/*     */   private static Constructor socketConstructor;
/*     */   private static Method connect;
/*     */ 
/*     */   public DefaultSocketFactory(Hashtable attributes)
/*     */   {
/*  72 */     this.attributes = attributes;
/*     */   }
/*     */ 
/*     */   public Socket create(String host, int port, StringBuffer otherHeaders, BooleanHolder useFullURL)
/*     */     throws Exception
/*     */   {
/*  91 */     int timeout = 0;
/*  92 */     if (this.attributes != null) {
/*  93 */       String value = (String)this.attributes.get(CONNECT_TIMEOUT);
/*  94 */       timeout = value != null ? Integer.parseInt(value) : 0;
/*     */     }
/*     */ 
/*  97 */     TransportClientProperties tcp = TransportClientPropertiesFactory.create("http");
/*     */ 
/*  99 */     Socket sock = null;
/* 100 */     boolean hostInNonProxyList = isHostInNonProxyList(host, tcp.getNonProxyHosts());
/*     */ 
/* 102 */     if (tcp.getProxyUser().length() != 0) {
/* 103 */       StringBuffer tmpBuf = new StringBuffer();
/*     */ 
/* 105 */       tmpBuf.append(tcp.getProxyUser()).append(":").append(tcp.getProxyPassword());
/*     */ 
/* 108 */       otherHeaders.append("Proxy-Authorization").append(": Basic ").append(Base64.encode(tmpBuf.toString().getBytes())).append("\r\n");
/*     */     }
/*     */ 
/* 113 */     if (port == -1) {
/* 114 */       port = 80;
/*     */     }
/* 116 */     if ((tcp.getProxyHost().length() == 0) || (tcp.getProxyPort().length() == 0) || (hostInNonProxyList))
/*     */     {
/* 120 */       sock = create(host, port, timeout);
/* 121 */       if (log.isDebugEnabled())
/* 122 */         log.debug(Messages.getMessage("createdHTTP00"));
/*     */     }
/*     */     else {
/* 125 */       sock = create(tcp.getProxyHost(), new Integer(tcp.getProxyPort()).intValue(), timeout);
/*     */ 
/* 128 */       if (log.isDebugEnabled()) {
/* 129 */         log.debug(Messages.getMessage("createdHTTP01", tcp.getProxyHost(), tcp.getProxyPort()));
/*     */       }
/*     */ 
/* 132 */       useFullURL.value = true;
/*     */     }
/* 134 */     return sock;
/*     */   }
/*     */ 
/*     */   private static Socket create(String host, int port, int timeout)
/*     */     throws Exception
/*     */   {
/* 147 */     Socket sock = null;
/* 148 */     if ((plain) || (timeout == 0)) {
/* 149 */       sock = new Socket(host, port);
/*     */     } else {
/* 151 */       Object address = inetConstructor.newInstance(new Object[] { host, new Integer(port) });
/* 152 */       sock = (Socket)socketConstructor.newInstance(new Object[0]);
/* 153 */       connect.invoke(sock, new Object[] { address, new Integer(timeout) });
/*     */     }
/* 155 */     return sock;
/*     */   }
/*     */ 
/*     */   protected boolean isHostInNonProxyList(String host, String nonProxyHosts)
/*     */   {
/* 168 */     if ((nonProxyHosts == null) || (host == null)) {
/* 169 */       return false;
/*     */     }
/*     */ 
/* 176 */     StringTokenizer tokenizer = new StringTokenizer(nonProxyHosts, "|\"");
/*     */ 
/* 178 */     while (tokenizer.hasMoreTokens()) {
/* 179 */       String pattern = tokenizer.nextToken();
/*     */ 
/* 181 */       if (log.isDebugEnabled()) {
/* 182 */         log.debug(Messages.getMessage("match00", new String[] { "HTTPSender", host, pattern }));
/*     */       }
/*     */ 
/* 187 */       if (match(pattern, host, false)) {
/* 188 */         return true;
/*     */       }
/*     */     }
/* 191 */     return false;
/*     */   }
/*     */ 
/*     */   protected static boolean match(String pattern, String str, boolean isCaseSensitive)
/*     */   {
/* 210 */     char[] patArr = pattern.toCharArray();
/* 211 */     char[] strArr = str.toCharArray();
/* 212 */     int patIdxStart = 0;
/* 213 */     int patIdxEnd = patArr.length - 1;
/* 214 */     int strIdxStart = 0;
/* 215 */     int strIdxEnd = strArr.length - 1;
/*     */ 
/* 217 */     boolean containsStar = false;
/*     */ 
/* 219 */     for (int i = 0; i < patArr.length; i++) {
/* 220 */       if (patArr[i] == '*') {
/* 221 */         containsStar = true;
/* 222 */         break;
/*     */       }
/*     */     }
/* 225 */     if (!containsStar)
/*     */     {
/* 228 */       if (patIdxEnd != strIdxEnd) {
/* 229 */         return false;
/*     */       }
/* 231 */       for (int i = 0; i <= patIdxEnd; i++) {
/* 232 */         char ch = patArr[i];
/* 233 */         if ((isCaseSensitive) && (ch != strArr[i])) {
/* 234 */           return false;
/*     */         }
/* 236 */         if ((!isCaseSensitive) && (Character.toUpperCase(ch) != Character.toUpperCase(strArr[i])))
/*     */         {
/* 239 */           return false;
/*     */         }
/*     */       }
/* 242 */       return true;
/*     */     }
/* 244 */     if (patIdxEnd == 0)
/* 245 */       return true;
/*     */     char ch;
/* 250 */     while (((ch = patArr[patIdxStart]) != '*') && (strIdxStart <= strIdxEnd)) {
/* 251 */       if ((isCaseSensitive) && (ch != strArr[strIdxStart])) {
/* 252 */         return false;
/*     */       }
/* 254 */       if ((!isCaseSensitive) && (Character.toUpperCase(ch) != Character.toUpperCase(strArr[strIdxStart])))
/*     */       {
/* 257 */         return false;
/*     */       }
/* 259 */       patIdxStart++;
/* 260 */       strIdxStart++;
/*     */     }
/* 262 */     if (strIdxStart > strIdxEnd)
/*     */     {
/* 266 */       for (int i = patIdxStart; i <= patIdxEnd; i++) {
/* 267 */         if (patArr[i] != '*') {
/* 268 */           return false;
/*     */         }
/*     */       }
/* 271 */       return true;
/*     */     }
/*     */ 
/* 275 */     while (((ch = patArr[patIdxEnd]) != '*') && (strIdxStart <= strIdxEnd)) {
/* 276 */       if ((isCaseSensitive) && (ch != strArr[strIdxEnd])) {
/* 277 */         return false;
/*     */       }
/* 279 */       if ((!isCaseSensitive) && (Character.toUpperCase(ch) != Character.toUpperCase(strArr[strIdxEnd])))
/*     */       {
/* 282 */         return false;
/*     */       }
/* 284 */       patIdxEnd--;
/* 285 */       strIdxEnd--;
/*     */     }
/* 287 */     if (strIdxStart > strIdxEnd)
/*     */     {
/* 291 */       for (int i = patIdxStart; i <= patIdxEnd; i++) {
/* 292 */         if (patArr[i] != '*') {
/* 293 */           return false;
/*     */         }
/*     */       }
/* 296 */       return true;
/*     */     }
/*     */ 
/* 301 */     while ((patIdxStart != patIdxEnd) && (strIdxStart <= strIdxEnd)) {
/* 302 */       int patIdxTmp = -1;
/*     */ 
/* 304 */       for (int i = patIdxStart + 1; i <= patIdxEnd; i++) {
/* 305 */         if (patArr[i] == '*') {
/* 306 */           patIdxTmp = i;
/* 307 */           break;
/*     */         }
/*     */       }
/* 310 */       if (patIdxTmp == patIdxStart + 1)
/*     */       {
/* 313 */         patIdxStart++;
/* 314 */         continue;
/*     */       }
/*     */ 
/* 319 */       int patLength = patIdxTmp - patIdxStart - 1;
/* 320 */       int strLength = strIdxEnd - strIdxStart + 1;
/* 321 */       int foundIdx = -1;
/*     */ 
/* 324 */       for (int i = 0; i <= strLength - patLength; i++) {
/* 325 */         int j = 0;
/*     */         while (true) if (j < patLength) {
/* 326 */             ch = patArr[(patIdxStart + j + 1)];
/* 327 */             if ((isCaseSensitive) && (ch != strArr[(strIdxStart + i + j)]))
/*     */             {
/*     */               break;
/*     */             }
/* 331 */             if ((!isCaseSensitive) && (Character.toUpperCase(ch) != Character.toUpperCase(strArr[(strIdxStart + i + j)])))
/*     */               break;
/* 325 */             j++; continue;
/*     */           }
/*     */           else
/*     */           {
/* 337 */             foundIdx = strIdxStart + i;
/* 338 */             break label549;
/*     */           } 
/*     */       }
/* 340 */       label549: if (foundIdx == -1) {
/* 341 */         return false;
/*     */       }
/* 343 */       patIdxStart = patIdxTmp;
/* 344 */       strIdxStart = foundIdx + patLength;
/*     */     }
/*     */ 
/* 349 */     for (int i = patIdxStart; i <= patIdxEnd; i++) {
/* 350 */       if (patArr[i] != '*') {
/* 351 */         return false;
/*     */       }
/*     */     }
/* 354 */     return true;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  55 */       inetClass = Class.forName("java.net.InetSocketAddress");
/*  56 */       plain = false;
/*  57 */       inetConstructor = inetClass.getConstructor(new Class[] { String.class, Integer.TYPE });
/*  58 */       socketConstructor = Socket.class.getConstructor(new Class[0]);
/*  59 */       connect = class$java$net$Socket.getMethod("connect", new Class[] { inetClass.getSuperclass(), Integer.TYPE });
/*     */     }
/*     */     catch (Exception e) {
/*  62 */       plain = true;
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.net.DefaultSocketFactory
 * JD-Core Version:    0.6.0
 */