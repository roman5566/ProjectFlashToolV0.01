/*     */ package org.apache.axis.handlers;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashSet;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.rpc.server.ServiceLifecycle;
/*     */ import org.apache.axis.AxisEngine;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.Message;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.message.SOAPEnvelope;
/*     */ import org.apache.axis.message.SOAPHeaderElement;
/*     */ import org.apache.axis.session.SimpleSession;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.SessionUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class SimpleSessionHandler extends BasicHandler
/*     */ {
/*  80 */   protected static Log log = LogFactory.getLog(SimpleSessionHandler.class.getName());
/*     */   public static final String SESSION_ID = "SimpleSession.id";
/*     */   public static final String SESSION_NS = "http://xml.apache.org/axis/session";
/*     */   public static final String SESSION_LOCALPART = "sessionID";
/*  86 */   public static final QName sessionHeaderName = new QName("http://xml.apache.org/axis/session", "sessionID");
/*     */ 
/*  89 */   private Hashtable activeSessions = new Hashtable();
/*     */ 
/*  93 */   private long reapPeriodicity = 30L;
/*  94 */   private long lastReapTime = 0L;
/*     */ 
/*  97 */   private int defaultSessionTimeout = 60;
/*     */ 
/*     */   public void invoke(MessageContext context)
/*     */     throws AxisFault
/*     */   {
/* 105 */     long curTime = System.currentTimeMillis();
/* 106 */     boolean reap = false;
/*     */ 
/* 109 */     synchronized (this) {
/* 110 */       if (curTime > this.lastReapTime + this.reapPeriodicity * 1000L) {
/* 111 */         reap = true;
/* 112 */         this.lastReapTime = curTime;
/*     */       }
/*     */     }
/*     */     Iterator i;
/* 116 */     if (reap) {
/* 117 */       Set entries = this.activeSessions.entrySet();
/* 118 */       Object victims = new HashSet();
/*     */ 
/* 121 */       for (i = entries.iterator(); i.hasNext(); ) {
/* 122 */         Map.Entry entry = (Map.Entry)i.next();
/* 123 */         Object key = entry.getKey();
/* 124 */         SimpleSession session = (SimpleSession)entry.getValue();
/* 125 */         if (curTime - session.getLastAccessTime() > session.getTimeout() * 1000)
/*     */         {
/* 127 */           log.debug(Messages.getMessage("timeout00", key.toString()));
/*     */ 
/* 131 */           ((Set)victims).add(key);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 136 */       for (i = ((Set)victims).iterator(); i.hasNext(); ) {
/* 137 */         Object key = i.next();
/* 138 */         SimpleSession session = (SimpleSession)this.activeSessions.get(key);
/* 139 */         this.activeSessions.remove(key);
/*     */ 
/* 145 */         Enumeration keys = session.getKeys();
/* 146 */         while ((keys != null) && (keys.hasMoreElements())) {
/* 147 */           String keystr = (String)keys.nextElement();
/* 148 */           Object obj = session.get(keystr);
/* 149 */           if ((obj != null) && ((obj instanceof ServiceLifecycle))) {
/* 150 */             ((ServiceLifecycle)obj).destroy();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 156 */     if (context.isClient())
/* 157 */       doClient(context);
/*     */     else
/* 159 */       doServer(context);
/*     */   }
/*     */ 
/*     */   public void doClient(MessageContext context)
/*     */     throws AxisFault
/*     */   {
/* 168 */     if (context.getPastPivot())
/*     */     {
/* 170 */       Message msg = context.getResponseMessage();
/* 171 */       if (msg == null)
/* 172 */         return;
/* 173 */       SOAPEnvelope env = msg.getSOAPEnvelope();
/* 174 */       SOAPHeaderElement header = env.getHeaderByName("http://xml.apache.org/axis/session", "sessionID");
/*     */ 
/* 176 */       if (header == null) {
/* 177 */         return;
/*     */       }
/*     */       try
/*     */       {
/* 181 */         Long id = (Long)header.getValueAsType(Constants.XSD_LONG);
/*     */ 
/* 184 */         AxisEngine engine = context.getAxisEngine();
/* 185 */         engine.setOption("SimpleSession.id", id);
/*     */ 
/* 187 */         header.setProcessed(true);
/*     */       } catch (Exception e) {
/* 189 */         throw AxisFault.makeFault(e);
/*     */       }
/*     */     } else {
/* 192 */       AxisEngine engine = context.getAxisEngine();
/* 193 */       Long id = (Long)engine.getOption("SimpleSession.id");
/* 194 */       if (id == null) {
/* 195 */         return;
/*     */       }
/*     */ 
/* 198 */       Message msg = context.getRequestMessage();
/* 199 */       if (msg == null) {
/* 200 */         throw new AxisFault(Messages.getMessage("noRequest00"));
/*     */       }
/* 202 */       SOAPEnvelope env = msg.getSOAPEnvelope();
/* 203 */       SOAPHeaderElement header = new SOAPHeaderElement("http://xml.apache.org/axis/session", "sessionID", id);
/*     */ 
/* 206 */       env.addHeader(header);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void doServer(MessageContext context)
/*     */     throws AxisFault
/*     */   {
/* 215 */     if (context.getPastPivot())
/*     */     {
/* 218 */       Long id = (Long)context.getProperty("SimpleSession.id");
/* 219 */       if (id == null) {
/* 220 */         return;
/*     */       }
/* 222 */       Message msg = context.getResponseMessage();
/* 223 */       if (msg == null)
/* 224 */         return;
/* 225 */       SOAPEnvelope env = msg.getSOAPEnvelope();
/* 226 */       SOAPHeaderElement header = new SOAPHeaderElement("http://xml.apache.org/axis/session", "sessionID", id);
/*     */ 
/* 229 */       env.addHeader(header);
/*     */     }
/*     */     else {
/* 232 */       Message msg = context.getRequestMessage();
/* 233 */       if (msg == null) {
/* 234 */         throw new AxisFault(Messages.getMessage("noRequest00"));
/*     */       }
/* 236 */       SOAPEnvelope env = msg.getSOAPEnvelope();
/* 237 */       SOAPHeaderElement header = env.getHeaderByName("http://xml.apache.org/axis/session", "sessionID");
/*     */       Long id;
/* 241 */       if (header != null)
/*     */         try
/*     */         {
/* 244 */           id = (Long)header.getValueAsType(Constants.XSD_LONG);
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/*     */           Long id;
/* 247 */           throw AxisFault.makeFault(e);
/*     */         }
/*     */       else {
/* 250 */         id = getNewSession();
/*     */       }
/*     */ 
/* 253 */       SimpleSession session = (SimpleSession)this.activeSessions.get(id);
/* 254 */       if (session == null)
/*     */       {
/* 256 */         id = getNewSession();
/* 257 */         session = (SimpleSession)this.activeSessions.get(id);
/*     */       }
/*     */ 
/* 261 */       session.touch();
/*     */ 
/* 264 */       context.setSession(session);
/* 265 */       context.setProperty("SimpleSession.id", id);
/*     */     }
/*     */   }
/*     */ 
/*     */   private synchronized Long getNewSession()
/*     */   {
/* 276 */     Long id = SessionUtils.generateSession();
/* 277 */     SimpleSession session = new SimpleSession();
/* 278 */     session.setTimeout(this.defaultSessionTimeout);
/* 279 */     this.activeSessions.put(id, session);
/* 280 */     return id;
/*     */   }
/*     */ 
/*     */   public void setReapPeriodicity(long reapTime)
/*     */   {
/* 293 */     this.reapPeriodicity = reapTime;
/*     */   }
/*     */ 
/*     */   public void setDefaultSessionTimeout(int defaultSessionTimeout)
/*     */   {
/* 302 */     this.defaultSessionTimeout = defaultSessionTimeout;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.handlers.SimpleSessionHandler
 * JD-Core Version:    0.6.0
 */