/*     */ package org.apache.axis.management;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.ConfigurationException;
/*     */ import org.apache.axis.EngineConfiguration;
/*     */ import org.apache.axis.WSDDEngineConfiguration;
/*     */ import org.apache.axis.deployment.wsdd.WSDDDeployment;
/*     */ import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
/*     */ import org.apache.axis.deployment.wsdd.WSDDHandler;
/*     */ import org.apache.axis.deployment.wsdd.WSDDService;
/*     */ import org.apache.axis.deployment.wsdd.WSDDTransport;
/*     */ import org.apache.axis.description.ServiceDesc;
/*     */ import org.apache.axis.handlers.soap.SOAPService;
/*     */ import org.apache.axis.management.jmx.DeploymentAdministrator;
/*     */ import org.apache.axis.management.jmx.DeploymentQuery;
/*     */ import org.apache.axis.management.jmx.ServiceAdministrator;
/*     */ import org.apache.axis.server.AxisServer;
/*     */ 
/*     */ public class ServiceAdmin
/*     */ {
/*  46 */   private static AxisServer axisServer = null;
/*     */ 
/*     */   public static void startService(String serviceName)
/*     */     throws AxisFault, ConfigurationException
/*     */   {
/*  56 */     AxisServer server = getEngine();
/*     */     try {
/*  58 */       SOAPService service = server.getConfig().getService(new QName("", serviceName));
/*     */ 
/*  60 */       service.start();
/*     */     } catch (ConfigurationException configException) {
/*  62 */       if ((configException.getContainedException() instanceof AxisFault)) {
/*  63 */         throw ((AxisFault)configException.getContainedException());
/*     */       }
/*  65 */       throw configException;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void stopService(String serviceName)
/*     */     throws AxisFault, ConfigurationException
/*     */   {
/*  78 */     AxisServer server = getEngine();
/*     */     try {
/*  80 */       SOAPService service = server.getConfig().getService(new QName("", serviceName));
/*     */ 
/*  82 */       service.stop();
/*     */     } catch (ConfigurationException configException) {
/*  84 */       if ((configException.getContainedException() instanceof AxisFault)) {
/*  85 */         throw ((AxisFault)configException.getContainedException());
/*     */       }
/*  87 */       throw configException;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String[] listServices() throws AxisFault, ConfigurationException {
/* 100 */     ArrayList list = new ArrayList();
/* 101 */     AxisServer server = getEngine();
/*     */     Iterator iter;
/*     */     try {
/* 104 */       iter = server.getConfig().getDeployedServices();
/*     */     } catch (ConfigurationException configException) {
/* 106 */       if ((configException.getContainedException() instanceof AxisFault)) {
/* 107 */         throw ((AxisFault)configException.getContainedException());
/*     */       }
/* 109 */       throw configException;
/*     */     }
/*     */ 
/* 112 */     while (iter.hasNext()) {
/* 113 */       ServiceDesc sd = (ServiceDesc)iter.next();
/* 114 */       String name = sd.getName();
/* 115 */       list.add(name);
/*     */     }
/* 117 */     return (String[])list.toArray(new String[list.size()]);
/*     */   }
/*     */ 
/*     */   public static AxisServer getEngine()
/*     */     throws AxisFault
/*     */   {
/* 127 */     if (axisServer == null)
/*     */     {
/* 129 */       throw new AxisFault("Unable to locate AxisEngine for ServiceAdmin Object");
/*     */     }
/*     */ 
/* 132 */     return axisServer;
/*     */   }
/*     */ 
/*     */   public static void setEngine(AxisServer axisSrv, String name)
/*     */   {
/* 141 */     axisServer = axisSrv;
/* 142 */     Registrar.register(new ServiceAdministrator(), "axis:type=server", "ServiceAdministrator");
/* 143 */     Registrar.register(new DeploymentAdministrator(), "axis:type=deploy", "DeploymentAdministrator");
/* 144 */     Registrar.register(new DeploymentQuery(), "axis:type=query", "DeploymentQuery");
/*     */   }
/*     */ 
/*     */   public static void start() {
/* 148 */     if (axisServer != null)
/* 149 */       axisServer.start();
/*     */   }
/*     */ 
/*     */   public static void stop()
/*     */   {
/* 154 */     if (axisServer != null)
/* 155 */       axisServer.stop();
/*     */   }
/*     */ 
/*     */   public static void restart()
/*     */   {
/* 160 */     if (axisServer != null) {
/* 161 */       axisServer.stop();
/* 162 */       axisServer.start();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void saveConfiguration() {
/* 167 */     if (axisServer != null)
/* 168 */       axisServer.saveConfiguration();
/*     */   }
/*     */ 
/*     */   private static WSDDEngineConfiguration getWSDDEngineConfiguration()
/*     */   {
/* 173 */     if (axisServer != null) {
/* 174 */       EngineConfiguration config = axisServer.getConfig();
/* 175 */       if ((config instanceof WSDDEngineConfiguration)) {
/* 176 */         return (WSDDEngineConfiguration)config;
/*     */       }
/* 178 */       throw new RuntimeException("WSDDDeploymentHelper.getWSDDEngineConfiguration(): EngineConguration not of type WSDDEngineConfiguration");
/*     */     }
/*     */ 
/* 181 */     return null;
/*     */   }
/*     */ 
/*     */   public static void setGlobalConfig(WSDDGlobalConfiguration globalConfig) {
/* 185 */     getWSDDEngineConfiguration().getDeployment().setGlobalConfiguration(globalConfig);
/*     */   }
/*     */ 
/*     */   public static WSDDGlobalConfiguration getGlobalConfig() {
/* 189 */     return getWSDDEngineConfiguration().getDeployment().getGlobalConfiguration();
/*     */   }
/*     */ 
/*     */   public static WSDDHandler getHandler(QName qname) {
/* 193 */     return getWSDDEngineConfiguration().getDeployment().getWSDDHandler(qname);
/*     */   }
/*     */ 
/*     */   public static WSDDHandler[] getHandlers() {
/* 197 */     return getWSDDEngineConfiguration().getDeployment().getHandlers();
/*     */   }
/*     */ 
/*     */   public static WSDDService getService(QName qname) {
/* 201 */     return getWSDDEngineConfiguration().getDeployment().getWSDDService(qname);
/*     */   }
/*     */ 
/*     */   public static WSDDService[] getServices() {
/* 205 */     return getWSDDEngineConfiguration().getDeployment().getServices();
/*     */   }
/*     */ 
/*     */   public static WSDDTransport getTransport(QName qname) {
/* 209 */     return getWSDDEngineConfiguration().getDeployment().getWSDDTransport(qname);
/*     */   }
/*     */ 
/*     */   public static WSDDTransport[] getTransports() {
/* 213 */     return getWSDDEngineConfiguration().getDeployment().getTransports();
/*     */   }
/*     */ 
/*     */   public static void deployHandler(WSDDHandler handler) {
/* 217 */     getWSDDEngineConfiguration().getDeployment().deployHandler(handler);
/*     */   }
/*     */ 
/*     */   public static void deployService(WSDDService service) {
/* 221 */     getWSDDEngineConfiguration().getDeployment().deployService(service);
/*     */   }
/*     */ 
/*     */   public static void deployTransport(WSDDTransport transport) {
/* 225 */     getWSDDEngineConfiguration().getDeployment().deployTransport(transport);
/*     */   }
/*     */ 
/*     */   public static void undeployHandler(QName qname) {
/* 229 */     getWSDDEngineConfiguration().getDeployment().undeployHandler(qname);
/*     */   }
/*     */ 
/*     */   public static void undeployService(QName qname) {
/* 233 */     getWSDDEngineConfiguration().getDeployment().undeployService(qname);
/*     */   }
/*     */ 
/*     */   public static void undeployTransport(QName qname) {
/* 237 */     getWSDDEngineConfiguration().getDeployment().undeployTransport(qname);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.management.ServiceAdmin
 * JD-Core Version:    0.6.0
 */