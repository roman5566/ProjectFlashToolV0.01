/*    */ package org.apache.axis.transport.http;
/*    */ 
/*    */ import java.io.FilterOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class ChunkedOutputStream extends FilterOutputStream
/*    */ {
/* 30 */   boolean eos = false;
/*    */ 
/* 51 */   static final byte[] CRLF = "\r\n".getBytes();
/* 52 */   static final byte[] LAST_TOKEN = "0\r\n\r\n".getBytes();
/*    */ 
/*    */   private ChunkedOutputStream()
/*    */   {
/* 33 */     super(null);
/*    */   }
/*    */ 
/*    */   public ChunkedOutputStream(OutputStream os) {
/* 37 */     super(os);
/*    */   }
/*    */ 
/*    */   public void write(int b) throws IOException
/*    */   {
/* 42 */     write(new byte[] { (byte)b }, 0, 1);
/*    */   }
/*    */ 
/*    */   public void write(byte[] b)
/*    */     throws IOException
/*    */   {
/* 48 */     write(b, 0, b.length);
/*    */   }
/*    */ 
/*    */   public void write(byte[] b, int off, int len)
/*    */     throws IOException
/*    */   {
/* 58 */     if (len == 0) return;
/*    */ 
/* 60 */     this.out.write(Integer.toHexString(len).getBytes());
/* 61 */     this.out.write(CRLF);
/* 62 */     this.out.write(b, off, len);
/* 63 */     this.out.write(CRLF);
/*    */   }
/*    */ 
/*    */   public void eos()
/*    */     throws IOException
/*    */   {
/* 74 */     synchronized (this) {
/* 75 */       if (this.eos) return;
/* 76 */       this.eos = true;
/*    */     }
/* 78 */     this.out.write(LAST_TOKEN);
/* 79 */     this.out.flush();
/*    */   }
/*    */ 
/*    */   public void close() throws IOException
/*    */   {
/* 84 */     eos();
/* 85 */     this.out.close();
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.transport.http.ChunkedOutputStream
 * JD-Core Version:    0.6.0
 */