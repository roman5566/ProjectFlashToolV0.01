/*    */ package org.apache.axis.management.jmx;
/*    */ 
/*    */ import org.apache.axis.AxisFault;
/*    */ import org.apache.axis.ConfigurationException;
/*    */ import org.apache.axis.Version;
/*    */ import org.apache.axis.management.ServiceAdmin;
/*    */ 
/*    */ public class ServiceAdministrator
/*    */   implements ServiceAdministratorMBean
/*    */ {
/*    */   public void start()
/*    */   {
/* 41 */     ServiceAdmin.start();
/*    */   }
/*    */ 
/*    */   public void stop()
/*    */   {
/* 48 */     ServiceAdmin.stop();
/*    */   }
/*    */ 
/*    */   public void restart()
/*    */   {
/* 55 */     ServiceAdmin.restart();
/*    */   }
/*    */ 
/*    */   public void startService(String serviceName)
/*    */     throws AxisFault, ConfigurationException
/*    */   {
/* 66 */     ServiceAdmin.startService(serviceName);
/*    */   }
/*    */ 
/*    */   public void stopService(String serviceName)
/*    */     throws AxisFault, ConfigurationException
/*    */   {
/* 77 */     ServiceAdmin.stopService(serviceName);
/*    */   }
/*    */ 
/*    */   public String getVersion()
/*    */   {
/* 86 */     return Version.getVersionText();
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.management.jmx.ServiceAdministrator
 * JD-Core Version:    0.6.0
 */