/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Vector;
/*     */ import org.apache.axis.client.Call;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class Options
/*     */ {
/*  35 */   protected static Log log = LogFactory.getLog(Options.class.getName());
/*     */ 
/*  38 */   String[] args = null;
/*  39 */   Vector usedArgs = null;
/*  40 */   URL defaultURL = null;
/*     */ 
/*     */   public Options(String[] _args)
/*     */     throws MalformedURLException
/*     */   {
/*  52 */     if (_args == null) {
/*  53 */       _args = new String[0];
/*     */     }
/*  55 */     this.args = _args;
/*  56 */     this.usedArgs = null;
/*  57 */     this.defaultURL = new URL("http://localhost:8080/axis/servlet/AxisServlet");
/*     */     try
/*     */     {
/*  65 */       getURL();
/*     */     } catch (MalformedURLException e) {
/*  67 */       log.error(Messages.getMessage("cantDoURL00"));
/*  68 */       throw e;
/*     */     }
/*  70 */     getUser();
/*  71 */     getPassword();
/*     */   }
/*     */ 
/*     */   public void setDefaultURL(String url)
/*     */     throws MalformedURLException
/*     */   {
/*  78 */     this.defaultURL = new URL(url);
/*     */   }
/*     */ 
/*     */   public void setDefaultURL(URL url) {
/*  82 */     this.defaultURL = url;
/*     */   }
/*     */ 
/*     */   public int isFlagSet(char optChar)
/*     */   {
/*  92 */     int value = 0;
/*     */ 
/*  96 */     for (int loop = 0; (this.usedArgs != null) && (loop < this.usedArgs.size()); loop++) {
/*  97 */       String arg = (String)this.usedArgs.elementAt(loop);
/*  98 */       if (arg.charAt(0) == '-')
/*  99 */         for (int i = 0; i < arg.length(); i++) {
/* 100 */           if (arg.charAt(i) != optChar) continue; value++;
/*     */         }
/*     */     }
/* 103 */     for (loop = 0; loop < this.args.length; loop++) {
/* 104 */       if ((this.args[loop] == null) || (this.args[loop].length() == 0) || 
/* 105 */         (this.args[loop].charAt(0) != '-'))
/*     */         continue;
/*     */       int i;
/* 107 */       while ((this.args[loop] != null) && ((i = this.args[loop].indexOf(optChar)) != -1)) {
/* 108 */         this.args[loop] = (this.args[loop].substring(0, i) + this.args[loop].substring(i + 1));
/* 109 */         if (this.args[loop].length() == 1)
/* 110 */           this.args[loop] = null;
/* 111 */         value++;
/* 112 */         if (this.usedArgs == null) this.usedArgs = new Vector();
/* 113 */         this.usedArgs.add("-" + optChar);
/*     */       }
/*     */     }
/* 116 */     return value;
/*     */   }
/*     */ 
/*     */   public String isValueSet(char optChar)
/*     */   {
/* 131 */     String value = null;
/*     */ 
/* 135 */     for (int loop = 0; (this.usedArgs != null) && (loop < this.usedArgs.size()); loop++) {
/* 136 */       String arg = (String)this.usedArgs.elementAt(loop);
/* 137 */       if ((arg.charAt(0) != '-') || (arg.charAt(1) != optChar))
/*     */         continue;
/* 139 */       value = arg.substring(2);
/* 140 */       if (loop + 1 < this.usedArgs.size()) {
/* 141 */         loop++; value = (String)this.usedArgs.elementAt(loop);
/*     */       }
/*     */     }
/* 144 */     for (loop = 0; loop < this.args.length; loop++) {
/* 145 */       if ((this.args[loop] == null) || (this.args[loop].length() == 0) || 
/* 146 */         (this.args[loop].charAt(0) != '-')) continue;
/* 147 */       int i = this.args[loop].indexOf(optChar);
/* 148 */       if (i == 1) {
/* 149 */         if (i != this.args[loop].length() - 1)
/*     */         {
/* 151 */           value = this.args[loop].substring(i + 1);
/* 152 */           this.args[loop] = this.args[loop].substring(0, i);
/*     */         }
/*     */         else
/*     */         {
/* 156 */           this.args[loop] = this.args[loop].substring(0, i);
/*     */ 
/* 159 */           if ((loop + 1 < this.args.length) && (this.args[(loop + 1)] != null))
/*     */           {
/* 161 */             if (this.args[(loop + 1)].charAt(0) != '-') {
/* 162 */               value = this.args[(loop + 1)];
/* 163 */               this.args[(loop + 1)] = null;
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 171 */         if (this.args[loop].length() == 1) {
/* 172 */           this.args[loop] = null;
/*     */         }
/*     */       }
/*     */     }
/* 176 */     if (value != null) {
/* 177 */       if (this.usedArgs == null) this.usedArgs = new Vector();
/* 178 */       this.usedArgs.add("-" + optChar);
/* 179 */       if (value.length() > 0) this.usedArgs.add(value);
/*     */     }
/* 181 */     return value;
/*     */   }
/*     */ 
/*     */   public String getRemainingFlags()
/*     */   {
/* 189 */     StringBuffer sb = null;
/*     */ 
/* 192 */     for (int loop = 0; loop < this.args.length; loop++) {
/* 193 */       if ((this.args[loop] == null) || (this.args[loop].length() == 0) || 
/* 194 */         (this.args[loop].charAt(0) != '-')) continue;
/* 195 */       if (sb == null) sb = new StringBuffer();
/* 196 */       sb.append(this.args[loop].substring(1));
/*     */     }
/* 198 */     return sb == null ? null : sb.toString();
/*     */   }
/*     */ 
/*     */   public String[] getRemainingArgs()
/*     */   {
/* 206 */     ArrayList al = null;
/*     */ 
/* 209 */     for (int loop = 0; loop < this.args.length; loop++) {
/* 210 */       if ((this.args[loop] == null) || (this.args[loop].length() == 0) || 
/* 211 */         (this.args[loop].charAt(0) == '-')) continue;
/* 212 */       if (al == null) al = new ArrayList();
/* 213 */       al.add(this.args[loop]);
/*     */     }
/* 215 */     if (al == null) return null;
/* 216 */     String[] a = new String[al.size()];
/* 217 */     for (loop = 0; loop < al.size(); loop++)
/* 218 */       a[loop] = ((String)al.get(loop));
/* 219 */     return a;
/*     */   }
/*     */ 
/*     */   public String getURL()
/*     */     throws MalformedURLException
/*     */   {
/* 226 */     String host = null;
/* 227 */     String port = null;
/* 228 */     String servlet = null;
/* 229 */     String protocol = null;
/*     */ 
/* 231 */     URL url = null;
/*     */ 
/* 234 */     Call.initialize();
/*     */ 
/* 236 */     if ((tmp = isValueSet('l')) != null) {
/* 237 */       url = new URL(tmp);
/* 238 */       host = url.getHost();
/* 239 */       port = "" + url.getPort();
/* 240 */       servlet = url.getFile();
/* 241 */       protocol = url.getProtocol();
/*     */     }
/*     */ 
/* 244 */     if ((tmp = isValueSet('f')) != null) {
/* 245 */       host = "";
/* 246 */       port = "-1";
/* 247 */       servlet = tmp;
/* 248 */       protocol = "file";
/*     */     }
/*     */ 
/* 251 */     String tmp = isValueSet('h'); if (host == null) host = tmp;
/* 252 */     tmp = isValueSet('p'); if (port == null) port = tmp;
/* 253 */     tmp = isValueSet('s'); if (servlet == null) servlet = tmp;
/*     */ 
/* 255 */     if (host == null) host = this.defaultURL.getHost();
/* 256 */     if (port == null) port = "" + this.defaultURL.getPort();
/* 257 */     if (servlet == null) servlet = this.defaultURL.getFile();
/* 259 */     else if ((servlet.length() > 0) && (servlet.charAt(0) != '/')) {
/* 260 */       servlet = "/" + servlet;
/*     */     }
/* 262 */     if (url == null) {
/* 263 */       if (protocol == null) protocol = this.defaultURL.getProtocol();
/* 264 */       tmp = protocol + "://" + host;
/* 265 */       if ((port != null) && (!port.equals("-1"))) tmp = tmp + ":" + port;
/* 266 */       if (servlet != null) tmp = tmp + servlet; 
/*     */     } else {
/* 267 */       tmp = url.toString();
/* 268 */     }log.debug(Messages.getMessage("return02", "getURL", tmp));
/* 269 */     return tmp;
/*     */   }
/*     */ 
/*     */   public String getHost() {
/*     */     try {
/* 274 */       URL url = new URL(getURL());
/* 275 */       return url.getHost();
/*     */     } catch (Exception exp) {
/*     */     }
/* 278 */     return "localhost";
/*     */   }
/*     */ 
/*     */   public int getPort()
/*     */   {
/*     */     try {
/* 284 */       URL url = new URL(getURL());
/* 285 */       return url.getPort();
/*     */     } catch (Exception exp) {
/*     */     }
/* 288 */     return -1;
/*     */   }
/*     */ 
/*     */   public String getUser()
/*     */   {
/* 293 */     return isValueSet('u');
/*     */   }
/*     */ 
/*     */   public String getPassword() {
/* 297 */     return isValueSet('w');
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.Options
 * JD-Core Version:    0.6.0
 */