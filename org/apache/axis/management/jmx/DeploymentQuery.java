/*     */ package org.apache.axis.management.jmx;
/*     */ 
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.ConfigurationException;
/*     */ import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
/*     */ import org.apache.axis.deployment.wsdd.WSDDHandler;
/*     */ import org.apache.axis.deployment.wsdd.WSDDService;
/*     */ import org.apache.axis.deployment.wsdd.WSDDTransport;
/*     */ import org.apache.axis.management.ServiceAdmin;
/*     */ 
/*     */ public class DeploymentQuery
/*     */   implements DeploymentQueryMBean
/*     */ {
/*     */   public WSDDGlobalConfiguration findGlobalConfig()
/*     */   {
/*  35 */     return ServiceAdmin.getGlobalConfig();
/*     */   }
/*     */ 
/*     */   public WSDDHandler findHandler(String qname)
/*     */   {
/*  45 */     return ServiceAdmin.getHandler(new QName(qname));
/*     */   }
/*     */ 
/*     */   public WSDDHandler[] findHandlers()
/*     */   {
/*  54 */     return ServiceAdmin.getHandlers();
/*     */   }
/*     */ 
/*     */   public WSDDService findService(String qname)
/*     */   {
/*  64 */     return ServiceAdmin.getService(new QName(qname));
/*     */   }
/*     */ 
/*     */   public WSDDService[] findServices()
/*     */   {
/*  73 */     return ServiceAdmin.getServices();
/*     */   }
/*     */ 
/*     */   public WSDDTransport findTransport(String qname)
/*     */   {
/*  83 */     return ServiceAdmin.getTransport(new QName(qname));
/*     */   }
/*     */ 
/*     */   public WSDDTransport[] findTransports()
/*     */   {
/*  92 */     return ServiceAdmin.getTransports();
/*     */   }
/*     */ 
/*     */   public String[] listServices()
/*     */     throws AxisFault, ConfigurationException
/*     */   {
/* 103 */     return ServiceAdmin.listServices();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.management.jmx.DeploymentQuery
 * JD-Core Version:    0.6.0
 */