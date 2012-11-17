/*    */ package org.apache.axis.configuration;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import org.apache.axis.AxisEngine;
/*    */ import org.apache.axis.ConfigurationException;
/*    */ 
/*    */ public class XMLStringProvider extends FileProvider
/*    */ {
/*    */   String xmlConfiguration;
/*    */ 
/*    */   public XMLStringProvider(String xmlConfiguration)
/*    */   {
/* 54 */     super(new ByteArrayInputStream(xmlConfiguration.getBytes()));
/* 55 */     this.xmlConfiguration = xmlConfiguration;
/*    */   }
/*    */ 
/*    */   public void writeEngineConfig(AxisEngine engine) throws ConfigurationException
/*    */   {
/*    */   }
/*    */ 
/*    */   public void configureEngine(AxisEngine engine) throws ConfigurationException
/*    */   {
/* 64 */     setInputStream(new ByteArrayInputStream(this.xmlConfiguration.getBytes()));
/* 65 */     super.configureEngine(engine);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.configuration.XMLStringProvider
 * JD-Core Version:    0.6.0
 */