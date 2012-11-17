/*    */ package org.apache.axis.attachments;
/*    */ 
/*    */ import org.apache.axis.utils.Messages;
/*    */ 
/*    */ public final class DimeTypeNameFormat
/*    */ {
/* 33 */   private byte format = 0;
/*    */   static final byte NOCHANGE_VALUE = 0;
/*    */   static final byte MIME_VALUE = 1;
/*    */   static final byte URI_VALUE = 2;
/*    */   static final byte UNKNOWN_VALUE = 3;
/*    */   static final byte NODATA_VALUE = 4;
/* 45 */   static final DimeTypeNameFormat NOCHANGE = new DimeTypeNameFormat(0);
/*    */ 
/* 48 */   public static final DimeTypeNameFormat MIME = new DimeTypeNameFormat(1);
/*    */ 
/* 51 */   public static final DimeTypeNameFormat URI = new DimeTypeNameFormat(2);
/*    */ 
/* 54 */   public static final DimeTypeNameFormat UNKNOWN = new DimeTypeNameFormat(3);
/*    */ 
/* 57 */   static final DimeTypeNameFormat NODATA = new DimeTypeNameFormat(4);
/*    */ 
/* 60 */   private static String[] toEnglish = { "NOCHANGE", "MIME", "URI", "UNKNOWN", "NODATA" };
/*    */ 
/* 62 */   private static DimeTypeNameFormat[] fromByte = { NOCHANGE, MIME, URI, UNKNOWN, NODATA };
/*    */ 
/*    */   private DimeTypeNameFormat()
/*    */   {
/*    */   }
/*    */ 
/*    */   private DimeTypeNameFormat(byte f)
/*    */   {
/* 36 */     this.format = f;
/*    */   }
/*    */ 
/*    */   public final String toString()
/*    */   {
/* 66 */     return toEnglish[this.format];
/*    */   }
/*    */ 
/*    */   public final byte toByte() {
/* 70 */     return this.format;
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 74 */     return this.format;
/*    */   }
/*    */ 
/*    */   public final boolean equals(Object x) {
/* 78 */     if (x == null) {
/* 79 */       return false;
/*    */     }
/* 81 */     if (!(x instanceof DimeTypeNameFormat)) {
/* 82 */       return false;
/*    */     }
/* 84 */     return ((DimeTypeNameFormat)x).format == this.format;
/*    */   }
/*    */ 
/*    */   public static DimeTypeNameFormat parseByte(byte x) {
/* 88 */     if ((x < 0) || (x > fromByte.length)) {
/* 89 */       throw new IllegalArgumentException(Messages.getMessage("attach.DimeStreamBadType", "" + x));
/*    */     }
/*    */ 
/* 92 */     return fromByte[x];
/*    */   }
/*    */ 
/*    */   public static DimeTypeNameFormat parseByte(Byte x) {
/* 96 */     return parseByte(x.byteValue());
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.attachments.DimeTypeNameFormat
 * JD-Core Version:    0.6.0
 */