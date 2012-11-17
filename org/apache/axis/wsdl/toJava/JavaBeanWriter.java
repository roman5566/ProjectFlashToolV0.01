/*      */ package org.apache.axis.wsdl.toJava;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.PrintWriter;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.Set;
/*      */ import java.util.Vector;
/*      */ import javax.xml.namespace.QName;
/*      */ import org.apache.axis.utils.JavaUtils;
/*      */ import org.apache.axis.utils.Messages;
/*      */ import org.apache.axis.wsdl.symbolTable.ContainedAttribute;
/*      */ import org.apache.axis.wsdl.symbolTable.ElementDecl;
/*      */ import org.apache.axis.wsdl.symbolTable.SchemaUtils;
/*      */ import org.apache.axis.wsdl.symbolTable.TypeEntry;
/*      */ import org.w3c.dom.DOMException;
/*      */ import org.w3c.dom.Node;
/*      */ 
/*      */ public class JavaBeanWriter extends JavaClassWriter
/*      */ {
/*      */   private TypeEntry type;
/*      */   private Vector elements;
/*      */   private Vector attributes;
/*      */   private TypeEntry extendType;
/*      */   protected JavaBeanHelperWriter helper;
/*   57 */   protected Vector names = new Vector();
/*      */ 
/*   60 */   protected ArrayList simpleValueTypes = new ArrayList();
/*      */ 
/*   64 */   protected Set enumerationTypes = new HashSet();
/*      */   protected PrintWriter pw;
/*   73 */   protected boolean enableDefaultConstructor = true;
/*      */ 
/*   76 */   protected boolean enableFullConstructor = false;
/*      */ 
/*   79 */   protected boolean enableSimpleConstructors = false;
/*      */ 
/*   82 */   protected boolean enableToString = false;
/*      */ 
/*   85 */   protected boolean enableSetters = true;
/*      */ 
/*   88 */   protected boolean enableGetters = true;
/*      */ 
/*   91 */   protected boolean enableEquals = true;
/*      */ 
/*   94 */   protected boolean enableHashCode = true;
/*      */ 
/*   97 */   protected boolean enableMemberFields = true;
/*      */ 
/*  100 */   protected boolean isAny = false;
/*      */ 
/*  103 */   protected boolean isMixed = false;
/*      */ 
/*  106 */   protected boolean parentIsAny = false;
/*      */ 
/*  109 */   protected boolean parentIsMixed = false;
/*      */ 
/*      */   protected JavaBeanWriter(Emitter emitter, TypeEntry type, Vector elements, TypeEntry extendType, Vector attributes, JavaWriter helper)
/*      */   {
/*  126 */     super(emitter, type.getName(), "complexType");
/*      */ 
/*  128 */     this.type = type;
/*  129 */     this.elements = elements;
/*  130 */     this.attributes = attributes;
/*  131 */     this.extendType = extendType;
/*  132 */     this.helper = ((JavaBeanHelperWriter)helper);
/*      */ 
/*  134 */     if (type.isSimpleType()) {
/*  135 */       this.enableSimpleConstructors = true;
/*  136 */       this.enableToString = true;
/*      */     }
/*  143 */     else if (null != extendType) {
/*  144 */       if (null != SchemaUtils.getComplexElementRestrictionBase(type.getNode(), emitter.getSymbolTable()))
/*      */       {
/*  146 */         this.enableMemberFields = false;
/*  147 */         this.enableGetters = false;
/*  148 */         this.enableSetters = false;
/*  149 */         this.enableEquals = false;
/*  150 */         this.enableHashCode = false;
/*      */       }
/*      */       else
/*      */       {
/*  155 */         this.enableFullConstructor = true;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  160 */     preprocess();
/*      */   }
/*      */ 
/*      */   protected void writeFileHeader(PrintWriter pw)
/*      */     throws IOException
/*      */   {
/*  170 */     writeHeaderComments(pw);
/*  171 */     writePackage(pw);
/*      */     try
/*      */     {
/*  175 */       String comments = SchemaUtils.getAnnotationDocumentation(this.type.getNode());
/*  176 */       comments = getJavadocDescriptionPart(comments, false);
/*  177 */       if ((comments != null) && (comments.trim().length() > 0))
/*      */       {
/*  179 */         pw.println();
/*  180 */         pw.println("/**");
/*  181 */         pw.println(comments);
/*  182 */         pw.println(" */");
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (DOMException e)
/*      */     {
/*      */     }
/*      */ 
/*  190 */     pw.println(getClassModifiers() + getClassText() + getClassName() + ' ' + getExtendsText() + getImplementsText() + "{");
/*      */   }
/*      */ 
/*      */   protected void writeFileBody(PrintWriter pw)
/*      */     throws IOException
/*      */   {
/*  201 */     this.pw = pw;
/*      */ 
/*  209 */     if (this.enableMemberFields) {
/*  210 */       writeMemberFields();
/*      */     }
/*      */ 
/*  214 */     if (this.enableDefaultConstructor) {
/*  215 */       writeDefaultConstructor();
/*      */     }
/*      */ 
/*  219 */     if (this.enableFullConstructor) {
/*  220 */       writeFullConstructor();
/*      */     }
/*      */ 
/*  224 */     if (this.enableSimpleConstructors) {
/*  225 */       writeSimpleConstructors();
/*      */     }
/*      */ 
/*  228 */     if ((!this.enableFullConstructor) && (!this.enableSimpleConstructors) && (this.enableMemberFields)) {
/*  229 */       writeMinimalConstructor();
/*      */     }
/*      */ 
/*  233 */     if (this.enableToString) {
/*  234 */       writeToStringMethod();
/*      */     }
/*      */ 
/*  238 */     writeAccessMethods();
/*      */ 
/*  241 */     if (this.enableEquals) {
/*  242 */       writeEqualsMethod();
/*      */     }
/*      */ 
/*  245 */     if (this.enableHashCode) {
/*  246 */       writeHashCodeMethod();
/*      */     }
/*      */ 
/*  251 */     if (!this.emitter.isHelperWanted())
/*      */     {
/*  254 */       this.helper.setPrintWriter(pw);
/*      */     }
/*      */ 
/*  257 */     this.helper.generate();
/*      */   }
/*      */ 
/*      */   protected void preprocess()
/*      */   {
/*  272 */     if (this.elements != null)
/*      */     {
/*  275 */       TypeEntry parent = this.extendType;
/*  276 */       while (((!this.parentIsAny) || (!this.parentIsMixed)) && (parent != null)) {
/*  277 */         if (SchemaUtils.isMixed(parent.getNode())) {
/*  278 */           this.parentIsMixed = true;
/*      */         }
/*  280 */         Vector hisElements = parent.getContainedElements();
/*  281 */         for (int i = 0; (hisElements != null) && (i < hisElements.size()); i++) {
/*  282 */           ElementDecl elem = (ElementDecl)hisElements.get(i);
/*  283 */           if (elem.getAnyElement()) {
/*  284 */             this.parentIsAny = true;
/*      */           }
/*      */         }
/*      */ 
/*  288 */         parent = SchemaUtils.getComplexElementExtensionBase(parent.getNode(), this.emitter.getSymbolTable());
/*      */       }
/*      */ 
/*  293 */       for (int i = 0; i < this.elements.size(); i++) {
/*  294 */         ElementDecl elem = (ElementDecl)this.elements.get(i);
/*  295 */         String typeName = elem.getType().getName();
/*  296 */         String variableName = null;
/*      */ 
/*  298 */         if (elem.getAnyElement()) {
/*  299 */           if ((!this.parentIsAny) && (!this.parentIsMixed)) {
/*  300 */             typeName = "org.apache.axis.message.MessageElement []";
/*  301 */             variableName = "_any";
/*      */           }
/*  303 */           this.isAny = true;
/*      */         } else {
/*  305 */           variableName = elem.getName();
/*  306 */           typeName = processTypeName(elem, typeName);
/*      */         }
/*      */ 
/*  309 */         if (variableName == null)
/*      */         {
/*      */           continue;
/*      */         }
/*      */ 
/*  314 */         variableName = JavaUtils.getUniqueValue(this.helper.reservedPropNames, variableName);
/*      */ 
/*  316 */         this.names.add(typeName);
/*  317 */         this.names.add(variableName);
/*      */ 
/*  319 */         if ((this.type.isSimpleType()) && ((variableName.endsWith("Value")) || (variableName.equals("_value"))))
/*      */         {
/*  322 */           this.simpleValueTypes.add(typeName);
/*      */         }
/*      */ 
/*  328 */         if (null == Utils.getEnumerationBaseAndValues(elem.getType().getNode(), this.emitter.getSymbolTable()))
/*      */           continue;
/*  330 */         this.enumerationTypes.add(typeName);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  335 */     if ((this.enableMemberFields) && (SchemaUtils.isMixed(this.type.getNode()))) {
/*  336 */       this.isMixed = true;
/*  337 */       if ((!this.isAny) && (!this.parentIsAny) && (!this.parentIsMixed)) {
/*  338 */         this.names.add("org.apache.axis.message.MessageElement []");
/*  339 */         this.names.add("_any");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  344 */     if (this.attributes != null)
/*      */     {
/*  346 */       for (int i = 0; i < this.attributes.size(); i++) {
/*  347 */         ContainedAttribute attr = (ContainedAttribute)this.attributes.get(i);
/*  348 */         String typeName = attr.getType().getName();
/*  349 */         String variableName = getAttributeName(attr);
/*      */ 
/*  353 */         if (attr.getOptional()) {
/*  354 */           typeName = Utils.getWrapperType(typeName);
/*      */         }
/*      */ 
/*  358 */         variableName = JavaUtils.getUniqueValue(this.helper.reservedPropNames, variableName);
/*      */ 
/*  361 */         this.names.add(typeName);
/*  362 */         this.names.add(variableName);
/*      */ 
/*  364 */         if ((this.type.isSimpleType()) && ((variableName.endsWith("Value")) || (variableName.equals("_value"))))
/*      */         {
/*  367 */           this.simpleValueTypes.add(typeName);
/*      */         }
/*      */ 
/*  373 */         if (null == Utils.getEnumerationBaseAndValues(attr.getType().getNode(), this.emitter.getSymbolTable()))
/*      */           continue;
/*  375 */         this.enumerationTypes.add(typeName);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  380 */     if ((this.extendType != null) && (this.extendType.getDimensions().equals("[]"))) {
/*  381 */       String typeName = this.extendType.getName();
/*  382 */       String elemName = this.extendType.getQName().getLocalPart();
/*  383 */       String variableName = Utils.xmlNameToJava(elemName);
/*      */ 
/*  385 */       this.names.add(typeName);
/*  386 */       this.names.add(variableName);
/*      */     }
/*      */ 
/*  389 */     if ((this.extendType != null) && (Utils.getEnumerationBaseAndValues(this.extendType.getNode(), this.emitter.getSymbolTable()) != null))
/*      */     {
/*  391 */       this.enableDefaultConstructor = false;
/*      */     }
/*      */ 
/*  396 */     for (int i = 1; i < this.names.size(); i += 2)
/*      */     {
/*  398 */       int suffix = 2;
/*  399 */       String s = (String)this.names.elementAt(i);
/*  400 */       if (i >= this.names.size() - 2)
/*      */         continue;
/*  402 */       int dup = this.names.indexOf(s, i + 1);
/*  403 */       while (dup > 0)
/*      */       {
/*  406 */         this.names.set(dup, this.names.get(dup) + Integer.toString(suffix));
/*  407 */         suffix++;
/*      */ 
/*  409 */         if (i >= this.names.size() - 2)
/*      */           break;
/*  411 */         dup = this.names.indexOf(s, dup + 1);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private String getAttributeName(ContainedAttribute attr)
/*      */   {
/*  424 */     String variableName = attr.getName();
/*  425 */     if (variableName == null) {
/*  426 */       variableName = Utils.getLastLocalPart(attr.getQName().getLocalPart());
/*      */     }
/*  428 */     return variableName;
/*      */   }
/*      */ 
/*      */   private String processTypeName(ElementDecl elem, String typeName)
/*      */   {
/*  439 */     if (elem.getAnyElement())
/*  440 */       typeName = "org.apache.axis.message.MessageElement []";
/*  441 */     else if ((elem.getType().getUnderlTypeNillable()) || ((elem.getNillable()) && (elem.getMaxOccursIsUnbounded())))
/*      */     {
/*  448 */       typeName = Utils.getWrapperType(elem.getType());
/*  449 */     } else if (((elem.getMinOccursIs0()) && (elem.getMaxOccursIsExactlyOne())) || (elem.getNillable()) || (elem.getOptional()))
/*      */     {
/*  465 */       typeName = Utils.getWrapperType(typeName);
/*      */     }
/*  467 */     return typeName;
/*      */   }
/*      */ 
/*      */   protected String getBinaryTypeEncoderName(String elementName)
/*      */   {
/*  476 */     TypeEntry type = getElementDecl(elementName);
/*  477 */     if (type != null)
/*      */     {
/*  479 */       String typeName = type.getQName().getLocalPart();
/*      */ 
/*  481 */       if (typeName.equals("base64Binary"))
/*  482 */         return "org.apache.axis.encoding.Base64";
/*  483 */       if (typeName.equals("hexBinary")) {
/*  484 */         return "org.apache.axis.types.HexBinary";
/*      */       }
/*  486 */       throw new RuntimeException("Unknown binary type " + typeName + " for element " + elementName);
/*      */     }
/*      */ 
/*  490 */     throw new RuntimeException("Unknown element " + elementName);
/*      */   }
/*      */ 
/*      */   protected TypeEntry getElementDecl(String elementName)
/*      */   {
/*  498 */     if (this.elements != null) {
/*  499 */       for (int i = 0; i < this.elements.size(); i++) {
/*  500 */         ElementDecl elem = (ElementDecl)this.elements.get(i);
/*      */         String variableName;
/*      */         String variableName;
/*  503 */         if (elem.getAnyElement())
/*  504 */           variableName = "_any";
/*      */         else {
/*  506 */           variableName = elem.getName();
/*      */         }
/*      */ 
/*  509 */         if (variableName.equals(elementName))
/*  510 */           return elem.getType();
/*      */       }
/*      */     }
/*  513 */     return null;
/*      */   }
/*      */ 
/*      */   protected String getClassModifiers()
/*      */   {
/*  523 */     Node node = this.type.getNode();
/*      */ 
/*  525 */     if ((node != null) && 
/*  526 */       (JavaUtils.isTrueExplicitly(Utils.getAttribute(node, "abstract"))))
/*      */     {
/*  528 */       return super.getClassModifiers() + "abstract ";
/*      */     }
/*      */ 
/*  532 */     return super.getClassModifiers();
/*      */   }
/*      */ 
/*      */   protected String getExtendsText()
/*      */   {
/*  543 */     String extendsText = "";
/*      */ 
/*  545 */     if ((this.extendType != null) && (!isUnion()) && ((!this.type.isSimpleType()) || (!this.extendType.isBaseType())) && (this.extendType.getDimensions().length() == 0))
/*      */     {
/*  548 */       extendsText = " extends " + this.extendType.getName() + " ";
/*      */     }
/*      */ 
/*  551 */     return extendsText;
/*      */   }
/*      */ 
/*      */   protected String getImplementsText()
/*      */   {
/*  562 */     String implementsText = " implements java.io.Serializable";
/*      */ 
/*  564 */     if ((this.type.isSimpleType()) && ((isUnion()) || (this.extendType == null) || (this.extendType.isBaseType())))
/*      */     {
/*  567 */       implementsText = implementsText + ", org.apache.axis.encoding.SimpleType";
/*      */     }
/*      */ 
/*  570 */     if (this.isAny) {
/*  571 */       implementsText = implementsText + ", org.apache.axis.encoding.AnyContentType";
/*      */     }
/*      */ 
/*  574 */     if (this.isMixed) {
/*  575 */       implementsText = implementsText + ", org.apache.axis.encoding.MixedContentType";
/*      */     }
/*      */ 
/*  578 */     implementsText = implementsText + " ";
/*      */ 
/*  580 */     return implementsText;
/*      */   }
/*      */ 
/*      */   protected void writeMemberFields()
/*      */   {
/*  589 */     if (isUnion()) {
/*  590 */       this.pw.println("    private java.lang.String _value;");
/*      */ 
/*  592 */       return;
/*      */     }
/*      */ 
/*  595 */     for (int i = 0; i < this.names.size(); i += 2)
/*      */     {
/*  597 */       String comments = "";
/*  598 */       if (this.elements != null)
/*      */       {
/*  600 */         if ((this.elements != null) && (i < this.elements.size() * 2))
/*      */         {
/*  602 */           ElementDecl elem = (ElementDecl)this.elements.get(i / 2);
/*  603 */           comments = elem.getDocumentation();
/*      */         }
/*      */       }
/*      */ 
/*  607 */       String typeName = (String)this.names.get(i);
/*  608 */       String variable = (String)this.names.get(i + 1);
/*      */ 
/*  611 */       if ((comments != null) && (comments.trim().length() > 0))
/*      */       {
/*  613 */         String flatComments = getJavadocDescriptionPart(comments, true).substring(7);
/*      */ 
/*  615 */         this.pw.println("    /* " + flatComments.trim() + " */");
/*      */       }
/*  617 */       this.pw.print("    private " + typeName + " " + variable + ";");
/*      */ 
/*  620 */       if ((this.elements == null) || (i >= this.elements.size() * 2))
/*  621 */         this.pw.println("  // attribute");
/*      */       else {
/*  623 */         this.pw.println();
/*      */       }
/*  625 */       this.pw.println();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeDefaultConstructor()
/*      */   {
/*  635 */     this.pw.println("    public " + this.className + "() {");
/*  636 */     this.pw.println("    }");
/*  637 */     this.pw.println();
/*      */   }
/*      */ 
/*      */   protected void writeMinimalConstructor()
/*      */   {
/*  642 */     if ((isUnion()) || (this.names.size() == 0)) {
/*  643 */       return;
/*      */     }
/*      */ 
/*  646 */     this.pw.println("    public " + this.className + "(");
/*  647 */     for (int i = 0; i < this.names.size(); i += 2) {
/*  648 */       String typeName = (String)this.names.get(i);
/*  649 */       String variable = (String)this.names.get(i + 1);
/*  650 */       this.pw.print("           " + typeName + " " + variable);
/*      */ 
/*  652 */       if (i >= this.names.size() - 2)
/*  653 */         this.pw.println(") {");
/*      */       else {
/*  655 */         this.pw.println(",");
/*      */       }
/*      */     }
/*      */ 
/*  659 */     for (int i = 0; i < this.names.size(); i += 2) {
/*  660 */       String variable = (String)this.names.get(i + 1);
/*  661 */       this.pw.println("           this." + variable + " = " + variable + ";");
/*  662 */       if (i >= this.names.size() - 2) {
/*      */         break;
/*      */       }
/*      */     }
/*  666 */     this.pw.println("    }");
/*  667 */     this.pw.println();
/*      */   }
/*      */ 
/*      */   protected void writeFullConstructor()
/*      */   {
/*  679 */     if (this.type.isSimpleType()) {
/*  680 */       return;
/*      */     }
/*      */ 
/*  684 */     Vector extendList = new Vector();
/*      */ 
/*  686 */     extendList.add(this.type);
/*      */ 
/*  688 */     TypeEntry parent = this.extendType;
/*      */ 
/*  690 */     while (parent != null) {
/*  691 */       if (parent.isSimpleType()) {
/*  692 */         return;
/*      */       }
/*  694 */       extendList.add(parent);
/*      */ 
/*  696 */       parent = SchemaUtils.getComplexElementExtensionBase(parent.getNode(), this.emitter.getSymbolTable());
/*      */     }
/*      */ 
/*  703 */     Vector paramTypes = new Vector();
/*  704 */     Vector paramNames = new Vector();
/*  705 */     boolean gotAny = false;
/*      */ 
/*  707 */     for (int i = extendList.size() - 1; i >= 0; i--) {
/*  708 */       TypeEntry te = (TypeEntry)extendList.elementAt(i);
/*      */ 
/*  712 */       String mangle = "";
/*      */ 
/*  714 */       if (i > 0) {
/*  715 */         mangle = "_" + Utils.xmlNameToJava(te.getQName().getLocalPart()) + "_";
/*      */       }
/*      */ 
/*  721 */       Vector attributes = te.getContainedAttributes();
/*  722 */       if (attributes != null) {
/*  723 */         for (int j = 0; j < attributes.size(); j++) {
/*  724 */           ContainedAttribute attr = (ContainedAttribute)attributes.get(j);
/*      */ 
/*  726 */           String name = getAttributeName(attr);
/*  727 */           String typeName = attr.getType().getName();
/*      */ 
/*  731 */           if (attr.getOptional()) {
/*  732 */             typeName = Utils.getWrapperType(typeName);
/*      */           }
/*      */ 
/*  735 */           paramTypes.add(typeName);
/*  736 */           paramNames.add(JavaUtils.getUniqueValue(this.helper.reservedPropNames, name));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  742 */       Vector elements = te.getContainedElements();
/*      */ 
/*  744 */       if (elements != null) {
/*  745 */         for (int j = 0; j < elements.size(); j++) {
/*  746 */           ElementDecl elem = (ElementDecl)elements.get(j);
/*      */ 
/*  748 */           if (elem.getAnyElement()) {
/*  749 */             if (!gotAny) {
/*  750 */               gotAny = true;
/*  751 */               paramTypes.add("org.apache.axis.message.MessageElement []");
/*  752 */               paramNames.add("_any");
/*      */             }
/*      */           } else {
/*  755 */             paramTypes.add(processTypeName(elem, elem.getType().getName()));
/*  756 */             String name = elem.getName() == null ? "param" + i : elem.getName();
/*  757 */             paramNames.add(JavaUtils.getUniqueValue(this.helper.reservedPropNames, name));
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  764 */     if ((this.isMixed) && (!this.isAny) && (!this.parentIsAny) && (!this.parentIsMixed)) {
/*  765 */       paramTypes.add("org.apache.axis.message.MessageElement []");
/*  766 */       paramNames.add("_any");
/*      */     }
/*      */ 
/*  770 */     int localParams = paramTypes.size() - this.names.size() / 2;
/*      */ 
/*  773 */     if (paramTypes.size() > 0)
/*      */     {
/*  777 */       if (localParams > 0) {
/*  778 */         for (int j = 0; j < localParams; j++) {
/*  779 */           String name = (String)paramNames.elementAt(j);
/*  780 */           if (paramNames.indexOf(name, localParams) != -1) {
/*  781 */             paramNames.set(j, "_" + name);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  786 */       this.pw.println("    public " + this.className + "(");
/*      */ 
/*  788 */       for (int i = 0; i < paramTypes.size(); i++) {
/*  789 */         this.pw.print("           " + paramTypes.elementAt(i) + " " + paramNames.elementAt(i));
/*      */ 
/*  792 */         if (i + 1 < paramTypes.size())
/*  793 */           this.pw.println(",");
/*      */         else {
/*  795 */           this.pw.println(") {");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  800 */       if ((this.extendType != null) && (localParams > 0)) {
/*  801 */         this.pw.println("        super(");
/*      */ 
/*  803 */         for (int j = 0; j < localParams; j++) {
/*  804 */           this.pw.print("            " + paramNames.elementAt(j));
/*      */ 
/*  806 */           if (j + 1 < localParams)
/*  807 */             this.pw.println(",");
/*      */           else {
/*  809 */             this.pw.println(");");
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  815 */       for (int j = localParams; j < paramNames.size(); j++) {
/*  816 */         this.pw.println("        this." + paramNames.elementAt(j) + " = " + paramNames.elementAt(j) + ";");
/*      */       }
/*      */ 
/*  820 */       this.pw.println("    }");
/*  821 */       this.pw.println();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeSimpleConstructors()
/*      */   {
/*  834 */     if (!this.type.isSimpleType()) {
/*  835 */       return;
/*      */     }
/*  837 */     this.pw.println("    // " + Messages.getMessage("needStringCtor"));
/*      */ 
/*  841 */     if (this.simpleValueTypes.size() == 0)
/*      */     {
/*  843 */       if (this.extendType != null)
/*      */       {
/*  846 */         TypeEntry baseType = this.type;
/*      */         while (true)
/*      */         {
/*  849 */           TypeEntry superType = SchemaUtils.getBaseType(baseType, this.emitter.getSymbolTable());
/*      */ 
/*  851 */           if (superType == null) {
/*      */             break;
/*      */           }
/*  854 */           baseType = superType;
/*      */         }
/*      */ 
/*  857 */         String baseJavaType = baseType.getName();
/*      */ 
/*  859 */         this.pw.println("    public " + this.className + "(" + baseJavaType + " _value) {");
/*      */ 
/*  861 */         this.pw.println("        super(_value);");
/*  862 */         this.pw.println("    }");
/*  863 */         this.pw.println();
/*      */       }
/*      */     }
/*  866 */     else if ((isUnion()) || (this.simpleValueTypes.get(0).equals("java.lang.String"))) {
/*  867 */       this.pw.println("    public " + this.className + "(java.lang.String _value) {");
/*      */ 
/*  869 */       this.pw.println("        this._value = _value;");
/*  870 */       this.pw.println("    }");
/*  871 */       int i = 0;
/*  872 */       Iterator iterator = this.simpleValueTypes.iterator();
/*  873 */       while (iterator.hasNext()) {
/*  874 */         String typeName = (String)iterator.next();
/*      */ 
/*  876 */         if (typeName.equals("java.lang.String")) {
/*  877 */           i += 2;
/*  878 */           continue;
/*      */         }
/*      */ 
/*  881 */         String capName = "_value";
/*  882 */         if (isUnion())
/*      */         {
/*  885 */           String name = (String)this.names.get(i + 1);
/*  886 */           capName = Utils.capitalizeFirstChar(name);
/*      */         }
/*      */ 
/*  889 */         this.pw.println("    public " + this.className + "(" + typeName + " _value) {");
/*      */ 
/*  891 */         this.pw.println("        set" + capName + "(_value);");
/*  892 */         this.pw.println("    }");
/*  893 */         this.pw.println();
/*  894 */         i += 2;
/*      */       }
/*  896 */     } else if (this.simpleValueTypes.size() == 1) {
/*  897 */       this.pw.println("    public " + this.className + "(" + this.simpleValueTypes.get(0) + " _value) {");
/*      */ 
/*  899 */       this.pw.println("        this._value = _value;");
/*  900 */       this.pw.println("    }");
/*  901 */       this.pw.println("    public " + this.className + "(java.lang.String _value) {");
/*      */ 
/*  903 */       writeSimpleTypeGetter((String)this.simpleValueTypes.get(0), null, "this._value =");
/*      */ 
/*  905 */       this.pw.println("    }");
/*  906 */       this.pw.println();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeSimpleTypeGetter(String simpleValueType, String name, String returnString)
/*      */   {
/*  921 */     String wrapper = JavaUtils.getWrapper(simpleValueType);
/*      */ 
/*  923 */     if (wrapper != null) {
/*  924 */       this.pw.println("        " + returnString + " new " + wrapper + "(_value)." + simpleValueType + "Value();");
/*      */     }
/*  927 */     else if (simpleValueType.equals("byte[]")) {
/*  928 */       String encoder = getBinaryTypeEncoderName("_value");
/*  929 */       this.pw.println("        " + returnString + " " + encoder + ".decode(_value);");
/*      */     }
/*  931 */     else if (simpleValueType.equals("org.apache.axis.types.URI")) {
/*  932 */       this.pw.println("        try {");
/*  933 */       this.pw.println("            " + returnString + " new org.apache.axis.types.URI(_value);");
/*      */ 
/*  935 */       this.pw.println("        }");
/*  936 */       this.pw.println("        catch (org.apache.axis.types.URI.MalformedURIException mue) {");
/*      */ 
/*  938 */       this.pw.println("            throw new java.lang.RuntimeException(mue.toString());");
/*      */ 
/*  940 */       this.pw.println("       }");
/*  941 */     } else if (simpleValueType.equals("java.util.Date")) {
/*  942 */       this.pw.println("        try {");
/*  943 */       this.pw.println("            " + returnString + " (java.text.DateFormat.getDateTimeInstance()).parse(_value);");
/*      */ 
/*  946 */       this.pw.println("        }");
/*  947 */       this.pw.println("        catch (java.text.ParseException e){");
/*  948 */       this.pw.println("            throw new java.lang.RuntimeException(e.toString());");
/*      */ 
/*  950 */       this.pw.println("        }");
/*  951 */     } else if (simpleValueType.equals("java.util.Calendar")) {
/*  952 */       this.pw.println("        java.util.Calendar cal =");
/*  953 */       this.pw.println("            (java.util.Calendar) new org.apache.axis.encoding.ser.CalendarDeserializer(");
/*      */ 
/*  955 */       this.pw.println("                java.lang.String.class, org.apache.axis.Constants.XSD_STRING).makeValue(_value);");
/*      */ 
/*  957 */       this.pw.println("        " + returnString + " cal;");
/*  958 */     } else if (this.enumerationTypes.contains(simpleValueType))
/*      */     {
/*  962 */       this.pw.println("        " + returnString + " " + simpleValueType + ".fromString(_value);");
/*      */     }
/*      */     else {
/*  965 */       this.pw.println("        " + returnString + " new " + simpleValueType + "(_value);");
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean isUnion()
/*      */   {
/*  977 */     return this.simpleValueTypes.size() > 1;
/*      */   }
/*      */ 
/*      */   protected void writeToStringMethod()
/*      */   {
/*  988 */     if (this.simpleValueTypes.size() == 0) {
/*  989 */       return;
/*      */     }
/*      */ 
/*  992 */     this.pw.println("    // " + Messages.getMessage("needToString"));
/*  993 */     this.pw.println("    public java.lang.String toString() {");
/*      */ 
/*  995 */     if ((isUnion()) || (this.simpleValueTypes.get(0).equals("java.lang.String"))) {
/*  996 */       this.pw.println("        return _value;");
/*      */     } else {
/*  998 */       String wrapper = JavaUtils.getWrapper((String)this.simpleValueTypes.get(0));
/*      */ 
/* 1001 */       if (wrapper != null) {
/* 1002 */         this.pw.println("        return new " + wrapper + "(_value).toString();");
/*      */       }
/*      */       else {
/* 1005 */         String simpleValueType0 = (String)this.simpleValueTypes.get(0);
/* 1006 */         if (simpleValueType0.equals("byte[]")) {
/* 1007 */           String encoder = getBinaryTypeEncoderName("_value");
/* 1008 */           this.pw.println("        return _value == null ? null : " + encoder + ".encode(_value);");
/*      */         }
/* 1011 */         else if (simpleValueType0.equals("java.util.Calendar")) {
/* 1012 */           this.pw.println("        return _value == null ? null : new org.apache.axis.encoding.ser.CalendarSerializer().getValueAsString(_value, null);");
/*      */         }
/*      */         else {
/* 1015 */           this.pw.println("        return _value == null ? null : _value.toString();");
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1021 */     this.pw.println("    }");
/* 1022 */     this.pw.println();
/*      */   }
/*      */ 
/*      */   protected void writeSimpleTypeSetter(String simpleValueType)
/*      */   {
/* 1032 */     String wrapper = JavaUtils.getWrapper(simpleValueType);
/*      */ 
/* 1034 */     if (wrapper != null) {
/* 1035 */       this.pw.println("        this._value = new " + wrapper + "(_value).toString();");
/*      */     }
/* 1038 */     else if (simpleValueType.equals("byte[]")) {
/* 1039 */       String encoder = getBinaryTypeEncoderName("_value");
/* 1040 */       this.pw.println("        this._value = _value == null ? null : " + encoder + ".encode(_value);");
/*      */     }
/* 1043 */     else if (simpleValueType.equals("java.util.Calendar")) {
/* 1044 */       this.pw.println("        this._value = _value == null ? null : new org.apache.axis.encoding.ser.CalendarSerializer().getValueAsString(_value, null);");
/*      */     }
/*      */     else {
/* 1047 */       this.pw.println("        this._value = _value == null ? null : _value.toString();");
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeAccessMethods()
/*      */   {
/* 1058 */     int j = 0;
/*      */ 
/* 1061 */     for (int i = 0; i < this.names.size(); j++) {
/* 1062 */       String typeName = (String)this.names.get(i);
/* 1063 */       String name = (String)this.names.get(i + 1);
/* 1064 */       String capName = Utils.capitalizeFirstChar(name);
/*      */ 
/* 1066 */       String documentation = "";
/* 1067 */       if (this.elements != null)
/*      */       {
/* 1069 */         if ((this.elements != null) && (i < this.elements.size() * 2))
/*      */         {
/* 1071 */           ElementDecl elem = (ElementDecl)this.elements.get(i / 2);
/* 1072 */           documentation = elem.getDocumentation();
/*      */         }
/*      */       }
/*      */ 
/* 1076 */       String get = "get";
/*      */ 
/* 1078 */       if (typeName.equals("boolean")) {
/* 1079 */         get = "is";
/*      */       }
/*      */ 
/* 1082 */       String comment = getJavadocDescriptionPart(documentation, true);
/* 1083 */       if (comment.length() > 3)
/*      */       {
/* 1085 */         comment = comment.substring(2);
/*      */       }
/* 1087 */       if (this.enableGetters) {
/*      */         try {
/* 1089 */           this.pw.println();
/* 1090 */           this.pw.println("    /**");
/* 1091 */           this.pw.println("     * Gets the " + name + " value for this " + getClassName() + ".");
/* 1092 */           this.pw.println("     * ");
/* 1093 */           this.pw.println("     * @return " + name + comment);
/* 1094 */           this.pw.println("     */");
/*      */         }
/*      */         catch (DOMException e) {
/*      */         }
/* 1098 */         this.pw.println("    public " + typeName + " " + get + capName + "() {");
/*      */ 
/* 1101 */         if (isUnion())
/* 1102 */           writeSimpleTypeGetter(typeName, name, "return");
/*      */         else {
/* 1104 */           this.pw.println("        return " + name + ";");
/*      */         }
/*      */ 
/* 1107 */         this.pw.println("    }");
/* 1108 */         this.pw.println();
/*      */       }
/*      */ 
/* 1111 */       if (this.enableSetters)
/*      */       {
/*      */         try {
/* 1114 */           String nm = isUnion() ? "_value" : name;
/* 1115 */           this.pw.println();
/* 1116 */           this.pw.println("    /**");
/* 1117 */           this.pw.println("     * Sets the " + nm + " value for this " + getClassName() + ".");
/* 1118 */           this.pw.println("     * ");
/* 1119 */           this.pw.println("     * @param " + nm + comment);
/* 1120 */           this.pw.println("     */");
/*      */         }
/*      */         catch (DOMException e)
/*      */         {
/*      */         }
/*      */ 
/* 1126 */         if (isUnion()) {
/* 1127 */           this.pw.println("    public void set" + capName + "(" + typeName + " _value) {");
/*      */ 
/* 1129 */           writeSimpleTypeSetter(typeName);
/*      */         } else {
/* 1131 */           this.pw.println("    public void set" + capName + "(" + typeName + " " + name + ") {");
/*      */ 
/* 1133 */           this.pw.println("        this." + name + " = " + name + ";");
/*      */         }
/*      */ 
/* 1136 */         this.pw.println("    }");
/* 1137 */         this.pw.println();
/*      */       }
/*      */ 
/* 1148 */       if ((this.elements != null) && (j < this.elements.size())) {
/* 1149 */         ElementDecl elem = (ElementDecl)this.elements.get(j);
/*      */ 
/* 1151 */         if (elem.getType().getQName().getLocalPart().indexOf("[") > 0) {
/* 1152 */           String compName = typeName.substring(0, typeName.lastIndexOf("["));
/*      */ 
/* 1155 */           if (this.enableGetters) {
/* 1156 */             this.pw.println("    public " + compName + " " + get + capName + "(int i) {");
/*      */ 
/* 1158 */             this.pw.println("        return this." + name + "[i];");
/* 1159 */             this.pw.println("    }");
/* 1160 */             this.pw.println();
/*      */           }
/*      */ 
/* 1163 */           if (this.enableSetters) {
/* 1164 */             this.pw.println("    public void set" + capName + "(int i, " + compName + " _value) {");
/*      */ 
/* 1189 */             this.pw.println("        this." + name + "[i] = _value;");
/* 1190 */             this.pw.println("    }");
/* 1191 */             this.pw.println();
/*      */           }
/*      */         }
/*      */       }
/* 1061 */       i += 2;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeEqualsMethod()
/*      */   {
/* 1205 */     this.pw.println("    private java.lang.Object __equalsCalc = null;");
/* 1206 */     this.pw.println("    public synchronized boolean equals(java.lang.Object obj) {");
/*      */ 
/* 1210 */     this.pw.println("        if (!(obj instanceof " + this.className + ")) return false;");
/*      */ 
/* 1212 */     this.pw.println("        " + this.className + " other = (" + this.className + ") obj;");
/*      */ 
/* 1214 */     this.pw.println("        if (obj == null) return false;");
/* 1215 */     this.pw.println("        if (this == obj) return true;");
/*      */ 
/* 1218 */     this.pw.println("        if (__equalsCalc != null) {");
/* 1219 */     this.pw.println("            return (__equalsCalc == obj);");
/* 1220 */     this.pw.println("        }");
/* 1221 */     this.pw.println("        __equalsCalc = obj;");
/*      */ 
/* 1224 */     String truth = "true";
/*      */ 
/* 1226 */     if ((this.extendType != null) && ((!this.type.isSimpleType()) || (this.simpleValueTypes.size() == 0)))
/*      */     {
/* 1229 */       truth = "super.equals(obj)";
/*      */     }
/*      */ 
/* 1232 */     this.pw.println("        boolean _equals;");
/*      */ 
/* 1234 */     if (this.names.size() == 0) {
/* 1235 */       this.pw.println("        _equals = " + truth + ";");
/* 1236 */     } else if (isUnion()) {
/* 1237 */       this.pw.println("        _equals = " + truth + " && " + " this.toString().equals(obj.toString());");
/*      */     }
/*      */     else {
/* 1240 */       this.pw.println("        _equals = " + truth + " && ");
/*      */ 
/* 1242 */       for (int i = 0; i < this.names.size(); i += 2) {
/* 1243 */         String variableType = (String)this.names.get(i);
/* 1244 */         String variable = (String)this.names.get(i + 1);
/* 1245 */         String get = "get";
/*      */ 
/* 1247 */         if (variableType.equals("boolean")) {
/* 1248 */           get = "is";
/*      */         }
/*      */ 
/* 1251 */         if ((variableType.equals("int")) || (variableType.equals("long")) || (variableType.equals("short")) || (variableType.equals("float")) || (variableType.equals("double")) || (variableType.equals("boolean")) || (variableType.equals("byte")))
/*      */         {
/* 1257 */           this.pw.print("            this." + variable + " == other." + get + Utils.capitalizeFirstChar(variable) + "()");
/*      */         }
/* 1260 */         else if (variableType.indexOf("[") >= 0)
/*      */         {
/* 1263 */           this.pw.println("            ((this." + variable + "==null && other." + get + Utils.capitalizeFirstChar(variable) + "()==null) || ");
/*      */ 
/* 1267 */           this.pw.println("             (this." + variable + "!=null &&");
/* 1268 */           this.pw.print("              java.util.Arrays.equals(this." + variable + ", other." + get + Utils.capitalizeFirstChar(variable) + "())))");
/*      */         }
/*      */         else
/*      */         {
/* 1272 */           this.pw.println("            ((this." + variable + "==null && other." + get + Utils.capitalizeFirstChar(variable) + "()==null) || ");
/*      */ 
/* 1276 */           this.pw.println("             (this." + variable + "!=null &&");
/* 1277 */           this.pw.print("              this." + variable + ".equals(other." + get + Utils.capitalizeFirstChar(variable) + "())))");
/*      */         }
/*      */ 
/* 1282 */         if (i == this.names.size() - 2)
/* 1283 */           this.pw.println(";");
/*      */         else {
/* 1285 */           this.pw.println(" &&");
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1290 */     this.pw.println("        __equalsCalc = null;");
/* 1291 */     this.pw.println("        return _equals;");
/* 1292 */     this.pw.println("    }");
/* 1293 */     this.pw.println("");
/*      */   }
/*      */ 
/*      */   protected void writeHashCodeMethod()
/*      */   {
/* 1303 */     this.pw.println("    private boolean __hashCodeCalc = false;");
/* 1304 */     this.pw.println("    public synchronized int hashCode() {");
/* 1305 */     this.pw.println("        if (__hashCodeCalc) {");
/* 1306 */     this.pw.println("            return 0;");
/* 1307 */     this.pw.println("        }");
/* 1308 */     this.pw.println("        __hashCodeCalc = true;");
/*      */ 
/* 1311 */     String start = "1";
/*      */ 
/* 1313 */     if ((this.extendType != null) && (!this.type.isSimpleType())) {
/* 1314 */       start = "super.hashCode()";
/*      */     }
/*      */ 
/* 1317 */     this.pw.println("        int _hashCode = " + start + ";");
/*      */ 
/* 1319 */     if (isUnion()) {
/* 1320 */       this.pw.println("        if (this._value != null) {");
/* 1321 */       this.pw.println("            _hashCode += this._value.hashCode();");
/* 1322 */       this.pw.println("        }");
/*      */     }
/*      */ 
/* 1325 */     for (int i = 0; (!isUnion()) && (i < this.names.size()); i += 2) {
/* 1326 */       String variableType = (String)this.names.get(i);
/* 1327 */       String variable = (String)this.names.get(i + 1);
/* 1328 */       String get = "get";
/*      */ 
/* 1330 */       if (variableType.equals("boolean")) {
/* 1331 */         get = "is";
/*      */       }
/*      */ 
/* 1334 */       if ((variableType.equals("int")) || (variableType.equals("short")) || (variableType.equals("byte")))
/*      */       {
/* 1336 */         this.pw.println("        _hashCode += " + get + Utils.capitalizeFirstChar(variable) + "();");
/*      */       }
/* 1338 */       else if (variableType.equals("boolean")) {
/* 1339 */         this.pw.println("        _hashCode += (" + get + Utils.capitalizeFirstChar(variable) + "() ? Boolean.TRUE : Boolean.FALSE).hashCode();");
/*      */       }
/* 1342 */       else if (variableType.equals("long")) {
/* 1343 */         this.pw.println("        _hashCode += new Long(" + get + Utils.capitalizeFirstChar(variable) + "()).hashCode();");
/*      */       }
/* 1346 */       else if (variableType.equals("float")) {
/* 1347 */         this.pw.println("        _hashCode += new Float(" + get + Utils.capitalizeFirstChar(variable) + "()).hashCode();");
/*      */       }
/* 1350 */       else if (variableType.equals("double")) {
/* 1351 */         this.pw.println("        _hashCode += new Double(" + get + Utils.capitalizeFirstChar(variable) + "()).hashCode();");
/*      */       }
/* 1354 */       else if (variableType.indexOf("[") >= 0)
/*      */       {
/* 1359 */         this.pw.println("        if (" + get + Utils.capitalizeFirstChar(variable) + "() != null) {");
/*      */ 
/* 1362 */         this.pw.println("            for (int i=0;");
/* 1363 */         this.pw.println("                 i<java.lang.reflect.Array.getLength(" + get + Utils.capitalizeFirstChar(variable) + "());");
/*      */ 
/* 1366 */         this.pw.println("                 i++) {");
/* 1367 */         this.pw.println("                java.lang.Object obj = java.lang.reflect.Array.get(" + get + Utils.capitalizeFirstChar(variable) + "(), i);");
/*      */ 
/* 1370 */         this.pw.println("                if (obj != null &&");
/* 1371 */         this.pw.println("                    !obj.getClass().isArray()) {");
/* 1372 */         this.pw.println("                    _hashCode += obj.hashCode();");
/* 1373 */         this.pw.println("                }");
/* 1374 */         this.pw.println("            }");
/* 1375 */         this.pw.println("        }");
/*      */       } else {
/* 1377 */         this.pw.println("        if (" + get + Utils.capitalizeFirstChar(variable) + "() != null) {");
/*      */ 
/* 1380 */         this.pw.println("            _hashCode += " + get + Utils.capitalizeFirstChar(variable) + "().hashCode();");
/*      */ 
/* 1383 */         this.pw.println("        }");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1388 */     this.pw.println("        __hashCodeCalc = false;");
/* 1389 */     this.pw.println("        return _hashCode;");
/* 1390 */     this.pw.println("    }");
/* 1391 */     this.pw.println("");
/*      */   }
/*      */ 
/*      */   public void generate()
/*      */     throws IOException
/*      */   {
/* 1399 */     String fqcn = getPackage() + "." + getClassName();
/* 1400 */     if ((this.emitter.isDeploy()) && (this.emitter.doesExist(fqcn))) {
/* 1401 */       if (this.emitter.isHelperWanted())
/* 1402 */         this.helper.generate();
/*      */     }
/*      */     else
/* 1405 */       super.generate();
/*      */   }
/*      */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.JavaBeanWriter
 * JD-Core Version:    0.6.0
 */