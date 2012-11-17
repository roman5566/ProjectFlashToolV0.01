/*    */ package org.apache.axis.components.image;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Image;
/*    */ import java.awt.MediaTracker;
/*    */ import java.awt.Toolkit;
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import org.apache.axis.utils.IOUtils;
/*    */ import org.apache.axis.utils.Messages;
/*    */ import sun.awt.image.codec.JPEGImageEncoderImpl;
/*    */ 
/*    */ public class JDK13IO extends Component
/*    */   implements ImageIO
/*    */ {
/*    */   public void saveImage(String mimeType, Image image, OutputStream os)
/*    */     throws Exception
/*    */   {
/* 49 */     BufferedImage rendImage = null;
/*    */ 
/* 52 */     if ((image instanceof BufferedImage)) {
/* 53 */       rendImage = (BufferedImage)image;
/*    */     } else {
/* 55 */       MediaTracker tracker = new MediaTracker(this);
/* 56 */       tracker.addImage(image, 0);
/* 57 */       tracker.waitForAll();
/* 58 */       rendImage = new BufferedImage(image.getWidth(null), image.getHeight(null), 1);
/* 59 */       Graphics g = rendImage.createGraphics();
/* 60 */       g.drawImage(image, 0, 0, null);
/*    */     }
/*    */ 
/* 64 */     if ("image/jpeg".equals(mimeType)) {
/* 65 */       JPEGImageEncoderImpl j = new JPEGImageEncoderImpl(os);
/* 66 */       j.encode(rendImage);
/*    */     }
/*    */     else {
/* 69 */       throw new IOException(Messages.getMessage("jpegOnly", mimeType));
/*    */     }
/*    */   }
/*    */ 
/*    */   public Image loadImage(InputStream in)
/*    */     throws Exception
/*    */   {
/* 79 */     if (in.available() <= 0) {
/* 80 */       return null;
/*    */     }
/*    */ 
/* 83 */     byte[] bytes = new byte[in.available()];
/* 84 */     IOUtils.readFully(in, bytes);
/* 85 */     return Toolkit.getDefaultToolkit().createImage(bytes);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.image.JDK13IO
 * JD-Core Version:    0.6.0
 */