/*     */ package org.apache.axis.transport.http;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Iterator;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.ConfigurationException;
/*     */ import org.apache.axis.EngineConfiguration;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.description.ServiceDesc;
/*     */ import org.apache.axis.handlers.soap.SOAPService;
/*     */ import org.apache.axis.server.AxisServer;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class AdminServlet extends AxisServletBase
/*     */ {
/*  50 */   private static Log log = LogFactory.getLog(AxisServlet.class.getName());
/*     */ 
/*     */   public void doGet(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/*  63 */     response.setContentType("text/html; charset=utf-8");
/*  64 */     StringBuffer buffer = new StringBuffer(512);
/*  65 */     buffer.append("<html><head><title>Axis</title></head><body>\n");
/*     */ 
/*  67 */     AxisServer server = getEngine();
/*     */ 
/*  70 */     String cmd = request.getParameter("cmd");
/*  71 */     if (cmd != null)
/*     */     {
/*  73 */       String callerIP = request.getRemoteAddr();
/*  74 */       if (isDevelopment())
/*     */       {
/*  76 */         if (cmd.equals("start")) {
/*  77 */           log.info(Messages.getMessage("adminServiceStart", callerIP));
/*  78 */           server.start();
/*     */         }
/*  80 */         else if (cmd.equals("stop")) {
/*  81 */           log.info(Messages.getMessage("adminServiceStop", callerIP));
/*  82 */           server.stop();
/*     */         }
/*  84 */         else if (cmd.equals("suspend")) {
/*  85 */           String name = request.getParameter("service");
/*  86 */           log.info(Messages.getMessage("adminServiceSuspend", name, callerIP));
/*  87 */           SOAPService service = server.getConfig().getService(new QName("", name));
/*  88 */           service.stop();
/*     */         }
/*  90 */         else if (cmd.equals("resume")) {
/*  91 */           String name = request.getParameter("service");
/*  92 */           log.info(Messages.getMessage("adminServiceResume", name, callerIP));
/*  93 */           SOAPService service = server.getConfig().getService(new QName("", name));
/*  94 */           service.start();
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/*  99 */         log.info(Messages.getMessage("adminServiceDeny", callerIP));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 104 */     if (server.isRunning()) {
/* 105 */       buffer.append("<H2>");
/* 106 */       buffer.append(Messages.getMessage("serverRun00"));
/* 107 */       buffer.append("</H2>");
/*     */     }
/*     */     else {
/* 110 */       buffer.append("<H2>");
/* 111 */       buffer.append(Messages.getMessage("serverStop00"));
/* 112 */       buffer.append("</H2>");
/*     */     }
/*     */ 
/* 115 */     if (isDevelopment()) {
/* 116 */       buffer.append("<p><a href=\"AdminServlet?cmd=start\">start server</a>\n");
/* 117 */       buffer.append("<p><a href=\"AdminServlet?cmd=stop\">stop server</a>\n");
/*     */       try
/*     */       {
/* 121 */         i = server.getConfig().getDeployedServices();
/*     */       }
/*     */       catch (ConfigurationException configException)
/*     */       {
/*     */         Iterator i;
/* 125 */         if ((configException.getContainedException() instanceof AxisFault)) {
/* 126 */           throw ((AxisFault)configException.getContainedException());
/*     */         }
/* 128 */         throw configException;
/*     */       }
/*     */       Iterator i;
/* 132 */       buffer.append("<p><h2>Services</h2>");
/* 133 */       buffer.append("<ul>");
/* 134 */       while (i.hasNext()) {
/* 135 */         ServiceDesc sd = (ServiceDesc)i.next();
/* 136 */         StringBuffer sb = new StringBuffer();
/* 137 */         sb.append("<li>");
/* 138 */         String name = sd.getName();
/* 139 */         sb.append(name);
/* 140 */         SOAPService service = server.getConfig().getService(new QName("", name));
/* 141 */         if (service.isRunning())
/* 142 */           sb.append("&nbsp;&nbsp;<a href=\"AdminServlet?cmd=suspend&service=" + name + "\">suspend</a>\n");
/*     */         else {
/* 144 */           sb.append("&nbsp;&nbsp;<a href=\"AdminServlet?cmd=resume&service=" + name + "\">resume</a>\n");
/*     */         }
/* 146 */         sb.append("</li>");
/* 147 */         buffer.append(sb.toString());
/*     */       }
/* 149 */       buffer.append("</ul>");
/*     */     }
/*     */ 
/* 152 */     buffer.append("<p>");
/* 153 */     buffer.append(Messages.getMessage("adminServiceLoad", Integer.toString(getLoadCounter())));
/*     */ 
/* 155 */     buffer.append("\n</body></html>\n");
/* 156 */     response.getWriter().print(new String(buffer));
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.http.AdminServlet
 * JD-Core Version:    0.6.0
 */