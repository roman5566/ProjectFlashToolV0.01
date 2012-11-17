/*    */ package org.apache.axis.components.image;
/*    */ 
/*    */ import com.sun.jimi.core.Jimi;
/*    */ import java.awt.Image;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class JimiIO
/*    */   implements ImageIO
/*    */ {
/*    */   public void saveImage(String id, Image image, OutputStream os)
/*    */     throws Exception
/*    */   {
/* 41 */     Jimi.putImage(id, image, os);
/*    */   }
/*    */ 
/*    */   public Image loadImage(InputStream in)
/*    */     throws Exception
/*    */   {
/* 50 */     return Jimi.getImage(in);
/*    */   }
/*    */ }

/* Location:           E:\Windows\Documents\flashdmp\New folder\ProjectFlashToolV0.01.jar
 * Qualified Name:     org.apache.axis.components.image.JimiIO
 * JD-Core Version:    0.6.0
 */