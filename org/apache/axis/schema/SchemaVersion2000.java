/*    */ package org.apache.axis.schema;
/*    */ 
/*    */ import java.util.Calendar;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.Constants;
/*    */ import org.apache.axis.encoding.TypeMappingImpl;
/*    */ import org.apache.axis.encoding.ser.CalendarDeserializerFactory;
/*    */ import org.apache.axis.encoding.ser.CalendarSerializerFactory;
/*    */ 
/*    */ public class SchemaVersion2000
/*    */   implements SchemaVersion
/*    */ {
/* 32 */   public static QName QNAME_NIL = new QName("http://www.w3.org/2000/10/XMLSchema-instance", "null");
/*    */ 
/*    */   public QName getNilQName()
/*    */   {
/* 47 */     return QNAME_NIL;
/*    */   }
/*    */ 
/*    */   public String getXsiURI()
/*    */   {
/* 55 */     return "http://www.w3.org/2000/10/XMLSchema-instance";
/*    */   }
/*    */ 
/*    */   public String getXsdURI()
/*    */   {
/* 63 */     return "http://www.w3.org/2000/10/XMLSchema";
/*    */   }
/*    */ 
/*    */   public void registerSchemaSpecificTypes(TypeMappingImpl tm)
/*    */   {
/* 71 */     tm.register(Calendar.class, Constants.XSD_TIMEINSTANT2000, new CalendarSerializerFactory(Calendar.class, Constants.XSD_TIMEINSTANT2000), new CalendarDeserializerFactory(Calendar.class, Constants.XSD_TIMEINSTANT2000));
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.schema.SchemaVersion2000
 * JD-Core Version:    0.6.0
 */