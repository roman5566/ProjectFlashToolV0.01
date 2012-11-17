/*     */ package org.apache.axis.client;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.net.JarURLConnection;
/*     */ import java.net.URL;
/*     */ import java.security.CodeSource;
/*     */ import java.security.ProtectionDomain;
/*     */ import javax.xml.parsers.SAXParser;
/*     */ import javax.xml.parsers.SAXParserFactory;
/*     */ import org.apache.axis.utils.ClassUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class HappyClient
/*     */ {
/*     */   PrintStream out;
/*     */ 
/*     */   public HappyClient(PrintStream out)
/*     */   {
/*  36 */     this.out = out;
/*     */   }
/*     */ 
/*     */   Class classExists(String classname)
/*     */   {
/*     */     try
/*     */     {
/*  46 */       return Class.forName(classname); } catch (ClassNotFoundException e) {
/*     */     }
/*  48 */     return null;
/*     */   }
/*     */ 
/*     */   boolean resourceExists(String resource)
/*     */   {
/*  59 */     InputStream instream = ClassUtils.getResourceAsStream(getClass(), resource);
/*  60 */     boolean found = instream != null;
/*  61 */     if (instream != null)
/*     */       try {
/*  63 */         instream.close();
/*     */       }
/*     */       catch (IOException e) {
/*     */       }
/*  67 */     return found;
/*     */   }
/*     */ 
/*     */   int probeClass(String category, String classname, String jarFile, String description, String errorText, String homePage)
/*     */     throws IOException
/*     */   {
/*  87 */     String url = "";
/*  88 */     if (homePage != null) {
/*  89 */       url = Messages.getMessage("happyClientHomepage", homePage);
/*     */     }
/*  91 */     String errorLine = "";
/*  92 */     if (errorText != null)
/*  93 */       errorLine = Messages.getMessage(errorText);
/*     */     try
/*     */     {
/*  96 */       Class clazz = classExists(classname);
/*  97 */       if (clazz == null)
/*     */       {
/*  99 */         String text = Messages.getMessage("happyClientMissingClass", category, classname, jarFile);
/*     */ 
/* 101 */         this.out.println(text);
/* 102 */         this.out.println(url);
/* 103 */         return 1;
/*     */       }
/* 105 */       String location = getLocation(clazz);
/*     */       String text;
/*     */       String text;
/* 107 */       if (location == null) {
/* 108 */         text = Messages.getMessage("happyClientFoundDescriptionClass", description, classname);
/*     */       }
/*     */       else {
/* 111 */         text = Messages.getMessage("happyClientFoundDescriptionClassLocation", description, classname, location);
/*     */       }
/*     */ 
/* 114 */       this.out.println(text);
/* 115 */       return 0;
/*     */     }
/*     */     catch (NoClassDefFoundError ncdfe) {
/* 118 */       this.out.println(Messages.getMessage("happyClientNoDependency", category, classname, jarFile));
/*     */ 
/* 120 */       this.out.println(errorLine);
/* 121 */       this.out.println(url);
/* 122 */       this.out.println(ncdfe.getMessage());
/* 123 */     }return 1;
/*     */   }
/*     */ 
/*     */   String getLocation(Class clazz)
/*     */   {
/*     */     try
/*     */     {
/* 136 */       URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
/* 137 */       String location = url.toString();
/* 138 */       if (location.startsWith("jar")) {
/* 139 */         url = ((JarURLConnection)url.openConnection()).getJarFileURL();
/* 140 */         location = url.toString();
/*     */       }
/*     */ 
/* 143 */       if (location.startsWith("file")) {
/* 144 */         File file = new File(url.getFile());
/* 145 */         return file.getAbsolutePath();
/*     */       }
/* 147 */       return url.toString();
/*     */     }
/*     */     catch (Throwable t) {
/*     */     }
/* 151 */     return Messages.getMessage("happyClientUnknownLocation");
/*     */   }
/*     */ 
/*     */   int needClass(String classname, String jarFile, String description, String errorText, String homePage)
/*     */     throws IOException
/*     */   {
/* 169 */     return probeClass(Messages.getMessage("happyClientError"), classname, jarFile, description, errorText, homePage);
/*     */   }
/*     */ 
/*     */   int wantClass(String classname, String jarFile, String description, String errorText, String homePage)
/*     */     throws IOException
/*     */   {
/* 193 */     return probeClass(Messages.getMessage("happyClientWarning"), classname, jarFile, description, errorText, homePage);
/*     */   }
/*     */ 
/*     */   int wantResource(String resource, String errorText)
/*     */     throws Exception
/*     */   {
/* 211 */     if (!resourceExists(resource)) {
/* 212 */       this.out.println(Messages.getMessage("happyClientNoResource", resource));
/* 213 */       this.out.println(errorText);
/* 214 */       return 0;
/*     */     }
/* 216 */     this.out.println(Messages.getMessage("happyClientFoundResource", resource));
/* 217 */     return 1;
/*     */   }
/*     */ 
/*     */   private String getParserName()
/*     */   {
/* 227 */     SAXParser saxParser = getSAXParser();
/* 228 */     if (saxParser == null) {
/* 229 */       return Messages.getMessage("happyClientNoParser");
/*     */     }
/*     */ 
/* 233 */     String saxParserName = saxParser.getClass().getName();
/* 234 */     return saxParserName;
/*     */   }
/*     */ 
/*     */   private SAXParser getSAXParser()
/*     */   {
/* 242 */     SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
/* 243 */     if (saxParserFactory == null) {
/* 244 */       return null;
/*     */     }
/* 246 */     SAXParser saxParser = null;
/*     */     try {
/* 248 */       saxParser = saxParserFactory.newSAXParser();
/*     */     } catch (Exception e) {
/*     */     }
/* 251 */     return saxParser;
/*     */   }
/*     */ 
/*     */   private String getParserLocation()
/*     */   {
/* 260 */     SAXParser saxParser = getSAXParser();
/* 261 */     if (saxParser == null) {
/* 262 */       return null;
/*     */     }
/* 264 */     String location = getLocation(saxParser.getClass());
/* 265 */     return location;
/*     */   }
/*     */ 
/*     */   public int getJavaVersionNumber()
/*     */   {
/* 280 */     int javaVersionNumber = 10;
/*     */     try {
/* 282 */       Class.forName("java.lang.Void");
/* 283 */       javaVersionNumber++;
/* 284 */       Class.forName("java.lang.ThreadLocal");
/* 285 */       javaVersionNumber++;
/* 286 */       Class.forName("java.lang.StrictMath");
/* 287 */       javaVersionNumber++;
/* 288 */       Class.forName("java.lang.CharSequence");
/* 289 */       javaVersionNumber++;
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*     */     }
/* 294 */     return javaVersionNumber;
/*     */   }
/*     */ 
/*     */   private void title(String title)
/*     */   {
/* 299 */     this.out.println();
/* 300 */     String message = Messages.getMessage(title);
/* 301 */     this.out.println(message);
/*     */ 
/* 303 */     for (int i = 0; i < message.length(); i++) {
/* 304 */       this.out.print("=");
/*     */     }
/* 306 */     this.out.println();
/*     */   }
/*     */ 
/*     */   public boolean verifyClientIsHappy(boolean warningsAsErrors)
/*     */     throws IOException
/*     */   {
/* 315 */     int needed = 0; int wanted = 0;
/* 316 */     this.out.println();
/* 317 */     title("happyClientTitle");
/* 318 */     title("happyClientNeeded");
/*     */ 
/* 323 */     needed = needClass("javax.xml.soap.SOAPMessage", "saaj.jar", "SAAJ", "happyClientNoAxis", "http://xml.apache.org/axis/");
/*     */ 
/* 329 */     needed += needClass("javax.xml.rpc.Service", "jaxrpc.jar", "JAX-RPC", "happyClientNoAxis", "http://xml.apache.org/axis/");
/*     */ 
/* 335 */     needed += needClass("org.apache.commons.discovery.Resource", "commons-discovery.jar", "Jakarta-Commons Discovery", "happyClientNoAxis", "http://jakarta.apache.org/commons/discovery.html");
/*     */ 
/* 341 */     needed += needClass("org.apache.commons.logging.Log", "commons-logging.jar", "Jakarta-Commons Logging", "happyClientNoAxis", "http://jakarta.apache.org/commons/logging.html");
/*     */ 
/* 348 */     needed += needClass("org.apache.log4j.Layout", "log4j-1.2.4.jar", "Log4j", "happyClientNoLog4J", "http://jakarta.apache.org/log4j");
/*     */ 
/* 356 */     needed += needClass("com.ibm.wsdl.factory.WSDLFactoryImpl", "wsdl4j.jar", "WSDL4Java", "happyClientNoAxis", null);
/*     */ 
/* 362 */     needed += needClass("javax.xml.parsers.SAXParserFactory", "xerces.jar", "JAXP", "happyClientNoAxis", "http://xml.apache.org/xerces-j/");
/*     */ 
/* 369 */     title("happyClientOptional");
/*     */ 
/* 371 */     wanted += wantClass("javax.mail.internet.MimeMessage", "mail.jar", "Mail", "happyClientNoAttachments", "http://java.sun.com/products/javamail/");
/*     */ 
/* 377 */     wanted += wantClass("javax.activation.DataHandler", "activation.jar", "Activation", "happyClientNoAttachments", "http://java.sun.com/products/javabeans/glasgow/jaf.html");
/*     */ 
/* 383 */     wanted += wantClass("org.apache.xml.security.Init", "xmlsec.jar", "XML Security", "happyClientNoSecurity", "http://xml.apache.org/security/");
/*     */ 
/* 389 */     wanted += wantClass("javax.net.ssl.SSLSocketFactory", Messages.getMessage("happyClientJSSEsources"), "Java Secure Socket Extension", "happyClientNoHTTPS", "http://java.sun.com/products/jsse/");
/*     */ 
/* 399 */     int warningMessages = 0;
/*     */ 
/* 401 */     String xmlParser = getParserName();
/* 402 */     String xmlParserLocation = getParserLocation();
/* 403 */     this.out.println(Messages.getMessage("happyClientXMLinfo", xmlParser, xmlParserLocation));
/*     */ 
/* 405 */     if (xmlParser.indexOf("xerces") <= 0) {
/* 406 */       warningMessages++;
/* 407 */       this.out.println();
/* 408 */       this.out.println(Messages.getMessage("happyClientRecommendXerces"));
/*     */     }
/* 410 */     if (getJavaVersionNumber() < 13) {
/* 411 */       warningMessages++;
/* 412 */       this.out.println();
/* 413 */       this.out.println(Messages.getMessage("happyClientUnsupportedJVM"));
/*     */     }
/*     */ 
/* 419 */     title("happyClientSummary");
/*     */     boolean happy;
/*     */     boolean happy;
/* 422 */     if (needed == 0)
/*     */     {
/* 424 */       this.out.println(Messages.getMessage("happyClientCorePresent"));
/* 425 */       happy = true;
/*     */     } else {
/* 427 */       happy = false;
/*     */ 
/* 429 */       this.out.println(Messages.getMessage("happyClientCoreMissing", Integer.toString(needed)));
/*     */     }
/*     */ 
/* 433 */     if (wanted > 0) {
/* 434 */       this.out.println();
/* 435 */       this.out.println(Messages.getMessage("happyClientOptionalMissing", Integer.toString(wanted)));
/*     */ 
/* 437 */       this.out.println(Messages.getMessage("happyClientOptionalOK"));
/* 438 */       if (warningsAsErrors)
/* 439 */         happy = false;
/*     */     }
/*     */     else {
/* 442 */       this.out.println(Messages.getMessage("happyClientOptionalPresent"));
/*     */     }
/* 444 */     if (warningMessages > 0) {
/* 445 */       this.out.println(Messages.getMessage("happyClientWarningMessageCount", Integer.toString(warningMessages)));
/*     */ 
/* 447 */       if (warningsAsErrors) {
/* 448 */         happy = false;
/*     */       }
/*     */     }
/*     */ 
/* 452 */     return happy;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 461 */     boolean isHappy = isClientHappy(args);
/* 462 */     System.exit(isHappy ? 0 : -1);
/*     */   }
/* 472 */   private static boolean isClientHappy(String[] args) { HappyClient happy = new HappyClient(System.out);
/*     */ 
/* 474 */     int missing = 0;
/*     */     boolean isHappy;
/*     */     try { boolean isHappy = happy.verifyClientIsHappy(false);
/* 477 */       for (int i = 0; i < args.length; i++) {
/* 478 */         missing += happy.probeClass("argument", args[i], null, null, null, null);
/*     */       }
/*     */ 
/* 487 */       if (missing > 0)
/* 488 */         isHappy = false;
/*     */     } catch (IOException e)
/*     */     {
/* 491 */       e.printStackTrace();
/* 492 */       isHappy = false;
/*     */     }
/* 494 */     return isHappy;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.client.HappyClient
 * JD-Core Version:    0.6.0
 */