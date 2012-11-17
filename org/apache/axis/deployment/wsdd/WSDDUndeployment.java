/*     */ package org.apache.axis.deployment.wsdd;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Iterator;
/*     */ import java.util.Vector;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.AxisEngine;
/*     */ import org.apache.axis.ConfigurationException;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.handlers.soap.SOAPService;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.w3c.dom.Element;
/*     */ import org.xml.sax.helpers.AttributesImpl;
/*     */ 
/*     */ public class WSDDUndeployment extends WSDDElement
/*     */   implements WSDDTypeMappingContainer
/*     */ {
/*  41 */   private Vector handlers = new Vector();
/*  42 */   private Vector chains = new Vector();
/*  43 */   private Vector services = new Vector();
/*  44 */   private Vector transports = new Vector();
/*  45 */   private Vector typeMappings = new Vector();
/*     */ 
/*     */   public void addHandler(QName handler)
/*     */   {
/*  49 */     this.handlers.add(handler);
/*     */   }
/*     */ 
/*     */   public void addChain(QName chain)
/*     */   {
/*  54 */     this.chains.add(chain);
/*     */   }
/*     */ 
/*     */   public void addTransport(QName transport)
/*     */   {
/*  59 */     this.transports.add(transport);
/*     */   }
/*     */ 
/*     */   public void addService(QName service)
/*     */   {
/*  64 */     this.services.add(service);
/*     */   }
/*     */ 
/*     */   public void deployTypeMapping(WSDDTypeMapping typeMapping)
/*     */     throws WSDDException
/*     */   {
/*  70 */     this.typeMappings.add(typeMapping);
/*     */   }
/*     */ 
/*     */   public WSDDUndeployment()
/*     */   {
/*     */   }
/*     */ 
/*     */   private QName getQName(Element el)
/*     */     throws WSDDException
/*     */   {
/*  82 */     String attr = el.getAttribute("name");
/*  83 */     if ((attr == null) || ("".equals(attr)))
/*  84 */       throw new WSDDException(Messages.getMessage("badNameAttr00"));
/*  85 */     return new QName("", attr);
/*     */   }
/*     */ 
/*     */   public WSDDUndeployment(Element e)
/*     */     throws WSDDException
/*     */   {
/*  97 */     super(e);
/*     */ 
/*  99 */     Element[] elements = getChildElements(e, "handler");
/*     */ 
/* 102 */     for (int i = 0; i < elements.length; i++) {
/* 103 */       addHandler(getQName(elements[i]));
/*     */     }
/*     */ 
/* 106 */     elements = getChildElements(e, "chain");
/* 107 */     for (i = 0; i < elements.length; i++) {
/* 108 */       addChain(getQName(elements[i]));
/*     */     }
/*     */ 
/* 111 */     elements = getChildElements(e, "transport");
/* 112 */     for (i = 0; i < elements.length; i++) {
/* 113 */       addTransport(getQName(elements[i]));
/*     */     }
/*     */ 
/* 116 */     elements = getChildElements(e, "service");
/* 117 */     for (i = 0; i < elements.length; i++)
/* 118 */       addService(getQName(elements[i]));
/*     */   }
/*     */ 
/*     */   protected QName getElementName()
/*     */   {
/* 140 */     return QNAME_UNDEPLOY;
/*     */   }
/*     */ 
/*     */   public void undeployFromRegistry(WSDDDeployment registry)
/*     */     throws ConfigurationException
/*     */   {
/* 147 */     for (int n = 0; n < this.handlers.size(); n++) {
/* 148 */       QName qname = (QName)this.handlers.get(n);
/* 149 */       registry.undeployHandler(qname);
/*     */     }
/*     */ 
/* 152 */     for (int n = 0; n < this.chains.size(); n++) {
/* 153 */       QName qname = (QName)this.chains.get(n);
/* 154 */       registry.undeployHandler(qname);
/*     */     }
/*     */ 
/* 157 */     for (int n = 0; n < this.transports.size(); n++) {
/* 158 */       QName qname = (QName)this.transports.get(n);
/* 159 */       registry.undeployTransport(qname);
/*     */     }
/*     */ 
/* 162 */     for (int n = 0; n < this.services.size(); n++) {
/* 163 */       QName qname = (QName)this.services.get(n);
/*     */       try
/*     */       {
/* 166 */         String sname = qname.getLocalPart();
/* 167 */         MessageContext messageContext = MessageContext.getCurrentContext();
/* 168 */         if (messageContext != null) {
/* 169 */           SOAPService service = messageContext.getAxisEngine().getService(sname);
/*     */ 
/* 171 */           if (service != null) service.clearSessions(); 
/*     */         }
/*     */       }
/*     */       catch (Exception exp) {
/* 174 */         throw new ConfigurationException(exp);
/*     */       }
/* 176 */       registry.undeployService(qname);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void writeElement(SerializationContext context, QName elementQName, QName qname)
/*     */     throws IOException
/*     */   {
/* 185 */     AttributesImpl attrs = new AttributesImpl();
/* 186 */     attrs.addAttribute("", "name", "name", "CDATA", context.qName2String(qname));
/*     */ 
/* 189 */     context.startElement(elementQName, attrs);
/* 190 */     context.endElement();
/*     */   }
/*     */ 
/*     */   public void writeToContext(SerializationContext context)
/*     */     throws IOException
/*     */   {
/* 196 */     context.registerPrefixForURI("", "http://xml.apache.org/axis/wsdd/");
/* 197 */     context.startElement(WSDDConstants.QNAME_UNDEPLOY, null);
/*     */ 
/* 199 */     Iterator i = this.handlers.iterator();
/*     */ 
/* 201 */     while (i.hasNext()) {
/* 202 */       QName qname = (QName)i.next();
/* 203 */       writeElement(context, QNAME_HANDLER, qname);
/*     */     }
/*     */ 
/* 206 */     i = this.chains.iterator();
/* 207 */     while (i.hasNext()) {
/* 208 */       QName qname = (QName)i.next();
/* 209 */       writeElement(context, QNAME_CHAIN, qname);
/*     */     }
/*     */ 
/* 212 */     i = this.services.iterator();
/* 213 */     while (i.hasNext()) {
/* 214 */       QName qname = (QName)i.next();
/* 215 */       writeElement(context, QNAME_SERVICE, qname);
/*     */     }
/*     */ 
/* 218 */     i = this.transports.iterator();
/* 219 */     while (i.hasNext()) {
/* 220 */       QName qname = (QName)i.next();
/* 221 */       writeElement(context, QNAME_TRANSPORT, qname);
/*     */     }
/*     */ 
/* 224 */     i = this.typeMappings.iterator();
/* 225 */     while (i.hasNext()) {
/* 226 */       WSDDTypeMapping mapping = (WSDDTypeMapping)i.next();
/* 227 */       mapping.writeToContext(context);
/*     */     }
/*     */ 
/* 230 */     context.endElement();
/*     */   }
/*     */ 
/*     */   public WSDDTypeMapping[] getTypeMappings()
/*     */   {
/* 239 */     WSDDTypeMapping[] t = new WSDDTypeMapping[this.typeMappings.size()];
/* 240 */     this.typeMappings.toArray(t);
/* 241 */     return t;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.WSDDUndeployment
 * JD-Core Version:    0.6.0
 */