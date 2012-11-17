/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import java.io.FileInputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import org.apache.axis.AxisEngine;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.EngineConfiguration;
/*     */ import org.apache.axis.Handler;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.WSDDEngineConfiguration;
/*     */ import org.apache.axis.client.AxisClient;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.deployment.wsdd.WSDDDeployment;
/*     */ import org.apache.axis.deployment.wsdd.WSDDDocument;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.server.AxisServer;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.xml.sax.InputSource;
/*     */ 
/*     */ public class Admin
/*     */ {
/*  54 */   protected static Log log = LogFactory.getLog(Admin.class.getName());
/*     */ 
/*     */   public Element[] AdminService(Element[] xml)
/*     */     throws Exception
/*     */   {
/*  63 */     log.debug("Enter: Admin::AdminService");
/*  64 */     MessageContext msgContext = MessageContext.getCurrentContext();
/*  65 */     Document doc = process(msgContext, xml[0]);
/*  66 */     Element[] result = new Element[1];
/*  67 */     result[0] = doc.getDocumentElement();
/*  68 */     log.debug("Exit: Admin::AdminService");
/*  69 */     return result;
/*     */   }
/*     */ 
/*     */   protected static Document processWSDD(MessageContext msgContext, AxisEngine engine, Element root)
/*     */     throws Exception
/*     */   {
/*  77 */     Document doc = null;
/*     */ 
/*  79 */     String action = root.getLocalName();
/*  80 */     if (action.equals("passwd")) {
/*  81 */       String newPassword = root.getFirstChild().getNodeValue();
/*  82 */       engine.setAdminPassword(newPassword);
/*  83 */       doc = XMLUtils.newDocument();
/*  84 */       doc.appendChild(root = doc.createElementNS("", "Admin"));
/*  85 */       root.appendChild(doc.createTextNode(Messages.getMessage("done00")));
/*  86 */       return doc;
/*     */     }
/*     */ 
/*  89 */     if (action.equals("quit")) {
/*  90 */       log.error(Messages.getMessage("quitRequest00"));
/*  91 */       if (msgContext != null)
/*     */       {
/*  94 */         msgContext.setProperty("quit.requested", "true");
/*     */       }
/*  96 */       doc = XMLUtils.newDocument();
/*  97 */       doc.appendChild(root = doc.createElementNS("", "Admin"));
/*  98 */       root.appendChild(doc.createTextNode(Messages.getMessage("quit00", "")));
/*  99 */       return doc;
/*     */     }
/*     */ 
/* 102 */     if (action.equals("list")) {
/* 103 */       return listConfig(engine);
/*     */     }
/*     */ 
/* 106 */     if (action.equals("clientdeploy"))
/*     */     {
/* 108 */       engine = engine.getClientEngine();
/*     */     }
/*     */ 
/* 111 */     WSDDDocument wsddDoc = new WSDDDocument(root);
/* 112 */     EngineConfiguration config = engine.getConfig();
/* 113 */     if ((config instanceof WSDDEngineConfiguration)) {
/* 114 */       WSDDDeployment deployment = ((WSDDEngineConfiguration)config).getDeployment();
/*     */ 
/* 116 */       wsddDoc.deploy(deployment);
/*     */     }
/* 118 */     engine.refreshGlobalOptions();
/*     */ 
/* 120 */     engine.saveConfiguration();
/*     */ 
/* 122 */     doc = XMLUtils.newDocument();
/* 123 */     doc.appendChild(root = doc.createElementNS("", "Admin"));
/* 124 */     root.appendChild(doc.createTextNode(Messages.getMessage("done00")));
/*     */ 
/* 126 */     return doc;
/*     */   }
/*     */ 
/*     */   public Document process(MessageContext msgContext, Element root)
/*     */     throws Exception
/*     */   {
/* 151 */     verifyHostAllowed(msgContext);
/*     */ 
/* 153 */     String rootNS = root.getNamespaceURI();
/* 154 */     AxisEngine engine = msgContext.getAxisEngine();
/*     */ 
/* 157 */     if ((rootNS != null) && (rootNS.equals("http://xml.apache.org/axis/wsdd/"))) {
/* 158 */       return processWSDD(msgContext, engine, root);
/*     */     }
/*     */ 
/* 163 */     throw new Exception(Messages.getMessage("adminServiceNoWSDD"));
/*     */   }
/*     */ 
/*     */   private void verifyHostAllowed(MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/* 175 */     Handler serviceHandler = msgContext.getService();
/* 176 */     if ((serviceHandler != null) && (!JavaUtils.isTrueExplicitly(serviceHandler.getOption("enableRemoteAdmin"))))
/*     */     {
/* 179 */       String remoteIP = msgContext.getStrProp("remoteaddr");
/* 180 */       if ((remoteIP != null) && (!remoteIP.equals("127.0.0.1")) && (!remoteIP.equals("0:0:0:0:0:0:0:1")))
/*     */       {
/*     */         try
/*     */         {
/* 185 */           InetAddress myAddr = InetAddress.getLocalHost();
/* 186 */           InetAddress remoteAddr = InetAddress.getByName(remoteIP);
/*     */ 
/* 188 */           if (log.isDebugEnabled()) {
/* 189 */             log.debug("Comparing remote caller " + remoteAddr + " to " + myAddr);
/*     */           }
/*     */ 
/* 193 */           if (!myAddr.equals(remoteAddr)) {
/* 194 */             log.error(Messages.getMessage("noAdminAccess01", remoteAddr.toString()));
/*     */ 
/* 196 */             throw new AxisFault("Server.Unauthorized", Messages.getMessage("noAdminAccess00"), null, null);
/*     */           }
/*     */         }
/*     */         catch (UnknownHostException e)
/*     */         {
/* 201 */           throw new AxisFault("Server.UnknownHost", Messages.getMessage("unknownHost00"), null, null);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Document listConfig(AxisEngine engine)
/*     */     throws AxisFault
/*     */   {
/* 221 */     StringWriter writer = new StringWriter();
/* 222 */     SerializationContext context = new SerializationContext(writer);
/* 223 */     context.setPretty(true);
/*     */     try {
/* 225 */       EngineConfiguration config = engine.getConfig();
/*     */ 
/* 227 */       if ((config instanceof WSDDEngineConfiguration)) {
/* 228 */         WSDDDeployment deployment = ((WSDDEngineConfiguration)config).getDeployment();
/*     */ 
/* 230 */         deployment.writeToContext(context);
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 236 */       throw new AxisFault(Messages.getMessage("noEngineWSDD"));
/*     */     }
/*     */     try
/*     */     {
/* 240 */       writer.close();
/* 241 */       return XMLUtils.newDocument(new InputSource(new StringReader(writer.getBuffer().toString())));
/*     */     } catch (Exception e) {
/* 243 */       log.error("exception00", e);
/* 244 */     }return null;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) throws Exception
/*     */   {
/* 249 */     int i = 0;
/*     */ 
/* 251 */     if ((args.length < 2) || ((!args[0].equals("client")) && (!args[0].equals("server"))))
/*     */     {
/* 253 */       log.error(Messages.getMessage("usage00", "Admin client|server <xml-file>"));
/*     */ 
/* 255 */       log.error(Messages.getMessage("where00", "<xml-file>"));
/* 256 */       log.error("<deploy>");
/*     */ 
/* 261 */       log.error("  <handler name=a class=className/>");
/* 262 */       log.error("  <chain name=a flow=\"a,b,c\" />");
/* 263 */       log.error("  <chain name=a request=\"a,b,c\" pivot=\"d\"");
/* 264 */       log.error("                  response=\"e,f,g\" />");
/* 265 */       log.error("  <service name=a handler=b />");
/* 266 */       log.error("</deploy>");
/* 267 */       log.error("<undeploy>");
/* 268 */       log.error("  <handler name=a/>");
/* 269 */       log.error("  <chain name=a/>");
/* 270 */       log.error("  <service name=a/>");
/* 271 */       log.error("</undeploy>");
/* 272 */       log.error("<list/>");
/*     */ 
/* 277 */       throw new IllegalArgumentException(Messages.getMessage("usage00", "Admin client|server <xml-file>"));
/*     */     }
/*     */ 
/* 283 */     Admin admin = new Admin();
/*     */     AxisEngine engine;
/*     */     AxisEngine engine;
/* 286 */     if (args[0].equals("client"))
/* 287 */       engine = new AxisClient();
/*     */     else
/* 289 */       engine = new AxisServer();
/* 290 */     engine.setShouldSaveConfig(true);
/* 291 */     engine.init();
/* 292 */     MessageContext msgContext = new MessageContext(engine);
/*     */     try
/*     */     {
/* 295 */       for (i = 1; i < args.length; i++) {
/* 296 */         if (log.isDebugEnabled()) {
/* 297 */           log.debug(Messages.getMessage("process00", args[i]));
/*     */         }
/* 299 */         Document doc = XMLUtils.newDocument(new FileInputStream(args[i]));
/* 300 */         Document result = admin.process(msgContext, doc.getDocumentElement());
/* 301 */         if (result != null)
/* 302 */           System.out.println(XMLUtils.DocumentToString(result));
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 307 */       log.error(Messages.getMessage("errorProcess00", args[i]), e);
/* 308 */       throw e;
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.Admin
 * JD-Core Version:    0.6.0
 */