/*    */ package org.apache.axis.components.jms;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import javax.jms.ConnectionFactory;
/*    */ import javax.jms.QueueConnectionFactory;
/*    */ import javax.jms.TopicConnectionFactory;
/*    */ import org.apache.axis.utils.BeanPropertyDescriptor;
/*    */ import org.apache.axis.utils.BeanUtils;
/*    */ import org.apache.axis.utils.ClassUtils;
/*    */ 
/*    */ public abstract class BeanVendorAdapter extends JMSVendorAdapter
/*    */ {
/*    */   protected static final String CONNECTION_FACTORY_CLASS = "transport.jms.ConnectionFactoryClass";
/*    */ 
/*    */   public QueueConnectionFactory getQueueConnectionFactory(HashMap cfConfig)
/*    */     throws Exception
/*    */   {
/* 43 */     return (QueueConnectionFactory)getConnectionFactory(cfConfig);
/*    */   }
/*    */ 
/*    */   public TopicConnectionFactory getTopicConnectionFactory(HashMap cfConfig)
/*    */     throws Exception
/*    */   {
/* 49 */     return (TopicConnectionFactory)getConnectionFactory(cfConfig);
/*    */   }
/*    */ 
/*    */   private ConnectionFactory getConnectionFactory(HashMap cfConfig)
/*    */     throws Exception
/*    */   {
/* 55 */     String classname = (String)cfConfig.get("transport.jms.ConnectionFactoryClass");
/* 56 */     if ((classname == null) || (classname.trim().length() == 0)) {
/* 57 */       throw new IllegalArgumentException("noCFClass");
/*    */     }
/* 59 */     Class factoryClass = ClassUtils.forName(classname);
/* 60 */     ConnectionFactory factory = (ConnectionFactory)factoryClass.newInstance();
/* 61 */     callSetters(cfConfig, factoryClass, factory);
/* 62 */     return factory;
/*    */   }
/*    */ 
/*    */   private void callSetters(HashMap cfConfig, Class factoryClass, ConnectionFactory factory)
/*    */     throws Exception
/*    */   {
/* 70 */     BeanPropertyDescriptor[] bpd = BeanUtils.getPd(factoryClass);
/* 71 */     for (int i = 0; i < bpd.length; i++)
/*    */     {
/* 73 */       BeanPropertyDescriptor thisBPD = bpd[i];
/* 74 */       String propName = thisBPD.getName();
/* 75 */       if (!cfConfig.containsKey(propName))
/*    */         continue;
/* 77 */       Object value = cfConfig.get(propName);
/* 78 */       if (value == null) {
/*    */         continue;
/*    */       }
/* 81 */       String validType = thisBPD.getType().getName();
/* 82 */       if (!value.getClass().getName().equals(validType))
/* 83 */         throw new IllegalArgumentException("badType");
/* 84 */       if (!thisBPD.isWriteable())
/* 85 */         throw new IllegalArgumentException("notWriteable");
/* 86 */       if (thisBPD.isIndexed())
/* 87 */         throw new IllegalArgumentException("noIndexedSupport");
/* 88 */       thisBPD.set(factory, value);
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.jms.BeanVendorAdapter
 * JD-Core Version:    0.6.0
 */