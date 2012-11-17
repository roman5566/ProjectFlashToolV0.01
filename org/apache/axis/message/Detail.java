/*    */ package org.apache.axis.message;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import javax.xml.soap.Name;
/*    */ import javax.xml.soap.SOAPException;
/*    */ 
/*    */ public class Detail extends SOAPFaultElement
/*    */   implements javax.xml.soap.Detail
/*    */ {
/*    */   public javax.xml.soap.DetailEntry addDetailEntry(Name name)
/*    */     throws SOAPException
/*    */   {
/* 42 */     DetailEntry entry = new DetailEntry(name);
/* 43 */     addChildElement(entry);
/* 44 */     return entry;
/*    */   }
/*    */ 
/*    */   public Iterator getDetailEntries()
/*    */   {
/* 53 */     return getChildElements();
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.Detail
 * JD-Core Version:    0.6.0
 */