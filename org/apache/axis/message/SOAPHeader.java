/*     */ package org.apache.axis.message;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.soap.Name;
/*     */ import javax.xml.soap.SOAPElement;
/*     */ import javax.xml.soap.SOAPException;
/*     */ import org.apache.axis.AxisEngine;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.encoding.DeserializationContext;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.handlers.soap.SOAPService;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.DOMException;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.xml.sax.Attributes;
/*     */ 
/*     */ public class SOAPHeader extends MessageElement
/*     */   implements javax.xml.soap.SOAPHeader
/*     */ {
/*  53 */   private static Log log = LogFactory.getLog(SOAPHeader.class.getName());
/*     */   private SOAPConstants soapConstants;
/*     */ 
/*     */   SOAPHeader(SOAPEnvelope env, SOAPConstants soapConsts)
/*     */   {
/*  58 */     super("Header", "soapenv", soapConsts != null ? soapConsts.getEnvelopeURI() : Constants.DEFAULT_SOAP_VERSION.getEnvelopeURI());
/*     */ 
/*  61 */     this.soapConstants = (soapConsts != null ? soapConsts : Constants.DEFAULT_SOAP_VERSION);
/*     */     try {
/*  63 */       setParentElement(env);
/*     */     }
/*     */     catch (SOAPException ex) {
/*  66 */       log.fatal(Messages.getMessage("exception00"), ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public SOAPHeader(String namespace, String localPart, String prefix, Attributes attributes, DeserializationContext context, SOAPConstants soapConsts)
/*     */     throws AxisFault
/*     */   {
/*  73 */     super(namespace, localPart, prefix, attributes, context);
/*  74 */     this.soapConstants = (soapConsts != null ? soapConsts : Constants.DEFAULT_SOAP_VERSION);
/*     */   }
/*     */ 
/*     */   public void setParentElement(SOAPElement parent) throws SOAPException {
/*  78 */     if (parent == null) {
/*  79 */       throw new IllegalArgumentException(Messages.getMessage("nullParent00"));
/*     */     }
/*     */     try
/*     */     {
/*  83 */       SOAPEnvelope env = (SOAPEnvelope)parent;
/*  84 */       super.setParentElement(env);
/*  85 */       setEnvelope(env);
/*     */     } catch (Throwable t) {
/*  87 */       throw new SOAPException(t);
/*     */     }
/*     */   }
/*     */ 
/*     */   public javax.xml.soap.SOAPHeaderElement addHeaderElement(Name name) throws SOAPException
/*     */   {
/*  93 */     SOAPHeaderElement headerElement = new SOAPHeaderElement(name);
/*  94 */     addChildElement(headerElement);
/*  95 */     return headerElement;
/*     */   }
/*     */ 
/*     */   private Vector findHeaderElements(String actor) {
/*  99 */     ArrayList actors = new ArrayList();
/* 100 */     actors.add(actor);
/* 101 */     return getHeadersByActor(actors);
/*     */   }
/*     */ 
/*     */   public Iterator examineHeaderElements(String actor) {
/* 105 */     return findHeaderElements(actor).iterator();
/*     */   }
/*     */ 
/*     */   public Iterator extractHeaderElements(String actor) {
/* 109 */     Vector results = findHeaderElements(actor);
/*     */ 
/* 111 */     Iterator iterator = results.iterator();
/*     */ 
/* 113 */     while (iterator.hasNext()) {
/* 114 */       ((SOAPHeaderElement)iterator.next()).detachNode();
/*     */     }
/*     */ 
/* 117 */     return results.iterator();
/*     */   }
/*     */ 
/*     */   public Iterator examineMustUnderstandHeaderElements(String actor) {
/* 121 */     if (actor == null) return null;
/*     */ 
/* 123 */     Vector result = new Vector();
/* 124 */     List headers = getChildren();
/* 125 */     if (headers != null) {
/* 126 */       for (int i = 0; i < headers.size(); i++) {
/* 127 */         SOAPHeaderElement she = (SOAPHeaderElement)headers.get(i);
/* 128 */         if (she.getMustUnderstand()) {
/* 129 */           String candidate = she.getActor();
/* 130 */           if (actor.equals(candidate)) {
/* 131 */             result.add(headers.get(i));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 136 */     return result.iterator();
/*     */   }
/*     */ 
/*     */   public Iterator examineAllHeaderElements() {
/* 140 */     return getChildElements();
/*     */   }
/*     */ 
/*     */   public Iterator extractAllHeaderElements() {
/* 144 */     Vector result = new Vector();
/* 145 */     List headers = getChildren();
/* 146 */     if (headers != null) {
/* 147 */       for (int i = 0; i < headers.size(); i++) {
/* 148 */         result.add(headers.get(i));
/*     */       }
/* 150 */       headers.clear();
/*     */     }
/* 152 */     return result.iterator();
/*     */   }
/*     */ 
/*     */   Vector getHeaders() {
/* 156 */     initializeChildren();
/* 157 */     return new Vector(getChildren());
/*     */   }
/*     */ 
/*     */   Vector getHeadersByActor(ArrayList actors)
/*     */   {
/* 164 */     Vector results = new Vector();
/* 165 */     List headers = getChildren();
/* 166 */     if (headers == null) {
/* 167 */       return results;
/*     */     }
/* 169 */     Iterator i = headers.iterator();
/* 170 */     SOAPConstants soapVer = getEnvelope().getSOAPConstants();
/* 171 */     boolean isSOAP12 = soapVer == SOAPConstants.SOAP12_CONSTANTS;
/* 172 */     String nextActor = soapVer.getNextRoleURI();
/* 173 */     while (i.hasNext()) {
/* 174 */       SOAPHeaderElement header = (SOAPHeaderElement)i.next();
/* 175 */       String actor = header.getActor();
/*     */ 
/* 178 */       if ((isSOAP12) && ("http://www.w3.org/2003/05/soap-envelope/role/none".equals(actor)))
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/* 184 */       if ((actor == null) || (nextActor.equals(actor)) || ((isSOAP12) && ("http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver".equals(actor))) || ((actors != null) && (actors.contains(actor))))
/*     */       {
/* 189 */         results.add(header);
/*     */       }
/*     */     }
/* 192 */     return results;
/*     */   }
/*     */ 
/*     */   void addHeader(SOAPHeaderElement header) {
/* 196 */     if (log.isDebugEnabled())
/* 197 */       log.debug(Messages.getMessage("addHeader00"));
/*     */     try {
/* 199 */       addChildElement(header);
/*     */     }
/*     */     catch (SOAPException ex) {
/* 202 */       log.fatal(Messages.getMessage("exception00"), ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   void removeHeader(SOAPHeaderElement header) {
/* 207 */     if (log.isDebugEnabled())
/* 208 */       log.debug(Messages.getMessage("removeHeader00"));
/* 209 */     removeChild(header);
/*     */   }
/*     */ 
/*     */   SOAPHeaderElement getHeaderByName(String namespace, String localPart, boolean accessAllHeaders)
/*     */   {
/* 219 */     QName name = new QName(namespace, localPart);
/* 220 */     SOAPHeaderElement header = (SOAPHeaderElement)getChildElement(name);
/*     */ 
/* 224 */     if (!accessAllHeaders) {
/* 225 */       MessageContext mc = MessageContext.getCurrentContext();
/* 226 */       if ((mc != null) && 
/* 227 */         (header != null)) {
/* 228 */         String actor = header.getActor();
/*     */ 
/* 231 */         String nextActor = getEnvelope().getSOAPConstants().getNextRoleURI();
/*     */ 
/* 233 */         if (nextActor.equals(actor)) {
/* 234 */           return header;
/*     */         }
/* 236 */         SOAPService soapService = mc.getService();
/* 237 */         if (soapService != null) {
/* 238 */           ArrayList actors = mc.getService().getActors();
/* 239 */           if ((actor != null) && ((actors == null) || (!actors.contains(actor))))
/*     */           {
/* 241 */             header = null;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 248 */     return header;
/*     */   }
/*     */ 
/*     */   Enumeration getHeadersByName(String namespace, String localPart, boolean accessAllHeaders)
/*     */   {
/* 264 */     ArrayList actors = null;
/* 265 */     boolean firstTime = false;
/*     */ 
/* 271 */     Vector v = new Vector();
/* 272 */     List headers = getChildren();
/* 273 */     if (headers == null) {
/* 274 */       return v.elements();
/*     */     }
/* 276 */     Iterator e = headers.iterator();
/*     */ 
/* 278 */     String nextActor = getEnvelope().getSOAPConstants().getNextRoleURI();
/*     */ 
/* 280 */     while (e.hasNext()) {
/* 281 */       SOAPHeaderElement header = (SOAPHeaderElement)e.next();
/* 282 */       if ((!header.getNamespaceURI().equals(namespace)) || (!header.getName().equals(localPart))) {
/*     */         continue;
/*     */       }
/* 285 */       if (!accessAllHeaders) {
/* 286 */         if (firstTime)
/*     */         {
/* 288 */           MessageContext mc = MessageContext.getCurrentContext();
/* 289 */           if ((mc != null) && (mc.getAxisEngine() != null)) {
/* 290 */             actors = mc.getAxisEngine().getActorURIs();
/*     */           }
/* 292 */           firstTime = false;
/*     */         }
/*     */ 
/* 295 */         String actor = header.getActor();
/* 296 */         if ((actor != null) && (!nextActor.equals(actor)) && ((actors == null) || (!actors.contains(actor))))
/*     */         {
/*     */           continue;
/*     */         }
/*     */       }
/*     */ 
/* 302 */       v.addElement(header);
/*     */     }
/*     */ 
/* 306 */     return v.elements();
/*     */   }
/*     */ 
/*     */   protected void outputImpl(SerializationContext context) throws Exception {
/* 310 */     List headers = getChildren();
/* 311 */     if (headers == null) {
/* 312 */       return;
/*     */     }
/* 314 */     boolean oldPretty = context.getPretty();
/* 315 */     context.setPretty(true);
/*     */ 
/* 317 */     if (log.isDebugEnabled()) {
/* 318 */       log.debug(headers.size() + " " + Messages.getMessage("headers00"));
/*     */     }
/*     */ 
/* 321 */     if (!headers.isEmpty())
/*     */     {
/* 323 */       context.startElement(new QName(this.soapConstants.getEnvelopeURI(), "Header"), null);
/*     */ 
/* 325 */       Iterator enumeration = headers.iterator();
/* 326 */       while (enumeration.hasNext())
/*     */       {
/* 328 */         ((NodeImpl)enumeration.next()).output(context);
/*     */       }
/*     */ 
/* 331 */       context.endElement();
/*     */     }
/*     */ 
/* 334 */     context.setPretty(oldPretty);
/*     */   }
/*     */ 
/*     */   public void addChild(MessageElement element) throws SOAPException
/*     */   {
/* 339 */     if (!(element instanceof SOAPHeaderElement)) {
/* 340 */       throw new SOAPException(Messages.getMessage("badSOAPHeader00"));
/*     */     }
/* 342 */     element.setEnvelope(getEnvelope());
/* 343 */     super.addChild(element);
/*     */   }
/*     */ 
/*     */   public SOAPElement addChildElement(SOAPElement element)
/*     */     throws SOAPException
/*     */   {
/* 349 */     if (!(element instanceof SOAPHeaderElement)) {
/* 350 */       throw new SOAPException(Messages.getMessage("badSOAPHeader00"));
/*     */     }
/* 352 */     SOAPElement child = super.addChildElement(element);
/* 353 */     setDirty();
/* 354 */     return child;
/*     */   }
/*     */ 
/*     */   public SOAPElement addChildElement(Name name) throws SOAPException {
/* 358 */     SOAPHeaderElement child = new SOAPHeaderElement(name);
/* 359 */     addChildElement(child);
/* 360 */     return child;
/*     */   }
/*     */ 
/*     */   public SOAPElement addChildElement(String localName) throws SOAPException
/*     */   {
/* 365 */     SOAPHeaderElement child = new SOAPHeaderElement(getNamespaceURI(), localName);
/*     */ 
/* 367 */     addChildElement(child);
/* 368 */     return child;
/*     */   }
/*     */ 
/*     */   public SOAPElement addChildElement(String localName, String prefix) throws SOAPException
/*     */   {
/* 373 */     SOAPHeaderElement child = new SOAPHeaderElement(getNamespaceURI(prefix), localName);
/*     */ 
/* 375 */     child.setPrefix(prefix);
/* 376 */     addChildElement(child);
/* 377 */     return child;
/*     */   }
/*     */ 
/*     */   public SOAPElement addChildElement(String localName, String prefix, String uri)
/*     */     throws SOAPException
/*     */   {
/* 383 */     SOAPHeaderElement child = new SOAPHeaderElement(uri, localName);
/* 384 */     child.setPrefix(prefix);
/* 385 */     child.addNamespaceDeclaration(prefix, uri);
/* 386 */     addChildElement(child);
/* 387 */     return child;
/*     */   }
/*     */ 
/*     */   public Node appendChild(Node newChild) throws DOMException {
/* 391 */     SOAPHeaderElement headerElement = null;
/* 392 */     if ((newChild instanceof SOAPHeaderElement))
/* 393 */       headerElement = (SOAPHeaderElement)newChild;
/*     */     else
/* 395 */       headerElement = new SOAPHeaderElement((Element)newChild);
/*     */     try {
/* 397 */       addChildElement(headerElement);
/*     */     } catch (SOAPException e) {
/* 399 */       throw new DOMException(11, e.toString());
/*     */     }
/* 401 */     return headerElement;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.SOAPHeader
 * JD-Core Version:    0.6.0
 */