/*    */ package org.apache.axis.deployment.wsdd;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.encoding.SerializationContext;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.w3c.dom.Element;
/*    */ import org.xml.sax.helpers.AttributesImpl;
/*    */ 
/*    */ public class WSDDHandler extends WSDDDeployableItem
/*    */ {
/*    */   public WSDDHandler()
/*    */   {
/*    */   }
/*    */ 
/*    */   public WSDDHandler(Element e)
/*    */     throws WSDDException
/*    */   {
/* 48 */     super(e);
/* 49 */     if ((this.type == null) && (getClass() == WSDDHandler.class))
/* 50 */       throw new WSDDException(Messages.getMessage("noTypeAttr00"));
/*    */   }
/*    */ 
/*    */   protected QName getElementName()
/*    */   {
/* 56 */     return QNAME_HANDLER;
/*    */   }
/*    */ 
/*    */   public void writeToContext(SerializationContext context)
/*    */     throws IOException
/*    */   {
/* 62 */     AttributesImpl attrs = new AttributesImpl();
/* 63 */     QName name = getQName();
/* 64 */     if (name != null) {
/* 65 */       attrs.addAttribute("", "name", "name", "CDATA", context.qName2String(name));
/*    */     }
/*    */ 
/* 69 */     attrs.addAttribute("", "type", "type", "CDATA", context.qName2String(getType()));
/*    */ 
/* 71 */     context.startElement(WSDDConstants.QNAME_HANDLER, attrs);
/* 72 */     writeParamsToContext(context);
/* 73 */     context.endElement();
/*    */   }
/*    */ 
/*    */   public void deployToRegistry(WSDDDeployment deployment)
/*    */   {
/* 78 */     deployment.addHandler(this);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.WSDDHandler
 * JD-Core Version:    0.6.0
 */