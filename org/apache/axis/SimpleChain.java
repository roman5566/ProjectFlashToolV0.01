/*     */ package org.apache.axis;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.handlers.BasicHandler;
/*     */ import org.apache.axis.strategies.InvocationStrategy;
/*     */ import org.apache.axis.strategies.WSDLGenStrategy;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ 
/*     */ public class SimpleChain extends BasicHandler
/*     */   implements Chain
/*     */ {
/*  45 */   private static Log log = LogFactory.getLog(SimpleChain.class.getName());
/*     */ 
/*  48 */   protected Vector handlers = new Vector();
/*  49 */   protected boolean invoked = false;
/*     */ 
/*  51 */   private String CAUGHTFAULT_PROPERTY = "org.apache.axis.SimpleChain.caughtFaultInResponse";
/*     */ 
/*  64 */   private static final HandlerIterationStrategy iVisitor = new InvocationStrategy();
/*     */ 
/*  67 */   private static final HandlerIterationStrategy wsdlVisitor = new WSDLGenStrategy();
/*     */ 
/*     */   public void init()
/*     */   {
/*  55 */     for (int i = 0; i < this.handlers.size(); i++)
/*  56 */       ((Handler)this.handlers.elementAt(i)).init();
/*     */   }
/*     */ 
/*     */   public void cleanup() {
/*  60 */     for (int i = 0; i < this.handlers.size(); i++)
/*  61 */       ((Handler)this.handlers.elementAt(i)).cleanup();
/*     */   }
/*     */ 
/*     */   public void invoke(MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/*  78 */     if (log.isDebugEnabled()) {
/*  79 */       log.debug("Enter: SimpleChain::invoke");
/*     */     }
/*     */ 
/*  82 */     this.invoked = true;
/*  83 */     doVisiting(msgContext, iVisitor);
/*     */ 
/*  85 */     if (log.isDebugEnabled())
/*  86 */       log.debug("Exit: SimpleChain::invoke");
/*     */   }
/*     */ 
/*     */   public void generateWSDL(MessageContext msgContext)
/*     */     throws AxisFault
/*     */   {
/*  99 */     if (log.isDebugEnabled()) {
/* 100 */       log.debug("Enter: SimpleChain::generateWSDL");
/*     */     }
/*     */ 
/* 103 */     this.invoked = true;
/* 104 */     doVisiting(msgContext, wsdlVisitor);
/*     */ 
/* 106 */     if (log.isDebugEnabled())
/* 107 */       log.debug("Exit: SimpleChain::generateWSDL");
/*     */   }
/*     */ 
/*     */   private void doVisiting(MessageContext msgContext, HandlerIterationStrategy visitor)
/*     */     throws AxisFault
/*     */   {
/* 113 */     int i = 0;
/*     */     try {
/* 115 */       Enumeration enumeration = this.handlers.elements();
/* 116 */       while (enumeration.hasMoreElements()) {
/* 117 */         Handler h = (Handler)enumeration.nextElement();
/* 118 */         visitor.visit(h, msgContext);
/* 119 */         i++;
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (AxisFault f)
/*     */     {
/* 127 */       if (!msgContext.isPropertyTrue(this.CAUGHTFAULT_PROPERTY))
/*     */       {
/* 130 */         Message respMsg = new Message(f);
/* 131 */         msgContext.setResponseMessage(respMsg);
/* 132 */         msgContext.setProperty(this.CAUGHTFAULT_PROPERTY, Boolean.TRUE);
/*     */       }while (true) {
/* 134 */         i--; if (i < 0) break;
/* 135 */         ((Handler)this.handlers.elementAt(i)).onFault(msgContext);
/* 136 */       }throw f;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onFault(MessageContext msgContext)
/*     */   {
/* 149 */     if (log.isDebugEnabled()) {
/* 150 */       log.debug("Enter: SimpleChain::onFault");
/*     */     }
/*     */ 
/* 153 */     for (int i = this.handlers.size() - 1; i >= 0; i--) {
/* 154 */       ((Handler)this.handlers.elementAt(i)).onFault(msgContext);
/*     */     }
/* 156 */     if (log.isDebugEnabled())
/* 157 */       log.debug("Exit: SimpleChain::onFault");
/*     */   }
/*     */ 
/*     */   public boolean canHandleBlock(QName qname)
/*     */   {
/* 162 */     for (int i = 0; i < this.handlers.size(); i++)
/* 163 */       if (((Handler)this.handlers.elementAt(i)).canHandleBlock(qname))
/* 164 */         return true;
/* 165 */     return false;
/*     */   }
/*     */ 
/*     */   public void addHandler(Handler handler) {
/* 169 */     if (handler == null) {
/* 170 */       throw new InternalException(Messages.getMessage("nullHandler00", "SimpleChain::addHandler"));
/*     */     }
/*     */ 
/* 174 */     if (this.invoked) {
/* 175 */       throw new InternalException(Messages.getMessage("addAfterInvoke00", "SimpleChain::addHandler"));
/*     */     }
/*     */ 
/* 179 */     this.handlers.add(handler);
/*     */   }
/*     */ 
/*     */   public boolean contains(Handler handler) {
/* 183 */     return this.handlers.contains(handler);
/*     */   }
/*     */ 
/*     */   public Handler[] getHandlers() {
/* 187 */     if (this.handlers.size() == 0) {
/* 188 */       return null;
/*     */     }
/* 190 */     Handler[] ret = new Handler[this.handlers.size()];
/* 191 */     return (Handler[])this.handlers.toArray(ret);
/*     */   }
/*     */ 
/*     */   public Element getDeploymentData(Document doc) {
/* 195 */     if (log.isDebugEnabled()) {
/* 196 */       log.debug(Messages.getMessage("enter00", "SimpleChain::getDeploymentData"));
/*     */     }
/*     */ 
/* 200 */     Element root = doc.createElementNS("", "chain");
/*     */ 
/* 202 */     StringBuffer str = new StringBuffer();
/* 203 */     int i = 0;
/* 204 */     while (i < this.handlers.size()) {
/* 205 */       if (i != 0) str.append(",");
/* 206 */       Handler h = (Handler)this.handlers.elementAt(i);
/* 207 */       str.append(h.getName());
/* 208 */       i++;
/*     */     }
/* 210 */     if (i > 0) {
/* 211 */       root.setAttribute("flow", str.toString());
/*     */     }
/*     */ 
/* 214 */     if (this.options != null) {
/* 215 */       Enumeration e = this.options.keys();
/* 216 */       while (e.hasMoreElements()) {
/* 217 */         String k = (String)e.nextElement();
/* 218 */         Object v = this.options.get(k);
/* 219 */         Element e1 = doc.createElementNS("", "option");
/* 220 */         e1.setAttribute("name", k);
/* 221 */         e1.setAttribute("value", v.toString());
/* 222 */         root.appendChild(e1);
/*     */       }
/*     */     }
/*     */ 
/* 226 */     if (log.isDebugEnabled()) {
/* 227 */       log.debug("Exit: SimpleChain::getDeploymentData");
/*     */     }
/*     */ 
/* 230 */     return root;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.SimpleChain
 * JD-Core Version:    0.6.0
 */