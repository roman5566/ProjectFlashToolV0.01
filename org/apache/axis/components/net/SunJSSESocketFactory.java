/*     */ package org.apache.axis.components.net;
/*     */ 
/*     */ import com.sun.net.ssl.KeyManagerFactory;
/*     */ import com.sun.net.ssl.SSLContext;
/*     */ import com.sun.net.ssl.TrustManager;
/*     */ import com.sun.net.ssl.TrustManagerFactory;
/*     */ import com.sun.net.ssl.internal.ssl.Provider;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.security.KeyStore;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.Security;
/*     */ import java.util.Hashtable;
/*     */ import sun.security.provider.Sun;
/*     */ 
/*     */ public class SunJSSESocketFactory extends JSSESocketFactory
/*     */   implements SecureSocketFactory
/*     */ {
/*     */   private String keystoreType;
/*  40 */   static String defaultKeystoreType = "JKS";
/*     */ 
/*  43 */   static String defaultProtocol = "TLS";
/*     */ 
/*  46 */   static String defaultAlgorithm = "SunX509";
/*     */ 
/*  49 */   static boolean defaultClientAuth = false;
/*     */ 
/*  52 */   private boolean clientAuth = false;
/*     */ 
/*  55 */   static String defaultKeystoreFile = System.getProperty("user.home") + "/.keystore";
/*     */ 
/*  59 */   static String defaultKeyPass = "changeit";
/*     */ 
/*     */   public SunJSSESocketFactory(Hashtable attributes)
/*     */   {
/*  67 */     super(attributes);
/*     */   }
/*     */ 
/*     */   protected void initFactory()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/*  78 */       Security.addProvider(new Sun());
/*  79 */       Security.addProvider(new Provider());
/*     */ 
/*  82 */       SSLContext context = getContext();
/*  83 */       this.sslFactory = context.getSocketFactory();
/*     */     } catch (Exception e) {
/*  85 */       if ((e instanceof IOException)) {
/*  86 */         throw ((IOException)e);
/*     */       }
/*  88 */       throw new IOException(e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   protected SSLContext getContext()
/*     */     throws Exception
/*     */   {
/* 100 */     if (this.attributes == null) {
/* 101 */       SSLContext context = SSLContext.getInstance("SSL");
/*     */ 
/* 104 */       context.init(null, null, null);
/* 105 */       return context;
/*     */     }
/*     */ 
/* 110 */     String keystoreFile = (String)this.attributes.get("keystore");
/* 111 */     if (keystoreFile == null) {
/* 112 */       keystoreFile = defaultKeystoreFile;
/*     */     }
/*     */ 
/* 115 */     this.keystoreType = ((String)this.attributes.get("keystoreType"));
/* 116 */     if (this.keystoreType == null) {
/* 117 */       this.keystoreType = defaultKeystoreType;
/*     */     }
/*     */ 
/* 122 */     this.clientAuth = (null != (String)this.attributes.get("clientauth"));
/* 123 */     String keyPass = (String)this.attributes.get("keypass");
/* 124 */     if (keyPass == null) {
/* 125 */       keyPass = defaultKeyPass;
/*     */     }
/*     */ 
/* 128 */     String keystorePass = (String)this.attributes.get("keystorePass");
/* 129 */     if (keystorePass == null) {
/* 130 */       keystorePass = keyPass;
/*     */     }
/*     */ 
/* 134 */     String protocol = (String)this.attributes.get("protocol");
/* 135 */     if (protocol == null) {
/* 136 */       protocol = defaultProtocol;
/*     */     }
/*     */ 
/* 140 */     String algorithm = (String)this.attributes.get("algorithm");
/* 141 */     if (algorithm == null) {
/* 142 */       algorithm = defaultAlgorithm;
/*     */     }
/*     */ 
/* 147 */     KeyStore kstore = initKeyStore(keystoreFile, keystorePass);
/*     */ 
/* 150 */     KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
/*     */ 
/* 153 */     kmf.init(kstore, keyPass.toCharArray());
/*     */ 
/* 156 */     TrustManager[] tm = null;
/*     */ 
/* 158 */     if (this.clientAuth) {
/* 159 */       TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
/*     */ 
/* 162 */       tmf.init(kstore);
/* 163 */       tm = tmf.getTrustManagers();
/*     */     }
/*     */ 
/* 168 */     SSLContext context = SSLContext.getInstance(protocol);
/*     */ 
/* 172 */     context.init(kmf.getKeyManagers(), tm, new SecureRandom());
/*     */ 
/* 174 */     return context;
/*     */   }
/*     */ 
/*     */   private KeyStore initKeyStore(String keystoreFile, String keyPass)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 189 */       KeyStore kstore = KeyStore.getInstance(this.keystoreType);
/*     */ 
/* 191 */       InputStream istream = new FileInputStream(keystoreFile);
/* 192 */       kstore.load(istream, keyPass.toCharArray());
/* 193 */       return kstore;
/*     */     } catch (FileNotFoundException fnfe) {
/* 195 */       throw fnfe;
/*     */     } catch (IOException ioe) {
/* 197 */       throw ioe;
/*     */     } catch (Exception ex) {
/* 199 */       ex.printStackTrace();
/* 200 */     }throw new IOException("Exception trying to load keystore " + keystoreFile + ": " + ex.getMessage());
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.net.SunJSSESocketFactory
 * JD-Core Version:    0.6.0
 */