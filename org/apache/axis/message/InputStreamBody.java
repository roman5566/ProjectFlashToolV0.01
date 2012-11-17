/*    */ package org.apache.axis.message;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import org.apache.axis.components.logger.LogFactory;
/*    */ import org.apache.axis.encoding.SerializationContext;
/*    */ import org.apache.axis.utils.IOUtils;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class InputStreamBody extends SOAPBodyElement
/*    */ {
/* 31 */   protected static Log log = LogFactory.getLog(InputStreamBody.class.getName());
/*    */   protected InputStream inputStream;
/*    */ 
/*    */   public InputStreamBody(InputStream inputStream)
/*    */   {
/* 38 */     this.inputStream = inputStream;
/*    */   }
/*    */ 
/*    */   public void outputImpl(SerializationContext context) throws IOException
/*    */   {
/*    */     try {
/* 44 */       byte[] buf = new byte[this.inputStream.available()];
/* 45 */       IOUtils.readFully(this.inputStream, buf);
/* 46 */       String contents = new String(buf);
/* 47 */       context.writeString(contents);
/*    */     }
/*    */     catch (IOException ex) {
/* 50 */       throw ex;
/*    */     }
/*    */     catch (Exception e) {
/* 53 */       log.error(Messages.getMessage("exception00"), e);
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.message.InputStreamBody
 * JD-Core Version:    0.6.0
 */