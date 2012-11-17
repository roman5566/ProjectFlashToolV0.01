/*     */ package org.apache.axis.transport.jms;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.components.jms.JMSVendorAdapter;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class JMSConnectorManager
/*     */ {
/*  36 */   protected static Log log = LogFactory.getLog(JMSConnectorManager.class.getName());
/*     */ 
/*  39 */   private static JMSConnectorManager s_instance = new JMSConnectorManager();
/*     */ 
/*  41 */   private static HashMap vendorConnectorPools = new HashMap();
/*  42 */   private int DEFAULT_WAIT_FOR_SHUTDOWN = 90000;
/*     */ 
/*     */   public static JMSConnectorManager getInstance()
/*     */   {
/*  50 */     return s_instance;
/*     */   }
/*     */ 
/*     */   public ShareableObjectPool getVendorPool(String vendorId)
/*     */   {
/*  58 */     return (ShareableObjectPool)vendorConnectorPools.get(vendorId);
/*     */   }
/*     */ 
/*     */   public JMSConnector getConnector(HashMap connectorProperties, HashMap connectionFactoryProperties, String username, String password, JMSVendorAdapter vendorAdapter)
/*     */     throws AxisFault
/*     */   {
/*  71 */     JMSConnector connector = null;
/*     */     try
/*     */     {
/*  76 */       ShareableObjectPool vendorConnectors = getVendorPool(vendorAdapter.getVendorId());
/*  77 */       if (vendorConnectors == null)
/*     */       {
/*  79 */         synchronized (vendorConnectorPools)
/*     */         {
/*  81 */           vendorConnectors = getVendorPool(vendorAdapter.getVendorId());
/*  82 */           if (vendorConnectors == null)
/*     */           {
/*  84 */             vendorConnectors = new ShareableObjectPool();
/*  85 */             vendorConnectorPools.put(vendorAdapter.getVendorId(), vendorConnectors);
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*  91 */       synchronized (vendorConnectors)
/*     */       {
/*     */         try
/*     */         {
/*  96 */           connector = JMSConnectorFactory.matchConnector(vendorConnectors.getElements(), connectorProperties, connectionFactoryProperties, username, password, vendorAdapter);
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/*     */         }
/*     */ 
/* 105 */         if (connector == null)
/*     */         {
/* 107 */           connector = JMSConnectorFactory.createClientConnector(connectorProperties, connectionFactoryProperties, username, password, vendorAdapter);
/*     */ 
/* 112 */           connector.start();
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 118 */       log.error(Messages.getMessage("cannotConnectError"), e);
/*     */ 
/* 120 */       if ((e instanceof AxisFault))
/* 121 */         throw ((AxisFault)e);
/* 122 */       throw new AxisFault("cannotConnect", e);
/*     */     }
/*     */ 
/* 125 */     return connector;
/*     */   }
/*     */ 
/*     */   void closeAllConnectors()
/*     */   {
/* 133 */     if (log.isDebugEnabled()) {
/* 134 */       log.debug("Enter: JMSConnectorManager::closeAllConnectors");
/*     */     }
/*     */ 
/* 137 */     synchronized (vendorConnectorPools)
/*     */     {
/* 139 */       Iterator iter = vendorConnectorPools.values().iterator();
/* 140 */       while (iter.hasNext())
/*     */       {
/* 143 */         ShareableObjectPool pool = (ShareableObjectPool)iter.next();
/* 144 */         synchronized (pool)
/*     */         {
/* 146 */           Iterator connectors = pool.getElements().iterator();
/* 147 */           while (connectors.hasNext())
/*     */           {
/* 149 */             JMSConnector conn = (JMSConnector)connectors.next();
/*     */             try
/*     */             {
/* 154 */               reserve(conn);
/* 155 */               closeConnector(conn);
/*     */             }
/*     */             catch (Exception e) {
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 163 */     if (log.isDebugEnabled())
/* 164 */       log.debug("Exit: JMSConnectorManager::closeAllConnectors");
/*     */   }
/*     */ 
/*     */   void closeMatchingJMSConnectors(HashMap connectorProps, HashMap cfProps, String username, String password, JMSVendorAdapter vendorAdapter)
/*     */   {
/* 175 */     if (log.isDebugEnabled()) {
/* 176 */       log.debug("Enter: JMSConnectorManager::closeMatchingJMSConnectors");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 181 */       String vendorId = vendorAdapter.getVendorId();
/*     */ 
/* 184 */       ShareableObjectPool vendorConnectors = null;
/* 185 */       synchronized (vendorConnectorPools)
/*     */       {
/* 187 */         vendorConnectors = getVendorPool(vendorId);
/*     */       }
/*     */ 
/* 191 */       if (vendorConnectors == null) {
/* 192 */         return;
/*     */       }
/* 194 */       synchronized (vendorConnectors)
/*     */       {
/* 197 */         JMSConnector connector = null;
/*     */ 
/* 204 */         while ((vendorConnectors.size() > 0) && ((connector = JMSConnectorFactory.matchConnector(vendorConnectors.getElements(), connectorProps, cfProps, username, password, vendorAdapter)) != null))
/*     */         {
/* 206 */           closeConnector(connector);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 212 */       log.warn(Messages.getMessage("failedJMSConnectorShutdown"), e);
/*     */     }
/*     */ 
/* 215 */     if (log.isDebugEnabled())
/* 216 */       log.debug("Exit: JMSConnectorManager::closeMatchingJMSConnectors");
/*     */   }
/*     */ 
/*     */   private void closeConnector(JMSConnector conn)
/*     */   {
/* 222 */     conn.stop();
/* 223 */     conn.shutdown();
/*     */   }
/*     */ 
/*     */   public void addConnectorToPool(JMSConnector conn)
/*     */   {
/* 231 */     if (log.isDebugEnabled()) {
/* 232 */       log.debug("Enter: JMSConnectorManager::addConnectorToPool");
/*     */     }
/*     */ 
/* 235 */     ShareableObjectPool vendorConnectors = null;
/* 236 */     synchronized (vendorConnectorPools)
/*     */     {
/* 238 */       String vendorId = conn.getVendorAdapter().getVendorId();
/* 239 */       vendorConnectors = getVendorPool(vendorId);
/*     */ 
/* 243 */       if (vendorConnectors == null)
/*     */       {
/* 245 */         vendorConnectors = new ShareableObjectPool();
/* 246 */         vendorConnectorPools.put(vendorId, vendorConnectors);
/*     */       }
/*     */     }
/*     */ 
/* 250 */     synchronized (vendorConnectors)
/*     */     {
/* 252 */       vendorConnectors.addObject(conn);
/*     */     }
/*     */ 
/* 255 */     if (log.isDebugEnabled())
/* 256 */       log.debug("Exit: JMSConnectorManager::addConnectorToPool");
/*     */   }
/*     */ 
/*     */   public void removeConnectorFromPool(JMSConnector conn)
/*     */   {
/* 265 */     if (log.isDebugEnabled()) {
/* 266 */       log.debug("Enter: JMSConnectorManager::removeConnectorFromPool");
/*     */     }
/*     */ 
/* 269 */     ShareableObjectPool vendorConnectors = null;
/* 270 */     synchronized (vendorConnectorPools)
/*     */     {
/* 272 */       vendorConnectors = getVendorPool(conn.getVendorAdapter().getVendorId());
/*     */     }
/* 274 */     if (vendorConnectors == null) {
/* 275 */       return;
/*     */     }
/* 277 */     synchronized (vendorConnectors)
/*     */     {
/* 281 */       vendorConnectors.release(conn);
/* 282 */       vendorConnectors.removeObject(conn);
/*     */     }
/*     */ 
/* 285 */     if (log.isDebugEnabled())
/* 286 */       log.debug("Exit: JMSConnectorManager::removeConnectorFromPool");
/*     */   }
/*     */ 
/*     */   public void reserve(JMSConnector connector)
/*     */     throws Exception
/*     */   {
/* 295 */     ShareableObjectPool pool = null;
/* 296 */     synchronized (vendorConnectorPools)
/*     */     {
/* 298 */       pool = getVendorPool(connector.getVendorAdapter().getVendorId());
/*     */     }
/* 300 */     if (pool != null)
/* 301 */       pool.reserve(connector);
/*     */   }
/*     */ 
/*     */   public void release(JMSConnector connector)
/*     */   {
/* 309 */     ShareableObjectPool pool = null;
/* 310 */     synchronized (vendorConnectorPools)
/*     */     {
/* 312 */       pool = getVendorPool(connector.getVendorAdapter().getVendorId());
/*     */     }
/* 314 */     if (pool != null)
/* 315 */       pool.release(connector);
/*     */   }
/*     */ 
/*     */   public class ShareableObjectPool
/*     */   {
/*     */     private HashMap m_elements;
/*     */     private HashMap m_expiring;
/* 331 */     private int m_numElements = 0;
/*     */ 
/*     */     public ShareableObjectPool()
/*     */     {
/* 335 */       this.m_elements = new HashMap();
/* 336 */       this.m_expiring = new HashMap();
/*     */     }
/*     */ 
/*     */     public void addObject(Object obj)
/*     */     {
/* 344 */       ReferenceCountedObject ref = new ReferenceCountedObject(obj);
/* 345 */       synchronized (this.m_elements)
/*     */       {
/* 347 */         if ((!this.m_elements.containsKey(obj)) && (!this.m_expiring.containsKey(obj)))
/* 348 */           this.m_elements.put(obj, ref);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void removeObject(Object obj, long waitTime)
/*     */     {
/* 359 */       ReferenceCountedObject ref = null;
/* 360 */       synchronized (this.m_elements)
/*     */       {
/* 362 */         ref = (ReferenceCountedObject)this.m_elements.get(obj);
/* 363 */         if (ref == null) {
/* 364 */           return;
/*     */         }
/* 366 */         this.m_elements.remove(obj);
/*     */ 
/* 368 */         if (ref.count() == 0) {
/* 369 */           return;
/*     */         }
/*     */ 
/* 372 */         this.m_expiring.put(obj, ref);
/*     */       }
/*     */ 
/* 376 */       long expiration = System.currentTimeMillis() + waitTime;
/* 377 */       while (ref.count() > 0)
/*     */       {
/*     */         try
/*     */         {
/* 381 */           Thread.sleep(5000L);
/*     */         } catch (InterruptedException e) {
/*     */         }
/* 384 */         if (System.currentTimeMillis() > expiration) {
/* 385 */           break;
/*     */         }
/*     */       }
/*     */ 
/* 389 */       this.m_expiring.remove(obj);
/*     */     }
/*     */ 
/*     */     public void removeObject(Object obj)
/*     */     {
/* 394 */       removeObject(obj, JMSConnectorManager.this.DEFAULT_WAIT_FOR_SHUTDOWN);
/*     */     }
/*     */ 
/*     */     public void reserve(Object obj)
/*     */       throws Exception
/*     */     {
/* 402 */       synchronized (this.m_elements)
/*     */       {
/* 404 */         if (this.m_expiring.containsKey(obj)) {
/* 405 */           throw new Exception("resourceUnavailable");
/*     */         }
/* 407 */         ReferenceCountedObject ref = (ReferenceCountedObject)this.m_elements.get(obj);
/* 408 */         ref.increment();
/*     */       }
/*     */     }
/*     */ 
/*     */     public void release(Object obj)
/*     */     {
/* 417 */       synchronized (this.m_elements)
/*     */       {
/* 419 */         ReferenceCountedObject ref = (ReferenceCountedObject)this.m_elements.get(obj);
/* 420 */         ref.decrement();
/*     */       }
/*     */     }
/*     */ 
/*     */     public synchronized Set getElements()
/*     */     {
/* 426 */       return this.m_elements.keySet();
/*     */     }
/*     */ 
/*     */     public synchronized int size()
/*     */     {
/* 431 */       return this.m_elements.size();
/*     */     }
/*     */ 
/*     */     public class ReferenceCountedObject
/*     */     {
/*     */       private Object m_object;
/*     */       private int m_refCount;
/*     */ 
/*     */       public ReferenceCountedObject(Object obj)
/*     */       {
/* 444 */         this.m_object = obj;
/* 445 */         this.m_refCount = 0;
/*     */       }
/*     */ 
/*     */       public synchronized void increment()
/*     */       {
/* 450 */         this.m_refCount += 1;
/*     */       }
/*     */ 
/*     */       public synchronized void decrement()
/*     */       {
/* 455 */         if (this.m_refCount > 0)
/* 456 */           this.m_refCount -= 1;
/*     */       }
/*     */ 
/*     */       public synchronized int count()
/*     */       {
/* 461 */         return this.m_refCount;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.jms.JMSConnectorManager
 * JD-Core Version:    0.6.0
 */