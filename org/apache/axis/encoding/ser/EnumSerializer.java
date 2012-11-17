/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.lang.reflect.Method;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.encoding.SerializationContext;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.axis.wsdl.fromJava.Types;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.w3c.dom.Element;
/*    */ import org.xml.sax.Attributes;
/*    */ 
/*    */ public class EnumSerializer extends SimpleSerializer
/*    */ {
/* 38 */   protected static Log log = LogFactory.getLog(EnumSerializer.class.getName());
/*    */ 
/* 41 */   private Method toStringMethod = null;
/*    */ 
/*    */   public EnumSerializer(Class javaType, QName xmlType) {
/* 44 */     super(javaType, xmlType);
/*    */   }
/*    */ 
/*    */   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context)
/*    */     throws IOException
/*    */   {
/* 54 */     context.startElement(name, attributes);
/* 55 */     context.writeString(getValueAsString(value, context));
/* 56 */     context.endElement();
/*    */   }
/*    */ 
/*    */   public String getValueAsString(Object value, SerializationContext context)
/*    */   {
/*    */     try
/*    */     {
/* 63 */       if (this.toStringMethod == null) {
/* 64 */         this.toStringMethod = this.javaType.getMethod("toString", null);
/*    */       }
/* 66 */       return (String)this.toStringMethod.invoke(value, null);
/*    */     } catch (Exception e) {
/* 68 */       log.error(Messages.getMessage("exception00"), e);
/*    */     }
/* 70 */     return null;
/*    */   }
/*    */ 
/*    */   public Element writeSchema(Class javaType, Types types)
/*    */     throws Exception
/*    */   {
/* 86 */     return types.writeEnumType(this.xmlType, javaType);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.EnumSerializer
 * JD-Core Version:    0.6.0
 */