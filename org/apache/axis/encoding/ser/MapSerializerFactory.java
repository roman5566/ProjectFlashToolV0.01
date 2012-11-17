/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ 
/*    */ public class MapSerializerFactory extends BaseSerializerFactory
/*    */ {
/*    */   public MapSerializerFactory(Class javaType, QName xmlType)
/*    */   {
/* 29 */     super(MapSerializer.class, xmlType, javaType);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.MapSerializerFactory
 * JD-Core Version:    0.6.0
 */