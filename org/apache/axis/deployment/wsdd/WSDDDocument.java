/*     */ package org.apache.axis.deployment.wsdd;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ import org.apache.axis.ConfigurationException;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.xml.sax.InputSource;
/*     */ 
/*     */ public class WSDDDocument extends WSDDConstants
/*     */ {
/*  39 */   protected static Log log = LogFactory.getLog(WSDDDocument.class.getName());
/*     */   private Document doc;
/*     */   private WSDDDeployment deployment;
/*     */   private WSDDUndeployment undeployment;
/*     */ 
/*     */   public WSDDDocument()
/*     */   {
/*     */   }
/*     */ 
/*     */   public WSDDDocument(Document document)
/*     */     throws WSDDException
/*     */   {
/*  65 */     setDocument(document);
/*     */   }
/*     */ 
/*     */   public WSDDDocument(Element e)
/*     */     throws WSDDException
/*     */   {
/*  74 */     this.doc = e.getOwnerDocument();
/*  75 */     if ("undeployment".equals(e.getLocalName()))
/*  76 */       this.undeployment = new WSDDUndeployment(e);
/*     */     else
/*  78 */       this.deployment = new WSDDDeployment(e);
/*     */   }
/*     */ 
/*     */   public WSDDDeployment getDeployment()
/*     */   {
/*  88 */     if (this.deployment == null) {
/*  89 */       this.deployment = new WSDDDeployment();
/*     */     }
/*  91 */     return this.deployment;
/*     */   }
/*     */ 
/*     */   public Document getDOMDocument()
/*     */     throws ConfigurationException
/*     */   {
/* 101 */     StringWriter writer = new StringWriter();
/* 102 */     SerializationContext context = new SerializationContext(writer);
/* 103 */     context.setPretty(true);
/*     */     try {
/* 105 */       this.deployment.writeToContext(context);
/*     */     } catch (Exception e) {
/* 107 */       log.error(Messages.getMessage("exception00"), e);
/*     */     }
/*     */     try {
/* 110 */       writer.close();
/* 111 */       return XMLUtils.newDocument(new InputSource(new StringReader(writer.getBuffer().toString()))); } catch (Exception e) {
/*     */     }
/* 113 */     return null;
/*     */   }
/*     */ 
/*     */   public void writeToContext(SerializationContext context)
/*     */     throws IOException
/*     */   {
/* 125 */     getDeployment().writeToContext(context);
/*     */   }
/*     */ 
/*     */   public void setDocument(Document document)
/*     */     throws WSDDException
/*     */   {
/* 134 */     this.doc = document;
/* 135 */     Element docEl = this.doc.getDocumentElement();
/* 136 */     if ("undeployment".equals(docEl.getLocalName()))
/* 137 */       this.undeployment = new WSDDUndeployment(docEl);
/*     */     else
/* 139 */       this.deployment = new WSDDDeployment(docEl);
/*     */   }
/*     */ 
/*     */   public void deploy(WSDDDeployment registry)
/*     */     throws ConfigurationException
/*     */   {
/* 150 */     if (this.deployment != null) {
/* 151 */       this.deployment.deployToRegistry(registry);
/*     */     }
/* 153 */     if (this.undeployment != null)
/* 154 */       this.undeployment.undeployFromRegistry(registry);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.WSDDDocument
 * JD-Core Version:    0.6.0
 */