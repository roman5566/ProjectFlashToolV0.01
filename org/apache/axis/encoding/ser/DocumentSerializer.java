/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.encoding.SerializationContext;
/*    */ import org.apache.axis.encoding.Serializer;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.axis.wsdl.fromJava.Types;
/*    */ import org.w3c.dom.Document;
/*    */ import org.w3c.dom.Element;
/*    */ import org.xml.sax.Attributes;
/*    */ 
/*    */ public class DocumentSerializer
/*    */   implements Serializer
/*    */ {
/*    */   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context)
/*    */     throws IOException
/*    */   {
/* 45 */     if (!(value instanceof Document)) {
/* 46 */       throw new IOException(Messages.getMessage("cantSerialize01"));
/*    */     }
/* 48 */     context.startElement(name, attributes);
/* 49 */     Document document = (Document)value;
/* 50 */     context.writeDOMElement(document.getDocumentElement());
/* 51 */     context.endElement();
/*    */   }
/*    */   public String getMechanismType() {
/* 54 */     return "Axis SAX Mechanism";
/*    */   }
/*    */ 
/*    */   public Element writeSchema(Class javaType, Types types)
/*    */     throws Exception
/*    */   {
/* 68 */     return null;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.DocumentSerializer
 * JD-Core Version:    0.6.0
 */