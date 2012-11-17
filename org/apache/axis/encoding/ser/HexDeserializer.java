/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.types.HexBinary;
/*    */ 
/*    */ public class HexDeserializer extends SimpleDeserializer
/*    */ {
/*    */   public HexDeserializer(Class javaType, QName xmlType)
/*    */   {
/* 33 */     super(javaType, xmlType);
/*    */   }
/*    */ 
/*    */   public Object makeValue(String source)
/*    */     throws Exception
/*    */   {
/*    */     Object result;
/*    */     Object result;
/* 45 */     if (this.javaType == new byte[0].getClass())
/* 46 */       result = HexBinary.decode(source);
/*    */     else {
/* 48 */       result = new HexBinary(source);
/*    */     }
/* 50 */     if (result == null) result = new HexBinary("");
/* 51 */     return result;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.HexDeserializer
 * JD-Core Version:    0.6.0
 */