/*     */ package org.apache.axis.wsdl.symbolTable;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.util.HashSet;
/*     */ import java.util.Vector;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ public abstract class TypeEntry extends SymTabEntry
/*     */   implements Serializable
/*     */ {
/*     */   protected Node node;
/*     */   protected TypeEntry refType;
/*  82 */   protected String dims = "";
/*     */ 
/*  86 */   protected boolean underlTypeNillable = false;
/*     */ 
/*  89 */   protected QName componentType = null;
/*     */ 
/*  94 */   protected QName itemQName = null;
/*     */   protected boolean undefined;
/*     */   protected boolean isBaseType;
/* 110 */   protected boolean isSimpleType = false;
/*     */ 
/* 114 */   protected boolean onlyLiteralReference = false;
/*     */ 
/* 117 */   protected HashSet types = null;
/*     */   protected Vector containedElements;
/*     */   protected Vector containedAttributes;
/*     */ 
/*     */   protected TypeEntry(QName pqName, TypeEntry refType, Node pNode, String dims)
/*     */   {
/* 140 */     super(pqName);
/*     */ 
/* 142 */     this.node = pNode;
/* 143 */     this.undefined = refType.undefined;
/* 144 */     this.refType = refType;
/*     */ 
/* 146 */     if (dims == null) {
/* 147 */       dims = "";
/*     */     }
/*     */ 
/* 150 */     this.dims = dims;
/*     */ 
/* 152 */     if (refType.undefined)
/*     */     {
/* 155 */       TypeEntry uType = refType;
/*     */ 
/* 157 */       while (!(uType instanceof Undefined)) {
/* 158 */         uType = uType.refType;
/*     */       }
/*     */ 
/* 161 */       ((Undefined)uType).register(this);
/*     */     } else {
/* 163 */       this.isBaseType = ((refType.isBaseType) && (refType.dims.equals("")) && (dims.equals("")));
/*     */     }
/*     */   }
/*     */ 
/*     */   protected TypeEntry(QName pqName, Node pNode)
/*     */   {
/* 176 */     super(pqName);
/*     */ 
/* 178 */     this.node = pNode;
/* 179 */     this.refType = null;
/* 180 */     this.undefined = false;
/* 181 */     this.dims = "";
/* 182 */     this.isBaseType = false;
/*     */   }
/*     */ 
/*     */   protected TypeEntry(QName pqName)
/*     */   {
/* 192 */     super(pqName);
/*     */ 
/* 194 */     this.node = null;
/* 195 */     this.undefined = false;
/* 196 */     this.dims = "";
/* 197 */     this.isBaseType = true;
/*     */   }
/*     */ 
/*     */   public Node getNode()
/*     */   {
/* 206 */     return this.node;
/*     */   }
/*     */ 
/*     */   public String getBaseType()
/*     */   {
/* 218 */     if (this.isBaseType) {
/* 219 */       return this.name;
/*     */     }
/* 221 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean isBaseType()
/*     */   {
/* 231 */     return this.isBaseType;
/*     */   }
/*     */ 
/*     */   public void setBaseType(boolean baseType)
/*     */   {
/* 240 */     this.isBaseType = baseType;
/*     */   }
/*     */ 
/*     */   public boolean isSimpleType()
/*     */   {
/* 249 */     return this.isSimpleType;
/*     */   }
/*     */ 
/*     */   public void setSimpleType(boolean simpleType)
/*     */   {
/* 258 */     this.isSimpleType = simpleType;
/*     */   }
/*     */ 
/*     */   public boolean isOnlyLiteralReferenced()
/*     */   {
/* 273 */     return this.onlyLiteralReference;
/*     */   }
/*     */ 
/*     */   public void setOnlyLiteralReference(boolean set)
/*     */   {
/* 282 */     this.onlyLiteralReference = set;
/*     */   }
/*     */ 
/*     */   protected TypeEntry getUndefinedTypeRef()
/*     */   {
/* 292 */     if ((this instanceof Undefined)) {
/* 293 */       return this;
/*     */     }
/*     */ 
/* 296 */     if ((this.undefined) && (this.refType != null) && 
/* 297 */       (this.refType.undefined)) {
/* 298 */       TypeEntry uType = this.refType;
/*     */ 
/* 300 */       while (!(uType instanceof Undefined)) {
/* 301 */         uType = uType.refType;
/*     */       }
/*     */ 
/* 304 */       return uType;
/*     */     }
/*     */ 
/* 308 */     return null;
/*     */   }
/*     */ 
/*     */   protected boolean updateUndefined(TypeEntry oldRef, TypeEntry newRef)
/*     */     throws IOException
/*     */   {
/* 322 */     boolean changedState = false;
/*     */ 
/* 325 */     if (this.refType == oldRef) {
/* 326 */       this.refType = newRef;
/* 327 */       changedState = true;
/*     */ 
/* 330 */       TypeEntry te = this.refType;
/*     */ 
/* 332 */       while ((te != null) && (te != this)) {
/* 333 */         te = te.refType;
/*     */       }
/*     */ 
/* 336 */       if (te == this)
/*     */       {
/* 339 */         this.undefined = false;
/* 340 */         this.isBaseType = false;
/* 341 */         this.node = null;
/*     */ 
/* 343 */         throw new IOException(Messages.getMessage("undefinedloop00", getQName().toString()));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 350 */     if ((this.refType != null) && (this.undefined) && (!this.refType.undefined)) {
/* 351 */       this.undefined = false;
/* 352 */       changedState = true;
/* 353 */       this.isBaseType = ((this.refType.isBaseType) && (this.refType.dims.equals("")) && (this.dims.equals("")));
/*     */     }
/*     */ 
/* 357 */     return changedState;
/*     */   }
/*     */ 
/*     */   public TypeEntry getRefType()
/*     */   {
/* 366 */     return this.refType;
/*     */   }
/*     */ 
/*     */   public void setRefType(TypeEntry refType)
/*     */   {
/* 375 */     this.refType = refType;
/*     */   }
/*     */ 
/*     */   public String getDimensions()
/*     */   {
/* 384 */     return this.dims;
/*     */   }
/*     */ 
/*     */   public boolean getUnderlTypeNillable()
/*     */   {
/* 395 */     if ((!this.underlTypeNillable) && (!getDimensions().equals("")) && (this.refType != null))
/*     */     {
/* 398 */       this.underlTypeNillable = this.refType.getUnderlTypeNillable();
/*     */     }
/* 400 */     return this.underlTypeNillable;
/*     */   }
/*     */ 
/*     */   public void setUnderlTypeNillable(boolean underlTypeNillable)
/*     */   {
/* 407 */     this.underlTypeNillable = underlTypeNillable;
/*     */   }
/*     */ 
/*     */   public QName getComponentType()
/*     */   {
/* 416 */     return this.componentType;
/*     */   }
/*     */ 
/*     */   public void setComponentType(QName componentType)
/*     */   {
/* 424 */     this.componentType = componentType;
/*     */   }
/*     */ 
/*     */   public QName getItemQName() {
/* 428 */     return this.itemQName;
/*     */   }
/*     */ 
/*     */   public void setItemQName(QName itemQName) {
/* 432 */     this.itemQName = itemQName;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 441 */     return toString("");
/*     */   }
/*     */ 
/*     */   protected String toString(String indent)
/*     */   {
/* 452 */     String refString = indent + "RefType:       null \n";
/*     */ 
/* 454 */     if (this.refType != null) {
/* 455 */       refString = indent + "RefType:\n" + this.refType.toString(new StringBuffer().append(indent).append("  ").toString()) + "\n";
/*     */     }
/*     */ 
/* 459 */     return super.toString(indent) + indent + "Class:         " + getClass().getName() + "\n" + indent + "Base?:         " + this.isBaseType + "\n" + indent + "Undefined?:    " + this.undefined + "\n" + indent + "isSimpleType?  " + this.isSimpleType + "\n" + indent + "Node:          " + getNode() + "\n" + indent + "Dims:          " + this.dims + "\n" + indent + "isOnlyLiteralReferenced: " + this.onlyLiteralReference + "\n" + refString;
/*     */   }
/*     */ 
/*     */   public HashSet getNestedTypes(SymbolTable symbolTable, boolean derivedFlag)
/*     */   {
/* 483 */     if (this.types == null) {
/* 484 */       this.types = Utils.getNestedTypes(this, symbolTable, derivedFlag);
/*     */     }
/* 486 */     return this.types;
/*     */   }
/*     */ 
/*     */   public Vector getContainedAttributes()
/*     */   {
/* 493 */     return this.containedAttributes;
/*     */   }
/*     */ 
/*     */   public void setContainedAttributes(Vector containedAttributes)
/*     */   {
/* 499 */     this.containedAttributes = containedAttributes;
/*     */   }
/*     */ 
/*     */   public Vector getContainedElements()
/*     */   {
/* 505 */     return this.containedElements;
/*     */   }
/*     */ 
/*     */   public void setContainedElements(Vector containedElements)
/*     */   {
/* 511 */     this.containedElements = containedElements;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.TypeEntry
 * JD-Core Version:    0.6.0
 */