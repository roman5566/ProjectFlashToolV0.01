/*    */ package org.apache.commons.codec.language;
/*    */ 
/*    */ import org.apache.commons.codec.EncoderException;
/*    */ import org.apache.commons.codec.StringEncoder;
/*    */ 
/*    */ public abstract class AbstractCaverphone
/*    */   implements StringEncoder
/*    */ {
/*    */   public Object encode(Object source)
/*    */     throws EncoderException
/*    */   {
/* 55 */     if (!(source instanceof String)) {
/* 56 */       throw new EncoderException("Parameter supplied to Caverphone encode is not of type java.lang.String");
/*    */     }
/* 58 */     return encode((String)source);
/*    */   }
/*    */ 
/*    */   public boolean isEncodeEqual(String str1, String str2)
/*    */     throws EncoderException
/*    */   {
/* 74 */     return encode(str1).equals(encode(str2));
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.commons.codec.language.AbstractCaverphone
 * JD-Core Version:    0.6.0
 */