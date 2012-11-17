/*    */ package org.apache.axis.deployment.wsdd;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.encoding.SerializationContext;
/*    */ import org.w3c.dom.Element;
/*    */ import org.xml.sax.helpers.AttributesImpl;
/*    */ 
/*    */ public class WSDDTransport extends WSDDTargetedChain
/*    */ {
/*    */   public WSDDTransport()
/*    */   {
/*    */   }
/*    */ 
/*    */   public WSDDTransport(Element e)
/*    */     throws WSDDException
/*    */   {
/* 47 */     super(e);
/*    */   }
/*    */ 
/*    */   protected QName getElementName() {
/* 51 */     return WSDDConstants.QNAME_TRANSPORT;
/*    */   }
/*    */ 
/*    */   public void writeToContext(SerializationContext context)
/*    */     throws IOException
/*    */   {
/* 59 */     AttributesImpl attrs = new AttributesImpl();
/* 60 */     QName name = getQName();
/* 61 */     if (name != null) {
/* 62 */       attrs.addAttribute("", "name", "name", "CDATA", context.qName2String(name));
/*    */     }
/*    */ 
/* 66 */     name = getPivotQName();
/* 67 */     if (name != null) {
/* 68 */       attrs.addAttribute("", "pivot", "pivot", "CDATA", context.qName2String(name));
/*    */     }
/*    */ 
/* 72 */     context.startElement(WSDDConstants.QNAME_TRANSPORT, attrs);
/* 73 */     writeFlowsToContext(context);
/* 74 */     writeParamsToContext(context);
/* 75 */     context.endElement();
/*    */   }
/*    */ 
/*    */   public void deployToRegistry(WSDDDeployment registry)
/*    */   {
/* 80 */     registry.addTransport(this);
/*    */ 
/* 82 */     super.deployToRegistry(registry);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.WSDDTransport
 * JD-Core Version:    0.6.0
 */