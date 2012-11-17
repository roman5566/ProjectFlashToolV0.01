/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.encoding.Base64;
/*    */ 
/*    */ public class Base64Deserializer extends SimpleDeserializer
/*    */ {
/*    */   public Base64Deserializer(Class javaType, QName xmlType)
/*    */   {
/* 33 */     super(javaType, xmlType);
/*    */   }
/*    */ 
/*    */   public Object makeValue(String source)
/*    */     throws Exception
/*    */   {
/* 44 */     byte[] value = Base64.decode(source);
/*    */ 
/* 46 */     if (value == null) {
/* 47 */       if (this.javaType == new Byte[0].getClass()) {
/* 48 */         return new Byte[0];
/*    */       }
/* 50 */       return new byte[0];
/*    */     }
/*    */ 
/* 54 */     if (this.javaType == new Byte[0].getClass()) {
/* 55 */       Byte[] data = new Byte[value.length];
/* 56 */       for (int i = 0; i < data.length; i++) {
/* 57 */         byte b = value[i];
/* 58 */         data[i] = new Byte(b);
/*    */       }
/* 60 */       return data;
/*    */     }
/* 62 */     return value;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.Base64Deserializer
 * JD-Core Version:    0.6.0
 */