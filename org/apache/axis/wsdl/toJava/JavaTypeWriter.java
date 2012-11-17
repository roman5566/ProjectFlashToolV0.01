/*     */ package org.apache.axis.wsdl.toJava;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Collections;
/*     */ import java.util.Vector;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.wsdl.gen.Generator;
/*     */ import org.apache.axis.wsdl.symbolTable.SchemaUtils;
/*     */ import org.apache.axis.wsdl.symbolTable.SymTabEntry;
/*     */ import org.apache.axis.wsdl.symbolTable.SymbolTable;
/*     */ import org.apache.axis.wsdl.symbolTable.Type;
/*     */ import org.apache.axis.wsdl.symbolTable.TypeEntry;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ public class JavaTypeWriter
/*     */   implements Generator
/*     */ {
/*     */   public static final String HOLDER_IS_NEEDED = "Holder is needed";
/*  41 */   private Generator typeWriter = null;
/*     */ 
/*  44 */   private Generator holderWriter = null;
/*     */ 
/*     */   public JavaTypeWriter(Emitter emitter, TypeEntry type, SymbolTable symbolTable)
/*     */   {
/*  56 */     if ((type.isReferenced()) && (!type.isOnlyLiteralReferenced()))
/*     */     {
/*  60 */       Node node = type.getNode();
/*     */ 
/*  62 */       boolean isSimpleList = SchemaUtils.isListWithItemType(node);
/*     */ 
/*  64 */       if ((!type.getName().endsWith("[]")) && (!isSimpleList))
/*     */       {
/*  67 */         Vector v = Utils.getEnumerationBaseAndValues(node, symbolTable);
/*     */ 
/*  69 */         if (v != null) {
/*  70 */           this.typeWriter = getEnumTypeWriter(emitter, type, v);
/*     */         } else {
/*  72 */           TypeEntry base = SchemaUtils.getComplexElementExtensionBase(node, symbolTable);
/*     */ 
/*  76 */           if (base == null) {
/*  77 */             base = SchemaUtils.getComplexElementRestrictionBase(node, symbolTable);
/*     */           }
/*     */ 
/*  81 */           if (base == null) {
/*  82 */             QName baseQName = SchemaUtils.getSimpleTypeBase(node);
/*     */ 
/*  84 */             if (baseQName != null) {
/*  85 */               base = symbolTable.getType(baseQName);
/*     */             }
/*     */           }
/*     */ 
/*  89 */           this.typeWriter = getBeanWriter(emitter, type, base);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*  95 */       if (holderIsNeeded(type)) {
/*  96 */         this.holderWriter = getHolderWriter(emitter, type);
/*     */       }
/*     */ 
/*  99 */       if ((this.typeWriter != null) && ((type instanceof Type)))
/* 100 */         ((Type)type).setGenerated(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void generate()
/*     */     throws IOException
/*     */   {
/* 112 */     if (this.typeWriter != null) {
/* 113 */       this.typeWriter.generate();
/*     */     }
/*     */ 
/* 116 */     if (this.holderWriter != null)
/* 117 */       this.holderWriter.generate();
/*     */   }
/*     */ 
/*     */   private boolean holderIsNeeded(SymTabEntry entry)
/*     */   {
/* 129 */     Boolean holderIsNeeded = (Boolean)entry.getDynamicVar("Holder is needed");
/*     */ 
/* 132 */     return (holderIsNeeded != null) && (holderIsNeeded.booleanValue());
/*     */   }
/*     */ 
/*     */   protected JavaWriter getEnumTypeWriter(Emitter emitter, TypeEntry type, Vector v)
/*     */   {
/* 145 */     return new JavaEnumTypeWriter(emitter, type, v);
/*     */   }
/*     */ 
/*     */   protected JavaWriter getBeanWriter(Emitter emitter, TypeEntry type, TypeEntry base)
/*     */   {
/* 157 */     Vector elements = type.getContainedElements();
/* 158 */     Vector attributes = type.getContainedAttributes();
/*     */ 
/* 163 */     Boolean isComplexFault = (Boolean)type.getDynamicVar(JavaGeneratorFactory.COMPLEX_TYPE_FAULT);
/*     */ 
/* 166 */     if ((isComplexFault != null) && (isComplexFault.booleanValue()))
/*     */     {
/* 168 */       return new JavaBeanFaultWriter(emitter, type, elements, base, attributes, getBeanHelperWriter(emitter, type, elements, base, attributes, true));
/*     */     }
/*     */ 
/* 174 */     return new JavaBeanWriter(emitter, type, elements, base, attributes, getBeanHelperWriter(emitter, type, elements, base, attributes, false));
/*     */   }
/*     */ 
/*     */   protected JavaWriter getBeanHelperWriter(Emitter emitter, TypeEntry type, Vector elements, TypeEntry base, Vector attributes, boolean forException)
/*     */   {
/* 192 */     return new JavaBeanHelperWriter(emitter, type, elements, base, attributes, forException ? JavaBeanFaultWriter.RESERVED_PROPERTY_NAMES : Collections.EMPTY_SET);
/*     */   }
/*     */ 
/*     */   protected Generator getHolderWriter(Emitter emitter, TypeEntry type)
/*     */   {
/* 206 */     return new JavaHolderWriter(emitter, type);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.toJava.JavaTypeWriter
 * JD-Core Version:    0.6.0
 */