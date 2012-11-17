/*     */ package org.apache.axis.message;
/*     */ 
/*     */ import java.io.CharArrayWriter;
/*     */ import javax.xml.soap.SOAPException;
/*     */ import org.apache.axis.AxisFault;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.encoding.DeserializationContext;
/*     */ import org.apache.axis.encoding.TypeMappingRegistry;
/*     */ import org.apache.axis.soap.SOAPConstants;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.utils.StringUtils;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.helpers.DefaultHandler;
/*     */ 
/*     */ public class SOAPHandler extends DefaultHandler
/*     */ {
/*  39 */   public MessageElement myElement = null;
/*     */   private MessageElement[] myElements;
/*  41 */   private int myIndex = 0;
/*     */   private CharArrayWriter val;
/*     */ 
/*     */   public SOAPHandler()
/*     */   {
/*     */   }
/*     */ 
/*     */   public SOAPHandler(MessageElement[] elements, int index)
/*     */   {
/*  54 */     this.myElements = elements;
/*  55 */     this.myIndex = index;
/*     */   }
/*     */ 
/*     */   public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/*  63 */     SOAPConstants soapConstants = context.getSOAPConstants();
/*     */ 
/*  65 */     if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
/*  66 */       String encodingStyle = attributes.getValue("http://www.w3.org/2003/05/soap-envelope", "encodingStyle");
/*     */ 
/*  69 */       if ((encodingStyle != null) && (!encodingStyle.equals("")) && (!encodingStyle.equals("http://www.w3.org/2003/05/soap-envelope/encoding/none")) && (!Constants.isSOAP_ENC(encodingStyle)))
/*     */       {
/*  72 */         TypeMappingRegistry tmr = context.getTypeMappingRegistry();
/*     */ 
/*  74 */         if (tmr.getTypeMapping(encodingStyle) == tmr.getDefaultTypeMapping()) {
/*  75 */           AxisFault fault = new AxisFault(Constants.FAULT_SOAP12_DATAENCODINGUNKNOWN, null, Messages.getMessage("invalidEncodingStyle"), null, null, null);
/*     */ 
/*  78 */           throw new SAXException(fault);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  85 */     if ((!context.isDoneParsing()) && (!context.isProcessingRef())) {
/*  86 */       if (this.myElement == null) {
/*     */         try {
/*  88 */           this.myElement = makeNewElement(namespace, localName, prefix, attributes, context);
/*     */         }
/*     */         catch (AxisFault axisFault) {
/*  91 */           throw new SAXException(axisFault);
/*     */         }
/*     */       }
/*  94 */       context.pushNewElement(this.myElement);
/*     */     }
/*     */   }
/*     */ 
/*     */   public MessageElement makeNewElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws AxisFault
/*     */   {
/* 103 */     return new MessageElement(namespace, localName, prefix, attributes, context);
/*     */   }
/*     */ 
/*     */   public void endElement(String namespace, String localName, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 111 */     if (this.myElement != null) {
/* 112 */       addTextNode();
/*     */ 
/* 114 */       if (this.myElements != null) {
/* 115 */         this.myElements[this.myIndex] = this.myElement;
/*     */       }
/* 117 */       this.myElement.setEndIndex(context.getCurrentRecordPos());
/*     */     }
/*     */   }
/*     */ 
/*     */   public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/* 128 */     addTextNode();
/* 129 */     SOAPHandler handler = new SOAPHandler();
/* 130 */     return handler;
/*     */   }
/*     */ 
/*     */   private void addTextNode() throws SAXException {
/* 134 */     if ((this.myElement != null) && 
/* 135 */       (this.val != null) && (this.val.size() > 0)) {
/* 136 */       String s = StringUtils.strip(this.val.toString());
/* 137 */       this.val.reset();
/*     */ 
/* 145 */       if (s.length() > 0)
/*     */         try
/*     */         {
/* 148 */           this.myElement.addTextNode(s);
/*     */         } catch (SOAPException e) {
/* 150 */           throw new SAXException(e);
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onEndChild(String namespace, String localName, DeserializationContext context)
/*     */     throws SAXException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void characters(char[] chars, int start, int end)
/*     */     throws SAXException
/*     */   {
/* 166 */     if (this.val == null) {
/* 167 */       this.val = new CharArrayWriter();
/*     */     }
/* 169 */     this.val.write(chars, start, end);
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.SOAPHandler
 * JD-Core Version:    0.6.0
 */