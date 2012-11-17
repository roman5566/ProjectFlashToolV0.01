/*    */ package org.apache.axis.encoding;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.Writer;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.xml.sax.Attributes;
/*    */ 
/*    */ public class AttributeSerializationContextImpl extends SerializationContext
/*    */ {
/*    */   SerializationContext parent;
/*    */ 
/*    */   public AttributeSerializationContextImpl(Writer writer, SerializationContext parent)
/*    */   {
/* 36 */     super(writer);
/* 37 */     this.parent = parent;
/*    */   }
/*    */ 
/*    */   public void startElement(QName qName, Attributes attributes)
/*    */     throws IOException
/*    */   {
/*    */   }
/*    */ 
/*    */   public void endElement()
/*    */     throws IOException
/*    */   {
/*    */   }
/*    */ 
/*    */   public String qName2String(QName qname)
/*    */   {
/* 54 */     return this.parent.qName2String(qname);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.AttributeSerializationContextImpl
 * JD-Core Version:    0.6.0
 */