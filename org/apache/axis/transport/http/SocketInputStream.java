/*    */ package org.apache.axis.transport.http;
/*    */ 
/*    */ import java.io.FilterInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.net.Socket;
/*    */ 
/*    */ public class SocketInputStream extends FilterInputStream
/*    */ {
/* 34 */   protected volatile boolean closed = false;
/* 35 */   Socket socket = null;
/*    */ 
/*    */   private SocketInputStream() {
/* 38 */     super(null);
/*    */   }
/*    */ 
/*    */   public SocketInputStream(InputStream is, Socket socket)
/*    */   {
/* 43 */     super(is);
/* 44 */     this.socket = socket;
/*    */   }
/*    */ 
/*    */   public void close() throws IOException {
/* 48 */     synchronized (this) {
/* 49 */       if (this.closed) return;
/* 50 */       this.closed = true;
/*    */     }
/* 52 */     this.in.close();
/* 53 */     this.in = null;
/* 54 */     this.socket.close();
/* 55 */     this.socket = null;
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.http.SocketInputStream
 * JD-Core Version:    0.6.0
 */