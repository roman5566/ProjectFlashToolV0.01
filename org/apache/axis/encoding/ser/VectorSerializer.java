/*     */ package org.apache.axis.encoding.ser;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Iterator;
/*     */ import java.util.Vector;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.Constants;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.encoding.Serializer;
/*     */ import org.apache.axis.utils.IdentityHashMap;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.fromJava.Types;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Element;
/*     */ import org.xml.sax.Attributes;
/*     */ 
/*     */ public class VectorSerializer
/*     */   implements Serializer
/*     */ {
/*  45 */   protected static Log log = LogFactory.getLog(VectorSerializer.class.getName());
/*     */ 
/*     */   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context)
/*     */     throws IOException
/*     */   {
/*  63 */     if (!(value instanceof Vector)) {
/*  64 */       throw new IOException(Messages.getMessage("noVector00", "VectorSerializer", value.getClass().getName()));
/*     */     }
/*     */ 
/*  68 */     Vector vector = (Vector)value;
/*     */ 
/*  71 */     if (isRecursive(new IdentityHashMap(), vector)) {
/*  72 */       throw new IOException(Messages.getMessage("badVector00"));
/*     */     }
/*     */ 
/*  75 */     context.startElement(name, attributes);
/*  76 */     for (Iterator i = vector.iterator(); i.hasNext(); )
/*     */     {
/*  78 */       Object item = i.next();
/*  79 */       context.serialize(Constants.QNAME_LITERAL_ITEM, null, item);
/*     */     }
/*  81 */     context.endElement();
/*     */   }
/*     */ 
/*     */   public boolean isRecursive(IdentityHashMap map, Vector vector)
/*     */   {
/*  86 */     map.add(vector);
/*  87 */     boolean recursive = false;
/*  88 */     for (int i = 0; (i < vector.size()) && (!recursive); i++)
/*     */     {
/*  90 */       Object o = vector.get(i);
/*  91 */       if ((o instanceof Vector)) {
/*  92 */         if (map.containsKey(o)) {
/*  93 */           return true;
/*     */         }
/*  95 */         recursive = isRecursive(map, (Vector)o);
/*     */       }
/*     */     }
/*     */ 
/*  99 */     return recursive;
/*     */   }
/*     */   public String getMechanismType() {
/* 102 */     return "Axis SAX Mechanism";
/*     */   }
/*     */ 
/*     */   public Element writeSchema(Class javaType, Types types)
/*     */     throws Exception
/*     */   {
/* 116 */     Element complexType = types.createElement("complexType");
/* 117 */     complexType.setAttribute("name", "Vector");
/* 118 */     types.writeSchemaTypeDecl(Constants.SOAP_VECTOR, complexType);
/* 119 */     Element seq = types.createElement("sequence");
/* 120 */     complexType.appendChild(seq);
/*     */ 
/* 122 */     Element element = types.createElement("element");
/* 123 */     element.setAttribute("name", "item");
/* 124 */     element.setAttribute("minOccurs", "0");
/* 125 */     element.setAttribute("maxOccurs", "unbounded");
/* 126 */     element.setAttribute("type", "xsd:anyType");
/* 127 */     seq.appendChild(element);
/*     */ 
/* 129 */     return complexType;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.VectorSerializer
 * JD-Core Version:    0.6.0
 */