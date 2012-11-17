/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Calendar;
/*    */ import java.util.Date;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.encoding.SerializationContext;
/*    */ import org.apache.axis.encoding.SimpleValueSerializer;
/*    */ import org.apache.axis.wsdl.fromJava.Types;
/*    */ import org.w3c.dom.Element;
/*    */ import org.xml.sax.Attributes;
/*    */ 
/*    */ public class DateSerializer
/*    */   implements SimpleValueSerializer
/*    */ {
/* 42 */   private static SimpleDateFormat zulu = new SimpleDateFormat("yyyy-MM-dd");
/*    */ 
/* 45 */   private static Calendar calendar = Calendar.getInstance();
/*    */ 
/*    */   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context)
/*    */     throws IOException
/*    */   {
/* 54 */     context.startElement(name, attributes);
/* 55 */     context.writeString(getValueAsString(value, context));
/* 56 */     context.endElement();
/*    */   }
/*    */ 
/*    */   public String getValueAsString(Object value, SerializationContext context) {
/* 60 */     StringBuffer buf = new StringBuffer();
/* 61 */     synchronized (calendar) {
/* 62 */       if ((value instanceof Calendar)) {
/* 63 */         value = ((Calendar)value).getTime();
/*    */       }
/* 65 */       if (calendar.get(0) == 0) {
/* 66 */         buf.append("-");
/* 67 */         calendar.setTime((Date)value);
/* 68 */         calendar.set(0, 1);
/* 69 */         value = calendar.getTime();
/*    */       }
/* 71 */       buf.append(zulu.format((Date)value));
/*    */     }
/* 73 */     return buf.toString();
/*    */   }
/*    */   public String getMechanismType() {
/* 76 */     return "Axis SAX Mechanism";
/*    */   }
/*    */ 
/*    */   public Element writeSchema(Class javaType, Types types)
/*    */     throws Exception
/*    */   {
/* 90 */     return null;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.DateSerializer
 * JD-Core Version:    0.6.0
 */