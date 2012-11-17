/*     */ package org.apache.axis.description;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import javax.xml.namespace.QName;
/*     */ 
/*     */ public class FieldDesc
/*     */   implements Serializable
/*     */ {
/*     */   private String fieldName;
/*     */   private QName xmlName;
/*     */   private QName xmlType;
/*     */   private Class javaType;
/*  40 */   private boolean _isElement = true;
/*     */ 
/*  43 */   private boolean minOccursIs0 = false;
/*     */ 
/*     */   protected FieldDesc(boolean isElement)
/*     */   {
/*  51 */     this._isElement = isElement;
/*     */   }
/*     */ 
/*     */   public String getFieldName()
/*     */   {
/*  58 */     return this.fieldName;
/*     */   }
/*     */ 
/*     */   public void setFieldName(String fieldName)
/*     */   {
/*  65 */     this.fieldName = fieldName;
/*     */   }
/*     */ 
/*     */   public QName getXmlName()
/*     */   {
/*  72 */     return this.xmlName;
/*     */   }
/*     */ 
/*     */   public void setXmlName(QName xmlName)
/*     */   {
/*  79 */     this.xmlName = xmlName;
/*     */   }
/*     */ 
/*     */   public Class getJavaType() {
/*  83 */     return this.javaType;
/*     */   }
/*     */ 
/*     */   public void setJavaType(Class javaType) {
/*  87 */     this.javaType = javaType;
/*     */   }
/*     */ 
/*     */   public QName getXmlType()
/*     */   {
/*  94 */     return this.xmlType;
/*     */   }
/*     */ 
/*     */   public void setXmlType(QName xmlType)
/*     */   {
/* 101 */     this.xmlType = xmlType;
/*     */   }
/*     */ 
/*     */   public boolean isElement()
/*     */   {
/* 110 */     return this._isElement;
/*     */   }
/*     */ 
/*     */   public boolean isIndexed() {
/* 114 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isMinOccursZero()
/*     */   {
/* 121 */     return this.minOccursIs0;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void setMinOccursIs0(boolean minOccursIs0)
/*     */   {
/* 133 */     this.minOccursIs0 = minOccursIs0;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.description.FieldDesc
 * JD-Core Version:    0.6.0
 */