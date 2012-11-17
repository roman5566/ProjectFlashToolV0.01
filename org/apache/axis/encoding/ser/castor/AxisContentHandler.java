/*     */ package org.apache.axis.encoding.ser.castor;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.helpers.DefaultHandler;
/*     */ 
/*     */ public class AxisContentHandler extends DefaultHandler
/*     */ {
/*     */   private SerializationContext context;
/*     */ 
/*     */   public AxisContentHandler(SerializationContext context)
/*     */   {
/*  83 */     setContext(context);
/*     */   }
/*     */ 
/*     */   public SerializationContext getContext()
/*     */   {
/*  92 */     return this.context;
/*     */   }
/*     */ 
/*     */   public void setContext(SerializationContext context)
/*     */   {
/* 101 */     this.context = context;
/*     */   }
/*     */ 
/*     */   public void startElement(String uri, String localName, String qName, Attributes attributes)
/*     */     throws SAXException
/*     */   {
/*     */     try
/*     */     {
/* 110 */       this.context.startElement(new QName(uri, localName), attributes);
/*     */     } catch (IOException ioe) {
/* 112 */       throw new SAXException(ioe);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void endElement(String uri, String localName, String qName)
/*     */     throws SAXException
/*     */   {
/*     */     try
/*     */     {
/* 122 */       this.context.endElement();
/*     */     } catch (IOException ioe) {
/* 124 */       throw new SAXException(ioe);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void characters(char[] ch, int start, int length)
/*     */     throws SAXException
/*     */   {
/*     */     try
/*     */     {
/* 134 */       this.context.writeChars(ch, start, length);
/*     */     } catch (IOException ioe) {
/* 136 */       throw new SAXException(ioe);
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.castor.AxisContentHandler
 * JD-Core Version:    0.6.0
 */