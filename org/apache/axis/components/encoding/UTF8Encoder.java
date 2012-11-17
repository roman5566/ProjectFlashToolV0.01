/*    */ package org.apache.axis.components.encoding;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.Writer;
/*    */ import org.apache.axis.i18n.Messages;
/*    */ 
/*    */ class UTF8Encoder extends AbstractXMLEncoder
/*    */ {
/*    */   public String getEncoding()
/*    */   {
/* 37 */     return "UTF-8";
/*    */   }
/*    */ 
/*    */   public void writeEncoded(Writer writer, String xmlString)
/*    */     throws IOException
/*    */   {
/* 48 */     if (xmlString == null) {
/* 49 */       return;
/*    */     }
/* 51 */     int length = xmlString.length();
/*    */ 
/* 53 */     for (int i = 0; i < length; i++) {
/* 54 */       char character = xmlString.charAt(i);
/* 55 */       switch (character)
/*    */       {
/*    */       case '&':
/* 59 */         writer.write("&amp;");
/* 60 */         break;
/*    */       case '"':
/* 62 */         writer.write("&quot;");
/* 63 */         break;
/*    */       case '<':
/* 65 */         writer.write("&lt;");
/* 66 */         break;
/*    */       case '>':
/* 68 */         writer.write("&gt;");
/* 69 */         break;
/*    */       case '\n':
/* 71 */         writer.write("\n");
/* 72 */         break;
/*    */       case '\r':
/* 74 */         writer.write("\r");
/* 75 */         break;
/*    */       case '\t':
/* 77 */         writer.write("\t");
/* 78 */         break;
/*    */       default:
/* 80 */         if (character < ' ') {
/* 81 */           throw new IllegalArgumentException(Messages.getMessage("invalidXmlCharacter00", Integer.toHexString(character), xmlString.substring(0, i)));
/*    */         }
/*    */ 
/* 85 */         if (character > '') {
/* 86 */           writer.write("&#x");
/* 87 */           writer.write(Integer.toHexString(character).toUpperCase());
/* 88 */           writer.write(";");
/*    */         } else {
/* 90 */           writer.write(character);
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.encoding.UTF8Encoder
 * JD-Core Version:    0.6.0
 */