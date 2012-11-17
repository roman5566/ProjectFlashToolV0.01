/*     */ package org.apache.axis.encoding.ser.castor;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Enumeration;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.encoding.Serializer;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.fromJava.Types;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.w3c.dom.Element;
/*     */ import org.xml.sax.Attributes;
/*     */ 
/*     */ public class CastorEnumTypeSerializer
/*     */   implements Serializer
/*     */ {
/*  42 */   protected static Log log = LogFactory.getLog(CastorEnumTypeSerializer.class.getName());
/*     */ 
/*     */   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context)
/*     */     throws IOException
/*     */   {
/*  61 */     context.startElement(name, attributes);
/*     */     try
/*     */     {
/*  65 */       Method method = value.getClass().getMethod("toString", new Class[0]);
/*     */ 
/*  68 */       String string = (String)method.invoke(value, new Object[0]);
/*     */ 
/*  71 */       context.writeString(string);
/*     */     }
/*     */     catch (Exception me) {
/*  74 */       log.error(Messages.getMessage("exception00"), me);
/*  75 */       throw new IOException("Castor object error: " + me.getLocalizedMessage());
/*     */     } finally {
/*  77 */       context.endElement();
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getMechanismType() {
/*  82 */     return "Axis SAX Mechanism";
/*     */   }
/*     */ 
/*     */   public Element writeSchema(Class javaType, Types types)
/*     */     throws Exception
/*     */   {
/* 106 */     Element simpleType = types.createElement("simpleType");
/* 107 */     Element restriction = types.createElement("restriction");
/* 108 */     simpleType.appendChild(restriction);
/* 109 */     restriction.setAttribute("base", "xsd:string");
/*     */ 
/* 111 */     Method enumerateMethod = javaType.getMethod("enumerate", new Class[0]);
/* 112 */     Enumeration en = (Enumeration)enumerateMethod.invoke(null, new Object[0]);
/* 113 */     while (en.hasMoreElements()) {
/* 114 */       Object obj = en.nextElement();
/* 115 */       Method toStringMethod = obj.getClass().getMethod("toString", new Class[0]);
/* 116 */       String value = (String)toStringMethod.invoke(obj, new Object[0]);
/*     */ 
/* 118 */       Element enumeration = types.createElement("enumeration");
/* 119 */       restriction.appendChild(enumeration);
/* 120 */       enumeration.setAttribute("value", value);
/*     */     }
/*     */ 
/* 123 */     return simpleType;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.castor.CastorEnumTypeSerializer
 * JD-Core Version:    0.6.0
 */