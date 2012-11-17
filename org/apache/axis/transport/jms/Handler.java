/*    */ package org.apache.axis.transport.jms;
/*    */ 
/*    */ import java.net.URL;
/*    */ import java.net.URLConnection;
/*    */ import java.net.URLStreamHandler;
/*    */ import org.apache.axis.client.Call;
/*    */ 
/*    */ public class Handler extends URLStreamHandler
/*    */ {
/*    */   protected String toExternalForm(URL url)
/*    */   {
/* 40 */     String destination = url.getPath().substring(1);
/* 41 */     String query = url.getQuery();
/*    */ 
/* 43 */     StringBuffer jmsurl = new StringBuffer("jms:/");
/* 44 */     jmsurl.append(destination).append("?").append(query);
/*    */ 
/* 46 */     return jmsurl.toString();
/*    */   }
/*    */ 
/*    */   protected URLConnection openConnection(URL url) {
/* 50 */     return new JMSURLConnection(url);
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 32 */     Call.setTransportForProtocol("jms", JMSTransport.class);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.jms.Handler
 * JD-Core Version:    0.6.0
 */