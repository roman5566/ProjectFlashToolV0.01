/*     */ package org.apache.axis.deployment.wsdd;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.ConfigurationException;
/*     */ import org.apache.axis.EngineConfiguration;
/*     */ import org.apache.axis.Handler;
/*     */ import org.apache.axis.SimpleTargetedChain;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.utils.ClassUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.NodeList;
/*     */ 
/*     */ public abstract class WSDDTargetedChain extends WSDDDeployableItem
/*     */ {
/*     */   private WSDDRequestFlow requestFlow;
/*     */   private WSDDResponseFlow responseFlow;
/*     */   private QName pivotQName;
/*     */ 
/*     */   protected WSDDTargetedChain()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected WSDDTargetedChain(Element e)
/*     */     throws WSDDException
/*     */   {
/*  53 */     super(e);
/*  54 */     Element reqEl = getChildElement(e, "requestFlow");
/*  55 */     if ((reqEl != null) && (reqEl.getElementsByTagName("*").getLength() > 0)) {
/*  56 */       this.requestFlow = new WSDDRequestFlow(reqEl);
/*     */     }
/*  58 */     Element respEl = getChildElement(e, "responseFlow");
/*  59 */     if ((respEl != null) && (respEl.getElementsByTagName("*").getLength() > 0)) {
/*  60 */       this.responseFlow = new WSDDResponseFlow(respEl);
/*     */     }
/*     */ 
/*  64 */     String pivotStr = e.getAttribute("pivot");
/*  65 */     if ((pivotStr != null) && (!pivotStr.equals("")))
/*  66 */       this.pivotQName = XMLUtils.getQNameFromString(pivotStr, e);
/*     */   }
/*     */ 
/*     */   public WSDDRequestFlow getRequestFlow()
/*     */   {
/*  72 */     return this.requestFlow;
/*     */   }
/*     */ 
/*     */   public void setRequestFlow(WSDDRequestFlow flow)
/*     */   {
/*  77 */     this.requestFlow = flow;
/*     */   }
/*     */ 
/*     */   public WSDDResponseFlow getResponseFlow()
/*     */   {
/*  82 */     return this.responseFlow;
/*     */   }
/*     */ 
/*     */   public void setResponseFlow(WSDDResponseFlow flow)
/*     */   {
/*  87 */     this.responseFlow = flow;
/*     */   }
/*     */ 
/*     */   public WSDDFaultFlow[] getFaultFlows()
/*     */   {
/*  96 */     return null;
/*     */   }
/*     */ 
/*     */   public WSDDFaultFlow getFaultFlow(QName name)
/*     */   {
/* 107 */     WSDDFaultFlow[] t = getFaultFlows();
/*     */ 
/* 109 */     for (int n = 0; n < t.length; n++) {
/* 110 */       if (t[n].getQName().equals(name)) {
/* 111 */         return t[n];
/*     */       }
/*     */     }
/*     */ 
/* 115 */     return null;
/*     */   }
/*     */ 
/*     */   public void setType(String type)
/*     */     throws WSDDException
/*     */   {
/* 124 */     throw new WSDDException(Messages.getMessage("noTypeSetting", getElementName().getLocalPart()));
/*     */   }
/*     */ 
/*     */   public QName getPivotQName()
/*     */   {
/* 130 */     return this.pivotQName;
/*     */   }
/*     */ 
/*     */   public void setPivotQName(QName pivotQName) {
/* 134 */     this.pivotQName = pivotQName;
/*     */   }
/*     */ 
/*     */   public Handler makeNewInstance(EngineConfiguration registry)
/*     */     throws ConfigurationException
/*     */   {
/* 147 */     Handler reqHandler = null;
/*     */ 
/* 149 */     WSDDChain req = getRequestFlow();
/* 150 */     if (req != null) {
/* 151 */       reqHandler = req.getInstance(registry);
/*     */     }
/* 153 */     Handler pivot = null;
/* 154 */     if (this.pivotQName != null) {
/* 155 */       if ("http://xml.apache.org/axis/wsdd/providers/java".equals(this.pivotQName.getNamespaceURI()))
/*     */         try {
/* 157 */           pivot = (Handler)ClassUtils.forName(this.pivotQName.getLocalPart()).newInstance();
/*     */         } catch (InstantiationException e) {
/* 159 */           throw new ConfigurationException(e);
/*     */         } catch (IllegalAccessException e) {
/* 161 */           throw new ConfigurationException(e);
/*     */         } catch (ClassNotFoundException e) {
/* 163 */           throw new ConfigurationException(e);
/*     */         }
/*     */       else {
/* 166 */         pivot = registry.getHandler(this.pivotQName);
/*     */       }
/*     */     }
/*     */ 
/* 170 */     Handler respHandler = null;
/* 171 */     WSDDChain resp = getResponseFlow();
/* 172 */     if (resp != null) {
/* 173 */       respHandler = resp.getInstance(registry);
/*     */     }
/* 175 */     Handler retVal = new SimpleTargetedChain(reqHandler, pivot, respHandler);
/*     */ 
/* 177 */     retVal.setOptions(getParametersTable());
/* 178 */     return retVal;
/*     */   }
/*     */ 
/*     */   public final void writeFlowsToContext(SerializationContext context)
/*     */     throws IOException
/*     */   {
/* 186 */     if (this.requestFlow != null) {
/* 187 */       this.requestFlow.writeToContext(context);
/*     */     }
/* 189 */     if (this.responseFlow != null)
/* 190 */       this.responseFlow.writeToContext(context);
/*     */   }
/*     */ 
/*     */   public void deployToRegistry(WSDDDeployment registry)
/*     */   {
/* 198 */     if (this.requestFlow != null) {
/* 199 */       this.requestFlow.deployToRegistry(registry);
/*     */     }
/*     */ 
/* 202 */     if (this.responseFlow != null)
/* 203 */       this.responseFlow.deployToRegistry(registry);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.WSDDTargetedChain
 * JD-Core Version:    0.6.0
 */