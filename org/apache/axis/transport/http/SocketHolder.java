/*    */ package org.apache.axis.transport.http;
/*    */ 
/*    */ import java.net.Socket;
/*    */ 
/*    */ public class SocketHolder
/*    */ {
/* 26 */   private Socket value = null;
/*    */ 
/*    */   public SocketHolder(Socket value) {
/* 29 */     this.value = value;
/*    */   }
/*    */ 
/*    */   public Socket getSocket() {
/* 33 */     return this.value;
/*    */   }
/*    */ 
/*    */   public void setSocket(Socket value) {
/* 37 */     this.value = value;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.http.SocketHolder
 * JD-Core Version:    0.6.0
 */