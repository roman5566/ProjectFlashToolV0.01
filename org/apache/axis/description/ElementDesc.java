/*     */ package org.apache.axis.description;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import javax.xml.namespace.QName;
/*     */ 
/*     */ public class ElementDesc extends FieldDesc
/*     */   implements Serializable
/*     */ {
/*  30 */   private int minOccurs = 1;
/*     */ 
/*  32 */   private int maxOccurs = 1;
/*     */ 
/*  35 */   private boolean nillable = false;
/*     */ 
/*  38 */   private boolean unbounded = false;
/*     */   private QName arrayType;
/*     */   private QName itemQName;
/*     */ 
/*     */   public ElementDesc()
/*     */   {
/*  46 */     super(true);
/*     */   }
/*     */ 
/*     */   public boolean isMinOccursZero() {
/*  50 */     return this.minOccurs == 0;
/*     */   }
/*     */ 
/*     */   public int getMinOccurs() {
/*  54 */     return this.minOccurs;
/*     */   }
/*     */ 
/*     */   public void setMinOccurs(int minOccurs) {
/*  58 */     this.minOccurs = minOccurs;
/*     */   }
/*     */ 
/*     */   public int getMaxOccurs() {
/*  62 */     return this.maxOccurs;
/*     */   }
/*     */ 
/*     */   public void setMaxOccurs(int maxOccurs) {
/*  66 */     this.maxOccurs = maxOccurs;
/*     */   }
/*     */ 
/*     */   public void setMaxOccursUnbounded(boolean ubnd) {
/*  70 */     this.unbounded = ubnd;
/*     */   }
/*     */ 
/*     */   public boolean isMaxOccursUnbounded() {
/*  74 */     return this.unbounded;
/*     */   }
/*     */ 
/*     */   public boolean isNillable()
/*     */   {
/*  83 */     return this.nillable;
/*     */   }
/*     */ 
/*     */   public void setNillable(boolean nillable)
/*     */   {
/*  92 */     this.nillable = nillable;
/*     */   }
/*     */ 
/*     */   public QName getArrayType() {
/*  96 */     return this.arrayType;
/*     */   }
/*     */ 
/*     */   public void setArrayType(QName arrayType) {
/* 100 */     this.arrayType = arrayType;
/*     */   }
/*     */ 
/*     */   public QName getItemQName() {
/* 104 */     return this.itemQName;
/*     */   }
/*     */ 
/*     */   public void setItemQName(QName itemQName) {
/* 108 */     this.itemQName = itemQName;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.description.ElementDesc
 * JD-Core Version:    0.6.0
 */