/*    */ package org.apache.axis.components.jms;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import org.apache.axis.AxisProperties;
/*    */ 
/*    */ public class JMSVendorAdapterFactory
/*    */ {
/* 32 */   private static HashMap s_adapters = new HashMap();
/*    */   private static final String VENDOR_PKG = "org.apache.axis.components.jms";
/*    */ 
/*    */   public static final JMSVendorAdapter getJMSVendorAdapter()
/*    */   {
/* 42 */     return (JMSVendorAdapter)AxisProperties.newInstance(JMSVendorAdapter.class);
/*    */   }
/*    */ 
/*    */   public static final JMSVendorAdapter getJMSVendorAdapter(String vendorId)
/*    */   {
/* 48 */     if (s_adapters.containsKey(vendorId)) {
/* 49 */       return (JMSVendorAdapter)s_adapters.get(vendorId);
/*    */     }
/*    */ 
/* 52 */     JMSVendorAdapter adapter = null;
/*    */     try
/*    */     {
/* 55 */       Class vendorClass = Class.forName(getVendorAdapterClassname(vendorId));
/* 56 */       adapter = (JMSVendorAdapter)vendorClass.newInstance();
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 60 */       return null;
/*    */     }
/*    */ 
/* 63 */     synchronized (s_adapters)
/*    */     {
/* 65 */       if (s_adapters.containsKey(vendorId)) {
/* 66 */         return (JMSVendorAdapter)s_adapters.get(vendorId);
/*    */       }
/* 68 */       if (adapter != null) {
/* 69 */         s_adapters.put(vendorId, adapter);
/*    */       }
/*    */     }
/* 72 */     return adapter;
/*    */   }
/*    */ 
/*    */   private static String getVendorAdapterClassname(String vendorId)
/*    */   {
/* 77 */     StringBuffer sb = new StringBuffer("org.apache.axis.components.jms").append(".");
/* 78 */     sb.append(vendorId);
/* 79 */     sb.append("VendorAdapter");
/*    */ 
/* 81 */     return sb.toString();
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 36 */     AxisProperties.setClassDefault(JMSVendorAdapter.class, "org.apache.axis.components.jms.JNDIVendorAdapter");
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.jms.JMSVendorAdapterFactory
 * JD-Core Version:    0.6.0
 */