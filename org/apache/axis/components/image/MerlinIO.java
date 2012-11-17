/*    */ package org.apache.axis.components.image;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Image;
/*    */ import java.awt.MediaTracker;
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.util.Iterator;
/*    */ import javax.imageio.IIOImage;
/*    */ import javax.imageio.ImageWriter;
/*    */ 
/*    */ public class MerlinIO extends Component
/*    */   implements ImageIO
/*    */ {
/*    */   public void saveImage(String mimeType, Image image, OutputStream os)
/*    */     throws Exception
/*    */   {
/* 45 */     ImageWriter writer = null;
/* 46 */     Iterator iter = javax.imageio.ImageIO.getImageWritersByMIMEType(mimeType);
/* 47 */     if (iter.hasNext()) {
/* 48 */       writer = (ImageWriter)iter.next();
/*    */     }
/* 50 */     writer.setOutput(javax.imageio.ImageIO.createImageOutputStream(os));
/* 51 */     BufferedImage rendImage = null;
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
/* 62 */     writer.write(new IIOImage(rendImage, null, null));
/* 63 */     writer.dispose();
/*    */   }
/*    */ 
/*    */   public Image loadImage(InputStream in)
/*    */     throws Exception
/*    */   {
/* 72 */     return javax.imageio.ImageIO.read(in);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.image.MerlinIO
 * JD-Core Version:    0.6.0
 */