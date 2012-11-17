/*    */ package org.apache.axis.encoding.ser.castor;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.encoding.DeserializationContext;
/*    */ import org.apache.axis.encoding.Deserializer;
/*    */ import org.apache.axis.encoding.DeserializerImpl;
/*    */ import org.apache.axis.message.MessageElement;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.exolab.castor.xml.MarshalException;
/*    */ import org.exolab.castor.xml.Unmarshaller;
/*    */ import org.exolab.castor.xml.ValidationException;
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public class CastorDeserializer extends DeserializerImpl
/*    */   implements Deserializer
/*    */ {
/*    */   public QName xmlType;
/*    */   public Class javaType;
/*    */ 
/*    */   public CastorDeserializer(Class javaType, QName xmlType)
/*    */   {
/* 46 */     this.xmlType = xmlType;
/* 47 */     this.javaType = javaType;
/*    */   }
/*    */ 
/*    */   public void onEndElement(String namespace, String localName, DeserializationContext context)
/*    */     throws SAXException
/*    */   {
/*    */     try
/*    */     {
/* 59 */       MessageElement msgElem = context.getCurElement();
/* 60 */       if (msgElem != null)
/*    */       {
/* 62 */         this.value = Unmarshaller.unmarshal(this.javaType, msgElem.getAsDOM());
/*    */       }
/*    */     } catch (MarshalException me) {
/* 65 */       log.error(Messages.getMessage("castorMarshalException00"), me);
/* 66 */       throw new SAXException(Messages.getMessage("castorMarshalException00") + me.getLocalizedMessage());
/*    */     }
/*    */     catch (ValidationException ve) {
/* 69 */       log.error(Messages.getMessage("castorValidationException00"), ve);
/* 70 */       throw new SAXException(Messages.getMessage("castorValidationException00") + ve.getLocation() + ": " + ve.getLocalizedMessage());
/*    */     }
/*    */     catch (Exception exp) {
/* 73 */       log.error(Messages.getMessage("exception00"), exp);
/* 74 */       throw new SAXException(exp);
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.castor.CastorDeserializer
 * JD-Core Version:    0.6.0
 */