/*     */ package org.apache.axis.client;
/*     */ 
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.Iterator;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.rpc.JAXRPCException;
/*     */ import javax.xml.rpc.Service;
/*     */ import javax.xml.rpc.ServiceException;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Message;
/*     */ import org.apache.axis.message.SOAPEnvelope;
/*     */ import org.apache.axis.message.SOAPHeaderElement;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public abstract class Stub
/*     */   implements javax.xml.rpc.Stub
/*     */ {
/*  39 */   protected Service service = null;
/*     */ 
/*  47 */   protected boolean maintainSessionSet = false;
/*  48 */   protected boolean maintainSession = false;
/*     */ 
/*  50 */   protected Properties cachedProperties = new Properties();
/*  51 */   protected String cachedUsername = null;
/*  52 */   protected String cachedPassword = null;
/*  53 */   protected URL cachedEndpoint = null;
/*  54 */   protected Integer cachedTimeout = null;
/*  55 */   protected QName cachedPortName = null;
/*     */ 
/*  58 */   private Vector headers = new Vector();
/*     */ 
/*  61 */   private Vector attachments = new Vector();
/*     */ 
/*  66 */   private boolean firstCall = true;
/*     */ 
/*  69 */   protected Call _call = null;
/*     */ 
/*     */   protected boolean firstCall()
/*     */   {
/*  75 */     boolean ret = this.firstCall;
/*  76 */     this.firstCall = false;
/*  77 */     return ret;
/*     */   }
/*     */ 
/*     */   public void _setProperty(String name, Object value)
/*     */   {
/*  95 */     if ((name == null) || (value == null)) {
/*  96 */       throw new JAXRPCException(Messages.getMessage(name == null ? "badProp03" : "badProp04"));
/*     */     }
/*     */ 
/* 100 */     if (name.equals("javax.xml.rpc.security.auth.username")) {
/* 101 */       if (!(value instanceof String)) {
/* 102 */         throw new JAXRPCException(Messages.getMessage("badProp00", new String[] { name, "java.lang.String", value.getClass().getName() }));
/*     */       }
/*     */ 
/* 106 */       this.cachedUsername = ((String)value);
/*     */     }
/* 108 */     else if (name.equals("javax.xml.rpc.security.auth.password")) {
/* 109 */       if (!(value instanceof String)) {
/* 110 */         throw new JAXRPCException(Messages.getMessage("badProp00", new String[] { name, "java.lang.String", value.getClass().getName() }));
/*     */       }
/*     */ 
/* 114 */       this.cachedPassword = ((String)value);
/*     */     }
/* 116 */     else if (name.equals("javax.xml.rpc.service.endpoint.address")) {
/* 117 */       if (!(value instanceof String)) {
/* 118 */         throw new JAXRPCException(Messages.getMessage("badProp00", new String[] { name, "java.lang.String", value.getClass().getName() }));
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 123 */         this.cachedEndpoint = new URL((String)value);
/*     */       }
/*     */       catch (MalformedURLException mue) {
/* 126 */         throw new JAXRPCException(mue.getMessage());
/*     */       }
/*     */     }
/* 129 */     else if (name.equals("javax.xml.rpc.session.maintain")) {
/* 130 */       if (!(value instanceof Boolean)) {
/* 131 */         throw new JAXRPCException(Messages.getMessage("badProp00", new String[] { name, "java.lang.Boolean", value.getClass().getName() }));
/*     */       }
/*     */ 
/* 137 */       this.maintainSessionSet = true;
/* 138 */       this.maintainSession = ((Boolean)value).booleanValue();
/*     */     } else {
/* 140 */       if ((name.startsWith("java.")) || (name.startsWith("javax."))) {
/* 141 */         throw new JAXRPCException(Messages.getMessage("badProp05", name));
/*     */       }
/*     */ 
/* 145 */       this.cachedProperties.put(name, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object _getProperty(String name)
/*     */   {
/* 157 */     if (name == null) {
/* 158 */       throw new JAXRPCException(Messages.getMessage("badProp05", name));
/*     */     }
/*     */ 
/* 162 */     if (name.equals("javax.xml.rpc.security.auth.username")) {
/* 163 */       return this.cachedUsername;
/*     */     }
/* 165 */     if (name.equals("javax.xml.rpc.security.auth.password")) {
/* 166 */       return this.cachedPassword;
/*     */     }
/* 168 */     if (name.equals("javax.xml.rpc.service.endpoint.address")) {
/* 169 */       return this.cachedEndpoint.toString();
/*     */     }
/* 171 */     if (name.equals("javax.xml.rpc.session.maintain")) {
/* 172 */       return this.maintainSessionSet ? Boolean.FALSE : this.maintainSession ? Boolean.TRUE : null;
/*     */     }
/* 174 */     if ((name.startsWith("java.")) || (name.startsWith("javax."))) {
/* 175 */       throw new JAXRPCException(Messages.getMessage("badProp05", name));
/*     */     }
/*     */ 
/* 179 */     return this.cachedProperties.get(name);
/*     */   }
/*     */ 
/*     */   public Object removeProperty(String name)
/*     */   {
/* 192 */     return this.cachedProperties.remove(name);
/*     */   }
/*     */ 
/*     */   public Iterator _getPropertyNames()
/*     */   {
/* 198 */     return this.cachedProperties.keySet().iterator();
/*     */   }
/*     */ 
/*     */   public void setUsername(String username)
/*     */   {
/* 205 */     this.cachedUsername = username;
/*     */   }
/*     */ 
/*     */   public String getUsername()
/*     */   {
/* 212 */     return this.cachedUsername;
/*     */   }
/*     */ 
/*     */   public void setPassword(String password)
/*     */   {
/* 219 */     this.cachedPassword = password;
/*     */   }
/*     */ 
/*     */   public String getPassword()
/*     */   {
/* 226 */     return this.cachedPassword;
/*     */   }
/*     */ 
/*     */   public int getTimeout()
/*     */   {
/* 233 */     return this.cachedTimeout == null ? 0 : this.cachedTimeout.intValue();
/*     */   }
/*     */ 
/*     */   public void setTimeout(int timeout)
/*     */   {
/* 240 */     this.cachedTimeout = new Integer(timeout);
/*     */   }
/*     */ 
/*     */   public QName getPortName()
/*     */   {
/* 247 */     return this.cachedPortName;
/*     */   }
/*     */ 
/*     */   public void setPortName(QName portName)
/*     */   {
/* 254 */     this.cachedPortName = portName;
/*     */   }
/*     */ 
/*     */   public void setPortName(String portName)
/*     */   {
/* 261 */     setPortName(new QName(portName));
/*     */   }
/*     */ 
/*     */   public void setMaintainSession(boolean session)
/*     */   {
/* 268 */     this.maintainSessionSet = true;
/* 269 */     this.maintainSession = session;
/* 270 */     this.cachedProperties.put("javax.xml.rpc.session.maintain", session ? Boolean.TRUE : Boolean.FALSE);
/*     */   }
/*     */ 
/*     */   public void setHeader(String namespace, String partName, Object headerValue)
/*     */   {
/* 281 */     this.headers.add(new SOAPHeaderElement(namespace, partName, headerValue));
/*     */   }
/*     */ 
/*     */   public void setHeader(SOAPHeaderElement header)
/*     */   {
/* 288 */     this.headers.add(header);
/*     */   }
/*     */ 
/*     */   public void extractAttachments(Call call)
/*     */   {
/* 296 */     this.attachments.clear();
/* 297 */     if (call.getResponseMessage() != null) {
/* 298 */       Iterator iterator = call.getResponseMessage().getAttachments();
/* 299 */       while (iterator.hasNext())
/* 300 */         this.attachments.add(iterator.next());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addAttachment(Object handler)
/*     */   {
/* 310 */     this.attachments.add(handler);
/*     */   }
/*     */ 
/*     */   public SOAPHeaderElement getHeader(String namespace, String partName)
/*     */   {
/* 317 */     for (int i = 0; i < this.headers.size(); i++) {
/* 318 */       SOAPHeaderElement header = (SOAPHeaderElement)this.headers.get(i);
/* 319 */       if ((header.getNamespaceURI().equals(namespace)) && (header.getName().equals(partName)))
/*     */       {
/* 321 */         return header;
/*     */       }
/*     */     }
/* 323 */     return null;
/*     */   }
/*     */ 
/*     */   public SOAPHeaderElement getResponseHeader(String namespace, String partName)
/*     */   {
/*     */     try
/*     */     {
/* 332 */       if (this._call == null)
/* 333 */         return null;
/* 334 */       return this._call.getResponseMessage().getSOAPEnvelope().getHeaderByName(namespace, partName);
/*     */     }
/*     */     catch (Exception e) {
/*     */     }
/* 338 */     return null;
/*     */   }
/*     */ 
/*     */   public SOAPHeaderElement[] getHeaders()
/*     */   {
/* 346 */     SOAPHeaderElement[] array = new SOAPHeaderElement[this.headers.size()];
/* 347 */     this.headers.copyInto(array);
/* 348 */     return array;
/*     */   }
/*     */ 
/*     */   public SOAPHeaderElement[] getResponseHeaders()
/*     */   {
/* 355 */     SOAPHeaderElement[] array = new SOAPHeaderElement[0];
/*     */     try
/*     */     {
/* 358 */       if (this._call == null)
/* 359 */         return array;
/* 360 */       Vector h = this._call.getResponseMessage().getSOAPEnvelope().getHeaders();
/* 361 */       array = new SOAPHeaderElement[h.size()];
/* 362 */       h.copyInto(array);
/* 363 */       return array;
/*     */     }
/*     */     catch (Exception e) {
/*     */     }
/* 367 */     return array;
/*     */   }
/*     */ 
/*     */   public Object[] getAttachments()
/*     */   {
/* 378 */     Object[] array = new Object[this.attachments.size()];
/* 379 */     this.attachments.copyInto(array);
/* 380 */     this.attachments.clear();
/* 381 */     return array;
/*     */   }
/*     */ 
/*     */   public void clearHeaders()
/*     */   {
/* 388 */     this.headers.clear();
/*     */   }
/*     */ 
/*     */   public void clearAttachments()
/*     */   {
/* 395 */     this.attachments.clear();
/*     */   }
/*     */ 
/*     */   protected void setRequestHeaders(Call call) throws AxisFault
/*     */   {
/* 400 */     SOAPHeaderElement[] headers = getHeaders();
/* 401 */     for (int i = 0; i < headers.length; i++)
/* 402 */       call.addHeader(headers[i]);
/*     */   }
/*     */ 
/*     */   protected void setAttachments(Call call)
/*     */     throws AxisFault
/*     */   {
/* 414 */     Object[] attachments = getAttachments();
/* 415 */     for (int i = 0; i < attachments.length; i++) {
/* 416 */       call.addAttachmentPart(attachments[i]);
/*     */     }
/* 418 */     clearAttachments();
/*     */   }
/*     */ 
/*     */   public Service _getService()
/*     */   {
/* 427 */     return this.service;
/*     */   }
/*     */ 
/*     */   public Call _createCall()
/*     */     throws ServiceException
/*     */   {
/* 435 */     this._call = ((Call)this.service.createCall());
/*     */ 
/* 439 */     return this._call;
/*     */   }
/*     */ 
/*     */   public Call _getCall()
/*     */   {
/* 446 */     return this._call;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   protected void getResponseHeaders(Call call)
/*     */     throws AxisFault
/*     */   {
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.client.Stub
 * JD-Core Version:    0.6.0
 */