/*     */ package org.apache.axis.wsdl.symbolTable;
/*     */ 
/*     */ import javax.xml.namespace.QName;
/*     */ 
/*     */ public class ElementDecl extends ContainedEntry
/*     */ {
/*     */   private String documentation;
/*  41 */   private boolean minOccursIs0 = false;
/*     */ 
/*  44 */   private boolean nillable = false;
/*     */ 
/*  47 */   private boolean optional = false;
/*     */ 
/*  53 */   private boolean anyElement = false;
/*     */ 
/*  56 */   private boolean maxOccursIsUnbounded = false;
/*     */   private boolean maxOccursExactOne;
/*     */ 
/*     */   public ElementDecl(TypeEntry type, QName name)
/*     */   {
/*  67 */     super(type, name);
/*     */   }
/*     */ 
/*     */   public boolean getMinOccursIs0()
/*     */   {
/*  76 */     return this.minOccursIs0;
/*     */   }
/*     */ 
/*     */   public void setMinOccursIs0(boolean minOccursIs0)
/*     */   {
/*  85 */     this.minOccursIs0 = minOccursIs0;
/*     */   }
/*     */ 
/*     */   public boolean getMaxOccursIsUnbounded()
/*     */   {
/*  94 */     return this.maxOccursIsUnbounded;
/*     */   }
/*     */ 
/*     */   public void setMaxOccursIsUnbounded(boolean maxOccursIsUnbounded)
/*     */   {
/* 103 */     this.maxOccursIsUnbounded = maxOccursIsUnbounded;
/*     */   }
/*     */ 
/*     */   public boolean getMaxOccursIsExactlyOne()
/*     */   {
/* 112 */     return this.maxOccursExactOne;
/*     */   }
/*     */ 
/*     */   public void setMaxOccursIsExactlyOne(boolean exactOne)
/*     */   {
/* 121 */     this.maxOccursExactOne = exactOne;
/*     */   }
/*     */ 
/*     */   public void setNillable(boolean nillable)
/*     */   {
/* 130 */     this.nillable = nillable;
/*     */   }
/*     */ 
/*     */   public boolean getNillable()
/*     */   {
/* 139 */     return this.nillable;
/*     */   }
/*     */ 
/*     */   public void setOptional(boolean optional)
/*     */   {
/* 148 */     this.optional = optional;
/*     */   }
/*     */ 
/*     */   public boolean getOptional()
/*     */   {
/* 157 */     return this.optional;
/*     */   }
/*     */ 
/*     */   public boolean getAnyElement()
/*     */   {
/* 166 */     return this.anyElement;
/*     */   }
/*     */ 
/*     */   public void setAnyElement(boolean anyElement)
/*     */   {
/* 175 */     this.anyElement = anyElement;
/*     */   }
/*     */ 
/*     */   public String getDocumentation()
/*     */   {
/* 184 */     return this.documentation;
/*     */   }
/*     */ 
/*     */   public void setDocumentation(String documentation)
/*     */   {
/* 192 */     this.documentation = documentation;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.ElementDecl
 * JD-Core Version:    0.6.0
 */