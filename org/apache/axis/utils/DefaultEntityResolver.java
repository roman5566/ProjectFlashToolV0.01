/*    */ package org.apache.axis.utils;
/*    */ 
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.xml.sax.EntityResolver;
/*    */ import org.xml.sax.InputSource;
/*    */ 
/*    */ public class DefaultEntityResolver
/*    */   implements EntityResolver
/*    */ {
/* 23 */   protected static Log log = LogFactory.getLog(XMLUtils.class.getName());
/*    */ 
/*    */   public InputSource resolveEntity(String publicId, String systemId)
/*    */   {
/* 30 */     return XMLUtils.getEmptyInputSource();
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.DefaultEntityResolver
 * JD-Core Version:    0.6.0
 */