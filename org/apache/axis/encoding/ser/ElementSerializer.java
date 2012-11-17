/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.MessageContext;
/*    */ import org.apache.axis.encoding.SerializationContext;
/*    */ import org.apache.axis.encoding.Serializer;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.axis.wsdl.fromJava.Types;
/*    */ import org.w3c.dom.Element;
/*    */ import org.xml.sax.Attributes;
/*    */ 
/*    */ public class ElementSerializer
/*    */   implements Serializer
/*    */ {
/*    */   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context)
/*    */     throws IOException
/*    */   {
/* 46 */     if (!(value instanceof Element)) {
/* 47 */       throw new IOException(Messages.getMessage("cantSerialize01"));
/*    */     }
/* 49 */     MessageContext mc = context.getMessageContext();
/* 50 */     context.setWriteXMLType(null);
/* 51 */     boolean writeWrapper = (mc == null) || (mc.isPropertyTrue("writeWrapperForElements", true));
/*    */ 
/* 53 */     if (writeWrapper)
/* 54 */       context.startElement(name, attributes);
/* 55 */     context.writeDOMElement((Element)value);
/* 56 */     if (writeWrapper)
/* 57 */       context.endElement(); 
/*    */   }
/*    */ 
/*    */   public String getMechanismType() {
/* 60 */     return "Axis SAX Mechanism";
/*    */   }
/*    */ 
/*    */   public Element writeSchema(Class javaType, Types types)
/*    */     throws Exception
/*    */   {
/* 74 */     return null;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.ElementSerializer
 * JD-Core Version:    0.6.0
 */