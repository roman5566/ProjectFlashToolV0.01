/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Calendar;
/*    */ import java.util.TimeZone;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.encoding.SerializationContext;
/*    */ import org.apache.axis.encoding.SimpleValueSerializer;
/*    */ import org.apache.axis.wsdl.fromJava.Types;
/*    */ import org.w3c.dom.Element;
/*    */ import org.xml.sax.Attributes;
/*    */ 
/*    */ public class TimeSerializer
/*    */   implements SimpleValueSerializer
/*    */ {
/* 42 */   private static SimpleDateFormat zulu = new SimpleDateFormat("HH:mm:ss.SSS'Z'");
/*    */ 
/*    */   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context)
/*    */     throws IOException
/*    */   {
/* 57 */     context.startElement(name, attributes);
/* 58 */     context.writeString(getValueAsString(value, context));
/* 59 */     context.endElement();
/*    */   }
/*    */ 
/*    */   public String getValueAsString(Object value, SerializationContext context) {
/* 63 */     StringBuffer buf = new StringBuffer();
/*    */ 
/* 65 */     ((Calendar)value).set(0, 0, 0);
/* 66 */     buf.append(zulu.format(((Calendar)value).getTime()));
/* 67 */     return buf.toString();
/*    */   }
/*    */   public String getMechanismType() {
/* 70 */     return "Axis SAX Mechanism";
/*    */   }
/*    */ 
/*    */   public Element writeSchema(Class javaType, Types types)
/*    */     throws Exception
/*    */   {
/* 84 */     return null;
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 46 */     zulu.setTimeZone(TimeZone.getTimeZone("GMT"));
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.TimeSerializer
 * JD-Core Version:    0.6.0
 */