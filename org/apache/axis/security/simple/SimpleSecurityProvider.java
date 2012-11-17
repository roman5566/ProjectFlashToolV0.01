/*     */ package org.apache.axis.security.simple;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.LineNumberReader;
/*     */ import java.util.HashMap;
/*     */ import java.util.StringTokenizer;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.security.AuthenticatedUser;
/*     */ import org.apache.axis.security.SecurityProvider;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class SimpleSecurityProvider
/*     */   implements SecurityProvider
/*     */ {
/*  39 */   protected static Log log = LogFactory.getLog(SimpleSecurityProvider.class.getName());
/*     */ 
/*  42 */   HashMap users = null;
/*  43 */   HashMap perms = null;
/*     */ 
/*  45 */   boolean initialized = false;
/*     */ 
/*     */   private synchronized void initialize(MessageContext msgContext)
/*     */   {
/*  50 */     if (this.initialized) return;
/*     */ 
/*  52 */     String configPath = msgContext.getStrProp("configPath");
/*  53 */     if (configPath == null)
/*  54 */       configPath = "";
/*     */     else {
/*  56 */       configPath = configPath + File.separator;
/*     */     }
/*  58 */     File userFile = new File(configPath + "users.lst");
/*  59 */     if (userFile.exists()) {
/*  60 */       this.users = new HashMap();
/*     */       try
/*     */       {
/*  64 */         FileReader fr = new FileReader(userFile);
/*  65 */         LineNumberReader lnr = new LineNumberReader(fr);
/*  66 */         String line = null;
/*     */ 
/*  69 */         while ((line = lnr.readLine()) != null) {
/*  70 */           StringTokenizer st = new StringTokenizer(line);
/*  71 */           if (st.hasMoreTokens()) {
/*  72 */             String userID = st.nextToken();
/*  73 */             String passwd = st.hasMoreTokens() ? st.nextToken() : "";
/*     */ 
/*  75 */             if (log.isDebugEnabled()) {
/*  76 */               log.debug(Messages.getMessage("fromFile00", userID, passwd));
/*     */             }
/*     */ 
/*  80 */             this.users.put(userID, passwd);
/*     */           }
/*     */         }
/*     */ 
/*  84 */         lnr.close();
/*     */       }
/*     */       catch (Exception e) {
/*  87 */         log.error(Messages.getMessage("exception00"), e);
/*  88 */         return;
/*     */       }
/*     */     }
/*  91 */     this.initialized = true;
/*     */   }
/*     */ 
/*     */   public AuthenticatedUser authenticate(MessageContext msgContext)
/*     */   {
/* 102 */     if (!this.initialized) {
/* 103 */       initialize(msgContext);
/*     */     }
/*     */ 
/* 106 */     String username = msgContext.getUsername();
/* 107 */     String password = msgContext.getPassword();
/*     */ 
/* 109 */     if (this.users != null) {
/* 110 */       if (log.isDebugEnabled()) {
/* 111 */         log.debug(Messages.getMessage("user00", username));
/*     */       }
/*     */ 
/* 115 */       if ((username == null) || (username.equals("")) || (!this.users.containsKey(username)))
/*     */       {
/* 118 */         return null;
/*     */       }
/* 120 */       String valid = (String)this.users.get(username);
/*     */ 
/* 122 */       if (log.isDebugEnabled()) {
/* 123 */         log.debug(Messages.getMessage("password00", password));
/*     */       }
/*     */ 
/* 127 */       if ((valid.length() > 0) && (!valid.equals(password))) {
/* 128 */         return null;
/*     */       }
/* 130 */       if (log.isDebugEnabled()) {
/* 131 */         log.debug(Messages.getMessage("auth00", username));
/*     */       }
/*     */ 
/* 134 */       return new SimpleAuthenticatedUser(username);
/*     */     }
/*     */ 
/* 137 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean userMatches(AuthenticatedUser user, String principal)
/*     */   {
/* 146 */     if (user == null) return principal == null;
/* 147 */     return user.getName().compareToIgnoreCase(principal) == 0;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.security.simple.SimpleSecurityProvider
 * JD-Core Version:    0.6.0
 */