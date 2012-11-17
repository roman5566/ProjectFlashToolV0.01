/*     */ package org.apache.axis.wsdl.gen;
/*     */ 
/*     */ import javax.wsdl.Binding;
/*     */ import javax.wsdl.Definition;
/*     */ import javax.wsdl.Message;
/*     */ import javax.wsdl.PortType;
/*     */ import javax.wsdl.Service;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.encoding.DefaultSOAPEncodingTypeMappingImpl;
/*     */ import org.apache.axis.encoding.TypeMapping;
/*     */ import org.apache.axis.utils.JavaUtils;
/*     */ import org.apache.axis.wsdl.symbolTable.BaseTypeMapping;
/*     */ import org.apache.axis.wsdl.symbolTable.SymbolTable;
/*     */ import org.apache.axis.wsdl.symbolTable.TypeEntry;
/*     */ 
/*     */ public class NoopFactory
/*     */   implements GeneratorFactory
/*     */ {
/* 115 */   private BaseTypeMapping btm = null;
/*     */ 
/*     */   public void generatorPass(Definition def, SymbolTable symbolTable)
/*     */   {
/*     */   }
/*     */ 
/*     */   public Generator getGenerator(Message message, SymbolTable symbolTable)
/*     */   {
/*  55 */     return new NoopGenerator();
/*     */   }
/*     */ 
/*     */   public Generator getGenerator(PortType portType, SymbolTable symbolTable)
/*     */   {
/*  66 */     return new NoopGenerator();
/*     */   }
/*     */ 
/*     */   public Generator getGenerator(Binding binding, SymbolTable symbolTable)
/*     */   {
/*  77 */     return new NoopGenerator();
/*     */   }
/*     */ 
/*     */   public Generator getGenerator(Service service, SymbolTable symbolTable)
/*     */   {
/*  88 */     return new NoopGenerator();
/*     */   }
/*     */ 
/*     */   public Generator getGenerator(TypeEntry type, SymbolTable symbolTable)
/*     */   {
/*  99 */     return new NoopGenerator();
/*     */   }
/*     */ 
/*     */   public Generator getGenerator(Definition definition, SymbolTable symbolTable)
/*     */   {
/* 111 */     return new NoopGenerator();
/*     */   }
/*     */ 
/*     */   public void setBaseTypeMapping(BaseTypeMapping btm)
/*     */   {
/* 123 */     this.btm = btm;
/*     */   }
/*     */ 
/*     */   public BaseTypeMapping getBaseTypeMapping()
/*     */   {
/* 133 */     if (this.btm == null) {
/* 134 */       this.btm = new BaseTypeMapping()
/*     */       {
/* 136 */         TypeMapping defaultTM = DefaultSOAPEncodingTypeMappingImpl.createWithDelegate();
/*     */ 
/*     */         public String getBaseName(QName qNameIn)
/*     */         {
/* 141 */           QName qName = new QName(qNameIn.getNamespaceURI(), qNameIn.getLocalPart());
/*     */ 
/* 144 */           Class cls = this.defaultTM.getClassForQName(qName);
/*     */ 
/* 147 */           if (cls == null) {
/* 148 */             return null;
/*     */           }
/*     */ 
/* 152 */           return JavaUtils.getTextClassName(cls.getName());
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/* 158 */     return this.btm;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.gen.NoopFactory
 * JD-Core Version:    0.6.0
 */