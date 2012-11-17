/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ 
/*    */ public class MapDeserializerFactory extends BaseDeserializerFactory
/*    */ {
/*    */   public MapDeserializerFactory(Class javaType, QName xmlType)
/*    */   {
/* 29 */     super(MapDeserializer.class, xmlType, javaType);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.MapDeserializerFactory
 * JD-Core Version:    0.6.0
 */