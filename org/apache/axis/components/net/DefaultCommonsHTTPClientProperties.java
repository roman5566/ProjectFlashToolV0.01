/*     */ package org.apache.axis.components.net;
/*     */ 
/*     */ import org.apache.axis.AxisProperties;
/*     */ 
/*     */ public class DefaultCommonsHTTPClientProperties
/*     */   implements CommonsHTTPClientProperties
/*     */ {
/*     */   public static final String MAXIMUM_TOTAL_CONNECTIONS_PROPERTY_KEY = "axis.http.client.maximum.total.connections";
/*     */   public static final String MAXIMUM_CONNECTIONS_PER_HOST_PROPERTY_KEY = "axis.http.client.maximum.connections.per.host";
/*     */   public static final String CONNECTION_POOL_TIMEOUT_KEY = "axis.http.client.connection.pool.timeout";
/*     */   public static final String CONNECTION_DEFAULT_CONNECTION_TIMEOUT_KEY = "axis.http.client.connection.default.connection.timeout";
/*     */   public static final String CONNECTION_DEFAULT_SO_TIMEOUT_KEY = "axis.http.client.connection.default.so.timeout";
/*     */ 
/*     */   protected final int getIntegerProperty(String property, String dephault)
/*     */   {
/*  63 */     return Integer.parseInt(AxisProperties.getProperty(property, dephault));
/*     */   }
/*     */ 
/*     */   public int getMaximumTotalConnections()
/*     */   {
/*  73 */     int i = getIntegerProperty("axis.http.client.maximum.total.connections", "20");
/*  74 */     if (i < 1) {
/*  75 */       throw new IllegalStateException("axis.http.client.maximum.total.connections must be > 1");
/*     */     }
/*  77 */     return i;
/*     */   }
/*     */ 
/*     */   public int getMaximumConnectionsPerHost()
/*     */   {
/*  87 */     int i = getIntegerProperty("axis.http.client.maximum.connections.per.host", "2");
/*  88 */     if (i < 1) {
/*  89 */       throw new IllegalStateException("axis.http.client.maximum.connections.per.host must be > 1");
/*     */     }
/*  91 */     return i;
/*     */   }
/*     */ 
/*     */   public int getConnectionPoolTimeout()
/*     */   {
/* 101 */     int i = getIntegerProperty("axis.http.client.connection.pool.timeout", "0");
/* 102 */     if (i < 0) {
/* 103 */       throw new IllegalStateException("axis.http.client.connection.pool.timeout must be >= 0");
/*     */     }
/* 105 */     return i;
/*     */   }
/*     */ 
/*     */   public int getDefaultConnectionTimeout()
/*     */   {
/* 115 */     int i = getIntegerProperty("axis.http.client.connection.default.connection.timeout", "0");
/* 116 */     if (i < 0) {
/* 117 */       throw new IllegalStateException("axis.http.client.connection.default.connection.timeout must be >= 0");
/*     */     }
/* 119 */     return i;
/*     */   }
/*     */ 
/*     */   public int getDefaultSoTimeout()
/*     */   {
/* 129 */     int i = getIntegerProperty("axis.http.client.connection.default.so.timeout", "0");
/* 130 */     if (i < 0) {
/* 131 */       throw new IllegalStateException("axis.http.client.connection.default.so.timeout must be >= 0");
/*     */     }
/* 133 */     return i;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.net.DefaultCommonsHTTPClientProperties
 * JD-Core Version:    0.6.0
 */