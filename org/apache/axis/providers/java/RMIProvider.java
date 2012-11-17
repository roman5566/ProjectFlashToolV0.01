/*    */ package org.apache.axis.providers.java;
/*    */ 
/*    */ import java.rmi.Naming;
/*    */ import java.rmi.RMISecurityManager;
/*    */ import org.apache.axis.Handler;
/*    */ import org.apache.axis.MessageContext;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class RMIProvider extends RPCProvider
/*    */ {
/* 34 */   protected static Log log = LogFactory.getLog(RMIProvider.class.getName());
/*    */ 
/* 40 */   protected static Log entLog = LogFactory.getLog("org.apache.axis.enterprise");
/*    */   public static final String OPTION_NAMING_LOOKUP = "NamingLookup";
/*    */   public static final String OPTION_INTERFACE_CLASSNAME = "InterfaceClassName";
/*    */ 
/*    */   protected Object makeNewServiceObject(MessageContext msgContext, String clsName)
/*    */     throws Exception
/*    */   {
/* 57 */     String namingLookup = getStrOption("NamingLookup", msgContext.getService());
/* 58 */     if (System.getSecurityManager() == null) {
/* 59 */       System.setSecurityManager(new RMISecurityManager());
/*    */     }
/* 61 */     Object targetObject = Naming.lookup(namingLookup);
/* 62 */     return targetObject;
/*    */   }
/*    */ 
/*    */   protected String getServiceClassNameOptionName()
/*    */   {
/* 70 */     return "InterfaceClassName";
/*    */   }
/*    */ 
/*    */   protected String getStrOption(String optionName, Handler service)
/*    */   {
/* 84 */     String value = null;
/* 85 */     if (service != null)
/* 86 */       value = (String)service.getOption(optionName);
/* 87 */     if (value == null)
/* 88 */       value = (String)getOption(optionName);
/* 89 */     return value;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.providers.java.RMIProvider
 * JD-Core Version:    0.6.0
 */