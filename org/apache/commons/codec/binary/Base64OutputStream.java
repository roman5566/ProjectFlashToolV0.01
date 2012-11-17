/*    */ package org.apache.commons.codec.binary;
/*    */ 
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class Base64OutputStream extends BaseNCodecOutputStream
/*    */ {
/*    */   public Base64OutputStream(OutputStream out)
/*    */   {
/* 53 */     this(out, true);
/*    */   }
/*    */ 
/*    */   public Base64OutputStream(OutputStream out, boolean doEncode)
/*    */   {
/* 66 */     super(out, new Base64(false), doEncode);
/*    */   }
/*    */ 
/*    */   public Base64OutputStream(OutputStream out, boolean doEncode, int lineLength, byte[] lineSeparator)
/*    */   {
/* 86 */     super(out, new Base64(lineLength, lineSeparator), doEncode);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.binary.Base64OutputStream
 * JD-Core Version:    0.6.0
 */