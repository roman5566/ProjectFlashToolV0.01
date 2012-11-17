/*     */ package org.apache.axis.message;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.soap.DetailEntry;
/*     */ import javax.xml.soap.Name;
/*     */ import javax.xml.soap.SOAPElement;
/*     */ import javax.xml.soap.SOAPException;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.description.FaultDesc;
/*     */ import org.apache.axis.description.OperationDesc;
/*     */ import org.apache.axis.encoding.DeserializationContext;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.helpers.AttributesImpl;
/*     */ 
/*     */ public class SOAPFault extends SOAPBodyElement
/*     */   implements javax.xml.soap.SOAPFault
/*     */ {
/*     */   protected AxisFault fault;
/*     */   protected String prefix;
/*     */   private Locale locale;
/*  51 */   protected Detail detail = null;
/*     */ 
/*     */   public SOAPFault(String namespace, String localName, String prefix, Attributes attrs, DeserializationContext context)
/*     */     throws AxisFault
/*     */   {
/*  57 */     super(namespace, localName, prefix, attrs, context);
/*     */   }
/*     */ 
/*     */   public SOAPFault(AxisFault fault)
/*     */   {
/*  62 */     this.fault = fault;
/*     */   }
/*     */ 
/*     */   public void outputImpl(SerializationContext context)
/*     */     throws Exception
/*     */   {
/*  68 */     SOAPConstants soapConstants = context.getMessageContext() == null ? SOAPConstants.SOAP11_CONSTANTS : context.getMessageContext().getSOAPConstants();
/*     */ 
/*  72 */     this.namespaceURI = soapConstants.getEnvelopeURI();
/*  73 */     this.name = "Fault";
/*     */ 
/*  75 */     context.registerPrefixForURI(this.prefix, soapConstants.getEnvelopeURI());
/*  76 */     context.startElement(new QName(getNamespaceURI(), getName()), this.attributes);
/*     */ 
/*  81 */     if ((this.fault instanceof AxisFault)) {
/*  82 */       AxisFault axisFault = this.fault;
/*  83 */       if (axisFault.getFaultCode() != null)
/*     */       {
/*  86 */         if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
/*  87 */           String faultCode = context.qName2String(axisFault.getFaultCode());
/*  88 */           context.startElement(Constants.QNAME_FAULTCODE_SOAP12, null);
/*  89 */           context.startElement(Constants.QNAME_FAULTVALUE_SOAP12, null);
/*  90 */           context.writeSafeString(faultCode);
/*  91 */           context.endElement();
/*  92 */           QName[] subcodes = axisFault.getFaultSubCodes();
/*  93 */           if (subcodes != null) {
/*  94 */             for (int i = 0; i < subcodes.length; i++) {
/*  95 */               faultCode = context.qName2String(subcodes[i]);
/*  96 */               context.startElement(Constants.QNAME_FAULTSUBCODE_SOAP12, null);
/*  97 */               context.startElement(Constants.QNAME_FAULTVALUE_SOAP12, null);
/*  98 */               context.writeSafeString(faultCode);
/*  99 */               context.endElement();
/*     */             }
/*     */ 
/* 102 */             for (int i = 0; i < subcodes.length; i++) {
/* 103 */               context.endElement();
/*     */             }
/*     */           }
/* 106 */           context.endElement();
/*     */         } else {
/* 108 */           String faultCode = context.qName2String(axisFault.getFaultCode());
/* 109 */           context.startElement(Constants.QNAME_FAULTCODE, null);
/* 110 */           context.writeSafeString(faultCode);
/* 111 */           context.endElement();
/*     */         }
/*     */       }
/*     */ 
/* 115 */       if (axisFault.getFaultString() != null) {
/* 116 */         if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
/* 117 */           context.startElement(Constants.QNAME_FAULTREASON_SOAP12, null);
/* 118 */           AttributesImpl attrs = new AttributesImpl();
/* 119 */           attrs.addAttribute("http://www.w3.org/XML/1998/namespace", "lang", "xml:lang", "CDATA", "en");
/* 120 */           context.startElement(Constants.QNAME_TEXT_SOAP12, attrs);
/*     */         } else {
/* 122 */           context.startElement(Constants.QNAME_FAULTSTRING, null);
/* 123 */         }context.writeSafeString(axisFault.getFaultString());
/* 124 */         context.endElement();
/* 125 */         if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
/* 126 */           context.endElement();
/*     */         }
/*     */       }
/*     */ 
/* 130 */       if (axisFault.getFaultActor() != null) {
/* 131 */         if (soapConstants == SOAPConstants.SOAP12_CONSTANTS)
/* 132 */           context.startElement(Constants.QNAME_FAULTROLE_SOAP12, null);
/*     */         else {
/* 134 */           context.startElement(Constants.QNAME_FAULTACTOR, null);
/*     */         }
/* 136 */         context.writeSafeString(axisFault.getFaultActor());
/* 137 */         context.endElement();
/*     */       }
/*     */ 
/* 140 */       if ((axisFault.getFaultNode() != null) && 
/* 141 */         (soapConstants == SOAPConstants.SOAP12_CONSTANTS)) {
/* 142 */         context.startElement(Constants.QNAME_FAULTNODE_SOAP12, null);
/* 143 */         context.writeSafeString(axisFault.getFaultNode());
/* 144 */         context.endElement();
/*     */       }
/*     */ 
/* 149 */       QName qname = getFaultQName(this.fault.getClass(), context);
/* 150 */       if ((qname == null) && (this.fault.detail != null)) {
/* 151 */         qname = getFaultQName(this.fault.detail.getClass(), context);
/*     */       }
/* 153 */       if (qname == null)
/*     */       {
/* 155 */         qname = new QName("", "faultData");
/*     */       }
/* 157 */       Element[] faultDetails = axisFault.getFaultDetails();
/* 158 */       if (faultDetails != null) {
/* 159 */         if (soapConstants == SOAPConstants.SOAP12_CONSTANTS)
/* 160 */           context.startElement(Constants.QNAME_FAULTDETAIL_SOAP12, null);
/*     */         else {
/* 162 */           context.startElement(Constants.QNAME_FAULTDETAILS, null);
/*     */         }
/*     */ 
/* 165 */         axisFault.writeDetails(qname, context);
/*     */ 
/* 167 */         for (int i = 0; i < faultDetails.length; i++)
/* 168 */           context.writeDOMElement(faultDetails[i]);
/*     */         Iterator it;
/* 171 */         if (this.detail != null) {
/* 172 */           for (it = this.detail.getChildren().iterator(); it.hasNext(); ) {
/* 173 */             ((NodeImpl)it.next()).output(context);
/*     */           }
/*     */         }
/*     */ 
/* 177 */         context.endElement();
/*     */       }
/*     */     }
/*     */ 
/* 181 */     context.endElement();
/*     */   }
/*     */ 
/*     */   private QName getFaultQName(Class cls, SerializationContext context) {
/* 185 */     QName qname = null;
/* 186 */     if (!cls.equals(AxisFault.class)) {
/* 187 */       FaultDesc faultDesc = null;
/* 188 */       if (context.getMessageContext() != null) {
/* 189 */         OperationDesc op = context.getMessageContext().getOperation();
/* 190 */         if (op != null) {
/* 191 */           faultDesc = op.getFaultByClass(cls);
/*     */         }
/*     */       }
/*     */ 
/* 195 */       if (faultDesc != null) {
/* 196 */         qname = faultDesc.getQName();
/*     */       }
/*     */     }
/* 199 */     return qname;
/*     */   }
/*     */ 
/*     */   public AxisFault getFault()
/*     */   {
/* 204 */     return this.fault;
/*     */   }
/*     */ 
/*     */   public void setFault(AxisFault fault)
/*     */   {
/* 209 */     this.fault = fault;
/*     */   }
/*     */ 
/*     */   public void setFaultCode(String faultCode)
/*     */     throws SOAPException
/*     */   {
/* 226 */     this.fault.setFaultCodeAsString(faultCode);
/*     */   }
/*     */ 
/*     */   public String getFaultCode()
/*     */   {
/* 235 */     return this.fault.getFaultCode().getLocalPart();
/*     */   }
/*     */ 
/*     */   public void setFaultActor(String faultActor)
/*     */     throws SOAPException
/*     */   {
/* 252 */     this.fault.setFaultActor(faultActor);
/*     */   }
/*     */ 
/*     */   public String getFaultActor()
/*     */   {
/* 263 */     return this.fault.getFaultActor();
/*     */   }
/*     */ 
/*     */   public void setFaultString(String faultString)
/*     */     throws SOAPException
/*     */   {
/* 278 */     this.fault.setFaultString(faultString);
/*     */   }
/*     */ 
/*     */   public String getFaultString()
/*     */   {
/* 288 */     return this.fault.getFaultString();
/*     */   }
/*     */ 
/*     */   public javax.xml.soap.Detail getDetail()
/*     */   {
/* 302 */     List children = getChildren();
/* 303 */     if ((children == null) || (children.size() <= 0)) {
/* 304 */       return null;
/*     */     }
/*     */ 
/* 307 */     for (int i = 0; i < children.size(); i++) {
/* 308 */       Object obj = children.get(i);
/* 309 */       if ((obj instanceof javax.xml.soap.Detail)) {
/* 310 */         return (javax.xml.soap.Detail)obj;
/*     */       }
/*     */     }
/* 313 */     return null;
/*     */   }
/*     */ 
/*     */   public javax.xml.soap.Detail addDetail()
/*     */     throws SOAPException
/*     */   {
/* 330 */     if (getDetail() != null) {
/* 331 */       throw new SOAPException(Messages.getMessage("valuePresent"));
/*     */     }
/* 333 */     Detail detail = convertToDetail(this.fault);
/* 334 */     addChildElement(detail);
/* 335 */     return detail;
/*     */   }
/*     */ 
/*     */   public void setFaultCode(Name faultCodeQName) throws SOAPException {
/* 339 */     String uri = faultCodeQName.getURI();
/* 340 */     String local = faultCodeQName.getLocalName();
/* 341 */     String prefix = faultCodeQName.getPrefix();
/*     */ 
/* 343 */     this.prefix = prefix;
/* 344 */     QName qname = new QName(uri, local);
/* 345 */     this.fault.setFaultCode(qname);
/*     */   }
/*     */ 
/*     */   public Name getFaultCodeAsName() {
/* 349 */     QName qname = this.fault.getFaultCode();
/* 350 */     String uri = qname.getNamespaceURI();
/* 351 */     String local = qname.getLocalPart();
/* 352 */     return new PrefixedQName(uri, local, this.prefix);
/*     */   }
/*     */ 
/*     */   public void setFaultString(String faultString, Locale locale) throws SOAPException {
/* 356 */     this.fault.setFaultString(faultString);
/* 357 */     this.locale = locale;
/*     */   }
/*     */ 
/*     */   public Locale getFaultStringLocale() {
/* 361 */     return this.locale;
/*     */   }
/*     */ 
/*     */   private Detail convertToDetail(AxisFault fault)
/*     */     throws SOAPException
/*     */   {
/* 374 */     this.detail = new Detail();
/* 375 */     Element[] darray = fault.getFaultDetails();
/* 376 */     fault.setFaultDetail(new Element[0]);
/* 377 */     for (int i = 0; i < darray.length; i++)
/*     */     {
/* 379 */       Element detailtEntryElem = darray[i];
/* 380 */       DetailEntry detailEntry = this.detail.addDetailEntry(new PrefixedQName(detailtEntryElem.getNamespaceURI(), detailtEntryElem.getLocalName(), detailtEntryElem.getPrefix()));
/*     */ 
/* 383 */       copyChildren(detailEntry, detailtEntryElem);
/*     */     }
/* 385 */     return this.detail;
/*     */   }
/*     */ 
/*     */   private static void copyChildren(SOAPElement soapElement, Element domElement)
/*     */     throws SOAPException
/*     */   {
/* 398 */     NodeList nl = domElement.getChildNodes();
/* 399 */     for (int j = 0; j < nl.getLength(); j++)
/*     */     {
/* 401 */       Node childNode = nl.item(j);
/* 402 */       if (childNode.getNodeType() == 3)
/*     */       {
/* 404 */         soapElement.addTextNode(childNode.getNodeValue());
/* 405 */         break;
/*     */       }
/* 407 */       if (childNode.getNodeType() != 1)
/*     */         continue;
/* 409 */       String uri = childNode.getNamespaceURI();
/* 410 */       SOAPElement childSoapElement = null;
/* 411 */       if (uri == null)
/*     */       {
/* 413 */         childSoapElement = soapElement.addChildElement(childNode.getLocalName());
/*     */       }
/*     */       else
/*     */       {
/* 418 */         childSoapElement = soapElement.addChildElement(childNode.getLocalName(), childNode.getPrefix(), uri);
/*     */       }
/*     */ 
/* 422 */       copyChildren(childSoapElement, (Element)childNode);
/*     */     }
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.SOAPFault
 * JD-Core Version:    0.6.0
 */