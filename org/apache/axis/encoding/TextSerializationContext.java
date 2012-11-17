/*    */ package org.apache.axis.encoding;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.Writer;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.MessageContext;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.w3c.dom.Element;
/*    */ import org.xml.sax.Attributes;
/*    */ 
/*    */ public class TextSerializationContext extends SerializationContext
/*    */ {
/* 38 */   private boolean ignore = false;
/* 39 */   private int depth = 0;
/*    */ 
/*    */   public TextSerializationContext(Writer writer)
/*    */   {
/* 43 */     super(writer);
/* 44 */     this.startOfDocument = false;
/*    */   }
/*    */ 
/*    */   public TextSerializationContext(Writer writer, MessageContext msgContext)
/*    */   {
/* 49 */     super(writer, msgContext);
/* 50 */     this.startOfDocument = false;
/*    */   }
/*    */ 
/*    */   public void serialize(QName elemQName, Attributes attributes, Object value, QName xmlType, Boolean sendNull, Boolean sendType)
/*    */     throws IOException
/*    */   {
/* 61 */     throw new IOException(Messages.getMessage("notImplemented00", "serialize"));
/*    */   }
/*    */ 
/*    */   public void writeDOMElement(Element el)
/*    */     throws IOException
/*    */   {
/* 68 */     throw new IOException(Messages.getMessage("notImplemented00", "writeDOMElement"));
/*    */   }
/*    */ 
/*    */   public void startElement(QName qName, Attributes attributes)
/*    */     throws IOException
/*    */   {
/* 75 */     this.depth += 1;
/* 76 */     if (this.depth == 2)
/* 77 */       this.ignore = true;
/*    */   }
/*    */ 
/*    */   public void endElement()
/*    */     throws IOException
/*    */   {
/* 84 */     this.depth -= 1;
/* 85 */     this.ignore = true;
/*    */   }
/*    */ 
/*    */   public void writeChars(char[] p1, int p2, int p3)
/*    */     throws IOException
/*    */   {
/* 91 */     if (!this.ignore)
/* 92 */       super.writeChars(p1, p2, p3);
/*    */   }
/*    */ 
/*    */   public void writeString(String string)
/*    */     throws IOException
/*    */   {
/* 99 */     if (!this.ignore)
/* 100 */       super.writeString(string);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.TextSerializationContext
 * JD-Core Version:    0.6.0
 */