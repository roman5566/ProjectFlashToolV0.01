/*     */ package org.apache.axis.encoding.ser;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.MessageContext;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.encoding.Serializer;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.fromJava.Types;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Element;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.helpers.AttributesImpl;
/*     */ 
/*     */ public class MapSerializer
/*     */   implements Serializer
/*     */ {
/*  46 */   protected static Log log = LogFactory.getLog(MapSerializer.class.getName());
/*     */ 
/*  50 */   private static final QName QNAME_KEY = new QName("", "key");
/*  51 */   private static final QName QNAME_ITEM = new QName("", "item");
/*  52 */   private static final QName QNAME_VALUE = new QName("", "value");
/*  53 */   private static final QName QNAME_ITEMTYPE = new QName("http://xml.apache.org/xml-soap", "item");
/*     */ 
/*     */   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context)
/*     */     throws IOException
/*     */   {
/*  70 */     if (!(value instanceof Map)) {
/*  71 */       throw new IOException(Messages.getMessage("noMap00", "MapSerializer", value.getClass().getName()));
/*     */     }
/*     */ 
/*  74 */     Map map = (Map)value;
/*     */ 
/*  76 */     context.startElement(name, attributes);
/*     */ 
/*  78 */     AttributesImpl itemsAttributes = new AttributesImpl();
/*  79 */     String encodingURI = context.getMessageContext().getEncodingStyle();
/*  80 */     String encodingPrefix = context.getPrefixForURI(encodingURI);
/*  81 */     String soapPrefix = context.getPrefixForURI(Constants.SOAP_MAP.getNamespaceURI());
/*  82 */     itemsAttributes.addAttribute(encodingURI, "type", encodingPrefix + ":type", "CDATA", encodingPrefix + ":Array");
/*     */ 
/*  84 */     itemsAttributes.addAttribute(encodingURI, "arrayType", encodingPrefix + ":arrayType", "CDATA", soapPrefix + ":item[" + map.size() + "]");
/*     */ 
/*  87 */     for (Iterator i = map.entrySet().iterator(); i.hasNext(); )
/*     */     {
/*  89 */       Map.Entry entry = (Map.Entry)i.next();
/*  90 */       Object key = entry.getKey();
/*  91 */       Object val = entry.getValue();
/*     */ 
/*  93 */       context.startElement(QNAME_ITEM, null);
/*     */ 
/*  96 */       context.serialize(QNAME_KEY, null, key, null, null, Boolean.TRUE);
/*  97 */       context.serialize(QNAME_VALUE, null, val, null, null, Boolean.TRUE);
/*     */ 
/*  99 */       context.endElement();
/*     */     }
/*     */ 
/* 102 */     context.endElement();
/*     */   }
/*     */   public String getMechanismType() {
/* 105 */     return "Axis SAX Mechanism";
/*     */   }
/*     */ 
/*     */   public Element writeSchema(Class javaType, Types types)
/*     */     throws Exception
/*     */   {
/* 119 */     Element complexType = types.createElement("complexType");
/* 120 */     complexType.setAttribute("name", "Map");
/* 121 */     Element seq = types.createElement("sequence");
/* 122 */     complexType.appendChild(seq);
/* 123 */     Element element = types.createElement("element");
/* 124 */     element.setAttribute("name", "item");
/* 125 */     element.setAttribute("minOccurs", "0");
/* 126 */     element.setAttribute("maxOccurs", "unbounded");
/* 127 */     element.setAttribute("type", types.getQNameString(new QName("http://xml.apache.org/xml-soap", "mapItem")));
/* 128 */     seq.appendChild(element);
/*     */ 
/* 130 */     Element itemType = types.createElement("complexType");
/* 131 */     itemType.setAttribute("name", "mapItem");
/* 132 */     Element seq2 = types.createElement("sequence");
/* 133 */     itemType.appendChild(seq2);
/* 134 */     Element element2 = types.createElement("element");
/* 135 */     element2.setAttribute("name", "key");
/* 136 */     element2.setAttribute("nillable", "true");
/* 137 */     element2.setAttribute("type", "xsd:anyType");
/* 138 */     seq2.appendChild(element2);
/* 139 */     Element element3 = types.createElement("element");
/* 140 */     element3.setAttribute("name", "value");
/* 141 */     element3.setAttribute("nillable", "true");
/* 142 */     element3.setAttribute("type", "xsd:anyType");
/* 143 */     seq2.appendChild(element3);
/* 144 */     types.writeSchemaTypeDecl(QNAME_ITEMTYPE, itemType);
/*     */ 
/* 146 */     return complexType;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.MapSerializer
 * JD-Core Version:    0.6.0
 */