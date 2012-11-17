/*     */ package org.apache.axis.description;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.rpc.holders.Holder;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.symbolTable.TypeEntry;
/*     */ 
/*     */ public class ParameterDesc
/*     */   implements Serializable
/*     */ {
/*     */   public static final byte IN = 1;
/*     */   public static final byte OUT = 2;
/*     */   public static final byte INOUT = 3;
/*     */   private transient QName name;
/*     */   public TypeEntry typeEntry;
/*  47 */   private byte mode = 1;
/*     */   private QName typeQName;
/*  51 */   private Class javaType = null;
/*     */ 
/*  53 */   private int order = -1;
/*     */ 
/*  55 */   private boolean isReturn = false;
/*     */ 
/*  57 */   private String mimeType = null;
/*     */   private QName itemQName;
/*     */   private QName itemType;
/*  65 */   private boolean inHeader = false;
/*  66 */   private boolean outHeader = false;
/*     */ 
/*  69 */   private String documentation = null;
/*     */ 
/*  72 */   private boolean omittable = false;
/*     */ 
/*  75 */   private boolean nillable = false;
/*     */ 
/*     */   public ParameterDesc()
/*     */   {
/*     */   }
/*     */ 
/*     */   public ParameterDesc(ParameterDesc copy)
/*     */   {
/*  86 */     this.name = copy.name;
/*  87 */     this.typeEntry = copy.typeEntry;
/*  88 */     this.mode = copy.mode;
/*  89 */     this.typeQName = copy.typeQName;
/*  90 */     this.javaType = copy.javaType;
/*  91 */     this.order = copy.order;
/*  92 */     this.isReturn = copy.isReturn;
/*  93 */     this.mimeType = copy.mimeType;
/*  94 */     this.inHeader = copy.inHeader;
/*  95 */     this.outHeader = copy.outHeader;
/*     */   }
/*     */ 
/*     */   public ParameterDesc(QName name, byte mode, QName typeQName)
/*     */   {
/* 106 */     this.name = name;
/* 107 */     this.mode = mode;
/* 108 */     this.typeQName = typeQName;
/*     */   }
/*     */ 
/*     */   public ParameterDesc(QName name, byte mode, QName typeQName, Class javaType, boolean inHeader, boolean outHeader)
/*     */   {
/* 123 */     this(name, mode, typeQName);
/* 124 */     this.javaType = javaType;
/* 125 */     this.inHeader = inHeader;
/* 126 */     this.outHeader = outHeader;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public ParameterDesc(QName name, byte mode, QName typeQName, Class javaType)
/*     */   {
/* 137 */     this(name, mode, typeQName, javaType, false, false);
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 141 */     return toString("");
/*     */   }
/*     */   public String toString(String indent) {
/* 144 */     String text = "";
/* 145 */     text = text + indent + "name:       " + this.name + "\n";
/* 146 */     text = text + indent + "typeEntry:  " + this.typeEntry + "\n";
/* 147 */     text = text + indent + "mode:       " + (this.mode == 3 ? "INOUT" : this.mode == 1 ? "IN" : "OUT") + "\n";
/*     */ 
/* 150 */     text = text + indent + "position:   " + this.order + "\n";
/* 151 */     text = text + indent + "isReturn:   " + this.isReturn + "\n";
/* 152 */     text = text + indent + "typeQName:  " + this.typeQName + "\n";
/* 153 */     text = text + indent + "javaType:   " + this.javaType + "\n";
/* 154 */     text = text + indent + "inHeader:   " + this.inHeader + "\n";
/* 155 */     text = text + indent + "outHeader:  " + this.outHeader + "\n";
/* 156 */     return text;
/*     */   }
/*     */ 
/*     */   public static byte modeFromString(String modeStr)
/*     */   {
/* 165 */     byte ret = 1;
/* 166 */     if (modeStr == null)
/* 167 */       return 1;
/* 168 */     if (modeStr.equalsIgnoreCase("out"))
/* 169 */       ret = 2;
/* 170 */     else if (modeStr.equalsIgnoreCase("inout")) {
/* 171 */       ret = 3;
/*     */     }
/* 173 */     return ret;
/*     */   }
/*     */ 
/*     */   public static String getModeAsString(byte mode)
/*     */   {
/* 178 */     if (mode == 3)
/* 179 */       return "inout";
/* 180 */     if (mode == 2)
/* 181 */       return "out";
/* 182 */     if (mode == 1) {
/* 183 */       return "in";
/*     */     }
/*     */ 
/* 186 */     throw new IllegalArgumentException(Messages.getMessage("badParameterMode", Byte.toString(mode)));
/*     */   }
/*     */ 
/*     */   public QName getQName()
/*     */   {
/* 191 */     return this.name;
/*     */   }
/*     */ 
/*     */   public String getName() {
/* 195 */     if (this.name == null) {
/* 196 */       return null;
/*     */     }
/*     */ 
/* 199 */     return this.name.getLocalPart();
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/* 204 */     this.name = new QName("", name);
/*     */   }
/*     */ 
/*     */   public void setQName(QName name) {
/* 208 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public QName getTypeQName() {
/* 212 */     return this.typeQName;
/*     */   }
/*     */ 
/*     */   public void setTypeQName(QName typeQName) {
/* 216 */     this.typeQName = typeQName;
/*     */   }
/*     */ 
/*     */   public Class getJavaType()
/*     */   {
/* 224 */     return this.javaType;
/*     */   }
/*     */ 
/*     */   public void setJavaType(Class javaType)
/*     */   {
/* 233 */     if ((javaType != null) && (
/* 234 */       ((this.mode != 1) && (!this.isReturn)) || ((Holder.class.isAssignableFrom(javaType)) || ((this.mode != 1) && (!this.isReturn) && (!Holder.class.isAssignableFrom(javaType))))))
/*     */     {
/* 238 */       throw new IllegalArgumentException(Messages.getMessage("setJavaTypeErr00", javaType.getName(), getModeAsString(this.mode)));
/*     */     }
/*     */ 
/* 245 */     this.javaType = javaType;
/*     */   }
/*     */ 
/*     */   public byte getMode() {
/* 249 */     return this.mode;
/*     */   }
/*     */ 
/*     */   public void setMode(byte mode) {
/* 253 */     this.mode = mode;
/*     */   }
/*     */ 
/*     */   public int getOrder() {
/* 257 */     return this.order;
/*     */   }
/*     */ 
/*     */   public void setOrder(int order) {
/* 261 */     this.order = order;
/*     */   }
/*     */ 
/*     */   public void setInHeader(boolean value) {
/* 265 */     this.inHeader = value;
/*     */   }
/*     */ 
/*     */   public boolean isInHeader() {
/* 269 */     return this.inHeader;
/*     */   }
/*     */ 
/*     */   public void setOutHeader(boolean value) {
/* 273 */     this.outHeader = value;
/*     */   }
/*     */ 
/*     */   public boolean isOutHeader() {
/* 277 */     return this.outHeader;
/*     */   }
/*     */ 
/*     */   public boolean getIsReturn()
/*     */   {
/* 285 */     return this.isReturn;
/*     */   }
/*     */ 
/*     */   public void setIsReturn(boolean value)
/*     */   {
/* 292 */     this.isReturn = value;
/*     */   }
/*     */ 
/*     */   public String getDocumentation()
/*     */   {
/* 299 */     return this.documentation;
/*     */   }
/*     */ 
/*     */   public void setDocumentation(String documentation)
/*     */   {
/* 306 */     this.documentation = documentation;
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream out) throws IOException
/*     */   {
/* 311 */     if (this.name == null) {
/* 312 */       out.writeBoolean(false);
/*     */     } else {
/* 314 */       out.writeBoolean(true);
/* 315 */       out.writeObject(this.name.getNamespaceURI());
/* 316 */       out.writeObject(this.name.getLocalPart());
/*     */     }
/* 318 */     if (this.typeQName == null) {
/* 319 */       out.writeBoolean(false);
/*     */     } else {
/* 321 */       out.writeBoolean(true);
/* 322 */       out.writeObject(this.typeQName.getNamespaceURI());
/* 323 */       out.writeObject(this.typeQName.getLocalPart());
/*     */     }
/* 325 */     out.defaultWriteObject();
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
/*     */   {
/* 330 */     if (in.readBoolean()) {
/* 331 */       this.name = new QName((String)in.readObject(), (String)in.readObject());
/*     */     }
/*     */     else {
/* 334 */       this.name = null;
/*     */     }
/* 336 */     if (in.readBoolean()) {
/* 337 */       this.typeQName = new QName((String)in.readObject(), (String)in.readObject());
/*     */     }
/*     */     else {
/* 340 */       this.typeQName = null;
/*     */     }
/* 342 */     in.defaultReadObject();
/*     */   }
/*     */ 
/*     */   public QName getItemQName() {
/* 346 */     return this.itemQName;
/*     */   }
/*     */ 
/*     */   public void setItemQName(QName itemQName) {
/* 350 */     this.itemQName = itemQName;
/*     */   }
/*     */ 
/*     */   public QName getItemType() {
/* 354 */     return this.itemType;
/*     */   }
/*     */ 
/*     */   public void setItemType(QName itemType) {
/* 358 */     this.itemType = itemType;
/*     */   }
/*     */ 
/*     */   public boolean isOmittable()
/*     */   {
/* 367 */     return this.omittable;
/*     */   }
/*     */ 
/*     */   public void setOmittable(boolean omittable)
/*     */   {
/* 376 */     this.omittable = omittable;
/*     */   }
/*     */ 
/*     */   public boolean isNillable()
/*     */   {
/* 384 */     return this.nillable;
/*     */   }
/*     */ 
/*     */   public void setNillable(boolean nillable)
/*     */   {
/* 392 */     this.nillable = nillable;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.description.ParameterDesc
 * JD-Core Version:    0.6.0
 */