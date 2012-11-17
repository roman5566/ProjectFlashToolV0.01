/*     */ package org.apache.axis.description;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import javax.xml.namespace.QName;
/*     */ 
/*     */ public class FaultDesc
/*     */   implements Serializable
/*     */ {
/*     */   private String name;
/*     */   private QName qname;
/*     */   private ArrayList parameters;
/*     */   private String className;
/*     */   private QName xmlType;
/*     */   private boolean complex;
/*     */ 
/*     */   public FaultDesc()
/*     */   {
/*     */   }
/*     */ 
/*     */   public FaultDesc(QName qname, String className, QName xmlType, boolean complex)
/*     */   {
/*  48 */     this.qname = qname;
/*  49 */     this.className = className;
/*  50 */     this.xmlType = xmlType;
/*  51 */     this.complex = complex;
/*     */   }
/*     */ 
/*     */   public QName getQName() {
/*  55 */     return this.qname;
/*     */   }
/*     */ 
/*     */   public void setQName(QName name) {
/*  59 */     this.qname = name;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  64 */     return this.name;
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/*  69 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public ArrayList getParameters() {
/*  73 */     return this.parameters;
/*     */   }
/*     */ 
/*     */   public void setParameters(ArrayList parameters) {
/*  77 */     this.parameters = parameters;
/*     */   }
/*     */ 
/*     */   public String getClassName() {
/*  81 */     return this.className;
/*     */   }
/*     */ 
/*     */   public void setClassName(String className) {
/*  85 */     this.className = className;
/*     */   }
/*     */ 
/*     */   public boolean isComplex() {
/*  89 */     return this.complex;
/*     */   }
/*     */ 
/*     */   public void setComplex(boolean complex) {
/*  93 */     this.complex = complex;
/*     */   }
/*     */ 
/*     */   public QName getXmlType() {
/*  97 */     return this.xmlType;
/*     */   }
/*     */ 
/*     */   public void setXmlType(QName xmlType) {
/* 101 */     this.xmlType = xmlType;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 105 */     return toString("");
/*     */   }
/*     */   public String toString(String indent) {
/* 108 */     String text = "";
/* 109 */     text = text + indent + "name: " + getName() + "\n";
/* 110 */     text = text + indent + "qname: " + getQName() + "\n";
/* 111 */     text = text + indent + "type: " + getXmlType() + "\n";
/* 112 */     text = text + indent + "Class: " + getClassName() + "\n";
/* 113 */     for (int i = 0; (this.parameters != null) && (i < this.parameters.size()); i++) {
/* 114 */       text = text + indent + " ParameterDesc[" + i + "]:\n";
/* 115 */       text = text + indent + ((ParameterDesc)this.parameters.get(i)).toString("  ") + "\n";
/*     */     }
/* 117 */     return text;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.description.FaultDesc
 * JD-Core Version:    0.6.0
 */