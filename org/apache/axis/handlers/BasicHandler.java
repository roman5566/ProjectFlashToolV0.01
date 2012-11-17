/*     */ package org.apache.axis.handlers;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.List;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Handler;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.utils.LockableHashtable;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ 
/*     */ public abstract class BasicHandler
/*     */   implements Handler
/*     */ {
/*  43 */   private static Log log = LogFactory.getLog(BasicHandler.class.getName());
/*     */ 
/*  46 */   protected boolean makeLockable = false;
/*     */   protected Hashtable options;
/*     */   protected String name;
/*     */ 
/*     */   protected void setOptionsLockable(boolean makeLockable)
/*     */   {
/*  56 */     this.makeLockable = makeLockable;
/*     */   }
/*     */ 
/*     */   protected void initHashtable()
/*     */   {
/*  61 */     if (this.makeLockable)
/*  62 */       this.options = new LockableHashtable();
/*     */     else
/*  64 */       this.options = new Hashtable();
/*     */   }
/*     */ 
/*     */   public void init()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void cleanup()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean canHandleBlock(QName qname)
/*     */   {
/*  85 */     return false;
/*     */   }
/*     */ 
/*     */   public void onFault(MessageContext msgContext)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setOption(String name, Object value)
/*     */   {
/*  96 */     if (this.options == null) initHashtable();
/*  97 */     this.options.put(name, value);
/*     */   }
/*     */ 
/*     */   public boolean setOptionDefault(String name, Object value)
/*     */   {
/* 111 */     boolean val = ((this.options == null) || (this.options.get(name) == null)) && (value != null);
/* 112 */     if (val) {
/* 113 */       setOption(name, value);
/*     */     }
/* 115 */     return val;
/*     */   }
/*     */ 
/*     */   public Object getOption(String name)
/*     */   {
/* 122 */     if (this.options == null) return null;
/* 123 */     return this.options.get(name);
/*     */   }
/*     */ 
/*     */   public Hashtable getOptions()
/*     */   {
/* 130 */     return this.options;
/*     */   }
/*     */ 
/*     */   public void setOptions(Hashtable opts) {
/* 134 */     this.options = opts;
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/* 142 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 150 */     return this.name;
/*     */   }
/*     */ 
/*     */   public Element getDeploymentData(Document doc) {
/* 154 */     log.debug("Enter: BasicHandler::getDeploymentData");
/*     */ 
/* 156 */     Element root = doc.createElementNS("", "handler");
/*     */ 
/* 158 */     root.setAttribute("class", getClass().getName());
/* 159 */     this.options = getOptions();
/* 160 */     if (this.options != null) {
/* 161 */       Enumeration e = this.options.keys();
/* 162 */       while (e.hasMoreElements()) {
/* 163 */         String k = (String)e.nextElement();
/* 164 */         Object v = this.options.get(k);
/* 165 */         Element e1 = doc.createElementNS("", "option");
/* 166 */         e1.setAttribute("name", k);
/* 167 */         e1.setAttribute("value", v.toString());
/* 168 */         root.appendChild(e1);
/*     */       }
/*     */     }
/* 171 */     log.debug("Exit: BasicHandler::getDeploymentData");
/* 172 */     return root;
/*     */   }
/*     */ 
/*     */   public void generateWSDL(MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/*     */   }
/*     */ 
/*     */   public List getUnderstoodHeaders()
/*     */   {
/* 185 */     return null;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.handlers.BasicHandler
 * JD-Core Version:    0.6.0
 */