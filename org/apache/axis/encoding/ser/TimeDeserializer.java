/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.types.Time;
/*    */ 
/*    */ public class TimeDeserializer extends SimpleDeserializer
/*    */ {
/*    */   public TimeDeserializer(Class javaType, QName xmlType)
/*    */   {
/* 35 */     super(javaType, xmlType);
/*    */   }
/*    */ 
/*    */   public Object makeValue(String source)
/*    */   {
/* 43 */     Time t = new Time(source);
/* 44 */     return t.getAsCalendar();
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.TimeDeserializer
 * JD-Core Version:    0.6.0
 */