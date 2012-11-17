/*    */ package org.apache.axis.schema;
/*    */ 
/*    */ import java.util.Calendar;
/*    */ import java.util.Date;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.Constants;
/*    */ import org.apache.axis.encoding.TypeMappingImpl;
/*    */ import org.apache.axis.encoding.ser.CalendarDeserializerFactory;
/*    */ import org.apache.axis.encoding.ser.CalendarSerializerFactory;
/*    */ 
/*    */ public class SchemaVersion2001
/*    */   implements SchemaVersion
/*    */ {
/* 32 */   public static QName QNAME_NIL = new QName("http://www.w3.org/2001/XMLSchema-instance", "nil");
/*    */ 
/*    */   public QName getNilQName()
/*    */   {
/* 48 */     return QNAME_NIL;
/*    */   }
/*    */ 
/*    */   public String getXsiURI()
/*    */   {
/* 56 */     return "http://www.w3.org/2001/XMLSchema-instance";
/*    */   }
/*    */ 
/*    */   public String getXsdURI()
/*    */   {
/* 64 */     return "http://www.w3.org/2001/XMLSchema";
/*    */   }
/*    */ 
/*    */   public void registerSchemaSpecificTypes(TypeMappingImpl tm)
/*    */   {
/* 73 */     tm.register(Date.class, Constants.XSD_DATETIME, new CalendarSerializerFactory(Date.class, Constants.XSD_DATETIME), new CalendarDeserializerFactory(Date.class, Constants.XSD_DATETIME));
/*    */ 
/* 83 */     tm.register(Calendar.class, Constants.XSD_DATETIME, new CalendarSerializerFactory(Calendar.class, Constants.XSD_DATETIME), new CalendarDeserializerFactory(Calendar.class, Constants.XSD_DATETIME));
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.schema.SchemaVersion2001
 * JD-Core Version:    0.6.0
 */