/*    */ package org.apache.axis.configuration;
/*    */ 
/*    */ import java.util.Hashtable;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.AxisEngine;
/*    */ import org.apache.axis.ConfigurationException;
/*    */ import org.apache.axis.EngineConfiguration;
/*    */ import org.apache.axis.Handler;
/*    */ import org.apache.axis.encoding.TypeMapping;
/*    */ import org.apache.axis.encoding.TypeMappingRegistry;
/*    */ import org.apache.axis.handlers.soap.SOAPService;
/*    */ 
/*    */ public class NullProvider
/*    */   implements EngineConfiguration
/*    */ {
/*    */   public void configureEngine(AxisEngine engine)
/*    */     throws ConfigurationException
/*    */   {
/*    */   }
/*    */ 
/*    */   public void writeEngineConfig(AxisEngine engine)
/*    */     throws ConfigurationException
/*    */   {
/*    */   }
/*    */ 
/*    */   public Hashtable getGlobalOptions()
/*    */     throws ConfigurationException
/*    */   {
/* 48 */     return null;
/*    */   }
/*    */ 
/*    */   public Handler getGlobalResponse() throws ConfigurationException {
/* 52 */     return null;
/*    */   }
/*    */ 
/*    */   public Handler getGlobalRequest() throws ConfigurationException {
/* 56 */     return null;
/*    */   }
/*    */ 
/*    */   public TypeMappingRegistry getTypeMappingRegistry() throws ConfigurationException {
/* 60 */     return null;
/*    */   }
/*    */ 
/*    */   public TypeMapping getTypeMapping(String encodingStyle) throws ConfigurationException {
/* 64 */     return null;
/*    */   }
/*    */ 
/*    */   public Handler getTransport(QName qname) throws ConfigurationException {
/* 68 */     return null;
/*    */   }
/*    */ 
/*    */   public SOAPService getService(QName qname) throws ConfigurationException {
/* 72 */     return null;
/*    */   }
/*    */ 
/*    */   public SOAPService getServiceByNamespaceURI(String namespace) throws ConfigurationException
/*    */   {
/* 77 */     return null;
/*    */   }
/*    */ 
/*    */   public Handler getHandler(QName qname) throws ConfigurationException {
/* 81 */     return null;
/*    */   }
/*    */ 
/*    */   public Iterator getDeployedServices()
/*    */     throws ConfigurationException
/*    */   {
/* 88 */     return null;
/*    */   }
/*    */ 
/*    */   public List getRoles()
/*    */   {
/* 98 */     return null;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.configuration.NullProvider
 * JD-Core Version:    0.6.0
 */