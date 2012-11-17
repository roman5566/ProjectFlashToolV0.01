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
/*    */ public class DocumentDeserializer extends DeserializerImpl
/*    */ {
/* 37 */   protected static Log log = LogFactory.getLog(DocumentDeserializer.class.getName());
/*    */   public static final String DESERIALIZE_CURRENT_ELEMENT = "DeserializeCurrentElement";
/*    */ 
/*    */   public final void onEndElement(String namespace, String localName, DeserializationContext context)
/*    */     throws SAXException
/*    */   {
/*    */     try
/*    */     {
/* 47 */       MessageElement msgElem = context.getCurElement();
/* 48 */       if (msgElem != null) {
/* 49 */         MessageContext messageContext = context.getMessageContext();
/* 50 */         Boolean currentElement = (Boolean)messageContext.getProperty("DeserializeCurrentElement");
/* 51 */         if ((currentElement != null) && (currentElement.booleanValue())) {
/* 52 */           this.value = msgElem.getAsDocument();
/* 53 */           messageContext.setProperty("DeserializeCurrentElement", Boolean.FALSE);
/* 54 */           return;
/*    */         }
/* 56 */         List children = msgElem.getChildren();
/* 57 */         if (children != null) {
/* 58 */           msgElem = (MessageElement)children.get(0);
/* 59 */           if (msgElem != null)
/* 60 */             this.value = msgElem.getAsDocument();
/*    */         }
/*    */       }
/*    */     }
/*    */     catch (Exception exp) {
/* 65 */       log.error(Messages.getMessage("exception00"), exp);
/* 66 */       throw new SAXException(exp);
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.encoding.ser.DocumentDeserializer
 * JD-Core Version:    0.6.0
 */