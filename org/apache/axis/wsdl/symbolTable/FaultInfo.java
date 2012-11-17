/*     */ package org.apache.axis.wsdl.symbolTable;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import javax.wsdl.Fault;
/*     */ import javax.wsdl.Message;
/*     */ import javax.wsdl.Part;
/*     */ import javax.wsdl.extensions.soap.SOAPHeaderFault;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.constants.Use;
/*     */ import org.apache.axis.utils.Messages;
/*     */ 
/*     */ public class FaultInfo
/*     */ {
/*     */   private Message message;
/*     */   private QName xmlType;
/*     */   private Use use;
/*     */   private QName qName;
/*     */   private String name;
/*     */ 
/*     */   public FaultInfo(Fault fault, Use use, String namespace, SymbolTable symbolTable)
/*     */   {
/*  70 */     this.message = fault.getMessage();
/*  71 */     this.xmlType = getFaultType(symbolTable, getFaultPart());
/*  72 */     this.use = (use != null ? use : Use.LITERAL);
/*  73 */     this.name = fault.getName();
/*     */ 
/*  75 */     Part part = getFaultPart();
/*     */ 
/*  77 */     if (part == null)
/*  78 */       this.qName = null;
/*  79 */     else if (part.getTypeName() != null)
/*  80 */       this.qName = new QName(namespace, part.getName());
/*     */     else
/*  82 */       this.qName = part.getElementName();
/*     */   }
/*     */ 
/*     */   public FaultInfo(SOAPHeaderFault fault, SymbolTable symbolTable)
/*     */     throws IOException
/*     */   {
/*  96 */     MessageEntry mEntry = symbolTable.getMessageEntry(fault.getMessage());
/*     */ 
/*  98 */     if (mEntry == null) {
/*  99 */       throw new IOException(Messages.getMessage("noMsg", fault.getMessage().toString()));
/*     */     }
/*     */ 
/* 103 */     this.message = mEntry.getMessage();
/*     */ 
/* 105 */     Part part = this.message.getPart(fault.getPart());
/*     */ 
/* 107 */     this.xmlType = getFaultType(symbolTable, part);
/* 108 */     this.use = Use.getUse(fault.getUse());
/*     */ 
/* 110 */     if (part == null)
/* 111 */       this.qName = null;
/* 112 */     else if (part.getTypeName() != null)
/* 113 */       this.qName = new QName(fault.getNamespaceURI(), part.getName());
/*     */     else {
/* 115 */       this.qName = part.getElementName();
/*     */     }
/*     */ 
/* 118 */     this.name = this.qName.getLocalPart();
/*     */   }
/*     */ 
/*     */   public FaultInfo(QName faultMessage, String faultPart, String faultUse, String faultNamespaceURI, SymbolTable symbolTable)
/*     */     throws IOException
/*     */   {
/* 135 */     MessageEntry mEntry = symbolTable.getMessageEntry(faultMessage);
/*     */ 
/* 137 */     if (mEntry == null) {
/* 138 */       throw new IOException(Messages.getMessage("noMsg", faultMessage.toString()));
/*     */     }
/*     */ 
/* 142 */     this.message = mEntry.getMessage();
/*     */ 
/* 144 */     Part part = this.message.getPart(faultPart);
/*     */ 
/* 146 */     this.xmlType = getFaultType(symbolTable, part);
/* 147 */     this.use = Use.getUse(faultUse);
/*     */ 
/* 149 */     if (part == null)
/* 150 */       this.qName = null;
/* 151 */     else if (part.getTypeName() != null)
/* 152 */       this.qName = new QName(faultNamespaceURI, part.getName());
/*     */     else {
/* 154 */       this.qName = part.getElementName();
/*     */     }
/*     */ 
/* 157 */     this.name = this.qName.getLocalPart();
/*     */   }
/*     */ 
/*     */   public Message getMessage()
/*     */   {
/* 166 */     return this.message;
/*     */   }
/*     */ 
/*     */   public QName getXMLType()
/*     */   {
/* 175 */     return this.xmlType;
/*     */   }
/*     */ 
/*     */   public Use getUse()
/*     */   {
/* 184 */     return this.use;
/*     */   }
/*     */ 
/*     */   public QName getQName()
/*     */   {
/* 201 */     return this.qName;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 212 */     return this.name;
/*     */   }
/*     */ 
/*     */   private Part getFaultPart()
/*     */   {
/* 224 */     Map parts = this.message.getParts();
/*     */ 
/* 227 */     if (parts.size() == 0) {
/* 228 */       return null;
/*     */     }
/* 230 */     return (Part)parts.values().iterator().next();
/*     */   }
/*     */ 
/*     */   private QName getFaultType(SymbolTable st, Part part)
/*     */   {
/* 245 */     if (part != null) {
/* 246 */       if (part.getTypeName() != null) {
/* 247 */         return part.getTypeName();
/*     */       }
/*     */ 
/* 251 */       TypeEntry entry = st.getElement(part.getElementName());
/*     */ 
/* 253 */       if ((entry != null) && (entry.getRefType() != null)) {
/* 254 */         return entry.getRefType().getQName();
/*     */       }
/*     */     }
/*     */ 
/* 258 */     return null;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.wsdl.symbolTable.FaultInfo
 * JD-Core Version:    0.6.0
 */