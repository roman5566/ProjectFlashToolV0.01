/*    */ package org.apache.commons.codec.binary;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public class Base32InputStream extends BaseNCodecInputStream
/*    */ {
/*    */   public Base32InputStream(InputStream in)
/*    */   {
/* 48 */     this(in, false);
/*    */   }
/*    */ 
/*    */   public Base32InputStream(InputStream in, boolean doEncode)
/*    */   {
/* 61 */     super(in, new Base32(false), doEncode);
/*    */   }
/*    */ 
/*    */   public Base32InputStream(InputStream in, boolean doEncode, int lineLength, byte[] lineSeparator)
/*    */   {
/* 81 */     super(in, new Base32(lineLength, lineSeparator), doEncode);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.binary.Base32InputStream
 * JD-Core Version:    0.6.0
 */