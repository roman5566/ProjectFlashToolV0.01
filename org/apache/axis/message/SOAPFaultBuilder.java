/*     */ package org.apache.axis.message;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.encoding.Callback;
/*     */ import org.apache.axis.encoding.CallbackTarget;
/*     */ import org.apache.axis.encoding.DeserializationContext;
/*     */ import org.apache.axis.encoding.Deserializer;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.XMLUtils;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class SOAPFaultBuilder extends SOAPHandler
/*     */   implements Callback
/*     */ {
/*  47 */   boolean waiting = false;
/*  48 */   boolean passedEnd = false;
/*     */   protected SOAPFault element;
/*     */   protected DeserializationContext context;
/*  52 */   static HashMap fields_soap11 = new HashMap();
/*  53 */   static HashMap fields_soap12 = new HashMap();
/*     */ 
/*  56 */   protected QName faultCode = null;
/*  57 */   protected QName[] faultSubCode = null;
/*  58 */   protected String faultString = null;
/*  59 */   protected String faultActor = null;
/*     */   protected Element[] faultDetails;
/*  61 */   protected String faultNode = null;
/*     */   protected SOAPFaultCodeBuilder code;
/*  65 */   protected Class faultClass = null;
/*  66 */   protected Object faultData = null;
/*     */   private static HashMap TYPES;
/*     */ 
/*     */   public SOAPFaultBuilder(SOAPFault element, DeserializationContext context)
/*     */   {
/*  84 */     this.element = element;
/*  85 */     this.context = context;
/*     */   }
/*     */ 
/*     */   public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/*  93 */     SOAPConstants soapConstants = context.getSOAPConstants();
/*     */ 
/*  95 */     if ((soapConstants == SOAPConstants.SOAP12_CONSTANTS) && (attributes.getValue("http://www.w3.org/2003/05/soap-envelope", "encodingStyle") != null))
/*     */     {
/*  98 */       AxisFault fault = new AxisFault(Constants.FAULT_SOAP12_SENDER, null, Messages.getMessage("noEncodingStyleAttrAppear", "Fault"), null, null, null);
/*     */ 
/* 101 */       throw new SAXException(fault);
/*     */     }
/*     */ 
/* 104 */     super.startElement(namespace, localName, prefix, attributes, context);
/*     */   }
/*     */ 
/*     */   void setFaultData(Object data) {
/* 108 */     this.faultData = data;
/* 109 */     if ((this.waiting) && (this.passedEnd))
/*     */     {
/* 112 */       createFault();
/*     */     }
/* 114 */     this.waiting = false;
/*     */   }
/*     */ 
/*     */   public void setFaultClass(Class faultClass) {
/* 118 */     this.faultClass = faultClass;
/*     */   }
/*     */ 
/*     */   public void endElement(String namespace, String localName, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 127 */     super.endElement(namespace, localName, context);
/* 128 */     if (!this.waiting)
/* 129 */       createFault();
/*     */     else
/* 131 */       this.passedEnd = true;
/*     */   }
/*     */ 
/*     */   void setWaiting(boolean waiting)
/*     */   {
/* 136 */     this.waiting = waiting;
/*     */   }
/*     */ 
/*     */   private void createFault()
/*     */   {
/* 143 */     AxisFault f = null;
/*     */ 
/* 145 */     SOAPConstants soapConstants = this.context.getMessageContext() == null ? SOAPConstants.SOAP11_CONSTANTS : this.context.getMessageContext().getSOAPConstants();
/*     */ 
/* 149 */     if (this.faultClass != null)
/*     */     {
/*     */       try
/*     */       {
/* 156 */         if (this.faultData != null) {
/* 157 */           if ((this.faultData instanceof AxisFault))
/*     */           {
/* 159 */             f = (AxisFault)this.faultData;
/*     */           }
/*     */           else
/*     */           {
/* 163 */             Class argClass = ConvertWrapper(this.faultData.getClass());
/*     */             try {
/* 165 */               Constructor con = this.faultClass.getConstructor(new Class[] { argClass });
/*     */ 
/* 168 */               f = (AxisFault)con.newInstance(new Object[] { this.faultData });
/*     */             }
/*     */             catch (Exception e)
/*     */             {
/*     */             }
/* 173 */             if ((f == null) && ((this.faultData instanceof Exception))) {
/* 174 */               f = AxisFault.makeFault((Exception)this.faultData);
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/* 179 */         if (AxisFault.class.isAssignableFrom(this.faultClass)) {
/* 180 */           if (f == null)
/*     */           {
/* 182 */             f = (AxisFault)this.faultClass.newInstance();
/*     */           }
/*     */ 
/* 185 */           if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
/* 186 */             f.setFaultCode(this.code.getFaultCode());
/*     */ 
/* 188 */             SOAPFaultCodeBuilder c = this.code;
/* 189 */             while ((c = c.getNext()) != null)
/* 190 */               f.addFaultSubCode(c.getFaultCode());
/*     */           }
/*     */           else {
/* 193 */             f.setFaultCode(this.faultCode);
/*     */           }
/*     */ 
/* 196 */           f.setFaultString(this.faultString);
/* 197 */           f.setFaultActor(this.faultActor);
/* 198 */           f.setFaultNode(this.faultNode);
/* 199 */           f.setFaultDetail(this.faultDetails);
/*     */         }
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/*     */     }
/*     */ 
/* 207 */     if (f == null) {
/* 208 */       if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
/* 209 */         this.faultCode = this.code.getFaultCode();
/* 210 */         if (this.code.getNext() != null)
/*     */         {
/* 212 */           Vector v = new Vector();
/*     */ 
/* 214 */           SOAPFaultCodeBuilder c = this.code;
/* 215 */           while ((c = c.getNext()) != null) {
/* 216 */             v.add(c.getFaultCode());
/*     */           }
/* 218 */           this.faultSubCode = ((QName[])v.toArray(new QName[v.size()]));
/*     */         }
/*     */       }
/*     */ 
/* 222 */       f = new AxisFault(this.faultCode, this.faultSubCode, this.faultString, this.faultActor, this.faultNode, this.faultDetails);
/*     */       try
/*     */       {
/* 230 */         Vector headers = this.element.getEnvelope().getHeaders();
/* 231 */         for (int i = 0; i < headers.size(); i++) {
/* 232 */           SOAPHeaderElement header = (SOAPHeaderElement)headers.elementAt(i);
/*     */ 
/* 234 */           f.addHeader(header);
/*     */         }
/*     */       }
/*     */       catch (AxisFault axisFault)
/*     */       {
/*     */       }
/*     */     }
/* 241 */     this.element.setFault(f);
/*     */   }
/*     */ 
/*     */   public SOAPHandler onStartChild(String namespace, String name, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 251 */     SOAPHandler retHandler = null;
/*     */ 
/* 253 */     SOAPConstants soapConstants = context.getMessageContext() == null ? SOAPConstants.SOAP11_CONSTANTS : context.getMessageContext().getSOAPConstants();
/*     */     QName qName;
/* 261 */     if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
/* 262 */       QName qName = (QName)fields_soap12.get(name);
/* 263 */       if (qName == null) {
/* 264 */         QName thisQName = new QName(namespace, name);
/* 265 */         if (thisQName.equals(Constants.QNAME_FAULTCODE_SOAP12))
/* 266 */           return this.code = new SOAPFaultCodeBuilder();
/* 267 */         if (thisQName.equals(Constants.QNAME_FAULTREASON_SOAP12))
/* 268 */           return new SOAPFaultReasonBuilder(this);
/* 269 */         if (thisQName.equals(Constants.QNAME_FAULTDETAIL_SOAP12))
/* 270 */           return new SOAPFaultDetailsBuilder(this);
/*     */       }
/*     */     }
/*     */     else {
/* 274 */       qName = (QName)fields_soap11.get(name);
/* 275 */       if ((qName == null) && (name.equals("detail"))) {
/* 276 */         return new SOAPFaultDetailsBuilder(this);
/*     */       }
/*     */     }
/* 279 */     if (qName != null) {
/* 280 */       Deserializer currentDeser = context.getDeserializerForType(qName);
/* 281 */       if (currentDeser != null) {
/* 282 */         currentDeser.registerValueTarget(new CallbackTarget(this, new QName(namespace, name)));
/*     */       }
/* 284 */       retHandler = (SOAPHandler)currentDeser;
/*     */     }
/*     */ 
/* 287 */     return retHandler;
/*     */   }
/*     */ 
/*     */   public void onEndChild(String namespace, String localName, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 293 */     if ("detail".equals(localName)) {
/* 294 */       MessageElement el = context.getCurElement();
/* 295 */       List children = el.getChildren();
/* 296 */       if (children != null) {
/* 297 */         Element[] elements = new Element[children.size()];
/* 298 */         for (int i = 0; i < elements.length; i++) {
/*     */           try {
/* 300 */             Node node = (Node)children.get(i);
/* 301 */             if ((node instanceof MessageElement)) {
/* 302 */               elements[i] = ((MessageElement)node).getAsDOM();
/* 303 */             } else if ((node instanceof Text)) {
/* 304 */               elements[i] = XMLUtils.newDocument().createElement("text");
/* 305 */               elements[i].appendChild(node);
/*     */             }
/*     */           } catch (Exception e) {
/* 308 */             throw new SAXException(e);
/*     */           }
/*     */         }
/* 311 */         this.faultDetails = elements;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setValue(Object value, Object hint)
/*     */   {
/* 324 */     String local = ((QName)hint).getLocalPart();
/* 325 */     if (((QName)hint).getNamespaceURI().equals("http://www.w3.org/2003/05/soap-envelope")) {
/* 326 */       if (local.equals("Role"))
/* 327 */         this.faultActor = ((String)value);
/* 328 */       else if (local.equals("Text"))
/* 329 */         this.faultString = ((String)value);
/* 330 */       else if (local.equals("Node")) {
/* 331 */         this.faultNode = ((String)value);
/*     */       }
/*     */     }
/* 334 */     else if (local.equals("faultcode"))
/* 335 */       this.faultCode = ((QName)value);
/* 336 */     else if (local.equals("faultstring"))
/* 337 */       this.faultString = ((String)value);
/* 338 */     else if (local.equals("faultactor"))
/* 339 */       this.faultActor = ((String)value);
/*     */   }
/*     */ 
/*     */   private Class ConvertWrapper(Class cls)
/*     */   {
/* 364 */     Class ret = (Class)TYPES.get(cls);
/* 365 */     if (ret != null) {
/* 366 */       return ret;
/*     */     }
/* 368 */     return cls;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  69 */     fields_soap11.put("faultcode", Constants.XSD_QNAME);
/*  70 */     fields_soap11.put("faultstring", Constants.XSD_STRING);
/*  71 */     fields_soap11.put("faultactor", Constants.XSD_STRING);
/*  72 */     fields_soap11.put("detail", null);
/*     */ 
/*  76 */     fields_soap12.put("Reason", null);
/*  77 */     fields_soap12.put("Role", Constants.XSD_STRING);
/*  78 */     fields_soap12.put("Node", Constants.XSD_STRING);
/*  79 */     fields_soap12.put("Detail", null);
/*     */ 
/* 348 */     TYPES = new HashMap(7);
/*     */ 
/* 351 */     TYPES.put(Integer.class, Integer.TYPE);
/* 352 */     TYPES.put(Float.class, Float.TYPE);
/* 353 */     TYPES.put(Boolean.class, Boolean.TYPE);
/* 354 */     TYPES.put(Double.class, Double.TYPE);
/* 355 */     TYPES.put(Byte.class, Byte.TYPE);
/* 356 */     TYPES.put(Short.class, Short.TYPE);
/* 357 */     TYPES.put(Long.class, Long.TYPE);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.SOAPFaultBuilder
 * JD-Core Version:    0.6.0
 */