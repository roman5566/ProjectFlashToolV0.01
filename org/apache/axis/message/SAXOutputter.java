/*     */ package org.apache.axis.message;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.ext.LexicalHandler;
/*     */ import org.xml.sax.helpers.DefaultHandler;
/*     */ 
/*     */ public class SAXOutputter extends DefaultHandler
/*     */   implements LexicalHandler
/*     */ {
/*  32 */   protected static Log log = LogFactory.getLog(SAXOutputter.class.getName());
/*     */   SerializationContext context;
/*  36 */   boolean isCDATA = false;
/*     */ 
/*     */   public SAXOutputter(SerializationContext context)
/*     */   {
/*  40 */     this.context = context;
/*     */   }
/*     */ 
/*     */   public void startDocument() throws SAXException {
/*  44 */     this.context.setSendDecl(true);
/*     */   }
/*     */ 
/*     */   public void endDocument() throws SAXException {
/*  48 */     if (log.isDebugEnabled())
/*  49 */       log.debug("SAXOutputter.endDocument");
/*     */   }
/*     */ 
/*     */   public void startPrefixMapping(String p1, String p2) throws SAXException
/*     */   {
/*  54 */     this.context.registerPrefixForURI(p1, p2);
/*     */   }
/*     */ 
/*     */   public void endPrefixMapping(String p1) throws SAXException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void characters(char[] p1, int p2, int p3) throws SAXException {
/*  62 */     if (log.isDebugEnabled())
/*  63 */       log.debug("SAXOutputter.characters ['" + new String(p1, p2, p3) + "']");
/*     */     try
/*     */     {
/*  66 */       if (!this.isCDATA)
/*  67 */         this.context.writeChars(p1, p2, p3);
/*     */       else
/*  69 */         this.context.writeString(new String(p1, p2, p3));
/*     */     }
/*     */     catch (IOException e) {
/*  72 */       throw new SAXException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void ignorableWhitespace(char[] p1, int p2, int p3) throws SAXException
/*     */   {
/*     */     try
/*     */     {
/*  80 */       this.context.writeChars(p1, p2, p3);
/*     */     } catch (IOException e) {
/*  82 */       throw new SAXException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void skippedEntity(String p1)
/*     */     throws SAXException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void startElement(String namespace, String localName, String qName, Attributes attributes) throws SAXException
/*     */   {
/*  93 */     if (log.isDebugEnabled()) {
/*  94 */       log.debug("SAXOutputter.startElement ['" + namespace + "' " + localName + "]");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*  99 */       this.context.startElement(new QName(namespace, localName), attributes);
/*     */     } catch (IOException e) {
/* 101 */       throw new SAXException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void endElement(String namespace, String localName, String qName)
/*     */     throws SAXException
/*     */   {
/* 108 */     if (log.isDebugEnabled()) {
/* 109 */       log.debug("SAXOutputter.endElement ['" + namespace + "' " + localName + "]");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 114 */       this.context.endElement();
/*     */     } catch (IOException e) {
/* 116 */       throw new SAXException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void startDTD(String name, String publicId, String systemId)
/*     */     throws SAXException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endDTD()
/*     */     throws SAXException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void startEntity(String name)
/*     */     throws SAXException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void endEntity(String name)
/*     */     throws SAXException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void startCDATA()
/*     */     throws SAXException
/*     */   {
/*     */     try
/*     */     {
/* 146 */       this.isCDATA = true;
/* 147 */       this.context.writeString("<![CDATA[");
/*     */     } catch (IOException e) {
/* 149 */       throw new SAXException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void endCDATA() throws SAXException
/*     */   {
/*     */     try
/*     */     {
/* 157 */       this.isCDATA = false;
/* 158 */       this.context.writeString("]]>");
/*     */     } catch (IOException e) {
/* 160 */       throw new SAXException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void comment(char[] ch, int start, int length)
/*     */     throws SAXException
/*     */   {
/* 169 */     if (log.isDebugEnabled())
/* 170 */       log.debug("SAXOutputter.comment ['" + new String(ch, start, length) + "']");
/*     */     try
/*     */     {
/* 173 */       this.context.writeString("<!--");
/* 174 */       this.context.writeChars(ch, start, length);
/* 175 */       this.context.writeString("-->");
/*     */     } catch (IOException e) {
/* 177 */       throw new SAXException(e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.SAXOutputter
 * JD-Core Version:    0.6.0
 */