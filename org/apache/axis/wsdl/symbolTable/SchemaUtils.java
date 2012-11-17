/*      */ package org.apache.axis.wsdl.symbolTable;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashSet;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.Vector;
/*      */ import javax.xml.namespace.QName;
/*      */ import javax.xml.rpc.holders.BooleanHolder;
/*      */ import javax.xml.rpc.holders.IntHolder;
/*      */ import javax.xml.rpc.holders.QNameHolder;
/*      */ import org.apache.axis.Constants;
/*      */ import org.apache.axis.i18n.Messages;
/*      */ import org.apache.axis.utils.JavaUtils;
/*      */ import org.w3c.dom.DOMException;
/*      */ import org.w3c.dom.NamedNodeMap;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.NodeList;
/*      */ 
/*      */ public class SchemaUtils
/*      */ {
/*   47 */   static final QName VALUE_QNAME = Utils.findQName("", "_value");
/*      */ 
/* 1994 */   private static String[] schemaTypes = { "string", "normalizedString", "token", "byte", "unsignedByte", "base64Binary", "hexBinary", "integer", "positiveInteger", "negativeInteger", "nonNegativeInteger", "nonPositiveInteger", "int", "unsignedInt", "long", "unsignedLong", "short", "unsignedShort", "decimal", "float", "double", "boolean", "time", "dateTime", "duration", "date", "gMonth", "gYear", "gYearMonth", "gDay", "gMonthDay", "Name", "QName", "NCName", "anyURI", "language", "ID", "IDREF", "IDREFS", "ENTITY", "ENTITIES", "NOTATION", "NMTOKEN", "NMTOKENS", "anySimpleType" };
/*      */ 
/* 2007 */   private static final Set schemaTypeSet = new HashSet(Arrays.asList(schemaTypes));
/*      */ 
/*      */   public static boolean isMixed(Node node)
/*      */   {
/*   55 */     if (isXSDNode(node, "complexType")) {
/*   56 */       String mixed = ((org.w3c.dom.Element)node).getAttribute("mixed");
/*   57 */       if ((mixed != null) && (mixed.length() > 0)) {
/*   58 */         return ("true".equalsIgnoreCase(mixed)) || ("1".equals(mixed));
/*      */       }
/*      */ 
/*   63 */       NodeList children = node.getChildNodes();
/*      */ 
/*   65 */       for (int j = 0; j < children.getLength(); j++) {
/*   66 */         Node kid = children.item(j);
/*   67 */         if (isXSDNode(kid, "complexContent")) {
/*   68 */           mixed = ((org.w3c.dom.Element)kid).getAttribute("mixed");
/*   69 */           return ("true".equalsIgnoreCase(mixed)) || ("1".equals(mixed));
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*   74 */     return false;
/*      */   }
/*      */ 
/*      */   public static Node getUnionNode(Node node)
/*      */   {
/*   79 */     if (isXSDNode(node, "simpleType"))
/*      */     {
/*   81 */       NodeList children = node.getChildNodes();
/*   82 */       for (int j = 0; j < children.getLength(); j++) {
/*   83 */         Node kid = children.item(j);
/*   84 */         if (isXSDNode(kid, "union")) {
/*   85 */           return kid;
/*      */         }
/*      */       }
/*      */     }
/*   89 */     return null;
/*      */   }
/*      */ 
/*      */   public static Node getListNode(Node node)
/*      */   {
/*   94 */     if (isXSDNode(node, "simpleType"))
/*      */     {
/*   96 */       NodeList children = node.getChildNodes();
/*   97 */       for (int j = 0; j < children.getLength(); j++) {
/*   98 */         Node kid = children.item(j);
/*   99 */         if (isXSDNode(kid, "list")) {
/*  100 */           return kid;
/*      */         }
/*      */       }
/*      */     }
/*  104 */     return null;
/*      */   }
/*      */ 
/*      */   public static boolean isSimpleTypeWithUnion(Node node) {
/*  108 */     return getUnionNode(node) != null;
/*      */   }
/*      */ 
/*      */   public static boolean isWrappedType(Node node)
/*      */   {
/*  123 */     if (node == null) {
/*  124 */       return false;
/*      */     }
/*      */ 
/*  128 */     if (isXSDNode(node, "element")) {
/*  129 */       NodeList children = node.getChildNodes();
/*  130 */       boolean hasComplexType = false;
/*  131 */       for (int j = 0; j < children.getLength(); j++) {
/*  132 */         Node kid = children.item(j);
/*  133 */         if (isXSDNode(kid, "complexType")) {
/*  134 */           node = kid;
/*  135 */           hasComplexType = true;
/*  136 */           break;
/*      */         }
/*      */       }
/*  139 */       if (!hasComplexType) {
/*  140 */         return false;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  145 */     if (isXSDNode(node, "complexType"))
/*      */     {
/*  150 */       NodeList children = node.getChildNodes();
/*      */ 
/*  152 */       for (int j = 0; j < children.getLength(); j++) {
/*  153 */         Node kid = children.item(j);
/*      */ 
/*  155 */         if (isXSDNode(kid, "complexContent"))
/*  156 */           return false;
/*  157 */         if (isXSDNode(kid, "simpleContent")) {
/*  158 */           return false;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  166 */       children = node.getChildNodes();
/*  167 */       int len = children.getLength();
/*  168 */       for (int j = 0; j < len; j++) {
/*  169 */         Node kid = children.item(j);
/*  170 */         String localName = kid.getLocalName();
/*  171 */         if ((localName == null) || (!Constants.isSchemaXSD(kid.getNamespaceURI())))
/*      */           continue;
/*  173 */         if (localName.equals("sequence")) {
/*  174 */           Node sequenceNode = kid;
/*  175 */           NodeList sequenceChildren = sequenceNode.getChildNodes();
/*  176 */           int sequenceLen = sequenceChildren.getLength();
/*  177 */           for (int k = 0; k < sequenceLen; k++) {
/*  178 */             Node sequenceKid = sequenceChildren.item(k);
/*  179 */             String sequenceLocalName = sequenceKid.getLocalName();
/*  180 */             if ((sequenceLocalName == null) || (!Constants.isSchemaXSD(sequenceKid.getNamespaceURI()))) {
/*      */               continue;
/*      */             }
/*  183 */             if (sequenceLocalName.equals("choice")) {
/*  184 */               Node choiceNode = sequenceKid;
/*  185 */               NodeList choiceChildren = choiceNode.getChildNodes();
/*  186 */               int choiceLen = choiceChildren.getLength();
/*  187 */               for (int l = 0; l < choiceLen; l++) {
/*  188 */                 Node choiceKid = choiceChildren.item(l);
/*  189 */                 String choiceLocalName = choiceKid.getLocalName();
/*  190 */                 if ((choiceLocalName == null) || (!Constants.isSchemaXSD(choiceKid.getNamespaceURI())))
/*      */                   continue;
/*  192 */                 if (!choiceLocalName.equals("element")) {
/*  193 */                   return false;
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/*      */             }
/*  199 */             else if (!sequenceLocalName.equals("element")) {
/*  200 */               return false;
/*      */             }
/*      */           }
/*      */ 
/*  204 */           return true;
/*      */         }
/*  206 */         return false;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  212 */     return true;
/*      */   }
/*      */ 
/*      */   public static Vector getContainedElementDeclarations(Node node, SymbolTable symbolTable)
/*      */   {
/*  235 */     if (node == null) {
/*  236 */       return null;
/*      */     }
/*      */ 
/*  240 */     if (isXSDNode(node, "element")) {
/*  241 */       NodeList children = node.getChildNodes();
/*      */ 
/*  243 */       for (int j = 0; j < children.getLength(); j++) {
/*  244 */         Node kid = children.item(j);
/*      */ 
/*  246 */         if (isXSDNode(kid, "complexType")) {
/*  247 */           node = kid;
/*      */ 
/*  249 */           break;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  255 */     if (isXSDNode(node, "complexType"))
/*      */     {
/*  259 */       NodeList children = node.getChildNodes();
/*  260 */       Node complexContent = null;
/*  261 */       Node simpleContent = null;
/*  262 */       Node extension = null;
/*      */ 
/*  264 */       for (int j = 0; j < children.getLength(); j++) {
/*  265 */         Node kid = children.item(j);
/*      */ 
/*  267 */         if (isXSDNode(kid, "complexContent")) {
/*  268 */           complexContent = kid;
/*      */ 
/*  270 */           break;
/*  271 */         }if (isXSDNode(kid, "simpleContent")) {
/*  272 */           simpleContent = kid;
/*      */         }
/*      */       }
/*      */ 
/*  276 */       if (complexContent != null) {
/*  277 */         children = complexContent.getChildNodes();
/*      */ 
/*  279 */         int j = 0;
/*  280 */         while ((j < children.getLength()) && (extension == null))
/*      */         {
/*  282 */           Node kid = children.item(j);
/*      */ 
/*  284 */           if ((isXSDNode(kid, "extension")) || (isXSDNode(kid, "restriction")))
/*      */           {
/*  286 */             extension = kid;
/*      */           }
/*  281 */           j++;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  291 */       if (simpleContent != null) {
/*  292 */         children = simpleContent.getChildNodes();
/*      */ 
/*  294 */         int len = children.getLength();
/*  295 */         int j = 0;
/*  296 */         while ((j < len) && (extension == null))
/*      */         {
/*  298 */           Node kid = children.item(j);
/*  299 */           String localName = kid.getLocalName();
/*      */ 
/*  301 */           if ((localName != null) && ((localName.equals("extension")) || (localName.equals("restriction"))) && (Constants.isSchemaXSD(kid.getNamespaceURI())))
/*      */           {
/*  306 */             QName extendsOrRestrictsTypeName = Utils.getTypeQName(children.item(j), new BooleanHolder(), false);
/*      */ 
/*  310 */             TypeEntry extendsOrRestrictsType = symbolTable.getTypeEntry(extendsOrRestrictsTypeName, false);
/*      */ 
/*  319 */             if ((extendsOrRestrictsType == null) || (extendsOrRestrictsType.isBaseType()))
/*      */             {
/*  324 */               Vector v = new Vector();
/*  325 */               ElementDecl elem = new ElementDecl(extendsOrRestrictsType, VALUE_QNAME);
/*      */ 
/*  328 */               v.add(elem);
/*      */ 
/*  330 */               return v;
/*      */             }
/*      */ 
/*  336 */             return null;
/*      */           }
/*  297 */           j++;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  342 */       if (extension != null) {
/*  343 */         node = extension;
/*      */       }
/*      */ 
/*  348 */       children = node.getChildNodes();
/*      */ 
/*  350 */       Vector v = new Vector();
/*  351 */       int len = children.getLength();
/*  352 */       for (int j = 0; j < len; j++) {
/*  353 */         Node kid = children.item(j);
/*  354 */         String localName = kid.getLocalName();
/*  355 */         if ((localName == null) || (!Constants.isSchemaXSD(kid.getNamespaceURI())))
/*      */           continue;
/*  357 */         if (localName.equals("sequence"))
/*  358 */           v.addAll(processSequenceNode(kid, symbolTable));
/*  359 */         else if (localName.equals("all"))
/*  360 */           v.addAll(processAllNode(kid, symbolTable));
/*  361 */         else if (localName.equals("choice"))
/*  362 */           v.addAll(processChoiceNode(kid, symbolTable));
/*  363 */         else if (localName.equals("group")) {
/*  364 */           v.addAll(processGroupNode(kid, symbolTable));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  369 */       return v;
/*  370 */     }if (isXSDNode(node, "group"))
/*      */     {
/*  396 */       return null;
/*      */     }
/*      */ 
/*  400 */     QName[] simpleQName = getContainedSimpleTypes(node);
/*      */ 
/*  402 */     if (simpleQName != null) {
/*  403 */       Vector v = null;
/*      */ 
/*  405 */       for (int i = 0; i < simpleQName.length; i++)
/*      */       {
/*  407 */         Type simpleType = symbolTable.getType(simpleQName[i]);
/*      */ 
/*  409 */         if (simpleType != null) {
/*  410 */           if (v == null) {
/*  411 */             v = new Vector();
/*      */           }
/*      */ 
/*  414 */           QName qname = null;
/*  415 */           if (simpleQName.length > 1)
/*  416 */             qname = new QName("", simpleQName[i].getLocalPart() + "Value");
/*      */           else {
/*  418 */             qname = new QName("", "value");
/*      */           }
/*      */ 
/*  421 */           v.add(new ElementDecl(simpleType, qname));
/*      */         }
/*      */       }
/*      */ 
/*  425 */       return v;
/*      */     }
/*      */ 
/*  429 */     return null;
/*      */   }
/*      */ 
/*      */   private static Vector processChoiceNode(Node choiceNode, SymbolTable symbolTable)
/*      */   {
/*  443 */     Vector v = new Vector();
/*  444 */     NodeList children = choiceNode.getChildNodes();
/*  445 */     int len = children.getLength();
/*  446 */     for (int j = 0; j < len; j++) {
/*  447 */       Node kid = children.item(j);
/*  448 */       String localName = kid.getLocalName();
/*  449 */       if ((localName == null) || (!Constants.isSchemaXSD(kid.getNamespaceURI())))
/*      */         continue;
/*  451 */       if (localName.equals("choice")) {
/*  452 */         v.addAll(processChoiceNode(kid, symbolTable));
/*  453 */       } else if (localName.equals("sequence")) {
/*  454 */         v.addAll(processSequenceNode(kid, symbolTable));
/*  455 */       } else if (localName.equals("group")) {
/*  456 */         v.addAll(processGroupNode(kid, symbolTable));
/*  457 */       } else if (localName.equals("element")) {
/*  458 */         ElementDecl elem = processChildElementNode(kid, symbolTable);
/*      */ 
/*  461 */         if (elem != null)
/*      */         {
/*  464 */           elem.setMinOccursIs0(true);
/*      */ 
/*  466 */           v.add(elem);
/*      */         }
/*      */       } else {
/*  468 */         if (!localName.equals("any"))
/*      */         {
/*      */           continue;
/*      */         }
/*  472 */         Type type = symbolTable.getType(Constants.XSD_ANY);
/*  473 */         ElementDecl elem = new ElementDecl(type, Utils.findQName("", "any"));
/*      */ 
/*  477 */         elem.setAnyElement(true);
/*  478 */         v.add(elem);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  483 */     return v;
/*      */   }
/*      */ 
/*      */   private static Node getChildByName(Node parentNode, String name)
/*      */     throws DOMException
/*      */   {
/*  493 */     if (parentNode == null) return null;
/*  494 */     NodeList children = parentNode.getChildNodes();
/*  495 */     if (children != null) {
/*  496 */       for (int i = 0; i < children.getLength(); i++) {
/*  497 */         Node child = children.item(i);
/*  498 */         if ((child != null) && (
/*  499 */           ((child.getNodeName() != null) && (name.equals(child.getNodeName()))) || ((child.getLocalName() != null) && (name.equals(child.getLocalName())))))
/*      */         {
/*  501 */           return child;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  506 */     return null;
/*      */   }
/*      */ 
/*      */   public static String getTextByPath(Node root, String path)
/*      */     throws DOMException
/*      */   {
/*  517 */     StringTokenizer st = new StringTokenizer(path, "/");
/*  518 */     Node node = root;
/*  519 */     while (st.hasMoreTokens()) {
/*  520 */       String elementName = st.nextToken();
/*  521 */       Node child = getChildByName(node, elementName);
/*  522 */       if (child == null)
/*  523 */         throw new DOMException(8, "could not find " + elementName);
/*  524 */       node = child;
/*      */     }
/*      */ 
/*  528 */     String text = "";
/*  529 */     NodeList children = node.getChildNodes();
/*  530 */     if (children != null) {
/*  531 */       for (int i = 0; i < children.getLength(); i++) {
/*  532 */         Node child = children.item(i);
/*  533 */         if ((child == null) || 
/*  534 */           (child.getNodeName() == null) || (
/*  534 */           (!child.getNodeName().equals("#text")) && (!child.getNodeName().equals("#cdata-section")))) {
/*      */           continue;
/*      */         }
/*  537 */         text = text + child.getNodeValue();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  542 */     return text;
/*      */   }
/*      */ 
/*      */   public static String getAnnotationDocumentation(Node typeNode)
/*      */   {
/*  553 */     Node annotationNode = typeNode.getFirstChild();
/*  554 */     while ((annotationNode != null) && 
/*  555 */       (!isXSDNode(annotationNode, "annotation")))
/*      */     {
/*  558 */       annotationNode = annotationNode.getNextSibling();
/*      */     }
/*      */ 
/*  561 */     if (annotationNode != null) {
/*  562 */       Node documentationNode = annotationNode.getFirstChild();
/*  563 */       while ((documentationNode != null) && 
/*  564 */         (!isXSDNode(documentationNode, "documentation")))
/*      */       {
/*  567 */         documentationNode = documentationNode.getNextSibling();
/*      */       }
/*      */     }
/*  570 */     Node documentationNode = null;
/*      */ 
/*  574 */     String text = "";
/*  575 */     if (documentationNode != null) {
/*  576 */       NodeList children = documentationNode.getChildNodes();
/*  577 */       if (children != null) {
/*  578 */         for (int i = 0; i < children.getLength(); i++) {
/*  579 */           Node child = children.item(i);
/*  580 */           if ((child == null) || 
/*  581 */             (child.getNodeName() == null) || (
/*  581 */             (!child.getNodeName().equals("#text")) && (!child.getNodeName().equals("#cdata-section")))) {
/*      */             continue;
/*      */           }
/*  584 */           text = text + child.getNodeValue();
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  590 */     return text;
/*      */   }
/*      */ 
/*      */   private static Vector processSequenceNode(Node sequenceNode, SymbolTable symbolTable)
/*      */   {
/*  604 */     Vector v = new Vector();
/*  605 */     NodeList children = sequenceNode.getChildNodes();
/*  606 */     int len = children.getLength();
/*  607 */     for (int j = 0; j < len; j++) {
/*  608 */       Node kid = children.item(j);
/*  609 */       String localName = kid.getLocalName();
/*      */ 
/*  611 */       if ((localName == null) || (!Constants.isSchemaXSD(kid.getNamespaceURI())))
/*      */         continue;
/*  613 */       if (localName.equals("choice")) {
/*  614 */         v.addAll(processChoiceNode(kid, symbolTable));
/*  615 */       } else if (localName.equals("sequence")) {
/*  616 */         v.addAll(processSequenceNode(kid, symbolTable));
/*  617 */       } else if (localName.equals("group")) {
/*  618 */         v.addAll(processGroupNode(kid, symbolTable));
/*  619 */       } else if (localName.equals("any"))
/*      */       {
/*  623 */         Type type = symbolTable.getType(Constants.XSD_ANY);
/*  624 */         ElementDecl elem = new ElementDecl(type, Utils.findQName("", "any"));
/*      */ 
/*  628 */         elem.setAnyElement(true);
/*  629 */         v.add(elem);
/*  630 */       } else if (localName.equals("element")) {
/*  631 */         ElementDecl elem = processChildElementNode(kid, symbolTable);
/*      */ 
/*  634 */         if (elem != null) {
/*  635 */           v.add(elem);
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  641 */     return v;
/*      */   }
/*      */ 
/*      */   private static Vector processGroupNode(Node groupNode, SymbolTable symbolTable)
/*      */   {
/*  656 */     Vector v = new Vector();
/*  657 */     if (groupNode.getAttributes().getNamedItem("ref") == null) {
/*  658 */       NodeList children = groupNode.getChildNodes();
/*  659 */       int len = children.getLength();
/*  660 */       for (int j = 0; j < len; j++) {
/*  661 */         Node kid = children.item(j);
/*  662 */         String localName = kid.getLocalName();
/*  663 */         if ((localName == null) || (!Constants.isSchemaXSD(kid.getNamespaceURI())))
/*      */           continue;
/*  665 */         if (localName.equals("choice"))
/*  666 */           v.addAll(processChoiceNode(kid, symbolTable));
/*  667 */         else if (localName.equals("sequence"))
/*  668 */           v.addAll(processSequenceNode(kid, symbolTable));
/*  669 */         else if (localName.equals("all"))
/*  670 */           v.addAll(processAllNode(kid, symbolTable));
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  675 */       QName nodeName = Utils.getNodeNameQName(groupNode);
/*  676 */       QName nodeType = Utils.getTypeQName(groupNode, new BooleanHolder(), false);
/*      */ 
/*  680 */       Type type = (Type)symbolTable.getTypeEntry(nodeType, false);
/*      */ 
/*  682 */       if ((type != null) && (type.getNode() != null))
/*      */       {
/*  684 */         Node node = type.getNode();
/*  685 */         NodeList children = node.getChildNodes();
/*  686 */         for (int j = 0; j < children.getLength(); j++) {
/*  687 */           QName subNodeKind = Utils.getNodeQName(children.item(j));
/*  688 */           if ((subNodeKind == null) || (!Constants.isSchemaXSD(subNodeKind.getNamespaceURI()))) {
/*      */             continue;
/*      */           }
/*  691 */           if (subNodeKind.getLocalPart().equals("sequence")) {
/*  692 */             v.addAll(processSequenceNode(children.item(j), symbolTable));
/*      */           }
/*  694 */           else if (subNodeKind.getLocalPart().equals("all"))
/*  695 */             v.addAll(processAllNode(children.item(j), symbolTable));
/*  696 */           else if (subNodeKind.getLocalPart().equals("choice")) {
/*  697 */             v.addAll(processChoiceNode(children.item(j), symbolTable));
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  704 */     return v;
/*      */   }
/*      */ 
/*      */   private static Vector processAllNode(Node allNode, SymbolTable symbolTable)
/*      */   {
/*  719 */     Vector v = new Vector();
/*  720 */     NodeList children = allNode.getChildNodes();
/*      */ 
/*  722 */     for (int j = 0; j < children.getLength(); j++) {
/*  723 */       Node kid = children.item(j);
/*      */ 
/*  725 */       if (isXSDNode(kid, "element")) {
/*  726 */         ElementDecl elem = processChildElementNode(kid, symbolTable);
/*      */ 
/*  728 */         if (elem != null) {
/*  729 */           v.add(elem);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  734 */     return v;
/*      */   }
/*      */ 
/*      */   private static ElementDecl processChildElementNode(Node elementNode, SymbolTable symbolTable)
/*      */   {
/*  752 */     QName nodeName = Utils.getNodeNameQName(elementNode);
/*  753 */     BooleanHolder forElement = new BooleanHolder();
/*  754 */     String comments = null;
/*  755 */     comments = getAnnotationDocumentation(elementNode);
/*      */ 
/*  759 */     QName nodeType = Utils.getTypeQName(elementNode, forElement, false);
/*  760 */     TypeEntry type = symbolTable.getTypeEntry(nodeType, forElement.value);
/*      */ 
/*  767 */     if (!forElement.value)
/*      */     {
/*  771 */       String form = Utils.getAttribute(elementNode, "form");
/*      */ 
/*  773 */       if ((form != null) && (form.equals("unqualified")))
/*      */       {
/*  776 */         nodeName = Utils.findQName("", nodeName.getLocalPart());
/*  777 */       } else if (form == null)
/*      */       {
/*  780 */         String def = Utils.getScopedAttribute(elementNode, "elementFormDefault");
/*      */ 
/*  783 */         if ((def == null) || (def.equals("unqualified")))
/*      */         {
/*  786 */           nodeName = Utils.findQName("", nodeName.getLocalPart());
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  791 */     if (type != null) {
/*  792 */       ElementDecl elem = new ElementDecl(type, nodeName);
/*  793 */       elem.setDocumentation(comments);
/*  794 */       String minOccurs = Utils.getAttribute(elementNode, "minOccurs");
/*      */ 
/*  797 */       if ((minOccurs != null) && (minOccurs.equals("0"))) {
/*  798 */         elem.setMinOccursIs0(true);
/*      */       }
/*      */ 
/*  801 */       String maxOccurs = Utils.getAttribute(elementNode, "maxOccurs");
/*  802 */       if (maxOccurs != null) {
/*  803 */         if (maxOccurs.equals("unbounded")) {
/*  804 */           elem.setMaxOccursIsUnbounded(true);
/*      */         }
/*  806 */         else if (maxOccurs.equals("1")) {
/*  807 */           elem.setMaxOccursIsExactlyOne(true);
/*      */         }
/*      */       }
/*      */       else {
/*  811 */         elem.setMaxOccursIsExactlyOne(true);
/*      */       }
/*  813 */       elem.setNillable(JavaUtils.isTrueExplicitly(Utils.getAttribute(elementNode, "nillable")));
/*      */ 
/*  817 */       String useValue = Utils.getAttribute(elementNode, "use");
/*      */ 
/*  819 */       if (useValue != null) {
/*  820 */         elem.setOptional(useValue.equalsIgnoreCase("optional"));
/*      */       }
/*      */ 
/*  823 */       return elem;
/*      */     }
/*      */ 
/*  826 */     return null;
/*      */   }
/*      */ 
/*      */   public static QName getElementAnonQName(Node node)
/*      */   {
/*  838 */     if (isXSDNode(node, "element")) {
/*  839 */       NodeList children = node.getChildNodes();
/*      */ 
/*  841 */       for (int j = 0; j < children.getLength(); j++) {
/*  842 */         Node kid = children.item(j);
/*      */ 
/*  844 */         if ((isXSDNode(kid, "complexType")) || (isXSDNode(kid, "simpleType")))
/*      */         {
/*  846 */           return Utils.getNodeNameQName(kid);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  851 */     return null;
/*      */   }
/*      */ 
/*      */   public static QName getAttributeAnonQName(Node node)
/*      */   {
/*  863 */     if (isXSDNode(node, "attribute")) {
/*  864 */       NodeList children = node.getChildNodes();
/*      */ 
/*  866 */       for (int j = 0; j < children.getLength(); j++) {
/*  867 */         Node kid = children.item(j);
/*      */ 
/*  869 */         if ((isXSDNode(kid, "complexType")) || (isXSDNode(kid, "simpleType")))
/*      */         {
/*  871 */           return Utils.getNodeNameQName(kid);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  876 */     return null;
/*      */   }
/*      */ 
/*      */   public static boolean isSimpleTypeOrSimpleContent(Node node)
/*      */   {
/*  887 */     if (node == null) {
/*  888 */       return false;
/*      */     }
/*      */ 
/*  892 */     if (isXSDNode(node, "element")) {
/*  893 */       NodeList children = node.getChildNodes();
/*      */ 
/*  895 */       for (int j = 0; j < children.getLength(); j++) {
/*  896 */         Node kid = children.item(j);
/*      */ 
/*  898 */         if (isXSDNode(kid, "complexType")) {
/*  899 */           node = kid;
/*      */ 
/*  901 */           break;
/*  902 */         }if (isXSDNode(kid, "simpleType")) {
/*  903 */           return true;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  909 */     if (isXSDNode(node, "simpleType")) {
/*  910 */       return true;
/*      */     }
/*      */ 
/*  913 */     if (isXSDNode(node, "complexType"))
/*      */     {
/*  917 */       NodeList children = node.getChildNodes();
/*  918 */       Node complexContent = null;
/*  919 */       Node simpleContent = null;
/*      */ 
/*  921 */       for (int j = 0; j < children.getLength(); j++) {
/*  922 */         Node kid = children.item(j);
/*      */ 
/*  924 */         if (isXSDNode(kid, "complexContent")) {
/*  925 */           complexContent = kid;
/*      */ 
/*  927 */           break;
/*  928 */         }if (isXSDNode(kid, "simpleContent")) {
/*  929 */           simpleContent = kid;
/*      */         }
/*      */       }
/*      */ 
/*  933 */       if (complexContent != null) {
/*  934 */         return false;
/*      */       }
/*      */ 
/*  937 */       if (simpleContent != null) {
/*  938 */         return true;
/*      */       }
/*      */     }
/*      */ 
/*  942 */     return false;
/*      */   }
/*      */ 
/*      */   private static boolean isXSDNode(Node node, String schemaLocalName)
/*      */   {
/*  958 */     if (node == null) {
/*  959 */       return false;
/*      */     }
/*  961 */     String localName = node.getLocalName();
/*  962 */     if (localName == null) {
/*  963 */       return false;
/*      */     }
/*  965 */     return (localName.equals(schemaLocalName)) && (Constants.isSchemaXSD(node.getNamespaceURI()));
/*      */   }
/*      */ 
/*      */   public static TypeEntry getComplexElementRestrictionBase(Node node, SymbolTable symbolTable)
/*      */   {
/*  980 */     if (node == null) {
/*  981 */       return null;
/*      */     }
/*      */ 
/*  985 */     if (isXSDNode(node, "element")) {
/*  986 */       NodeList children = node.getChildNodes();
/*  987 */       Node complexNode = null;
/*      */ 
/*  989 */       int j = 0;
/*  990 */       for (; (j < children.getLength()) && (complexNode == null); j++) {
/*  991 */         if (isXSDNode(children.item(j), "complexType")) {
/*  992 */           complexNode = children.item(j);
/*  993 */           node = complexNode;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  999 */     if (isXSDNode(node, "complexType"))
/*      */     {
/* 1003 */       NodeList children = node.getChildNodes();
/* 1004 */       Node content = null;
/* 1005 */       Node restriction = null;
/*      */ 
/* 1007 */       for (int j = 0; (j < children.getLength()) && (content == null); )
/*      */       {
/* 1009 */         Node kid = children.item(j);
/*      */ 
/* 1011 */         if ((isXSDNode(kid, "complexContent")) || (isXSDNode(kid, "simpleContent")))
/* 1012 */           content = kid;
/* 1008 */         j++;
/*      */       }
/*      */ 
/* 1016 */       if (content != null) {
/* 1017 */         children = content.getChildNodes();
/*      */ 
/* 1019 */         int j = 0;
/* 1020 */         while ((j < children.getLength()) && (restriction == null))
/*      */         {
/* 1022 */           Node kid = children.item(j);
/*      */ 
/* 1024 */           if (isXSDNode(kid, "restriction"))
/* 1025 */             restriction = kid;
/* 1021 */           j++;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1030 */       if (restriction == null) {
/* 1031 */         return null;
/*      */       }
/*      */ 
/* 1035 */       QName restrictionType = Utils.getTypeQName(restriction, new BooleanHolder(), false);
/*      */ 
/* 1039 */       if (restrictionType == null) {
/* 1040 */         return null;
/*      */       }
/*      */ 
/* 1044 */       return symbolTable.getType(restrictionType);
/*      */     }
/*      */ 
/* 1048 */     return null;
/*      */   }
/*      */ 
/*      */   public static TypeEntry getComplexElementExtensionBase(Node node, SymbolTable symbolTable)
/*      */   {
/* 1063 */     if (node == null) {
/* 1064 */       return null;
/*      */     }
/*      */ 
/* 1067 */     TypeEntry cached = (TypeEntry)symbolTable.node2ExtensionBase.get(node);
/*      */ 
/* 1069 */     if (cached != null) {
/* 1070 */       return cached;
/*      */     }
/*      */ 
/* 1074 */     if (isXSDNode(node, "element")) {
/* 1075 */       NodeList children = node.getChildNodes();
/* 1076 */       Node complexNode = null;
/*      */ 
/* 1078 */       int j = 0;
/* 1079 */       for (; (j < children.getLength()) && (complexNode == null); j++) {
/* 1080 */         if (isXSDNode(children.item(j), "complexType")) {
/* 1081 */           complexNode = children.item(j);
/* 1082 */           node = complexNode;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1088 */     if (isXSDNode(node, "complexType"))
/*      */     {
/* 1092 */       NodeList children = node.getChildNodes();
/* 1093 */       Node content = null;
/* 1094 */       Node extension = null;
/*      */ 
/* 1096 */       for (int j = 0; (j < children.getLength()) && (content == null); )
/*      */       {
/* 1098 */         Node kid = children.item(j);
/*      */ 
/* 1100 */         if ((isXSDNode(kid, "complexContent")) || (isXSDNode(kid, "simpleContent")))
/*      */         {
/* 1102 */           content = kid;
/*      */         }
/* 1097 */         j++;
/*      */       }
/*      */ 
/* 1106 */       if (content != null) {
/* 1107 */         children = content.getChildNodes();
/*      */ 
/* 1109 */         int j = 0;
/* 1110 */         while ((j < children.getLength()) && (extension == null))
/*      */         {
/* 1112 */           Node kid = children.item(j);
/*      */ 
/* 1114 */           if (isXSDNode(kid, "extension"))
/* 1115 */             extension = kid;
/* 1111 */           j++;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1120 */       if (extension == null) {
/* 1121 */         cached = null;
/*      */       }
/*      */       else
/*      */       {
/* 1125 */         QName extendsType = Utils.getTypeQName(extension, new BooleanHolder(), false);
/*      */ 
/* 1129 */         if (extendsType == null) {
/* 1130 */           cached = null;
/*      */         }
/*      */         else
/*      */         {
/* 1134 */           cached = symbolTable.getType(extendsType);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1139 */     symbolTable.node2ExtensionBase.put(node, cached);
/*      */ 
/* 1141 */     return cached;
/*      */   }
/*      */ 
/*      */   public static QName getSimpleTypeBase(Node node)
/*      */   {
/* 1153 */     QName[] qname = getContainedSimpleTypes(node);
/*      */ 
/* 1155 */     if ((qname != null) && (qname.length > 0)) {
/* 1156 */       return qname[0];
/*      */     }
/*      */ 
/* 1159 */     return null;
/*      */   }
/*      */ 
/*      */   public static QName[] getContainedSimpleTypes(Node node)
/*      */   {
/* 1170 */     QName[] baseQNames = null;
/*      */ 
/* 1172 */     if (node == null) {
/* 1173 */       return null;
/*      */     }
/*      */ 
/* 1177 */     if (isXSDNode(node, "element")) {
/* 1178 */       NodeList children = node.getChildNodes();
/*      */ 
/* 1180 */       for (int j = 0; j < children.getLength(); j++) {
/* 1181 */         if (isXSDNode(children.item(j), "simpleType")) {
/* 1182 */           node = children.item(j);
/*      */ 
/* 1184 */           break;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1190 */     if (isXSDNode(node, "simpleType"))
/*      */     {
/* 1194 */       NodeList children = node.getChildNodes();
/* 1195 */       Node restrictionNode = null;
/* 1196 */       Node unionNode = null;
/*      */ 
/* 1198 */       int j = 0;
/* 1199 */       while ((j < children.getLength()) && (restrictionNode == null))
/*      */       {
/* 1201 */         if (isXSDNode(children.item(j), "restriction"))
/* 1202 */           restrictionNode = children.item(j);
/* 1203 */         else if (isXSDNode(children.item(j), "union"))
/* 1204 */           unionNode = children.item(j);
/* 1200 */         j++;
/*      */       }
/*      */ 
/* 1210 */       if (restrictionNode != null) {
/* 1211 */         baseQNames = new QName[1];
/* 1212 */         baseQNames[0] = Utils.getTypeQName(restrictionNode, new BooleanHolder(), false);
/*      */       }
/*      */ 
/* 1216 */       if (unionNode != null) {
/* 1217 */         baseQNames = Utils.getMemberTypeQNames(unionNode);
/*      */       }
/*      */ 
/* 1221 */       if ((baseQNames != null) && (restrictionNode != null) && (unionNode != null))
/*      */       {
/* 1223 */         NodeList enums = restrictionNode.getChildNodes();
/*      */ 
/* 1225 */         for (int i = 0; i < enums.getLength(); i++) {
/* 1226 */           if (isXSDNode(enums.item(i), "enumeration"))
/*      */           {
/* 1230 */             return null;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1236 */     return baseQNames;
/*      */   }
/*      */ 
/*      */   public static Node getRestrictionOrExtensionNode(Node node)
/*      */   {
/* 1248 */     Node re = null;
/*      */ 
/* 1250 */     if (node == null) {
/* 1251 */       return re;
/*      */     }
/*      */ 
/* 1255 */     if (isXSDNode(node, "element")) {
/* 1256 */       NodeList children = node.getChildNodes();
/*      */ 
/* 1258 */       for (int j = 0; j < children.getLength(); j++) {
/* 1259 */         Node n = children.item(j);
/*      */ 
/* 1261 */         if ((!isXSDNode(n, "simpleType")) && (!isXSDNode(n, "complexType")) && (!isXSDNode(n, "simpleContent")))
/*      */           continue;
/* 1263 */         node = n;
/*      */ 
/* 1265 */         break;
/*      */       }
/*      */     }
/*      */     NodeList children;
/*      */     int j;
/* 1271 */     if ((isXSDNode(node, "simpleType")) || (isXSDNode(node, "complexType")))
/*      */     {
/* 1274 */       children = node.getChildNodes();
/* 1275 */       Node complexContent = null;
/*      */ 
/* 1277 */       if (node.getLocalName().equals("complexType")) {
/* 1278 */         int j = 0;
/* 1279 */         while ((j < children.getLength()) && (complexContent == null))
/*      */         {
/* 1281 */           Node kid = children.item(j);
/*      */ 
/* 1283 */           if ((isXSDNode(kid, "complexContent")) || (isXSDNode(kid, "simpleContent")))
/*      */           {
/* 1285 */             complexContent = kid;
/*      */           }
/* 1280 */           j++;
/*      */         }
/*      */ 
/* 1289 */         node = complexContent;
/*      */       }
/*      */ 
/* 1293 */       if (node != null) {
/* 1294 */         children = node.getChildNodes();
/*      */ 
/* 1296 */         for (j = 0; (j < children.getLength()) && (re == null); )
/*      */         {
/* 1298 */           Node kid = children.item(j);
/*      */ 
/* 1300 */           if ((isXSDNode(kid, "extension")) || (isXSDNode(kid, "restriction")))
/*      */           {
/* 1302 */             re = kid;
/*      */           }
/* 1297 */           j++;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1308 */     return re;
/*      */   }
/*      */ 
/*      */   public static QName getArrayComponentQName(Node node, IntHolder dims, BooleanHolder underlTypeNillable, QNameHolder itemQName, BooleanHolder forElement, SymbolTable symbolTable)
/*      */   {
/* 1328 */     dims.value = 1;
/* 1329 */     underlTypeNillable.value = false;
/*      */ 
/* 1331 */     QName qName = getCollectionComponentQName(node, itemQName, forElement, symbolTable);
/*      */ 
/* 1333 */     if (qName == null) {
/* 1334 */       qName = getArrayComponentQName_JAXRPC(node, dims, underlTypeNillable, symbolTable);
/*      */     }
/*      */ 
/* 1337 */     return qName;
/*      */   }
/*      */ 
/*      */   public static QName getCollectionComponentQName(Node node, QNameHolder itemQName, BooleanHolder forElement, SymbolTable symbolTable)
/*      */   {
/* 1371 */     boolean storeComponentQName = false;
/*      */ 
/* 1373 */     if (node == null) {
/* 1374 */       return null;
/*      */     }
/*      */ 
/* 1377 */     if ((itemQName != null) && (isXSDNode(node, "complexType")))
/*      */     {
/* 1382 */       Node sequence = getChildByName(node, "sequence");
/* 1383 */       if (sequence == null) {
/* 1384 */         return null;
/*      */       }
/* 1386 */       NodeList children = sequence.getChildNodes();
/* 1387 */       Node element = null;
/* 1388 */       for (int i = 0; i < children.getLength(); i++) {
/* 1389 */         if (children.item(i).getNodeType() == 1) {
/* 1390 */           if (element == null)
/* 1391 */             element = children.item(i);
/*      */           else {
/* 1393 */             return null;
/*      */           }
/*      */         }
/*      */       }
/* 1397 */       if (element == null) {
/* 1398 */         return null;
/*      */       }
/*      */ 
/* 1403 */       node = element;
/* 1404 */       storeComponentQName = true;
/*      */       try {
/* 1406 */         symbolTable.createTypeFromRef(node);
/*      */       } catch (IOException e) {
/* 1408 */         throw new RuntimeException(Messages.getMessage("exception01", e.toString()));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1413 */     if (isXSDNode(node, "element"))
/*      */     {
/* 1417 */       QName componentTypeQName = Utils.getTypeQName(node, forElement, true);
/*      */ 
/* 1421 */       if (componentTypeQName != null) {
/* 1422 */         QName fullQName = Utils.getTypeQName(node, forElement, false);
/*      */ 
/* 1424 */         if (!componentTypeQName.equals(fullQName)) {
/* 1425 */           if (storeComponentQName) {
/* 1426 */             String name = Utils.getAttribute(node, "name");
/* 1427 */             if (name != null)
/*      */             {
/* 1429 */               String def = Utils.getScopedAttribute(node, "elementFormDefault");
/*      */ 
/* 1431 */               String namespace = "";
/* 1432 */               if ((def != null) && (def.equals("qualified"))) {
/* 1433 */                 namespace = Utils.getScopedAttribute(node, "targetNamespace");
/*      */               }
/* 1435 */               itemQName.value = new QName(namespace, name);
/*      */             }
/*      */           }
/* 1438 */           return componentTypeQName;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1443 */     return null;
/*      */   }
/*      */ 
/*      */   private static QName getArrayComponentQName_JAXRPC(Node node, IntHolder dims, BooleanHolder underlTypeNillable, SymbolTable symbolTable)
/*      */   {
/* 1480 */     dims.value = 0;
/* 1481 */     underlTypeNillable.value = false;
/* 1482 */     if (node == null) {
/* 1483 */       return null;
/*      */     }
/*      */ 
/* 1487 */     if (isXSDNode(node, "element")) {
/* 1488 */       NodeList children = node.getChildNodes();
/*      */ 
/* 1490 */       for (int j = 0; j < children.getLength(); j++) {
/* 1491 */         Node kid = children.item(j);
/*      */ 
/* 1493 */         if (isXSDNode(kid, "complexType")) {
/* 1494 */           node = kid;
/*      */ 
/* 1496 */           break;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1502 */     if (isXSDNode(node, "complexType"))
/*      */     {
/* 1506 */       NodeList children = node.getChildNodes();
/* 1507 */       Node complexContentNode = null;
/*      */ 
/* 1509 */       for (int j = 0; j < children.getLength(); j++) {
/* 1510 */         Node kid = children.item(j);
/*      */ 
/* 1512 */         if ((!isXSDNode(kid, "complexContent")) && (!isXSDNode(kid, "simpleContent")))
/*      */           continue;
/* 1514 */         complexContentNode = kid;
/*      */ 
/* 1516 */         break;
/*      */       }
/*      */ 
/* 1522 */       Node restrictionNode = null;
/*      */ 
/* 1524 */       if (complexContentNode != null) {
/* 1525 */         children = complexContentNode.getChildNodes();
/*      */ 
/* 1527 */         for (int j = 0; j < children.getLength(); j++) {
/* 1528 */           Node kid = children.item(j);
/*      */ 
/* 1530 */           if (isXSDNode(kid, "restriction")) {
/* 1531 */             restrictionNode = kid;
/*      */ 
/* 1533 */             break;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1539 */       QName baseType = null;
/*      */ 
/* 1541 */       if (restrictionNode != null) {
/* 1542 */         baseType = Utils.getTypeQName(restrictionNode, new BooleanHolder(), false);
/*      */ 
/* 1545 */         if ((baseType != null) && (
/* 1546 */           (!baseType.getLocalPart().equals("Array")) || (!Constants.isSOAP_ENC(baseType.getNamespaceURI()))))
/*      */         {
/* 1548 */           if (!symbolTable.arrayTypeQNames.contains(baseType)) {
/* 1549 */             baseType = null;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1557 */       Node groupNode = null;
/* 1558 */       Node attributeNode = null;
/*      */ 
/* 1560 */       if (baseType != null) {
/* 1561 */         children = restrictionNode.getChildNodes();
/*      */ 
/* 1563 */         int j = 0;
/*      */ 
/* 1565 */         for (; (j < children.getLength()) && (groupNode == null) && (attributeNode == null); j++) {
/* 1566 */           Node kid = children.item(j);
/*      */ 
/* 1568 */           if ((isXSDNode(kid, "sequence")) || (isXSDNode(kid, "all"))) {
/* 1569 */             groupNode = kid;
/*      */ 
/* 1571 */             if (groupNode.getChildNodes().getLength() == 0)
/*      */             {
/* 1582 */               groupNode = null;
/*      */             }
/*      */           }
/*      */ 
/* 1586 */           if (!isXSDNode(kid, "attribute"))
/*      */           {
/*      */             continue;
/*      */           }
/* 1590 */           BooleanHolder isRef = new BooleanHolder();
/* 1591 */           QName refQName = Utils.getTypeQName(kid, isRef, false);
/*      */ 
/* 1594 */           if ((refQName == null) || (!isRef.value) || (!refQName.getLocalPart().equals("arrayType")) || (!Constants.isSOAP_ENC(refQName.getNamespaceURI())))
/*      */           {
/*      */             continue;
/*      */           }
/* 1598 */           attributeNode = kid;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1605 */       if (attributeNode != null) {
/* 1606 */         String wsdlArrayTypeValue = null;
/* 1607 */         Vector attrs = Utils.getAttributesWithLocalName(attributeNode, "arrayType");
/*      */ 
/* 1611 */         int i = 0;
/* 1612 */         while ((i < attrs.size()) && (wsdlArrayTypeValue == null))
/*      */         {
/* 1614 */           Node attrNode = (Node)attrs.elementAt(i);
/* 1615 */           String attrName = attrNode.getNodeName();
/* 1616 */           QName attrQName = Utils.getQNameFromPrefixedName(attributeNode, attrName);
/*      */ 
/* 1619 */           if (Constants.isWSDL(attrQName.getNamespaceURI()))
/* 1620 */             wsdlArrayTypeValue = attrNode.getNodeValue();
/* 1613 */           i++;
/*      */         }
/*      */ 
/* 1628 */         if (wsdlArrayTypeValue != null) {
/* 1629 */           int i = wsdlArrayTypeValue.indexOf('[');
/*      */ 
/* 1631 */           if (i > 0) {
/* 1632 */             String prefixedName = wsdlArrayTypeValue.substring(0, i);
/*      */ 
/* 1634 */             String mangledString = wsdlArrayTypeValue.replace(',', '[');
/*      */ 
/* 1637 */             dims.value = 0;
/*      */ 
/* 1639 */             int index = mangledString.indexOf('[');
/*      */ 
/* 1641 */             while (index > 0) {
/* 1642 */               dims.value += 1;
/*      */ 
/* 1644 */               index = mangledString.indexOf('[', index + 1);
/*      */             }
/*      */ 
/* 1647 */             return Utils.getQNameFromPrefixedName(restrictionNode, prefixedName);
/*      */           }
/*      */         }
/*      */       }
/* 1651 */       else if (groupNode != null)
/*      */       {
/* 1654 */         NodeList elements = groupNode.getChildNodes();
/* 1655 */         Node elementNode = null;
/*      */ 
/* 1657 */         int i = 0;
/* 1658 */         while ((i < elements.getLength()) && (elementNode == null))
/*      */         {
/* 1660 */           Node kid = elements.item(i);
/*      */ 
/* 1662 */           if (isXSDNode(kid, "element")) {
/* 1663 */             elementNode = elements.item(i);
/*      */ 
/* 1665 */             break;
/*      */           }
/* 1659 */           i++;
/*      */         }
/*      */ 
/* 1671 */         if (elementNode != null)
/*      */         {
/* 1673 */           String underlTypeNillableValue = Utils.getAttribute(elementNode, "nillable");
/*      */ 
/* 1676 */           if ((underlTypeNillableValue != null) && (underlTypeNillableValue.equals("true")))
/*      */           {
/* 1679 */             underlTypeNillable.value = true;
/*      */           }
/*      */ 
/* 1682 */           String maxOccursValue = Utils.getAttribute(elementNode, "maxOccurs");
/*      */ 
/* 1685 */           if ((maxOccursValue != null) && (maxOccursValue.equalsIgnoreCase("unbounded")))
/*      */           {
/* 1689 */             dims.value = 1;
/*      */ 
/* 1691 */             return Utils.getTypeQName(elementNode, new BooleanHolder(), true);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1698 */     return null;
/*      */   }
/*      */ 
/*      */   private static void addAttributeToVector(Vector v, Node child, SymbolTable symbolTable)
/*      */   {
/* 1715 */     QName attributeName = Utils.getNodeNameQName(child);
/* 1716 */     BooleanHolder forElement = new BooleanHolder();
/* 1717 */     QName attributeType = Utils.getTypeQName(child, forElement, false);
/*      */ 
/* 1724 */     if (!forElement.value)
/*      */     {
/* 1729 */       String form = Utils.getAttribute(child, "form");
/*      */ 
/* 1731 */       if ((form != null) && (form.equals("unqualified")))
/*      */       {
/* 1734 */         attributeName = Utils.findQName("", attributeName.getLocalPart());
/*      */       }
/* 1736 */       else if (form == null)
/*      */       {
/* 1739 */         String def = Utils.getScopedAttribute(child, "attributeFormDefault");
/*      */ 
/* 1742 */         if ((def == null) || (def.equals("unqualified")))
/*      */         {
/* 1745 */           attributeName = Utils.findQName("", attributeName.getLocalPart());
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 1750 */       attributeName = attributeType;
/*      */     }
/*      */ 
/* 1754 */     TypeEntry type = symbolTable.getTypeEntry(attributeType, forElement.value);
/*      */ 
/* 1759 */     if ((type instanceof Element)) {
/* 1760 */       type = ((Element)type).getRefType();
/*      */     }
/*      */ 
/* 1765 */     if ((type != null) && (attributeName != null)) {
/* 1766 */       ContainedAttribute attr = new ContainedAttribute(type, attributeName);
/*      */ 
/* 1769 */       String useValue = Utils.getAttribute(child, "use");
/*      */ 
/* 1771 */       if (useValue != null) {
/* 1772 */         attr.setOptional(useValue.equalsIgnoreCase("optional"));
/*      */       }
/*      */ 
/* 1775 */       v.add(attr);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void addAttributeToVector(Vector v, SymbolTable symbolTable, QName type, QName name)
/*      */   {
/* 1791 */     TypeEntry typeEnt = symbolTable.getTypeEntry(type, false);
/*      */ 
/* 1793 */     if (typeEnt != null)
/*      */     {
/* 1795 */       v.add(new ContainedAttribute(typeEnt, name));
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void addAttributeGroupToVector(Vector v, Node attrGrpnode, SymbolTable symbolTable)
/*      */   {
/* 1811 */     QName attributeGroupType = Utils.getTypeQName(attrGrpnode, new BooleanHolder(), false);
/*      */ 
/* 1813 */     TypeEntry type = symbolTable.getTypeEntry(attributeGroupType, false);
/*      */ 
/* 1816 */     if (type != null)
/* 1817 */       if (type.getNode() != null)
/*      */       {
/* 1820 */         NodeList children = type.getNode().getChildNodes();
/*      */ 
/* 1822 */         for (int j = 0; j < children.getLength(); j++) {
/* 1823 */           Node kid = children.item(j);
/*      */ 
/* 1825 */           if (isXSDNode(kid, "attribute"))
/* 1826 */             addAttributeToVector(v, kid, symbolTable);
/* 1827 */           else if (isXSDNode(kid, "attributeGroup"))
/* 1828 */             addAttributeGroupToVector(v, kid, symbolTable);
/*      */         }
/*      */       }
/* 1831 */       else if (type.isBaseType())
/*      */       {
/* 1835 */         if (type.getQName().equals(Constants.SOAP_COMMON_ATTRS11))
/*      */         {
/* 1838 */           addAttributeToVector(v, symbolTable, Constants.XSD_ID, new QName("http://schemas.xmlsoap.org/soap/encoding/", "id"));
/*      */ 
/* 1841 */           addAttributeToVector(v, symbolTable, Constants.XSD_ANYURI, new QName("http://schemas.xmlsoap.org/soap/encoding/", "href"));
/*      */         }
/* 1844 */         else if (type.getQName().equals(Constants.SOAP_COMMON_ATTRS12))
/*      */         {
/* 1848 */           addAttributeToVector(v, symbolTable, Constants.XSD_ID, new QName("http://www.w3.org/2003/05/soap-encoding", "id"));
/*      */         }
/* 1851 */         else if (type.getQName().equals(Constants.SOAP_ARRAY_ATTRS11))
/*      */         {
/* 1855 */           addAttributeToVector(v, symbolTable, Constants.XSD_STRING, new QName("http://www.w3.org/2003/05/soap-encoding", "arrayType"));
/*      */ 
/* 1858 */           addAttributeToVector(v, symbolTable, Constants.XSD_STRING, new QName("http://www.w3.org/2003/05/soap-encoding", "offset"));
/*      */         }
/* 1861 */         else if (type.getQName().equals(Constants.SOAP_ARRAY_ATTRS12))
/*      */         {
/* 1871 */           addAttributeToVector(v, symbolTable, Constants.XSD_STRING, new QName("http://www.w3.org/2003/05/soap-encoding", "arraySize"));
/*      */ 
/* 1874 */           addAttributeToVector(v, symbolTable, Constants.XSD_QNAME, new QName("http://www.w3.org/2003/05/soap-encoding", "itemType"));
/*      */         }
/*      */       }
/*      */   }
/*      */ 
/*      */   public static Vector getContainedAttributeTypes(Node node, SymbolTable symbolTable)
/*      */   {
/* 1905 */     Vector v = null;
/*      */ 
/* 1907 */     if (node == null) {
/* 1908 */       return null;
/*      */     }
/*      */ 
/* 1913 */     if (isXSDNode(node, "element")) {
/* 1914 */       NodeList children = node.getChildNodes();
/* 1915 */       int len = children.getLength();
/* 1916 */       for (int j = 0; j < len; j++) {
/* 1917 */         Node kid = children.item(j);
/*      */ 
/* 1919 */         if (isXSDNode(kid, "complexType")) {
/* 1920 */           node = kid;
/*      */ 
/* 1922 */           break;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1928 */     if (isXSDNode(node, "complexType"))
/*      */     {
/* 1932 */       NodeList children = node.getChildNodes();
/* 1933 */       Node content = null;
/* 1934 */       int len = children.getLength();
/* 1935 */       for (int j = 0; j < len; j++) {
/* 1936 */         Node kid = children.item(j);
/*      */ 
/* 1938 */         if ((!isXSDNode(kid, "complexContent")) && (!isXSDNode(kid, "simpleContent")))
/*      */           continue;
/* 1940 */         content = kid;
/*      */ 
/* 1942 */         break;
/*      */       }
/*      */ 
/* 1947 */       if (content != null) {
/* 1948 */         children = content.getChildNodes();
/* 1949 */         len = children.getLength();
/* 1950 */         for (int j = 0; j < len; j++) {
/* 1951 */           Node kid = children.item(j);
/*      */ 
/* 1953 */           if ((!isXSDNode(kid, "extension")) && (!isXSDNode(kid, "restriction")))
/*      */             continue;
/* 1955 */           node = kid;
/*      */ 
/* 1957 */           break;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1963 */       children = node.getChildNodes();
/* 1964 */       len = children.getLength();
/* 1965 */       for (int i = 0; i < len; i++) {
/* 1966 */         Node child = children.item(i);
/*      */ 
/* 1968 */         if (isXSDNode(child, "attributeGroup")) {
/* 1969 */           if (v == null) {
/* 1970 */             v = new Vector();
/*      */           }
/* 1972 */           addAttributeGroupToVector(v, child, symbolTable);
/* 1973 */         } else if (isXSDNode(child, "anyAttribute"))
/*      */         {
/* 1975 */           if (v == null)
/* 1976 */             v = new Vector();
/*      */         } else {
/* 1978 */           if (!isXSDNode(child, "attribute"))
/*      */             continue;
/* 1980 */           if (v == null) {
/* 1981 */             v = new Vector();
/*      */           }
/* 1983 */           addAttributeToVector(v, child, symbolTable);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1988 */     return v;
/*      */   }
/*      */ 
/*      */   private static boolean isSimpleSchemaType(String s)
/*      */   {
/* 2018 */     if (s == null) {
/* 2019 */       return false;
/*      */     }
/*      */ 
/* 2022 */     return schemaTypeSet.contains(s);
/*      */   }
/*      */ 
/*      */   public static boolean isSimpleSchemaType(QName qname)
/*      */   {
/* 2033 */     if ((qname == null) || (!Constants.isSchemaXSD(qname.getNamespaceURI()))) {
/* 2034 */       return false;
/*      */     }
/*      */ 
/* 2037 */     return isSimpleSchemaType(qname.getLocalPart());
/*      */   }
/*      */ 
/*      */   public static TypeEntry getBaseType(TypeEntry type, SymbolTable symbolTable)
/*      */   {
/* 2050 */     Node node = type.getNode();
/* 2051 */     TypeEntry base = getComplexElementExtensionBase(node, symbolTable);
/*      */ 
/* 2053 */     if (base == null) {
/* 2054 */       base = getComplexElementRestrictionBase(node, symbolTable);
/*      */     }
/*      */ 
/* 2057 */     if (base == null) {
/* 2058 */       QName baseQName = getSimpleTypeBase(node);
/* 2059 */       if (baseQName != null) {
/* 2060 */         base = symbolTable.getType(baseQName);
/*      */       }
/*      */     }
/* 2063 */     return base;
/*      */   }
/*      */ 
/*      */   public static boolean isListWithItemType(Node node)
/*      */   {
/* 2074 */     return getListItemType(node) != null;
/*      */   }
/*      */ 
/*      */   public static QName getListItemType(Node node)
/*      */   {
/* 2084 */     if (node == null) {
/* 2085 */       return null;
/*      */     }
/*      */ 
/* 2089 */     if (isXSDNode(node, "element")) {
/* 2090 */       NodeList children = node.getChildNodes();
/* 2091 */       for (int j = 0; j < children.getLength(); j++) {
/* 2092 */         if (isXSDNode(children.item(j), "simpleType")) {
/* 2093 */           node = children.item(j);
/* 2094 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2099 */     if (isXSDNode(node, "simpleType")) {
/* 2100 */       NodeList children = node.getChildNodes();
/* 2101 */       for (int j = 0; j < children.getLength(); j++) {
/* 2102 */         if (isXSDNode(children.item(j), "list")) {
/* 2103 */           Node listNode = children.item(j);
/* 2104 */           org.w3c.dom.Element listElement = (org.w3c.dom.Element)listNode;
/*      */ 
/* 2106 */           String type = listElement.getAttribute("itemType");
/* 2107 */           if (type.equals("")) {
/* 2108 */             Node localType = null;
/* 2109 */             children = listNode.getChildNodes();
/* 2110 */             for (j = 0; (j < children.getLength()) && (localType == null); j++) {
/* 2111 */               if (isXSDNode(children.item(j), "simpleType")) {
/* 2112 */                 localType = children.item(j);
/*      */               }
/*      */             }
/* 2115 */             if (localType != null) {
/* 2116 */               return getSimpleTypeBase(localType);
/*      */             }
/* 2118 */             return null;
/*      */           }
/*      */ 
/* 2125 */           return Utils.getQNameFromPrefixedName(node, type);
/*      */         }
/*      */       }
/*      */     }
/* 2129 */     return null;
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.SchemaUtils
 * JD-Core Version:    0.6.0
 */