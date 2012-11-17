/*    */ package org.apache.axis.management.jmx;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
/*    */ import org.apache.axis.deployment.wsdd.WSDDHandler;
/*    */ import org.apache.axis.management.ServiceAdmin;
/*    */ 
/*    */ public class DeploymentAdministrator
/*    */   implements DeploymentAdministratorMBean
/*    */ {
/*    */   public void saveConfiguration()
/*    */   {
/* 29 */     ServiceAdmin.saveConfiguration();
/*    */   }
/*    */ 
/*    */   public void configureGlobalConfig(WSDDGlobalConfiguration config) {
/* 33 */     ServiceAdmin.setGlobalConfig(config);
/*    */   }
/*    */ 
/*    */   public void deployHandler(WSDDHandler handler) {
/* 37 */     ServiceAdmin.deployHandler(handler);
/*    */   }
/*    */ 
/*    */   public void deployService(WSDDServiceWrapper service) {
/* 41 */     ServiceAdmin.deployService(service.getWSDDService());
/*    */   }
/*    */ 
/*    */   public void deployTransport(WSDDTransportWrapper transport) {
/* 45 */     ServiceAdmin.deployTransport(transport.getWSDDTransport());
/*    */   }
/*    */ 
/*    */   public void undeployHandler(String qname) {
/* 49 */     ServiceAdmin.undeployHandler(new QName(qname));
/*    */   }
/*    */ 
/*    */   public void undeployService(String qname) {
/* 53 */     ServiceAdmin.undeployService(new QName(qname));
/*    */   }
/*    */ 
/*    */   public void undeployTransport(String qname) {
/* 57 */     ServiceAdmin.undeployTransport(new QName(qname));
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.management.jmx.DeploymentAdministrator
 * JD-Core Version:    0.6.0
 */