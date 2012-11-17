/*     */ package org.apache.axis.message;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.Vector;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.apache.axis.InternalException;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.w3c.dom.Attr;
/*     */ import org.w3c.dom.DOMException;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ public class NamedNodeMapImpl
/*     */   implements NamedNodeMap
/*     */ {
/*     */   protected Vector nodes;
/*  39 */   private static Document doc = null;
/*     */ 
/*     */   public NamedNodeMapImpl()
/*     */   {
/*  50 */     this.nodes = new Vector();
/*     */   }
/*     */ 
/*     */   public Node getNamedItem(String name)
/*     */   {
/*  60 */     if (name == null) {
/*  61 */       Thread.dumpStack();
/*  62 */       throw new IllegalArgumentException("local name = null");
/*     */     }
/*     */ 
/*  65 */     for (Iterator iter = this.nodes.iterator(); iter.hasNext(); ) {
/*  66 */       Attr attr = (Attr)iter.next();
/*  67 */       if (name.equals(attr.getName())) {
/*  68 */         return attr;
/*     */       }
/*     */     }
/*  71 */     return null;
/*     */   }
/*     */ 
/*     */   public Node setNamedItem(Node arg)
/*     */     throws DOMException
/*     */   {
/* 105 */     String name = arg.getNodeName();
/*     */ 
/* 107 */     if (name == null) {
/* 108 */       Thread.dumpStack();
/* 109 */       throw new IllegalArgumentException("local name = null");
/*     */     }
/*     */ 
/* 112 */     for (int i = 0; i < this.nodes.size(); i++) {
/* 113 */       Attr attr = (Attr)this.nodes.get(i);
/*     */ 
/* 115 */       if (name.equals(attr.getName())) {
/* 116 */         this.nodes.remove(i);
/* 117 */         this.nodes.add(i, arg);
/* 118 */         return attr;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 123 */     this.nodes.add(arg);
/* 124 */     return null;
/*     */   }
/*     */ 
/*     */   public Node removeNamedItem(String name)
/*     */     throws DOMException
/*     */   {
/* 143 */     if (name == null) {
/* 144 */       Thread.dumpStack();
/* 145 */       throw new IllegalArgumentException("local name = null");
/*     */     }
/* 147 */     for (int i = 0; i < this.nodes.size(); i++) {
/* 148 */       Attr attr = (Attr)this.nodes.get(i);
/*     */ 
/* 150 */       if (name.equals(attr.getLocalName())) {
/* 151 */         this.nodes.remove(i);
/* 152 */         return attr;
/*     */       }
/*     */     }
/* 155 */     return null;
/*     */   }
/*     */ 
/*     */   public Node item(int index)
/*     */   {
/* 167 */     return (this.nodes != null) && (index < this.nodes.size()) ? (Node)this.nodes.elementAt(index) : null;
/*     */   }
/*     */ 
/*     */   public int getLength()
/*     */   {
/* 176 */     return this.nodes != null ? this.nodes.size() : 0;
/*     */   }
/*     */ 
/*     */   public Node getNamedItemNS(String namespaceURI, String localName)
/*     */   {
/* 194 */     if (namespaceURI == null) namespaceURI = "";
/* 195 */     if (localName == null) {
/* 196 */       Thread.dumpStack();
/* 197 */       throw new IllegalArgumentException("local name = null");
/*     */     }
/*     */ 
/* 200 */     for (Iterator iter = this.nodes.iterator(); iter.hasNext(); ) {
/* 201 */       Attr attr = (Attr)iter.next();
/* 202 */       if ((namespaceURI.equals(attr.getNamespaceURI())) && (localName.equals(attr.getLocalName())))
/*     */       {
/* 204 */         return attr;
/*     */       }
/*     */     }
/* 207 */     return null;
/*     */   }
/*     */ 
/*     */   public Node setNamedItemNS(Node arg)
/*     */     throws DOMException
/*     */   {
/* 241 */     String namespaceURI = arg.getNamespaceURI();
/* 242 */     String localName = arg.getLocalName();
/*     */ 
/* 244 */     if (namespaceURI == null) namespaceURI = "";
/* 245 */     if (localName == null) {
/* 246 */       Thread.dumpStack();
/* 247 */       throw new IllegalArgumentException("local name = null");
/*     */     }
/*     */ 
/* 250 */     for (int i = 0; i < this.nodes.size(); i++) {
/* 251 */       Attr attr = (Attr)this.nodes.get(i);
/*     */ 
/* 253 */       if ((!namespaceURI.equals(attr.getNamespaceURI())) || (!namespaceURI.equals(attr.getLocalName())))
/*     */         continue;
/* 255 */       this.nodes.remove(i);
/* 256 */       this.nodes.add(i, arg);
/* 257 */       return attr;
/*     */     }
/*     */ 
/* 262 */     this.nodes.add(arg);
/* 263 */     return null;
/*     */   }
/*     */ 
/*     */   public Node removeNamedItemNS(String namespaceURI, String localName)
/*     */     throws DOMException
/*     */   {
/* 289 */     if (namespaceURI == null) namespaceURI = "";
/* 290 */     if (localName == null) {
/* 291 */       Thread.dumpStack();
/* 292 */       throw new IllegalArgumentException("local name = null");
/*     */     }
/*     */ 
/* 295 */     for (int i = 0; i < this.nodes.size(); i++) {
/* 296 */       Attr attr = (Attr)this.nodes.get(i);
/*     */ 
/* 298 */       if ((!namespaceURI.equals(attr.getNamespaceURI())) || (!localName.equals(attr.getLocalName())))
/*     */         continue;
/* 300 */       this.nodes.remove(i);
/* 301 */       return attr;
/*     */     }
/*     */ 
/* 304 */     return null;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  42 */       doc = XMLUtils.newDocument();
/*     */     }
/*     */     catch (ParserConfigurationException e)
/*     */     {
/*     */       Document doc;
/*  44 */       throw new InternalException(e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.NamedNodeMapImpl
 * JD-Core Version:    0.6.0
 */