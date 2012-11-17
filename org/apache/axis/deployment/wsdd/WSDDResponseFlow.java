/*    */ package org.apache.axis.deployment.wsdd;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import org.w3c.dom.Element;
/*    */ 
/*    */ public class WSDDResponseFlow extends WSDDChain
/*    */ {
/*    */   public WSDDResponseFlow()
/*    */   {
/*    */   }
/*    */ 
/*    */   public WSDDResponseFlow(Element e)
/*    */     throws WSDDException
/*    */   {
/* 44 */     super(e);
/*    */   }
/*    */ 
/*    */   protected QName getElementName() {
/* 48 */     return WSDDConstants.QNAME_RESPFLOW;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.WSDDResponseFlow
 * JD-Core Version:    0.6.0
 */