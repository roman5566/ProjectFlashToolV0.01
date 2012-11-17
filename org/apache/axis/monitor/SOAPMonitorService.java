/*     */ package org.apache.axis.monitor;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ import javax.servlet.ServletConfig;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.HttpServlet;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ 
/*     */ public class SOAPMonitorService extends HttpServlet
/*     */ {
/*  68 */   private static ServerSocket server_socket = null;
/*  69 */   private static Vector connections = null;
/*     */ 
/*     */   public static void publishMessage(Long id, Integer type, String target, String soap)
/*     */   {
/*  85 */     if (connections != null) {
/*  86 */       Enumeration e = connections.elements();
/*  87 */       while (e.hasMoreElements()) {
/*  88 */         ConnectionThread ct = (ConnectionThread)e.nextElement();
/*  89 */         ct.publishMessage(id, type, target, soap);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void init()
/*     */     throws ServletException
/*     */   {
/*  98 */     if (connections == null)
/*     */     {
/* 100 */       connections = new Vector();
/*     */     }
/* 102 */     if (server_socket == null)
/*     */     {
/* 104 */       ServletConfig config = super.getServletConfig();
/* 105 */       String port = config.getInitParameter("SOAPMonitorPort");
/* 106 */       if (port == null)
/*     */       {
/* 108 */         port = "0";
/*     */       }
/*     */       try
/*     */       {
/* 112 */         server_socket = new ServerSocket(Integer.parseInt(port));
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 116 */         server_socket = null;
/*     */       }
/* 118 */       if (server_socket != null)
/*     */       {
/* 120 */         new Thread(new ServerSocketThread()).start();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 130 */     Enumeration e = connections.elements();
/* 131 */     while (e.hasMoreElements()) {
/* 132 */       ConnectionThread ct = (ConnectionThread)e.nextElement();
/* 133 */       ct.close();
/*     */     }
/*     */ 
/* 136 */     if (server_socket != null) {
/*     */       try {
/* 138 */         server_socket.close(); } catch (Exception x) {
/*     */       }
/* 140 */       server_socket = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void doGet(HttpServletRequest request, HttpServletResponse response)
/*     */     throws IOException, ServletException
/*     */   {
/* 151 */     int port = 0;
/* 152 */     if (server_socket != null) {
/* 153 */       port = server_socket.getLocalPort();
/*     */     }
/* 155 */     response.setContentType("text/html");
/* 156 */     response.getWriter().println("<html>");
/* 157 */     response.getWriter().println("<head>");
/* 158 */     response.getWriter().println("<title>SOAP Monitor</title>");
/* 159 */     response.getWriter().println("</head>");
/* 160 */     response.getWriter().println("<body>");
/* 161 */     response.getWriter().println("<object classid=\"clsid:8AD9C840-044E-11D1-B3E9-00805F499D93\" width=100% height=100% codebase=\"http://java.sun.com/products/plugin/1.3/jinstall-13-win32.cab#Version=1,3,0,0\">");
/* 162 */     response.getWriter().println("<param name=code value=SOAPMonitorApplet.class>");
/* 163 */     response.getWriter().println("<param name=\"type\" value=\"application/x-java-applet;version=1.3\">");
/* 164 */     response.getWriter().println("<param name=\"scriptable\" value=\"false\">");
/* 165 */     response.getWriter().println("<param name=\"port\" value=\"" + port + "\">");
/* 166 */     response.getWriter().println("<comment>");
/* 167 */     response.getWriter().println("<embed type=\"application/x-java-applet;version=1.3\" code=SOAPMonitorApplet.class width=100% height=100% port=\"" + port + "\" scriptable=false pluginspage=\"http://java.sun.com/products/plugin/1.3/plugin-install.html\">");
/* 168 */     response.getWriter().println("<noembed>");
/* 169 */     response.getWriter().println("</comment>");
/* 170 */     response.getWriter().println("</noembed>");
/* 171 */     response.getWriter().println("</embed>");
/* 172 */     response.getWriter().println("</object>");
/* 173 */     response.getWriter().println("</body>");
/* 174 */     response.getWriter().println("</html>");
/*     */   }
/*     */ 
/*     */   class ConnectionThread
/*     */     implements Runnable
/*     */   {
/* 201 */     private Socket socket = null;
/* 202 */     private ObjectInputStream in = null;
/* 203 */     private ObjectOutputStream out = null;
/* 204 */     private boolean closed = false;
/*     */ 
/*     */     public ConnectionThread(Socket s)
/*     */     {
/* 210 */       this.socket = s;
/*     */       try
/*     */       {
/* 218 */         this.out = new ObjectOutputStream(this.socket.getOutputStream());
/* 219 */         this.out.flush();
/* 220 */         this.in = new ObjectInputStream(this.socket.getInputStream());
/*     */       } catch (Exception e) {
/*     */       }
/* 223 */       synchronized (SOAPMonitorService.connections) {
/* 224 */         SOAPMonitorService.connections.addElement(this);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void close()
/*     */     {
/* 232 */       this.closed = true;
/*     */       try {
/* 234 */         this.socket.close();
/*     */       }
/*     */       catch (IOException ioe)
/*     */       {
/*     */       }
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       try
/*     */       {
/*     */         Object o;
/* 243 */         while (!this.closed)
/* 244 */           o = this.in.readObject();
/*     */       }
/*     */       catch (Exception e) {
/*     */       }
/* 248 */       synchronized (SOAPMonitorService.connections) {
/* 249 */         SOAPMonitorService.connections.removeElement(this);
/*     */       }
/*     */ 
/* 252 */       if (this.out != null) {
/*     */         try {
/* 254 */           this.out.close(); } catch (IOException ioe) {
/*     */         }
/* 256 */         this.out = null;
/*     */       }
/* 258 */       if (this.in != null) {
/*     */         try {
/* 260 */           this.in.close(); } catch (IOException ioe) {
/*     */         }
/* 262 */         this.in = null;
/*     */       }
/*     */ 
/* 265 */       close();
/*     */     }
/*     */ 
/*     */     public synchronized void publishMessage(Long id, Integer message_type, String target, String soap)
/*     */     {
/* 277 */       if (this.out != null)
/*     */         try {
/* 279 */           switch (message_type.intValue()) {
/*     */           case 0:
/* 281 */             this.out.writeObject(message_type);
/* 282 */             this.out.writeObject(id);
/* 283 */             this.out.writeObject(target);
/* 284 */             this.out.writeObject(soap);
/* 285 */             this.out.flush();
/* 286 */             break;
/*     */           case 1:
/* 288 */             this.out.writeObject(message_type);
/* 289 */             this.out.writeObject(id);
/* 290 */             this.out.writeObject(soap);
/* 291 */             this.out.flush();
/*     */           }
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   class ServerSocketThread
/*     */     implements Runnable
/*     */   {
/*     */     ServerSocketThread()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 187 */       while (SOAPMonitorService.server_socket != null)
/*     */         try {
/* 189 */           Socket socket = SOAPMonitorService.server_socket.accept();
/* 190 */           new Thread(new SOAPMonitorService.ConnectionThread(SOAPMonitorService.this, socket)).start();
/*     */         }
/*     */         catch (IOException ioe)
/*     */         {
/*     */         }
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.monitor.SOAPMonitorService
 * JD-Core Version:    0.6.0
 */