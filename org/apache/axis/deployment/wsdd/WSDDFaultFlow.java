/*    */ package org.apache.axis.deployment.wsdd;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import org.w3c.dom.Element;
/*    */ 
/*    */ public class WSDDFaultFlow extends WSDDChain
/*    */ {
/*    */   public WSDDFaultFlow()
/*    */   {
/*    */   }
/*    */ 
/*    */   public WSDDFaultFlow(Element e)
/*    */     throws WSDDException
/*    */   {
/* 44 */     super(e);
/*    */   }
/*    */ 
/*    */   protected QName getElementName()
/*    */   {
/* 49 */     return WSDDConstants.QNAME_FAULTFLOW;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.WSDDFaultFlow
 * JD-Core Version:    0.6.0
 */