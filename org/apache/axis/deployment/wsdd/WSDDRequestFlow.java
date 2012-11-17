/*    */ package org.apache.axis.deployment.wsdd;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import org.w3c.dom.Element;
/*    */ 
/*    */ public class WSDDRequestFlow extends WSDDChain
/*    */ {
/*    */   public WSDDRequestFlow()
/*    */   {
/*    */   }
/*    */ 
/*    */   public WSDDRequestFlow(Element e)
/*    */     throws WSDDException
/*    */   {
/* 44 */     super(e);
/*    */   }
/*    */ 
/*    */   protected QName getElementName() {
/* 48 */     return WSDDConstants.QNAME_REQFLOW;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.WSDDRequestFlow
 * JD-Core Version:    0.6.0
 */