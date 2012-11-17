/*     */ package org.apache.axis.encoding.ser;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ public class MapDeserializer extends DeserializerImpl
/*     */ {
/*  44 */   protected static Log log = LogFactory.getLog(MapDeserializer.class.getName());
/*     */ 
/*  48 */   public static final Object KEYHINT = new Object();
/*  49 */   public static final Object VALHINT = new Object();
/*  50 */   public static final Object NILHINT = new Object();
/*     */ 
/*     */   public void onStartElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/*  68 */     if (log.isDebugEnabled()) {
/*  69 */       log.debug("Enter MapDeserializer::startElement()");
/*     */     }
/*     */ 
/*  72 */     if (context.isNil(attributes)) {
/*  73 */       return;
/*     */     }
/*     */ 
/*  77 */     setValue(new HashMap());
/*     */ 
/*  79 */     if (log.isDebugEnabled())
/*  80 */       log.debug("Exit: MapDeserializer::startElement()");
/*     */   }
/*     */ 
/*     */   public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 103 */     if (log.isDebugEnabled()) {
/* 104 */       log.debug("Enter: MapDeserializer::onStartChild()");
/*     */     }
/*     */ 
/* 107 */     if (localName.equals("item")) {
/* 108 */       ItemHandler handler = new ItemHandler(this);
/*     */ 
/* 111 */       addChildDeserializer(handler);
/*     */ 
/* 113 */       if (log.isDebugEnabled()) {
/* 114 */         log.debug("Exit: MapDeserializer::onStartChild()");
/*     */       }
/*     */ 
/* 117 */       return handler;
/*     */     }
/*     */ 
/* 120 */     return this;
/*     */   }
/*     */ 
/*     */   public void setChildValue(Object value, Object hint)
/*     */     throws SAXException
/*     */   {
/* 131 */     if (log.isDebugEnabled()) {
/* 132 */       log.debug(Messages.getMessage("gotValue00", "MapDeserializer", "" + value));
/*     */     }
/* 134 */     ((Map)this.value).put(hint, value);
/*     */   }
/*     */ 
/*     */   class ItemHandler extends DeserializerImpl
/*     */   {
/*     */     Object key;
/*     */     Object myValue;
/* 146 */     int numSet = 0;
/* 147 */     MapDeserializer md = null;
/*     */ 
/*     */     ItemHandler(MapDeserializer md) {
/* 150 */       this.md = md;
/*     */     }
/*     */ 
/*     */     public void setChildValue(Object val, Object hint)
/*     */       throws SAXException
/*     */     {
/* 159 */       if (hint == MapDeserializer.KEYHINT)
/* 160 */         this.key = val;
/* 161 */       else if (hint == MapDeserializer.VALHINT)
/* 162 */         this.myValue = val;
/* 163 */       else if (hint != MapDeserializer.NILHINT) {
/* 164 */         return;
/*     */       }
/* 166 */       this.numSet += 1;
/* 167 */       if (this.numSet == 2)
/* 168 */         this.md.setChildValue(this.myValue, this.key);
/*     */     }
/*     */ 
/*     */     public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */       throws SAXException
/*     */     {
/* 178 */       QName typeQName = context.getTypeFromAttributes(namespace, localName, attributes);
/*     */ 
/* 181 */       Deserializer dser = context.getDeserializerForType(typeQName);
/*     */ 
/* 184 */       if (dser == null) {
/* 185 */         dser = new DeserializerImpl();
/*     */       }
/*     */ 
/* 191 */       DeserializerTarget dt = null;
/* 192 */       if (context.isNil(attributes))
/* 193 */         dt = new DeserializerTarget(this, MapDeserializer.NILHINT);
/* 194 */       else if (localName.equals("key"))
/* 195 */         dt = new DeserializerTarget(this, MapDeserializer.KEYHINT);
/* 196 */       else if (localName.equals("value")) {
/* 197 */         dt = new DeserializerTarget(this, MapDeserializer.VALHINT);
/*     */       }
/*     */ 
/* 201 */       if (dt != null) {
/* 202 */         dser.registerValueTarget(dt);
/*     */       }
/*     */ 
/* 206 */       addChildDeserializer(dser);
/*     */ 
/* 208 */       return (SOAPHandler)dser;
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.MapDeserializer
 * JD-Core Version:    0.6.0
 */