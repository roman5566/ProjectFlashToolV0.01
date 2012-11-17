/*     */ package org.apache.axis.encoding.ser.castor;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.apache.axis.components.logger.LogFactory;
/*     */ import org.apache.axis.encoding.SerializationContext;
/*     */ import org.apache.axis.encoding.Serializer;
/*     */ import org.apache.axis.utils.Messages;
/*     */ import org.apache.axis.wsdl.fromJava.Types;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.exolab.castor.xml.MarshalException;
/*     */ import org.exolab.castor.xml.Marshaller;
/*     */ import org.exolab.castor.xml.ValidationException;
/*     */ import org.w3c.dom.Element;
/*     */ import org.xml.sax.Attributes;
/*     */ 
/*     */ public class CastorSerializer
/*     */   implements Serializer
/*     */ {
/*  83 */   protected static Log log = LogFactory.getLog(CastorSerializer.class.getName());
/*     */ 
/*     */   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 102 */       AxisContentHandler hand = new AxisContentHandler(context);
/* 103 */       Marshaller marshaller = new Marshaller(hand);
/*     */ 
/* 108 */       marshaller.setMarshalAsDocument(false);
/* 109 */       String localPart = name.getLocalPart();
/* 110 */       int arrayDims = localPart.indexOf('[');
/* 111 */       if (arrayDims != -1) {
/* 112 */         localPart = localPart.substring(0, arrayDims);
/*     */       }
/* 114 */       marshaller.setRootElement(localPart);
/*     */ 
/* 116 */       marshaller.marshal(value);
/*     */     } catch (MarshalException me) {
/* 118 */       log.error(Messages.getMessage("castorMarshalException00"), me);
/* 119 */       throw new IOException(Messages.getMessage("castorMarshalException00") + me.getLocalizedMessage());
/*     */     }
/*     */     catch (ValidationException ve)
/*     */     {
/* 123 */       log.error(Messages.getMessage("castorValidationException00"), ve);
/* 124 */       throw new IOException(Messages.getMessage("castorValidationException00") + ve.getLocation() + ": " + ve.getLocalizedMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getMechanismType()
/*     */   {
/* 131 */     return "Axis SAX Mechanism";
/*     */   }
/*     */ 
/*     */   public Element writeSchema(Class javaType, Types types)
/*     */     throws Exception
/*     */   {
/* 146 */     return null;
/*     */   }
/*     */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.castor.CastorSerializer
 * JD-Core Version:    0.6.0
 */