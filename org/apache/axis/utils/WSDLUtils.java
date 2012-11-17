/*    */ package org.apache.axis.utils;
/*    */ 
/*    */ import java.util.List;
/*    */ import java.util.ListIterator;
/*    */ import javax.wsdl.Port;
/*    */ import javax.wsdl.extensions.UnknownExtensibilityElement;
/*    */ import javax.wsdl.extensions.soap.SOAPAddress;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.w3c.dom.Element;
/*    */ 
/*    */ public class WSDLUtils
/*    */ {
/* 32 */   protected static Log log = LogFactory.getLog(WSDLUtils.class.getName());
/*    */ 
/*    */   public static String getAddressFromPort(Port p)
/*    */   {
/* 40 */     List extensibilityList = p.getExtensibilityElements();
/* 41 */     for (ListIterator li = extensibilityList.listIterator(); li.hasNext(); ) {
/* 42 */       Object obj = li.next();
/* 43 */       if ((obj instanceof SOAPAddress))
/* 44 */         return ((SOAPAddress)obj).getLocationURI();
/* 45 */       if ((obj instanceof UnknownExtensibilityElement))
/*    */       {
/* 47 */         UnknownExtensibilityElement unkElement = (UnknownExtensibilityElement)obj;
/* 48 */         QName name = unkElement.getElementType();
/* 49 */         if ((name.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/")) && (name.getLocalPart().equals("address")))
/*    */         {
/* 51 */           return unkElement.getElement().getAttribute("location");
/*    */         }
/*    */       }
/*    */     }
/*    */ 
/* 56 */     return null;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.WSDLUtils
 * JD-Core Version:    0.6.0
 */