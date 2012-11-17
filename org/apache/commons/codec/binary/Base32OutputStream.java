/*    */ package org.apache.commons.codec.binary;
/*    */ 
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class Base32OutputStream extends BaseNCodecOutputStream
/*    */ {
/*    */   public Base32OutputStream(OutputStream out)
/*    */   {
/* 48 */     this(out, true);
/*    */   }
/*    */ 
/*    */   public Base32OutputStream(OutputStream out, boolean doEncode)
/*    */   {
/* 61 */     super(out, new Base32(false), doEncode);
/*    */   }
/*    */ 
/*    */   public Base32OutputStream(OutputStream out, boolean doEncode, int lineLength, byte[] lineSeparator)
/*    */   {
/* 81 */     super(out, new Base32(lineLength, lineSeparator), doEncode);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.binary.Base32OutputStream
 * JD-Core Version:    0.6.0
 */