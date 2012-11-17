/*     */ package org.apache.axis.deployment.wsdd;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.ConfigurationException;
/*     */ import org.apache.axis.EngineConfiguration;
/*     */ import org.apache.axis.Handler;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.utils.ClassUtils;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.LockableHashtable;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Element;
/*     */ import org.xml.sax.helpers.AttributesImpl;
/*     */ 
/*     */ public abstract class WSDDDeployableItem extends WSDDElement
/*     */ {
/*     */   public static final int SCOPE_PER_ACCESS = 0;
/*     */   public static final int SCOPE_PER_REQUEST = 1;
/*     */   public static final int SCOPE_SINGLETON = 2;
/*  50 */   public static String[] scopeStrings = { "per-access", "per-request", "singleton" };
/*     */ 
/*  54 */   protected static Log log = LogFactory.getLog(WSDDDeployableItem.class.getName());
/*     */   protected LockableHashtable parameters;
/*     */   protected QName qname;
/*     */   protected QName type;
/*  67 */   protected int scope = 2;
/*     */ 
/*  70 */   protected Handler singletonInstance = null;
/*     */ 
/*     */   public WSDDDeployableItem()
/*     */   {
/*     */   }
/*     */ 
/*     */   public WSDDDeployableItem(Element e)
/*     */     throws WSDDException
/*     */   {
/*  87 */     super(e);
/*     */ 
/*  89 */     String name = e.getAttribute("name");
/*  90 */     if ((name != null) && (!name.equals("")))
/*     */     {
/*  92 */       this.qname = new QName("", name);
/*     */     }
/*     */ 
/*  95 */     String typeStr = e.getAttribute("type");
/*  96 */     if ((typeStr != null) && (!typeStr.equals(""))) {
/*  97 */       this.type = XMLUtils.getQNameFromString(typeStr, e);
/*     */     }
/*     */ 
/* 103 */     String scopeStr = e.getAttribute("scope");
/* 104 */     if (scopeStr != null) {
/* 105 */       for (int i = 0; i < scopeStrings.length; i++) {
/* 106 */         if (scopeStr.equals(scopeStrings[i])) {
/* 107 */           this.scope = i;
/* 108 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 113 */     this.parameters = new LockableHashtable();
/*     */ 
/* 116 */     Element[] paramElements = getChildElements(e, "parameter");
/* 117 */     for (int i = 0; i < paramElements.length; i++) {
/* 118 */       Element param = paramElements[i];
/* 119 */       String pname = param.getAttribute("name");
/* 120 */       String value = param.getAttribute("value");
/* 121 */       String locked = param.getAttribute("locked");
/* 122 */       this.parameters.put(pname, value, JavaUtils.isTrueExplicitly(locked));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/* 132 */     this.qname = new QName(null, name);
/*     */   }
/*     */ 
/*     */   public void setQName(QName qname)
/*     */   {
/* 137 */     this.qname = qname;
/*     */   }
/*     */ 
/*     */   public QName getQName()
/*     */   {
/* 146 */     return this.qname;
/*     */   }
/*     */ 
/*     */   public QName getType()
/*     */   {
/* 155 */     return this.type;
/*     */   }
/*     */ 
/*     */   public void setType(QName type)
/*     */   {
/* 164 */     this.type = type;
/*     */   }
/*     */ 
/*     */   public void setParameter(String name, String value)
/*     */   {
/* 172 */     if (this.parameters == null) {
/* 173 */       this.parameters = new LockableHashtable();
/*     */     }
/* 175 */     this.parameters.put(name, value);
/*     */   }
/*     */ 
/*     */   public String getParameter(String name)
/*     */   {
/* 183 */     if ((name == null) || (this.parameters == null)) {
/* 184 */       return null;
/*     */     }
/*     */ 
/* 187 */     return (String)this.parameters.get(name);
/*     */   }
/*     */ 
/*     */   public LockableHashtable getParametersTable()
/*     */   {
/* 196 */     return this.parameters;
/*     */   }
/*     */ 
/*     */   public void setOptionsHashtable(Hashtable hashtable)
/*     */   {
/* 206 */     if (hashtable == null) {
/* 207 */       return;
/*     */     }
/* 209 */     this.parameters = new LockableHashtable(hashtable);
/*     */   }
/*     */ 
/*     */   public void writeParamsToContext(SerializationContext context)
/*     */     throws IOException
/*     */   {
/* 215 */     if (this.parameters == null) {
/* 216 */       return;
/*     */     }
/* 218 */     Set entries = this.parameters.entrySet();
/* 219 */     Iterator i = entries.iterator();
/* 220 */     while (i.hasNext()) {
/* 221 */       Map.Entry entry = (Map.Entry)i.next();
/* 222 */       String name = (String)entry.getKey();
/* 223 */       AttributesImpl attrs = new AttributesImpl();
/*     */ 
/* 225 */       attrs.addAttribute("", "name", "name", "CDATA", name);
/* 226 */       attrs.addAttribute("", "value", "value", "CDATA", entry.getValue().toString());
/*     */ 
/* 228 */       if (this.parameters.isKeyLocked(name)) {
/* 229 */         attrs.addAttribute("", "locked", "locked", "CDATA", "true");
/*     */       }
/*     */ 
/* 232 */       context.startElement(QNAME_PARAM, attrs);
/* 233 */       context.endElement();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeParameter(String name)
/*     */   {
/* 243 */     if (this.parameters != null)
/* 244 */       this.parameters.remove(name);
/*     */   }
/*     */ 
/*     */   public final Handler getInstance(EngineConfiguration registry)
/*     */     throws ConfigurationException
/*     */   {
/* 257 */     if (this.scope == 2) {
/* 258 */       synchronized (this) {
/* 259 */         if (this.singletonInstance == null)
/* 260 */           this.singletonInstance = getNewInstance(registry);
/*     */       }
/* 262 */       return this.singletonInstance;
/*     */     }
/*     */ 
/* 265 */     return getNewInstance(registry);
/*     */   }
/*     */ 
/*     */   private Handler getNewInstance(EngineConfiguration registry)
/*     */     throws ConfigurationException
/*     */   {
/* 271 */     QName type = getType();
/* 272 */     if ((type == null) || ("http://xml.apache.org/axis/wsdd/providers/java".equals(type.getNamespaceURI())))
/*     */     {
/* 274 */       return makeNewInstance(registry);
/*     */     }
/* 276 */     return registry.getHandler(type);
/*     */   }
/*     */ 
/*     */   protected Handler makeNewInstance(EngineConfiguration registry)
/*     */     throws ConfigurationException
/*     */   {
/* 291 */     Class c = null;
/* 292 */     Handler h = null;
/*     */     try
/*     */     {
/* 295 */       c = getJavaClass();
/*     */     } catch (ClassNotFoundException e) {
/* 297 */       throw new ConfigurationException(e);
/*     */     }
/*     */ 
/* 300 */     if (c != null) {
/*     */       try {
/* 302 */         h = (Handler)createInstance(c);
/*     */       } catch (Exception e) {
/* 304 */         throw new ConfigurationException(e);
/*     */       }
/*     */ 
/* 307 */       if (h != null) {
/* 308 */         if (this.qname != null)
/* 309 */           h.setName(this.qname.getLocalPart());
/* 310 */         h.setOptions(getParametersTable());
/*     */         try {
/* 312 */           h.init();
/*     */         } catch (Exception e) {
/* 314 */           String msg = e + JavaUtils.LS + JavaUtils.stackToString(e);
/* 315 */           log.debug(msg);
/* 316 */           throw new ConfigurationException(e);
/*     */         } catch (Error e) {
/* 318 */           String msg = e + JavaUtils.LS + JavaUtils.stackToString(e);
/* 319 */           log.debug(msg);
/* 320 */           throw new ConfigurationException(msg);
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 325 */       h = registry.getHandler(getType());
/*     */     }
/*     */ 
/* 328 */     return h;
/*     */   }
/*     */ 
/*     */   Object createInstance(Class _class)
/*     */     throws InstantiationException, IllegalAccessException
/*     */   {
/* 339 */     return _class.newInstance();
/*     */   }
/*     */ 
/*     */   public Class getJavaClass()
/*     */     throws ClassNotFoundException
/*     */   {
/* 350 */     QName type = getType();
/* 351 */     if ((type != null) && ("http://xml.apache.org/axis/wsdd/providers/java".equals(type.getNamespaceURI())))
/*     */     {
/* 353 */       return ClassUtils.forName(type.getLocalPart());
/*     */     }
/* 355 */     return null;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.WSDDDeployableItem
 * JD-Core Version:    0.6.0
 */