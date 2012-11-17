/*    */ package org.apache.axis.handlers.soap;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Enumeration;
/*    */ import java.util.Vector;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.AxisFault;
/*    */ import org.apache.axis.Message;
/*    */ import org.apache.axis.MessageContext;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.description.OperationDesc;
/*    */ import org.apache.axis.handlers.BasicHandler;
/*    */ import org.apache.axis.message.SOAPEnvelope;
/*    */ import org.apache.axis.message.SOAPHeaderElement;
/*    */ import org.apache.axis.soap.SOAPConstants;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class MustUnderstandChecker extends BasicHandler
/*    */ {
/* 43 */   private static Log log = LogFactory.getLog(MustUnderstandChecker.class.getName());
/*    */ 
/* 46 */   private SOAPService service = null;
/*    */ 
/*    */   public MustUnderstandChecker(SOAPService service) {
/* 49 */     this.service = service;
/*    */   }
/*    */ 
/*    */   public void invoke(MessageContext msgContext) throws AxisFault
/*    */   {
/* 54 */     if (log.isDebugEnabled()) {
/* 55 */       log.debug(Messages.getMessage("semanticCheck00"));
/*    */     }
/*    */ 
/* 58 */     Message msg = msgContext.getCurrentMessage();
/* 59 */     if (msg == null) {
/* 60 */       return;
/*    */     }
/* 62 */     SOAPEnvelope env = msg.getSOAPEnvelope();
/* 63 */     Vector headers = null;
/* 64 */     if (this.service != null) {
/* 65 */       ArrayList acts = this.service.getActors();
/* 66 */       headers = env.getHeadersByActor(acts);
/*    */     } else {
/* 68 */       headers = env.getHeaders();
/*    */     }
/*    */ 
/* 72 */     Vector misunderstoodHeaders = null;
/* 73 */     Enumeration enumeration = headers.elements();
/* 74 */     while (enumeration.hasMoreElements()) {
/* 75 */       SOAPHeaderElement header = (SOAPHeaderElement)enumeration.nextElement();
/*    */ 
/* 79 */       if ((msgContext != null) && (msgContext.getOperation() != null)) {
/* 80 */         OperationDesc oper = msgContext.getOperation();
/* 81 */         if (oper.getParamByQName(header.getQName()) != null) {
/*    */           continue;
/*    */         }
/*    */       }
/* 85 */       if ((header.getMustUnderstand()) && (!header.isProcessed())) {
/* 86 */         if (misunderstoodHeaders == null)
/* 87 */           misunderstoodHeaders = new Vector();
/* 88 */         misunderstoodHeaders.addElement(header);
/*    */       }
/*    */     }
/* 91 */     SOAPConstants soapConstants = msgContext.getSOAPConstants();
/*    */ 
/* 95 */     if (misunderstoodHeaders != null) {
/* 96 */       AxisFault fault = new AxisFault(soapConstants.getMustunderstandFaultQName(), null, null, null, null, null);
/*    */ 
/* 101 */       StringBuffer whatWasMissUnderstood = new StringBuffer(256);
/*    */ 
/* 103 */       enumeration = misunderstoodHeaders.elements();
/* 104 */       while (enumeration.hasMoreElements()) {
/* 105 */         SOAPHeaderElement badHeader = (SOAPHeaderElement)enumeration.nextElement();
/*    */ 
/* 107 */         QName badQName = new QName(badHeader.getNamespaceURI(), badHeader.getName());
/*    */ 
/* 109 */         if (whatWasMissUnderstood.length() != 0)
/* 110 */           whatWasMissUnderstood.append(", ");
/* 111 */         whatWasMissUnderstood.append(badQName.toString());
/*    */ 
/* 113 */         if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
/* 114 */           SOAPHeaderElement newHeader = new SOAPHeaderElement("http://www.w3.org/2003/05/soap-envelope", "NotUnderstood");
/*    */ 
/* 117 */           newHeader.addAttribute(null, "qname", badQName);
/*    */ 
/* 120 */           fault.addHeader(newHeader);
/*    */         }
/*    */       }
/* 123 */       fault.setFaultString(Messages.getMessage("noUnderstand00", whatWasMissUnderstood.toString()));
/*    */ 
/* 125 */       throw fault;
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.handlers.soap.MustUnderstandChecker
 * JD-Core Version:    0.6.0
 */