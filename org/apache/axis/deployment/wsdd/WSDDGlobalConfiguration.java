/*     */ package org.apache.axis.deployment.wsdd;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.ConfigurationException;
/*     */ import org.apache.axis.EngineConfiguration;
/*     */ import org.apache.axis.Handler;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.NodeList;
/*     */ 
/*     */ public class WSDDGlobalConfiguration extends WSDDDeployableItem
/*     */ {
/*     */   private WSDDRequestFlow requestFlow;
/*     */   private WSDDResponseFlow responseFlow;
/*  42 */   private ArrayList roles = new ArrayList();
/*     */ 
/*     */   public WSDDGlobalConfiguration()
/*     */   {
/*     */   }
/*     */ 
/*     */   public WSDDGlobalConfiguration(Element e)
/*     */     throws WSDDException
/*     */   {
/*  59 */     super(e);
/*  60 */     Element reqEl = getChildElement(e, "requestFlow");
/*  61 */     if ((reqEl != null) && (reqEl.getElementsByTagName("*").getLength() > 0)) {
/*  62 */       this.requestFlow = new WSDDRequestFlow(reqEl);
/*     */     }
/*  64 */     Element respEl = getChildElement(e, "responseFlow");
/*  65 */     if ((respEl != null) && (respEl.getElementsByTagName("*").getLength() > 0)) {
/*  66 */       this.responseFlow = new WSDDResponseFlow(respEl);
/*     */     }
/*     */ 
/*  69 */     Element[] roleElements = getChildElements(e, "role");
/*  70 */     for (int i = 0; i < roleElements.length; i++) {
/*  71 */       String role = XMLUtils.getChildCharacterData(roleElements[i]);
/*  72 */       this.roles.add(role);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected QName getElementName()
/*     */   {
/*  78 */     return WSDDConstants.QNAME_GLOBAL;
/*     */   }
/*     */ 
/*     */   public WSDDRequestFlow getRequestFlow()
/*     */   {
/*  86 */     return this.requestFlow;
/*     */   }
/*     */ 
/*     */   public void setRequestFlow(WSDDRequestFlow reqFlow)
/*     */   {
/*  94 */     this.requestFlow = reqFlow;
/*     */   }
/*     */ 
/*     */   public WSDDResponseFlow getResponseFlow()
/*     */   {
/* 102 */     return this.responseFlow;
/*     */   }
/*     */ 
/*     */   public void setResponseFlow(WSDDResponseFlow responseFlow)
/*     */   {
/* 109 */     this.responseFlow = responseFlow;
/*     */   }
/*     */ 
/*     */   public WSDDFaultFlow[] getFaultFlows()
/*     */   {
/* 118 */     return null;
/*     */   }
/*     */ 
/*     */   public WSDDFaultFlow getFaultFlow(QName name)
/*     */   {
/* 129 */     WSDDFaultFlow[] t = getFaultFlows();
/*     */ 
/* 131 */     for (int n = 0; n < t.length; n++) {
/* 132 */       if (t[n].getQName().equals(name)) {
/* 133 */         return t[n];
/*     */       }
/*     */     }
/*     */ 
/* 137 */     return null;
/*     */   }
/*     */ 
/*     */   public QName getType()
/*     */   {
/* 146 */     return null;
/*     */   }
/*     */ 
/*     */   public void setType(String type)
/*     */     throws WSDDException
/*     */   {
/* 155 */     throw new WSDDException(Messages.getMessage("noTypeOnGlobalConfig00"));
/*     */   }
/*     */ 
/*     */   public Handler makeNewInstance(EngineConfiguration registry)
/*     */   {
/* 167 */     return null;
/*     */   }
/*     */ 
/*     */   public void writeToContext(SerializationContext context)
/*     */     throws IOException
/*     */   {
/* 175 */     context.startElement(QNAME_GLOBAL, null);
/* 176 */     writeParamsToContext(context);
/* 177 */     if (this.requestFlow != null)
/* 178 */       this.requestFlow.writeToContext(context);
/* 179 */     if (this.responseFlow != null)
/* 180 */       this.responseFlow.writeToContext(context);
/* 181 */     context.endElement();
/*     */   }
/*     */ 
/*     */   public void deployToRegistry(WSDDDeployment registry) throws ConfigurationException
/*     */   {
/* 186 */     if (this.requestFlow != null)
/* 187 */       this.requestFlow.deployToRegistry(registry);
/* 188 */     if (this.responseFlow != null)
/* 189 */       this.responseFlow.deployToRegistry(registry);
/*     */   }
/*     */ 
/*     */   public List getRoles() {
/* 193 */     return (List)this.roles.clone();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration
 * JD-Core Version:    0.6.0
 */