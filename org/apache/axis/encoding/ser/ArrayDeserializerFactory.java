/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import javax.xml.rpc.encoding.Deserializer;
/*    */ 
/*    */ public class ArrayDeserializerFactory extends BaseDeserializerFactory
/*    */ {
/*    */   private QName componentXmlType;
/*    */ 
/*    */   public ArrayDeserializerFactory()
/*    */   {
/* 31 */     super(ArrayDeserializer.class);
/*    */   }
/*    */ 
/*    */   public ArrayDeserializerFactory(QName componentXmlType)
/*    */   {
/* 39 */     super(ArrayDeserializer.class);
/* 40 */     this.componentXmlType = componentXmlType;
/*    */   }
/*    */ 
/*    */   public Deserializer getDeserializerAs(String mechanismType)
/*    */   {
/* 51 */     ArrayDeserializer dser = (ArrayDeserializer)super.getDeserializerAs(mechanismType);
/* 52 */     dser.defaultItemType = this.componentXmlType;
/* 53 */     return dser;
/*    */   }
/*    */ 
/*    */   public void setComponentType(QName componentType) {
/* 57 */     this.componentXmlType = componentType;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.ArrayDeserializerFactory
 * JD-Core Version:    0.6.0
 */