/*    */ package org.apache.axis.schema;
/*    */ 
/*    */ import java.util.Calendar;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.Constants;
/*    */ import org.apache.axis.encoding.TypeMappingImpl;
/*    */ import org.apache.axis.encoding.ser.CalendarDeserializerFactory;
/*    */ import org.apache.axis.encoding.ser.CalendarSerializerFactory;
/*    */ 
/*    */ public class SchemaVersion1999
/*    */   implements SchemaVersion
/*    */ {
/* 32 */   public static QName QNAME_NIL = new QName("http://www.w3.org/1999/XMLSchema-instance", "null");
/*    */ 
/*    */   public QName getNilQName()
/*    */   {
/* 48 */     return QNAME_NIL;
/*    */   }
/*    */ 
/*    */   public String getXsiURI()
/*    */   {
/* 56 */     return "http://www.w3.org/1999/XMLSchema-instance";
/*    */   }
/*    */ 
/*    */   public String getXsdURI()
/*    */   {
/* 64 */     return "http://www.w3.org/1999/XMLSchema";
/*    */   }
/*    */ 
/*    */   public void registerSchemaSpecificTypes(TypeMappingImpl tm)
/*    */   {
/* 73 */     tm.register(Calendar.class, Constants.XSD_TIMEINSTANT1999, new CalendarSerializerFactory(Calendar.class, Constants.XSD_TIMEINSTANT1999), new CalendarDeserializerFactory(Calendar.class, Constants.XSD_TIMEINSTANT1999));
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.schema.SchemaVersion1999
 * JD-Core Version:    0.6.0
 */