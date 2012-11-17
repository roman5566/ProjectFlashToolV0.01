/*    */ package org.apache.axis.message;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import javax.xml.soap.Name;
/*    */ 
/*    */ public class PrefixedQName
/*    */   implements Name
/*    */ {
/* 24 */   private static final String emptyString = "".intern();
/*    */   private String prefix;
/*    */   private QName qName;
/*    */ 
/*    */   public PrefixedQName(String uri, String localName, String pre)
/*    */   {
/* 30 */     this.qName = new QName(uri, localName);
/* 31 */     this.prefix = (pre == null ? emptyString : pre.intern());
/*    */   }
/*    */ 
/*    */   public PrefixedQName(QName qname)
/*    */   {
/* 37 */     this.qName = qname;
/* 38 */     this.prefix = emptyString;
/*    */   }
/*    */ 
/*    */   public String getLocalName() {
/* 42 */     return this.qName.getLocalPart();
/*    */   }
/*    */ 
/*    */   public String getQualifiedName() {
/* 46 */     StringBuffer buf = new StringBuffer(this.prefix);
/* 47 */     if (this.prefix != emptyString)
/* 48 */       buf.append(':');
/* 49 */     buf.append(this.qName.getLocalPart());
/* 50 */     return buf.toString();
/*    */   }
/*    */ 
/*    */   public String getURI() {
/* 54 */     return this.qName.getNamespaceURI();
/*    */   }
/*    */ 
/*    */   public String getPrefix() {
/* 58 */     return this.prefix;
/*    */   }
/*    */   public boolean equals(Object obj) {
/* 61 */     if (obj == this) {
/* 62 */       return true;
/*    */     }
/* 64 */     if (!(obj instanceof PrefixedQName)) {
/* 65 */       return false;
/*    */     }
/* 67 */     if (!this.qName.equals(((PrefixedQName)obj).qName)) {
/* 68 */       return false;
/*    */     }
/*    */ 
/* 71 */     return this.prefix == ((PrefixedQName)obj).prefix;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 77 */     return this.prefix.hashCode() + this.qName.hashCode();
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 81 */     return this.qName.toString();
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.PrefixedQName
 * JD-Core Version:    0.6.0
 */