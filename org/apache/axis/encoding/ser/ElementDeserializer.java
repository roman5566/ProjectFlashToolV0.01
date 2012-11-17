/*    */ package org.apache.axis.encoding.ser;
/*    */ 
/*    */ import java.util.List;
/*    */ import org.apache.axis.MessageContext;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.encoding.DeserializationContext;
/*    */ import org.apache.axis.encoding.DeserializerImpl;
/*    */ import org.apache.axis.message.MessageElement;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public class ElementDeserializer extends DeserializerImpl
/*    */ {
/* 38 */   protected static Log log = LogFactory.getLog(ElementDeserializer.class.getName());
/*    */   public static final String DESERIALIZE_CURRENT_ELEMENT = "DeserializeCurrentElement";
/*    */ 
/*    */   public final void onEndElement(String namespace, String localName, DeserializationContext context)
/*    */     throws SAXException
/*    */   {
/*    */     try
/*    */     {
/* 48 */       MessageElement msgElem = context.getCurElement();
/* 49 */       if (msgElem != null) {
/* 50 */         MessageContext messageContext = context.getMessageContext();
/* 51 */         Boolean currentElement = (Boolean)messageContext.getProperty("DeserializeCurrentElement");
/* 52 */         if ((currentElement != null) && (currentElement.booleanValue())) {
/* 53 */           this.value = msgElem.getAsDOM();
/* 54 */           messageContext.setProperty("DeserializeCurrentElement", Boolean.FALSE);
/* 55 */           return;
/*    */         }
/* 57 */         List children = msgElem.getChildren();
/* 58 */         if (children != null) {
/* 59 */           msgElem = (MessageElement)children.get(0);
/* 60 */           if (msgElem != null)
/* 61 */             this.value = msgElem.getAsDOM();
/*    */         }
/*    */       }
/*    */     }
/*    */     catch (Exception exp) {
/* 66 */       log.error(Messages.getMessage("exception00"), exp);
/* 67 */       throw new SAXException(exp);
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.ElementDeserializer
 * JD-Core Version:    0.6.0
 */