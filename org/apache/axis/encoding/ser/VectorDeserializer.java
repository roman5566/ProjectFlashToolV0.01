/*     */ package org.apache.axis.encoding.ser;
/*     */ 
/*     */ import java.util.Vector;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.encoding.DeserializationContext;
/*     */ import org.apache.axis.encoding.Deserializer;
/*     */ import org.apache.axis.encoding.DeserializerImpl;
/*     */ import org.apache.axis.encoding.DeserializerTarget;
/*     */ import org.apache.axis.message.SOAPHandler;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class VectorDeserializer extends DeserializerImpl
/*     */ {
/*  41 */   protected static Log log = LogFactory.getLog(VectorDeserializer.class.getName());
/*     */ 
/*  44 */   public int curIndex = 0;
/*     */ 
/*     */   public void onStartElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/*  61 */     if (log.isDebugEnabled()) {
/*  62 */       log.debug("Enter: VectorDeserializer::startElement()");
/*     */     }
/*     */ 
/*  65 */     if (context.isNil(attributes)) {
/*  66 */       return;
/*     */     }
/*     */ 
/*  70 */     setValue(new Vector());
/*     */ 
/*  72 */     if (log.isDebugEnabled())
/*  73 */       log.debug("Exit: VectorDeserializer::startElement()");
/*     */   }
/*     */ 
/*     */   public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/*  95 */     if (log.isDebugEnabled()) {
/*  96 */       log.debug("Enter: VectorDeserializer::onStartChild()");
/*     */     }
/*     */ 
/*  99 */     if (attributes == null) {
/* 100 */       throw new SAXException(Messages.getMessage("noType01"));
/*     */     }
/*     */ 
/* 104 */     if (context.isNil(attributes)) {
/* 105 */       setChildValue(null, new Integer(this.curIndex++));
/* 106 */       return null;
/*     */     }
/*     */ 
/* 110 */     QName itemType = context.getTypeFromAttributes(namespace, localName, attributes);
/*     */ 
/* 114 */     Deserializer dSer = null;
/* 115 */     if (itemType != null) {
/* 116 */       dSer = context.getDeserializerForType(itemType);
/*     */     }
/* 118 */     if (dSer == null) {
/* 119 */       dSer = new DeserializerImpl();
/*     */     }
/*     */ 
/* 125 */     dSer.registerValueTarget(new DeserializerTarget(this, new Integer(this.curIndex)));
/* 126 */     this.curIndex += 1;
/*     */ 
/* 128 */     if (log.isDebugEnabled()) {
/* 129 */       log.debug("Exit: VectorDeserializer::onStartChild()");
/*     */     }
/*     */ 
/* 134 */     addChildDeserializer(dSer);
/*     */ 
/* 136 */     return (SOAPHandler)dSer;
/*     */   }
/*     */ 
/*     */   public void setChildValue(Object value, Object hint)
/*     */     throws SAXException
/*     */   {
/* 147 */     if (log.isDebugEnabled()) {
/* 148 */       log.debug(Messages.getMessage("gotValue00", "VectorDeserializer", "" + value));
/*     */     }
/* 150 */     int offset = ((Integer)hint).intValue();
/* 151 */     Vector v = (Vector)this.value;
/*     */ 
/* 154 */     if (offset >= v.size()) {
/* 155 */       v.setSize(offset + 1);
/*     */     }
/* 157 */     v.setElementAt(value, offset);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.VectorDeserializer
 * JD-Core Version:    0.6.0
 */