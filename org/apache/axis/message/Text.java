/*     */ package org.apache.axis.message;
/*     */ 
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.apache.axis.InternalException;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.w3c.dom.CharacterData;
/*     */ import org.w3c.dom.DOMException;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ 
/*     */ public class Text extends NodeImpl
/*     */   implements javax.xml.soap.Text
/*     */ {
/*     */   public Text(CharacterData data)
/*     */   {
/*  33 */     if (data == null)
/*     */     {
/*  35 */       throw new IllegalArgumentException("Text value may not be null.");
/*     */     }
/*  37 */     this.textRep = data;
/*     */   }
/*     */ 
/*     */   public Text(String s) {
/*     */     try {
/*  42 */       Document doc = XMLUtils.newDocument();
/*  43 */       this.textRep = doc.createTextNode(s);
/*     */     } catch (ParserConfigurationException e) {
/*  45 */       throw new InternalException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Text()
/*     */   {
/*  51 */     this((String)null);
/*     */   }
/*     */ 
/*     */   public boolean isComment()
/*     */   {
/*  61 */     String temp = this.textRep.getNodeValue().trim();
/*     */ 
/*  63 */     return (temp.startsWith("<!--")) && (temp.endsWith("-->"));
/*     */   }
/*     */ 
/*     */   public String getNodeValue()
/*     */     throws DOMException
/*     */   {
/*  74 */     return this.textRep.getNodeValue();
/*     */   }
/*     */ 
/*     */   public void setNodeValue(String nodeValue) throws DOMException
/*     */   {
/*  79 */     setDirty();
/*  80 */     this.textRep.setNodeValue(nodeValue);
/*     */   }
/*     */ 
/*     */   public org.w3c.dom.Text splitText(int offset)
/*     */     throws DOMException
/*     */   {
/*  97 */     int length = this.textRep.getLength();
/*     */ 
/* 100 */     String tailData = this.textRep.substringData(offset, length);
/* 101 */     this.textRep.deleteData(offset, length);
/*     */ 
/* 104 */     Text tailText = new Text(tailData);
/* 105 */     Node myParent = getParentNode();
/* 106 */     if (myParent != null) {
/* 107 */       NodeList brothers = myParent.getChildNodes();
/* 108 */       for (int i = 0; i < brothers.getLength(); i++) {
/* 109 */         if (brothers.item(i).equals(this)) {
/* 110 */           myParent.insertBefore(tailText, this);
/* 111 */           return tailText;
/*     */         }
/*     */       }
/*     */     }
/* 115 */     return tailText;
/*     */   }
/*     */ 
/*     */   public String getData()
/*     */     throws DOMException
/*     */   {
/* 122 */     return this.textRep.getData();
/*     */   }
/*     */ 
/*     */   public void setData(String data)
/*     */     throws DOMException
/*     */   {
/* 129 */     this.textRep.setData(data);
/*     */   }
/*     */ 
/*     */   public int getLength()
/*     */   {
/* 138 */     return this.textRep.getLength();
/*     */   }
/*     */ 
/*     */   public String substringData(int offset, int count)
/*     */     throws DOMException
/*     */   {
/* 149 */     return this.textRep.substringData(offset, count);
/*     */   }
/*     */ 
/*     */   public void appendData(String arg)
/*     */     throws DOMException
/*     */   {
/* 159 */     this.textRep.appendData(arg);
/*     */   }
/*     */ 
/*     */   public void insertData(int offset, String arg)
/*     */     throws DOMException
/*     */   {
/* 169 */     this.textRep.insertData(offset, arg);
/*     */   }
/*     */ 
/*     */   public void replaceData(int offset, int count, String arg)
/*     */     throws DOMException
/*     */   {
/* 180 */     this.textRep.replaceData(offset, count, arg);
/*     */   }
/*     */ 
/*     */   public void deleteData(int offset, int count)
/*     */     throws DOMException
/*     */   {
/* 190 */     this.textRep.deleteData(offset, count);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 195 */     return this.textRep.getNodeValue();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 200 */     if (!(obj instanceof Text))
/*     */     {
/* 202 */       return false;
/*     */     }
/* 204 */     return (this == obj) || (hashCode() == obj.hashCode());
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 209 */     if (this.textRep == null)
/*     */     {
/* 211 */       return -1;
/*     */     }
/* 213 */     return this.textRep.getData() != null ? this.textRep.getData().hashCode() : 0;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.Text
 * JD-Core Version:    0.6.0
 */