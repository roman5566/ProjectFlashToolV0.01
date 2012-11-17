/*    */ package org.apache.axis.utils;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public class IOUtils
/*    */ {
/*    */   public static int readFully(InputStream in, byte[] b)
/*    */     throws IOException
/*    */   {
/* 40 */     return readFully(in, b, 0, b.length);
/*    */   }
/*    */ 
/*    */   public static int readFully(InputStream in, byte[] b, int off, int len)
/*    */     throws IOException
/*    */   {
/* 53 */     int total = 0;
/*    */     while (true) {
/* 55 */       int got = in.read(b, off + total, len - total);
/* 56 */       if (got < 0) {
/* 57 */         return total == 0 ? -1 : total;
/*    */       }
/* 59 */       total += got;
/* 60 */       if (total == len)
/* 61 */         return total;
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.utils.IOUtils
 * JD-Core Version:    0.6.0
 */