/*    */ package org.apache.axis.components.encoding;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.Writer;
/*    */ import org.apache.axis.i18n.Messages;
/*    */ 
/*    */ class UTF16Encoder extends AbstractXMLEncoder
/*    */ {
/*    */   public String getEncoding()
/*    */   {
/* 36 */     return "UTF-16";
/*    */   }
/*    */ 
/*    */   public void writeEncoded(Writer writer, String xmlString)
/*    */     throws IOException
/*    */   {
/* 47 */     if (xmlString == null) {
/* 48 */       return;
/*    */     }
/* 50 */     int length = xmlString.length();
/*    */ 
/* 52 */     for (int i = 0; i < length; i++) {
/* 53 */       char character = xmlString.charAt(i);
/* 54 */       switch (character)
/*    */       {
/*    */       case '&':
/* 58 */         writer.write("&amp;");
/* 59 */         break;
/*    */       case '"':
/* 61 */         writer.write("&quot;");
/* 62 */         break;
/*    */       case '<':
/* 64 */         writer.write("&lt;");
/* 65 */         break;
/*    */       case '>':
/* 67 */         writer.write("&gt;");
/* 68 */         break;
/*    */       case '\n':
/* 70 */         writer.write("\n");
/* 71 */         break;
/*    */       case '\r':
/* 73 */         writer.write("\r");
/* 74 */         break;
/*    */       case '\t':
/* 76 */         writer.write("\t");
/* 77 */         break;
/*    */       default:
/* 79 */         if (character < ' ') {
/* 80 */           throw new IllegalArgumentException(Messages.getMessage("invalidXmlCharacter00", Integer.toHexString(character), xmlString.substring(0, i)));
/*    */         }
/*    */ 
/* 84 */         if (character > 65535) {
/* 85 */           writer.write(55232 + (character >> '\n'));
/* 86 */           writer.write(0xDC00 | character & 0x3FF);
/*    */         } else {
/* 88 */           writer.write(character);
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.encoding.UTF16Encoder
 * JD-Core Version:    0.6.0
 */