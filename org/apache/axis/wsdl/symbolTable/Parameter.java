/*     */ package org.apache.axis.wsdl.symbolTable;
/*     */ 
/*     */ import javax.xml.namespace.QName;
/*     */ 
/*     */ public class Parameter
/*     */ {
/*     */   public static final byte IN = 1;
/*     */   public static final byte OUT = 2;
/*     */   public static final byte INOUT = 3;
/*     */   private QName qname;
/*     */   private String name;
/*  50 */   private MimeInfo mimeInfo = null;
/*     */   private TypeEntry type;
/*  56 */   private byte mode = 1;
/*     */ 
/*  62 */   private boolean inHeader = false;
/*     */ 
/*  65 */   private boolean outHeader = false;
/*     */ 
/*  68 */   private boolean omittable = false;
/*     */ 
/*  71 */   private boolean nillable = false;
/*     */ 
/*     */   public String toString()
/*     */   {
/*  80 */     return "(" + this.type + (this.mimeInfo == null ? "" : new StringBuffer().append("(").append(this.mimeInfo).append(")").toString()) + ", " + getName() + ", " + (this.mode == 3 ? "INOUT)" : this.mode == 1 ? "IN)" : new StringBuffer().append("OUT)").append(this.inHeader ? "(IN soap:header)" : "").append(this.outHeader ? "(OUT soap:header)" : "").toString());
/*     */   }
/*     */ 
/*     */   public QName getQName()
/*     */   {
/* 100 */     return this.qname;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 111 */     if ((this.name == null) && (this.qname != null)) {
/* 112 */       return this.qname.getLocalPart();
/*     */     }
/*     */ 
/* 115 */     return this.name;
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/* 126 */     this.name = name;
/*     */ 
/* 128 */     if (this.qname == null)
/* 129 */       this.qname = new QName("", name);
/*     */   }
/*     */ 
/*     */   public void setQName(QName qname)
/*     */   {
/* 139 */     this.qname = qname;
/*     */   }
/*     */ 
/*     */   public MimeInfo getMIMEInfo()
/*     */   {
/* 148 */     return this.mimeInfo;
/*     */   }
/*     */ 
/*     */   public void setMIMEInfo(MimeInfo mimeInfo)
/*     */   {
/* 157 */     this.mimeInfo = mimeInfo;
/*     */   }
/*     */ 
/*     */   public TypeEntry getType()
/*     */   {
/* 166 */     return this.type;
/*     */   }
/*     */ 
/*     */   public void setType(TypeEntry type)
/*     */   {
/* 175 */     this.type = type;
/*     */   }
/*     */ 
/*     */   public byte getMode()
/*     */   {
/* 184 */     return this.mode;
/*     */   }
/*     */ 
/*     */   public void setMode(byte mode)
/*     */   {
/* 196 */     if ((mode <= 3) && (mode >= 1))
/* 197 */       this.mode = mode;
/*     */   }
/*     */ 
/*     */   public boolean isInHeader()
/*     */   {
/* 207 */     return this.inHeader;
/*     */   }
/*     */ 
/*     */   public void setInHeader(boolean inHeader)
/*     */   {
/* 216 */     this.inHeader = inHeader;
/*     */   }
/*     */ 
/*     */   public boolean isOutHeader()
/*     */   {
/* 225 */     return this.outHeader;
/*     */   }
/*     */ 
/*     */   public void setOutHeader(boolean outHeader)
/*     */   {
/* 234 */     this.outHeader = outHeader;
/*     */   }
/*     */ 
/*     */   public boolean isOmittable() {
/* 238 */     return this.omittable;
/*     */   }
/*     */ 
/*     */   public void setOmittable(boolean omittable) {
/* 242 */     this.omittable = omittable;
/*     */   }
/*     */ 
/*     */   public boolean isNillable()
/*     */   {
/* 250 */     return this.nillable;
/*     */   }
/*     */ 
/*     */   public void setNillable(boolean nillable)
/*     */   {
/* 258 */     this.nillable = nillable;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.Parameter
 * JD-Core Version:    0.6.0
 */