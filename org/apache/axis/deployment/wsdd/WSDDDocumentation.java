/*    */ package org.apache.axis.deployment.wsdd;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axis.encoding.SerializationContext;
/*    */ import org.apache.axis.utils.XMLUtils;
/*    */ import org.w3c.dom.Element;
/*    */ 
/*    */ public class WSDDDocumentation extends WSDDElement
/*    */ {
/*    */   private String value;
/*    */ 
/*    */   protected QName getElementName()
/*    */   {
/* 38 */     return WSDDConstants.QNAME_DOC;
/*    */   }
/*    */ 
/*    */   public WSDDDocumentation(String value)
/*    */   {
/* 43 */     this.value = value;
/*    */   }
/*    */ 
/*    */   public WSDDDocumentation(Element e)
/*    */     throws WSDDException
/*    */   {
/* 54 */     super(e);
/* 55 */     this.value = XMLUtils.getChildCharacterData(e);
/*    */   }
/*    */ 
/*    */   public String getValue()
/*    */   {
/* 63 */     return this.value;
/*    */   }
/*    */ 
/*    */   public void setValue(String value)
/*    */   {
/* 71 */     this.value = value;
/*    */   }
/*    */ 
/*    */   public void writeToContext(SerializationContext context)
/*    */     throws IOException
/*    */   {
/* 80 */     context.startElement(QNAME_DOC, null);
/* 81 */     context.writeSafeString(this.value);
/* 82 */     context.endElement();
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.deployment.wsdd.WSDDDocumentation
 * JD-Core Version:    0.6.0
 */