/*    */ package org.apache.axis.utils;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class Mapping
/*    */   implements Serializable
/*    */ {
/*    */   private String namespaceURI;
/*    */   private String prefix;
/*    */ 
/*    */   public Mapping(String namespaceURI, String prefix)
/*    */   {
/* 29 */     setPrefix(prefix);
/* 30 */     setNamespaceURI(namespaceURI);
/*    */   }
/*    */ 
/*    */   public String getNamespaceURI() {
/* 34 */     return this.namespaceURI;
/*    */   }
/*    */ 
/*    */   public void setNamespaceURI(String namespaceURI) {
/* 38 */     this.namespaceURI = namespaceURI.intern();
/*    */   }
/*    */ 
/*    */   public String getPrefix() {
/* 42 */     return this.prefix;
/*    */   }
/*    */ 
/*    */   public void setPrefix(String prefix) {
/* 46 */     this.prefix = prefix.intern();
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.Mapping
 * JD-Core Version:    0.6.0
 */