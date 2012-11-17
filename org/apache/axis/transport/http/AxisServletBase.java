/*     */ package org.apache.axis.transport.http;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.servlet.ServletConfig;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.HttpServlet;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.AxisProperties;
/*     */ import org.apache.axis.EngineConfiguration;
/*     */ import org.apache.axis.EngineConfigurationFactory;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.configuration.EngineConfigurationFactoryFinder;
/*     */ import org.apache.axis.server.AxisServer;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class AxisServletBase extends HttpServlet
/*     */ {
/*  56 */   protected AxisServer axisServer = null;
/*     */ 
/*  58 */   private static Log log = LogFactory.getLog(AxisServlet.class.getName());
/*     */ 
/*  61 */   private static boolean isDebug = false;
/*     */ 
/*  66 */   private static int loadCounter = 0;
/*     */ 
/*  71 */   private static Object loadCounterLock = new Object();
/*     */   protected static final String ATTR_AXIS_ENGINE = "AxisEngine";
/*  82 */   private String webInfPath = null;
/*     */ 
/*  87 */   private String homeDir = null;
/*     */   private boolean isDevelopment;
/*     */   private static final String INIT_PROPERTY_DEVELOPMENT_SYSTEM = "axis.development.system";
/*     */ 
/*     */   public void init()
/*     */     throws ServletException
/*     */   {
/* 105 */     ServletContext context = getServletConfig().getServletContext();
/*     */ 
/* 107 */     this.webInfPath = context.getRealPath("/WEB-INF");
/* 108 */     this.homeDir = context.getRealPath("/");
/*     */ 
/* 110 */     isDebug = log.isDebugEnabled();
/* 111 */     if (log.isDebugEnabled()) log.debug("In AxisServletBase init");
/* 112 */     this.isDevelopment = JavaUtils.isTrueExplicitly(getOption(context, "axis.development.system", null));
/*     */   }
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 127 */     super.destroy();
/*     */ 
/* 130 */     if (this.axisServer != null)
/*     */     {
/* 132 */       synchronized (this.axisServer) {
/* 133 */         if (this.axisServer != null)
/*     */         {
/* 135 */           this.axisServer.cleanup();
/*     */ 
/* 137 */           this.axisServer = null;
/* 138 */           storeEngine(this, null);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public AxisServer getEngine()
/*     */     throws AxisFault
/*     */   {
/* 150 */     if (this.axisServer == null)
/* 151 */       this.axisServer = getEngine(this);
/* 152 */     return this.axisServer;
/*     */   }
/*     */ 
/*     */   public static AxisServer getEngine(HttpServlet servlet)
/*     */     throws AxisFault
/*     */   {
/* 164 */     AxisServer engine = null;
/* 165 */     if (isDebug) {
/* 166 */       log.debug("Enter: getEngine()");
/*     */     }
/* 168 */     ServletContext context = servlet.getServletContext();
/* 169 */     synchronized (context) {
/* 170 */       engine = retrieveEngine(servlet);
/* 171 */       if (engine == null) {
/* 172 */         Map environment = getEngineEnvironment(servlet);
/*     */ 
/* 185 */         engine = AxisServer.getServer(environment);
/*     */ 
/* 187 */         engine.setName(servlet.getServletName());
/* 188 */         storeEngine(servlet, engine);
/*     */       }
/*     */     }
/*     */ 
/* 192 */     if (isDebug) {
/* 193 */       log.debug("Exit: getEngine()");
/*     */     }
/* 195 */     return engine;
/*     */   }
/*     */ 
/*     */   private static void storeEngine(HttpServlet servlet, AxisServer engine)
/*     */   {
/* 204 */     ServletContext context = servlet.getServletContext();
/* 205 */     String axisServletName = servlet.getServletName();
/* 206 */     if (engine == null) {
/* 207 */       context.removeAttribute(axisServletName + "AxisEngine");
/*     */ 
/* 209 */       AxisServer server = (AxisServer)context.getAttribute("AxisEngine");
/*     */ 
/* 212 */       if ((server != null) && (servlet.getServletName().equals(server.getName())))
/* 213 */         context.removeAttribute("AxisEngine");
/*     */     }
/*     */     else {
/* 216 */       if (context.getAttribute("AxisEngine") == null)
/*     */       {
/* 219 */         context.setAttribute("AxisEngine", engine);
/*     */       }
/* 221 */       context.setAttribute(axisServletName + "AxisEngine", engine);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static AxisServer retrieveEngine(HttpServlet servlet)
/*     */   {
/* 235 */     Object contextObject = servlet.getServletContext().getAttribute(servlet.getServletName() + "AxisEngine");
/* 236 */     if (contextObject == null)
/*     */     {
/* 239 */       contextObject = servlet.getServletContext().getAttribute("AxisEngine");
/*     */     }
/* 241 */     if ((contextObject instanceof AxisServer)) {
/* 242 */       AxisServer server = (AxisServer)contextObject;
/*     */ 
/* 244 */       if ((server != null) && (servlet.getServletName().equals(server.getName()))) {
/* 245 */         return server;
/*     */       }
/* 247 */       return null;
/*     */     }
/* 249 */     return null;
/*     */   }
/*     */ 
/*     */   protected static Map getEngineEnvironment(HttpServlet servlet)
/*     */   {
/* 259 */     Map environment = new HashMap();
/*     */ 
/* 261 */     String attdir = servlet.getInitParameter("axis.attachments.Directory");
/* 262 */     if (attdir != null) {
/* 263 */       environment.put("axis.attachments.Directory", attdir);
/*     */     }
/* 265 */     ServletContext context = servlet.getServletContext();
/* 266 */     environment.put("servletContext", context);
/*     */ 
/* 268 */     String webInfPath = context.getRealPath("/WEB-INF");
/* 269 */     if (webInfPath != null) {
/* 270 */       environment.put("servlet.realpath", webInfPath + File.separator + "attachments");
/*     */     }
/*     */ 
/* 273 */     EngineConfiguration config = EngineConfigurationFactoryFinder.newFactory(servlet).getServerEngineConfig();
/*     */ 
/* 277 */     if (config != null) {
/* 278 */       environment.put("engineConfig", config);
/*     */     }
/*     */ 
/* 281 */     return environment;
/*     */   }
/*     */ 
/*     */   public static int getLoadCounter()
/*     */   {
/* 293 */     return loadCounter;
/*     */   }
/*     */ 
/*     */   protected static void incLockCounter()
/*     */   {
/* 300 */     synchronized (loadCounterLock) {
/* 301 */       loadCounter += 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static void decLockCounter()
/*     */   {
/* 309 */     synchronized (loadCounterLock) {
/* 310 */       loadCounter -= 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void service(HttpServletRequest req, HttpServletResponse resp)
/*     */     throws ServletException, IOException
/*     */   {
/* 325 */     incLockCounter();
/*     */     try {
/* 327 */       super.service(req, resp);
/*     */     }
/*     */     finally {
/* 330 */       decLockCounter();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String getWebappBase(HttpServletRequest request)
/*     */   {
/* 341 */     StringBuffer baseURL = new StringBuffer(128);
/* 342 */     baseURL.append(request.getScheme());
/* 343 */     baseURL.append("://");
/* 344 */     baseURL.append(request.getServerName());
/* 345 */     if (request.getServerPort() != 80) {
/* 346 */       baseURL.append(":");
/* 347 */       baseURL.append(request.getServerPort());
/*     */     }
/* 349 */     baseURL.append(request.getContextPath());
/* 350 */     return baseURL.toString();
/*     */   }
/*     */ 
/*     */   public ServletContext getServletContext()
/*     */   {
/* 358 */     return getServletConfig().getServletContext();
/*     */   }
/*     */ 
/*     */   protected String getWebInfPath()
/*     */   {
/* 366 */     return this.webInfPath;
/*     */   }
/*     */ 
/*     */   protected String getHomeDir()
/*     */   {
/* 374 */     return this.homeDir;
/*     */   }
/*     */ 
/*     */   protected String getOption(ServletContext context, String param, String dephault)
/*     */   {
/* 388 */     String value = AxisProperties.getProperty(param);
/*     */ 
/* 390 */     if (value == null) {
/* 391 */       value = getInitParameter(param);
/*     */     }
/* 393 */     if (value == null)
/* 394 */       value = context.getInitParameter(param);
/*     */     try {
/* 396 */       AxisServer engine = getEngine(this);
/* 397 */       if ((value == null) && (engine != null))
/* 398 */         value = (String)engine.getOption(param);
/*     */     }
/*     */     catch (AxisFault axisFault) {
/*     */     }
/* 402 */     return value != null ? value : dephault;
/*     */   }
/*     */ 
/*     */   public boolean isDevelopment()
/*     */   {
/* 410 */     return this.isDevelopment;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.http.AxisServletBase
 * JD-Core Version:    0.6.0
 */