/*    */ package org.apache.axis.description;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import javax.xml.namespace.QName;
/*    */ 
/*    */ public class AttributeDesc extends FieldDesc
/*    */   implements Serializable
/*    */ {
/*    */   public AttributeDesc()
/*    */   {
/* 29 */     super(false);
/*    */   }
/*    */ 
/*    */   public void setAttributeName(String name)
/*    */   {
/* 39 */     setXmlName(new QName("", name));
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.description.AttributeDesc
 * JD-Core Version:    0.6.0
 */