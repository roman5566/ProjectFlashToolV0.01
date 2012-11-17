/*     */ package org.apache.axis.client;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.net.URL;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.rpc.ServiceException;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.EngineConfiguration;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.deployment.wsdd.WSDDConstants;
/*     */ import org.apache.axis.message.SOAPBodyElement;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.Options;
/*     */ import org.apache.axis.utils.StringUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class AdminClient
/*     */ {
/*  48 */   protected static Log log = LogFactory.getLog(AdminClient.class.getName());
/*     */ 
/*  51 */   private static ThreadLocal defaultConfiguration = new ThreadLocal();
/*     */   protected Call call;
/* 202 */   protected static final String ROOT_UNDEPLOY = WSDDConstants.QNAME_UNDEPLOY.getLocalPart();
/*     */ 
/*     */   public static void setDefaultConfiguration(EngineConfiguration config)
/*     */   {
/*  63 */     defaultConfiguration.set(config);
/*     */   }
/*     */ 
/*     */   private static String getUsageInfo()
/*     */   {
/*  68 */     String lSep = System.getProperty("line.separator");
/*  69 */     StringBuffer msg = new StringBuffer();
/*     */ 
/*  71 */     msg.append(Messages.getMessage("acUsage00")).append(lSep);
/*  72 */     msg.append(Messages.getMessage("acUsage01")).append(lSep);
/*  73 */     msg.append(Messages.getMessage("acUsage02")).append(lSep);
/*  74 */     msg.append(Messages.getMessage("acUsage03")).append(lSep);
/*  75 */     msg.append(Messages.getMessage("acUsage04")).append(lSep);
/*  76 */     msg.append(Messages.getMessage("acUsage05")).append(lSep);
/*  77 */     msg.append(Messages.getMessage("acUsage06")).append(lSep);
/*  78 */     msg.append(Messages.getMessage("acUsage07")).append(lSep);
/*  79 */     msg.append(Messages.getMessage("acUsage08")).append(lSep);
/*  80 */     msg.append(Messages.getMessage("acUsage09")).append(lSep);
/*  81 */     msg.append(Messages.getMessage("acUsage10")).append(lSep);
/*  82 */     msg.append(Messages.getMessage("acUsage11")).append(lSep);
/*  83 */     msg.append(Messages.getMessage("acUsage12")).append(lSep);
/*  84 */     msg.append(Messages.getMessage("acUsage13")).append(lSep);
/*  85 */     msg.append(Messages.getMessage("acUsage14")).append(lSep);
/*  86 */     msg.append(Messages.getMessage("acUsage15")).append(lSep);
/*  87 */     msg.append(Messages.getMessage("acUsage16")).append(lSep);
/*  88 */     msg.append(Messages.getMessage("acUsage17")).append(lSep);
/*  89 */     msg.append(Messages.getMessage("acUsage18")).append(lSep);
/*  90 */     msg.append(Messages.getMessage("acUsage19")).append(lSep);
/*  91 */     msg.append(Messages.getMessage("acUsage20")).append(lSep);
/*  92 */     msg.append(Messages.getMessage("acUsage21")).append(lSep);
/*  93 */     msg.append(Messages.getMessage("acUsage22")).append(lSep);
/*  94 */     msg.append(Messages.getMessage("acUsage23")).append(lSep);
/*  95 */     msg.append(Messages.getMessage("acUsage24")).append(lSep);
/*  96 */     msg.append(Messages.getMessage("acUsage25")).append(lSep);
/*  97 */     msg.append(Messages.getMessage("acUsage26")).append(lSep);
/*  98 */     return msg.toString();
/*     */   }
/*     */ 
/*     */   public AdminClient()
/*     */   {
/*     */     try
/*     */     {
/* 116 */       initAdminClient();
/*     */     } catch (ServiceException e) {
/* 118 */       System.err.println(Messages.getMessage("couldntCall00") + ": " + e);
/* 119 */       this.call = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public AdminClient(boolean ignored)
/*     */     throws ServiceException
/*     */   {
/* 130 */     initAdminClient();
/*     */   }
/*     */ 
/*     */   private void initAdminClient()
/*     */     throws ServiceException
/*     */   {
/* 142 */     EngineConfiguration config = (EngineConfiguration)defaultConfiguration.get();
/*     */     Service service;
/*     */     Service service;
/* 145 */     if (config != null)
/* 146 */       service = new Service(config);
/*     */     else {
/* 148 */       service = new Service();
/*     */     }
/* 150 */     this.call = ((Call)service.createCall());
/*     */   }
/*     */ 
/*     */   public Call getCall()
/*     */   {
/* 162 */     return this.call;
/*     */   }
/*     */ 
/*     */   public String list(Options opts)
/*     */     throws Exception
/*     */   {
/* 172 */     processOpts(opts);
/* 173 */     return list();
/*     */   }
/*     */ 
/*     */   public String list()
/*     */     throws Exception
/*     */   {
/* 182 */     log.debug(Messages.getMessage("doList00"));
/* 183 */     String str = "<m:list xmlns:m=\"http://xml.apache.org/axis/wsdd/\"/>";
/* 184 */     ByteArrayInputStream input = new ByteArrayInputStream(str.getBytes());
/* 185 */     return process(input);
/*     */   }
/*     */ 
/*     */   public String quit(Options opts)
/*     */     throws Exception
/*     */   {
/* 195 */     processOpts(opts);
/* 196 */     return quit();
/*     */   }
/*     */ 
/*     */   public String quit()
/*     */     throws Exception
/*     */   {
/* 210 */     log.debug(Messages.getMessage("doQuit00"));
/* 211 */     String str = "<m:quit xmlns:m=\"http://xml.apache.org/axis/wsdd/\"/>";
/* 212 */     ByteArrayInputStream input = new ByteArrayInputStream(str.getBytes());
/* 213 */     return process(input);
/*     */   }
/*     */ 
/*     */   public String undeployHandler(String handlerName)
/*     */     throws Exception
/*     */   {
/* 223 */     log.debug(Messages.getMessage("doQuit00"));
/* 224 */     String str = "<m:" + ROOT_UNDEPLOY + " xmlns:m=\"" + "http://xml.apache.org/axis/wsdd/" + "\">" + "<handler name=\"" + handlerName + "\"/>" + "</m:" + ROOT_UNDEPLOY + ">";
/*     */ 
/* 227 */     ByteArrayInputStream input = new ByteArrayInputStream(str.getBytes());
/* 228 */     return process(input);
/*     */   }
/*     */ 
/*     */   public String undeployService(String serviceName)
/*     */     throws Exception
/*     */   {
/* 238 */     log.debug(Messages.getMessage("doQuit00"));
/* 239 */     String str = "<m:" + ROOT_UNDEPLOY + " xmlns:m=\"" + "http://xml.apache.org/axis/wsdd/" + "\">" + "<service name=\"" + serviceName + "\"/>" + "</m:" + ROOT_UNDEPLOY + ">";
/*     */ 
/* 242 */     ByteArrayInputStream input = new ByteArrayInputStream(str.getBytes());
/* 243 */     return process(input);
/*     */   }
/*     */ 
/*     */   public String process(String[] args)
/*     */     throws Exception
/*     */   {
/* 279 */     StringBuffer sb = new StringBuffer();
/*     */ 
/* 281 */     Options opts = new Options(args);
/* 282 */     opts.setDefaultURL("http://localhost:8080/axis/services/AdminService");
/*     */ 
/* 284 */     if (opts.isFlagSet('d') > 0);
/* 288 */     args = opts.getRemainingArgs();
/*     */ 
/* 290 */     if ((args == null) || (opts.isFlagSet('?') > 0)) {
/* 291 */       System.out.println(Messages.getMessage("usage00", "AdminClient [Options] [list | <deployment-descriptor-files>]"));
/* 292 */       System.out.println("");
/* 293 */       System.out.println(getUsageInfo());
/* 294 */       return null;
/*     */     }
/*     */ 
/* 297 */     for (int i = 0; i < args.length; i++) {
/* 298 */       InputStream input = null;
/*     */ 
/* 300 */       if (args[i].equals("list")) {
/* 301 */         sb.append(list(opts));
/* 302 */       } else if (args[i].equals("quit")) {
/* 303 */         sb.append(quit(opts));
/* 304 */       } else if (args[i].equals("passwd")) {
/* 305 */         System.out.println(Messages.getMessage("changePwd00"));
/* 306 */         if (args[(i + 1)] == null) {
/* 307 */           System.err.println(Messages.getMessage("needPwd00"));
/* 308 */           return null;
/*     */         }
/* 310 */         String str = "<m:passwd xmlns:m=\"http://xml.apache.org/axis/wsdd/\">";
/* 311 */         str = str + args[(i + 1)];
/* 312 */         str = str + "</m:passwd>";
/* 313 */         input = new ByteArrayInputStream(str.getBytes());
/* 314 */         i++;
/* 315 */         sb.append(process(opts, input));
/*     */       }
/* 318 */       else if (args[i].indexOf(File.pathSeparatorChar) == -1) {
/* 319 */         System.out.println(Messages.getMessage("processFile00", args[i]));
/* 320 */         sb.append(process(opts, args[i]));
/*     */       } else {
/* 322 */         StringTokenizer tokenizer = null;
/* 323 */         tokenizer = new StringTokenizer(args[i], File.pathSeparator);
/*     */ 
/* 325 */         while (tokenizer.hasMoreTokens()) {
/* 326 */           String file = tokenizer.nextToken();
/* 327 */           System.out.println(Messages.getMessage("processFile00", file));
/* 328 */           sb.append(process(opts, file));
/* 329 */           if (tokenizer.hasMoreTokens()) {
/* 330 */             sb.append("\n");
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 336 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public void processOpts(Options opts)
/*     */     throws Exception
/*     */   {
/* 345 */     if (this.call == null) {
/* 346 */       throw new Exception(Messages.getMessage("nullCall00"));
/*     */     }
/*     */ 
/* 349 */     URL address = new URL(opts.getURL());
/* 350 */     setTargetEndpointAddress(address);
/* 351 */     setLogin(opts.getUser(), opts.getPassword());
/*     */ 
/* 353 */     String tName = opts.isValueSet('t');
/* 354 */     setTransport(tName);
/*     */   }
/*     */ 
/*     */   public void setLogin(String user, String password)
/*     */   {
/* 364 */     this.call.setUsername(user);
/* 365 */     this.call.setPassword(password);
/*     */   }
/*     */ 
/*     */   public void setTargetEndpointAddress(URL address)
/*     */   {
/* 374 */     this.call.setTargetEndpointAddress(address);
/*     */   }
/*     */ 
/*     */   public void setTransport(String transportName)
/*     */   {
/* 383 */     if ((transportName != null) && (!transportName.equals("")))
/* 384 */       this.call.setProperty("transport_name", transportName);
/*     */   }
/*     */ 
/*     */   public String process(InputStream input) throws Exception
/*     */   {
/* 389 */     return process(null, input);
/*     */   }
/*     */ 
/*     */   public String process(URL xmlURL) throws Exception {
/* 393 */     return process(null, xmlURL.openStream());
/*     */   }
/*     */ 
/*     */   public String process(String xmlFile)
/*     */     throws Exception
/*     */   {
/* 403 */     FileInputStream in = new FileInputStream(xmlFile);
/* 404 */     String result = process(null, in);
/* 405 */     return result;
/*     */   }
/*     */ 
/*     */   public String process(Options opts, String xmlFile) throws Exception {
/* 409 */     processOpts(opts);
/* 410 */     return process(xmlFile);
/*     */   }
/*     */ 
/*     */   public String process(Options opts, InputStream input)
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 424 */       if (this.call == null)
/*     */       {
/* 426 */         throw new Exception(Messages.getMessage("nullCall00"));
/*     */       }
/*     */ 
/* 429 */       if (opts != null)
/*     */       {
/* 431 */         processOpts(opts);
/*     */       }
/*     */ 
/* 434 */       this.call.setUseSOAPAction(true);
/* 435 */       this.call.setSOAPActionURI("urn:AdminService");
/*     */ 
/* 437 */       Vector result = null;
/* 438 */       Object[] params = { new SOAPBodyElement(input) };
/* 439 */       result = (Vector)this.call.invoke(params);
/*     */ 
/* 441 */       if ((result == null) || (result.isEmpty())) {
/* 442 */         throw new AxisFault(Messages.getMessage("nullResponse00"));
/*     */       }
/*     */ 
/* 445 */       SOAPBodyElement body = (SOAPBodyElement)result.elementAt(0);
/* 446 */       String str = body.toString();
/*     */       return str; } finally { input.close(); } throw localObject;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*     */     try
/*     */     {
/* 461 */       AdminClient admin = new AdminClient();
/*     */ 
/* 463 */       String result = admin.process(args);
/* 464 */       if (result != null)
/* 465 */         System.out.println(StringUtils.unescapeNumericChar(result));
/*     */       else
/* 467 */         System.exit(1);
/*     */     }
/*     */     catch (AxisFault ae) {
/* 470 */       System.err.println(Messages.getMessage("exception00") + " " + ae.dumpToString());
/* 471 */       System.exit(1);
/*     */     } catch (Exception e) {
/* 473 */       System.err.println(Messages.getMessage("exception00") + " " + e.getMessage());
/* 474 */       System.exit(1);
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.client.AdminClient
 * JD-Core Version:    0.6.0
 */