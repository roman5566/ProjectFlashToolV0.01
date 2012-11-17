/*     */ package org.apache.axis.transport.jms;
/*     */ 
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class JMSURLHelper
/*     */ {
/*     */   private URL url;
/*     */   private String destination;
/*     */   private HashMap properties;
/*     */   private Vector requiredProperties;
/*     */   private Vector appProperties;
/*     */ 
/*     */   public JMSURLHelper(URL url)
/*     */     throws MalformedURLException
/*     */   {
/*  49 */     this(url, null);
/*     */   }
/*     */ 
/*     */   public JMSURLHelper(URL url, String[] requiredProperties) throws MalformedURLException {
/*  53 */     this.url = url;
/*  54 */     this.properties = new HashMap();
/*  55 */     this.appProperties = new Vector();
/*     */ 
/*  59 */     this.destination = url.getPath();
/*  60 */     if (this.destination.startsWith("/")) {
/*  61 */       this.destination = this.destination.substring(1);
/*     */     }
/*  63 */     if ((this.destination == null) || (this.destination.trim().length() < 1)) {
/*  64 */       throw new MalformedURLException("Missing destination in URL");
/*     */     }
/*     */ 
/*  67 */     String query = url.getQuery();
/*  68 */     StringTokenizer st = new StringTokenizer(query, "&;");
/*  69 */     while (st.hasMoreTokens()) {
/*  70 */       String keyValue = st.nextToken();
/*  71 */       int eqIndex = keyValue.indexOf("=");
/*  72 */       if (eqIndex > 0)
/*     */       {
/*  74 */         String key = keyValue.substring(0, eqIndex);
/*  75 */         String value = keyValue.substring(eqIndex + 1);
/*  76 */         if (key.startsWith("msgProp.")) {
/*  77 */           key = key.substring("msgProp.".length());
/*     */ 
/*  79 */           addApplicationProperty(key);
/*     */         }
/*  81 */         this.properties.put(key, value);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  86 */     addRequiredProperties(requiredProperties);
/*  87 */     validateURL();
/*     */   }
/*     */ 
/*     */   public String getDestination() {
/*  91 */     return this.destination;
/*     */   }
/*     */ 
/*     */   public void setDestination(String destination) {
/*  95 */     this.destination = destination;
/*     */   }
/*     */ 
/*     */   public String getVendor() {
/*  99 */     return getPropertyValue("vendor");
/*     */   }
/*     */ 
/*     */   public String getDomain() {
/* 103 */     return getPropertyValue("domain");
/*     */   }
/*     */ 
/*     */   public HashMap getProperties() {
/* 107 */     return this.properties;
/*     */   }
/*     */ 
/*     */   public String getPropertyValue(String property) {
/* 111 */     return (String)this.properties.get(property);
/*     */   }
/*     */ 
/*     */   public void addRequiredProperties(String[] properties)
/*     */   {
/* 116 */     if (properties == null) {
/* 117 */       return;
/*     */     }
/* 119 */     for (int i = 0; i < properties.length; i++)
/*     */     {
/* 121 */       addRequiredProperty(properties[i]);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addRequiredProperty(String property) {
/* 126 */     if (property == null) {
/* 127 */       return;
/*     */     }
/* 129 */     if (this.requiredProperties == null) {
/* 130 */       this.requiredProperties = new Vector();
/*     */     }
/* 132 */     this.requiredProperties.addElement(property);
/*     */   }
/*     */ 
/*     */   public Vector getRequiredProperties() {
/* 136 */     return this.requiredProperties;
/*     */   }
/*     */ 
/*     */   public void addApplicationProperty(String property)
/*     */   {
/* 143 */     if (property == null) {
/* 144 */       return;
/*     */     }
/* 146 */     if (this.appProperties == null) {
/* 147 */       this.appProperties = new Vector();
/*     */     }
/* 149 */     this.appProperties.addElement(property);
/*     */   }
/*     */ 
/*     */   public void addApplicationProperty(String property, String value)
/*     */   {
/* 156 */     if (property == null) {
/* 157 */       return;
/*     */     }
/* 159 */     if (this.appProperties == null) {
/* 160 */       this.appProperties = new Vector();
/*     */     }
/* 162 */     this.properties.put(property, value);
/* 163 */     this.appProperties.addElement(property);
/*     */   }
/*     */ 
/*     */   public Vector getApplicationProperties()
/*     */   {
/* 171 */     return this.appProperties;
/*     */   }
/*     */ 
/*     */   public String getURLString()
/*     */   {
/* 181 */     StringBuffer text = new StringBuffer("jms:/");
/* 182 */     text.append(getDestination());
/* 183 */     text.append("?");
/* 184 */     Map props = (Map)this.properties.clone();
/* 185 */     boolean firstEntry = true;
/* 186 */     for (Iterator itr = this.properties.keySet().iterator(); itr.hasNext(); ) {
/* 187 */       String key = (String)itr.next();
/* 188 */       if (!firstEntry) {
/* 189 */         text.append("&");
/*     */       }
/* 191 */       if (this.appProperties.contains(key)) {
/* 192 */         text.append("msgProp.");
/*     */       }
/* 194 */       text.append(key);
/* 195 */       text.append("=");
/* 196 */       text.append(props.get(key));
/* 197 */       firstEntry = false;
/*     */     }
/* 199 */     return text.toString();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 204 */     return getURLString();
/*     */   }
/*     */ 
/*     */   private void validateURL() throws MalformedURLException
/*     */   {
/* 209 */     Vector required = getRequiredProperties();
/* 210 */     if (required == null) {
/* 211 */       return;
/*     */     }
/* 213 */     for (int i = 0; i < required.size(); i++)
/*     */     {
/* 215 */       String key = (String)required.elementAt(i);
/* 216 */       if (this.properties.get(key) == null)
/* 217 */         throw new MalformedURLException();
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.jms.JMSURLHelper
 * JD-Core Version:    0.6.0
 */