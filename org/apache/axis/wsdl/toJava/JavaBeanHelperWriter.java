/*     */ package org.apache.axis.wsdl.toJava;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.symbolTable.CollectionTE;
/*     */ import org.apache.axis.wsdl.symbolTable.ContainedAttribute;
/*     */ import org.apache.axis.wsdl.symbolTable.DefinedElement;
/*     */ import org.apache.axis.wsdl.symbolTable.DefinedType;
/*     */ import org.apache.axis.wsdl.symbolTable.ElementDecl;
/*     */ import org.apache.axis.wsdl.symbolTable.SchemaUtils;
/*     */ import org.apache.axis.wsdl.symbolTable.TypeEntry;
/*     */ 
/*     */ public class JavaBeanHelperWriter extends JavaClassWriter
/*     */ {
/*     */   protected TypeEntry type;
/*     */   protected Vector elements;
/*     */   protected Vector attributes;
/*     */   protected TypeEntry extendType;
/*  52 */   protected PrintWriter wrapperPW = null;
/*     */ 
/*  55 */   protected Vector elementMetaData = null;
/*     */   protected boolean canSearchParents;
/*     */   protected Set reservedPropNames;
/*     */ 
/*     */   protected JavaBeanHelperWriter(Emitter emitter, TypeEntry type, Vector elements, TypeEntry extendType, Vector attributes, Set reservedPropNames)
/*     */   {
/*  76 */     super(emitter, type.getName() + "_Helper", "helper");
/*     */ 
/*  78 */     this.type = type;
/*  79 */     this.elements = elements;
/*  80 */     this.attributes = attributes;
/*  81 */     this.extendType = extendType;
/*  82 */     this.reservedPropNames = reservedPropNames;
/*     */ 
/*  92 */     if ((null != extendType) && (null != SchemaUtils.getComplexElementRestrictionBase(type.getNode(), emitter.getSymbolTable())))
/*     */     {
/*  96 */       this.canSearchParents = false;
/*     */     }
/*  98 */     else this.canSearchParents = true;
/*     */   }
/*     */ 
/*     */   protected void setPrintWriter(PrintWriter pw)
/*     */   {
/* 111 */     this.wrapperPW = pw;
/*     */   }
/*     */ 
/*     */   protected PrintWriter getPrintWriter(String filename)
/*     */     throws IOException
/*     */   {
/* 127 */     return this.wrapperPW == null ? super.getPrintWriter(filename) : this.wrapperPW;
/*     */   }
/*     */ 
/*     */   protected void registerFile(String file)
/*     */   {
/* 140 */     if (this.wrapperPW == null)
/* 141 */       super.registerFile(file);
/*     */   }
/*     */ 
/*     */   protected String verboseMessage(String file)
/*     */   {
/* 154 */     if (this.wrapperPW == null) {
/* 155 */       return super.verboseMessage(file);
/*     */     }
/* 157 */     return null;
/*     */   }
/*     */ 
/*     */   protected void writeFileHeader(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/* 170 */     if (this.wrapperPW == null)
/* 171 */       super.writeFileHeader(pw);
/*     */   }
/*     */ 
/*     */   protected void writeFileBody(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/* 183 */     writeMetaData(pw);
/* 184 */     writeSerializer(pw);
/* 185 */     writeDeserializer(pw);
/*     */   }
/*     */ 
/*     */   protected void writeFileFooter(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/* 197 */     if (this.wrapperPW == null)
/* 198 */       super.writeFileFooter(pw);
/*     */   }
/*     */ 
/*     */   protected void closePrintWriter(PrintWriter pw)
/*     */   {
/* 215 */     if (this.wrapperPW == null)
/* 216 */       pw.close();
/*     */   }
/*     */ 
/*     */   protected void writeMetaData(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/* 229 */     if (this.elements != null) {
/* 230 */       for (int i = 0; i < this.elements.size(); i++) {
/* 231 */         ElementDecl elem = (ElementDecl)this.elements.get(i);
/*     */ 
/* 254 */         if (this.elementMetaData == null) {
/* 255 */           this.elementMetaData = new Vector();
/*     */         }
/*     */ 
/* 258 */         this.elementMetaData.add(elem);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 264 */     pw.println("    // " + Messages.getMessage("typeMeta"));
/* 265 */     pw.println("    private static org.apache.axis.description.TypeDesc typeDesc =");
/*     */ 
/* 267 */     pw.println("        new org.apache.axis.description.TypeDesc(" + Utils.getJavaLocalName(this.type.getName()) + ".class, " + (this.canSearchParents ? "true" : "false") + ");");
/*     */ 
/* 272 */     pw.println();
/* 273 */     pw.println("    static {");
/* 274 */     pw.println("        typeDesc.setXmlType(" + Utils.getNewQName(this.type.getQName()) + ");");
/*     */ 
/* 278 */     if ((this.attributes != null) || (this.elementMetaData != null)) {
/* 279 */       if (this.attributes != null) {
/* 280 */         boolean wroteAttrDecl = false;
/*     */ 
/* 282 */         for (int i = 0; i < this.attributes.size(); i++) {
/* 283 */           ContainedAttribute attr = (ContainedAttribute)this.attributes.get(i);
/* 284 */           TypeEntry te = attr.getType();
/* 285 */           QName attrName = attr.getQName();
/* 286 */           String fieldName = getAsFieldName(attr.getName());
/*     */ 
/* 288 */           QName attrXmlType = te.getQName();
/*     */ 
/* 290 */           pw.print("        ");
/*     */ 
/* 292 */           if (!wroteAttrDecl) {
/* 293 */             pw.print("org.apache.axis.description.AttributeDesc ");
/*     */ 
/* 295 */             wroteAttrDecl = true;
/*     */           }
/*     */ 
/* 298 */           pw.println("attrField = new org.apache.axis.description.AttributeDesc();");
/*     */ 
/* 300 */           pw.println("        attrField.setFieldName(\"" + fieldName + "\");");
/*     */ 
/* 302 */           pw.println("        attrField.setXmlName(" + Utils.getNewQNameWithLastLocalPart(attrName) + ");");
/*     */ 
/* 305 */           if (attrXmlType != null) {
/* 306 */             pw.println("        attrField.setXmlType(" + Utils.getNewQName(attrXmlType) + ");");
/*     */           }
/*     */ 
/* 310 */           pw.println("        typeDesc.addFieldDesc(attrField);");
/*     */         }
/*     */       }
/*     */ 
/* 314 */       if (this.elementMetaData != null) {
/* 315 */         boolean wroteElemDecl = false;
/*     */ 
/* 317 */         for (int i = 0; i < this.elementMetaData.size(); i++) {
/* 318 */           ElementDecl elem = (ElementDecl)this.elementMetaData.elementAt(i);
/*     */ 
/* 321 */           if (elem.getAnyElement())
/*     */           {
/*     */             continue;
/*     */           }
/* 325 */           String fieldName = getAsFieldName(elem.getName());
/* 326 */           QName xmlName = elem.getQName();
/*     */ 
/* 329 */           TypeEntry elemType = elem.getType();
/* 330 */           QName xmlType = null;
/*     */ 
/* 332 */           if ((elemType.getDimensions().length() > 1) && (elemType.getClass() == DefinedType.class))
/*     */           {
/* 338 */             elemType = elemType.getRefType();
/* 339 */           } else if ((elemType.getClass() == DefinedElement.class) && (elemType.getRefType() != null))
/*     */           {
/* 345 */             elemType = elemType.getRefType();
/* 346 */           } else if ((elemType.isSimpleType()) && (elemType.getRefType() != null))
/*     */           {
/* 349 */             elemType = elemType.getRefType();
/*     */           }
/*     */           else
/*     */           {
/* 353 */             while ((elemType instanceof CollectionTE)) {
/* 354 */               elemType = elemType.getRefType();
/*     */             }
/*     */           }
/* 357 */           xmlType = elemType.getQName();
/*     */ 
/* 359 */           pw.print("        ");
/*     */ 
/* 361 */           if (!wroteElemDecl) {
/* 362 */             pw.print("org.apache.axis.description.ElementDesc ");
/*     */ 
/* 364 */             wroteElemDecl = true;
/*     */           }
/*     */ 
/* 367 */           pw.println("elemField = new org.apache.axis.description.ElementDesc();");
/*     */ 
/* 369 */           pw.println("        elemField.setFieldName(\"" + fieldName + "\");");
/*     */ 
/* 371 */           pw.println("        elemField.setXmlName(" + Utils.getNewQNameWithLastLocalPart(xmlName) + ");");
/*     */ 
/* 374 */           if (xmlType != null) {
/* 375 */             pw.println("        elemField.setXmlType(" + Utils.getNewQName(xmlType) + ");");
/*     */           }
/*     */ 
/* 379 */           if (elem.getMinOccursIs0()) {
/* 380 */             pw.println("        elemField.setMinOccurs(0);");
/*     */           }
/* 382 */           if (elem.getNillable())
/* 383 */             pw.println("        elemField.setNillable(true);");
/*     */           else {
/* 385 */             pw.println("        elemField.setNillable(false);");
/*     */           }
/*     */ 
/* 388 */           if (elem.getMaxOccursIsUnbounded()) {
/* 389 */             pw.println("        elemField.setMaxOccursUnbounded(true);");
/*     */           }
/* 391 */           QName itemQName = elem.getType().getItemQName();
/* 392 */           if (itemQName != null) {
/* 393 */             pw.println("        elemField.setItemQName(" + Utils.getNewQName(itemQName) + ");");
/*     */           }
/*     */ 
/* 397 */           pw.println("        typeDesc.addFieldDesc(elemField);");
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 402 */     pw.println("    }");
/* 403 */     pw.println();
/* 404 */     pw.println("    /**");
/* 405 */     pw.println("     * " + Messages.getMessage("returnTypeMeta"));
/* 406 */     pw.println("     */");
/* 407 */     pw.println("    public static org.apache.axis.description.TypeDesc getTypeDesc() {");
/*     */ 
/* 409 */     pw.println("        return typeDesc;");
/* 410 */     pw.println("    }");
/* 411 */     pw.println();
/*     */   }
/*     */ 
/*     */   private String getAsFieldName(String fieldName)
/*     */   {
/* 435 */     if ((fieldName.length() > 1) && (Character.isUpperCase(fieldName.charAt(1))))
/*     */     {
/* 437 */       fieldName = Utils.capitalizeFirstChar(fieldName);
/*     */     }
/*     */ 
/* 441 */     return JavaUtils.getUniqueValue(this.reservedPropNames, fieldName);
/*     */   }
/*     */ 
/*     */   protected void writeSerializer(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/* 453 */     String typeDesc = "typeDesc";
/* 454 */     String ser = " org.apache.axis.encoding.ser.BeanSerializer";
/*     */ 
/* 456 */     if (this.type.isSimpleType()) {
/* 457 */       ser = " org.apache.axis.encoding.ser.SimpleSerializer";
/*     */     }
/*     */ 
/* 460 */     pw.println("    /**");
/* 461 */     pw.println("     * Get Custom Serializer");
/* 462 */     pw.println("     */");
/* 463 */     pw.println("    public static org.apache.axis.encoding.Serializer getSerializer(");
/*     */ 
/* 465 */     pw.println("           java.lang.String mechType, ");
/* 466 */     pw.println("           java.lang.Class _javaType,  ");
/* 467 */     pw.println("           javax.xml.namespace.QName _xmlType) {");
/* 468 */     pw.println("        return ");
/* 469 */     pw.println("          new " + ser + "(");
/* 470 */     pw.println("            _javaType, _xmlType, " + typeDesc + ");");
/* 471 */     pw.println("    }");
/* 472 */     pw.println();
/*     */   }
/*     */ 
/*     */   protected void writeDeserializer(PrintWriter pw)
/*     */     throws IOException
/*     */   {
/* 484 */     String typeDesc = "typeDesc";
/* 485 */     String dser = " org.apache.axis.encoding.ser.BeanDeserializer";
/*     */ 
/* 487 */     if (this.type.isSimpleType()) {
/* 488 */       dser = " org.apache.axis.encoding.ser.SimpleDeserializer";
/*     */     }
/*     */ 
/* 491 */     pw.println("    /**");
/* 492 */     pw.println("     * Get Custom Deserializer");
/* 493 */     pw.println("     */");
/* 494 */     pw.println("    public static org.apache.axis.encoding.Deserializer getDeserializer(");
/*     */ 
/* 496 */     pw.println("           java.lang.String mechType, ");
/* 497 */     pw.println("           java.lang.Class _javaType,  ");
/* 498 */     pw.println("           javax.xml.namespace.QName _xmlType) {");
/* 499 */     pw.println("        return ");
/* 500 */     pw.println("          new " + dser + "(");
/* 501 */     pw.println("            _javaType, _xmlType, " + typeDesc + ");");
/* 502 */     pw.println("    }");
/* 503 */     pw.println();
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.JavaBeanHelperWriter
 * JD-Core Version:    0.6.0
 */