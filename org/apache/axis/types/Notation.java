/*     */ package org.apache.axis.types;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.description.AttributeDesc;
/*     */ import org.apache.axis.description.ElementDesc;
/*     */ import org.apache.axis.description.FieldDesc;
/*     */ import org.apache.axis.description.TypeDesc;
/*     */ 
/*     */ public class Notation
/*     */   implements Serializable
/*     */ {
/*     */   NCName name;
/*     */   URI publicURI;
/*     */   URI systemURI;
/* 124 */   private static TypeDesc typeDesc = new TypeDesc(Notation.class);
/*     */ 
/*     */   public Notation()
/*     */   {
/*     */   }
/*     */ 
/*     */   public Notation(NCName name, URI publicURI, URI systemURI)
/*     */   {
/*  40 */     this.name = name;
/*  41 */     this.publicURI = publicURI;
/*  42 */     this.systemURI = systemURI;
/*     */   }
/*     */ 
/*     */   public NCName getName() {
/*  46 */     return this.name;
/*     */   }
/*     */ 
/*     */   public void setName(NCName name) {
/*  50 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public URI getPublic() {
/*  54 */     return this.publicURI;
/*     */   }
/*     */ 
/*     */   public void setPublic(URI publicURI) {
/*  58 */     this.publicURI = publicURI;
/*     */   }
/*     */ 
/*     */   public URI getSystem() {
/*  62 */     return this.systemURI;
/*     */   }
/*     */ 
/*     */   public void setSystem(URI systemURI) {
/*  66 */     this.systemURI = systemURI;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj) {
/*  70 */     if ((obj == null) || (!(obj instanceof Notation)))
/*  71 */       return false;
/*  72 */     Notation other = (Notation)obj;
/*  73 */     if (this.name == null) {
/*  74 */       if (other.getName() != null)
/*  75 */         return false;
/*     */     }
/*  77 */     else if (!this.name.equals(other.getName())) {
/*  78 */       return false;
/*     */     }
/*  80 */     if (this.publicURI == null) {
/*  81 */       if (other.getPublic() != null)
/*  82 */         return false;
/*     */     }
/*  84 */     else if (!this.publicURI.equals(other.getPublic())) {
/*  85 */       return false;
/*     */     }
/*  87 */     if (this.systemURI == null) {
/*  88 */       if (other.getSystem() != null)
/*  89 */         return false;
/*     */     }
/*  91 */     else if (!this.systemURI.equals(other.getSystem())) {
/*  92 */       return false;
/*     */     }
/*  94 */     return true;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 106 */     int hash = 0;
/* 107 */     if (null != this.name) {
/* 108 */       hash += this.name.hashCode();
/*     */     }
/* 110 */     if (null != this.publicURI) {
/* 111 */       hash += this.publicURI.hashCode();
/*     */     }
/* 113 */     if (null != this.systemURI) {
/* 114 */       hash += this.systemURI.hashCode();
/*     */     }
/* 116 */     return hash;
/*     */   }
/*     */ 
/*     */   public static TypeDesc getTypeDesc()
/*     */   {
/* 151 */     return typeDesc;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 128 */     FieldDesc field = new AttributeDesc();
/* 129 */     field.setFieldName("name");
/* 130 */     field.setXmlName(Constants.XSD_NCNAME);
/* 131 */     typeDesc.addFieldDesc(field);
/*     */ 
/* 134 */     field = new AttributeDesc();
/* 135 */     field.setFieldName("public");
/* 136 */     field.setXmlName(Constants.XSD_ANYURI);
/* 137 */     typeDesc.addFieldDesc(field);
/*     */ 
/* 140 */     ElementDesc element = null;
/* 141 */     element = new ElementDesc();
/* 142 */     element.setFieldName("system");
/* 143 */     element.setXmlName(Constants.XSD_ANYURI);
/*     */ 
/* 146 */     element.setNillable(true);
/* 147 */     typeDesc.addFieldDesc(field);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.types.Notation
 * JD-Core Version:    0.6.0
 */