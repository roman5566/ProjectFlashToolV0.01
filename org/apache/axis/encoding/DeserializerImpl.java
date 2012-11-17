/*     */ package org.apache.axis.encoding;
/*     */ 
/*     */ import java.io.StringWriter;
/*     */ import java.util.HashSet;
/*     */ import java.util.Vector;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.Part;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.message.EnvelopeHandler;
/*     */ import org.apache.axis.message.MessageElement;
/*     */ import org.apache.axis.message.SAX2EventRecorder;
/*     */ import org.apache.axis.message.SAXOutputter;
/*     */ import org.apache.axis.message.SOAPHandler;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class DeserializerImpl extends SOAPHandler
/*     */   implements javax.xml.rpc.encoding.Deserializer, Deserializer, Callback
/*     */ {
/*  49 */   protected static Log log = LogFactory.getLog(DeserializerImpl.class.getName());
/*     */ 
/*  52 */   protected Object value = null;
/*     */ 
/*  57 */   private final boolean debugEnabled = log.isDebugEnabled();
/*     */ 
/*  60 */   protected boolean isEnded = false;
/*     */ 
/*  62 */   protected Vector targets = null;
/*     */ 
/*  64 */   protected QName defaultType = null;
/*     */ 
/*  66 */   protected boolean componentsReadyFlag = false;
/*     */ 
/*  72 */   private HashSet activeDeserializers = new HashSet();
/*     */ 
/*  74 */   protected boolean isHref = false;
/*  75 */   protected boolean isNil = false;
/*  76 */   protected String id = null;
/*     */ 
/*     */   public String getMechanismType()
/*     */   {
/*  85 */     return "Axis SAX Mechanism";
/*     */   }
/*     */ 
/*     */   public Object getValue()
/*     */   {
/*  94 */     return this.value;
/*     */   }
/*     */ 
/*     */   public void setValue(Object value)
/*     */   {
/* 102 */     this.value = value;
/*     */   }
/*     */ 
/*     */   public Object getValue(Object hint)
/*     */   {
/* 113 */     return null;
/*     */   }
/*     */ 
/*     */   public void setChildValue(Object value, Object hint)
/*     */     throws SAXException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setValue(Object value, Object hint)
/*     */     throws SAXException
/*     */   {
/* 127 */     if ((hint instanceof Deserializer))
/*     */     {
/* 129 */       this.activeDeserializers.remove(hint);
/*     */ 
/* 133 */       if (componentsReady())
/*     */       {
/* 135 */         valueComplete();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setDefaultType(QName qName)
/*     */   {
/* 150 */     this.defaultType = qName;
/*     */   }
/*     */   public QName getDefaultType() {
/* 153 */     return this.defaultType;
/*     */   }
/*     */ 
/*     */   public void registerValueTarget(Target target)
/*     */   {
/* 168 */     if (this.targets == null) {
/* 169 */       this.targets = new Vector();
/*     */     }
/*     */ 
/* 172 */     this.targets.addElement(target);
/*     */   }
/*     */ 
/*     */   public Vector getValueTargets()
/*     */   {
/* 180 */     return this.targets;
/*     */   }
/*     */ 
/*     */   public void removeValueTargets()
/*     */   {
/* 187 */     if (this.targets != null)
/* 188 */       this.targets = null;
/*     */   }
/*     */ 
/*     */   public void moveValueTargets(Deserializer other)
/*     */   {
/* 203 */     if ((other == null) || (other.getValueTargets() == null)) {
/* 204 */       return;
/*     */     }
/*     */ 
/* 207 */     if (this.targets == null) {
/* 208 */       this.targets = new Vector();
/*     */     }
/*     */ 
/* 211 */     this.targets.addAll(other.getValueTargets());
/* 212 */     other.removeValueTargets();
/*     */   }
/*     */ 
/*     */   public boolean componentsReady()
/*     */   {
/* 228 */     return (this.componentsReadyFlag) || ((!this.isHref) && (this.isEnded) && (this.activeDeserializers.isEmpty()));
/*     */   }
/*     */ 
/*     */   public void valueComplete()
/*     */     throws SAXException
/*     */   {
/* 245 */     if ((componentsReady()) && 
/* 246 */       (this.targets != null)) {
/* 247 */       for (int i = 0; i < this.targets.size(); i++) {
/* 248 */         Target target = (Target)this.targets.get(i);
/* 249 */         target.set(this.value);
/* 250 */         if (this.debugEnabled) {
/* 251 */           log.debug(Messages.getMessage("setValueInTarget00", "" + this.value, "" + target));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 256 */       removeValueTargets();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addChildDeserializer(Deserializer dSer)
/*     */   {
/* 265 */     if (this.activeDeserializers != null) {
/* 266 */       this.activeDeserializers.add(dSer);
/*     */     }
/*     */ 
/* 270 */     dSer.registerValueTarget(new CallbackTarget(this, dSer));
/*     */   }
/*     */ 
/*     */   public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 319 */     super.startElement(namespace, localName, prefix, attributes, context);
/*     */ 
/* 323 */     if (context.isNil(attributes)) {
/* 324 */       this.value = null;
/* 325 */       this.isNil = true;
/* 326 */       return;
/*     */     }
/*     */ 
/* 329 */     SOAPConstants soapConstants = context.getSOAPConstants();
/*     */ 
/* 338 */     this.id = attributes.getValue("id");
/* 339 */     if (this.id != null) {
/* 340 */       context.addObjectById(this.id, this.value);
/* 341 */       if (this.debugEnabled) {
/* 342 */         log.debug(Messages.getMessage("deserInitPutValueDebug00", "" + this.value, this.id));
/*     */       }
/* 344 */       context.registerFixup("#" + this.id, this);
/*     */     }
/*     */ 
/* 347 */     String href = attributes.getValue(soapConstants.getAttrHref());
/* 348 */     if (href != null) {
/* 349 */       this.isHref = true;
/*     */ 
/* 351 */       Object ref = context.getObjectByRef(href);
/* 352 */       if (this.debugEnabled) {
/* 353 */         log.debug(Messages.getMessage("gotForID00", new String[] { "" + ref, href, ref == null ? "*null*" : ref.getClass().toString() }));
/*     */       }
/*     */ 
/* 358 */       if (ref == null)
/*     */       {
/* 360 */         context.registerFixup(href, this);
/* 361 */         return;
/*     */       }
/*     */ 
/* 364 */       if ((ref instanceof MessageElement)) {
/* 365 */         context.replaceElementHandler(new EnvelopeHandler(this));
/*     */ 
/* 367 */         SAX2EventRecorder r = context.getRecorder();
/* 368 */         context.setRecorder(null);
/* 369 */         ((MessageElement)ref).publishToHandler(context);
/* 370 */         context.setRecorder(r);
/*     */       }
/*     */       else {
/* 373 */         if ((!href.startsWith("#")) && (this.defaultType != null) && ((ref instanceof Part)))
/*     */         {
/* 375 */           Deserializer dser = context.getDeserializerForType(this.defaultType);
/* 376 */           if (null != dser) {
/* 377 */             dser.startElement(namespace, localName, prefix, attributes, context);
/*     */ 
/* 380 */             ref = dser.getValue();
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 386 */         this.value = ref;
/* 387 */         this.componentsReadyFlag = true;
/* 388 */         valueComplete();
/*     */       }
/*     */     }
/*     */     else {
/* 392 */       this.isHref = false;
/* 393 */       onStartElement(namespace, localName, prefix, attributes, context);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onStartElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 416 */     if (getClass().equals(DeserializerImpl.class)) {
/* 417 */       QName type = context.getTypeFromAttributes(namespace, localName, attributes);
/*     */ 
/* 423 */       if (type == null) {
/* 424 */         type = this.defaultType;
/* 425 */         if (type == null) {
/* 426 */           type = Constants.XSD_STRING;
/*     */         }
/*     */       }
/*     */ 
/* 430 */       if (this.debugEnabled) {
/* 431 */         log.debug(Messages.getMessage("gotType00", "Deser", "" + type));
/*     */       }
/*     */ 
/* 437 */       if (type != null) {
/* 438 */         Deserializer dser = context.getDeserializerForType(type);
/* 439 */         if (dser == null) {
/* 440 */           dser = context.getDeserializerForClass(null);
/*     */         }
/* 442 */         if (dser != null)
/*     */         {
/* 444 */           dser.moveValueTargets(this);
/* 445 */           context.replaceElementHandler((SOAPHandler)dser);
/*     */ 
/* 447 */           boolean isRef = context.isProcessingRef();
/* 448 */           context.setProcessingRef(true);
/* 449 */           dser.startElement(namespace, localName, prefix, attributes, context);
/*     */ 
/* 451 */           context.setProcessingRef(isRef);
/*     */         } else {
/* 453 */           throw new SAXException(Messages.getMessage("noDeser00", "" + type));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 480 */     return null;
/*     */   }
/*     */ 
/*     */   public final void endElement(String namespace, String localName, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 498 */     super.endElement(namespace, localName, context);
/*     */ 
/* 500 */     this.isEnded = true;
/* 501 */     if (!this.isHref) {
/* 502 */       onEndElement(namespace, localName, context);
/*     */     }
/*     */ 
/* 508 */     if (componentsReady()) {
/* 509 */       valueComplete();
/*     */     }
/*     */ 
/* 515 */     if (this.id != null) {
/* 516 */       context.addObjectById(this.id, this.value);
/* 517 */       if (this.debugEnabled)
/* 518 */         log.debug(Messages.getMessage("deserPutValueDebug00", "" + this.value, this.id));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onEndElement(String namespace, String localName, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 539 */     if ((getClass().equals(DeserializerImpl.class)) && (this.targets != null) && (!this.targets.isEmpty()))
/*     */     {
/* 542 */       StringWriter writer = new StringWriter();
/* 543 */       SerializationContext serContext = new SerializationContext(writer, context.getMessageContext());
/*     */ 
/* 546 */       serContext.setSendDecl(false);
/*     */ 
/* 548 */       SAXOutputter so = null;
/* 549 */       so = new SAXOutputter(serContext);
/* 550 */       context.getCurElement().publishContents(so);
/* 551 */       if (!this.isNil)
/* 552 */         this.value = writer.getBuffer().toString();
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.DeserializerImpl
 * JD-Core Version:    0.6.0
 */