/*     */ package org.apache.axis.deployment.wsdd;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Vector;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.Chain;
/*     */ import org.apache.axis.ConfigurationException;
/*     */ import org.apache.axis.EngineConfiguration;
/*     */ import org.apache.axis.Handler;
/*     */ import org.apache.axis.SimpleChain;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.w3c.dom.Element;
/*     */ import org.xml.sax.helpers.AttributesImpl;
/*     */ 
/*     */ public class WSDDChain extends WSDDHandler
/*     */ {
/*  39 */   private Vector handlers = new Vector();
/*     */ 
/*     */   public WSDDChain()
/*     */   {
/*     */   }
/*     */ 
/*     */   public WSDDChain(Element e)
/*     */     throws WSDDException
/*     */   {
/*  56 */     super(e);
/*     */ 
/*  60 */     if (this.type != null) {
/*  61 */       return;
/*     */     }
/*  63 */     Element[] elements = getChildElements(e, "handler");
/*  64 */     if (elements.length != 0) {
/*  65 */       for (int i = 0; i < elements.length; i++) {
/*  66 */         WSDDHandler handler = new WSDDHandler(elements[i]);
/*  67 */         addHandler(handler);
/*     */       }
/*     */     }
/*     */ 
/*  71 */     elements = getChildElements(e, "chain");
/*  72 */     if (elements.length != 0)
/*  73 */       for (int i = 0; i < elements.length; i++) {
/*  74 */         WSDDChain chain = new WSDDChain(elements[i]);
/*  75 */         addHandler(chain);
/*     */       }
/*     */   }
/*     */ 
/*     */   protected QName getElementName()
/*     */   {
/*  83 */     return WSDDConstants.QNAME_CHAIN;
/*     */   }
/*     */ 
/*     */   public void addHandler(WSDDHandler handler)
/*     */   {
/*  91 */     this.handlers.add(handler);
/*     */   }
/*     */ 
/*     */   public Vector getHandlers()
/*     */   {
/* 101 */     return this.handlers;
/*     */   }
/*     */ 
/*     */   public void removeHandler(WSDDHandler victim)
/*     */   {
/* 109 */     this.handlers.remove(victim);
/*     */   }
/*     */ 
/*     */   public Handler makeNewInstance(EngineConfiguration registry)
/*     */     throws ConfigurationException
/*     */   {
/* 121 */     Chain c = new SimpleChain();
/*     */ 
/* 123 */     for (int n = 0; n < this.handlers.size(); n++) {
/* 124 */       WSDDHandler handler = (WSDDHandler)this.handlers.get(n);
/* 125 */       Handler h = handler.getInstance(registry);
/* 126 */       if (h != null)
/* 127 */         c.addHandler(h);
/*     */       else {
/* 129 */         throw new ConfigurationException("Can't find handler name:'" + handler.getQName() + "' type:'" + handler.getType() + "' in the registry");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 135 */     return c;
/*     */   }
/*     */ 
/*     */   public void writeToContext(SerializationContext context)
/*     */     throws IOException
/*     */   {
/* 144 */     AttributesImpl attrs = new AttributesImpl();
/* 145 */     QName name = getQName();
/* 146 */     if (name != null) {
/* 147 */       attrs.addAttribute("", "name", "name", "CDATA", context.qName2String(name));
/*     */     }
/*     */ 
/* 150 */     if (getType() != null) {
/* 151 */       attrs.addAttribute("", "type", "type", "CDATA", context.qName2String(getType()));
/*     */     }
/*     */ 
/* 155 */     context.startElement(getElementName(), attrs);
/* 156 */     for (int n = 0; n < this.handlers.size(); n++) {
/* 157 */       WSDDHandler handler = (WSDDHandler)this.handlers.get(n);
/* 158 */       handler.writeToContext(context);
/*     */     }
/* 160 */     context.endElement();
/*     */   }
/*     */ 
/*     */   public void deployToRegistry(WSDDDeployment registry)
/*     */   {
/* 165 */     if (getQName() != null) {
/* 166 */       registry.addHandler(this);
/*     */     }
/* 168 */     for (int n = 0; n < this.handlers.size(); n++) {
/* 169 */       WSDDHandler handler = (WSDDHandler)this.handlers.get(n);
/* 170 */       if (handler.getQName() != null)
/* 171 */         handler.deployToRegistry(registry);
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.WSDDChain
 * JD-Core Version:    0.6.0
 */