/*     */ package org.apache.axis.wsdl.symbolTable;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.rpc.holders.BooleanHolder;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ public class Utils
/*     */ {
/*  36 */   static final Map nsmap = new HashMap();
/*     */ 
/*     */   static QName findQName(String namespace, String localName)
/*     */   {
/*  47 */     QName qname = null;
/*     */ 
/*  50 */     Map ln2qn = (Map)nsmap.get(namespace);
/*     */ 
/*  52 */     if (null == ln2qn) {
/*  53 */       ln2qn = new HashMap();
/*     */ 
/*  55 */       nsmap.put(namespace, ln2qn);
/*     */ 
/*  57 */       qname = new QName(namespace, localName);
/*     */ 
/*  59 */       ln2qn.put(localName, qname);
/*     */     } else {
/*  61 */       qname = (QName)ln2qn.get(localName);
/*     */ 
/*  63 */       if (null == qname) {
/*  64 */         qname = new QName(namespace, localName);
/*     */ 
/*  66 */         ln2qn.put(localName, qname);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  73 */     return qname;
/*     */   }
/*     */ 
/*     */   public static String getScopedAttribute(Node node, String attr)
/*     */   {
/*  88 */     if (node == null) {
/*  89 */       return null;
/*     */     }
/*     */ 
/*  92 */     if (node.getAttributes() == null) {
/*  93 */       return getScopedAttribute(node.getParentNode(), attr);
/*     */     }
/*     */ 
/*  96 */     Node attrNode = node.getAttributes().getNamedItem(attr);
/*     */ 
/*  98 */     if (attrNode != null) {
/*  99 */       return attrNode.getNodeValue();
/*     */     }
/* 101 */     return getScopedAttribute(node.getParentNode(), attr);
/*     */   }
/*     */ 
/*     */   public static String getAttribute(Node node, String attr)
/*     */   {
/* 115 */     if ((node == null) || (node.getAttributes() == null)) {
/* 116 */       return null;
/*     */     }
/*     */ 
/* 119 */     Node attrNode = node.getAttributes().getNamedItem(attr);
/*     */ 
/* 121 */     if (attrNode != null) {
/* 122 */       return attrNode.getNodeValue();
/*     */     }
/* 124 */     return null;
/*     */   }
/*     */ 
/*     */   public static Vector getAttributesWithLocalName(Node node, String localName)
/*     */   {
/* 139 */     Vector v = new Vector();
/*     */ 
/* 141 */     if (node == null) {
/* 142 */       return v;
/*     */     }
/*     */ 
/* 145 */     NamedNodeMap map = node.getAttributes();
/*     */ 
/* 147 */     if (map != null) {
/* 148 */       for (int i = 0; i < map.getLength(); i++) {
/* 149 */         Node attrNode = map.item(i);
/*     */ 
/* 151 */         if ((attrNode == null) || (!attrNode.getLocalName().equals(localName)))
/*     */           continue;
/* 153 */         v.add(attrNode);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 158 */     return v;
/*     */   }
/*     */ 
/*     */   public static QName getNodeQName(Node node)
/*     */   {
/* 171 */     if (node == null) {
/* 172 */       return null;
/*     */     }
/*     */ 
/* 175 */     String localName = node.getLocalName();
/*     */ 
/* 177 */     if (localName == null) {
/* 178 */       return null;
/*     */     }
/*     */ 
/* 181 */     String namespace = node.getNamespaceURI();
/*     */ 
/* 183 */     return findQName(namespace, localName);
/*     */   }
/*     */ 
/*     */   public static QName getNodeNameQName(Node node)
/*     */   {
/* 196 */     if (node == null) {
/* 197 */       return null;
/*     */     }
/*     */ 
/* 200 */     String localName = null;
/* 201 */     String namespace = null;
/*     */ 
/* 204 */     localName = getAttribute(node, "name");
/*     */ 
/* 207 */     if (localName == null) {
/* 208 */       QName ref = getTypeQNameFromAttr(node, "ref");
/*     */ 
/* 210 */       if (ref != null) {
/* 211 */         localName = ref.getLocalPart();
/* 212 */         namespace = ref.getNamespaceURI();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 220 */     Node search = node.getParentNode();
/*     */ 
/* 222 */     while (search != null) {
/* 223 */       String ln = search.getLocalName();
/*     */ 
/* 225 */       if (ln.equals("schema")) {
/* 226 */         search = null;
/* 227 */       } else if ((ln.equals("element")) || (ln.equals("attribute")))
/*     */       {
/* 229 */         localName = ">" + getNodeNameQName(search).getLocalPart();
/*     */ 
/* 231 */         search = null;
/* 232 */       } else if ((ln.equals("complexType")) || (ln.equals("simpleType")))
/*     */       {
/* 234 */         localName = getNodeNameQName(search).getLocalPart() + ">" + localName;
/*     */ 
/* 236 */         search = null;
/*     */       } else {
/* 238 */         search = search.getParentNode();
/*     */       }
/*     */     }
/*     */ 
/* 242 */     if (localName == null) {
/* 243 */       return null;
/*     */     }
/*     */ 
/* 247 */     if (namespace == null) {
/* 248 */       namespace = getScopedAttribute(node, "targetNamespace");
/*     */     }
/*     */ 
/* 251 */     return findQName(namespace, localName);
/*     */   }
/*     */ 
/*     */   public static QName getTypeQName(Node node, BooleanHolder forElement, boolean ignoreMaxOccurs)
/*     */   {
/* 282 */     if (node == null) {
/* 283 */       return null;
/*     */     }
/*     */ 
/* 286 */     forElement.value = false;
/*     */ 
/* 290 */     QName qName = getTypeQNameFromAttr(node, "type");
/*     */ 
/* 293 */     if (qName == null) {
/* 294 */       String localName = node.getLocalName();
/*     */ 
/* 298 */       if ((localName != null) && (!localName.equals("attributeGroup")) && (!localName.equals("group")) && (!localName.equals("list")))
/*     */       {
/* 302 */         forElement.value = true;
/*     */       }
/*     */ 
/* 305 */       qName = getTypeQNameFromAttr(node, "ref");
/*     */     }
/*     */ 
/* 309 */     if (qName == null) {
/* 310 */       qName = getTypeQNameFromAttr(node, "itemType");
/*     */     }
/*     */ 
/* 318 */     if ((!ignoreMaxOccurs) && 
/* 319 */       (qName != null)) {
/* 320 */       String maxOccursValue = getAttribute(node, "maxOccurs");
/* 321 */       String minOccursValue = getAttribute(node, "minOccurs");
/* 322 */       String nillableValue = getAttribute(node, "nillable");
/*     */ 
/* 324 */       if (maxOccursValue == null) {
/* 325 */         maxOccursValue = "1";
/*     */       }
/*     */ 
/* 328 */       if (minOccursValue == null) {
/* 329 */         minOccursValue = "1";
/*     */       }
/*     */ 
/* 332 */       if ((!minOccursValue.equals("0")) || (!maxOccursValue.equals("1")))
/*     */       {
/* 337 */         if ((!maxOccursValue.equals("1")) || (!minOccursValue.equals("1")))
/*     */         {
/* 339 */           String localPart = qName.getLocalPart();
/* 340 */           String wrapped = (nillableValue != null) && (nillableValue.equals("true")) ? " wrapped" : "";
/*     */ 
/* 343 */           String range = "[";
/* 344 */           if (!minOccursValue.equals("1")) {
/* 345 */             range = range + minOccursValue;
/*     */           }
/* 347 */           range = range + ",";
/* 348 */           if (!maxOccursValue.equals("1")) {
/* 349 */             range = range + maxOccursValue;
/*     */           }
/* 351 */           range = range + "]";
/* 352 */           localPart = localPart + range + wrapped;
/* 353 */           qName = findQName(qName.getNamespaceURI(), localPart);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 359 */     if (qName == null) {
/* 360 */       forElement.value = true;
/* 361 */       qName = getTypeQNameFromAttr(node, "element");
/*     */     }
/*     */ 
/* 365 */     if (qName == null) {
/* 366 */       forElement.value = false;
/* 367 */       qName = getTypeQNameFromAttr(node, "base");
/*     */     }
/*     */ 
/* 370 */     return qName;
/*     */   }
/*     */ 
/*     */   public static QName[] getMemberTypeQNames(Node node)
/*     */   {
/* 381 */     String attribute = getAttribute(node, "memberTypes");
/*     */ 
/* 383 */     if (attribute == null) {
/* 384 */       return null;
/*     */     }
/*     */ 
/* 387 */     StringTokenizer tokenizer = new StringTokenizer(attribute, " ");
/* 388 */     QName[] memberTypes = new QName[tokenizer.countTokens()];
/*     */ 
/* 390 */     for (int i = 0; tokenizer.hasMoreElements(); i++) {
/* 391 */       String element = (String)tokenizer.nextElement();
/*     */ 
/* 393 */       memberTypes[i] = XMLUtils.getFullQNameFromString(element, node);
/*     */     }
/*     */ 
/* 396 */     return memberTypes;
/*     */   }
/*     */ 
/*     */   private static QName getTypeQNameFromAttr(Node node, String typeAttrName)
/*     */   {
/* 421 */     if (node == null) {
/* 422 */       return null;
/*     */     }
/*     */ 
/* 426 */     String prefixedName = getAttribute(node, typeAttrName);
/*     */ 
/* 431 */     if ((prefixedName == null) && (typeAttrName.equals("type")) && 
/* 432 */       (getAttribute(node, "ref") == null) && (getAttribute(node, "base") == null) && (getAttribute(node, "element") == null))
/*     */     {
/* 437 */       QName anonQName = SchemaUtils.getElementAnonQName(node);
/*     */ 
/* 439 */       if (anonQName == null) {
/* 440 */         anonQName = SchemaUtils.getAttributeAnonQName(node);
/*     */       }
/*     */ 
/* 443 */       if (anonQName != null) {
/* 444 */         return anonQName;
/*     */       }
/*     */ 
/* 448 */       String localName = node.getLocalName();
/*     */ 
/* 450 */       if ((localName != null) && (Constants.isSchemaXSD(node.getNamespaceURI())) && ((localName.equals("element")) || (localName.equals("attribute"))))
/*     */       {
/* 454 */         return Constants.XSD_ANYTYPE;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 460 */     if (prefixedName == null) {
/* 461 */       return null;
/*     */     }
/*     */ 
/* 465 */     QName qName = getQNameFromPrefixedName(node, prefixedName);
/*     */ 
/* 474 */     return qName;
/*     */   }
/*     */ 
/*     */   public static QName getQNameFromPrefixedName(Node node, String prefixedName)
/*     */   {
/* 487 */     String localName = prefixedName.substring(prefixedName.lastIndexOf(":") + 1);
/*     */ 
/* 489 */     String namespace = null;
/*     */ 
/* 492 */     if (prefixedName.length() == localName.length()) {
/* 493 */       namespace = getScopedAttribute(node, "xmlns");
/*     */     }
/*     */     else {
/* 496 */       namespace = getScopedAttribute(node, "xmlns:" + prefixedName.substring(0, prefixedName.lastIndexOf(":")));
/*     */     }
/*     */ 
/* 502 */     return findQName(namespace, localName);
/*     */   }
/*     */ 
/*     */   public static HashSet getDerivedTypes(TypeEntry type, SymbolTable symbolTable)
/*     */   {
/* 516 */     HashSet types = (HashSet)symbolTable.derivedTypes.get(type);
/*     */ 
/* 518 */     if (types != null) {
/* 519 */       return types;
/*     */     }
/*     */ 
/* 522 */     types = new HashSet();
/*     */ 
/* 524 */     symbolTable.derivedTypes.put(type, types);
/*     */ 
/* 526 */     if ((type != null) && (type.getNode() != null)) {
/* 527 */       getDerivedTypes(type, types, symbolTable);
/*     */     }
/* 529 */     else if ((type != null) && (Constants.isSchemaXSD(type.getQName().getNamespaceURI())) && ((type.getQName().getLocalPart().equals("anyType")) || (type.getQName().getLocalPart().equals("any"))))
/*     */     {
/* 534 */       Collection typeValues = symbolTable.getTypeIndex().values();
/* 535 */       types.addAll(typeValues);
/*     */     }
/*     */ 
/* 546 */     return types;
/*     */   }
/*     */ 
/*     */   private static void getDerivedTypes(TypeEntry type, HashSet types, SymbolTable symbolTable)
/*     */   {
/* 560 */     if (types.size() == symbolTable.getTypeEntryCount()) {
/* 561 */       return;
/*     */     }
/*     */ 
/* 565 */     Iterator it = symbolTable.getTypeIndex().values().iterator();
/* 566 */     while (it.hasNext()) {
/* 567 */       Type t = (Type)it.next();
/*     */ 
/* 569 */       if (((t instanceof DefinedType)) && (t.getNode() != null) && (!types.contains(t)) && (((DefinedType)t).getComplexTypeExtensionBase(symbolTable) == type))
/*     */       {
/* 573 */         types.add(t);
/* 574 */         getDerivedTypes(t, types, symbolTable);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static HashSet getNestedTypes(TypeEntry type, SymbolTable symbolTable, boolean derivedFlag)
/*     */   {
/* 595 */     HashSet types = new HashSet();
/*     */ 
/* 597 */     getNestedTypes(type, types, symbolTable, derivedFlag);
/*     */ 
/* 599 */     return types;
/*     */   }
/*     */ 
/*     */   private static void getNestedTypes(TypeEntry type, HashSet types, SymbolTable symbolTable, boolean derivedFlag)
/*     */   {
/* 614 */     if (type == null) {
/* 615 */       return;
/*     */     }
/*     */ 
/* 619 */     if (types.size() == symbolTable.getTypeEntryCount()) {
/* 620 */       return;
/*     */     }
/*     */ 
/* 624 */     if (derivedFlag) {
/* 625 */       HashSet derivedTypes = getDerivedTypes(type, symbolTable);
/* 626 */       Iterator it = derivedTypes.iterator();
/*     */ 
/* 628 */       while (it.hasNext()) {
/* 629 */         TypeEntry derivedType = (TypeEntry)it.next();
/*     */ 
/* 631 */         if (!types.contains(derivedType)) {
/* 632 */           types.add(derivedType);
/* 633 */           getNestedTypes(derivedType, types, symbolTable, derivedFlag);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 640 */     if (type.getNode() == null) {
/* 641 */       return;
/*     */     }
/*     */ 
/* 644 */     Node node = type.getNode();
/*     */ 
/* 647 */     Vector v = SchemaUtils.getContainedElementDeclarations(node, symbolTable);
/*     */ 
/* 650 */     if (v != null) {
/* 651 */       for (int i = 0; i < v.size(); i++) {
/* 652 */         ElementDecl elem = (ElementDecl)v.get(i);
/*     */ 
/* 654 */         if (!types.contains(elem.getType())) {
/* 655 */           types.add(elem.getType());
/* 656 */           getNestedTypes(elem.getType(), types, symbolTable, derivedFlag);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 663 */     v = SchemaUtils.getContainedAttributeTypes(node, symbolTable);
/*     */ 
/* 665 */     if (v != null) {
/* 666 */       for (int i = 0; i < v.size(); i++) {
/* 667 */         ContainedAttribute attr = (ContainedAttribute)v.get(i);
/* 668 */         TypeEntry te = attr.getType();
/* 669 */         if (!types.contains(te)) {
/* 670 */           types.add(te);
/* 671 */           getNestedTypes(te, types, symbolTable, derivedFlag);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 677 */     if ((type.getRefType() != null) && (!types.contains(type.getRefType()))) {
/* 678 */       types.add(type.getRefType());
/* 679 */       getNestedTypes(type.getRefType(), types, symbolTable, derivedFlag);
/*     */     }
/*     */ 
/* 705 */     TypeEntry extendType = SchemaUtils.getComplexElementExtensionBase(node, symbolTable);
/*     */ 
/* 708 */     if ((extendType != null) && 
/* 709 */       (!types.contains(extendType))) {
/* 710 */       types.add(extendType);
/* 711 */       getNestedTypes(extendType, types, symbolTable, derivedFlag);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String genQNameAttributeString(QName qname, String prefix)
/*     */   {
/* 748 */     if ((qname.getNamespaceURI() == null) || (qname.getNamespaceURI().equals("")))
/*     */     {
/* 750 */       return qname.getLocalPart();
/*     */     }
/*     */ 
/* 753 */     return prefix + ":" + qname.getLocalPart() + "\" xmlns:" + prefix + "=\"" + qname.getNamespaceURI();
/*     */   }
/*     */ 
/*     */   public static String genQNameAttributeStringWithLastLocalPart(QName qname, String prefix)
/*     */   {
/* 758 */     String lastLocalPart = getLastLocalPart(qname.getLocalPart());
/* 759 */     if ((qname.getNamespaceURI() == null) || (qname.getNamespaceURI().equals("")))
/*     */     {
/* 761 */       return lastLocalPart;
/*     */     }
/*     */ 
/* 764 */     return prefix + ":" + lastLocalPart + "\" xmlns:" + prefix + "=\"" + qname.getNamespaceURI();
/*     */   }
/*     */ 
/*     */   public static String getLastLocalPart(String localPart)
/*     */   {
/* 769 */     int anonymousDelimitorIndex = localPart.lastIndexOf('>');
/* 770 */     if ((anonymousDelimitorIndex > -1) && (anonymousDelimitorIndex < localPart.length() - 1)) {
/* 771 */       localPart = localPart.substring(anonymousDelimitorIndex + 1);
/*     */     }
/* 773 */     return localPart;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.Utils
 * JD-Core Version:    0.6.0
 */