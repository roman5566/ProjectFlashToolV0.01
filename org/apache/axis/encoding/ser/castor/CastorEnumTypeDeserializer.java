/*    */ package org.apache.axis.encoding.ser.castor;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.encoding.DeserializationContext;
/*    */ import org.apache.axis.encoding.Deserializer;
/*    */ import org.apache.axis.encoding.DeserializerImpl;
/*    */ import org.apache.axis.message.MessageElement;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public class CastorEnumTypeDeserializer extends DeserializerImpl
/*    */   implements Deserializer
/*    */ {
/*    */   public QName xmlType;
/*    */   public Class javaType;
/*    */ 
/*    */   public CastorEnumTypeDeserializer(Class javaType, QName xmlType)
/*    */   {
/* 43 */     this.xmlType = xmlType;
/* 44 */     this.javaType = javaType;
/*    */   }
/*    */ 
/*    */   public void onEndElement(String namespace, String localName, DeserializationContext context)
/*    */     throws SAXException
/*    */   {
/*    */     try
/*    */     {
/* 54 */       MessageElement msgElem = context.getCurElement();
/* 55 */       if (msgElem != null) {
/* 56 */         Method method = this.javaType.getMethod("valueOf", new Class[] { String.class });
/* 57 */         this.value = method.invoke(null, new Object[] { msgElem.getValue() });
/*    */       }
/*    */     } catch (Exception exp) {
/* 60 */       log.error(Messages.getMessage("exception00"), exp);
/* 61 */       throw new SAXException(exp);
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.castor.CastorEnumTypeDeserializer
 * JD-Core Version:    0.6.0
 */