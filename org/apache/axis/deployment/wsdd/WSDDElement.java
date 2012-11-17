/*     */ package org.apache.axis.deployment.wsdd;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.util.Vector;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ 
/*     */ public abstract class WSDDElement extends WSDDConstants
/*     */   implements Serializable
/*     */ {
/*     */   private String name;
/*     */ 
/*     */   public WSDDElement()
/*     */   {
/*     */   }
/*     */ 
/*     */   public WSDDElement(Element e)
/*     */     throws WSDDException
/*     */   {
/*  54 */     validateCandidateElement(e);
/*     */   }
/*     */ 
/*     */   protected abstract QName getElementName();
/*     */ 
/*     */   private void validateCandidateElement(Element e)
/*     */     throws WSDDException
/*     */   {
/*  68 */     QName name = getElementName();
/*     */ 
/*  70 */     if ((null == e) || (null == e.getNamespaceURI()) || (null == e.getLocalName()) || (!e.getNamespaceURI().equals(name.getNamespaceURI())) || (!e.getLocalName().equals(name.getLocalPart())))
/*     */     {
/*  74 */       throw new WSDDException(Messages.getMessage("invalidWSDD00", e.getLocalName(), name.getLocalPart()));
/*     */     }
/*     */   }
/*     */ 
/*     */   public Element getChildElement(Element e, String name)
/*     */   {
/*  82 */     Element[] elements = getChildElements(e, name);
/*  83 */     if (elements.length == 0)
/*  84 */       return null;
/*  85 */     return elements[0];
/*     */   }
/*     */ 
/*     */   public Element[] getChildElements(Element e, String name)
/*     */   {
/*  90 */     NodeList nl = e.getChildNodes();
/*  91 */     Vector els = new Vector();
/*     */ 
/*  93 */     for (int i = 0; i < nl.getLength(); i++) {
/*  94 */       Node thisNode = nl.item(i);
/*  95 */       if (!(thisNode instanceof Element)) {
/*     */         continue;
/*     */       }
/*  98 */       Element el = (Element)thisNode;
/*  99 */       if (el.getLocalName().equals(name)) {
/* 100 */         els.add(el);
/*     */       }
/*     */     }
/*     */ 
/* 104 */     Element[] elements = new Element[els.size()];
/* 105 */     els.toArray(elements);
/*     */ 
/* 107 */     return elements;
/*     */   }
/*     */ 
/*     */   public abstract void writeToContext(SerializationContext paramSerializationContext)
/*     */     throws IOException;
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.WSDDElement
 * JD-Core Version:    0.6.0
 */