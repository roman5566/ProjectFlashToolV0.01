/*     */ package org.apache.axis.providers.java;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Properties;
/*     */ import org.apache.axis.Handler;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.utils.ClassUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.omg.CORBA.ORB;
/*     */ import org.omg.CosNaming.NameComponent;
/*     */ import org.omg.CosNaming.NamingContext;
/*     */ import org.omg.CosNaming.NamingContextHelper;
/*     */ 
/*     */ public class CORBAProvider extends RPCProvider
/*     */ {
/*  40 */   protected static Log log = LogFactory.getLog(CORBAProvider.class.getName());
/*     */   private static final String DEFAULT_ORB_INITIAL_HOST = "localhost";
/*     */   private static final String DEFAULT_ORB_INITIAL_PORT = "900";
/*  49 */   protected static Log entLog = LogFactory.getLog("org.apache.axis.enterprise");
/*     */   public static final String OPTION_ORB_INITIAL_HOST = "ORBInitialHost";
/*     */   public static final String OPTION_ORB_INITIAL_PORT = "ORBInitialPort";
/*     */   public static final String OPTION_NAME_ID = "NameID";
/*     */   public static final String OPTION_NAME_KIND = "NameKind";
/*     */   public static final String OPTION_INTERFACE_CLASSNAME = "InterfaceClassName";
/*     */   public static final String OPTION_HELPER_CLASSNAME = "HelperClassName";
/* 101 */   private static final Class[] CORBA_OBJECT_CLASS = { org.omg.CORBA.Object.class };
/*     */ 
/*     */   protected java.lang.Object makeNewServiceObject(MessageContext msgContext, String clsName)
/*     */     throws Exception
/*     */   {
/*  71 */     String orbInitialHost = getStrOption("ORBInitialHost", msgContext.getService());
/*  72 */     if (orbInitialHost == null)
/*  73 */       orbInitialHost = "localhost";
/*  74 */     String orbInitialPort = getStrOption("ORBInitialPort", msgContext.getService());
/*  75 */     if (orbInitialPort == null)
/*  76 */       orbInitialPort = "900";
/*  77 */     String nameId = getStrOption("NameID", msgContext.getService());
/*  78 */     String nameKind = getStrOption("NameKind", msgContext.getService());
/*  79 */     String helperClassName = getStrOption("HelperClassName", msgContext.getService());
/*     */ 
/*  82 */     Properties orbProps = new Properties();
/*  83 */     orbProps.put("org.omg.CORBA.ORBInitialHost", orbInitialHost);
/*  84 */     orbProps.put("org.omg.CORBA.ORBInitialPort", orbInitialPort);
/*  85 */     ORB orb = ORB.init(new String[0], orbProps);
/*     */ 
/*  88 */     NamingContext root = NamingContextHelper.narrow(orb.resolve_initial_references("NameService"));
/*  89 */     NameComponent nc = new NameComponent(nameId, nameKind);
/*  90 */     NameComponent[] ncs = { nc };
/*  91 */     org.omg.CORBA.Object corbaObject = root.resolve(ncs);
/*     */ 
/*  93 */     Class helperClass = ClassUtils.forName(helperClassName);
/*     */ 
/*  95 */     Method narrowMethod = helperClass.getMethod("narrow", CORBA_OBJECT_CLASS);
/*  96 */     java.lang.Object targetObject = narrowMethod.invoke(null, new java.lang.Object[] { corbaObject });
/*     */ 
/*  98 */     return targetObject;
/*     */   }
/*     */ 
/*     */   protected String getServiceClassNameOptionName()
/*     */   {
/* 109 */     return "InterfaceClassName";
/*     */   }
/*     */ 
/*     */   protected String getStrOption(String optionName, Handler service)
/*     */   {
/* 124 */     String value = null;
/* 125 */     if (service != null)
/* 126 */       value = (String)service.getOption(optionName);
/* 127 */     if (value == null)
/* 128 */       value = (String)getOption(optionName);
/* 129 */     return value;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.providers.java.CORBAProvider
 * JD-Core Version:    0.6.0
 */