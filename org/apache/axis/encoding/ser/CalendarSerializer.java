/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Calendar;
/*    */ import java.util.Date;
/*    */ import java.util.TimeZone;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.encoding.SerializationContext;
/*    */ import org.apache.axis.encoding.SimpleValueSerializer;
/*    */ import org.apache.axis.wsdl.fromJava.Types;
/*    */ import org.w3c.dom.Element;
/*    */ import org.xml.sax.Attributes;
/*    */ 
/*    */ public class CalendarSerializer
/*    */   implements SimpleValueSerializer
/*    */ {
/* 42 */   private static SimpleDateFormat zulu = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
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
/* 63 */     Date date = (value instanceof Date) ? (Date)value : ((Calendar)value).getTime();
/*    */ 
/* 67 */     synchronized (zulu)
/*    */     {
/* 69 */       return zulu.format(date);
/*    */     }
/*    */   }
/*    */ 
/*    */   public String getMechanismType() {
/* 73 */     return "Axis SAX Mechanism";
/*    */   }
/*    */ 
/*    */   public Element writeSchema(Class javaType, Types types)
/*    */     throws Exception
/*    */   {
/* 87 */     return null;
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 47 */     zulu.setTimeZone(TimeZone.getTimeZone("GMT"));
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.CalendarSerializer
 * JD-Core Version:    0.6.0
 */