/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.beans.IntrospectionException;
/*    */ import java.lang.reflect.Method;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.utils.cache.MethodCache;
/*    */ 
/*    */ public class EnumDeserializer extends SimpleDeserializer
/*    */ {
/* 33 */   private Method fromStringMethod = null;
/*    */ 
/* 35 */   private static final Class[] STRING_CLASS = { String.class };
/*    */ 
/*    */   public EnumDeserializer(Class javaType, QName xmlType) {
/* 38 */     super(javaType, xmlType);
/*    */   }
/*    */ 
/*    */   public Object makeValue(String source)
/*    */     throws Exception
/*    */   {
/* 44 */     if (this.isNil)
/* 45 */       return null;
/* 46 */     if (this.fromStringMethod == null) {
/*    */       try {
/* 48 */         this.fromStringMethod = MethodCache.getInstance().getMethod(this.javaType, "fromString", STRING_CLASS);
/*    */       } catch (Exception e) {
/* 50 */         throw new IntrospectionException(e.toString());
/*    */       }
/*    */     }
/* 53 */     return this.fromStringMethod.invoke(null, new Object[] { source });
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.EnumDeserializer
 * JD-Core Version:    0.6.0
 */