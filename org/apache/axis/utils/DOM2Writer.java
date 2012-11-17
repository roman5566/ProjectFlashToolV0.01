/*     */ package org.apache.axis.utils;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.io.Writer;
/*     */ import org.w3c.dom.Attr;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ 
/*     */ public class DOM2Writer
/*     */ {
/*     */   public static String nodeToString(Node node, boolean omitXMLDecl)
/*     */   {
/*  47 */     StringWriter sw = new StringWriter();
/*     */ 
/*  49 */     serializeAsXML(node, sw, omitXMLDecl);
/*     */ 
/*  51 */     return sw.toString();
/*     */   }
/*     */ 
/*     */   public static void serializeAsXML(Node node, Writer writer, boolean omitXMLDecl)
/*     */   {
/*  60 */     serializeAsXML(node, writer, omitXMLDecl, false);
/*     */   }
/*     */ 
/*     */   public static void serializeAsXML(Node node, Writer writer, boolean omitXMLDecl, boolean pretty)
/*     */   {
/*  70 */     PrintWriter out = new PrintWriter(writer);
/*  71 */     if (!omitXMLDecl) {
/*  72 */       out.print("<?xml version=\"1.0\" encoding=\"");
/*  73 */       out.print(XMLUtils.getEncoding());
/*  74 */       out.println("\"?>");
/*     */     }
/*  76 */     NSStack namespaceStack = new NSStack();
/*  77 */     print(node, namespaceStack, node, out, pretty, 0);
/*  78 */     out.flush();
/*     */   }
/*     */ 
/*     */   private static void print(Node node, NSStack namespaceStack, Node startnode, PrintWriter out, boolean pretty, int indent)
/*     */   {
/*  86 */     if (node == null)
/*     */     {
/*  88 */       return;
/*     */     }
/*     */ 
/*  91 */     boolean hasChildren = false;
/*  92 */     int type = node.getNodeType();
/*     */ 
/*  94 */     switch (type)
/*     */     {
/*     */     case 9:
/*  98 */       NodeList children = node.getChildNodes();
/*     */ 
/* 100 */       if (children == null)
/*     */         break;
/* 102 */       int numChildren = children.getLength();
/*     */ 
/* 104 */       for (int i = 0; i < numChildren; i++)
/*     */       {
/* 106 */         print(children.item(i), namespaceStack, startnode, out, pretty, indent);
/*     */       }
/* 104 */       break;
/*     */     case 1:
/* 115 */       namespaceStack.push();
/*     */ 
/* 117 */       if (pretty) {
/* 118 */         for (int i = 0; i < indent; i++) {
/* 119 */           out.print(' ');
/*     */         }
/*     */       }
/* 122 */       out.print('<' + node.getNodeName());
/*     */ 
/* 124 */       String elPrefix = node.getPrefix();
/* 125 */       String elNamespaceURI = node.getNamespaceURI();
/*     */ 
/* 127 */       if ((elPrefix != null) && (elNamespaceURI != null) && (elPrefix.length() > 0))
/*     */       {
/* 131 */         boolean prefixIsDeclared = false;
/*     */         try
/*     */         {
/* 135 */           String namespaceURI = namespaceStack.getNamespaceURI(elPrefix);
/*     */ 
/* 137 */           if (elNamespaceURI.equals(namespaceURI))
/*     */           {
/* 139 */             prefixIsDeclared = true;
/*     */           }
/*     */         }
/*     */         catch (IllegalArgumentException e)
/*     */         {
/*     */         }
/*     */ 
/* 146 */         if (!prefixIsDeclared)
/*     */         {
/* 148 */           printNamespaceDecl(node, namespaceStack, startnode, out);
/*     */         }
/*     */       }
/*     */ 
/* 152 */       NamedNodeMap attrs = node.getAttributes();
/* 153 */       int len = attrs != null ? attrs.getLength() : 0;
/*     */ 
/* 155 */       for (int i = 0; i < len; i++)
/*     */       {
/* 157 */         Attr attr = (Attr)attrs.item(i);
/*     */ 
/* 159 */         out.print(' ' + attr.getNodeName() + "=\"" + normalize(attr.getValue()) + '"');
/*     */ 
/* 162 */         String attrPrefix = attr.getPrefix();
/* 163 */         String attrNamespaceURI = attr.getNamespaceURI();
/*     */ 
/* 165 */         if ((attrPrefix == null) || (attrNamespaceURI == null) || (attrPrefix.length() <= 0))
/*     */         {
/*     */           continue;
/*     */         }
/* 169 */         boolean prefixIsDeclared = false;
/*     */         try
/*     */         {
/* 173 */           String namespaceURI = namespaceStack.getNamespaceURI(attrPrefix);
/*     */ 
/* 175 */           if (attrNamespaceURI.equals(namespaceURI))
/*     */           {
/* 177 */             prefixIsDeclared = true;
/*     */           }
/*     */         }
/*     */         catch (IllegalArgumentException e)
/*     */         {
/*     */         }
/*     */ 
/* 184 */         if (prefixIsDeclared)
/*     */           continue;
/* 186 */         printNamespaceDecl(attr, namespaceStack, startnode, out);
/*     */       }
/*     */ 
/* 191 */       NodeList children = node.getChildNodes();
/*     */ 
/* 193 */       if (children != null)
/*     */       {
/* 195 */         int numChildren = children.getLength();
/*     */ 
/* 197 */         hasChildren = numChildren > 0;
/*     */ 
/* 199 */         if (hasChildren)
/*     */         {
/* 201 */           out.print('>');
/* 202 */           if (pretty) {
/* 203 */             out.print(JavaUtils.LS);
/*     */           }
/*     */         }
/* 206 */         for (int i = 0; i < numChildren; i++)
/*     */         {
/* 208 */           print(children.item(i), namespaceStack, startnode, out, pretty, indent + 1);
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 214 */         hasChildren = false;
/*     */       }
/*     */ 
/* 217 */       if (!hasChildren)
/*     */       {
/* 219 */         out.print("/>");
/* 220 */         if (pretty) {
/* 221 */           out.print(JavaUtils.LS);
/*     */         }
/*     */       }
/* 224 */       namespaceStack.pop();
/* 225 */       break;
/*     */     case 5:
/* 230 */       out.print('&');
/* 231 */       out.print(node.getNodeName());
/* 232 */       out.print(';');
/* 233 */       break;
/*     */     case 4:
/* 238 */       out.print("<![CDATA[");
/* 239 */       out.print(node.getNodeValue());
/* 240 */       out.print("]]>");
/* 241 */       break;
/*     */     case 3:
/* 246 */       out.print(normalize(node.getNodeValue()));
/* 247 */       break;
/*     */     case 8:
/* 252 */       out.print("<!--");
/* 253 */       out.print(node.getNodeValue());
/* 254 */       out.print("-->");
/* 255 */       if (!pretty) break;
/* 256 */       out.print(JavaUtils.LS); break;
/*     */     case 7:
/* 262 */       out.print("<?");
/* 263 */       out.print(node.getNodeName());
/*     */ 
/* 265 */       String data = node.getNodeValue();
/*     */ 
/* 267 */       if ((data != null) && (data.length() > 0))
/*     */       {
/* 269 */         out.print(' ');
/* 270 */         out.print(data);
/*     */       }
/*     */ 
/* 273 */       out.println("?>");
/* 274 */       if (!pretty) break;
/* 275 */       out.print(JavaUtils.LS); break;
/*     */     case 2:
/*     */     case 6:
/*     */     }
/*     */ 
/* 280 */     if ((type == 1) && (hasChildren == true))
/*     */     {
/* 282 */       if (pretty) {
/* 283 */         for (int i = 0; i < indent; i++)
/* 284 */           out.print(' ');
/*     */       }
/* 286 */       out.print("</");
/* 287 */       out.print(node.getNodeName());
/* 288 */       out.print('>');
/* 289 */       if (pretty)
/* 290 */         out.print(JavaUtils.LS);
/* 291 */       hasChildren = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void printNamespaceDecl(Node node, NSStack namespaceStack, Node startnode, PrintWriter out)
/*     */   {
/* 300 */     switch (node.getNodeType())
/*     */     {
/*     */     case 2:
/* 304 */       printNamespaceDecl(((Attr)node).getOwnerElement(), node, namespaceStack, startnode, out);
/*     */ 
/* 306 */       break;
/*     */     case 1:
/* 311 */       printNamespaceDecl((Element)node, node, namespaceStack, startnode, out);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void printNamespaceDecl(Element owner, Node node, NSStack namespaceStack, Node startnode, PrintWriter out)
/*     */   {
/* 322 */     String namespaceURI = node.getNamespaceURI();
/* 323 */     String prefix = node.getPrefix();
/*     */ 
/* 325 */     if (((!namespaceURI.equals("http://www.w3.org/2000/xmlns/")) || (!prefix.equals("xmlns"))) && ((!namespaceURI.equals("http://www.w3.org/XML/1998/namespace")) || (!prefix.equals("xml"))))
/*     */     {
/* 328 */       if (XMLUtils.getNamespace(prefix, owner, startnode) == null)
/*     */       {
/* 330 */         out.print(" xmlns:" + prefix + "=\"" + namespaceURI + '"');
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 335 */       prefix = node.getLocalName();
/* 336 */       namespaceURI = node.getNodeValue();
/*     */     }
/*     */ 
/* 339 */     namespaceStack.add(namespaceURI, prefix);
/*     */   }
/*     */ 
/*     */   public static String normalize(String s)
/*     */   {
/* 344 */     return XMLUtils.xmlEncodeString(s);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.DOM2Writer
 * JD-Core Version:    0.6.0
 */