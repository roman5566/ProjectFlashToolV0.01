/*     */ package org.apache.axis.message;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import javax.xml.soap.SOAPElement;
/*     */ import javax.xml.soap.SOAPException;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.i18n.Messages;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Attr;
/*     */ import org.w3c.dom.CDATASection;
/*     */ import org.w3c.dom.CharacterData;
/*     */ import org.w3c.dom.Comment;
/*     */ import org.w3c.dom.DOMException;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.helpers.AttributesImpl;
/*     */ 
/*     */ public class NodeImpl
/*     */   implements org.w3c.dom.Node, javax.xml.soap.Node, Serializable, Cloneable
/*     */ {
/*  48 */   protected static Log log = LogFactory.getLog(NodeImpl.class.getName());
/*     */   protected String name;
/*     */   protected String prefix;
/*     */   protected String namespaceURI;
/*  54 */   protected transient Attributes attributes = NullAttributes.singleton;
/*     */ 
/*  56 */   protected Document document = null;
/*  57 */   protected NodeImpl parent = null;
/*  58 */   protected ArrayList children = null;
/*     */ 
/*  61 */   protected CharacterData textRep = null;
/*     */ 
/*  63 */   protected boolean _isDirty = false;
/*     */   private static final String NULL_URI_NAME = "intentionalNullURI";
/*     */ 
/*     */   public NodeImpl()
/*     */   {
/*     */   }
/*     */ 
/*     */   public NodeImpl(CharacterData text)
/*     */   {
/*  77 */     this.textRep = text;
/*  78 */     this.namespaceURI = text.getNamespaceURI();
/*  79 */     this.name = text.getLocalName();
/*     */   }
/*     */ 
/*     */   public short getNodeType()
/*     */   {
/*  86 */     if (this.textRep != null) {
/*  87 */       if ((this.textRep instanceof Comment))
/*  88 */         return 8;
/*  89 */       if ((this.textRep instanceof CDATASection)) {
/*  90 */         return 4;
/*     */       }
/*  92 */       return 3;
/*     */     }
/*     */ 
/*  99 */     return 1;
/*     */   }
/*     */ 
/*     */   public void normalize()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean hasAttributes()
/*     */   {
/* 130 */     return this.attributes.getLength() > 0;
/*     */   }
/*     */ 
/*     */   public boolean hasChildNodes()
/*     */   {
/* 140 */     return (this.children != null) && (!this.children.isEmpty());
/*     */   }
/*     */ 
/*     */   public String getLocalName()
/*     */   {
/* 153 */     return this.name;
/*     */   }
/*     */ 
/*     */   public String getNamespaceURI()
/*     */   {
/* 173 */     return this.namespaceURI;
/*     */   }
/*     */ 
/*     */   public String getNodeName()
/*     */   {
/* 180 */     return (this.prefix != null) && (this.prefix.length() > 0) ? this.prefix + ":" + this.name : this.name;
/*     */   }
/*     */ 
/*     */   public String getNodeValue()
/*     */     throws DOMException
/*     */   {
/* 194 */     if (this.textRep == null) {
/* 195 */       return null;
/*     */     }
/* 197 */     return this.textRep.getData();
/*     */   }
/*     */ 
/*     */   public String getPrefix()
/*     */   {
/* 233 */     return this.prefix;
/*     */   }
/*     */ 
/*     */   public void setNodeValue(String nodeValue)
/*     */     throws DOMException
/*     */   {
/* 246 */     throw new DOMException(6, "Cannot use TextNode.set in " + this);
/*     */   }
/*     */ 
/*     */   public void setPrefix(String prefix)
/*     */   {
/* 282 */     this.prefix = prefix;
/*     */   }
/*     */ 
/*     */   public void setOwnerDocument(Document doc)
/*     */   {
/* 291 */     this.document = doc;
/*     */   }
/*     */ 
/*     */   public Document getOwnerDocument()
/*     */   {
/* 302 */     if (this.document == null) {
/* 303 */       NodeImpl node = getParent();
/* 304 */       if (node != null) {
/* 305 */         return node.getOwnerDocument();
/*     */       }
/*     */     }
/* 308 */     return this.document;
/*     */   }
/*     */ 
/*     */   public NamedNodeMap getAttributes()
/*     */   {
/* 317 */     makeAttributesEditable();
/* 318 */     return convertAttrSAXtoDOM(this.attributes);
/*     */   }
/*     */ 
/*     */   public org.w3c.dom.Node getFirstChild()
/*     */   {
/* 326 */     if ((this.children != null) && (!this.children.isEmpty())) {
/* 327 */       return (Cloneable)this.children.get(0);
/*     */     }
/* 329 */     return null;
/*     */   }
/*     */ 
/*     */   public org.w3c.dom.Node getLastChild()
/*     */   {
/* 338 */     if ((this.children != null) && (!this.children.isEmpty())) {
/* 339 */       return (Cloneable)this.children.get(this.children.size() - 1);
/*     */     }
/* 341 */     return null;
/*     */   }
/*     */ 
/*     */   public org.w3c.dom.Node getNextSibling()
/*     */   {
/* 350 */     SOAPElement parent = getParentElement();
/* 351 */     if (parent == null) {
/* 352 */       return null;
/*     */     }
/* 354 */     Iterator iter = parent.getChildElements();
/* 355 */     org.w3c.dom.Node nextSibling = null;
/* 356 */     while (iter.hasNext()) {
/* 357 */       if (iter.next() == this) {
/* 358 */         if (iter.hasNext()) {
/* 359 */           return (Cloneable)iter.next();
/*     */         }
/* 361 */         return null;
/*     */       }
/*     */     }
/*     */ 
/* 365 */     return nextSibling;
/*     */   }
/*     */ 
/*     */   public org.w3c.dom.Node getParentNode()
/*     */   {
/* 377 */     return getParent();
/*     */   }
/*     */ 
/*     */   public org.w3c.dom.Node getPreviousSibling()
/*     */   {
/* 385 */     SOAPElement parent = getParentElement();
/* 386 */     if (parent == null) {
/* 387 */       return null;
/*     */     }
/* 389 */     NodeList nl = parent.getChildNodes();
/* 390 */     int len = nl.getLength();
/* 391 */     int i = 0;
/* 392 */     org.w3c.dom.Node previousSibling = null;
/* 393 */     while (i < len) {
/* 394 */       if (nl.item(i) == this) {
/* 395 */         return previousSibling;
/*     */       }
/* 397 */       previousSibling = nl.item(i);
/* 398 */       i++;
/*     */     }
/* 400 */     return previousSibling;
/*     */   }
/*     */ 
/*     */   public org.w3c.dom.Node cloneNode(boolean deep)
/*     */   {
/* 429 */     return new NodeImpl(this.textRep);
/*     */   }
/*     */ 
/*     */   public NodeList getChildNodes()
/*     */   {
/* 438 */     if (this.children == null) {
/* 439 */       return NodeListImpl.EMPTY_NODELIST;
/*     */     }
/* 441 */     return new NodeListImpl(this.children);
/*     */   }
/*     */ 
/*     */   public boolean isSupported(String feature, String version)
/*     */   {
/* 461 */     return false;
/*     */   }
/*     */ 
/*     */   public org.w3c.dom.Node appendChild(org.w3c.dom.Node newChild)
/*     */     throws DOMException
/*     */   {
/* 484 */     if (newChild == null) {
/* 485 */       throw new DOMException(3, "Can't append a null node.");
/*     */     }
/*     */ 
/* 489 */     initializeChildren();
/*     */ 
/* 493 */     ((NodeImpl)newChild).detachNode();
/* 494 */     this.children.add(newChild);
/* 495 */     ((NodeImpl)newChild).parent = this;
/* 496 */     setDirty();
/* 497 */     return newChild;
/*     */   }
/*     */ 
/*     */   public org.w3c.dom.Node removeChild(org.w3c.dom.Node oldChild)
/*     */     throws DOMException
/*     */   {
/* 511 */     if (removeNodeFromChildList((NodeImpl)oldChild)) {
/* 512 */       setDirty();
/* 513 */       return oldChild;
/*     */     }
/* 515 */     throw new DOMException(8, "NodeImpl Not found");
/*     */   }
/*     */ 
/*     */   private boolean removeNodeFromChildList(NodeImpl n)
/*     */   {
/* 520 */     boolean removed = false;
/* 521 */     initializeChildren();
/* 522 */     Iterator itr = this.children.iterator();
/* 523 */     while (itr.hasNext()) {
/* 524 */       NodeImpl node = (NodeImpl)itr.next();
/* 525 */       if (node == n) {
/* 526 */         removed = true;
/* 527 */         itr.remove();
/*     */       }
/*     */     }
/* 530 */     return removed;
/*     */   }
/*     */ 
/*     */   public org.w3c.dom.Node insertBefore(org.w3c.dom.Node newChild, org.w3c.dom.Node refChild)
/*     */     throws DOMException
/*     */   {
/* 558 */     initializeChildren();
/* 559 */     int position = this.children.indexOf(refChild);
/* 560 */     if (position < 0) {
/* 561 */       position = 0;
/*     */     }
/* 563 */     this.children.add(position, newChild);
/* 564 */     setDirty();
/* 565 */     return newChild;
/*     */   }
/*     */ 
/*     */   public org.w3c.dom.Node replaceChild(org.w3c.dom.Node newChild, org.w3c.dom.Node oldChild)
/*     */     throws DOMException
/*     */   {
/* 592 */     initializeChildren();
/* 593 */     int position = this.children.indexOf(oldChild);
/* 594 */     if (position < 0) {
/* 595 */       throw new DOMException(8, "NodeImpl Not found");
/*     */     }
/*     */ 
/* 598 */     this.children.remove(position);
/* 599 */     this.children.add(position, newChild);
/* 600 */     setDirty();
/* 601 */     return oldChild;
/*     */   }
/*     */ 
/*     */   public String getValue()
/*     */   {
/* 614 */     return this.textRep.getNodeValue();
/*     */   }
/*     */ 
/*     */   public void setParentElement(SOAPElement parent)
/*     */     throws SOAPException
/*     */   {
/* 628 */     if (parent == null)
/* 629 */       throw new IllegalArgumentException(Messages.getMessage("nullParent00"));
/*     */     try
/*     */     {
/* 632 */       setParent((NodeImpl)parent);
/*     */     } catch (Throwable t) {
/* 634 */       throw new SOAPException(t);
/*     */     }
/*     */   }
/*     */ 
/*     */   public SOAPElement getParentElement()
/*     */   {
/* 650 */     return (SOAPElement)getParent();
/*     */   }
/*     */ 
/*     */   public void detachNode()
/*     */   {
/* 659 */     setDirty();
/* 660 */     if (this.parent != null) {
/* 661 */       this.parent.removeChild(this);
/* 662 */       this.parent = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void recycleNode()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setValue(String value)
/*     */   {
/* 692 */     if ((this instanceof Text)) {
/* 693 */       setNodeValue(value);
/* 694 */     } else if (this.children != null) {
/* 695 */       if (this.children.size() != 1) {
/* 696 */         throw new IllegalStateException("setValue() may not be called on a non-Text node with more than one child.");
/*     */       }
/* 698 */       javax.xml.soap.Node child = (javax.xml.soap.Node)this.children.get(0);
/* 699 */       if (!(child instanceof Text)) {
/* 700 */         throw new IllegalStateException("setValue() may not be called on a non-Text node with a non-Text child.");
/*     */       }
/* 702 */       ((javax.xml.soap.Text)child).setNodeValue(value);
/*     */     } else {
/* 704 */       appendChild(new Text(value));
/*     */     }
/*     */   }
/*     */ 
/*     */   protected AttributesImpl makeAttributesEditable()
/*     */   {
/* 714 */     if ((this.attributes == null) || ((this.attributes instanceof NullAttributes)))
/* 715 */       this.attributes = new AttributesImpl();
/* 716 */     else if (!(this.attributes instanceof AttributesImpl)) {
/* 717 */       this.attributes = new AttributesImpl(this.attributes);
/*     */     }
/* 719 */     return (AttributesImpl)this.attributes;
/*     */   }
/*     */ 
/*     */   protected NamedNodeMap convertAttrSAXtoDOM(Attributes saxAttr)
/*     */   {
/*     */     try
/*     */     {
/* 731 */       Document doc = XMLUtils.newDocument();
/* 732 */       AttributesImpl saxAttrs = (AttributesImpl)saxAttr;
/* 733 */       NamedNodeMap domAttributes = new NamedNodeMapImpl();
/* 734 */       for (int i = 0; i < saxAttrs.getLength(); i++) {
/* 735 */         String uri = saxAttrs.getURI(i);
/* 736 */         String qname = saxAttrs.getQName(i);
/* 737 */         String value = saxAttrs.getValue(i);
/* 738 */         if ((uri != null) && (uri.trim().length() > 0))
/*     */         {
/* 741 */           if ("intentionalNullURI".equals(uri)) {
/* 742 */             uri = null;
/*     */           }
/* 744 */           Attr attr = doc.createAttributeNS(uri, qname);
/* 745 */           attr.setValue(value);
/* 746 */           domAttributes.setNamedItemNS(attr);
/*     */         } else {
/* 748 */           Attr attr = doc.createAttribute(qname);
/* 749 */           attr.setValue(value);
/* 750 */           domAttributes.setNamedItem(attr);
/*     */         }
/*     */       }
/* 753 */       return domAttributes;
/*     */     } catch (Exception ex) {
/* 755 */       log.error(Messages.getMessage("saxToDomFailed00"), ex);
/*     */     }
/* 757 */     return null;
/*     */   }
/*     */ 
/*     */   protected void initializeChildren()
/*     */   {
/* 765 */     if (this.children == null)
/* 766 */       this.children = new ArrayList();
/*     */   }
/*     */ 
/*     */   protected NodeImpl getParent()
/*     */   {
/* 775 */     return this.parent;
/*     */   }
/*     */ 
/*     */   protected void setParent(NodeImpl parent)
/*     */     throws SOAPException
/*     */   {
/* 785 */     if (this.parent == parent) {
/* 786 */       return;
/*     */     }
/* 788 */     if (this.parent != null) {
/* 789 */       this.parent.removeChild(this);
/*     */     }
/* 791 */     if (parent != null) {
/* 792 */       parent.appendChild(this);
/*     */     }
/* 794 */     setDirty();
/* 795 */     this.parent = parent;
/*     */   }
/*     */ 
/*     */   public void output(SerializationContext context)
/*     */     throws Exception
/*     */   {
/* 804 */     if (this.textRep == null)
/* 805 */       return;
/* 806 */     boolean oldPretty = context.getPretty();
/* 807 */     context.setPretty(false);
/* 808 */     if ((this.textRep instanceof CDATASection)) {
/* 809 */       context.writeString("<![CDATA[");
/* 810 */       context.writeString(((org.w3c.dom.Text)this.textRep).getData());
/* 811 */       context.writeString("]]>");
/* 812 */     } else if ((this.textRep instanceof Comment)) {
/* 813 */       context.writeString("<!--");
/* 814 */       context.writeString(this.textRep.getData());
/* 815 */       context.writeString("-->");
/* 816 */     } else if ((this.textRep instanceof org.w3c.dom.Text)) {
/* 817 */       context.writeSafeString(((org.w3c.dom.Text)this.textRep).getData());
/*     */     }
/* 819 */     context.setPretty(oldPretty);
/*     */   }
/*     */ 
/*     */   public boolean isDirty()
/*     */   {
/* 827 */     return this._isDirty;
/*     */   }
/*     */ 
/*     */   public void setDirty(boolean dirty)
/*     */   {
/* 837 */     this._isDirty = dirty;
/* 838 */     if ((this._isDirty) && (this.parent != null))
/* 839 */       this.parent.setDirty();
/*     */   }
/*     */ 
/*     */   public void setDirty()
/*     */   {
/* 845 */     this._isDirty = true;
/* 846 */     if (this.parent != null)
/* 847 */       this.parent.setDirty();
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/* 853 */     if (this.children != null) {
/* 854 */       for (int i = 0; i < this.children.size(); i++) {
/* 855 */         ((NodeImpl)this.children.get(i)).reset();
/*     */       }
/*     */     }
/* 858 */     this._isDirty = false;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.NodeImpl
 * JD-Core Version:    0.6.0
 */