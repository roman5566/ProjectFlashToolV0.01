/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.io.ObjectStreamException;
/*    */ import java.lang.reflect.Constructor;
/*    */ import javax.xml.namespace.QName;
/*    */ import javax.xml.rpc.JAXRPCException;
/*    */ import javax.xml.rpc.encoding.Deserializer;
/*    */ import org.apache.axis.utils.JavaUtils;
/*    */ 
/*    */ public class SimpleListDeserializerFactory extends BaseDeserializerFactory
/*    */ {
/* 37 */   private static final Class[] STRING_CLASS = { String.class };
/*    */   private final Class clazzType;
/* 41 */   private transient Constructor constructor = null;
/*    */ 
/*    */   public SimpleListDeserializerFactory(Class javaType, QName xmlType)
/*    */   {
/* 47 */     super(SimpleListDeserializer.class, xmlType, javaType.getComponentType());
/* 48 */     this.clazzType = javaType;
/* 49 */     Class componentType = javaType.getComponentType();
/*    */     try {
/* 51 */       if (!componentType.isPrimitive()) {
/* 52 */         this.constructor = componentType.getDeclaredConstructor(STRING_CLASS);
/*    */       }
/*    */       else
/*    */       {
/* 56 */         Class wrapper = JavaUtils.getWrapperClass(componentType);
/* 57 */         if (wrapper != null)
/* 58 */           this.constructor = wrapper.getDeclaredConstructor(STRING_CLASS);
/*    */       }
/*    */     }
/*    */     catch (NoSuchMethodException e) {
/* 62 */       throw new IllegalArgumentException(e.toString());
/*    */     }
/*    */   }
/*    */ 
/*    */   public Deserializer getDeserializerAs(String mechanismType)
/*    */     throws JAXRPCException
/*    */   {
/* 72 */     if (this.javaType == Object.class) {
/* 73 */       return null;
/*    */     }
/* 75 */     SimpleListDeserializer deser = (SimpleListDeserializer)super.getDeserializerAs(mechanismType);
/* 76 */     if (deser != null)
/* 77 */       deser.setConstructor(this.constructor);
/* 78 */     return deser;
/*    */   }
/*    */ 
/*    */   private Object readResolve() throws ObjectStreamException {
/* 82 */     return new SimpleListDeserializerFactory(this.clazzType, this.xmlType);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.SimpleListDeserializerFactory
 * JD-Core Version:    0.6.0
 */